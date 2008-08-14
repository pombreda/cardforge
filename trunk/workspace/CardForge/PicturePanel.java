import java.awt.*;
import javax.swing.*;
import java.io.*;

public class PicturePanel extends JPanel
{
  public PicturePanel(String filename)
  {
    checkFilename(filename);

    ImageIcon i = new ImageIcon(filename);
    this.add(new JLabel(i));
  }
  private void checkFilename(String s)
  {
    File f = new File(s);
    if(! f.exists())
      throw new RuntimeException("PicturePanel : checkFilename() filename does not exist - " +s);
  }
}