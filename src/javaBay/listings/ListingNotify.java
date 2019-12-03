package javaBay.listings;

import javaBay.Lot;
import javaBay.SpaceUtils;
import javaBay.auth.UserSession;
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

    public ListingNotify(int jobID) {
        //System.out.println(jobID);
        // find the space
        space = SpaceUtils.getSpace();
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }

        // create the exporter
        Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                        new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'stub'
            theStub = (RemoteEventListener) myDefaultExporter.export(this);

            UserSession user = UserSession.getInstance();
            int userID = user.getUserID();
            // add the listener
            Lot template = new Lot(jobID);
            space.notify(template, null, this.theStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notify(RemoteEvent ev) {
        // this is the method called when we are notified
        // of an object of interest
        Lot template = new Lot();
        try {
            Lot listing = (Lot)space.readIfExists(template, null, Long.MAX_VALUE);
            System.out.println("----- LISTING NOTIFY -----");
            System.out.println(listing.lotName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
