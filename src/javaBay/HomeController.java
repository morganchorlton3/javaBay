package javaBay;

import javaBay.auth.UserSession;
import javaBay.listings.ListingNotify;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class HomeController {
    @FXML
    Button startAuction, updateBtn, loginBtn, registerBtn, logoutBtn, ViewUserListingBtn, createListingBtn, viewListingBtn;

    @FXML
    ListView userListings;

    @FXML
    Text userWelcome;

    private JavaSpace space;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds

    @FXML
    public void initialize() {
        UserSession user = UserSession.getInstance();
        if(user != null) {
            userWelcome.setText("Hi, " + user.getUserName());
            loginBtn.setVisible(false);
            registerBtn.setVisible(false);
            logoutBtn.setVisible(true);
            checkForLots();
        }else {
            createListingBtn.setDisable(true);
            ViewUserListingBtn.setDisable(true);
            viewListingBtn.setDisable(true);
            userListings.setDisable(true);
        }
    }

    @FXML
    private void createListing(ActionEvent event) throws IOException {
        if(checkAuthStatus()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("listings/newListing.fxml"));
                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 720));
            } catch (Exception e) {
                e.printStackTrace();
                Alerts.auctionAlert("Error loading new Listing page");
            }
        }else{
            Alerts.auctionAlert("Please login to create a listing");
        }
    }

    @FXML
    private void openAddToAuction(ActionEvent event) throws IOException {
        //addJob();
    }


    @FXML
    private void loadLogin(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("auth/login.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        }catch ( Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("Error loading login page");
        }
    }

    @FXML
    private void loadRegister(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("auth/Register.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        }catch ( Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("Error loading login page");
        }
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        UserSession user = UserSession.getInstance();
        //remove user session
        user.cleanUserSession();
        //reload Home page
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("Error loading new Listing page");
        }
        Alerts.userAlert("You have successfully been logged out");
    }

    @FXML
    private void userListing(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("listings/UserListings.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("Error loading Listing page");
        }
    }

    @FXML
    private void view(ActionEvent event) throws IOException {
        String selectedLot= (String) userListings.getSelectionModel().getSelectedItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        Lot template = new Lot(selectedLot);
        try {
            space = SpaceUtils.getSpace();
            Lot result = (Lot) space.read(template, null, TWO_SECONDS);
            System.out.println(result);
            Lot.getInstace(result.lotNumber, result.lotName, result.lotDescription, result.userID, result.userName, result.BINprice, result.currentAprice);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("listings/DetailedLot.fxml"));
                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 720));
            }catch ( Exception e) {
                e.printStackTrace();
                Alerts.auctionAlert("Error loading login page");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkForLots() {
        int jobCounter = 0;
        ObservableList userListingList = FXCollections.observableArrayList();
        Set<String> stringSet = null;
        while (true) {
            try {
                space = SpaceUtils.getSpace();
                UserSession user = UserSession.getInstance();
                int userID = user.getUserID();
                int status = 0;
                Lot template = new Lot(jobCounter);
                Lot result = (Lot) space.readIfExists(template, null, TWO_SECONDS);
                if (result.Status == 2 ) {
                    System.out.println("Lot Purchased");
                    jobCounter++;
                } else {
                    jobCounter++;
                    ///userListings.setText("Lot No: " + result.lotNumber + " Lot Name: " + result.lotName);
                    String lotToAdd = result.lotName;
                    System.out.println("Lot Name: " + result.lotName + "lot Status: " + result.Status);
                    userListings.getItems().addAll(lotToAdd);
                }
            } catch (Exception e) {
                break;
            }

        }

    }
    private boolean checkAuthStatus(){
        UserSession user = UserSession.getInstance();
        if (user == null){
            return false;
        }
        return true;
    }
}
