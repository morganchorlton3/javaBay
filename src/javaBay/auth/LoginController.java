package javaBay.auth;

import javaBay.Alerts;
import javaBay.Lot;
import javaBay.SpaceUtils;
import javaBay.listings.ListingNotify;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;

import java.io.IOException;

public class LoginController {
    @FXML
    TextField username, password;

    @FXML
    Button loginBtn;


    private JavaSpace space;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_HOURS = 2 * 1000 * 60 *60;

    @FXML
    private void backBtn(ActionEvent event) throws IOException {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loginUser(ActionEvent event) throws IOException {
        try {
            String inputUsername  = username.getText();
            String inputPassword = password.getText();
            User userTemplate = new User(inputUsername, inputPassword);
            space = SpaceUtils.getSpace();
            User loggedInUser = (User) space.readIfExists(userTemplate, null, TWO_HOURS);
            int userId = loggedInUser.userId;
            String userName = loggedInUser.userName;
            String userEmail = loggedInUser.userEmail;
            UserSession.getInstace(userId, userName, userEmail);
            Alerts.auctionAlert("user ID: " + userId + ", Username: " + userName + ", user email: " + userEmail);
            Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
            new ListingNotify();
        } catch (Exception e) {
            Alerts.auctionAlert("No user found please register");
        }
    }

}
