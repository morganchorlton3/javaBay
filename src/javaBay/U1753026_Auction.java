package javaBay;

import net.jini.core.entry.Entry;

public class U1753026_Auction implements Entry {
    // Variables
    public Integer nextItem;

    // No arg contructor
    public U1753026_Auction(){
    }

    public U1753026_Auction(int n){
        // set count to n
        nextItem = n;
    }

    public void addItem(){
        nextItem++;
    }
}
