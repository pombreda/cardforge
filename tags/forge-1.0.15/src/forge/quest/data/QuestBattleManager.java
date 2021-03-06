package forge.quest.data;

import forge.AllZone;
import forge.FileUtil;
import forge.deck.Deck;
import forge.deck.DeckManager;
import forge.error.ErrorViewer;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;

import java.io.File;
import java.util.*;

/**
 * <p>QuestBattleManager class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class QuestBattleManager {
    /** Constant <code>aiDecks</code> */
    private static transient Map<String, Deck> aiDecks = new HashMap<String, Deck>();
    /** Constant <code>easyAIDecks</code> */
    private static transient List<String> easyAIDecks;
    /** Constant <code>mediumAIDecks</code> */
    private static transient List<String> mediumAIDecks;
    /** Constant <code>hardAIDecks</code> */
    private static transient List<String> hardAIDecks;
    /** Constant <code>veryHardAIDecks</code> */
    private static transient List<String> veryHardAIDecks;

    static {
        List<String> aiDeckNames = getAIDeckNames();
        easyAIDecks = readFile(ForgeProps.getFile(NewConstants.QUEST.EASY), aiDeckNames);
        mediumAIDecks = readFile(ForgeProps.getFile(NewConstants.QUEST.MEDIUM), aiDeckNames);
        hardAIDecks = readFile(ForgeProps.getFile(NewConstants.QUEST.HARD), aiDeckNames);
        veryHardAIDecks = readFile(ForgeProps.getFile(NewConstants.QUEST.VERYHARD), aiDeckNames);
    }


    /**
     * <p>removeAIDeck.</p>
     *
     * @param deckName a {@link java.lang.String} object.
     */
    public static void removeAIDeck(String deckName) {
        aiDecks.remove(deckName);
    }

    /**
     * <p>addAIDeck.</p>
     *
     * @param d a {@link forge.deck.Deck} object.
     */
    public static void addAIDeck(Deck d) {
        aiDecks.put(d.getName(), d);
    }

    /**
     * <p>getAIDeck.</p>
     *
     * @param deckName a {@link java.lang.String} object.
     * @return a {@link forge.deck.Deck} object.
     */
    public static Deck getAIDeck(String deckName) {
        if (!aiDecks.containsKey(deckName)) {
            ErrorViewer.showError(new Exception(),
                    "QuestData : getDeckFromMap(String deckName) error, deck name not found - %s", deckName);
        }

        return aiDecks.get(deckName);
    }

    /**
     * <p>getAIDeckNewFormat.</p>
     *
     * @param deckName a {@link java.lang.String} object.
     * @return a {@link forge.deck.Deck} object.
     */
    public static Deck getAIDeckNewFormat(String deckName) {
        return (new DeckManager(ForgeProps.getFile(NewConstants.QUEST.DECKS))).getDeck(deckName);
    }

    /**
     * <p>getAIDeckNames.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public static List<String> getAIDeckNames() {
        return new ArrayList<String>(aiDecks.keySet());
    }

    /**
     * <p>getOpponent.</p>
     *
     * @param aiDeck a {@link java.util.List} object.
     * @param number a int.
     * @return a {@link java.lang.String} object.
     */
    public static String getOpponent(List<String> aiDeck, int number) {
        //This is to make sure that the opponents do not change when the deck editor is launched.
        List<String> deckListCopy = new ArrayList<String>(aiDeck);
        Collections.shuffle(deckListCopy, new Random(AllZone.getQuestData().getRandomSeed()));

        return deckListCopy.get(number);

    }


    /**
     * <p>getOpponents.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getOpponents() {
        int index = AllZone.getQuestData().getDifficultyIndex();

        if (AllZone.getQuestData().getWin() < QuestPreferences.getWinsForMediumAI(index)) {
            return new String[]{
                    getOpponent(easyAIDecks, 0),
                    getOpponent(easyAIDecks, 1),
                    getOpponent(easyAIDecks, 2)};
        }

        if (AllZone.getQuestData().getWin() == QuestPreferences.getWinsForMediumAI(index)) {
            return new String[]{
                    getOpponent(easyAIDecks, 0),
                    getOpponent(mediumAIDecks, 0),
                    getOpponent(mediumAIDecks, 1)};
        }

        if (AllZone.getQuestData().getWin() < QuestPreferences.getWinsForHardAI(index)) {
            return new String[]{
                    getOpponent(mediumAIDecks, 0),
                    getOpponent(mediumAIDecks, 1),
                    getOpponent(mediumAIDecks, 2)};
        }

        if (AllZone.getQuestData().getWin() == QuestPreferences.getWinsForHardAI(index)) {
            return new String[]{
                    getOpponent(mediumAIDecks, 0),
                    getOpponent(hardAIDecks, 0),
                    getOpponent(hardAIDecks, 1)};
        }

        if (AllZone.getQuestData().getWin() >= QuestPreferences.getWinsForVeryHardAI(index)) {
            return new String[]{
                    getOpponent(hardAIDecks, 0),
                    getOpponent(hardAIDecks, 1),
                    getOpponent(veryHardAIDecks, 0)};
        }

        return new String[]{
                getOpponent(hardAIDecks, 0),
                getOpponent(hardAIDecks, 1),
                getOpponent(hardAIDecks, 2)};
    }

    /**
     * <p>readFile.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param aiDecks a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    private static List<String> readFile(File file, List<String> aiDecks) {
        ArrayList<String> list = FileUtil.readFile(file);

        //remove any blank lines
        ArrayList<String> noBlankLines = new ArrayList<String>();
        String s;
        for (String aList : list) {
            s = aList.trim();
            if (!s.equals("")) {
                noBlankLines.add(s);
            }
        }
        list = noBlankLines;

        if (list.size() < 3) {
            ErrorViewer.showError(new Exception(),
                    "QuestData : readFile() error, file %s is too short, it must contain at least 3 ai deck names",
                    file);
        }


        for (String aList : list) {
            if (!aiDecks.contains(aList)) {
                aiDecks.add(aList);
            }
        }

        return list;
    }

}
