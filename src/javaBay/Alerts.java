package javaBay;

import javafx.scene.control.Alert;

public class Alerts {
    public static void auctionAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Auction Status");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.show();
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
