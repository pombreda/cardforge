import java.util.*;

public class CardFactoryUtil {
	private static Random random = new Random();

	public static boolean AI_doesCreatureAttack(Card card) {
		Combat combat = ComputerUtil.getAttackers();
		Card[] att = combat.getAttackers();
		for (int i = 0; i < att.length; i++)
			if (att[i].equals(card))
				return true;

		return false;
	}

	// returns null if list.size() == 0
	public static Card AI_getBestCreature(CardList list) {
		CardList all = list;
		all = all.getType("Creature");

		CardList flying = all.filter(new CardListFilter() {
			public boolean addCard(Card c) {
				return c.getKeyword().contains("Flying");
			}
		});
		// get biggest flying creature
		Card biggest = null;
		if (flying.size() != 0) {
			biggest = flying.get(0);

			for (int i = 0; i < flying.size(); i++)
				if (biggest.getAttack() < flying.get(i).getAttack())
					biggest = flying.get(i);
		}

		// if flying creature is small, get biggest non-flying creature
		if (all.size() != 0 && (biggest == null || biggest.getAttack() < 3)) {
			biggest = all.get(0);

			for (int i = 0; i < all.size(); i++)
				if (biggest.getAttack() < all.get(i).getAttack())
					biggest = all.get(i);
		}
		return biggest;
	}

	public static Input input_targetCreaturePlayer(final SpellAbility spell) {
		return input_targetCreaturePlayer(spell, Command.Blank);
	}

	public static Input input_targetCreaturePlayer(final SpellAbility spell,
			final Command paid) {
		Input target = new Input() {
			public void showMessage() {
				AllZone.Display
						.showMessage("Select target Creature, Player, or Planeswalker");
				ButtonUtil.enableOnlyCancel();
			}

			public void selectButtonCancel() {
				stop();
			}

			public void selectCard(Card card, PlayerZone zone) {
				if ((card.isCreature() || card.isPlaneswalker())
						&& zone.is(Constant.Zone.Play)) {
					spell.setTargetCard(card);
					done();
				}
			}// selectCard()

			public void selectPlayer(String player) {
				spell.setTargetPlayer(player);
				done();
			}

			void done() {
				paid.execute();

				if (spell instanceof Ability_Tap
						&& spell.getManaCost().equals("0"))
					stopSetNext(new Input_NoCost_TapAbility((Ability_Tap) spell));
				else if (spell.getManaCost().equals("0")) {
					AllZone.Stack.add(spell);
					stop();
				} else
					stopSetNext(new Input_PayManaCost(spell));
			}
		};
		return target;
	}// input_targetCreaturePlayer()

	// CardList choices are the only cards the user can successful select
	// sacrifices one of the CardList choices
	public static Input input_sacrifice(final SpellAbility spell,
			final CardList choices, final String message) {
		Input target = new Input() {
			public void showMessage() {
				AllZone.Display.showMessage(message);
				ButtonUtil.enableOnlyCancel();
			}

			public void selectButtonCancel() {
				stop();
			}

			public void selectCard(Card card, PlayerZone zone) {
				if (choices.contains(card)) {
					AllZone.getZone(card).remove(card);
					AllZone.GameAction.moveToGraveyard(card);

					if (spell.getManaCost().equals("0")) {
						AllZone.Stack.add(spell);
						stop();
					} else
						stopSetNext(new Input_PayManaCost(spell));
				}
			}
		};
		return target;
	}// input_sacrifice()

	public static SpellAbility ability_cycle(final Card sourceCard,
			final String cycleCost) {
		final SpellAbility cycle = new Ability_Hand(sourceCard, cycleCost) {
			public boolean canPlayAI() {
				return false;
			}

			public void resolve() {
				AllZone.GameAction.discard(sourceCard);
				AllZone.GameAction.drawCard(sourceCard.getController());
			}
		};
		cycle.setDescription("Cycling " + cycleCost + " (" + cycleCost
				+ ", Discard this card: Draw a card.)");
		cycle.setStackDescription(sourceCard + " Cycling: Draw a card");
		return cycle;
	}// ability_cycle()

