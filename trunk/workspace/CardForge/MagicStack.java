import java.util.*;

public class MagicStack extends MyObservable
{
  private ArrayList stack = new ArrayList();

  public void reset() {stack.clear(); this.updateObservers();}

   public void add(SpellAbility sp)
  {
      push(sp);
  }
  public int size()
  {
    return stack.size();
  }
  public void push(SpellAbility sp)
  {
    stack.add(0, sp);
    this.updateObservers();
  }
  public SpellAbility pop()
  {
    SpellAbility sp = (SpellAbility) stack.remove(0);
    this.updateObservers();
    return sp;
  }
  //index = 0 is the top, index = 1 is the next to top, etc...
  public SpellAbility peek(int index)
  {
    return (SpellAbility) stack.get(index);
  }
  public SpellAbility peek()
  {
    return peek(0);
  }
  public ArrayList getSourceCards()
  {
    ArrayList a = new ArrayList();
    Iterator it = stack.iterator();
    while(it.hasNext())
      a.add(((SpellAbility)it.next()).getSourceCard());

    return a;
  }
}
