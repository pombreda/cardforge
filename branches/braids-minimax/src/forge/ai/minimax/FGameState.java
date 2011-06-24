package forge.ai.minimax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.slightlymagic.braids.game.ai.minimax.MinimaxGameState;
import net.slightlymagic.braids.game.ai.minimax.MinimaxMove;
import net.slightlymagic.braids.game.ai.minimax.MinimaxPlayer;
import net.slightlymagic.braids.util.DeepCopier;
import net.slightlymagic.braids.util.UtilFunctions;
import forge.AIPlayer;
import forge.AllZone;
import forge.AllZoneUtil;
import forge.Card;
import forge.CardList;
import forge.Combat;
import forge.ComputerAI_Input;
import forge.ComputerUtil;
import forge.ComputerUtil_Attack2;
import forge.ComputerUtil_Block2;
import forge.Constant;
import forge.DefaultPlayerZone;
import forge.Display;
import forge.EndOfCombat;
import forge.EndOfTurn;
import forge.GameAction;
import forge.GameInfo;
import forge.HumanPlayer;
import forge.MagicStack;
import forge.NameChanger;
import forge.Phase;
import forge.Player;
import forge.PlayerZone;
import forge.PlayerZone_ComesIntoPlay;
import forge.Quest_Assignment;
import forge.StaticEffects;
import forge.card.mana.ManaPool;
import forge.card.spellability.SpellAbility;
import forge.card.trigger.TriggerHandler;
import forge.gui.input.Input;
import forge.gui.input.InputControl;
import forge.gui.input.Input_PassPriority;

/**
 * Do not add static members (static fields) to this class.
 * Ever.
 * 
 * To do so would make it very difficult to clone.
 */
public class FGameState implements MinimaxGameState, Serializable {

	private static final long serialVersionUID = 1446592967388632637L;

	private byte[] digest;
	
	//////////////////////////////////////////////////////////////////////////
	// Begin fields derived from forge.AllZone

	private forge.Player humanPlayer;
    private forge.Player computerPlayer;
    private forge.quest.data.QuestData questData;
    private Quest_Assignment questAssignment;
    private NameChanger nameChanger;
    private EndOfTurn endOfTurn;
    private EndOfCombat	endOfCombat;
    private Phase phase;
    private MagicStack stack;
    private InputControl inputControl;
    private GameAction gameAction;
    private StaticEffects staticEffects;
    private GameInfo gameInfo;
    private TriggerHandler triggerHandler;
    private ComputerAI_Input computer;
    private Combat combat;
    private PlayerZone humanBattlefield;
    private PlayerZone humanHand;
    private PlayerZone humanGraveyard;
    private PlayerZone humanLibrary;
    private PlayerZone humanExile;
    private PlayerZone humanCommand;
    private PlayerZone computerBattlefield;
    private PlayerZone computerHand;
    private PlayerZone computerGraveyard;
    private PlayerZone computerLibrary;
    private PlayerZone computerExile;
    private PlayerZone computerCommand;
    private PlayerZone stackZone;
    private ManaPool manaPool;
    private ManaPool computerManaPool;
    private Display display;
    private Map<String,PlayerZone> namesToZones;

	// End fields derived from forge.AllZone
	//////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Begin fields derived from forge.ComputerUtil_Block2
    
    private CardList attackers;
    private CardList attackersLeft;
    private CardList blockedButUnkilled;
    private CardList blockersLeft;
    private int diff;
    
    // End fields derived from forge.ComputerUtil_Block2
    ///////////////////////////////////////////////////////////////////////////

    
    ///////////////////////////////////////////////////////////////////////////
    // Begin fields derived from forge.Phase

    private int	gameBegins;
    private int	stormCount;
    private int	playerSpellCount;
    private int	playerCreatureSpellCount;
    private int	playerInstantSpellCount;
    private int	computerSpellCount;
    private int	computerCreatureSpellCount;
    private int	computerInstantSpellCount;

