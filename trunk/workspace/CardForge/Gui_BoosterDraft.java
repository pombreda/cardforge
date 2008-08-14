import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class Gui_BoosterDraft extends JFrame implements CardDetail
{
  private BoosterDraft boosterDraft;

  private final boolean limitedDeckEditor = true;

  private TableModel allCardModel;
  private TableModel deckModel;

  private JScrollPane jScrollPane1 = new JScrollPane();
  private JScrollPane jScrollPane2 = new JScrollPane();
  private Border border1;
  private TitledBorder titledBorder1;
  private Border border2;
  private TitledBorder titledBorder2;
  private JPanel cardDetailPanel = new JPanel();
  private Border border3;
  private TitledBorder titledBorder3;
  private JPanel picturePanel = new JPanel();
  private JLabel statsLabel = new JLabel();
  private JTable allCardTable = new JTable();
  private JTable deckTable = new JTable();
  private JScrollPane jScrollPane3 = new JScrollPane();
  private JPanel jPanel3 = new JPanel();
  private JLabel cdLabel4 = new JLabel();
  private JLabel cdLabel1 = new JLabel();
  private JLabel cdLabel2 = new JLabel();
  private JLabel cdLabel3 = new JLabel();
  private GridLayout gridLayout1 = new GridLayout();
  private JLabel cdLabel5 = new JLabel();
  private JTextArea cdTextArea = new JTextArea();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JLabel statsLabel2 = new JLabel();
  private JButton jButton1 = new JButton();
  public static void main(String[] args)
  {
    Constant.Runtime.GameType[0] = Constant.GameType.Draft;
    Constant.Runtime.HumanDeck[0] = new Deck(Constant.GameType.Sealed);

    Gui_BoosterDraft g = new Gui_BoosterDraft();
    g.showGui(new BoosterDraftTest());
  }


  public void showGui(BoosterDraft in_boosterDraft)
  {
    boosterDraft = in_boosterDraft;

    setup();
    showChoices(boosterDraft.nextChoice());

    allCardModel.sort(1, true);
    deckModel.sort(1, true);

    show();
  }
  private void addListeners()
  {
    this.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent ev)
      {
        int n = JOptionPane.showConfirmDialog(null, "Your progress cannot be saved, do you want to exit?", "", JOptionPane.YES_NO_OPTION);
        if(n == JOptionPane.YES_OPTION)
        {
          dispose();
          new Gui_NewGame();
        }
      }//windowClosing()
    });
  }//addListeners()

  private void setup()
  {
    addListeners();
//    setupMenu();

    //construct allCardTable, get all cards
    allCardModel = new TableModel(new CardList(), (CardDetail)this);
    allCardModel.addListeners(allCardTable);
    allCardTable.setModel(allCardModel);


    //construct deckModel
    deckModel = new TableModel((CardDetail)this);
    deckModel.addListeners(deckTable);
    deckTable.setModel(deckModel);

    //add cards to GUI from deck
//    refreshGui();

    allCardTable.addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent e)
      {
        if((e.getModifiers() & e.BUTTON3_MASK) != 0)
          jButton1_actionPerformed(null);
      }
    });//MouseListener


    //get stats from deck
    deckModel.addTableModelListener(new TableModelListener()
    {
      public void tableChanged(TableModelEvent ev)
      {
        CardList deck = deckModel.getCards();
        statsLabel.setText(getStats(deck));
      }
    });


    //get stats from all cards
    allCardModel.addTableModelListener(new TableModelListener()
    {
      public void tableChanged(TableModelEvent ev)
      {
        CardList deck = allCardModel.getCards();
        statsLabel2.setText(getStats(deck));
      }
    });

    setSize(1024,  768);
  }//setupAndDisplay()

  private String getStats(CardList deck)
  {
    int total = deck.size();
    int creature = deck.getType("Creature").size();
    int land = deck.getType("Land").size();

    String show = "Total - " +total +", Creatures - " +creature +", Land - " +land;
    String[] color = Constant.Color.Colors;
    for(int i = 0; i < 5; i++)
      show += ", " +color[i] +" - " +CardListUtil.getColor(deck, color[i]).size();

    return show;
  }//getStats()

  public Gui_BoosterDraft()
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
  public void updateCardDetail(Card c)
  {
    CardDetailUtil.updateCardDetail(c, cdTextArea, cardDetailPanel, picturePanel,
      new JLabel[] {cdLabel1, cdLabel2, cdLabel3, cdLabel4, cdLabel5});
  }
  private void jbInit() throws Exception
  {
    border1 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(148, 145, 140));
    titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)),"Previous Picked Cards");
    border2 = BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140));
    titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)),"Choose one card");
    border3 = BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140));
    titledBorder3 = new TitledBorder(border3,"Card Detail");
    this.getContentPane().setLayout(null);
    jScrollPane1.setBorder(titledBorder2);
    jScrollPane1.setBounds(new Rectangle(19, 28, 661, 344));
    jScrollPane2.setBorder(titledBorder1);
    jScrollPane2.setBounds(new Rectangle(19, 478, 661, 184));
    cardDetailPanel.setBorder(titledBorder3);
    cardDetailPanel.setBounds(new Rectangle(693, 23, 239, 323));
    cardDetailPanel.setLayout(null);
    picturePanel.setBorder(BorderFactory.createEtchedBorder());
    picturePanel.setBounds(new Rectangle(698, 362, 226, 301));
    picturePanel.setLayout(borderLayout1);
    statsLabel.setFont(new java.awt.Font("Dialog", 0, 16));
    statsLabel.setText("Total - 0, Creatures - 0 Land - 0");
    statsLabel.setBounds(new Rectangle(19, 665, 665, 31));
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.setTitle("Booster Draft");
    jScrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane3.setBounds(new Rectangle(6, 168, 225, 143));
    jPanel3.setBounds(new Rectangle(7, 21, 224, 141));
    jPanel3.setLayout(gridLayout1);
    cdLabel4.setFont(new java.awt.Font("Dialog", 0, 14));
    cdLabel4.setHorizontalAlignment(SwingConstants.LEFT);
    cdLabel1.setFont(new java.awt.Font("Dialog", 0, 14));
    cdLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    cdLabel2.setFont(new java.awt.Font("Dialog", 0, 14));
    cdLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    cdLabel3.setFont(new java.awt.Font("Dialog", 0, 14));
    cdLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    gridLayout1.setColumns(1);
    gridLayout1.setRows(0);
    cdLabel5.setFont(new java.awt.Font("Dialog", 0, 14));
    cdLabel5.setHorizontalAlignment(SwingConstants.LEFT);
    cdTextArea.setFont(new java.awt.Font("Dialog", 0, 12));
    cdTextArea.setLineWrap(true);
    cdTextArea.setWrapStyleWord(true);
    statsLabel2.setBounds(new Rectangle(19, 378, 665, 31));
    statsLabel2.setText("Total - 0, Creatures - 0 Land - 0");
    statsLabel2.setFont(new java.awt.Font("Dialog", 0, 16));
    jButton1.setBounds(new Rectangle(238, 418, 147, 44));
    jButton1.setFont(new java.awt.Font("Dialog", 0, 16));
    jButton1.setText("Choose Card");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    this.getContentPane().add(cardDetailPanel, null);
    cardDetailPanel.add(jScrollPane3, null);
    jScrollPane3.getViewport().add(cdTextArea, null);
    cardDetailPanel.add(jPanel3, null);
    jPanel3.add(cdLabel1, null);
    jPanel3.add(cdLabel2, null);
    jPanel3.add(cdLabel3, null);
    jPanel3.add(cdLabel4, null);
    this.getContentPane().add(picturePanel, null);
    this.getContentPane().add(jScrollPane1, null);
    this.getContentPane().add(statsLabel2, null);
    this.getContentPane().add(statsLabel, null);
    this.getContentPane().add(jScrollPane2, null);
    this.getContentPane().add(jButton1, null);
    jScrollPane2.getViewport().add(deckTable, null);
    jScrollPane1.getViewport().add(allCardTable, null);
    jPanel3.add(cdLabel5, null);
  }

  void addButton_actionPerformed(ActionEvent e)
  {
    int n = allCardTable.getSelectedRow();
    if(n != -1)
    {
      setTitle("Deck Editor - " +Constant.Runtime.HumanDeck[0].getName() +" - changed");

      Card c = allCardModel.rowToCard(n);
      deckModel.addCard(c);
      deckModel.resort();

      if(limitedDeckEditor)
      {
        allCardModel.removeCard(c);
      }

      //3 conditions" 0 cards left, select the same row, select next row
      int size = allCardModel.getRowCount();
      if(size != 0)
      {
        if(size == n)
          n--;
        allCardTable.addRowSelectionInterval(n, n);
      }
    }//if(valid row)
  }//addButton_actionPerformed

  void removeButton_actionPerformed(ActionEvent e)
  {
    int n = deckTable.getSelectedRow();
    if(n != -1)
    {
      setTitle("Deck Editor - " +Constant.Runtime.HumanDeck[0].getName() +" - changed");

      Card c = deckModel.rowToCard(n);
      deckModel.removeCard(c);

      if(limitedDeckEditor)
      {
        allCardModel.addCard(c);
        allCardModel.resort();
      }

      //3 conditions" 0 cards left, select the same row, select next row
      int size = deckModel.getRowCount();
      if(size != 0)
      {
        if(size == n)
          n--;
        deckTable.addRowSelectionInterval(n, n);
      }
    }//if(valid row)
  }//removeButton_actionPerformed

  //if true, don't do anything else
  private boolean checkSaveDeck()
  {
    //a crappy way of checking if the deck has been saved
    if(getTitle().endsWith("changed"))
    {

      int n = JOptionPane.showConfirmDialog(null, "Do you want to save this deck?", "Save Deck", JOptionPane.YES_NO_CANCEL_OPTION);
      if(n == JOptionPane.CANCEL_OPTION)
        return true;
      else if(n == JOptionPane.YES_OPTION)
        saveItem_actionPerformed();
    }
    return false;
  }//checkSaveDeck()
  private void newItem_actionPerformed()
  {
    if(checkSaveDeck())
      return;

    setTitle("Deck Editor");

    Deck deck = Constant.Runtime.HumanDeck[0];
    while(deck.countMain() != 0)
      deck.addSideboard(deck.removeMain(0));

    refreshGui();
  }//newItem_actionPerformed
  private void closeItem_actionPerformed()
  {
    //check if saved, show dialog "yes, "no"
    checkSaveDeck();
    dispose();
  }
  private void stats_actionPerformed(CardList list)
  {

  }
  private void saveAsItem_actionPerformed()
  {
    String s = "";
    while(s!= null && s.length() == 0)
    {
      s = JOptionPane.showInputDialog(null, "Deck Name", "Save As", JOptionPane.QUESTION_MESSAGE);
    }
    if(s != null)
    {
      //see if duplicate name?
      if(AllZone.IO.getKeyList().contains(s))
      {
        int n = JOptionPane.showConfirmDialog(null, "Do you want overwrite a deck that has the same name?", "Save Deck", JOptionPane.YES_NO_OPTION);
        if(n == JOptionPane.NO_OPTION)
        {
          //get new name
          saveAsItem_actionPerformed();
          return;
        }
      }
      Constant.Runtime.HumanDeck[0].setName(s);
      saveItem_actionPerformed();
    }
  }//saveItem_actionPerformed()
  private void saveItem_actionPerformed()
  {
    Deck d = Constant.Runtime.HumanDeck[0];
    if(d.getName().length() != 0)
    {
      refreshDeck();

      Deck saveDeck = Constant.Runtime.HumanDeck[0];
      AllZone.IO.writeObject(saveDeck.getName(), saveDeck);

      setTitle("Deck Editor - " +saveDeck.getName());
    }
    else
      saveAsItem_actionPerformed();
  }
  private void openItem_actionPerformed()
  {
    if(checkSaveDeck())
      return;

    ArrayList choices = getDecks();
    if(choices.size() == 0)
    {
      JOptionPane.showMessageDialog(null, "No decks found");
      return;
    }
    Object o = JOptionPane.showInputDialog(null, "Deck Name", "Open Deck", JOptionPane.OK_CANCEL_OPTION, null, choices.toArray(), choices.get(0));
    if(o != null)
    {
      String deckName = (String)o;
      setTitle("Deck Editor - " +deckName);

      Constant.Runtime.HumanDeck[0] = (Deck) AllZone.IO.readObject(deckName);
      refreshGui();
    }
  }//openItem_actionPerformed()
  public void deleteItem_actionPerformed()
  {
    int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this deck?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
    if(n == JOptionPane.YES_OPTION)
    {
      AllZone.IO.deleteObject(Constant.Runtime.HumanDeck[0].getName());
      Constant.Runtime.HumanDeck[0] = new Deck(Constant.Runtime.GameType[0]);

      //so newItem_actionPerformed() won't ask the user if they want to save this deck
      setTitle("Deck Editor");
      newItem_actionPerformed();
    }
  }
  public void renameItem_actionPerformed()
  {
    String newName = "";
    while(newName.equals(""))
    {
      newName = JOptionPane.showInputDialog(null, "Deck Name", "Rename Deck", JOptionPane.QUESTION_MESSAGE);
      if(newName == null)
        break;
    }

    //when the user selects "Cancel"
    if(newName != null)
    {
      String oldName = Constant.Runtime.HumanDeck[0].getName();
      AllZone.IO.deleteObject(oldName);

      Constant.Runtime.HumanDeck[0].setName(newName);
      setTitle("Deck Editor - " +newName +" - changed");
    }
  }
  //returns, ArrayList of String deck names
  private ArrayList getDecks()
  {
    String type = Constant.Runtime.GameType[0];
    String[] deckName = AllZone.IO.getKeyString();
    ArrayList list = new ArrayList();

    Deck d;
    for(int i = 0; i < deckName.length; i++)
    {
      d = (Deck) AllZone.IO.readObject(deckName[i]);
      if(d.getDeckType().equals(type))
        list.add(deckName[i]);
    }

    Collections.sort(list);
    return list;
  }
  private void setupMenu()
  {
    final boolean[] isSaved = new boolean[1];

    JMenuItem newItem = new JMenuItem("New");
    JMenuItem openItem = new JMenuItem("Open");
    JMenuItem saveItem = new JMenuItem("Save");
    JMenuItem saveAsItem = new JMenuItem("Save As");
    JMenuItem renameItem = new JMenuItem("Rename");
    JMenuItem deleteItem = new JMenuItem("Delete");
    JMenuItem statsPoolItem = new JMenuItem("Statistics - Card Pool");
    JMenuItem statsDeckItem = new JMenuItem("Statistics - Deck");
    JMenuItem closeItem = new JMenuItem("Close");

    newItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      newItem_actionPerformed();
    }});
    openItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      openItem_actionPerformed();
    }});
    saveItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      saveItem_actionPerformed();
    }});
    saveAsItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      saveAsItem_actionPerformed();
    }});
    renameItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      renameItem_actionPerformed();
    }});
    deleteItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      deleteItem_actionPerformed();
    }});
    statsPoolItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      stats_actionPerformed(allCardModel.getCards());
    }});
    statsDeckItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      stats_actionPerformed(deckModel.getCards());
    }});
    closeItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      closeItem_actionPerformed();
    }});

    JMenu fileMenu = new JMenu("Deck Actions");
    fileMenu.add(newItem);
    fileMenu.add(openItem);
    fileMenu.add(saveItem);
    fileMenu.add(saveAsItem);

    fileMenu.addSeparator();
    fileMenu.add(renameItem);
    fileMenu.add(deleteItem);