	// CardList choices are the only cards the user can successful select
	public static Input input_targetSpecific(final SpellAbility spell,
			final CardList choices, final String message) {
		return input_targetSpecific(spell, choices, message, Command.Blank);
	}

	// CardList choices are the only cards the user can successful select
	public static Input input_targetSpecific(final SpellAbility spell,
			final CardList choices, final String message, final Command paid) {
		Input target = new Input() {
			public void showMessage() {
				AllZone.Display.showMessage(message);
				ButtonUtil.enableOnlyCancel();
			}

			public void selectButtonCancel() {
				stop();
			}

			public void selectCard(Card card, PlayerZone zone) {
				if (choices.contains(card)) {
					spell.setTargetCard(card);
					if (spell instanceof Ability_Tap
							&& spell.getManaCost().equals("0"))
						stopSetNext(new Input_NoCost_TapAbility(
								(Ability_Tap) spell));
					else if (spell.getManaCost().equals("0")) {
						AllZone.Stack.add(spell);
						stop();
					} else
						stopSetNext(new Input_PayManaCost(spell));

					paid.execute();
				}
			}// selectCard()
		};
		return target;
	}// input_targetSpecific()

	public static Input input_discard() {
		return input_discard(1);
	}

	public static Input input_discard(final int nCards) {
		Input target = new Input() {
			int n = 0;

			public void showMessage() {
				AllZone.Display.showMessage("Select a card to discard");
				ButtonUtil.disableAll();

				// in case no more cards in hand
				if (n == nCards || AllZone.Human_Hand.getCards().length == 0)
					stop();
			}

			public void selectCard(Card card, PlayerZone zone) {
				if (zone.is(Constant.Zone.Hand)) {
					AllZone.GameAction.discard(card);
					n++;
					showMessage();
				}
			}
		};
		return target;
	}// input_discard()

	// cardType is like "Creature", "Land", "Artifact", "Goblin", "Legendary"
	// cardType can also be "All", which will allow any permanent to be selected
	public static Input input_targetType(final SpellAbility spell,
			final String cardType) {
		Input target = new Input() {
			public void showMessage() {
				AllZone.Display.showMessage("Select target " + cardType);

				if (cardType.equals("All"))
					AllZone.Display.showMessage("Select target permanent");

				ButtonUtil.enableOnlyCancel();
			}

			public void selectButtonCancel() {
				stop();
			}

			public void selectCard(Card card, PlayerZone zone) {
				if ((card.getType().contains(cardType) || cardType
						.equals("All"))
						&& zone.is(Constant.Zone.Play)) {
					spell.setTargetCard(card);
					stopSetNext(new Input_PayManaCost(spell));
				}
			}
		};
		return target;
	}// input_targetType()

	public static Input input_targetCreature(final SpellAbility spell) {
		return input_targetCreature(spell, Command.Blank);
	}

	public static Input input_targetCreature(final SpellAbility spell,
			final Command paid) {
		Input target = new Input() {
			public void showMessage() {
				AllZone.Display.showMessage("Select target creature for "
						+ spell.getSourceCard());
				ButtonUtil.enableOnlyCancel();
			}

			public void selectButtonCancel() {
				stop();
			}

			public void selectCard(Card card, PlayerZone zone) {
				if (card.isCreature() && zone.is(Constant.Zone.Play)) {
					spell.setTargetCard(card);
					done();
				}
			}

			void done() {
				if (spell instanceof Ability_Tap
						&& spell.getManaCost().equals("0"))
					stopSetNext(new Input_NoCost_TapAbility((Ability_Tap) spell));
				else if (spell.getManaCost().equals("0"))// for
															// "sacrifice this card"
															// abilities
				{
					AllZone.Stack.add(spell);
					stop();
				} else
					stopSetNext(new Input_PayManaCost(spell));

				paid.execute();
			}
		};
		return target;
	}// input_targetCreature()

