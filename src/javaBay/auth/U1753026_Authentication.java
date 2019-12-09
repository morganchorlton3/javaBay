package javaBay.auth;

import net.jini.core.entry.Entry;

public class U1753026_Authentication implements Entry {
    // Variables
    public Integer nextUser;

    // No arg contructor
    public U1753026_Authentication(){
    }

    public U1753026_Authentication(int n){
        // set count to n
        nextUser = n;
    }

    public void addItem(){
        nextUser++;
    }
}
