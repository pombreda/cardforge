import java.util.*;

public class ComputerUtil_Block2
{
  private CardList attackers;
  private CardList possibleBlockers;
  private int blockersLife;
  private Random random = new Random();

  public ComputerUtil_Block2(Card[] attackers, Card[] possibleBlockers, int blockerLife)
  {
    this(new CardList(attackers), new CardList(possibleBlockers), blockerLife);
  }

  public ComputerUtil_Block2(CardList in_attackers, CardList in_possibleBlockers, int in_blockersLife)
  {
    attackers        = new CardList(in_attackers.toArray());
    possibleBlockers = getUntappedCreatures(new CardList(in_possibleBlockers.toArray()));

   //the Computer will try to keep his life at 3, instead of just at 1 life
    blockersLife     = in_blockersLife - 3;

    //high to low attack, might shuffle some of the time, not sure
//    if( random.nextInt(9) < 3)
      attackers.shuffle();
//    else
      CardListUtil.sortAttack(attackers);



    //low to high attack
    CardListUtil.sortAttackLowFirst(possibleBlockers);

    possibleBlockers = removeValuableBlockers(possibleBlockers);
  }
  private CardList getUntappedCreatures(CardList list)
  {
    list = list.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        return c.isCreature() && c.isUntapped();
      }
    });
    return list;
  }//getUntappedCreatures()

  private CardList removeValuableBlockers(CardList in)
  {
    final String[] noBlock = {"Elvish Piper", "Urborg Syphon-Mage", "Sparksmith", "Royal Assassin", "Marble Titan", "Kamahl, Pit Fighter"};

    CardList out = in.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        for(int i = 0; i < noBlock.length; i++)
          if(c.getName().equals(noBlock[i]))
            return false;

        return true;
      }
    });//CardListFilter

    return out;
  }

  //checks for flying and stuff like that
  private CardList getPossibleBlockers(Card attacker)
  {
    CardList list = new CardList();
    for(int i = 0; i < possibleBlockers.size(); i++)
      if(CombatUtil.canBlock(attacker, possibleBlockers.get(i)))
        list.add(possibleBlockers.get(i));

    return list;
  }

  //finds a blocker that destroys the attacker, the blocker is not destroyed
  //returns null if no blocker is found
  Card safeSingleBlock(Card attacker)
  {
    CardList c = getPossibleBlockers(attacker);

    for(int i = 0; i < c.size(); i++)
      if(CombatUtil.canDestroyAttacker(attacker, c.get(i)) &&
      (! CombatUtil.canDestroyAttacker(c.get(i), attacker)))
        return c.get(i);

    return null;
  }//safeSingleBlock()

  //finds a blocker, both the attacker and blocker are destroyed
  //returns null if no blocker is found
  Card tradeSingleBlock(Card attacker)
  {
    CardList c = getPossibleBlockers(attacker);

    for(int i = 0; i < c.size(); i++)
      if(CombatUtil.canDestroyAttacker(attacker, c.get(i)))
      {
      //do not block a non-flyer with a flyer
      if((! c.get(i).getKeyword().contains("Flying")) || attacker.getKeyword().contains("Flying"))
        return c.get(i);
      }
    return null;
  }//tradeSingleBlock()



  //finds a blocker, neither attacker and blocker are destroyed
  //returns null if no blocker is found
  Card shieldSingleBlock(Card attacker)
  {
    CardList c = getPossibleBlockers(attacker);

    for(int i = 0; i < c.size(); i++)
      if(! CombatUtil.canDestroyAttacker(c.get(i), attacker))
        return c.get(i);

    return null;
  }//shieldSingleBlock()



  //finds multiple blockers
  //returns an array of size 0 if not multiple blocking
  Card[] multipleBlock(Card attacker)
  {
    CardList c = getPossibleBlockers(attacker);

    int defense = attacker.getDefense() - attacker.getDamage();
    //if attacker cannot be destroyed
    if(defense > CardListUtil.sumAttack(c))
      return new Card[] {};

    CardList block = new CardList();
    c.shuffle();

    while(defense > CardListUtil.sumAttack(block))
      block.add(c.remove(0));

    Card check[] = block.toArray();

    //no single blockers, that should be handled somewhere else
    if(check.length == 1)
      return new Card[] {};

    return check;
  }//multipleBlock()


  //finds a blocker, both the attacker lives and blockers dies
  //returns null if no blocker is found
  Card chumpSingleBlock(Card attacker)
  {
    if(blockersLife <= CardListUtil.sumAttack(attackers))
    {
      CardList c = getPossibleBlockers(attacker);
      CardListUtil.sortAttackLowFirst(c);
      if(! c.isEmpty())
        return c.get(0);
    }
    return null;
  }//tradeSingleBlock()


  private void testing(String s)
  {
    boolean print = false;
    if(print)
      System.out.println(s);
  }

  public Combat getBlockers()
  {
    Card c;
    Combat combat = new Combat();
    boolean shouldBlock;

    for(int i = 0; i < attackers.size(); i++)
    {
      shouldBlock = random.nextBoolean();
      if(AllZone.Computer_Life.getLife() < (AllZone.Human_Life.getLife() - 4))
        shouldBlock = true;

      testing("shouldBlock - " +shouldBlock);
      c = null;

      //add attacker to combat
      combat.addAttacker(attackers.get(i));

      //safe block - attacker dies, blocker lives
      //this is usually a trap, so rarely block like this
      if(MyRandom.percentTrue(10))
      {
        testing("safe");
        if(c != null)
          c = safeSingleBlock(attackers.get(i));
      }

      //TODO: improve this
      if(c == null && shouldBlock && safeSingleBlock(attackers.get(i)) == null)
      {
        //shield block - attacker lives, blocker lives
        c = shieldSingleBlock(attackers.get(i));
        if(c != null)
          testing("shield");
     }

     //TODO: improvethis
      if(c == null && shouldBlock  && safeSingleBlock(attackers.get(i)) == null)
     {
        //trade block - attacker dies, blocker dies
        c = tradeSingleBlock(attackers.get(i));

        if(c != null)
          testing("trading");
     }

      //TODO: this could be improved
      if(c == null)
      {
        //chump block - attacker lives, blocker dies
        c = chumpSingleBlock(attackers.get(i));
        if(c != null)
          testing("chumping");
      }

      if(c != null)
      {
        possibleBlockers.remove(c);
        combat.addBlocker(attackers.get(i), c);
      }

      //multiple blockers
      if(c == null && shouldBlock)
      {
        Card[] m = multipleBlock(attackers.get(i));
        for(int inner = 0; inner < m.length; inner++)
        {
          //to prevent a single flyer from blocking a single non-flyer
          //tradeSingleBlock() checks for a flyer blocking a non-flyer also
          if(m.length != 1)
          {
            possibleBlockers.remove(m[inner]);
            combat.addBlocker(attackers.get(i), m[inner]);
          }
        }//for
      }//if
    }//for attackers

    return combat;
  }//getBlockers()
}