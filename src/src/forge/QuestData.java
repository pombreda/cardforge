package forge;

import com.esotericsoftware.minlog.Log;
import forge.deck.Deck;
import forge.deck.DeckManager;
import forge.error.ErrorViewer;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;

import java.io.*;
import java.util.*;


//when you create QuestData and AFTER you copy the AI decks over
//you have to call one of these two methods below
//see Gui_QuestOptions for more details
//
//static readAIQuestDeckFiles(QuestData data, ArrayList aiDeckNames)
//OR non-static readAIQuestDeckFiles()
//which reads the files "questDecks-easy", "questDecks-medium","questDecks-hard",
@Deprecated
/**
 * <p>QuestData class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class QuestData implements NewConstants {
    QuestData_Prefs qdPrefs = null;

    private int rankIndex;
    private int win;
    private int lost;

    private int plantLevel;
    private int wolfPetLevel;
    private int crocPetLevel;
    private int birdPetLevel;
    private int houndPetLevel;

    private String selectedPet;

    private int life;
    private int estatesLevel;
    private int luckyCoinLevel;
    private int sleightOfHandLevel;
    private int gearLevel;

    private int questsPlayed;

    private long credits;

    private int diffIndex;
    private String difficulty;
    private String mode = "";

    private ArrayList<String> easyAIDecks;
    private ArrayList<String> mediumAIDecks;
    private ArrayList<String> hardAIDecks;

    private Map<String, Deck> myDecks = new HashMap<String, Deck>();
    private Map<String, Deck> aiDecks = new HashMap<String, Deck>();

    //holds String card names
    private ArrayList<String> cardPool = new ArrayList<String>();
    private ArrayList<String> newCardList = new ArrayList<String>();
    private ArrayList<String> shopList = new ArrayList<String>();

    private ArrayList<Integer> availableQuests = new ArrayList<Integer>();
    private ArrayList<Integer> completedQuests = new ArrayList<Integer>();


    /** Constant <code>FANTASY="Fantasy"</code> */
    public static final String FANTASY = "Fantasy";
    /** Constant <code>REALISTIC="Realistic"</code> */
    public static final String REALISTIC = "Realistic";

    //TODO: Temporary.
    public boolean useNewQuestUI = false;

    /**
     * <p>Constructor for QuestData.</p>
     */
    public QuestData() {
        qdPrefs = new QuestData_Prefs();

        for (int i = 0; i < qdPrefs.getStartingBasic(); i++) {
            cardPool.add("Forest");
            cardPool.add("Mountain");
            cardPool.add("Swamp");
            cardPool.add("Island");
            cardPool.add("Plains");
        }

        for (int i = 0; i < qdPrefs.getStartingSnowBasic(); i++) {
            cardPool.add("Snow-Covered Forest");
            cardPool.add("Snow-Covered Mountain");
            cardPool.add("Snow-Covered Swamp");
            cardPool.add("Snow-Covered Island");
            cardPool.add("Snow-Covered Plains");
        }
    }//QuestData


    /**
     * <p>readAIQuestDeckFiles.</p>
     *
     * @param data a {@link forge.QuestData} object.
     * @param aiDeckNames a {@link java.util.ArrayList} object.
     */
    static public void readAIQuestDeckFiles(QuestData data, ArrayList<String> aiDeckNames) {
        data.easyAIDecks = readFile(ForgeProps.getFile(QUEST.EASY), aiDeckNames);
        data.mediumAIDecks = readFile(ForgeProps.getFile(QUEST.MEDIUM), aiDeckNames);
        data.hardAIDecks = readFile(ForgeProps.getFile(QUEST.HARD), aiDeckNames);
    }

    /**
     * <p>getOpponents.</p>
     *
     * @param aiDeck a {@link java.util.ArrayList} object.
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getOpponents(ArrayList<String> aiDeck) {
        Collections.shuffle(aiDeck);

        return new String[]{aiDeck.get(0).toString(), aiDeck.get(1).toString(), aiDeck.get(2).toString()};

    }//getOpponents()

    /**
     * <p>readFile.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param aiDecks a {@link java.util.ArrayList} object.
     * @return a {@link java.util.ArrayList} object.
     */
    private static ArrayList<String> readFile(File file, ArrayList<String> aiDecks) {
        ArrayList<String> list = FileUtil.readFile(file);

        //remove any blank lines
        ArrayList<String> noBlankLines = new ArrayList<String>();
        String s;
        for (int i = 0; i < list.size(); i++) {
            s = list.get(i).toString().trim();
            if (!s.equals("")) noBlankLines.add(s);
        }
        list = noBlankLines;

        if (list.size() < 3) ErrorViewer.showError(new Exception(),
                "QuestData : readFile() error, file %s is too short, it must contain at least 3 ai decks names",
                file);


        for (int i = 0; i < list.size(); i++)
            /*if(!aiDecks.contains(list.get(i).toString())) ErrorViewer.showError(new Exception(),
                    "QuestData : readFile() error, file %s contains the invalid ai deck name: %s", file,
                    list.get(i));*/
            if (!aiDecks.contains(list.get(i).toString())) {
                aiDecks.add(list.get(i).toString());
            }


        return list;
    }//readFile()

    /**
     * <p>loadData.</p>
     *
     * @return a {@link forge.QuestData} object.
     */
    static public QuestData loadData() {
        try {
            //read file "questData"
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(ForgeProps.getFile(QUEST.DATA)));
            Object o = in.readObject();
            in.close();

            QuestData_State state = (QuestData_State) o;

            QuestData data = new QuestData();

            data.win = state.win;
            data.lost = state.lost;
            data.credits = state.credits;
            data.rankIndex = state.rankIndex;
            data.difficulty = state.difficulty;

            data.mode = state.mode;
            if (data.mode == null)
                data.mode = REALISTIC;

            data.plantLevel = state.plantLevel;
            data.wolfPetLevel = state.wolfPetLevel;
            data.crocPetLevel = state.crocPetLevel;
            data.birdPetLevel = state.birdPetLevel;
            data.houndPetLevel = state.houndPetLevel;
            data.selectedPet = state.selectedPet;
            data.life = state.life;
            data.estatesLevel = state.estatesLevel;
            data.luckyCoinLevel = state.luckyCoinLevel;
            data.sleightOfHandLevel = state.sleightOfHandLevel;
            data.gearLevel = state.gearLevel;
            data.questsPlayed = state.questsPlayed;
            data.availableQuests = state.availableQuests;
            data.completedQuests = state.completedQuests;

            data.shopList = state.shopList;
            data.cardPool = state.cardPool;
            data.myDecks = state.myDecks;
            data.aiDecks = state.aiDecks;

            readAIQuestDeckFiles(data, new ArrayList<String>(data.aiDecks.keySet()));

            return data;
        }//try
        catch (Exception ex) {
            ErrorViewer.showError(ex, "Error loading Quest Data");
            throw new RuntimeException(ex);
        }
    }//loadData()


    //returns Strings of the card names
    /**
     * <p>getCardpool.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getCardpool() {
        //make a copy so the internal ArrrayList cannot be changed externally
        return new ArrayList<String>(cardPool);
    }

    /**
     * <p>Getter for the field <code>shopList</code>.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getShopList() {
        if (shopList != null)
            return new ArrayList<String>(shopList);
        else
            return null;
    }


    /**
     * <p>Getter for the field <code>availableQuests</code>.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<Integer> getAvailableQuests() {
        if (availableQuests != null)
            return new ArrayList<Integer>(availableQuests);
        else
            return null;
    }

    /**
     * <p>Getter for the field <code>completedQuests</code>.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<Integer> getCompletedQuests() {
        if (completedQuests != null)
            return new ArrayList<Integer>(completedQuests);
        else
            return null;
    }
    
    //this Deck object is a Constructed deck
    //deck.getDeckType() is Constant.GameType.Sealed
    //sealed since the card pool is the Deck sideboard
    /**
     * <p>getDeck.</p>
     *
     * @param deckName a {@link java.lang.String} object.
     * @return a {@link forge.deck.Deck} object.
     */
    public Deck getDeck(String deckName) {
        //have to always set the card pool aka the Deck sideboard
        //because new cards may have been added to the card pool by addCards()

        //this sets the cards in Deck main
        Deck d = getDeckFromMap(myDecks, deckName);

        //below is probably not needed

        //remove old cards from card pool
        for (int i = 0; i < d.countSideboard(); i++)
            d.removeSideboard(i);

        //add all cards to card pool
        for (int i = 0; i < cardPool.size(); i++)
            d.addSideboard(cardPool.get(i).toString());

        return d;
    }

    /**
     * <p>getDeckFromMap.</p>
     *
     * @param map a {@link java.util.Map} object.
     * @param deckName a {@link java.lang.String} object.
     * @return a {@link forge.deck.Deck} object.
     */
    private Deck getDeckFromMap(Map<String, Deck> map, String deckName) {
        if (!map.containsKey(deckName)) ErrorViewer.showError(new Exception(),
                "QuestData : getDeckFromMap(String deckName) error, deck name not found - %s", deckName);

        return map.get(deckName);
    }
    
    //returns human player decks
    //returns ArrayList of String deck names
    /**
     * <p>getDeckNames.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getDeckNames() {
        return getDeckNames_String(myDecks);
    }//getDecks()

    //returns ArrayList of Deck String names
    /**
     * <p>getDeckNames_String.</p>
     *
     * @param map a {@link java.util.Map} object.
     * @return a {@link java.util.ArrayList} object.
     */
    private ArrayList<String> getDeckNames_String(Map<String, Deck> map) {
        ArrayList<String> out = new ArrayList<String>();

        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext())
            out.add(it.next().toString());

        return out;
    }
    
    //get new cards that were added to your card pool by addCards()
    /**
     * <p>getAddedCards.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getAddedCards() {
        return new ArrayList<String>(newCardList);
    }

    /**
     * <p>Getter for the field <code>win</code>.</p>
     *
     * @return a int.
     */
    public int getWin() {
        return win;
    }

    /**
     * <p>Getter for the field <code>lost</code>.</p>
     *
     * @return a int.
     */
    public int getLost() {
        return lost;
    }

    //********************FANTASY STUFF START***********************

    /**
     * <p>Getter for the field <code>plantLevel</code>.</p>
     *
     * @return a int.
     */
    public int getPlantLevel() {
        return plantLevel;
    }

    /**
     * <p>Getter for the field <code>wolfPetLevel</code>.</p>
     *
     * @return a int.
     */
    public int getWolfPetLevel() {
        return wolfPetLevel;
    }

    /**
     * <p>Getter for the field <code>crocPetLevel</code>.</p>
     *
     * @return a int.
     */
    public int getCrocPetLevel() {
        return crocPetLevel;
    }

    /**
     * <p>Getter for the field <code>birdPetLevel</code>.</p>
     *
     * @return a int.
     */
    public int getBirdPetLevel() {
        return birdPetLevel;
    }

    /**
     * <p>Getter for the field <code>houndPetLevel</code>.</p>
     *
     * @return a int.
     */
    public int getHoundPetLevel() {
        return houndPetLevel;
    }

    /**
     * <p>Getter for the field <code>life</code>.</p>
     *
     * @return a int.
     */
    public int getLife() {
        return life;
    }

    /**
     * <p>Getter for the field <code>estatesLevel</code>.</p>
     *
     * @return a int.
     */
    public int getEstatesLevel() {
        return estatesLevel;
    }

    /**
     * <p>Getter for the field <code>luckyCoinLevel</code>.</p>
     *
     * @return a int.
     */
    public int getLuckyCoinLevel() {
        return luckyCoinLevel;
    }

    /**
     * <p>Getter for the field <code>sleightOfHandLevel</code>.</p>
     *
     * @return a int.
     */
    public int getSleightOfHandLevel() {
        return sleightOfHandLevel;
    }

    /**
     * <p>Getter for the field <code>gearLevel</code>.</p>
     *
     * @return a int.
     */
    public int getGearLevel() {
        return gearLevel;
    }

    /**
     * <p>Getter for the field <code>questsPlayed</code>.</p>
     *
     * @return a int.
     */
    public int getQuestsPlayed() {
        return questsPlayed;
    }

    //********************FANTASY STUFF END***********************
    /**
     * <p>Getter for the field <code>credits</code>.</p>
     *
     * @return a long.
     */
    public long getCredits() {
        return credits;
    }

    /**
     * <p>Getter for the field <code>mode</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMode() {
        if (mode == null)
            return "";
        return mode;
    }

    //should be called first, because the difficultly won't change
    /**
     * <p>Getter for the field <code>difficulty</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * <p>Getter for the field <code>diffIndex</code>.</p>
     *
     * @return a int.
     */
    public int getDiffIndex() {
        return diffIndex;
    }
}
