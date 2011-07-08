package forge.gui;

import java.awt.Font;


/**
 * A replacement FontConstants to allow backward-compatibility with JRE 1.5;
 * this is no longer needed, because we now directly support Java 6.
 *
 * @deprecated use fields of same name at java.awt.Font
 * 
 * @see java.awt.Font
 * 
 * @author Forge
 * @version $Id: $
 */
public class ForgeFontConstants {
    /** Constant <code>DIALOG</code> */
    public static final String DIALOG = Font.DIALOG;

    /** Constant <code>DIALOG_INPUT</code> */
    public static final String DIALOG_INPUT = Font.DIALOG_INPUT;

    /** Constant <code>MONOSPACED</code> */
    public static final String MONOSPACED = Font.MONOSPACED;

    /** Constant <code>SANS_SERIF</code> */
    public static final String SANS_SERIF = Font.SANS_SERIF;

    /** Constant <code>SERIF</code> */
    public static final String SERIF = Font.SERIF;
}
