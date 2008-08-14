/*
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Gui_DownloadPictures
{
  public static void startDownload(JFrame frame)
  {
    //non-modal, not sure it if this should be modal or non-modal?
    final JDialog d = new JDialog(frame, false);
    d.setTitle("Downloading");

    Box box = Box.createVerticalBox();

    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent a1)
      {
        d.dispose();
      }
    });

    final JLabel note = new JLabel();

    box.add(Box.createVerticalStrut(30));
    box.add(note);
    box.add(Box.createVerticalStrut(30));

    JPanel p1 = new JPanel();
    p1.add(cancel);
    box.add(p1);

    d.getContentPane().add(box);

    Dimension screen = d.getToolkit().getScreenSize();

    d.setBounds(screen.width / 3, 150,  //position
                300, 200);  //size

    final Card[] card = getNeededCards();
    note.setText("Downloading card pictures, please wait...");
    if(card.length == 0)
    {
      JOptionPane.showMessageDialog(frame, "All card pictures have been downloaded.");
      return;
    }

    d.show();

    try{
      Thread thread = new Thread()
      {
        public void run()
        {
          BufferedInputStream in;
          BufferedOutputStream out;

          int n;

          for(int i = 0; i < card.length; i++)
          {
            try{
              in = new BufferedInputStream(new URL(card[i].url).openStream());
              out = new BufferedOutputStream(new FileOutputStream(card[i].name));

              while((n = in.read()) != -1)
              {
                out.write(n);

                //user cancelled
                if(! d.isDisplayable())
                {
                  in.close();
                  out.flush();
                  out.close();

                  //delete what was writtin so far
                  //File file = new File(card[i].name);
                  //file.delete();

                  return;
                }//if - cancel
              }//while - read and write file

              in.close();
              out.flush();
              out.close();
            }catch(Exception ex)
            {
              JOptionPane.showMessageDialog(null, "You must be connected to the Internet. \nError downloading card:\n" +card[i].name +"\n" +card[i].url, "Error", JOptionPane.ERROR_MESSAGE);
//              throw new RuntimeException("Gui_DownloadPictures : error 1 - " +ex);
              break;
            }
          }//for
          d.dispose();
        }//run
      };//Thread

      SwingUtilities.invokeLater(thread);
    }catch(Exception ex) {throw new RuntimeException("Gui_DownloadPictures error 2 - " +ex.toString());}
  }//startDownload()

  private static Card[] getNeededCards()
  {
    //read all card names and urls
    Card[] card = readFile();
    ArrayList list = new ArrayList();
    File file;

    //check to see which cards we already have
    for(int i = 0; i < card.length; i++)
    {
      file = new File(card[i].name);
      if(! file.exists())
        list.add(card[i]);
    }
    //return all card names and urls that are needed
    Card[] out = new Card[list.size()];
    list.toArray(out);

//    for(int i = 0; i < out.length; i++)
//      System.out.println(out[i].name +" " +out[i].url);
    return out;
  }//getNeededCards()

  //the old version read from a file, the new version uses a "global url"
  private static Card[] readFile()
  {
    //String name = card.englishName.replace(", ", "_").replaceAll("[ -]", "_").replaceAll("[':\"!]", "").toLowerCase();
    //String url = "http://mi.wizards.com/global/images/magic/general/" + name + ".jpg";

    CardList list = AllZone.CardFactory.getAllCards();
    Card[] out = new Card[list.size()];
    String url = "http://mi.wizards.com/global/images/magic/general/";

    for(int i = 0; i < list.size(); i++)
    {
      String name = cleanString(list.get(i).getName()) +".jpg";
      out[i] = new Card(name, url + name);
    }

    return out;
  }//readFile()

  public static String cleanString2(String cardName)
  {
    String name = cardName.replace(", ", "_").replaceAll("[ -]", "_").replaceAll("[':\"!]", "").toLowerCase();
    return name;
  }

  public static String cleanString(String in)
  {
    StringBuffer out = new StringBuffer();
    char c;
    for(int i = 0; i < in.length(); i++)
    {
      c = in.charAt(i);
      if(c == ' ' || c == '-')
        out.append('_');
      else if(Character.isLetterOrDigit(c))
      {
        out.append(c);
      }
    }
    return out.toString().toLowerCase();
  }//cleanString()

  static class Card
  {
    final public String name;
    final public String url;

    Card(String cardName, String cardURL)
    {
      name = cardName;
      url = cardURL;
    }
  }//Card

  public static void main(String[] args)
  {
    Gui_DownloadPictures g = new Gui_DownloadPictures();
    Card[] c = getNeededCards();

    CardList list = AllZone.CardFactory.getAllCards();
    for(int i =0; i < 10; i++)
    {
      System.out.println(c[i].name +" - " +list.get(i).getName());
    }

    System.exit(0);
  }

}
*/



