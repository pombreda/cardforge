package forge;

import java.util.HashMap;
import java.util.Observer;
import java.util.Stack;

import com.esotericsoftware.minlog.Log;

import forge.ai.minimax.FGameState;
import forge.ai.minimax.Unstatic;
import forge.card.spellability.SpellAbility;
import forge.card.spellability.Spell_Permanent;

public class Phase extends MyObservable implements java.io.Serializable
{

	private static final long serialVersionUID = 5207222278370963197L;

	private int phaseIndex;
	private int turn;

    private Stack<Player> extraTurns = new Stack<Player>();
    
	private int extraCombats;
	
	private int nCombatsThisTurn;
	
    private Player playerTurn = AllZone.getHumanPlayer();
    public boolean isPlayerTurn(Player player) {
        return playerTurn.isPlayer(player);
    }
    
    public void setPlayerTurn(Player s) {
    	playerTurn = s;
    }
    
    public Player getPlayerTurn() {
    	return playerTurn;
    }
    
    // priority player
    
    private Player pPlayerPriority = AllZone.getHumanPlayer();
    public Player getPriorityPlayer() {
    	return pPlayerPriority;
    }
    
    public void setPriorityPlayer(Player p) {
    	pPlayerPriority = p;
    }
    
    private Player pFirstPriority = AllZone.getHumanPlayer();
    public Player getFirstPriority() {
    	return pFirstPriority;
    }
    
    public void setFirstPriority(Player p) {
    	pFirstPriority = p;
    }
    
    public void setPriority(Player p) {
        if(AllZone.getStack() != null)
            AllZone.getStack().chooseOrderOfSimultaneousStackEntryAll();

    	pFirstPriority = p;
    	pPlayerPriority = p;
    }
    
    public void resetPriority() {
    	setPriority(playerTurn);
    }
    
	private boolean bPhaseEffects = true;
    public boolean doPhaseEffects() {
    	return bPhaseEffects;
    }
    
    public void setPhaseEffects(boolean b) {
    	bPhaseEffects = b;
    } 
    
    private boolean bSkipPhase = true;
    public boolean doSkipPhase() {
    	return bSkipPhase;
    }
    
    public void setSkipPhase(boolean b) {
    	bSkipPhase = b;
    } 
    
    private boolean bCombat = false;
    public boolean inCombat() {
    	return bCombat;
    }
    
    public void setCombat(boolean b) {
    	bCombat = b;
    } 
    
    private boolean bRepeat = false;
    public void repeatPhase() {
    	bRepeat = true;
    } 
    
	private String phaseOrder[] = {
			Constant.Phase.Untap,
			Constant.Phase.Upkeep,
			Constant.Phase.Draw, 
			Constant.Phase.Main1,
			Constant.Phase.Combat_Begin,
			Constant.Phase.Combat_Declare_Attackers,
			Constant.Phase.Combat_Declare_Attackers_InstantAbility,
			Constant.Phase.Combat_Declare_Blockers,
			Constant.Phase.Combat_Declare_Blockers_InstantAbility,
			Constant.Phase.Combat_FirstStrikeDamage,
			Constant.Phase.Combat_Damage,
			Constant.Phase.Combat_End,
			Constant.Phase.Main2,
			Constant.Phase.End_Of_Turn,
			Constant.Phase.Cleanup
	};
    
	public Phase() {
		reset();
	}
	
    public void reset() {
        turn = 1;
        playerTurn = AllZone.getHumanPlayer();
        resetPriority();
        bPhaseEffects = true;
        needToNextPhase = false;
        setGameBegins(0);
        phaseIndex = 0;
        extraTurns.clear();
        nCombatsThisTurn = 0;
        extraCombats = 0;
        bCombat = false;
        bRepeat = false;
        this.updateObservers();
    }
    
    public void turnReset(){
    	setStormCount(0);
        setPlayerSpellCount(0);
        setPlayerCreatureSpellCount(0);
        setPlayerInstantSpellCount(0);
        setComputerSpellCount(0);
        setComputerCreatureSpellCount(0);
        setComputerInstantSpellCount(0);
        playerTurn.setNumLandsPlayed(0);
    }

