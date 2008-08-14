import java.util.*;

import javax.swing.*;
import java.awt.event.*;

interface DeckDisplay
{
  public void updateDisplay(CardList top, CardList bottom);

  //top shows available card pool
  //if constructed, top shows all cards
  //if sealed, top shows 5 booster packs
  //if draft, top shows cards that were chosen
  public CardList getTop();

  //bottom shows cards that the user has chosen for his library
  public CardList getBottom();

  public void setTitle(String message);
}

public class Gui_DeckEditor_Menu extends JMenuBar
{
  private final boolean debugPrint = false;

  private final DeckIO deckIO = new DeckIO(Constant.IO.deckFile);

  private boolean isDeckSaved;
  private String currentDeckName;
  private String currentGameType;

  private JMenuItem newDraftItem;
  private DeckDisplay deckDisplay;

  private Command exitCommand;

  public Gui_DeckEditor_Menu(DeckDisplay in_display, Command exit)
  {
    deckDisplay = in_display;
    exitCommand = exit;

    //this is added just to make save() and saveAs() work ok
    //when first started up, just a silly patch
    currentGameType = Constant.GameType.Constructed;
    setDeckData("", false);

    setupMenu();
  }

  public void newConstructed()
  {
    if(debugPrint)
      System.out.println("New Constructed");

//    if(! isDeckSaved)
//      save();

    currentGameType = Constant.GameType.Constructed;
    setDeckData("", false);

    deckDisplay.updateDisplay(
        AllZone.CardFactory.getAllCards(),
        new CardList()
    );
  }//new constructed

  private void newSealed()
  {
    if(debugPrint)
      System.out.println("New Sealed");

//    if(! isDeckSaved)
//      save();

    currentGameType = Constant.GameType.Sealed;
    setDeckData("", false);

    deckDisplay.updateDisplay(
        new ReadBoosterPack().getBoosterPack5(),
        new CardList()
    );
  }//new sealed

  private void newDraft()
  {
    if(debugPrint)
      System.out.println("New Draft");

//    if(! isDeckSaved)
//      save();

    currentGameType = Constant.GameType.Draft;

    //move all cards from deck main and sideboard to CardList
    Deck deck = deckIO.readBoosterDeck(currentDeckName)[0];
    setDeckData("", false);

    CardList top = new CardList();

    for(int i = 0; i < deck.countMain(); i++)
      top.add(AllZone.CardFactory.getCard(deck.getMain(i), Constant.Player.Human));

    for(int i = 0; i < deck.countSideboard(); i++)
      top.add(AllZone.CardFactory.getCard(deck.getSideboard(i), Constant.Player.Human));

    deckDisplay.updateDisplay(
        top,
        new CardList()
    );
  }//new draft


  private void openConstructed()
  {
    if(debugPrint)
      System.out.println("Open Constructed");

//    if(! isDeckSaved)
//      save();

    String name = getUserInput_OpenDeck(Constant.GameType.Constructed);

    if(name.equals(""))
      return;

    //must be AFTER get user input, since user could cancel
    currentGameType = Constant.GameType.Constructed;
    newDraftItem.setEnabled(false);

    Deck deck = deckIO.readDeck(name);
    setDeckData(name, true);

    CardList main = new CardList();
    for(int i = 0; i < deck.countMain(); i++)
      main.add(AllZone.CardFactory.getCard(deck.getMain(i), Constant.Player.Human));

    deckDisplay.updateDisplay(
        AllZone.CardFactory.getAllCards(),
        main
    );
  }//open constructed

  private void openSealed()
  {
    if(debugPrint)
      System.out.println("Open Sealed");

//    if(! isDeckSaved)
//      save();

    String name = getUserInput_OpenDeck(Constant.GameType.Sealed);

    if(name.equals(""))
      return;

    //must be AFTER get user input, since user could cancel
    currentGameType = Constant.GameType.Sealed;
    newDraftItem.setEnabled(false);

    Deck deck = deckIO.readDeck(name);
    setDeckData(name, true);

    CardList top = new CardList();
    for(int i = 0; i < deck.countSideboard(); i++)
      top.add(AllZone.CardFactory.getCard(deck.getSideboard(i), Constant.Player.Human));

    CardList bottom = new CardList();
    for(int i = 0; i < deck.countMain(); i++)
      bottom.add(AllZone.CardFactory.getCard(deck.getMain(i), Constant.Player.Human));

    deckDisplay.updateDisplay(
        top,
        bottom
    );

  }//open sealed

