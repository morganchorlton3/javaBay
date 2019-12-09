import javaBay.U1753026_Lot;
import javaBay.auth.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LotTests {
    // Lot Tests
    @Test
    public void createLot(){
        byte[] image = new byte[0];
        U1753026_Lot testLot = new U1753026_Lot(1,"Test Lot", "Test Lot Description", 1,"test", 1.0,11.0,image);
        assertEquals("Test Lot", testLot.lotName);
    }

}