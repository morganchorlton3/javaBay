package javaBay.listings;

import javaBay.SpaceUtils;
import javaBay.U1753026_Lot;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

public class HelloWorldchange {
	public static void main(String args[]) {

		JavaSpace space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());

		// Find the transaction manager on the network
		TransactionManager mgr = SpaceUtils.getManager();
		if (mgr == null) {
			System.out.println("Transaction manager");
		}
		if (space == null) {
			System.out.println("Space");
		}

		try {
			// First we need to create the transaction object
			Transaction.Created trc = null;
			try {
				trc = TransactionFactory.create(mgr, Lease.FOREVER);
			} catch (Exception e) {
				System.out.println("Transaction Error");
			}

			Transaction txn = trc.transaction;

			// Now take the initial object back out of the space...
			try {
				U1753026_Lot template = new U1753026_Lot();
				U1753026_Lot object = (U1753026_Lot) space.take(template, txn, Lease.FOREVER);
				System.out.println(object.lotName);
				if (object == null) {
					System.out.println("No Object");
					txn.abort();
					System.exit(1);
				}

				//Write back again
				object.lotName= "New Message";
				System.out.println(object.toString());


				space.write(object, txn, Lease.FOREVER);

			} catch (Exception e) {
				System.out.println("Final Error");
				txn.abort();
				System.exit(1);
			}
			// ... and commit the transaction.
			txn.commit();
		} catch (Exception e) {
			System.out.println("Transaction failed");
		}
	}
}
