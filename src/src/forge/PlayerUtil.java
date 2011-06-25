package forge;


/**
 * <p>PlayerUtil class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class PlayerUtil {
    /**
     * <p>worshipFlag.</p>
     *
     * @param player a {@link forge.Player} object.
     * @return a boolean.
     */
    public static boolean worshipFlag(Player player) {
        // Instead of hardcoded Ali from Cairo like cards, it is now a Keyword
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);
        list = list.getKeyword("Damage that would reduce your life total to less than 1 reduces it to 1 instead.");
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return !c.isFaceDown();
            }
        });

        return list.size() > 0;
    }
}
