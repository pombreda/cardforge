
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/*
import mtgForge.AllZone;
import mtgForge.Card;
import mtgForge.CardList;
import mtgForge.CardPanel;
import mtgForge.Constant;
import mtgForge.Display;
import mtgForge.MagicStack;
import mtgForge.player.PlayerZone;
*/

public class GuiDisplay3 extends javax.swing.JFrame implements Display {
    private GuiInput inputControl;



    public GuiDisplay3() {
        initComponents();

        addObservers();
        addListeners();
        addMenu();

        inputControl = new GuiInput();
    }


    public void show() {
        //causes an error if put in the constructor, causes some random null pointer exception
        AllZone.InputControl.updateObservers();

        setSize(1024, 740);
        super.show();
    }

    public void assignDamage(CardList blockers, int damage) {
        new Gui_MultipleBlockers3(blockers, damage, this);
    }

    private void addMenu()
    {
        JMenuItem humanGraveyard = new JMenuItem("View Graveyard");
        humanGraveyard.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                Card c[] = AllZone.Human_Graveyard.getCards();

                if(AllZone.NameChanger.shouldChangeCardName())
                  c = AllZone.NameChanger.changeCard(c);

                if(c.length == 0)
                    AllZone.Display.getChoiceOptional("Player's Grave", new String[]
                    {"no cards"});
                else
                    AllZone.Display.getChoiceOptional("Player's Grave", c);
            }
        });

        JMenuItem computerGraveyard = new JMenuItem("Computer - View Graveyard");
        computerGraveyard.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                Card c[] = AllZone.Computer_Graveyard.getCards();

                if(AllZone.NameChanger.shouldChangeCardName())
                  c = AllZone.NameChanger.changeCard(c);

                if(c.length == 0)
                    AllZone.Display.getChoiceOptional("Computer's Grave", new String[]
                    {"no cards"});
                else
                    AllZone.Display.getChoiceOptional("Computer's Grave", c);
            }
        });


        JMenuItem concedeGame = new JMenuItem("Concede Game");
        concedeGame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
              dispose();
              Constant.Runtime.WinLose.addLose();
              new Gui_WinLose();
            }
        });


        JMenuItem gameMenu = new JMenu("Menu");
        gameMenu.add(humanGraveyard);
        gameMenu.add(computerGraveyard);
        gameMenu.add(this.eotCheckboxForMenu);
        gameMenu.add(new JSeparator());
        gameMenu.add(concedeGame);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(gameMenu);
        menuBar.add(new MenuItem_HowToPlay());
        this.setJMenuBar(menuBar);
    }//addMenu()
    public MyButton getButtonOK() {
        MyButton ok = new MyButton() {
            public void select() {
                inputControl.selectButtonOK();
            }

            public boolean isSelectable() {
                return okButton.isEnabled();
            }

            public void setSelectable(boolean b) {
                okButton.setEnabled(b);
            }

            public String getText() {
                return okButton.getText();
            }

            public void setText(String text) {
                okButton.setText(text);
            }

            public void reset() {
                okButton.setText("OK");
            }
        };
        return ok;
    }//getButtonOK()

    public MyButton getButtonCancel() {
        MyButton cancel = new MyButton() {
            public void select() {
                inputControl.selectButtonCancel();
            }

            public boolean isSelectable() {
                return cancelButton.isEnabled();
            }

            public void setSelectable(boolean b) {
                cancelButton.setEnabled(b);
            }

            public String getText() {
                return cancelButton.getText();
            }

            public void setText(String text) {
                cancelButton.setText(text);
            }

            public void reset() {
                cancelButton.setText("Cancel");
            }
        };
        return cancel;
    }//getButtonCancel()

    public void showCombat(String message) {
        combatArea.setText(message);
    }

    public void showMessage(String s) {
        messageArea.setText(s);
    }

    //returned Object could be null
    public Object getChoiceOptional(String message, Object choices[]) {
        final JList list = new JList(choices);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if(choices[0] instanceof Card) {
            list.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent ev) {
                    if(list.getSelectedValue() instanceof Card)
                        updateCardDetail((Card) list.getSelectedValue());
                }
            });
        }
        int check = JOptionPane.showConfirmDialog(null, new JScrollPane(list), message,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if(check == JOptionPane.CANCEL_OPTION) return null;

        return list.getSelectedValue();
    }//getChoiceOptional()

    // returned Object will never be null
    public Object getChoice(String message, Object choices[]) {
        final JList list = new JList(choices);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if(choices[0] instanceof Card) {
            list.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent ev) {
                    if(list.getSelectedValue() instanceof Card)
                        updateCardDetail((Card) list.getSelectedValue());
                }
            });
        }
        Object o = list.getSelectedValue();
        while(o == null) {
            JOptionPane.showMessageDialog(null, new JScrollPane(list), message, JOptionPane.OK_OPTION);
            o = list.getSelectedValue();
        }

        return o;
    }//getChoice()

    private void addListeners() {
        //mouse Card Detail
        playerHandPanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));
        playerLandPanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));
        playerCreaturePanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));

        oppLandPanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));
        oppCreaturePanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));


        //opponent life mouse listener
        oppLifeLabel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                inputControl.selectPlayer(Constant.Player.Computer);
            }
        });

        //self life mouse listener
        playerLifeLabel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                inputControl.selectPlayer(Constant.Player.Human);
            }
        });

        //self play (land) ---- Mouse
        playerLandPanel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                Object o = playerLandPanel.getComponentAt(e.getPoint());
                if(o instanceof CardPanel) {
                    CardPanel cardPanel = (CardPanel) o;
                    inputControl.selectCard(cardPanel.getCard(), AllZone.Human_Play);
                }
            }
        });
        //self play (no land) ---- Mouse
        playerCreaturePanel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                Object o = playerCreaturePanel.getComponentAt(e.getPoint());
                if(o instanceof CardPanel) {
                    CardPanel cardPanel = (CardPanel) o;
                    inputControl.selectCard(cardPanel.getCard(), AllZone.Human_Play);
                }
            }
        });
        //self hand ---- Mouse
        playerHandPanel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                Object o = playerHandPanel.getComponentAt(e.getPoint());
                if(o instanceof CardPanel) {
                    CardPanel cardPanel = (CardPanel) o;
                    inputControl.selectCard(cardPanel.getCard(), AllZone.Human_Hand);
                }
            }
        });
        //*****************************************************************
        //computer

        //computer play (land) ---- Mouse
        oppLandPanel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                Object o = oppLandPanel.getComponentAt(e.getPoint());
                if(o instanceof CardPanel) {
                    CardPanel cardPanel = (CardPanel) o;
                    inputControl.selectCard(cardPanel.getCard(), AllZone.Computer_Play);
                }
            }
        });

        //computer play (no land) ---- Mouse
        oppCreaturePanel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                Object o = oppCreaturePanel.getComponentAt(e.getPoint());
                if(o instanceof CardPanel) {
                    CardPanel cardPanel = (CardPanel) o;
                    inputControl.selectCard(cardPanel.getCard(), AllZone.Computer_Play);
                }
            }
        });

    }//addListener()

    public void updateCardDetail(Card c) {
//      if(! c.isToken())
//        System.out.println(c +" " +c.getSpellAbility()[0].canPlay() +" " +c.getSpellAbility()[0].getManaCost());

        if(c == null) return;

        cdLabel1.setText("");
        cdLabel2.setText("");
        cdLabel3.setText("");
        cdLabel4.setText("");
        cdLabel5.setText("");
        cdArea.setText("");

        if(c.isLand()) cdLabel1.setText(c.getName());
        else cdLabel1.setText(c.getName() + "  - " + c.getManaCost());

        cdLabel2.setText(GuiDisplayUtil.formatCardType(c));

        if(c.isCreature()) {
            String stats = "" + c.getAttack() + " / " + c.getDefense();
            cdLabel3.setText(stats);
        }

        if(c.isCreature())
            cdLabel4.setText("Damage: " + c.getDamage() + " Assigned Damage: " + c.getAssignedDamage());

        if(c.isPlaneswalker()) cdLabel4.setText("Assigned Damage: " + c.getAssignedDamage());

        String uniqueID = c.getUniqueNumber() + " ";
        cdLabel5.setText("Card ID  " + uniqueID);

        this.cdArea.setText(c.getText());

        cdPanel.setBorder(GuiDisplayUtil.getBorder(c));

        //picture
        picturePanel.removeAll();
        picturePanel.add(GuiDisplayUtil.getPicture(c));
        picturePanel.revalidate();
    }//updateCardDetail()

    private void addObservers() {
        //Human Hand, Graveyard, and Library totals
        {//make sure to not interfer with anything below, since this is a very long method
            Observer o = new Observer() {
                public void update(Observable a, Object b) {
                    playerHandValue.setText("" + AllZone.Human_Hand.getCards().length);
                    playerGraveValue.setText("" + AllZone.Human_Graveyard.getCards().length);
                    playerLibraryValue.setText("" + AllZone.Human_Library.getCards().length);
                }
            };
            AllZone.Human_Hand.addObserver(o);
            AllZone.Human_Graveyard.addObserver(o);
            AllZone.Human_Library.addObserver(o);
        }

        //opponent Hand, Graveyard, and Library totals
        {//make sure to not interfer with anything below, since this is a very long method
            Observer o = new Observer() {
                public void update(Observable a, Object b) {
                    oppHandValue.setText("" + AllZone.Computer_Hand.getCards().length);
                    oppGraveValue.setText("" + AllZone.Computer_Graveyard.getCards().length);
                    oppLibraryValue.setText("" + AllZone.Computer_Library.getCards().length);
                }
            };
            AllZone.Computer_Hand.addObserver(o);
            AllZone.Computer_Graveyard.addObserver(o);
            AllZone.Computer_Library.addObserver(o);
        }


        //opponent life
        oppLifeLabel.setText("" + AllZone.Computer_Life.getLife());
        AllZone.Computer_Life.addObserver(new Observer() {
            public void update(Observable a, Object b) {
                int life = AllZone.Computer_Life.getLife();
                oppLifeLabel.setText("" + life);
            }
        });
        AllZone.Computer_Life.updateObservers();

        //player life
        playerLifeLabel.setText("" + AllZone.Human_Life.getLife());
        AllZone.Human_Life.addObserver(new Observer() {
            public void update(Observable a, Object b) {
                int life = AllZone.Human_Life.getLife();
                playerLifeLabel.setText("" + life);
            }
        });
        AllZone.Human_Life.updateObservers();

        //stack
        AllZone.Stack.addObserver(new Observer() {
            public void update(Observable a, Object b) {
                stackPanel.removeAll();
                MagicStack stack = AllZone.Stack;
                int count = 1;
                JLabel label;

                for(int i = stack.size() - 1; 0 <= i; i--) {
                    label = new JLabel("" + (count++) + ". " + stack.peek(i).getStackDescription());

                    //update card detail
                    final CardPanel cardPanel = new CardPanel(stack.peek(i).getSourceCard());
                    cardPanel.setLayout(new BorderLayout());
                    cardPanel.add(label);
                    cardPanel.addMouseMotionListener(new MouseMotionAdapter() {

                        public void mouseMoved(MouseEvent me) {
                            GuiDisplay3.this.updateCardDetail(cardPanel.getCard());
                        }//mouseMoved
                    });

                    stackPanel.add(cardPanel);
                }

                stackPanel.revalidate();
                stackPanel.repaint();
            }
        });
        AllZone.Stack.updateObservers();
        //END, stack


        //self hand
        AllZone.Human_Hand.addObserver(new Observer() {
            public void update(Observable a, Object b) {
                PlayerZone pZone = (PlayerZone) a;
                JPanel p = playerHandPanel;
                p.removeAll();

                Card c[] = pZone.getCards();
                JPanel panel;
                for(int i = 0; i < c.length; i++) {
                    panel = GuiDisplayUtil.getCardPanel(c[i]);
                    p.add(panel);
                }
                p.revalidate();
                p.repaint();
            }
        });
        AllZone.Human_Hand.updateObservers();
        //END, self hand

        //self play (land)
        AllZone.Human_Play.addObserver(new Observer() {
            public void update(Observable a, Object b) {
                PlayerZone pZone = (PlayerZone) a;
                JPanel p = playerLandPanel;
                p.removeAll();

                GuiDisplayUtil.setupLandPanel(p, AllZone.Human_Play.getCards());

                p.revalidate();
                p.repaint();
            }
        });
        AllZone.Human_Play.updateObservers();
        //END - self play (only land)


        //self play (no land)
        AllZone.Human_Play.addObserver(new Observer() {
            public void update(Observable a, Object b) {
                PlayerZone pZone = (PlayerZone) a;
                JPanel p = playerCreaturePanel;
                p.removeAll();

                GuiDisplayUtil.setupNoLandPanel(p, AllZone.Human_Play.getCards());

                p.revalidate();
                p.repaint();
            }
        });
        AllZone.Human_Play.updateObservers();
        //END - self play (no land)


        //computer play (no land)
        AllZone.Computer_Play.addObserver(new Observer() {
            public void update(Observable a, Object b) {
                PlayerZone pZone = (PlayerZone) a;
                JPanel p = oppCreaturePanel;
                p.removeAll();

                GuiDisplayUtil.setupNoLandPanel(p, AllZone.Computer_Play.getCards());

                p.revalidate();
                p.repaint();
            }
        });
        AllZone.Computer_Play.updateObservers();
        //END - computer play (no land)

        //computer play (land)
        AllZone.Computer_Play.addObserver(new Observer() {
            public void update(Observable a, Object b) {
                PlayerZone pZone = (PlayerZone) a;
                JPanel p = oppLandPanel;
                p.removeAll();

                GuiDisplayUtil.setupLandPanel(p, AllZone.Computer_Play.getCards());

                p.revalidate();
                p.repaint();
            }
        });
        AllZone.Computer_Play.updateObservers();
        //END - computer play (only land)

    }//addObservers()

    private void initComponents() {
       getContentPane().setLayout(new BorderLayout());

        //The left Part: messageArea, yesNoPanel, oppPanel, stackPanel, combatPanel, playerPanel
        JPanel left = new JPanel() {

            public Dimension getPreferredSize() {
                return new Dimension(300, super.getPreferredSize().height);
            }
        };
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        getContentPane().add(left, BorderLayout.WEST);

        //The middle Part: zonesPanel

        //The right Part: cdPanel, picturePanel
        JPanel right = new JPanel() {

            public Dimension getPreferredSize() {
                return new Dimension(210, super.getPreferredSize().height);
            }
        };
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        getContentPane().add(right, BorderLayout.EAST);

        //Preparing the Frame
        setTitle(Constant.ProgramName);
        setFont(new Font("Times New Roman", 0, 16));
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });
        JMenu menuBar = new JMenu("Menu");
        JMenuBar jMenuBar1 = new JMenuBar();
        jMenuBar1.add(menuBar);
        setJMenuBar(jMenuBar1);
        pack();

        initMsgYesNo(left);
        initOpp(left);
        initStackCombat(left);
        initPlayer(left);
        initZones();
        initCardPicture(right);
    }

    private void initMsgYesNo(JPanel left) {
        messageArea.setEditable(false);
        messageArea.setFont(getFont());
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        left.add(new JScrollPane(messageArea));
//        messagePane.setBounds(10, 20, 290, 100);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        JPanel yesNoPanel = new JPanel(new FlowLayout());
        yesNoPanel.setBorder(new EtchedBorder());
        yesNoPanel.add(cancelButton);
        yesNoPanel.add(okButton);
        left.add(yesNoPanel);
    }

    private void initOpp(JPanel left) {
        oppLifeLabel.setFont(new Font("MS Sans Serif", 0, 40));
        oppLifeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel oppLibraryLabel = new JLabel("Library:", SwingConstants.TRAILING);
        oppLibraryLabel.setFont(new Font("MS Sans Serif", 0, 18));

        JLabel oppHandLabel = new JLabel("Hand:", SwingConstants.TRAILING);
        oppHandLabel.setFont(new Font("MS Sans Serif", 0, 18));

        JLabel oppGraveLabel = new JLabel("Grave:", SwingConstants.TRAILING);
        oppGraveLabel.setFont(new Font("MS Sans Serif", 0, 18));

        oppHandValue.setFont(new Font("MS Sans Serif", 0, 18));
        oppHandValue.setHorizontalAlignment(SwingConstants.LEADING);

        oppLibraryValue.setFont(new Font("MS Sans Serif", 0, 18));
        oppLibraryValue.setHorizontalAlignment(SwingConstants.LEADING);

        oppGraveValue.setFont(new Font("MS Sans Serif", 0, 18));
        oppGraveValue.setHorizontalAlignment(SwingConstants.LEADING);

        JPanel oppNumbersPanel = new JPanel(new GridLayout(0, 2, 5, 1));
        oppNumbersPanel.add(oppHandLabel);
        oppNumbersPanel.add(oppHandValue);
        oppNumbersPanel.add(oppLibraryLabel);
        oppNumbersPanel.add(oppLibraryValue);
        oppNumbersPanel.add(oppGraveLabel);
        oppNumbersPanel.add(oppGraveValue);

        JPanel oppPanel = new JPanel();
        oppPanel.setBorder(new TitledBorder(new EtchedBorder(), "Computer"));
        oppPanel.setLayout(new FlowLayout());
        oppPanel.add(oppNumbersPanel);
        oppPanel.add(oppLifeLabel);
        left.add(oppPanel);
    }

    private void initStackCombat(JPanel left) {
        stackPanel.setLayout(new GridLayout(0, 1, 10, 10));
        JScrollPane stackPane = new JScrollPane(stackPanel);
        stackPane.setBorder(new EtchedBorder());
        left.add(stackPane);

        combatArea.setEditable(false);
        combatArea.setFont(getFont());
        combatArea.setLineWrap(true);
        combatArea.setWrapStyleWord(true);
        JScrollPane combatPane = new JScrollPane(combatArea);
        combatPane.setBorder(new TitledBorder(new EtchedBorder(), "Combat"));
        left.add(combatPane);
    }

    private void initPlayer(JPanel left) {
        playerLifeLabel.setFont(new Font("MS Sans Serif", 0, 40));
        playerLifeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel playerLibraryLabel = new JLabel("Library:", SwingConstants.TRAILING);
        playerLibraryLabel.setFont(new Font("MS Sans Serif", 0, 18));

        JLabel playerHandLabel = new JLabel("Hand:", SwingConstants.TRAILING);
        playerHandLabel.setFont(new Font("MS Sans Serif", 0, 18));

        JLabel playerGraveLabel = new JLabel("Grave:", SwingConstants.TRAILING);
        playerGraveLabel.setFont(new Font("MS Sans Serif", 0, 18));

        playerHandValue.setFont(new Font("MS Sans Serif", 0, 18));
        playerHandValue.setHorizontalAlignment(SwingConstants.LEADING);

        playerLibraryValue.setFont(new Font("MS Sans Serif", 0, 18));
        playerLibraryValue.setHorizontalAlignment(SwingConstants.LEADING);

        playerGraveValue.setFont(new Font("MS Sans Serif", 0, 18));
        playerGraveValue.setHorizontalAlignment(SwingConstants.LEADING);

        JPanel playerNumbersPanel = new JPanel(new GridLayout(0, 2, 5, 1));
        playerNumbersPanel.add(playerHandLabel);
        playerNumbersPanel.add(playerHandValue);
        playerNumbersPanel.add(playerLibraryLabel);
        playerNumbersPanel.add(playerLibraryValue);
        playerNumbersPanel.add(playerGraveLabel);
        playerNumbersPanel.add(playerGraveValue);

        JPanel playerPanel = new JPanel();
        playerPanel.setBorder(new TitledBorder(new EtchedBorder(), "Player"));
        playerPanel.setLayout(new FlowLayout());
        playerPanel.add(playerNumbersPanel);
        playerPanel.add(playerLifeLabel);
        left.add(playerPanel);
    }

    private void initZones() {
        oppLandPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        oppLandPanel.setBorder(new EtchedBorder());

        oppCreaturePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        oppCreaturePanel.setBorder(new EtchedBorder());

        playerCreaturePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        playerCreaturePanel.setBorder(new EtchedBorder());

        playerLandPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        playerLandPanel.setBorder(new EtchedBorder());

        playerHandPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        playerHandPanel.setBorder(new EtchedBorder());

        JPanel zonesPanel = new JPanel();
        zonesPanel.setLayout(new BoxLayout(zonesPanel, BoxLayout.Y_AXIS));
        zonesPanel.add(oppLandPanel);
        zonesPanel.add(oppCreaturePanel);
        zonesPanel.add(playerCreaturePanel);
        zonesPanel.add(playerLandPanel);
        zonesPanel.add(playerHandPanel);

        JScrollPane zonesPane = new JScrollPane(zonesPanel);
        zonesPane.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
        getContentPane().add(zonesPane, BorderLayout.CENTER);
    }

    private void initCardPicture(JPanel right) {
        cdLabel1.setFont(getFont());
        cdLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        cdLabel1.setText("jLabel3");

        cdLabel2.setFont(getFont());
        cdLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        cdLabel2.setText("jLabel4");

        cdLabel3.setFont(getFont());
        cdLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        cdLabel3.setText("jLabel5");

        cdLabel4.setFont(getFont());
        cdLabel4.setText("jLabel6");

        cdLabel5.setFont(getFont());
        cdLabel5.setText("jLabel7");

        JPanel cdLabels = new JPanel(new GridLayout(5, 0, 0, 5));
        cdLabels.add(cdLabel1);
        cdLabels.add(cdLabel2);
        cdLabels.add(cdLabel3);
        cdLabels.add(cdLabel4);
        cdLabels.add(cdLabel5);

        cdArea.setFont(getFont());
        cdArea.setLineWrap(true);
        cdArea.setWrapStyleWord(true);

        JScrollPane cdPane = new JScrollPane(cdArea);

        cdPanel.setLayout(new GridLayout(2, 1, 0, 5));
        cdPanel.setBorder(new EtchedBorder());
        cdPanel.add(cdLabels);
        cdPanel.add(cdPane);
        right.add(cdPanel);

        picturePanel.setBorder(new EtchedBorder());
        right.add(picturePanel);
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        inputControl.selectButtonCancel();
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        inputControl.selectButtonOK();
    }

    /**
     * Exit the Application
     */
    private void exitForm(WindowEvent evt) {
        dispose();
        Constant.Runtime.WinLose.addLose();
        new Gui_WinLose();
    }

    public boolean stopEOT() {return eotCheckboxForMenu.isSelected();}

     public static JCheckBoxMenuItem eotCheckboxForMenu = new JCheckBoxMenuItem("Stop at End of Turn", false);

    JButton   cancelButton        = new JButton();
    JButton   okButton            = new JButton();
    JTextArea messageArea         = new JTextArea();
    JTextArea cdArea              = new JTextArea();
    JTextArea combatArea          = new JTextArea();
    JPanel    stackPanel          = new JPanel();
    JPanel    oppLandPanel        = new JPanel();
    JPanel    oppCreaturePanel    = new JPanel();
    JPanel    playerCreaturePanel = new JPanel();
    JPanel    playerLandPanel     = new JPanel();
    JPanel    playerHandPanel     = new JPanel();
    JPanel    cdPanel             = new JPanel();
    JPanel    picturePanel        = new JPanel();
    JLabel    oppLifeLabel        = new JLabel();
    JLabel    playerLifeLabel     = new JLabel();
    JLabel    cdLabel1            = new JLabel();
    JLabel    cdLabel2            = new JLabel();
    JLabel    cdLabel3            = new JLabel();
    JLabel    cdLabel4            = new JLabel();
    JLabel    cdLabel5            = new JLabel();
    JLabel    oppHandValue        = new JLabel();
    JLabel    oppLibraryValue     = new JLabel();
    JLabel    oppGraveValue       = new JLabel();
    JLabel    playerHandValue     = new JLabel();
    JLabel    playerLibraryValue  = new JLabel();
    JLabel    playerGraveValue    = new JLabel();
}
//very hacky


