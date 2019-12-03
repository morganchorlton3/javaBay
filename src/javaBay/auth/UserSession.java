package javaBay.auth;

import java.util.HashSet;
import java.util.Set;

public final class UserSession {

    private static UserSession instance;

    int userID;
    private String username, userEmail;


    public UserSession(int userID, String name, String email) {
        this.userID = userID;
        this.username = name;
        this.userEmail = email;
    }

    public UserSession() {
    }

    public static UserSession getInstace(int ID, String userName, String email) {
        if(instance == null) {
            instance = new UserSession(ID, userName, email);
        }
        return instance;
    }

    public static UserSession getInstance() {
        return instance;
    }

    public String getUserName() {
        return username;
    }

    public int getUserID(){ return userID;}

    public void cleanUserSession() {
        instance = null;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
