public interface Display
{
    public Object getChoice(String message, Object choices[]);
    public Object getChoiceOptional(String message, Object choices[]);
    public void showMessage(String s);
    public MyButton getButtonOK();
    public MyButton getButtonCancel();
//    public void showStatus(String message);
    public void showCombat(String message);
    public void show();

    public boolean stopEOT();

    //assigns combat damage, used by Combat.setAssignedDamage()
    public void assignDamage(CardList blockers, int damage);
}
