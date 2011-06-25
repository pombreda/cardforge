package forge;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;

import forge.properties.NewConstants;

import java.util.*;

//balance the number of colors and creature/spells for
//new cards that are won in quest mode
@Deprecated
/**
 * <p>QuestData_BoosterPack class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class QuestData_BoosterPack implements NewConstants {
//    final private static String      comment          = "//";

    private ArrayList<String> commonCreature = new ArrayList<String>();
    private ArrayList<String> commonSpell = new ArrayList<String>();

    private ArrayList<String> uncommonCreature = new ArrayList<String>();
    private ArrayList<String> uncommonSpell = new ArrayList<String>();

    private ArrayList<String> rareCreature = new ArrayList<String>();
    private ArrayList<String> rareSpell = new ArrayList<String>();

    private Random random = MyRandom.random;

    private String[] colors = {
            "gold", Constant.Color.Colorless, Constant.Color.Black, Constant.Color.Blue, Constant.Color.Green,
            Constant.Color.Red, Constant.Color.White,

            //repeat colors in order to make Colorless and gold not as popular
            Constant.Color.Black, Constant.Color.Blue, Constant.Color.Green, Constant.Color.Red,
            Constant.Color.White};


    //testing
    private ArrayList<String> colorTest = new ArrayList<String>();

    //prints statistics to make sure everything is working
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        QuestData_BoosterPack q = new QuestData_BoosterPack();

        for (int i = 0; i < 100; i++)
            q.print(q.getNewCards(6, 3, 1));

        q.colorStats();
    }//main()

    //testing
    //nCommon is the number of common cards
    /**
     * <p>getNewCards.</p>
     *
     * @param nCommon a int.
     * @param nUncommon a int.
     * @param nRare a int.
     * @return a {@link java.util.ArrayList} object.
     */
    private ArrayList<String> getNewCards(int nCommon, int nUncommon, int nRare) {
        ArrayList<String> out = new ArrayList<String>();

        out.addAll(getCommon(nCommon));
        out.addAll(getUncommon(nUncommon));
        out.addAll(getRare(nRare));

        return out;
    }//getNewCards()

    //testing
    /**
     * <p>print.</p>
     *
     * @param list a {@link java.util.ArrayList} object.
     */
    private void print(ArrayList<String> list) {
        int nCreature = 0;
        int nSpell = 0;

        Card c;
        for (int i = 0; i < list.size(); i++) {
            c = AllZone.getCardFactory().getCard(list.get(i).toString(), null);

            if (c.isCreature()) nCreature++;
            else nSpell++;

            colorTest.add(getColor(c));
        }//for
        System.out.println("creatures " + nCreature + " -  noncreatures " + nSpell);
    }//print()

    //testing
    /**
     * <p>colorStats.</p>
     */
    private void colorStats() {
        String[] colors = {
                "gold", Constant.Color.Colorless, Constant.Color.Black, Constant.Color.Blue, Constant.Color.Green,
                Constant.Color.Red, Constant.Color.White};

        for (int outer = 0; outer < colors.length; outer++) {
            int n = 0;

            for (int z = 0; z < colorTest.size(); z++) {
                if (colorTest.get(z).equals(colors[outer])) n++;
            }
            System.out.println(colors[outer] + " " + n);
        }
    }//colorStats()

    /**
     * <p>Constructor for QuestData_BoosterPack.</p>
     */
    public QuestData_BoosterPack() {
        //setup(ForgeProps.getFile(QUEST.COMMON), commonCreature, commonSpell);
        //setup(ForgeProps.getFile(QUEST.UNCOMMON), uncommonCreature, uncommonSpell);
        //setup(ForgeProps.getFile(QUEST.RARE), rareCreature, rareSpell);

        CardList AllCards = new CardList(AllZone.getCardFactory().getAllCards().toArray());

        for (int i = 0; i < AllCards.size(); i++) {
            Card aCard = AllCards.get(i);
            String rr = aCard.getSVar("Rarity");

            if (rr.equals("Common")) {
                if (aCard.isCreature())
                    commonCreature.add(aCard.getName());
                else
                    commonSpell.add(aCard.getName());
            } else if (rr.equals("Uncommon")) {
                if (aCard.isCreature())
                    uncommonCreature.add(aCard.getName());
                else
                    uncommonSpell.add(aCard.getName());
            } else if (rr.equals("Rare")) {
                if (aCard.isCreature())
                    rareCreature.add(aCard.getName());
                else
                    rareSpell.add(aCard.getName());
            } else if (rr.equals("Mythic")) {
                if (aCard.isCreature())
                    rareCreature.add(aCard.getName());
                else
                    rareSpell.add(aCard.getName());
            }

        }
    }

    /**
     * <p>getColorIndex.</p>
     *
     * @param n a int.
     * @return a int.
     */
    private int getColorIndex(int n) {
        return n % colors.length;
    }

    //return a number that is a multiple of colors.length
    //we want to loop all the way through the colors array  
    /**
     * <p>getLoopStop.</p>
     *
     * @param n a int.
     * @return a int.
     */
    private int getLoopStop(int n) {
        int stop = colors.length;
        while (stop < n)
            stop += colors.length;

        return stop;
    }

    /**
     * <p>getCommon.</p>
     *
     * @param n a int.
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getCommon(int n) {
        ArrayList<String> bag = new ArrayList<String>();
        String c;
        int stop = getLoopStop(n);

        for (int i = 0; i < stop; i++) {
            c = colors[getColorIndex(i)];

            //creatures are more common than spells
            bag.add(getColor(c, commonCreature));
            bag.add(getColor(c, commonCreature));
            bag.add(getColor(c, commonSpell));
        }
        return getRandom(n, bag);
    }//getCommon()


    /**
     * <p>getUncommon.</p>
     *
     * @param n a int.
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getUncommon(int n) {
        ArrayList<String> bag = new ArrayList<String>();
        String c;
        int stop = getLoopStop(n);

        for (int i = 0; i < stop; i++) {
            c = colors[getColorIndex(i)];

            //creatures are more common than spells
            bag.add(getColor(c, uncommonCreature));
            bag.add(getColor(c, uncommonCreature));
            bag.add(getColor(c, uncommonSpell));
        }
        return getRandom(n, bag);
    }//getUncommon()

    /**
     * <p>getRare.</p>
     *
     * @param n a int.
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getRare(int n) {
        ArrayList<String> bag = new ArrayList<String>();
        String c;
        int stop = getLoopStop(n);

        for (int i = 0; i < stop; i++) {
            c = colors[getColorIndex(i)];

            bag.add(getColor(c, rareCreature));
            bag.add(getColor(c, rareSpell));
        }
        return getRandom(n, bag);
    }//getRare()

    /**
     * <p>getRare.</p>
     *
     * @param n a int.
     * @param colorIndex a int.
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<String> getRare(int n, int colorIndex) {
        ArrayList<String> bag = new ArrayList<String>();
        String c;
        int stop = getLoopStop(n);

        for (int i = 0; i < stop; i++) {
            c = colors[colorIndex];

            bag.add(getColor(c, rareCreature));
            bag.add(getColor(c, rareSpell));
        }
        return getRandom(n, bag);
    }//getRare()

    //returns String of the card name that matches the parameter "color"
    /**
     * <p>getColor.</p>
     *
     * @param color a {@link java.lang.String} object.
     * @param list a {@link java.util.ArrayList} object.
     * @return a {@link java.lang.String} object.
     */
    private String getColor(String color, ArrayList<String> list) {
        Collections.shuffle(list, random);

        Card c;
        String s;
        for (int i = 0; i < list.size(); i++) {
            s = list.get(i).toString();
            c = AllZone.getCardFactory().getCard(s, null);
            if (getColor(c).equals(color)) return s;
        }
        //just get a random card
        //this will happens if there are 0 gold cards
        return list.get(0).toString();
    }//getColor()


    /**
     * <p>getColor.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a {@link java.lang.String} object.
     */
    private String getColor(Card c) {
        String m = c.getManaCost();
        Set<String> colors = new HashSet<String>();

        for (int i = 0; i < m.length(); i++) {
            switch (m.charAt(i)) {
                case ' ':
                    break;
                case 'G':
                    colors.add(Constant.Color.Green);
                    break;
                case 'W':
                    colors.add(Constant.Color.White);
                    break;
                case 'B':
                    colors.add(Constant.Color.Black);
                    break;
                case 'U':
                    colors.add(Constant.Color.Blue);
                    break;
                case 'R':
                    colors.add(Constant.Color.Red);
                    break;
            }
        }
        if (colors.isEmpty()) return Constant.Color.Colorless;

        if (1 < colors.size()) return "gold";

        ArrayList<String> list = new ArrayList<String>(colors);
        return list.get(0).toString();
    }

    /**
     * <p>Getter for the field <code>random</code>.</p>
     *
     * @param n a int.
     * @param list a {@link java.util.ArrayList} object.
     * @return a {@link java.util.ArrayList} object.
     */
    private ArrayList<String> getRandom(int n, ArrayList<String> list) {
        //must always shuffle since we are starting at 0
        Collections.shuffle(list, random);

        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < n; i++)
            out.add(list.get(i));

        return out;
    }

    /*    @SuppressWarnings("unchecked")
        private void setup(File file, ArrayList creatureList, ArrayList spellList) {
            ArrayList all = readFile(file);

            Card c;
            String s;
            for(int i = 0; i < all.size(); i++) {
                s = all.get(i).toString();

                c = AllZone.getCardFactory().getCard(s, "");
                if(c.isCreature()) creatureList.add(s);
                else spellList.add(s);
            }
        }//setup()

        @SuppressWarnings("unchecked")
        private ArrayList readFile(File file) {
            ArrayList cardList = new ArrayList();

            BufferedReader in;
            try {
                in = new BufferedReader(new FileReader(file));
                String line = in.readLine();

                //stop reading if end of file or blank line is read
                while(line != null && (line.trim().length() != 0)) {
                    if(!line.startsWith(comment)) {
                        cardList.add(line.trim());
                    }

                    line = in.readLine();
                }//if

            } catch(Exception ex) {
                ErrorViewer.showError(ex);
                throw new RuntimeException("QuestBoosterPack : readFile() error, " + ex);
            }

            return cardList;
        }//readFile()
    */
    /**
     * <p>getRarity.</p>
     *
     * @param cardName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getRarity(String cardName) {
        if (commonCreature.contains(cardName) || commonSpell.contains(cardName)) return "Common";
        else if (uncommonCreature.contains(cardName) || uncommonSpell.contains(cardName)) return "Uncommon";
        else if (rareCreature.contains(cardName) || rareSpell.contains(cardName)) return "Rare";
        else if (cardName.equals("Forest") || cardName.equals("Plains") || cardName.equals("Island")
                || cardName.equals("Swamp") || cardName.equals("Mountain")
                || cardName.equals("Snow-Covered Forest") || cardName.equals("Snow-Covered Plains") || cardName.equals("Snow-Covered Island")
                || cardName.equals("Snow-Covered Swamp") || cardName.equals("Snow-Covered Mountain")) return "Land";
        else return "error";
    }
}
