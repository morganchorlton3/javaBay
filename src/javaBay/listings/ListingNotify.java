package javaBay.listings;

import javaBay.Alerts;
import javaBay.U1753026_Auction;
import javaBay.U1753026_Lot;
import javaBay.SpaceUtils;
import javaBay.auth.U1753026_Authentication;
import javaBay.auth.UserSession;
import javafx.application.Platform;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;


public class ListingNotify implements RemoteEventListener {
    private JavaSpace space;
    private RemoteEventListener theStub;

    public ListingNotify() {
        //System.out.println(jobID);
        // find the space
        space = SpaceUtils.getSpace();
        if (space == null){
            Alerts.space("Failed to find space please check it is running");
        }else {

            // create the exporter
            Exporter myDefaultExporter =
                    new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                            new BasicILFactory(), false, true);

            try {
                // register this as a remote object
                // and get a reference to the 'stub'
                theStub = (RemoteEventListener) myDefaultExporter.export(this);

                // add the listener
                U1753026_Lot template = new U1753026_Lot();
                space.notify(template, null, this.theStub, Lease.FOREVER, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notify(RemoteEvent ev) {
        // this is the method called when we are notified
        // of an object of interest
        // add the listener
        //System.out.println("----- LISTING NOTIFY -----");
        U1753026_Lot template= new U1753026_Lot();
        try{
            U1753026_Lot result = (U1753026_Lot)space.read(template,null,Long.MAX_VALUE);
            if (result.Status == 1 && UserSession.getInstance().getUserID() == result.userID){
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Alerts.auctionAlert("You Have a bid to accept");
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
