package javaBay;

import javaBay.auth.UserSession;
import javaBay.listings.ListingNotify;
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
import java.util.stream.Collectors;

public class HomeController {
    @FXML
    Button startAuction, updateBtn, loginBtn, registerBtn, logoutBtn, ViewUserListingBtn, createListingBtn, viewListingBtn;

    @FXML
    ListView userListings;

    @FXML
    Text userWelcome;

    private JavaSpace space;

    private static final long TWOS = 2 * 1000;

    //runs on load of page
    @FXML
    public void initialize() {
        //gets current user session
        UserSession user = UserSession.getInstance();
        if(user != null) {
            userWelcome.setText("Hi, " + user.getUserName());
            loginBtn.setVisible(false);
            registerBtn.setVisible(false);
            logoutBtn.setVisible(true);
            checkForLots();
        }else {
            //If there is no user disable all the buttons apart form register and login
            createListingBtn.setDisable(true);
            ViewUserListingBtn.setDisable(true);
            viewListingBtn.setDisable(true);
            userListings.setDisable(true);
        }
    }

    //Load Create listing page
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

    //Load Login page
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
    //Load register page
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

    //log user out
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

    //load all of a users individual listings
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

    //View Listing
    @FXML
    private void view(ActionEvent event) throws IOException {
        String selectedLot= (String) userListings.getSelectionModel().getSelectedItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        Lot template = new Lot(selectedLot);
        try {
            space = SpaceUtils.getSpace();
            Lot result = (Lot) space.read(template, null, TWOS);
            //clear instance in case it has already been used
            Lot.emptyInstance();
            //set lot instance to be used in next window
            Lot.getInstace(result.lotNumber, result.lotName, result.lotDescription, result.userID, result.userName, result.BINprice, result.currentAprice, result.lotImage);
            //load new window
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
        while (true) {
            try {
                space = SpaceUtils.getSpace();
                UserSession user = UserSession.getInstance();
                int userID = user.getUserID();
                Lot template = new Lot(jobCounter);
                Lot result = (Lot) space.readIfExists(template, null, TWOS);
                if(result.userID == userID){
                    new ListingNotify(result.lotNumber);
                }
                if(result.Status == 1){
                    //Lot has an active bid
                    if (result.userID == userID){
                        //If the user logged in display alert for bid to accept
                        Alerts.bidToAccept(result);
                    }
                    //add lot to list view
                    String lotToAdd = result.lotName;
                    userListings.getItems().addAll(lotToAdd);
                    jobCounter++;
                }else if (result.Status == 2 | result.Status == 3 ) {
                    //Lot already purchased Don't show and go to next item
                    jobCounter++;
                } else {
                    //Add job to list view
                    jobCounter++;
                    String lotToAdd = result.lotName;
                    userListings.getItems().addAll(lotToAdd);
                }
            } catch (Exception e) {
                break;
            }

        }

    }
    //check if user is logged in
    private boolean checkAuthStatus(){
        UserSession user = UserSession.getInstance();
        if (user == null){
            return false;
        }
        return true;
    }

}
