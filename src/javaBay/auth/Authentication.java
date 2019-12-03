package javaBay.auth;

import net.jini.core.entry.Entry;

public class Authentication implements Entry {
    // Variables
    public Integer nextUser;

    // No arg contructor
    public Authentication (){
    }

    public Authentication(int n){
        // set count to n
        nextUser = n;
    }

    public void addItem(){
        nextUser++;
    }
}