import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Gui_DownloadPictures
{
  public static void startDownload(JFrame frame)
  {
    //non-modal, not sure it if this should be modal or non-modal?
    final JDialog d = new JDialog(frame, false);
    d.setTitle("Downloading");

    Box box = Box.createVerticalBox();

    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent a1)
      {
        d.dispose();
      }
    });

    final JLabel note = new JLabel();

    box.add(Box.createVerticalStrut(30));
    box.add(note);
    box.add(Box.createVerticalStrut(30));

    JPanel p1 = new JPanel();
    p1.add(cancel);
    box.add(p1);

    d.getContentPane().add(box);

    Dimension screen = d.getToolkit().getScreenSize();

    d.setBounds(screen.width / 3, 150,  //position
                300, 200);  //size

    final Card[] card = getNeededCards();
    note.setText("Downloading card pictures, please wait...");
    if(card.length == 0)
    {
      JOptionPane.showMessageDialog(frame, "All card pictures have been downloaded.");
      return;
    }

    d.show();

    try{
      Thread thread = new Thread()
      {
        public void run()
        {
          BufferedInputStream in;
          BufferedOutputStream out;

          int n;

          for(int i = 0; i < card.length; i++)
          {
            try{
              in = new BufferedInputStream(new URL(card[i].url).openStream());
              out = new BufferedOutputStream(new FileOutputStream(card[i].name));

              while((n = in.read()) != -1)
              {
                out.write(n);

                //user cancelled
                if(! d.isDisplayable())
                {
                  in.close();
                  out.flush();
                  out.close();

                  //delete what was writtin so far
                  File file = new File(card[i].name);
                  file.delete();

                  return;
                }//if - cancel
              }//while - read and write file

              in.close();
              out.flush();
              out.close();
            }catch(Exception ex)
            {
              JOptionPane.showMessageDialog(null, "You must be connected to the Internet.", "Error", JOptionPane.ERROR_MESSAGE);
//              throw new RuntimeException("Gui_DownloadPictures : error 1 - " +ex);
              break;
            }
          }//for
          d.dispose();
        }//run
      };//Thread

      SwingUtilities.invokeLater(thread);
    }catch(Exception ex) {throw new RuntimeException("Gui_DownloadPictures error 2 - " +ex.toString());}
  }//startDownload()

  private static Card[] getNeededCards()
  {
    //read all card names and urls
    Card[] card = readFile();
    ArrayList list = new ArrayList();
    File file;

    //check to see which cards we already have
    for(int i = 0; i < card.length; i++)
    {
      file = new File(card[i].name);
      if(! file.exists())
        list.add(card[i]);
    }
    //return all card names and urls that are needed
    Card[] out = new Card[list.size()];
    list.toArray(out);

//    for(int i = 0; i < out.length; i++)
//      System.out.println(out[i].name +" " +out[i].url);
    return out;
  }//getNeededCards()

  private static Card[] readFile()
  {
    try{
      BufferedReader in = new BufferedReader(new FileReader("card-pictures.txt"));
      String line;
      ArrayList list = new ArrayList();
      StringTokenizer tok;

      line = in.readLine();
      while(line != null && (! line.equals("")))
      {
        tok = new StringTokenizer(line);
        list.add(new Card(tok.nextToken(), tok.nextToken()));

        line = in.readLine();
      }

      Card[] out = new Card[list.size()];
      list.toArray(out);
      return out;

    }catch(Exception ex)
    {
      ex.printStackTrace();
      throw new RuntimeException("Gui_DownloadPictures : readFile() error");
    }
  }//readFile()

  static class Card
  {
    final public String name;
    final public String url;

    Card(String cardName, String cardURL)
    {
      name = cardName;
      url = cardURL;
    }
  }//Card
}

