package forge;


import forge.card.cardFactory.CardFactory;
import forge.card.mana.ManaPool;
import forge.card.trigger.TriggerHandler;
import forge.gui.input.InputControl;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Please use public getters and setters instead of direct field access.
 * <p/>
 * If you need a setter, by all means, add it.
 */
public class AllZone implements NewConstants {
    //only for testing, should read decks from local directory
//  public static final IO IO = new IO("all-decks");

    private static final Player HumanPlayer = new HumanPlayer("Human");
    private static final Player ComputerPlayer = new AIPlayer("Computer");

    private static forge.quest.data.QuestData QuestData = null;
    private static Quest_Assignment QuestAssignment = null;
    private static final NameChanger NameChanger = new NameChanger();

    private static EndOfTurn EndOfTurn = new EndOfTurn();
    private static EndOfCombat EndOfCombat = new EndOfCombat();

    private static final Phase Phase = new Phase();

    // Phase is now a prerequisite for CardFactory
    private static final CardFactory CardFactory = new CardFactory(ForgeProps.getFile(CARDSFOLDER));

    private static final MagicStack Stack = new MagicStack();
    private static final InputControl InputControl = new InputControl();
    private static final GameAction GameAction = new GameAction();
    private static final StaticEffects StaticEffects = new StaticEffects();
    private static final GameInfo GameInfo = new GameInfo();

    private static final TriggerHandler TriggerHandler = new TriggerHandler();

    //initialized at Runtime since it has to be the last object constructed

    private static ComputerAI_Input Computer;

    //shared between Input_Attack, Input_Block, Input_CombatDamage , InputState_Computer

    private static Combat Combat = new Combat();

    //Human_Play, Computer_Play is different because Card.comesIntoPlay() is called when a card is added by PlayerZone.add(Card)
    private final static PlayerZone Human_Battlefield = new PlayerZone_ComesIntoPlay(Constant.Zone.Battlefield, AllZone.getHumanPlayer());
    private final static PlayerZone Human_Hand = new DefaultPlayerZone(Constant.Zone.Hand, AllZone.getHumanPlayer());
    private final static PlayerZone Human_Graveyard = new DefaultPlayerZone(Constant.Zone.Graveyard, AllZone.getHumanPlayer());
    private final static PlayerZone Human_Library = new DefaultPlayerZone(Constant.Zone.Library, AllZone.getHumanPlayer());
    private final static PlayerZone Human_Exile = new DefaultPlayerZone(Constant.Zone.Exile, AllZone.getHumanPlayer());
    private final static PlayerZone Human_Command = new DefaultPlayerZone(Constant.Zone.Command, AllZone.getHumanPlayer());

    private final static PlayerZone Computer_Battlefield = new PlayerZone_ComesIntoPlay(Constant.Zone.Battlefield, AllZone.getComputerPlayer());
    private final static PlayerZone Computer_Hand = new DefaultPlayerZone(Constant.Zone.Hand, AllZone.getComputerPlayer());
    private final static PlayerZone Computer_Graveyard = new DefaultPlayerZone(Constant.Zone.Graveyard, AllZone.getComputerPlayer());
    private final static PlayerZone Computer_Library = new DefaultPlayerZone(Constant.Zone.Library, AllZone.getComputerPlayer());
    private final static PlayerZone Computer_Exile = new DefaultPlayerZone(Constant.Zone.Exile, AllZone.getComputerPlayer());
    private final static PlayerZone Computer_Command = new DefaultPlayerZone(Constant.Zone.Command, AllZone.getComputerPlayer());

    private final static PlayerZone Stack_Zone = new DefaultPlayerZone(Constant.Zone.Stack, null);

    private static final ManaPool ManaPool = new ManaPool(AllZone.getHumanPlayer());
    private static final ManaPool Computer_ManaPool = new ManaPool(AllZone.getComputerPlayer());

    private static Display Display;

    private final static Map<String, PlayerZone> map = new HashMap<String, PlayerZone>();

    static {
        map.put(Constant.Zone.Graveyard + AllZone.getHumanPlayer(), Human_Graveyard);
        map.put(Constant.Zone.Hand + AllZone.getHumanPlayer(), Human_Hand);
        map.put(Constant.Zone.Library + AllZone.getHumanPlayer(), Human_Library);
        map.put(Constant.Zone.Battlefield + AllZone.getHumanPlayer(), Human_Battlefield);
        map.put(Constant.Zone.Exile + AllZone.getHumanPlayer(), Human_Exile);
        map.put(Constant.Zone.Command + AllZone.getHumanPlayer(), Human_Command);

        map.put(Constant.Zone.Graveyard + AllZone.getComputerPlayer(), Computer_Graveyard);
        map.put(Constant.Zone.Hand + AllZone.getComputerPlayer(), Computer_Hand);
        map.put(Constant.Zone.Library + AllZone.getComputerPlayer(), Computer_Library);
        map.put(Constant.Zone.Battlefield + AllZone.getComputerPlayer(), Computer_Battlefield);
        map.put(Constant.Zone.Exile + AllZone.getComputerPlayer(), Computer_Exile);
        map.put(Constant.Zone.Command + AllZone.getComputerPlayer(), Computer_Command);

        map.put(Constant.Zone.Stack + null, Stack_Zone);

    }