    // End fields derived from forge.Phase
    ///////////////////////////////////////////////////////////////////////////
    
    
    // from forge.PlayerZone_ComesIntoPlay:
	private boolean simultaneousEntry = false; // For Cards with Multiple Token Entry. Only Affects Allies at the moment.
    
    
    public FGameState() {
	}
    
    public void bootstrap() {

    	// From forge.AllZone:
    	humanPlayer = new HumanPlayer("Human");
    	computerPlayer = new AIPlayer("Computer");
    	nameChanger = new NameChanger();
    	endOfTurn = new EndOfTurn();
    	endOfCombat = new EndOfCombat();
    	phase = new Phase();
		stack = new MagicStack();
		inputControl = new InputControl();
		gameAction = new GameAction();
		staticEffects = new StaticEffects();
		gameInfo = new GameInfo();
		triggerHandler = new TriggerHandler();
		combat = new Combat();
	    humanBattlefield	= new PlayerZone_ComesIntoPlay(Constant.Zone.Battlefield, humanPlayer);
	    humanHand      		= new DefaultPlayerZone(Constant.Zone.Hand      , humanPlayer);
	    humanGraveyard 		= new DefaultPlayerZone(Constant.Zone.Graveyard , humanPlayer);
	    humanLibrary   		= new DefaultPlayerZone(Constant.Zone.Library   , humanPlayer);
	    humanExile   		= new DefaultPlayerZone(Constant.Zone.Exile, humanPlayer);
	    humanCommand   		= new DefaultPlayerZone(Constant.Zone.Command, humanPlayer);

	    computerBattlefield	= new PlayerZone_ComesIntoPlay(Constant.Zone.Battlefield, computerPlayer);
	    computerHand      	= new DefaultPlayerZone(Constant.Zone.Hand      , computerPlayer);
	    computerGraveyard 	= new DefaultPlayerZone(Constant.Zone.Graveyard , computerPlayer);
	    computerLibrary   	= new DefaultPlayerZone(Constant.Zone.Library   , computerPlayer);
	    computerExile   	= new DefaultPlayerZone(Constant.Zone.Exile, computerPlayer);
	    computerCommand   	= new DefaultPlayerZone(Constant.Zone.Command, computerPlayer);
	    stackZone   = new DefaultPlayerZone(Constant.Zone.Stack, null);

	    //initialized at runtime since it has to be the last object constructed (?)
		manaPool = new ManaPool(humanPlayer);
		humanPlayer.setManaPool(manaPool);

		computerManaPool = new ManaPool(computerPlayer);
		computerPlayer.setManaPool(computerManaPool);

		namesToZones = new HashMap<String,PlayerZone>();
		namesToZones.put(Constant.Zone.Graveyard         + humanPlayer, humanGraveyard);
		namesToZones.put(Constant.Zone.Hand              + humanPlayer, humanHand);
		namesToZones.put(Constant.Zone.Library           + humanPlayer, humanLibrary);
		namesToZones.put(Constant.Zone.Battlefield		+ humanPlayer, humanBattlefield);
		namesToZones.put(Constant.Zone.Exile 			+ humanPlayer, humanExile);
		namesToZones.put(Constant.Zone.Command 			+ humanPlayer, humanCommand);
		
		namesToZones.put(Constant.Zone.Graveyard         + computerPlayer, computerGraveyard);
		namesToZones.put(Constant.Zone.Hand              + computerPlayer, computerHand);
		namesToZones.put(Constant.Zone.Library           + computerPlayer, computerLibrary);
		namesToZones.put(Constant.Zone.Battlefield       + computerPlayer, computerBattlefield);
		namesToZones.put(Constant.Zone.Exile 			+ computerPlayer, computerExile);
		namesToZones.put(Constant.Zone.Command 			+ computerPlayer, computerCommand);
		
		namesToZones.put(Constant.Zone.Stack				+ null, stackZone);

		
	    // From forge.ComputerUtil_Block2:
	    attackers = new CardList(); //all attackers
	    attackersLeft = new CardList(); //keeps track of all currently unblocked attackers
	    blockedButUnkilled = new CardList(); //blocked attackers that currently wouldn't be destroyed
	    blockersLeft = new CardList(); //keeps track of all unassigned blockers

    }
    