	public void handleBeginPhase(){
		AllZone.getPhase().setPhaseEffects(false);
		// Handle effects that happen at the beginning of phases
        final String phase = AllZone.getPhase().getPhase();
        final Player turn = AllZone.getPhase().getPlayerTurn();
        AllZone.getPhase().setSkipPhase(true);
        AllZone.getGameAction().checkStateEffects();

        if(phase.equals(Constant.Phase.Untap)) {
            PhaseUtil.handleUntap();
	    }
	    else if(phase.equals(Constant.Phase.Upkeep)){
	    	PhaseUtil.handleUpkeep();
	    }
	    
	    else if(phase.equals(Constant.Phase.Draw)){
	    	PhaseUtil.handleDraw();
	    }
        
	    else if(phase.equals(Constant.Phase.Combat_Begin)){
	    	PhaseUtil.verifyCombat();
	    }
        
	    else if (phase.equals(Constant.Phase.Combat_Declare_Attackers_InstantAbility)){
            if(inCombat()) {
            	PhaseUtil.handleDeclareAttackers();
            }
            else
            	AllZone.getPhase().setNeedToNextPhase(true);
	    }
        
        // we can skip AfterBlockers and AfterAttackers if necessary
	    else if(phase.equals(Constant.Phase.Combat_Declare_Blockers)){
            if(inCombat()) {
	            PhaseUtil.verifyCombat();
            }
            else
            	AllZone.getPhase().setNeedToNextPhase(true);
        }
        
	    else if (phase.equals(Constant.Phase.Combat_Declare_Blockers_InstantAbility)){
	    	// After declare blockers are finished being declared mark them blocked and trigger blocking things
            if(!inCombat()) 
            	AllZone.getPhase().setNeedToNextPhase(true);
            else{
            	PhaseUtil.handleDeclareBlockers();
            }
	    }
        
	    else if (phase.equals(Constant.Phase.Combat_FirstStrikeDamage)){
	    	if(!inCombat())
	    		AllZone.getPhase().setNeedToNextPhase(true);
	    	else{
	    		AllZone.getCombat().verifyCreaturesInPlay();

				// no first strikers, skip this step
				if (!AllZone.getCombat().setAssignedFirstStrikeDamage())		
					AllZone.getPhase().setNeedToNextPhase(true);
				
				else{
			    	if (!AllZone.getGameInfo().isPreventCombatDamageThisTurn())
			    		 Combat.dealAssignedDamage();
			        
			        AllZone.getGameAction().checkStateEffects();
			        CombatUtil.showCombat();
				}
	    	}
	    }
	    	
	    else if (phase.equals(Constant.Phase.Combat_Damage)){
	    	if(!inCombat())
	    		AllZone.getPhase().setNeedToNextPhase(true);
	    	else{
	    		AllZone.getCombat().verifyCreaturesInPlay();
	    		
		        AllZone.getCombat().setAssignedDamage();
	            
	    		if (!AllZone.getGameInfo().isPreventCombatDamageThisTurn())
	    			Combat.dealAssignedDamage();
	    			
	    		AllZone.getGameAction().checkStateEffects();
		        CombatUtil.showCombat();
	    	}
	    }
        
	    else if (phase.equals(Constant.Phase.Combat_End))
        {
	    	// End Combat always happens
			AllZone.getEndOfCombat().executeUntil();
			AllZone.getEndOfCombat().executeAt();
        }

	    else if(phase.equals(Constant.Phase.End_Of_Turn)) {
	    	AllZone.getEndOfTurn().executeAt();
        }
        
	    else if(phase.equals(Constant.Phase.Cleanup)){
	    	AllZone.getPhase().getPlayerTurn().setAssignedDamage(0);
	    	
	    	//reset dealt damage to vars
	    	Player opp = AllZone.getPhase().getPlayerTurn().getOpponent();
			CardList oppList = AllZoneUtil.getCreaturesInPlay(opp);
			for(int i = 0; i < oppList.size(); i++) {
				Card c = oppList.get(i);
				c.setDealtDmgToHumanThisTurn(false);
				c.setDealtDmgToComputerThisTurn(false);
			}
	    	
	    	//Reset Damage received map
	    	CardList list = AllZoneUtil.getCardsInPlay();
			for(Card c:list) {
				c.resetPreventNextDamage();
				c.resetReceivedDamageFromThisTurn();
				c.resetDealtDamageToThisTurn();
			}
			AllZone.getHumanPlayer().resetPreventNextDamage();
			AllZone.getComputerPlayer().resetPreventNextDamage();

	    	AllZone.getEndOfTurn().executeUntil();
	    	CardList cHand = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());
	    	CardList hHand = AllZoneUtil.getPlayerHand(AllZone.getHumanPlayer());
	    	for(Card c:cHand) c.setDrawnThisTurn(false);
	    	for(Card c:hHand) c.setDrawnThisTurn(false);
	    	AllZone.getHumanPlayer().resetNumDrawnThisTurn();
	    	AllZone.getComputerPlayer().resetNumDrawnThisTurn();
	    }
        
