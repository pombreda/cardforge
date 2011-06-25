package forge;

import forge.error.ErrorViewer;

import javax.swing.*;
import java.awt.Color;
import java.awt.*;


/**
 * <p>TestPanel class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class TestPanel extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1 = new JLabel();

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        TestPanel p = new TestPanel();
        p.setSize(300, 300);
        p.setVisible(true);
    }

    /**
     * <p>Constructor for TestPanel.</p>
     */
    public TestPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ErrorViewer.showError(ex);
            ex.printStackTrace();
        }
    }

    /**
     * <p>jbInit.</p>
     *
     * @throws java.lang.Exception if any.
     */
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(null);
        jPanel1.setForeground(Color.orange);
        jPanel1.setBounds(new Rectangle(15, 36, 252, 156));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
        jLabel1.setForeground(new Color(70, 90, 163));
        jLabel1.setText("jLabel1");
        this.getContentPane().add(jPanel1, null);
        jPanel1.add(jLabel1, null);
    }
}
