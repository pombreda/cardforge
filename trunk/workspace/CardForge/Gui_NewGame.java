

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;


import java.util.*;

public class Gui_NewGame extends JFrame
{
  private final DeckIO deckIO = new DeckIO(Constant.IO.deckFile);
  private ArrayList allDecks;
  private static Gui_DeckEditor editor;

  private JLabel titleLabel = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JLabel jLabel3 = new JLabel();
  private JComboBox humanComboBox = new JComboBox();
  private JComboBox computerComboBox = new JComboBox();
  private JButton deckEditorButton = new JButton();
  private JButton startButton = new JButton();
  private ButtonGroup buttonGroup1 = new ButtonGroup();
  private JRadioButton sealedRadioButton = new JRadioButton();
  private JRadioButton singleRadioButton = new JRadioButton();
  private JPanel jPanel2 = new JPanel();
  private Border border1;
  private TitledBorder titledBorder1;
  private JRadioButton draftRadioButton = new JRadioButton();
  private JPanel jPanel1 = new JPanel();
  private Border border2;
  private TitledBorder titledBorder2;
  private static JCheckBox newGuiCheckBox = new JCheckBox();

  public static void error(String message)
  {
/*You can e-mail this error message to mtgrares@yahoo.com.*/
    String display = "Sorry, there has been an error.\n\n" +message +"\n\nYou may need to restart this program.";
    JTextArea area = new JTextArea(display, 10, 20);

    area.setEditable(false);

    area.setLineWrap(true);
    area.setWrapStyleWord(true);

    JPanel p = new JPanel();
    area.setBackground(p.getBackground());

    JScrollPane scroll = new JScrollPane(area);
    scroll.setBorder(null);

    JOptionPane.showMessageDialog(null, scroll, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public static void main(String[] args)
  {

    try{
      Object[] o = UIManager.getInstalledLookAndFeels();
      if(3 < o.length)
      {
//      UIManager.setLookAndFeel("javax.swing.plaf.MetalLookAndFeel");
      final Color background = new Color(204, 204, 204);

      UIManager.put("Panel.background", background);
      UIManager.put("JPanel.background", background);
      UIManager.put("Button.background", background);
      UIManager.put("RadioButton.background", background);
      UIManager.put("MenuBar.background", background);
      UIManager.put("Menu.background", background);
      UIManager.put("ComboBox.background", background);
      UIManager.put("MenuItem.background", background);
      UIManager.put("Dialog.background", background);
      UIManager.put("OptionPane.background", background);
      UIManager.put("ScrollBar.background", background);
      }
    }catch(Exception ex) {}


    try{

      Constant.Runtime.GameType[0] = Constant.GameType.Constructed;
      AllZone.Computer = new ComputerAI_Input(new ComputerAI_General());

      new Gui_NewGame();

      }catch(Exception ex)
      {
        error(ex.getMessage());
      }
  }
  public Gui_NewGame()
  {
    allDecks = getDecks();
    Constant.Runtime.WinLose.reset();

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    if(Constant.Runtime.GameType[0].equals(Constant.GameType.Constructed))
    {
      singleRadioButton.setSelected(true);
      updateDeckComboBoxes();
    }
    if(Constant.Runtime.GameType[0].equals(Constant.GameType.Sealed))
    {
      sealedRadioButton.setSelected(true);
      updateDeckComboBoxes();
    }
    if(Constant.Runtime.GameType[0].equals(Constant.GameType.Draft))
    {
      draftRadioButton.setSelected(true);
      draftRadioButton_actionPerformed(null);
    }

    addListeners();

    Dimension screen = this.getToolkit().getScreenSize();
    setBounds(screen.width / 4, 50,  //position
              460, 460);  //size

    setTitle(Constant.ProgramName);
    setupMenu();
    show();


  }//init()
  private void setupMenu()
  {
    JMenuItem lookAndFeel = new JMenuItem("Display Options");

    JMenuItem download = new JMenuItem("Download Card Pictures");
    JMenuItem about = new JMenuItem("About");

    JMenu menu = new JMenu("Menu");
    menu.add(lookAndFeel);
    menu.add(download);
    menu.add(about);

    JMenuBar bar = new JMenuBar();
    bar.add(menu);
    bar.add(new MenuItem_HowToPlay());

    //change back later
    //setJMenuBar(bar);

    final HashMap map = new HashMap();
    map.put("Metal",   "javax.swing.plaf.metal.MetalLookAndFeel");
    map.put("Motif",   "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    map.put("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

    //add action listeners
    lookAndFeel.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent a)
      {

        JList list = new JList(map.keySet().toArray());

        int n = JOptionPane.showConfirmDialog(null, list, "Choose one", JOptionPane.OK_CANCEL_OPTION);
        if(n == JOptionPane.CANCEL_OPTION)
          return;

        try{
          String data = (String) map.get(list.getSelectedValue());
          UIManager.setLookAndFeel(data);
          SwingUtilities.updateComponentTreeUI(Gui_NewGame.this);
        }catch(Exception ex) {}
      }
    });
    download.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent a)
      {
        Gui_DownloadPictures.startDownload(Gui_NewGame.this);
      }
    });
    about.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent a)
      {

        JTextArea area = new JTextArea(8, 25);
        Font f = new Font(area.getFont().getName(), Font.PLAIN, 13);
        area.setFont(f);

//        area.setText("Something useful and nice");
//        area.setText("Thank you for playing this demo. I think this demo is very fun and you would really enjoy the full version. I really liked programming this project and I am very happy with the results.");
        area.setText("I enjoyed programming this project.  I'm glad other people also enjoy my program.  MTG Forge has turned out to be very successful.\n\nmtgrares@yahoo.com\nhttp://mtgrares.blogspot.com\n\nWritten by: Forge");

//        area.setText("I enjoyed programming this project.  I didn't know if it would ever get finished.  Card Warrior has turned out to be very successful.\n\nCard Warrior version 1 is done and I am slowly working on version 2.  \n\nmtgrares@yahoo.com\nhttp://mtgrares.blogspot.com\n\nWritten by: Forge\nJanuary 5, 2008");

        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);

        JPanel p =new JPanel();
        area.setBackground(p.getBackground());

        JOptionPane.showMessageDialog(Gui_NewGame.this, area, "About", JOptionPane.INFORMATION_MESSAGE);
      }
    });
  }//setupMenu()

  //returns, ArrayList of Deck objects
  private ArrayList getDecks()
  {
    ArrayList list = new ArrayList();
    Deck[] deck = deckIO.getDecks();
    for(int i = 0; i < deck.length; i++)
      list.add(deck[i]);

    Collections.sort(list, new DeckSort());
    return list;
  }
  private void addListeners()
  {
    this.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent ev)
      {
        System.exit(0);
      }
    });
  }//addListeners()
  private void setupSealed()
  {
    Deck deck = new Deck(Constant.GameType.Sealed);

    ReadBoosterPack booster = new ReadBoosterPack();
    CardList pack = booster.getBoosterPack5();

    for(int i = 0; i < pack.size(); i++)
        deck.addSideboard(pack.get(i).getName());

    Constant.Runtime.HumanDeck[0] = deck;
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140));
    titledBorder1 = new TitledBorder(border1,"Game Type");
    border2 = BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140));
    titledBorder2 = new TitledBorder(border2,"Library");
    titleLabel.setBounds(new Rectangle(155, 8, 171, 57));
    titleLabel.setText("New Game");
    titleLabel.setFont(new java.awt.Font("Dialog", 0, 26));
    this.getContentPane().setLayout(null);
    jLabel2.setText("Your Deck");
    jLabel2.setBounds(new Rectangle(9, 12, 85, 27));
    jLabel3.setText("Opponent");
    jLabel3.setBounds(new Rectangle(9, 45, 85, 27));
    humanComboBox.setBounds(new Rectangle(75, 14, 197, 23));
    humanComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        humanComboBox_actionPerformed(e);
      }
    });
    computerComboBox.setBounds(new Rectangle(75, 47, 197, 23));
    deckEditorButton.setBounds(new Rectangle(278, 24, 124, 36));
    deckEditorButton.setToolTipText("");
    deckEditorButton.setText("Deck Editor");
    deckEditorButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deckEditorButton_actionPerformed(e);
      }
    });
    startButton.setBounds(new Rectangle(159, 337, 139, 54));
    startButton.setFont(new java.awt.Font("Dialog", 0, 18));
    startButton.setHorizontalTextPosition(SwingConstants.LEADING);
    startButton.setText("Start Game");
    startButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startButton_actionPerformed(e);
      }
    });
    sealedRadioButton.setToolTipText("");
    sealedRadioButton.setText("Sealed Deck (Medium) - Create your deck from 75 available cards");
    sealedRadioButton.setBounds(new Rectangle(14, 51, 406, 28));
    sealedRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sealedRadioButton_actionPerformed(e);
      }
    });
    singleRadioButton.setText("Constructed (Easy) - Use all of the cards to defeat the computer");
    singleRadioButton.setBounds(new Rectangle(14, 17, 403, 31));
    singleRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        singleRadioButton_actionPerformed(e);
      }
    });
    jPanel2.setBorder(titledBorder1);
    jPanel2.setBounds(new Rectangle(10, 71, 425, 120));
    jPanel2.setLayout(null);
    draftRadioButton.setToolTipText("");
    draftRadioButton.setText("Booster Draft (Hard)  - Pick cards 1 at a time to create your deck");
    draftRadioButton.setBounds(new Rectangle(14, 82, 399, 25));
    draftRadioButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        draftRadioButton_actionPerformed(e);
      }
    });
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setBounds(new Rectangle(10, 209, 425, 93));
    jPanel1.setLayout(null);
    newGuiCheckBox.setText("Resizable Game Area");
    newGuiCheckBox.setBounds(new Rectangle(159, 305, 164, 25));
    this.getContentPane().add(titleLabel, null);
    jPanel1.add(computerComboBox, null);
    jPanel1.add(humanComboBox, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jLabel3, null);
    jPanel1.add(deckEditorButton, null);
    this.getContentPane().add(startButton, null);
    this.getContentPane().add(newGuiCheckBox, null);
    this.getContentPane().add(jPanel2, null);
    jPanel2.add(singleRadioButton, null);
    jPanel2.add(sealedRadioButton, null);
    jPanel2.add(draftRadioButton, null);
    this.getContentPane().add(jPanel1, null);
    buttonGroup1.add(singleRadioButton);
    buttonGroup1.add(sealedRadioButton);
    buttonGroup1.add(draftRadioButton);
  }

  void deckEditorButton_actionPerformed(ActionEvent e) {
    if(editor == null)
    {
      editor = new Gui_DeckEditor();

      {
        {
          Command exit = new Command()
          {
            public void execute()
            {
              new Gui_NewGame();
            }
          };
          editor.show(exit);
          editor.show();
        }//run()
      }
    }//if

    editor.show();
    dispose();
  }

  void startButton_actionPerformed(ActionEvent e)
  {
    if(humanComboBox.getSelectedItem() == null ||
       computerComboBox.getSelectedItem() == null)
      return;

    String human    = humanComboBox.getSelectedItem().toString();

    String computer = null;
    if(computerComboBox.getSelectedItem() != null)
      computer = computerComboBox.getSelectedItem().toString();

    if(draftRadioButton.isSelected())
    {
      if(human.equals("New Draft"))
      {
        dispose();
        Gui_BoosterDraft draft = new Gui_BoosterDraft();
        draft.showGui(new BoosterDraft_1());
        return;
      }
      else//load old draft
      {
        Deck[] deck = deckIO.readBoosterDeck(human);
        int index = Integer.parseInt(computer);

        Constant.Runtime.HumanDeck[0] = deck[0];
        Constant.Runtime.ComputerDeck[0] = deck[index];

        if(Constant.Runtime.ComputerDeck[0] == null)
          throw new RuntimeException("Gui_NewGame : startButton() error - computer deck is null");
      }//else - load old draft
    }//if
    else
    {
      //non-draft decks
      if(human.equals("Generate Deck") && Constant.GameType.Sealed.equals(Constant.Runtime.GameType[0]))
        Constant.Runtime.HumanDeck[0] = generateSealedDeck();
      else if(human.equals("Generate Deck") && Constant.GameType.Constructed.equals(Constant.Runtime.GameType[0]))
        Constant.Runtime.HumanDeck[0] = generateConstructedDeck();
      else
        Constant.Runtime.HumanDeck[0] = deckIO.readDeck(human);

      if(computer.equals("Generate Deck") && Constant.GameType.Sealed.equals(Constant.Runtime.GameType[0]))
        Constant.Runtime.ComputerDeck[0] = generateSealedDeck();
      else if(computer.equals("Generate Deck") && Constant.GameType.Constructed.equals(Constant.Runtime.GameType[0]))
        Constant.Runtime.ComputerDeck[0] = generateConstructedDeck();
      else
        Constant.Runtime.ComputerDeck[0] = deckIO.readDeck(computer);

    }//else

    //DO NOT CHANGE THIS ORDER, GuiDisplay needs to be created before cards are added

    if(newGuiCheckBox.isSelected())
      AllZone.Display = new GuiDisplay3();
    else
      AllZone.Display = new GuiDisplay2();

    AllZone.GameAction.newGame(Constant.Runtime.HumanDeck[0], Constant.Runtime.ComputerDeck[0]);
    AllZone.Display.show();

    dispose();
  }//startButton_actionPerformed()

  private Deck generateSealedDeck()
  {
    GenerateSealedDeck gen = new GenerateSealedDeck();
    CardList name = gen.generateDeck();
    Deck deck = new Deck(Constant.GameType.Sealed);

    for(int i = 0; i < 40; i++)
      deck.addMain(name.get(i).getName());
    return deck;
  }


  private Deck generateConstructedDeck()
  {
    GenerateConstructedDeck gen = new GenerateConstructedDeck();
    CardList name = gen.generateDeck();
    Deck deck = new Deck(Constant.GameType.Constructed);

    for(int i = 0; i < 60; i++)
      deck.addMain(name.get(i).getName());
    return deck;
  }

  void singleRadioButton_actionPerformed(ActionEvent e) {
    Constant.Runtime.GameType[0] = Constant.GameType.Constructed;
    updateDeckComboBoxes();
  }

  void sealedRadioButton_actionPerformed(ActionEvent e) {
    Constant.Runtime.GameType[0] = Constant.GameType.Sealed;
    updateDeckComboBoxes();
  }
  private void updateDeckComboBoxes()
  {
    humanComboBox.removeAllItems();
    computerComboBox.removeAllItems();

    if(Constant.GameType.Sealed.equals(Constant.Runtime.GameType[0]) ||
       Constant.GameType.Constructed.equals(Constant.Runtime.GameType[0]))
    {
      humanComboBox.addItem("Generate Deck");
      computerComboBox.addItem("Generate Deck");
    }

    Deck d;
    for(int i = 0; i < allDecks.size(); i++)
    {
      d = (Deck)allDecks.get(i);
      if(d.getDeckType().equals(Constant.Runtime.GameType[0]))
      {
        humanComboBox.addItem(d.getName());
        computerComboBox.addItem(d.getName());
      }
    }//for

    if(Constant.Runtime.HumanDeck[0] != null)
      humanComboBox.setSelectedItem(Constant.Runtime.HumanDeck[0].getName());

  }/*updateComboBoxes()*/

  void draftRadioButton_actionPerformed(ActionEvent e)
  {
    Constant.Runtime.GameType[0] = Constant.GameType.Draft;
    humanComboBox.removeAllItems();
    computerComboBox.removeAllItems();

    humanComboBox.addItem("New Draft");
    Object[] key = deckIO.getBoosterDecks().keySet().toArray();
    Arrays.sort(key);
    for(int i = 0; i < key.length; i++)
      humanComboBox.addItem(key[i]);

    for(int i = 0; i < 7; i++)
      computerComboBox.addItem("" +(i+1));
  }

  void humanComboBox_actionPerformed(ActionEvent e) {

  }/* draftRadioButton_actionPerformed() */
}