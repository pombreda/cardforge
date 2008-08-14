import java.util.*;

public class CardList
{
    private ArrayList list = new ArrayList();

    public CardList() {}
    public CardList(Card c[]) { addAll(c);}
    public CardList(Object c[]) {addAll(c);}

    public void reverse()
    {
      Collections.reverse(list);
    }

    public boolean equals(CardList a)
    {
      if(list.size() != a.size())
        return false;

      for(int i = 0; i < list.size(); i++)
        if(! list.get(i).equals(a.get(i)))
          return false;

      return true;
    }

    public int size()                       {return list.size();}
    public void add(Card c)              {list.add(c);}
    public void add(int n, Card c)  {list.add(n, c);}
    public boolean contains(Card c) {return list.contains(c);}

    //probably remove getCard() in the future
    public Card getCard(int index){return (Card)list.get(index);}
    public Card get(int i) {return getCard(i);}

    public void addAll(Object c[])
    {
	for(int i = 0; i < c.length; i++)
	    list.add((Card)c[i]);
    }
    public boolean containsName(Card c)
    {
	return containsName(c.getName());
    }
    public boolean containsName(String name)
    {
	for(int i = 0; i < size(); i++)
	    if(getCard(i).getName().equals(name))
		return true;

	return false;
    }
    //returns new subset of all the cards with the same name
    public CardList getName(String name)
    {
	CardList c = new CardList();

	for(int i = 0; i < size(); i++)
	    if(getCard(i).getName().equals(name))
		c.add(getCard(i));

	return c;
    }
    //cardType is like "Land" or "Golin", returns a new CardList that is a subset of current CardList
    public CardList getType(String cardType)
    {
	CardList c = new CardList();
        Card card;
	for(int i = 0; i < size(); i++)
	{
          card = getCard(i);
	    if(card.getType().contains(cardType) ||
               (cardType.equals("Sliver") &&  card.isCreature() && card.getType().contains("Shapeshifter"))
              )//changleings, see Lorwyn FAQ
		c.add(getCard(i));
	}
	return c;
    }//getType()
    public CardList filter(CardListFilter f)
    {
	CardList c = new CardList();
	for(int i = 0; i < size(); i++)
	    if(f.addCard(getCard(i)))
		c.add(getCard(i));

	return c;
    }
    public Card[] toArray()
    {
	Card[] c = new Card[list.size()];
	list.toArray(c);
	return c;
    }
    public String toString() {return list.toString();}
    public boolean isEmpty() {return list.isEmpty();}
    public Card remove(int i) {return (Card)list.remove(i);}
    public void remove(Card c) {list.remove(c);}
    public void clear() {list.clear();}

    public void shuffle()
    {
      Collections.shuffle(list, MyRandom.random);
      Collections.shuffle(list, MyRandom.random);
      Collections.shuffle(list, MyRandom.random);
    }
    public void sort(Comparator c) {Collections.sort(list, c);}
}