package forge.quest.data;

import forge.*;
import forge.error.ErrorViewer;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;

import java.io.File;
import java.util.*;

public class QuestBattleManager {
    private static transient Map<String, Deck> aiDecks = new HashMap<String, Deck>();
    private static transient List<String> easyAIDecks;
    private static transient List<String> mediumAIDecks;
    private static transient List<String> hardAIDecks;

   static {
        List<String> aiDeckNames = getAIDeckNames();
        easyAIDecks = readFile(ForgeProps.getFile(NewConstants.QUEST.EASY), aiDeckNames);
        mediumAIDecks = readFile(ForgeProps.getFile(NewConstants.QUEST.MEDIUM), aiDeckNames);
        hardAIDecks = readFile(ForgeProps.getFile(NewConstants.QUEST.HARD), aiDeckNames);

    }


    public static void removeAIDeck(String deckName) {
        aiDecks.remove(deckName);
    }

    public static void addAIDeck(Deck d) {
        aiDecks.put(d.getName(), d);
    }

    public static Deck getAIDeck(String deckName) {
        if (!aiDecks.containsKey(deckName)) {
            ErrorViewer.showError(new Exception(),
                    "QuestData : getDeckFromMap(String deckName) error, deck name not found - %s", deckName);
        }

        return aiDecks.get(deckName);
    }

    public static Deck getAIDeckNewFormat(String deckName) {
        DeckIO deckIO = new NewDeckIO(ForgeProps.getFile(NewConstants.QUEST.DECKS), true);
        Deck aiDeck = deckIO.readDeck(deckName);
        return aiDeck;
    }

    public static List<String> getAIDeckNames() {
        return new ArrayList<String>(aiDecks.keySet());
    }

    public static String[] getOpponents(List<String> aiDeck) {
        //This is to make sure that the opponents do not change when the deck editor is launched.
        List<String> deckListCopy = new ArrayList<String>(aiDeck);
        Collections.shuffle(deckListCopy, new Random(AllZone.QuestData.getRandomSeed()));

        return new String[]{deckListCopy.get(0), deckListCopy.get(1), deckListCopy.get(2)};

    }


    public static String[] getOpponents() {
        int index = AllZone.QuestData.getDifficultyIndex();

        if (AllZone.QuestData.getWin() < AllZone.QuestData.getPreferences().getWinsForMediumAI(index)) {
            return getOpponents(easyAIDecks);
        }

        if (AllZone.QuestData.getWin() < AllZone.QuestData.getPreferences().getWinsForHardAI(index)) {
            return QuestBattleManager.getOpponents(mediumAIDecks);
        }

        return getOpponents(hardAIDecks);
    }

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
                    "QuestData : readFile() error, file %s is too short, it must contain at least 3 ai decks names",
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