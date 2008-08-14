import java.util.*;
import javax.swing.*;

public class GameAction
{
  //  private StaticEffects staticEffects = new StaticEffects();

  //returns null if playes does not have a Planeswalker
  public Card getPlaneswalker(String player)
  {
    PlayerZone p = AllZone.getZone(Constant.Zone.Play, player);
    CardList c = new CardList(p.getCards());
    c = c.getType("Planeswalker");

    if(c.isEmpty())
      return null;

    return c.get(0);
  }

  private Card getCurrentCard(int ID)
  {
    CardList all = new CardList();
    all.addAll(AllZone.Human_Graveyard.getCards());
    all.addAll(AllZone.Human_Hand.getCards());
    all.addAll(AllZone.Human_Library.getCards());
    all.addAll(AllZone.Human_Play.getCards());
    all.addAll(AllZone.Human_Removed.getCards());

    all.addAll(AllZone.Computer_Graveyard.getCards());
    all.addAll(AllZone.Computer_Hand.getCards());
    all.addAll(AllZone.Computer_Library.getCards());
    all.addAll(AllZone.Computer_Play.getCards());
    all.addAll(AllZone.Computer_Removed.getCards());

    for(int i = 0; i < all.size(); i++)
      if(all.get(i).getUniqueNumber() == ID)
        return all.get(i);

    return null;
  }//getCurrentCard()

  public Card moveTo(PlayerZone zone, Card c)
  {
    //c = getCurrentCard(c); - breaks things, seems to not be needed
    //not 100% sure though, this might be broken

    //create new Card, which resets stats and anything that might have changed during play
    if(! c.isToken())
      c = AllZone.CardFactory.copyCard(c);

    PlayerZone p = AllZone.getZone(c);
    //like if a Sorcery was resolved and needs to be put in the graveyard
    //used by Input_Instant
    if(p != null)
      p.remove(c);

    zone.add(c);
    return c;
  }
  //card can be anywhere like in Hand or in Play
  public void moveToGraveyard(Card c)
  {
    //must put card in OWNER's graveyard not controller's
    PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, c.getOwner());
    moveTo(grave, c);
  }

  public void discardRandom(String player)
  {
    Card[] c = AllZone.getZone(Constant.Zone.Hand, player).getCards();
    if(c.length != 0)
      discard(CardUtil.getRandom(c));
  }

  public void discard(Card c)
  {
    moveToGraveyard(c);
  }

  public void checkStateEffects()
  {
//    System.out.println("checking !!!");
//    RuntimeException run = new RuntimeException();
//    run.printStackTrace();

    JFrame frame = (JFrame) AllZone.Display;
    if(! frame.isDisplayable())
      return;

    boolean stop = false;

    if(AllZone.Computer_Life.getLife() <= 0)
    {
      Constant.Runtime.WinLose.addWin();
      stop = true;
    }
    if(AllZone.Human_Life.getLife() <= 0)
    {
      Constant.Runtime.WinLose.addLose();
      stop = true;
    }

    if(stop)
    {
      frame.dispose();
      new Gui_WinLose();
      return;
    }

    //card state effects like Glorious Anthem
    GameActionUtil.executeCardStateEffects();

    //System.out.println("checking state effects");
    ArrayList creature = PlayerZoneUtil.getCardType(AllZone.Computer_Play, "Creature");
    creature.addAll     (PlayerZoneUtil.getCardType(AllZone.Human_Play   , "Creature"));

    Card c;
    Iterator it = creature.iterator();
    while(it.hasNext())
    {
      c = (Card)it.next();
      if(c.getDefense() <= c.getDamage())
        destroy(c);
    }

    destroyLegendaryCreatures();
    destroyPlaneswalkers();
  }//checkStateEffects()

  private void destroyPlaneswalkers()
  {
    //get all Planeswalkers
    CardList list = new CardList();
    list.addAll(AllZone.Human_Play.getCards());
    list.addAll(AllZone.Computer_Play.getCards());
    list = list.getType("Planeswalker");

    Card c;
    for(int i = 0; i < list.size(); i++)
    {
      c = list.get(i);
      if(c.getCounters() <= 0)
        AllZone.GameAction.moveToGraveyard(c);
    }
  }

  private void destroyLegendaryCreatures()
  {
    ArrayList a = PlayerZoneUtil.getCardType(AllZone.Human_Play   , "Legendary");
    a.addAll     (PlayerZoneUtil.getCardType(AllZone.Computer_Play, "Legendary"));

    while(! a.isEmpty())
    {
      ArrayList b = getCardsNamed(a, ((Card)a.get(0)).getName());
      a.remove(0);
      if(1 < b.size())
      {
        for(int i = 0; i < b.size(); i++)
          AllZone.GameAction.destroy((Card)b.get(i));
      }
    }
  }//destroyLegendaryCreatures()

  //ArrayList search is all Card objects, returns ArrayList of Cards
  public ArrayList getCardsNamed(ArrayList search, String name)
  {
    ArrayList a = new ArrayList();
    Card c[] = CardUtil.toCard(search);

    for(int i = 0; i < c.length; i++)
    {
      if(c[i].getName().equals(name))
        a.add(c[i]);
    }
    return a;
  }
  public void sacrifice(Card c)
  {
    sacrificeDestroy(c);
  }
  public void destroyNoRegeneration(Card c)
  {
    if(! AllZone.GameAction.isCardInPlay(c) || c.getKeyword().contains("Indestructible"))
      return;

    sacrificeDestroy(c);
  }
  private void sacrificeDestroy(Card c)
  {
    if(! isCardInPlay(c))
      return;

    PlayerZone play = AllZone.getZone(c);

    if(c.getOwner().equals(Constant.Player.Human) || c.getOwner().equals(Constant.Player.Computer))
      ;
    else
      throw new RuntimeException("GameAction : destroy() invalid card.getOwner() - " +c +" " +c.getOwner());

    play.remove(c);

    //tokens don't go into the graveyard
    //TODO: must change this if any cards have effects that trigger "when creatures go to the graveyard"
    if(! c.isToken())
      //resets the card, untaps the card, removes anything "extra", resets attack and defense
      moveToGraveyard(c);


    c.destroy();

  }//sacrificeDestroy()
  public void destroy(Card c)
  {
    if(! AllZone.GameAction.isCardInPlay(c) || c.getKeyword().contains("Indestructible"))
      return;

    if(c.getShield() > 0)
    {
      c.subtractShield();
      c.setDamage(0);
      return;
    }
    this.sacrificeDestroy(c);
  }
  public void drawCard(String player)
  {
    //TODO: show that milled player looses

    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
    PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, player);

    if(library.size() != 0)
    {
      Card c = library.get(0);
      library.remove(0);
      hand.add(c);
    }
  }
  //is this card a permanent that is in play?
  public boolean isCardInPlay(Card c)
  {
    return PlayerZoneUtil.isCardInZone(AllZone.Computer_Play, c) ||
        PlayerZoneUtil.isCardInZone(AllZone.Human_Play   , c);
  }
  public String getOpponent(String p)
  {
    return p.equals(Constant.Player.Human) ? Constant.Player.Computer : Constant.Player.Human;
  }
  //TODO: shuffling seems to change a card's unique number but i'm not 100% sure
  public void shuffle(String player)
  {
    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
    Card c[] = library.getCards();

    if(c.length <= 1)
      return;

    ArrayList list = new ArrayList(Arrays.asList(c));
      //overdone but wanted to make sure it was really random
      Random random = new Random();
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);

