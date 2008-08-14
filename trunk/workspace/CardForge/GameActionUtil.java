import java.util.*;

public class GameActionUtil
{
  public static void executeUpkeepEffects()
  {
    upkeep_Nettletooth_Djinn();
    upkeep_Fledgling_Djinn();
    upkeep_Juzam_Djinn();
    upkeep_Seizan_Perverter_of_Truth();
    upkeep_Howling_Mine();
    upkeep_Serendib_Efreet();
    upkeep_Bitterblossom();
    upkeep_Battle_of_Wits();
    upkeep_Klass();
  }


  private static void upkeep_Klass()
  {
    final String player = AllZone.Phase.getActivePlayer();
    PlayerZone playZone =  AllZone.getZone(Constant.Zone.Play, player);

    CardList elf = new CardList(playZone.getCards());
    elf = elf.getType("Elf");

    CardList list = new CardList(playZone.getCards());
    list = list.getName("Klaas, Elf Friend");

    if(0 < list.size() && 10 <= elf.size())
    {
      Ability ability = new Ability(list.get(0), "0")
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(player);
          PlayerLife life = AllZone.GameAction.getPlayerLife(opponent);
          life.setLife(0);
        }
      };//Ability

      ability.setStackDescription("Klaas, Elf Friend - " +player +" wins the game");
      AllZone.Stack.add(ability);
    }//if
  }//upkeep_Klass





  private static void upkeep_Battle_of_Wits()
  {
    final String player = AllZone.Phase.getActivePlayer();
    PlayerZone playZone =  AllZone.getZone(Constant.Zone.Play, player);
    PlayerZone libraryZone = AllZone.getZone(Constant.Zone.Library, player);

    CardList list = new CardList(playZone.getCards());
    list = list.getName("Battle of Wits");

    if(0 < list.size() && 200 <= libraryZone.size())
    {
      Ability ability = new Ability(list.get(0), "0")
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(player);
          PlayerLife life = AllZone.GameAction.getPlayerLife(opponent);
          life.setLife(0);
        }
      };//Ability

      ability.setStackDescription("Battle of Wits - " +player +" wins the game");
      AllZone.Stack.add(ability);
    }//if
  }//upkeep_Battle_of_Wits
  private static void upkeep_Bitterblossom()
  {
    final String player = AllZone.Phase.getActivePlayer();
    PlayerZone playZone =  AllZone.getZone(Constant.Zone.Play, player);

    CardList list = new CardList(playZone.getCards());
    list = list.getName("Bitterblossom");

    Ability ability;
    for(int i = 0; i < list.size(); i++)
    {
      ability = new Ability(list.get(i), "0")
      {
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(player).subtractLife(1);
          Card c = new Card();

          c.setOwner(player);
          c.setController(player);

          c.setName("Token");
          c.setManaCost("B");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Faerie");
          c.addType("Rogue");
          c.addKeyword("Flying");

          c.setAttack(1);
          c.setDefense(1);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, player);
          play.add(c);
        }//resolve()
      };//Ability
      ability.setStackDescription("Bitterblossom - deals 1 damage to " +player +" and put a 1/1 token into play.");

      AllZone.Stack.add(ability);
    }//for
  }//upkeep_Bitterblossom()




  private static void upkeep_Serendib_Efreet()
  {
    final String player = AllZone.Phase.getActivePlayer();
    PlayerZone playZone =  AllZone.getZone(Constant.Zone.Play, player);

    CardList list = new CardList(playZone.getCards());
    list = list.getName("Serendib Efreet");

    Ability ability;
    for(int i = 0; i < list.size(); i++)
    {
      ability = new Ability(list.get(i), "0")
      {
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(player).subtractLife(1);
        }
      };//Ability
      ability.setStackDescription("Serendib Efreet - deals 1 damage to " +player);

      AllZone.Stack.add(ability);
    }//for
  }//upkeep_Serendib_Efreet()




  private static void upkeep_Nettletooth_Djinn()
  {
    final String player = AllZone.Phase.getActivePlayer();
    PlayerZone playZone =  AllZone.getZone(Constant.Zone.Play, player);

    CardList list = new CardList(playZone.getCards());
    list = list.getName("Nettletooth Djinn");

    Ability ability;
    for(int i = 0; i < list.size(); i++)
    {
      ability = new Ability(list.get(i), "0")
      {
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(player).subtractLife(1);
        }
      };//Ability
      ability.setStackDescription("Nettletooth Djinn - deals 1 damage to " +player);

      AllZone.Stack.add(ability);
    }//for
  }//upkeep_Nettletooth_Djinn()



  private static void upkeep_Howling_Mine()
  {
    final String player = AllZone.Phase.getActivePlayer();

    CardList list = new CardList();
    list.addAll(AllZone.Human_Play.getCards());
    list.addAll(AllZone.Computer_Play.getCards());

    //does not check to see if Howling Mine is untapped
    list = list.getName("Howling Mine");

    for(int i = 0; i < list.size(); i++)
      AllZone.GameAction.drawCard(player);
  }//upkeep_Howling_Mine()


  private static void upkeep_Seizan_Perverter_of_Truth()
  {
    final String player = AllZone.Phase.getActivePlayer();

    //get all creatures
    CardList list = new CardList();
    list.addAll(AllZone.Human_Play.getCards());
    list.addAll(AllZone.Computer_Play.getCards());

    list = list.getName("Seizan, Perverter of Truth");

    if(list.size() == 0)
      return;

    Ability ability = new Ability(list.get(0), "0")
    {
      public void resolve()
      {
        AllZone.GameAction.getPlayerLife(player).subtractLife(2);
        AllZone.GameAction.drawCard(player);
        AllZone.GameAction.drawCard(player);
      }
    };
    ability.setStackDescription("Seizan, Perverter of Truth - " +player +" loses 2 life and draws 2 cards");

    AllZone.Stack.add(ability);
  }//upkeep_Seizan_Perverter_of_Truth()


  private static void upkeep_Juzam_Djinn()
  {
    final String player = AllZone.Phase.getActivePlayer();
    PlayerZone playZone =  AllZone.getZone(Constant.Zone.Play, player);

    CardList list = new CardList(playZone.getCards());
    list = list.getName("Juzam Djinn");

    Ability ability;
    for(int i = 0; i < list.size(); i++)
    {
      ability = new Ability(list.get(i), "0")
      {
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(player).subtractLife(1);
        }
      };//Ability
      ability.setStackDescription("Juzam Djinn - deals 1 damage to " +player);

      AllZone.Stack.add(ability);
    }//for
  }//upkeep_Juzam_Djinn()


  private static void upkeep_Fledgling_Djinn()
  {
    final String player = AllZone.Phase.getActivePlayer();
    PlayerZone playZone =  AllZone.getZone(Constant.Zone.Play, player);

    CardList list = new CardList(playZone.getCards());
    list = list.getName("Fledgling Djinn");

    Ability ability;
    for(int i = 0; i < list.size(); i++)
    {
      ability = new Ability(list.get(i), "0")
      {
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(player).subtractLife(1);
        }
      };//Ability
      ability.setStackDescription("Fledgling Djinn - deals 1 damage to " +player);

      AllZone.Stack.add(ability);
    }//for
  }//upkeep_Fledgling_Djinn()

  public static void executeCardStateEffects()
  {
    Serra_Avatar.execute();
    Avatar_Token.execute();

    Baru.execute();
    Reach_of_Branches.execute();

    Essence_Warden.execute();
    Soul_Warden.execute();

    Nightmare.execute();
    Korlash.execute();

    Glorious_Anthem.execute();
    Gaeas_Anthem.execute();
    Imperious_Perfect.execute();
    Mad_Auntie.execute();

    Wonder.execute();
    Anger.execute();
    Valor.execute();

    Veteran_Armorer.execute();
    Radiant_Archangel.execute();
    Castle_Raptors.execute();

    Sliver_Legion.execute();
    Muscle_Sliver.execute();

    Bonesplitter_Sliver.execute();
    Might_Sliver.execute();
    Watcher_Sliver.execute();

    Winged_Sliver.execute();
    Sinew_Sliver.execute();
    Horned_Sliver.execute();

    Heart_Sliver.execute();
    Gemhide_Sliver.execute();

    Blade_Sliver.execute();
    Battering_Sliver.execute();

    Joiner_Adept.execute();
  }//executeCardStateEffects()


  private static Command Joiner_Adept = new Command()
  {
    CardList gloriousAnthemList = new CardList();

    String[] keyword = {"tap: add B", "tap: add W", "tap: add G", "tap: add U", "tap: add R"};

    final void addMana(Card c)
    {
      for(int i = 0; i < keyword.length; i++)
        c.addKeyword(keyword[i]);
    }

    final void removeMana(Card c)
    {
        for(int i = 0; i < keyword.length; i++)
          c.removeKeyword(keyword[i]);
    }

    public void execute()
    {
      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        removeMana(c);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Joiner Adept");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length && outer < 1; outer++) //1 is a cheat
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Land");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          addMana(c);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver



  private static Command Battering_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      String keyword = "Trample";

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.removeKeyword(keyword);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Battering Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.addKeyword(keyword);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver





  private static Command Blade_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      int pumpAttack = 1;
      int pumpDefense = 0;

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - pumpAttack);
        c.setDefense(c.getDefense() - pumpDefense);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Blade Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + pumpAttack);
          c.setDefense(c.getDefense() + pumpDefense);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver




  private static Command Gemhide_Sliver = new Command()
  {
   CardList gloriousAnthemList = new CardList();

    String[] keyword = {"tap: add B", "tap: add W", "tap: add G", "tap: add U", "tap: add R"};

    final void addMana(Card c)
    {
      for(int i = 0; i < keyword.length; i++)
        c.addKeyword(keyword[i]);
    }

    final void removeMana(Card c)
    {
        for(int i = 0; i < keyword.length; i++)
          c.removeKeyword(keyword[i]);
    }

    public void execute()
    {
      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        removeMana(c);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Gemhide Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length && outer < 1; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          addMana(c);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver



  private static Command Heart_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      String keyword = "Haste";

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.removeKeyword(keyword);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Heart Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.addKeyword(keyword);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver



  private static Command Horned_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      String keyword = "Trample";

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.removeKeyword(keyword);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Horned Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.addKeyword(keyword);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver


  private static Command Sinew_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      int pumpAttack = 1;
      int pumpDefense = 1;

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - pumpAttack);
        c.setDefense(c.getDefense() - pumpDefense);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Sinew Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + pumpAttack);
          c.setDefense(c.getDefense() + pumpDefense);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver



  private static Command Winged_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      String keyword = "Flying";

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.removeKeyword(keyword);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Winged Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.addKeyword(keyword);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver



  private static Command Bonesplitter_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      int pumpAttack = 2;
      int pumpDefense = 0;

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - pumpAttack);
        c.setDefense(c.getDefense() - pumpDefense);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Bonesplitter Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + pumpAttack);
          c.setDefense(c.getDefense() + pumpDefense);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver



  private static Command Might_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      int pumpAttack = 2;
      int pumpDefense = 2;

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - pumpAttack);
        c.setDefense(c.getDefense() - pumpDefense);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Might Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + pumpAttack);
          c.setDefense(c.getDefense() + pumpDefense);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver



  private static Command Watcher_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      int pumpAttack = 0;
      int pumpDefense = 2;

      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - pumpAttack);
        c.setDefense(c.getDefense() - pumpDefense);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Watcher Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + pumpAttack);
          c.setDefense(c.getDefense() + pumpDefense);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver




  private static Command Muscle_Sliver = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - 1);
        c.setDefense(c.getDefense() - 1);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Muscle Sliver");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + 1);
          c.setDefense(c.getDefense() + 1);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Muscles_Sliver




  private static Command Sliver_Legion = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    int pump = 0;

    public void execute()
    {
      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - pump);
        c.setDefense(c.getDefense() - pump);
      }

      //add +pump/+pump to cards
      list.clear();
      PlayerZone[] zone = getZone("Sliver Legion");

      //get all Slivers
      CardList all = new CardList();
      all.addAll(AllZone.Human_Play.getCards());
      all.addAll(AllZone.Computer_Play.getCards());

      CardList allSliver = all.getType("Sliver");
      pump = allSliver.size();

      //for each zone found add +pump/+pump to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Sliver");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + pump);
          c.setDefense(c.getDefense() + pump);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Sliver_Legion




