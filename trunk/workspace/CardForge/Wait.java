import java.util.*;

public class Wait extends Thread
{
    public static void delay() 
    {
	Thread w = new Wait();
	try{
	    w.sleep(1000);
	}catch(Exception ex) {throw new RuntimeException("Wait : delay() " +ex);}
    }
}