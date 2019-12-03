package javaBay;

import net.jini.core.entry.Entry;

public class Auction implements Entry {
    // Variables
    public Integer nextItem;

    // No arg contructor
    public Auction (){
    }

    public Auction (int n){
        // set count to n
        nextItem = n;
    }

    public void addItem(){
        nextItem++;
    }
}
