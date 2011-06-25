package forge;


import forge.card.cardFactory.CardFactoryUtil;
import forge.card.spellability.SpellAbility;
import forge.error.ErrorViewer;
import forge.gui.ForgeAction;
import forge.gui.GuiUtils;
import forge.gui.game.CardDetailPanel;
import forge.gui.game.CardPanel;
import forge.gui.game.CardPicturePanel;
import forge.gui.input.Input_Attack;
import forge.gui.input.Input_Block;
import forge.gui.input.Input_PayManaCost;
import forge.gui.input.Input_PayManaCost_Ability;
import forge.properties.ForgePreferences;
import forge.properties.ForgeProps;
import forge.properties.NewConstants;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout.Node;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Observable;
import java.util.Observer;

import static org.jdesktop.swingx.MultiSplitLayout.parseModel;

/**
 * <p>GuiDisplay3 class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class GuiDisplay3 extends JFrame implements CardContainer, Display, NewConstants, NewConstants.GUI.GuiDisplay, NewConstants.LANG.GuiDisplay {
    /** Constant <code>serialVersionUID=4519302185194841060L</code> */
    private static final long serialVersionUID = 4519302185194841060L;

    private GuiInput inputControl;

    Font statFont = new Font("Dialog", Font.PLAIN, 12);
    Font lifeFont = new Font("Dialog", Font.PLAIN, 40);
    Font checkboxFont = new Font("Dialog", Font.PLAIN, 9);


    /** Constant <code>greenColor</code> */
    public static Color greenColor = new Color(0, 164, 0);

    private Action HUMAN_GRAVEYARD_ACTION;
    private Action HUMAN_REMOVED_ACTION;
    private Action HUMAN_FLASHBACK_ACTION;
    private Action COMPUTER_GRAVEYARD_ACTION;
    private Action COMPUTER_REMOVED_ACTION;
    private Action CONCEDE_ACTION;
    public Card cCardHQ;

    //private CardList multiBlockers = new CardList();

    /**
     * <p>Constructor for GuiDisplay3.</p>
     */
    public GuiDisplay3() {
        AllZone.setDisplay(this);
        setupActions();
        initComponents();

        addObservers();
        addListeners();
        addMenu();
        inputControl = new GuiInput();
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            //causes an error if put in the constructor, causes some random null pointer exception
            AllZone.getInputControl().updateObservers();

            //Use both so that when "un"maximizing, the frame isn't tiny
            setSize(1024, 740);
            System.out.println(getExtendedState());
            setExtendedState(Frame.MAXIMIZED_BOTH);
            System.out.println(getExtendedState());
        }
        super.setVisible(visible);
    }

    /** {@inheritDoc} */
    public void assignDamage(Card attacker, CardList blockers, int damage) {
        if (damage <= 0)
            return;
        new Gui_MultipleBlockers3(attacker, blockers, damage, this);
    }

    /*
    public void addAssignDamage(Card attacker, Card blocker, int damage)
    {
        multiBlockers.add(blocker);
    }

    public void addAssignDamage(Card attacker, int damage) {
        //new Gui_MultipleBlockers3(attacker, blockers, damage, this);
        new Gui_MultipleBlockers3(attacker, multiBlockers, damage, this);
    }
    */

    /**
     * <p>setupActions.</p>
     */
    private void setupActions() {
        HUMAN_GRAVEYARD_ACTION = new ZoneAction(AllZone.getHumanGraveyard(), HUMAN_GRAVEYARD);
        HUMAN_REMOVED_ACTION = new ZoneAction(AllZone.getHumanExile(), HUMAN_REMOVED);
        HUMAN_FLASHBACK_ACTION = new ZoneAction(AllZone.getHumanExile(), HUMAN_FLASHBACK) {

            private static final long serialVersionUID = 8120331222693706164L;

            @Override
            protected Card[] getCards() {
                return CardFactoryUtil.getGraveyardActivationCards(AllZone.getHumanPlayer()).toArray();
            }

            @Override
            protected void doAction(Card c) {
                if (!c.isLand()) {
                    SpellAbility[] sa = c.getSpellAbility();
                    sa[1].setActivatingPlayer(AllZone.getHumanPlayer());
                    if (sa[1].canPlay() && !c.isUnCastable()) AllZone.getGameAction().playSpellAbility(sa[1]);
                } else    // PlayLand checks if the land can be played
                    AllZone.getHumanPlayer().playLand(c);
            }
        };
        COMPUTER_GRAVEYARD_ACTION = new ZoneAction(AllZone.getComputerGraveyard(), COMPUTER_GRAVEYARD);
        COMPUTER_REMOVED_ACTION = new ZoneAction(AllZone.getComputerExile(), COMPUTER_REMOVED);
        CONCEDE_ACTION = new ConcedeAction();
    }

    /**
     * <p>addMenu.</p>
     */
    private void addMenu() {
        Object[] obj = {
                HUMAN_GRAVEYARD_ACTION, HUMAN_REMOVED_ACTION, HUMAN_FLASHBACK_ACTION, COMPUTER_GRAVEYARD_ACTION,
                COMPUTER_REMOVED_ACTION, new JSeparator(),
                playsoundCheckboxForMenu, new JSeparator(), ErrorViewer.ALL_THREADS_ACTION,
                CONCEDE_ACTION};

        JMenu gameMenu = new JMenu(ForgeProps.getLocalized(MENU_BAR.MENU.TITLE));
        for (Object o : obj) {
            if (o instanceof ForgeAction) gameMenu.add(((ForgeAction) o).setupButton(new JMenuItem()));
            else if (o instanceof Action) gameMenu.add((Action) o);
            else if (o instanceof Component) gameMenu.add((Component) o);
            else throw new AssertionError();
        }

        JMenu gamePhases = new JMenu(ForgeProps.getLocalized(MENU_BAR.PHASE.TITLE));

        JMenuItem aiLabel = new JMenuItem("Computer");
        JMenuItem humanLabel = new JMenuItem("Human");

        Component[] objPhases = {aiLabel, cbAIUpkeep, cbAIDraw, cbAIBeginCombat,
                cbAIEndCombat, cbAIEndOfTurn, new JSeparator(),
                humanLabel, cbHumanUpkeep, cbHumanDraw, cbHumanBeginCombat,
                cbHumanEndCombat, cbHumanEndOfTurn};

        for (Component cmp : objPhases) {
            gamePhases.add(cmp);
        }

        // Dev Mode Creation
        JMenu devMenu = new JMenu(ForgeProps.getLocalized(MENU_BAR.DEV.TITLE));

        devMenu.setEnabled(Constant.Runtime.DevMode[0]);

        if (Constant.Runtime.DevMode[0]) {
            canLoseByDecking.setSelected(Constant.Runtime.Mill[0]);

            Action viewAIHand = new ZoneAction(AllZone.getComputerHand(), COMPUTER_HAND.BASE);
            Action viewAILibrary = new ZoneAction(AllZone.getComputerLibrary(), COMPUTER_LIBRARY.BASE);
            Action viewHumanLibrary = new ZoneAction(AllZone.getHumanLibrary(), HUMAN_LIBRARY.BASE);
            ForgeAction generateMana = new ForgeAction(MANAGEN) {
                private static final long serialVersionUID = 7171104690016706405L;

                public void actionPerformed(ActionEvent arg0) {
                    GuiDisplayUtil.devModeGenerateMana();
                }
            };

            // + Battlefield setup +
            ForgeAction setupBattleField = new ForgeAction(SETUPBATTLEFIELD) {
                private static final long serialVersionUID = -6660930759092583160L;

                public void actionPerformed(ActionEvent arg0) {
                    GuiDisplayUtil.devSetupGameState();
                }
            };
            // - Battlefield setup -

            //DevMode Tutor
            ForgeAction tutor = new ForgeAction(TUTOR) {
                private static final long serialVersionUID = 2003222642609217705L;

                public void actionPerformed(ActionEvent arg0) {
                    GuiDisplayUtil.devModeTutor();
                }
            };
            //end DevMode Tutor

            Object[] objDev = {GuiDisplay4.canLoseByDecking, viewAIHand, viewAILibrary, viewHumanLibrary, generateMana, setupBattleField, tutor};
            for (Object o : objDev) {
                if (o instanceof ForgeAction)
                    devMenu.add(((ForgeAction) o).setupButton(new JMenuItem()));
                else if (o instanceof Component)
                    devMenu.add((Component) o);
                else if (o instanceof Action)
                    devMenu.add((Action) o);
            }
        }

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(gameMenu);
        menuBar.add(gamePhases);
        menuBar.add(devMenu);
        menuBar.add(new MenuItem_HowToPlay());
        this.setJMenuBar(menuBar);
    }//addMenu()

    /**
     * <p>getButtonOK.</p>
     *
     * @return a {@link forge.MyButton} object.
     */
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

    /**
     * <p>getButtonCancel.</p>
     *
     * @return a {@link forge.MyButton} object.
     */
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

    /** {@inheritDoc} */
    public void showCombat(String message) {
        combatArea.setText(message);
    }

    /** {@inheritDoc} */
    public void showMessage(String s) {
        messageArea.setText(s);

        Border border = null;
        int thickness = 3;

        if (AllZone.getStack().size() > 0 && AllZone.getStack().peekInstance().getActivatingPlayer().isComputer())
            border = BorderFactory.createLineBorder(new Color(0, 255, 255), thickness);
        else if (s.contains("Main"))
            border = BorderFactory.createLineBorder(new Color(30, 0, 255), thickness);
        else if (s.contains("To Block"))
            border = BorderFactory.createLineBorder(new Color(13, 179, 0), thickness);
        else if (s.contains("Play Instants and Abilities") || s.contains("Combat") || s.contains("Damage"))
            border = BorderFactory.createLineBorder(new Color(255, 174, 0), thickness);
        else if (s.contains("Declare Attackers"))
            border = BorderFactory.createLineBorder(new Color(255, 0, 0), thickness);
        else if (s.contains("Upkeep") || s.contains("Draw") || s.contains("End of Turn"))
            border = BorderFactory.createLineBorder(new Color(200, 0, 170), thickness);
        else
            border = new EmptyBorder(1, 1, 1, 1);

        messageArea.setBorder(border);
    }

    /**
     * <p>addListeners.</p>
     */
    private void addListeners() {
        //mouse Card Detail
        playerHandPanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));
        playerLandPanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));
        playerCreaturePanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));

        oppLandPanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));
        oppCreaturePanel.addMouseMotionListener(GuiDisplayUtil.getCardDetailMouse(this));


        //opponent life mouse listener
        oppLifeLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                inputControl.selectPlayer(AllZone.getComputerPlayer());
            }
        });

        //self life mouse listener
        playerLifeLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                inputControl.selectPlayer(AllZone.getHumanPlayer());
            }
        });

        //self play (land) ---- Mouse
        playerLandPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Object o = playerLandPanel.getComponentAt(e.getPoint());
                if (o instanceof CardPanel) {
                    CardPanel cardPanel = (CardPanel) o;

                    if (cardPanel.getCard().isUntapped()) {
                        MP3Player mp3 = new MP3Player("tap.mp3");
                        mp3.play();
                    }

                    if (cardPanel.getCard().isTapped()
                            && (inputControl.input instanceof Input_PayManaCost || inputControl.input instanceof Input_PayManaCost_Ability)) {

                        while (cardPanel.connectedCard != null) {
                            cardPanel = cardPanel.connectedCard;

                            if (cardPanel.getCard().isUntapped()) {

                                break;
                            }
                        }
                    }

                    inputControl.selectCard(cardPanel.getCard(), AllZone.getHumanBattlefield());

                }
            }
        });
        //self play (no land) ---- Mouse
        playerCreaturePanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Object o = playerCreaturePanel.getComponentAt(e.getPoint());
                if (o instanceof CardPanel) {
                    CardPanel cardPanel = (CardPanel) o;

                    CardList att = new CardList(AllZone.getCombat().getAttackers());
                    //CardList block = AllZone.getCombat().getAllBlockers();

                    if ((cardPanel.getCard().isTapped()
                            || cardPanel.getCard().hasSickness()
                            || ((cardPanel.getCard().hasKeyword("Vigilance")) && att.contains(cardPanel.getCard())))
                            && (inputControl.input instanceof Input_Attack)) {
                        while (cardPanel.connectedCard != null) {
                            cardPanel = cardPanel.connectedCard;
                            if (cardPanel.getCard().isUntapped() && !cardPanel.getCard().hasSickness()) {
                                break;
                            }
                        }
                    }
                    //right click:
                    if (e.isMetaDown()) {
                        if (att.contains(cardPanel.getCard())
                                && (inputControl.input instanceof Input_Attack)
                                && !cardPanel.getCard().hasKeyword("CARDNAME attacks each turn if able.")) {
                            cardPanel.getCard().untap();
                            AllZone.getCombat().removeFromCombat(cardPanel.getCard());
                        } else if (inputControl.input instanceof Input_Block) {
                            Card crd = cardPanel.getCard();
                            if (crd.getController().isHuman())
                                AllZone.getCombat().removeFromCombat(crd);
                            ((Input_Block) inputControl.input).removeFromAllBlocking(crd);
                        }
                    } else inputControl.selectCard(cardPanel.getCard(), AllZone.getHumanBattlefield());
                }
            }
        });
        //self hand ---- Mouse
        playerHandPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Object o = playerHandPanel.getComponentAt(e.getPoint());
                if (o instanceof CardPanel) {
                    CardContainer cardPanel = (CardContainer) o;
                    inputControl.selectCard(cardPanel.getCard(), AllZone.getHumanHand());
                    okButton.requestFocusInWindow();
                }
            }
        });

        //*****************************************************************
        //computer

        //computer play (land) ---- Mouse
        oppLandPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Object o = oppLandPanel.getComponentAt(e.getPoint());
                if (o instanceof CardPanel) {
                    CardContainer cardPanel = (CardContainer) o;
                    inputControl.selectCard(cardPanel.getCard(), AllZone.getComputerBattlefield());
                }
            }
        });

        //computer play (no land) ---- Mouse
        oppCreaturePanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Object o = oppCreaturePanel.getComponentAt(e.getPoint());
                if (o instanceof CardPanel) {
                    CardContainer cardPanel = (CardContainer) o;
                    inputControl.selectCard(cardPanel.getCard(), AllZone.getComputerBattlefield());
                }
            }
        });


    }//addListener()

    /**
     * <p>getCard.</p>
     *
     * @return a {@link forge.Card} object.
     */
    public Card getCard() {
        return detail.getCard();
    }

    /** {@inheritDoc} */
    public void setCard(Card card) {
        detail.setCard(card);
        picture.setCard(card);
    }

    /**
     * <p>addObservers.</p>
     */
    private void addObservers() {
        //Human Hand, Graveyard, and Library totals
        {//make sure to not interfer with anything below, since this is a very long method
            Observer o = new Observer() {
                public void update(Observable a, Object b) {
                    playerHandValue.setText("" + AllZone.getHumanHand().size());
                    playerGraveValue.setText("" + AllZone.getHumanGraveyard().size());
                    playerLibraryValue.setText("" + AllZone.getHumanLibrary().size());
                    playerFBValue.setText("" + CardFactoryUtil.getGraveyardActivationCards(AllZone.getHumanPlayer()).size());
                    playerRemovedValue.setText("" + AllZone.getHumanExile().size());

                }
            };
            AllZone.getHumanHand().addObserver(o);
            AllZone.getHumanGraveyard().addObserver(o);
            AllZone.getHumanLibrary().addObserver(o);
        }

        //opponent Hand, Graveyard, and Library totals
        {//make sure to not interfer with anything below, since this is a very long method
            Observer o = new Observer() {
                public void update(Observable a, Object b) {
                    oppHandValue.setText("" + AllZone.getComputerHand().size());
                    oppGraveValue.setText("" + AllZone.getComputerGraveyard().size());
                    oppLibraryValue.setText("" + AllZone.getComputerLibrary().size());
                    oppRemovedValue.setText("" + AllZone.getComputerExile().size());
                }
            };
            AllZone.getComputerHand().addObserver(o);
            AllZone.getComputerGraveyard().addObserver(o);
            AllZone.getComputerLibrary().addObserver(o);
        }


        //opponent life
        oppLifeLabel.setText("" + AllZone.getComputerPlayer().getLife());
        AllZone.getComputerPlayer().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                int life = AllZone.getComputerPlayer().getLife();
                oppLifeLabel.setText("" + life);
            }
        });
        AllZone.getComputerPlayer().updateObservers();

        if (AllZone.getQuestData() != null) {
            File base = ForgeProps.getFile(IMAGE_ICON);
            String iconName = "";
            if (Constant.Quest.oppIconName[0] != null) {
                iconName = Constant.Quest.oppIconName[0];
                File file = new File(base, iconName);
                ImageIcon icon = new ImageIcon(file.toString());
                oppIconLabel.setIcon(icon);
                oppIconLabel.setAlignmentX(100);

            }
        }

        oppPCLabel.setText("Poison Counters: " + AllZone.getComputerPlayer().getPoisonCounters());
        AllZone.getComputerPlayer().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                int pcs = AllZone.getComputerPlayer().getPoisonCounters();
                oppPCLabel.setText("Poison Counters: " + pcs);
            }
        });
        AllZone.getComputerPlayer().updateObservers();

        //player life
        playerLifeLabel.setText("" + AllZone.getHumanPlayer().getLife());
        AllZone.getHumanPlayer().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                int life = AllZone.getHumanPlayer().getLife();
                playerLifeLabel.setText("" + life);
            }
        });
        AllZone.getHumanPlayer().updateObservers();

        playerPCLabel.setText("Poison Counters: " + AllZone.getHumanPlayer().getPoisonCounters());
        AllZone.getHumanPlayer().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                int pcs = AllZone.getHumanPlayer().getPoisonCounters();
                playerPCLabel.setText("Poison Counters: " + pcs);
            }
        });
        AllZone.getHumanPlayer().updateObservers();

        //stack
        AllZone.getStack().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                stackPanel.removeAll();
                MagicStack stack = AllZone.getStack();
                int count = 1;
                JLabel label;

                for (int i = stack.size() - 1; 0 <= i; i--) {
                    label = new JLabel("" + (count++) + ". " + stack.peekInstance(i).getStackDescription());


                    //update card detail
                    final CardPanel cardPanel = new CardPanel(stack.peekInstance(i).getSourceCard());
                    cardPanel.setLayout(new BorderLayout());
                    cardPanel.add(label);
                    cardPanel.addMouseMotionListener(new MouseMotionAdapter() {

                        @Override
                        public void mouseMoved(MouseEvent me) {
                            setCard(cardPanel.getCard());
                        }//mouseMoved
                    });

                    stackPanel.add(cardPanel);
                }

                stackPanel.revalidate();
                stackPanel.repaint();

                okButton.requestFocusInWindow();

            }
        });
        AllZone.getStack().updateObservers();
        //END, stack


        //self hand
        AllZone.getHumanHand().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                PlayerZone pZone = (PlayerZone) a;
                JPanel p = playerHandPanel;
                p.removeAll();

                Card c[] = AllZoneUtil.getCardsInZone(pZone).toArray();
                JPanel panel;
                for (int i = 0; i < c.length; i++) {
                    panel = new CardPanel(c[i]);
                    p.add(panel);
                }

                p.revalidate();
                p.repaint();
            }
        });
        AllZone.getHumanHand().updateObservers();
        //END, self hand

        //self play (land)
        AllZone.getHumanBattlefield().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                //PlayerZone pZone = (PlayerZone) a; //unused
                JPanel p = playerLandPanel;
                p.removeAll();

                GuiDisplayUtil.setupLandPanel(p, AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer()).toArray());
                p.revalidate();
                p.repaint();
            }
        });
        AllZone.getHumanBattlefield().updateObservers();
        //END - self play (only land)


        //self play (no land)
        AllZone.getHumanBattlefield().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                //PlayerZone pZone = (PlayerZone) a; //unused
                JPanel p = playerCreaturePanel;
                p.removeAll();

                GuiDisplayUtil.setupNoLandPanel(p, AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer()).toArray());
                p.revalidate();
                p.repaint();
            }
        });
        AllZone.getHumanBattlefield().updateObservers();
        //END - self play (no land)


        //computer play (no land)
        AllZone.getComputerBattlefield().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                //PlayerZone pZone = (PlayerZone) a; //unused
                JPanel p = oppCreaturePanel;
                p.removeAll();

                GuiDisplayUtil.setupNoLandPanel(p, AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer()).toArray());

                p.revalidate();
                p.repaint();
            }
        });
        AllZone.getComputerBattlefield().updateObservers();
        //END - computer play (no land)

        //computer play (land)
        AllZone.getComputerBattlefield().addObserver(new Observer() {
            public void update(Observable a, Object b) {
                //PlayerZone pZone = (PlayerZone) a; //unused
                JPanel p = oppLandPanel;
                p.removeAll();

                GuiDisplayUtil.setupLandPanel(p, AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer()).toArray());
                p.revalidate();
                p.repaint();
            }
        });
        AllZone.getComputerBattlefield().updateObservers();
        //END - computer play (only land)

    }//addObservers()

    /**
     * <p>initComponents.</p>
     */
    private void initComponents() {
        //Preparing the Frame
        setTitle(ForgeProps.getLocalized(LANG.PROGRAM_NAME));
        if (!Gui_NewGame.useLAFFonts.isSelected()) setFont(new Font("Times New Roman", 0, 16));
        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                concede();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                File f = ForgeProps.getFile(LAYOUT);
                Node layout = pane.getMultiSplitLayout().getModel();
                try {
                    XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(f)));
                    encoder.writeObject(layout);
                    encoder.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //making the multi split pane
        Node model;
        File f = ForgeProps.getFile(LAYOUT);
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(f)));
            model = (Node) decoder.readObject();
            decoder.close();
            pane.getMultiSplitLayout().setModel(model);
            //pane.getMultiSplitLayout().setFloatingDividers(false);
        } catch (Exception ex) {
            model = parseModel(""//
                    + "(ROW "//
                    + "(COLUMN"//
                    + " (LEAF weight=0.2 name=info)"//
                    + " (LEAF weight=0.2 name=compy)"//
                    + " (LEAF weight=0.2 name=stack)"//
                    + " (LEAF weight=0.2 name=combat)"//
                    + " (LEAF weight=0.2 name=human)) "//
                    + "(COLUMN weight=1"//
                    + " (LEAF weight=0.2 name=compyLand)"//
                    + " (LEAF weight=0.2 name=compyPlay)"//
                    + " (LEAF weight=0.2 name=humanPlay)"//
                    + " (LEAF weight=0.2 name=humanLand)"//
                    + " (LEAF weight=0.2 name=humanHand)) "//
                    + "(COLUMN"//
                    + " (LEAF weight=0.5 name=detail)"//
                    + " (LEAF weight=0.5 name=picture)))");
            pane.setModel(model);
        }
        pane.getMultiSplitLayout().setFloatingDividers(false);
        getContentPane().add(pane);

        //adding the individual parts

        if (!Gui_NewGame.useLAFFonts.isSelected()) initFonts(pane);

        initMsgYesNo(pane);
        initOpp(pane);
        initStackCombat(pane);
        initPlayer(pane);
        initZones(pane);
        initCardPicture(pane);
    }

    /**
     * <p>initFonts.</p>
     *
     * @param pane a {@link javax.swing.JPanel} object.
     */
    private void initFonts(JPanel pane) {
        messageArea.setFont(getFont());

        oppLifeLabel.setFont(lifeFont);

        oppPCLabel.setFont(statFont);
        oppLibraryLabel.setFont(statFont);

        oppHandValue.setFont(statFont);
        oppLibraryValue.setFont(statFont);
        oppRemovedValue.setFont(statFont);
        oppGraveValue.setFont(statFont);

        playerLifeLabel.setFont(lifeFont);
        playerPCLabel.setFont(statFont);

        playerHandValue.setFont(statFont);
        playerLibraryValue.setFont(statFont);
        playerRemovedValue.setFont(statFont);
        playerGraveValue.setFont(statFont);
        playerFBValue.setFont(statFont);

        combatArea.setFont(getFont());
    }

    /**
     * <p>initMsgYesNo.</p>
     *
     * @param pane a {@link javax.swing.JPanel} object.
     */
    private void initMsgYesNo(JPanel pane) {
//        messageArea.setBorder(BorderFactory.createEtchedBorder());
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
                okButton.requestFocusInWindow();
            }
        });
        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okButtonActionPerformed(evt);

                if (AllZone.getPhase().isNeedToNextPhase()) {
                    //for debugging: System.out.println("There better be no nextPhase in the stack.");
                    AllZone.getPhase().setNeedToNextPhase(false);
                    AllZone.getPhase().nextPhase();
                }
                okButton.requestFocusInWindow();
            }
        });
        okButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                // TODO make triggers on escape
                int code = arg0.getKeyCode();
                if (code == KeyEvent.VK_ESCAPE) {
                    cancelButton.doClick();
                }
            }
        });

        okButton.requestFocusInWindow();

        //if(okButton.isEnabled())
        //okButton.doClick();
        JPanel yesNoPanel = new JPanel(new FlowLayout());
        yesNoPanel.setBorder(new EtchedBorder());
        yesNoPanel.add(cancelButton);
        yesNoPanel.add(okButton);

        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scroll = new JScrollPane(messageArea);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scroll);
        panel.add(yesNoPanel, BorderLayout.SOUTH);
        pane.add(new ExternalPanel(panel), "info");
    }

    /**
     * <p>initOpp.</p>
     *
     * @param pane a {@link javax.swing.JPanel} object.
     */
    private void initOpp(JPanel pane) {
        //oppLifeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //oppPCLabel.setHorizontalAlignment(SwingConstants.TOP);
        oppPCLabel.setForeground(greenColor);

        JLabel oppHandLabel = new JLabel(ForgeProps.getLocalized(COMPUTER_HAND.BUTTON), SwingConstants.TRAILING);
        if (!Gui_NewGame.useLAFFonts.isSelected()) oppHandLabel.setFont(statFont);

        //JLabel oppGraveLabel = new JLabel("Grave:", SwingConstants.TRAILING);
        JButton oppGraveButton = new JButton(COMPUTER_GRAVEYARD_ACTION);
        oppGraveButton.setText((String) COMPUTER_GRAVEYARD_ACTION.getValue("buttonText"));
        oppGraveButton.setMargin(new Insets(0, 0, 0, 0));
        oppGraveButton.setHorizontalAlignment(SwingConstants.TRAILING);
        if (!Gui_NewGame.useLAFFonts.isSelected()) oppGraveButton.setFont(statFont);


        JPanel gravePanel = new JPanel(new BorderLayout());
        gravePanel.add(oppGraveButton, BorderLayout.EAST);

        JButton oppRemovedButton = new JButton(COMPUTER_REMOVED_ACTION);
        oppRemovedButton.setText((String) COMPUTER_REMOVED_ACTION.getValue("buttonText"));
        oppRemovedButton.setMargin(new Insets(0, 0, 0, 0));
        //removedButton.setHorizontalAlignment(SwingConstants.TRAILING);
        if (!Gui_NewGame.useLAFFonts.isSelected()) oppRemovedButton.setFont(statFont);


        oppHandValue.setHorizontalAlignment(SwingConstants.LEADING);
        oppLibraryValue.setHorizontalAlignment(SwingConstants.LEADING);
        oppGraveValue.setHorizontalAlignment(SwingConstants.LEADING);
        oppRemovedValue.setHorizontalAlignment(SwingConstants.LEADING);

        JPanel oppNumbersPanel = new JPanel(new GridLayout(0, 2, 3, 1));
        oppNumbersPanel.add(oppHandLabel);
        oppNumbersPanel.add(oppHandValue);
        oppNumbersPanel.add(oppRemovedButton);
        oppNumbersPanel.add(oppRemovedValue);
        oppNumbersPanel.add(oppLibraryLabel);
        oppNumbersPanel.add(oppLibraryValue);
        oppNumbersPanel.add(gravePanel);
        oppNumbersPanel.add(oppGraveValue);

        oppLifeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel oppIconLifePanel = new JPanel(new GridLayout(0, 1, 0, 0));
        oppIconLifePanel.add(oppIconLabel);
        oppIconLifePanel.add(oppLifeLabel);

        JPanel oppPanel = new JPanel();
        oppPanel.setBorder(new TitledBorder(new EtchedBorder(), ForgeProps.getLocalized(COMPUTER_TITLE)));
        oppPanel.setLayout(new BorderLayout());
        oppPanel.add(oppNumbersPanel, BorderLayout.WEST);
        // oppPanel.add(oppIconLabel, BorderLayout.CENTER);
        // oppPanel.add(oppLifeLabel, BorderLayout.EAST);
        oppPanel.add(oppIconLifePanel, BorderLayout.EAST);
        oppPanel.add(oppPCLabel, BorderLayout.AFTER_LAST_LINE);
        pane.add(new ExternalPanel(oppPanel), "compy");
    }

    /**
     * <p>initStackCombat.</p>
     *
     * @param pane a {@link javax.swing.JPanel} object.
     */
    private void initStackCombat(JPanel pane) {
        stackPanel.setLayout(new GridLayout(0, 1, 10, 10));
        JScrollPane stackPane = new JScrollPane(stackPanel);
        stackPane.setBorder(new EtchedBorder());
        pane.add(new ExternalPanel(stackPane), "stack");

        combatArea.setEditable(false);
        combatArea.setLineWrap(true);
        combatArea.setWrapStyleWord(true);

        JScrollPane combatPane = new JScrollPane(combatArea);

        combatPane.setBorder(new TitledBorder(new EtchedBorder(), ForgeProps.getLocalized(COMBAT)));
        pane.add(new ExternalPanel(combatPane), "combat");
    }

    /**
     * <p>initPlayer.</p>
     *
     * @param pane a {@link javax.swing.JPanel} object.
     */
    private void initPlayer(JPanel pane) {
        //int fontSize = 12;
        playerLifeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        playerPCLabel.setForeground(greenColor);

        JLabel playerLibraryLabel = new JLabel(ForgeProps.getLocalized(HUMAN_LIBRARY.BUTTON),
                SwingConstants.TRAILING);
        if (!Gui_NewGame.useLAFFonts.isSelected()) playerLibraryLabel.setFont(statFont);

        JLabel playerHandLabel = new JLabel(ForgeProps.getLocalized(HUMAN_HAND.TITLE), SwingConstants.TRAILING);
        if (!Gui_NewGame.useLAFFonts.isSelected()) playerHandLabel.setFont(statFont);

        //JLabel playerGraveLabel = new JLabel("Grave:", SwingConstants.TRAILING);
        JButton playerGraveButton = new JButton(HUMAN_GRAVEYARD_ACTION);
        playerGraveButton.setText((String) HUMAN_GRAVEYARD_ACTION.getValue("buttonText"));
        playerGraveButton.setMargin(new Insets(0, 0, 0, 0));
        playerGraveButton.setHorizontalAlignment(SwingConstants.TRAILING);
        if (!Gui_NewGame.useLAFFonts.isSelected()) playerGraveButton.setFont(statFont);


        JButton playerFlashBackButton = new JButton(HUMAN_FLASHBACK_ACTION);
        playerFlashBackButton.setText((String) HUMAN_FLASHBACK_ACTION.getValue("buttonText"));
        playerFlashBackButton.setMargin(new Insets(0, 0, 0, 0));
        playerFlashBackButton.setHorizontalAlignment(SwingConstants.TRAILING);
        if (!Gui_NewGame.useLAFFonts.isSelected()) playerFlashBackButton.setFont(statFont);


        JPanel gravePanel = new JPanel(new BorderLayout());
        gravePanel.add(playerGraveButton, BorderLayout.EAST);

        JPanel playerFBPanel = new JPanel(new BorderLayout());
        playerFBPanel.add(playerFlashBackButton, BorderLayout.EAST);

        JButton playerRemovedButton = new JButton(HUMAN_REMOVED_ACTION);
        playerRemovedButton.setText((String) HUMAN_REMOVED_ACTION.getValue("buttonText"));
        playerRemovedButton.setMargin(new Insets(0, 0, 0, 0));
        //removedButton.setHorizontalAlignment(SwingConstants.TRAILING);
        if (!Gui_NewGame.useLAFFonts.isSelected()) playerRemovedButton.setFont(statFont);

        playerHandValue.setHorizontalAlignment(SwingConstants.LEADING);
        playerLibraryValue.setHorizontalAlignment(SwingConstants.LEADING);
        playerGraveValue.setHorizontalAlignment(SwingConstants.LEADING);
        playerFBValue.setHorizontalAlignment(SwingConstants.LEADING);

        //playerRemovedValue.setFont(new Font("MS Sans Serif", 0, fontSize));
        playerRemovedValue.setHorizontalAlignment(SwingConstants.LEADING);

        JPanel playerNumbersPanel = new JPanel(new GridLayout(0, 2, 5, 1));
        playerNumbersPanel.add(playerHandLabel);
        playerNumbersPanel.add(playerHandValue);
        playerNumbersPanel.add(playerRemovedButton);
        playerNumbersPanel.add(playerRemovedValue);
        playerNumbersPanel.add(playerLibraryLabel);
        playerNumbersPanel.add(playerLibraryValue);
        playerNumbersPanel.add(gravePanel);
        playerNumbersPanel.add(playerGraveValue);
        playerNumbersPanel.add(playerFBPanel);
        playerNumbersPanel.add(playerFBValue);

        JPanel playerPanel = new JPanel();
        playerPanel.setBorder(new TitledBorder(new EtchedBorder(), ForgeProps.getLocalized(HUMAN_TITLE)));
        playerPanel.setLayout(new BorderLayout());
        playerPanel.add(playerNumbersPanel, BorderLayout.WEST);
        playerPanel.add(playerLifeLabel, BorderLayout.EAST);
        playerPanel.add(playerPCLabel, BorderLayout.AFTER_LAST_LINE);
        pane.add(new ExternalPanel(playerPanel), "human");
    }

    /**
     * <p>initZones.</p>
     *
     * @param pane a {@link javax.swing.JPanel} object.
     */
    private void initZones(JPanel pane) {
        JPanel[] zones = {oppLandPanel, oppCreaturePanel, playerCreaturePanel, playerLandPanel, playerHandPanel};
        String[] names = {"compyLand", "compyPlay", "humanPlay", "humanLand", "humanHand"};
        for (int i = 0; i < names.length; i++) {
            zones[i].setLayout(null);
            zones[i].setBorder(BorderFactory.createEtchedBorder());
            Dimension d = zones[i].getPreferredSize();
            d.height = 100;
            zones[i].setPreferredSize(d);
            pane.add(new ExternalPanel(new JScrollPane(zones[i])), names[i]);
        }
        playerHandPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    /**
     * <p>initCardPicture.</p>
     *
     * @param pane a {@link javax.swing.JPanel} object.
     */
    private void initCardPicture(JPanel pane) {
        pane.add(new ExternalPanel(detail), "detail");
        pane.add(new ExternalPanel(picture), "picture");
    }

    /**
     * <p>cancelButtonActionPerformed.</p>
     *
     * @param evt a {@link java.awt.event.ActionEvent} object.
     */
    private void cancelButtonActionPerformed(ActionEvent evt) {
        inputControl.selectButtonCancel();
    }

    /**
     * <p>okButtonActionPerformed.</p>
     *
     * @param evt a {@link java.awt.event.ActionEvent} object.
     */
    private void okButtonActionPerformed(ActionEvent evt) {
        inputControl.selectButtonOK();
    }

    /**
     * Exit the Application
     */
    private void concede() {
        savePrefs();
        dispose();
        Constant.Runtime.matchState.addLose();
        if (!Constant.Quest.fantasyQuest[0])
            new Gui_WinLose();
        else {
            //new Gui_WinLose(Constant.Quest.humanList[0], Constant.Quest.computerList[0],Constant.Quest.humanLife[0], Constant.Quest.computerLife[0]);
            CardList humanList = forge.quest.data.QuestUtil.getHumanPlantAndPet(AllZone.getQuestData(), AllZone.getQuestAssignment());
            CardList computerList = forge.quest.data.QuestUtil.getComputerCreatures(AllZone.getQuestData(), AllZone.getQuestAssignment());

            int humanLife = AllZone.getQuestData().getLife();
            int computerLife = 20;

            if (AllZone.getQuestAssignment() != null)
                computerLife = AllZone.getQuestAssignment().getComputerLife();
            new Gui_WinLose(humanList, computerList, humanLife, computerLife);
        }
    }

    // ********** Phase stuff in Display ******************
    /** {@inheritDoc} */
    public boolean stopAtPhase(Player turn, String phase) {
        if (turn.isComputer()) {
            if (phase.equals(Constant.Phase.End_Of_Turn))
                return cbAIEndOfTurn.isSelected();
            else if (phase.equals(Constant.Phase.Upkeep))
                return cbAIUpkeep.isSelected();
            else if (phase.equals(Constant.Phase.Draw))
                return cbAIDraw.isSelected();
            else if (phase.equals(Constant.Phase.Combat_Begin))
                return cbAIBeginCombat.isSelected();
            else if (phase.equals(Constant.Phase.Combat_End))
                return cbAIEndCombat.isSelected();
        } else {
            if (phase.equals(Constant.Phase.End_Of_Turn))
                return cbHumanEndOfTurn.isSelected();
            else if (phase.equals(Constant.Phase.Upkeep))
                return cbHumanUpkeep.isSelected();
            else if (phase.equals(Constant.Phase.Draw))
                return cbHumanDraw.isSelected();
            else if (phase.equals(Constant.Phase.Combat_Begin))
                return cbHumanBeginCombat.isSelected();
            else if (phase.equals(Constant.Phase.Combat_End))
                return cbHumanEndCombat.isSelected();
        }
        return true;
    }

    /**
     * <p>loadPrefs.</p>
     *
     * @return a boolean.
     */
    public boolean loadPrefs() {
        ForgePreferences fp = Gui_NewGame.preferences;

        cbAIUpkeep.setSelected(fp.bAIUpkeep);
        cbAIDraw.setSelected(fp.bAIDraw);
        cbAIEndOfTurn.setSelected(fp.bAIEOT);
        cbAIBeginCombat.setSelected(fp.bAIBeginCombat);
        cbAIEndCombat.setSelected(fp.bAIEndCombat);

        cbHumanUpkeep.setSelected(fp.bHumanUpkeep);
        cbHumanDraw.setSelected(fp.bHumanDraw);
        cbHumanEndOfTurn.setSelected(fp.bHumanEOT);
        cbHumanBeginCombat.setSelected(fp.bHumanBeginCombat);
        cbHumanEndCombat.setSelected(fp.bHumanEndCombat);

        return true;
    }

    /**
     * <p>savePrefs.</p>
     *
     * @return a boolean.
     */
    public boolean savePrefs() {
        Constant.Runtime.Mill[0] = canLoseByDecking.isSelected();
        ForgePreferences fp = Gui_NewGame.preferences;

        fp.bAIUpkeep = cbAIUpkeep.isSelected();
        fp.bAIDraw = cbAIDraw.isSelected();
        fp.bAIEOT = cbAIEndOfTurn.isSelected();
        fp.bAIBeginCombat = cbAIBeginCombat.isSelected();
        fp.bAIEndCombat = cbAIEndCombat.isSelected();

        fp.bHumanUpkeep = cbHumanUpkeep.isSelected();
        fp.bHumanDraw = cbHumanDraw.isSelected();
        fp.bHumanEOT = cbHumanEndOfTurn.isSelected();
        fp.bHumanBeginCombat = cbHumanBeginCombat.isSelected();
        fp.bHumanEndCombat = cbHumanEndCombat.isSelected();

        return true;
    }

    /** Constant <code>playsoundCheckboxForMenu</code> */
    public static JCheckBoxMenuItem playsoundCheckboxForMenu = new JCheckBoxMenuItem("Play Sound", false);

    // Phases
    /** Constant <code>cbAIUpkeep</code> */
    public static JCheckBoxMenuItem cbAIUpkeep = new JCheckBoxMenuItem("Upkeep", true);
    /** Constant <code>cbAIDraw</code> */
    public static JCheckBoxMenuItem cbAIDraw = new JCheckBoxMenuItem("Draw", true);
    /** Constant <code>cbAIEndOfTurn</code> */
    public static JCheckBoxMenuItem cbAIEndOfTurn = new JCheckBoxMenuItem("End of Turn", true);
    /** Constant <code>cbAIBeginCombat</code> */
    public static JCheckBoxMenuItem cbAIBeginCombat = new JCheckBoxMenuItem("Begin Combat", true);
    /** Constant <code>cbAIEndCombat</code> */
    public static JCheckBoxMenuItem cbAIEndCombat = new JCheckBoxMenuItem("End Combat", true);

    /** Constant <code>cbHumanUpkeep</code> */
    public static JCheckBoxMenuItem cbHumanUpkeep = new JCheckBoxMenuItem("Upkeep", true);
    /** Constant <code>cbHumanDraw</code> */
    public static JCheckBoxMenuItem cbHumanDraw = new JCheckBoxMenuItem("Draw", true);
    /** Constant <code>cbHumanEndOfTurn</code> */
    public static JCheckBoxMenuItem cbHumanEndOfTurn = new JCheckBoxMenuItem("End of Turn", true);
    /** Constant <code>cbHumanBeginCombat</code> */
    public static JCheckBoxMenuItem cbHumanBeginCombat = new JCheckBoxMenuItem("Begin Combat", true);
    /** Constant <code>cbHumanEndCombat</code> */
    public static JCheckBoxMenuItem cbHumanEndCombat = new JCheckBoxMenuItem("End Combat", true);

    // ********** End of Phase stuff in Display ******************

    // ****** Developer Mode ******* 

    /** Constant <code>canLoseByDecking</code> */
    public static JCheckBoxMenuItem canLoseByDecking = new JCheckBoxMenuItem("Lose by Decking", true);

    // *****************************

    JXMultiSplitPane pane = new JXMultiSplitPane();
    JButton cancelButton = new JButton();
    JButton okButton = new JButton();
    JTextArea messageArea = new JTextArea(1, 10);
    JTextArea combatArea = new JTextArea();
    JPanel stackPanel = new JPanel();
    JPanel oppLandPanel = new JPanel();
    JPanel oppCreaturePanel = new JPanel();
    JPanel playerCreaturePanel = new JPanel();
    JPanel playerLandPanel = new JPanel();
    JPanel playerHandPanel = new JPanel();
    JPanel cdPanel = new JPanel();
    JLabel oppLifeLabel = new JLabel();
    JLabel oppIconLabel = new JLabel();
    JLabel playerLifeLabel = new JLabel();
    JLabel oppPCLabel = new JLabel();
    JLabel playerPCLabel = new JLabel();
    JLabel oppLibraryLabel = new JLabel(
            ForgeProps.getLocalized(COMPUTER_LIBRARY.BUTTON),
            SwingConstants.TRAILING);
    JLabel oppHandValue = new JLabel();
    JLabel oppLibraryValue = new JLabel();
    JLabel oppGraveValue = new JLabel();
    JLabel oppRemovedValue = new JLabel();
    JLabel playerHandValue = new JLabel();
    JLabel playerLibraryValue = new JLabel();
    JLabel playerGraveValue = new JLabel();
    JLabel playerFBValue = new JLabel();
    JLabel playerRemovedValue = new JLabel();

    CardDetailPanel detail = new CardDetailPanel(null);
    CardPicturePanel picture = new CardPicturePanel(null);

    private class ZoneAction extends ForgeAction {
        private static final long serialVersionUID = -5822976087772388839L;
        private PlayerZone zone;
        private String title;

        public ZoneAction(PlayerZone zone, String property) {
            super(property);
            title = ForgeProps.getLocalized(property + "/title");
            this.zone = zone;
        }

        public void actionPerformed(ActionEvent e) {
            Card[] c = getCards();

            if (AllZone.getNameChanger().shouldChangeCardName()) c = AllZone.getNameChanger().changeCard(c);

            if (c.length == 0) GuiUtils.getChoiceOptional(title, new String[]{"no cards"});
            else {
                Card choice = GuiUtils.getChoiceOptional(title, c);
                if (choice != null) doAction(choice);
            }
        /**
         * <p>main.</p>
         *
         * @param args an array of {@link java.lang.String} objects.
         */
        }

        /*
        protected PlayerZone getZone() {
            return zone;
        }
        */
        protected Card[] getCards() {
            return AllZoneUtil.getCardsInZone(zone).toArray();
        }

        protected void doAction(Card c) {
        }
    }

    private class ConcedeAction extends ForgeAction {

        private static final long serialVersionUID = -6976695235601916762L;

        public ConcedeAction() {
            super(CONCEDE);
        }

        public void actionPerformed(ActionEvent e) {
            concede();
        }
    }

    /**
     * <p>canLoseByDecking.</p>
     *
     * @return a boolean.
     */
    public boolean canLoseByDecking() {
        return canLoseByDecking.isSelected();
    }
}

