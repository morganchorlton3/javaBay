package javaBay.listings;

import javaBay.Alerts;
import javaBay.Lot;
import javaBay.SpaceUtils;
import javaBay.auth.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class UserListingController {

    private JavaSpace space;

    @FXML
    ListView activeUserListings, bidsToAcceptListings, boughtItems;

    @FXML
    Button backBtn, viewListingBtn;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds

    @FXML
    public void initialize() {

        int jobcounter = 0;
        ObservableList userListingList = FXCollections.observableArrayList();
        Set<String> stringSet = null;
        while (true) {
            try {
                space = SpaceUtils.getSpace();
                UserSession user = UserSession.getInstance();
                int userID = user.getUserID();
                System.out.println(userID);
                Lot template = new Lot(jobcounter);
                Lot result = (Lot) space.read(template, null, TWO_SECONDS);
                System.out.println(result.toString());
                if(result.userID != userID){
                    jobcounter++;
                }else if (result.Status == 0) {
                    String lotToAdd = result.lotName;
                    activeUserListings.getItems().addAll(lotToAdd);
                    jobcounter++;
                } else if (result.Status == 1){
                    ///userListings.setText("Lot No: " + result.lotNumber + " Lot Name: " + result.lotName);
                    String lotToAdd = result.lotName;
                    bidsToAcceptListings.getItems().addAll(lotToAdd);
                    activeUserListings.getItems().addAll(lotToAdd);
                    jobcounter++;
                }else if(result.Status == 2 | result.Status == 3){
                    String lotToAdd = result.lotName;
                    boughtItems.getItems().addAll(lotToAdd);
                    jobcounter++;
                }
            } catch (Exception e) {
                break;
            }

        }

    }

    @FXML
    private void backBtn(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void viewListing(ActionEvent event) throws IOException {
        String selectedLot = (String) activeUserListings.getSelectionModel().getSelectedItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        Lot template = new Lot(selectedLot);
        try {
            space = SpaceUtils.getSpace();
            Lot result = (Lot) space.read(template, null, TWO_SECONDS);
            Lot.emptyInstance();
            Lot.getInstace(result.lotNumber, result.lotName, result.lotDescription, result.userID, result.userName, result.BINprice, result.currentAprice, result.lotImage);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("UpdateUserListing.fxml"));
                Stage stage = (Stage) viewListingBtn.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 720));
            }catch ( Exception e) {
                e.printStackTrace();
                Alerts.auctionAlert("Error loading login page");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void viewBidToAccept(){
        String selectedLot = (String) bidsToAcceptListings.getSelectionModel().getSelectedItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        Lot template = new Lot(selectedLot);
        try {
            space = SpaceUtils.getSpace();
            Lot result = (Lot) space.read(template, null, TWO_SECONDS);
            Lot.emptyInstance();
            Lot.getInstace(result.lotNumber, result.lotName, result.lotDescription, result.userID, result.userName, result.BINprice, result.currentAprice, result.lotImage);
            try {
                Alerts.bidToAccept(result);
                Parent root = FXMLLoader.load(getClass().getResource("UserListings.fxml"));
                Stage stage = (Stage) viewListingBtn.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 720));
            }catch ( Exception e) {
                e.printStackTrace();
                Alerts.auctionAlert("Error loading login page");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
