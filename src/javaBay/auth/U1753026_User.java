package javaBay.auth;

import net.jini.core.entry.Entry;

public class U1753026_User implements Entry {
    // Variables
    public Integer userId;
    public String userName, userEmail, userPassword;

    // No arg contructor
    public U1753026_User(){
    }

    //Createuser login template
    public U1753026_User(String name, String password){
        userName = name;
        userPassword = password;
    }

    public U1753026_User(String email){
        userEmail = email;
    }

    // Register user
    public U1753026_User(int id, String name, String email, String password){
        userId = id;
        userName = name;
        userEmail = email;
        userPassword = password;
    }
}
