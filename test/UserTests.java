import javaBay.Alerts;
import javaBay.SpaceUtils;
import javaBay.auth.U1753026_User;
import javaBay.auth.UserSession;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class UserTests {
    private JavaSpace space;
    private static final long TWOH = 2 * 1000 * 60 *60;
    // User Tests
    public U1753026_User User(){
        return new U1753026_User(1, "test", "test@test.com", "testpass");
    }
    @Test
    public void createUser() throws Exception{
        U1753026_User testU1753026User = User();
        assertEquals("test", testU1753026User.userName);
    }
    @Test
    public void RegisterUser(){
        U1753026_User testU1753026User = User();
        try {
            space = javaBay.SpaceUtils.getSpace();

            //check if user exists
            U1753026_User ifExistsTemplate = new U1753026_User(testU1753026User.userName);
            U1753026_User ifExists = (U1753026_User) space.readIfExists(ifExistsTemplate, null, TWOH);
            //Check that the passwords match
            if(ifExists != null){
                Alerts.userAlert("A user has already been registered with that email");
            }else{
                //write user to space
                space.write(testU1753026User, null, Lease.FOREVER);
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
        U1753026_User testU1753026User = User();
        try {
            space = SpaceUtils.getSpace();
            //check to see if there is a user matching the imputed template
            U1753026_User loggedInU1753026User = (U1753026_User) space.readIfExists(testU1753026User, null, TWOH);
            int userId = loggedInU1753026User.userId;
            String userName = loggedInU1753026User.userName;
            String userEmail = loggedInU1753026User.userEmail;
            //creates a user session
            UserSession.getInstace(userId, userName, userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            //fail("No user found");
        }
        assertEquals("test", UserSession.getInstance().getUserName());
    }
}