    public static Player getHumanPlayer() {
        return HumanPlayer;
    }

    public static Player getComputerPlayer() {
        return ComputerPlayer;
    }

    public static forge.quest.data.QuestData getQuestData() {
        return QuestData;
    }

    public static void setQuestData(forge.quest.data.QuestData questData) {
        QuestData = questData;
    }

    public static Quest_Assignment getQuestAssignment() {
        return QuestAssignment;
    }

    public static void setQuestAssignment(Quest_Assignment assignment) {
        QuestAssignment = assignment;
    }

    public static NameChanger getNameChanger() {
        return NameChanger;
    }

    public static EndOfTurn getEndOfTurn() {
        return EndOfTurn;
    }

    public static forge.EndOfCombat getEndOfCombat() {
        return EndOfCombat;
    }

    public static Phase getPhase() {
        return Phase;
    }

    public static CardFactory getCardFactory() {
        return CardFactory;
    }

    public static MagicStack getStack() {
        return Stack;
    }

    public static InputControl getInputControl() {
        return InputControl;
    }

    public static GameAction getGameAction() {
        return GameAction;
    }

    public static StaticEffects getStaticEffects() {
        return StaticEffects;
    }

    public static GameInfo getGameInfo() {
        return GameInfo;
    }

    public static TriggerHandler getTriggerHandler() {
        return TriggerHandler;
    }

    public static ComputerAI_Input getComputer() {
        return Computer;
    }

    public static void setComputer(ComputerAI_Input input) {
        Computer = input;
    }

    public static Combat getCombat() {
        return Combat;
    }

    public static void setCombat(Combat attackers) {
        Combat = attackers;
    }

    //Human_Play, Computer_Play is different because Card.comesIntoPlay() is called when a card is added by PlayerZone.add(Card)
    public static PlayerZone getHumanBattlefield() {
        return Human_Battlefield;
    }

    public static PlayerZone getHumanHand() {
        return Human_Hand;
    }

    public static PlayerZone getHumanGraveyard() {
        return Human_Graveyard;
    }

    public static PlayerZone getHumanLibrary() {
        return Human_Library;
    }

    public static PlayerZone getHumanExile() {
        return Human_Exile;
    }

    public static PlayerZone getHumanCommand() {
        return Human_Command;
    }

    public static PlayerZone getComputerBattlefield() {
        return Computer_Battlefield;
    }

    public static PlayerZone getComputerHand() {
        return Computer_Hand;
    }

    public static PlayerZone getComputerGraveyard() {
        return Computer_Graveyard;
    }

    public static PlayerZone getComputerLibrary() {
        return Computer_Library;
    }

    public static PlayerZone getComputerExile() {
        return Computer_Exile;
    }

    public static PlayerZone getComputerCommand() {
        return Computer_Command;
    }

    public static PlayerZone getStackZone() {
        return Stack_Zone;
    }

    public static ManaPool getManaPool() {
        return ManaPool;
    }

    public static ManaPool getComputerManaPool() {
        return Computer_ManaPool;
    }

    public static Display getDisplay() {
        return Display;
    }

    public static void setDisplay(Display display) {
        Display = display;
    }

    private static Map<String, PlayerZone> getMap() {
        return map;
    }

    public static PlayerZone getZone(Card c) {
        Iterator<PlayerZone> it = getMap().values().iterator();
        PlayerZone p;
        while (it.hasNext()) {
            p = (PlayerZone) it.next();

            if (AllZoneUtil.isCardInZone(p, c))
                return p;
        }
        return null;
    }

    public static PlayerZone getZone(String zone, Player player) {
        if (zone.equals("Stack")) player = null;
        Object o = getMap().get(zone + player);
        if (o == null)
            throw new RuntimeException("AllZone : getZone() invalid parameters " + zone + " " + player);

        return (PlayerZone) o;
    }

    public static void resetZoneMoveTracking() {
        ((DefaultPlayerZone) getHumanCommand()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getHumanLibrary()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getHumanHand()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getHumanBattlefield()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getHumanGraveyard()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getComputerCommand()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getComputerLibrary()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getComputerHand()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getComputerBattlefield()).resetCardsAddedThisTurn();
        ((DefaultPlayerZone) getComputerGraveyard()).resetCardsAddedThisTurn();
    }
}//AllZone
