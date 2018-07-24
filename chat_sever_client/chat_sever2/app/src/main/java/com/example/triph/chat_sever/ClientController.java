package com.example.triph.chat_sever;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

class ClientController {
    private static volatile boolean stop = false;
    private static volatile ConnectionListener connectionListener;
    private static volatile LoginStateListener loginStateListener;
    private static volatile MessageListener messageListener;
    private static volatile NameListener nameListener;
    private static volatile LogOutListener logOutListener;
    private static volatile PrintStream printStream;
    private static volatile Scanner scanner;
    private static ConcurrentLinkedQueue<String> commands = new ConcurrentLinkedQueue<>();
    static void setConnectionListener(ConnectionListener connectionListener) {
        ClientController.connectionListener = connectionListener;
    }

    static void setLoginStateListener(LoginStateListener loginStateListener) {
        ClientController.loginStateListener = loginStateListener;
    }

    static void setMessageListener(MessageListener messageListener) {
        ClientController.messageListener = messageListener;
    }

    static void setNameListener(NameListener nameListener) {
        ClientController.nameListener = nameListener;
    }

    static void setLogOutListener(LogOutListener logOutListener) {
        ClientController.logOutListener = logOutListener;
    }

    static void addCommand(String command) {
        commands.add(command);
    }

    static void connect() {
        stop = false;
        commands.clear();
        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client;
                try {
                    client = new Socket("192.168.31.225", 55240);
                    InputStream inputStream = client.getInputStream();
                    printStream = new PrintStream(client.getOutputStream(),true);
                    scanner = new Scanner(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (connectionListener != null)
                        connectionListener.onConnectionError();
                    return;
                }
                Thread outThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!stop) {
                            String command = commands.poll();
                            if (command != null)
                                printStream.println(command);
                        }
                        printStream.close();
                        stop  = true;
                    }
                });
                outThread.start();
                Thread inThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!stop && scanner.hasNext()) {
                            String response = scanner.nextLine();
                            switch (response) {
                                case "*** ERR: username is too short":
                                    if (loginStateListener != null) {
                                        loginStateListener.onLoginFailed("Username must be at least 3 characters");
                                    }
                                    break;
                                case "*** ERR: name existed":
                                    if (loginStateListener != null) {
                                        loginStateListener.onLoginFailed("Username exists");
                                    }
                                    break;
                                case "~[8] user has logged in":
                                    if (loginStateListener != null) {
                                        loginStateListener.onLoginSuccess();
                                    }
                                    break;
                                case "~[8] user has signed out":
                                    stop = true;
                                    break;
                            }
                            String[] parts = response.split("~\\|>\\$\\?<@");
                            if(!parts[0].equals(response)) {
                                switch (parts[0]) {
                                    case "history":
                                        if (messageListener!=null) {
                                            Sms sms = new Sms(parts[1], parts[3], Long.parseLong(parts[2]), false);
                                            messageListener.onNewMessage(sms);
                                        }
                                        break;
                                    case "newSms":
                                        if (messageListener!=null) {
                                            Sms sms = new Sms(parts[1], parts[3], Long.parseLong(parts[2]), Boolean.parseBoolean(parts[4]));
                                            messageListener.onNewMessage(sms);
                                        }
                                        break;
                                    case "name":
                                        if (nameListener!=null) {
                                            nameListener.onNameReceive(parts[1]);
                                        }
                                        break;
                                    case "users":
                                        if (nameListener != null) {
                                            nameListener.onNameListReceive(response);
                                        }
                                        break;
                                }
                            }
                        }
                        scanner.close();
                        stop = true;
                        if (logOutListener!=null) {
                            logOutListener.onLogOut();
                        }
                    }
                });
                inThread.start();
            }
        });
        connect.start();
    }

    interface ConnectionListener {
        void onConnectionError();
    }

    interface LoginStateListener {
        void onLoginSuccess();

        void onLoginFailed(String e);
    }

    interface MessageListener {
        void onNewMessage(Sms sms);
    }

    interface NameListener {
        void onNameReceive(String name);

        void onNameListReceive(String user);
    }

    interface LogOutListener {
        void onLogOut();
    }
}
