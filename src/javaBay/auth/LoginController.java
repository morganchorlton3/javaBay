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
            U1753026_User u1753026User = new U1753026_User(inputUsername, inputPassword);
            UserSession loggedInUser = LoginUser(u1753026User);
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

    public UserSession LoginUser(U1753026_User u1753026User){
        try {
            space = SpaceUtils.getSpace();
            //check to see if there is a user matching the imputed template
            U1753026_User loggedInU1753026User = (U1753026_User) space.readIfExists(u1753026User, null, TWOH);
            int userId = loggedInU1753026User.userId;
            String userName = loggedInU1753026User.userName;
            String userEmail = loggedInU1753026User.userEmail;
            //creates a user session
            UserSession.getInstace(userId, userName, userEmail);
        } catch (Exception e) {
            Alerts.auctionAlert("No user found please register");
        }
        return UserSession.getInstance();
    }

}
