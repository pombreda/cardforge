import java.util.*;

public class TableSorter implements Comparator
{
  private final int column;
  private boolean ascending;

  private CardList all;

  public TableSorter(CardList in_all, int in_column, boolean in_ascending)
  {
    all = new CardList(in_all.toArray());
    column = in_column;
    ascending = in_ascending;
  }
  //                             0       1       2       3        4     5          6
  //private String column[] = {"Qty", "Name", "Cost", "Color", "Type", "Stats", "Rarity"};
  public int compare(Object in_a, Object in_b)
  {
    Comparable aCom = null;
    Comparable bCom = null;

    Card a = (Card)in_a;
    Card b = (Card)in_b;

    if(column == 0)//Qty
    {
      aCom = new Integer(countCardName(a.getName(), all));
      bCom = new Integer(countCardName(b.getName(), all));
    }
    else if (column == 1)//Name
    {
      aCom = a.getName();
      bCom = b.getName();
    }
    else if (column == 2)//Cost
    {
      aCom = new Integer(CardUtil.getConvertedManaCost(a.getManaCost()));
      bCom = new Integer(CardUtil.getConvertedManaCost(b.getManaCost()));

      if(a.isLand())
        aCom = new Integer(-1);
      if(b.isLand())
        bCom = new Integer(-1);
    }
    else if (column == 3)//Color
    {
      aCom = getColor(a);
      bCom = getColor(b);
    }
    else if (column == 4)//Type
    {
      aCom = getType(a);
      bCom = getType(b);
    }
    else if (column == 5)//Stats, attack and defense
    {
      aCom = new Float(-1);
      bCom = new Float(-1);

      if(a.isCreature())
        aCom = new Float(a.getAttack() +"." +a.getDefense());
      if(b.isCreature())
        bCom = new Float(b.getAttack() +"." +b.getDefense());
    }
    else if (column == 6)//Rarity
    {
      aCom = getRarity(a);
      bCom = getRarity(b);
    }

    if(ascending)
      return aCom.compareTo(bCom);
    else
      return bCom.compareTo(aCom);
  }//compare()
  private int countCardName(String name, CardList c)
  {
    int count = 0;
    for(int i = 0; i < c.size(); i++)
      if(name.equals(c.get(i).getName()))
         count++;

    return count;
  }
  private Integer getRarity(Card c)
  {
    if(c.getRarity().equals("Common"))
      return new Integer(1);
    else if(c.getRarity().equals("Uncommon"))
      return new Integer(2);
    else if(c.getRarity().equals("Rare"))
      return new Integer(3);
    else if(c.getRarity().equals("Land"))
      return new Integer(4);
    else
      return new Integer(5);
  }
  public static String getColor(Card c)
  {
    ArrayList list = CardUtil.getColors(c);

    if(list.size() == 1)
      return list.get(0).toString();

    return "multi";
  }
  private Comparable getType(Card c)
  {
    return c.getType().toString();

/*
    if(c.isCreature())
      return new Integer(1);
    else if(c.isSorcery())
      return new Integer(2);
    else if(c.isInstant())
      return new Integer(3);
    else if(c.isLocalEnchantment())
      return new Integer(4);
    else if(c.isGlobalEnchantment())
      return new Integer(5);
    else if(c.isArtifact())
      return new Integer(6);
    else if(c.isBasicLand())
      return new Integer(7);
    else if(c.isLand())
      return new Integer(8);
    else
      return new Integer(9);
*/
  }
}

/*
  private Integer getType(Card c)
  {
    if(c.isCreature())
      return new Integer(1);
    else if(c.isSorcery())
      return new Integer(2);
    else if(c.isInstant())
      return new Integer(3);
    else if(c.isLocalEnchantment())
      return new Integer(4);
    else if(c.isGlobalEnchantment())
      return new Integer(5);
    else if(c.isArtifact())
      return new Integer(6);
    else if(c.isBasicLand())
      return new Integer(7);
    else if(c.isLand())
      return new Integer(8);
    else
      return new Integer(9);
  }
*/
