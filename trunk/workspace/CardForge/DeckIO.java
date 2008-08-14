import java.util.*;
import java.io.*;

//reads and write Deck objects
public class DeckIO
{
  private final File file;
  private ArrayList deckList = new ArrayList();

  //key is String of the humans deck
  //data is Deck[] size of 8
  //humans deck is Deck[0]
  private Map boosterMap = new HashMap();

  public static void main(String[] args)
  {
    Deck[] deck = new Deck[8];
    for(int i = 0; i < deck.length; i++)
    {
      deck[i] = new Deck(Constant.GameType.Draft);
      deck[i].setName("" +i);
    }

    deck[0].addMain("Lantern Kami");

    DeckIO io = new DeckIO("booster-decks");
//    io.writeBoosterDeck(deck);
//    io.close();

//    io.deleteBoosterDeck("0");
//    System.out.println(io.getBoosterDecks().keySet().size());
//    io.close();
  }

  public DeckIO(String filename)
  {
    file = new File(filename);

    readFile();
  }

  public boolean isUnique(String deckName)
  {
    Deck d;
    for(int i = 0; i < deckList.size(); i++)
    {
      d = (Deck)deckList.get(i);
      if(d.getName().equals(deckName))
        return false;
    }
    return true;
  }
  public boolean isUniqueDraft(String deckName)
  {
    ArrayList key = new ArrayList(boosterMap.keySet());

    for(int i = 0; i < key.size(); i++)
    {
      if(key.get(i).equals(deckName))
        return false;
    }
    return true;
  }
  public boolean hasName(String deckName)
  {
    ArrayList string = new ArrayList();

    for(int i = 0; i < deckList.size(); i++)
      string.add(deckList.get(i).toString());

    Iterator it = boosterMap.keySet().iterator();
    while(it.hasNext())
      string.add(it.next().toString());

    return string.contains(deckName);
  }

  public Deck readDeck(String deckName)
  {
    return (Deck) deckList.get(findDeckIndex(deckName));
  }

  private int findDeckIndex(String deckName)
  {
    int n = -1;
    for(int i = 0; i < deckList.size(); i++)
      if(((Deck)deckList.get(i)).getName().equals(deckName))
        n = i;

    if(n == -1)
      throw new RuntimeException("DeckIO : findDeckIndex() error, deck name not found - " +deckName);

    return n;
  }

  public void writeDeck(Deck deck)
  {
    if(deck.getDeckType().equals(Constant.GameType.Draft))
      throw new RuntimeException("DeckIO : writeDeck() error, deck type is Draft");

    deckList.add(deck);
  }

  public void deleteDeck(String deckName)
  {
    deckList.remove(findDeckIndex(deckName));
  }

  public Deck[] readBoosterDeck(String deckName)
  {
    if(! boosterMap.containsKey(deckName))
      throw new RuntimeException("DeckIO : readBoosterDeck() error, deck name not found - " +deckName);

    return (Deck[]) boosterMap.get(deckName);
  }

  public void writeBoosterDeck(Deck[] deck)
  {
    checkBoosterDeck(deck);

    boosterMap.put(deck[0].toString(), deck);
  }//writeBoosterDeck()

  public void deleteBoosterDeck(String deckName)
  {
    if(! boosterMap.containsKey(deckName))
      throw new RuntimeException("DeckIO : deleteBoosterDeck() error, deck name not found - " +deckName);

    boosterMap.remove(deckName);
  }

  private void checkBoosterDeck(Deck[] deck)
  {
    if(deck == null     ||
       deck.length != 8 ||
       deck[0].getName().equals("") ||
       (!deck[0].getDeckType().equals(Constant.GameType.Draft)))
    {
      throw new RuntimeException("DeckIO : checkBoosterDeck() error, invalid deck");
    }
//    for(int i = 0; i < deck.length; i++)
//      if(deck[i].getName().equals(""))
//        throw new RuntimeException("DeckIO : checkBoosterDeck() error, deck does not have name - " +deck[i].getName());
  }//checkBoosterDeck()


  public Deck[] getDecks()
  {
    Deck[] out = new Deck[deckList.size()];
    deckList.toArray(out);
    return out;
  }
  public Map getBoosterDecks() {return new HashMap(boosterMap);}

  public void close() {writeFile();}

  private void readFile()
  {
    try{
      if(! file.exists())
        return;

      ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

      deckList   = (ArrayList) in.readObject();
      boosterMap = (Map)       in.readObject();

      in.close();
    }catch(Exception ex){throw new RuntimeException("DeckIO : read() error, " +ex);}
  }
  private void writeFile()
  {
    try{
      if(! file.exists())
        file.createNewFile();

      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

      out.writeObject(deckList);
      out.writeObject(boosterMap);

      out.flush();
      out.close();
    }catch(Exception ex){throw new RuntimeException("DeckIO : write() error, " +ex.getMessage());}
  }
}//DeckIO