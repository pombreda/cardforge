package forge;
//import java.util.*;
import javax.swing.SwingUtilities;

import forge.error.ErrorViewer;


public class ComputerAI_Burn2 implements Computer {

    public void main1() {
        ComputerUtil.chooseLandsToPlay();
        Runnable run = new Runnable() {
            public void run() {
                synchronized(ComputerAI_Burn2.this) {
                    if(AllZone.Stack.size() == 0
                            && AllZone.Phase.is(Constant.Phase.Main1, AllZone.ComputerPlayer)) {
                    	
                        if(AllZone.Stack.size() == 0
                                && AllZone.Phase.is(Constant.Phase.Main1, AllZone.ComputerPlayer)) {
                            
                            //AllZone.Phase.nextPhase();
                            //for debugging: System.out.println("need to nextPhase(ComputerAI_Burn2.running) = true; Note, this is untested, did it work?");
                            AllZone.Phase.setNeedToNextPhase(true);
                        }
                        
                    }//if
                }//synchronized
            }//run()
        };//Runnable
        try {
            SwingUtilities.invokeLater(run);
//      Thread thread = new Thread(run);
//      thread.start();
        } catch(Exception ex) {
            ErrorViewer.showError(ex);
            throw new RuntimeException("ComputerAI_Burn : main1() error, " + ex);
        }
        
    }//main1()
    
    public void main2() {
    	ComputerUtil.chooseLandsToPlay();
        //AllZone.Phase.nextPhase();
        //for debugging: System.out.println("need to nextPhase(ComputerAI_Burn2.main2) = true; Note, this is not tested, did it work?");
        AllZone.Phase.setNeedToNextPhase(true);
    }
    
	public void begin_combat() {
		AllZone.Phase.setNeedToNextPhase(true);
	}
    
    public void declare_attackers_after()
    {
    	 AllZone.Phase.setNeedToNextPhase(true);
    }
    
    public void declare_attackers() {
        final Combat c = ComputerUtil.getAttackers();
        c.setAttackingPlayer(AllZone.Combat.getAttackingPlayer());
        c.setDefendingPlayer(AllZone.Combat.getDefendingPlayer());
        AllZone.Combat = c;
        
        Card[] att = c.getAttackers();
        for(int i = 0; i < att.length; i++)
            att[i].tap();
        
        AllZone.Computer_Battlefield.updateObservers();
        
        CombatUtil.showCombat();
        
        //AllZone.Phase.nextPhase();
        //for debugging: System.out.println("need to nextPhase(ComputerAI_Burn2.declare_attackers) = true");
        AllZone.Phase.setNeedToNextPhase(true);
    }
    
    public void declare_blockers() {
        Combat c = ComputerUtil.getBlockers();
        c.setAttackingPlayer(AllZone.Combat.getAttackingPlayer());
        c.setDefendingPlayer(AllZone.Combat.getDefendingPlayer());
        AllZone.Combat = c;
        
        CombatUtil.showCombat();
        
        //AllZone.Phase.nextPhase();
        //for debugging: System.out.println("need to nextPhase(ComputerAI_Burn2.declare_blockers) = true");
        AllZone.Phase.setNeedToNextPhase(true);
        
        //System.out.println(Arrays.asList(c.getAttackers()));
        //System.out.println(Arrays.asList(c.getBlockers()));
    }
    
    public void declare_blockers_after() {
        //AllZone.Phase.nextPhase();
        //for debugging: System.out.println("need to nextPhase(ComputerAI_Burn2.declre_blockers_after) = true");
        AllZone.Phase.setNeedToNextPhase(true);
    }
    
    public void after_declare_blockers()
    {
    	AllZone.Phase.setNeedToNextPhase(true);
    }
    
    public void end_of_combat()
    {
    	AllZone.Phase.setNeedToNextPhase(true);
    }
    
    //end of Human's turn
    public void end_of_turn() {
        //AllZone.Phase.nextPhase();
        //for debugging: System.out.println("need to nextPhase(ComputerAI_Burn2.end_of_turn) = true");
        AllZone.Phase.setNeedToNextPhase(true);
    }
    
    //must shuffle this
    public Card[] getLibrary() {
        CardFactory cf = AllZone.CardFactory;
        CardList library = new CardList();
        for(int i = 0; i < 4; i++) {
            library.add(cf.getCard("Lightning Bolt", AllZone.ComputerPlayer));
            library.add(cf.getCard("Shock", AllZone.ComputerPlayer));
            library.add(cf.getCard("Steel Wall", AllZone.ComputerPlayer));
        }
        for(int i = 0; i < 3; i++) {
            library.add(cf.getCard("Char", AllZone.ComputerPlayer));
            library.add(cf.getCard("Shock", AllZone.ComputerPlayer));
//      library.add(cf.getCard("Nevinyrral's Disk", AllZone.ComputerPlayer));
        }
        for(int i = 0; i < 2; i++) {
            library.add(cf.getCard("Pyroclasm", AllZone.ComputerPlayer));
//      library.add(cf.getCard("Hidetsugu's Second Rite", AllZone.ComputerPlayer));
//      library.add(cf.getCard("Char", AllZone.ComputerPlayer));
            library.add(cf.getCard("Flamebreak", AllZone.ComputerPlayer));
        }
        
        library.add(cf.getCard("Lava Spike", AllZone.ComputerPlayer));
        

        //	library.add(cf.getCard("Tanglebloom", AllZone.ComputerPlayer));
        
        for(int i = 0; i < 17; i++)
            library.add(cf.getCard("Mountain", AllZone.ComputerPlayer));
        
        if(library.size() != 40)
            throw new RuntimeException("ComputerAI_Burn : getLibrary() error, library size is " + library.size());
        
        return library.toArray();
    }

    public void stack_not_empty() {
        //same as Input.stop() method
        //ends the method
        //different than methods because this isn't a phase like Main1 or Declare Attackers
        AllZone.InputControl.resetInput();
        AllZone.InputControl.updateObservers();
    }
}