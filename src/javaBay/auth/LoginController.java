package javaBay.auth;

import javaBay.Alerts;
import javaBay.SpaceUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;
import java.io.IOException;

public class LoginController {
    @FXML
    TextField username, password;

    @FXML
    Button loginBtn;

    private JavaSpace space;

    private static final long TWOH = 2 * 1000 * 60 *60;

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
    private void HandleloginUser(ActionEvent event) throws IOException {
        //try and log the user in
        try {
            String inputUsername  = username.getText();
            String inputPassword = password.getText();
            //creates a new template with the imputed credentials
            User user = new User(inputUsername, inputPassword);
            UserSession loggedInUser = LoginUser(user);
            if (loggedInUser != null) {
                Alerts.auctionAlert("Hi, " + loggedInUser.getUserName() + " ,you are now logged in");
                //creates a user session
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
                    Stage stage = (Stage) loginBtn.getScene().getWindow();
                    stage.setScene(new Scene(root, 1200, 720));
                } catch (Exception e) {
                    e.printStackTrace();
                    Alerts.auctionAlert("Error loading new Listing page");
                }
            }else{
                System.out.println("Login Failed");
            }
        } catch (Exception e) {
            Alerts.auctionAlert("No user found please register");
        }
    }

    public UserSession LoginUser(User user){
        try {
            space = SpaceUtils.getSpace();
            //check to see if there is a user matching the imputed template
            User loggedInUser = (User) space.readIfExists(user, null, TWOH);
            int userId = loggedInUser.userId;
            String userName = loggedInUser.userName;
            String userEmail = loggedInUser.userEmail;
            //creates a user session
            UserSession.getInstace(userId, userName, userEmail);
        } catch (Exception e) {
            Alerts.auctionAlert("No user found please register");
        }
        return UserSession.getInstance();
    }

}
