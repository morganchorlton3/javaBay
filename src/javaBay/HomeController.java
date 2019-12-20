package javaBay;

import javaBay.auth.UserSession;
import javaBay.listings.ListingChecker;
import javaBay.listings.ListingNotify;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HomeController {
    @FXML
    Button startAuction, updateBtn, loginBtn, registerBtn, logoutBtn, ViewUserListingBtn, createListingBtn, viewListingBtn;

    @FXML
    ListView<U1753026_Lot> userListings;

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
        //Get String from list view
        String selectedLot = (String) userListings.getSelectionModel().getSelectedItems().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        //manipulate String
        //System.out.println(selectedLot);
        String[] splitString = selectedLot.split("ID: ");
        int selectedItemID  = Integer.parseInt(splitString[1]);

        U1753026_Lot template = new U1753026_Lot(selectedItemID);
        try {
            space = SpaceUtils.getSpace();
            U1753026_Lot result = (U1753026_Lot) space.read(template, null, TWOS);

            //clear instance in case it has already been used
            U1753026_Lot.emptyInstance();
            //Check if the status has been bought
            /*if (result.Status != 0 && result.Status != 1){
                Alerts.auctionAlert("Sorry this item has just been bought");
            }else {*/
                //set lot instance to be used in next window
                U1753026_Lot.getInstace(result.lotNumber, result.lotName, result.lotDescription, result.userID, result.userName, result.BINprice, result.currentAprice, result.lotImage);
                //load new window
                try {
                    //Load A New Window
                    /*root = FXMLLoader.load(getClass().getResource("listings/DetailedLot.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Listing: " + result.lotName);
                    stage.setScene(new Scene(root, 450, 450));
                    stage.show();*/
                    Parent root = FXMLLoader.load(getClass().getResource("listings/DetailedLot.fxml"));
                    Stage stage = (Stage) loginBtn.getScene().getWindow();
                    stage.setScene(new Scene(root, 1200, 720));

                } catch (Exception e) {
                    e.printStackTrace();
                    Alerts.auctionAlert("Error loading Listing page");
                }
           // }
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
                U1753026_Lot template = new U1753026_Lot(jobCounter);
                U1753026_Lot result = (U1753026_Lot) space.readIfExists(template, null, TWOS);
                //Print out lot
                //System.out.println(result.toString());

                if(result.Status == 1){
                    //Lot has an active bid
                    if (result.userID == userID){
                        //If the user logged in display alert for bid to accept
                        Alerts.bidToAccept(result);
                    }
                    //Increase Job Counter
                    jobCounter++;
                    //Set cell inside list view with current lot
                    userListings.getItems().add(result);
                    //setCell(result);
                    updateCell();
                }else if (result.Status == 2 | result.Status == 3 ) {
                    //Lot already purchased Don't show and go to next item
                    jobCounter++;
                } else {
                    //Add job to list view
                    userListings.getItems().add(result);
                    //setCell(result);
                    updateCell();
                    jobCounter++;
                }
            } catch (Exception e) {
                break;
            }
        }

    }
    private void updateCell(){
        userListings.setCellFactory((list) -> {
            return new ListCell<U1753026_Lot>() {
                private ImageView imageView = new ImageView();
                @Override
                protected void updateItem(U1753026_Lot item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) return;
                    try {
                        // get Image
                        ByteArrayInputStream bis = new ByteArrayInputStream(item.lotImage);
                        BufferedImage bImage = ImageIO.read(bis);
                        Image image = SwingFXUtils.toFXImage(bImage, null);
                        //Set Image inside List view
                        imageView.setImage(image);
                        //Set fixed width and height
                        imageView.setFitWidth(150);
                        imageView.setFitHeight(150);
                        //Update text in list view
                        setGraphic(imageView);
                        setText(item.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
        });
    }
    /*private void setCell(U1753026_Lot lot){
        userListings.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(userListing item, boolean empty) {
               // super.updateItem(lot.toString(), empty);
                //System.out.println(name);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        // get Image
                        ByteArrayInputStream bis = new ByteArrayInputStream(lot.lotImage);
                        BufferedImage bImage = ImageIO.read(bis);
                        Image image = SwingFXUtils.toFXImage(bImage, null);
                        //Set Image inside List view
                        imageView.setImage(image);
                        //Set fixed width and height
                        imageView.setFitWidth(150);
                        imageView.setFitHeight(150);
                        //Update text in list view
                        setText(name);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }*/


    //check if user is logged in
    private boolean checkAuthStatus(){
        UserSession user = UserSession.getInstance();
        if (user == null){
            return false;
        }
        return true;
    }

}
