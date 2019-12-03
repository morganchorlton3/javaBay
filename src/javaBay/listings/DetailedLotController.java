package javaBay.listings;

import javaBay.Alerts;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.jini.core.lease.Lease.FOREVER;

public class DetailedLotController {

    private static JavaSpace space;
    private Lot currentLot;
    private static final long TWO_MINUTES = 2 * 1000 * 60;

    @FXML
    Text lotName, lotDescription, lotBINPrice, lotStartAPrice, lotCurrentAPrice;
    @FXML
    ImageView lotImage;
    @FXML
    TextField bidValue;
    @FXML
    Button back, placeBid;

    private static int ONE_SECOND = 1000;  // 1000 milliseconds
    private static int TWO_SECONDS = 2000;  // 2000 milliseconds
    private static int THREE_SECONDS = 3000;  // 3000 milliseconds

    @FXML
    public void initialize() {
        Lot listing = Lot.getInstance();
        JavaSpace space = SpaceUtils.getSpace();
        Lot template = new Lot(listing.lotNumber);
        try {
            currentLot = (Lot) space.read(template, null, TWO_MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentLot.Status == 2){
            bidValue.setDisable(true);
            placeBid.setDisable(true);
        }
        lotName.setText("Lot Name: " + currentLot.lotName);
        Image lotImg = new Image(currentLot.lotImage.toURI().toString());
        lotImage.setImage(lotImg);
        lotDescription.setText("Lot Description: " + currentLot.lotDescription);
        lotBINPrice.setText("Buy It Now Price: " + currentLot.BINprice.toString());
        lotCurrentAPrice.setText("Current Auction Price: " + currentLot.currentAprice.toString());
        lotStartAPrice.setText("Start Auction Price: " + currentLot.startAprice.toString());

    }

    @FXML
    private void backHome(ActionEvent event) throws IOException {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void placeBid(ActionEvent event) throws IOException {
        Lot listing = Lot.getInstance();
        if(bidValue.getText().matches("[a-zA-Z]+")){
            Alerts.userAlert("Your Bid can only contain numbers");
        }else if(Double.parseDouble(bidValue.getText()) < listing.startAprice){
            Alerts.userAlert("Your bid cant be lower than the current bid");
        }else{
            Double bid = Double.valueOf(bidValue.getText());
            placeBid(bid);
        }
    }

    @FXML
    private void lotBIN(ActionEvent event) throws IOException {
        try {
            buyItNow();
        }catch (Exception e){
            System.out.print("Transaction failed " + e);
        }
    }

    public void placeBid(Double bid){
        Lot listing = Lot.getInstance();
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
                Lot template = new Lot(listing.lotNumber);
                Lot lot = (Lot) space.take(template, txn, ONE_SECOND);
                if (lot == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                    System.exit(1);
                }

                // ... edit that object and write it back again...
                lot.currentAprice = bid;
                lot.Status = 1;
                space.write(lot, txn, FOREVER);
                Alerts.auctionAlert("You have placed a bid with the value of: " + lot.currentAprice);
                lotCurrentAPrice.setText("Current Auction Price: " + bid.toString());
                bidValue.clear();
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

    public void buyItNow(){
        Lot listing = Lot.getInstance();
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
                Lot template = new Lot(listing.lotNumber);
                Lot lot = (Lot) space.take(template, txn, ONE_SECOND);
                if (lot == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                    System.exit(1);
                }

                // ... edit that object and write it back again...
                lot.Status = 2;
                space.write(lot, txn, FOREVER);
                Alerts.auctionAlert("You have successfully bought this item for: " + lot.BINprice);
            } catch (Exception e) {
                System.out.println("Failed to read or write to space " + e);
                txn.abort();
                System.exit(1);
            }

            // ... and commit the transaction.
            txn.commit();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("DetailedLot.fxml"));
                Stage stage = (Stage) back.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 720));
            }catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }
    }

}
