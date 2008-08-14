import java.util.*;

public class ComputerUtil
{
  private static Random random = new Random();

  //if return true, go to next phase
  static public boolean playCards()
  {
    return playCards(getSpellAbility());
  }


  //if return true, go to next phase
  static public boolean playCards(SpellAbility[] all)
  {
    //not sure "playing biggest spell" matters?
    sortSpellAbilityByCost(all);
//    MyRandom.shuffle(all);

    for(int i = 0; i < all.length; i++)
    {
      if(canPayCost(all[i]) && all[i].canPlay() && all[i].canPlayAI())
      {
        if(all[i].isSpell())
          AllZone.Computer_Hand.remove(all[i].getSourceCard());

        if(all[i] instanceof Ability_Tap)
          all[i].getSourceCard().tap();

        payManaCost(all[i]);
        all[i].chooseTargetAI();
        AllZone.Stack.add(all[i]);

        return false;
      }
    }//while
    return true;
  }//playCards()
  final static public void playNoStack(SpellAbility sa)
  {
    if(canPayCost(sa))
    {
      if(sa.isSpell())
      {
        AllZone.Computer_Hand.remove(sa.getSourceCard());
        //probably doesn't really matter anyways
        //sa.getSourceCard().comesIntoPlay(); - messes things up, maybe for the future fix this
      }

      if(sa instanceof Ability_Tap)
        sa.getSourceCard().tap();

      payManaCost(sa);
      sa.resolve();

      //destroys creatures if they have lethal damage, etc..
      AllZone.GameAction.checkStateEffects();
    }
  }//play()

  //gets Spell's of cards in hand and Abilities of cards in play
  //checks to see
  //1. if canPlay() returns true, 2. can pay for mana
  static public SpellAbility[] getSpellAbility()
  {
    CardList all = new CardList();
    all.addAll(AllZone.Computer_Play.getCards());
    all.addAll(AllZone.Computer_Hand.getCards());
    all = all.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        if(c.isBasicLand())
          return false;

        return true;
      }
    });
    ArrayList spellAbility = new ArrayList();
    for(int outer = 0; outer < all.size(); outer++)
    {
      SpellAbility[] sa = all.get(outer).getSpellAbility();
      for(int i = 0; i < sa.length; i++)
        if(sa[i].canPlayAI() && canPayCost(sa[i]) /*&& sa[i].canPlay()*/)
          spellAbility.add(sa[i]);//this seems like it needs to be copied, not sure though
    }

    SpellAbility[] sa = new SpellAbility[spellAbility.size()];
    spellAbility.toArray(sa);
    return sa;
  }
  static public boolean canPlay(SpellAbility sa)
  {
    return sa.canPlayAI() && canPayCost(sa);
  }
  static public boolean canPayCost(SpellAbility sa)
  {
    if(sa.getManaCost().equals(("0")))
       return true;

    CardList land = getAvailableMana();
    ManaCost cost = new ManaCost(sa.getManaCost());
    String color;

    for(int i = 0; i < land.size(); i++)
    {
      color = getColor(land.get(i));

      if(cost.isNeeded(color))
      {
        cost.subtractMana(color);
      }

      if(cost.isPaid())
        return true;
    }
    return false;
  }//canPayCost()


  static public void payManaCost(SpellAbility sa)
  {
    if(sa.getManaCost().equals(("0")))
       return;

    CardList land = getAvailableMana();
    ManaCost cost = new ManaCost(sa.getManaCost());
    String color;

    for(int i = 0; i < land.size(); i++)
    {
      color = getColor(land.get(i));

      if(cost.isNeeded(color))
      {
        land.get(i).tap();
        cost.subtractMana(color);
      }

      if(cost.isPaid())
        break;
    }

    if(! cost.isPaid())
      throw new RuntimeException("ComputerUtil : payManaCost() cost was not paid");
  }//payManaCost()

  //get the color that the land could produce
  //Swamps produce Black
  public static String getColor(Card land)
  {
    Map map = new HashMap();
    map.put("tap: add B", Constant.Color.Black);
    map.put("tap: add W", Constant.Color.White);
    map.put("tap: add G", Constant.Color.Green);
    map.put("tap: add R", Constant.Color.Red);
    map.put("tap: add U", Constant.Color.Blue);
    map.put("tap: add 1", Constant.Color.Colorless);

    //this fails on Vine Trellis and probably 9th Pain Lands
    try{
      Object o = land.getKeyword().get(0);
      return map.get(o).toString();
    }catch(Exception ex)//I hope this fixes "the problem" that I can't re-create
    {
      return Constant.Color.Colorless;
    }
  }

/*
  //only works with mono-colored spells
  static public void payManaCost(int convertedCost)
  {
    CardList land = getAvailableMana();
    //converted colered mana requirements into colorless
    ManaCost cost = new ManaCost("" +convertedCost);
    Card c;
    for(int i = 0; i < land.size(); i++)
    {
      if(cost.isPaid())
        break;

      land.get(i).tap();
      cost.subtractMana(Constant.Color.Red);
    }//for
    if(! cost.isPaid())
      throw new RuntimeException("ComputerUtil : payManaCost() cost was not paid");
  }//payManaCost()
*/

  static public CardList getAvailableMana()
  {
    CardList list = new CardList(AllZone.Computer_Play.getCards());
    CardList mana = list.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        if(c.isCreature() && c.hasSickness())
          return false;

        return CardUtil.isMana(c) && c.isUntapped();
      }
    });//CardListFilter
    return mana;
  }//getAvailableMana()

  //plays a land if one is available
  static public void playLand()
  {
    ArrayList landList = PlayerZoneUtil.getCardType(AllZone.Computer_Hand, "Land");
    if(! landList.isEmpty())
    {
      AllZone.Computer_Hand.remove(landList.get(0));
      AllZone.Computer_Play.add(landList.get(0));
    }
  }
  static public void untapDraw()
  {
    AllZone.GameAction.drawCard(Constant.Player.Computer);

    CardList permanent = new CardList(AllZone.Computer_Play.getCards());
    for(int i = 0; i < permanent.size(); i++)
      permanent.get(i).untap();
  }
  static public Combat getAttackers()
  {
    ComputerUtil_Attack2 att = new ComputerUtil_Attack2(
        AllZone.Computer_Play.getCards(),
        AllZone.Human_Play.getCards()   ,  AllZone.Human_Life.getLife());

    return att.getAttackers();
  }
  static public Combat getBlockers()
  {
    ComputerUtil_Block2 block = new ComputerUtil_Block2(
      AllZone.Combat.getAttackers()   ,
      AllZone.Computer_Play.getCards(), AllZone.Computer_Life.getLife());

    return block.getBlockers();
  }
  static void sortSpellAbilityByCost(SpellAbility sa[])
  {
    //sort from highest cost to lowest
    //we want the highest costs first
    Comparator c = new Comparator()
    {
      public int compare(Object a, Object b)
      {
        int a1 = CardUtil.getConvertedManaCost((SpellAbility)a);
        int b1 = CardUtil.getConvertedManaCost((SpellAbility)b);

        //puts creatures in front of spells
        if(((SpellAbility)a).getSourceCard().isCreature())
          a1 += 1;

        if(((SpellAbility)b).getSourceCard().isCreature())
          b1 += 1;


        return b1 - a1;
      }
    };//Comparator
    Arrays.sort(sa, c);
  }//sortSpellAbilityByCost()
}