private static Command Serra_Avatar = new Command()
{
public void execute()
{
//get all creatures
CardList list = new CardList();
list.addAll(AllZone.Human_Play.getCards());
list.addAll(AllZone.Computer_Play.getCards());
list = list.getName("Serra Avatar");

for(int i = 0; i < list.size(); i++)
{
Card card = list.get(i);
int n = AllZone.GameAction.getPlayerLife(card.getController()).getLife();
card.setAttack(n);
card.setDefense(n);
}//for
}//execute
};//Serra Avator

	private static Command Avatar_Token = new Command() {
		public void execute() {
			// get all creatures
			CardList list = new CardList();
			list.addAll(AllZone.Human_Play.getCards());
			list.addAll(AllZone.Computer_Play.getCards());
			list = list.getName("Token");

			for (int i = 0; i < list.size(); i++) {
				Card card = list.get(i);
				if (card.getType().contains("Avatar") 
						//&& card.getKeyword().contains("This creature's power and toughness are each equal to your life total.")
						) {
					int n = AllZone.GameAction.getPlayerLife(card.getController())
						.getLife();
					card.setAttack(n);
					card.setDefense(n);
				}
			}// for
		}// execute
	};// Avator Token

  //Reach of Branches
  private static Command Reach_of_Branches = new Command()
  {
    CardList oldForest = new CardList();

    public void execute()
    {
      //count card "Reach of Branches" in graveyard
      final String player = AllZone.Phase.getActivePlayer();
      final PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, player);
      CardList tempList = new CardList(grave.getCards());
      final CardList nCard = tempList.getName("Reach of Branches");

      //get all Forest that player has
      final PlayerZone play = AllZone.getZone(Constant.Zone.Play, player);
      CardList newForest = new CardList(play.getCards());
      newForest = newForest.getType("Forest");

      //if "Reach of Branches" is in graveyard and played a Forest
      if(0 < nCard.size() && newForest(oldForest, newForest))
      {
        SpellAbility ability = new Ability(new Card(), "0")
        {
          public void resolve()
          {
            //return all Reach of Branches to hand
            PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, player);
            for(int i = 0; i < nCard.size(); i++)
            {
              grave.remove(nCard.get(i));
              hand.add(nCard.get(i));
            }
          }//resolve()
        };//SpellAbility
        ability.setStackDescription("Reach of Branches - return card to " +player +"'s hand");
        AllZone.Stack.add(ability);
      }//if

      //potential problem: if a Forest is bounced to your hand "Reach Branches"
      //won't trigger when you play that Forest
      oldForest.addAll(newForest.toArray());
    }//execute

    //check if newList has anything that oldList doesn't have
    boolean newForest(CardList oldList, CardList newList)
    {
      //check if a Forest came into play under your control
      for(int i = 0; i < newList.size(); i++)
        if(! oldList.contains(newList.get(i)))
          return true;

      return false;
    }//newForest()
  };//Reach of Branches



  private static Command Mad_Auntie = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - 1);
        c.setDefense(c.getDefense() - 1);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Mad Auntie");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Goblin");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + 1);
          c.setDefense(c.getDefense() + 1);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute
  };//Mad_Auntie()



  private static Command Imperious_Perfect = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - 1);
        c.setDefense(c.getDefense() - 1);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Imperious Perfect");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Elf");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + 1);
          c.setDefense(c.getDefense() + 1);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute
  };//Imperious_Perfect()

  private static Command Baru = new Command()
  {
    CardList old = new CardList();
    public void execute()
    {
      //get all Forests
      CardList all = new CardList();
      all.addAll(AllZone.Human_Play.getCards());
      all.addAll(AllZone.Computer_Play.getCards());
      CardList current = all.getType("Forest");

      for(int outer = 0; outer < current.size(); outer++)
      {
        if(old.contains(current.get(outer)))
          continue;

        final CardList test = all.getName("Baru, Fist of Krosa");
        SpellAbility ability = new Ability(new Card(), "0")
        {
          public void resolve()
          {
            Card c = null;
            if(! test.isEmpty())
              c = test.get(0);

            CardList all = new CardList(AllZone.getZone(Constant.Zone.Play, c.getController()).getCards());
            all = all.getType("Creature");
            for(int i = 0; i < all.size(); i++)
            {
              all.get(i).setAttack(all.get(i).getAttack() + 1);
              all.get(i).setDefense(all.get(i).getDefense() + 1);
              all.get(i).addKeyword("Trample");

              final Card c1 = all.get(i);
              AllZone.EndOfTurn.addUntil(new Command()
              {
                public void execute()
                {
                  c1.setAttack(c1.getAttack() - 1);
                  c1.setDefense(c1.getDefense() - 1);
                  c1.removeKeyword("Trample");
                }
              });
            }//for
          }
        };
        ability.setStackDescription("Baru, Fist of Krosa - creatures get +1/+1 until end of turn.");

        if(!all.getName("Baru, Fist of Krosa").isEmpty())
          AllZone.Stack.push(ability);
      }//outer for

      old = current;
    }//execute()
  };//Baru



  private static Command Essence_Warden = new Command()
  {
    CardList old = new CardList();
    public void execute()
    {
      //get all creatures
      CardList current = new CardList();
      current.addAll(AllZone.Human_Play.getCards());
      current.addAll(AllZone.Computer_Play.getCards());
      current = current.getType("Creature");


      CardList cardList = current.getName("Essence Warden");

      //Essence Warden gains life except when another Essence Warden comes into play
      //this is the only way I could get things to work
      //remove all cards named Soul Warden, ugly hack
      for(int i = 0; i < cardList.size(); i++)
        current.remove(cardList.get(i));


      for(int outer = 0; outer < cardList.size(); outer++)
      {
        final int[] n = new int[1];
        for(int i = 0; i < current.size(); i++)
        {
          if(! old.contains(current.getCard(i)))
            n[0]++;
        }

        final PlayerLife life =  AllZone.GameAction.getPlayerLife(cardList.get(outer).getController());
        SpellAbility ability = new Ability(new Card(), "0")
        {
          public void resolve()
          {
            life.addLife(n[0]);
          }
        };
        ability.setStackDescription(cardList.get(outer).getName() +" - " +cardList.get(outer).getController() +" gains " +n[0] +" life");

        if(n[0] != 0)
          AllZone.Stack.push(ability);
      }//outer for

      old = current;
    }//execute()
  };



  private static Command Soul_Warden = new Command()
  {
    CardList old = new CardList();
    public void execute()
    {
      //get all creatures
      CardList current = new CardList();
      current.addAll(AllZone.Human_Play.getCards());
      current.addAll(AllZone.Computer_Play.getCards());
      current = current.getType("Creature");


      CardList cardList = current.getName("Soul Warden");

      //Soul Warden gains life except when another Soul Warden comes into play
      //this is the only way I could get things to work
      //remove all cards named Soul Warden, ugly hack
      for(int i = 0; i < cardList.size(); i++)
        current.remove(cardList.get(i));


      for(int outer = 0; outer < cardList.size(); outer++)
      {
        final int[] n = new int[1];
        for(int i = 0; i < current.size(); i++)
        {
          if(! old.contains(current.getCard(i)))
            n[0]++;
        }

        final PlayerLife life =  AllZone.GameAction.getPlayerLife(cardList.get(outer).getController());
        SpellAbility ability = new Ability(new Card(), "0")
        {
          public void resolve()
          {
            life.addLife(n[0]);
          }
        };
        ability.setStackDescription(cardList.get(outer).getName() +" - " +cardList.get(outer).getController() +" gains " +n[0] +" life");

        if(n[0] != 0)
          AllZone.Stack.push(ability);
      }//outer for

      old = current;
    }//execute()
  };


  private static Command Korlash = new Command()
  {
    public void execute()
    {
      //get all creatures
      CardList list = new CardList();
      list.addAll(AllZone.Human_Play.getCards());
      list.addAll(AllZone.Computer_Play.getCards());
      list = list.getName("Korlash, Heir to Blackblade");

      for(int i = 0; i < list.size(); i++)
      {
        Card c = list.get(i);
        c.setAttack(countSwamps(c));
        c.setDefense(c.getAttack());
      }
    }//execute()
    private int countSwamps(Card c)
    {
      PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getController());
      CardList swamps = new CardList(play.getCards());
      swamps = swamps.getType("Swamp");
      return swamps.size();
    }
  };

  private static Command Nightmare = new Command()
  {
    public void execute()
    {
      //get all creatures
      CardList list = new CardList();
      list.addAll(AllZone.Human_Play.getCards());
      list.addAll(AllZone.Computer_Play.getCards());
      list = list.getName("Nightmare");

      for(int i = 0; i < list.size(); i++)
      {
        Card c = list.get(i);
        c.setAttack(countSwamps(c));
        c.setDefense(c.getAttack());
      }

    }//execute()

    private int countSwamps(Card c)
    {
      PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getController());
      CardList swamps = new CardList(play.getCards());
      swamps = swamps.getType("Swamp");
      return swamps.size();
    }
  };


  private static Command Castle_Raptors = new Command()
  {
    CardList old = new CardList();
    int pump = 2;
    public void execute()
    {
      Card c;
      //reset all previous cards stats
      for(int i = 0; i < old.size(); i++)
      {
        c = old.get(i);
        c.setDefense(c.getDefense() - pump);
      }
      old.clear();

      CardList list = getCard("Castle Raptors");
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        //only add boost if card is untapped
        if(c.isUntapped())
        {
          c.setDefense(c.getDefense() + pump);
          old.add(c);
        }
      }//for
    }//execute()
    CardList getCard(String name)
    {
      CardList list = new CardList();
      list.addAll(AllZone.Human_Play.getCards());
      list.addAll(AllZone.Computer_Play.getCards());
      list = list.getName(name);
      return list;
    }//getRaptor()
  };//Castle Raptors




  private static Command Radiant_Archangel = new Command()
  {
    CardList old = new CardList();
    int pump = 0;
    public void execute()
    {
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < old.size(); i++)
      {
        c = old.get(i);
        c.setAttack(c.getAttack() - pump);
        c.setDefense(c.getDefense() - pump);
      }

      old.clear();

      //get all cards names Radiant, Archangel
      CardList angel = getAngel();
      pump = countFlying();

      for(int i = 0; i < angel.size(); i++)
      {
        c = angel.get(i);
        c.setAttack(c.getAttack() + pump);
        c.setDefense(c.getDefense() + pump);
      }
      old = angel;
    }//execute()

    CardList getAngel()
    {
      CardList angel = new CardList();
      angel.addAll(AllZone.Human_Play.getCards());
      angel.addAll(AllZone.Computer_Play.getCards());
      angel = angel.getName("Radiant, Archangel");
      return angel;
    }//getAngel()

    int countFlying()
    {
      //count number of creatures with flying
      CardList flying = new CardList();
      flying.addAll(AllZone.Human_Play.getCards());
      flying.addAll(AllZone.Computer_Play.getCards());
      flying = flying.filter(new CardListFilter()
      {
        public boolean addCard(Card c)
        {
          return c.isCreature() && c.getKeyword().contains("Flying");
        }
      });
      return flying.size();
    }
  };//Radiant, Archangel



  private static Command Veteran_Armorer = new Command()
  {
    CardList old = new CardList();
    public void execute()
    {
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < old.size(); i++)
      {
        c = old.get(i);
        c.setDefense(c.getDefense() - 1);
      }

      //add +1/+1 to cards
      old.clear();
      PlayerZone[] zone = getZone("Veteran Armorer");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Creature");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setDefense(c.getDefense() + 1);

          old.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Veteran_Armorer




  private static Command Valor = new Command()
  {
    CardList old = new CardList();

    public void execute()
    {
      //reset creatures
      removeHaste(old);

      if(isInGrave(Constant.Player.Computer))
        addHaste(Constant.Player.Computer);

      if(isInGrave(Constant.Player.Human))
        addHaste(Constant.Player.Human);
    }//execute()
    void addHaste(String player)
    {
      PlayerZone play = AllZone.getZone(Constant.Zone.Play, player);
      CardList list = new CardList(play.getCards());
      list = list.getType("Creature");

      //add creatures to "old" or previous list of creatures
      old.addAll(list.toArray());

      addHaste(list);
    }
    boolean isInGrave(String player)
    {
      PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, player);
      CardList list = new CardList(grave.getCards());
      return list.containsName("Valor");
    }
    void removeHaste(CardList list)
    {
      for(int i = 0; i < list.size(); i++)
        list.get(i).removeKeyword("Vigilance");
    }
    void addHaste(CardList list)
    {
      for(int i = 0; i < list.size(); i++)
        list.get(i).addKeyword("Vigilance");
    }
  };//Valor




  private static Command Anger = new Command()
  {
    CardList old = new CardList();

    public void execute()
    {
      //reset creatures
      removeHaste(old);

      if(isInGrave(Constant.Player.Computer))
        addHaste(Constant.Player.Computer);

      if(isInGrave(Constant.Player.Human))
        addHaste(Constant.Player.Human);
    }//execute()
    void addHaste(String player)
    {
      PlayerZone play = AllZone.getZone(Constant.Zone.Play, player);
      CardList list = new CardList(play.getCards());
      list = list.getType("Creature");

      //add creatures to "old" or previous list of creatures
      old.addAll(list.toArray());

      addHaste(list);
    }
    boolean isInGrave(String player)
    {
      PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, player);
      CardList list = new CardList(grave.getCards());
      return list.containsName("Anger");
    }
    void removeHaste(CardList list)
    {
      for(int i = 0; i < list.size(); i++)
        list.get(i).removeKeyword("Haste");
    }
    void addHaste(CardList list)
    {
      for(int i = 0; i < list.size(); i++)
        list.get(i).addKeyword("Haste");
    }
  };//Anger



  private static Command Wonder = new Command()
  {
    CardList old = new CardList();

    public void execute()
    {
      //reset creatures
      removeFlying(old);

      if(isInGrave(Constant.Player.Computer))
        addFlying(Constant.Player.Computer);

      if(isInGrave(Constant.Player.Human))
        addFlying(Constant.Player.Human);
    }//execute()
    void addFlying(String player)
    {
      PlayerZone play = AllZone.getZone(Constant.Zone.Play, player);
      CardList list = new CardList(play.getCards());
      list = list.getType("Creature");

      //add creatures to "old" or previous list of creatures
      old.addAll(list.toArray());

      addFlying(list);
    }
    boolean isInGrave(String player)
    {
      PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, player);
      CardList list = new CardList(grave.getCards());
      return list.containsName("Wonder");
    }
    void removeFlying(CardList list)
    {
      for(int i = 0; i < list.size(); i++)
        list.get(i).removeKeyword("Flying");
    }
    void addFlying(CardList list)
    {
      for(int i = 0; i < list.size(); i++)
        list.get(i).addKeyword("Flying");
    }
  };//Wonder

