package forge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * <p>Gui_ProgressBarWindow class.</p>
 *
 * @author Forge
 * @version $Id: $
 * @since 1.0.15
 */
public class Gui_ProgressBarWindow extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 5832740611050396643L;
    private final JPanel contentPanel = new JPanel();

    /**
     * Launch the application.
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        try {
            Gui_ProgressBarWindow dialog = new Gui_ProgressBarWindow();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JProgressBar progressBar = new JProgressBar();

    /**
     * Create the dialog.
     */
    public Gui_ProgressBarWindow() {
        setResizable(false);
        setTitle("Some Progress");
        Dimension screen = this.getToolkit().getScreenSize();
        setBounds(screen.width / 3, 100, //position
                450, 84); //size
        getContentPane().setLayout(null);
        contentPanel.setBounds(0, 0, 442, 58);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel);
        contentPanel.setLayout(null);
        {
            progressBar.setValue(50);
            //progressBar.setBackground(Color.GRAY);
            //progressBar.setForeground(Color.BLUE);
            progressBar.setBounds(12, 12, 418, 32);
            contentPanel.add(progressBar);
        }
    }

    /**
     * <p>setProgressRange.</p>
     *
     * @param min a int.
     * @param max a int.
     */
    public void setProgressRange(int min, int max) {
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
    }

    /**
     * <p>increment.</p>
     */
    public void increment() {
        progressBar.setValue(progressBar.getValue() + 1);

        if (progressBar.getValue() % 10 == 0)
            contentPanel.paintImmediately(progressBar.getBounds());
    }
}
