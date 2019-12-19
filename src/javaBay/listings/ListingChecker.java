package javaBay.listings;

import javaBay.Alerts;
import javaBay.SpaceUtils;
import javaBay.U1753026_Lot;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

public class ListingChecker implements RemoteEventListener {

    private JavaSpace space;
    private RemoteEventListener theStub;

    public ListingChecker() {
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

            // add the listener
            U1753026_Lot template = new U1753026_Lot();
            space.notify(template, null, this.theStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // create an example object being listened for
		/*try{
			U1753026_Lot msg = new U1753026_Lot();
			msg.contents = "Hello World";
			space.write(msg, null, Lease.FOREVER);
			System.out.println("Object added");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
    }

    public void notify(RemoteEvent ev) {
        // this is the method called when we are notified
        // of an object of interest
        U1753026_Lot template = new U1753026_Lot();
        System.out.println("--- Notify ---");

        try {
            U1753026_Lot result = (U1753026_Lot)space.read(template, null, Long.MAX_VALUE);
            System.out.println("--- Read Item  ---");
            System.out.println(result.lotName);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // that's all we need to do in this demo so we can quit...
        System.exit(0);
    }

}

