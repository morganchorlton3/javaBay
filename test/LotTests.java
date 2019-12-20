import javaBay.*;
import javaBay.auth.User;
import javaBay.auth.UserSession;
import javaBay.listings.DetailedLotController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;

import static junit.framework.Assert.fail;
import static net.jini.core.lease.Lease.FOREVER;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LotTests {

    private JavaSpace space;

    private static final long TWOM = 2 * 60;

   /* @Before
    public void stopAllSpaces(){
        try {
            U1753026_Auction template = new U1753026_Auction();
            space = SpaceUtils.getSpace();
            try {
                U1753026_Auction returnedObject = (U1753026_Auction) space.take(template, null, TWOM);
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " Object removed");
                //Alerts.auctionAlert("Auction Started");


            } catch (Exception e) {
                e.printStackTrace();
            }
            //Lot
            U1753026_Lot lotTemplate = new U1753026_Lot();
            try {
                U1753026_Lot returnedObject = (U1753026_Lot) space.take(lotTemplate, null, TWOM);
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " Object removed");
                //Alerts.auctionAlert("Auction Started");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Before
    public void startAuction(){
        U1753026_Auction template = new U1753026_Auction();
        try {
            space = SpaceUtils.getSpace();
            U1753026_Auction returnedObject = (U1753026_Auction)space.readIfExists(template,null, TWOM);
            if (returnedObject == null) {
                // there is no object in the space, so create one
                try {
                    U1753026_Auction u1753026AuctionStatus = new U1753026_Auction(0);
                    space.write(u1753026AuctionStatus, null, Lease.FOREVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " object is already in the space");
                //Alerts.auctionAlert("Auction Started");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

*/
    @Test
    public void Test01createLot() {
        try {
            U1753026_Auction template = new U1753026_Auction();
            space = SpaceUtils.getSpace();
            try {
                U1753026_Auction returnedObject = (U1753026_Auction) space.take(template, null, TWOM);
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " Object removed");
                //Alerts.auctionAlert("Auction Started");


            } catch (Exception e) {
                e.printStackTrace();
            }
            //Lot
            U1753026_Lot lotTemplate = new U1753026_Lot();
            try {
                U1753026_Lot returnedObject = (U1753026_Lot) space.take(lotTemplate, null, TWOM);
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " Object removed");
                //Alerts.auctionAlert("Auction Started");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        U1753026_Auction template = new U1753026_Auction();
        try {
            space = SpaceUtils.getSpace();
            U1753026_Auction returnedObject = (U1753026_Auction)space.readIfExists(template,null, TWOM);
            if (returnedObject == null) {
                // there is no object in the space, so create one
                try {
                    U1753026_Auction u1753026AuctionStatus = new U1753026_Auction(0);
                    space.write(u1753026AuctionStatus, null, Lease.FOREVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " object is already in the space");
                //Alerts.auctionAlert("Auction Started");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
            //Create Instance
            U1753026_Lot.getInstace(newU1753026Lot.lotNumber, newU1753026Lot.lotName, newU1753026Lot.lotDescription,
                    newU1753026Lot.userID, newU1753026Lot.userName, newU1753026Lot.BINprice, newU1753026Lot.currentAprice, newU1753026Lot.lotImage);

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

    @Test
    public void Test02placeBid(){
        double bid = 99.00;
        U1753026_Lot listing = U1753026_Lot.getInstance();

        // Find the transaction manager on the network
        TransactionManager mgr = SpaceUtils.getManager();
        if (mgr == null) {
            fail();
        }

        //check for space
        JavaSpace space = SpaceUtils.getSpace();

        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(mgr, TWOM);
            } catch (Exception e) {
                fail();
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                U1753026_Lot template = new U1753026_Lot(U1753026_Lot.getInstance().lotNumber);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, txn, TWOM);
                if (u1753026Lot == null) {
                    txn.abort();
                    fail();
                }

                // ... edit that object and write it back again...
                u1753026Lot.currentAprice = bid;
                u1753026Lot.Status = 1;
                space.write(u1753026Lot, txn, TWOM);
                //Clear Instance
                U1753026_Lot.emptyInstance();
                //Update instance
                U1753026_Lot.getInstace(u1753026Lot.lotNumber, u1753026Lot.lotName, u1753026Lot.lotDescription,
                        u1753026Lot.userID, u1753026Lot.userName, u1753026Lot.BINprice, u1753026Lot.currentAprice, u1753026Lot.lotImage);
            } catch (Exception e) {
                txn.abort();
                e.printStackTrace();
            }
            // ... and commit the transaction.
            txn.commit();
            //display to user that the bid has been placed
            assertEquals(bid, U1753026_Lot.getInstance().currentAprice, 0.01);

        } catch (Exception e) {

        }

    }

    @Test
    public void Test03updateLot(){
        U1753026_Lot listing = U1753026_Lot.getInstance();
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // Find the transaction manager on the network
        TransactionManager mgr = SpaceUtils.getManager();
        if (mgr == null) {
            //Alerts.space("Failed to find Transaction Manager please check it is running");
            fail("Failed to find Transaction Manager please check it is running");
            System.exit(1);
        }

        //check for space
        JavaSpace space = SpaceUtils.getSpace();
        if (space == null) {
            //Alerts.space("Failed to find space please check it is running");
            fail("Failed to find space please check it is running");
        }

        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(mgr, TWOM);
            } catch (Exception e) {
                //Alerts.space("Error Creating Transaction");
                fail("Error Creating Transaction");
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, txn, TWOM);
                if (u1753026Lot == null) {
                    //Alerts.space("Failed to find the lot");
                    fail("Failed to find the lot");
                    txn.abort();
                    //Alerts.auctionAlert("Error Placing Bid");
                    fail("Error Placing Bid");
                }

                // ... edit that object and write it back again...
                u1753026Lot.lotName = "Updated " + u1753026Lot.lotName;
                space.write(u1753026Lot, txn, TWOM);
                //Clear Instance
                U1753026_Lot.emptyInstance();
                //Update instance
                U1753026_Lot.getInstace(u1753026Lot.lotNumber, u1753026Lot.lotName, u1753026Lot.lotDescription,
                        u1753026Lot.userID, u1753026Lot.userName, u1753026Lot.BINprice, u1753026Lot.currentAprice, u1753026Lot.lotImage);
            } catch (Exception e) {
                //Alerts.space("Failed to write to space");
                fail("Failed to write to space");
                txn.abort();
                assertEquals("Updated Test Lot", U1753026_Lot.getInstance().lotName);
            }

            // ... and commit the transaction.
            txn.commit();

        } catch (Exception e) {
            //Alerts.space("Transaction Failed");
            fail("Transaction Failed");
        }
    }

    @Test
    public void Test04BuyLot(){
        U1753026_Lot listing = U1753026_Lot.getInstance();
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // Find the transaction manager on the network
        TransactionManager mgr = SpaceUtils.getManager();
        if (mgr == null) {
            //Alerts.space("Failed to find Transaction Manager please check it is running");
            fail("Failed to find Transaction Manager please check it is running");
            System.exit(1);
        }

        //check for space
        JavaSpace space = SpaceUtils.getSpace();
        if (space == null) {
            //Alerts.space("Failed to find space please check it is running");
            fail("Failed to find space please check it is running");
        }

        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(mgr, TWOM);
            } catch (Exception e) {
                //Alerts.space("Error Creating Transaction");
                fail("Error Creating Transaction");
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, txn, TWOM);
                if (u1753026Lot == null) {
                    //Alerts.space("Failed to find the lot");
                    fail("Failed to find the lot");
                    txn.abort();
                    //Alerts.auctionAlert("Error Placing Bid");
                    fail("Error Placing Bid");
                }

                // ... edit that object and write it back again...
                u1753026Lot.Status = 2;
                space.write(u1753026Lot, txn, FOREVER);
                //Clear Instance
                U1753026_Lot.emptyInstance();
                //Update instance
                U1753026_Lot.getInstace(u1753026Lot.lotNumber, u1753026Lot.lotName, u1753026Lot.lotDescription,
                        u1753026Lot.userID, u1753026Lot.userName, u1753026Lot.BINprice, u1753026Lot.currentAprice, u1753026Lot.lotImage);
            } catch (Exception e) {
                //Alerts.space("Failed to write to space");
                fail("Failed to write to space");
                txn.abort();
                assertTrue(U1753026_Lot.getInstance().Status == 2);
            }

            // ... and commit the transaction.
            txn.commit();

        } catch (Exception e) {
            //Alerts.space("Transaction Failed");
            fail("Transaction Failed");
        }
    }
    @Test
    public void Test04removeLot() {
        U1753026_Lot listing = U1753026_Lot.getInstance();
        JavaSpace space = SpaceUtils.getSpace();
        try {
            U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
            U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, null, TWOM);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
            U1753026_Lot u1753026Lot = (U1753026_Lot) space.read(template, null, TWOM);
            if (u1753026Lot == null){
                assertTrue(true);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}