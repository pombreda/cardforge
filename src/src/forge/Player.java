
package forge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public abstract class Player extends MyObservable{
	protected String name;
	protected int poisonCounters;
	protected int life;
	protected int assignedDamage;
	protected int numPowerSurgeLands;
	
	protected boolean bFirstTurn;
	
	protected Card lastDrawnCard;
	
	public Player(String myName) {
		this(myName, 20, 0);
	}
	
	public Player(String myName, int myLife, int myPoisonCounters) {
		name = myName;
		life = myLife;
		poisonCounters = myPoisonCounters;
		assignedDamage = 0;
		lastDrawnCard = null;
		bFirstTurn = true;
	}
	
	public void reset(){
		life = 20;
		poisonCounters = 0;
		assignedDamage = 0;
		lastDrawnCard = null;
		bFirstTurn = true;
		this.updateObservers();
	}
	
	public String getName() {
		return name;
	}
	
	public abstract boolean isHuman();
	public abstract boolean isComputer();
	public abstract boolean isPlayer(Player p1);
	
	public abstract Player getOpponent();
	
	//////////////////////////
	//
	// methods for manipulating life
	//
	//////////////////////////
	
	public boolean setLife(final int newLife, final Card source) {
		boolean change = false;
		//rule 118.5
		if(life > newLife) {
			change = loseLife(life - newLife, source);
		}
		else if(newLife > life) {
			change = gainLife(newLife - life, source);
		}
		else {
			//life == newLife
			change = false;
		}
		this.updateObservers();
		return change;
	}
	
	public int getLife() {
		return life;
	}
	
	private void addLife(final int toAdd) {
		life += toAdd;
		this.updateObservers();
	}
	
	public boolean gainLife(final int toGain, final Card source) {
		boolean newLifeSet = false;
		if(!canGainLife()) return false;
		if(toGain > 0) {
			addLife(toGain);
			newLifeSet = true;
			this.updateObservers();
		}
		else System.out.println("Player - trying to gain negative or 0 life");
		
		Object[] Life_Whenever_Parameters = new Object[1];
    	Life_Whenever_Parameters[0] = toGain;
    	AllZone.GameAction.CheckWheneverKeyword(getPlayerCard(), "GainLife", Life_Whenever_Parameters);

		return newLifeSet;
	}
	
	protected abstract Card getPlayerCard();
	
	public boolean canGainLife() {
		if(AllZoneUtil.isCardInPlay("Sulfuric Vortex")) return false;
		return true;
	}
	
	public boolean loseLife(final int toLose, final Card c) {
		boolean newLifeSet = false;
		if(!canLoseLife()) return false;
		if(toLose > 0) {
			life -= toLose;
			newLifeSet = true;
			this.updateObservers();
		}
		else System.out.println("Player - trying to lose positive or 0 life");
		return newLifeSet;
	}
	
	public boolean canLoseLife() {
		return true;
	}
	
	private void subtractLife(final int toSub) {
		life -= toSub;
		this.updateObservers();
	}
	
	public void payLife(final int cost) {
		life -= cost;
		this.updateObservers();
	}
	
	public boolean payLife(int lifePayment, Card source) {
    	
    	if (lifePayment <= life){
    		subtractLife(lifePayment);
    		return true;
    	}
    	return false;
	}
	
	//////////////////////////
	//
	// methods for handling damage
	//
	//////////////////////////
	
	public void addDamage(final int damage, final Card source) {
		int damageToDo = damage;
		
		if( reducePlayerDamageToZero(source, false) )
        	damageToDo = 0;
		
		if( source.getKeyword().contains("Infect") ) {
        	addPoisonCounters(damageToDo);
        }
        else {
        	if(PlayerUtil.worshipFlag(this) && life <= damageToDo) {
        		damageToDo = Math.min(damageToDo, life - 1);
        	}
        	//rule 118.2. Damage dealt to a player normally causes that player to lose that much life.
        	loseLife(damageToDo, source);
        }
        	
        if(source.getKeyword().contains("Lifelink")) GameActionUtil.executeLifeLinkEffects(source, damageToDo);
        
        CardList cl = CardFactoryUtil.getAurasEnchanting(source, "Guilty Conscience");
        for(Card c:cl) {
            GameActionUtil.executeGuiltyConscienceEffects(source, c, damageToDo);
        }
        
        GameActionUtil.executePlayerDamageEffects(this, source, damageToDo, false);
	}
	
	private boolean reducePlayerDamageToZero(final Card source, final boolean isCombat) {
		boolean reduce = false;
    	if(isCombat) {
    		//for future use
    	}
    	reduce = reduce || source.getKeyword().contains("Prevent all damage that would be dealt to and dealt by CARDNAME.");
		reduce = reduce || source.getKeyword().contains("Prevent all damage that would be dealt by CARDNAME.");
		
		//Spirit of Resistance
		if(AllZoneUtil.isCardInPlay("Spirit of Resistance", this)) {
			if( AllZoneUtil.getPlayerColorInPlay(this, Constant.Color.Black).size() > 0
					&& AllZoneUtil.getPlayerColorInPlay(this, Constant.Color.Blue).size() > 0
					&& AllZoneUtil.getPlayerColorInPlay(this, Constant.Color.Green).size() > 0
					&& AllZoneUtil.getPlayerColorInPlay(this, Constant.Color.Red).size() > 0
					&& AllZoneUtil.getPlayerColorInPlay(this, Constant.Color.White).size() > 0) {
				reduce = true;
			}
		}
		return reduce;
	}
	
	public void setAssignedDamage(int n)   		{	assignedDamage = n; }
    public int  getAssignedDamage()        		{	return assignedDamage; }
    
    public void addCombatDamage(final int damage, final Card source) {
    	int damageToDo = damage;
    	if (source.getKeyword().contains("Prevent all combat damage that would be dealt to and dealt by CARDNAME.")
    			|| source.getKeyword().contains("Prevent all combat damage that would be dealt by CARDNAME."))
        	damageToDo = 0;
        addDamage(damageToDo, source);
    	
    	//GameActionUtil.executePlayerDamageEffects(player, source, damage, true);
    	GameActionUtil.executePlayerCombatDamageEffects(source);
    	CombatUtil.executeCombatDamageEffects(source);
    }
	
	//////////////////////////
	//
	// methods for handling Poison counters
	//
	//////////////////////////
	
	public void addPoisonCounters(int num) {
		poisonCounters += num;
		this.updateObservers();
	}
	
	public void setPoisonCounters(int num) {
		poisonCounters = num;
		this.updateObservers();
	}
	
	public int getPoisonCounters() {
		return poisonCounters;
	}
	
	public void subtractPoisonCounters(int num) {
		poisonCounters -= num;
		this.updateObservers();
	}
	
	public boolean hasShroud() {
		return false;
	}
	
	
	public boolean canPlaySpells() {
		return true;
	}
	
	public boolean canPlayAbilities() {
		return true;
	}
	
	public CardList getCards(PlayerZone zone) {
		//TODO
		return new CardList();
	}
	

	////////////////////////////////
	///
	/// replaces AllZone.GameAction.draw* methods
	///
	////////////////////////////////
	
	public abstract void mayDrawCard();
	
	public abstract void mayDrawCards(int numCards);
	
	public void drawCard() {
		drawCards(1);
	}
	
	public void drawCards() {
		drawCards(1);
	}
	
	public abstract boolean dredge();
	
	public void drawCards(int n) {
		PlayerZone library = AllZone.getZone(Constant.Zone.Library, this);
		PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, this);
		for(int i = 0; i < n; i++) {
			if(getDredge().size() == 0 || !dredge()) {
				doDraw(library, hand);
			}
		}
	}
	
	private void doDraw(PlayerZone library, PlayerZone hand) {
		if(library.size() != 0) {
			Card c = library.get(0);
			library.remove(0);
			hand.add(c);
			setLastDrawnCard(c);

			GameActionUtil.executeDrawCardTriggeredEffects(this);
		}
		//lose:
		else if(Constant.Runtime.Mill[0]) {
			if(!AllZoneUtil.isCardInPlay("Platinum Angel", this) && !AllZoneUtil.isCardInPlay("Abyssal Persecutor", this.getOpponent())) {
				setLife(0, null);
				AllZone.GameAction.checkStateEffects();
			}
		}
	}
	
	protected CardList getDredge() {
        CardList dredge = new CardList();
        CardList cl = AllZoneUtil.getPlayerGraveyard(this);
        
        for(Card c:cl) {
            ArrayList<String> kw = c.getKeyword();
            for(int i = 0; i < kw.size(); i++) {
                if(kw.get(i).toString().startsWith("Dredge")) {
                    if(AllZoneUtil.getPlayerCardsInLibrary(this).size() >= getDredgeNumber(c)) dredge.add(c);
                }
            }
        }
        return dredge;
    }//hasDredge()
    
    protected int getDredgeNumber(Card c) {
        ArrayList<String> a = c.getKeyword();
        for(int i = 0; i < a.size(); i++)
            if(a.get(i).toString().startsWith("Dredge")) {
                String s = a.get(i).toString();
                return Integer.parseInt("" + s.charAt(s.length() - 1));
            }
        
        throw new RuntimeException("Input_Draw : getDredgeNumber() card doesn't have dredge - " + c.getName());
    }//getDredgeNumber()
    
    ////////////////////////////////
	///
	/// replaces AllZone.GameAction.discard* methods
	///
	////////////////////////////////
    
    public abstract CardList discard(final int num, final SpellAbility sa);
    
    public CardList discard(final SpellAbility sa) {
    	return discard(1, sa);
    }
    
    public void discard(Card c, SpellAbility sa) {
    	doDiscard(c, sa);
    }
    
    public void doDiscard(Card c, SpellAbility sa) {
    	if (sa!= null){
    		;
    	}
    	
    	AllZone.GameAction.CheckWheneverKeyword(c,"DiscardsCard",null);
    	
        AllZone.GameAction.discard_nath(c);
        AllZone.GameAction.discard_megrim(c);
        
        // necro disrupts madness
        if(AllZoneUtil.getPlayerCardsInPlay(c.getOwner(), "Necropotence").size() > 0) {	
        	AllZone.GameAction.exile(c);
        	return;
        }
        
        AllZone.GameAction.discard_madness(c);
        
        if((c.getKeyword().contains("If a spell or ability an opponent controls causes you to discard CARDNAME, put it onto the battlefield instead of putting it into your graveyard.")
        		|| c.getKeyword().contains("If a spell or ability an opponent controls causes you to discard CARDNAME, put it onto the battlefield with two +1/+1 counters on it instead of putting it into your graveyard."))	
        		&& !c.getController().equals(sa.getSourceCard().getController())) {
        	AllZone.GameAction.discard_PutIntoPlayInstead(c);
        }
        else if (c.getKeyword().contains("If a spell or ability an opponent controls causes you to discard CARDNAME, return it to your hand.")) {
        	;
        }
        else {
        	AllZone.GameAction.moveToGraveyard(c);
        }
    }//end doDiscard
    
    public void discardHand(SpellAbility sa) {
        //PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, player);
        CardList list = AllZoneUtil.getPlayerHand(this);
        AllZone.GameAction.discardRandom(this, list.size(), sa);
    }
    
    public void discardRandom(SpellAbility sa) {
        discardRandom(1, sa);
    }
    
    public void discardRandom(final int num, final SpellAbility sa) {
    	for(int i = 0; i < num; i++) {
    		Card[] c = AllZone.getZone(Constant.Zone.Hand, this).getCards();
    		if(c.length != 0) doDiscard(CardUtil.getRandom(c), sa);
    	}
    }
    
    public abstract void handToLibrary(final int numToLibrary, String libPos);
    
    ////////////////////////////////
    public void shuffle() {
        PlayerZone library = AllZone.getZone(Constant.Zone.Library, this);
        Card c[] = library.getCards();
        
        if(c.length <= 1) return;
        
        ArrayList<Object> list = new ArrayList<Object>(Arrays.asList(c));
        //overdone but wanted to make sure it was really random
        Random random = new Random();
        Collections.shuffle(list, random);
        Collections.shuffle(list, random);
        Collections.shuffle(list, random);
        Collections.shuffle(list, random);
        Collections.shuffle(list, random);
        Collections.shuffle(list, random);
        
        Object o;
        for(int i = 0; i < list.size(); i++) {
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
    ////////////////////////////////
    
    ////////////////////////////////
    protected abstract void doScry(CardList topN, int N);
    
    public void scry(int numScry) {
        CardList topN = new CardList();
        PlayerZone library = AllZone.getZone(Constant.Zone.Library, this);
        numScry = Math.min(numScry, library.size());
        for(int i = 0; i < numScry; i++) {
            topN.add(library.get(0));
            library.remove(0);
        }
        doScry(topN, topN.size());
    }
    ///////////////////////////////
    
    ///////////////////////////////
    ////
    ////	properties about the player and his/her cards/game status
    ////
    ///////////////////////////////
    public boolean hasPlaneswalker() {
        return null != getPlaneswalker();
    }
    
    public Card getPlaneswalker() {
    	CardList c = AllZoneUtil.getPlayerTypeInPlay(this, "Planeswalker");
    	if(null != c && c.size() > 0) return c.get(0);
    	else return null;
    }
    
    public int getNumPowerSurgeLands() {
    	return numPowerSurgeLands;
    }
    
    public int setNumPowerSurgeLands(int n) {
    	numPowerSurgeLands = n;
    	return numPowerSurgeLands;
    }
    
    public Card getLastDrawnCard() {
    	return lastDrawnCard;
    }
    
    public Card setLastDrawnCard(Card c) {
    	lastDrawnCard = c;
    	return lastDrawnCard;
    }
    
    public Card resetLastDrawnCard() {
    	Card old = lastDrawnCard;
    	lastDrawnCard = null;
    	return old;
    }
	
    public boolean isFirstTurn() { return bFirstTurn; }
    public void setFirstTurn(boolean b) { bFirstTurn = b; }
    
	////////////////////////////////
	//
	// generic Object overrides
	//
	/////////////////////////////////
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Player){
			Player p1 = (Player)o;
			return p1.getName().equals(name);
		} else return false;
	}
	
	@Override
	public String toString() {
		return name;
	}
}