public class Phase extends MyObservable
{
  private int phaseIndex;
  private int turn;

  private String phases[][] =
  {
    //human's turn
    {Constant.Player.Human    , Constant.Phase.Untap}                                  ,
//	{Constant.Player.Human    , Constant.Phase.Upkeep}                                  ,
    {Constant.Player.Human    , Constant.Phase.Draw}                                     ,
    {Constant.Player.Human    , Constant.Phase.Main1}                                  ,
    {Constant.Player.Human    , Constant.Phase.Combat_Declare_Attackers}               ,
    {Constant.Player.Computer , Constant.Phase.Combat_Declare_Blockers}                ,
    {Constant.Player.Human    , Constant.Phase.Combat_Declare_Blockers_InstantAbility} ,
    {Constant.Player.Computer , Constant.Phase.Combat_Declare_Blockers_InstantAbility} ,
    {Constant.Player.Human    , Constant.Phase.Combat_Damage}                          ,
    {Constant.Player.Human    , Constant.Phase.Main2}                                  ,
    {Constant.Player.Human    , Constant.Phase.At_End_Of_Turn}                         ,
//	{Constant.Player.Computer , Constant.Phase.End_Of_Turn}                            ,
    {Constant.Player.Human    , Constant.Phase.Until_End_Of_Turn}                      ,
    {Constant.Player.Human    , Constant.Phase.Cleanup}                                  ,

    //computer's turn
    {Constant.Player.Computer    , Constant.Phase.Untap}                                  ,
    {Constant.Player.Computer    , Constant.Phase.Draw}                                     ,
    {Constant.Player.Computer , Constant.Phase.Main1}                                  ,
    {Constant.Player.Computer , Constant.Phase.Combat_Declare_Attackers}               ,
    {Constant.Player.Human    , Constant.Phase.Combat_Declare_Blockers}                ,
    {Constant.Player.Computer , Constant.Phase.Combat_Declare_Blockers_InstantAbility} ,
    {Constant.Player.Human    , Constant.Phase.Combat_Declare_Blockers_InstantAbility} ,
    {Constant.Player.Human    , Constant.Phase.Combat_Damage}                          ,
    {Constant.Player.Computer , Constant.Phase.Main2}                                  ,
    {Constant.Player.Computer , Constant.Phase.At_End_Of_Turn}                         ,
    {Constant.Player.Human    , Constant.Phase.End_Of_Turn}                            ,
    {Constant.Player.Computer , Constant.Phase.Until_End_Of_Turn}                      ,
    {Constant.Player.Computer    , Constant.Phase.Cleanup}                                  ,
  };

  public Phase()
  {
    reset();
  }
  public void reset()
  {
    turn = 1;
    phaseIndex = 0;
    this.updateObservers();
  }
  public void setPhase(String phase, String player)
  {
    phaseIndex = findIndex(phase, player);
    this.updateObservers();
  }
  public void nextPhase()
  {
//    System.out.println("next phase - " +getActivePlayer() +" " +getPhase());

    phaseIndex++;
    if(phases.length <= phaseIndex)
      phaseIndex = 0;

    if(getPhase().equals(Constant.Phase.Untap))
      turn++;

    this.updateObservers();
  }
  public synchronized boolean is(String phase, String player)
  {
    return (getPhase().equals(phase) && getActivePlayer().equals(player));
  }
  private int findIndex(String phase, String player)
  {
    for(int i = 0; i < phases.length; i++)
    {
      if(player.equals(phases[i][0]) && phase.equals(phases[i][1]))
        return i;
    }
    throw new RuntimeException("Phase : findIndex() invalid argument, phase = " +phase +" player = " +player);
  }
  public String getActivePlayer()
  {
    return phases[phaseIndex][0];
  }
  public String getPhase()
  {
    return phases[phaseIndex][1];
  }
  public int getTurn()
  {
    return turn;
  }
  public void setTurn(int in_turn)
  {
    turn = in_turn;
  }
  public static void main(String args[])
  {
    Phase phase = new Phase();
    for(int i = 0; i < phase.phases.length + 3; i++)
    {
      System.out.println(phase.getActivePlayer() +" " +phase.getPhase());
      phase.nextPhase();
    }
  }
}