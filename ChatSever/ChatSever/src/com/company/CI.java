package com.company;

import com.sun.istack.internal.NotNull;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CI implements Runnable, Observer, Comparable<CI> {
    public static SimpleDateFormat simpledateformat = new SimpleDateFormat("HH:mm - dd MMM yyyy");
    private static String regexSplitSequence = "~\\|>\\$\\?<@";
    private boolean done;
    private InputStream inputStream;
    private PrintStream outputStream;
    private User user;

    public CI(InputStream inputStream, PrintStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        done = false;
        ChatHistory.getInstance().registerObserver(this);
    }
    public void run() {
        outputStream.println("In run()");
        Scanner sc = new Scanner(inputStream);
        while (!done) {
            String input = (sc.hasNextLine()) ? sc.nextLine() : "quit~|>$?<@";
            commandIn(input);
        }
        outputStream.println("Goodbye.");
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ChatHistory.getInstance().removeObserver(this);
        }
    }
    private void printChatHistory() {
        outputStream.println("chat history");
        synchronized (ChatHistory.getInstance().chathistoryoutput()) {
            for (ChatMessage message : ChatHistory.getInstance().chathistoryoutput()) {
                outputStream.println("history~|>$?<@" + message.userOwnBy().nameOutPut() + "~|>$?<@" +
                        message.timeStampOutput() + "~|>$?<@" + message.messageOutput());
            }
        }
        outputStream.println("~[><]");
    }
    private void printAllUsers() {
        outputStream.println("~[888] USER LIST");
        HashSet<User> users = UserNameList.getInstance().usersOutPut();
        for (User user : users) {
            outputStream.println(user.nameOutPut() + "~|>$?<@" + user.timeStampOutPut());
        }
        outputStream.println("~[888]");
    }
    private void sendMessage(String arg) {
        ChatHistory.getInstance().insert(new ChatMessage(arg,user));
    }
    private void signOut() {
        if (user != null) {
            UserNameList.getInstance().remove(user);
            ChatHistory.getInstance().remove(user);
            user = null;
        }
        outputStream.println("~[8] user has signed out");
    }
    private void checkUser(String username) {
        if (user != null) {
            outputStream.println("*** ERR: user has already logged in");
            return;
        }
        if (username == null || username.trim().length() < 3) {
            outputStream.println("*** ERR: username is too short");
            return;
        }
        User temp = new User(username);
        if (UserNameList.getInstance().userIsUnavailable(temp)) {
            outputStream.println("*** ERR: name existed");
            return;
        }
        user = temp;
        UserNameList.getInstance().insert(user);
        outputStream.println("~[8] user has logged in");
        outputStream.println(user.nameOutPut() + "~|>$?<@" + user.timeStampOutPut());
        System.out.println("added user " + user.nameOutPut() + " at " + simpledateformat.format(user.timeStampOutPut()));
    }
    private void commandIn(@NotNull String input) {
        System.out.println("#1 " + input);
        outputStream.println("#2 " + input);
        String[] parts = input.split(regexSplitSequence);
        System.out.println(parts.length);
        String cmd, arg = null;
        if (parts.length >= 2) {
            arg = parts[parts.length - 1];
        }
        if (parts.length >= 1) {
            cmd = parts[0];
            if (cmd.equals(input)) {
                outputStream.println("*** ERR: not a command");
                return;
            }
            outputStream.println("cmd: " + cmd);
            switch (cmd) {
                case "name":
                    if (user == null) {
                        outputStream.println("*** ERR: user did not log in");
                        return;
                    }
                    outputStream.println("name~|>$?<@"+user.nameOutPut());
                    break;
                case "user":
                    checkUser(arg);
                    break;
                case "users":
                    if (user == null) {
                        outputStream.println("*** ERR: user must log in to view all users");
                        return;
                    }
                    printAllUsers();
                    break;
                case "message":
                    if (user == null) {
                        outputStream.println("*** ERR: user must log in before texting");
                        return;
                    }
                    if (arg == null) {
                        return;
                    }
                    if (parts.length == 2) {
                        sendMessage(arg);
                        return;
                    }
                    break;
                case "messages":
                    if (user == null) {
                        outputStream.println("*** ERR: user must log in to view chat history");
                        return;
                    }
                    printChatHistory();
                    break;
                case "quit": case "signout":
                    signOut();
                    outputStream.println("QUIT");
                    done = true;
                    break;
                default:
                    outputStream.println("*** ERR: not a valid command");
                    break;
            }
        }
    }
    @Override
    public void update(ChatMessage message) {
        boolean isMine = message.userOwnBy() == user;
        outputStream.println("newSms~|>$?<@" + message.userOwnBy().nameOutPut() + "~|>$?<@" +
                message.timeStampOutput() + "~|>$?<@" + message.messageOutput() + "~|>$?<@" + isMine);
    }
    @Override
    public int compareTo(CI o) {
        if (this == o)
            return 0;
        return -1;
    }
}