        if (!AllZone.getPhase().isNeedToNextPhase()){
            // Run triggers if phase isn't being skipped
            HashMap<String,Object> runParams = new HashMap<String,Object>();
    		runParams.put("Phase", phase);
    		runParams.put("Player", turn);
    		AllZone.getTriggerHandler().runTrigger("Phase", runParams);
            
        }

        //This line fixes Combat Damage triggers not going off when they should
        AllZone.getStack().unfreezeStack();

        if(!phase.equals(Constant.Phase.Untap)) //Nobody recieves priority during untap
		    resetPriority();
	}
	
    public void nextPhase() {
        //experimental, add executeCardStateEffects() here:
        for(String effect:AllZone.getStaticEffects().getStateBasedMap().keySet()) {
            Command com = GameActionUtil.commands.get(effect);
            com.execute();
        }
        
        GameActionUtil.executeCardStateEffects();
        
        needToNextPhase = false;

        // If the Stack isn't empty why is nextPhase being called?
        if(AllZone.getStack().size() != 0) {
        	Log.debug("Phase.nextPhase() is called, but Stack isn't empty.");
            return;
        }
        this.bPhaseEffects = true;
		if(!AllZoneUtil.isCardInPlay("Upwelling")) {
			AllZone.getManaPool().clearPool();
			AllZone.getComputerManaPool().clearPool();
		}
        
        if (getPhase().equals(Constant.Phase.Combat_Declare_Attackers)) {
        	AllZone.getStack().unfreezeStack();
        	nCombatsThisTurn++;
        } 
        else if (getPhase().equals(Constant.Phase.Untap)) {
        	nCombatsThisTurn = 0;
        }
        
        if (getPhase().equals(Constant.Phase.Combat_End)) {
            AllZone.getCombat().reset();
            
            Display display = AllZone.getDisplay();
            if (display != null) {
            	display.showCombat("");
            }
			
            AllZone.getDisplay().showCombat("");
            resetAttackedThisCombat(getPlayerTurn());
            this.bCombat = false;
        }
        
        if (phaseOrder[phaseIndex].equals(Constant.Phase.Cleanup))
        	if (!bRepeat)
        		AllZone.getPhase().setPlayerTurn(handleNextTurn());
        
        if (is(Constant.Phase.Combat_Declare_Blockers)){        	
         	AllZone.getStack().unfreezeStack();
        }
        
        if (is(Constant.Phase.Combat_End) && extraCombats > 0){
        	// TODO: ExtraCombat needs to be changed for other spell/abilities that give extra combat
        	// can do it like ExtraTurn stack ExtraPhases

        	Player player = getPlayerTurn();
        	Player opp = player.getOpponent();

        	bCombat = true;
        	extraCombats--;
        	AllZone.getCombat().reset();
        	AllZone.getCombat().setAttackingPlayer(player);
        	AllZone.getCombat().setDefendingPlayer(opp);
        	phaseIndex = findIndex(Constant.Phase.Combat_Declare_Attackers);
        }  
        else {
        	if (!bRepeat){	// for when Cleanup needs to repeat itself
	            phaseIndex++;
	            phaseIndex %= phaseOrder.length;
        	}
        	else
        		bRepeat = false;
        }
        
        // **** Anything BELOW Here is actually in the next phase. Maybe move this to handleBeginPhase
        if(getPhase().equals(Constant.Phase.Untap)){
            turn++;
        }

        // When consecutively skipping phases (like in combat) this section pushes through that block
        this.updateObservers();
        if(AllZone.getPhase() != null && AllZone.getPhase().isNeedToNextPhase()) {
                AllZone.getPhase().setNeedToNextPhase(false);
                AllZone.getPhase().nextPhase();
        }
    }
    
    private Player handleNextTurn() {
    	Player nextTurn = extraTurns.isEmpty() ?  getPlayerTurn().getOpponent() : extraTurns.pop();

        AllZone.resetZoneMoveTracking();
    	
    	return skipTurnTimeVault(nextTurn);
	}

	private Player skipTurnTimeVault(Player turn) {
        //time vault:
		CardList vaults = AllZoneUtil.getPlayerCardsInPlay(turn, "Time Vault");
		vaults = vaults.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.isTapped();
            }
        });
		
		if (vaults.size() > 0){
            final Card crd = vaults.get(0);

            if(turn.isHuman()) {
            	if(GameActionUtil.showYesNoDialog(crd, "Untap " + crd + "?")) {
            		crd.untap();
            		turn = extraTurns.isEmpty() ?  turn.getOpponent() : extraTurns.pop();
            	}
            }
            else{
            	// TODO: Should AI skip his turn for time vault?
            }
		}
    	return turn;
	}

	public synchronized boolean is(String phase, Player player) {
        return getPhase().equals(phase) && getPlayerTurn().isPlayer(player);
    }
	
	public synchronized boolean is(String phase) {
        return (getPhase().equals(phase));
    }
	
	public boolean isAfter(String phase) {
		return phaseIndex > findIndex(phase);
	}
	
	public boolean isBefore(String phase) {
		return phaseIndex < findIndex(phase);
	}
    
    private int findIndex(String phase) {
        for(int i = 0; i < phaseOrder.length; i++) {
            if(phase.equals(phaseOrder[i])) 
            	return i;
        }
        throw new RuntimeException("Phase : findIndex() invalid argument, phase = " + phase);
    }
    
    public String getPhase() {
    	return phaseOrder[phaseIndex];
    }
    
    public int getTurn() {
        return turn;
    }

    public Player getNextTurn(){
    	if (extraTurns.isEmpty())
    		return getPlayerTurn().getOpponent();
    	
    	return extraTurns.peek();
    }
    
    public boolean isNextTurn(Player pl){
    	Player next = getNextTurn();
    	return (pl.equals(next));
    }
    
    public void addExtraTurn(Player player) {
    	// use a stack to handle extra turns, make sure the bottom of the stack restores original turn order
    	if (extraTurns.isEmpty())
    		extraTurns.push(getPlayerTurn().getOpponent());
    	
    	extraTurns.push(player);
    }
    
    public void skipTurn(Player player) {
    	// skipping turn without having extras is equivalent to giving your opponent an extra turn
    	if (extraTurns.isEmpty())
    		addExtraTurn(player.getOpponent());
    	else{
    		int pos = extraTurns.lastIndexOf(player);
    		if (pos == -1)
    			addExtraTurn(player.getOpponent());
    		else
    			extraTurns.remove(pos);
    	}
    }
    
    public void addExtraCombat() {
    	// Extra combats can only happen 
    	extraCombats++;
    }

    public boolean isFirstCombat() {
    	return (nCombatsThisTurn == 1);
    }
    
    public void resetAttackedThisCombat(Player player) {
        // resets the status of attacked/blocked this phase
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);

        list = list.getType("Creature");
        
        for(int i = 0; i < list.size(); i++) {
            Card c = list.get(i);
            if(c.getCreatureAttackedThisCombat()) c.setCreatureAttackedThisCombat(false);
            if(c.getCreatureBlockedThisCombat()) c.setCreatureBlockedThisCombat(false);
            
            if(c.getCreatureGotBlockedThisCombat()) c.setCreatureGotBlockedThisCombat(false);
            
            AllZone.getGameInfo().setAssignedFirstStrikeDamageThisCombat(false);
            AllZone.getGameInfo().setResolvedFirstStrikeDamageThisCombat(false);
        }
    }

    public void passPriority(){
    	Player actingPlayer = getPriorityPlayer();
    	Player lastToAct = getFirstPriority();
    	
    	// actingPlayer is the player who may act
    	// the lastToAct is the player who gained Priority First in this segment of Priority
    	
    	if (lastToAct.equals(actingPlayer)){
    		// pass the priority to other player
    		setPriorityPlayer(actingPlayer.getOpponent());
    		AllZone.getInputControl().resetInput();
            AllZone.getStack().chooseOrderOfSimultaneousStackEntryAll();
    	}
    	else{
    		if (AllZone.getStack().size() == 0){
    			// end phase
    			needToNextPhase = true;
    			pPlayerPriority = getPlayerTurn();	// this needs to be set early as we exit the phase
    		}
    		else{
                if(!AllZone.getStack().hasSimultaneousStackEntries())
    			    AllZone.getStack().resolveStack();
    		}
            AllZone.getStack().chooseOrderOfSimultaneousStackEntryAll();
    	}
    }
    
    @Override
    public void addObserver(Observer o) {
        super.deleteObservers();
        super.addObserver(o);
    }
    
    boolean needToNextPhase = false;
    
    public void setNeedToNextPhase(boolean needToNextPhase) {
        this.needToNextPhase = needToNextPhase;
    }
    
    public boolean isNeedToNextPhase() {
        return this.needToNextPhase;
    }
    
    //This should only be true four times! that is for the initial nextPhases in MyObservable
    int needToNextPhaseInit = 0;
    
    public boolean isNeedToNextPhaseInit() {
        needToNextPhaseInit++;
        if(needToNextPhaseInit <= 4) {
            return true;
        }
        return false;
    }

	public static boolean canCastSorcery(Player player)
	{
		return AllZone.getPhase().isPlayerTurn(player) && (AllZone.getPhase().getPhase().equals(Constant.Phase.Main2) || 
			AllZone.getPhase().getPhase().equals(Constant.Phase.Main1)) && AllZone.getStack().size() == 0;
	}
	
	public String buildActivateString(String startPhase, String endPhase){
		StringBuilder sb = new StringBuilder();
		
		boolean add = false;
		for(int i = 0; i < phaseOrder.length; i++){
			if (phaseOrder[i].equals(startPhase))
				add = true;
			
			if (add){
				if (sb.length() != 0)
					sb.append(",");
				sb.append(phaseOrder[i]);
			}
			
			if (phaseOrder[i].equals(endPhase))
				add = false;
		}
		
		return sb.toString();
	}

    public static void main(String args[]) {
        Phase phase = new Phase();
        for(int i = 0; i < phase.phaseOrder.length; i++) {
            System.out.println(phase.getPlayerTurn() + " " + phase.getPhase());
            phase.nextPhase();
        }
    }

	public static void increaseSpellCount(SpellAbility sp){
		incrementStormCount();

		if (sp.getActivatingPlayer().isHuman()) {
			incrementPlayerSpellCount();
			if (sp instanceof Spell_Permanent && sp.getSourceCard().isCreature()) {
				incrementPlayerCreatureSpellCount();
			}
			if (sp.getSourceCard().isInstant()) {
				incrementPlayerInstantSpellCount();
			}
		} 
		
		else {
			incrementComputerSpellCount();
			if (sp instanceof Spell_Permanent && sp.getSourceCard().isCreature()) {
				incrementComputerCreatureSpellCount();
			}
			if (sp.getSourceCard().isInstant()) {
				incrementComputerInstantSpellCount();
			}
		}
	}

	protected static void incrementComputerInstantSpellCount() {
		FGameState gs = Unstatic.getGlobalGameState();
		gs.setComputerInstantSpellCount(1 + gs.getComputerInstantSpellCount());
	}

	protected static void incrementComputerCreatureSpellCount() {
		FGameState gs = Unstatic.getGlobalGameState();
		gs.setComputerCreatureSpellCount(1 + gs.getComputerCreatureSpellCount());
	}

	protected static void incrementComputerSpellCount() {
		FGameState gs = Unstatic.getGlobalGameState();
		gs.setComputerSpellCount(1 + gs.getComputerSpellCount());
		
	}

	protected static void incrementPlayerInstantSpellCount() {
		FGameState gs = Unstatic.getGlobalGameState();
		gs.setPlayerInstantSpellCount(1 + gs.getPlayerInstantSpellCount());
		
	}

	protected static void incrementPlayerCreatureSpellCount() {
		FGameState gs = Unstatic.getGlobalGameState();
		gs.setPlayerCreatureSpellCount(1 + gs.getPlayerCreatureSpellCount());
		
	}

	protected static void incrementPlayerSpellCount() {
		FGameState gs = Unstatic.getGlobalGameState();
		gs.setPlayerSpellCount(1 + gs.getPlayerSpellCount());
		
	}

	protected static void incrementStormCount() {
		FGameState gs = Unstatic.getGlobalGameState();
		gs.setStormCount(1 + gs.getStormCount());
		
	}

	public static void setStormCount(int stormCount) {
		Unstatic.getGlobalGameState().setStormCount(stormCount);
	}

	public static int getStormCount() {
		return Unstatic.getGlobalGameState().getStormCount();
	}

	public static void setGameBegins(int gameBegins) {
		Unstatic.getGlobalGameState().setGameBegins(gameBegins);
	}

	public static int getGameBegins() {
		return Unstatic.getGlobalGameState().getGameBegins();
	}
	
	// this is a hack for the setup game state mode, do not use outside of devSetupGameState code
	// as it avoids calling any of the phase effects that may be necessary in a less enforced context
	public void setDevPhaseState(String phaseID)
	{
		this.phaseIndex = findIndex(phaseID);
	}

    static int getPlayerSpellCount() {
    	return Unstatic.getGlobalGameState().getPlayerSpellCount();
    }

    static void setPlayerSpellCount(int i) {
    	Unstatic.getGlobalGameState().setPlayerSpellCount(i);
    }
    
    static int getPlayerCreatureSpellCount() {
    	return Unstatic.getGlobalGameState().getPlayerCreatureSpellCount();
    }

    static void setPlayerCreatureSpellCount(int i) { 
    	Unstatic.getGlobalGameState().setPlayerCreatureSpellCount(i);
    }

    static int getPlayerInstantSpellCount() {
    	return Unstatic.getGlobalGameState().getPlayerInstantSpellCount();
    }
    
    static void setPlayerInstantSpellCount(int i) { 
    	Unstatic.getGlobalGameState().setPlayerInstantSpellCount(i);
    }
    
    static int getComputerSpellCount() {
    	return Unstatic.getGlobalGameState().getComputerSpellCount();
    }
    
    static void setComputerSpellCount(int i) { 
    	Unstatic.getGlobalGameState().setComputerSpellCount(i);
    }
    
    static int getComputerCreatureSpellCount() {
    	return Unstatic.getGlobalGameState().getComputerCreatureSpellCount();
    }
    
    static void setComputerCreatureSpellCount(int i) { 
    	Unstatic.getGlobalGameState().setComputerCreatureSpellCount(i);
    }
    
    static int getComputerInstantSpellCount() {
    	return Unstatic.getGlobalGameState().getComputerInstantSpellCount();
    }
    
    static void setComputerInstantSpellCount(int i) { 
    	Unstatic.getGlobalGameState().setComputerInstantSpellCount(i);
    }
}
