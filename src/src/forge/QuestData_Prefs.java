package forge;

import forge.properties.ForgeProps;
import forge.properties.NewConstants.QUEST;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;

@Deprecated
/**
 * <p>QuestData_Prefs class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class QuestData_Prefs implements Serializable {
    /** Constant <code>serialVersionUID=3266336025656577905L</code> */
    private static final long serialVersionUID = 3266336025656577905L;

    private int numDiff = 4;

    // Descriptive difficulty names
    private String[] sDifficulty = {"Easy", "Normal", "Hard", "Very Hard"};

    // Default match wins it takes to gain a booster
    private int[] winsForBooster = {1, 1, 2, 2};
    private int[] winsForRankIncrease = {1, 2, 3, 4};
    private int[] winsForMediumAI = {6, 6, 11, 11};
    private int[] winsForHardAI = {9, 9, 21, 21};
    private int[] winsForVeryHardAI = {29, 29, 31, 31};

    // Default starting land for a quest
    private int startingBasicLand = 20;
    private int startingSnowBasicLand = 20;

    // Default starting amount of each rarity
    private int[] startingCommons = {45, 40, 40, 40};
    private int[] startingUncommons = {20, 15, 15, 15};
    private int[] startingRares = {10, 10, 10, 10};

    private int startingCredits = 250;

    private int boosterPackRare = 1;
    private int boosterPackUncommon = 3;
    private int boosterPackCommon = 9;

    private int matchRewardBase = 10;
    private double matchRewardTotalWins = 0.3;
    private int matchRewardNoLosses = 10;

    private int matchRewardPoisonWinBonus = 50;
    private int matchRewardMilledWinBonus = 40;
    private int matchRewardAltWinBonus = 100;

    private int matchRewardWinOnFirstTurn = 1500;
    private int matchRewardWinByTurnFive = 250;
    private int matchRewardWinByTurnTen = 50;
    private int matchRewardWinByTurnFifteen = 5;
    private int matchRewardMullToZero = 500;

    /**
     * <p>Constructor for QuestData_Prefs.</p>
     */
    public QuestData_Prefs() {
        // if quest.prefs exists
        grabPrefsFromFile();
    }

    /**
     * <p>grabPrefsFromFile.</p>
     */
    public void grabPrefsFromFile() {
        try {
            BufferedReader input = new BufferedReader(new FileReader(ForgeProps.getFile(QUEST.PREFS)));
            String line = null;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("#") || line.length() == 0)
                    continue;
                String[] split = line.split("=");

                if (split[0].equals("difficultyString"))
                    setDifficulty(split[1]);
                else if (split[0].equals("winsForBooster"))
                    setWinsForBooster(split[1]);
                else if (split[0].equals("winsForRankIncrease"))
                    setWinsForRank(split[1]);
                else if (split[0].equals("winsForMediumAI"))
                    setWinsForMediumAI(split[1]);
                else if (split[0].equals("winsForHardAI"))
                    setWinsForHardAI(split[1]);
                else if (split[0].equals("winsForVeryHardAI"))
                    setWinsForHardAI(split[1]);
                else if (split[0].equals("startingBasicLand"))
                    setStartingBasic(split[1]);
                else if (split[0].equals("startingSnowBasicLand"))
                    setStartingSnowBasic(split[1]);
                else if (split[0].equals("startingCommons"))
                    setStartingCommons(split[1]);
                else if (split[0].equals("startingUncommons"))
                    setStartingUncommons(split[1]);
                else if (split[0].equals("startingRares"))
                    setStartingRares(split[1]);
                else if (split[0].equals("startingCredits"))
                    setStartingCredits(split[1]);
                else if (split[0].equals("boosterPackCommon"))
                    setNumCommon(split[1]);
                else if (split[0].equals("boosterPackUncommon"))
                    setNumUncommon(split[1]);
                else if (split[0].equals("boosterPackRare"))
                    setNumRares(split[1]);
                else if (split[0].equals("matchRewardBase"))
                    setMatchRewardBase(split[1]);
                else if (split[0].equals("matchRewardTotalWins"))
                    setMatchRewardTotalWins(split[1]);
                else if (split[0].equals("matchRewardNoLosses"))
                    setMatchRewardNoLosses(split[1]);
                else if (split[0].equals("matchRewardMilledWinBonus"))
                    setMatchRewardMilledWinBonus(split[1]);
                else if (split[0].equals("matchRewardPoisonWinBonus"))
                    setMatchRewardPoisonWinBonus(split[1]);
                else if (split[0].equals("matchRewardAltWinBonus"))
                    setMatchRewardAltWinBonus(split[1]);
                else if (split[0].equals("matchRewardWinOnFirstTurn"))
                    setMatchRewardWinFirst(split[1]);
                else if (split[0].equals("matchRewardWinByTurnFive"))
                    setMatchRewardWinByFifth(split[1]);
                else if (split[0].equals("matchRewardWinByTurnTen"))
                    setMatchRewardWinByTen(split[1]);
                else if (split[0].equals("matchRewardWinByTurnFifteen"))
                    setMatchRewardWinByFifteen(split[1]);
                else if (split[0].equals("matchRewardMullToZero"))
                    setMatchMullToZero(split[1]);
            }
        } catch (Exception e) {
            System.out.println("Trouble grabbing quest data preferences. Using default values.");
        }
    }

    // getters
    /**
     * <p>getDifficulty.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getDifficulty() {
        return sDifficulty;
    }

    /**
     * <p>getDifficulty.</p>
     *
     * @param index a int.
     * @return a {@link java.lang.String} object.
     */
    public String getDifficulty(int index) {
        return sDifficulty[index];
    }

    /**
     * <p>Getter for the field <code>winsForBooster</code>.</p>
     *
     * @param index a int.
     * @return a int.
     */
    public int getWinsForBooster(int index) {
        return winsForBooster[index];
    }

    /**
     * <p>Getter for the field <code>winsForRankIncrease</code>.</p>
     *
     * @param index a int.
     * @return a int.
     */
    public int getWinsForRankIncrease(int index) {
        return winsForRankIncrease[index];
    }

    /**
     * <p>Getter for the field <code>winsForMediumAI</code>.</p>
     *
     * @param index a int.
     * @return a int.
     */
    public int getWinsForMediumAI(int index) {
        return winsForMediumAI[index];
    }

    /**
     * <p>Getter for the field <code>winsForHardAI</code>.</p>
     *
     * @param index a int.
     * @return a int.
     */
    public int getWinsForHardAI(int index) {
        return winsForHardAI[index];
    }

    /**
     * <p>Getter for the field <code>winsForVeryHardAI</code>.</p>
     *
     * @param index a int.
     * @return a int.
     */
    public int getWinsForVeryHardAI(int index) {
        return winsForVeryHardAI[index];
    }

    /**
     * <p>getStartingBasic.</p>
     *
     * @return a int.
     */
    public int getStartingBasic() {
        return startingBasicLand;
    }

    /**
     * <p>getStartingSnowBasic.</p>
     *
     * @return a int.
     */
    public int getStartingSnowBasic() {
        return startingSnowBasicLand;
    }

    /**
     * <p>Getter for the field <code>startingCommons</code>.</p>
     *
     * @param index a int.
     * @return a int.
     */
    public int getStartingCommons(int index) {
        return startingCommons[index];
    }

    /**
     * <p>Getter for the field <code>startingUncommons</code>.</p>
     *
     * @param index a int.
     * @return a int.
     */
    public int getStartingUncommons(int index) {
        return startingUncommons[index];
    }

    /**
     * <p>Getter for the field <code>startingRares</code>.</p>
     *
     * @param index a int.
     * @return a int.
     */
    public int getStartingRares(int index) {
        return startingRares[index];
    }

    /**
     * <p>Getter for the field <code>startingCredits</code>.</p>
     *
     * @return a int.
     */
    public int getStartingCredits() {
        return startingCredits;
    }

    /**
     * <p>getNumCommon.</p>
     *
     * @return a int.
     */
    public int getNumCommon() {
        return boosterPackCommon;
    }

    /**
     * <p>getNumUncommon.</p>
     *
     * @return a int.
     */
    public int getNumUncommon() {
        return boosterPackUncommon;
    }

    /**
     * <p>getNumRare.</p>
     *
     * @return a int.
     */
    public int getNumRare() {
        return boosterPackRare;
    }


    /**
     * <p>Getter for the field <code>matchRewardBase</code>.</p>
     *
     * @return a int.
     */
    public int getMatchRewardBase() {
        return matchRewardBase;
    }

    /**
     * <p>Getter for the field <code>matchRewardTotalWins</code>.</p>
     *
     * @return a double.
     */
    public double getMatchRewardTotalWins() {
        return matchRewardTotalWins;
    }

    /**
     * <p>Getter for the field <code>matchRewardNoLosses</code>.</p>
     *
     * @return a int.
     */
    public int getMatchRewardNoLosses() {
        return matchRewardNoLosses;
    }

    /**
     * <p>Getter for the field <code>matchRewardPoisonWinBonus</code>.</p>
     *
     * @return a int.
     */
    public int getMatchRewardPoisonWinBonus() {
        return matchRewardPoisonWinBonus;
    }

    /**
     * <p>Getter for the field <code>matchRewardMilledWinBonus</code>.</p>
     *
     * @return a int.
     */
    public int getMatchRewardMilledWinBonus() {
        return matchRewardMilledWinBonus;
    }

    /**
     * <p>Getter for the field <code>matchRewardAltWinBonus</code>.</p>
     *
     * @return a int.
     */
    public int getMatchRewardAltWinBonus() {
        return matchRewardAltWinBonus;
    }


    /**
     * <p>getMatchRewardWinFirst.</p>
     *
     * @return a int.
     */
    public int getMatchRewardWinFirst() {
        return matchRewardWinOnFirstTurn;
    }

    /**
     * <p>getMatchRewardWinByFifth.</p>
     *
     * @return a int.
     */
    public int getMatchRewardWinByFifth() {
        return matchRewardWinByTurnFive;
    }

    /**
     * <p>getMatchRewardWinByTen.</p>
     *
     * @return a int.
     */
    public int getMatchRewardWinByTen() {
        return matchRewardWinByTurnTen;
    }

    /**
     * <p>getMatchRewardWinByFifteen.</p>
     *
     * @return a int.
     */
    public int getMatchRewardWinByFifteen() {
        return matchRewardWinByTurnFifteen;
    }

    /**
     * <p>getMatchMullToZero.</p>
     *
     * @return a int.
     */
    public int getMatchMullToZero() {
        return matchRewardMullToZero;
    }


    // setters
    /**
     * <p>setDifficulty.</p>
     *
     * @param diff a {@link java.lang.String} object.
     */
    public void setDifficulty(String diff) {
        this.sDifficulty = diff.split(",");
    }

    /**
     * <p>Setter for the field <code>winsForBooster</code>.</p>
     *
     * @param wins a {@link java.lang.String} object.
     */
    public void setWinsForBooster(String wins) {
        String[] winsStr = wins.split(",");

        for (int i = 0; i < numDiff; i++)
            this.winsForBooster[i] = Integer.parseInt(winsStr[i]);
    }

    /**
     * <p>setWinsForRank.</p>
     *
     * @param wins a {@link java.lang.String} object.
     */
    public void setWinsForRank(String wins) {
        String[] winsStr = wins.split(",");

        for (int i = 0; i < numDiff; i++)
            this.winsForRankIncrease[i] = Integer.parseInt(winsStr[i]);
    }

    /**
     * <p>Setter for the field <code>winsForMediumAI</code>.</p>
     *
     * @param wins a {@link java.lang.String} object.
     */
    public void setWinsForMediumAI(String wins) {
        String[] winsStr = wins.split(",");

        for (int i = 0; i < numDiff; i++)
            this.winsForMediumAI[i] = Integer.parseInt(winsStr[i]);
    }

    /**
     * <p>Setter for the field <code>winsForHardAI</code>.</p>
     *
     * @param wins a {@link java.lang.String} object.
     */
    public void setWinsForHardAI(String wins) {
        String[] winsStr = wins.split(",");

        for (int i = 0; i < numDiff; i++)
            this.winsForHardAI[i] = Integer.parseInt(winsStr[i]);
    }

    /**
     * <p>setStartingBasic.</p>
     *
     * @param land a {@link java.lang.String} object.
     */
    public void setStartingBasic(String land) {
        this.startingBasicLand = Integer.parseInt(land);
    }

    /**
     * <p>setStartingSnowBasic.</p>
     *
     * @param land a {@link java.lang.String} object.
     */
    public void setStartingSnowBasic(String land) {
        this.startingSnowBasicLand = Integer.parseInt(land);
    }

    /**
     * <p>Setter for the field <code>startingCommons</code>.</p>
     *
     * @param rarity a {@link java.lang.String} object.
     */
    public void setStartingCommons(String rarity) {
        String[] splitStr = rarity.split(",");

        for (int i = 0; i < numDiff; i++)
            this.startingCommons[i] = Integer.parseInt(splitStr[i]);
    }

    /**
     * <p>Setter for the field <code>startingUncommons</code>.</p>
     *
     * @param rarity a {@link java.lang.String} object.
     */
    public void setStartingUncommons(String rarity) {
        String[] splitStr = rarity.split(",");

        for (int i = 0; i < numDiff; i++)
            this.startingUncommons[i] = Integer.parseInt(splitStr[i]);
    }

    /**
     * <p>Setter for the field <code>startingRares</code>.</p>
     *
     * @param rarity a {@link java.lang.String} object.
     */
    public void setStartingRares(String rarity) {
        String[] splitStr = rarity.split(",");

        for (int i = 0; i < numDiff; i++)
            this.startingRares[i] = Integer.parseInt(splitStr[i]);
    }

    /**
     * <p>Setter for the field <code>startingCredits</code>.</p>
     *
     * @param credits a {@link java.lang.String} object.
     */
    public void setStartingCredits(String credits) {
        this.startingCredits = Integer.parseInt(credits);
    }

    /**
     * <p>setNumCommon.</p>
     *
     * @param pack a {@link java.lang.String} object.
     */
    public void setNumCommon(String pack) {
        this.boosterPackCommon = Integer.parseInt(pack);
    }

    /**
     * <p>setNumUncommon.</p>
     *
     * @param pack a {@link java.lang.String} object.
     */
    public void setNumUncommon(String pack) {
        this.boosterPackUncommon = Integer.parseInt(pack);
    }

    /**
     * <p>setNumRares.</p>
     *
     * @param pack a {@link java.lang.String} object.
     */
    public void setNumRares(String pack) {
        this.boosterPackRare = Integer.parseInt(pack);
    }


    /**
     * <p>Setter for the field <code>matchRewardBase</code>.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardBase(String match) {
        this.matchRewardBase = Integer.parseInt(match);
    }

    /**
     * <p>Setter for the field <code>matchRewardTotalWins</code>.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardTotalWins(String match) {
        this.matchRewardTotalWins = Double.parseDouble(match);
    }

    /**
     * <p>Setter for the field <code>matchRewardNoLosses</code>.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardNoLosses(String match) {
        this.matchRewardNoLosses = Integer.parseInt(match);
    }

    /**
     * <p>Setter for the field <code>matchRewardPoisonWinBonus</code>.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardPoisonWinBonus(String match) {
        this.matchRewardPoisonWinBonus = Integer.parseInt(match);
    }

    /**
     * <p>Setter for the field <code>matchRewardMilledWinBonus</code>.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardMilledWinBonus(String match) {
        this.matchRewardMilledWinBonus = Integer.parseInt(match);
    }

    /**
     * <p>Setter for the field <code>matchRewardAltWinBonus</code>.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardAltWinBonus(String match) {
        this.matchRewardAltWinBonus = Integer.parseInt(match);
    }


    /**
     * <p>setMatchRewardWinFirst.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardWinFirst(String match) {
        this.matchRewardWinOnFirstTurn = Integer.parseInt(match);
    }

    /**
     * <p>setMatchRewardWinByFifth.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardWinByFifth(String match) {
        this.matchRewardWinByTurnFive = Integer.parseInt(match);
    }

    /**
     * <p>setMatchRewardWinByTen.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardWinByTen(String match) {
        this.matchRewardWinByTurnTen = Integer.parseInt(match);
    }

    /**
     * <p>setMatchRewardWinByFifteen.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchRewardWinByFifteen(String match) {
        this.matchRewardWinByTurnFifteen = Integer.parseInt(match);
    }

    /**
     * <p>setMatchMullToZero.</p>
     *
     * @param match a {@link java.lang.String} object.
     */
    public void setMatchMullToZero(String match) {
        this.matchRewardMullToZero = Integer.parseInt(match);
    }
}
