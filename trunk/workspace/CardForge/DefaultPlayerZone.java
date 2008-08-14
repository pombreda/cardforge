import java.util.*;

public class DefaultPlayerZone extends PlayerZone implements java.io.Serializable
{
  private ArrayList cards = new ArrayList();
  private String zoneName;
  private String playerName;
  private boolean update = true;

  public DefaultPlayerZone(String zone, String player)
  {
    zoneName = zone;
    playerName = player;
  }
  //************ BEGIN - these methods fire updateObservers() *************
  public void add(Object o)
  {
    Card c = (Card)o;
    c.addObserver(this);

    c.setTurnInZone(AllZone.Phase.getTurn());

    cards.add((Card)c);
    update();
  }
  public void update(Observable ob, Object object)
  {
    this.update();
  }
  public void add(Card c, int index)
  {
    cards.add(index, c);
    c.setTurnInZone(AllZone.Phase.getTurn());
    update();
  }
  public void remove(Object c)
  {
    cards.remove((Card)c);
    update();
  }
  public void remove(int index)
  {
    cards.remove(index);
    update();
  }
  public void setCards(Card c[])
  {
    cards = new ArrayList(Arrays.asList(c));
    update();
  }
  //removes all cards
  public void reset()
  {
    cards.clear();
    update();
  }
  //************ END - these methods fire updateObservers() *************

  public boolean is(String zone)
  {
    return zone.equals(zoneName);
  }
  public boolean is(String zone, String player)
  {
    return (zone.equals(zoneName) && player.equals(playerName));
  }
  public String getPlayer()
  {
    return playerName;
  }
  public String getZone()
  {
    return zoneName;
  }
  public int size()
  {
    return cards.size();
  }
  public Card get(int index)
  {
    return (Card)cards.get(index);
  }

  public Card[] getCards()
  {
    Card c[] = new Card[cards.size()];
    cards.toArray(c);
    return c;
  }
  public void update()
  {
    if(update)
      updateObservers();
  }
  public void setUpdate(boolean b) {update = b;}
  public boolean getUpdate() {return update;}
}
