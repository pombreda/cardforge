package forge;

import java.util.Random;

/**
 * <p>MyRandom class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class MyRandom {
    /** Constant <code>random</code> */
    public static Random random = new Random();

    //if percent is like 50, the its like 50% of the time will be true
    /**
     * <p>percentTrue.</p>
     *
     * @param percent a int.
     * @return a boolean.
     */
    public static boolean percentTrue(int percent) {
        return percent > random.nextInt(100);
    }
}
