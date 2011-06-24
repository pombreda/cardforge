package forge;


import forge.gui.input.Input;

import java.util.Observable;
import java.util.Observer;


public class GuiInput extends MyObservable implements Observer {
    Input input;

    public GuiInput() {
        AllZone.getInputControl().addObserver(this);
        AllZone.getStack().addObserver(this);
        AllZone.getPhase().addObserver(this);
    }

    public void update(Observable observable, Object obj) {
        Input tmp = AllZone.getInputControl().updateInput();
        if (tmp != null) {
            setInput(tmp);
        }
    }

    private void setInput(Input in) {
        input = in;
        input.showMessage();
    }

    public void showMessage() {
        input.showMessage();
    }

    public void selectButtonOK() {
        input.selectButtonOK();
    }

    public void selectButtonCancel() {
        input.selectButtonCancel();
    }

    public void selectPlayer(Player player) {
        input.selectPlayer(player);
    }

    public void selectCard(Card card, PlayerZone zone) {
        input.selectCard(card, zone);
    }

    @Override
    public String toString() {
        return input.toString();
    }
}
