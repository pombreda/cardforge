package forge;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;

public class Gui_ProgressBarWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5832740611050396643L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
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
	
	public void setProgressRange(int min, int max) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
	}
	
	public void increment() {
		progressBar.setValue(progressBar.getValue() + 1);
		
		if (progressBar.getValue() % 10 == 0)
			contentPanel.paintImmediately(progressBar.getBounds());
	}
}
