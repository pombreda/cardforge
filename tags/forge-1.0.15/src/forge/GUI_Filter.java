package forge;


import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;


/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for
 * non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or business for any
 * purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit
 * www.cloudgarden.com for details. Use of Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE
 * HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR
 * COMMERCIAL PURPOSE.
 *
 * @author Forge
 * @version $Id: $
 */
public class GUI_Filter extends javax.swing.JDialog {

    /** Constant <code>serialVersionUID=-8475271235196182185L</code> */
    private static final long serialVersionUID = -8475271235196182185L;
    private JLabel jLabel1;
    private JTextField NameText;
    private JLabel jLabel5;
    private JTextField cardText;
    @SuppressWarnings("unused")
    private JTextField cardType;
    private JPanel jPanel1;
    private JCheckBox jCheckBoxColorless;
    private JCheckBox jCheckBoxWhite;
    private JCheckBox jCheckBoxRed;
    private JCheckBox jCheckBoxGreen;
    private JCheckBox jCheckBoxBlue;
    private JSeparator jSeparator1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JCheckBox jCheckBoxPlaneswalker;
    private JCheckBox jCheckBoxArtifact;
    private JCheckBox jCheckBoxCreature;
    private JCheckBox jCheckBoxEnchant;
    private JCheckBox jCheckBoxInstant;
    private JCheckBox jCheckBoxLand;
    private JCheckBox jCheckBoxSorcery;
    private JSeparator jSeparator2;
    private JPanel jPanel2;
    private JCheckBox jCheckBoxBlack;
    private JButton jButtonOk;
    //private ButtonGroup buttonGroup1;
    private DeckDisplay deckDisplay;
    public CardList filterCardList;
    int kCode;


    /**
     * <p>Constructor for GUI_Filter.</p>
     *
     * @param g a {@link javax.swing.JFrame} object.
     * @param display a {@link forge.DeckDisplay} object.
     */
    public GUI_Filter(JFrame g, DeckDisplay display) {
        super(g);
        deckDisplay = display;
        initGUI();
    }

