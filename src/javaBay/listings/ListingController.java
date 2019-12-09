package javaBay.listings;

import javaBay.Alerts;
import javaBay.U1753026_Auction;
import javaBay.U1753026_Lot;
import javaBay.SpaceUtils;
import javaBay.auth.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


import static net.jini.core.lease.Lease.FOREVER;

public class ListingController {
    @FXML
    Button createBtn;

    @FXML
    TextField listingName, listingDescription, listingAPrice, listingBINPrice;

    final FileChooser fileChooser = new FileChooser();

    private Desktop desktop = Desktop.getDesktop();

    private JavaSpace space;

    private static int ONES = 1000;
    private static final long TWOS = 2 * 1000;
    private static int THREES = 3000;
    private File imageFile;

    @FXML
    private void chooseImage(ActionEvent event) throws IOException {
        Stage stage = (Stage) createBtn.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }

    private void openFile(File file) {
        try{
            imageFile = file;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void createListing(ActionEvent event) throws IOException {
        String name = listingName.getText();
        String description = listingDescription.getText();

        try {
            space = SpaceUtils.getSpace();
            U1753026_Auction qsTemplate = new U1753026_Auction();
            U1753026_Auction qStatus = (U1753026_Auction)space.take(qsTemplate,null, TWOS);

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
            //Image
            BufferedImage image = ImageIO.read(imageFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            byte[] lotImg = outputStream.toByteArray();
            //create Lot
            U1753026_Lot newU1753026Lot = new U1753026_Lot(jobNumber, lotName, lotDescription, user.getUserID(), user.getUserName(), priceBTN, priceA, lotImg);
            space.write(newU1753026Lot, null, FOREVER);

            // update the QueueStatus object by incrementing the counter and write it back to the space
            qStatus.addItem();
            space.write( qStatus, null, FOREVER);
            new ListingNotify(newU1753026Lot.lotNumber);
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
                trc = TransactionFactory.create(mgr, THREES);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                U1753026_Lot template = new U1753026_Lot(jobID);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, txn, ONES);
                if (u1753026Lot == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                    System.exit(1);
                }

                // ... edit that object and write it back again...
                u1753026Lot.Status = 3;
                space.write(u1753026Lot, txn, FOREVER);
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

    public static void declineBid(int jobID){
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
                trc = TransactionFactory.create(mgr, THREES);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                U1753026_Lot template = new U1753026_Lot(jobID);
                U1753026_Lot u1753026Lot = (U1753026_Lot) space.take(template, txn, ONES);
                if (u1753026Lot == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                    System.exit(1);
                }

                // ... edit that object and write it back again...
                u1753026Lot.Status = 0;
                space.write(u1753026Lot, txn, FOREVER);
                Alerts.auctionAlert("You have declined the bid!");
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
