package forge;

import forge.card.trigger.TriggerHandler;
import forge.error.ErrorViewer;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * <p>ReadCard class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class ReadCard implements Runnable, NewConstants {
    private BufferedReader in;
    private String fileList[];
    private ArrayList<Card> allCards = new ArrayList<Card>();
    /** Constant <code>zipFile</code> */
    private static File zipFile;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String args[]) throws Exception {
        try {
            ReadCard read = new ReadCard(ForgeProps.getFile(CARDSFOLDER));

            javax.swing.SwingUtilities.invokeAndWait(read);
            //    read.run();

            Card c[] = new Card[read.allCards.size()];
            read.allCards.toArray(c);
            for (int i = 0; i < c.length; i++) {
                System.out.println(c[i].getName());
                System.out.println(c[i].getManaCost());
                System.out.println(c[i].getType());
                System.out.println(c[i].getSpellText());
                System.out.println(c[i].getKeyword());
                System.out.println(c[i].getBaseAttack() + "/" + c[i].getBaseDefense() + "\n");
            }
        } catch (Exception ex) {
            ErrorViewer.showError(ex);
            System.out.println("Error reading file " + ex);
        }
    }

    /**
     * <p>getCards.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<Card> getCards() {
        return new ArrayList<Card>(allCards);
    }

    /**
     * <p>Constructor for ReadCard.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public ReadCard(String filename) {
        this(new File(filename));
    }

    /**
     * <p>Constructor for ReadCard.</p>
     *
     * @param file a {@link java.io.File} object.
     */
    public ReadCard(File file) {
        if (!file.exists())
            throw new RuntimeException("ReadCard : constructor error -- file not found -- filename is "
                    + file.getAbsolutePath());

        if (!file.isDirectory())
            throw new RuntimeException("ReadCard : constructor error -- not a direcotry -- "
                    + file.getAbsolutePath());
        zipFile = new File(file, CARDSFOLDER + ".zip");

        if (!zipFile.exists())
            fileList = file.list();
        //makes the checked exception, into an unchecked runtime exception
        //try {
        //    in = new BufferedReader(new FileReader(file));
        //} catch(Exception ex) {
        //    ErrorViewer.showError(ex, "File \"%s\" not found", file.getAbsolutePath());
        //    throw new RuntimeException("ReadCard : constructor error -- file not found -- filename is "
        //            + file.getPath());
        //}
    }//ReadCard()

    /**
     * <p>run.</p>
     */
    public void run() {
        Card c = null;
        ArrayList<String> cardNames = new ArrayList<String>();
        File fl = null;

        if (zipFile.exists()) {
            try {
                ZipFile zip = new ZipFile(zipFile);
                ZipEntry entry;
                Enumeration<? extends ZipEntry> e = zip.entries();
                while (e.hasMoreElements()) {
                    entry = (ZipEntry) e.nextElement();
                    if (!entry.getName().endsWith(".txt"))
                        continue;
                    in = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
                    c = new Card();
                    loadCard(c, cardNames);
                    cardNames.add(c.getName());
                    allCards.add(c);
                    in.close();
                }
            } catch (Exception e) {

            }

        } else {
            for (int i = 0; i < fileList.length; i++) {
                if (!fileList[i].endsWith(".txt"))
                    continue;

                try {
                    fl = new File("res/cardsfolder/" + fileList[i]);
                    in = new BufferedReader(new FileReader(fl));
                } catch (Exception ex) {
                    ErrorViewer.showError(ex, "File \"%s\" exception", fl.getAbsolutePath());
                    throw new RuntimeException("ReadCard : run error -- file exception -- filename is "
                            + fl.getPath());
                }

                c = new Card();
                loadCard(c, cardNames);
                cardNames.add(c.getName());
                allCards.add(c);

                try {
                    in.close();
                } catch (IOException ex) {
                    ErrorViewer.showError(ex, "File \"%s\" exception", fl.getAbsolutePath());
                    throw new RuntimeException("ReadCard : run error -- file exception -- filename is "
                            + fl.getPath());
                }
            }
        }

    }//run()

    /**
     * <p>addTypes.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param types a {@link java.lang.String} object.
     */
    private void addTypes(Card c, String types) {
        StringTokenizer tok = new StringTokenizer(types);
        while (tok.hasMoreTokens())
            c.addType(tok.nextToken());
    }

    /**
     * <p>readLine.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    private String readLine() {
        //makes the checked exception, into an unchecked runtime exception
        try {
            String s = in.readLine();
            if (s != null) s = s.trim();
            return s;
        } catch (Exception ex) {
            ErrorViewer.showError(ex);
            throw new RuntimeException("ReadCard : readLine(Card) error");
        }
    }//readLine(Card)

    /**
     * <p>loadCard.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param cardNames a {@link java.util.ArrayList} object.
     */
    private void loadCard(Card c, ArrayList<String> cardNames) {
        String s = readLine();
        while (!s.equals("End")) {
            if (s.startsWith("#")) {
                //no need to do anything, this indicates a comment line
            } else if (s.startsWith("Name:")) {
                String t = s.substring(5);
                //System.out.println(s);
                if (cardNames.contains(t)) {
                    System.out.println("ReadCard:run() error - duplicate card name: " + t);
                    throw new RuntimeException("ReadCard:run() error - duplicate card name: " + t);
                } else
                    c.setName(t);
            } else if (s.startsWith("ManaCost:")) {
                String t = s.substring(9);
                //System.out.println(s);
                if (!t.equals("no cost"))
                    c.setManaCost(t);
            } else if (s.startsWith("Types:"))
                addTypes(c, s.substring(6));

            else if (s.startsWith("Text:")) {
                String t = s.substring(5);
                // if (!t.equals("no text"));
                if (t.equals("no text")) t = ("");
                c.setText(t);
            } else if (s.startsWith("PT:")) {
                String t = s.substring(3);
                String pt[] = t.split("/");
                int att = pt[0].contains("*") ? 0 : Integer.parseInt(pt[0]);
                int def = pt[1].contains("*") ? 0 : Integer.parseInt(pt[1]);
                c.setBaseAttackString(pt[0]);
                c.setBaseDefenseString(pt[1]);
                c.setBaseAttack(att);
                c.setBaseDefense(def);
            } else if (s.startsWith("Loyalty:")) {
                String splitStr[] = s.split(":");
                int loyal = Integer.parseInt(splitStr[1]);
                c.setBaseLoyalty(loyal);
            } else if (s.startsWith("K:")) {
                String t = s.substring(2);
                c.addIntrinsicKeyword(t);
            } else if (s.startsWith("SVar:")) {
                String t[] = s.split(":", 3);
                c.setSVar(t[1], t[2]);
            } else if (s.startsWith("A:")) {
                String t = s.substring(2);
                c.addIntrinsicAbility(t);
            } else if (s.startsWith("T:")) {
                String t = s.substring(2);
                c.addTrigger(TriggerHandler.parseTrigger(t, c));
            } else if (s.startsWith("SetInfo:")) {
                String t = s.substring(8);
                c.addSet(new SetInfo(t));
            }

            s = readLine();
        } // while !End
    }
}
