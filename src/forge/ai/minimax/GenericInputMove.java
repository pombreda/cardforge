package forge.ai.minimax;

import static net.slightlymagic.braids.util.UtilFunctions.checkNullOrNotInstance;
import net.slightlymagic.braids.util.UtilFunctions;
import net.slightlymagic.braids.util.lambda.Null;
import forge.AllZone;
import forge.Card;
import forge.Player;
import forge.PlayerZone;
import forge.gui.input.Input;

/**
 * A MinimaxMove that represents a potential selection associated with an
 * Input instance.
 * 
 * This class is also invocable via its apply method.
 */
public class GenericInputMove extends AbstractInputMove {

	enum MethodCallType {
		SELECT_CARD,
		SELECT_PLAYER,
		SELECT_OK,
		SELECT_CANCEL,
	}

	private MethodCallType methodCallType;
	private Card card;
	private PlayerZone zone;
	private Player player;

	
	/**
	 * Creates a generic MinimaxMove based on an Input instance where the user
	 * may select a card.
	 * 
	 * @param source  the input instance whose selectCard method to call
	 * 
	 * @param card  the card to pass to source.selectCard
	 */
	public GenericInputMove(Input source, Card card) {
		super(source);
		setCard(card);
		setZone(AllZone.getZone(card));
		setMethodCallType(MethodCallType.SELECT_CARD);
	}

	/**
	 * Creates a generic MinimaxMove based on an Input instance where the user
	 * may select a player.
	 * 
	 * @param source  the input instance whose selectPlayer method we later call
	 * 
	 * @param player the player that the AI selected
	 */
	public GenericInputMove(Input source, Player player) {
		super(source);
		setPlayer(player);
		setMethodCallType(MethodCallType.SELECT_PLAYER);
	}
	
	/**
	 * Creates a generic MinimaxMove based on an Input instance where the user
	 * may click the OK (x)or Cancel button(s).
	 * 
	 * @param source  the input instance whose selectButtonOK or 
	 * selectButtonCancel to call
	 * 
	 * @param isOK  if true, then this move represents pressing the OK button 
	 * (or whatever to which the OK button has been renamed); if false, then
	 * this move represents pressing the Cancel button (ditto).
	 */
	public GenericInputMove(Input source, boolean isOK) {
		super(source);
		
		if (isOK) {
			setMethodCallType(MethodCallType.SELECT_OK);
		}
		else {
			setMethodCallType(MethodCallType.SELECT_CANCEL);
		}
	}

	
	public void setCard(Card card) {
		if (card == null) {
			throw new NullPointerException();
		}
		this.card = card;
	}

	public Card getCard() {
		return card;
	}
	
	public void setZone(PlayerZone zone) {
		if (zone == null) {
			throw new NullPointerException();
		}
		this.zone = zone;
	}

	public PlayerZone getZone() {
		return zone;
	}

	public void setMethodCallType(MethodCallType methodCallType) {
		if (methodCallType == null) {
			throw new NullPointerException();
		}
		this.methodCallType = methodCallType;
	}

	public MethodCallType getMethodCallType() {
		return methodCallType;
	}

	public void setPlayer(Player player) {
		if (player == null) {
			throw new NullPointerException();
		}
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public Null apply() {
		Input source = getSource();
		source.setSilent(true);

		if (getMethodCallType() == MethodCallType.SELECT_CARD) {
			source.selectCard(getCard(), getZone());
		}
		else if (getMethodCallType() == MethodCallType.SELECT_PLAYER) {
			source.selectPlayer(getPlayer());
		}
		else if (getMethodCallType() == MethodCallType.SELECT_CANCEL) {
			source.selectButtonCancel();
		}
		else if (getMethodCallType() == MethodCallType.SELECT_OK) {
			source.selectButtonOK();
		}
		else {
			throw new IllegalStateException("unsupported methodCallType: " + 
					UtilFunctions.safeToString(getMethodCallType()));
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		GenericInputMove that = checkNullOrNotInstance(this, obj);
		
		if (that == null || this.methodCallType != that.methodCallType) {
			return false;
		}
		else if (this.card == null && that.card != null ||
				 this.zone == null && that.card != null ||
				 this.player == null && that.player != null)
		{
			return false;
		}
		else if (this.card != null && !this.card.equals(that.card) ||
				 this.zone != null && !this.zone.equals(that.zone) ||
				 this.player != null && !this.player.equals(that.player))
		{
			return false;
		}
		
		return super.equals(that);
	}	
	
	public String toString() {
		
		if (getMethodCallType() == MethodCallType.SELECT_CARD) {
			return "selects card " + getCard().toString() + " in " + getZone().toString();
		}
		else if (getMethodCallType() == MethodCallType.SELECT_PLAYER) {
			return "selects player " + getPlayer().toString();
		}
		else if (getMethodCallType() == MethodCallType.SELECT_CANCEL) {
			return "selects Cancel";
		}
		else if (getMethodCallType() == MethodCallType.SELECT_OK) {
			return "selects OK";
		}
		else {
			throw new IllegalStateException("unsupported methodCallType: " + 
					UtilFunctions.safeToString(getMethodCallType()));
		}
		//return null;  // unreachable code
	}
}
