package javaBay.listings;

import javaBay.Alerts;
import javaBay.SpaceUtils;
import javaBay.U1753026_Lot;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

public class HelloWorldNotify implements RemoteEventListener {

	private JavaSpace space;
	private RemoteEventListener theStub;

	public HelloWorldNotify() {
		//Space
		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		//exporter
		Exporter myDefaultExporter =
				new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						new BasicILFactory(), false, true);

		try {
			// register this as a remote object
			theStub = (RemoteEventListener) myDefaultExporter.export(this);

			// add the notify
			U1753026_Lot template = new U1753026_Lot();
			space.notify(template, null, this.theStub, Lease.FOREVER, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notify(RemoteEvent ev) {
		//Object changed
		U1753026_Lot template = new U1753026_Lot();
		System.out.println("--- Notify ---");

		try {
			U1753026_Lot result = (U1753026_Lot)space.read(template, null, Long.MAX_VALUE);
			System.out.println("--- Read Item  ---");
			System.out.println("You have a new bid on your " + result.lotName + " At the value of £" + result.currentAprice);
			Alerts.auctionAlert("You have a new bid on your " + result.lotName + " At the value of £" + result.currentAprice );
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		//Security manager
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());


		new HelloWorldNotify();
	}
}
