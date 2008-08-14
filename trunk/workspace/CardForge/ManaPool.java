public class ManaPool extends MyObservable
{
  private int mana[];

  public ManaPool()
  {
    reset();
  }
  public void reset()
  {
    mana = new int[6];
    this.updateObservers();
  }
  private int getIndex(String color)
  {
    if(color.equals(Constant.Color.Black))
      return 0;
    else if(color.equals(Constant.Color.Blue))
      return 1;
    else if(color.equals(Constant.Color.Colorless))
      return 2;
    else if(color.equals(Constant.Color.Green))
      return 3;
    else if(color.equals(Constant.Color.Red))
      return 4;
    else if(color.equals(Constant.Color.White))
      return 5;

    throw new RuntimeException("ManaPool:getIndex() invalid color argument " +color);
  }
  public int getColor(String color)
  {
    return mana[getIndex(color)];
  }
  public void subtractColor(String color)
  {
    mana[getIndex(color)]--;
    updateObservers();
  }
  public void subtractColor(String color, int n)
  {
    mana[getIndex(color)] = mana[getIndex(color)] - n;
    updateObservers();
  }
  public void addColor(String color)
  {
    mana[getIndex(color)]++;
    updateObservers();
  }
  public void addColor(String color, int n)
  {
    mana[getIndex(color)] = mana[getIndex(color)] + n;
    updateObservers();
  }
}
