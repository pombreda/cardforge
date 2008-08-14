public interface Command extends java.io.Serializable
{
    public static Command Blank = new Command() {public void execute() {}};
    
    public void execute();    
}
