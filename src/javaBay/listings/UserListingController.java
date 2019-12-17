package javaBay.listings;

import javaBay.Alerts;
import javaBay.U1753026_Lot;
import javaBay.SpaceUtils;
import javaBay.auth.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class UserListingController {

    private JavaSpace space;

    @FXML
    ListView<U1753026_Lot> activeUserListings, bidsToAcceptListings, boughtItems;

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
                U1753026_Lot template = new U1753026_Lot(jobcounter);
                U1753026_Lot result = (U1753026_Lot) space.read(template, null, TWO_SECONDS);
                System.out.println(result.toString());
                if(result.userID != userID){
                    jobcounter++;
                }else if (result.Status == 0) {
                    //Add item to list
                    activeUserListings.getItems().add(result);
                    //Update with image
                    updateCell(activeUserListings);
                    jobcounter++;
                } else if (result.Status == 1){
                    //Add item to list
                    bidsToAcceptListings.getItems().add(result);
                    //Update with image
                    updateCell(bidsToAcceptListings);
                    //Add item to list
                    activeUserListings.getItems().add(result);
                    //Update with image
                    updateCell(activeUserListings);
                    jobcounter++;
                }else if(result.Status == 2 | result.Status == 3){
                    //Add item to list
                    boughtItems.getItems().add(result);
                    //Update with image
                    updateCell(boughtItems);
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
        String[] splitString = selectedLot.split("ID: ");
        int selectedItemID  = Integer.parseInt(splitString[1]);
        U1753026_Lot template = new U1753026_Lot(selectedItemID);
        try {
            space = SpaceUtils.getSpace();
            U1753026_Lot result = (U1753026_Lot) space.read(template, null, TWO_SECONDS);
            U1753026_Lot.emptyInstance();
            U1753026_Lot.getInstace(result.lotNumber, result.lotName, result.lotDescription, result.userID, result.userName, result.BINprice, result.currentAprice, result.lotImage);
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

        String[] splitString = selectedLot.split("ID: ");
        int selectedItemID  = Integer.parseInt(splitString[1]);
        U1753026_Lot template = new U1753026_Lot(selectedItemID);
        try {
            space = SpaceUtils.getSpace();
            U1753026_Lot result = (U1753026_Lot) space.read(template, null, TWO_SECONDS);
            U1753026_Lot.emptyInstance();
            U1753026_Lot.getInstace(result.lotNumber, result.lotName, result.lotDescription, result.userID, result.userName, result.BINprice, result.currentAprice, result.lotImage);
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

    private void setCell(U1753026_Lot lot, ListView listView){

        listView.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
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
                        imageView.setFitWidth(60);
                        imageView.setFitHeight(60);
                        //Update text in list view
                        setText(name);
                        setGraphic(imageView);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void updateCell(ListView<U1753026_Lot> listView){
        listView.setCellFactory((list) -> {
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
                        imageView.setFitWidth(60);
                        imageView.setFitHeight(60);
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

}
