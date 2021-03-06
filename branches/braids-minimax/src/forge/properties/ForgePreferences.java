
package forge.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import forge.properties.Preferences;

public class ForgePreferences extends Preferences {
	public boolean newGui;
	public boolean stackAiLand;
	public boolean millingLossCondition;
	public boolean developerMode;
	
	public String laf;
	public boolean lafFonts;
	public StackOffsetType stackOffset;
	public int maxStackSize;
	public CardSizeType cardSize;
	public boolean cardOverlay;
	public boolean scaleLargerThanOriginal;
	
	// Phases
	public boolean bAIUpkeep;
	public boolean bAIDraw;
	public boolean bAIEOT;
	public boolean bAIBeginCombat;
	public boolean bAIEndCombat;
	public boolean bHumanUpkeep;
	public boolean bHumanDraw;
	public boolean bHumanEOT;
	public boolean bHumanBeginCombat;
	public boolean bHumanEndCombat;

	private List<SavePreferencesListener> saveListeners = new ArrayList<SavePreferencesListener>();
	private final String fileName;

	public ForgePreferences (String fileName) throws Exception {
		this.fileName = fileName;
		File f = new File(fileName);
		if(!f.exists())
		{
			f.createNewFile();
		}
		try {
			FileInputStream stream = new FileInputStream(fileName);
			load(stream);
			stream.close();
		} catch (FileNotFoundException ex) {
		    throw new Exception("File not found: \"" + fileName + "\".", ex);
		} catch (IOException ex) {
			throw new Exception("Error reading \"" + fileName + "\".", ex);
		}

		newGui = getBoolean("gui.new", true);
		stackAiLand = getBoolean("AI.stack.land", false);
		millingLossCondition = getBoolean("loss.condition.milling", true);
		developerMode = getBoolean("developer.mode", false);
		
		laf = get("gui.laf", "");	
		lafFonts = getBoolean("gui.laf.fonts", false);
		
		cardOverlay = getBoolean("card.overlay", true);
		cardSize = CardSizeType.valueOf(get("card.images.size", "medium"));
		stackOffset = StackOffsetType.valueOf(get("stack.offset", "tiny"));
		maxStackSize = getInt("stack.max.size", 3);
		scaleLargerThanOriginal = getBoolean("card.scale.larger.than.original", true);
		
		// Stop at Phases
		bAIUpkeep = getBoolean("phase.ai.upkeep", true);
		bAIDraw = getBoolean("phase.ai.draw", true);
		bAIEOT = getBoolean("phase.ai.eot", true);
		bAIBeginCombat = getBoolean("phase.ai.beginCombat", true);
		bAIEndCombat = getBoolean("phase.ai.endCombat", true);
		bHumanUpkeep = getBoolean("phase.human.upkeep", true);
		bHumanDraw = getBoolean("phase.human.draw", true);
		bHumanEOT = getBoolean("phase.human.eot", true);
		bHumanBeginCombat = getBoolean("phase.human.beginCombat", true);
		bHumanEndCombat = getBoolean("phase.human.endCombat", true);
	}

	public void save () throws Exception{

		set("gui.new", newGui);

		set("AI.stack.land", stackAiLand);
		set("loss.condition.milling", millingLossCondition);
		set("developer.mode", developerMode);
		
		set("gui.laf", laf);
		set("gui.laf.fonts", lafFonts);
		
		set("card.overlay", cardOverlay);
		set("card.images.size", cardSize);
		set("stack.offset", stackOffset);
		set("stack.max.size", maxStackSize);
		set("card.scale.larger.than.original", scaleLargerThanOriginal);
		for (SavePreferencesListener listeners : saveListeners)
			listeners.savePreferences();

		set("phase.ai.upkeep", bAIUpkeep);
		set("phase.ai.draw", bAIDraw);
		set("phase.ai.eot", bAIEOT);
		set("phase.ai.beginCombat", bAIBeginCombat);
		set("phase.ai.endCombat", bAIEndCombat);
		set("phase.human.upkeep", bHumanUpkeep);
		set("phase.human.draw", bHumanDraw);
		set("phase.human.eot", bHumanEOT);
		set("phase.human.beginCombat", bHumanBeginCombat);
		set("phase.human.endCombat", bHumanEndCombat);
		
		try {
			FileOutputStream stream = new FileOutputStream(fileName);
			store(stream, "Forge");
			stream.close();
		} catch (IOException ex) {
			throw new Exception("Error saving \"" + fileName + "\".", ex);
		}
	}

	public void addSaveListener (SavePreferencesListener listener) {
		saveListeners.add(listener);
	}

	static public enum CardSizeType {
		tiny, smaller, small, medium, large, huge
	}

	static public enum StackOffsetType {
		tiny, small, medium, large
	}
}
