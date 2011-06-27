package forge;

import forge.deck.Deck;
import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: dhudson
 */
@Test
public class GuiBoosterDraftTest {

    /**
     *
     *
     */
    @Test
    public void GuiBoosterDraftTest1() {
        Constant.Runtime.GameType[0] = Constant.GameType.Draft;
        Constant.Runtime.HumanDeck[0] = new Deck(Constant.GameType.Sealed);

        Gui_BoosterDraft g = new Gui_BoosterDraft();
        g.showGui(new BoosterDraftTest());
    }
}
