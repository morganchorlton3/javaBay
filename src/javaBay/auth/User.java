package javaBay.auth;

import net.jini.core.entry.Entry;

public class User implements Entry {
    // Variables
    public Integer userId;
    public String userName, userEmail, userPassword;

    // No arg contructor
    public User (){
    }

    //Createuser login template
    public User ( String name, String password){
        userName = name;
        userPassword = password;
    }

    public User (String email){
        userEmail = email;
    }

    // Register user
    public User (int id, String name, String email, String password){
        userId = id;
        userName = name;
        userEmail = email;
        userPassword = password;
    }
}