  private void openDraft()
  {
    if(debugPrint)
      System.out.println("Open Draft");

//    if(! isDeckSaved)
//      save();

    String name = getUserInput_OpenDeck(Constant.GameType.Draft);

    if(name.equals(""))
      return;

    //must be AFTER get user input, since user could cancel
    currentGameType = Constant.GameType.Draft;
    newDraftItem.setEnabled(true);

    Deck deck = deckIO.readBoosterDeck(name)[0];
    setDeckData(name, true);

    CardList top = new CardList();
    for(int i = 0; i < deck.countSideboard(); i++)
      top.add(AllZone.CardFactory.getCard(deck.getSideboard(i), Constant.Player.Human));

    CardList bottom = new CardList();
    for(int i = 0; i < deck.countMain(); i++)
      bottom.add(AllZone.CardFactory.getCard(deck.getMain(i), Constant.Player.Human));

    deckDisplay.updateDisplay(
        top,
        bottom
    );
  }//open draft

  private void save()
  {
    if(debugPrint)
      System.out.println("Save");

    if(currentDeckName.equals(""))
      saveAs();
    else if(currentGameType.equals(Constant.GameType.Draft))
    {
      setDeckData(currentDeckName, true);
      //write booster deck
      Deck[] all = deckIO.readBoosterDeck(currentDeckName);
      all[0] = getDeck();
      deckIO.writeBoosterDeck(all);
    }
    else//constructed or sealed
    {
      setDeckData(currentDeckName, true);
      deckIO.deleteDeck(currentDeckName);
      deckIO.writeDeck(getDeck());
    }
  }//save

  private void saveAs()
  {
    if(debugPrint)
      System.out.println("Save As");

    String name = getUserInput_GetDeckName();

    if(name.equals(""))
      return;
    else if(currentGameType.equals(Constant.GameType.Draft))
    {
      //MUST copy array
      Deck[] read = deckIO.readBoosterDeck(currentDeckName);
      Deck[] all = new Deck[read.length];

      System.arraycopy(read, 0, all, 0, read.length);

      setDeckData(name, true);

      all[0] = getDeck();
      deckIO.writeBoosterDeck(all);
    }
    else//constructed and sealed
    {
      setDeckData(name, true);
      deckIO.writeDeck(getDeck());
    }
  }//save as

  private void delete()
  {
    if(debugPrint)
      System.out.println("Delete");

    if(currentGameType.equals("") || currentDeckName.equals(""))
      return;

    int n = JOptionPane.showConfirmDialog(null, "Do you want to delete this deck " +currentDeckName  +" ?", "Delete", JOptionPane.YES_NO_OPTION);
    if(n == JOptionPane.NO_OPTION)
      return;

    if(currentGameType.equals(Constant.GameType.Draft))
      deckIO.deleteBoosterDeck(currentDeckName);
    else
      deckIO.deleteDeck(currentDeckName);

    setDeckData("", true);
    deckDisplay.updateDisplay(new CardList(), new CardList());
  }//delete

  public void close()
  {
    if(debugPrint)
      System.out.println("Close");

//    if(! isDeckSaved)
//      save();

    deckIO.close();
    exitCommand.execute();
  }//close

  private void setDeckData(String deckName, boolean in_isDeckSaved)
  {
    currentDeckName = deckName;
    isDeckSaved = in_isDeckSaved;

    deckDisplay.setTitle("Deck Editor : " +currentDeckName);
  }
  public String getDeckName()  {return currentDeckName;}
  public String getGameType()  {return currentGameType;}
  public boolean isDeckSaved() {return isDeckSaved;}

  private String getUserInput_GetDeckName()
  {
    Object o = JOptionPane.showInputDialog(null, "Save As", "Deck Name", JOptionPane.OK_CANCEL_OPTION);

    if(o == null)
      return "";

    String deckName = cleanString(o.toString());

    boolean isUniqueName;
    if(currentGameType.equals(Constant.GameType.Draft))
      isUniqueName = deckIO.isUniqueDraft(deckName);
    else
      isUniqueName = deckIO.isUnique(deckName);

    if((! isUniqueName) || deckName.equals(""))
    {
      JOptionPane.showMessageDialog(null, "Please pick another deck name, a deck currently has that name.");
      return getUserInput_GetDeckName();
    }

    return deckName;
  }//getUserInput_GetDeckName()

  //only accepts numbers, letters or dashes up to 10 characters in length
  private String cleanString(String in)
  {
    String out = "";
    char[] c = in.toCharArray();

    for(int i = 0; i < c.length && i < 20; i++)
      if(Character.isLetterOrDigit(c[i]) || c[i] == '-')
        out += c[i];

    return out;
  }


