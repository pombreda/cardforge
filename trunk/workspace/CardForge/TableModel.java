import java.util.*;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

class TableModel extends AbstractTableModel
{
  //holds 1 copy of each card, DOES NOT HOLD multiple cards with the same name
  private CardList dataNoCopies = new CardList();

  //holds multiple card
  //example: if there are 4 Elvish Pipers, dataNoCopies has 1 copy, and dataCopies has 3
  private CardList dataCopies = new CardList();

  //used by sort(), holds old data to compare with sorted data, to see if any change was made
  private CardList oldList = new CardList();

  private CardDetail cardDetail;
  private String column[] = {"Qty", "Name", "Cost", "Color", "Type", "Stats", "Rarity"};

  //used to resort(), used when addCard(Card) is called
  private int recentSortedColumn;
  private boolean recentAscending;

  public TableModel(CardDetail cd) {this(new CardList(), cd);}

  public TableModel(CardList inData, CardDetail in_cardDetail)
  {
    cardDetail = in_cardDetail;
    //intialize dataNoCopies and dataCopies
    addCard(inData);
  }
  public void clear()
  {
    dataNoCopies.clear();
    dataCopies.clear();
    fireTableDataChanged();
  }
  public CardList getCards()
  {
    CardList all = new CardList();
    all.addAll(dataCopies.toArray());
    all.addAll(dataNoCopies.toArray());

    return all;
  }
  public void removeCard(Card c)
  {
    //remove card from "dataCopies",
    //if not found there, remove card from "dataNoCopies"
    int index = findCardName(c.getName(), dataCopies);

    if(index != -1)//found card name
      dataCopies.remove(index);
    else
    {
      index = findCardName(c.getName(), dataNoCopies);
      dataNoCopies.remove(index);
    }

    fireTableDataChanged();
  }
  private int findCardName(String name, CardList list)
  {
    for(int i = 0; i < list.size(); i++)
      if(list.get(i).getName().equals(name))
        return i;

    return -1;
  }
  public void addCard(Card c)
  {
    if(0 == countQuantity(c, dataNoCopies))
      dataNoCopies.add(c);
    else
      dataCopies.add(c);

    fireTableDataChanged();
  }
  final public void addCard(CardList c)
  {
    for(int i = 0; i < c.size(); i++)
      addCard(c.get(i));
  }
  public Card rowToCard(int row)
  {
    return dataNoCopies.get(row);
  }
  private int countQuantity(Card c)
  {
    return countQuantity(c, dataNoCopies) + countQuantity(c, dataCopies);
  }
  //CardList data is either class members "dataNoCopies" or "dataCopies"
  private int countQuantity(Card c, CardList data)
  {
    int count = 0;
    for(int i = 0; i < data.size(); i++)
      //are the card names the same?
      if(data.get(i).getName().equals(c.getName()))
        count++;

    return count;
  }

  public int getRowCount()    {return dataNoCopies.size();}
  public int getColumnCount() {return column.length;}

  public String getColumnName(int n)            {return column[n];}
  public Object getValueAt(int row, int column) {return getColumn(dataNoCopies.get(row), column);}

  private Object getColumn(Card c, int column)
  {
    switch(column)
    {
      case 0: return new Integer(countQuantity(c));
      case 1: return c.getName();
      case 2: return c.getManaCost();
      case 3: return TableSorter.getColor(c);
      case 4: return GuiDisplayUtil.formatCardType(c);
      case 5: return c.isCreature() ? c.getAttack() +"/" +c.getDefense() : "";
      case 6: return c.getRarity();

      default: return "error";
    }
  }
  private void swap(Card[] c, int indexA, int indexB)
  {
    Card hold = c[indexA];
    c[indexA] = c[indexB];
    c[indexB] = hold;
  }

  public void addListeners(final JTable table)
  {
    //updates card detail, listens to any key strokes
    table.addKeyListener(new KeyListener()
    {
      public void keyPressed(KeyEvent ev){}
      public void keyTyped(KeyEvent ev){}
      public void keyReleased(KeyEvent ev)
      {
        int row = table.getSelectedRow();
        if(row != -1)
        {
          cardDetail.updateCardDetail(dataNoCopies.get(row));
        }
      }
    });
    //updates card detail, listens to any mouse clicks
    table.addMouseListener(new MouseAdapter()
    {
        public void mousePressed(MouseEvent e)
        {
          int row = table.getSelectedRow();
          if(row != -1)
          {
            cardDetail.updateCardDetail(dataNoCopies.get(row));
          }
        }
    });

    //sorts
    MouseListener mouse = new MouseAdapter()
    {
      public void mousePressed(MouseEvent e)
      {
        TableColumnModel columnModel = table.getColumnModel();
        int viewColumn = columnModel.getColumnIndexAtX(e.getX());
        int column = table.convertColumnIndexToModel(viewColumn);

        if(column != -1)
        {
          //sort ascending
          boolean change = sort(column, true);

          if(! change)
            sort(column, false);//sort descending

          fireTableDataChanged();
        }
      }//mousePressed()
    };//MouseListener
    table.getTableHeader().addMouseListener(mouse);
  }//addCardListener()

  //called by the GUI when a card is added to re-sort
  public void resort()
  {
    sort(recentSortedColumn, recentAscending);
    this.fireTableDataChanged();
  }

  //returns true if any data changed positions
  public boolean sort(int column, boolean ascending)
  {
    //used by addCard() to resort the cards
    recentSortedColumn = column;
    recentAscending = ascending;

    CardList all = new CardList();
    all.addAll(dataNoCopies.toArray());
    all.addAll(dataCopies.toArray());

    TableSorter sorter = new TableSorter(all, column, ascending);
    Card[] array = all.toArray();
    Arrays.sort(array, sorter);

    //determine if any data changed position
    boolean hasChanged = false;
    CardList check = removeDuplicateNames(array);
    for(int i = 0; i < check.size(); i++)
      //do the card names match?
      if(! check.get(i).getName().equals(dataNoCopies.get(i).getName()))
        hasChanged = true;

    //clear everything, and add sorted data back into the model
    dataNoCopies.clear();
    dataCopies.clear();
    addCard(new CardList(array));

    return hasChanged;
  }//sort()
  private CardList removeDuplicateNames(Card[] c)
  {
    TreeSet check = new TreeSet();
    CardList list = new CardList();

    for(int i = 0; i < c.length; i++)
    {
      if(! check.contains(c[i].getName()))
      {
        check.add(c[i].getName());
        list.add(c[i]);
      }
    }

    return list;
  }
}//CardTableModel