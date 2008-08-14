import java.util.Observable;

public class MyObservable extends Observable
{
  public final void updateObservers()
  {
    this.setChanged();
    this.notifyObservers();
  }
}

