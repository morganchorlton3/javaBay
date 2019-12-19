import javaBay.*;
import javaBay.auth.User;
import javaBay.auth.UserSession;
import javaBay.listings.DetailedLotController;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;

import static junit.framework.Assert.fail;
import static org.junit.Assert.*;

public class LotTests {

    private JavaSpace space;

    private static final long TWOM = 2 * 1000 * 60;

    @Before
    public void startAuction(){
        JavaSpace space = SpaceUtils.getSpace();
        U1753026_Auction template = new U1753026_Auction();
        try {
            U1753026_Auction returnedObject = (U1753026_Auction)space.readIfExists(template,null, TWOM);
            if (returnedObject == null) {
                // there is no object in the space, so create one
                try {
                    U1753026_Auction u1753026AuctionStatus = new U1753026_Auction(0);
                    space.write(u1753026AuctionStatus, null, Lease.FOREVER);
                    assertTrue(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                assertTrue(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createLot() {
        try {
            space = SpaceUtils.getSpace();
            U1753026_Auction qsTemplate = new U1753026_Auction();
            U1753026_Auction qStatus = (U1753026_Auction)space.take(qsTemplate,null, TWOM);

            // if there is no QueueStatus object in the space then we can't do much, so print an error and exit
            if (qStatus == null) {
                Alerts.auctionAlert("No Auction service running");
            }
            // create the new QueueItem, write it to the space, and update the GUI
            int jobNumber = qStatus.nextItem;
            byte[] lotImg = new byte[0];
            //create Lot
            U1753026_Lot newU1753026Lot = new U1753026_Lot(jobNumber, "Test Lot", "Test Description", 1,"Morgan",1.0,50.0, lotImg);
            space.write(newU1753026Lot, null, Lease.FOREVER);

            // update the QueueStatus object by incrementing the counter and write it back to the space
            qStatus.addItem();
            space.write(qStatus, null, Lease.FOREVER);
            assertEquals("Test Lot", newU1753026Lot.lotName);
        }  catch ( Exception e) {
            e.printStackTrace();
            fail("Failed To Find Space");

        }

    }
    private U1753026_Lot loadLot(){
        U1753026_Lot template = new U1753026_Lot(1);
        try {
            space = SpaceUtils.getSpace();
            U1753026_Lot result = (U1753026_Lot) space.read(template, null, Lease.FOREVER);
            //clear instance in case it has already been used
            return result;
        }catch(Exception e){
            e.printStackTrace();
            fail("Failed To Find Space");
        }
        return null;
    }

    //@Test
    /*public void placeBid(){
        U1753026_Lot template = new U1753026_Lot(1);
        try {
            space = SpaceUtils.getSpace();
            U1753026_Lot result = (U1753026_Lot) space.read(template, null, Lease.FOREVER);
            //clear instance in case it has already been used
            assertEquals("Test Lot", result.lotName);
        }catch(Exception e){
            e.printStackTrace();
            fail("Failed To Find Space");
        }
        assertFalse(true);
    }*/

    /*
    @Test
    public void placeBid(){
        try{
            if (System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());

            // Find the transaction manager on the network
            TransactionManager mgr = SpaceUtils.getManager();
            if (mgr == null) {
                fail("Failed to create the transaction manager");
            }

            //check for space
            JavaSpace space = SpaceUtils.getSpace();
            if (space == null) {
                fail("Failed to find the space");
            }

            try {
                // First we need to create the transaction object
                Transaction.Created trc = null;
                try {
                    trc = TransactionFactory.create(mgr, Lease.FOREVER);
                } catch (Exception e) {
                    fail("Couldn't create the transaction");
                }

                Transaction txn = trc.transaction;

                // Now take the initial object back out of the space...
                try {
                    U1753026_Lot listing = loadLot();
                    System.out.println(listing.toString());
                    if (listing== null) {
                        Alerts.space("Failed to find lot");
                        txn.abort();
                        fail("Failed To Find Space");
                    }

                    double bid = 99.00;

                    listing.currentAprice = bid; //Set to Test Value
                    listing.Status = 1;
                    space.write(listing, txn, Lease.FOREVER);

                    //Check the bid has been placed
                    U1753026_Lot result = loadLot();
                    assertEquals(bid, result.currentAprice, 00.00);

                } catch (Exception e) {
                    txn.abort();
                    fail("Failed to read space");
                }
                // ... and commit the transaction.
                txn.commit();
            } catch (Exception e) {
                fail("Failed To Find Space");
            }
        }catch (Exception e){
            e.printStackTrace();
            fail("Failed To Find Space");
        }
    }*/

}