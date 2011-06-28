package forge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.slightlymagic.braids.util.progress_monitor.ProgressMonitor;
import net.slightlymagic.braids.util.progress_monitor.StderrProgressMonitor;

import forge.card.trigger.TriggerHandler;
import forge.error.ErrorViewer;
import forge.properties.NewConstants;


/**
 * <p>ReadCard class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class ReadCard implements Runnable, NewConstants {
    public static final String DEFAULT_CHARSET_NAME = "US-ASCII";

	public static final Pattern HYPHENS_AND_SPACES = Pattern.compile("[ -]");
	public static final Pattern PUNCTUATION_TO_ZAP = Pattern.compile("[,'\"]");
	public static final Pattern MULTIPLE_UNDERSCORES = Pattern.compile("__+");

    
	private Charset charset;
    private File cardsFolder;
	private Map<String, Card> namesToCards;
	private ZipFile zip;

	private Enumeration<? extends ZipEntry> zipEnum;

	private String[] fileList;
	private int fileListIx;

	private ProgressMonitor monitor;
	

	//private String fileList[];
    //private ArrayList<Card> allCards = new ArrayList<Card>();
    
    /*
     * <p>main.</p>
     * 
     * @deprecated
     * 
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
	/*
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
    */

	/**
	 * Create a lazy card reader.
	 * 
	 * @param dirname  path to the cardsfolder directory as a String
	 * @param namesToCards  we populate this map as we read cards 
	 */
    public ReadCard(String dirname, Map<String,Card> namesToCards) {
        this(new File(dirname), namesToCards);
    }

	/**
	 * Create a lazy card reader.
	 * 
	 * @param cardsfolder  indicates location of the cardsfolder
	 * @param namesToCards  we populate this map as we read cards 
	 */
    public ReadCard(File cardsFolder, Map<String,Card> namesToCards) {
        if (!cardsFolder.exists())
            throw new RuntimeException("ReadCard : constructor error -- file not found -- filename is "
                    + cardsFolder.getAbsolutePath());

        if (!cardsFolder.isDirectory())
            throw new RuntimeException("ReadCard : constructor error -- not a direcotry -- "
                    + cardsFolder.getAbsolutePath());

        if (namesToCards == null) {
        	throw new NullPointerException();
        }
        
        this.cardsFolder = cardsFolder;
        this.namesToCards = namesToCards;
        
        File zipFile = new File(cardsFolder, CARDSFOLDER + ".zip");
        
        // Prepare resources to read cards lazily.
    	if (zipFile.exists()) {
			try {
				this.zip = new ZipFile(zipFile);

			} catch (Exception exn) {
				System.err.println("Error reading zip file \"" + 
						 zipFile.getAbsolutePath() + "\": " + exn + ". " +
						 "Defaulting to txt files in \"" +
						 cardsFolder.getAbsolutePath() +
						 "\"."
						 );
			}
				
    	}

    	if (zip != null) {
            zipEnum = zip.entries();
    	}
    	else {
            fileList = cardsFolder.list();
            fileListIx = 0;
    	}
    	
    	setEncoding(DEFAULT_CHARSET_NAME);
    	
    	monitor = new StderrProgressMonitor(1, 0L);
    }//ReadCard()

    /**
     * Set the character encoding to use when loading cards.
     * 
     * @param charsetName  the name of the charset, for example, "UTF-8"
     */
    public void setEncoding(String charsetName) {
    	this.charset = Charset.forName(charsetName);
    }
    
    
    /**
     * Reads the rest of ALL the cards into memory.  This is not lazy.
     */
    public void run() {
    	loadCardsUntilYouFind(null);
    }

    /**
     * Starts reading cards into memory until the given card is found.
     * 
     * After that, we save our place in the list of cards (on disk) in case we
     * need to load more. 
     * 
     * @param cardName the name to find; if null, load all cards.
     * 
     * @return the Card or null if it was not found.
     */
    protected Card loadCardsUntilYouFind(String cardName) {

    	if (cardName == null) {
    		int numCardsAlreadyRead = namesToCards.size();

    		if (zip != null) {
    			monitor.setTotalUnitsThisPhase(zip.size() - numCardsAlreadyRead);
    		}
    		else {
    			monitor.setTotalUnitsThisPhase(fileList.length - numCardsAlreadyRead);
    		}
    	}
    	
        Card c = null;

        if (zip != null) {
            ZipEntry entry;

        	// zipEnum was initialized in the constructor.
            while (zipEnum.hasMoreElements()) {
                entry = (ZipEntry) zipEnum.nextElement();
                
            	if (cardName == null) {
            		monitor.incrementUnitsCompletedThisPhase(1);
            	}
                if (!entry.getName().endsWith(".txt")) {
                    continue;
                }

                c = loadCard(entry);
                if (cardName != null && cardName.equals(c.getName())) {
                	return c;
                }
            }

        } else {
        	// fileListIx was initialized in the constructor.
            for (; fileListIx < fileList.length; fileListIx++) {
            	if (cardName == null) {
            		monitor.incrementUnitsCompletedThisPhase(1);
            	}

            	if (!fileList[fileListIx].endsWith(".txt")) {
                    continue;
                }

            	c = null;
            	try {
                    c = loadCard(fileList[fileListIx]);
            	}
            	catch (FileNotFoundException ex) {
            		File fl = new File(fileList[fileListIx]);
                    ErrorViewer.showError(ex, "File \"%s\" exception", fl.getAbsolutePath());
                    throw new RuntimeException("ReadCard : run error -- file exception -- filename is "
                            + fl.getPath(), ex);
            	}
            	
                if (cardName != null && cardName.equals(c.getName())) {
                	return c;
                }

            }
        }

        return null;
    }//run()


    protected Card loadCard(String fileBaseName) throws FileNotFoundException {
    	File fl = new File(cardsFolder, fileBaseName);
        FileInputStream fileInputStream = null;
        Card result = null;
        try {
            fileInputStream = new FileInputStream(fl);
            result = loadCard(fileInputStream);
        } 
        finally {
            try {
                fileInputStream.close();
            } catch (Throwable ignored) {
            	;
            }
        }

        return result;
    }
    
    
    protected Card loadCard(ZipEntry entry) {
        InputStream zipInputStream = null;
        Card result = null;
        try {
        	zipInputStream = zip.getInputStream(entry);
            result = loadCard(zipInputStream);
        } 
        catch (IOException exn) {
        	throw new RuntimeException(exn);
		}
        finally {
            try {
				zipInputStream.close();
			} catch (IOException ignored) {
				;
			}
        }

        return result;
    }
    
    
    /*
     * <p>getCards.</p>
     *
     * @deprecated
     *
     * @return a {@link java.util.ArrayList} object.
     */
    /*
    public ArrayList<Card> getCards() {
        return new ArrayList<Card>(allCards);
    }
    */

    /**
     * <p>addTypes to an existing card</p>
     *
     * @param c a {@link forge.Card} object.
     * @param types a {@link java.lang.String} object.
     */
    public static void addTypes(Card c, String types) {
        StringTokenizer tok = new StringTokenizer(types);
        while (tok.hasMoreTokens())
            c.addType(tok.nextToken());
    }

    /**
     * <p>Reads a line from the given reader and handles exceptions.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String readLine(BufferedReader in) {
        //makes the checked exception, into an unchecked runtime exception
        try {
            String s = in.readLine();
            if (s != null) s = s.trim();
            return s;
        } catch (Exception ex) {
            ErrorViewer.showError(ex);
            throw new RuntimeException("ReadCard : readLine(Card) error", ex);
        }
    }//readLine(Card)

    /**
     * <p>load a card.</p>
     *
     * @param c a {@link forge.Card} object; an input and an output variable
     */
    protected Card loadCard(InputStream inputStream) {
        Card c = new Card();
    	
    	InputStreamReader inputStreamReader = null;
    	BufferedReader in = null;
    	try {
	        inputStreamReader = new InputStreamReader(inputStream, charset);
			in = new BufferedReader(inputStreamReader);
	        
	        String s = readLine(in);
	        while (!s.equals("End")) {
	            if (s.startsWith("#")) {
	                //no need to do anything, this indicates a comment line
	            } else if (s.startsWith("Name:")) {
	                String t = s.substring(5);
	                //System.out.println(s);
	                if (!namesToCards.containsKey(t)) {
	                    c.setName(t);
	                }
	                else {
	                	break;  // this card has already been loaded.
	                }
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
	
	            s = readLine(in);
	        } // while !End
    	}
    	finally {
            try {
				in.close();
			} catch (IOException ignored) {
				;
			}
            try {
				inputStreamReader.close();
			} catch (IOException ignored) {
				;
			}
    	}

    	// Never put the card in twice.
    	Card cardInMap = namesToCards.get(c.getName()); 
    	if (cardInMap == null) {
    		namesToCards.put(c.getName(), c);
    		cardInMap = c;
    	}
    	return cardInMap;
    }

    /**
     * Attempt to guess what a given card's file name would be.  
     * 
     * @param asciiCardName  the card name in canonicalized ASCII form
     * 
     * @return  the likeliest name of the card's txt file, including the ".txt" 
     * suffix
     * 
     * @see CardUtil#canonicalizeCardName
     */
	public static String toFileName(String asciiCardName) {
		String result = asciiCardName;

		/*
		 * friarsol wrote: "hyphens and spaces are converted to underscores,
		 * commas and apostrophes are removed (I'm not sure if there are any
		 * other punctuation used)."
		 * 
		 * @see http://www.slightlymagic.net/forum/viewtopic.php?f=52&t=4887#p63189
		 */

		result = HYPHENS_AND_SPACES.matcher(result).replaceAll("_");
		result = MULTIPLE_UNDERSCORES.matcher(result).replaceAll("_");
		result = PUNCTUATION_TO_ZAP.matcher(result).replaceAll("");

		result = result.toLowerCase();
		result += ".txt";
		
		return result;
	}

	public Card findCard(String cardFileBaseName, String canonicalASCIIName) {
		
		Card result = null;
		
        if (zip != null) {
        	ZipEntry entry = zip.getEntry(cardFileBaseName);
        	
        	if (entry != null) {
        		result = loadCard(entry);
        	}
        }
        
        if (result == null) {
        	try {
        		result = loadCard(cardFileBaseName);
        	}
        	catch (FileNotFoundException ignored) {
        		;  // result is still null
        	}
        }

        if (result != null && result.getName().equals(canonicalASCIIName)) {
			return result;
		}
		else {
			System.err.println("ReadCard.findCard: guessed wrong name for canonical name \"" + 
					canonicalASCIIName + "\", estimated base name =\"" + 
					cardFileBaseName + "\".");

			return loadCardsUntilYouFind(canonicalASCIIName);
		}
	}
}
