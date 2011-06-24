package forge;
public interface Command extends java.io.Serializable
{
    public void execute();    

    public static final Command Blank = new Command() {
	    private static final long serialVersionUID = 2689172297036001710L;	
		public void execute() { ; }
	};
}