class Gui_MultipleBlockers3 extends JFrame
{
  private int assignDamage;
  private GuiDisplay3 guiDisplay;

  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel mainPanel = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JLabel numberLabel = new JLabel();
  private JPanel jPanel3 = new JPanel();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JPanel creaturePanel = new JPanel();

  public static void main(String[] args)
  {
    CardList list = new CardList();
    list.add(AllZone.CardFactory.getCard("Elvish Piper", ""));
    list.add(AllZone.CardFactory.getCard("Lantern Kami", ""));
    list.add(AllZone.CardFactory.getCard("Frostling", ""));
    list.add(AllZone.CardFactory.getCard("Frostling", ""));

    for(int i = 0; i < 2; i++)
      new Gui_MultipleBlockers3(list, i+1, null);
  }

  Gui_MultipleBlockers3(CardList creatureList, int damage, GuiDisplay3 display)
  {
    this();
    assignDamage = damage;
    updateDamageLabel();//update user message about assigning damage
    guiDisplay = display;

    for(int i = 0; i < creatureList.size(); i++)
      creaturePanel.add(GuiDisplayUtil.getCardPanel(creatureList.get(i)));

    JDialog dialog = new JDialog(this, true);
    dialog.setTitle("Multiple Blockers");
    dialog.setContentPane(mainPanel);
    dialog.setSize(470, 260);
    dialog.show();
  }
  public Gui_MultipleBlockers3() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
//    setSize(470, 280);
//    show();
  }
  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    this.setTitle("Multiple Blockers");
    mainPanel.setLayout(null);
    numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
    numberLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    numberLabel.setText("Assign");
    numberLabel.setBounds(new Rectangle(52, 30, 343, 24));
    jPanel3.setLayout(borderLayout3);
    jPanel3.setBounds(new Rectangle(26, 75, 399, 114));
    creaturePanel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        creaturePanel_mousePressed(e);
      }
    });
    creaturePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        creaturePanel_mouseMoved(e);
      }
    });
    mainPanel.add(jPanel3, null);
    jPanel3.add(jScrollPane1, BorderLayout.CENTER);
    mainPanel.add(numberLabel, null);
    jScrollPane1.getViewport().add(creaturePanel, null);
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
  }

  void okButton_actionPerformed(ActionEvent e) {
    dispose();
  }

  void creaturePanel_mousePressed(MouseEvent e) {
    Object o = creaturePanel.getComponentAt(e.getPoint());
    if(o instanceof CardPanel)
    {
      CardPanel cardPanel = (CardPanel)o;
      Card c = cardPanel.getCard();
      c.setAssignedDamage(c.getAssignedDamage() + 1);

      if(guiDisplay != null)
        guiDisplay.updateCardDetail(c);
    }
    //reduce damage, show new user message, exit if necessary
    assignDamage--;
    updateDamageLabel();
    if(assignDamage == 0)
      dispose();
  }//creaturePanel_mousePressed()
  void updateDamageLabel()
  {
    numberLabel.setText("Assign " +assignDamage +" damage - click on card to assign damage");
  }

  void creaturePanel_mouseMoved(MouseEvent e)
  {
    Object o = creaturePanel.getComponentAt(e.getPoint());
    if(o instanceof CardPanel)
    {
      CardPanel cardPanel = (CardPanel)o;
      Card c = cardPanel.getCard();

      if(guiDisplay != null)
        guiDisplay.updateCardDetail(c);
    }
  }
}
