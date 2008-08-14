import java.util.*;

public class GenerateConstructedDeck
{
  private String color1;
  private String color2;

  private Map map = new HashMap();

  public GenerateConstructedDeck() {setupMap();}

  private void setupMap()
  {
    map.put(Constant.Color.Black , "Swamp");
    map.put(Constant.Color.Blue  , "Island");
    map.put(Constant.Color.Green , "Forest");
    map.put(Constant.Color.Red   , "Mountain");
    map.put(Constant.Color.White , "Plains");
  }

  public CardList generateDeck()
  {
    CardList deck;

    int check;

    do{
      deck = get2ColorDeck();
      check = deck.getType("Creature").size();

    }while(check < 16 || 24 < check);

    addLand(deck);

    if(deck.size() != 60)
      throw new RuntimeException("GenerateConstructedDeck() : generateDeck() error, deck size it not 60, deck size is " +deck.size());

    return deck;
  }
  //25 lands
  private void addLand(CardList list)
  {
    Card land;
    for(int i = 0; i < 13; i++)
    {
      land = AllZone.CardFactory.getCard(map.get(color1).toString(), Constant.Player.Computer);
      list.add(land);

      land = AllZone.CardFactory.getCard(map.get(color2).toString(), Constant.Player.Computer);
      list.add(land);
    }
  }//addLand()
  private CardList getCards()
  {
    return filterBadCards(AllZone.CardFactory.getAllCards());
  }//getCards()
  private CardList get2ColorDeck()
  {
    CardList deck = get2Colors(getCards());

    CardList out = new CardList();
    deck.shuffle();

    //trim deck size down to 34 cards, presumes 26 land, for a total of 60 cards
    for(int i = 0; i < 34 && i < deck.size(); i++)
      out.add(deck.get(i));

    return out;
  }
  private CardList get2Colors(CardList in)
  {
    int a;
    int b;

    do{
      a = CardUtil.getRandomIndex(Constant.Color.onlyColors);
      b = CardUtil.getRandomIndex(Constant.Color.onlyColors);
    }while(a == b);//do not want to get the same color twice

    color1 = Constant.Color.onlyColors[a];
    color2 = Constant.Color.onlyColors[b];

    CardList out = new CardList();
    out.addAll(CardListUtil.getColor(in, color1).toArray());
    out.addAll(CardListUtil.getColor(in, color2).toArray());

    CardList artifact = in.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        return c.isArtifact() && !c.getName().equals("Sarcomite Myr");
      }
    });

    if(MyRandom.percentTrue(20))
      out.addAll(artifact.toArray());

    out = filterBadCards(out);

    return out;
  }

  private CardList filterBadCards(CardList list)
  {
    //remove "bad" and multi-colored cards
    final ArrayList remove = new ArrayList();
    remove.add("Sarcomite Myr");
    remove.add("Force of Savagery");
    remove.add("Darksteel Colossus");
    remove.add("Jokulhaups");
    remove.add("Steel Wall");
    remove.add("Ornithopter");
    remove.add("Amnesia");
    remove.add("Battle of Wits");
    remove.add("Ashes to Ashes");
    remove.add("Haunted Angel");
    remove.add("Sky Swallower");
    remove.add("Magus of the Library");
    remove.add("The Unspeakable");
    remove.add("Wall of Kelp");

    remove.add("Incendiary Command");
    remove.add("Memnarch");
    remove.add("Plague Wind");
    remove.add("Klaas, Elf Friend");
    remove.add("Delirium Skeins");


    remove.add("Undying Beast");
    remove.add("Wit's End");
    remove.add("Hymn to Tourach");

    remove.add("Blinding Light");
    remove.add("Hymn to Tourach");

    final ArrayList goodLand = new ArrayList();
    goodLand.add("Faerie Conclave");
    goodLand.add("Forbidding Watchtower");
    goodLand.add("Treetop Village");

    CardList out = list.filter(new CardListFilter()
    {
      public boolean addCard(Card c)
      {
        return CardUtil.getColors(c).size() == 1 && //no gold colored cards
               !c.isLand()                       && //no land
               !remove.contains(c.getName())     || //OR very important
               goodLand.contains(c.getName());
      }
    });

    return out;
  }//filterBadCards()
  public static void main(String[] args)
  {
    GenerateConstructedDeck g = new GenerateConstructedDeck();

    for(int i = 0; i < 10; i++)
    {
      CardList c = g.generateDeck();
      System.out.println(c.getType("Creature").size() +" - " +c.size());
    }
    System.exit(1);

  }//main
}