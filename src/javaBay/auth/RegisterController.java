package javaBay.auth;

import javaBay.Alerts;
import javaBay.Auction;
import javaBay.Lot;
import javaBay.SpaceUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

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
    private void registerUser(ActionEvent event) throws IOException {
        try {
            space = SpaceUtils.getSpace();
            Authentication authTemplate = new Authentication();
            Authentication authStatus = (Authentication) space.take(authTemplate, null, TWO_SECONDS);

            // if there is no QueueStatus object in the space then we can't do much, so print an error and exit
            if (authStatus == null) {
                Alerts.auctionAlert("Error no authentication service found please restart");
                System.exit(1);
            }

            //User userTemplate = new User(email.getText());
            //User checkIfExists = (User) space.read(userTemplate, null, TWO_MINUTES);
            /*if (checkIfExists.userEmail.equals(email.getText())) {
                Alerts.userAlert("A user with that email already exists please login");
            }*/
            // create the new QueueItem, write it to the space, and update the GUI
            int id = authStatus.nextUser;
            String name = username.getText();
            String userEmail = email.getText();
            String userPassword = password.getText();
            User newUser = new User(id, name, userEmail, userPassword);
            space.write(newUser, null, Lease.FOREVER);

            // update the QueueStatus object by incrementing the counter and write it back to the space
            authStatus.addItem();
            space.write(authStatus, null, Lease.FOREVER);
            Alerts.auctionAlert("User registered please login");
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../Home.fxml"));
                Stage stage = (Stage) registerBtn.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 720));
            } catch (Exception e) {
                e.printStackTrace();
                Alerts.auctionAlert("Error loading Home page");

            }
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("there was an error please contact your admin");
        }
    }
}
