package javaBay.listings;

import javaBay.Alerts;
import javaBay.U1753026_Lot;
import javaBay.SpaceUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.io.IOException;

import static net.jini.core.lease.Lease.FOREVER;

public class UpdateUserController {
    private static JavaSpace space;
    private U1753026_Lot currentU1753026Lot;
    private static final long TWO_MINUTES = 2 * 1000 * 60;

    private static int ONE_SECOND = 1000;  // 1000 milliseconds
    private static int TWO_SECONDS = 2000;  // 2000 milliseconds
    private static int THREE_SECONDS = 3000;  // 3000 milliseconds

    @FXML
    TextField lotName, lotDescription, lotBINPrice, lotStartAPrice;
    @FXML
    Button backBtn;

    @FXML
    private void backBtn(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("UserListings.fxml"));
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        U1753026_Lot listing = U1753026_Lot.getInstance();
        JavaSpace space = SpaceUtils.getSpace();
        U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
        try {
            currentU1753026Lot = (U1753026_Lot) space.read(template, null, TWO_MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentU1753026Lot.currentAprice > currentU1753026Lot.startAprice){
            lotStartAPrice.setDisable(true);
        }
        lotName.setText(currentU1753026Lot.lotName);
        lotDescription.setText(currentU1753026Lot.lotDescription);
        lotBINPrice.setText(currentU1753026Lot.BINprice.toString());
        lotStartAPrice.setText(currentU1753026Lot.startAprice.toString());
    }

    @FXML
    public void updateListing(){
        U1753026_Lot listing = U1753026_Lot.getInstance();
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
                U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, txn, ONE_SECOND);
                if (u1753026Lot == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                    System.exit(1);
                }

                // ... edit that object and write it back again...
                u1753026Lot.lotName = lotName.getText();
                u1753026Lot.lotDescription = lotDescription.getText();
                u1753026Lot.BINprice = Double.parseDouble(lotBINPrice.getText());
                if( u1753026Lot.currentAprice.equals(u1753026Lot.startAprice)){
                    u1753026Lot.startAprice = Double.parseDouble(lotStartAPrice.getText());
                    u1753026Lot.currentAprice = Double.parseDouble(lotStartAPrice.getText());
                }
                space.write(u1753026Lot, txn, FOREVER);
                Alerts.auctionAlert("Lot Updated");
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
    @FXML
    public void removeListing(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove your listing", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            U1753026_Lot listing = U1753026_Lot.getInstance();
            JavaSpace space = SpaceUtils.getSpace();
            try {
                U1753026_Lot template = new U1753026_Lot(listing.lotNumber);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, null, ONE_SECOND);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
                Stage stage = (Stage) backBtn.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 720));

            } catch (Exception e) {
                e.printStackTrace();
            }
            Alerts.auctionAlert("Lot Removed!");

        }
    }
}