//      random = java.security.SecureRandom.getInstance("SHA1PRNG");

      Object o;
      for(int i = 0; i < list.size(); i++)
      {
        o = list.remove(random.nextInt(list.size()));
        list.add(random.nextInt(list.size()), o);
      }

      Collections.shuffle(list, random);
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);
      Collections.shuffle(list, random);


      list.toArray(c);
      library.setCards(c);
  }//shuffle
  public boolean isCardInZone(Card card, PlayerZone p)
  {
    ArrayList list = new ArrayList(Arrays.asList(p.getCards()));
    return list.contains(card);
  }
  public PlayerLife getPlayerLife(String player)
  {
    if(player == null)
      throw new RuntimeException("GameAction : getPlayerLife() player == null");

    if(player.equals(Constant.Player.Human))
      return AllZone.Human_Life;
    else if(player.equals(Constant.Player.Computer))
      return AllZone.Computer_Life;
    else
      throw new RuntimeException("GameAction : getPlayerLife() invalid player string " +player);
  }
  //removes all damage from player's creatures
  public void removeDamage(String player)
  {
    PlayerZone p = AllZone.getZone(Constant.Zone.Play, player);
    ArrayList a = PlayerZoneUtil.getCardType(p, "Creature");
    Card c[] = CardUtil.toCard(a);
    for(int i = 0; i < c.length; i++)
      c[i].setDamage(0);
  }
  public void newGame(Deck humanDeck, Deck computerDeck)
  {
//    AllZone.Computer = new ComputerAI_Input(new ComputerAI_General());

    AllZone.Computer_Life.setLife(20);
    AllZone.Human_Life.setLife(20);

    AllZone.Human_Life.setAssignedDamage(0);
    AllZone.Computer_Life.setAssignedDamage(0);

    AllZone.Stack.reset();
    AllZone.Phase.reset();
    AllZone.Combat.reset();
    AllZone.Display.showCombat("");

    AllZone.Human_Graveyard.reset();
    AllZone.Human_Hand.reset();
    AllZone.Human_Library.reset();
    AllZone.Human_Play.reset();
    AllZone.Human_Removed.reset();

    AllZone.Computer_Graveyard.reset();
    AllZone.Computer_Hand.reset();
    AllZone.Computer_Library.reset();
    AllZone.Computer_Play.reset();
    AllZone.Computer_Removed.reset();

    AllZone.InputControl.resetInput();

    //this never worked anyways
    //randomly lets the user choose who plays first and who draws first
//    chooseWhoPlaysFirst();

    {//re-number cards just so their unique numbers are low, just for user friendliness
      CardFactory c = AllZone.CardFactory;
      Card card;
      int nextUniqueNumber = 1;
      for(int i = 0; i < humanDeck.countMain(); i++)
      {
        card = c.getCard(humanDeck.getMain(i), Constant.Player.Human);
        card.setUniqueNumber(nextUniqueNumber++);
        AllZone.Human_Library.add(card);
      }

      for(int i = 0; i < computerDeck.countMain(); i++)
      {
        card = c.getCard(computerDeck.getMain(i), Constant.Player.Computer);
        card.setUniqueNumber(nextUniqueNumber++);
        AllZone.Computer_Library.add(card);
      }
    }//end re-numbering

    for(int i = 0; i < 100; i++)
      this.shuffle(Constant.Player.Human);

    //do this instead of shuffling Computer's deck
    boolean smoothLand = true;

    if(smoothLand)
    {
      Card[] c = smoothComputerManaCurve(AllZone.Computer_Library.getCards());
      AllZone.Computer_Library.setCards(c);
    }
    else
    {
      AllZone.Computer_Library.setCards(AllZone.Computer_Library.getCards());
      this.shuffle(Constant.Player.Computer);
    }

    for(int i = 0; i < 7; i++)
    {
      this.drawCard(Constant.Player.Human);
      this.drawCard(Constant.Player.Computer);
    }
    AllZone.Stack.reset();//this works, it clears the stack of Upkeep effects like Bitterblossom
    AllZone.InputControl.setInput(new Input_Mulligan());
  }//newGame()

  //this is where the computer cheats
  //changes AllZone.Computer_Library
  private Card[] smoothComputerManaCurve(Card[] in)
  {
    CardList library = new CardList(in);
    library.shuffle();

    //remove all land
    CardList land = library.getType("Land");
    for(int i = 0; i < land.size(); i++)
      library.remove(land.get(i));

    //non-basic lands are removed, because the computer doesn't seem to
    //effectively used them very well
    land = threadLand(land);

    //mana weave, total of 7 land
    library.add(7, land.get(0));
    library.add(8, land.get(1));
    library.add(9, land.get(2));
    library.add(10, land.get(3));
    library.add(11, land.get(4));

    library.add(13, land.get(5));
    library.add(16, land.get(6));

    //add the rest of land to the end of the deck
    for(int i = 0; i < land.size(); i++)
      if(! library.contains(land.get(i)))
      library.add(land.get(i));

/*
    //check
    for(int i = 0; i < 12; i++)
      System.out.println(library.get(i));
*/
    return library.toArray();
  }//smoothComputerManaCurve()

  private CardList getComputerLand(CardList in)
  {
    CardList land = new CardList();
    land = in.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        return c.isLand();
      }
    });

    return land;
  }//getComputerLand()

  //non-basic lands are removed, because the computer doesn't seem to
  //effectively used them very well
  public CardList threadLand(CardList in)
  {
    String[] basicLand = {"Forest", "Swamp", "Mountain", "Island", "Plains"};
    ArrayList land = new ArrayList();

    //get different CardList of all Forest, Swamps, etc...
    CardList check;
    for(int i = 0; i < basicLand.length; i++)
    {
      check = in.getName(basicLand[i]);

      if(! check.isEmpty())
        land.add(check);
    }
/*
    //get non-basic land CardList
    check = in.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        return c.isLand() && !c.isBasicLand();
      }
    });
    if(! check.isEmpty())
      land.add(check);
*/

    //thread all separate CardList's of land together to get something like
    //Mountain, Plains, Island, Mountain, Plains, Island
    CardList out = new CardList();

    int i = 0;
    while(! land.isEmpty())
    {
      i = (i + 1) % land.size();

      check = (CardList) land.get(i);
      if(check.isEmpty())
      {
        land.remove(i);
        i--;
        continue;
      }

      out.add(check.get(0));
      check.remove(0);
    }//while

    return out;
  }//threadLand()


  private int getDifferentLand(CardList list, String land)
  {
    int out = 0;

    return out;
  }


  //decides who goes first when starting another game, used by newGame()
  public void chooseWhoPlaysFirst()
  {
    //lets the user decides who plays first
    boolean humanChoose = false;
    if(Constant.Runtime.WinLose.getWin() == 0 && Constant.Runtime.WinLose.getLose() == 0)
      humanChoose = MyRandom.random.nextBoolean();
    else if(! Constant.Runtime.WinLose.didWinRecently())
      humanChoose = true;

    //does the player go first?
    boolean humanFirst;

    if(humanChoose)
    {
      int n = JOptionPane.showConfirmDialog(null, "Do you want to play first?", "", JOptionPane.YES_NO_OPTION);
      if(n == JOptionPane.YES_OPTION)
        humanFirst =  true;
      else
        humanFirst = false;
    }
    else//computer randomly decides who goes first
      humanFirst = MyRandom.random.nextBoolean();

    String message;
    if(humanFirst)
      message = "You play first";
    else
    {
      message = "Computer plays first";
      AllZone.Phase.setPhase(Constant.Phase.Main1, Constant.Player.Computer);
    }

    //show message if player doesn't get to choose
    if(! humanChoose)
      JOptionPane.showMessageDialog(null, message);
  }//choooseWhoPlaysFirst()

  //if Card had the type "Aura" this method would always return true, since local enchantments are always attached to something
  //if Card is "Equipment", returns true if attached to something
  public boolean isAttachee(Card c)
  {
    CardList list = new CardList(AllZone.Computer_Play.getCards());
    list.addAll                 (AllZone.Human_Play.getCards());

    for(int i = 0; i < list.size(); i++)
    {
      CardList check = new CardList(list.getCard(i).getAttachedCards());
      if(check.contains(c))
        return true;
    }

    return false;
  }//isAttached(Card c)

  public boolean canTarget(String targetPlayer)
  {
    return true;
  }
  public boolean canTarget(Card target, Card source)
  {
    if(target.getKeyword().contains("Cannot be target of spells or abilities"))
      return false;

    return true;
  }
  public void playCard(Card c)
  {
    SpellAbility[] choices = canPlaySpellAbility(c.getSpellAbility());
    SpellAbility sa;
/*
 System.out.println(choices.length);
 for(int i = 0; i < choices.length; i++)
     System.out.println(choices[i]);
*/
    if(choices.length == 0)
      return;
    else if(choices.length == 1)
      sa = choices[0];
    else
      sa = (SpellAbility) AllZone.Display.getChoiceOptional("Choose", choices);

    if(sa == null)
      return;

    playSpellAbility(sa);
  }
  public void playSpellAbility(SpellAbility sa)
  {
    if(sa.getBeforePayMana() == null)
      AllZone.InputControl.setInput(new Input_PayManaCost(sa));
    else
      AllZone.InputControl.setInput(sa.getBeforePayMana());
  }
  public SpellAbility[] canPlaySpellAbility(SpellAbility[] sa)
  {
    ArrayList list = new ArrayList();

    for(int i = 0; i < sa.length; i++)
      if(sa[i].canPlay())
        list.add(sa[i]);

    SpellAbility[] array = new SpellAbility[list.size()];
    list.toArray(array);
    return array;
  }//canPlaySpellAbility()

  public static void main(String[] args)
  {
    GameAction gameAction = new GameAction();
    GenerateConstructedDeck gen = new GenerateConstructedDeck();

    for(int i = 0; i < 2000; i++)
    {
      CardList list = gen.generateDeck();

      Card[] card = gameAction.smoothComputerManaCurve(list.toArray());

      CardList check = new CardList();
      for(int a = 0; a < 30; a++)
        check.add(card[a]);

      if(check.getType("Land").size() != 7)
      {
        System.out.println("error - " +check);
        break;
      }
    }//for
  }
}