    /**
     * <p>initGUI.</p>
     */
    private void initGUI() {
        try {

            AnchorLayout thisLayout = new AnchorLayout();
            getContentPane().setLayout(thisLayout);
            {
                NameText = new JTextField();
                getContentPane().add(
                        getJPanel2(),
                        new AnchorConstraint(293, 972, 837, 534, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL));
                getContentPane().add(
                        getJButtonOk(),
                        new AnchorConstraint(873, 638, 965, 384, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL));
                getContentPane().add(
                        getJPanel1(),
                        new AnchorConstraint(293, 483, 837, 45, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL));
                getContentPane().add(
                        NameText,
                        new AnchorConstraint(38, 969, 126, 362, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL));
                NameText.setPreferredSize(new java.awt.Dimension(148, 24));
                NameText.addKeyListener(new java.awt.event.KeyAdapter() {
                    @Override
                    public void keyPressed(java.awt.event.KeyEvent e) {

                        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                            FilterCardTable();
                        }
                    }
                });
            }
            {
                jLabel1 = new JLabel();
                getContentPane().add(
                        jLabel1,
                        new AnchorConstraint(4, 313, 153, 41, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL));
                getContentPane().add(
                        getJTextField1(),
                        new AnchorConstraint(159, 969, 248, 360, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL));
                getContentPane().add(
                        getJLabel5(),
                        new AnchorConstraint(126, 313, 275, 41, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                                AnchorConstraint.ANCHOR_REL));
                jLabel1.setText("Name:");
                jLabel1.setPreferredSize(new java.awt.Dimension(75, 50));
                jLabel1.setLayout(null);
                jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 16));
                jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
            }
            setVisible(true);
            this.setPreferredSize(new java.awt.Dimension(280, 300));

            Dimension screen = getToolkit().getScreenSize();
            // int x = (screen.width - 280) / 2;
            // int y = (screen.height - 300) / 2;
            // this.setBounds(x, y, 280, 300);
            // this.setResizable(false);
            int x = (screen.width - 340) / 2;
            int y = (screen.height - 360) / 2;
            this.setBounds(x, y, 340, 360);
            this.setResizable(true);
            this.setTitle("Filter");
            // pack();
            try {
                // Call Java 6 method without making the Java 5 compiler 
                // unhappy:
                Method method = this.getClass().getMethod("setIconImage");
                method.invoke(this, new Object[]{null});
            } catch (NoSuchMethodError err) {
                // setIconImage is @since 1.6
                err.printStackTrace();
            }
            this.addWindowListener(new WListener());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Getter for the field <code>jPanel1</code>.</p>
     *
     * @return a {@link javax.swing.JPanel} object.
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            jPanel1 = new JPanel();
            AnchorLayout jPanel1Layout = new AnchorLayout();
            jPanel1.setPreferredSize(new java.awt.Dimension(121, 183));
            jPanel1.setLayout(jPanel1Layout);
            jPanel1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            jPanel1.setBackground(new java.awt.Color(192, 192, 192));
            jPanel1.add(getJCheckBoxBlack(), new AnchorConstraint(134, 985, 240, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel1.add(getJCheckBoxColorless(), new AnchorConstraint(878, 983, 950, 84,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL));
            jPanel1.add(getJCheckBoxWhite(), new AnchorConstraint(726, 987, 798, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel1.add(getJCheckBoxRed(), new AnchorConstraint(577, 987, 654, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel1.add(getJCheckBoxGreen(), new AnchorConstraint(428, 987, 494, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel1.add(getJCheckBoxBlue(), new AnchorConstraint(279, 987, 356, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel1.add(getJSeparator1(), new AnchorConstraint(107, 987, 139, 12, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel1.add(getJLabel2(), new AnchorConstraint(-20, 990, 123, 16, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
        }
        return jPanel1;
    }

    /**
     * <p>Getter for the field <code>jLabel2</code>.</p>
     *
     * @return a {@link javax.swing.JLabel} object.
     */
    private JLabel getJLabel2() {
        if (jLabel2 == null) {
            jLabel2 = new JLabel();
            jLabel2.setText("Color");
            jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14));
            jLabel2.setPreferredSize(new java.awt.Dimension(152, 39));
            jLabel2.setLayout(null);
        }
        return jLabel2;
    }

    /**
     * <p>Getter for the field <code>jSeparator1</code>.</p>
     *
     * @return a {@link javax.swing.JSeparator} object.
     */
    private JSeparator getJSeparator1() {
        if (jSeparator1 == null) {
            jSeparator1 = new JSeparator();
            jSeparator1.setPreferredSize(new java.awt.Dimension(117, 6));
            jSeparator1.setLayout(null);
        }
        return jSeparator1;
    }

    /**
     * <p>Getter for the field <code>jCheckBoxBlue</code>.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBoxBlue() {
        if (jCheckBoxBlue == null) {
            jCheckBoxBlue = new JCheckBox();
            jCheckBoxBlue.setLayout(null);
            jCheckBoxBlue.setText("Blue");
            jCheckBoxBlue.setPreferredSize(new java.awt.Dimension(109, 14));
            jCheckBoxBlue.setSelected(true);
            jCheckBoxBlue.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxBlue.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxBlue;
    }

    /**
     * <p>Getter for the field <code>jCheckBoxGreen</code>.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBoxGreen() {
        if (jCheckBoxGreen == null) {
            jCheckBoxGreen = new JCheckBox();
            jCheckBoxGreen.setLayout(null);
            jCheckBoxGreen.setText("Green");
            jCheckBoxGreen.setPreferredSize(new java.awt.Dimension(109, 12));
            jCheckBoxGreen.setSelected(true);
            jCheckBoxGreen.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxGreen.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxGreen;
    }

    /**
     * <p>Getter for the field <code>jCheckBoxRed</code>.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBoxRed() {
        if (jCheckBoxRed == null) {
            jCheckBoxRed = new JCheckBox();
            jCheckBoxRed.setLayout(null);
            jCheckBoxRed.setText("Red");
            jCheckBoxRed.setPreferredSize(new java.awt.Dimension(109, 14));
            jCheckBoxRed.setSelected(true);
            jCheckBoxRed.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxRed.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxRed;
    }

    /**
     * <p>Getter for the field <code>jCheckBoxWhite</code>.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBoxWhite() {
        if (jCheckBoxWhite == null) {
            jCheckBoxWhite = new JCheckBox();
            jCheckBoxWhite.setLayout(null);
            jCheckBoxWhite.setText("White");
            jCheckBoxWhite.setPreferredSize(new java.awt.Dimension(109, 13));
            jCheckBoxWhite.setSelected(true);
            jCheckBoxWhite.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxWhite.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxWhite;
    }

    /**
     * <p>Getter for the field <code>jCheckBoxColorless</code>.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBoxColorless() {
        if (jCheckBoxColorless == null) {
            jCheckBoxColorless = new JCheckBox();
            jCheckBoxColorless.setLayout(null);
            jCheckBoxColorless.setText("Colorless");
            jCheckBoxColorless.setPreferredSize(new java.awt.Dimension(80, 15));
            jCheckBoxColorless.setSelected(true);
            jCheckBoxColorless.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxColorless.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxColorless;
    }

    /**
     * <p>Getter for the field <code>jButtonOk</code>.</p>
     *
     * @return a {@link javax.swing.JButton} object.
     */
    private JButton getJButtonOk() {
        if (jButtonOk == null) {
            jButtonOk = new JButton();
            jButtonOk.setLayout(null);
            jButtonOk.setText("OK");
            jButtonOk.setPreferredSize(new java.awt.Dimension(100, 25));
            jButtonOk.addMouseListener(new CustomListener());
        }
        return jButtonOk;
    }

    /**
     * <p>Getter for the field <code>jCheckBoxBlack</code>.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBoxBlack() {
        if (jCheckBoxBlack == null) {
            jCheckBoxBlack = new JCheckBox();
            jCheckBoxBlack.setLayout(null);
            jCheckBoxBlack.setText("Black");
            jCheckBoxBlack.setPreferredSize(new java.awt.Dimension(97, 20));
            jCheckBoxBlack.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxBlack.setSelected(true);
            jCheckBoxBlack.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxBlack;
    }

    /**
     * <p>Getter for the field <code>jPanel2</code>.</p>
     *
     * @return a {@link javax.swing.JPanel} object.
     */
    private JPanel getJPanel2() {
        if (jPanel2 == null) {
            jPanel2 = new JPanel();
            AnchorLayout jPanel2Layout = new AnchorLayout();
            jPanel2.setPreferredSize(new java.awt.Dimension(121, 183));
            jPanel2.setLayout(jPanel2Layout);
            jPanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            jPanel2.setBackground(new java.awt.Color(192, 192, 192));
            jPanel2.add(getJSeparator2(), new AnchorConstraint(112, 987, 166, 20, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJLabel3(), new AnchorConstraint(-200, 951, -61, -166, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJLabel4(), new AnchorConstraint(-19, 985, 128, 4, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJCheckBox1(), new AnchorConstraint(877, 948, 948, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJCheckBox2(), new AnchorConstraint(751, 948, 827, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJCheckBox3(), new AnchorConstraint(625, 948, 702, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJCheckBox4(), new AnchorConstraint(505, 948, 581, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJCheckBox5(), new AnchorConstraint(379, 948, 450, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJCheckBox6(), new AnchorConstraint(254, 948, 325, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
            jPanel2.add(getJCheckBox7(), new AnchorConstraint(133, 948, 232, 79, AnchorConstraint.ANCHOR_REL,
                    AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
        }
        return jPanel2;
    }

    /**
     * <p>Getter for the field <code>jLabel3</code>.</p>
     *
     * @return a {@link javax.swing.JLabel} object.
     */
    private JLabel getJLabel3() {
        if (jLabel3 == null) {
            jLabel3 = new JLabel();
            jLabel3.setText("Color");
            jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14));
            jLabel3.setPreferredSize(new java.awt.Dimension(152, 39));
            jLabel3.setLayout(null);
        }
        return jLabel3;
    }

    /**
     * <p>Getter for the field <code>jLabel4</code>.</p>
     *
     * @return a {@link javax.swing.JLabel} object.
     */
    private JLabel getJLabel4() {
        if (jLabel4 == null) {
            jLabel4 = new JLabel();
            jLabel4.setText("Type");
            jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14));
            jLabel4.setPreferredSize(new java.awt.Dimension(105, 27));
            jLabel4.setLayout(null);
        }
        return jLabel4;
    }

    /**
     * <p>Getter for the field <code>jSeparator2</code>.</p>
     *
     * @return a {@link javax.swing.JSeparator} object.
     */
    private JSeparator getJSeparator2() {
        if (jSeparator2 == null) {
            jSeparator2 = new JSeparator();
            jSeparator2.setPreferredSize(new java.awt.Dimension(116, 10));
            jSeparator2.setLayout(null);
        }
        return jSeparator2;
    }

    /**
     * <p>getJCheckBox1.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBox1() {
        if (jCheckBoxSorcery == null) {
            jCheckBoxSorcery = new JCheckBox();
            jCheckBoxSorcery.setLayout(null);
            jCheckBoxSorcery.setText("Sorcery");
            jCheckBoxSorcery.setSelected(true);
            jCheckBoxSorcery.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxSorcery.setPreferredSize(new java.awt.Dimension(93, 13));
            jCheckBoxSorcery.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxSorcery;
    }

    /**
     * <p>getJCheckBox2.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBox2() {
        if (jCheckBoxPlaneswalker == null) {
            jCheckBoxPlaneswalker = new JCheckBox();
            jCheckBoxPlaneswalker.setLayout(null);
            jCheckBoxPlaneswalker.setText("Planeswalker");
            jCheckBoxPlaneswalker.setSelected(true);
            jCheckBoxPlaneswalker.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxPlaneswalker.setPreferredSize(new java.awt.Dimension(93, 14));
            jCheckBoxPlaneswalker.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxPlaneswalker;
    }

    /**
     * <p>getJCheckBox3.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBox3() {
        if (jCheckBoxLand == null) {
            jCheckBoxLand = new JCheckBox();
            jCheckBoxLand.setLayout(null);
            jCheckBoxLand.setText("Land");
            jCheckBoxLand.setSelected(true);
            jCheckBoxLand.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxLand.setPreferredSize(new java.awt.Dimension(93, 14));
            jCheckBoxLand.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxLand;
    }

    /**
     * <p>getJCheckBox4.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBox4() {
        if (jCheckBoxInstant == null) {
            jCheckBoxInstant = new JCheckBox();
            jCheckBoxInstant.setLayout(null);
            jCheckBoxInstant.setText("Instant");
            jCheckBoxInstant.setSelected(true);
            jCheckBoxInstant.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxInstant.setPreferredSize(new java.awt.Dimension(93, 14));
            jCheckBoxInstant.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxInstant;
    }

    /**
     * <p>getJCheckBox5.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBox5() {
        if (jCheckBoxEnchant == null) {
            jCheckBoxEnchant = new JCheckBox();
            jCheckBoxEnchant.setLayout(null);
            jCheckBoxEnchant.setText("Enchant");
            jCheckBoxEnchant.setSelected(true);
            jCheckBoxEnchant.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxEnchant.setPreferredSize(new java.awt.Dimension(93, 13));
            jCheckBoxEnchant.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxEnchant;
    }

    /**
     * <p>getJCheckBox6.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBox6() {
        if (jCheckBoxCreature == null) {
            jCheckBoxCreature = new JCheckBox();
            jCheckBoxCreature.setLayout(null);
            jCheckBoxCreature.setText("Creature");
            jCheckBoxCreature.setSelected(true);
            jCheckBoxCreature.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxCreature.setPreferredSize(new java.awt.Dimension(93, 13));
            jCheckBoxCreature.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxCreature;
    }

    /**
     * <p>getJCheckBox7.</p>
     *
     * @return a {@link javax.swing.JCheckBox} object.
     */
    private JCheckBox getJCheckBox7() {
        if (jCheckBoxArtifact == null) {
            jCheckBoxArtifact = new JCheckBox();
            jCheckBoxArtifact.setLayout(null);
            jCheckBoxArtifact.setText("Artifact");
            jCheckBoxArtifact.setSelected(true);
            jCheckBoxArtifact.setBackground(new java.awt.Color(192, 192, 192));
            jCheckBoxArtifact.setPreferredSize(new java.awt.Dimension(93, 18));
            jCheckBoxArtifact.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return jCheckBoxArtifact;
    }

    /**
     * <p>getJTextField1.</p>
     *
     * @return a {@link javax.swing.JTextField} object.
     */
    private JTextField getJTextField1() {
        if (cardText == null) {
            cardText = new JTextField();
            cardText.setPreferredSize(new java.awt.Dimension(168, 30));
            cardText.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {

                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        FilterCardTable();
                    }
                }
            });
        }
        return cardText;
    }

    /**
     * <p>Getter for the field <code>jLabel5</code>.</p>
     *
     * @return a {@link javax.swing.JLabel} object.
     */
    private JLabel getJLabel5() {
        if (jLabel5 == null) {
            jLabel5 = new JLabel();
            jLabel5.setText("Card Text:");
            jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16));
            jLabel5.setPreferredSize(new java.awt.Dimension(75, 50));
            jLabel5.setLayout(null);
        }
        return jLabel5;
    }


    public class CustomListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            FilterCardTable();
        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

        public void mousePressed(MouseEvent e) {

        }

        public void mouseReleased(MouseEvent e) {

        }
    }

    public class WListener implements WindowListener {

        public void windowActivated(WindowEvent arg0) {

        }

        public void windowClosed(WindowEvent arg0) {

        }

        public void windowClosing(WindowEvent arg0) {
            Gui_DeckEditor g = (Gui_DeckEditor) deckDisplay;
            g.setEnabled(true);
        }

        public void windowDeactivated(WindowEvent arg0) {

        }

        public void windowDeiconified(WindowEvent arg0) {

        }

        public void windowIconified(WindowEvent arg0) {

        }

        public void windowOpened(WindowEvent arg0) {

        }
    }

    /**
     * <p>FilterCardTable.</p>
     */
    private void FilterCardTable() {

        String name = NameText.getText();
        String cText = cardText.getText();

		/*
		 * TODO Braids: "getAllCards copies the entire array, but that does not
		 * seem to be needed here. Significant performance improvement is
		 * possible if this code used getCards instead (along with a for each
		 * loop instead of using get(i), if applicable)."
		 */
        CardList filterCardList = AllZone.getCardFactory().getAllCards();
        CardFilter filter = new CardFilter();
        Gui_DeckEditor g = (Gui_DeckEditor) deckDisplay;
        g.blackCheckBox.setSelected(true);
        g.blackCheckBox.setEnabled(true);
        g.blueCheckBox.setSelected(true);
        g.blueCheckBox.setEnabled(true);
        g.greenCheckBox.setSelected(true);
        g.greenCheckBox.setEnabled(true);
        g.redCheckBox.setSelected(true);
        g.redCheckBox.setEnabled(true);
        g.whiteCheckBox.setSelected(true);
        g.whiteCheckBox.setEnabled(true);
        g.colorlessCheckBox.setSelected(true);
        g.colorlessCheckBox.setEnabled(true);
        g.artifactCheckBox.setSelected(true);
        g.artifactCheckBox.setEnabled(true);
        g.creatureCheckBox.setSelected(true);
        g.creatureCheckBox.setEnabled(true);
        g.enchantmentCheckBox.setSelected(true);
        g.enchantmentCheckBox.setEnabled(true);
        g.instantCheckBox.setSelected(true);
        g.instantCheckBox.setEnabled(true);
        g.landCheckBox.setSelected(true);
        g.landCheckBox.setEnabled(true);
        g.planeswalkerCheckBox.setSelected(true);
        g.planeswalkerCheckBox.setEnabled(true);
        g.sorceryCheckBox.setSelected(true);
        g.sorceryCheckBox.setEnabled(true);
        g.setEnabled(true);
        if (name != "") {
            if (cText != "") {
                filterCardList = filter.CardListNameFilter(filterCardList, name);
                if (filterCardList.size() == 0) {
                    JOptionPane.showMessageDialog(null, "Sorry, cards with name: " + name + " not found.",
                            "Filter", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    filterCardList = filter.CardListTextFilter(filterCardList, cText);
                    if (filterCardList.size() == 0) {
                        JOptionPane.showMessageDialog(null, "Sorry, cards with text: " + cText + " not found.",
                                "Filter", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        if (jCheckBoxBlack.isSelected() == false) {
                            filterCardList = filter.CardListColorFilter(filterCardList, "black");
                            g.blackCheckBox.setSelected(false);
                            g.blackCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxBlue.isSelected() == false) {
                            filterCardList = filter.CardListColorFilter(filterCardList, "blue");
                            g.blueCheckBox.setSelected(false);
                            g.blueCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxGreen.isSelected() == false) {
                            filterCardList = filter.CardListColorFilter(filterCardList, "green");
                            g.greenCheckBox.setSelected(false);
                            g.greenCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxRed.isSelected() == false) {
                            filterCardList = filter.CardListColorFilter(filterCardList, "red");
                            g.redCheckBox.setSelected(false);
                            g.redCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxWhite.isSelected() == false) {
                            filterCardList = filter.CardListColorFilter(filterCardList, "white");
                            g.whiteCheckBox.setSelected(false);
                            g.whiteCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxColorless.isSelected() == false) {
                            filterCardList = filter.CardListColorFilter(filterCardList, "colorless");
                            g.colorlessCheckBox.setSelected(false);
                            g.colorlessCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxArtifact.isSelected() == false) {
                            filterCardList = filter.CardListTypeFilter(filterCardList, "artifact");
                            g.artifactCheckBox.setSelected(false);
                            g.artifactCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxCreature.isSelected() == false) {
                            filterCardList = filter.CardListTypeFilter(filterCardList, "creature");
                            g.creatureCheckBox.setSelected(false);
                            g.creatureCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxEnchant.isSelected() == false) {
                            filterCardList = filter.CardListTypeFilter(filterCardList, "enchantment");
                            g.enchantmentCheckBox.setSelected(false);
                            g.enchantmentCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxInstant.isSelected() == false) {
                            filterCardList = filter.CardListTypeFilter(filterCardList, "instant");
                            g.instantCheckBox.setSelected(false);
                            g.instantCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxLand.isSelected() == false) {
                            filterCardList = filter.CardListTypeFilter(filterCardList, "land");
                            g.landCheckBox.setSelected(false);
                            g.landCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxPlaneswalker.isSelected() == false) {
                            filterCardList = filter.CardListTypeFilter(filterCardList, "planeswalker");
                            g.planeswalkerCheckBox.setSelected(false);
                            g.planeswalkerCheckBox.setEnabled(false);
                        }
                        if (jCheckBoxSorcery.isSelected() == false) {
                            filterCardList = filter.CardListTypeFilter(filterCardList, "sorcery");
                            g.sorceryCheckBox.setSelected(false);
                            g.sorceryCheckBox.setEnabled(false);
                        }
                        deckDisplay.updateDisplay(filterCardList, deckDisplay.getBottom());
                    }
                }
            }

        }

        dispose();

    }

}