import java.io.*;
import java.util.*;

public class IO
{
  private final File file;

  private Map map = new HashMap();

  public IO(String filename)
  {
    file = new File(filename);
    try{
      if(! file.exists())
      {
        file.createNewFile();
        writeMap();
      }
    }catch(Exception ex){throw new RuntimeException("IO : constructor error, bad filename - " +filename +", error message - "+ex.getMessage());}
  }

  private void readMap()
  {
    try{
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
      map = (Map)in.readObject();
      in.close();
    }catch(Exception ex){throw new RuntimeException("IO : readMap() error, " +ex.getMessage());}
  }
  private void writeMap()
  {
    try{
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
      out.writeObject(map);

      out.flush();
      out.close();

    }catch(Exception ex){throw new RuntimeException("IO : writeMap error, " +ex.getMessage());}
  }

  public void writeObject(String key, Serializable ser)
  {
    readMap();
    map.put(key, ser);
    writeMap();
  }
  //may return null;
  public Object readObject(String key)
  {
    readMap();
    return map.get(key);
  }
  public void deleteObject(String key)
  {
    readMap();
    map.remove(key);
    writeMap();
  }
  public ArrayList getKeyList()
  {
    readMap();

    ArrayList list = new ArrayList();
    Iterator it = map.keySet().iterator();
    while(it.hasNext())
      list.add(it.next());
    return list;
  }
  public String[] getKeyString()
  {
    ArrayList list = getKeyList();
    String[] s = new String[list.size()];
    list.toArray(s);
    return s;
  }
}