package forge.card.cardFactory;


import com.esotericsoftware.minlog.Log;
import forge.*;
import forge.card.spellability.*;
import forge.gui.GuiUtils;
import forge.gui.input.Input;
import forge.gui.input.Input_PayManaCost;

import java.util.*;
import java.util.Map.Entry;


/**
 * <p>CardFactoryUtil class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class CardFactoryUtil {
    private static Random random = MyRandom.random;

    /**
     * <p>AI_getMostExpensivePermanent.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @param spell a {@link forge.Card} object.
     * @param targeted a boolean.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getMostExpensivePermanent(CardList list, final Card spell, boolean targeted) {
        CardList all = list;
        if (targeted) {
            all = all.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return CardFactoryUtil.canTarget(spell, c);
                }
            });
        }
        
       return AI_getMostExpensivePermanent(all);
    }
        
     public static Card AI_getMostExpensivePermanent(CardList all){ 
    	 if (all.size() == 0) return null;
    	 Card biggest = null;
    	 biggest = all.get(0);

    	 int bigCMC = 0;
    	 for (int i = 0; i < all.size(); i++) {
    		 Card card = all.get(i);
    		 int curCMC = card.getCMC();

    		 //Add all cost of all auras with the same controller
    		 CardList auras = new CardList(card.getEnchantedBy().toArray());
    		 auras.getController(card.getController());
    		 curCMC += auras.getTotalConvertedManaCost() + auras.size();

    		 if (curCMC >= bigCMC) {
    			 bigCMC = curCMC;
    			 biggest = all.get(i);
    		 }
    	 }

    	 return biggest;
    }

    //for Sarkhan the Mad
    /**
     * <p>AI_getCheapestCreature.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @param spell a {@link forge.Card} object.
     * @param targeted a boolean.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getCheapestCreature(CardList list, final Card spell, boolean targeted) {
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.isCreature();
            }
        });
        return AI_getCheapestPermanent(list, spell, targeted);
    }

    /**
     * <p>AI_getCheapestPermanent.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @param spell a {@link forge.Card} object.
     * @param targeted a boolean.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getCheapestPermanent(CardList list, final Card spell, boolean targeted) {
        CardList all = list;
        if (targeted) {
            all = all.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return CardFactoryUtil.canTarget(spell, c);
                }
            });
        }
        if (all.size() == 0) return null;

        //get cheapest card:
        Card cheapest = null;
        cheapest = all.get(0);

        for (int i = 0; i < all.size(); i++) {
            if (CardUtil.getConvertedManaCost(cheapest.getManaCost()) <= CardUtil.getConvertedManaCost(cheapest.getManaCost())) {
                cheapest = all.get(i);
            }
        }

        return cheapest;

    }

    /**
     * <p>AI_getBestLand.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getBestLand(CardList list) {
        CardList land = list.getType("Land");
        if (!(land.size() > 0)) return null;

        CardList nbLand = land.filter(new CardListFilter() // prefer to target non basic lands
        {
            public boolean addCard(Card c) {
                return (!c.isBasicLand());
            }
        });

        if (nbLand.size() > 0) {
            //TODO: Rank non basics?

            Random r = MyRandom.random;
            return nbLand.get(r.nextInt(nbLand.size()));
        }

        // if no non-basic lands, target the least represented basic land type
        String names[] = {"Plains", "Island", "Swamp", "Mountain", "Forest"};
        String sminBL = "";
        int iminBL = 20000; // hopefully no one will ever have more than 20000 lands of one type....
        int n = 0;
        for (int i = 0; i < 5; i++) {
            n = land.getType(names[i]).size();
            if (n < iminBL && n > 0) // if two or more are tied, only the first one checked will be used
            {
                iminBL = n;
                sminBL = names[i];
            }
        }
        if (iminBL == 20000) return null; // no basic land was a minimum

        CardList BLand = land.getType(sminBL);
        for (int i = 0; i < BLand.size(); i++)
            if (!BLand.get(i).isTapped()) // prefer untapped lands
                return BLand.get(i);

        Random r = MyRandom.random;
        return BLand.get(r.nextInt(BLand.size())); // random tapped land of least represented type
    }


    //The AI doesn't really pick the best enchantment, just the most expensive.
    /**
     * <p>AI_getBestEnchantment.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @param spell a {@link forge.Card} object.
     * @param targeted a boolean.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getBestEnchantment(CardList list, final Card spell, boolean targeted) {
        CardList all = list;
        all = all.getType("Enchantment");
        if (targeted) {
            all = all.filter(new CardListFilter() {

                public boolean addCard(Card c) {
                    return CardFactoryUtil.canTarget(spell, c);
                }
            });
        }
        if (all.size() == 0) {
            return null;
        }

        //get biggest Enchantment
        Card biggest = null;
        biggest = all.get(0);

        int bigCMC = 0;
        for (int i = 0; i < all.size(); i++) {
            int curCMC = CardUtil.getConvertedManaCost(all.get(i).getManaCost());

            if (curCMC > bigCMC) {
                bigCMC = curCMC;
                biggest = all.get(i);
            }
        }

        return biggest;
    }


    //The AI doesn't really pick the best artifact, just the most expensive.
    /**
     * <p>AI_getBestArtifact.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getBestArtifact(CardList list) {
        CardList all = list;
        all = all.getType("Artifact");
        if (all.size() == 0) {
            return null;
        }

        //get biggest Artifact
        Card biggest = null;
        biggest = all.get(0);

        int bigCMC = 0;
        for (int i = 0; i < all.size(); i++) {
            int curCMC = CardUtil.getConvertedManaCost(all.get(i).getManaCost());

            if (curCMC > bigCMC) {
                bigCMC = curCMC;
                biggest = all.get(i);
            }
        }

        return biggest;
    }

    /**
     * <p>AI_getHumanArtifact.</p>
     *
     * @param spell a {@link forge.Card} object.
     * @param targeted a boolean.
     * @return a {@link forge.CardList} object.
     */
    public static CardList AI_getHumanArtifact(final Card spell, boolean targeted) {
        CardList artifact = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
        artifact = artifact.getType("Artifact");
        if (targeted) {
            artifact = artifact.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return canTarget(spell, c);
                }
            });
        }
        return artifact;
    }

    /**
     * <p>AI_doesCreatureAttack.</p>
     *
     * @param card a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean AI_doesCreatureAttack(Card card) {
        Combat combat = ComputerUtil.getAttackers();
        Card[] att = combat.getAttackers();
        for (int i = 0; i < att.length; i++)
            if (att[i].equals(card)) return true;

        return false;
    }

    /**
     * <p>evaluateCreatureList.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a int.
     */
    public static int evaluateCreatureList(CardList list) {
        int value = 0;
        for (int i = 0; i < list.size(); i++) value += evaluateCreature(list.get(i));

        return value;
    }

    /**
     * <p>evaluatePermanentList.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a int.
     */
    public static int evaluatePermanentList(CardList list) {
        int value = 0;
        for (int i = 0; i < list.size(); i++) value += list.get(i).getCMC() + 1;

        return value;
    }

    /**
     * <p>evaluateCreature.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a int.
     */
    public static int evaluateCreature(Card c) {

        int value = 100;
        if (c.isToken()) value = 80; //tokens should be worth less than actual cards
        int power = c.getNetAttack();
        int toughness = c.getNetDefense();

        //Doran
        if (AllZoneUtil.isCardInPlay("Doran, the Siege Tower")) power = toughness;

        if (c.hasKeyword("Prevent all combat damage that would be dealt by CARDNAME.")
                || c.hasKeyword("Prevent all damage that would be dealt by CARDNAME.")
                || c.hasKeyword("Prevent all combat damage that would be dealt to and dealt by CARDNAME.")
                || c.hasKeyword("Prevent all damage that would be dealt to and dealt by CARDNAME."))
            power = 0;

        value += power * 15;
        value += toughness * 10;
        value += c.getCMC() * 5;

        //Evasion keywords
        if (c.hasKeyword("Flying")) value += power * 10;
        if (c.hasKeyword("Horsemanship")) value += power * 10;
        if (c.hasKeyword("Unblockable")) value += power * 10;
        if (c.hasKeyword("Fear")) value += power * 6;
        if (c.hasKeyword("Intimidate")) value += power * 6;
        if (c.hasStartOfKeyword("CARDNAME can't be blocked except by")) value += power * 5;
        if (c.hasStartOfKeyword("CARDNAME can't be blocked by")) value += power * 2;

        //Battle stats increasing keywords
        if (c.hasKeyword("Double Strike")) value += 10 + power * 15;
        value += c.getKeywordMagnitude("Bushido") * 16;
        value += c.getAmountOfKeyword("Flanking") * 15;

        //Other good keywords
        if (c.hasKeyword("Deathtouch") && power > 0) value += 25;
        value += c.getAmountOfKeyword("Exalted") * 15;
        if (c.hasKeyword("First Strike") && !c.hasKeyword("Double Strike") && power > 0) value += 10 + power * 5;
        if (c.hasKeyword("Lifelink")) value += power * 10;
        if (c.hasKeyword("Trample") && power > 1) value += power * 3;
        if (c.hasKeyword("Vigilance")) value += power * 5 + toughness * 5;
        if (c.hasKeyword("Wither")) value += power * 10;
        value += c.getKeywordMagnitude("Rampage");
        value += c.getKeywordMagnitude("Annihilator") * 50;
        if (c.hasKeyword("Changeling")) value += 5;
        if (c.hasKeyword("Whenever a creature dealt damage by CARDNAME this turn is put into a graveyard, put a +1/+1 counter on CARDNAME.")
                && power > 0) value += 2;
        if (c.hasKeyword("Whenever a creature dealt damage by CARDNAME this turn is put into a graveyard, put a +2/+2 counter on CARDNAME.")
                && power > 0) value += 4;
        if (c.hasKeyword("Whenever CARDNAME is dealt damage, put a +1/+1 counter on it.")) value += 10;

        //Defensive Keywords
        if (c.hasKeyword("Reach")) value += 5;
        if (c.hasKeyword("CARDNAME can block creatures with shadow as though they didn't have shadow.")) value += 3;

        //Protection
        if (c.hasKeyword("Indestructible")) value += 70;
        if (c.hasKeyword("Prevent all damage that would be dealt to CARDNAME.")) value += 60;
        if (c.hasKeyword("Prevent all combat damage that would be dealt to CARDNAME.")) value += 50;
        if (c.hasKeyword("Shroud")) value += 30;
        if (c.hasKeyword("Hexproof")) value += 35;
        if (c.hasStartOfKeyword("Protection")) value += 20;
        if (c.hasStartOfKeyword("PreventAllDamageBy")) value += 10;
        value += c.getKeywordMagnitude("Absorb") * 11;

        //Activated Abilities
        if (c.hasStartOfKeyword("ab")) value += 10;

        //Bad keywords
        if (c.hasKeyword("Defender") || c.hasKeyword("CARDNAME can't attack.")) value -= power * 9 + 40;
        if (c.hasKeyword("CARDNAME can't block.")) value -= 10;
        if (c.hasKeyword("CARDNAME attacks each turn if able.")) value -= 10;
        if (c.hasKeyword("CARDNAME can block only creatures with flying.")) value -= toughness * 5;

        if (c.hasStartOfKeyword("When CARDNAME is dealt damage, destroy it.")) value -= (toughness - 1) * 9;

        if (c.hasKeyword("CARDNAME can't attack or block.")) value = 50 + c.getCMC() * 5; //reset everything - useless
        if (c.hasKeyword("At the beginning of the end step, destroy CARDNAME.")) value -= 50;
        if (c.hasKeyword("At the beginning of the end step, exile CARDNAME.")) value -= 50;
        if (c.hasKeyword("At the beginning of the end step, sacrifice CARDNAME.")) value -= 50;
        if (c.hasStartOfKeyword("At the beginning of your upkeep, CARDNAME deals")) value -= 20;
        if (c.hasStartOfKeyword("At the beginning of your upkeep, destroy CARDNAME unless you pay")) value -= 20;
        if (c.hasStartOfKeyword("At the beginning of your upkeep, sacrifice CARDNAME unless you pay")) value -= 20;
        if (c.hasStartOfKeyword("Upkeep:")) value -= 20;
        if (c.hasStartOfKeyword("Cumulative upkeep")) value -= 30;
        if (c.hasStartOfKeyword("(Echo unpaid)")) value -= 10;
        if (c.hasStartOfKeyword("Fading")) value -= 20; //not used atm
        if (c.hasStartOfKeyword("Vanishing")) value -= 20; //not used atm

        if (c.isUntapped()) value += 1;

        return value;

    } //evaluateCreature

    //returns null if list.size() == 0
    /**
     * <p>AI_getBestCreature.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a {@link forge.Card} object.
     */
    
    public static Card AI_getBest(CardList list) {
    	// Get Best will filter by appropriate getBest list if ALL of the list is of that type
    	if (list.getNotType("Creature").size() == 0)
    		return AI_getBestCreature(list);
    	
    	if (list.getNotType("Land").size() == 0)
    		return AI_getBestLand(list);
    
    	// TODO: Once we get an EvaluatePermanent this should call getBestPermanent()
    	return AI_getMostExpensivePermanent(list);
    }
    
    public static Card AI_getBestCreature(CardList list) {
        CardList all = list;
        all = all.getType("Creature");
        Card biggest = null;

        if (all.size() != 0) {
            biggest = all.get(0);

            for (int i = 0; i < all.size(); i++)
                if (evaluateCreature(biggest) < evaluateCreature(all.get(i))) biggest = all.get(i);
        }
        return biggest;
    }

    //This selection rates tokens higher
    /**
     * <p>AI_getBestCreatureToBounce.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getBestCreatureToBounce(CardList list) {
        int tokenBonus = 40;
        CardList all = list;
        all = all.getType("Creature");
        Card biggest = null;         //returns null if list.size() == 0
        int biggestvalue = 0;
        int newvalue = 0;

        if (all.size() != 0) {
            biggest = all.get(0);

            for (int i = 0; i < all.size(); i++) {
                biggestvalue = evaluateCreature(biggest);
                if (biggest.isToken()) biggestvalue += tokenBonus;     // raise the value of tokens
                newvalue = evaluateCreature(all.get(i));
                if (all.get(i).isToken()) newvalue += tokenBonus;        // raise the value of tokens
                if (biggestvalue < newvalue) biggest = all.get(i);
            }
        }
        return biggest;
    }

    //returns null if list.size() == 0
    /**
     * <p>AI_getWorstCreature.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getWorstCreature(CardList list) {
        CardList all = list;
        all = all.getType("Creature");
        //get smallest creature
        Card smallest = null;

        if (all.size() != 0) {
            smallest = all.get(0);

            for (int i = 0; i < all.size(); i++)
                if (evaluateCreature(smallest) > evaluateCreature(all.get(i))) smallest = all.get(i);
        }
        return smallest;
    }

    /**
     * <p>AI_getWorstPermanent.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @param biasEnch a boolean.
     * @param biasLand a boolean.
     * @param biasArt a boolean.
     * @param biasCreature a boolean.
     * @return a {@link forge.Card} object.
     */
    public static Card AI_getWorstPermanent(final CardList list, boolean biasEnch, boolean biasLand, boolean biasArt, boolean biasCreature) {
        if (list.size() == 0) return null;

        if (biasEnch && list.getType("Enchantment").size() > 0) {
            return AI_getCheapestPermanent(list.getType("Enchantment"), null, false);
        }

        if (biasArt && list.getType("Artifact").size() > 0) {
            return AI_getCheapestPermanent(list.getType("Artifact"), null, false);
        }

        if (biasLand && list.getType("Land").size() > 0) {
            return getWorstLand(list.getType("Land"));
        }

        if (biasCreature && list.getType("Creature").size() > 0) {
            return AI_getWorstCreature(list.getType("Creature"));
        }

        if (list.getType("Land").size() > 6) {
            return getWorstLand(list.getType("Land"));
        }

        if (list.getType("Artifact").size() > 0 || list.getType("Enchantment").size() > 0) {
            return AI_getCheapestPermanent(list.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return c.isArtifact() || c.isEnchantment();
                }
            }), null, false);
        }

        if (list.getType("Creature").size() > 0) {
            return AI_getWorstCreature(list.getType("Creature"));
        }

        //Planeswalkers fall through to here, lands will fall through if there aren't very many
        return AI_getCheapestPermanent(list, null, false);
    }

    /**
     * <p>input_Spell.</p>
     *
     * @param spell a {@link forge.card.spellability.SpellAbility} object.
     * @param choices a {@link forge.CardList} object.
     * @param free a boolean.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input input_Spell(final SpellAbility spell, final CardList choices, final boolean free) {
        Input target = new Input() {
            private static final long serialVersionUID = 2781418414287281005L;

            @Override
            public void showMessage() {
                if (choices.size() == 0) stop();
                if (spell.getTargetCard() != null) stop();
                AllZone.getDisplay().showMessage("Select target Spell: ");
                Card choice = GuiUtils.getChoiceOptional("Choose a Spell", choices.toArray());
                if (choice != null) {
                    spell.setTargetCard(choice);
                    done();
                } else stop();

            }

            @Override
            public void selectButtonCancel() {
                stop();
            }

            void done() {
                choices.clear();
                if (spell.getManaCost().equals("0") || this.isFree()) {
                    if (spell.getTargetCard() != null) AllZone.getStack().add(spell);
                    stop();
                } else stopSetNext(new Input_PayManaCost(spell));
            }
        };
        return target;
    }//input_targetSpell()

    /**
     * <p>input_destroyNoRegeneration.</p>
     *
     * @param choices a {@link forge.CardList} object.
     * @param message a {@link java.lang.String} object.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input input_destroyNoRegeneration(final CardList choices, final String message) {
        Input target = new Input() {
            private static final long serialVersionUID = -6637588517573573232L;

            @Override
            public void showMessage() {
                AllZone.getDisplay().showMessage(message);
                ButtonUtil.disableAll();
            }

            @Override
            public void selectCard(Card card, PlayerZone zone) {
                if (choices.contains(card)) {
                    AllZone.getGameAction().destroyNoRegeneration(card);
                    stop();
                }
            }
        };
        return target;
    }//input_destroyNoRegeneration()

    /**
     * <p>ability_Flashback.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param cost a {@link java.lang.String} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility ability_Flashback(final Card sourceCard, String cost) {
        Cost fbCost = new Cost(cost, sourceCard.getName(), true);
        final SpellAbility flashback = new Spell(sourceCard) {

            private static final long serialVersionUID = -4196027546564209412L;

            @Override
            public void resolve() {
                SpellAbility[] sa = sourceCard.getSpellAbility();
                AllZone.getGameAction().moveToStack(sourceCard);
                SpellAbility flash = sa[0];
                flash.setFlashBackAbility(true);
                AllZone.getStack().add(flash);
            }

            @Override
            public boolean canPlayAI() {
                return ComputerUtil.canPayCost(this);
            }

            @Override
            public boolean canPlay() {
                Card sourceCard = this.getSourceCard();

                return AllZoneUtil.isCardInPlayerGraveyard(sourceCard.getController(), sourceCard)
                        && (sourceCard.isInstant() || Phase.canCastSorcery(sourceCard.getController()));

            }

        };

        flashback.setPayCosts(fbCost);

        String costString = fbCost.toString().replace(":", ".");

        StringBuilder sbDesc = new StringBuilder();
        sbDesc.append("Flashback: ").append(costString);
        flashback.setDescription(sbDesc.toString());
        // possibly add Flashback into here?

        StringBuilder sbStack = new StringBuilder();
        sbStack.append("Flashback: ").append(sourceCard.getName());
        flashback.setStackDescription(sbStack.toString());

        return flashback;

    }//ability_Flashback()

    /**
     * <p>ability_Unearth.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param manaCost a {@link java.lang.String} object.
     * @return a {@link forge.card.spellability.Ability_Activated} object.
     */
    public static Ability_Activated ability_Unearth(final Card sourceCard, String manaCost) {

        Cost cost = new Cost(manaCost, sourceCard.getName(), true);
        final Ability_Activated unearth = new Ability_Activated(sourceCard, cost, null) {
            private static final long serialVersionUID = -5633945565395478009L;

            @Override
            public void resolve() {
                Card card = AllZone.getGameAction().moveToPlay(sourceCard);

                card.addIntrinsicKeyword("At the beginning of the end step, exile CARDNAME.");
                card.addIntrinsicKeyword("Haste");
                card.setUnearthed(true);
            }

            @Override
            public boolean canPlayAI() {
                if (AllZone.getPhase().isAfter(Constant.Phase.Main1) || AllZone.getPhase().isPlayerTurn(AllZone.getHumanPlayer()))
                    return false;
                return ComputerUtil.canPayCost(this);
            }
        };
        SpellAbility_Restriction restrict = new SpellAbility_Restriction();
        restrict.setZone("Graveyard");
        restrict.setSorcerySpeed(true);
        unearth.setRestrictions(restrict);

        StringBuilder sbStack = new StringBuilder();
        sbStack.append("Unearth: ").append(sourceCard.getName());
        unearth.setStackDescription(sbStack.toString());

        return unearth;
    }//ability_Unearth()

    /**
     * <p>ability_Morph_Down.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility ability_Morph_Down(final Card sourceCard) {
        final SpellAbility morph_down = new Spell(sourceCard) {
            private static final long serialVersionUID = -1438810964807867610L;

            @Override
            public void resolve() {
                //card.setName("Morph");
                sourceCard.setIsFaceDown(true);
                sourceCard.setManaCost("");
                sourceCard.setColor(new ArrayList<Card_Color>()); //remove all colors
                sourceCard.addColor("0");
                sourceCard.setBaseAttack(2);
                sourceCard.setBaseDefense(2);
                sourceCard.comesIntoPlay();
                sourceCard.setIntrinsicKeyword(new ArrayList<String>()); //remove all keywords
                sourceCard.setType(new ArrayList<String>()); //remove all types
                sourceCard.addType("Creature");

                AllZone.getGameAction().moveToPlay(sourceCard);
            }

            @Override
            public boolean canPlay() {
                return Phase.canCastSorcery(sourceCard.getController())
                        && !AllZoneUtil.isCardInPlay(sourceCard);
            }

        };

        morph_down.setManaCost("3");
        morph_down.setDescription("(You may cast this face down as a 2/2 creature for 3.)");
        morph_down.setStackDescription("Morph - Creature 2/2");

        return morph_down;
    }

    /**
     * <p>ability_Morph_Up.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param cost a {@link forge.card.spellability.Cost} object.
     * @param orgManaCost a {@link java.lang.String} object.
     * @param a a int.
     * @param d a int.
     * @return a {@link forge.card.spellability.Ability_Activated} object.
     */
    public static Ability_Activated ability_Morph_Up(final Card sourceCard, Cost cost, String orgManaCost, int a, int d) {
        //final String player = sourceCard.getController();
        //final String manaCost = cost;
        final int attack = a;
        final int defense = d;
        final String origManaCost = orgManaCost;
        final Ability_Activated morph_up = new Ability_Activated(sourceCard, cost, null) {
            private static final long serialVersionUID = -3663857013937085953L;

            @Override
            public void resolve() {
                //card.setName("Morph");
                sourceCard.setIsFaceDown(false);
                sourceCard.setManaCost(origManaCost);
                sourceCard.addColor(origManaCost);
                sourceCard.setBaseAttack(attack);
                sourceCard.setBaseDefense(defense);
                sourceCard.setIntrinsicKeyword(sourceCard.getPrevIntrinsicKeyword());
                sourceCard.setType(sourceCard.getPrevType());
                sourceCard.turnFaceUp();
            }

            @Override
            public boolean canPlay() {
                // unMorphing a card is a Special Action, and not affected by Linvala
                return sourceCard.getController().equals(this.getActivatingPlayer()) && sourceCard.isFaceDown()
                        && AllZoneUtil.isCardInPlay(sourceCard);
            }

        };//morph_up

        //morph_up.setManaCost(cost);
        String costDesc = cost.toString();
        //get rid of the ": " at the end
        costDesc = costDesc.substring(0, costDesc.length() - 2);
        StringBuilder sb = new StringBuilder();
        sb.append("Morph");
        if (!cost.isOnlyManaCost()) sb.append(" -");
        sb.append(" ").append(costDesc).append(" (Turn this face up any time for its morph cost.)");
        morph_up.setDescription(sb.toString());

        StringBuilder sbStack = new StringBuilder();
        sbStack.append(sourceCard.getName()).append(" - turn this card face up.");
        morph_up.setStackDescription(sbStack.toString());

        return morph_up;
    }

    /**
     * <p>ability_Rebel_Search.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param cost a {@link java.lang.String} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility ability_Rebel_Search(final Card sourceCard, String cost) {
        final int converted = Integer.parseInt(cost) - 1;

        final Cost abCost = new Cost("T", sourceCard.getName(), true);
        abCost.setMana(cost);
        final SpellAbility ability = new Ability_Activated(sourceCard, abCost, null) {
            private static final long serialVersionUID = 7219065355049285681L;

            @Override
            public boolean canPlay() {
                for (int i = 0; i < AllZone.getStack().size(); i++) {
                    if (AllZone.getStack().peekInstance(i).getSourceCard().equals(sourceCard)) return false;
                }

                if (AllZoneUtil.isCardInPlay(sourceCard)
                        && !sourceCard.hasSickness()
                        && !sourceCard.isTapped()
                        && super.canPlay()) return true;
                else return false;
            }

            @Override
            public boolean canPlayAI() {
                CardList rebels = new CardList();
                CardList list = AllZoneUtil.getPlayerCardsInLibrary(sourceCard.getController());
                list = list.filter(new CardListFilter() {
                    public boolean addCard(Card c) {
                        return ((c.isType("Rebel") || c.hasKeyword("Changeling")))
                                && c.isPermanent();
                    }
                });

                if (list.size() == 0) return false;

                for (int i = 0; i < list.size(); i++) {
                    if (CardUtil.getConvertedManaCost(list.get(i).getManaCost()) <= converted) {
                        rebels.add(list.get(i));
                    }
                }

                if (AllZone.getPhase().getPhase().equals(Constant.Phase.Main2)
                        && rebels.size() > 0) return true;
                else return false;

            }


            @Override
            public void resolve() {

                CardList rebels = new CardList();
                CardList list = AllZoneUtil.getPlayerCardsInLibrary(sourceCard.getController());
                list = list.getType("Rebel");
                list = list.getPermanents();

                if (list.size() == 0) return;

                for (int i = 0; i < list.size(); i++) {
                    if (CardUtil.getConvertedManaCost(list.get(i).getManaCost()) <= converted) {
                        rebels.add(list.get(i));
                    }
                }
                if (rebels.size() == 0) return;

                if (sourceCard.getController().isComputer()) {
                    Card rebel = AI_getBestCreature(rebels);
                    AllZone.getGameAction().moveToPlay(rebel);
                } else //human
                {
                    Object o = GuiUtils.getChoiceOptional("Select target Rebel", rebels.toArray());
                    if (o != null) {
                        Card rebel = (Card) o;
                        AllZone.getGameAction().moveToPlay(rebel);
                        if (rebel.isAura()) {
                            Object obj = null;
                            if (rebel.hasKeyword("Enchant creature")) {
                                CardList creats = AllZoneUtil.getCreaturesInPlay();
                                obj = GuiUtils.getChoiceOptional("Pick a creature to attach "
                                        + rebel.getName() + " to", creats.toArray());
                            }
                            if (obj != null) {
                                Card target = (Card) obj;
                                if (AllZoneUtil.isCardInPlay(target)) {
                                    rebel.enchantCard(target);
                                }
                            }
                        }
                    }
                }
                sourceCard.getController().shuffle();
            }
        };
        StringBuilder sbDesc = new StringBuilder();
        sbDesc.append(cost).append(", tap: Search your library for a Rebel permanent card with converted mana cost ");
        sbDesc.append(converted).append(" or less and put it onto the battlefield. Then shuffle your library.");
        ability.setDescription(sbDesc.toString());

        StringBuilder sbStack = new StringBuilder();
        sbStack.append(sourceCard.getName()).append(" - search for a Rebel and put it onto the battlefield.");
        ability.setStackDescription(sbStack.toString());

        return ability;
    }

    /**
     * <p>ability_cycle.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param cycleCost a {@link java.lang.String} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility ability_cycle(final Card sourceCard, String cycleCost) {
        cycleCost += " Discard<1/CARDNAME>";
        Cost abCost = new Cost(cycleCost, sourceCard.getName(), true);

        final SpellAbility cycle = new Ability_Activated(sourceCard, abCost, null) {
            private static final long serialVersionUID = -4960704261761785512L;

            @Override
            public boolean canPlayAI() {
            	
            	if(AllZone.getPhase().isBefore(Constant.Phase.Main2))
            		return false;

                //The AI should cycle lands if it has 6 already and no cards in hand with higher CMC
                CardList hand = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());
                CardList lands = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer());
                lands.addAll(hand);
                lands = lands.getType("Land");

                if (sourceCard.isLand() && lands.size() >= Math.max(hand.getHighestConvertedManaCost(), 6))
                    return true;

                //TODO: When else should AI Cycle?
                return false;
            }

            @Override
            public boolean canPlay() {
                if (AllZoneUtil.isCardInPlay("Stabilizer")) return false;
                return super.canPlay();
            }

            @Override
            public void resolve() {
                sourceCard.getController().drawCard();
                sourceCard.cycle();
            }
        };
        cycle.setIsCycling(true);
        StringBuilder sbDesc = new StringBuilder();
        sbDesc.append("Cycling ").append(cycle.getManaCost()).append(" (").append(abCost.toString()).append(" Draw a card.)");
        cycle.setDescription(sbDesc.toString());

        StringBuilder sbStack = new StringBuilder();
        sbStack.append(sourceCard).append(" Cycling: Draw a card");
        cycle.setStackDescription(sbStack.toString());

        cycle.getRestrictions().setZone(Constant.Zone.Hand);
        return cycle;
    }//ability_cycle()

    /**
     * <p>ability_typecycle.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param cycleCost a {@link java.lang.String} object.
     * @param type a {@link java.lang.String} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility ability_typecycle(final Card sourceCard, String cycleCost, final String type) {
        String description;
        cycleCost += " Discard<1/CARDNAME>";
        Cost abCost = new Cost(cycleCost, sourceCard.getName(), true);

        final SpellAbility cycle = new Ability_Activated(sourceCard, abCost, null) {
            private static final long serialVersionUID = -4960704261761785512L;

            @Override
            public boolean canPlayAI() {
                return false;
            }

            // some AI code could be added (certain colored mana needs analyze method maybe)

            @Override
            public boolean canPlay() {
                if (AllZoneUtil.isCardInPlay("Stabilizer")) return false;
                return super.canPlay();
            }

            @Override
            public void resolve() {
                CardList cards = AllZoneUtil.getPlayerCardsInLibrary(sourceCard.getController());
                CardList sameType = new CardList();

                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i).isType(type)) {
                        sameType.add(cards.get(i));
                    }
                }

                if (sameType.size() == 0) {
                    sourceCard.getController().discard(sourceCard, this);
                    return;
                }

                Object o = GuiUtils.getChoiceOptional("Select a card", sameType.toArray());
                if (o != null) {
                    //ability.setTargetCard((Card)o);

                    sourceCard.getController().discard(sourceCard, this);
                    Card c1 = (Card) o;
                    AllZone.getGameAction().moveToHand(c1);

                }
                sourceCard.getController().shuffle();
            }
        };
        if (type.contains("Basic")) description = "Basic land";
        else description = type;

        cycle.setIsCycling(true);
        StringBuilder sbDesc = new StringBuilder();
        sbDesc.append(description).append("cycling (").append(abCost.toString()).append(" Search your library for a ");
        sbDesc.append(description).append(" card, reveal it, and put it into your hand. Then shuffle your library.)");
        cycle.setDescription(sbDesc.toString());

        StringBuilder sbStack = new StringBuilder();
        sbStack.append(sourceCard).append(" ").append(description);
        sbStack.append("cycling: Search your library for a ").append(description).append(" card.)");
        cycle.setStackDescription(sbStack.toString());

        cycle.getRestrictions().setZone(Constant.Zone.Hand);

        return cycle;
    }//ability_typecycle()


    /**
     * <p>ability_transmute.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param transmuteCost a {@link java.lang.String} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility ability_transmute(final Card sourceCard, String transmuteCost) {
        transmuteCost += " Discard<1/CARDNAME>";
        Cost abCost = new Cost(transmuteCost, sourceCard.getName(), true);

        final SpellAbility transmute = new Ability_Activated(sourceCard, abCost, null) {
            private static final long serialVersionUID = -4960704261761785512L;

            @Override
            public boolean canPlayAI() {
                return false;
            }

            @Override
            public boolean canPlay() {
                return super.canPlay() && Phase.canCastSorcery(sourceCard.getController());
            }

            @Override
            public void resolve() {
                CardList cards = AllZoneUtil.getPlayerCardsInLibrary(sourceCard.getController());
                CardList sameCost = new CardList();

                for (int i = 0; i < cards.size(); i++) {
                    if (CardUtil.getConvertedManaCost(cards.get(i).getManaCost()) == CardUtil.getConvertedManaCost(sourceCard.getManaCost())) {
                        sameCost.add(cards.get(i));
                    }
                }


                if (sameCost.size() == 0) return;


                Object o = GuiUtils.getChoiceOptional("Select a card", sameCost.toArray());
                if (o != null) {
                    //ability.setTargetCard((Card)o);

                    sourceCard.getController().discard(sourceCard, this);
                    Card c1 = (Card) o;

                    AllZone.getGameAction().moveToHand(c1);

                }
                sourceCard.getController().shuffle();
            }

        };
        StringBuilder sbDesc = new StringBuilder();
        sbDesc.append("Transmute (").append(abCost.toString());
        sbDesc.append("Search your library for a card with the same converted mana cost as this card, reveal it, ");
        sbDesc.append("and put it into your hand. Then shuffle your library. Transmute only as a sorcery.)");
        transmute.setDescription(sbDesc.toString());

        StringBuilder sbStack = new StringBuilder();
        sbStack.append(sourceCard).append(" Transmute: Search your library for a card with the same converted mana cost.)");
        transmute.setStackDescription(sbStack.toString());

        transmute.getRestrictions().setZone(Constant.Zone.Hand);
        return transmute;
    }//ability_transmute()

    /**
     * <p>ability_suspend.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param suspendCost a {@link java.lang.String} object.
     * @param suspendCounters a int.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility ability_suspend(final Card sourceCard, final String suspendCost, final int suspendCounters) {
        // be careful with Suspend ability, it will not hit the stack
        final SpellAbility suspend = new Ability_Static(sourceCard, suspendCost) {
            private static final long serialVersionUID = 21625903128384507L;

            @Override
            public boolean canPlay() {
                if (!(getRestrictions().canPlay(sourceCard, this)))
                    return false;

                if (sourceCard.isInstant())
                    return true;

                return Phase.canCastSorcery(sourceCard.getOwner());
            }

            @Override
            public boolean canPlayAI() {
                return false;
                // Suspend currently not functional for the AI,
                // seems to be an issue with regaining Priority after Suspension
            }

            @Override
            public void resolve() {
                Card c = AllZone.getGameAction().exile(sourceCard);
                c.addCounter(Counters.TIME, suspendCounters);
            }
        };
        StringBuilder sbDesc = new StringBuilder();
        sbDesc.append("Suspend ").append(suspendCounters).append(": ").append(suspendCost);
        suspend.setDescription(sbDesc.toString());

        StringBuilder sbStack = new StringBuilder();
        sbStack.append(sourceCard.getName()).append(" suspending for ").append(suspendCounters).append(" turns.)");
        suspend.setStackDescription(sbStack.toString());

        suspend.getRestrictions().setZone(Constant.Zone.Hand);
        return suspend;
    }//ability_suspend()

    /**
     * <p>eqPump_Equip.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @param Tough a int.
     * @param extrinsicKeywords an array of {@link java.lang.String} objects.
     * @param abCost a {@link forge.card.spellability.Cost} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility eqPump_Equip(final Card sourceCard, final int Power, final int Tough, final String[] extrinsicKeywords, final Cost abCost) {
        Target target = new Target(sourceCard, "Select target creature you control", "Creature.YouCtrl".split(","));
        final SpellAbility equip = new Ability_Activated(sourceCard, abCost, target) {
            private static final long serialVersionUID = -4960704261761785512L;

            @Override
            public void resolve() {
                Card targetCard = getTargetCard();
                if (AllZoneUtil.isCardInPlay(targetCard)
                        && CardFactoryUtil.canTarget(sourceCard, targetCard)) {

                    if (sourceCard.isEquipping()) {
                        Card crd = sourceCard.getEquipping().get(0);
                        if (crd.equals(targetCard)) return;

                        sourceCard.unEquipCard(crd);
                    }
                    sourceCard.equipCard(targetCard);
                }
            }

            // An animated artifact equipmemt can't equip a creature
            @Override
            public boolean canPlay() {
                return AllZone.getZone(sourceCard).is(Constant.Zone.Battlefield)
                        && !sourceCard.isCreature()
                        && Phase.canCastSorcery(sourceCard.getController());
            }

            @Override
            public boolean canPlayAI() {
                return getCreature().size() != 0
                        && !sourceCard.isEquipping();
            }

            @Override
            public void chooseTargetAI() {
                Card target = CardFactoryUtil.AI_getBestCreature(getCreature());
                setTargetCard(target);
            }

            CardList getCreature() {
                CardList list = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer());
                list = list.filter(new CardListFilter() {
                    public boolean addCard(Card c) {
                        return c.isCreature()
                                && (CombatUtil.canAttack(c) || (CombatUtil.canAttackNextTurn(c) && AllZone.getPhase().is(Constant.Phase.Main2)))
                                && CardFactoryUtil.canTarget(sourceCard, c)
                                && (c.getNetDefense() + Tough > 0 || sourceCard.getName().equals("Skullclamp"));
                    }
                });

                // Is there at least 1 Loxodon Punisher and/or Goblin Gaveleer to target
                CardList equipMagnetList = list;
                equipMagnetList = equipMagnetList.getEquipMagnets();

                if (!equipMagnetList.isEmpty() && Tough >= 0) {
                    return equipMagnetList;
                }

                // This equipment is keyword only
                if (Power == 0 && Tough == 0) {
                    list = list.filter(new CardListFilter() {
                        public boolean addCard(Card c) {
                            ArrayList<String> extKeywords = new ArrayList<String>(Arrays.asList(extrinsicKeywords));
                            for (String s : extKeywords) {

                                // We want to give a new keyword
                                if (!c.hasKeyword(s))
                                    return true;
                            }
                            //no new keywords:
                            return false;
                        }
                    });
                }

                return list;
            }//getCreature()
        };//equip ability

        String costDesc = abCost.toString();
        //get rid of the ": " at the end
        costDesc = costDesc.substring(0, costDesc.length() - 2);

        StringBuilder sbDesc = new StringBuilder();
        sbDesc.append("Equip");
        if (!abCost.isOnlyManaCost()) sbDesc.append(" -");
        sbDesc.append(" ").append(costDesc);
        equip.setDescription(sbDesc.toString());

        return equip;
    }//eqPump_Equip() ( was vanila_equip() )

    /**
     * <p>eqPump_onEquip.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @param Tough a int.
     * @param extrinsicKeywords an array of {@link java.lang.String} objects.
     * @param abCost a {@link forge.card.spellability.Cost} object.
     * @return a {@link forge.Command} object.
     */
    public static Command eqPump_onEquip(final Card sourceCard, final int Power, final int Tough, final String[] extrinsicKeywords, final Cost abCost) {

        Command onEquip = new Command() {

            private static final long serialVersionUID = 8130682765214560887L;

            public void execute() {
                if (sourceCard.isEquipping()) {
                    Card crd = sourceCard.getEquipping().get(0);

                    for (int i = 0; i < extrinsicKeywords.length; i++) {
                        if (!(extrinsicKeywords[i].equals("none"))
                                && (!crd.hasKeyword(extrinsicKeywords[i])))    // prevent Flying, Flying
                            crd.addExtrinsicKeyword(extrinsicKeywords[i]);
                    }

                    crd.addSemiPermanentAttackBoost(Power);
                    crd.addSemiPermanentDefenseBoost(Tough);
                }
            }//execute()
        };//Command


        return onEquip;
    }//eqPump_onEquip ( was vanila_onequip() )

    /**
     * <p>eqPump_unEquip.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @param Tough a int.
     * @param extrinsicKeywords an array of {@link java.lang.String} objects.
     * @param abCost a {@link forge.card.spellability.Cost} object.
     * @return a {@link forge.Command} object.
     */
    public static Command eqPump_unEquip(final Card sourceCard, final int Power, final int Tough, final String[] extrinsicKeywords, final Cost abCost) {

        Command onUnEquip = new Command() {

            private static final long serialVersionUID = 5783423127748320501L;

            public void execute() {
                if (sourceCard.isEquipping()) {
                    Card crd = sourceCard.getEquipping().get(0);

                    for (int i = 0; i < extrinsicKeywords.length; i++) {
                        crd.removeExtrinsicKeyword(extrinsicKeywords[i]);
                    }

                    crd.addSemiPermanentAttackBoost(-1 * Power);
                    crd.addSemiPermanentDefenseBoost(-1 * Tough);

                }

            }//execute()
        };//Command

        return onUnEquip;
    }//eqPump_unEquip ( was vanila_unequip() )

    /**
     * <p>enPump_Enchant.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @param Tough a int.
     * @param extrinsicKeywords an array of {@link java.lang.String} objects.
     * @param spellDescription an array of {@link java.lang.String} objects.
     * @param stackDescription an array of {@link java.lang.String} objects.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility enPump_Enchant(final Card sourceCard, final int Power, final int Tough, final String[] extrinsicKeywords,
                                              final String[] spellDescription, final String[] stackDescription) {

        Cost cost = new Cost(sourceCard.getManaCost(), sourceCard.getName(), true);
        Target tgt = new Target(sourceCard, "C");
        final SpellAbility enchant = new Spell_Permanent(sourceCard, cost, tgt) {
            private static final long serialVersionUID = -8259560434384053776L;


            public boolean canPlayAI() {
                CardList list = AllZoneUtil.getCreaturesInPlay(AllZone.getComputerPlayer());

                if (list.isEmpty()) return false;

                //else (is there a Rabid Wombat or a Uril, the Miststalker to target?)

                if (Tough >= -1) {    // we want Rabid Wombat or a Uril, the Miststalker to gain at least +1 toughness
                    CardList auraMagnetList = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer());
                    auraMagnetList = auraMagnetList.getEnchantMagnets();

                    if (!auraMagnetList.isEmpty()) {    // AI has a special target creature(s) to enchant
                        auraMagnetList.shuffle();
                        for (int i = 0; i < auraMagnetList.size(); i++) {
                            if (CardFactoryUtil.canTarget(sourceCard, auraMagnetList.get(i))) {
                                setTargetCard(auraMagnetList.get(i));    // Target only Rabid Wombat or Uril, the Miststalker
                                return true;
                            }
                        }
                    }
                }

                /*
                *  else (if aura is keyword only)
                *  Do not duplicate keyword or enchant card with Defender or enchant card already enchanted
                */
                if (Power == 0 && Tough == 0) {
                    list = list.filter(new CardListFilter() {
                        public boolean addCard(Card c) {
                            ArrayList<String> extKeywords = new ArrayList<String>(Arrays.asList(extrinsicKeywords));
                            for (String s : extKeywords) {
                                if (!c.hasKeyword(s)
                                        && !c.hasKeyword("Defender")
                                        && !c.isEnchanted())
                                    return true;
                            }
                            // no new keywords:
                            return false;
                        }
                    });
                }

                /*
                *  else aura is power/toughness boost and may have keyword(s)
                *  Do not reduce power to <= zero or kill by reducing toughness to <= zero
                *  Do not enchant card with Defender or enchant card already enchanted
                */
                CardListUtil.sortAttack(list);
                CardListUtil.sortFlying(list);

                for (int i = 0; i < list.size(); i++) {
                    if (CardFactoryUtil.canTarget(sourceCard, list.get(i))
                            && list.get(i).getNetAttack() + Power > 0
                            && list.get(i).getNetDefense() + Tough > 0
                            && !list.get(i).hasKeyword("Defender")
                            && !list.get(i).isEnchanted()) {
                        setTargetCard(list.get(i));
                        return true;
                    }
                }
                return false;
            }//canPlayAI()

            @Override
            public void resolve() {
                Card aura = AllZone.getGameAction().moveToPlay(sourceCard);

                Card c = getTargetCard();

                // i think this is checked for already in fizzle?
                if (AllZoneUtil.isCardInPlay(c) && CardFactoryUtil.canTarget(sourceCard, c)) {
                    aura.enchantCard(c);
                }
            }//resolve()
        };//enchant ability

        if (!spellDescription[0].replaceAll("[\\+\\-]", "").equals("Enchanted creature gets 0/0.")) {
            enchant.setDescription(spellDescription[0]);
        }
        enchant.setStackDescription(stackDescription[0]);

        return enchant;
    }//enPump_Enchant()

    /**
     * <p>enPump_onEnchant.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @param Tough a int.
     * @param extrinsicKeywords an array of {@link java.lang.String} objects.
     * @param spellDescription an array of {@link java.lang.String} objects.
     * @param stackDescription an array of {@link java.lang.String} objects.
     * @return a {@link forge.Command} object.
     */
    public static Command enPump_onEnchant(final Card sourceCard, final int Power, final int Tough, final String[] extrinsicKeywords,
                                           final String[] spellDescription, final String[] stackDescription) {

        Command onEnchant = new Command() {

            private static final long serialVersionUID = -357890638647936585L;

            public void execute() {
                if (sourceCard.isEnchanting()) {
                    Card crd = sourceCard.getEnchanting().get(0);

                    for (int i = 0; i < extrinsicKeywords.length; i++) {
                        if (!(extrinsicKeywords[i].equals("none"))
                                && (!crd.hasKeyword(extrinsicKeywords[i])))    // prevent Flying, Flying
                            crd.addExtrinsicKeyword(extrinsicKeywords[i]);
                    }

                    crd.addSemiPermanentAttackBoost(Power);
                    crd.addSemiPermanentDefenseBoost(Tough);
                }
            }//execute()
        };//Command

        return onEnchant;
    }//enPump_onEnchant

    /**
     * <p>enPump_unEnchant.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @param Tough a int.
     * @param extrinsicKeywords an array of {@link java.lang.String} objects.
     * @param spellDescription an array of {@link java.lang.String} objects.
     * @param stackDescription an array of {@link java.lang.String} objects.
     * @return a {@link forge.Command} object.
     */
    public static Command enPump_unEnchant(final Card sourceCard, final int Power, final int Tough, final String[] extrinsicKeywords,
                                           final String[] spellDescription, final String[] stackDescription) {

        Command onUnEnchant = new Command() {

            private static final long serialVersionUID = -7121856650546173401L;

            public void execute() {
                if (sourceCard.isEnchanting()) {
                    Card crd = sourceCard.getEnchanting().get(0);

                    for (int i = 0; i < extrinsicKeywords.length; i++) {
                        crd.removeExtrinsicKeyword(extrinsicKeywords[i]);
                    }

                    crd.addSemiPermanentAttackBoost(-1 * Power);
                    crd.addSemiPermanentDefenseBoost(-1 * Tough);
                }
            }//execute()
        };//Command

        return onUnEnchant;
    }//enPump_unEnchant

    /**
     * <p>enPump_LeavesPlay.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @param Tough a int.
     * @param extrinsicKeywords an array of {@link java.lang.String} objects.
     * @param spellDescription an array of {@link java.lang.String} objects.
     * @param stackDescription an array of {@link java.lang.String} objects.
     * @return a {@link forge.Command} object.
     */
    public static Command enPump_LeavesPlay(final Card sourceCard, final int Power, final int Tough, final String[] extrinsicKeywords,
                                            final String[] spellDescription, final String[] stackDescription) {

        Command onLeavesPlay = new Command() {

            private static final long serialVersionUID = -924212760053167271L;

            public void execute() {
                if (sourceCard.isEnchanting()) {
                    Card crd = sourceCard.getEnchanting().get(0);
                    sourceCard.unEnchantCard(crd);
                }
            }//execute()
        };//Command

        return onLeavesPlay;
    }//enPump_LeavesPlay

    /**
     * <p>enPumpCurse_Enchant.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @param Tough a int.
     * @param extrinsicKeywords an array of {@link java.lang.String} objects.
     * @param spellDescription an array of {@link java.lang.String} objects.
     * @param stackDescription an array of {@link java.lang.String} objects.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility enPumpCurse_Enchant(final Card sourceCard, final int Power, final int Tough, final String[] extrinsicKeywords,
                                                   final String[] spellDescription, final String[] stackDescription) {

        Cost cost = new Cost(sourceCard.getManaCost(), sourceCard.getName(), true);
        Target tgt = new Target(sourceCard, "C");
        final SpellAbility enchant = new Spell_Permanent(sourceCard, cost, tgt) {
            private static final long serialVersionUID = -4021229901439299033L;


            public boolean canPlayAI() {
                CardList list = AllZoneUtil.getCreaturesInPlay(AllZone.getHumanPlayer());

                if (list.isEmpty()) return false;

                //else we may need to filter the list and remove inappropriate targets

                final ArrayList<String> extKeywords = new ArrayList<String>(Arrays.asList(extrinsicKeywords));

                list = list.filter(new CardListFilter() {
                    public boolean addCard(Card c) {
                        for (String s : extKeywords) {

                            /* If extrinsicKeywords contains "CARDNAME attacks each turn if able." then remove creatures 
                            *     with Defender and creatures with the keyword "CARDNAME attacks each turn if able."
                            *     and creatures with a keyword that starts with "CARDNAME can't attack"
                            */
                            if (s.contains("CARDNAME attacks each turn if able.")) {
                                if (c.hasKeyword("Defender")
                                        || c.hasKeyword("CARDNAME attacks each turn if able.")
                                        || c.hasStartOfKeyword("CARDNAME can't attack"))
                                    return false;
                            }

                            /* If extrinsicKeywords contains "CARDNAME can't attack." or "CARDNAME can't attack or block."
                            *     then remove creatures with Defender and remove creatures that have one or more of these
                            *     keywords to start with
                            */
                            if (s.contains("CARDNAME can't attack.") || s.contains("CARDNAME can't attack or block.")) {
                                if (c.hasKeyword("Defender")
                                        || c.hasKeyword("CARDNAME can't attack.")
                                        || c.hasKeyword("CARDNAME can't attack or block."))
                                    return false;
                            }

                            /* If extrinsicKeywords contains "CARDNAME doesn't untap during your untap step."
                             *     then remove creatures with Vigilance that are untapped and remove creatures that have the keyword 
                             *     "CARDNAME doesn't untap during your untap step."  and remove creatures that are untapped
                            */
                            if (s.contains("CARDNAME doesn't untap during your untap step.")) {
                                if ((c.hasKeyword("Vigilance") && c.isUntapped())
                                        || c.hasKeyword("CARDNAME doesn't untap during your untap step.")
                                        || c.isUntapped())
                                    return false;
                            }
                        }

                        // Card c is a potential target if we reach this point.
                        return true;
                    }
                });

                /*
                // If extrinsicKeywords contains "CARDNAME can't attack." or "CARDNAME can't attack or block."
                //     then remove creatures with Defender from the list and remove creatures that have one
                //     or more of these keywords to start with
                //
                final ArrayList<String> extKeywords = new ArrayList<String>(Arrays.asList(extrinsicKeywords));
                
                if (extKeywords.contains("CARDNAME can't attack.") || extKeywords.contains("CARDNAME can't attack or block.")) {
                    list = list.filter(new CardListFilter() {
                        public boolean addCard(Card c) {
                            return c.isCreature()
                                    && !c.hasKeyword("Defender")
                                    && !c.hasKeyword("CARDNAME can't attack.")
                                    && !c.hasKeyword("CARDNAME can't attack or block.");
                        }
                    });
                }

                // If extrinsicKeywords contains "CARDNAME doesn't untap during your untap step."
                //     then remove creatures with Vigilance from the list
                //
                if (extKeywords.contains("HIDDEN CARDNAME doesn't untap during your untap step.")) {
                    list = list.filter(new CardListFilter() {
                        public boolean addCard(Card c) {
                            if (c.hasKeyword("CARDNAME doesn't untap during your untap step."))
                                return false;

                            if (c.hasKeyword("Vigilance") && c.isUntapped())
                                return false;

                            return c.isCreature() && (c.isTapped() || Power < 1);
                        }
                    });
                }
                                
                //else (if aura is keyword only or is Cagemail)
                
                if (Power >= 0 && Tough >= 0) {    // This aura is keyword only or is Cagemail
                    list = list.filter(new CardListFilter() {
                        public boolean addCard(Card c){
                            ArrayList<String> extKeywords = new ArrayList<String>(Arrays.asList(extrinsicKeywords));
                            for (String s:extKeywords) {
                                if (!c.hasKeyword(s))
                                    return true;
                            }
                                //no new keywords:
                                return false;
                        }
                    });
                    
                }
                
                //else aura is power/toughness decrease and may have keyword(s)
                */

                CardListUtil.sortAttack(list);
                CardListUtil.sortFlying(list);

                for (int i = 0; i < list.size(); i++) {
                    if (CardFactoryUtil.canTarget(sourceCard, list.get(i))) {
                        setTargetCard(list.get(i));
                        return true;
                    }
                }
                return false;
            }//canPlayAI()

            public void resolve() {
                Card aura = AllZone.getGameAction().moveToPlay(sourceCard);

                Card c = getTargetCard();

                if (AllZoneUtil.isCardInPlay(c) && CardFactoryUtil.canTarget(sourceCard, c)) {
                    aura.enchantCard(c);
                }
            }//resolve()
        };//enchant ability

        if (!spellDescription[0].replaceAll("[\\+\\-]", "").equals("Enchanted creature gets 0/0.")) {
            enchant.setDescription(spellDescription[0]);
        }
        enchant.setStackDescription(stackDescription[0]);

        return enchant;
    }//enPumpCurse_Enchant()

    /**
     * <p>getEldraziSpawnAbility.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a {@link forge.card.spellability.Ability_Mana} object.
     */
    public static Ability_Mana getEldraziSpawnAbility(final Card c) {
        Cost cost = new Cost("Sac<1/CARDNAME>", c.getName(), true);
        Ability_Mana mana = new Ability_Mana(c, cost, "1") {
            private static final long serialVersionUID = -2478676548112738019L;
        };
        mana.setDescription("Sacrifice CARDNAME: Add 1 to your mana pool.");
        return mana;
    }


    /**
     * <p>entersBattleFieldWithCounters.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param type a {@link forge.Counters} object.
     * @param n a int.
     * @return a {@link forge.Command} object.
     */
    public static Command entersBattleFieldWithCounters(final Card c, final Counters type, final int n) {
        Command addCounters = new Command() {
            private static final long serialVersionUID = 4825430555490333062L;

            public void execute() {
                c.addCounter(type, n);
            }
        };
        return addCounters;
    }

    /**
     * <p>fading.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @return a {@link forge.Command} object.
     */
    public static Command fading(final Card sourceCard, final int Power) {
        Command fade = new Command() {
            private static final long serialVersionUID = 431920157968451817L;
            public boolean firstTime = true;

            public void execute() {

                //testAndSet - only needed when enters the battlefield.
                if (firstTime) {
                    sourceCard.addCounter(Counters.FADE, Power);
                }
                firstTime = false;
            }
        };
        return fade;
    } // fading

    /**
     * <p>vanishing.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Power a int.
     * @return a {@link forge.Command} object.
     */
    public static Command vanishing(final Card sourceCard, final int Power) {
        Command age = new Command() {
            private static final long serialVersionUID = 431920157968451817L;
            public boolean firstTime = true;

            public void execute() {

                //testAndSet - only needed when enters the battlefield
                if (firstTime) {
                    sourceCard.addCounter(Counters.TIME, Power);
                }
                firstTime = false;
            }
        };
        return age;
    } // vanishing

    /**
     * <p>ability_Soulshift.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Manacost a {@link java.lang.String} object.
     * @return a {@link forge.Command} object.
     */
    public static Command ability_Soulshift(final Card sourceCard, final String Manacost) {
        final Command Soulshift = new Command() {
            private static final long serialVersionUID = -4960704261761785512L;

            public void execute() {
                AllZone.getStack().add(soulshiftTrigger(sourceCard, Manacost));
            }

        };

        return Soulshift;
    }//ability_Soulshift()

    /**
     * <p>soulshiftTrigger.</p>
     *
     * @param sourceCard a {@link forge.Card} object.
     * @param Manacost a {@link java.lang.String} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility soulshiftTrigger(final Card sourceCard, final String Manacost) {
        final SpellAbility desc = new Ability(sourceCard, "0") {
            private static final long serialVersionUID = -4960704261761785512L;

            @Override
            public void resolve() {
                CardList cards = AllZoneUtil.getPlayerGraveyard(sourceCard.getController());
                CardList sameCost = new CardList();
                int Cost = CardUtil.getConvertedManaCost(Manacost);
                for (int i = 0; i < cards.size(); i++) {
                    if ((CardUtil.getConvertedManaCost(cards.get(i).getManaCost()) <= Cost)
                            && cards.get(i).isType("Spirit")) {
                        sameCost.add(cards.get(i));
                    }
                }

                if (sameCost.size() == 0) return;

                if (sourceCard.getController().isHuman()) {
                    StringBuilder question = new StringBuilder();
                    question.append("Return target Spirit card with converted mana cost ");
                    question.append(Manacost).append(" or less from your graveyard to your hand?");

                    if (GameActionUtil.showYesNoDialog(sourceCard, question.toString())) {
                        Object o = GuiUtils.getChoiceOptional("Select a card", sameCost.toArray());
                        if (o != null) {

                            Card c1 = (Card) o;
                            AllZone.getGameAction().moveToHand(c1);
                        }
                    }
                } else {
                    //Wiser choice should be here
                    Card choice = null;
                    sameCost.shuffle();
                    choice = sameCost.getCard(0);

                    if (!(choice == null)) {
                        AllZone.getGameAction().moveToHand(choice);
                    }
                }
            }// resolve()
        };// SpellAbility desc

        // The spell description below fails to appear in the card detail panel
        StringBuilder sbDesc = new StringBuilder();
        sbDesc.append("Soulshift ").append(Manacost);
        sbDesc.append(" - When this permanent is put into a graveyard from play, you may return target Spirit card with converted mana cost ");
        sbDesc.append(Manacost).append(" or less from your graveyard to your hand.");
        desc.setDescription(sbDesc.toString());

        StringBuilder sbStack = new StringBuilder();
        sbStack.append(sourceCard.getName()).append(" - Soulshift ").append(Manacost);
        desc.setStackDescription(sbStack.toString());

        return desc;
    }//soul_desc()

    //CardList choices are the only cards the user can successful select
    /**
     * <p>input_targetSpecific.</p>
     *
     * @param spell a {@link forge.card.spellability.SpellAbility} object.
     * @param choices a {@link forge.CardList} object.
     * @param message a {@link java.lang.String} object.
     * @param targeted a boolean.
     * @param free a boolean.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input input_targetSpecific(final SpellAbility spell, final CardList choices, final String message, final boolean targeted, final boolean free) {
        return input_targetSpecific(spell, choices, message, Command.Blank, targeted, free);
    }

    //CardList choices are the only cards the user can successful select
    /**
     * <p>input_targetSpecific.</p>
     *
     * @param spell a {@link forge.card.spellability.SpellAbility} object.
     * @param choices a {@link forge.CardList} object.
     * @param message a {@link java.lang.String} object.
     * @param paid a {@link forge.Command} object.
     * @param targeted a boolean.
     * @param free a boolean.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input input_targetSpecific(final SpellAbility spell, final CardList choices, final String message, final Command paid, final boolean targeted, final boolean free) {
        Input target = new Input() {
            private static final long serialVersionUID = -1779224307654698954L;

            @Override
            public void showMessage() {
                AllZone.getDisplay().showMessage(message);
                ButtonUtil.enableOnlyCancel();
            }

            @Override
            public void selectButtonCancel() {
                stop();
            }

            @Override
            public void selectCard(Card card, PlayerZone zone) {
                if (targeted && !canTarget(spell, card)) {
                    AllZone.getDisplay().showMessage("Cannot target this card (Shroud? Protection?).");
                } else if (choices.contains(card)) {
                    spell.setTargetCard(card);
                    if (spell.getManaCost().equals("0") || free) {
                        this.setFree(false);
                        AllZone.getStack().add(spell);
                        stop();
                    } else stopSetNext(new Input_PayManaCost(spell));

                    paid.execute();
                }
            }//selectCard()
        };
        return target;
    }//input_targetSpecific()

    //CardList choices are the only cards the user can successful select
    /**
     * <p>input_targetChampionSac.</p>
     *
     * @param crd a {@link forge.Card} object.
     * @param spell a {@link forge.card.spellability.SpellAbility} object.
     * @param choices a {@link forge.CardList} object.
     * @param message a {@link java.lang.String} object.
     * @param targeted a boolean.
     * @param free a boolean.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input input_targetChampionSac(final Card crd, final SpellAbility spell, final CardList choices, final String message, final boolean targeted, final boolean free) {
        Input target = new Input() {
            private static final long serialVersionUID = -3320425330743678663L;

            @Override
            public void showMessage() {
                AllZone.getDisplay().showMessage(message);
                ButtonUtil.enableOnlyCancel();
            }

            @Override
            public void selectButtonCancel() {
                AllZone.getGameAction().sacrifice(crd);
                stop();
            }

            @Override
            public void selectCard(Card card, PlayerZone zone) {
                if (choices.contains(card)) {
                    if (card == spell.getSourceCard()) {
                        AllZone.getGameAction().sacrifice(spell.getSourceCard());
                        stop();
                    } else {
                        spell.getSourceCard().setChampionedCard(card);
                        AllZone.getGameAction().exile(card);

                        stop();

                        //Run triggers
                        HashMap<String, Object> runParams = new HashMap<String, Object>();
                        runParams.put("Card", spell.getSourceCard());
                        runParams.put("Championed", card);
                        AllZone.getTriggerHandler().runTrigger("Championed", runParams);
                    }
                }
            }//selectCard()
        };
        return target;
    }//input_targetSpecific()

    /**
     * <p>input_equipCreature.</p>
     *
     * @param equip a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input input_equipCreature(final SpellAbility equip) {
        Input runtime = new Input() {
            private static final long serialVersionUID = 2029801495067540196L;

            @Override
            public void showMessage() {
                //get all creatures you control
                CardList list = AllZoneUtil.getCreaturesInPlay(AllZone.getHumanPlayer());

                stopSetNext(input_targetSpecific(equip, list,
                        "Select target creature to equip", true, false));
            }
        };//Input
        return runtime;
    }

    /**
     * custom input method only for use in Recall
     *
     * @param numCards a int.
     * @param recall a {@link forge.Card} object.
     * @param sa a {@link forge.card.spellability.SpellAbility} object.
     * @return input
     */
    public static Input input_discardRecall(final int numCards, final Card recall, final SpellAbility sa) {
        Input target = new Input() {
            private static final long serialVersionUID = 1942999595292561944L;
            int n = 0;

            @Override
            public void showMessage() {
                if (AllZone.getHumanHand().size() == 0) stop();

                AllZone.getDisplay().showMessage("Select a card to discard");
                ButtonUtil.disableAll();
            }

            @Override
            public void selectCard(Card card, PlayerZone zone) {
                if (zone.is(Constant.Zone.Hand)) {
                    card.getController().discard(card, sa);
                    n++;

                    //in case no more cards in hand
                    if (n == numCards || AllZone.getHumanHand().size() == 0) done();
                    else
                        showMessage();
                }
            }

            void done() {
                AllZone.getDisplay().showMessage("Returning cards to hand.");
                AllZone.getGameAction().exile(recall);
                CardList grave = AllZoneUtil.getPlayerGraveyard(AllZone.getHumanPlayer());
                for (int i = 1; i <= n; i++) {
                    String title = "Return card from grave to hand";
                    Object o = GuiUtils.getChoice(title, grave.toArray());
                    if (o == null) break;
                    Card toHand = (Card) o;
                    grave.remove(toHand);
                    AllZone.getGameAction().moveToHand(toHand);
                }
                stop();
            }
        };
        return target;
    }//input_discardRecall()

    /**
     * <p>MasteroftheWildHunt_input_targetCreature.</p>
     *
     * @param spell a {@link forge.card.spellability.SpellAbility} object.
     * @param choices a {@link forge.CardList} object.
     * @param paid a {@link forge.Command} object.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input MasteroftheWildHunt_input_targetCreature(final SpellAbility spell, final CardList choices, final Command paid) {
        Input target = new Input() {
            private static final long serialVersionUID = -1779224307654698954L;

            @Override
            public void showMessage() {
                AllZone.getDisplay().showMessage("Select target wolf to damage for " + spell.getSourceCard());
                ButtonUtil.enableOnlyCancel();
            }

            @Override
            public void selectButtonCancel() {
                stop();
            }

            @Override
            public void selectCard(Card card, PlayerZone zone) {
                if (choices.size() == 0) stop();
                if (choices.contains(card)) {
                    spell.setTargetCard(card);
                    paid.execute();
                    stop();
                }
            }//selectCard()
        };
        return target;
    }//input_MasteroftheWildHunt_input_targetCreature()

    /**
     * <p>modularInput.</p>
     *
     * @param ability a {@link forge.card.spellability.SpellAbility} object.
     * @param card a {@link forge.Card} object.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input modularInput(final SpellAbility ability, final Card card) {
        Input modularInput = new Input() {

            private static final long serialVersionUID = 2322926875771867901L;

            @Override
            public void showMessage() {
                AllZone.getDisplay().showMessage("Select target artifact creature");
                ButtonUtil.enableOnlyCancel();
            }

            @Override
            public void selectButtonCancel() {
                stop();
            }

            @Override
            public void selectCard(Card card2, PlayerZone zone) {
                if (card2.isCreature() && card2.isArtifact() && zone.is(Constant.Zone.Battlefield)
                        && CardFactoryUtil.canTarget(ability, card)) {
                    ability.setTargetCard(card2);
                    ability.setStackDescription("Put " + card.getCounters(Counters.P1P1)
                            + " +1/+1 counter/s from " + card + " on " + card2);
                    AllZone.getStack().add(ability);
                    stop();
                }
            }
        };
        return modularInput;
    }

    /**
     * <p>AI_getHumanCreature.</p>
     *
     * @param spell a {@link forge.Card} object.
     * @param targeted a boolean.
     * @return a {@link forge.CardList} object.
     */
    public static CardList AI_getHumanCreature(final Card spell, boolean targeted) {
        CardList creature = AllZoneUtil.getCreaturesInPlay(AllZone.getHumanPlayer());
        if (targeted) {
            creature = creature.filter(AllZoneUtil.getCanTargetFilter(spell));
        }
        return creature;
    }

    /**
     * <p>AI_getHumanCreature.</p>
     *
     * @param keyword a {@link java.lang.String} object.
     * @param spell a {@link forge.Card} object.
     * @param targeted a boolean.
     * @return a {@link forge.CardList} object.
     */
    public static CardList AI_getHumanCreature(final String keyword, final Card spell, final boolean targeted) {
        CardList creature = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
        creature = creature.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                if (targeted)
                    return c.isCreature()
                            && c.hasKeyword(keyword)
                            && canTarget(spell, c);
                else
                    return c.isCreature()
                            && c.hasKeyword(keyword);
            }
        });
        return creature;
    }//AI_getHumanCreature()

    /**
     * <p>AI_getHumanCreature.</p>
     *
     * @param toughness a int.
     * @param spell a {@link forge.Card} object.
     * @param targeted a boolean.
     * @return a {@link forge.CardList} object.
     */
    public static CardList AI_getHumanCreature(final int toughness, final Card spell, final boolean targeted) {
        CardList creature = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
        creature = creature.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                if (targeted) return c.isCreature() && (c.getNetDefense() <= toughness) && canTarget(spell, c);
                else return c.isCreature() && (c.getNetDefense() <= toughness);
            }
        });
        return creature;
    }//AI_getHumanCreature()

    /**
     * <p>AI_targetHuman.</p>
     *
     * @return a {@link forge.CommandArgs} object.
     */
    public static CommandArgs AI_targetHuman() {
        return new CommandArgs() {
            private static final long serialVersionUID = 8406907523134006697L;

            public void execute(Object o) {
                SpellAbility sa = (SpellAbility) o;
                sa.setTargetPlayer(AllZone.getHumanPlayer());
            }
        };
    }//targetHuman()

    /**
     * <p>getNumberOfPermanentsByColor.</p>
     *
     * @param color a {@link java.lang.String} object.
     * @return a int.
     */
    public static int getNumberOfPermanentsByColor(String color) {
        CardList cards = AllZoneUtil.getCardsInPlay();

        CardList coloredPerms = new CardList();

        for (int i = 0; i < cards.size(); i++) {
            if (CardUtil.getColors(cards.get(i)).contains(color)) coloredPerms.add(cards.get(i));
        }
        return coloredPerms.size();
    }

    /**
     * <p>multipleControlled.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean multipleControlled(Card c) {
        CardList list = AllZoneUtil.getPlayerCardsInPlay(c.getController());
        list.remove(c);

        return list.containsName(c.getName());
    }

    /**
     * <p>oppHasKismet.</p>
     *
     * @param player a {@link forge.Player} object.
     * @return a boolean.
     */
    public static boolean oppHasKismet(Player player) {
        Player opp = player.getOpponent();
        CardList list = AllZoneUtil.getPlayerCardsInPlay(opp);
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.getName().equals("Kismet") || c.getName().equals("Frozen AEther")
                        || c.getName().equals("Loxodon Gatekeeper");
            }
        });
        return list.size() > 0;
    }

    /**
     * <p>getNumberOfManaSymbolsControlledByColor.</p>
     *
     * @param colorAbb a {@link java.lang.String} object.
     * @param player a {@link forge.Player} object.
     * @return a int.
     */
    public static int getNumberOfManaSymbolsControlledByColor(String colorAbb, Player player) {
        CardList cards = AllZoneUtil.getPlayerCardsInPlay(player);
        return getNumberOfManaSymbolsByColor(colorAbb, cards);
    }

    /**
     * <p>getNumberOfManaSymbolsByColor.</p>
     *
     * @param colorAbb a {@link java.lang.String} object.
     * @param cards a {@link forge.CardList} object.
     * @return a int.
     */
    public static int getNumberOfManaSymbolsByColor(String colorAbb, CardList cards) {
        int count = 0;
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (!c.isToken()) {
                String manaCost = c.getManaCost();
                manaCost = manaCost.trim();
                count += countOccurrences(manaCost, colorAbb);
            }
        }
        return count;
    }

    /**
     * <p>multiplyManaCost.</p>
     *
     * @param manacost a {@link java.lang.String} object.
     * @param multiplier a int.
     * @return a {@link java.lang.String} object.
     */
    public static String multiplyManaCost(String manacost, int multiplier) {
        if (multiplier == 0) return "";
        if (multiplier == 1) return manacost;

        String tokenized[] = manacost.split("\\s");
        StringBuilder sb = new StringBuilder();

        if (Character.isDigit(tokenized[0].charAt(0))) //manacost starts with "colorless" number cost
        {
            int cost = Integer.parseInt(tokenized[0]);
            cost = multiplier * cost;
            tokenized[0] = "" + cost;
            sb.append(tokenized[0]);
        } else {
            for (int i = 0; i < multiplier; i++) {
                //tokenized[0] = tokenized[0] + " " + tokenized[0];
                sb.append((" "));
                sb.append(tokenized[0]);
            }
        }

        for (int i = 1; i < tokenized.length; i++) {
            for (int j = 0; j < multiplier; j++) {
                //tokenized[i] = tokenized[i] + " " + tokenized[i];
                sb.append((" "));
                sb.append(tokenized[i]);

            }
        }

        String result = sb.toString();
        System.out.println("result: " + result);
        result = result.trim();
        return result;
    }

    /**
     * <p>isTargetStillValid.</p>
     *
     * @param ability a {@link forge.card.spellability.SpellAbility} object.
     * @param target a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean isTargetStillValid(SpellAbility ability, Card target) {

        if (AllZone.getZone(target) == null) return false; // for tokens that disappeared

        Card source = ability.getSourceCard();
        Target tgt = ability.getTarget();
        if (tgt != null) {
            // Reconfirm the Validity of a TgtValid, or if the Creature is still a Creature
            if (tgt.doesTarget() && !target.isValidCard(tgt.getValidTgts(), ability.getActivatingPlayer(), ability.getSourceCard()))
                return false;

            // Check if the target is in the zone it needs to be in to be targeted
            if (!AllZone.getZone(target).is(tgt.getZone()))
                return false;
        } else {
            // If an Aura's target is removed before it resolves, the Aura fizzles
            if (source.isAura() && !AllZone.getZone(target).is(Constant.Zone.Battlefield))
                return false;
        }

        // Make sure it's still targetable as well
        return canTarget(source, target);
    }

    /**
     * <p>canTarget.</p>
     *
     * @param ability a {@link forge.card.spellability.SpellAbility} object.
     * @param target a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean canTarget(SpellAbility ability, Card target) {
        return canTarget(ability.getSourceCard(), target);
    }

    /**
     * <p>isColored.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean isColored(Card c) {
        return c.isWhite() || c.isBlue() || c.isBlack() || c.isRed() || c.isGreen();
    }

    /**
     * <p>canTarget.</p>
     *
     * @param spell a {@link forge.Card} object.
     * @param target a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean canTarget(Card spell, Card target) {
        if (target == null) return true;
        //System.out.println("Target:" + target);

        if (target.isImmutable())
            return false;

        PlayerZone zone = AllZone.getZone(target);
        // if zone is null, it means its on the stack 
        if (zone == null || !zone.is(Constant.Zone.Battlefield)) {
            // targets not in play, can normally be targeted
            return true;
        }

        if (AllZoneUtil.isCardInPlay("Spellbane Centaur", target.getController()) && target.isCreature()
                && spell.isBlue()) return false;

        if (target.getName().equals("Gaea's Revenge") && !spell.isGreen()) return false;

        if (hasProtectionFrom(spell, target)) return false;

        if (target.getKeyword() != null) {
            ArrayList<String> list = target.getKeyword();

            String kw = "";
            for (int i = 0; i < list.size(); i++) {
                kw = list.get(i);
                if (kw.equals("Shroud")) return false;

                if (kw.equals("Hexproof")) {
                    if (!spell.getController().equals(target.getController())) return false;
                }

                if (kw.equals("CARDNAME can't be the target of Aura spells.")) {
                    if (spell.isAura() && spell.isSpell()) return false;
                }

                if (kw.equals("CARDNAME can't be the target of red spells or abilities from red sources.")) {
                    if (spell.isRed()) return false;
                }

                if (kw.equals("CARDNAME can't be the target of black spells.")) {
                    if (spell.isBlack() && spell.isSpell()) return false;
                }

                if (kw.equals("CARDNAME can't be the target of blue spells.")) {
                    if (spell.isBlue() && spell.isSpell()) return false;
                }

                if (kw.equals("CARDNAME can't be the target of spells.")) {
                    if (spell.isSpell()) return false;
                }
            }
        }
        return true;
    }

    //does "target" have protection from "card"?
    /**
     * <p>hasProtectionFrom.</p>
     *
     * @param card a {@link forge.Card} object.
     * @param target a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean hasProtectionFrom(Card card, Card target) {
        if (target == null) return false;

        if (target.isImmutable())
            return true;

        if (target.getKeyword() != null) {
            ArrayList<String> list = target.getKeyword();

            String kw = "";
            for (int i = 0; i < list.size(); i++) {
                kw = list.get(i);

                if (kw.equals("Protection from white")
                        && card.isWhite()
                        && !card.getName().contains("White Ward")) return true;
                if (kw.equals("Protection from blue")
                        && card.isBlue()
                        && !card.getName().contains("Blue Ward")) return true;
                if (kw.equals("Protection from black")
                        && card.isBlack()
                        && !card.getName().contains("Black Ward")) return true;
                if (kw.equals("Protection from red")
                        && card.isRed()
                        && !card.getName().contains("Red Ward")) return true;
                if (kw.equals("Protection from green")
                        && card.isGreen()
                        && !card.getName().contains("Green Ward")) return true;

                if (kw.equals("Protection from creatures")
                        && card.isCreature()) return true;

                if (kw.equals("Protection from artifacts")
                        && card.isArtifact()) return true;

                if (kw.equals("Protection from enchantments")
                        && card.isEnchantment()
                        && !card.getName().contains("Tattoo Ward")) return true;

                if (kw.equals("Protection from everything")) return true;

                if (kw.equals("Protection from colored spells")
                        && (card.isInstant() || card.isSorcery() || card.isAura())
                        && isColored(card)) return true;

                if (kw.equals("Protection from Dragons")
                        && card.isType("Dragon")) return true;
                if (kw.equals("Protection from Demons")
                        && card.isType("Demon")) return true;
                if (kw.equals("Protection from Goblins")
                        && card.isType("Goblin")) return true;
                if (kw.equals("Protection from Clerics")
                        && card.isType("Cleric")) return true;
                if (kw.equals("Protection from Gorgons")
                        && card.isType("Gorgon")) return true;

                if (kw.startsWith("Protection:")) { //uses isValidCard
                    String characteristic = kw.split(":")[1];
                    String characteristics[] = characteristic.split(",");
                    if (card.isValidCard(characteristics, card.getController(), card)) return true;
                }

            }
        }
        return false;
    }

    /**
     * <p>isCounterable.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean isCounterable(Card c) {
        if (!c.hasKeyword("CARDNAME can't be countered.")) return true;
        else return false;
    }


    //returns the number of equipments named "e" card c is equipped by
    /**
     * <p>hasNumberEquipments.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param e a {@link java.lang.String} object.
     * @return a int.
     */
    public static int hasNumberEquipments(Card c, String e) {
        if (!c.isEquipped()) return 0;

        final String equipmentName = e;
        CardList list = new CardList(c.getEquippedBy().toArray());
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.getName().equals(equipmentName);
            }

        });

        return list.size();

    }

    /**
     * <p>getGraveyardActivationCards.</p>
     *
     * @param player a {@link forge.Player} object.
     * @return a {@link forge.CardList} object.
     */
    public static CardList getGraveyardActivationCards(final Player player) {
        CardList cl = AllZoneUtil.getPlayerGraveyard(player);
        cl = cl.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return activateFromGrave(c, player);
            }
        });
        return cl;
    }

    /**
     * <p>activateFromGrave.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param player a {@link forge.Player} object.
     * @return a boolean.
     */
    public static boolean activateFromGrave(Card c, Player player) {
        if (c.hasFlashback() || c.hasUnearth())
            return true;

        final CardList crucible = AllZoneUtil.getPlayerCardsInPlay(player, "Crucible of Worlds");
        if (c.isLand() && crucible.size() > 0)
            return true;

        for (SpellAbility sa : c.getSpellAbility()) {
            if (sa.getRestrictions().getZone().equals(Constant.Zone.Graveyard))
                return true;
        }

        return false;
    }

    /**
     * <p>countOccurrences.</p>
     *
     * @param arg1 a {@link java.lang.String} object.
     * @param arg2 a {@link java.lang.String} object.
     * @return a int.
     */
    public static int countOccurrences(String arg1, String arg2) {

        int count = 0;
        int index = 0;
        while ((index = arg1.indexOf(arg2, index)) != -1) {
            ++index;
            ++count;
        }
        return count;
    }

    /**
     * <p>parseMath.</p>
     *
     * @param l an array of {@link java.lang.String} objects.
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] parseMath(String[] l) {
        String[] m = {"none"};
        if (l.length > 1) m[0] = l[1];

        return m;
    }


    //parser for player targeted X variables
    /**
     * <p>playerXCount.</p>
     *
     * @param players a {@link java.util.ArrayList} object.
     * @param s a {@link java.lang.String} object.
     * @param source a {@link forge.Card} object.
     * @return a int.
     */
    public static int playerXCount(ArrayList<Player> players, String s, Card source) {
        if (players.size() == 0) return 0;

        final String[] l = s.split("/");
        final String[] m = parseMath(l);

        int n = 0;

        // count valid cards on the battlefield
        if (l[0].contains("Valid")) {
            String restrictions = l[0].replace("Valid ", "");
            final String rest[] = restrictions.split(",");
            CardList cardsonbattlefield = AllZoneUtil.getCardsInPlay();
            cardsonbattlefield = cardsonbattlefield.getValidCards(rest, players.get(0), source);

            n = cardsonbattlefield.size();

            return doXMath(n, m, source);
        }

        final String[] sq;
        sq = l[0].split("\\.");

        if (sq[0].contains("CardsInHand")) {
            if (players.size() > 0) {
                return doXMath(AllZoneUtil.getPlayerHand(players.get(0)).size(), m, source);
            }
        }

        if (sq[0].contains("CardsInLibrary")) {
            if (players.size() > 0) {
                return doXMath(AllZoneUtil.getPlayerCardsInLibrary(players.get(0)).size(), m, source);
            }
        }
        
        if (sq[0].contains("CardsInGraveyard")) {
        	if (players.size() > 0) {
        		return doXMath(AllZoneUtil.getPlayerGraveyard(players.get(0)).size(), m, source);
        	}
        }
        if (sq[0].contains("LandsInGraveyard"))
            if (players.size() > 0) {
                return doXMath(AllZoneUtil.getPlayerTypeInGraveyard(players.get(0), "Land").size(), m, source);
            }

        if (sq[0].contains("CreaturesInPlay")) {
            if (players.size() > 0) {
                return doXMath(AllZoneUtil.getCreaturesInPlay(players.get(0)).size(), m, source);
            }
        }

        if (sq[0].contains("CardsInPlay")) {
            if (players.size() > 0) {
                return doXMath(AllZoneUtil.getPlayerCardsInPlay(players.get(0)).size(), m, source);
            }
        }

        if (sq[0].contains("LifeTotal")) {
            if (players.size() > 0) {
                return doXMath(players.get(0).getLife(), m, source);
            }
        }

        if (sq[0].contains("TopOfLibraryCMC")) {
            if (players.size() > 0) {
                return doXMath(AllZoneUtil.getPlayerCardsInLibrary(players.get(0), 1).getTotalConvertedManaCost(), m, source);
            }
        }

        return doXMath(n, m, source);
    }

    //parser for non-mana X variables
    /**
     * <p>xCount.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param s a {@link java.lang.String} object.
     * @return a int.
     */
    public static int xCount(Card c, String s) {
        int n = 0;

        Player cardController = c.getController();
        Player oppController = cardController.getOpponent();

        final String[] l = s.split("/");
        final String[] m = parseMath(l);

        //accept straight numbers
        if (l[0].contains("Number$")) {
            String number = l[0].replace("Number$", "");
            return Integer.parseInt(number);
        }

        //Manapool
        if (l[0].contains("ManaPool")) {
            String color = l[0].split(":")[1];
            return AllZone.getManaPool().getAmountOfColor(color);
        }

        // count valid cards on the battlefield
        if (l[0].contains("Valid")) {
            String restrictions = l[0].replace("Valid ", "");
            restrictions = restrictions.replace("Count$", "");
            final String rest[] = restrictions.split(",");
            CardList cardsonbattlefield = AllZoneUtil.getCardsInPlay();
            cardsonbattlefield = cardsonbattlefield.getValidCards(rest, cardController, c);

            n = cardsonbattlefield.size();

            return doXMath(n, m, c);
        }

        final String[] sq;
        sq = l[0].split("\\.");

        if (sq[0].contains("xPaid")) {
            return c.getXManaCostPaid();
        }

        if (sq[0].contains("xLifePaid")) {
            if (c.getController().isHuman()) {
                return c.getXLifePaid();
            } else {
                //copied for xPaid
                //not implemented for Compy
                //int dam = ComputerUtil.getAvailableMana().size()- CardUtil.getConvertedManaCost(c);
                //if (dam < 0) dam = 0;
                //return dam;
                return 0;
            }
        }

        CardList someCards = new CardList();

        //Complex counting methods

        //TriggeringObjects
        if (sq[0].startsWith("Triggered")) {
            return doXMath((Integer) c.getTriggeringObject(sq[0].substring(9)), m, c);
        }

        // Count$Domain
        if (sq[0].contains("Domain")) {
            someCards.addAll(AllZoneUtil.getPlayerCardsInPlay(cardController));
            String basic[] = {"Forest", "Plains", "Mountain", "Island", "Swamp"};

            for (int i = 0; i < basic.length; i++)
                if (!someCards.getType(basic[i]).isEmpty()) n++;

            return doXMath(n, m, c);
        }

        // Count$YourLifeTotal
        if (sq[0].contains("YourLifeTotal")) {
            if (cardController.isComputer()) return doXMath(AllZone.getComputerPlayer().getLife(), m, c);
            else if (cardController.isHuman()) return doXMath(AllZone.getHumanPlayer().getLife(), m, c);

            return 0;
        }

        // Count$OppLifeTotal
        if (sq[0].contains("OppLifeTotal")) {
            if (oppController.isComputer()) return doXMath(AllZone.getComputerPlayer().getLife(), m, c);
            else if (oppController.isHuman()) return doXMath(AllZone.getHumanPlayer().getLife(), m, c);

            return 0;
        }

        // Count$YourPoisonCounters
        if (sq[0].contains("YourPoisonCounters")) {
            if (cardController.isComputer()) return doXMath(AllZone.getComputerPlayer().getPoisonCounters(), m, c);
            else if (cardController.isHuman()) return doXMath(AllZone.getHumanPlayer().getPoisonCounters(), m, c);

            return 0;
        }

        // Count$OppPoisonCounters
        if (sq[0].contains("OppPoisonCounters")) {
            if (oppController.isComputer()) return doXMath(AllZone.getComputerPlayer().getPoisonCounters(), m, c);
            else if (oppController.isHuman()) return doXMath(AllZone.getHumanPlayer().getPoisonCounters(), m, c);

            return 0;
        }

        // Count$HighestLifeTotal
        if (sq[0].contains("HighestLifeTotal")) {
            return Math.max(AllZone.getHumanPlayer().getLife(), AllZone.getComputerPlayer().getLife());
        }

        // Count$LowestLifeTotal
        if (sq[0].contains("LowestLifeTotal")) {
            return Math.min(AllZone.getHumanPlayer().getLife(), AllZone.getComputerPlayer().getLife());
        }

        // Count$TopOfLibraryCMC
        if (sq[0].contains("TopOfLibraryCMC")) {
            CardList topcard = AllZoneUtil.getPlayerCardsInLibrary(cardController, 1);
            return doXMath(topcard.getTotalConvertedManaCost(), m, c);
        }


        // Count$Chroma.<mana letter>
        if (sq[0].contains("Chroma")) return doXMath(
                getNumberOfManaSymbolsControlledByColor(sq[1], cardController), m, c);

        // Count$Hellbent.<numHB>.<numNotHB>
        if (sq[0].contains("Hellbent")) {
            if (cardController.hasHellbent())
                return doXMath(Integer.parseInt(sq[1]), m, c); // Hellbent
            else
                return doXMath(Integer.parseInt(sq[2]), m, c); // not Hellbent
        }

        //Count$Metalcraft.<numMC>.<numNotMC>
        if (sq[0].contains("Metalcraft")) {
            if (cardController.hasMetalcraft())
                return doXMath(Integer.parseInt(sq[1]), m, c);
            else
                return doXMath(Integer.parseInt(sq[2]), m, c);
        }

        if (sq[0].contains("Threshold")) {
            if (cardController.hasThreshold())
                return doXMath(Integer.parseInt(sq[1]), m, c); // Have Threshold
            else
                return doXMath(Integer.parseInt(sq[2]), m, c); // not Threshold
        }
        
        if (sq[0].contains("Landfall")) {
            if (cardController.hasLandfall())
                return doXMath(Integer.parseInt(sq[1]), m, c); // Have Landfall
            else
                return doXMath(Integer.parseInt(sq[2]), m, c); // not Landfall
        }

        if (sq[0].startsWith("Devoured")) {
            final String validDevoured = l[0].split(" ")[1];
            final Card csource = c;
            CardList cl = c.getDevoured();

            cl = cl.filter(new CardListFilter() {
                public boolean addCard(Card cdev)
                {
                    return cdev.isValidCard(validDevoured.split(","),csource.getController(),csource);
                }
            });

            return doXMath(cl.size(),m,c);
        }

        // Count$CardPower
        if (sq[0].contains("CardPower")) return doXMath(c.getNetAttack(), m, c);
        // Count$CardToughness
        if (sq[0].contains("CardToughness")) return doXMath(c.getNetDefense(), m, c);
        // Count$CardPowerPlusToughness
        if (sq[0].contains("CardSumPT")) return doXMath((c.getNetAttack() + c.getNetDefense()), m, c);
        // Count$CardManaCost
        if (sq[0].contains("CardManaCost")) return doXMath(CardUtil.getConvertedManaCost(c), m, c);
        // Count$CardCounters.<counterType>
        if (sq[0].contains("CardCounters"))
            return doXMath(c.getCounters(Counters.getType(sq[1])), m, c);
        // Count$TimesKicked
        if (sq[0].contains("TimesKicked"))
            return doXMath(c.getMultiKickerMagnitude(), m, c);
        if (sq[0].contains("NumCounters")) {
            int num = c.getCounters(Counters.getType(sq[1]));
            return doXMath(num, m, c);
        }
        if (sq[0].contains("NumBlockingMe"))
            return doXMath(AllZone.getCombat().getBlockers(c).size(), m, c);

        //Count$IfMainPhase.<numMain>.<numNotMain> // 7/10
        if (sq[0].contains("IfMainPhase")) {
            String cPhase = AllZone.getPhase().getPhase();
            if ((cPhase.equals(Constant.Phase.Main1) ||
                    cPhase.equals(Constant.Phase.Main2)) &&
                    AllZone.getPhase().getPlayerTurn().equals(cardController))
                return doXMath(Integer.parseInt(sq[1]), m, c);
            else
                return doXMath(Integer.parseInt(sq[2]), m, c); // not Main Phase
        }

        //Count$ThisTurnEntered <ZoneDestination> <ZoneOrigin> <Valid>
        //or
        //Count$ThisTurnEntered <ZoneDestination <Valid>
        if (sq[0].startsWith("ThisTurnEntered")) {
            String[] workingCopy = l[0].split(" ");
            String destination, origin, validFilter;

            destination = workingCopy[1];
            if (workingCopy[2].equals("from")) {
                origin = workingCopy[3];
                validFilter = workingCopy[4];
            } else {
                origin = "Any";
                validFilter = workingCopy[2];
            }

            final String[] valid = validFilter.split(",");
            final Card csource = c;
            CardList res = ((DefaultPlayerZone) AllZone.getZone(destination, AllZone.getHumanPlayer())).getCardsAddedThisTurn(origin);
            res.addAll(((DefaultPlayerZone) AllZone.getZone(destination, AllZone.getComputerPlayer())).getCardsAddedThisTurn(origin));

            res = res.filter(new CardListFilter() {
                public boolean addCard(Card csubject) {
                    return csubject.isValidCard(valid, csource.getController(), csource);
                }
            });


            return doXMath(res.size(), m, c);
        }

        //Generic Zone-based counting
        // Count$QualityAndZones.Subquality

        // build a list of cards in each possible specified zone

        // if a card was ever written to count two different zones,
        // make sure they don't get added twice.
        boolean MF = false, MY = false, MH = false;
        boolean OF = false, OY = false, OH = false;

        if (sq[0].contains("YouCtrl")) if (MF == false) {
            someCards.addAll(AllZoneUtil.getPlayerCardsInPlay(cardController));
            MF = true;
        }

        if (sq[0].contains("InYourYard")) if (MY == false) {
            someCards.addAll(AllZoneUtil.getPlayerGraveyard(cardController));
            MY = true;
        }

        if (sq[0].contains("InYourLibrary")) if (MY == false) {
            someCards.addAll(AllZoneUtil.getPlayerCardsInLibrary(cardController));
            MY = true;
        }

        if (sq[0].contains("InYourHand")) if (MH == false) {
            someCards.addAll(AllZoneUtil.getPlayerHand(cardController));
            MH = true;
        }

        if (sq[0].contains("OppCtrl")) if (OF == false) {
            someCards.addAll(AllZoneUtil.getPlayerCardsInPlay(oppController));
            OF = true;
        }

        if (sq[0].contains("InOppYard")) if (OY == false) {
            someCards.addAll(AllZoneUtil.getPlayerGraveyard(oppController));
            OY = true;
        }

        if (sq[0].contains("InOppHand")) if (OH == false) {
            someCards.addAll(AllZoneUtil.getPlayerHand(oppController));
            OH = true;
        }

        if (sq[0].contains("OnBattlefield")) {
            if (MF == false) someCards.addAll(AllZoneUtil.getPlayerCardsInPlay(cardController));
            if (OF == false) someCards.addAll(AllZoneUtil.getPlayerCardsInPlay(oppController));
        }

        if (sq[0].contains("InAllYards")) {
            if (MY == false) someCards.addAll(AllZoneUtil.getPlayerGraveyard(cardController));
            if (OY == false) someCards.addAll(AllZoneUtil.getPlayerGraveyard(oppController));
        }

        if (sq[0].contains("InAllHands")) {
            if (MH == false) someCards.addAll(AllZoneUtil.getPlayerHand(cardController));
            if (OH == false) someCards.addAll(AllZoneUtil.getPlayerHand(oppController));
        }

        // filter lists based on the specified quality


        // "Clerics you control" - Count$TypeYouCtrl.Cleric
        if (sq[0].contains("Type")) {
            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    if (c.isType(sq[1])) return true;

                    return false;
                }
            });
        }

        // "Named <CARDNAME> in all graveyards" - Count$NamedAllYards.<CARDNAME>

        if (sq[0].contains("Named")) {
            if (sq[1].equals("CARDNAME"))
                sq[1] = c.getName();

            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    if (c.getName().equals(sq[1])) return true;

                    return false;
                }
            });
        }

        // Refined qualities

        // "Untapped Lands" - Count$UntappedTypeYouCtrl.Land
        if (sq[0].contains("Untapped")) {
            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return !c.isTapped();
                }
            });
        }

        if (sq[0].contains("Tapped")) {
            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return c.isTapped();
                }
            });
        }

        // "White Creatures" - Count$WhiteTypeYouCtrl.Creature
        if (sq[0].contains("White")) {
            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return CardUtil.isColor(c, Constant.Color.White);
                }
            });
        }

        if (sq[0].contains("Blue")) {
            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return CardUtil.isColor(c, Constant.Color.Blue);
                }
            });
        }

        if (sq[0].contains("Black")) {
            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return CardUtil.isColor(c, Constant.Color.Black);
                }
            });
        }

        if (sq[0].contains("Red")) {
            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return CardUtil.isColor(c, Constant.Color.Red);
                }
            });
        }

        if (sq[0].contains("Green")) {
            someCards = someCards.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return CardUtil.isColor(c, Constant.Color.Green);
                }
            });
        }

        if (sq[0].contains("Multicolor")) someCards = someCards.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return (CardUtil.getColors(c).size() > 1);
            }
        });

        if (sq[0].contains("Monocolor")) someCards = someCards.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return (CardUtil.getColors(c).size() == 1);
            }
        });

        //Count$CardMulticolor.<numMC>.<numNotMC>
        if (sq[0].contains("CardMulticolor")) {
            if (CardUtil.getColors(c).size() > 1)
                return doXMath(Integer.parseInt(sq[1]), m, c);
            else
                return doXMath(Integer.parseInt(sq[2]), m, c);
        }

        // 1/10 - Count$MaxCMCYouCtrl
        if (sq[0].contains("MaxCMC")) {
            int mmc = 0;
            int cmc = 0;
            for (int i = 0; i < someCards.size(); i++) {
                cmc = CardUtil.getConvertedManaCost(someCards.getCard(i).getManaCost());
                if (cmc > mmc) mmc = cmc;
            }

            return doXMath(mmc, m, c);
        }

        n = someCards.size();

        return doXMath(n, m, c);
    }

    private static int doXMath(int num, String m, Card c) {
        if (m.equals("none")) return num;

        String[] s = m.split("\\.");
        int secondaryNum = 0;

        try {
            if (s.length == 2) {
                secondaryNum = Integer.parseInt(s[1]);
            }
        } catch (Exception e) {
            secondaryNum = xCount(c, c.getSVar(s[1]));
        }

        if (s[0].contains("Plus")) return num + secondaryNum;
        else if (s[0].contains("NMinus")) return secondaryNum - num;
        else if (s[0].contains("Minus")) return num - secondaryNum;
        else if (s[0].contains("Twice")) return num * 2;
        else if (s[0].contains("HalfUp")) return (int) (Math.ceil(num / 2.0));
        else if (s[0].contains("HalfDown")) return (int) (Math.floor(num / 2.0));
        else if (s[0].contains("ThirdUp")) return (int) (Math.ceil(num / 3.0));
        else if (s[0].contains("ThirdDown")) return (int) (Math.floor(num / 3.0));
        else if (s[0].contains("Negative")) return num * -1;
        else if (s[0].contains("Times")) return num * secondaryNum;
        else return num;
    }

    /**
     * <p>doXMath.</p>
     *
     * @param num a int.
     * @param m an array of {@link java.lang.String} objects.
     * @param c a {@link forge.Card} object.
     * @return a int.
     */
    public static int doXMath(int num, String[] m, Card c) {
        if (m.length == 0)
            return num;

        return doXMath(num, m[0], c);
    }

    /**
     * <p>handlePaid.</p>
     *
     * @param paidList a {@link forge.CardList} object.
     * @param string a {@link java.lang.String} object.
     * @param source a {@link forge.Card} object.
     * @return a int.
     */
    public static int handlePaid(CardList paidList, String string, Card source) {
        if (paidList == null || paidList.size() == 0)
            return 0;

        if (string.startsWith("Amount")) {
            if (string.contains(".")) {
                String[] splitString = string.split("\\.", 2);
                return doXMath(paidList.size(), splitString[1], source);
            } else
                return paidList.size();

        }
        if (string.contains("Valid")) {
            final String m[] = {"none"};

            String valid = string.replace("Valid ", "");
            final String[] l;
            l = valid.split("/"); // separate the specification from any math
            valid = l[0];
            if (l.length > 1) m[0] = l[1];
            CardList list = paidList.getValidCards(valid, source.getController(), source);
            return doXMath(list.size(), m, source);
        }

        int tot = 0;
        for (Card c : paidList)
            tot += xCount(c, string);

        return tot;
    }


    /**
     * <p>getNumberOfMostProminentCreatureType.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @param type a {@link java.lang.String} object.
     * @return a int.
     */
    public static int getNumberOfMostProminentCreatureType(CardList list, String type) {
        list = list.getType(type);
        return list.size();
    }

    /**
     * <p>input_UntapUpToNType.</p>
     *
     * @param n a int.
     * @param type a {@link java.lang.String} object.
     * @return a {@link forge.gui.input.Input} object.
     */
    public static Input input_UntapUpToNType(final int n, final String type) {
        Input untap = new Input() {
            private static final long serialVersionUID = -2167059918040912025L;

            int stop = n;
            int count = 0;

            @Override
            public void showMessage() {
                AllZone.getDisplay().showMessage("Select a " + type + " to untap");
                ButtonUtil.enableOnlyCancel();
            }

            @Override
            public void selectButtonCancel() {
                stop();
            }

            @Override
            public void selectCard(Card card, PlayerZone zone) {
                if (card.isType(type) && zone.is(Constant.Zone.Battlefield)) {
                    card.untap();
                    count++;
                    if (count == stop) stop();
                }
            }//selectCard()
        };

        return untap;
    }

    /**
     * <p>getMostProminentCreatureType.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getMostProminentCreatureType(CardList list) {

        if (list.size() == 0) return "";

        Map<String, Integer> map = new HashMap<String, Integer>();

        for (Card c : list) {
            ArrayList<String> typeList = c.getType();

            for (String var : typeList) {
                if (CardUtil.isACreatureType(var)) {
                    if (!map.containsKey(var)) map.put(var, 1);
                    else map.put(var, map.get(var) + 1);
                }
            }
        }//for

        int max = 0;
        String maxType = "";

        for (Entry<String, Integer> entry : map.entrySet()) {
            String type = entry.getKey();
            //Log.debug(type + " - " + entry.getValue());

            if (max < entry.getValue()) {
                max = entry.getValue();
                maxType = type;
            }
        }

        return maxType;
    }

    /**
     * <p>getMostProminentColor.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getMostProminentColor(CardList list) {

        Map<String, Integer> map = new HashMap<String, Integer>();

        for (Card c : list) {
            ArrayList<String> colorList = CardUtil.getColors(c);

            for (String color : colorList) {
                if (color.equals("colorless")) ;
                else if (!map.containsKey(color)) map.put(color, 1);
                else {
                    map.put(color, map.get(color) + 1);
                }
            }
        }//for

        int max = 0;
        String maxColor = "";

        for (Entry<String, Integer> entry : map.entrySet()) {
            String color = entry.getKey();
            Log.debug(color + " - " + entry.getValue());

            if (max < entry.getValue()) {
                max = entry.getValue();
                maxColor = color;
            }
        }

        return maxColor;
    }


    /**
     * <p>chooseCreatureTypeAI.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a {@link java.lang.String} object.
     */
    public static String chooseCreatureTypeAI(Card c) {
        String s = "";
        //TODO, take into account what human has

        CardList humanPlay = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
        CardList humanLib = AllZoneUtil.getPlayerCardsInLibrary(AllZone.getHumanPlayer());

        CardList compPlay = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer());
        CardList compHand = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());
        CardList compLib = AllZoneUtil.getPlayerCardsInLibrary(AllZone.getComputerPlayer());
        CardList compAll = new CardList();
        compAll.addAll(compLib);
        compAll.addAll(compHand);
        compAll.addAll(compPlay);

        humanPlay = humanPlay.getType("Creature");
        humanLib = humanLib.getType("Creature");

        compPlay = compPlay.getType("Creature");
        compHand = compHand.getType("Creature");
        compAll = compAll.getType("Creature");

        //Buffs
        if (c.getName().equals("Conspiracy") || c.getName().equals("Cover of Darkness")
                || c.getName().equals("Belbe's Portal") || c.getName().equals("Steely Resolve")
                || c.getName().equals("Shared Triumph")) {

            String type = "";
            int number = 0;

            type = getMostProminentCreatureType(compAll);
            number = getNumberOfMostProminentCreatureType(compAll, type);
            if (number >= 5) s = type;

            if ((c.getName().equals("Shared Triumph") || c.getName().equals("Cover of Darkness")
                    || c.getName().equals("Steely Resolve")) && compPlay.size() > 7) {
                type = getMostProminentCreatureType(compPlay);
                number = getNumberOfMostProminentCreatureType(compPlay, type);
                if (number >= 3) s = type;
            } else if ((c.getName().equals("Belbe's Portal")) && compHand.size() > 1) {
                type = getMostProminentCreatureType(compHand);
                number = getNumberOfMostProminentCreatureType(compHand, type);
                if (number >= 2) s = type;
            } else if ((c.getName().equals("Conspiracy")) && compAll.size() > 1) {
                CardList turnTimber = compAll;
                turnTimber = turnTimber.getName("Turntimber Ranger");
                if (c.getName().equals("Conspiracy") && turnTimber.size() > 0) s = "Ally";
            }

        }
        //Debuffs
        else if (c.getName().equals("Engineered Plague")) {
            String type = "";
            int number = 0;
            if (c.getName().equals("Engineered Plague") && humanPlay.size() > 6) {
                type = getMostProminentCreatureType(humanPlay);
                number = getNumberOfMostProminentCreatureType(humanPlay, type);
                if (number >= 3) s = type;
                else if (humanLib.size() > 0) {
                    type = getMostProminentCreatureType(humanLib);
                    number = getNumberOfMostProminentCreatureType(humanLib, type);
                    if (number >= 5) s = type;
                }
            }
        }
        return s;
    }

    /**
     * <p>countBasicLandTypes.</p>
     *
     * @param player a {@link forge.Player} object.
     * @return a int.
     */
    public static int countBasicLandTypes(Player player) {
        String basic[] = {"Forest", "Plains", "Mountain", "Island", "Swamp"};
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);
        int count = 0;

        for (int i = 0; i < basic.length; i++)
            if (!list.getType(basic[i]).isEmpty()) count++;

        return count;
    }

    //total cost to pay for an attacker c, cards like Propaganda, Ghostly Prison, Collective Restraint, ...
    /**
     * <p>getPropagandaCost.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getPropagandaCost(Card c) {
        int cost = 0;

        CardList list = AllZoneUtil.getCardsInPlay();
        for (Card card : list) {
            if (card.hasStartOfKeyword("Creatures can't attack unless their controller pays")) {
                int KeywordPosition = card.getKeywordPosition("Creatures can't attack unless their controller pays");
                String parse = card.getKeyword().get(KeywordPosition).toString();
                String k[] = parse.split(":");

                String restrictions[] = k[1].split(",");
                if (!c.isValidCard(restrictions, card.getController(), card))
                    continue;

                String costString = k[2];
                if (costString.equals("X"))
                    cost += CardFactoryUtil.xCount(card, card.getSVar("X"));
                else if (costString.equals("Y"))
                    cost += CardFactoryUtil.xCount(card, card.getSVar("Y"));
                else
                    cost += Integer.parseInt(k[2]);
            }
        }

        String s = Integer.toString(cost);

        return s;
    }

    /**
     * <p>getUsableManaSources.</p>
     *
     * @param player a {@link forge.Player} object.
     * @return a int.
     */
    public static int getUsableManaSources(Player player) {
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                for (Ability_Mana am : c.getAIPlayableMana())
                    if (am.canPlay()) return true;
                return false;
            }
        });

        return list.size();
    }

    /**
     * <p>getTopCard.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a {@link forge.Card} object.
     */
    public static Card getTopCard(Card c) {
        PlayerZone lib = AllZone.getZone(Constant.Zone.Library, c.getController());
        if (lib.size() > 0)
            return lib.get(0);
        else
            return null;
    }

    /**
     * <p>makeTokenSaproling.</p>
     *
     * @param controller a {@link forge.Player} object.
     * @return a {@link forge.CardList} object.
     */
    public static CardList makeTokenSaproling(Player controller) {
        return makeToken("Saproling", "G 1 1 Saproling", controller, "G", new String[]{"Creature", "Saproling"}, 1, 1, new String[]{""});
    }

    /**
     * <p>makeToken.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param imageName a {@link java.lang.String} object.
     * @param controller a {@link forge.Player} object.
     * @param manaCost a {@link java.lang.String} object.
     * @param types an array of {@link java.lang.String} objects.
     * @param baseAttack a int.
     * @param baseDefense a int.
     * @param intrinsicKeywords an array of {@link java.lang.String} objects.
     * @return a {@link forge.CardList} object.
     */
    public static CardList makeToken(String name, String imageName, Player controller, String manaCost, String[] types, int baseAttack, int baseDefense, String[] intrinsicKeywords) {
        CardList list = new CardList();
        Card c = new Card();
        c.setName(name);
        c.setImageName(imageName);

        //c.setController(controller);
        //c.setOwner(controller);

        // TODO: most tokens mana cost is 0, this needs to be fixed
        //c.setManaCost(manaCost);
        c.addColor(manaCost);
        c.setToken(true);

        for (String t : types)
            c.addType(t);

        c.setBaseAttack(baseAttack);
        c.setBaseDefense(baseDefense);

        for (String kw : intrinsicKeywords)
            if (kw.startsWith("HIDDEN"))
                c.addExtrinsicKeyword(kw);
            else c.addIntrinsicKeyword(kw);

        int multiplier = AllZoneUtil.getDoublingSeasonMagnitude(controller);
        // TODO: does this need to set PlayerZone_ComesIntoPlay.SimultaneousEntry like Rite of Replication does?
        for (int i = 0; i < multiplier; i++) {
            Card temp = CardFactory.copyStats(c);
            temp.setController(controller);
            temp.setOwner(controller);
            temp.setToken(true);
            CardFactory.parseKeywords(temp, temp.getName());
            temp = CardFactory.postFactoryKeywords(temp);
            AllZone.getGameAction().moveToPlay(temp);
            list.add(temp);
        }
        return list;
    }

    /**
     * <p>copyTokens.</p>
     *
     * @param tokenList a {@link forge.CardList} object.
     * @return a {@link forge.CardList} object.
     */
    public static CardList copyTokens(CardList tokenList) {
        CardList list = new CardList();

        for (int tokenAdd = 0; tokenAdd < tokenList.size(); tokenAdd++) {
            Card thisToken = tokenList.getCard(tokenAdd);

            ArrayList<String> tal = thisToken.getType();
            String tokenTypes[] = new String[tal.size()];
            tal.toArray(tokenTypes);

            ArrayList<String> kal = thisToken.getIntrinsicKeyword();
            String tokenKeywords[] = new String[kal.size()];
            kal.toArray(tokenKeywords);
            CardList tokens = makeToken(thisToken.getName(), thisToken.getImageName(), thisToken.getController(), thisToken.getManaCost(), tokenTypes, thisToken.getBaseAttack(), thisToken.getBaseDefense(), tokenKeywords);
            
            for (Card token : tokens)
            	token.setColor(thisToken.getColor());
            
            list.addAll(tokens);
        }

        return list;
    }

    /**
     * <p>getBushidoEffects.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a {@link java.util.ArrayList} object.
     */
    public static ArrayList<Ability> getBushidoEffects(Card c) {
        ArrayList<String> keywords = c.getKeyword();
        ArrayList<Ability> list = new ArrayList<Ability>();

        final Card crd = c;

        for (String kw : keywords) {
            if (kw.contains("Bushido")) {
                String[] parse = kw.split(" ");
                String s = parse[1];
                final int magnitude = Integer.parseInt(s);


                Ability ability = new Ability(c, "0") {
                    @Override
                    public void resolve() {
                        final Command untilEOT = new Command() {

                            private static final long serialVersionUID = 3014846051064254493L;

                            public void execute() {
                                if (AllZoneUtil.isCardInPlay(crd)) {
                                    crd.addTempAttackBoost(-1 * magnitude);
                                    crd.addTempDefenseBoost(-1 * magnitude);
                                }
                            }
                        };

                        AllZone.getEndOfTurn().addUntil(untilEOT);

                        crd.addTempAttackBoost(magnitude);
                        crd.addTempDefenseBoost(magnitude);
                    }
                };
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                sb.append(" - (Bushido) gets +");
                sb.append(magnitude);
                sb.append("/+");
                sb.append(magnitude);
                sb.append(" until end of turn.");
                ability.setStackDescription(sb.toString());

                list.add(ability);
            }
        }
        return list;
    }

    /**
     * <p>getNeededXDamage.</p>
     *
     * @param ability a {@link forge.card.spellability.SpellAbility} object.
     * @return a int.
     */
    static public int getNeededXDamage(SpellAbility ability) {
        //when targeting a creature, make sure the AI won't overkill on X damage
        Card target = ability.getTargetCard();
        int neededDamage = -1;

        Card c = ability.getSourceCard();

        if (target != null && c.getText().contains("deals X damage to target") && !c.getName().equals("Death Grasp"))
            neededDamage = target.getNetDefense() - target.getDamage();

        return neededDamage;
    }

    /**
     * getWorstLand(String)
     * <p/>
     * This function finds the worst land a player has in play based on:
     * worst
     * 1. tapped, basic land
     * 2. tapped, non-basic land
     * 3. untapped, basic land
     * 4. untapped, non-basic land
     * best
     * <p/>
     * This is useful when the AI needs to find one of its lands to sacrifice
     *
     * @param player - AllZone.getHumanPlayer() or AllZone.getComputerPlayer()
     * @return the worst land found based on the description above
     */
    public static Card getWorstLand(Player player) {
        CardList lands = AllZoneUtil.getPlayerLandsInPlay(player);
        return getWorstLand(lands);
    }//end getWorstLand

    /**
     * <p>getWorstLand.</p>
     *
     * @param lands a {@link forge.CardList} object.
     * @return a {@link forge.Card} object.
     */
    public static Card getWorstLand(CardList lands) {
        Card worstLand = null;
        //first, check for tapped, basic lands
        for (int i = 0; i < lands.size(); i++) {
            Card tmp = lands.get(i);
            if (tmp.isTapped() && tmp.isBasicLand()) {
                worstLand = tmp;
            }
        }
        //next, check for tapped, non-basic lands
        if (worstLand == null) {
            for (int i = 0; i < lands.size(); i++) {
                Card tmp = lands.get(i);
                if (tmp.isTapped()) {
                    worstLand = tmp;
                }
            }
        }
        //next, untapped, basic lands
        if (worstLand == null) {
            for (int i = 0; i < lands.size(); i++) {
                Card tmp = lands.get(i);
                if (tmp.isUntapped() && tmp.isBasicLand()) {
                    worstLand = tmp;
                }
            }
        }
        //next, untapped, non-basic lands
        if (worstLand == null) {
            for (int i = 0; i < lands.size(); i++) {
                Card tmp = lands.get(i);
                if (tmp.isUntapped()) {
                    worstLand = tmp;
                }
            }
        }
        return worstLand;
    }//end getWorstLand

    //may return null
    /**
     * <p>getRandomCard.</p>
     *
     * @param list a {@link forge.CardList} object.
     * @return a {@link forge.Card} object.
     */
    static public Card getRandomCard(CardList list) {
        if (list.size() == 0) return null;

        int index = random.nextInt(list.size());
        return list.get(index);
    }

    /**
     * <p>revertManland.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param cost a {@link java.lang.String} object.
     * @param timeStamp a long.
     * @param removeTypes an array of {@link java.lang.String} objects.
     * @param removeKeywords an array of {@link java.lang.String} objects.
     */
    public static void revertManland(Card c, String[] removeTypes, String[] removeKeywords, String cost, long timeStamp) {
        c.setBaseAttack(0);
        c.setBaseDefense(0);
        for (String r : removeTypes)
            c.removeType(r);

        for (String k : removeKeywords)
            c.removeIntrinsicKeyword(k);

        //c.setManaCost(cost);
        c.removeColor(cost, c, false, timeStamp);
        c.unEquipAllCards();
    }

    /**
     * <p>activateManland.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param cost a {@link java.lang.String} object.
     * @return a long.
     * @param attack a int.
     * @param defense a int.
     * @param addTypes an array of {@link java.lang.String} objects.
     * @param addKeywords an array of {@link java.lang.String} objects.
     */
    public static long activateManland(Card c, int attack, int defense, String[] addTypes, String[] addKeywords, String cost) {
        c.setBaseAttack(attack);
        c.setBaseDefense(defense);

        for (String r : addTypes) {
            // if the card doesn't have that type, add it
            if (!c.isType(r))
                c.addType(r);
        }
        for (String k : addKeywords) {
            // if the card doesn't have that keyword, add it (careful about stackable keywords)
            if (!c.getIntrinsicKeyword().contains(k))
                c.addIntrinsicKeyword(k);
        }

        //c.setManaCost(cost);
        if (cost.equals(""))
            cost = "0";

        long timestamp = c.addColor(cost, c, false, true);
        return timestamp;
    }

    /**
     * <p>playLandEffects.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void playLandEffects(Card c) {
        final Player player = c.getController();

        // > 0 because land amount isn't incremented until after playLandEffects
        boolean extraLand = player.getNumLandsPlayed() > 0;

        if (extraLand) {
            CardList fastbonds = AllZoneUtil.getPlayerCardsInPlay(player, "Fastbond");
            for (final Card f : fastbonds) {
                SpellAbility ability = new Ability(f, "0") {
                    @Override
                    public void resolve() {
                        f.getController().addDamage(1, f);
                    }
                };
                ability.setStackDescription("Fastbond - Deals 1 damage to you.");

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }
    }


    /**
     * <p>isNegativeCounter.</p>
     *
     * @param c a {@link forge.Counters} object.
     * @return a boolean.
     */
    public static boolean isNegativeCounter(Counters c) {
        return c == Counters.AGE || c == Counters.BLAZE || c == Counters.BRIBERY || c == Counters.DOOM || c == Counters.ICE ||
                c == Counters.M1M1 || c == Counters.M0M2 || c == Counters.M0M1 || c == Counters.TIME;
    }

    /**
     * <p>checkEmblemKeyword.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a {@link java.lang.String} object.
     */
    public static String checkEmblemKeyword(Card c) {
        if (c.hasKeyword("Artifacts, creatures, enchantments, and lands you control are indestructible."))
            return "Elspeth_Emblem";

        if (c.hasKeyword("Mountains you control have 'tap: This land deals 1 damage to target creature or player.'"))
            return "Koth_Emblem";

        return "";
    }
    /*
    //whenever CARDNAME becomes the target of a spell or ability, ... :
    public static void checkTargetingEffects(SpellAbility sa, final Card c)
    {
    	
    	//if (AllZoneUtil.isCardInPlay(c)) 
    	//{
    	if (c.hasKeyword("When CARDNAME becomes the target of a spell or ability, return CARDNAME to its owner's hand.") ) { // || (c.isCreature() && AllZoneUtil.isCardInPlay("Cowardice"))
    		SpellAbility ability = new Ability(c, "0")
    		{
    			public void resolve()
    			{
    				AllZone.getGameAction().moveToHand(c);
    			}
    		};
    		StringBuilder sb = new StringBuilder();
    		sb.append(c).append(" - return CARDNAME to its owner's hand.");
    		ability.setStackDescription(sb.toString());
    		
    		AllZone.getStack().add(ability);
    	}
    	if (c.hasKeyword("When CARDNAME becomes the target of a spell or ability, destroy CARDNAME.") 
    			|| AllZoneUtil.isCardInPlay("Horobi, Death's Wail")) {
    		
    		SpellAbility ability = new Ability(c, "0")
    		{
    			public void resolve()
    			{
    				AllZone.getGameAction().destroy(c); 	
    			}
    		};
    		StringBuilder sb = new StringBuilder();
    		sb.append(c).append(" - destroy CARDNAME.");
    		ability.setStackDescription(sb.toString());
    		
    		AllZone.getStack().add(ability);
    	}
    	if (c.hasKeyword("When CARDNAME becomes the target of a spell or ability, sacrifice it.")) {
    		SpellAbility ability = new Ability(c, "0")
    		{
    			public void resolve()
    			{
    				AllZone.getGameAction().sacrifice(c);	
    			}
    		};
    		StringBuilder sb = new StringBuilder();
    		sb.append(c).append(" - sacrifice CARDNAME.");
    		ability.setStackDescription(sb.toString());
    		
    		AllZone.getStack().add(ability);
    	}

    	//When enchanted creature becomes the target of a spell or ability, <destroy/exile/sacrifice> <that creature/CARDNAME>. (It can't be regenerated.)
    	ArrayList<Card> auras = c.getEnchantedBy();
    	for(int a=0;a<auras.size();a++)
    	{
    		final Card aura = auras.get(a);
    		ArrayList<String> keywords = aura.getKeyword();
    		for(int i=0;i<keywords.size();i++)
    		{
    			final String keyword = keywords.get(i);
    			if(keyword.startsWith("When enchanted creature becomes the target of a spell or ability, "))
    			{
    				final String action[] = new String[1];
    				action[0] = keyword.substring(66);
    				String stackDesc = action[0];
    				stackDesc = stackDesc.replace("that", "enchanted");
    				stackDesc = stackDesc.substring(0,1).toUpperCase().concat(stackDesc.substring(1));
    				stackDesc = aura.getName().concat(" (").concat(Integer.toString(aura.getUniqueNumber())).concat(") - ").concat(stackDesc);
    				
    				Ability saTrigger = new Ability(aura,"0") {
    					public void resolve()
    					{
    						Card target = null;
    						boolean noRegen = false;
    						if(action[0].endsWith(" It can't be regenerated."))
    	    				{
    	    					noRegen = true;
    	    					action[0] = action[0].substring(0,action[0].length()-25);
    	    				}
    	    				
    	    				if(action[0].endsWith("CARDNAME."))
    	    				{
    	    					target = aura;
    	    				}
    	    				else if(action[0].endsWith("that creature."))
    	    				{
    	    					target = c;
    	    				}
    	    				else
    	    				{
    	    					throw new IllegalArgumentException("There is a problem in the keyword " + keyword + "for card \"" + c.getName() + "\"");
    	    				}
    	    				
    	    				if(action[0].startsWith("exile"))
    	    				{   					
    	    					AllZone.getGameAction().exile(target);
    	    				}
    	    				else if(action[0].startsWith("destroy"))
    	    				{
    	    					if(noRegen)
    	    					{
    	    						AllZone.getGameAction().destroyNoRegeneration(target);
    	    					}
    	    					else
    	    					{
    	    						AllZone.getGameAction().destroy(target);
    	    					}    					
    	    				}
    	    				else if(action[0].startsWith("sacrifice"))
    	    				{
    	    					AllZone.getGameAction().sacrifice(target);
    	    				}
    	    				else
    	    				{
    	    					throw new IllegalArgumentException("There is a problem in the keyword " + keyword + "for card \"" + c.getName() + "\"");
    	    				}
    					}
    				};
    				
    				saTrigger.setStackDescription(stackDesc);
    				
    				AllZone.getStack().add(saTrigger);
    			}
    		}
    	}
    	//}
    }*/
}
