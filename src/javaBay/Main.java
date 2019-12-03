package javaBay;

import javaBay.auth.Authentication;
import javaBay.listings.ListingNotify;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class Main extends Application {

    private static JavaSpace space;

    @FXML
    TextArea auctionItems;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;

    private static final long ONESECOND = 1000;  // one thousand milliseconds

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1200, 720));
        primaryStage.show();
        JavaSpace space = SpaceUtils.getSpace();
        startAuthentication(space);
        startAuction(space);
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void startAuction(JavaSpace space){
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        Auction template = new Auction();
        try {
            Auction returnedObject = (Auction)space.readIfExists(template,null, ONESECOND);
            if (returnedObject == null) {
                // there is no object in the space, so create one
                try {
                    Auction auctionStatus = new Auction(0);
                    space.write(auctionStatus, null, Lease.FOREVER);
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

    public static void startAuthentication(JavaSpace space){
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        Authentication template = new Authentication();
        try {
            Authentication returnedObject = (Authentication) space.readIfExists(template,null, ONESECOND);
            if (returnedObject == null) {
                // there is no object in the space, so create one
                try {
                    Authentication auctionStatus = new Authentication(0);
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
