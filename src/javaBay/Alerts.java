package javaBay;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerts {
    public static void auctionAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Auction Status");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.show();
    }
    public static void bidToAccept(Double bid, int jobID){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Bid");
        alert.setHeaderText("Confirm Bid of " + bid.toString());
        alert.setContentText("Do you want to confirm this bid of " + bid.toString() + "?" );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            javaBay.listings.ListingController.acceptBid(jobID);
        } else {
            System.out.println("Decline");
        }
    }

    public static void userAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Authentication Alert");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.show();
    }
}
