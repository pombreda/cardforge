
package forge;


public class Input_StackNotEmpty extends Input implements java.io.Serializable {
    private static final long serialVersionUID = -3015125043127874730L;
    
    @Override
    public void showMessage() {
        updateGUI();
        
        ButtonUtil.enableOnlyOK();
        String phase = AllZone.Phase.getPhase();
        String player = AllZone.Phase.getActivePlayer();
        AllZone.Display.showMessage("Spells or Abilities on are on the Stack\nPhase: " + phase + ", Player: "
                + player);
    }
    
    @Override
    public void selectButtonOK() {
        updateGUI();
        
        SpellAbility sa = AllZone.Stack.pop();
        Card c = sa.getSourceCard();
        
        final Card crd = c;
        if(sa.isBuyBackAbility()) {
            c.addReplaceMoveToGraveyardCommand(new Command() {
                private static final long serialVersionUID = -2559488318473330418L;
                
                public void execute() {
                    PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, crd.getController());
                    AllZone.GameAction.moveTo(hand, crd);
                }
            });
        }
        
        // To stop Copied Spells from going into the graveyard.
        if(sa.getSourceCard().isCopiedSpell()) {
            c.addReplaceMoveToGraveyardCommand(new Command() {
                private static final long serialVersionUID = -2559488318473330418L;                
                public void execute() {
                }
            });
        }
        sa.resolve();
        
        if(sa.getSourceCard().getKeyword().contains("Draw a card.")) AllZone.GameAction.drawCard(sa.getSourceCard().getController());
        
        for(int i = 0; i < sa.getSourceCard().getKeyword().size(); i++) {
            String k = sa.getSourceCard().getKeyword().get(i);
            if(k.startsWith("Scry")) {
                String kk[] = k.split(" ");
                AllZone.GameAction.scry(sa.getSourceCard().getController(), Integer.parseInt(kk[1]));
            }
        }
        AllZone.GameAction.checkStateEffects();
        
        //special consideration for "Beacon of Unrest" and other "Beacon" cards
        if((c.isInstant() || c.isSorcery()) && (!c.getName().startsWith("Beacon"))
                && (!c.getName().startsWith("Pulse")) && !AllZone.GameAction.isCardRemovedFromGame(c)) //hack to make flashback work
        {
            if(c.getReplaceMoveToGraveyard().size() == 0) AllZone.GameAction.moveToGraveyard(c);
            else c.replaceMoveToGraveyard();
        }
        

        //update all zones, something things arent' updated for some reason
        AllZone.Human_Hand.updateObservers();
        AllZone.Human_Play.updateObservers();
        AllZone.Computer_Play.updateObservers();
        
        if(AllZone.InputControl.getInput() == this) AllZone.InputControl.resetInput();
    }
    
    @Override
    public void selectCard(Card card, PlayerZone zone) {
        InputUtil.playInstantAbility(card, zone);
    }//selectCard()
    
    private void updateGUI() {
        AllZone.Computer_Play.updateObservers();
        AllZone.Human_Play.updateObservers();
        AllZone.Human_Hand.updateObservers();
    }
}
