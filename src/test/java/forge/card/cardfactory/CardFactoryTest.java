package forge.card.cardFactory;

import forge.Card;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;
import org.testng.annotations.Test;

/**
 * <p>Mana_PartTest class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
@Test(timeOut = 1000)
public class CardFactoryTest implements NewConstants {

    /**
     *
     */
    @Test(timeOut = 1000)
    public void CardFactoryTest1() {
        CardFactory f = new CardFactory(ForgeProps.getFile(CARDSFOLDER));
        Card c = f.getCard("Arc-Slogger", null);
        System.out.println(c.getOwner());
    }
}
