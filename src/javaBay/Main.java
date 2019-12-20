package javaBay;

import javaBay.auth.U1753026_Authentication;
import javaBay.listings.ListingNotify;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.rmi.server.ExportException;

public class Main extends Application {

    private static JavaSpace space;

    @FXML
    TextArea auctionItems;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;

    private static final long ONESECOND = 1000;  // one thousand milliseconds

    ListingNotify notifyer;

    @Override
    public void start(Stage primaryStage) throws Exception{
        JavaSpace space = SpaceUtils.getSpace();
        if (space == null){
            Alerts.space("Failed to find space please check it is running");
        }else{
            //Testing Stop Auction
            //stopAllSpaces(space);

            startAuthentication(space);
            startAuction(space);
            //Try
            if (System.getSecurityManager() == null)
                System.setSecurityManager(new SecurityManager());

             notifyer = new ListingNotify();



        }
        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        primaryStage.setTitle("Java Bay");
        primaryStage.setScene(new Scene(root, 1200, 720));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void startAuction(JavaSpace space){
        U1753026_Auction template = new U1753026_Auction();
        try {
            U1753026_Auction returnedObject = (U1753026_Auction)space.readIfExists(template,null, ONESECOND);
            if (returnedObject == null) {
                // there is no object in the space, so create one
                try {
                    U1753026_Auction u1753026AuctionStatus = new U1753026_Auction(0);
                    space.write(u1753026AuctionStatus, null, Lease.FOREVER);
                    //System.out.println(template.getClass().getName() + " object added to space");
                    Alerts.auctionAlert("Object Added to Space");
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

    public static void stopAllSpaces(JavaSpace space){
        U1753026_Auction template = new U1753026_Auction();
        try {
            U1753026_Auction returnedObject = (U1753026_Auction)space.take(template,null, ONESECOND);
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " Object removed");
                //Alerts.auctionAlert("Auction Started");


        } catch (Exception e) {
            e.printStackTrace();
        }
        //Lot
        U1753026_Lot lotTemplate = new U1753026_Lot();
        try {
            U1753026_Lot returnedObject = (U1753026_Lot)space.take(lotTemplate,null, ONESECOND);
            // there is already an object available, so don't create one
            System.out.println(template.getClass().getName() + " Object removed");
            //Alerts.auctionAlert("Auction Started");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startAuthentication(JavaSpace space){
        U1753026_Authentication template = new U1753026_Authentication();
        try {
            U1753026_Authentication returnedObject = (U1753026_Authentication) space.readIfExists(template,null, ONESECOND);
            if (returnedObject == null) {
                // there is no object in the space, so create one
                try {
                    U1753026_Authentication auctionStatus = new U1753026_Authentication(0);
                    space.write(auctionStatus, null, Lease.FOREVER);
                    //System.out.println(template.getClass().getName() + " object added to space");
                    Alerts.userAlert("Authentication service started started");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " object is already in the space");
                //Alerts.userAlert("Authentication service already started");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