private static Command Glorious_Anthem = new Command()
{
CardList gloriousAnthemList = new CardList();
public void execute()
{
CardList list = gloriousAnthemList;
Card c;
//reset all cards in list - aka "old" cards
for(int i = 0; i < list.size(); i++)
{
c = list.get(i);
c.setAttack(c.getAttack() - 1);
c.setDefense(c.getDefense() - 1);
}

//add +1/+1 to cards
list.clear();
PlayerZone[] zone = getZone("Glorious Anthem");

//for each zone found add +1/+1 to each card
for(int outer = 0; outer < zone.length; outer++)
{
CardList creature = new CardList(zone[outer].getCards());
creature = creature.getType("Creature");

for(int i = 0; i < creature.size(); i++)
{
c = creature.get(i);
c.setAttack(c.getAttack() + 1);
c.setDefense(c.getDefense() + 1);

gloriousAnthemList.add(c);
}//for inner
}//for outer
}//execute()
};//Glorious Anthem



  private static Command Gaeas_Anthem = new Command()
  {
    CardList gloriousAnthemList = new CardList();
    public void execute()
    {
      CardList list = gloriousAnthemList;
      Card c;
      //reset all cards in list - aka "old" cards
      for(int i = 0; i < list.size(); i++)
      {
        c = list.get(i);
        c.setAttack(c.getAttack() - 1);
        c.setDefense(c.getDefense() - 1);
      }

      //add +1/+1 to cards
      list.clear();
      PlayerZone[] zone = getZone("Gaea's Anthem");

      //for each zone found add +1/+1 to each card
      for(int outer = 0; outer < zone.length; outer++)
      {
        CardList creature = new CardList(zone[outer].getCards());
        creature = creature.getType("Creature");

        for(int i = 0; i < creature.size(); i++)
        {
          c = creature.get(i);
          c.setAttack(c.getAttack() + 1);
          c.setDefense(c.getDefense() + 1);

          gloriousAnthemList.add(c);
        }//for inner
      }//for outer
    }//execute()
  };//Gaea's Anthem



  //returns all PlayerZones that has at least 1 Glorious Anthem
  //if Computer has 2 Glorious Anthems, AllZone.Computer_Play will be returned twice
  private static PlayerZone[] getZone(String cardName)
  {
    CardList all = new CardList();
    all.addAll(AllZone.Human_Play.getCards());
    all.addAll(AllZone.Computer_Play.getCards());

    ArrayList zone = new ArrayList();
    for(int i = 0; i < all.size(); i++)
      if(all.get(i).getName().equals(cardName))
        zone.add(AllZone.getZone(all.get(i)));

    PlayerZone[] z = new PlayerZone[zone.size()];
    zone.toArray(z);
    return z;
  }
}