//    fileMenu.add(statsPoolItem);
//    fileMenu.add(statsDeckItem);
    fileMenu.addSeparator();
    fileMenu.add(closeItem);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(fileMenu);

    this.setJMenuBar(menuBar);
  }/*setupMenu();  */

  //refresh Gui from deck, Gui shows the cards in the deck
  private void refreshGui()
  {
    Deck deck = Constant.Runtime.HumanDeck[0];
    if(deck == null) //this is just a patch, i know
      deck = new Deck(Constant.Runtime.GameType[0]);

    allCardModel.clear();
    deckModel.clear();

    Card c;
    ReadBoosterPack pack = new ReadBoosterPack();
    for(int i = 0; i < deck.countMain(); i++)
    {
      c = AllZone.CardFactory.getCard(deck.getMain(i), Constant.Player.Human);

      //add rarity to card if this is a sealed card pool
      if(! Constant.Runtime.GameType[0].equals(Constant.GameType.Constructed))
        c.setRarity(pack.getRarity(c.getName()));;

      deckModel.addCard(c);
    }//for

    if(deck.isSealed() || deck.isRegular())
    {
      //add sideboard to GUI
      for(int i = 0; i < deck.countSideboard(); i++)
      {
        c = AllZone.CardFactory.getCard(deck.getSideboard(i), Constant.Player.Human);
        c.setRarity(pack.getRarity(c.getName()));
        allCardModel.addCard(c);
      }
    }
    else
    {
      CardList all = AllZone.CardFactory.getAllCards();
      for(int i = 0; i < all.size(); i++)
        allCardModel.addCard(all.get(i));
    }

    allCardModel.resort();
    deckModel.resort();
  }//refreshGui()

  //updates Constant.Runtime.HumanDeck[0] from the cards shown in the GUI
  private void refreshDeck()
  {
    //make new Deck
    Deck deck = new Deck(Constant.Runtime.GameType[0]);
    deck.setName(Constant.Runtime.HumanDeck[0].getName());
    Constant.Runtime.HumanDeck[0] = deck;

    //update Deck with cards shown in GUI
    CardList list = deckModel.getCards();
    for(int i = 0; i < list.size(); i++)
      deck.addMain(list.get(i).getName());

    if(deck.isSealed())
    {
      //add sideboard to deck
      list = allCardModel.getCards();
      for(int i = 0; i < list.size(); i++)
        deck.addSideboard(list.get(i).getName());
    }
  }/* refreshDeck() */

  void jButton1_actionPerformed(ActionEvent e)
  {
    //pick card
    int n = allCardTable.getSelectedRow();
    if(n == -1)//is valid selection?
      return;

    Card c = allCardModel.rowToCard(n);

    deckModel.addCard(c);
    deckModel.resort();

    //get next booster pack
    boosterDraft.setChoice(c);
    if(boosterDraft.hasNextChoice())
    {
      showChoices(boosterDraft.nextChoice());
    }
    else
    {
      //quit
      saveDraft();
      dispose();
    }
  }/*OK Button*/
  private void showChoices(CardList list)
  {
    allCardModel.clear();

    ReadBoosterPack pack = new ReadBoosterPack();
    Card c;
    for(int i = 0; i < list.size(); i++)
    {
      c = list.get(i);
      c.setRarity(pack.getRarity(c.getName()));
      allCardModel.addCard(c);
    }
    allCardModel.resort();
    allCardTable.setRowSelectionInterval(0, 0);

  }//showChoices()

  private Deck getPlayersDeck()
  {
    Deck deck = new Deck(Constant.GameType.Draft);
    Constant.Runtime.HumanDeck[0] = deck;

    //add sideboard to deck
    CardList list = deckModel.getCards();
    for(int i = 0; i < list.size(); i++)
      deck.addSideboard(list.get(i).getName());


    for(int i = 0; i < 40; i++)
    {
      deck.addSideboard("Forest");
      deck.addSideboard("Mountain");
      deck.addSideboard("Swamp");
      deck.addSideboard("Island");
      deck.addSideboard("Plains");
    }

    return deck;
  }//getPlayersDeck()

  private void saveDraft()
  {
    String s = "";
    while(s == null || s.length() == 0)
    {
      s = JOptionPane.showInputDialog(null, "Please name this draft", "Save Draft", JOptionPane.QUESTION_MESSAGE);
    }
    //TODO: check if overwriting the same name, and let the user delete old drafts

    //construct computer's decks
    //save draft
    Deck[] computer = boosterDraft.getDecks();

    Deck human = getPlayersDeck();
    human.setName(s);

    Deck[] all = {human      , computer[0], computer[1], computer[2],
                  computer[3], computer[4], computer[5], computer[6]};

    DeckIO deckIO = new DeckIO(Constant.IO.deckFile);
    deckIO.writeBoosterDeck(all);

    //write file
    deckIO.close();

    //close and open next screen
    dispose();
    new Gui_NewGame();
  }/*saveDraft()*/
}