package javaBay.listings;

import javaBay.Alerts;
import javaBay.U1753026_Lot;
import javaBay.SpaceUtils;
import javafx.embed.swing.SwingFXUtils;
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
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static net.jini.core.lease.Lease.FOREVER;

public class DetailedLotController {

    private static JavaSpace space;
    private U1753026_Lot currentU1753026Lot;
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
    public void initialize() throws IOException {
        U1753026_Lot listing = U1753026_Lot.getInstance();
        JavaSpace space = SpaceUtils.getSpace();
        U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
        try {
            currentU1753026Lot = (U1753026_Lot) space.read(template, null, TWO_MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentU1753026Lot.Status == 2){
            bidValue.setDisable(true);
            placeBid.setDisable(true);
        }
        lotName.setText("Lot Name: " + currentU1753026Lot.lotName);
        ByteArrayInputStream bis = new ByteArrayInputStream(currentU1753026Lot.lotImage);
        BufferedImage bImage= ImageIO.read(bis);
        Image image = SwingFXUtils.toFXImage(bImage, null);
        lotImage.setImage(image);
        lotDescription.setText("Lot Description: " + currentU1753026Lot.lotDescription);
        lotBINPrice.setText("Buy It Now Price: " + currentU1753026Lot.BINprice.toString());
        lotCurrentAPrice.setText("Current Auction Price: " + currentU1753026Lot.currentAprice.toString());
        lotStartAPrice.setText("Start Auction Price: " + currentU1753026Lot.startAprice.toString());

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
        U1753026_Lot listing = U1753026_Lot.getInstance();
        if(bidValue.getText().matches("[a-zA-Z]+")){
            Alerts.userAlert("Your Bid can only contain numbers");
        }else if(Double.parseDouble(bidValue.getText()) < listing.startAprice){
            Alerts.userAlert("Your bid cant be lower than the current bid");
        }else if(Double.parseDouble(bidValue.getText()) > listing.BINprice){
            Alerts.userAlert("Your bid cant be higher than the buy it now price");
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
            Alerts.space("Transaction Failed");
        }
    }

    public void placeBid(Double bid){
        U1753026_Lot listing = U1753026_Lot.getInstance();

        // Find the transaction manager on the network
        TransactionManager mgr = SpaceUtils.getManager();
        if (mgr == null) {
            Alerts.space("Failed to create Transaction manager, please try again later");
        }

        //check for space
        JavaSpace space = SpaceUtils.getSpace();
        if (space == null) {
            Alerts.space("Failed to find javaspace");
        }

        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(mgr, Lease.FOREVER);
            } catch (Exception e) {
                Alerts.space("Couldn't not create transaction");
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, txn, Lease.FOREVER);
                if (u1753026Lot == null) {
                    Alerts.space("Failed to find lot");
                    txn.abort();
                    System.exit(1);
                }

                // ... edit that object and write it back again...
                u1753026Lot.currentAprice = bid;
                u1753026Lot.Status = 1;
                space.write(u1753026Lot, txn, Lease.FOREVER);
                //Alerts.auctionAlert("You have placed a bid with the value of: " + u1753026Lot.currentAprice);
                lotCurrentAPrice.setText("Current Auction Price: " + bid.toString());
                bidValue.clear();
            } catch (Exception e) {
                Alerts.space("Failed to read or write to space");
                txn.abort();
                System.exit(1);
            }
            // ... and commit the transaction.
            txn.commit();
        } catch (Exception e) {
            Alerts.space("Transaction Failed");
        }
    }

    public void buyItNow(){
        U1753026_Lot listing = U1753026_Lot.getInstance();
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // Find the transaction manager on the network
        TransactionManager mgr = SpaceUtils.getManager();
        if (mgr == null) {
            Alerts.space("Failed to find Transaction Manager please check it is running");
            System.exit(1);
        }

        //check for space
        JavaSpace space = SpaceUtils.getSpace();
        if (space == null) {
            Alerts.space("Failed to find space please check it is running");
            System.exit(1);
        }

        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(mgr, THREE_SECONDS);
            } catch (Exception e) {
                Alerts.space("Error Creating Transaction");
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, txn, ONE_SECOND);
                if (u1753026Lot == null) {
                    Alerts.space("Failed to find the lot");
                    txn.abort();
                    Alerts.auctionAlert("Error Placing Bid");
                }

                // ... edit that object and write it back again...
                u1753026Lot.Status = 2;
                space.write(u1753026Lot, txn, FOREVER);
                Alerts.auctionAlert("You have successfully bought this item for: " + u1753026Lot.BINprice);
            } catch (Exception e) {
                Alerts.space("Failed to write to space");
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
            Alerts.space("Transaction Failed");
        }
    }

}
