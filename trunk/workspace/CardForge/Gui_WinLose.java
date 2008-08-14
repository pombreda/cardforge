import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;


import java.awt.event.*;

public class Gui_WinLose extends JFrame
{
  private JLabel titleLabel = new JLabel();
  private JButton continueButton = new JButton();
  private JButton restartButton = new JButton();
  private JButton quitButton = new JButton();
  private JLabel statsLabel = new JLabel();
  private JPanel jPanel2 = new JPanel();
  private TitledBorder titledBorder1;
  private Border border1;

  public static void main(String[] args)
  {
    Constant.Runtime.GameType[0] = Constant.GameType.Sealed;

    Constant.Runtime.WinLose.addWin();
    Constant.Runtime.WinLose.addLose();


    //setup limited deck
    Deck deck = new Deck(Constant.GameType.Sealed);
    CardList pack = new CardList(BoosterPack.getBoosterPack(1).toArray());

    for(int i = 0; i < pack.size(); i++)
      if((i%2) == 0)
        deck.addSideboard(pack.get(i).getName());
      else
        deck.addMain(pack.get(i).getName());

    Constant.Runtime.HumanDeck[0] = deck;
    //end - setup limited deck

    new Gui_WinLose();
  }

  public Gui_WinLose() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    setup();

    Dimension screen = this.getToolkit().getScreenSize();
    setBounds(screen.width / 3, 100,  //position
              215, 370);  //size
    show();
  }
  private void setup()
  {
    WinLose winLose = Constant.Runtime.WinLose;

    //3 is the match length, 3 is the number of games
    //disable buttons if match is up, or human player won 2 or lost 2 games already
    if((winLose.countWinLose() == 3) || (winLose.getWin() == 2) || (winLose.getLose() == 2))
    {
//      editDeckButton.setEnabled(false);
      continueButton.setEnabled(false);
    }

    //show Wins and Loses
    statsLabel.setText("Won: " +winLose.getWin() +", Lost: " +winLose.getLose());

    //show "You Won" or "You Lost"
    if(winLose.didWinRecently())
      titleLabel.setText("You Won");
    else
      titleLabel.setText("You Lost");

  }//setup();
  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    border1 = BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140));
    titleLabel.setFont(new java.awt.Font("Dialog", 0, 26));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    titleLabel.setText("You Won");
    titleLabel.setBounds(new Rectangle(-4, 0, 198, 60));
    this.getContentPane().setLayout(null);
    continueButton.setBounds(new Rectangle(22, 21, 123, 30));
    continueButton.setText("Continue Match");
    continueButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        continueButton_actionPerformed(e);
      }
    });
    restartButton.setBounds(new Rectangle(22, 90, 123, 30));
    restartButton.setText("Restart Match");
    restartButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        restartButton_actionPerformed(e);
      }
    });
    quitButton.setBounds(new Rectangle(22, 158, 123, 30));
    quitButton.setText("Quit Match");
    quitButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        quitButton_actionPerformed(e);
      }
    });
    statsLabel.setFont(new java.awt.Font("Dialog", 0, 16));
    statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
    statsLabel.setText("Wins 2, Lost 1");
    statsLabel.setBounds(new Rectangle(12, 59, 170, 30));
    jPanel2.setBorder(BorderFactory.createLineBorder(Color.black));
    jPanel2.setBounds(new Rectangle(20, 104, 166, 217));
    jPanel2.setLayout(null);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
    });
    this.getContentPane().add(statsLabel, null);
    this.getContentPane().add(jPanel2, null);
    jPanel2.add(continueButton, null);
    jPanel2.add(quitButton, null);
    jPanel2.add(restartButton, null);
    this.getContentPane().add(titleLabel, null);
  }

  void editDeckButton_actionPerformed(ActionEvent e) {
    Command exit = new Command()
    {
      public void execute()
      {
        new Gui_WinLose();
      }
    };
    Gui_DeckEditor editor = new Gui_DeckEditor();

    editor.show(exit);

    dispose();
  }//editDeckButton_actionPerformed()

  void continueButton_actionPerformed(ActionEvent e) {
    //open up "Game" screen
//    AllZone.Computer_Play.reset();//sometimes computer has creature in play in the 2nd game of the match
    AllZone.GameAction.newGame(Constant.Runtime.HumanDeck[0], Constant.Runtime.ComputerDeck[0]);
    AllZone.Display.show();

    dispose();
  }

  void restartButton_actionPerformed(ActionEvent e) {
    Constant.Runtime.WinLose.reset();
    AllZone.GameAction.newGame(Constant.Runtime.HumanDeck[0], Constant.Runtime.ComputerDeck[0]);
    AllZone.Display.show();

    dispose();
  }

  void quitButton_actionPerformed(ActionEvent e) {
    new Gui_NewGame();
    dispose();
  }

  void this_windowClosing(WindowEvent e) {
    quitButton_actionPerformed(null);
  }
}