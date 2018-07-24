package com.company;
import java.text.SimpleDateFormat;
import java.util.HashSet;

public class UserNameList {
    private static UserNameList usernamelist = new UserNameList();
    HashSet<User> userlist;
    private UserNameList(){
        userlist = new HashSet<>();
    }
    public static UserNameList getInstance() {
        return usernamelist;
    }
    public void insert(User user){
        userlist.add(user);
    }
    public void remove(User user){
        userlist.remove(user);
    }
    boolean userIsUnavailable(User user) {
        return userlist.contains(user);
    }
    HashSet<User> usersOutPut() {
        return userlist;
    }
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder("~o0o~ All users:");
        for (User user : userlist) {
            result.append("\n").append(user.nameOutPut()).append("\t").append(CI.simpledateformat.format(user.timeStampOutPut()));
        }
        result.append("\n..........\n\n");
        System.out.println(result.toString());
        return result.toString();
    }
}
