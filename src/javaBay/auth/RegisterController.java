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
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.io.IOException;

public class RegisterController {
    @FXML
    TextField username;
    @FXML
    TextField email;
    @FXML
    TextField password, confirm_password;

    @FXML
    Button registerBtn;

    private JavaSpace space;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;

    @FXML
    private void backBtn(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
            Stage stage = (Stage) registerBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void HandleRegisterUser(ActionEvent event) throws IOException {
        try {
            space = SpaceUtils.getSpace();
            U1753026_Authentication authTemplate = new U1753026_Authentication();
            U1753026_Authentication authStatus = (U1753026_Authentication) space.take(authTemplate, null, TWO_SECONDS);

            // if there is no QueueStatus object in the space then we can't do much, so print an error and exit
            if (authStatus == null) {
                Alerts.auctionAlert("Error no authentication service found please restart");
                System.exit(1);
            }
            //Create user locally
            int id = authStatus.nextUser;
            String name = username.getText();
            String userEmail = email.getText();
            String userPassword = password.getText();
            String userPasswordConfirm = confirm_password.getText();
            User newUser = new User(id, name, userEmail, userPassword);
            if(RegisterUser(newUser, userPasswordConfirm, authStatus)){
                Alerts.auctionAlert("User registered please login");
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
                    Stage stage = (Stage) registerBtn.getScene().getWindow();
                    stage.setScene(new Scene(root, 1200, 720));
                } catch (Exception e) {
                    e.printStackTrace();
                    Alerts.auctionAlert("Error loading Home page");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("there was an error please contact your admin");
        }

    }
    public boolean RegisterUser(User user, String userPasswordConfirm, U1753026_Authentication authStatus){
        try {

            //check if user exists
            User ifExistsTemplate = new User(user.userName);
            User ifExists = (User) space.readIfExists(ifExistsTemplate, null, TWO_MINUTES);
            //Check that the passwords match
            if (!user.userPassword.equals(userPasswordConfirm)){
                Alerts.userAlert("Your passwords don't match");
            }else if(ifExists != null){
                Alerts.userAlert("A user has already been registered with that email");
            }else{
                //write user to space
                space.write(user, null, Lease.FOREVER);
                //Update user int count
                authStatus.addItem();
                space.write(authStatus, null, Lease.FOREVER);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("there was an error please contact your admin");
        }
        //Failed return blank user
        return false;
    }
}