	public MinimaxGameState cloneFor(MinimaxPlayer playerI) 
	{
		Player player = (Player) playerI;
		
		byte[][] digestOut = new byte[1][];
		
		FGameState clone = 
			(FGameState) DeepCopier.staticCopyWithDigest(digestOut, this);
		
		clone.setDigest(digestOut[0]);

		for (Player clonedPlayer : clone.getNonOustedPlayers()) {
			if (!player.isPlayer(clonedPlayer)) {
				fillWithDummyCardsIfNotAlreadyDoneSo(
						getHandZoneFor(clonedPlayer));
			}

			fillWithDummyCardsIfNotAlreadyDoneSo(
					getLibraryZoneFor(clonedPlayer));
		}
		
		return clone;
	}

	private PlayerZone getLibraryZoneFor(Player player) {
		if (getHumanPlayer().isPlayer(player)) {
			return getHumanLibrary();
		}
		else {
			return getComputerLibrary();
		}
	}

	private PlayerZone getHandZoneFor(Player player) {
		if (getHumanPlayer().isPlayer(player)) {
			return getHumanHand();
		}
		else {
			return getComputerHand();
		}
	}

	public Collection<Player> getNonOustedPlayers() {
		ArrayList<Player> result = new ArrayList<Player>(2);
		
		result.add(getHumanPlayer());
		result.add(getComputerPlayer());

		return result;
	}
	
