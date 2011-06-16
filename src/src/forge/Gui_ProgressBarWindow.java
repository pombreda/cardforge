package forge;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import java.awt.Color;

public class Gui_ProgressBarWindow extends JDialog {

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
		setTitle("Some Progress");
		setResizable(false);
		setBounds(100, 100, 450, 84);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 442, 58);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		{
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
