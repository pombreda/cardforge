import javax.swing.*;

public class CardPanel extends JPanel
{
  private Card card;

  public CardPanel(Card card)
  {
    this.card = card;
  }
  public Card getCard()
  {
    return card;
  }
}