	public static Input input_targetCreature_NoCost_TapAbility(
			final Ability_Tap spell) {
		Input target = new Input() {
			public void showMessage() {
				AllZone.Display.showMessage("Select target creature");
				ButtonUtil.enableOnlyCancel();
			}

			public void selectButtonCancel() {
				stop();
			}

			public void selectCard(Card card, PlayerZone zone) {
				if (card.isCreature() && zone.is(Constant.Zone.Play)) {
					spell.setTargetCard(card);
					spell.getSourceCard().tap();
					AllZone.Stack.push(spell);
					stop();
				}
			}
		};
		return target;
	}// input_targetCreature()

	public static Input input_targetPlayer(final SpellAbility spell) {
		Input target = new Input() {
			public void showMessage() {
				AllZone.Display.showMessage("Select target player");
				ButtonUtil.enableOnlyCancel();
			}

			public void selectButtonCancel() {
				stop();
			}

			public void selectPlayer(String player) {
				spell.setTargetPlayer(player);
				if (spell.getManaCost().equals("0")) {
					AllZone.Stack.add(spell);
					stop();
				} else
					stopSetNext(new Input_PayManaCost(spell));
			}
		};
		return target;
	}// input_targetPlayer()

	public static CardList AI_getHumanCreature() {
		CardList creature = new CardList(AllZone.Human_Play.getCards());
		creature = creature.getType("Creature");
		return creature;
	}

	public static CardList AI_getHumanCreature(final String keyword) {
		CardList creature = new CardList(AllZone.Human_Play.getCards());
		creature = creature.filter(new CardListFilter() {
			public boolean addCard(Card c) {
				return c.isCreature() && c.getKeyword().contains(keyword);
			}
		});
		return creature;
	}// AI_getHumanCreature()

	public static CardList AI_getHumanCreature(final int toughness) {
		CardList creature = new CardList(AllZone.Human_Play.getCards());
		creature = creature.filter(new CardListFilter() {
			public boolean addCard(Card c) {
				return c.isCreature() && (c.getDefense() <= toughness);
			}
		});
		return creature;
	}// AI_getHumanCreature()

	public static CommandArgs AI_targetHumanCreatureOrPlayer() {
		return new CommandArgs() {
			public void execute(Object o) {
				SpellAbility sa = (SpellAbility) o;

				CardList creature = new CardList(AllZone.Human_Play.getCards());
				creature = creature.getType("Creature");
				Card c = getRandomCard(creature);

				if ((c == null) || random.nextBoolean()) {
					sa.setTargetPlayer(Constant.Player.Human);
				} else {
					sa.setTargetCard(c);
				}
			}
		};// CommandArgs
	}// human_creatureOrPlayer()

	public static CommandArgs AI_targetHuman() {
		return new CommandArgs() {
			public void execute(Object o) {
				SpellAbility sa = (SpellAbility) o;
				sa.setTargetPlayer(Constant.Player.Human);
			}
		};
	}// targetHuman()

	// is it the computer's main phase before attacking?
	public static boolean AI_isMainPhase() {
		return AllZone.Phase.getPhase().equals(Constant.Phase.Main1)
				&& AllZone.Phase.getActivePlayer().equals(
						Constant.Player.Computer);
	}

	public static CommandArgs AI_targetComputer() {
		return new CommandArgs() {
			public void execute(Object o) {
				SpellAbility sa = (SpellAbility) o;
				sa.setTargetPlayer(Constant.Player.Computer);
			}
		};
	}// targetComputer()

	// type can also be "All"
	public static CommandArgs AI_targetType(final String type,
			final PlayerZone zone) {
		return new CommandArgs() {
			public void execute(Object o) {
				CardList filter = new CardList(zone.getCards());

				if (!type.equals("All"))
					filter = filter.getType(type);

				Card c = getRandomCard(filter);
				if (c != null) {
					SpellAbility sa = (SpellAbility) o;
					sa.setTargetCard(c);

					// doesn't work for some reason
					// if(shouldAttack && CombatUtil.canAttack(c))
					// AllZone.Combat.addAttacker(c);
				}
			}// execute()
		};
	}// targetInPlay()

	// may return null
	static public Card getRandomCard(CardList list) {
		if (list.size() == 0)
			return null;

		int index = random.nextInt(list.size());
		return list.get(index);
	}

	// may return null
	static public Card getRandomCard(PlayerZone zone) {
		return getRandomCard(new CardList(zone.getCards()));
	}
}