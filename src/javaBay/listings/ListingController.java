package javaBay.listings;

import javaBay.Alerts;
import javaBay.Auction;
import javaBay.Lot;
import javaBay.SpaceUtils;
import javaBay.auth.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.io.IOException;

import static net.jini.core.lease.Lease.FOREVER;

public class ListingController {
    @FXML
    Button createBtn;

    @FXML
    TextField listingName, listingDescription, listingAPrice, listingBINPrice;

    private JavaSpace space;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;

    private static int ONE_SECOND = 1000;  // 1000 milliseconds
    private static int THREE_SECONDS = 3000;  // 3000 milliseconds

    private static final long ONESECOND = 1000;  // one thousand milliseconds

    @FXML
    private void createListing(ActionEvent event) throws IOException {
        String name = listingName.getText();
        String description = listingDescription.getText();

        try {
            space = SpaceUtils.getSpace();
            Auction qsTemplate = new Auction();
            Auction qStatus = (Auction)space.take(qsTemplate,null, TWO_SECONDS);

            // if there is no QueueStatus object in the space then we can't do much, so print an error and exit
            if (qStatus == null){
                System.out.println("No " + qsTemplate.getClass().getName() + " object found.  Has 'StartPrintQueue' been run?");
                System.exit(1);
            }

            // create the new QueueItem, write it to the space, and update the GUI
            int jobNumber = qStatus.nextItem;
            String lotName = listingName.getText();
            String lotDescription = listingDescription.getText();
            Double priceBTN = Double.parseDouble(listingBINPrice.getText());
            Double priceA = Double.parseDouble(listingAPrice.getText());
            UserSession user = UserSession.getInstance();
            Lot newLot = new Lot(jobNumber, lotName, lotDescription, user.getUserID(), user.getUserName(), priceBTN, priceA);
            space.write( newLot, null, FOREVER);

            // update the QueueStatus object by incrementing the counter and write it back to the space
            qStatus.addItem();
            space.write( qStatus, null, FOREVER);
            new ListingNotify(newLot.lotNumber);
            Alerts.auctionAlert("Lot added to auction");

            try {
                Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
                Stage stage = (Stage) createBtn.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 720));
            }catch ( Exception e) {
                e.printStackTrace();
                Alerts.auctionAlert("Error loading login page");
            }
        }  catch ( Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("Error adding Lot to Auction");
        }
    }

    public static void acceptBid(int jobID){
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // Find the transaction manager on the network
        TransactionManager mgr = SpaceUtils.getManager();
        if (mgr == null) {
            System.err.println("Failed to find the transaction manager");
            System.exit(1);
        }

        //check for space
        JavaSpace space = SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }

        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(mgr, THREE_SECONDS);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                Lot template = new Lot(jobID);
                Lot lot = (Lot) space.take(template, txn, ONE_SECOND);
                if (lot == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                    System.exit(1);
                }

                // ... edit that object and write it back again...
                lot.Status = 3;
                space.write(lot, txn, FOREVER);
                Alerts.auctionAlert("You have accepted the bid your item has now sold!");
            } catch (Exception e) {
                System.out.println("Failed to read or write to space " + e);
                txn.abort();
                System.exit(1);
            }

            // ... and commit the transaction.
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }

    }
}
