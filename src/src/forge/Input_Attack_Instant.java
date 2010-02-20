
package forge;


public class Input_Attack_Instant extends Input {
    private static final long serialVersionUID = -8244057741266587006L;
    
    @Override
    public void showMessage() {
        //GameActionUtil.executeExaltedEffects();
        
        AllZone.Combat.verifyCreaturesInPlay();
        CombatUtil.showCombat();
        
        ButtonUtil.enableOnlyOK();
        AllZone.Display.showMessage("Declare Attackers: Play Instants and Abilities");
    }
    
    @Override
    public void selectButtonOK() {
        //AllZone.Phase.nextPhase();
        //for debugging: System.out.println("need to nextPhase(Input_Block_Instant.selectButtonOK) = true");
        AllZone.Phase.setNeedToNextPhase(true);
    }
    
    @Override
    public void selectCard(Card card, PlayerZone zone) {
        InputUtil.playInstantAbility(card, zone);
    }//selectCard()
}
