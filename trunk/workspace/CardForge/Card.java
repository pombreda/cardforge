import java.util.*;

public class Card extends MyObservable
{
  private static int nextUniqueNumber;
  private int uniqueNumber = nextUniqueNumber++;

  private Collection keyword   = new TreeSet();
  private ArrayList attached   = new ArrayList();
  private ArrayList type         = new ArrayList();
  private ArrayList spellAbility = new ArrayList();

  private boolean tapped;
  private boolean sickness = true;//summoning sickness
  private boolean token = false;

  private int attack;
  private int defense;
  private int damage;
  private int assignedDamage;
  private int nShield;
  private int turnInZone;
  private int loyaltyCounter;

  private String owner      = "";
  private String controller = "";
  private String name       = "";
  private String rarity       = "";
  private String text         = "";
  private String manaCost = "";

  private Command comesIntoPlayCommand = Command.Blank;
  private Command destroyCommand           = Command.Blank;

  public void addCounter(int n) {loyaltyCounter += n;}
  public void subtractCounter(int n) {loyaltyCounter -= n;}
  public int getCounters() {return loyaltyCounter;}

  //the amount of damage needed to kill the creature
  public int getKillDamage() {return getDefense() - getDamage();}

  public int getTurnInZone()          {return turnInZone;}
  public void setTurnInZone(int turn) {turnInZone = turn;}

  public void setManaCost(String s) {manaCost = s;}
  public String getManaCost()          {return manaCost;}

  public String getSpellText() {return text;}

  public void setText(String t) {text = t;}
  public String getText()
  {
    if(isInstant() || isSorcery())
    {
      String s = getSpellText();
      SpellAbility[] sa = getSpellAbility();
      for(int i = 0; i < sa.length; i++)
        s += sa[i].toString() +"\r\n";

      return s;
    }

    String s = "";
    ArrayList keyword = getKeyword();
    for(int i = 0; i < keyword.size(); i++)
    {
      if(i != 0)
        s += ", ";
      s += keyword.get(i).toString();
    }

    s += "\r\n" +text +"\r\n";

    SpellAbility[] sa = getSpellAbility();
    for(int i = 0; i < sa.length; i++)
    {
      //presumes the first SpellAbility added to this card, is the "main" spell
      //skip the first SpellAbility for creatures, since it says "Summon this creature"
      //looks bad on the Gui card detail
      if(isPermanent() && i != 0)
        s += sa[i].toString() +"\r\n";
    }

    return s.trim();
  }//getText()

  public void clearSpellAbility()               {spellAbility.clear();}
  public void addSpellAbility(SpellAbility a) {spellAbility.add(a);}
  public SpellAbility[] getSpellAbility()
  {
    SpellAbility[] s = new SpellAbility[spellAbility.size()];
    spellAbility.toArray(s);
    return s;
  }


  //shield = regeneration
  public void setShield(int n) {nShield = n;}
  public int  getShield()          {return nShield;}
  public void addShield()         {nShield++;}
  public void subtractShield() {nShield--;}
  public void resetShield()      {nShield = 0;}

  //is this "Card" supposed to be a token?
  public void setToken(boolean b) {token = b;}
  public boolean isToken()            {return token;}

  public void setComesIntoPlay(Command c) {comesIntoPlayCommand = c;}
  public void comesIntoPlay()			   {comesIntoPlayCommand.execute();}

  public void setDestroy(Command c)    {destroyCommand = c;}
  public void destroy()				{destroyCommand.execute(); }

  public void setSickness(boolean b) {sickness = b;}
  public boolean hasSickness()
  {
    if(getKeyword().contains("Haste"))
      return false;

    return sickness;
  }

  public void setRarity(String s) {rarity = s;}
  public String getRarity()          {return rarity;}

  public void addDamage(int n) {setDamage(getDamage() + n);}
  public void setDamage(int n) {damage = n;}
  public int getDamage()          {return damage;}

  public void setAssignedDamage(int n) {assignedDamage = n;}
  public int  getAssignedDamage()         {return assignedDamage;}

  public String getName()          {return name;}
  public String getOwner()        {return owner;}
  public String getController(){return controller;}

  public void setName(String s)                {name = s; this.updateObservers();}
  public void setOwner(String player)        {owner = player;}
  public void setController(String player){controller = player; this.updateObservers();}

  //array size might equal 0, will NEVER be null
  public Card[] getAttachedCards()
  {
    Card c[] = new Card[attached.size()];
    attached.toArray(c);
    return c;
  }
  public boolean hasAttachedCards() {return getAttachedCards().length != 0;}
  public void attachCard(Card c)       {attached.add(c);      this.updateObservers();}
  public void unattachCard(Card c)    {attached.remove(c); this.updateObservers();}

  public void setType(ArrayList a)    {type = new ArrayList(a);}
  public void addType(String a)         {type.add(a);        this.updateObservers();}
  public void removeType(String a)    {type.remove(a); this.updateObservers();}
  public ArrayList getType()              {return new ArrayList(type);}

  public int getAttack() {return attack;}
  public int getDefense(){return defense;}

  public void setAttack(int n)    {attack  = n; this.updateObservers();}
  public void setDefense(int n)  {defense = n; this.updateObservers();}

  public boolean isUntapped()         {return ! tapped;}
  public boolean isTapped()            {return tapped;}
  public void setTapped(boolean b) {tapped = b;  updateObservers();}
  public void tap()                        {setTapped(true);}
  public void untap()                     {setTapped(false);}

  //keywords are like flying, fear, first strike, etc...
  public ArrayList getKeyword()          {return new ArrayList(keyword);}
  public void setKeyword(ArrayList a) {keyword = new ArrayList(a); this.updateObservers();}
  public void addKeyword(String s)     {keyword.add(s);                    this.updateObservers();}
  public void removeKeyword(String s) {keyword.remove(s);              this.updateObservers();}

  public boolean isPermanent()  {return !(isInstant() || isSorcery());}

  public boolean isCreature()   {return type.contains("Creature");}
  public boolean isBasicLand() {return type.contains("Basic");}
  public boolean isLand()         {return type.contains("Land");}
  public boolean isSorcery()    {return type.contains("Sorcery");}
  public boolean isInstant()    {return type.contains("Instant");}
  public boolean isArtifact()  {return type.contains("Artifact");}

  public boolean isPlaneswalker()  {return type.contains("Planeswalker");}


  //global and local enchantments
  public boolean isEnchantment()          {return typeContains("Enchantment"); }
  public boolean isLocalEnchantment()  {return typeContains("Aura");   }
  public boolean isGlobalEnchantment() {return typeContains("Enchantment") && (! isLocalEnchantment());}

  private boolean typeContains(String s)
  {
    Iterator it = this.getType().iterator();
    while(it.hasNext())
      if(it.next().toString().startsWith(s))
        return true;

    return false;
  }

  public void setUniqueNumber(int n) {uniqueNumber = n; this.updateObservers();}
  public int  getUniqueNumber()         {return uniqueNumber;}

  public boolean equals(Object o)
  {
    if(o instanceof Card)
    {
      Card c = (Card)o;
      int a = getUniqueNumber();
      int b = c.getUniqueNumber();
      return (a == b);
    }
    return false;
  }
  public int hashCode()
  {
    return getUniqueNumber();
  }
  public String toString()
  {
    return this.getName() +" (" +this.getUniqueNumber() +")";
  }
}