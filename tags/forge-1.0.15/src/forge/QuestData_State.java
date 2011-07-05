package forge;


/**
 * QuestData_State.java
 *
 * Created on 26.10.2009
 */


import forge.deck.Deck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


/**
 * The class QuestData_State.
 *
 * @author Clemens Koza
 * @version V0.0 26.10.2009
 */


@Deprecated
public class QuestData_State implements Serializable {
    /** Constant <code>serialVersionUID=7007940230351051937L</code> */
    private static final long serialVersionUID = 7007940230351051937L;

    int rankIndex, win, lost;
    int plantLevel, wolfPetLevel, crocPetLevel, birdPetLevel, houndPetLevel, life, estatesLevel, luckyCoinLevel, sleightOfHandLevel, gearLevel, questsPlayed;
    long credits;
    String difficulty, mode, selectedPet;

    ArrayList<Integer> availableQuests, completedQuests;
    ArrayList<String> cardPool, shopList;
    Map<String, Deck> myDecks, aiDecks;

    /**
     * <p>Constructor for QuestData_State.</p>
     */
    public QuestData_State() {
    }

    /**
     * This constructor is used by QestData_State in the default package to create a replacement object for the
     * obsolete class.
     *
     * @param rankIndex a int.
     * @param win a int.
     * @param lost a int.
     * @param plantLevel a int.
     * @param wolfPetLevel a int.
     * @param crocPetLevel a int.
     * @param birdPetLevel a int.
     * @param houndPetLevel a int.
     * @param selectedPet a {@link java.lang.String} object.
     * @param life a int.
     * @param estatesLevel a int.
     * @param luckyCoinLevel a int.
     * @param sleightOfHandLevel a int.
     * @param gearLevel a int.
     * @param questsPlayed a int.
     * @param availableQuests a {@link java.util.ArrayList} object.
     * @param completedQuests a {@link java.util.ArrayList} object.
     * @param credits a long.
     * @param difficulty a {@link java.lang.String} object.
     * @param mode a {@link java.lang.String} object.
     * @param cardPool a {@link java.util.ArrayList} object.
     * @param shopList a {@link java.util.ArrayList} object.
     * @param myDecks a {@link java.util.Map} object.
     * @param aiDecks a {@link java.util.Map} object.
     */
    public QuestData_State(int rankIndex, int win, int lost, int plantLevel, int wolfPetLevel, int crocPetLevel, int birdPetLevel, int houndPetLevel, String selectedPet, int life, int estatesLevel, int luckyCoinLevel, int sleightOfHandLevel, int gearLevel, int questsPlayed,
                           ArrayList<Integer> availableQuests, ArrayList<Integer> completedQuests, long credits, String difficulty, String mode,
                           ArrayList<String> cardPool, ArrayList<String> shopList, Map<String, Deck> myDecks, Map<String, Deck> aiDecks) {
        this.rankIndex = rankIndex;
        this.win = win;
        this.lost = lost;
        this.plantLevel = plantLevel;
        this.wolfPetLevel = wolfPetLevel;
        this.crocPetLevel = crocPetLevel;
        this.birdPetLevel = birdPetLevel;
        this.houndPetLevel = houndPetLevel;
        this.life = life;
        this.estatesLevel = estatesLevel;
        this.luckyCoinLevel = luckyCoinLevel;
        this.sleightOfHandLevel = sleightOfHandLevel;
        this.gearLevel = gearLevel;
        this.questsPlayed = questsPlayed;
        this.availableQuests = availableQuests;
        this.completedQuests = completedQuests;
        this.credits = credits;
        this.difficulty = difficulty;
        this.mode = mode;
        this.cardPool = cardPool;
        this.shopList = shopList;
        this.myDecks = myDecks;
        this.aiDecks = aiDecks;
    }
}
