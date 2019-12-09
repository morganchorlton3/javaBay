import javaBay.Alerts;
import javaBay.SpaceUtils;
import javaBay.U1753026_Lot;
import javaBay.auth.User;
import javaBay.auth.UserSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class UserTests {
    private JavaSpace space;
    private static final long TWOH = 2 * 1000 * 60 *60;
    // User Tests
    public User User(){
        return new User(1, "test", "test@test.com", "testpass");
    }
    @Test
    public void createUser() throws Exception{
        User testUser = User();
        assertEquals("test", testUser.userName);
    }
    @Test
    public void RegisterUser(){
        User testUser = User();
        try {
            space = javaBay.SpaceUtils.getSpace();

            //check if user exists
            User ifExistsTemplate = new User(testUser.userName);
            User ifExists = (User) space.readIfExists(ifExistsTemplate, null, TWOH);
            //Check that the passwords match
            if(ifExists != null){
                Alerts.userAlert("A user has already been registered with that email");
            }else{
                //write user to space
                space.write(testUser, null, Lease.FOREVER);
                //Update user int count
                //authStatus.addItem();
                //space.write(authStatus, null, Lease.FOREVER);
                assertTrue(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.auctionAlert("there was an error please contact your admin");
        }

    }

    @Test
    public void LoginUser(){
        User testUser = User();
        try {
            space = SpaceUtils.getSpace();
            //check to see if there is a user matching the imputed template
            User loggedInUser = (User) space.readIfExists(testUser, null, TWOH);
            int userId = loggedInUser.userId;
            String userName = loggedInUser.userName;
            String userEmail = loggedInUser.userEmail;
            //creates a user session
            UserSession.getInstace(userId, userName, userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            //fail("No user found");
        }
        assertEquals("test", UserSession.getInstance().getUserName());
    }
}
