import java.util.*;

public class CommandList implements java.io.Serializable, Command
{
  private ArrayList a = new ArrayList();

  //bug fix, when token is pumped up like with Giant Growth
  //and Sorceress Queen targets token, the effects need to be done
  //in this order, weird I know, DO NOT CHANGE THIS
  public void add(Command c) {a.add(0,c);}


  public Command get(int i) {return (Command) a.get(i);}
  public Command remove(int i) {return (Command)a.remove(i);}
  public int size() {return a.size();}
  public void clear() {a.clear();}

  public void execute()
  {
    for(int i = 0; i < size(); i++)
      get(i).execute();
  }

}
