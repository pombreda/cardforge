package forge.ai.minimax;

import forge.card.cardFactory.CardFactory;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;

/**
 * This class exists strictly to pander to prior code that relied on static
 * fields within the game-state.
 * 
 * For a proper AI, no such fields must actually be static. However, there is
 * plenty of existing code that accesses and changes this information from
 * static scopes. This is the resulting compromise.
 * 
 * Being Unstatic is rather like being undead. You're not exactly static, and
 * you're not exactly NOT static.
 */
public class Unstatic {
	
	private static FGameState globalGameState = null;
	private static CardFactory cardFactory = null;
	
	
	/**
	 * Fetch the current, global game-state, usually for purposes of making
	 * theoretical changes.
	 * 
	 * @see #setGlobalGameState
	 * 
	 * @return the current, global game-state
	 */
	public static FGameState getGlobalGameState() {
		if (globalGameState == null) {
			globalGameState = new FGameState();
			globalGameState.bootstrap();
		}
		
		return globalGameState; 
	}
	
	public static CardFactory getCardFactory() {
		if (cardFactory == null) {
			initCardFactory();
		}
		return cardFactory;
	}

	public static void initCardFactory() {
		cardFactory = new CardFactory(ForgeProps.getFile(NewConstants.CARDSFOLDER));
	}
	
	
	/**
	 * Set the global game-state to something else, for purposes such as
	 * theoretical changes to be placed in a minimax AI's game-tree.
	 * 
	 * It is generally best to call getGlobalGameState first and to squirrel
	 * away the value until the theoretical changes are no longer needed.
	 * 
	 * For example:
	 * 
	 * FGameState originalState = Unstatic.getGlobalGameState();
	 * FGameState theoreticalState = originalState.cloneFor(somePlayer);
	 * Unstatic.setGlobalGameState(theoreticalState);
	 * // ... make some changes ...
	 * Unstatic.setGlobalGameState(originalState);
	 * theoreticalState = null;
	 * // You might want to call System.gc() here.
	 * 
	 * @param gs the game-state to use from now on in most operations within
	 * Forge
	 */
	public static void setGlobalGameState(FGameState gs) { 
		globalGameState = gs; 
	}
}