	/**
	 * We interpret the presence of a single dummy-card in the zone as evidence
	 * that the entire zone has already been filled with dummy-cards.  If the
	 * zone is empty, we do nothing.
	 * 
	 * @param zone  the zone to potentially modify
	 */
	public static void fillWithDummyCardsIfNotAlreadyDoneSo(PlayerZone zone) {
		int size = zone.size();
		if (size > 0 && !zone.getCards()[0].equals(Constants.DUMMY_CARD)) {
			Card[] replacements = new Card[size];
			for (int i = 0; i < size; i++) {
				// It saves on memory to use the same card instance over and 
				// over. I hope this doesn't cause problems.
				replacements[i] = Constants.DUMMY_CARD;
			}
			zone.setCards(replacements);
		}
	}

	
	public Collection<MinimaxMove> getPossibleMoves() {
		Player priorityPlayer = (Player) whoHasPriority();

		// Create the result ArrayList with a healthy estimate of the number of
		// possible moves.
		CardList handList = AllZoneUtil.getPlayerHand(priorityPlayer);
		CardList inPlayList = AllZoneUtil.getPlayerCardsInPlay(priorityPlayer);

		ArrayList<MinimaxMove> result = 
			new ArrayList<MinimaxMove>(handList.size() + inPlayList.size() * 2);
		
		InputControl inputControl = AllZone.getInputControl();
		Input input = null;
		if (inputControl != null) {
			input = inputControl.getInput();
		}
		
		if (getPhase().is(Constant.Phase.Combat_Declare_Attackers, priorityPlayer)) {
			// Use first-generation AI for this, except that we also allow
			// the player to pass (not attack) at all.

			Player defendingPlayer = getOpponentOf(priorityPlayer);
					
			ComputerUtil_Attack2 att = 
				new ComputerUtil_Attack2(
					AllZoneUtil.getPlayerCardsInPlay(priorityPlayer),
					AllZoneUtil.getPlayerCardsInPlay(defendingPlayer), 
					defendingPlayer.getLife());
			
			Combat combat = att.getAttackers();
			MinimaxMove move = new DeclareAttackersMove(combat); 
			result.add(move);
			result.add(Constants.PASS_MOVE);
		}
		else if (getPhase().is(Constant.Phase.Combat_Declare_Blockers, priorityPlayer)) 
		{
			// Use first-generation AI for this, except that we also allow
			// the player to pass (not block) at all. 

			CardList blockers = AllZoneUtil.getPlayerCardsInPlay(priorityPlayer);
		    Combat combat = 
		    	ComputerUtil_Block2.getBlockers(AllZone.getCombat(), blockers);
		    
		    MinimaxMove move = new DeclareBlockersMove(combat); 
			result.add(move);
			result.add(Constants.PASS_MOVE);
		}
		else if (input != null && !(input instanceof Input_PassPriority)) 
		{
			// Add the possible decisions as individual MinimaxMoves.
			
			InputControl inputCtrl = AllZone.getInputControl();
			
			for (MinimaxMove move : inputCtrl.getMoves()) {
				result.add(move);
			}
			// Do not add PASS_MOVE; we are making mandatory decisions for
			// spells and effects.
		}
		else if (Constants.PHASES_WHERE_PLAYERS_CAN_USE_ABILITIES.contains(getPhase().getPhase())) 
		{
			// It's a normal state where we can play instants and abilities
			// (at least).  I hope.
			
			if (priorityPlayer.canPlayLand()) {
				CardList landList = handList.filter(AllZoneUtil.lands);

				for (Card land : landList) {
					result.add(new PlayLandMove(land));
				}
			}

			// Passing true as the second parameter causes ComputerUtil to 
			// ignore the first generation AI.
			for (SpellAbility move : ComputerUtil.getSpellAbility(priorityPlayer, true)) {
				result.add(move);
			}

			result.add(Constants.PASS_MOVE);
		}
		
		UtilFunctions.smartRemoveDuplicatesAndNulls(result);
		result.trimToSize();
		return result;
	}

	
	private Player getOpponentOf(Player player) {
		if (getHumanPlayer().isPlayer(player)) {
			return getComputerPlayer();
		}
		else {
			return getHumanPlayer();
		}
	}

	public MinimaxPlayer whoHasPriority() {
		return phase.getPriorityPlayer();
	}
	
	/**
	 * Convenience function to automatically perform the cast to type Player.
	 * @return a Player instead of a MinimaxPlayer
	 * @see #whoHasPriority()
	 */
	public Player whoHasPriorityAsPlayer() {
		return (Player) whoHasPriority();
	}

	public void setDigest(byte[] digest) {
		this.digest = digest;
	}
	
	public byte[] getDigest() {
		return this.digest;  // null means we haven't implemented digests
	}
	

	
	//////////////////////////////////////////////////////////////////////////
	// Begin methods derived from forge.AllZone

	public forge.Player getHumanPlayer() {
		return humanPlayer;
	}

	public void setHumanPlayer(forge.Player humanPlayer) {
		this.humanPlayer = humanPlayer;
	}

	public forge.Player getComputerPlayer() {
		return computerPlayer;
	}

	public void setComputerPlayer(forge.Player computerPlayer) {
		this.computerPlayer = computerPlayer;
	}

	public forge.quest.data.QuestData getQuestData() {
		return questData;
	}

	public void setQuestData(forge.quest.data.QuestData questData) {
		this.questData = questData;
	}

	public Quest_Assignment getQuestAssignment() {
		return questAssignment;
	}

	public void setQuestAssignment(Quest_Assignment questAssignment) {
		this.questAssignment = questAssignment;
	}

	public NameChanger getNameChanger() {
		return nameChanger;
	}

	public void setNameChanger(NameChanger nameChanger) {
		this.nameChanger = nameChanger;
	}

	public EndOfTurn getEndOfTurn() {
		return endOfTurn;
	}

	public void setEndOfTurn(EndOfTurn endOfTurn) {
		this.endOfTurn = endOfTurn;
	}

