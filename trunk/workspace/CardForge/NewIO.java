import java.util.*;
import java.io.*;

public class NewIO
{
    private HashMap map = new HashMap();
    private final File file;
    
    public NewIO(String filename)
    {
        file = new File(filename);
        if(! file.exists())
            createBlankFile();
    }
    //would like one class to do all of the IO
    //internally just a Map is used, and written to the hard drive
    //WARNING - different parts of the program have to use different, unique keys
    //the data is immediately written, nothing is cached or buffered
    public void write(String key, Serializable data)
    {
        readData();
        map.put(key, data);
        
        ObjectOutputStream out = getWriter();

        try{
            out.writeObject(map);
            
            out.flush();
            out.close();
            out = null;
        }catch(IOException e){throw new RuntimeException("IO : write() error - " +e);}
    }
    public Object read(String key)
    {
        readData();
        return map.get(key);
    }
    public boolean containsKey(String key)
    {
        return map.containsKey(key);
    }
    private ObjectOutputStream getWriter()
    {
        try{             
            return new ObjectOutputStream(new FileOutputStream(file));
        }
        catch(IOException e) 
        { 
            throw new RuntimeException("IO : getWriter() - error - " +e);
        }
    }    
    private ObjectInputStream getReader()
    {
        try{
            return new ObjectInputStream(new FileInputStream(file));    
        }
        catch(IOException e) 
        { 
            throw new RuntimeException("IO : getReader() - error - " +e);
        }
    }//getReader()
    private void readData()
    {
        ObjectInputStream in = getReader();
        Object o;
        try{
            o = in.readObject();            
            in.close();
            in = null;
        }        
        catch(WriteAbortedException e)
        {
            //deletes current file
            //a new file will be constructed next time by the constructor
            try{
                in.close();
                in = null;
            }catch(IOException e2) {}
            file.delete();
            throw new RuntimeException("IO : readData() - WriterAbortedException - all old decks are lost, sorry - please restart");
        }
        catch(Exception e) {throw new RuntimeException("IO : readData() - error - " +e); }
        
        if(o instanceof HashMap)
            map = (HashMap)o;
    }
    private void createBlankFile()
    {
        try
        { 
            if(file.exists())
                file.delete();
            else
                file.createNewFile();
            
            ObjectOutputStream out = getWriter();
            out.writeObject(map);
            
            out.flush();
            out.close();
            out = null;            
        }
        catch(IOException e)
        {
            throw new RuntimeException("IO : createBlankFile() - error - " +e);
        }
    }//createBlankFile()
}
