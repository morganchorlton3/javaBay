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
    public static void bidToAccept(U1753026_Lot listing){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Bid");
        alert.setHeaderText("Confirm Bid of " + listing.currentAprice.toString());
        alert.setContentText(
                "You have a new bid on your item: " + listing.lotName + "\n" +
                "Do you want to confirm this bid of " + listing.currentAprice.toString() + "?"
        );
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            javaBay.listings.ListingController.acceptBid(listing.lotNumber);
        } else {
            javaBay.listings.ListingController.declineBid(listing.lotNumber);
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
