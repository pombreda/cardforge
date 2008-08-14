import javax.swing.*;
import java.awt.event.*;

public class MenuItem_HowToPlay extends JMenuItem
{
  public MenuItem_HowToPlay()
  {
    super("(How to Play)");

    this.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent a)
      {
        String text = getString();

        JTextArea area = new JTextArea(text, 25, 40);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);

        JPanel p = new JPanel();
        area.setBackground(p.getBackground());

        JOptionPane.showMessageDialog(null, new JScrollPane(area), "How to play", JOptionPane.INFORMATION_MESSAGE);
      }
    });
  }//constructor

  private String getString()
  {
    String newLine = "\r\n\r\n";
    StringBuffer s = new StringBuffer();

    s.append("How to Play" +newLine);
    s.append("1.  This game is similar to many other trading card games.  You start out with 20 life and your goal is to reduce your opponents life to 0 by attacking with your creatures." +newLine);
    s.append("2.  You use land to pay for spells.  You can play one land a turn." +newLine);
    s.append("3.  Each land produces a different magical energy.  This magical energy is shortened to one letter on cards.\r\n");
    s.append("    Forests make G\r\n" );
    s.append("    Swamps make B\r\n" );
    s.append("    Plains make W\r\n" );
    s.append("    Islands make U\r\n" );
    s.append("    Mountains make R" +newLine);
    s.append("4.  Each card has a name and a cost.  The cost looks like this \"2GG\" A cost like that would require two Forest lands and two other lands.  The number 2 can be paid for by any land.  A cost like \"R\", would require a Mountain land." +newLine);
    s.append("5.  Creature cards stay in play and can attack on the turn AFTER they are played.  A creature's attack and defense is shown like 2/4 meaning that the creature has an attack power of 2 and a defense of 4.  If this creature receives 4 damage it is put into the graveyard." +newLine);
    s.append("6.  When you attack with your creatures the computer has a chance to block with his creatures.  When you attack you \"tap\" your creatures by turning them sideways.  Your creatures will untap during your next turn.  When you block, only untapped creatures can block.  Usually a creature cannot attack and block during the same turn." +newLine);
    s.append("7.  Sorcery and Instant cards have an effect on the game. After you play any card it goes on the stack to the left, click OK and the stack will clear.  Sorcery cards can only be played during your turn and when the stack is empty.  Instant cards can be played at any time and are more versatile." +newLine);
    s.append("8.  If a card has a target, you get to choose that target.  If the target is a player, click on that player's life points." +newLine);
    s.append("9.  When you mulligan, the cards in your hand are shuffled into you deck and you are given 1 less card." +newLine);

    s.append("10.  In sealed mode you are given 75 cards and you have to make your deck from just those cards." +newLine);
    s.append("11.  In draft mode you select 1 card at a time and then make your deck from just those cards.  After you are done drafting you have to type in a filename, then go to the Deck Editor and from the menu select \"Open Deck - Draft\" and find the filename.  This will allow you to construct your deck.  You can then play against the other 7 computer opponents that were drafting with you." +newLine);

    s.append("Abilities" +newLine);
    s.append("1.  Some cards like Elvish Piper have abilities that can be played.  These abilities can be played starting during your next turn.  Many abilities, like Elvish Piper has, are useful because they can be played many times." +newLine);
    s.append("2.  Some abilities make you sacrifice that card by putting it in the graveyard like Mogg Fanatic. A sacrifice ability can only be used once." +newLine);

    s.append("Specific Abilities" +newLine);
    s.append("1.  Flying:  Flying makes your creatures harder to block.  Only creatures with flying can block other flyers.  Creatures with flying are harder to block." +newLine);
    s.append("2.  Haste:  Haste lets a creature attack or use any abilities immediately during this turn." +newLine);
    s.append("3.  Fear:  Creatures with fear can only be blocked by artifact or black creatures.  Creatures with fear are harder to block." +newLine);
    s.append("4.  Cycling:  When you cycle a card you pay some cost like 2 and then you discard that card, and then draw a new card.  Cycling helps make your deck more versatile." +newLine);
    s.append("5.  Vigilance:  This means that the creature will not tap when attacking.  This creature can both attack and block during the same turn." +newLine);
    s.append("6.  Trample:  If you use 2/1 creature to block an attacking 3/4 creature with trample, you will still receive 2 damage because the 3/4 trampled over your 2/1 creature.  Trample damage is calculated by (attack - blocker's defense), in this case 3-1 which is 2." +newLine);
    s.append("Planeswalkers" +newLine);
    s.append("There are only 3 planeswalkers (Liliana Vess, Garruk Wildspeaker, Chandra Nalaar) but they have specific rules.  " +newLine);
    s.append("You can only use one ability a turn.  A planeswalker can be attacked, but you can also block with your creatures.  For each 1 damage a planeswalker receives, you remove 1 counter.  When a planeswalker doesn't have any counters, it goes to the graveyard.");

    return s.toString();
  }
}//MenuItem_HowToPlay

