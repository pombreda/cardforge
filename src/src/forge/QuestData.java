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

    private QuestData_BoosterPack boosterPack = new QuestData_BoosterPack();

    //used by shouldAddAdditionalCards()
    private Random random = MyRandom.random;

    //feel free to change this to something funnier
    private String[] rankArray = {
            "Level 0 - Confused Wizard", "Level 1 - Mana Mage", "Level 2 - Death by Megrim",
            "Level 3 - Shattered the Competition", "Level 4 - Black Knighted", "Level 5 - Shockingly Good",
            "Level 6 - Regressed into Timmy", "Level 7 - Loves Blue Control", "Level 8 - Immobilized by Fear",
            "Level 9 - Lands = Friends", "Saltblasted for your talent", "Serra Angel is your girlfriend",};


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

    //adds cards to card pool and sets difficulty
    /**
     * <p>newGame.</p>
     *
     * @param difficulty a int.
     * @param m a {@link java.lang.String} object.
     */
    public void newGame(int difficulty, String m) {
        setDifficulty(difficulty);

        ArrayList<String> list = new ArrayList<String>();
        list.addAll(boosterPack.getCommon(qdPrefs.getStartingCommons(difficulty)));
        list.addAll(boosterPack.getUncommon(qdPrefs.getStartingUncommons(difficulty)));
        list.addAll(boosterPack.getRare(qdPrefs.getStartingRares(difficulty)));

        //because cardPool already has basic land added to it
        cardPool.addAll(list);
        credits = qdPrefs.getStartingCredits();

        mode = m;
        if (mode.equals(FANTASY))
            life = 15;
        else
            life = 20;
    }


    /**
     * <p>getOpponents.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getOpponents() {
        int index = getDiffIndex();

        if (getWin() < qdPrefs.getWinsForMediumAI(index)) return getOpponents(easyAIDecks);

        if (getWin() < qdPrefs.getWinsForHardAI(index)) return getOpponents(mediumAIDecks);

        return getOpponents(hardAIDecks);
    }//getOpponents()


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
     * <p>refreshAIQuestDeckFiles.</p>
     *
     * @param aiDeckNames a {@link java.util.ArrayList} object.
     */
    public void refreshAIQuestDeckFiles(ArrayList<String> aiDeckNames) {
        easyAIDecks = readFile(ForgeProps.getFile(QUEST.EASY), aiDeckNames);
        mediumAIDecks = readFile(ForgeProps.getFile(QUEST.MEDIUM), aiDeckNames);
        hardAIDecks = readFile(ForgeProps.getFile(QUEST.HARD), aiDeckNames);
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
     * <p>readAIQuestDeckFiles.</p>
     */
    public void readAIQuestDeckFiles() {
        readAIQuestDeckFiles(this, ai_getDeckNames());
    }

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
     * <p>Setter for the field <code>shopList</code>.</p>
     *
     * @param list a {@link java.util.ArrayList} object.
     */
    public void setShopList(ArrayList<String> list) {
        shopList = list;
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
     * <p>Setter for the field <code>availableQuests</code>.</p>
     *
     * @param list a {@link java.util.ArrayList} object.
     */
    public void setAvailableQuests(ArrayList<Integer> list) {
        availableQuests = list;
    }

    /**
     * <p>clearAvailableQuests.</p>
     */
    public void clearAvailableQuests() {
        availableQuests.clear();
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

    /**
     * <p>Setter for the field <code>completedQuests</code>.</p>
     *
     * @param list a {@link java.util.ArrayList} object.
     */
    public void setCompletedQuests(ArrayList<Integer> list) {
        completedQuests = list;
    }


    /**
     * <p>clearShopList.</p>
     */
    public void clearShopList() {
        shopList.clear();
    }

    //rename - removeDeck, addDeck
    //copy - addDeck

    /**
     * <p>removeDeck.</p>
     *
     * @param deckName a {@link java.lang.String} object.
     */
    public void removeDeck(String deckName) {
        myDecks.remove(deckName);
    }

    /**
     * <p>ai_removeDeck.</p>
     *
     * @param deckName a {@link java.lang.String} object.
     */
    public void ai_removeDeck(String deckName) {
        aiDecks.remove(deckName);
    }

    /**
     * <p>addDeck.</p>
     *
     * @param d a {@link forge.deck.Deck} object.
     */
    public void addDeck(Deck d) {
        myDecks.put(d.getName(), d);
    }

    /**
     * <p>ai_addDeck.</p>
     *
     * @param d a {@link forge.deck.Deck} object.
     */
    public void ai_addDeck(Deck d) {
        aiDecks.put(d.getName(), d);
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

    //this Deck object is a Constructed deck
    //deck.getDeckType() is Constant.GameType.Constructed
    //constructed because the computer can use any card
    /**
     * <p>ai_getDeck.</p>
     *
     * @param deckName a {@link java.lang.String} object.
     * @return a {@link forge.deck.Deck} object.
     */
    public Deck ai_getDeck(String deckName) {
        return getDeckFromMap(aiDecks, deckName);
    }

    /**
     * <p>ai_getDeckNewFormat.</p>
     *
     * @param deckName a {@link java.lang.String} object.
     * @return a {@link forge.deck.Deck} object.
     */
    public Deck ai_getDeckNewFormat(String deckName) {
        DeckManager deckManager = new DeckManager(ForgeProps.getFile(QUEST.DECKS));
        return deckManager.getDeck(deckName);
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

    //returns AI computer decks
    //returns ArrayList of String deck names
    /**
     * <p>ai_getDeckNames.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> ai_getDeckNames() {
        return getDeckNames_String(aiDecks);
    }

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

    //get new cards that were added to your card pool by addCards()
    /**
     * <p>addToNewList.</p>
     *
     * @param added a {@link java.util.ArrayList} object.
     */
    public void addToNewList(ArrayList<String> added) {
        newCardList.addAll(added);
    }

    //adds 11 cards, to the current card pool
    //(I chose 11 cards instead of 15 in order to make things more challenging)
    /**
     * <p>addCards.</p>
     */
    public void addCards() {
        int nCommon = qdPrefs.getNumCommon();
        int nUncommon = qdPrefs.getNumUncommon();
        int nRare = qdPrefs.getNumRare();

        ArrayList<String> newCards = new ArrayList<String>();
        newCards.addAll(boosterPack.getCommon(nCommon));
        newCards.addAll(boosterPack.getUncommon(nUncommon));
        newCards.addAll(boosterPack.getRare(nRare));


        cardPool.addAll(newCards);

        //getAddedCards() uses newCardList
        newCardList = newCards;

    }//addCards()

    /**
     * <p>addRandomRare.</p>
     *
     * @param n a int.
     */
    public void addRandomRare(int n) {
        ArrayList<String> newCards = new ArrayList<String>();
        newCards.addAll(boosterPack.getRare(n));

        cardPool.addAll(newCards);
        newCardList.addAll(newCards);
    }

    /**
     * <p>addRandomRare.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String addRandomRare() {

        ArrayList<String> newCards = new ArrayList<String>();
        newCards.addAll(boosterPack.getRare(1));

        cardPool.addAll(newCards);
        newCardList.addAll(newCards);

        Card c = AllZone.getCardFactory().getCard(newCards.get(0), AllZone.getHumanPlayer());
        c.setCurSetCode(c.getMostRecentSet());
        return CardUtil.buildFilename(c);
        //return GuiDisplayUtil.cleanString(newCards.get(0));
    }

    /**
     * <p>addCard.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public void addCard(Card c) {
        cardPool.add(c.getName());
    }

    /**
     * <p>addCard.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public void addCard(String s) {
        cardPool.add(s);
    }

    /**
     * <p>removeCard.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public void removeCard(Card c) {

        String s = c.getName();
        if (!cardPool.contains(s))
            return;

        for (int i = 0; i < cardPool.size(); i++) {
            String str = cardPool.get(i);
            if (str.equals(s)) {
                cardPool.remove(i);
                break;
            }
        }
    }

    /**
     * <p>addCardToShopList.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public void addCardToShopList(Card c) {
        shopList.add(c.getName());
    }

    /**
     * <p>removeCardFromShopList.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public void removeCardFromShopList(Card c) {
        String s = c.getName();
        if (!shopList.contains(s))
            return;

        for (int i = 0; i < shopList.size(); i++) {
            String str = shopList.get(i);
            if (str.equals(s)) {
                shopList.remove(i);
                break;
            }
        }
    }


    //gets all of the cards that are in the cardpool
    /**
     * <p>getCards.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getCards() {
        //copy CardList in order to keep private variables private
        //if we just return cardPool, it could be changed externally
        return new ArrayList<String>(cardPool);
    }


    /**
     * <p>getTotalNumberOfGames.</p>
     *
     * @param difficulty a int.
     * @return a int.
     */
    public int getTotalNumberOfGames(int difficulty) {
        //-2 because you start a level 1, and the last level is secret
        int numberLevels = rankArray.length - 2;
        int nMatches = qdPrefs.getWinsForRankIncrease(difficulty);

        return numberLevels * nMatches;
    }

    //this changes getRank()
    /**
     * <p>addWin.</p>
     */
    public void addWin() {
        win++;

        if (win % qdPrefs.getWinsForRankIncrease(diffIndex) == 0) rankIndex++;
    }//addWin()

    /**
     * <p>addLost.</p>
     */
    public void addLost() {
        lost++;
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
     * <p>addPlantLevel.</p>
     */
    public void addPlantLevel() {
        plantLevel++;
    }

    /**
     * <p>Getter for the field <code>plantLevel</code>.</p>
     *
     * @return a int.
     */
    public int getPlantLevel() {
        return plantLevel;
    }

    /**
     * <p>addWolfPetLevel.</p>
     */
    public void addWolfPetLevel() {
        wolfPetLevel++;
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
     * <p>addCrocPetLevel.</p>
     */
    public void addCrocPetLevel() {
        crocPetLevel++;
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
     * <p>addBirdPetLevel.</p>
     */
    public void addBirdPetLevel() {
        birdPetLevel++;
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
     * <p>addHoundPetLevel.</p>
     */
    public void addHoundPetLevel() {
        houndPetLevel++;
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
     * <p>Setter for the field <code>selectedPet</code>.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public void setSelectedPet(String s) {
        selectedPet = s;
    }

    /**
     * <p>Getter for the field <code>selectedPet</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSelectedPet() {
        return selectedPet;
    }


    /**
     * <p>Setter for the field <code>life</code>.</p>
     *
     * @param n a int.
     */
    public void setLife(int n) {
        life = n;
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
     * <p>addLife.</p>
     *
     * @param n a int.
     */
    public void addLife(int n) {
        life += n;
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
     * <p>addEstatesLevel.</p>
     *
     * @param n a int.
     */
    public void addEstatesLevel(int n) {
        estatesLevel += n;
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
     * <p>addLuckyCoinLevel.</p>
     *
     * @param n a int.
     */
    public void addLuckyCoinLevel(int n) {
        luckyCoinLevel += n;
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
     * <p>addSleightOfHandLevel.</p>
     *
     * @param n a int.
     */
    public void addSleightOfHandLevel(int n) {
        sleightOfHandLevel += n;
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
     * <p>addGearLevel.</p>
     *
     * @param n a int.
     */
    public void addGearLevel(int n) {
        gearLevel += n;
    }

    /**
     * <p>Getter for the field <code>questsPlayed</code>.</p>
     *
     * @return a int.
     */
    public int getQuestsPlayed() {
        return questsPlayed;
    }

    /**
     * <p>addQuestsPlayed.</p>
     */
    public void addQuestsPlayed() {
        questsPlayed++;
    }

    //********************FANTASY STUFF END***********************

    /**
     * <p>addCredits.</p>
     *
     * @param c a long.
     */
    public void addCredits(long c) {
        credits += c;
    }

    /**
     * <p>subtractCredits.</p>
     *
     * @param c a long.
     */
    public void subtractCredits(long c) {
        credits -= c;
        if (credits < 0)
            credits = 0;
    }

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

    /**
     * <p>Setter for the field <code>difficulty</code>.</p>
     *
     * @param i a int.
     */
    public void setDifficulty(int i) {
        diffIndex = i;
        difficulty = qdPrefs.getDifficulty(i);
    }

    /**
     * <p>setDifficultyIndex.</p>
     */
    public void setDifficultyIndex() {
        String[] diffStr = qdPrefs.getDifficulty();
        for (int i = 0; i < diffStr.length; i++)
            if (difficulty.equals(diffStr[i]))
                diffIndex = i;
    }

    /**
     * <p>getDifficutlyChoices.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getDifficutlyChoices() {
        return qdPrefs.getDifficulty();
    }

    /**
     * <p>getRank.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRank() {
        //is rankIndex too big?
        if (rankIndex == rankArray.length) rankIndex--;

        return rankArray[rankIndex];
    }

    //add cards after a certain number of wins or losses
    /**
     * <p>shouldAddCards.</p>
     *
     * @param didWin a boolean.
     * @return a boolean.
     */
    public boolean shouldAddCards(boolean didWin) {
        int n = qdPrefs.getWinsForBooster(diffIndex);

        if (didWin) return getWin() % n == 0;
        else return getLost() % n == 0;
    }

    /**
     * <p>shouldAddAdditionalCards.</p>
     *
     * @param didWin a boolean.
     * @return a boolean.
     */
    public boolean shouldAddAdditionalCards(boolean didWin) {
        float chance = 0.5f;
        if (getLuckyCoinLevel() >= 1)
            chance = 0.65f;

        float r = random.nextFloat();
        Log.debug("Random:" + r);

        if (didWin) return r <= chance;

        else return false;
    }


    //opponentName is one of the Strings returned by getOpponents()
    /**
     * <p>getOpponentDeck.</p>
     *
     * @param opponentName a {@link java.lang.String} object.
     * @return a {@link forge.deck.Deck} object.
     */
    public Deck getOpponentDeck(String opponentName) {
        return null;
    }

    /**
     * <p>hasSaveFile.</p>
     *
     * @return a boolean.
     */
    public boolean hasSaveFile() {
        //File f = new File(this.saveFileName); // The static field QuestData.saveFileName should be accessed in a static way
        // No warning is given for it below in getBackupFilename
        return ForgeProps.getFile(QUEST.DATA).exists();
    }

    //returns somethings like "questData-10"
    //find a new filename
    /**
     * <p>getBackupFilename.</p>
     *
     * @return a {@link java.io.File} object.
     */
    @SuppressWarnings("unused")
    static private File getBackupFilename() {
        //I made this a long because maybe an int would overflow, but who knows
        File original = ForgeProps.getFile(QUEST.DATA);
        File parent = original.getParentFile();
        String name = original.getName();
        long n = 1;

        File f;
        while ((f = new File(parent, name + "-" + n)).exists())
            n++;

        return f;
    }//getBackupFilename()

    /**
     * <p>saveData.</p>
     *
     * @param q a {@link forge.QuestData} object.
     */
    static public void saveData(QuestData q) {
        try {
            /*	
                  //rename previous file "questData" to something like questData-23
                  //just in case there is an error when playing the game or saving
                  File file = new File(saveFileName);
                  if(file.exists())
                    file.renameTo(getBackupFilename());
            */

            //setup QuestData_State
            QuestData_State state = new QuestData_State();
            state.win = q.win;
            state.lost = q.lost;
            state.credits = q.credits;
            state.difficulty = q.difficulty;
            state.mode = q.mode;
            state.rankIndex = q.rankIndex;

            state.plantLevel = q.plantLevel;
            state.wolfPetLevel = q.wolfPetLevel;
            state.crocPetLevel = q.crocPetLevel;
            state.birdPetLevel = q.birdPetLevel;
            state.houndPetLevel = q.houndPetLevel;
            state.selectedPet = q.selectedPet;
            state.life = q.life;
            state.estatesLevel = q.estatesLevel;
            state.luckyCoinLevel = q.luckyCoinLevel;
            state.sleightOfHandLevel = q.sleightOfHandLevel;
            state.gearLevel = q.gearLevel;
            state.questsPlayed = q.questsPlayed;
            state.availableQuests = q.availableQuests;

            state.cardPool = q.cardPool;
            state.shopList = q.shopList;
            state.myDecks = q.myDecks;
            state.aiDecks = q.aiDecks;

            //write object
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ForgeProps.getFile(QUEST.DATA)));
            out.writeObject(state);
            out.flush();
            out.close();
        } catch (Exception ex) {
            ErrorViewer.showError(ex, "Error saving Quest Data");
            throw new RuntimeException(ex);
        }
    }//saveData()

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        QuestData q = new QuestData();
        for (int i = 0; i < 20; i++)
            q.addCards();

        for (int i = 0; i < 10; i++) {
            QuestData.saveData(q);
            QuestData.loadData();
        }

        System.exit(1);
    }


}
