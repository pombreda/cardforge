package forge;

import forge.card.spellability.SpellAbility;
import forge.error.ErrorViewer;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;


/**
 * <p>NameChanger class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class NameChanger implements NewConstants {
    private Map<String, String> mutatedMap = new HashMap<String, String>();
    private Map<String, String> originalMap = new HashMap<String, String>();

    private boolean changeCardName;

    /**
     * <p>Constructor for NameChanger.</p>
     */
    public NameChanger() {
//      readFile();
        setShouldChangeCardName(false);
    }

    //should change card name?
    /**
     * <p>shouldChangeCardName.</p>
     *
     * @return a boolean.
     */
    public boolean shouldChangeCardName() {
        return changeCardName;
    }

    /**
     * <p>setShouldChangeCardName.</p>
     *
     * @param b a boolean.
     */
    public void setShouldChangeCardName(boolean b) {
        changeCardName = b;
    }

    //returns an array of copies
    /**
     * <p>changeCard.</p>
     *
     * @param c an array of {@link forge.Card} objects.
     * @return an array of {@link forge.Card} objects.
     */
    public Card[] changeCard(Card c[]) {
        for (int i = 0; i < c.length; i++)
            changeCard(c[i]);

        return c;
    }

    //changes card name, getText(), and all SpellAbility getStackDescription() and toString()
    /**
     * <p>changeCard.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a {@link forge.Card} object.
     */
    public Card changeCard(Card c) {
        //change name
        String newName = changeName(c.getName());
        c.setName(newName);

        //change text
        String s;
        s = c.getSpellText();
        c.setText(changeString(c, s));

        //change all SpellAbilities
        SpellAbility[] spell = c.getSpellAbility();
        for (int i = 0; i < spell.length; i++) {
            s = spell[i].getStackDescription();
            spell[i].setStackDescription(changeString(c, s));

            s = spell[i].toString();
            spell[i].setDescription(changeString(c, s));
        }

        return c;
    }//getMutatedCard()

    /**
     * <p>changeString.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param in a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String changeString(Card c, String in) {
        //String name = getOriginalName(c.getName()); // unused
//    in = in.replaceAll(name, changeName(name));

        return in;
    }

    //always returns mutated (alias) for the card name
    //if argument is a mutated name, it returns the same mutated name
    /**
     * <p>changeName.</p>
     *
     * @param originalName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String changeName(String originalName) {
        Object o = mutatedMap.get(originalName);

        if (o == null) return originalName;

        return o.toString();
    }//getMutatedName()

    //always returns the original cardname
    //if argument is a original name, it returns the same original name
    /**
     * <p>getOriginalName.</p>
     *
     * @param mutatedName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getOriginalName(String mutatedName) {
        Object o = originalMap.get(mutatedName);

        if (o == null) return mutatedName;

        return o.toString();
    }//getOriginalName()

    /**
     * <p>readFile.</p>
     */
    @SuppressWarnings("unused")
    private void readFile() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(ForgeProps.getFile(NAME_MUTATOR)));

            String line = in.readLine();

            //stop reading if end of file or blank line is read
            while (line != null && (line.trim().length() != 0)) {
                processLine(line.trim());

                line = in.readLine();
            }//while
        }//try
        catch (Exception ex) {
//~      throw new RuntimeException("NameMutator : readFile() error, " +ex);


            //~ (could be cleaner...)
            try {
                BufferedReader in = new BufferedReader(new FileReader(ForgeProps.getFile(NAME_MUTATOR)));

                String line;

                //stop reading if end of file or blank line is read
                while ((line = in.readLine()) != null && (line.trim().length() != 0)) {
                    processLine(line.trim());
                }//while
            } catch (Exception ex2) {
                // Show orig exception
                ErrorViewer.showError(ex2);
                throw new RuntimeException(String.format("NameMutator : readFile() error, %s", ex), ex);
            }
            //~
        }
    }//readFile()

    //line is formated "original card name : alias card name"
    /**
     * <p>processLine.</p>
     *
     * @param line a {@link java.lang.String} object.
     */
    private void processLine(String line) {
        StringTokenizer tok = new StringTokenizer(line, ":");

        if (tok.countTokens() != 2)
            throw new RuntimeException(
                    "NameMutator : processLine() error, invalid line in file name-mutator.txt - " + line);

        String original = tok.nextToken().trim();
        String mutated = tok.nextToken().trim();

        mutatedMap.put(original, mutated);
        originalMap.put(mutated, original);
    }

    /**
     * <p>printMap.</p>
     *
     * @param map a {@link java.util.Map} object.
     */
    @SuppressWarnings("unused")
    // printMap
    private void printMap(Map<String, String> map) {
        for (Entry<String, String> e : map.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
    }//main()
}
