package forge.gui;
/**
 * ForgeAction.java
 *
 * Created on 02.09.2009
 */


import forge.properties.ForgeProps;

import javax.swing.*;


/**
 * The class ForgeAction.
 *
 * @author Clemens Koza
 * @version V0.0 02.09.2009
 */
public abstract class ForgeAction extends AbstractAction {

    private static final long serialVersionUID = -1881183151063146955L;
    private String property;

    /**
     * @param property A Property key containing the keys "/button" and "/menu".
     */
    public ForgeAction(String property) {
        super(ForgeProps.getLocalized(property + "/button"));
        this.property = property;
        putValue("buttonText", ForgeProps.getLocalized(property + "/button"));
        putValue("menuText", ForgeProps.getLocalized(property + "/menu"));
    }

    protected String getProperty() {
        return property;
    }

    public <T extends AbstractButton> T setupButton(T button) {
        button.setAction(this);
        button.setText((String) getValue(button instanceof JMenuItem ? "menuText" : "buttonText"));
        return button;
    }
}
