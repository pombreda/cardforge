package forge.gui;


/**
 * A replacement FontConstants to allow backward-compatibility with JRE 1.5
 */

public class ForgeFontConstants {
    public static final String DIALOG, DIALOG_INPUT, MONOSPACED, SANS_SERIF, SERIF;

    static {
        String dialog = "Dialog";
        String dialogInput = "DialogInput";
        String monospaced = "Monospaced";
        String sansSerif = "SansSerif";
        String serif = "Serif";

        Exception exception = null;
        try {
            // Fetch Java 6 values (if present) without making the Java 5
            // compiler unhappy:
            @SuppressWarnings("rawtypes")
            Class fontClass = Class.forName("java.awt.Font");

            dialog = (String) fontClass.getField("DIALOG").get(null);
            dialogInput = (String) fontClass.getField("DIALOG_INPUT").get(null);
            monospaced = (String) fontClass.getField("MONOSPACED").get(null);
            sansSerif = (String) fontClass.getField("SANS_SERIF").get(null);
            serif = (String) fontClass.getField("SERIF").get(null);

        } catch (ClassNotFoundException err) {
            exception = err;
        } catch (IllegalAccessException err) {
            exception = err;
        } catch (NoSuchFieldException err) {
            exception = err;
        } finally {
            if (exception != null) {
                System.err.print("Exception thrown: " + exception);
                exception.printStackTrace();
            }
        }

        DIALOG = dialog;
        DIALOG_INPUT = dialogInput;
        MONOSPACED = monospaced;
        SANS_SERIF = sansSerif;
        SERIF = serif;
    }
}