	public EndOfCombat getEndOfCombat() {
		return endOfCombat;
	}

	public void setEndOfCombat(EndOfCombat endOfCombat) {
		this.endOfCombat = endOfCombat;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public MagicStack getStack() {
		return stack;
	}

	public void setStack(MagicStack stack) {
		this.stack = stack;
	}

	public InputControl getInputControl() {
		return inputControl;
	}

	public void setInputControl(InputControl inputControl) {
		this.inputControl = inputControl;
	}

	public GameAction getGameAction() {
		return gameAction;
	}

	public void setGameAction(GameAction gameAction) {
		this.gameAction = gameAction;
	}

	public StaticEffects getStaticEffects() {
		return staticEffects;
	}

	public void setStaticEffects(StaticEffects staticEffects) {
		this.staticEffects = staticEffects;
	}

	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}

	public TriggerHandler getTriggerHandler() {
		return triggerHandler;
	}

	public void setTriggerHandler(TriggerHandler triggerHandler) {
		this.triggerHandler = triggerHandler;
	}

	public ComputerAI_Input getComputer() {
		return computer;
	}

	public void setComputer(ComputerAI_Input computer) {
		this.computer = computer;
	}

	public Combat getCombat() {
		return combat;
	}

	public void setCombat(Combat combat) {
		this.combat = combat;
	}

	public PlayerZone getHumanBattlefield() {
		return humanBattlefield;
	}

	public void setHumanBattlefield(PlayerZone humanBattlefield) {
		this.humanBattlefield = humanBattlefield;
	}

	public PlayerZone getHumanHand() {
		return humanHand;
	}

	public void setHumanHand(PlayerZone humanHand) {
		this.humanHand = humanHand;
	}

	public PlayerZone getHumanGraveyard() {
		return humanGraveyard;
	}

	public void setHumanGraveyard(PlayerZone humanGraveyard) {
		this.humanGraveyard = humanGraveyard;
	}

	public PlayerZone getHumanLibrary() {
		return humanLibrary;
	}

	public void setHumanLibrary(PlayerZone humanLibrary) {
		this.humanLibrary = humanLibrary;
	}

	public PlayerZone getHumanExile() {
		return humanExile;
	}

	public void setHumanExile(PlayerZone humanExile) {
		this.humanExile = humanExile;
	}

	public PlayerZone getHumanCommand() {
		return humanCommand;
	}

	public void setHumanCommand(PlayerZone humanCommand) {
		this.humanCommand = humanCommand;
	}

	public PlayerZone getComputerBattlefield() {
		return computerBattlefield;
	}

	public void setComputerBattlefield(PlayerZone computerBattlefield) {
		this.computerBattlefield = computerBattlefield;
	}

	public PlayerZone getComputerHand() {
		return computerHand;
	}

	public void setComputerHand(PlayerZone computerHand) {
		this.computerHand = computerHand;
	}

	public PlayerZone getComputerGraveyard() {
		return computerGraveyard;
	}

	public void setComputerGraveyard(PlayerZone computerGraveyard) {
		this.computerGraveyard = computerGraveyard;
	}

	public PlayerZone getComputerLibrary() {
		return computerLibrary;
	}

	public void setComputerLibrary(PlayerZone computerLibrary) {
		this.computerLibrary = computerLibrary;
	}

	public PlayerZone getComputerExile() {
		return computerExile;
	}

	public void setComputerExile(PlayerZone computerExile) {
		this.computerExile = computerExile;
	}

	public PlayerZone getComputerCommand() {
		return computerCommand;
	}

	public void setComputerCommand(PlayerZone computerCommand) {
		this.computerCommand = computerCommand;
	}

	public PlayerZone getStackZone() {
		return stackZone;
	}

	public void setStackZone(PlayerZone stackZone) {
		this.stackZone = stackZone;
	}

