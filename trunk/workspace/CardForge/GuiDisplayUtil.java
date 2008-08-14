import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class GuiDisplayUtil
{
  public static JPanel getCardPanel(Card c)
  {
    return getCardPanel(c, c.getName());
  }

  public static JPanel getCardPanel(Card c, String name) {
		JPanel panel = new CardPanel(c);

		panel.setBorder(getBorder(c));

		Image cardImage = ImageCache.getImage(c);

		if (cardImage != null) {

			panel.setLayout(new GridLayout(1, 1));

			JLabel imageLabel = new JLabel();

			if (c.isTapped()) {
				cardImage = ImageUtil.getTappedImage(cardImage);
			}

			imageLabel.setIcon(new ImageIcon(cardImage));
			panel.add(imageLabel);
		} else {

			panel.setLayout(new GridLayout(4, 1));

			panel.add(new JLabel(name + "   " + c.getManaCost()));
			panel.add(new JLabel(formatCardType(c)));

			JPanel p1 = new JPanel();
			panel.add(p1);
			JLabel tapLabel = new JLabel();
			p1.add(tapLabel);

			if (c.isTapped()) {

				if (!c.isCreature()) {
					panel.setLayout(new GridLayout(3, 1));
				}

				p1.setBackground(Color.white);
				tapLabel.setText("Tapped");
			}

			String stats = c.getAttack() + " / " + c.getDefense();

			if (c.isCreature())
				panel.add(new JLabel(stats));

		}

		return panel;
	}
  
  public static Border getBorder(Card card)
  {
    Color color;
    if(card.isArtifact())
      color = Color.gray;
    else if(CardUtil.getColor(card).equals(Constant.Color.Black) || card.getName().equals("Swamp") || card.getName().equals("Bog"))
      color = Color.black;
    else if(CardUtil.getColor(card).equals(Constant.Color.Green) || card.getName().equals("Forest") || card.getName().equals("Grass"))
      color = new Color(0, 220, 39);
    else if(CardUtil.getColor(card).equals(Constant.Color.White) || card.getName().equals("Plains") || card.getName().equals("White Sand"))
      color = Color.white;
    else if(CardUtil.getColor(card).equals(Constant.Color.Red) || card.getName().equals("Mountain") || card.getName().equals("Rock"))
      color = Color.red;
    else if(CardUtil.getColor(card).equals(Constant.Color.Blue) || card.getName().equals("Island") || card.getName().equals("Underwater"))
      color = Color.blue;
    else
      color = Color.black;

    if(CardUtil.getColors(card).size() != 1)
    {
      color = Color.orange;
    }

    return BorderFactory.createLineBorder(color, 3);
  }
  public static MouseMotionListener getCardDetailMouse(final GuiDisplay3 visual)
  {
    return new MouseMotionAdapter()
    {
      public void mouseMoved(MouseEvent me)
      {
        JPanel panel = (JPanel)me.getSource();
        Object o = panel.getComponentAt(me.getPoint());

        if((o != null) && (o instanceof CardPanel))
        {
          CardPanel cardPanel = (CardPanel)o;
          visual.updateCardDetail(cardPanel.getCard());
        }
      }//mouseMoved
    };
  }

  public static MouseMotionListener getCardDetailMouse(final GuiDisplay2 visual)
  {
    return new MouseMotionAdapter()
    {
      public void mouseMoved(MouseEvent me)
      {
        JPanel panel = (JPanel)me.getSource();
        Object o = panel.getComponentAt(me.getPoint());

        if((o != null) && (o instanceof CardPanel))
        {
          CardPanel cardPanel = (CardPanel)o;
          visual.updateCardDetail(cardPanel.getCard());
        }
      }//mouseMoved
    };
  }



  public static String formatCardType(Card card)
  {
    ArrayList list = card.getType();
    String returnString = "";
    String s;
    for(int i = 0; i < list.size(); i++)
    {
      s = list.get(i).toString();
      if(s.equals("Creature") || s.equals("Land"))
      {
        s += " - ";
      }
      else
        s += " ";

      returnString += s;
    }

    return returnString;
  }
  public static JPanel getPicture(Card c)
  {
    if(AllZone.NameChanger.shouldChangeCardName())
      return new JPanel();

    String baseDir = Constant.IO.imageBaseDir;
    String suffix = ".jpg";


    String filename = baseDir +cleanString(c.getName()) +suffix;
    File file = new File(filename);

    //try current directory
    if(! file.exists())
    {
      filename = cleanString(c.getName()) +suffix;
      file = new File(filename);
    }


    if(file.exists())
    {
	return new PicturePanel(filename);
    }
    else
    {
      JPanel p = new JPanel();

      JTextArea text = new JTextArea("\r\n\r\n" +filename, 10, 15);
      Font f = text.getFont();
      f = f.deriveFont(f.getSize() + 2.0f);
      text.setFont(f);
      text.setBackground(p.getBackground());

      p.add(text);

      if(c.isToken())
        return new JPanel();

      return p;
    }

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
  }
  public static void setupNoLandPanel(JPanel j, Card c[])
  {
    ArrayList a = new ArrayList();
    for(int i = 0; i < c.length; i++)
      if(c[i].isCreature() || c[i].isGlobalEnchantment() || c[i].isArtifact() || c[i].isPlaneswalker())
        a.add(c[i]);

    setupPanel(j, a);
  }
  public static void setupLandPanel(JPanel j, Card c[])
  {
    ArrayList a = new ArrayList();
    for(int i = 0; i < c.length; i++)
      if(
          (!(c[i].isCreature() || c[i].isGlobalEnchantment() || c[i].isArtifact() || c[i].isPlaneswalker())) &&
           !AllZone.GameAction.isAttachee(c[i])
        )
        a.add(c[i]);

    setupPanel(j, a);
  }
  //list holds Card objects
  //puts local enchanments in the right order
  //adds "<<" to local enchanments names
  private static void setupPanel(JPanel p, ArrayList list)
  {
    //remove all local enchantments
    Card c;
    for(int i = 0; i < list.size(); i++)
    {
      c = (Card)list.get(i);
      if(c.isLocalEnchantment())
        list.remove(i);
    }

    //add local enchantments to the permanents
    //put local enchantments "next to" the permanent they are enchanting
    //the inner for loop is backward so permanents with more than one local enchantments are in the right order
    Card ca[];
    for(int i = 0; i < list.size(); i++)
    {
      c = (Card)list.get(i);
      if(c.hasAttachedCards())
      {
        ca = c.getAttachedCards();
        for(int inner = ca.length - 1; 0 <= inner; inner--)
          list.add(i + 1, ca[inner]);
      }
    }

    //add all Cards in list to the GUI, add arrows to Local Enchantments
    JPanel addPanel;
    for(int i = 0; i < list.size(); i++)
    {
      c = (Card)list.get(i);
      if(c.isLocalEnchantment())
        addPanel = getCardPanel(c, "<< " +c.getName());
      else
        addPanel = getCardPanel(c);

      p.add(addPanel);
    }
  }//setupPanel()
}
