package forge;

import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: dhudson
 */
@Test(timeOut = 10)
public class ReadBoosterPackTest {
    /**
     *
     *
     */
    @Test(timeOut = 1000)
    public void ReadBoosterPackTest1() {
        //testing
        ReadBoosterPack r = new ReadBoosterPack();


        for (int i = 0; i < 1000; i++) {
            r.getBoosterPack5();
        }
    }//main()
}