	public ManaPool getManaPool() {
		return manaPool;
	}

	public void setManaPool(ManaPool manaPool) {
		this.manaPool = manaPool;
	}

	public ManaPool getComputerManaPool() {
		return computerManaPool;
	}

	public void setComputerManaPool(ManaPool computerManaPool) {
		this.computerManaPool = computerManaPool;
	}

	public Display getDisplay() {
		return display;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

    //shared among Input_Attack, Input_Block, Input_CombatDamage , InputState_Computer
	public Map<String, PlayerZone> getNamesToZones() {
		return namesToZones;
	}

	public void setNamesToZones(Map<String, PlayerZone> map) {
		this.namesToZones = map;
	}

	// End methods derived from forge.AllZone
	//////////////////////////////////////////////////////////////////////////

	
	//////////////////////////////////////////////////////////////////////////
    // Begin methods derived from forge.ComputerUtil_Block2

	public CardList getAttackers() {
		return attackers;
	}

	public void setAttackers(CardList attackers) {
		this.attackers = attackers;
	}

	public CardList getAttackersLeft() {
		return attackersLeft;
	}

	public void setAttackersLeft(CardList attackersLeft) {
		this.attackersLeft = attackersLeft;
	}

	public CardList getBlockedButUnkilled() {
		return blockedButUnkilled;
	}

	public void setBlockedButUnkilled(CardList blockedButUnkilled) {
		this.blockedButUnkilled = blockedButUnkilled;
	}

	public CardList getBlockersLeft() {
		return blockersLeft;
	}

	public void setBlockersLeft(CardList blockersLeft) {
		this.blockersLeft = blockersLeft;
	}

	public int getDiff() {
		return diff;
	}

	public void setDiff(int diff) {
		this.diff = diff;
	}

    // End methods derived from forge.ComputerUtil_Block2
	//////////////////////////////////////////////////////////////////////////


	//////////////////////////////////////////////////////////////////////////
    // Begin methods derived from forge.Phase
	
	public int getGameBegins() {
		return gameBegins;
	}

	public void setGameBegins(int begins) {
		gameBegins = begins;
	}

	public int getStormCount() {
		return stormCount;
	}

	public void setStormCount(int count) {
		stormCount = count;
	}

	public int getPlayerSpellCount() {
		return playerSpellCount;
	}

	public void setPlayerSpellCount(int count) {
		playerSpellCount = count;
	}

	public int getPlayerCreatureSpellCount() {
		return playerCreatureSpellCount;
	}

	public void setPlayerCreatureSpellCount(int count) {
		playerCreatureSpellCount = count;
	}

	public int getPlayerInstantSpellCount() {
		return playerInstantSpellCount;
	}

	public void setPlayerInstantSpellCount(int count) {
		playerInstantSpellCount = count;
	}

	public int getComputerSpellCount() {
		return computerSpellCount;
	}

	public void setComputerSpellCount(int count) {
		computerSpellCount = count;
	}

	public int getComputerCreatureSpellCount() {
		return computerCreatureSpellCount;
	}

	public void setComputerCreatureSpellCount(int count) {
		computerCreatureSpellCount = count;
	}

	public int getComputerInstantSpellCount() {
		return computerInstantSpellCount;
	}

	public void setComputerInstantSpellCount(int count) {
		computerInstantSpellCount = count;
	}

    // End methods derived from forge.Phase
	//////////////////////////////////////////////////////////////////////////
	

	//////////////////////////////////////////////////////////////////////////
	// Begin methods derived from forge.PlayerZone_ComesIntoPlay
	
	public boolean isSimultaneousEntry() {
		return simultaneousEntry;
	}

	public void setSimultaneousEntry(boolean simultaneousEntry) {
		this.simultaneousEntry = simultaneousEntry;
	}

	// End methods derived from forge.PlayerZone_ComesIntoPlay
	//////////////////////////////////////////////////////////////////////////
}