  private String getUserInput_OpenDeck(String deckType)
  {
    ArrayList choices = getDeckNames(deckType);
    if(choices.size() == 0)
    {
      JOptionPane.showMessageDialog(null, "No decks found", "Open Deck", JOptionPane.PLAIN_MESSAGE);
      return "";
    }
    Object o = JOptionPane.showInputDialog(null, "Deck Name", "Open Deck", JOptionPane.OK_CANCEL_OPTION, null, choices.toArray(), choices.toArray()[0]);

    if(o == null)
      return "";

    return o.toString();
  }//getUserInput_OpenDeck()


  private ArrayList getDeckNames(String deckType)
  {
    ArrayList list = new ArrayList();

    //only get decks accoring to the Gui_NewGame screen option
    if(deckType.equals(Constant.GameType.Draft))
    {
      Iterator it = deckIO.getBoosterDecks().keySet().iterator();

      while(it.hasNext())
        list.add(it.next().toString());
    }
    else
    {
      Deck[] d = deckIO.getDecks();
      for(int i = 0; i < d.length; i++)
        if(deckType.equals(d[i].getDeckType()))
          list.add(d[i].toString());
    }

    Collections.sort(list);
    return list;
  }//getDecks()

  private Deck getDeck()
  {
    Deck deck = new Deck(currentGameType);
    deck.setName(currentDeckName);
    CardList list;
    String cardName;

    //always move "bottom" to main
    list = deckDisplay.getBottom();
    for(int i = 0; i < list.size(); i++)
    {
      cardName = list.get(i).getName();
      deck.addMain(AllZone.NameChanger.getOriginalName(cardName));
    }

    //if sealed or draft, move "top" to sideboard
    if(! currentGameType.equals(Constant.GameType.Constructed))
    {
      list = deckDisplay.getTop();
      for(int i = 0; i < list.size(); i++)
      {
        cardName = list.get(i).getName();
        deck.addSideboard(AllZone.NameChanger.getOriginalName(cardName));
      }
    }
    return deck;
  }//getDeck()

  private void setupMenu()
  {
    JMenuItem newConstructed = new JMenuItem("New Deck - Constructed");
    JMenuItem newSealed      = new JMenuItem("New Deck - Sealed");
    JMenuItem newDraft       = new JMenuItem("New Deck - Draft");

    JMenuItem openConstructed = new JMenuItem("Open Deck - Constructed");
    JMenuItem openSealed      = new JMenuItem("Open Deck - Sealed");
    JMenuItem openDraft       = new JMenuItem("Open Deck - Draft");

    newDraftItem = newDraft;
    newDraftItem.setEnabled(false);

    JMenuItem save = new JMenuItem("Save");
    JMenuItem saveAs = new JMenuItem("Save As");
    JMenuItem delete = new JMenuItem("Delete");
    JMenuItem close = new JMenuItem("Close");

    JMenu fileMenu = new JMenu("Deck Actions");
    fileMenu.add(newConstructed);
    fileMenu.add(newSealed);
    fileMenu.add(newDraft);
    fileMenu.addSeparator();

    fileMenu.add(openConstructed);
    fileMenu.add(openSealed);
    fileMenu.add(openDraft);
    fileMenu.addSeparator();

    fileMenu.add(save);
    fileMenu.add(saveAs);
    fileMenu.add(delete);
    fileMenu.addSeparator();

    fileMenu.add(close);

    this.add(fileMenu);

    //add listeners
    newConstructed.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {newConstructed();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : newConstructed() error - " +ex);}
    }});

    newSealed.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {newSealed();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : newSealed() error - " +ex);}
    }});

    newDraft.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {newDraft();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : newDraft() error - " +ex);}
    }});

    openConstructed.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {openConstructed();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : openConstructed() error - " +ex);}
    }});

    openSealed.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {openSealed();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : openSealed() error - " +ex);}
    }});

    openDraft.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {openDraft();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : openDraft() error - " +ex);}
    }});

    save.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {save();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : save() error - " +ex);}
    }});

    saveAs.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {saveAs();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : saveAs() error - " +ex);}
    }});

    delete.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {delete();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : delete() error - " +ex);}
    }});

    close.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ev)
    {
      try{
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run() {close();}
        });
        }catch(Exception ex) {throw new RuntimeException("Gui_DeckEditor_Menu : close() error - " +ex);}
    }});
  }//setupMenu()
}