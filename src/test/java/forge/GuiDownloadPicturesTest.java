package forge;

import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: dhudson
 */
@Test
public class GuiDownloadPicturesTest {
    /**
     */
    @Test(timeOut = 10)
    public void GuiDownloadPicturesTest1() {
        Gui_DownloadPictures.startDownload(null);
    }
}
