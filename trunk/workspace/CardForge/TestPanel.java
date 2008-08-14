import javax.swing.*;
import java.awt.*;

public class TestPanel extends JFrame
{
  private JPanel jPanel1 = new JPanel();
  private JLabel jLabel1 = new JLabel();
  public static void main(String[] args)
  {
    TestPanel p = new TestPanel();
    p.setSize(300, 300);
    p.setVisible(true);
  }

  public TestPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
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