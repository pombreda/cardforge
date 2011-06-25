package forge;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>KeyListenerTest class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class KeyListenerTest implements KeyListener {

    /** {@inheritDoc} */
    public void keyPressed(KeyEvent arg0) {
        int code = arg0.getKeyCode();

        if (code == KeyEvent.VK_ENTER) {
            //Do something here
            System.out.println("You pressed enter");
        }

    }

    /** {@inheritDoc} */
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

}
