import java.util.*;


public class CardFactory
{
  //String cardname is the key, Card is the value
  private Map map = new HashMap();
  private Random random = new Random();

  public CardFactory(String filename)
  {
    try{
     readCards(filename);
    }catch(Exception ex)
    {
      Gui_NewGame.error(ex.getMessage());
    }
  }
  public CardList getAllCards()
  {
    Iterator it = map.keySet().iterator();
    Card c;
    CardList all = new CardList();
    while(it.hasNext())
    {
      c = getCard(it.next().toString(), Constant.Player.Human);
      all.add(c);
    }

    return all;
  }//getAllCards()

  private void readCards(String filename)
  {
    map.clear();

    ReadCard read = new ReadCard(Constant.IO.cardFile);
    try
    {
      read.run();
//      javax.swing.SwingUtilities.invokeAndWait(read);
      }catch(Exception ex)
      {
        ex.printStackTrace();
        throw new RuntimeException("CardFactory : readCards() thread error - " +ex.getMessage());
      }

      ArrayList simpleList = read.getCards();
      Card s;
      Iterator it = simpleList.iterator();
      while(it.hasNext())
      {
        s = (Card)it.next();
        map.put(s.getName(), s);
      }
  }//readCard()
  public Card copyCard(Card in)
  {
    Card out = getCard(in.getName(),  in.getOwner());
    out.setUniqueNumber(in.getUniqueNumber());
    return out;
  }

  public Card getCard(String cardName, String owner)
  {
    cardName = AllZone.NameChanger.getOriginalName(cardName);
    return getCard2(cardName, owner);
  }



  private Card getCard2(final String cardName, final String owner)
  {
    //o should be Card object
    Object o = map.get(cardName);
    if(o == null)
      throw new RuntimeException("CardFactory : getCard() invalid card name - " +cardName);

    final Card card = copyStats(o);
    card.setOwner(owner);
    card.setController(owner);
    //may have to change the spell
    //this is so permanents like creatures and artifacts have a "default" spell
    if(! card.isLand())
      card.addSpellAbility(new Spell_Permanent(card));

    //contributed code
    //*************** START *********** START **************************
    if(cardName.equals("Dark Banishing"))
    {
      final SpellAbility spell = new Spell(card)
      {
        Card check;

        public boolean canPlayAI()
        {
          check = getNonBlackCreature();
          return check != null;
        }

        public void chooseTargetAI()
        {
          Card c = getNonBlackCreature();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        Card getNonBlackCreature()
        {
          int maxAttack = 0;
          Card bestCard = null;
          CardList nonBlackCards = CardFactoryUtil.AI_getHumanCreature();

          for(int i = 0; i < nonBlackCards.size(); i++)
            if(!CardUtil.getColors(nonBlackCards.get(i)).contains(Constant.Color.Black))
              if(nonBlackCards.get (i).getAttack() > maxAttack)
          {
            maxAttack = nonBlackCards.get(i).getAttack();
            bestCard = nonBlackCards.get(i);
          }

          return bestCard;
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            AllZone.GameAction.destroyNoRegeneration(getTargetCard());
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      //target
      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target non-black creature for " +spell.getSourceCard());
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if((!CardUtil.getColors(card).contains(Constant.Color.Black))
             && card.isCreature()
             && zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }
      };//SpellAbility - target

      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Filthy Cur"))
    {
      final Card newCard = new Card()
      {
        public void addDamage(final int n)
        {
          super.addDamage(n);
          SpellAbility ability = new Ability(card, "0")
          {
            public void resolve()
            {
              AllZone.GameAction.getPlayerLife(getController()).subtractLife(n);
            }
          };
          ability.setStackDescription("Filthy Cur - causes " +n +" damage to " +getController());
          AllZone.Stack.add(ability);
        }//addDamage()
      };//Card

      newCard.setOwner(card.getOwner());
      newCard.setController(card.getController());

      newCard.setManaCost(card.getManaCost());
      newCard.setName(card.getName());
      newCard.addType("Creature");
      newCard.addType("Hound");
      newCard.setText(card.getSpellText());
      newCard.setAttack(card.getAttack());
      newCard.setDefense(card.getDefense());

      newCard.addSpellAbility(new Spell_Permanent(newCard));

      return newCard;
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Shinka Gatekeeper"))
    {
      final Card newCard = new Card()
      {
        public void addDamage(final int n)
        {
          super.addDamage(n);
          SpellAbility ability = new Ability(card, "0")
          {
            public void resolve()
            {
              AllZone.GameAction.getPlayerLife(getController()).subtractLife(n);
            }
          };
          ability.setStackDescription("Shinka Gatekeeper - causes " +n +" damage to " +getController());
          AllZone.Stack.add(ability);
        }//addDamage()
      };//Card

      newCard.setOwner(card.getOwner());
      newCard.setController(card.getController());

      newCard.setManaCost(card.getManaCost());
      newCard.setName(card.getName());
      newCard.addType("Creature");
      newCard.addType("Ogre");
      newCard.addType("Warrior");
      newCard.setText(card.getSpellText());
      newCard.setAttack(card.getAttack());
      newCard.setDefense(card.getDefense());

      newCard.addSpellAbility(new Spell_Permanent(newCard));

      return newCard;
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Jackal Pup"))
    {
      final Card newCard = new Card()
      {
        public void addDamage(final int n)
        {
          super.addDamage(n);
          SpellAbility ability = new Ability(card, "0")
          {
            public void resolve()
            {
              AllZone.GameAction.getPlayerLife(getController()).subtractLife(n);
            }
          };
          ability.setStackDescription("Jackal Pup - causes " +n +" damage to " +getController());
          AllZone.Stack.add(ability);
        }//addDamage()
      };//Card

      newCard.setOwner(card.getOwner());
      newCard.setController(card.getController());

      newCard.setManaCost(card.getManaCost());
      newCard.setName(card.getName());
      newCard.addType("Creature");
      newCard.addType("Hound");
      newCard.setText(card.getSpellText());
      newCard.setAttack(card.getAttack());
      newCard.setDefense(card.getDefense());

      newCard.addSpellAbility(new Spell_Permanent(newCard));

      return newCard;
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Stuffy Doll"))
    {
      final Card newCard = new Card()
      {
        public void addDamage(final int n)
        {
          super.addDamage(n);
          final String opponent = AllZone.GameAction.getOpponent(owner);

          SpellAbility ability = new Ability(card, "0")
          {
            public void resolve()
            {
              AllZone.GameAction.getPlayerLife(opponent).subtractLife(n);
            }
          };
          ability.setStackDescription("Stuffy Doll - causes " +n +" damage to " +opponent);
          AllZone.Stack.add(ability);
        }//addDamage()
      };//Card

      newCard.setOwner(card.getOwner());
      newCard.setController(card.getController());

      newCard.setManaCost(card.getManaCost());
      newCard.setName(card.getName());
      newCard.addType("Artifact");
      newCard.addType("Creature");
      newCard.addType("Construct");
      newCard.setText("Whenever damage is dealt to Stuffy Doll, it deals that much damage to your opponent.");
      newCard.setAttack(0);
      newCard.setDefense(1);

      newCard.addKeyword("Indestructible");

      final Ability_Tap ability = new Ability_Tap(newCard)
      {
        public void resolve()
        {
          newCard.addDamage(1);
        }
      };//SpellAbility
      ability.setDescription("tap: Stuffy Doll deals 1 damage to itself.");
      ability.setStackDescription("Stuffy Doll - deals 1 damage to itself.");
      ability.setBeforePayMana(new Input_NoCost_TapAbility(ability));

//      card.addSpellAbility(ability);
//      return card;
///*
      newCard.addSpellAbility(new Spell_Permanent(newCard));
      newCard.addSpellAbility(ability);

      return newCard;
//*/
    }//*************** END ************ END **************************


//    computer plays 2 land of these type instead of just 1 per turn

    //*************** START *********** START **************************
    //Ravinca Duel Lands
    if(cardName.equals("Blood Crypt") || cardName.equals("Breeding Pool") || cardName.equals("Godless Shrine") || cardName.equals("Hallowed Fountain") || cardName.equals("Overgrown Tomb") || cardName.equals("Sacred Foundry") || cardName.equals("Steam Vents") || cardName.equals("Stomping Ground") || cardName.equals("Temple Garden") || cardName.equals("Watery Grave"))
    {
      //if this isn't don't, computer plays more than 1 copy
      card.clearSpellAbility();

      card.setComesIntoPlay(new Command()
      {
        public void execute()
        {
          if(card.getController().equals(Constant.Player.Human))
            humanExecute();
          else
            computerExecute();
        }
        public void computerExecute()
        {
          boolean pay = false;

          if(AllZone.Computer_Life.getLife() > 9)
            pay = MyRandom.random.nextBoolean();

          if(pay)
            AllZone.Computer_Life.subtractLife(2);
          else
            card.tap();
        }
        public void humanExecute()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          if(2 < life.getLife())
          {
            String[] choices = {"Yes", "No"};
            Object o = AllZone.Display.getChoice("Pay 2 life?", choices);
            if(o.equals("Yes"))
              life.subtractLife(2);
            else
              tapCard();
          }//if
          else
            tapCard();
        }//execute()
        private void tapCard()
        {
          card.tap();
        }
      });
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Faerie Conclave"))
    {
      card.setComesIntoPlay(new Command()
      {
        public void execute()
        {
          card.tap();
        }
      });

      final Command eot1 = new Command()
      {
        public void execute()
        {
          Card c = card;

          c.setAttack(0);
          c.setDefense(0);
          c.removeKeyword("Flying");
          c.removeType("Creature");
          c.removeType("Faerie");
        }
      };

      final SpellAbility a1 = new Ability(card, "1 U")
      {
        public boolean canPlayAI()
        {
          return false;
        }
        public void resolve()
        {
          Card c = card;

          c.setAttack(2);
          c.setDefense(1);

          //to prevent like duplication like "Flying Flying Creature Creature"
          if(! c.getKeyword().contains("Flying"))
          {
            c.addKeyword("Flying");
            c.addType("Creature");
            c.addType("Faerie");
          }
          AllZone.EndOfTurn.addUntil(eot1);
        }
      };//SpellAbility
      card.setManaCost("U");

      card.clearSpellAbility();
      card.addSpellAbility(a1);
      a1.setDescription("1U: Faerie Conclave becomes a 2/1 blue Faerie creature with flying until end of turn. It's still a land.");
      a1.setStackDescription(card +" becomes a 2/1 creature with flying until EOT");

      Command paid1 = new Command() {public void execute() {AllZone.Stack.add(a1);}};

      a1.setBeforePayMana(new Input_PayManaCost_Ability(a1.getManaCost(), paid1));

    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Forbidding Watchtower"))
    {
      card.setComesIntoPlay(new Command()
      {
        public void execute()
        {
          card.tap();
        }
      });

      final Command eot1 = new Command()
      {
        public void execute()
        {
          Card c = card;

          c.setAttack(0);
          c.setDefense(0);
          c.removeType("Creature");
          c.removeType("Soldier");
        }
      };

      final SpellAbility a1 = new Ability(card, "1 W")
      {
        public boolean canPlayAI()
        {
          return false;
        }
        public void resolve()
        {
          Card c = card;

          c.setAttack(1);
          c.setDefense(5);

          //to prevent like duplication like "Creature Creature"
          if(! c.getKeyword().contains("Creature"))
          {
            c.addType("Creature");
            c.addType("Soldier");
          }
          AllZone.EndOfTurn.addUntil(eot1);
        }
      };//SpellAbility

      card.clearSpellAbility();
      card.addSpellAbility(a1);
      a1.setStackDescription(card +" becomes a 1/5 creature until EOT");

      Command paid1 = new Command() {public void execute() {AllZone.Stack.add(a1);}};

      a1.setBeforePayMana(new Input_PayManaCost_Ability(a1.getManaCost(), paid1));

    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Forbidding Watchtower"))
    {
      card.setComesIntoPlay(new Command()
      {
        public void execute()
        {
          card.tap();
        }
      });

      final Command eot1 = new Command()
      {
        public void execute()
        {
          Card c = card;

          c.setAttack(0);
          c.setDefense(0);
          c.removeType("Creature");
          c.removeType("Soldier");
        }
      };

      final SpellAbility a1 = new Ability(card, "1 W")
      {
        public boolean canPlayAI()
        {
          return false;
        }
        public void resolve()
        {
          Card c = card;

          c.setAttack(1);
          c.setDefense(5);

          //to prevent like duplication like "Creature Creature"
          if(! c.getType().contains("Creature"))
          {
            c.addType("Creature");
            c.addType("Soldier");
          }
          AllZone.EndOfTurn.addUntil(eot1);
        }
      };//SpellAbility

      card.clearSpellAbility();
      card.addSpellAbility(a1);
      a1.setStackDescription(card +" becomes a 1/5 creature until EOT");

      Command paid1 = new Command() {public void execute() {AllZone.Stack.add(a1);}};

      a1.setBeforePayMana(new Input_PayManaCost_Ability(a1.getManaCost(), paid1));

    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Treetop Village"))
    {
      card.setComesIntoPlay(new Command()
      {
        public void execute()
        {
          card.tap();
        }
      });

      final Command eot1 = new Command()
      {
        public void execute()
        {
          Card c = card;

          c.setAttack(0);
          c.setDefense(0);
          c.removeType("Creature");
          c.removeType("Ape");
          c.removeKeyword("Trample");
        }
      };

      final SpellAbility a1 = new Ability(card, "1 G")
      {
        public boolean canPlayAI()
        {
          return ! card.getType().contains("Creature");
        }
        public void resolve()
        {
          Card c = card;

          c.setAttack(3);
          c.setDefense(3);

          //to prevent like duplication like "Creature Creature"
          if(! c.getKeyword().contains("Trample"))
          {
            c.addType("Creature");
            c.addType("Ape");
            c.addKeyword("Trample");
          }
          AllZone.EndOfTurn.addUntil(eot1);
        }
      };//SpellAbility

      card.clearSpellAbility();
      card.addSpellAbility(a1);
      a1.setStackDescription(card +" becomes a 3/3 creature with trample until EOT");

      Command paid1 = new Command() {public void execute() {AllZone.Stack.add(a1);}};

      a1.setBeforePayMana(new Input_PayManaCost_Ability(a1.getManaCost(), paid1));

    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Serra Avenger"))
    {
      SpellAbility spell = new Spell_Permanent(card)
      {
        public boolean canPlay()
        {
          return super.canPlay() && 6 < AllZone.Phase.getTurn();
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }
    //*************** END ************ END **************************


    //*************** START *********** START **************************
    if(cardName.equals("Force of Savagery"))
    {
      SpellAbility spell = new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          CardList list = new CardList(AllZone.Computer_Play.getCards());

          return list.containsName("Glorious Anthem") || list.containsName("Gaea's Anthem");
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }
    //*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Furnace Whelp"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            card.setAttack(card.getAttack() - 1);
        }
      };

      SpellAbility ability = new Ability(card, "R")
      {
        public boolean canPlayAI() {return CardFactoryUtil.AI_doesCreatureAttack(card);}
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(card.getAttack() + 1);
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }
      };
      ability.setDescription("R: Furnance Whelp gets +1/+0 until end of turn.");
      ability.setStackDescription(card +" gets +1/+0 until end of turn.");
      card.addSpellAbility(ability);
    }
    //*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Pyrohemia"))
    {
      SpellAbility ability = new Ability(card, "R")
      {
        public boolean canPlayAI()
        {
          CardList human    = new CardList(AllZone.Human_Play.getCards());
          CardList computer = new CardList(AllZone.Computer_Play.getCards());

          human = human.getType("Creature");
          computer = computer.getType("Creature");

          return AllZone.Computer_Life.getLife() > 2 && !(human.size() == 0 && 0 < computer.size());
        }
        public void resolve()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          for(int i = 0; i < list.size(); i++)
            list.get(i).addDamage(1);

          AllZone.Human_Life.subtractLife(1);
          AllZone.Computer_Life.subtractLife(1);
        }//resolve()
      };//SpellAbility
      ability.setDescription("R: Pyrohemia deals 1 damage to each creature and each player.");
      ability.setStackDescription(card +" deals 1 damage to each creature and each player.");

      card.clearSpellAbility();
     card.addSpellAbility(new Spell_Permanent(card)
     {
       public boolean canPlayAI()
       {
         //get all creatures
         CardList list = new CardList();
         list.addAll(AllZone.Human_Play.getCards());
         list.addAll(AllZone.Computer_Play.getCards());
         list = list.getType("Creature");

         return 0 < list.size();
       }
      });

      card.addSpellAbility(ability);
    }
    //*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Pestilence"))
    {
      SpellAbility ability = new Ability(card, "B")
      {
        public boolean canPlayAI()
        {
          CardList human    = new CardList(AllZone.Human_Play.getCards());
          CardList computer = new CardList(AllZone.Computer_Play.getCards());

          human = human.getType("Creature");
          computer = computer.getType("Creature");

          return AllZone.Computer_Life.getLife() > 2 && !(human.size() == 0 && 0 < computer.size());
        }
        public void resolve()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          for(int i = 0; i < list.size(); i++)
            list.get(i).addDamage(1);

          AllZone.Human_Life.subtractLife(1);
          AllZone.Computer_Life.subtractLife(1);
        }//resolve()
      };//SpellAbility
      ability.setDescription("B: Pestilence deals 1 damage to each creature and each player.");
      ability.setStackDescription(card +" deals 1 damage to each creature and each player.");

      card.clearSpellAbility();
      card.addSpellAbility(new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          return 0 < list.size();
        }
      });

      card.addSpellAbility(ability);
    }
    //*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Greater Forgeling"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(3);
            card.setDefense(4);
          }
        }
      };

      SpellAbility ability = new Ability(card, "1 R")
      {
        public boolean canPlayAI()
        {
          return MyRandom.random.nextBoolean() && CardFactoryUtil.AI_doesCreatureAttack(card) &&
              3 < card.getDefense();
        }
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(card.getAttack() + 3);
            card.setDefense(card.getDefense() - 3);
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }
      };

      ability.setDescription("1 R: Greater Forgeling gets +3/-3 until end of turn.");
      ability.setStackDescription(card +" gets +3/-3 until end of turn.");
      card.addSpellAbility(ability);
    }
    //*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Akroma, Angel of Fury"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            card.setAttack(card.getAttack() - 1);
        }
      };

      SpellAbility ability = new Ability(card, "R")
      {
        public boolean canPlayAI() {return CardFactoryUtil.AI_doesCreatureAttack(card);}
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(card.getAttack() + 1);
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }
      };
      ability.setDescription("R: Akroma, Angel of Fury gets +1/+0 until end of turn.");
      ability.setStackDescription(card +" gets +1/+0 until end of turn.");
      card.addSpellAbility(ability);
    }
    //*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Shivan Dragon"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            card.setAttack(card.getAttack() - 1);
        }
      };

      SpellAbility ability = new Ability(card, "R")
      {
        public boolean canPlayAI() {return CardFactoryUtil.AI_doesCreatureAttack(card);}
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(card.getAttack() + 1);
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }
      };
      ability.setDescription("R: Shivan Dragon gets +1/+0 until end of turn.");
      ability.setStackDescription(card +" gets +1/+0 until end of turn.");
      card.addSpellAbility(ability);
    }
    //*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Furnace Spirit"))
    {
      final Command untilEOT = new Command()
      {
        public boolean canPlayAI() {return false; /*return CardFactoryUtil.AI_doesCreatureAttack(card);*/}
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            card.setAttack(card.getAttack() - 1);
        }
      };

      SpellAbility ability = new Ability(card, "R")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(card.getAttack() + 1);
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }
      };
      ability.setDescription("R: Furnace Spirit gets +1/+0 until end of turn.");
      ability.setStackDescription(card +" gets +1/+0 until end of turn.");
      card.addSpellAbility(ability);
    }
    //*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Liliana Vess"))
    {
      //computer only plays ability 1 and 3, discard and return creature from graveyard to play
      final int turn[] = new int[1];
      turn[0] = -1;

      final Card card2 = new Card()
      {
        public void addDamage(int n)
        {
          subtractCounter(n);
          AllZone.GameAction.checkStateEffects();
        }
        public String getText()
        {
          return super.getText() + "\r\nLoyality counters " +getCounters();
        }
      };
      card2.addCounter(5);

      card2.setOwner(owner);
      card2.setController(owner);

      card2.setName(card.getName());
      card2.setType(card.getType());
      card2.setManaCost(card.getManaCost());
      card2.addSpellAbility(new Spell_Permanent(card2));

      //ability2
      final SpellAbility ability2 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.subtractCounter(2);
          turn[0] = AllZone.Phase.getTurn();

          String player = card2.getController();
          if(player.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void computerResolve()
        {
          CardList creature = new CardList(AllZone.Computer_Library.getCards());
          creature = creature.getType("Creature");
          if(creature.size() != 0)
          {
            Card c = creature.get(0);
            AllZone.GameAction.shuffle(card2.getController());

            //move to top of library
            AllZone.Computer_Library.remove(c);
            AllZone.Computer_Library.add(c, 0);
          }
        }//computerResolve()
        public void humanResolve()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card2.getController());

          CardList list = new CardList(library.getCards());

          if(list.size() != 0)
          {
            Object o = AllZone.Display.getChoiceOptional("Select any card", list.toArray());

            AllZone.GameAction.shuffle(card2.getController());
            if(o != null)
            {
              //put creature on top of library
              library.remove(o);
              library.add((Card)o, 0);
            }
          }//if
        }//resolve()
        public boolean canPlayAI()
        {
          return false;
        }

        public boolean canPlay()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card2.getController());

          return 2 <= card2.getCounters()                           &&
                      AllZone.getZone(card2).is(Constant.Zone.Play) &&
                      1 < library.size()                            &&
                      turn[0] != AllZone.Phase.getTurn();
        }//canPlay()
      };//SpellAbility ability2

      ability2.setBeforePayMana(new Input()
      {
        int check = -1;
         public void showMessage()
         {
           if(check != AllZone.Phase.getTurn())
           {
             check = AllZone.Phase.getTurn();
             turn[0] = AllZone.Phase.getTurn();
             AllZone.Stack.push(ability2);
           }
           stop();
         }//showMessage()
      });

      //ability3
      final SpellAbility ability3 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.subtractCounter(8);
          turn[0] = AllZone.Phase.getTurn();

          //get all graveyard creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());
          list = list.getType("Creature");

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card2.getController());
          PlayerZone grave = null;
          Card c = null;
          for(int i = 0; i < list.size(); i++)
          {
            //this is a rough hack, but no one will ever see this code anyways, lol ;+)
            c = list.get(i);
            c.setController(card.getController());

            grave = AllZone.getZone(c);
            if(grave != null)
              grave.remove(c);

            play.add(c);
          }
        }
        public boolean canPlay()
        {
          return 8 <= card2.getCounters() && AllZone.getZone(card2).is(Constant.Zone.Play) &&
              turn[0] != AllZone.Phase.getTurn();
        }//canPlay()
        public boolean canPlayAI()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());
          list = list.getType("Creature");

          return 3 < list.size();
        }
      };
      ability3.setBeforePayMana(new Input()
      {
        int check = -1;
         public void showMessage()
         {
           if(check != AllZone.Phase.getTurn())
           {
             check = AllZone.Phase.getTurn();
             turn[0] = AllZone.Phase.getTurn();
             AllZone.Stack.push(ability3);
           }
           stop();
         }//showMessage()
      });

      //ability 1
      final SpellAbility ability1 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.addCounter(1);
          turn[0] = AllZone.Phase.getTurn();

          String s = getTargetPlayer();
          setStackDescription("Liliana Vess - " +s +" discards a card");

          if(s.equals(Constant.Player.Human))
            AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          else
            AllZone.GameAction.discardRandom(Constant.Player.Computer);
        }
        public boolean canPlayAI()
        {
          if(ability3.canPlay() && ability3.canPlayAI())
            return false;
          else
          {
            setTargetPlayer(Constant.Player.Human);
            return true;
          }
        }
        public boolean canPlay()
        {
          return 0 < card2.getCounters() && AllZone.getZone(card2).is(Constant.Zone.Play) &&
              turn[0] != AllZone.Phase.getTurn();
        }//canPlay()
      };//SpellAbility ability1

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target player");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectPlayer(String player)
        {
          turn[0] = AllZone.Phase.getTurn();
          ability1.setTargetPlayer(player);
          AllZone.Stack.add(ability1);
          stop();
        }
      };//Input target
      ability1.setBeforePayMana(target);
      ability1.setDescription("+1: Target player discards a card.");
      card2.addSpellAbility(ability1);

      ability2.setDescription("-2: Search your library for a card, then shuffle your library and put that card on top of it.");
      ability2.setStackDescription("Liliana Vess - Search your library for a card, then shuffle your library and put that card on top of it.");
      card2.addSpellAbility(ability2);

      ability3.setDescription("-8: Put all creature cards in all graveyards into play under your control.");
      ability3.setStackDescription("Liliana Vess - Put all creature cards in all graveyards into play under your control.");
      card2.addSpellAbility(ability3);

      return card2;
    }
    //*************** END ************ END **************************
    
    
    
  //*************** START *********** START **************************
    if(cardName.equals("Ajani Goldmane"))
    {
      //computer only plays ability 1 and 3, gain life and put X\X token into play
      final int turn[] = new int[1];
      turn[0] = -1;

      final Card card2 = new Card()
      {
        public void addDamage(int n)
        {
          subtractCounter(n);
          AllZone.GameAction.checkStateEffects();
        }
        public String getText()
        {
          return super.getText() + "\r\nLoyality counters " +getCounters();
        }
      };
      card2.addCounter(4);

      card2.setOwner(owner);
      card2.setController(owner);

      card2.setName(card.getName());
      card2.setType(card.getType());
      card2.setManaCost(card.getManaCost());
      card2.addSpellAbility(new Spell_Permanent(card2));

      //ability2: all controller's creatures get +1\+1 and vigilance until EOT
      final SpellAbility ability2 = new Ability(card2, "0")
      {
    	  final Command untilEOT = new Command()
          {
            public void execute()
            {
                String player = card2.getController();
                CardList creatures;
                if(player.equals(Constant.Player.Human)) {
                	creatures = new CardList(AllZone.Human_Play.getCards());
                } else {
                	creatures = new CardList(AllZone.Computer_Play.getCards());
                }
                
                for (int i = 0; i < creatures.size(); i++) {
              	  Card card = creatures.get(i);
              	  card.setAttack(card.getAttack() - 1);
              	  card.setDefense(card.getDefense() - 1);
              	  card.removeKeyword("Vigilance");
                }
            }
          };
          
        public void resolve()
        {
          card2.subtractCounter(2);
          turn[0] = AllZone.Phase.getTurn();

          String player = card2.getController();
          CardList creatures;
          if(player.equals(Constant.Player.Human)) {
        	creatures = new CardList(AllZone.Human_Play.getCards());
          } else {
        	creatures = new CardList(AllZone.Computer_Play.getCards());
          }
          
          for (int i = 0; i < creatures.size(); i++) {
        	  Card card = creatures.get(i);
        	  card.setAttack(card.getAttack() + 1);
        	  card.setDefense(card.getDefense()+ 1);
        	  card.addKeyword("Vigilance");
          }

          AllZone.EndOfTurn.addUntil(untilEOT);
        }

        public boolean canPlayAI()
        {
          return false;
        }

        public boolean canPlay()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card2.getController());

          return 0 < card2.getCounters() &&
          			AllZone.getZone(card2).is(Constant.Zone.Play) &&
                      turn[0] != AllZone.Phase.getTurn();
        }//canPlay()
      };//SpellAbility ability2

      ability2.setBeforePayMana(new Input()
      {
        int check = -1;
         public void showMessage()
         {
           if(check != AllZone.Phase.getTurn())
           {
             check = AllZone.Phase.getTurn();
             turn[0] = AllZone.Phase.getTurn();
             AllZone.Stack.push(ability2);
           }
           stop();
         }//showMessage()
      });

      //ability3
      final SpellAbility ability3 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.subtractCounter(6);
          turn[0] = AllZone.Phase.getTurn();
          
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());

          //Create token
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("W");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Avatar");
          c.setAttack(AllZone.GameAction.getPlayerLife(card.getController()).getLife());
          c.setDefense(AllZone.GameAction.getPlayerLife(card.getController()).getLife());

          c.addKeyword("This creature's power and toughness are each equal to your life total.");
          
          play.add(c);
        }
        public boolean canPlay()
        {
          return 6 <= card2.getCounters() && AllZone.getZone(card2).is(Constant.Zone.Play) &&
              turn[0] != AllZone.Phase.getTurn();
        }//canPlay()
        public boolean canPlayAI()
        {
        	// may be it's better to put only if you have less than 5 life
        	return true;
        }
      };
      ability3.setBeforePayMana(new Input()
      {
        int check = -1;
         public void showMessage()
         {
           if(check != AllZone.Phase.getTurn())
           {
             check = AllZone.Phase.getTurn();
             turn[0] = AllZone.Phase.getTurn();
             AllZone.Stack.push(ability3);
           }
           stop();
         }//showMessage()
      });

      //ability 1: gain 2 life
      final SpellAbility ability1 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.addCounter(1);
          turn[0] = AllZone.Phase.getTurn();

          AllZone.GameAction.getPlayerLife(card.getController()).addLife(2);
          
        }
        public boolean canPlayAI()
        {
          if(ability3.canPlay() && ability3.canPlayAI()) {
            return false;
          } else
          {
            return true;
          }
        }
        public boolean canPlay()
        {
          return 0 < card2.getCounters() && AllZone.getZone(card2).is(Constant.Zone.Play) &&
              turn[0] != AllZone.Phase.getTurn();
        }//canPlay()
      };//SpellAbility ability1
      
      ability1.setBeforePayMana(new Input()
      {
        int check = -1;
         public void showMessage()
         {
           if(check != AllZone.Phase.getTurn())
           {
             check = AllZone.Phase.getTurn();
             turn[0] = AllZone.Phase.getTurn();
             AllZone.Stack.push(ability1);
           }
           stop();
         }//showMessage()
      });
      
      ability1.setDescription("+1: You gain 2 life.");
      ability1.setStackDescription("Ajani Goldmane - Human gains 2 life");
      card2.addSpellAbility(ability1);

      ability2.setDescription("-1: Put a +1/+1 counter on each creature you control. Those creatures gain vigilance until end of turn.");
      ability2.setStackDescription("Ajani Goldmane - Put a +1/+1 counter on each creature you control. They get vigilance.");
      card2.addSpellAbility(ability2);

      ability3.setDescription("-6: Put a white Avatar creature token into play with \"This creature's power and toughness are each equal to your life total.\"");
      ability3.setStackDescription("Ajani Goldmane - Put X\\X white Avatar creature token into play.");
      card2.addSpellAbility(ability3);

      return card2;
    }
    //*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Chandra Nalaar"))
    {
      //computer only plays ability 1 and 3, discard and return creature from graveyard to play
      final int turn[] = new int[1];
      turn[0] = -1;

      final Card card2 = new Card()
      {
        public void addDamage(int n)
        {
          subtractCounter(n);
          AllZone.GameAction.checkStateEffects();
        }
        public String getText()
        {
          return super.getText() + "\r\nLoyality counters " +getCounters();
        }
      };
      card2.addCounter(6);

      card2.setOwner(owner);
      card2.setController(owner);

      card2.setName(card.getName());
      card2.setType(card.getType());
      card2.setManaCost(card.getManaCost());
      card2.addSpellAbility(new Spell_Permanent(card2));

      //ability 1
      final SpellAbility ability1 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.addCounter(1);
          turn[0] = AllZone.Phase.getTurn();

          PlayerLife life = AllZone.GameAction.getPlayerLife(getTargetPlayer());
          life.subtractLife(1);
        }
        public boolean canPlay()
        {
          return AllZone.getZone(card2).is(Constant.Zone.Play) &&
                 turn[0] != AllZone.Phase.getTurn();
        }
        public boolean canPlayAI()
        {
          setTargetPlayer(Constant.Player.Human);
          setStackDescription("Chandra Nalaar - deals 1 damage to " +Constant.Player.Human);
          return card2.getCounters() < 8;
        }
      };//SpellAbility ability1

      Input target1 = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target player");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectPlayer(String player)
        {
          turn[0] = AllZone.Phase.getTurn();

          ability1.setTargetPlayer(player);
          ability1.setStackDescription("Chandra Nalaar - deals 1 damage to " +player);

          AllZone.Stack.add(ability1);
          stop();
        }
      };//Input target
      ability1.setBeforePayMana(target1);
      ability1.setDescription("+1: Chandra Nalaar deals 1 damage to target player.");
      card2.addSpellAbility(ability1);
      //end ability1




      //ability 2
      final int damage2[] = new int[1];

      final SpellAbility ability2 = new Ability(card2, "0")
      {
        public void resolve()
        {
          turn[0] = AllZone.Phase.getTurn();

          card2.subtractCounter(damage2[0]);
          getTargetCard().addDamage(damage2[0]);

          damage2[0] = 0;
        }//resolve()
        public boolean canPlay()
        {
          return AllZone.getZone(card2).is(Constant.Zone.Play) &&
                 turn[0] != AllZone.Phase.getTurn();
        }
        public boolean canPlayAI()
        {
          return false;
        }
      };//SpellAbility ability2

      Input target2 = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target creature");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          if(c.isCreature())
          {
            turn[0] = AllZone.Phase.getTurn();


            damage2[0] = getDamage();

            ability2.setTargetCard(c);
            ability2.setStackDescription("Chandra Nalaar - deals damage to " +c);

            AllZone.Stack.add(ability2);
            stop();
          }
        }//selectCard()
        int getDamage()
        {
          int size = card2.getCounters();
          Object choice[] = new Object[size];

          for(int i = 0; i < choice.length; i++)
            choice[i] = new Integer(i + 1);

           Integer damage = (Integer) AllZone.Display.getChoice("Select X", choice);
           return damage.intValue();
        }
      };//Input target
      ability2.setBeforePayMana(target2);
      ability2.setDescription("-X: Chandra Nalaar deals X damage to target creature.");
      card2.addSpellAbility(ability2);
      //end ability2



      //ability 3
      final SpellAbility ability3 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.subtractCounter(8);
          turn[0] = AllZone.Phase.getTurn();

          PlayerLife life = AllZone.GameAction.getPlayerLife(getTargetPlayer());
          life.subtractLife(10);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, getTargetPlayer());
          CardList list = new CardList(play.getCards());
          list = list.getType("Creature");

          for(int i = 0; i < list.size(); i++)
            list.get(i).addDamage(10);
        }//resolve()
        public boolean canPlay()
        {
          return AllZone.getZone(card2).is(Constant.Zone.Play) &&
                 turn[0] != AllZone.Phase.getTurn()            &&
                 7 < card2.getCounters();
        }
        public boolean canPlayAI()
        {
          setTargetPlayer(Constant.Player.Human);
          setStackDescription("Chandra Nalaar - deals 10 damage to " +Constant.Player.Human +" and each creature he or she controls.");
          return true;
        }
      };//SpellAbility ability3

      Input target3 = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target player");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectPlayer(String player)
        {
          turn[0] = AllZone.Phase.getTurn();

          ability3.setTargetPlayer(player);
          ability3.setStackDescription("Chandra Nalaar - deals 10 damage to " +player +" and each creature he or she controls.");

          AllZone.Stack.add(ability3);
          stop();
        }
      };//Input target
      ability3.setBeforePayMana(target3);
      ability3.setDescription("-8: Chandra Nalaar deals 10 damage to target player and each creature he or she controls.");
      card2.addSpellAbility(ability3);
      //end ability3

      return card2;
    }
    //*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Ashcoat Bear"))
    {
      SpellAbility spell = new Spell_Permanent(card)
      {
        public boolean canPlayAI() {return super.canPlay();}
        public boolean canPlay()
        {
          return true;
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }
    //*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Garruk Wildspeaker"))
    {
      final int turn[] = new int[1];
      turn[0] = -1;

      final Card card2 = new Card()
      {
        public void addDamage(int n)
        {
          subtractCounter(n);
          AllZone.GameAction.checkStateEffects();
        }
        public String getText()
        {
          return super.getText() + "\r\nLoyality counters " +getCounters();
        }
      };
      card2.addCounter(3);

      card2.setOwner(owner);
      card2.setController(owner);

      card2.setName(card.getName());
      card2.setType(card.getType());
      card2.setManaCost(card.getManaCost());
      card2.addSpellAbility(new Spell_Permanent(card2));

      //ability1
      final SpellAbility ability1 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.addCounter(1);

          turn[0] = AllZone.Phase.getTurn();

          //only computer uses the stack
          CardList tapped = new CardList(AllZone.Computer_Play.getCards());
          tapped = tapped.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isLand() && c.isTapped();
            }
          });

          for(int i = 0; i < 2 && i < tapped.size(); i++)
            tapped.get(i).untap();
        }//resolve()
        public boolean canPlayAI()
        {
          return card2.getCounters() < 4 && AllZone.Phase.getPhase().equals(Constant.Phase.Main2);
        }
        public boolean canPlay()
        {
          return  AllZone.getZone(card2).is(Constant.Zone.Play) &&
                  turn[0] != AllZone.Phase.getTurn();
        }//canPlay()
      };
      final Input targetLand = new Input()
      {
        private int count;
        public void showMessage()
        {
          AllZone.Display.showMessage("Select a land to untap");
          ButtonUtil.disableAll();
        }
        public void selectCard(Card c, PlayerZone zone)
        {
          if(c.isLand() && zone.is(Constant.Zone.Play))
          {
            count++;
            c.untap();
          }

          //doesn't use the stack, its just easier this way
          if(count == 2)
          {
            count = 0;
            turn[0] = AllZone.Phase.getTurn();
            card2.addCounter(1);
            stop();
          }
        }//selectCard()
      };//Input

      Input runtime1 = new Input()
      {
        public void showMessage()
        {
          stopSetNext(targetLand);
        }
      };//Input
      ability1.setDescription("+1: Untap two target lands.");
      ability1.setStackDescription("Garruk Wildspeaker - Untap two target lands.");

      ability1.setBeforePayMana(runtime1);
      card2.addSpellAbility(ability1);
      //end ability 1


      //start ability 2
      final SpellAbility ability2 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.subtractCounter(1);
          turn[0] = AllZone.Phase.getTurn();

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card2.getController());
          play.add(getToken());
        }
        Card getToken()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("G");
          c.setToken(true);
          c.addKeyword("Token");

          c.addType("Creature");
          c.addType("Beast");
          c.setAttack(3);
          c.setDefense(3);

          return c;
        }//makeToken()

        public boolean canPlay()
        {
          return  AllZone.getZone(card2).is(Constant.Zone.Play) &&
                  turn[0] != AllZone.Phase.getTurn() &&
                  0 < card2.getCounters();
        }//canPlay()
        public boolean canPlayAI()
        {
          CardList c = new CardList(AllZone.Computer_Play.getCards());
          c = c.getType("Creature");
          return c.size() < 4;
        }
      };//SpellAbility ability 2
      Input runtime2 = new Input()
      {
        int check = -1;
        public void showMessage()
        {
          if(check != AllZone.Phase.getTurn())
          {
            check = AllZone.Phase.getTurn();
            turn[0] = AllZone.Phase.getTurn();

            AllZone.Stack.push(ability2);
            stop();
          }
        }
      };//Input
      ability2.setStackDescription(card2.getName() +" -  Put a 3/3 green Beast creature token into play.");
      ability2.setDescription("-1: Put a 3/3 green Beast creature token into play.");
      ability2.setBeforePayMana(runtime2);
      card2.addSpellAbility(ability2);
      //end ability 2


      //start ability 3
      final SpellAbility ability3 = new Ability(card2, "0")
      {
        public void resolve()
        {
          card2.subtractCounter(4);
          turn[0] = AllZone.Phase.getTurn();

          final int boost = 3;
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card2.getController());
          CardList list = new CardList(play.getCards());
          Card c;

          for(int i = 0; i < list.size(); i++)
          {
            final Card[] target = new Card[1];
            target[0] = list.get(i);

            final Command untilEOT = new Command()
            {
              public void execute()
              {
                if(AllZone.GameAction.isCardInPlay(target[0]))
                {
                  target[0].setAttack (target[0].getAttack() - boost);
                  target[0].setDefense(target[0].getDefense()- boost);

                  target[0].removeKeyword("Trample");
                }
              }
            };//Command

            if(AllZone.GameAction.isCardInPlay(target[0]))
            {
              target[0].setAttack (target[0].getAttack() + boost);
              target[0].setDefense(target[0].getDefense()+ boost);

              target[0].addKeyword("Trample");

              AllZone.EndOfTurn.addUntil(untilEOT);
            }//if
          }//for

        }//resolve()
        public boolean canPlay()
        {
          return  AllZone.getZone(card2).is(Constant.Zone.Play) &&
                  turn[0] != AllZone.Phase.getTurn() &&
                  3 < card2.getCounters();
        }//canPlay()
        public boolean canPlayAI()
        {
          CardList c = new CardList(AllZone.Computer_Play.getCards());
          c = c.getType("Creature");
          return c.size() >= 4 && AllZone.Phase.getPhase().equals(Constant.Phase.Main1);
        }
      };//SpellAbility ability3
      Input runtime3 = new Input()
      {
        int check = -1;
        public void showMessage()
        {
          if(check != AllZone.Phase.getTurn())
          {
            check = AllZone.Phase.getTurn();
            turn[0] = AllZone.Phase.getTurn();

            AllZone.Stack.push(ability3);
            stop();
          }
        }
      };//Input
      ability3.setStackDescription(card2.getName() +" -  Creatures you control get +3/+3 and trample until end of turn.");
      ability3.setDescription("-4: Creatures you control get +3/+3 and trample until end of turn.");
      ability3.setBeforePayMana(runtime3);
      card2.addSpellAbility(ability3);
      //end ability 3

      return card2;
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Caller of the Claw"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          int stop = countGraveyard();
          for(int i = 0; i < stop; i++)
            makeToken();
        }//resolve()
        int countGraveyard()
        {
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          CardList list = new CardList(grave.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && (c.getTurnInZone() == AllZone.Phase.getTurn());
            }
          });
          return list.size();
        }//countGraveyard()
        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("G");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Bear");
          c.setAttack(2);
          c.setDefense(2);

          play.add(c);
        }//makeToken()
      };//SpellAbility

      Command comesIntoPlay = new Command()
      {
        public void execute()
        {
          AllZone.Stack.add(ability);
        }
      };//Command
      ability.setStackDescription("Caller of the Claw - Put a 2/2 green Bear creature token into play for each nontoken creature put into your graveyard from play this turn.");
      card.setComesIntoPlay(comesIntoPlay);

      SpellAbility spell = new Spell_Permanent(card)
      {
        public boolean canPlayAI() {return super.canPlay();}
        public boolean canPlay()
        {
          return true;
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Relentless Rats"))
    {
      final Command comesIntoPlay = new Command()
      {
        public void execute()
        {
          ArrayList creature = PlayerZoneUtil.getCardsNamed(AllZone.Computer_Play, "Relentless Rats");
          creature.addAll     (PlayerZoneUtil.getCardsNamed(AllZone.Human_Play   , "Relentless Rats"));

          int n = creature.size() - 1;//minus one for "himself"
          Card c[] = CardUtil.toCard(creature);
          for(int i = 0; i < c.length; i++)
          {
            c[i].setAttack(2 + n);
            c[i].setDefense(2 + n);
          }
        }//execute()
      };//Command
      card.setComesIntoPlay(comesIntoPlay);

      Command destroy = new Command()
      {
        public void execute()
        {
          ArrayList creature = PlayerZoneUtil.getCardsNamed(AllZone.Computer_Play, "Relentless Rats");
          creature.addAll     (PlayerZoneUtil.getCardsNamed(AllZone.Human_Play   , "Relentless Rats"));

          Card c[] = CardUtil.toCard(creature);
          for(int i = 0; i < c.length; i++)
          {
            c[i].setAttack(c[i].getAttack() - 1);
            c[i].setDefense(c[i].getDefense() - 1);
          }
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Kiki-Jiki, Mirror Breaker"))
    {
      final SpellAbility ability = new Ability_Tap(card)
      {
        public boolean canPlayAI() {return getCreature().size() != 0;}
        public void chooseTargetAI()
        {
          setTargetCard(getCreature().get(0));
        }
        CardList getCreature()
        {
          CardList list = new CardList(AllZone.Computer_Play.getCards());
          list.addAll(AllZone.Human_Play.getCards());

          list = list.getType("Creature");
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (!c.getType().contains("Legendary") && (!c.isToken()));
            }
          });
          CardListUtil.sortAttack(list);
          return list;
        }//getCreature()
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            //copy creature and put it into play
            Card copy = getCard(getTargetCard().getName(), card.getController());
            copy.setToken(true);
            copy.addKeyword("Token");
            copy.addKeyword("Haste");

            PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
            play.add(copy);

            //have to do this since getTargetCard() might change
            //if Kiki-Jiki somehow gets untapped again
            final Card[] target = new Card[1];
            target[0] = copy;
            Command atEOT = new Command()
            {
              public void execute()
              {
                //technically your opponent could steal the token
                //and the token shouldn't be sacrificed
                if(AllZone.GameAction.isCardInPlay(target[0]))
                  AllZone.GameAction.sacrifice(target[0]);
              }
            };//Command
            AllZone.EndOfTurn.addAt(atEOT);
          }//is card in play?
        }//resolve()
      };//SpellAbility

      Input runtime = new Input()
      {
        public void showMessage()
        {
          //get all non-legendary creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() &&
                  (!c.getType().contains("Legendary")) &&
                  (!c.isToken());
            }
          });
          stopSetNext(CardFactoryUtil.input_targetSpecific(ability, list, "Select target creature to copy that is not legendary or a token"));
        }
      };//Input
      ability.setStackDescription("Kiki-Jiki - copy card.");
      ability.setDescription("tap: Put a token into play that's a copy of target nonlegendary creature you control. That creature token has haste. Sacrifice it at end of turn.");
      ability.setBeforePayMana(runtime);
      card.addSpellAbility(ability);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Nevinyrral's Disk"))
    {
      SpellAbility summoningSpell = new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          boolean nevinyrralInPlay = false;

          CardList inPlay = new CardList();
          inPlay.addAll(AllZone.Computer_Play.getCards());
          for(int i=0; i<inPlay.size(); ++i)
          {
            if( inPlay.getCard(i).getName().equals("Nevinyrral's Disk"))
            {
              nevinyrralInPlay = true;
            }
          }
          return ! nevinyrralInPlay && (0 < CardFactoryUtil.AI_getHumanCreature().size());
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(summoningSpell);

      card.setComesIntoPlay(new Command()
      {
        public void execute()
        {
          card.tap();
        }
      });
      final SpellAbility ability = new Ability_Tap(card, "1")
      {
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());
          all = filter(all);

          for(int i = 0; i < all.size(); i++)
            AllZone.GameAction.destroy(all.get(i));
        }
        private CardList filter(CardList list)
        {
          return list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isArtifact() || c.isCreature() || c.isEnchantment();
            }
          });
        }//filter()
        public boolean canPlayAI()
        {
          CardList human    = new CardList(AllZone.Human_Play.getCards());
          CardList computer = new CardList(AllZone.Computer_Play.getCards());

          human    = human.getType("Creature");
          computer = computer.getType("Creature");

          //the computer will at least destroy 2 more human creatures
          return computer.size() < human.size()-1 || AllZone.Computer_Life.getLife() < 7;
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("1, tap: Destroy all artifacts, creatures, and enchantments.");
      ability.setStackDescription("Destroy all artifacts, creatures, and enchantments.");
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Magus of the Disk"))
    {
      SpellAbility summoningSpell = new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          boolean nevinyrralInPlay = false;

          CardList inPlay = new CardList();
          inPlay.addAll(AllZone.Computer_Play.getCards());
          for(int i=0; i<inPlay.size(); ++i)
          {
            if( inPlay.getCard(i).getName().equals("Nevinyrral's Disk"))
            {
              nevinyrralInPlay = true;
            }
          }
          return ! nevinyrralInPlay && (0 < CardFactoryUtil.AI_getHumanCreature().size());
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(summoningSpell);

      card.setComesIntoPlay(new Command()
      {
        public void execute()
        {
          card.tap();
        }
      });
      final SpellAbility ability = new Ability_Tap(card, "1")
      {
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());
          all = filter(all);

          for(int i = 0; i < all.size(); i++)
            AllZone.GameAction.destroy(all.get(i));
        }
        private CardList filter(CardList list)
        {
          return list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isArtifact() || c.isCreature() || c.isEnchantment();
            }
          });
        }//filter()
        public boolean canPlayAI()
        {
          CardList human    = new CardList(AllZone.Human_Play.getCards());
          CardList computer = new CardList(AllZone.Computer_Play.getCards());

          human    = human.getType("Creature");
          computer = computer.getType("Creature");

          //the computer will at least destroy 2 more human creatures
          return computer.size() < human.size()-1  || AllZone.Computer_Life.getLife() < 7;
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("1, tap: Destroy all artifacts, creatures, and enchantments.");
      ability.setStackDescription("Destroy all artifacts, creatures, and enchantments.");
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Tanglebloom"))
    {
      final SpellAbility a1 = new Ability_Tap(card, "1")
      {
        public boolean canPlayAI() {return AllZone.Phase.getPhase().equals(Constant.Phase.Main2);}
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(card.getController()).addLife(1);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("1, tap: You gain 1 life.");
      a1.setStackDescription("Tanglebloom - " +card.getController() +" gains 1 life.");

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Wellwisher"))
    {
      final SpellAbility ability = new Ability_Tap(card, "0")
      {
        public boolean canPlay()
        {
          setStackDescription(card.getName() +" - " +card.getController() +" gains " +countElf() +" life.");

          return super.canPlay();
        }
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(card.getController()).addLife(countElf());
        }
        int countElf()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Elf");
          return list.size();
        }
      };//SpellAbility
      ability.setDescription("tap: You gain 1 life for each Elf in play.");
      ability.setBeforePayMana(new Input_NoCost_TapAbility((Ability_Tap) ability));
      card.addSpellAbility(ability);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Horseshoe Crab"))
    {
      final SpellAbility a1 = new Ability(card, "U")
      {
        public boolean canPlayAI()
        {
          return card.isTapped();
        }
        public void resolve()
        {
          card.untap();
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("U: Untap Horseshoe Crab.");
      a1.setStackDescription("Untap Horseshoe Crab");

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Sliver Queen"))
    {
      final SpellAbility a1 = new Ability(card, "2")
      {
        public void resolve()
        {
          makeToken();
        }
        void makeToken()
        {
          Card c = new Card();

          c.setName("Token");

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("1");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Sliver");
          c.setAttack(1);
          c.setDefense(1);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//makeToken()
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("2: Put a 1/1 colorless Sliver creature token into play.");
      a1.setStackDescription("Put a 1/1 colorless Sliver creature token into play.");

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Blizzard Elemental"))
    {
      final SpellAbility a1 = new Ability(card, "3 U")
      {
        public boolean canPlayAI()
        {
          return card.isTapped();
        }
        public void resolve()
        {
          card.untap();
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("3U: Untap Blizzard Elemental.");
      a1.setStackDescription("Untap " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************







    //*************** START *********** START **************************
    if(cardName.equals("Troll Ascetic"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "1 G")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("1 G: Regenerate Troll Ascetic.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************


    //*************** START *********** START **************************
    if(cardName.equals("Silvos, Rogue Elemental"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "G")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("G: Regenerate Silvos, Rogue Elemental.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Carnassid"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "1 G")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("1 G: Regenerate Carnassid.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Korlash, Heir to Blackblade"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "1 B")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("1 B: Regenerate Korlash.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));

      ///////////////////////////////////////////

      final SpellAbility ability = new Ability(card, "0")
      {
        public void chooseTargetAI()
        {
          PlayerZone p = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(p.getCards());
          list = list.getName(cardName);

          AllZone.GameAction.discard(list.get(0));
        }

        public boolean canPlay()
        {
          PlayerZone p = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(p.getCards());
          list = list.getName(cardName);
          return 0 < list.size() && AllZone.getZone(card).getZone().equals(Constant.Zone.Play);
        }

        public void resolve()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());

          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          CardList list = new CardList(library.getCards());
          CardList swamp = list.getType("Swamp");

          for(int i = 0; i < 2 && (! swamp.isEmpty()); i++)
          {
            Card c = swamp.get(0);
            swamp.remove(c);

            library.remove(c);
            play.add(c);
            c.tap();
          }
          GameActionUtil.executeCardStateEffects();
        }
      };
      Input removeCard = new Input()
      {
        int n = 0;
        public void showMessage()
        {
          //this is called twice, this is an ugly hack
          if(n % 2 == 0)
            stop();


          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(hand.getCards());

          list = list.getName(cardName);
          AllZone.GameAction.discard(list.get(0));

          AllZone.Stack.push(ability);
          stop();
        }
      };
      ability.setBeforePayMana(removeCard);

      ability.setDescription("Grandeur - Discard Korlash and put two Swamps from your library into play tapped.");
      ability.setStackDescription(cardName +" - Search for two swamps and put them into play tapped.");

      card.addSpellAbility(ability);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Tarox Bladewing"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          int n = card.getAttack();

          card.setDefense(card.getDefense() - n/2);
          card.setAttack(card.getAttack() - n/2);
        }
      };

      final SpellAbility ability = new Ability(card, "0")
      {
        public void chooseTargetAI()
        {
          PlayerZone p = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(p.getCards());
          list = list.getName(cardName);

          AllZone.GameAction.discard(list.get(0));
        }

        public boolean canPlay()
        {
          PlayerZone p = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(p.getCards());
          list = list.getName(cardName);
          return 0 < list.size() && AllZone.getZone(card).getZone().equals(Constant.Zone.Play);
        }

        public void resolve()
        {
          card.setDefense(card.getDefense() + card.getAttack());
          card.setAttack(card.getAttack() + card.getAttack());

          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };
      Input removeCard = new Input()
      {
        int n = 0;
        public void showMessage()
        {
          //this is called twice, this is an ugly hack
          if(n % 2 == 0)
            stop();


          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(hand.getCards());

          list = list.getName(cardName);
          AllZone.GameAction.discard(list.get(0));

          AllZone.Stack.push(ability);
          stop();
        }
      };
      ability.setBeforePayMana(removeCard);

      ability.setDescription("Grandeur - Discard another card named Tarox Bladewing: Tarox Bladewing gets +X/+X until end of turn, where X is his power.");
      ability.setStackDescription(cardName +" - gets +X/+X until end of turn.");

      card.addSpellAbility(ability);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Baru, Fist of Krosa"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void chooseTargetAI()
        {
          PlayerZone p = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(p.getCards());
          list = list.getName(cardName);

          AllZone.GameAction.discard(list.get(0));
        }

        public boolean canPlay()
        {
          PlayerZone p = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(p.getCards());
          list = list.getName(cardName);
          return 0 < list.size() && AllZone.getZone(card).getZone().equals(Constant.Zone.Play);
        }

        public void resolve()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList list = new CardList(play.getCards());
          list = list.getType("Land");
          makeToken(list.size());
        }
        void makeToken(int stats)
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("G");
          c.setToken(true);
          c.addKeyword("Token");

          c.addType("Creature");
          c.addType("Wurm");
          c.setAttack(stats);
          c.setDefense(stats);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getController());
          play.add(c);
        }//makeToken()
      };
      Input removeCard = new Input()
      {
        int n = 0;
        public void showMessage()
        {
          //this is called twice, this is an ugly hack
          if(n % 2 == 0)
            stop();


          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(hand.getCards());

          list = list.getName(cardName);
          AllZone.GameAction.discard(list.get(0));

          AllZone.Stack.push(ability);
          stop();
        }
      };
      ability.setBeforePayMana(removeCard);

      ability.setDescription("Grandeur - Discard another card named Baru, Fist of Krosa: Put an X/X green Wurm creature token into play, where X is the number of lands that you control.");
      ability.setStackDescription(cardName +" - put X/X token into play.");

      card.addSpellAbility(ability);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Malach of the Dawn"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "W W W")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("WWW: Regenerate Malach of the Dawn.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Ghost Ship"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "U U U")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("UUU: Regenerate Ghost Ship.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Drudge Skeletons"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "B")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("B: Regenerate Drudge Skeletons.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Revered Dead"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "W")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("W: Regenerate Revered Dead.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Will-o'-the-Wisp"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "B")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("B: Regenerate Will-o'-the-Wisp.");
      a1.setStackDescription("Regenerate " +card);

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Ancient Silverback"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "G")
      {
        public boolean canPlayAI() {return false;}
        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("G: Regenerate Ancient Silverback.");
      a1.setStackDescription("Regenerate Ancient Silverback");

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Hunted Troll"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {

          for(int i = 0; i < 4; i++)
            makeToken();
        }
        void makeToken()
        {
          //warning, different owner and controller
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, opponent);
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(opponent);

          c.setManaCost("U");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Faerie ");
          c.setAttack(1);
          c.setDefense(1);
          c.addKeyword("Flying");

          play.add(c);
        }//makeToken()
      };//SpellAbility

      Command intoPlay = new Command()
      {
        public void execute()
        {
          AllZone.Stack.add(ability);
        }
      };
      ability.setStackDescription("Hunted Troll - Opponent puts 4 Faerie tokens with flying into play");
      card.setComesIntoPlay(intoPlay);


      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setShield(0);
        }
      };

      final SpellAbility a1 = new Ability(card, "G")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          card.addShield();
          AllZone.EndOfTurn.addUntil(untilEOT);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("G: Regenerate Hunted Troll.");
      a1.setStackDescription("Regenerate Hunted Troll");

      a1.setBeforePayMana(new Input_PayManaCost(a1));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Blinkmoth Nexus"))
    {
      final Command eot1 = new Command()
      {
        public void execute()
        {
          Card c = card;

          c.setAttack(0);
          c.setDefense(0);
          c.removeKeyword("Flying");
          c.removeType("Artifact");
          c.removeType("Creature");
          c.removeType("Blinkmoth");
        }
      };

      final SpellAbility a1 = new Ability(card, "1")
      {
        public boolean canPlayAI()
        {
          return false;
          //it turns into a creature, but doesn't attack
//          return (! card.getKeyword().contains("Flying") &&
//                 (CardFactoryUtil.AI_getHumanCreature("Flying").isEmpty()));
        }
        public void resolve()
        {
          Card c = card;

          c.setAttack(1);
          c.setDefense(1);
          //to prevent like duplication like "Flying Flying Creature Creature"
          if(! c.getKeyword().contains("Flying"))
          {
            c.addKeyword("Flying");
            c.addType("Artifact");
            c.addType("Creature");
            c.addType("Blinkmoth");
          }
          AllZone.EndOfTurn.addUntil(eot1);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setDescription("1: Blinkmoth Nexus becomes a 1/1 Blinkmoth artifact creature with flying until end of turn. It's still a land.");
      a1.setStackDescription(card +" becomes a 1/1 creature with flying until EOT");

      Command paid1 = new Command() {public void execute() {AllZone.Stack.add(a1);}};

      a1.setBeforePayMana(new Input_PayManaCost_Ability(a1.getManaCost(), paid1));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Mishra's Factory"))
    {
      final Command eot1 = new Command()
      {
        public void execute()
        {
          Card c = card;

          c.setAttack(0);
          c.setDefense(0);
          c.removeType("Artifact");
          c.removeType("Creature");
          c.removeType("Assembly-Worker");
        }
      };

      final SpellAbility a1 = new Ability(card, "1")
      {
        public boolean canPlayAI()
        {
          return false;
          //it turns into a creature, but doesn't attack
//          return (! card.getKeyword().contains("Flying") &&
//                 (CardFactoryUtil.AI_getHumanCreature("Flying").isEmpty()));
        }
        public void resolve()
        {
          Card c = card;

          c.setAttack(2);
          c.setDefense(2);
          //to prevent like duplication like "Creature Creature"
          if(! c.getKeyword().contains("Creature"))
          {
            c.addType("Artifact");
            c.addType("Creature");
            c.addType("Assembly-Worker");
          }
          AllZone.EndOfTurn.addUntil(eot1);
        }
      };//SpellAbility
      card.addSpellAbility(a1);
      a1.setStackDescription(card +" - becomes a 2/2 creature until EOT");

      Command paid1 = new Command() {public void execute() {AllZone.Stack.add(a1);}};

      a1.setBeforePayMana(new Input_PayManaCost_Ability(a1.getManaCost(), paid1));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Venerable Monk"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          Card c = card;
          PlayerLife life = AllZone.GameAction.getPlayerLife(c.getController());
          life.addLife(2);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" gains 2 life");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Ambassador Oak"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("G");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Elf");
          c.addType("Warrior");

          c.setAttack(1);
          c.setDefense(1);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);

        }//resolve()
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Ambassador Oak - put a 1/1 green Elf Warrior creature token into play.");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Mudbutton Torchrunner"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(getTargetCard() != null)
            getTargetCard().addDamage(3);
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(3);
        }
      };
      Command leavesPlay = new Command()
      {
        public void execute()
        {
          if(card.getController().equals(Constant.Player.Human))
            AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreaturePlayer(ability));
          else
          {
            CardList list = CardFactoryUtil.AI_getHumanCreature(3);
            CardListUtil.sortAttack(list);

            if(MyRandom.percentTrue(50))
              CardListUtil.sortFlying(list);

            for(int i = 0; i < list.size(); i++)
              if(2 <= list.get(i).getAttack())
                ability.setTargetCard(list.get(i));

            if(ability.getTargetCard() == null)
              ability.setTargetPlayer(Constant.Player.Human);

            AllZone.Stack.add(ability);
          }
        }//execute()
      };//Command
      card.setDestroy(leavesPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Mulldrifter"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          for(int i = 0; i < 2; i++)
            AllZone.GameAction.drawCard(card.getController());
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getName() +" - " +card.getController() +" draws 2 cards.");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);

      card.addSpellAbility(new Spell_Evoke(card, "2 U")
      {
        public boolean canPlayAI() {return false;}
      });
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Meadowboon"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerZone zone = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList list = new CardList(zone.getCards());
          list = list.getType("Creature");
          Card c;

          for(int i = 0; i < list.size(); i++)
          {
            c = list.get(i);
            c.setAttack(c.getAttack() + 1);
            c.setDefense(c.getDefense() + 1);
          }
        }//resolve()
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getName() +" - " +card.getController() +" puts a +1/+1 on each creature he controls.");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);

      card.addSpellAbility(new Spell_Evoke(card, "3 W")
      {
        public boolean canPlayAI() {return false;}
      });
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Sengir Autocrat"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          for(int i = 0; i < 3; i++)
            makeToken();
        }
        void makeToken()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("B");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Serf");
          c.setAttack(0);
          c.setDefense(1);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//makeToken()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getName() +" - " +card.getController() +" puts three 0/1 tokens into play");
          AllZone.Stack.add(ability);
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());

          all = all.getType("Serf");
          for(int i = 0; i < all.size(); i++)
            AllZone.GameAction.destroy(all.get(i));
        }//execute
      };//Command
      card.setComesIntoPlay(intoPlay);
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Haunted Angel"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          makeToken();

          //remove this card from the graveyard and from the game

          //fixed - error if this card is copied like with Kiki, Jiki mirror breaker
          //null pointer exception

          if(card.isToken())
            return;

          AllZone.getZone(card).remove(card);
        }
        void makeToken()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("B");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Angel");
          c.setAttack(3);
          c.setDefense(3);
          c.addKeyword("Flying");

          String opponent = AllZone.GameAction.getOpponent(card.getController());
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, opponent);
          play.add(c);
        }//makeToken()
      };//SpellAbility
      Command destroy = new Command()
      {
        public void execute()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          ability.setStackDescription(card.getName() +" - " +opponent +" puts a 3/3 flying token into play");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Angel of Mercy"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          Card c = card;
          PlayerLife life = AllZone.GameAction.getPlayerLife(c.getController());
          life.addLife(3);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Angel of Mercy - " +card.getController() +" gains 3 life");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Groundbreaker") || cardName.equals("Ball Lightning") ||
       cardName.equals("Blistering Firecat") || cardName.equals("Spark Elemental"))
    {
      final SpellAbility spell = new Ability(card, "0")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            AllZone.GameAction.sacrifice(card);
        }
      };
      spell.setStackDescription("Sacrifice " +card);

      final Command destroy = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            AllZone.Stack.add(spell);
        }
      };

      Command intoPlay = new Command()
      {
        public void execute()
        {
          AllZone.EndOfTurn.addAt(destroy);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Primal Plasma") || cardName.equals("Primal Clay"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          String choice = "";
          String choices[] = {"3/3", "2/2 with flying", "1/6 with defender"};

          if(card.getController().equals(Constant.Player.Human))
          {
            choice = (String) AllZone.Display.getChoice("Choose one", choices);
          }
          else
            choice = choices[MyRandom.random.nextInt(3)];

          if(choice.equals("2/2 with flying"))
          {
            card.setAttack(2);
            card.setDefense(2);
            card.addKeyword("Flying");
          }
          if(choice.equals("1/6 with defender"))
          {
            card.setAttack(1);
            card.setDefense(6);
            card.addKeyword("Defender");
          }

        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getName() +" - choose: 3/3, 2/2 flying, 1/6 defender");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Phyrexian Gargantua"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.subtractLife(2);

          AllZone.GameAction.drawCard(card.getController());
          AllZone.GameAction.drawCard(card.getController());
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Phyrexian Gargantua - " +card.getController() +" draws 2 cards and loses 2 life");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Phyrexian Rager"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.subtractLife(1);

          AllZone.GameAction.drawCard(card.getController());
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Phyrexian Rager - " +card.getController() +" draws 1 card and loses 1 life");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Anger"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getName() +" - " +card.getOwner() +" creatures have Haste.");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Valor"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getName() +" - " +card.getOwner() +" creatures have Vigilance.");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Wonder"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getName() +" - " +card.getOwner() +" creatures have Flying.");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Kokusho, the Evening Star"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());

          PlayerLife opp = AllZone.GameAction.getPlayerLife(opponent);
          PlayerLife my = AllZone.GameAction.getPlayerLife(card.getController());

          opp.subtractLife(5);
          my.addLife(5);
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          ability.setStackDescription("Kokusho, the Evening Star - " +opponent +" loses 5 life and " +card.getController() +" gains 5 life");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Symbiotic Elf"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          makeToken();
          makeToken();
        }
        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("G");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Insect");
          c.setAttack(1);
          c.setDefense(1);

          play.add(c);
        }//makeToken()
      };//SpellAbility

      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Symbiotic Elf - " +card.getController() +" puts two 1/1 tokens into play ");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Chittering Rats"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());

          PlayerZone hand    = AllZone.getZone(Constant.Zone.Hand   , opponent);
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, opponent);

          if(hand.size() == 0)
            return;

          //randomly move card from hand to top of library
          int index = MyRandom.random.nextInt(hand.size());
          Card card = hand.get(index);

          hand.remove(card);
          library.add(card, 0);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Chittering Rats - Opponent randomly puts a card from his hand on top of his library.");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Kemuri-Onna"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          if(Constant.Player.Human.equals(opponent))
            AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          else//computer
            AllZone.GameAction.discardRandom(opponent);
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          ability.setStackDescription(card.getName() +" - " +opponent +" discards a card");

          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Highway Robber"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          AllZone.GameAction.getPlayerLife(opponent).subtractLife(2);

          AllZone.GameAction.getPlayerLife(card.getController()).addLife(2);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Highway Robber - " +card.getController() +" gains 2 life and opponent loses 2 life.");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Serpent Warrior"))
    {
      SpellAbility summoningSpell = new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          return AllZone.Computer_Life.getLife()>3;
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(summoningSpell);

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {

          AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(3);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Serpent Warrior - "
                                      +card.getController() +" loses 3 life");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Foul Imp"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(2);
        }
        public boolean canPlayAI()
        {
          return 4 < AllZone.Computer_Life.getLife();
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription("Foul Imp - " +card.getController() +" loses 2 life");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Drekavac"))
    {
      final Input discard = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Discard from your hand a non-creature card");
          ButtonUtil.disableAll();
        }
        public void selectCard(Card c, PlayerZone zone)
        {
          if(zone.is(Constant.Zone.Hand))
          {
            AllZone.GameAction.discard(c);
            if(c.isCreature())
              AllZone.GameAction.sacrifice(card);
            stop();
          }
        }
      };//Input

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(card.getController().equals(Constant.Player.Human))
          {
            if(AllZone.Human_Hand.getCards().length == 0)
              AllZone.GameAction.sacrifice(card);
            else
              AllZone.InputControl.setInput(discard);
          }
          else
          {
            CardList list = new CardList(AllZone.Computer_Hand.getCards());
            list = list.filter(new CardListFilter()
            {
              public boolean addCard(Card c)
              {
                return (!c.isCreature());
              }
            });
            AllZone.GameAction.discard(list.get(0));
          }//else
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" sacrifices Drekavac unless he discards a non-creature card");
          AllZone.Stack.add(ability);
        }
      };
      SpellAbility spell = new Spell_Permanent(card)
      {
        //could never get the AI to work correctly
        //it always played the same card 2 or 3 times
        public boolean canPlayAI() {return false;}

        public boolean canPlay()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList list = new CardList(hand.getCards());
          list.remove(card);
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (!c.isCreature());
            }
          });
          return list.size() != 0;
        }//canPlay()
      };
      card.setComesIntoPlay(intoPlay);
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Minotaur Explorer"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          if(hand.getCards().length == 0)
            AllZone.GameAction.sacrifice(card);
          else
            AllZone.GameAction.discardRandom(card.getController());
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" - discards at random or sacrifices Minotaur Explorer");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Goretusk Firebeast"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          AllZone.GameAction.getPlayerLife(opponent).subtractLife(4);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          ability.setStackDescription("Goretusk Firebeast - deals 4 damage to " +opponent);
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Goblin Ringleader"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerZone libraryZone = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone hand        = AllZone.getZone(Constant.Zone.Hand   , card.getController());

          //get top 4 cards of the library
          CardList top = new CardList();
          Card[] library = libraryZone.getCards();
          for(int i = 0; i < 4 && i < library.length; i++)
            top.add(library[i]);

          //put top 4 cards on bottom of library
          for(int i = 0; i < top.size(); i++)
          {
            libraryZone.remove(top.get(i));
            libraryZone.add(top.get(i));
          }

          CardList goblin = top.getType("Goblin");

          for(int i = 0; i < goblin.size(); i++)
            AllZone.GameAction.moveTo(hand, goblin.get(i));
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          AllZone.Stack.add(ability);
        }
      };
      ability.setStackDescription("Goblin Ringleader - reveal the top four cards of your library. Put all Goblin cards revealed this way into your hand and the rest on the bottom of your library.");
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Sylvan Messenger"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerZone libraryZone = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone hand        = AllZone.getZone(Constant.Zone.Hand   , card.getController());

          //get top 4 cards of the library
          CardList top = new CardList();
          Card[] library = libraryZone.getCards();
          for(int i = 0; i < 4 && i < library.length; i++)
            top.add(library[i]);

          //put top 4 cards on bottom of library
          for(int i = 0; i < top.size(); i++)
          {
            libraryZone.remove(top.get(i));
            libraryZone.add(top.get(i));
          }

          CardList goblin = top.getType("Elf");

          for(int i = 0; i < goblin.size(); i++)
            AllZone.GameAction.moveTo(hand, goblin.get(i));
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          AllZone.Stack.add(ability);
        }
      };
      ability.setStackDescription("Sylvan Messenger - reveal the top four cards of your library. Put all Elf cards revealed this way into your hand and the rest on the bottom of your library.");
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************







    //*************** START *********** START **************************
    if(cardName.equals("Ryusei, the Falling Star"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");
          for(int i = 0; i < list.size(); i++)
            if(! list.get(i).getKeyword().contains("Flying"))
              list.get(i).addDamage(5);
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          AllZone.Stack.add(ability);
        }
      };
      ability.setStackDescription("Ryusei, the Falling Star - deals 5 damage to each creature without flying");
      card.setDestroy(destroy);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Flametongue Kavu"))
    {
      final CommandReturn getCreature = new CommandReturn()
      {
        //get target card, may be null
        public Object execute()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(4);
          CardListUtil.sortAttack(list);
          CardListUtil.sortFlying(list);

          if(list.size() != 0)
          {
            Card c = list.get(0);
            if(3 <= c.getAttack() ||
              (2 <= c.getAttack() && c.getKeyword().contains("Flying")))
              return c;
          }
          if((AllZone.Computer_Life.getLife() < 10) &&
             (CardFactoryUtil.AI_getHumanCreature().size() != 0))
          {
            list = CardFactoryUtil.AI_getHumanCreature();
            CardListUtil.sortAttack(list);
            CardListUtil.sortFlying(list);

            return list.get(0);
          }
          return null;
        }//execute()
      };//CommandReturn

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            getTargetCard().addDamage(4);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          if(card.getController().equals(Constant.Player.Human))
          {
            AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreature(ability));
            ButtonUtil.disableAll();
          }
          else//computer
          {
            Object o = getCreature.execute();
            if(o != null)//should never happen, but just in case
            {
              ability.setTargetCard((Card)o);
              AllZone.Stack.add(ability);
            }
          }//else
        }//execute()
      };
      card.setComesIntoPlay(intoPlay);

      card.clearSpellAbility();
      card.addSpellAbility(new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          Object o = getCreature.execute();

          return (o != null) && AllZone.getZone(getSourceCard()).is(Constant.Zone.Hand);
        }
      });
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Shriekmaw"))
    {
      final CommandReturn getCreature = new CommandReturn()
      {
        //get target card, may be null
        public Object execute()
        {
          CardList nonblack = CardFactoryUtil.AI_getHumanCreature();
          nonblack = nonblack.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return(!c.isArtifact() && !CardUtil.getColors(c).contains(Constant.Color.Black));
            }
          });

          CardList list = new CardList(nonblack.toArray());
          CardListUtil.sortAttack(list);
          CardListUtil.sortFlying(list);

          if(list.size() != 0)
          {
            Card c = list.get(0);
            if(2 <= c.getAttack() && c.getKeyword().contains("Flying"))
              return c;
          }

          if((AllZone.Computer_Life.getLife() < 10) && list.size() != 0)
          {
            CardListUtil.sortAttack(list);

            if(MyRandom.percentTrue(50))
              CardListUtil.sortFlying(list);

            return list.get(0);
          }
          return null;
        }//execute()
      };//CommandReturn

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          Card c = getTargetCard();

          if(AllZone.GameAction.isCardInPlay(c) &&
             !CardUtil.getColors(c).contains(Constant.Color.Black) &&
             !c.isArtifact())
          {
            AllZone.GameAction.destroy(c);
          }
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          Input target = new Input()
          {
            public void showMessage()
            {
              AllZone.Display.showMessage("Select target nonartifact, nonblack creature to destroy");
              ButtonUtil.disableAll();
            }
            public void selectCard(Card card, PlayerZone zone)
            {
              if(card.isCreature() && zone.is(Constant.Zone.Play) &&
                 !card.isArtifact() && !CardUtil.getColors(card).contains(Constant.Color.Black))
              {
                ability.setTargetCard(card);
                AllZone.Stack.add(ability);
                stop();
              }
            }
          };//Input target


          if(card.getController().equals(Constant.Player.Human))
          {
            //get all creatures
            CardList list = new CardList();
            list.addAll(AllZone.Human_Play.getCards());
            list.addAll(AllZone.Computer_Play.getCards());
            list = list.getType("Creature");

            list = list.filter(new CardListFilter()
            {
              public boolean addCard(Card c)
              {
                return(!c.isArtifact() && !CardUtil.getColors(c).contains(Constant.Color.Black));
              }
            });

            if(list.size() != 0)
              AllZone.InputControl.setInput(target);
//              AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreature(ability));
          }
          else//computer
          {
            Object o = getCreature.execute();
            if(o != null)//should never happen, but just in case
            {
              ability.setTargetCard((Card)o);
              AllZone.Stack.add(ability);
            }
          }//else
        }//execute()
      };
      card.setComesIntoPlay(intoPlay);

      card.clearSpellAbility();
      card.addSpellAbility(new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          Object o = getCreature.execute();

          return (o != null) && AllZone.getZone(getSourceCard()).is(Constant.Zone.Hand);
        }
      });

      card.addSpellAbility(new Spell_Evoke(card, "1 B")
      {
        public boolean canPlayAI()
        {
          Object o = getCreature.execute();

          return (o != null) && AllZone.getZone(getSourceCard()).is(Constant.Zone.Hand);
        }
      });
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Briarhorn"))
    {
      final CommandReturn getCreature = new CommandReturn()
      {
        //get target card, may be null
        public Object execute()
        {
          Combat combat = ComputerUtil.getAttackers();
          Card[] c = combat.getAttackers();

          if(c.length == 0)
          {
            CardList list = new CardList();
            list.addAll(AllZone.Computer_Play.getCards());
            list = list.filter(new CardListFilter()
            {
              public boolean addCard(Card c)
              {
                return(c.isCreature() && !c.hasSickness());
              }
            });

            if(list.size() == 0)
              return card;
            else
            {
              CardListUtil.sortAttack(list);
              CardListUtil.sortFlying(list);

              for(int i = 0; i < list.size(); i++)
                if(list.get(i).isUntapped())
                  return list.get(i);

              return list.get(0);
            }

          }

          return c[0];
        }//execute()
      };//CommandReturn

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          final Card c = getTargetCard();

          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(c.getAttack() + 3);
            c.setDefense(c.getDefense() + 3);

            AllZone.EndOfTurn.addUntil(new Command()
            {
              public void execute()
              {
                c.setAttack(c.getAttack() - 3);
                c.setDefense(c.getDefense() - 3);
              }
            });
          }//if
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          if(card.getController().equals(Constant.Player.Human))
          {
              AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreature(ability));
          }
          else//computer
          {
            Object o = getCreature.execute();
            if(o != null)//should never happen, but just in case
            {
              ability.setTargetCard((Card)o);
              AllZone.Stack.add(ability);
            }
          }//else
        }//execute()
      };
      card.setComesIntoPlay(intoPlay);

      card.clearSpellAbility();
      card.addSpellAbility(new Spell_Permanent(card)
      {
        //because this card has Flash
        public boolean canPlay() {return true;}

        public boolean canPlayAI()
        {
          Object o = getCreature.execute();

          return (o != null) && AllZone.getZone(getSourceCard()).is(Constant.Zone.Hand);
        }
      });

      card.addSpellAbility(new Spell_Evoke(card, "1 G")
      {
        public boolean canPlayAI()
        {
          return false;
        }
        //because this card has Flash
        public boolean canPlay() {return true;}
      });
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Inner-Flame Acolyte"))
    {
      final CommandReturn getCreature = new CommandReturn()
      {
        //get target card, may be null
        public Object execute()
        {
          Combat combat = ComputerUtil.getAttackers();
          Card[] c = combat.getAttackers();
          CardList list = new CardList();

          if(c.length == 0)
          {
            list.addAll(AllZone.Computer_Play.getCards());
            list = list.filter(new CardListFilter()
            {
              public boolean addCard(Card c)
              {
                return c.isCreature();
              }
            });

            if(list.size() == 0)
              return card;
            else
            {
              CardListUtil.sortAttack(list);
              CardListUtil.sortFlying(list);

              for(int i = 0; i < list.size(); i++)
                if(list.get(i).isUntapped())
                  return list.get(i);

              return list.get(0);
            }
          }

          return c[0];
        }//execute()
      };//CommandReturn

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          final Card c = getTargetCard();

          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(c.getAttack() + 2);
            c.addKeyword("Haste");

            AllZone.EndOfTurn.addUntil(new Command()
            {
              public void execute()
              {
                c.setAttack(c.getAttack() - 2);
                c.removeKeyword("Haste");
              }
            });
          }//if
        }//resolve()
      };//SpellAbility
      Command intoPlay = new Command()
      {
        public void execute()
        {
          if(card.getController().equals(Constant.Player.Human))
          {
              AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreature(ability));
          }
          else//computer
          {
            Object o = getCreature.execute();
            if(o != null)//should never happen, but just in case
            {
              ability.setTargetCard((Card)o);
              AllZone.Stack.add(ability);
            }
          }//else
        }//execute()
      };
      card.setComesIntoPlay(intoPlay);

      card.clearSpellAbility();
      card.addSpellAbility(new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          Object o = getCreature.execute();

          return (o != null) && AllZone.getZone(getSourceCard()).is(Constant.Zone.Hand);
        }
      });

      card.addSpellAbility(new Spell_Evoke(card, "R")
      {
        public boolean canPlayAI()
        {
          return false;
        }
      });
    }//*************** END ************ END **************************







    //*************** START *********** START **************************
    if(cardName.equals("Timetwister"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          discardDraw7(Constant.Player.Human);
          discardDraw7(Constant.Player.Computer);
        }//resolve()

        void discardDraw7(String player)
        {
          // Discard hand into graveyard
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, player);
          Card[] c = hand.getCards();
          for(int i = 0; i < c.length; i++)
            AllZone.GameAction.discard(c[i]);

          // Move graveyard into library
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, player);
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
          Card[] g = grave.getCards();
          for (int i = 0; i < g.length; i++)
          {
            grave.remove(g[i]);
            library.add(g[i],0);
          }

          // Shuffle library
          AllZone.GameAction.shuffle(player);

          // Draw seven cards
          for(int i = 0; i < 7; i++)
            AllZone.GameAction.drawCard(player);
        }

        // Simple, If computer has two or less playable cards remaining in hand play Timetwister
        public boolean canPlayAI()
        {
          Card[] c = removeLand(AllZone.Computer_Hand.getCards());
          return 2 >= c.length;
        }
        Card[] removeLand(Card[] in)
        {
          CardList c = new CardList(in);
          c = c.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return !c.isLand();
            }
          });
          return c.toArray();
        }//removeLand()

      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Slaughterhouse Bouncer"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            getTargetCard().addDamage(3);
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          //check to see if any other creatures in play
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          //check to see if any cards in hand
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          if(hand.getCards().length == 0 && list.size() != 0)
          {
            if(card.getController().equals(Constant.Player.Human))
            {
              AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreature(ability));
              ButtonUtil.disableAll();
            }
            else//computer
            {
              //1.try to get human creature with defense of 3
              list = CardFactoryUtil.AI_getHumanCreature();
              list = list.filter(new CardListFilter()
              {
                public boolean addCard(Card c) {return c.getDefense() == 3;}
              });
              //2.try to get human creature with defense of 2 or less
              if(list.isEmpty())
                list = CardFactoryUtil.AI_getHumanCreature(2);
              //3.get any computer creature
              if(list.isEmpty())
              {
                list = new CardList(AllZone.Computer_Play.getCards());
                list = list.getType("Creature");
              }
              list.shuffle();
              ability.setTargetCard(list.get(0));
              AllZone.Stack.add(ability);
            }
          }//if ok to play
        }//execute()
      };//Command
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Undying Beast"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          card.setDamage(0);
          card.setAssignedDamage(0);
          card.untap();

          //moves card to top of library
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getOwner());
          library.add(card, 0);
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          if(card.isToken())
            return;

          //remove from graveyard
          PlayerZone grave = AllZone.getZone(card);
          grave.remove(card);

          ability.setStackDescription("Put Undying Beast on top of its owner's library.");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************









    //*************** START *********** START **************************
    if(cardName.equals("Fire Imp"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            getTargetCard().addDamage(2);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          if(card.getController().equals(Constant.Player.Human))
          {
            AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreature(ability));
            ButtonUtil.disableAll();
          }
          else//computer
          {
            CardList list = CardFactoryUtil.AI_getHumanCreature(2);
            CardListUtil.sortAttack(list);
            CardListUtil.sortFlying(list);

            if(list.isEmpty())
            {
              list = CardFactoryUtil.AI_getHumanCreature();
              list.shuffle();
            }

            ability.setTargetCard(list.get(0));
            AllZone.Stack.add(ability);
          }//else
        }//execute()
      };//Command
      card.setComesIntoPlay(intoPlay);

      card.clearSpellAbility();
      card.addSpellAbility(new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature();

          return (list.size() > 0) && AllZone.getZone(getSourceCard()).is(Constant.Zone.Hand);
        }
      });

    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Man-o'-War"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          Card c = getTargetCard();
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, c.getOwner());

          if(AllZone.GameAction.isCardInPlay(c))
          {
            AllZone.getZone(c).remove(c);

            if(! c.isToken())
            {
              Card newCard = AllZone.CardFactory.getCard(c.getName(), c.getOwner());
              hand.add(newCard);
            }
          }
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          if(card.getController().equals(Constant.Player.Human))
          {
            AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreature(ability));
            ButtonUtil.disableAll();
          }
          else//computer
          {
            Card human = CardFactoryUtil.AI_getBestCreature(CardFactoryUtil.AI_getHumanCreature());
            ability.setTargetCard(human);
            AllZone.Stack.add(ability);
          }//else
        }//execute()
      };//Command
      card.setComesIntoPlay(intoPlay);

      card.clearSpellAbility();
      card.addSpellAbility(new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature();

          return (list.size() > 0) && AllZone.getZone(getSourceCard()).is(Constant.Zone.Hand);
        }
      });
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Keening Banshee"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            getTargetCard().addDamage(2);
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          if(card.getController().equals(Constant.Player.Human))
          {
            AllZone.InputControl.setInput(CardFactoryUtil.input_targetCreature(ability));
            ButtonUtil.disableAll();
          }
          else//computer
          {
            CardList list = CardFactoryUtil.AI_getHumanCreature(2);
            CardListUtil.sortAttack(list);
            CardListUtil.sortFlying(list);

            if(list.isEmpty())
            {
              list = CardFactoryUtil.AI_getHumanCreature();
              list.shuffle();
            }

            ability.setTargetCard(list.get(0));
            AllZone.Stack.add(ability);
          }//else
        }//execute()
      };//Command
      card.setComesIntoPlay(intoPlay);

      card.clearSpellAbility();
      card.addSpellAbility(new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature();

          return (list.size() > 0) && AllZone.getZone(getSourceCard()).is(Constant.Zone.Hand);
        }
      });

    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Dragon Roost"))
    {
      final SpellAbility ability = new Ability(card, "5 R R")
      {
        public void resolve()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("R");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Dragon");
          c.setAttack(5);
          c.setDefense(5);
          c.addKeyword("Flying");

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//resolve()
      };
      ability.setDescription("5RR: Put a 5/5 red Dragon creature token with flying into play.");
      ability.setStackDescription("Dragon Roost - Put a 5/5 red Dragon creature token with flying into play.");
      card.addSpellAbility(ability);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("The Hive"))
    {
      final SpellAbility ability = new Ability_Tap(card, "5")
      {
        public void resolve()
        {
          Card c = new Card();
          c.setName("Wasp");

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("");
          c.setToken(true);

          c.addType("Artifact");
          c.addType("Creature");
          c.addType("Insect");

          c.setAttack(1);
          c.setDefense(1);
          c.addKeyword("Flying");
          c.addKeyword("Token");

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//resolve()
      };
      ability.setDescription("5, tap: Put a 1/1 Insect artifact creature token with flying named Wasp into play.");
      ability.setStackDescription("The Hive - Put a 1/1 token with flying into play.");
      card.addSpellAbility(ability);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Centaur Glade"))
    {
      final SpellAbility ability = new Ability(card, "2 G G")
      {
        public void resolve()
        {
          Card c = new Card();
          c.setName("Token");

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("G");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Centaur");

          c.setAttack(3);
          c.setDefense(3);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//resolve()
      };
      ability.setDescription("2GG: Put a 3/3 green Centaur creature token into play.");
      ability.setStackDescription("Centaur Glade - Put a 3/3 token into play.");
      card.addSpellAbility(ability);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Eternal Witness"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          if(AllZone.GameAction.isCardInZone(getTargetCard(), grave))
          {
            PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
            AllZone.GameAction.moveTo(hand, getTargetCard());
          }
        }//resolve()
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());

          if(grave.getCards().length == 0)
            return;

          if(card.getController().equals(Constant.Player.Human))
          {
            Object o = AllZone.Display.getChoiceOptional("Select target card", grave.getCards());
            if(o != null)
            {
              ability.setTargetCard((Card)o);
              AllZone.Stack.add(ability);
            }
          }
          else//computer
          {
            CardList list = new CardList(grave.getCards());
            Card best = CardFactoryUtil.AI_getBestCreature(list);

            if(best == null)
            {
              list.shuffle();
              best = list.get(0);
            }
            ability.setTargetCard(best);
            AllZone.Stack.add(ability);
          }
        }//execute()
      };//Command
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Gravedigger"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          if(AllZone.GameAction.isCardInZone(getTargetCard(), grave))
          {
            PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
            AllZone.GameAction.moveTo(hand, getTargetCard());
          }
        }//resolve()
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          CardList list = new CardList(grave.getCards());
          list = list.getType("Creature");

          if(list.isEmpty())
            return;

          if(card.getController().equals(Constant.Player.Human))
          {
            Object o = AllZone.Display.getChoiceOptional("Select target card", list.toArray());
            if(o != null)
            {
              ability.setTargetCard((Card)o);
              AllZone.Stack.add(ability);
            }
          }//if
          else//computer
          {
            Card best = CardFactoryUtil.AI_getBestCreature(list);
            ability.setTargetCard(best);
            AllZone.Stack.add(ability);
          }
        }//execute()
      };//Command
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Raise Dead"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI() {return getGraveCreatures().size() != 0;}

        public void chooseTargetAI()
        {
          CardList grave = getGraveCreatures();
          Card target = CardFactoryUtil.AI_getBestCreature(grave);
          setTargetCard(target);
        }

        public void resolve()
        {
          if(card.getController().equals(Constant.Player.Human))
          {
            Card c = (Card) AllZone.Display.getChoice("Select card", getGraveCreatures().toArray());
            setTargetCard(c);
          }

          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());

          if(AllZone.GameAction.isCardInZone(getTargetCard(), grave))
             AllZone.GameAction.moveTo(hand, getTargetCard());
        }//resolve()
        public boolean canPlay()
        {
          return getGraveCreatures().size() != 0;
        }
        CardList getGraveCreatures()
        {
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          CardList list = new CardList(grave.getCards());
          list = list.getType("Creature");
          return list;
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Anarchist"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          if(AllZone.GameAction.isCardInZone(getTargetCard(), grave))
          {
            PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
            AllZone.GameAction.moveTo(hand, getTargetCard());
          }
        }//resolve()
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          PlayerZone grave = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          CardList sorcery = new CardList(grave.getCards());
          sorcery = sorcery.getType("Sorcery");

          String controller = card.getController();

          if(sorcery.size() == 0)
            return;

          if(controller.equals(Constant.Player.Human))
          {
            Object o = AllZone.Display.getChoiceOptional("Select target card", sorcery.toArray());
            if(o != null)
            {
              ability.setTargetCard((Card)o);
              AllZone.Stack.add(ability);
            }
          }
          else //computer
          {
            sorcery.shuffle();
            ability.setTargetCard(sorcery.get(0));
            AllZone.Stack.add(ability);
          }

        }//execute()
      };//Command
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Kavu Climber"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" draws a card");
          AllZone.Stack.add(ability);
        }
      };
      ability.setStackDescription("Computer draws a card");
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Barkhide Mauler"))
    {
      card.addSpellAbility(CardFactoryUtil.ability_cycle(card, "2"));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Penumbra Kavu"))
    {
      final Ability ability = new Ability(card, "0")
      {
        public void resolve()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("B");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Kavu");
          c.setAttack(3);
          c.setDefense(3);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//resolve()
      };//Ability

      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" puts a 3/3 creature into play from Penumbra Kavu");
          AllZone.Stack.add(ability);
        }
      };

      card.setDestroy(destroy);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Penumbra Bobcat"))
    {
      final Ability ability = new Ability(card, "0")
      {
        public void resolve()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("B");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Cat");
          c.setAttack(2);
          c.setDefense(1);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//resolve()
      };//Ability

      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" puts a 2/1 creature into play from Penumbra Bobcat");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Aven Fisher"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
        }
      };
      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" draws a card");
          AllZone.Stack.add(ability);
        }
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Peregrine Drake"))
    {
      final Input untap = new Input()
      {
        int stop = 5;
        int count = 0;

        public void showMessage()
        {
          AllZone.Display.showMessage("Select a land to untap");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isLand() && zone.is(Constant.Zone.Play))
          {
            card.untap();
            count++;
            if(count == stop)
              stop();
          }
        }//selectCard()
      };

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(card.getController().equals("Human"))
            AllZone.InputControl.setInput(untap);
          else
          {
            CardList list = new CardList(AllZone.Computer_Play.getCards());
            list = list.filter(new CardListFilter()
            {
              public boolean addCard(Card c)
              {
                return c.isLand() && c.isTapped();
              }
            });
            for(int i = 0; i < 5 && i < list.size(); i++)
              list.get(i).untap();
          }//else
        }//resolve()
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" untaps up to 5 lands.");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Cloud of Faeries"))
    {
      final Input untap = new Input()
      {
        int stop = 2;
        int count = 0;

        public void showMessage()
        {
          AllZone.Display.showMessage("Select a land to untap");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isLand() && zone.is(Constant.Zone.Play))
          {
            card.untap();
            count++;
            if(count == stop)
              stop();
          }
        }//selectCard()
      };

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(card.getController().equals("Human"))
            AllZone.InputControl.setInput(untap);
          else
          {
            CardList list = new CardList(AllZone.Computer_Play.getCards());
            list = list.filter(new CardListFilter()
            {
              public boolean addCard(Card c)
              {
                return c.isLand() && c.isTapped();
              }
            });
            for(int i = 0; i < 2 && i < list.size(); i++)
            {
              list.get(i).untap();
            }
          }//else
        }//resolve()
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" untaps up to 2 lands.");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);

      //add cycling
      card.addSpellAbility(CardFactoryUtil.ability_cycle(card, "2"));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Vodalian Merchant"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());

          if(card.getController().equals("Human"))
            AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          else
            AllZone.GameAction.discardRandom("Computer");
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" draws a card, then discards a card");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Whirlpool Rider"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          //shuffle hand into library, then shuffle library
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone hand    = AllZone.getZone(Constant.Zone.Hand   , card.getController());
          Card c[] = hand.getCards();
          for(int i = 0; i < c.length; i++)
              AllZone.GameAction.moveTo(library, c[i]);
          AllZone.GameAction.shuffle(card.getController());

          //draw same number of cards as before
          for(int i = 0; i < c.length; i++)
            AllZone.GameAction.drawCard(card.getController());
        }
      };
      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card.getController() +" shuffles the cards from his hand into his library, then draws that many cards.");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Sky Swallower"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          String opp = AllZone.GameAction.getOpponent(card.getController());
          PlayerZone oppPlay = AllZone.getZone(Constant.Zone.Play, opp);
          PlayerZone myPlay = AllZone.getZone(Constant.Zone.Play, card.getController());

          CardList list = new CardList(myPlay.getCards());
          list.remove(card);//doesn't move Sky Swallower
          while(! list.isEmpty())
          {
            //so "comes into play" abilities don't trigger
            list.get(0).setComesIntoPlay(Command.Blank);

            oppPlay.add(list.get(0));
            myPlay.remove(list.get(0));

            list.get(0).setController(opp);
            list.remove(0);
          }
        }//resolve()
      };

      Command intoPlay = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(AllZone.GameAction.getOpponent(card.getController()) +" gains control of all other permanents you control");
          AllZone.Stack.add(ability);
        }
      };
      card.setComesIntoPlay(intoPlay);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Terror"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return (getCreature().size() != 0) && (AllZone.Phase.getTurn() > 4);
        }
        public void chooseTargetAI()
        {
          Card best = CardFactoryUtil.AI_getBestCreature(getCreature());
          setTargetCard(best);
        }
        CardList getCreature()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature();
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (! CardUtil.getColors(c).contains(Constant.Color.Black)) &&
                     (! c.getType().contains("Artifact"))                     &&
                     (2 < c.getAttack());
            }
          });
          return list;
        }//getCreature()
        public void resolve()
        {
          AllZone.GameAction.destroyNoRegeneration(getTargetCard());
        }//resolve()
      };

      Input runtime = new Input()
      {
        public void showMessage()
        {
          CardList choice = new CardList();
          choice.addAll(AllZone.Human_Play.getCards());
          choice.addAll(AllZone.Computer_Play.getCards());

          choice = choice.getType("Creature");
          choice = choice.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (! CardUtil.getColors(c).contains(Constant.Color.Black)) &&
                     (! c.getType().contains("Artifact"));
            }
          });

          stopSetNext(CardFactoryUtil.input_targetSpecific(spell, choice, "Select target non-artifact, non-black creature to destroy."));
        }
      };

      card.clearSpellAbility();
      spell.setBeforePayMana(runtime);
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Expunge"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return (getCreature().size() != 0) && (AllZone.Phase.getTurn() > 4);
        }
        public void chooseTargetAI()
        {
          Card best = CardFactoryUtil.AI_getBestCreature(getCreature());
          setTargetCard(best);
        }
        CardList getCreature()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature();
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (! CardUtil.getColors(c).contains(Constant.Color.Black)) &&
                     (! c.getType().contains("Artifact"))                     &&
                     (2 < c.getAttack());
            }
          });
          return list;
        }//getCreature()
        public void resolve()
        {
          AllZone.GameAction.destroyNoRegeneration(getTargetCard());
        }//resolve()
      };

      Input runtime = new Input()
      {
        public void showMessage()
        {
          CardList choice = new CardList();
          choice.addAll(AllZone.Human_Play.getCards());
          choice.addAll(AllZone.Computer_Play.getCards());

          choice = choice.getType("Creature");
          choice = choice.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (! CardUtil.getColors(c).contains(Constant.Color.Black)) &&
                     (! c.getType().contains("Artifact"));
            }
          });

          stopSetNext(CardFactoryUtil.input_targetSpecific(spell, choice, "Select target non-artifact, non-black creature to destroy."));
        }
      };

      card.clearSpellAbility();
      spell.setBeforePayMana(runtime);

      spell.setDescription(card.getText());
      card.addSpellAbility(spell);


      card.addSpellAbility(CardFactoryUtil.ability_cycle(card, "2"));
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Pongify"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return (getCreature().size() != 0) && (AllZone.Phase.getTurn() > 4);
        }
        public void chooseTargetAI()
        {
          Card best = CardFactoryUtil.AI_getBestCreature(getCreature());
          setTargetCard(best);
        }
        CardList getCreature()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature();
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (3 < c.getAttack());
            }
          });
          return list;
        }//getCreature()
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            PlayerZone play = AllZone.getZone(getTargetCard());
            makeToken(play);

            AllZone.GameAction.destroyNoRegeneration(getTargetCard());
          }
        }//resolve()
        void makeToken(PlayerZone play)
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("G");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Ape");
          c.setAttack(3);
          c.setDefense(3);

          play.add(c);
        }
      };//SpellAbility

      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Devour in Shadow"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return (getCreature().size() != 0) && (AllZone.Phase.getTurn() > 4);
        }
        public void chooseTargetAI()
        {
          Card best = CardFactoryUtil.AI_getBestCreature(getCreature());
          setTargetCard(best);

          if(AllZone.Computer_Life.getLife() <= best.getDefense())
          {
            CardList human = CardFactoryUtil.AI_getHumanCreature(AllZone.Computer_Life.getLife() - 1);
            CardListUtil.sortAttack(human);

            if(0 < human.size())
              setTargetCard(human.get(0));
          }
        }
        CardList getCreature()
        {
          return CardFactoryUtil.AI_getHumanCreature();
        }//getCreature()
        public void resolve()
        {
          AllZone.GameAction.destroyNoRegeneration(getTargetCard());

          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.subtractLife(getTargetCard().getDefense());
        }//resolve()
      };

      card.clearSpellAbility();
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Faceless Butcher"))
    {
      final CommandReturn getCreature = new CommandReturn()
      {
        public Object execute()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          //remove "this card"
          list.remove(card);

          return list;
        }
      };//CommandReturn

      final SpellAbility abilityComes = new Ability(card, "0")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            PlayerZone play = AllZone.getZone(getTargetCard());
            play.remove(getTargetCard());
          }
        }//resolve()
      };

      final Input inputComes = new Input()
      {
        public void showMessage()
        {
          CardList choice = (CardList)getCreature.execute();

          stopSetNext(CardFactoryUtil.input_targetSpecific(abilityComes, choice, "Select target creature to remove from the game"));
          ButtonUtil.disableAll();//to disable the Cancel button
        }
      };
      Command commandComes = new Command()
      {
        public void execute()
        {
          CardList creature = (CardList)getCreature.execute();
          String s = card.getController();
          if(creature.size() == 0)
            return;
          else if(s.equals(Constant.Player.Human))
            AllZone.InputControl.setInput(inputComes);
          else //computer
          {
            Card target;

            //try to target human creature
            CardList human = CardFactoryUtil.AI_getHumanCreature();
            target = CardFactoryUtil.AI_getBestCreature(human);//returns null if list is empty

            if(target == null)
            {
              //must target computer creature
              CardList computer = new CardList(AllZone.Computer_Play.getCards());
              computer = computer.getType("Creature");
              computer.remove(card);

              computer.shuffle();
              target = computer.get(0);
            }
            abilityComes.setTargetCard(target);
            AllZone.Stack.add(abilityComes);
          }//else
        }//execute()
      };//CommandComes
      Command commandDestroy = new Command()
      {
        public void execute()
        {
          Object o = abilityComes.getTargetCard();
          if(o == null || ((Card)o).isToken())
            return;

          SpellAbility ability = new Ability(card, "0")
          {
            public void resolve()
            {
              //copy card to reset card attributes like attack and defense
              Card c = abilityComes.getTargetCard();
              if(! c.isToken())
              {
                c = AllZone.CardFactory.copyCard(c);
                c.setController(c.getOwner());

                PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getOwner());
                play.add(c);
              }
            }//resolve()
          };//SpellAbility
          ability.setStackDescription("Faceless Butcher - returning creature to play");
          AllZone.Stack.add(ability);
        }//execute()
      };//Command

      card.setComesIntoPlay(commandComes);
      card.setDestroy(commandDestroy);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Oblivion Ring") || cardName.equals("Oubliette"))
    {
      final SpellAbility enchantment = new Spell(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            PlayerZone play = AllZone.getZone(getTargetCard());
            play.remove(getTargetCard());

            //put permanent into play
            Card c = getSourceCard();
            AllZone.getZone(Constant.Zone.Play, c.getController()).add(c);
          }
        }//resolve()

        public boolean canPlayAI()
        {
          //try to target human creature
          CardList human = CardFactoryUtil.AI_getHumanCreature();
          Card target = CardFactoryUtil.AI_getBestCreature(human);//returns null if list is empty

          if(target == null)
            return false;
          else
          {
            setTargetCard(target);
            return true;
          }
        }//canPlayAI()
      };//SpellAbility enchantment


      final Input target = new Input()
      {
        //showMessage() is always the first method called
        public void showMessage()
        {
          AllZone.Display.showMessage("Select non-land to remove from the game.");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}

        public void selectCard(Card c, PlayerZone zone)
        {
          if(zone.is(Constant.Zone.Play) && !c.isLand())
          {
            enchantment.setTargetCard(c);

            stopSetNext(new Input_PayManaCost(enchantment));
          }
        }
      };//Input target

      Command commandDestroy = new Command()
      {
        public void execute()
        {
          Object o = enchantment.getTargetCard();
          if(o == null || ((Card)o).isToken())
            return;

          SpellAbility ability = new Ability(card, "0")
          {
            public void resolve()
            {
              //copy card to reset card attributes like attack and defense
              Card c = enchantment.getTargetCard();
              if(! c.isToken())
              {
                c = AllZone.CardFactory.copyCard(c);
                c.setController(c.getOwner());

                PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getOwner());
                play.add(c);
              }
            }//resolve()
          };//SpellAbility
          ability.setStackDescription(card.getName() +" - returning creature to play");
          AllZone.Stack.add(ability);
        }//execute()
      };//Command
      card.setDestroy(commandDestroy);

      card.clearSpellAbility();
      card.addSpellAbility(enchantment);

      enchantment.setBeforePayMana(target);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Test Destroy"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI() {return false;}
        public void resolve()
        {
          AllZone.GameAction.destroy(getTargetCard());
        }//resolve()
      };

      card.clearSpellAbility();
      spell.setBeforePayMana(CardFactoryUtil.input_targetType(spell, "All"));
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Take Possession"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI() {return 0 < CardFactoryUtil.AI_getHumanCreature().size();}

        public void chooseTargetAI()
        {
          Card best = CardFactoryUtil.AI_getBestCreature(CardFactoryUtil.AI_getHumanCreature());
          setTargetCard(best);
        }

        public void resolve()
        {
          Card c = getTargetCard();
          c.setController(card.getController());

          ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(false);
          ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(false);

          PlayerZone from = AllZone.getZone(c);
          PlayerZone to = AllZone.getZone(Constant.Zone.Play, card.getController());

          from.remove(c);
          to.add(c);

          ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(true);
          ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(true);

        }//resolve()
      };

      card.clearSpellAbility();
      spell.setBeforePayMana(CardFactoryUtil.input_targetType(spell, "All"));
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Night's Whisper"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return AllZone.Computer_Life.getLife()>2;
        }
        public void resolve()
        {
          //draw 2 cards, subtract 2 life
          String player = card.getController();
          AllZone.GameAction.drawCard(player);
          AllZone.GameAction.drawCard(player);

          AllZone.GameAction.getPlayerLife(player).subtractLife(2);
        }//resolve()
      };

      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Infest"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList human    = new CardList(AllZone.Human_Play.getCards());
          CardList computer = new CardList(AllZone.Computer_Play.getCards());

          human    = human.getType("Creature");
          computer = computer.getType("Creature");

          human = CardListUtil.filterToughness(human, 2);
          computer = CardListUtil.filterToughness(computer, 2);

          //the computer will at least destroy 2 more human creatures
          return computer.size() < human.size()-1;
        }//canPlayAI()

        public void resolve()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          for(int i = 0; i < list.size(); i++)
            list.get(i).addDamage(2);
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Ember-Fist Zubera"))
    {
      //counts Zubera in all graveyards for this turn
      final CommandReturn countZubera = new CommandReturn()
      {
        public Object execute()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());

          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (c.getTurnInZone() == AllZone.Phase.getTurn()) &&
                      c.getType().contains("Zubera");
            }
          });//CardListFilter()

          return new Integer(list.size());
        }
      };

      final Input[] input = new Input[1];

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          //human chooses target on resolve,
          //computer chooses target in Command destroy
          if(Constant.Player.Human.equals(card.getController()))
             AllZone.InputControl.setInput(input[0]);
          else
          {
            int damage = ((Integer)countZubera.execute()).intValue();

            if(getTargetCard() != null)
            {
              if(AllZone.GameAction.isCardInPlay(getTargetCard()))
              {
                Card c = getTargetCard();
                c.addDamage(damage);
              }
            }
            else
              AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
          }
        }//resolve()
      };//SpellAbility

      input[0] = new Input()
      {
        public void showMessage()
        {
          int damage = ((Integer)countZubera.execute()).intValue();
          AllZone.Display.showMessage("Select target Creature or Player - " + damage +" damage ");
          ButtonUtil.disableAll();
        }
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play))
          {
            int damage = ((Integer)countZubera.execute()).intValue();
            card.addDamage(damage);

            //have to do this since state effects aren't checked
            //after this "Input" class is done
            //basically this makes everything work right
            //Ember-Fist Zubera can destroy a 2/2 creature
            AllZone.GameAction.checkStateEffects();
            stop();
          }
        }//selectCard()
        public void selectPlayer(String player)
        {
          int damage = ((Integer)countZubera.execute()).intValue();
          AllZone.GameAction.getPlayerLife(player).subtractLife(damage);
          stop();
        }//selectPlayer()
      };//Input

      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card +" causes damage to creature or player");

          int damage = ((Integer)countZubera.execute()).intValue();

          String con = card.getController();
          CardList list = CardFactoryUtil.AI_getHumanCreature();

          //human chooses target on resolve,
          //computer chooses target in Command destroy
          if(con.equals(Constant.Player.Computer))
            ability.setTargetPlayer(Constant.Player.Human);

          AllZone.Stack.add(ability);
        }//execute()
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Ashen-Skin Zubera"))
    {
      //counts Zubera in all graveyards for this turn
      final CommandReturn countZubera = new CommandReturn()
      {
        public Object execute()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());

          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (c.getTurnInZone() == AllZone.Phase.getTurn()) &&
                      c.getType().contains("Zubera");
            }
          });//CardListFilter()
          return new Integer(list.size());
        }
      };//CommandReturn

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          int discard = ((Integer)countZubera.execute()).intValue();

          if(Constant.Player.Human.equals(getTargetPlayer()))
            AllZone.InputControl.setInput(CardFactoryUtil.input_discard(discard));
          else
          {
            for(int i = 0; i < discard; i++)
              AllZone.GameAction.discardRandom(Constant.Player.Computer);
          }
        }//resolve()
      };//SpellAbility

      Command destroy = new Command()
      {
        public void execute()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          ability.setTargetPlayer(opponent);
          ability.setStackDescription(card +" - " + opponent +" discards cards");

          AllZone.Stack.add(ability);
        }//execute()
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Floating-Dream Zubera"))
    {
      //counts Zubera in all graveyards for this turn
      final CommandReturn countZubera = new CommandReturn()
      {
        public Object execute()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());

          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (c.getTurnInZone() == AllZone.Phase.getTurn()) &&
                      c.getType().contains("Zubera");
            }
          });//CardListFilter()
          return new Integer(list.size());
        }
      };//CommandReturn

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          int number = ((Integer)countZubera.execute()).intValue();

          for(int i = 0; i < number; i++)
            AllZone.GameAction.drawCard(getTargetPlayer());
        }//resolve()
      };//SpellAbility

      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setTargetPlayer(card.getController());
          ability.setStackDescription(card +" - " +card.getController() +" draws cards");
          AllZone.Stack.add(ability);

        }//execute()
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Silent-Chant Zubera"))
    {
      //counts Zubera in all graveyards for this turn
      final CommandReturn countZubera = new CommandReturn()
      {
        public Object execute()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());

          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (c.getTurnInZone() == AllZone.Phase.getTurn()) &&
                      c.getType().contains("Zubera");
            }
          });//CardListFilter()
          return new Integer(list.size());
        }
      };//CommandReturn

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          int number = ((Integer)countZubera.execute()).intValue();

          PlayerLife life = AllZone.GameAction.getPlayerLife(getTargetPlayer());
          life.addLife(number * 2);
        }//resolve()
      };//SpellAbility

      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setTargetPlayer(card.getController());
          ability.setStackDescription(card +" - " +card.getController() +" gains life");
          AllZone.Stack.add(ability);

        }//execute()
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Dripping-Tongue Zubera"))
    {
      //counts Zubera in all graveyards for this turn
      final CommandReturn countZubera = new CommandReturn()
      {
        public Object execute()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());

          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return (c.getTurnInZone() == AllZone.Phase.getTurn()) &&
                      c.getType().contains("Zubera");
            }
          });//CardListFilter()
          return new Integer(list.size());
        }
      };//CommandReturn

      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          int count = ((Integer)countZubera.execute()).intValue();
          for(int i = 0; i < count; i++)
            makeToken();
        }//resolve()
        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Spirit");
          c.setAttack(1);
          c.setDefense(1);

          play.add(c);
        }//makeToken()

      };//SpellAbility

      Command destroy = new Command()
      {
        public void execute()
        {
          ability.setTargetPlayer(card.getController());
          ability.setStackDescription(card +" - " +card.getController() +" puts tokens into play");
          AllZone.Stack.add(ability);
        }//execute()
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Keiga, the Tide Star"))
    {
      final SpellAbility ability = new Ability(card, "0")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
            PlayerZone oldPlay = AllZone.getZone(getTargetCard());

            //so "comes into play" abilities don't trigger
            getTargetCard().setComesIntoPlay(Command.Blank);

            play.add(getTargetCard());
            oldPlay.remove(getTargetCard());

            getTargetCard().setController(card.getController());
          }
        }//resolve()
      };

      final Input targetInput = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target creature");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play))
          {
            ability.setTargetCard(card);
            ability.setStackDescription("Gain control of " +ability.getTargetCard());
            AllZone.Stack.add(ability);
            stop();
          }
        }
      };//Input
      Command destroy = new Command()
      {
        public void execute()
        {
          String con = card.getController();
          CardList list = CardFactoryUtil.AI_getHumanCreature();

          if(con.equals(Constant.Player.Human))
            AllZone.InputControl.setInput(targetInput);
          else if(list.size() != 0)
          {
            Card target = CardFactoryUtil.AI_getBestCreature(list);
            ability.setTargetCard(target);
            AllZone.Stack.add(ability);
          }
        }//execute()
      };
      card.setDestroy(destroy);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Angelic Blessing"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          final Command eot = new Command()
          {
            public void execute()
            {
              Card c = getTargetCard();
              if(AllZone.GameAction.isCardInPlay(c))
              {
                c.setAttack (c.getAttack()  - 3);
                c.setDefense(c.getDefense() - 3);
                c.removeKeyword("Flying");
              }
            }//execute()
          };//Command

          Card c = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack (c.getAttack()  + 3);
            c.setDefense(c.getDefense() + 3);
            c.addKeyword("Flying");

            AllZone.EndOfTurn.addUntil(eot);
          }
        }//resolve()
        public boolean canPlayAI()
        {
          Combat combat = ComputerUtil.getAttackers();
          return (0 != combat.getAttackers().length);
        }
        public void chooseTargetAI()
        {
          Combat combat = ComputerUtil.getAttackers();
          Card[] attacker = combat.getAttackers();
          if(attacker.length != 0)
            setTargetCard(attacker[0]);
          else
          {
            CardList list = new CardList(AllZone.Computer_Play.getCards());
            list = list.getType("Creature");
            Card best = CardFactoryUtil.AI_getBestCreature(list);
            setTargetCard(best);
          }
        }//chooseTargetAI()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
    }//*************** END ************ END **************************


/*

    //*************** START *********** START **************************
    if(cardName.equals("Molten Rain"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList land = new CardList(AllZone.Human_Play.getCards());
          land = land.getType("Basic");
          return land.size() != 0;
        }

        public void chooseTargetAI()
        {
          //target basic land that Human only has 1 or 2 in play
          CardList land = new CardList(AllZone.Human_Play.getCards());
          land = land.getType("Basic");

          Card target = null;

          String[] name = {"Forest", "Swamp", "Plains", "Mountain", "Island"};
          for(int i = 0; i < name.length; i++)
            if(land.getName(name[i]).size() == 1)
            {
              target = land.getName(name[i]).get(0);
              break;
            }

          //see if there are only 2 lands of the same type
          if(target == null)
          {
            for(int i = 0; i < name.length; i++)
              if(land.getName(name[i]).size() == 2)
              {
              target = land.getName(name[i]).get(0);
              break;
            }
          }//if
          if(target == null)
          {
            land.shuffle();
            target = land.get(0);
          }
          setTargetCard(target);
        }//chooseTargetAI()

        public void resolve()
        {
          Card c = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(c))
          {
            AllZone.GameAction.destroy(c);

            if(! c.getType().contains("Basic"))
              AllZone.GameAction.getPlayerLife(c.getController()).subtractLife(2);
          }
        }//resolve()

      };//Spell
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target Land");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isLand() && zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }//selectCard()
      };
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************

*/


//*************** START *********** START **************************
if(cardName.equals("Molten Rain"))
{
final SpellAbility spell = new Spell(card)
{
public void resolve()
{
  Card c = getTargetCard();
  if(AllZone.GameAction.isCardInPlay(c))
    AllZone.GameAction.destroy(c);
}//resolve()

};//Spell
card.clearSpellAbility();
card.addSpellAbility(spell);

spell.setChooseTargetAI(CardFactoryUtil.AI_targetType("Land", AllZone.Human_Play));
spell.setBeforePayMana(CardFactoryUtil.input_targetType(spell, "Land"));
}//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Elvish Piper"))
    {
      final SpellAbility ability = new Ability_Tap(card, "G")
      {
        public boolean canPlayAI() {return getCreature().size() != 0;}
        public void chooseTargetAI()
        {
          card.tap();
          Card target = CardFactoryUtil.AI_getBestCreature(getCreature());
          setTargetCard(target);
        }
        CardList getCreature()
        {
          CardList list = new CardList(AllZone.Computer_Hand.getCards());
          list = list.getType("Creature");
          return list;
        }

        public void resolve()
        {
          Card c = getTargetCard();
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());

          if(AllZone.GameAction.isCardInZone(c, hand))
          {
            hand.remove(c);
            play.add(c);
          }
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("G, tap: Put a creature card from your hand into play.");

      final Command paid  = new Command()
      {
        public void execute()
        {
          AllZone.InputControl.resetInput();
          AllZone.Stack.add(ability);
        }
      };
      final Command unpaid = new Command()
      {
        public void execute()
        {
            card.untap();
        }
      };
      final Input target = new Input()
      {
        public void showMessage()
        {
          ButtonUtil.enableOnlyCancel();
          AllZone.Display.showMessage("Select creature from your hand to put into play");
        }
        public void selectCard(Card c, PlayerZone zone)
        {
          if(c.isCreature() && zone.is(Constant.Zone.Hand, Constant.Player.Human))
          {
            card.tap();

            ability.setTargetCard(c);//since setTargetCard() changes stack description
            ability.setStackDescription("Put into play " +c);

            AllZone.InputControl.setInput(new Input_PayManaCost_Ability(ability.getManaCost(), paid, unpaid));
          }
        }
        public void selectButtonCancel()
        {
          card.untap();
          stop();
        }
      };//Input target
      ability.setBeforePayMana(target);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Weathered Wayfarer"))
    {
      final SpellAbility ability = new Ability_Tap(card, "W")
      {
        public void resolve()
        {
          //getTargetCard() will NEVER be null

          //checks to see if card is still in the library
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          if(AllZone.GameAction.isCardInZone(getTargetCard(), library))
          {
            PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
            AllZone.GameAction.moveTo(hand, getTargetCard());
          }
        }//resolve()
        public boolean canPlay()
        {
          String oppPlayer = AllZone.GameAction.getOpponent(card.getController());

          PlayerZone selfZone = AllZone.getZone(Constant.Zone.Play, card.getController());
          PlayerZone oppZone = AllZone.getZone(Constant.Zone.Play, oppPlayer);

          CardList self = new CardList(selfZone.getCards());
          CardList opp = new CardList(oppZone.getCards());

          self = self.getType("Land");
          opp = opp.getType("Land");

          //checks to see if any land in library
          PlayerZone selfLibrary = AllZone.getZone(Constant.Zone.Library, card.getController());
          CardList library = new CardList(selfLibrary.getCards());
          library = library.getType("Land");

          return (self.size() < opp.size()) && (library.size() != 0) && super.canPlay();
        }
       public void chooseTargetAI()
       {
         PlayerZone selfLibrary = AllZone.getZone(Constant.Zone.Library, card.getController());
         CardList library = new CardList(selfLibrary.getCards());
         library = library.getType("Land");

         setTargetCard(library.get(0));
       }
      };//SpellAbility

      Input target = new Input()
      {
        public void showMessage()
        {
          CardList land = new CardList(AllZone.Human_Library.getCards());
          land = land.getType("Land");
          Object o = AllZone.Display.getChoiceOptional("Select a Land", land.toArray());

          //techincally not correct, but correct enough
          //this allows players to look at their decks without paying anything
          if(o == null)
            stop();
          else
          {
            AllZone.GameAction.shuffle("Human");
            ability.setTargetCard((Card)o);
            stopSetNext(new Input_PayManaCost(ability));
          }
        }//showMessage()
      };//Input - target

      card.addSpellAbility(ability);
      ability.setDescription("W, tap: Search your library for a land card, reveal it, and put it into your hand. Then shuffle your library. Play this ability only if an opponent controls more lands than you.");
      ability.setBeforePayMana(target);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Disciple of Kangee"))
    {
      final SpellAbility ability = new Ability_Tap(card, "U")
      {
        public boolean canPlayAI()
        {
          if(CardFactoryUtil.AI_doesCreatureAttack(card))
            return false;

          return CardFactoryUtil.AI_getHumanCreature("Flying").isEmpty() &&
                 (getCreature().size() != 0);
        }
        public void chooseTargetAI()
        {
          card.tap();
          Card target = CardFactoryUtil.AI_getBestCreature(getCreature());
          setTargetCard(target);
        }
        CardList getCreature()
        {
          CardList list = new CardList(AllZone.Computer_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && (!CardFactoryUtil.AI_doesCreatureAttack(c)) &&
                     (! c.getKeyword().contains("Flying"));
            }
          });
          list.remove(card);
          return list;
        }//getCreature()
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            final Card[] creature = new Card[1];
            final Command EOT = new Command()
            {
              public void execute()
              {
                if(AllZone.GameAction.isCardInPlay(creature[0]))
                  creature[0].removeKeyword("Flying");
              }
            };
            creature[0] = getTargetCard();
            creature[0].addKeyword("Flying");
            AllZone.EndOfTurn.addUntil(EOT);
          }//if (card is in play)
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("U, tap: Target creature gains flying.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature(ability));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Puppeteer"))
    {
      //tap - target creature
      final SpellAbility ability = new Ability_Tap(card, "U")
      {
        public boolean canPlayAI() {return getTapped().size() != 0;}
        public void chooseTargetAI()
        {
          card.tap();
          Card target = CardFactoryUtil.AI_getBestCreature(getTapped());
          setTargetCard(target);
        }
        CardList getTapped()
        {
          CardList list = new CardList(AllZone.Computer_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && c.isTapped();
            }
          });
          return list;
        }//getTapped()
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            Card c = getTargetCard();
            if(c.isTapped())
              c.untap();
            else
              c.tap();
          }
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("U, tap: Tap or untap target creature.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature(ability));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Kabuto Moth"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          CardList list = getAttackers();
          if(list.isEmpty())
            return false;
          else
          {
            setTargetCard(list.get(0));
            return true;
          }
        }//canPlayAI()

        CardList getAttackers()
        {
          Card[] c = ComputerUtil.getAttackers().getAttackers();
          CardList list = new CardList(c);

          //remove "this card" from attackers, if it is there
          list.remove(card);
          return list;
        }


        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            final Card[] creature = new Card[1];

            creature[0] = getTargetCard();
            creature[0].setAttack(creature[0].getAttack() + 1);
            creature[0].setDefense(creature[0].getDefense() + 2);

            final Command EOT = new Command()
            {
              public void execute()
              {
                if(AllZone.GameAction.isCardInPlay(creature[0]))
                {
                  creature[0].setAttack(creature[0].getAttack() - 1);
                  creature[0].setDefense(creature[0].getDefense() - 2);
                }
              }
            };
            AllZone.EndOfTurn.addUntil(EOT);
          }//is card in play?
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Target creature gets +1/+2 until end of turn.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Sorceress Queen"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          Card c = getCreature();
          if(c == null)
            return false;
          else
          {
            setTargetCard(c);
            return true;
          }
        }//canPlayAI()
        //may return null
        public Card getCreature()
        {
          CardList untapped = CardFactoryUtil.AI_getHumanCreature();
          untapped = untapped.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isUntapped() && 2 < c.getDefense();
            }
          });
          if(untapped.isEmpty())
            return null;

          Card big = CardFactoryUtil.AI_getBestCreature(untapped);
          return big;
        }
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            final Card[] creature = new Card[1];

            creature[0] = getTargetCard();
            final int[] originalAttack  = {creature[0].getAttack()};
            final int[] originalDefense = {creature[0].getDefense()};

            creature[0].setAttack(0);
            creature[0].setDefense(2);

            final Command EOT = new Command()
            {
              public void execute()
              {
                if(AllZone.GameAction.isCardInPlay(creature[0]))
                {
                  creature[0].setAttack(originalAttack[0]);
                  creature[0].setDefense(originalDefense[0]);
                }
              }
            };
            AllZone.EndOfTurn.addUntil(EOT);
          }//is card in play?
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Target creature other than Sorceress Queen becomes 0/2 until end of turn.");
      //this ability can target "this card" when it shouldn't be able to
      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************








    //*************** START *********** START **************************
    if(cardName.equals("Akki Drillmaster"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            final Card[] creature = new Card[1];

            creature[0] = getTargetCard();
            creature[0].addKeyword("Haste");

            final Command EOT = new Command()
            {
              public void execute()
              {
                if(AllZone.GameAction.isCardInPlay(creature[0]))
                  creature[0].removeKeyword("Haste");
              }
            };//Command
            AllZone.EndOfTurn.addUntil(EOT);
          }//is card in play?
        }//resolve()
        public boolean canPlayAI() {return false;}
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Target creature gains haste until end of turn.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Immaculate Magistrate"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            PlayerZone zone = AllZone.getZone(Constant.Zone.Play, card.getController());
            CardList list = new CardList(zone.getCards());
            int nElf = list.getType("Elf").size();

            Card c = getTargetCard();
            c.setAttack(c.getAttack() + nElf);
            c.setDefense(c.getDefense() + nElf);
          }//is card in play?
        }//resolve()
        public boolean canPlayAI()
        {
          CardList list = new CardList(AllZone.Computer_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isUntapped() && !c.equals(card) && c.isCreature();
            }
          });

          if(list.isEmpty())
            return false;

          list.shuffle();

          setTargetCard(list.get(0));
          return true;
        }//canPlayAI()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Put a +1/+1 counter on target creature for each Elf you control.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Vulshok Sorcerer"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public void chooseTargetAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(1);
          list.shuffle();

          if(list.isEmpty() || AllZone.Human_Life.getLife() < 5)
            setTargetPlayer(Constant.Player.Human);
          else
            setTargetCard(list.get(0));
        }
        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
              getTargetCard().addDamage(1);
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(1);
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Vulshok Sorcerer deals 1 damage to target creature or player.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(ability));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Prodigal Sorcerer") || cardName.equals("Prodigal Pyromancer"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public void chooseTargetAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(1);
          list.shuffle();

          if(list.isEmpty() || AllZone.Human_Life.getLife() < 5)
            setTargetPlayer(Constant.Player.Human);
          else
            setTargetCard(list.get(0));
        }
        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
              getTargetCard().addDamage(1);
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(1);
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: " +card.getName() +" deals 1 damage to target creature or player.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(ability));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Kamahl, Pit Fighter"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature();

          boolean b = true;

          //use ability only if no human creatures can block
          for(int i = 0; i < list.size(); i++)
            if(list.get(i).isUntapped())
              b = false;

          return b || AllZone.Phase.getPhase().equals(Constant.Phase.Main2);
        }

        public void chooseTargetAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(3);
          list.shuffle();

          if(list.isEmpty() || AllZone.Human_Life.getLife() < 17)
            setTargetPlayer(Constant.Player.Human);
          else
            setTargetCard(list.get(0));
        }
        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
              getTargetCard().addDamage(3);
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(3);
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: " +card.getName() +" deals 3 damage to target creature or player.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(ability));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Mawcor"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          return AllZone.Phase.getPhase().equals(Constant.Phase.Main2);
        }

        public void chooseTargetAI()
        {
          setTargetPlayer(Constant.Player.Human);
        }
        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
              getTargetCard().addDamage(1);
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(1);
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Mawcor deals 1 damage to target creature or player.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(ability));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Orcish Artillery"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          return AllZone.Computer_Life.getLife() > 6;
        }

        public void chooseTargetAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(2);
          list.shuffle();

          if(list.isEmpty() || AllZone.Human_Life.getLife() < 5)
            setTargetPlayer(Constant.Player.Human);
          else
            setTargetCard(list.get(0));
        }
        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              getTargetCard().addDamage(2);
              //3 damage to self
              AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(3);
            }
          }
          else
          {
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(2);
            //3 damage to self
            AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(3);
          }
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Orcish Artillery deals 2 damage to target creature or player and 3 damage to you.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(ability));
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Sparksmith"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          int n = countGoblins();
          return (AllZone.Computer_Life.getLife() > n) &&
                 (CardFactoryUtil.AI_getHumanCreature(n).size() != 0);
        }
        public void chooseTargetAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(countGoblins());
          list.shuffle();
          setTargetCard(list.get(0));
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
            life.subtractLife(countGoblins());

            getTargetCard().addDamage(countGoblins());
          }
        }//resolve()
        int countGoblins()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Goblin");
          return list.size();
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Sparksmith deals X damage to target creature and X damage to you, where X is the number of Goblins in play.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature(ability));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Royal Assassin"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          CardList human = CardFactoryUtil.AI_getHumanCreature();
          human = human.filter(new CardListFilter()
          {
            public boolean addCard(Card c) {return c.isTapped();}
          });

          CardListUtil.sortAttack(human);
          CardListUtil.sortFlying(human);

          if(0 < human.size())
            setTargetCard(human.get(0));

          return 0 < human.size();
        }
        public void resolve()
        {
          Card c = getTargetCard();

          if(AllZone.GameAction.isCardInPlay(c) && c.isTapped())
          {
            AllZone.GameAction.destroy(c);
          }
        }//resolve()
      };//SpellAbility

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target tapped creature to destroy");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          if(c.isCreature() && zone.is(Constant.Zone.Play) && c.isTapped())
          {
            //tap ability
            card.tap();

            ability.setTargetCard(c);
            AllZone.Stack.add(ability);
            stop();
          }
        }//selectCard()
      };//Input

      card.addSpellAbility(ability);
      ability.setDescription("tap: Destroy target tapped creature.");
      ability.setBeforePayMana(target);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Hex"))
    {
      final Card[] target = new Card[6];
      final int[] index = new int[1];

      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList human = CardFactoryUtil.AI_getHumanCreature();

          CardListUtil.sortAttack(human);
          CardListUtil.sortFlying(human);

          if(6 <= human.size())
          {
            for(int i = 0; i < 6; i++)
              target[i] = human.get(i);
          }

          return 6 <= human.size();
        }
        public void resolve()
        {
          for(int i = 0; i < target.length; i++)
            if(AllZone.GameAction.isCardInPlay(target[i]))
              AllZone.GameAction.destroy(target[i]);
        }//resolve()
      };//SpellAbility


      final Input input = new Input()
      {
        public void showMessage()
        {
          int count = 6 - index[0];
          AllZone.Display.showMessage("Select target " + count +" creatures to destroy");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          if(c.isCreature() && zone.is(Constant.Zone.Play))
          {
            target[index[0]] = c;
            index[0]++;
            showMessage();

            if(index[0] == target.length)
              stopSetNext(new Input_PayManaCost(spell));
          }
        }//selectCard()
      };//Input

      Input runtime = new Input()
      {
        public void showMessage()
        {
          index[0] = 0;
          stopSetNext(input);
        }
      };//Input

      card.clearSpellAbility();
      card.addSpellAbility(spell);
      spell.setBeforePayMana(runtime);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Peel from Reality"))
    {
      final Card[] target = new Card[2];
      final int[] index = new int[1];

      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return false;
        }
        public void resolve()
        {
          //bounce two creatures in target[]
          for(int i = 0; i < target.length; i++)
          {
            Card c = target[i];
            PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, c.getOwner());

            if(AllZone.GameAction.isCardInPlay(c))
              AllZone.GameAction.moveTo(hand, c);
          }
        }//resolve()
      };//SpellAbility


      final Input input = new Input()
      {
        public void showMessage()
        {
          if(index[0] == 0)
            AllZone.Display.showMessage("Select target creature you control to bounce return to their owners' hand");
          else
            AllZone.Display.showMessage("Select target creature you don't control to return to their owners' hand");

          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          //must target creature you control
          if(index[0] == 0 && !c.getController().equals(card.getController()))
             return;

          //must target creature you don't control
          if(index[0] == 1 && c.getController().equals(card.getController()))
             return;


          if(c.isCreature() && zone.is(Constant.Zone.Play))
          {
            target[index[0]] = c;
            index[0]++;
            showMessage();

            if(index[0] == target.length)
              stopSetNext(new Input_PayManaCost(spell));
          }
        }//selectCard()
      };//Input

      Input runtime = new Input()
      {
        public void showMessage()
        {
          index[0] = 0;
          stopSetNext(input);
        }
      };//Input

      card.clearSpellAbility();
      card.addSpellAbility(spell);
      spell.setBeforePayMana(runtime);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Ashes to Ashes"))
    {
      final Card[] target = new Card[2];
      final int[] index = new int[1];

      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return 2 <= getNonArtifact().size() &&
                 5 < AllZone.Computer_Life.getLife() ;
        }
        public void chooseTargetAI()
        {
          CardList human = getNonArtifact();
          CardListUtil.sortAttack(human);

          target[0] = human.get(0);
          target[1] = human.get(1);
        }
        CardList getNonArtifact()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature();
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return !c.isArtifact();
            }
          });
          return list;
        }//getNonArtifact()

        public void resolve()
        {
          for(int i = 0; i < target.length; i++)
          {
            Card c = target[i];
            PlayerZone remove = AllZone.getZone(Constant.Zone.Removed_From_Play, c.getOwner());

            AllZone.GameAction.moveTo(remove, c);
          }

          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.subtractLife(5);
        }//resolve()
      };//SpellAbility


      final Input input = new Input()
      {
        public void showMessage()
        {
          if(index[0] == 0)
            AllZone.Display.showMessage("Select 1st target non-artifact creature to remove from the game");
          else
            AllZone.Display.showMessage("Select 2nd target non-artifact creature to remove from the game");

          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          if(! CardUtil.getColor(c).equals(Constant.Color.Colorless) &&
             c.isCreature()              &&
             zone.is(Constant.Zone.Play))
          {
            target[index[0]] = c;
            index[0]++;
            showMessage();

            if(index[0] == target.length)
              stopSetNext(new Input_PayManaCost(spell));
          }
        }//selectCard()
      };//Input

      Input runtime = new Input()
      {
        public void showMessage()
        {
          index[0] = 0;
          stopSetNext(input);
        }
      };//Input

      card.clearSpellAbility();
      card.addSpellAbility(spell);
      spell.setBeforePayMana(runtime);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Visara the Dreadful"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          if(CardFactoryUtil.AI_doesCreatureAttack(card))
            return false;

          return CardFactoryUtil.AI_getHumanCreature().size() != 0;
        }
        public void chooseTargetAI()
        {
          CardList creature = CardFactoryUtil.AI_getHumanCreature();
          Card target = CardFactoryUtil.AI_getBestCreature(creature);
          setTargetCard(target);
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            AllZone.GameAction.destroyNoRegeneration(getTargetCard());
          }
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Destroy target creature. It can't be regenerated");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature(ability));
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Wojek Embermage"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          return (CardFactoryUtil.AI_getHumanCreature(1).size() != 0) &&
                  (AllZone.Phase.getPhase().equals(Constant.Phase.Main2));
        }

        public void chooseTargetAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(1);
          list.shuffle();
          setTargetCard(list.get(0));
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            CardList list = getRadiance(getTargetCard());
            for(int i = 0; i < list.size(); i++)
              list.get(i).addDamage(1);
          }
        }//resolve()
        //parameter Card c, is included in CardList
        //no multi-colored cards
        CardList getRadiance(Card c)
        {
          String color = CardUtil.getColor(c);
          if(color.equals(Constant.Color.Colorless))
          {
            CardList list = new CardList();
            list.add(c);
            return list;
          }

          CardList sameColor = new CardList();

          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          for(int i = 0; i < list.size(); i++)
            if(CardUtil.getColor(list.get(i)).equals(color))
              sameColor.add(list.get(i));

          return sameColor;
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("Radiance - tap: Wojek Embermage deals 1 damage to target creature and each other creature that shares a color with it.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature(ability));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Timberwatch Elf"))
    {
      final Card[] creature = new Card[1];
      final int[] pump = new int[1];
      final Command EOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(creature[0]))
          {
            creature[0].setAttack(creature[0].getAttack()  - pump[0]);
            creature[0].setDefense(creature[0].getDefense() - pump[0]);
          }
        }
      };

      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          CardList c = getAttackers();
          if(c.isEmpty())
            return false;
          else
          {
            setTargetCard(c.get(0));
            return true;
          }
        }//canPlayAI()
        CardList getAttackers()
        {
          Card[] c = ComputerUtil.getAttackers().getAttackers();
          CardList list = new CardList(c);

          //remove "this card" from attackers, if it is there
          list.remove(card);
          list.shuffle();
          return list;
        }//getAttackers()

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            creature[0] = getTargetCard();
            pump[0]     = countElves();

            creature[0].setAttack(creature[0].getAttack()  + pump[0]);
            creature[0].setDefense(creature[0].getDefense() + pump[0]);

            AllZone.EndOfTurn.addUntil(EOT);
          }
        }//resolve()
        int countElves()
        {
          CardList elf = new CardList();
          elf.addAll(AllZone.Human_Play.getCards());
          elf.addAll(AllZone.Computer_Play.getCards());

          elf = elf.getType("Elf");
          return elf.size();
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Target creature gets +X/+X until end of turn, where X is the number of Elves in play.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************








    //*************** START *********** START **************************
    if(cardName.equals("Mad Auntie"))
    {
      final Card[] creature = new Card[1];
      final Command EOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(creature[0]))
          {
            creature[0].setShield(0);
          }
        }
      };

      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
            return false;
        }//canPlayAI()
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            getTargetCard().addShield();
            AllZone.EndOfTurn.addUntil(EOT);
          }
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Regenerate another target Goblin.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Intrepid Hero"))
    {
      //tap ability - no cost - target creature

      final Ability_Tap ability = new Ability_Tap(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            AllZone.GameAction.destroy(getTargetCard());
          }
        }//resolve()
        public boolean canPlayAI()
        {
          return getHumanCreatures().size() != 0;
        }
        public void chooseTargetAI()
        {
          CardList human = getHumanCreatures();
          human.shuffle();
          setTargetCard(human.get(0));
        }
        CardList getHumanCreatures()
        {
          CardList list = new CardList(AllZone.Human_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && 3 < c.getAttack();
            }
          });
          return list;
        }
      };//SpellAbility

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target creature with power 4 or greater");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play) && 3 < card.getAttack())
          {
            ability.setTargetCard(card);
            stopSetNext(new Input_NoCost_TapAbility(ability));
          }
        }
      };

      card.addSpellAbility(ability);
      ability.setDescription("tap: Destroy target creature with power 4 or greater.");

      ability.setBeforePayMana(target);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Adarkar Valkyrie"))
    {
      //tap ability - no cost - target creature - EOT

      final Card[] target = new Card[1];
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          //resets the Card destroy Command
          target[0].setDestroy(Command.Blank);
        }
      };

      final Ability_Tap ability = new Ability_Tap(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            target[0] = getTargetCard();
            AllZone.EndOfTurn.addUntil(untilEOT);

            //when destroyed, return to play
            //add triggered ability to target card
            target[0].setDestroy(new Command()
            {
              public void execute()
              {
                AllZone.Stack.add(new Ability(card, "0", "Return " +target[0] +" from graveyard to play")
                {
                  public void resolve()
                  {
                    PlayerZone grave = AllZone.getZone(target[0]);
                    //checks to see if card is still in the graveyard
                    if(AllZone.GameAction.isCardInZone(target[0], grave))
                    {
                      PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
                      AllZone.GameAction.moveTo(play, target[0]);
                      target[0].setController(card.getController());
                    }
                  }
                });
              }//execute()
            });
          }//if
        }//resolve()
        public boolean canPlayAI()
        {
          return false;
        }
      };//SpellAbility

      Input targetInput = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target non-token creature other than this card");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          //must target non-token creature, and cannot target itself
          if(c.isCreature() && (!c.isToken()) && (!c.equals(card)))
          {
            ability.setTargetCard(c);
            stopSetNext(new Input_NoCost_TapAbility(ability));
          }
        }
      };

      card.addSpellAbility(ability);
      ability.setDescription("tap: When target creature other than Adarkar Valkyrie is put into a graveyard this turn, return that card to play under your control.");

      ability.setBeforePayMana(targetInput);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Imperious Perfect"))
    {
      //mana tap ability
      final Ability_Tap ability = new Ability_Tap(card, "G")
      {
        public boolean canPlayAI()
        {
          String phase = AllZone.Phase.getPhase();
          return phase.equals(Constant.Phase.Main2);
        }
        public void chooseTargetAI() {card.tap();}

        public void resolve()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("G");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Elf");
          c.addType("Warrior");

          c.setAttack(1);
          c.setDefense(1);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//resolve()
      };//SpellAbility

      card.addSpellAbility(ability);

      ability.setDescription("G, tap: Put a 1/1 green Elf Warrior creature token into play.");
      ability.setStackDescription("Imperious Perfect - Put a 1/1 green Elf Warrior creature token into play.");
      ability.setBeforePayMana(new Input_PayManaCost(ability));
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Wall of Kelp"))
    {
      //mana tap ability
      final Ability_Tap ability = new Ability_Tap(card, "U U")
      {
        public boolean canPlayAI()
        {
          String phase = AllZone.Phase.getPhase();
          return phase.equals(Constant.Phase.Main2);
        }
        public void chooseTargetAI() {card.tap();}

        public void resolve()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("U");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Kelp");
          c.addType("Wall");
          c.addKeyword("Defender");

          c.setAttack(0);
          c.setDefense(1);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);
        }//resolve()
      };//SpellAbility

      card.addSpellAbility(ability);

      ability.setDescription("UU, tap: Put a 0/1 blue Kelp Wall creature token with defender into play.");
      ability.setStackDescription("Wall of Kelp - Put a 0/1 blue Kelp Wall creature token with defender into play");
      ability.setBeforePayMana(new Input_PayManaCost(ability));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Vedalken Mastermind"))
    {
      //mana tap ability
      final Ability_Tap ability = new Ability_Tap(card, "U")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, getTargetCard().getOwner());
          AllZone.GameAction.moveTo(hand, getTargetCard());
        }//resolve()
      };//SpellAbility


      Input runtime = new Input()
      {
        public void showMessage()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList choice = new CardList(play.getCards());
          stopSetNext(CardFactoryUtil.input_targetSpecific(ability, choice, "Select a permanent you control."));
        }
      };
      card.addSpellAbility(ability);
      ability.setDescription("U, tap: Return target permanent you control to its owner's hand.");
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Helldozer"))
    {
      //mana tap ability
      final Ability_Tap ability = new Ability_Tap(card, "B B B")
      {
        public boolean canPlayAI()
        {
          if(CardFactoryUtil.AI_doesCreatureAttack(card))
            return false;

          CardList land = new CardList(AllZone.Human_Play.getCards());
          land = land.getType("Land");
          return land.size() != 0;
        }

        public void chooseTargetAI()
        {
          card.tap();

          //target basic land that Human only has 1 or 2 in play
          CardList land = new CardList(AllZone.Human_Play.getCards());
          land = land.getType("Land");

          Card target = null;

          String[] name = {"Forest", "Swamp", "Plains", "Mountain", "Island"};
          for(int i = 0; i < name.length; i++)
            if(land.getName(name[i]).size() == 1)
            {
              target = land.getName(name[i]).get(0);
              break;
            }

          //see if there are only 2 lands of the same type
          if(target == null)
          {
            for(int i = 0; i < name.length; i++)
              if(land.getName(name[i]).size() == 2)
              {
              target = land.getName(name[i]).get(0);
              break;
            }
          }//if
          if(target == null)
          {
            land.shuffle();
            target = land.get(0);
          }
          setTargetCard(target);
        }//chooseTargetAI()

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            AllZone.GameAction.destroy(getTargetCard());

            //if non-basic, untap Helldozer
            if(! getTargetCard().getType().contains("Basic"))
              card.untap();
          }
        }//resolve()
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("BBB, tap: Destroy target land. If that land is nonbasic, untap Helldozer.");
      ability.setBeforePayMana(CardFactoryUtil.input_targetType(ability, "Land"));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Cao Cao, Lord of Wei"))
    {
      //mana tap ability
      final Ability_Tap ability = new Ability_Tap(card, "0")
      {
        public void chooseTargetAI() {card.tap();}
        public boolean canPlayAI()
        {
          int hand = AllZone.Human_Hand.getCards().length;
          if((! CardFactoryUtil.AI_doesCreatureAttack(card)) && hand != 0)
            return true;

          return 2 <= hand;
        }

        public void resolve()
        {
          String player = AllZone.GameAction.getOpponent(card.getController());

          if(player.equals(Constant.Player.Human))
             AllZone.InputControl.setInput(CardFactoryUtil.input_discard(2));
          else
          {
            AllZone.GameAction.discardRandom(player);
            AllZone.GameAction.discardRandom(player);
          }
        }//resolve()
        public boolean canPlay()
        {
          String opp = AllZone.GameAction.getOpponent(card.getController());
          setStackDescription(card.getName() +" - " +opp +" discards 2 cards");

          String phase         = AllZone.Phase.getPhase();
          String activePlayer = AllZone.Phase.getActivePlayer();

          return super.canPlay() &&
                 phase.equals(Constant.Phase.Main1) &&
                 card.getController().equals(activePlayer);
        }
      };//SpellAbility

      ability.setChooseTargetAI(CardFactoryUtil.AI_targetHuman());
      ability.setDescription("tap: Target opponent discards two cards. Play this ability only during your turn, before the combat phase.");
      ability.setBeforePayMana(new Input_NoCost_TapAbility(ability));

      card.addSpellAbility(ability);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Wayward Soul"))
    {
      //mana ability
      final Ability ability = new Ability(card, "U")
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(3);
            card.setDefense(2);
            card.setKeyword(new ArrayList());
            card.addKeyword("Flying");

            card.setAssignedDamage(0);
            card.setDamage(0);
            card.untap();
            AllZone.getZone(card).remove(card);

            //put card on top of library
            PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getOwner());
            library.add(card, 0);
          }
        }//resolve()
      };//SpellAbility

      Input runtime = new Input()
      {
        public void showMessage()
        {
          ability.setStackDescription("Put " +card +" on top of its owner's library");

          stopSetNext(new Input_PayManaCost(ability));
        }
      };
      ability.setDescription("U: Put Wayward Soul on top of its owner's library.");
      ability.setStackDescription("Put Wayward Soul on top of its owner's library.");
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Coastal Hornclaw"))
    {
      //sacrifice ability - targets itself - until EOT
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.removeKeyword("Flying");
        }
      };

      //mana tap ability
      final Ability ability = new Ability(card, "0")
      {
        public boolean canPlayAI()
        {
          CardList land = new CardList(AllZone.Computer_Play.getCards());
          land = land.getType("Land");

          CardList canBlock = CardFactoryUtil.AI_getHumanCreature();
          canBlock = canBlock.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isUntapped();
            }
          });

          return (land.size() != 0)                       &&
                 (! card.getKeyword().contains("Flying")) &&
                 CardFactoryUtil.AI_getHumanCreature("Flying").isEmpty() &&
                 (! card.hasSickness()) &&
                 (AllZone.Phase.getPhase().equals(Constant.Phase.Main1));
        }

        public void chooseTargetAI()
        {
          CardList land = new CardList(AllZone.Computer_Play.getCards());
          land = land.getType("Land");
          land.shuffle();
          AllZone.GameAction.sacrifice(land.get(0));
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.addKeyword("Flying");
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility


      Input runtime = new Input()
      {
        public void showMessage()
        {
          ability.setStackDescription(card +" gains flying until EOT.");

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList choice = new CardList(play.getCards());
          choice = choice.getType("Land");
          stopSetNext(CardFactoryUtil.input_sacrifice(ability, choice, "Select a land to sacrifice."));
        }
      };
      ability.setStackDescription(card +" gains flying until end of turn.");
      ability.setDescription("Sacrifice a land: Coastal Hornclaw gains flying until end of turn.");
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Spitting Spider"))
    {
      final Ability ability = new Ability(card, "0")
      {
        public boolean canPlayAI()
        {
          return false;
        }
        public void resolve()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && c.getKeyword().contains("Flying");
            }
          });

          for(int i = 0; i < list.size(); i++)
            list.get(i).addDamage(1);
        }//resolve()
      };//SpellAbility

      Input runtime = new Input()
      {
        public void showMessage()
        {

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList choice = new CardList(play.getCards());
          choice = choice.getType("Land");
          stopSetNext(CardFactoryUtil.input_sacrifice(ability, choice, "Select a land to sacrifice."));
        }
      };
      ability.setStackDescription(card +" deals 1 damage to each creature with flying.");
      ability.setDescription("Sacrifice a land: Spitting Spider deals 1 damage to each creature with flying.");
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Killer Whale"))
    {
      final Ability ability = new Ability(card, "U")
      {
        public boolean canPlayAI()
        {
          return false;
        }
        public void resolve()
        {
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              card.removeKeyword("Flying");
            }
          };

          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.addKeyword("Flying");
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility

      ability.setStackDescription(card +" gains flying until end of turn.");
      ability.setDescription("U: Killer Whale gains flying until end of turn.");
      card.addSpellAbility(ability);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Lay Waste") || cardName.equals("Stone Rain") ||
       cardName.equals("Ice Storm") || cardName.equals("Sinkhole"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList land = new CardList(AllZone.Human_Play.getCards());
          land = land.getType("Basic");
          return land.size() != 0;
        }

        public void chooseTargetAI()
        {
          //target basic land that Human only has 1 or 2 in play
          CardList land = new CardList(AllZone.Human_Play.getCards());
          land = land.getType("Basic");

          Card target = null;

          String[] name = {"Forest", "Swamp", "Plains", "Mountain", "Island"};
          for(int i = 0; i < name.length; i++)
            if(land.getName(name[i]).size() == 1)
            {
              target = land.getName(name[i]).get(0);
              break;
            }

          //see if there are only 2 lands of the same type
          if(target == null)
          {
            for(int i = 0; i < name.length; i++)
              if(land.getName(name[i]).size() == 2)
              {
              target = land.getName(name[i]).get(0);
              break;
            }
          }//if
          if(target == null)
          {
            land.shuffle();
            target = land.get(0);
          }
          setTargetCard(target);
        }//chooseTargetAI()
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            AllZone.GameAction.destroy(getTargetCard());
        }//resolve()
      };//SpellAbility

      card.clearSpellAbility();
      spell.setBeforePayMana(CardFactoryUtil.input_targetType(spell, "Land"));

      card.addSpellAbility(spell);

      if(cardName.equals("Lay Waste"))
      {
        card.addSpellAbility(CardFactoryUtil.ability_cycle(card, "2"));
        spell.setDescription("Destroy target land");
      }

      return card;
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Wild Mongrel"))
    {
      //sacrifice ability - targets itself - until EOT
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setAttack(card.getAttack() - 1);
          card.setDefense(card.getDefense() - 1);
        }
      };

      //mana tap ability
      final Ability ability = new Ability(card, "0")
      {
        public boolean canPlayAI()
        {
          Card[] hand = AllZone.Computer_Hand.getCards();
          return CardFactoryUtil.AI_doesCreatureAttack(card) && (hand.length != 0);
        }
        public void  chooseTargetAI() {AllZone.GameAction.discardRandom(Constant.Player.Computer);}

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(card.getAttack() + 1);
            card.setDefense(card.getDefense() + 1);

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility


      Input runtime = new Input()
      {
        public void showMessage()
        {
          ability.setStackDescription(card +" gets +1/+1 until EOT.");

          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          CardList choice = new CardList(hand.getCards());
          stopSetNext(CardFactoryUtil.input_sacrifice(ability, choice, "Select a card to discard."));
        }
      };
      ability.setStackDescription(card +" gets +1/+1 until end of turn.");
      ability.setDescription("Discard a card: Wild Mongrel gets +1/+1 until end of turn.");
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************









    //*************** START *********** START **************************
    if(cardName.equals("Whiptongue Frog"))
    {
      //mana ability - targets itself - until EOT
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.removeKeyword("Flying");
        }
      };

      //mana tap ability
      final Ability ability = new Ability(card, "U")
      {
        public boolean canPlayAI()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          return (! card.hasSickness()) &&
                 (! card.getKeyword().contains("Flying")) &&
                 (AllZone.Phase.getPhase().equals(Constant.Phase.Main1));
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.addKeyword("Flying");
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility


      Input runtime = new Input()
      {
        public void showMessage()
        {
          ability.setStackDescription(card +" gains flying until EOT.");
          stopSetNext(new Input_PayManaCost(ability));
        }
      };
      ability.setStackDescription("Whiptongue Frog gains flying until EOT.");
      ability.setDescription("U: Whiptongue Frog gains flying until end of turn.");
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Sarcomite Myr"))
    {
      //mana ability - targets itself - until EOT
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.removeKeyword("Flying");
        }
      };

      //mana tap ability
      final Ability ability = new Ability(card, "2")
      {
        public boolean canPlayAI()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");

          return (! card.hasSickness()) &&
                 (! card.getKeyword().contains("Flying")) &&
                 (AllZone.Phase.getPhase().equals(Constant.Phase.Main1));
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.addKeyword("Flying");
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility


      Input runtime = new Input()
      {
        public void showMessage()
        {
          ability.setStackDescription(card +" gains flying until EOT.");
          stopSetNext(new Input_PayManaCost(ability));
        }
      };
      ability.setStackDescription(card +" - gains flying until EOT.");
      ability.setDescription("2: Sarcomite Myr gains flying until end of turn.");
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);

      //ability 2
      final Ability ability2 = new Ability(card, "2")
      {
        public boolean canPlayAI()
        {
          return false;
        }

        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
        }//resolve()
      };//SpellAbility

      card.addSpellAbility(ability2);
      ability2.setDescription("2, Sacrifice Sarcomite Myr: Draw a card.");
      ability2.setStackDescription("Sarcomite Myr - draw a card");
      ability2.setBeforePayMana(new Input_PayManaCost_Ability(ability2.getManaCost(), new Command()
      {
        public void execute()
        {
          AllZone.GameAction.sacrifice(card);
          AllZone.Stack.add(ability2);
        }
      }));

    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Nantuko Shade"))
    {
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          card.setAttack(card.getAttack() - 1);
          card.setDefense(card.getDefense() - 1);
        }
      };

      //mana tap ability
      final Ability ability = new Ability(card, "B")
      {
        public boolean canPlayAI() {return CardFactoryUtil.AI_doesCreatureAttack(card);}

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(card.getAttack() + 1);
            card.setDefense(card.getDefense() + 1);

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility


      Input runtime = new Input()
      {
        public void showMessage()
        {
          ability.setStackDescription(card +" gets +1/+1 until EOT");
          stopSetNext(new Input_PayManaCost(ability));
        }
      };
      ability.setDescription("B: gets +1/+1 until end of turn.");
      ability.setStackDescription(card +" gets +1/+1 until end of turn.");

      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Goblin Balloon Brigade"))
    {
      //mana ability - targets itself - until EOT
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            card.removeKeyword("Flying");
        }
      };

      //mana tap ability
      final Ability ability = new Ability(card, "R")
      {
        public boolean canPlayAI()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          return flying.isEmpty()    &&
              (! card.hasSickness()) &&
              (! card.getKeyword().contains("Flying")) &&
              (! CardFactoryUtil.AI_getHumanCreature().isEmpty());
        }
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.addKeyword("Flying");
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility


      Input runtime = new Input()
      {
        public void showMessage()
        {
          ability.setStackDescription(card +" gains flying until EOT.");
          stopSetNext(new Input_PayManaCost(ability));
        }
      };
      ability.setStackDescription("Goblin Balloon Brigade gains flying until EOT.");
      ability.setDescription("R: Goblin Balloon Brigade gains flying until end of turn.");
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Manta Riders"))
    {
      //mana ability - targets itself - until EOT
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            card.removeKeyword("Flying");
        }
      };

      //mana tap ability
      final Ability ability = new Ability(card, "U")
      {
        public boolean canPlayAI()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          return flying.isEmpty()    &&
              (! card.hasSickness()) &&
              (! card.getKeyword().contains("Flying")) &&
              (! CardFactoryUtil.AI_getHumanCreature().isEmpty());
        }
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.addKeyword("Flying");
            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility


      Input runtime = new Input()
      {
        public void showMessage()
        {
          ability.setStackDescription(card +" gains flying until EOT.");
          stopSetNext(new Input_PayManaCost(ability));
        }
      };
      ability.setStackDescription(card.getName() +" gains flying until EOT.");
      ability.setDescription("U: Manta Riders gains flying until end of turn.");
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Turtleshell Changeling"))
    {
      //mana ability - targets itself - until EOT
      //mana ability
      final Ability ability = new Ability(card, "1 U")
      {
        public boolean canPlayAI()
        {
          return CardFactoryUtil.AI_doesCreatureAttack(card) && card.getAttack() == 1;
        }
        public void resolve()
        {
          //in case ability is played twice
          final int[] oldAttack = new int[1];
          final int[] oldDefense = new int[1];

          oldAttack[0]  = card.getAttack();
          oldDefense[0] = card.getDefense();

          card.setAttack(oldDefense[0]);
          card.setDefense(oldAttack[0]);

          //EOT
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              card.setAttack(oldAttack[0]);
              card.setDefense(oldDefense[0]);
            }
          };

          AllZone.EndOfTurn.addUntil(untilEOT);
        }//resolve()
      };//SpellAbility


      ability.setStackDescription(card +" - switch power and toughness until EOT.");
      ability.setDescription("1U: Switch Turtleshell Changeling's power and toughness until end of turn.");
      card.addSpellAbility(ability);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Wings of Velis Vel"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          Combat combat = ComputerUtil.getAttackers();
          Card[] attack = combat.getAttackers();

          CardList small = new CardList(AllZone.Computer_Play.getCards());
          small = small.getType("Creature");

          //try to make a good attacker
          if(0 < small.size())
          {
            CardListUtil.sortAttackLowFirst(small);
            setTargetCard(small.get(0));

            return true && AllZone.Phase.getPhase().equals(Constant.Phase.Main1);
          }

          return false;
        }//canPlayAI()
        public void resolve()
        {
          //in case ability is played twice
          final int[] oldAttack = new int[1];
          final int[] oldDefense = new int[1];

          final Card card[] = new Card[1];
          card[0] = getTargetCard();

          oldAttack[0]  = card[0].getAttack();
          oldDefense[0] = card[0].getDefense();

          card[0].setAttack(4);
          card[0].setDefense(4);
          card[0].addKeyword("Flying");

          //EOT
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              card[0].setAttack(oldAttack[0]);
              card[0].setDefense(oldDefense[0]);

              card[0].removeKeyword("Flying");
            }
          };

          AllZone.EndOfTurn.addUntil(untilEOT);
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Epic Proportions"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList list = new CardList(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          if(list.isEmpty())
           return false;

          //else
          CardListUtil.sortAttack(list);
          CardListUtil.sortFlying(list);

          setTargetCard(list.get(0));
          return true;
        }//canPlayAI()
        public void resolve()
        {
          Card c = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(c.getAttack() + 5);
            c.setDefense(c.getDefense() + 5);
            c.addKeyword("Trample");
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Lignify"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList c = CardFactoryUtil.AI_getHumanCreature();
          CardListUtil.sortAttack(c);
          CardListUtil.sortFlying(c);

          if(c.isEmpty())
            return false;

          if(2 <= c.get(0).getAttack() && c.get(0).getKeyword().contains("Flying"))
          {
            setTargetCard(c.get(0));
            return true;
          }

          CardListUtil.sortAttack(c);
          if(4 <= c.get(0).getAttack())
          {
            setTargetCard(c.get(0));
            return true;
          }

          return false;
        }//canPlayAI()
        public void resolve()
        {
          Card c = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(0);
            c.setDefense(4);

            c.setType(new ArrayList());
            c.addType("Creature");
            c.addType("Treefolk");

            c.setKeyword(new ArrayList());

            c.clearSpellAbility();
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Control Magic"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList c = CardFactoryUtil.AI_getHumanCreature();
          CardListUtil.sortAttack(c);
          CardListUtil.sortFlying(c);

          if(c.isEmpty())
            return false;

          if(2 <= c.get(0).getAttack() && c.get(0).getKeyword().contains("Flying"))
          {
            setTargetCard(c.get(0));
            return true;
          }

          CardListUtil.sortAttack(c);
          if(4 <= c.get(0).getAttack())
          {
            setTargetCard(c.get(0));
            return true;
          }

          return false;
        }//canPlayAI()
        public void resolve()
        {
          Card c = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(c))
          {
            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(false);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(false);

            PlayerZone from = AllZone.getZone(c);
            from.remove(c);

            PlayerZone to = AllZone.getZone(Constant.Zone.Play, card.getController());
            to.add(c);

            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(true);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(true);
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    //destroy doesn't work
    if(cardName.equals("Sower of Temptation"))
    {
      final Card moveCreature[] = new Card[1];

      final SpellAbility spell = new Spell_Permanent(card)
      {
        public boolean canPlayAI()
        {
          CardList c = CardFactoryUtil.AI_getHumanCreature();
          CardListUtil.sortAttack(c);
          CardListUtil.sortFlying(c);

          if(c.isEmpty())
            return false;

          if(2 <= c.get(0).getAttack() && c.get(0).getKeyword().contains("Flying"))
          {
            setTargetCard(c.get(0));
            return true;
          }

          CardListUtil.sortAttack(c);
          if(4 <= c.get(0).getAttack())
          {
            setTargetCard(c.get(0));
            return true;
          }

          return false;
        }//canPlayAI()
        public void resolve()
        {
          super.resolve();

          Card c = getTargetCard();
          moveCreature[0] = c;

          if(AllZone.GameAction.isCardInPlay(c))
          {
            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(false);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(false);

            c.setSickness(true);
            c.setController(card.getController());

            PlayerZone from = AllZone.getZone(c);
            from.remove(c);

            PlayerZone to = AllZone.getZone(Constant.Zone.Play, card.getController());
            to.add(c);

            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(true);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(true);
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));

      card.setDestroy(new Command()
      {
        public void execute()
        {
          Card c = moveCreature[0];

          if(AllZone.GameAction.isCardInPlay(c))
          {
            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(false);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(false);

            PlayerZone from = AllZone.getZone(c);
            from.remove(c);

            PlayerZone to = AllZone.getZone(Constant.Zone.Play, c.getOwner());
            to.add(c);

            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(true);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(true);
          }//if
        }//execute()
      });//Command
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Terramorphic Expanse"))
    {
      //tap sacrifice
      final Ability_Tap ability = new Ability_Tap(card, "0")
      {
        public boolean canPlayAI()
        {
          return false;
          /*
          //sacrifice Sakura-Tribe Elder if Human has any creatures
          CardList list = new CardList(AllZone.Human_Play.getCards());
          list = list.getType("Creature");
          return list.size() != 0 && card.isUntapped();
          */
        }
        public void chooseTargetAI()
        {
          AllZone.GameAction.sacrifice(card);
        }
        public boolean canPlay()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          CardList list = new CardList(library.getCards());
          list = list.getType("Basic");
          return list.size() > 0;
        }//canPlay()
        public void resolve()
        {
          if(owner.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void computerResolve()
        {
          CardList play = new CardList(AllZone.Computer_Play.getCards());
          play = play.getType("Basic");

          CardList library = new CardList(AllZone.Computer_Library.getCards());
          library = library.getType("Basic");

          //this shouldn't happen, but it is defensive programming, haha
          if(library.isEmpty())
            return;

          Card land = null;

          //try to find a basic land that isn't in play
          for(int i = 0; i < library.size(); i++)
            if(! play.containsName(library.get(i)))
            {
              land = library.get(i);
              break;
            }

          //if not found
          //library will have at least 1 basic land because canPlay() checks that
          if(land == null)
            land = library.get(0);

          land.tap();
          AllZone.Computer_Library.remove(land);
          AllZone.Computer_Play.add(land);

          AllZone.GameAction.shuffle(Constant.Player.Computer);
        }//computerResolve()

        public void humanResolve()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone play    = AllZone.getZone(Constant.Zone.Play   , card.getController());

          CardList basicLand = new CardList(library.getCards());
          basicLand = basicLand.getType("Basic");

          Object o = AllZone.Display.getChoiceOptional("Choose a basic land", basicLand.toArray());
          if(o != null)
          {
            Card land = (Card)o;
            land.tap();

            library.remove(land);
            play.add(land);
          }
          AllZone.GameAction.shuffle(card.getController());
        }//resolve()
      };//SpellAbility

      Input runtime = new Input()
      {
        boolean once = true;
        public void showMessage()
        {
          //this is necessary in order not to have a StackOverflowException
          //because this updates a card, it creates a circular loop of observers
          if(once)
          {
            once = false;
            AllZone.GameAction.sacrifice(card);

            ability.setStackDescription(card.getController() +" - Search your library for a basic land card and put it into play tapped. Then shuffle your library.");
            AllZone.Stack.add(ability);

            stop();
          }
        }//showMessage()
      };
      card.addSpellAbility(ability);
      ability.setDescription("tap, Sacrifice Terramorphic Expanse: Search your library for a basic land card and put it into play tapped. Then shuffle your library.");
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************


    //*************** START *********** START **************************
    if(cardName.equals("Frostling"))
    {
      final Ability ability = new Ability(card, "0")
      {
        public boolean canPlayAI() {return getCreature().size() != 0;}

        public void  chooseTargetAI()
        {
          CardList list = getCreature();
          list.shuffle();
          setTargetCard(list.get(0));

          AllZone.GameAction.sacrifice(card);
        }
        CardList getCreature()
        {
          //toughness of 1
          CardList list = CardFactoryUtil.AI_getHumanCreature(1);
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              //only get 1/1 flyers or 2/1 creatures
              return (2 <= c.getAttack()) || c.getKeyword().contains("Flying");
            }
          });
          return list;
        }//getCreature()

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            getTargetCard().addDamage(1);
        }//resolve()
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("Sacrifice Frostling: Frostling deals 1 damage to target creature.");
      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature(ability, new Command()
      {
        public void execute()
        {
          AllZone.GameAction.sacrifice(card);
        }
      }));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Mogg Fanatic"))
    {
      final Ability ability = new Ability(card, "0")
      {
        public boolean canPlayAI() {return getCreature().size() != 0;}

        public void chooseTargetAI()
        {
          if(AllZone.Human_Life.getLife() < 3)
            setTargetPlayer(Constant.Player.Human);
          else
          {
            CardList list = getCreature();
            list.shuffle();
            setTargetCard(list.get(0));
          }
          AllZone.GameAction.sacrifice(card);
        }//chooseTargetAI()
        CardList getCreature()
        {
          //toughness of 1
          CardList list = CardFactoryUtil.AI_getHumanCreature(1);
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              //only get 1/1 flyers or 2/1 creatures
              return (2 <= c.getAttack()) || c.getKeyword().contains("Flying");
            }
          });
          return list;
        }//getCreature()

        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
              getTargetCard().addDamage(1);
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(1);
        }//resolve()
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("Sacrifice Mogg Fanatic: Mogg Fanatic deals 1 damage to target creature or player.");
      ability.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(ability, new Command()
      {
        public void execute()
        {
          AllZone.GameAction.sacrifice(card);
        }
      }));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Goblin Skycutter"))
    {
      final Ability ability = new Ability(card, "0")
      {
        public boolean canPlayAI() {return getFlying().size() != 0;}

        public void chooseTargetAI()
        {
          AllZone.GameAction.sacrifice(card);

          CardList flying = getFlying();
          flying.shuffle();
          setTargetCard(flying.get(0));
        }

        CardList getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          flying = flying.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.getDefense() == 2;
            }
          });
          return flying;
        }//getFlying()

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            getTargetCard().addDamage(2);
            getTargetCard().removeKeyword("Flying");
          }

          final Card[] target = {getTargetCard()};
          AllZone.EndOfTurn.addUntil(new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(getTargetCard()))
                getTargetCard().addKeyword("Flying");
            }
          });
        }//resolve()
      };//SpellAbility

      Input runtime = new Input()
      {
        public void showMessage()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && c.getKeyword().contains("Flying");
            }
          });

          stopSetNext(CardFactoryUtil.input_targetSpecific(ability, list, "Select a creature with flying to deal 2 damage to", new Command()
          {
            public void execute()
            {
              AllZone.GameAction.sacrifice(card);
            }
          }));
        }//showMessage()
      };//Input

      card.addSpellAbility(ability);
      ability.setDescription("Sacrifice Goblin Skycutter: Goblin Skycutter deals 2 damage to target creature with flying. That creature loses flying until end of turn.");
      ability.setBeforePayMana(runtime);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Sakura-Tribe Elder"))
    {
      //tap sacrifice
      final SpellAbility ability = new Ability_Activated(card, "0")
      {
        public boolean canPlayAI()
        {
          //sacrifice Sakura-Tribe Elder if Human has any creatures
          CardList list = new CardList(AllZone.Human_Play.getCards());
          list = list.getType("Creature");
          return list.size() != 0;
        }
        public void chooseTargetAI()
        {
          AllZone.GameAction.sacrifice(card);
        }
        public boolean canPlay()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          CardList list = new CardList(library.getCards());
          list = list.getType("Basic");
          return list.size() > 0 && super.canPlay();
        }//canPlay()
        public void resolve()
        {
          if(owner.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void computerResolve()
        {
          CardList play = new CardList(AllZone.Computer_Play.getCards());
          play = play.getType("Basic");

          CardList library = new CardList(AllZone.Computer_Library.getCards());
          library = library.getType("Basic");

          //this shouldn't happen, but it is defensive programming, haha
          if(library.isEmpty())
            return;

          Card land = null;

          //try to find a basic land that isn't in play
          for(int i = 0; i < library.size(); i++)
            if(! play.containsName(library.get(i)))
            {
              land = library.get(i);
              break;
            }

          //if not found
          //library will have at least 1 basic land because canPlay() checks that
          if(land == null)
            land = library.get(0);

          land.tap();
          AllZone.Computer_Library.remove(land);
          AllZone.Computer_Play.add(land);

          AllZone.GameAction.shuffle(Constant.Player.Computer);
        }//computerResolve()
        public void humanResolve()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone play    = AllZone.getZone(Constant.Zone.Play   , card.getController());

          CardList basicLand = new CardList(library.getCards());
          basicLand = basicLand.getType("Basic");
          if(basicLand.isEmpty())
            return;

          Object o = AllZone.Display.getChoiceOptional("Choose a basic land", basicLand.toArray());
          if(o != null)
          {
            Card land = (Card)o;
            land.tap();

            library.remove(land);
            play.add(land);
          }

          AllZone.GameAction.shuffle(card.getController());
        }//resolve()
      };//SpellAbility

      Input runtime = new Input()
      {
        boolean once = true;
        public void showMessage()
        {
          //this is necessary in order not to have a StackOverflowException
          //because this updates a card, it creates a circular loop of observers
          if(once)
          {
            once = false;
            AllZone.GameAction.sacrifice(card);
            AllZone.Stack.add(ability);

            stop();
          }
        }//showMessage()
      };
      card.addSpellAbility(ability);
      ability.setBeforePayMana(runtime);
      ability.setDescription("Sacrifice Sakura-Tribe Elder: Search your library for a basic land card, put that card into play tapped, then shuffle your library.");
      ability.setStackDescription("Search your library for a basic land card, put that card into play tapped, then shuffle your library.");
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Transluminant"))
    {
      final Command atEOT = new Command()
      {
        public void execute()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("W");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Spirit");
          c.setAttack(1);
          c.setDefense(1);
          c.addKeyword("Flying");

          PlayerZone play    = AllZone.getZone(Constant.Zone.Play   , card.getController());
          play.add(c);
        }//execute()
      };//Command

      final Ability ability = new Ability(card, "W")
      {
        public boolean canPlayAI()
        {
          CardList list = new CardList(AllZone.Human_Play.getCards());
          list = list.getType("Creature");

          String phase = AllZone.Phase.getPhase();
          return phase.equals(Constant.Phase.Main2) && list.size() != 0;
        }
        public void chooseTargetAI()
        {
          AllZone.GameAction.sacrifice(card);
        }

        public void resolve()
        {
          AllZone.EndOfTurn.addAt(atEOT);
        }//resolve()
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("W, Sacrifice Transluminant: Put a 1/1 white Spirit creature token with flying into play at end of turn.");
      ability.setStackDescription("Put a 1/1 white Spirit creature token with flying into play at end of turn.");
      ability.setBeforePayMana(new Input_PayManaCost_Ability(ability.getManaCost(), new Command()
      {
        public void execute()
        {
          AllZone.GameAction.sacrifice(card);
          AllZone.Stack.add(ability);
        }
      }));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Llanowar Behemoth"))
    {
      final Ability ability = new Ability(card, "0")
      {
        public boolean canPlayAI()
        {
          return (getUntapped().size() != 0) && CardFactoryUtil.AI_doesCreatureAttack(card);
        }
        public void chooseTargetAI()
        {
          Card c = getUntapped().get(0);
          c.tap();
          setTargetCard(c);
        }
        CardList getUntapped()
        {
          CardList list = new CardList(AllZone.Computer_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && c.isUntapped() && (!CardFactoryUtil.AI_doesCreatureAttack(c));
            }
          });
          return list;
        }//getUntapped()

        public void resolve()
        {
          card.setAttack(card.getAttack() + 1);
          card.setDefense(card.getDefense() + 1);

          Command untilEOT = new Command()
          {
            public void execute()
            {
              card.setAttack(card.getAttack() - 1);
              card.setDefense(card.getDefense() - 1);
            }//execute()
          };//Command

          AllZone.EndOfTurn.addUntil(untilEOT);
        }//resolve()
      };//SpellAbility

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select an untapped creature you control");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          if(c.isCreature() && zone.is(Constant.Zone.Play, card.getController()) && c.isUntapped())
          {
            ability.setStackDescription(card +" gets +1/+1 until end of turn.");
            c.tap();
            AllZone.Stack.add(ability);
            stop();
          }
        }
      };//Input
      ability.setBeforePayMana(target);
      ability.setDescription("Tap an untapped creature you control: Llanowar Behemoth gets +1/+1 until end of turn.");

      card.addSpellAbility(ability);
    }//*************** END ************ END **************************







    //*************** START *********** START **************************
    if(cardName.equals("White Shield Crusader"))
    {
      //has 2 non-tap abilities that effects itself

      final Command eot1 = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            card.removeKeyword("Flying");
        }
      };
      final SpellAbility ability1 = new Ability(card, "W")
      {
        public boolean canPlayAI()
        {
          //can play if human does not have any flying creatures and
          //if this card doesn't already have flying
          CardList list = new CardList(AllZone.Human_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && (c.getKeyword().contains("Flying"));
            }
          });
          return list.isEmpty() &&
              (!card.getKeyword().contains("Flying")) &&
              (! card.hasSickness()) &&
              (AllZone.Phase.getPhase().equals(Constant.Phase.Main1));
        }//canPlayAI()
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.addKeyword("Flying");
            AllZone.EndOfTurn.addUntil(eot1);
          }
        }//resolve()
      };//SpellAbility

      //**** start of ability2
      final Command eot2 = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(card))
            card.setAttack(card.getAttack() - 1);
        }
      };
      final SpellAbility ability2 = new Ability(card, "W W")
      {
        public boolean canPlayAI() {return CardFactoryUtil.AI_doesCreatureAttack(card);}

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(card))
          {
            card.setAttack(card.getAttack() + 1);
            AllZone.EndOfTurn.addUntil(eot2);
          }
        }//resolve()
      };//SpellAbility

      card.addSpellAbility(ability1);
      card.addSpellAbility(ability2);

      ability1.setDescription("W: White Shield Crusader gains flying until end of turn.");
      ability2.setDescription("WW: White Shield Crusader gets +1/+0 until end of turn.");

      ability1.setStackDescription(card +" gains flying until end of turn");
      ability2.setStackDescription(card +" gets +1/+0 until end of turn.");

      ability1.setBeforePayMana(new Input_PayManaCost(ability1));
      ability2.setBeforePayMana(new Input_PayManaCost(ability2));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Memnarch"))
    {
      //has 2 non-tap abilities that effects itself
      final SpellAbility ability1 = new Ability(card, "1 U U")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            getTargetCard().addType("Artifact");
          }
        }//resolve()
        public boolean canPlayAI()
        {
          CardList list = getCreature();
          return list.size() != 0;
        }
        public void chooseTargetAI()
        {
          Card target = CardFactoryUtil.AI_getBestCreature(getCreature());
          setTargetCard(target);
        }//chooseTargetAI()
        CardList getCreature()
        {
          CardList list = new CardList(AllZone.Human_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && (!c.isArtifact());
            }
          });
          return list;
        }//getCreature()
      };//SpellAbility

      //**** start of ability2
      final SpellAbility ability2 = new Ability(card, "3 U")
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            //gain control of target artifact
            PlayerZone from = AllZone.getZone(Constant.Zone.Play, getTargetCard().getController());
            from.remove(getTargetCard());


            getTargetCard().setController(card.getController());

            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(false);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(false);

            PlayerZone to = AllZone.getZone(Constant.Zone.Play, card.getController());
            to.add(getTargetCard());
            to.setUpdate(true);

            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(true);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(true);


//            AllZone.GameAction.moveTo(play, getTargetCard());

            //TODO: controller probably is not set correctly
            //TODO: when you take control, the creature looses type "Artifact" since
            //      GameAction.moveTo() makes a new card object
          }
        }//resolve()
        public boolean canPlayAI()
        {
          CardList list = getArtifactCreatures();
          return list.size() != 0;
        }
        public void chooseTargetAI()
        {
          CardList list = getArtifactCreatures();
          Card target = CardFactoryUtil.AI_getBestCreature(list);
          if(target == null)
            target = AllZone.Human_Play.get(0);

          setTargetCard(target);
        }
        CardList getArtifactCreatures()
        {
          CardList list = new CardList(AllZone.Human_Play.getCards());
          list = list.getType("Artifact");
          list = list.getType("Creature");
          return list;
        }
      };//SpellAbility
      card.addSpellAbility(ability1);
      card.addSpellAbility(ability2);

      ability1.setDescription("1UU: Target permanent becomes an artifact in addition to its other types.(This effect doesn't end at end of turn.)");
      ability2.setDescription("3U: Gain control of target artifact.(This effect doesn't end at end of turn.)");

      ability1.setBeforePayMana(CardFactoryUtil.input_targetType(ability1, "All"));
      ability2.setBeforePayMana(CardFactoryUtil.input_targetType(ability2, "Artifact"));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Meloku the Clouded Mirror"))
    {
      final SpellAbility ability = new Ability(card, "1")
      {
        public void resolve()
        {
          Card c = new Card();
          c.setToken(true);

          c.setManaCost("U");
          c.setAttack(1);
          c.setDefense(1);
          c.addKeyword("Flying");
          c.addType("Creature");
          c.addType("Illusion");
          c.setOwner(card.getController());
          c.setController(card.getController());

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);

          //TODO: the "bounced" land should be chosen by the user
          //instead of "automatically" done for him
          CardList island = new CardList(play.getCards());
          island = island.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.getType().contains("Land") && c.isTapped();
            }
          });
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());

          if(! island.isEmpty())
            AllZone.GameAction.moveTo(hand, island.get(0));
        }//resolve()

      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("1, Return a land you control to its owner's hand: Put a 1/1 blue Illusion creature token with flying into play.");
      ability.setStackDescription("Put 1/1 token with flying into play");
      ability.setBeforePayMana(new Input_PayManaCost(ability));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Squall Drifter"))
    {
      final SpellAbility ability = new Ability_Tap(card, "W")
      {
        public void resolve()
        {
          Card c = getTargetCard();
          c.tap();
        }
        public boolean canPlayAI() {return false;}
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("W, tap: Tap target creature.");

      ability.setBeforePayMana(CardFactoryUtil.input_targetCreature(ability));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("That Which Was Taken"))
    {
      final SpellAbility ability = new Ability_Tap(card, "4")
      {
        public void resolve()
        {
          Card c = getTargetCard();

          if(AllZone.GameAction.isCardInPlay(c))
            c.addKeyword("Indestructible");
        }
        public boolean canPlayAI()
        {
          return 0 < getCreatures().size();
        }
        public void chooseTargetAI()
        {
          Card c = CardFactoryUtil.AI_getBestCreature(getCreatures());
          setTargetCard(c);
        }
        CardList getCreatures()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Computer_Play.getCards());
          return list.getType("Creature");
        }
      };//SpellAbility

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target permanent");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          if(zone.is(Constant.Zone.Play) && c != card)//cannot target self
          {
            ability.setTargetCard(c);
            stopSetNext(new Input_PayManaCost(ability));
          }
        }
      };//Input -- target

      ability.setBeforePayMana(target);
      ability.setDescription("4, tap: Tap a divinity counter on target permanent other than That Which Was Taken.");

      card.addSpellAbility(ability);
    }//*************** END ************ END **************************







    //*************** START *********** START **************************
    if(cardName.equals("Stern Judge"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public void resolve()
        {
          AllZone.Human_Life.subtractLife(countSwamps("Human"));
          AllZone.Computer_Life.subtractLife(countSwamps("Computer"));
        }
        int countSwamps(String player)
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, player);
          CardList swamps = new CardList(play.getCards());
          swamps = swamps.getType("Swamp");
          return swamps.size();
        }
        public boolean canPlayAI()
        {
          int computer = countSwamps(Constant.Player.Computer);
          int human    = countSwamps(Constant.Player.Human);

          if((computer >= AllZone.Computer_Life.getLife()) || (human == 0))
            return false;

          return computer <= human;
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Each player loses 1 life for each Swamp he or she controls.");
      ability.setStackDescription("Stern Judge - Each player loses 1 life for each Swamp he or she controls");
      ability.setBeforePayMana(new Input_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Cackling Imp"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          AllZone.GameAction.getPlayerLife(opponent).subtractLife(1);
        }
        public boolean canPlayAI()
        {
          //computer should play ability if this creature doesn't attack
          Combat c = ComputerUtil.getAttackers();
          CardList list = new CardList(c.getAttackers());

          //could this creature attack?, if attacks, do not use ability
          return (! list.contains(card));
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Target player loses 1 life.");
      ability.setStackDescription("Cackling Imp - Opponent loses 1 life.");
      ability.setBeforePayMana(new Input_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Thought Courier"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI() {return false;}
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
          AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Draw a card, then discard a card.");
      ability.setStackDescription("Thought Courier - draw a card, then discard a card.");
      ability.setBeforePayMana(new Input_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Tortuga"))
    {
      final Input discardThenDraw = new Input()
      {
        int nCards = 1;
        int n = 0;

        public void showMessage()
        {
          AllZone.Display.showMessage("Select a card to discard");
          ButtonUtil.disableAll();

          //in case no more cards in hand
          if(n == nCards || AllZone.Human_Hand.getCards().length == 0)
          {
            stop();
            AllZone.GameAction.drawCard(card.getController());
            n = 0; //very important, otherwise the 2nd time you play this ability, you
                   //don't have to discard
          }
        }
        public void selectCard(Card card, PlayerZone zone)
        {
          if(zone.is(Constant.Zone.Hand))
          {
            AllZone.GameAction.discard(card);
            n++;
            showMessage();
          }
        }
      };//SpellAbility





      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI() {return false;}
        public void resolve()
        {
          AllZone.InputControl.setInput(discardThenDraw);
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Discard a card, then draw a card.");
      ability.setStackDescription("Tortuga - Discard a card, then draw a card.");
      ability.setBeforePayMana(new Input_NoCost_TapAbility(ability));


      final Ability_Tap ability2 = new Ability_Tap(card)
      {
        public boolean canPlay()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          return hand.getCards().length == 7;
        }
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
        }
      };//SpellAbility
      card.addSpellAbility(ability2);
      ability2.setDescription("tap: Draw a card. Play this ability only if you have exactly 7 cards in your hand.");
      ability2.setStackDescription("Tortuga - draw a card.");
      ability2.setBeforePayMana(new Input_NoCost_TapAbility(ability2));

    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Library of Alexandria"))
    {
      final Ability_Tap ability2 = new Ability_Tap(card)
      {
        public boolean canPlay()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          return hand.getCards().length == 7 && super.canPlay();
        }
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
        }
      };//SpellAbility
      card.addSpellAbility(ability2);
      ability2.setDescription("tap: Draw a card. Play this ability only if you have exactly 7 cards in your hand.");
      ability2.setStackDescription("Library of Alexandria - draw a card.");
      ability2.setBeforePayMana(new Input_NoCost_TapAbility(ability2));

    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Magus of the Library"))
    {
      final Ability_Tap ability2 = new Ability_Tap(card)
      {
        public boolean canPlay()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          return hand.getCards().length == 7 && super.canPlay();
        }
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
        }
      };//SpellAbility
      card.addSpellAbility(ability2);
      ability2.setDescription("tap: Draw a card. Play this ability only if you have exactly 7 cards in hand.");
      ability2.setStackDescription("Magus of the Library - draw a card.");
      ability2.setBeforePayMana(new Input_NoCost_TapAbility(ability2));

    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Bonded Fetch"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI() {return false;}
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
          AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Draw a card, then discard a card.");
      ability.setStackDescription("Bonded Fetch - draw a card, then discard a card.");
      ability.setBeforePayMana(new Input_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Hammerfist Giant"))
    {
      final Ability_Tap ability = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(4);
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c) {return ! c.getKeyword().contains("Flying");}
          });

          return list.size() > 3 && 6 < AllZone.Computer_Life.getLife();
        }//canPlayAI()
        public void resolve()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.getType("Creature");

          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return ! c.getKeyword().contains("Flying");
            }
          });
          for(int i = 0; i < list.size(); i++)
            list.get(i).addDamage(4);

          AllZone.Human_Life.subtractLife(4);
          AllZone.Computer_Life.subtractLife(4);
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("tap: Hammerfist Giant deals 4 damage to each creature without flying and each player.");
      ability.setStackDescription("Hammerfist Giant - deals 4 damage to each creature without flying and each player.");
      ability.setBeforePayMana(new Input_NoCost_TapAbility(ability));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Urborg Syphon-Mage"))
    {
      final Ability_Tap ability = new Ability_Tap(card, "2 B")
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          AllZone.GameAction.getPlayerLife(opponent).subtractLife(2);

          AllZone.GameAction.getPlayerLife(card.getController()).addLife(2);

          //computer discards here, todo: should discard when ability put on stack
          if(card.getController().equals(Constant.Player.Computer))
            AllZone.GameAction.discardRandom(Constant.Player.Computer);
        }
        public boolean canPlay()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          return super.canPlay() && hand.getCards().length != 0;
        }
        public boolean canPlayAI()
        {
          int life = AllZone.Human_Life.getLife();
          Card[] hand = AllZone.Computer_Hand.getCards();
          return ((life < 11) || (5 < AllZone.Phase.getTurn())) &&
                 hand.length > 0;
        }
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("2B, tap, Discard a card: Each other player loses 2 life. You gain life equal to the life lost this way.");
      ability.setStackDescription("Urborg Syphon-Mage - Opponent loses 2 life, and you gain 2 life");
      ability.setBeforePayMana(new Input_PayManaCost_Ability("2 B", new Command()
      {
        public void execute()
        {
          card.tap();
          AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          AllZone.Stack.add(ability);
        }
      }));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Llanowar Mentor"))
    {
      final Ability_Tap ability = new Ability_Tap(card, "G")
      {
        public void resolve()
        {
          makeToken();

          //computer discards here, todo: should discard when ability put on stack
          if(card.getController().equals(Constant.Player.Computer))
            AllZone.GameAction.discardRandom(Constant.Player.Computer);
        }
        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("G");
          c.setToken(true);
          c.addKeyword("Token");

          c.addType("Creature");
          c.addType("Elf");
          c.addType("Druid");
          c.setAttack(1);
          c.setDefense(1);


          //custom settings
          c.setName("Llanowar Elves");
          c.addKeyword("tap: add G");


          play.add(c);
        }//makeToken()

        public boolean canPlay()
        {
          Card c[] = AllZone.getZone(Constant.Zone.Hand, card.getController()).getCards();

          return super.canPlay() && (0 < c.length);
        }

        public boolean canPlayAI()
        {
          boolean canDiscard =  0 < AllZone.Computer_Hand.getCards().length;
          return canPlay() && canDiscard && AllZone.Phase.getPhase().equals(Constant.Phase.Main2);
        }
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("G, tap, Discard a card: Put a 1/1 green Elf Druid creature token named Llanowar Elves into play with \"tap: add G\" ");
      ability.setStackDescription("Llanowar Mentor - Put a 1/1 token into play");
      ability.setBeforePayMana(new Input_PayManaCost_Ability("G", new Command()
      {
        public void execute()
        {
          card.tap();
          AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          AllZone.Stack.add(ability);
        }
      }));
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Sparkspitter"))
    {
      final Ability_Tap ability = new Ability_Tap(card, "R")
      {
        public void resolve()
        {
          makeToken();

          //computer discards here, todo: should discard when ability put on stack
          if(card.getController().equals(Constant.Player.Computer))
            AllZone.GameAction.discardRandom(Constant.Player.Computer);
        }
        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = CardFactory.this.getCard("Spark Elemental", card.getController());
          c.setToken(true);
          play.add(c);
        }//makeToken()

        public boolean canPlay()
        {
          Card c[] = AllZone.getZone(Constant.Zone.Hand, card.getController()).getCards();

          return super.canPlay() && (0 < c.length);
        }

        public boolean canPlayAI()
        {
          boolean canDiscard =  0 < AllZone.Computer_Hand.getCards().length;
          return canPlay() && canDiscard;
        }
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("R, tap, Discard a card: Put a 3/1 red Elemental creature token named Spark Elemental into play with trample, haste, and \"At end of turn, sacrifice Spark Elemental.\" ");
      ability.setStackDescription("Sparkspitter - Put a 3/1 token into play");
      ability.setBeforePayMana(new Input_PayManaCost_Ability("R", new Command()
      {
        public void execute()
        {
          card.tap();
          AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          AllZone.Stack.add(ability);
        }
      }));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Sliversmith"))
    {
      final Ability_Tap ability = new Ability_Tap(card, "1")
      {
        public void resolve()
        {
          makeToken();

          //computer discards here, todo: should discard when ability put on stack
          if(card.getController().equals(Constant.Player.Computer))
            AllZone.GameAction.discardRandom(Constant.Player.Computer);
        }
        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("1");
          c.setToken(true);
          c.addKeyword("Token");

          c.addType("Artifact");
          c.addType("Creature");
          c.addType("Sliver");
          c.setAttack(1);
          c.setDefense(1);


          //custom settings
          c.setName("Metallic Sliver");

          play.add(c);
        }//makeToken()

        public boolean canPlay()
        {
          Card c[] = AllZone.getZone(Constant.Zone.Hand, card.getController()).getCards();

          return super.canPlay() && (0 < c.length);
        }

        public boolean canPlayAI()
        {
          boolean canDiscard =  0 < AllZone.Computer_Hand.getCards().length;
          return canPlay() && canDiscard && AllZone.Phase.getPhase().equals(Constant.Phase.Main2);
        }
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("1, tap, Discard a card: Put a 1/1 Sliver artifact creature token named Metallic Sliver into play.");
      ability.setStackDescription(card +" - Put a 1/1 token into play");
      ability.setBeforePayMana(new Input_PayManaCost_Ability("1", new Command()
      {
        public void execute()
        {
          card.tap();
          AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          AllZone.Stack.add(ability);
        }
      }));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Cloudseeker"))
    {
      final Ability_Tap ability = new Ability_Tap(card, "U")
      {
        public void resolve()
        {
          makeToken();

          //computer discards here, todo: should discard when ability put on stack
          if(card.getController().equals(Constant.Player.Computer))
            AllZone.GameAction.discardRandom(Constant.Player.Computer);
        }
        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("U");
          c.setToken(true);
          c.addKeyword("Token");

          c.addType("Creature");
          c.addType("Sprite");
          c.setAttack(1);
          c.setDefense(1);


          //custom settings
          c.setName("Cloud Sprite");
          c.addKeyword("Flying");
          c.addKeyword("This creature can block only creatures with flying");

          play.add(c);
        }//makeToken()

        public boolean canPlay()
        {
          Card c[] = AllZone.getZone(Constant.Zone.Hand, card.getController()).getCards();

          return super.canPlay() && (0 < c.length);
        }

        public boolean canPlayAI()
        {
          boolean canDiscard =  0 < AllZone.Computer_Hand.getCards().length;
          return canPlay() && canDiscard && AllZone.Phase.getPhase().equals(Constant.Phase.Main2);
        }
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("U, tap, Discard a card: Put a 1/1 blue Sprite creature token named Cloud Sprite into play with flying and \"this creature can block only creatures with flying\".");
      ability.setStackDescription("Cloudseeker - Put a 1/1 token into play");
      ability.setBeforePayMana(new Input_PayManaCost_Ability("U", new Command()
      {
        public void execute()
        {
          card.tap();
          AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          AllZone.Stack.add(ability);
        }
      }));
    }//*************** END ************ END **************************







    //*************** START *********** START **************************
    if(cardName.equals("Goldmeadow Lookout"))
    {
      final Ability_Tap ability = new Ability_Tap(card, "G")
      {
        public void resolve()
        {
          makeToken();

          //computer discards here, todo: should discard when ability put on stack
          if(card.getController().equals(Constant.Player.Computer))
            AllZone.GameAction.discardRandom(Constant.Player.Computer);
        }
        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("W");
          c.setToken(true);
          c.addKeyword("Token");

          c.addType("Creature");
          c.addType("Kithkin");
          c.addType("Soldier");
          c.setAttack(1);
          c.setDefense(1);


          //custom settings
          c.setName("Goldmeadow Harrier");
          final SpellAbility ability = new Ability_Tap(c, "W")
          {
            public void resolve()
            {
              Card c = getTargetCard();
              c.tap();
            }
            public boolean canPlayAI() {return false;}
          };//SpellAbility
          c.addSpellAbility(new Spell_Permanent(c));
          c.addSpellAbility(ability);
          ability.setDescription("W, tap: Tap target creature.");
          ability.setBeforePayMana(CardFactoryUtil.input_targetCreature(ability));


          play.add(c);
        }//makeToken()

        public boolean canPlay()
        {
          Card c[] = AllZone.getZone(Constant.Zone.Hand, card.getController()).getCards();

          return super.canPlay() && (0 < c.length);
        }

        public boolean canPlayAI()
        {
          boolean canDiscard =  0 < AllZone.Computer_Hand.getCards().length;
          return canPlay() && canDiscard && AllZone.Phase.getPhase().equals(Constant.Phase.Main2);
        }
      };//SpellAbility

      card.addSpellAbility(ability);
      ability.setDescription("W, tap, Discard a card: Put a 1/1 white Kithkin Soldier creature token named Goldmeadow Harrier into play with \"W, tap target creature.\"");
      ability.setStackDescription("Goldmeadow Lookout - Put a 1/1 token into play");
      ability.setBeforePayMana(new Input_PayManaCost_Ability("W", new Command()
      {
        public void execute()
        {
          card.tap();
          AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
          AllZone.Stack.add(ability);
        }
      }));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Ghost Warden"))
    {
      final SpellAbility[] ability = new SpellAbility[1];

      final Command eot = new Command()
      {
        public void execute()
        {
          Card c = ability[0].getTargetCard();
          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(c.getAttack() - 1);
            c.setDefense(c.getDefense() - 1);
          }
        }
      };
      ability[0] = new Ability_Tap(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public void chooseTargetAI()
        {
          setTargetCard(getAttacker());
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          CardList att = new CardList(c.getAttackers());
          att.remove(card);
          att.shuffle();

          if(att.size() != 0)
            return att.get(0);
          else
            return null;
        }//getAttacker()

        public void resolve()
        {
          Card c = ability[0].getTargetCard();
          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(c.getAttack() + 1);
            c.setDefense(c.getDefense() + 1);

            AllZone.EndOfTurn.addUntil(eot);
          }
        }//resolve()
      };//SpellAbility
      card.addSpellAbility(ability[0]);
      ability[0].setDescription("tap: Target creature gets +1/+1 until end of turn.");

      final Input target = new Input()
      {
        public void showMessage()
        {
          ButtonUtil.enableOnlyCancel();
          AllZone.Display.showMessage("Select creature to get +1/+1");
        }
        public void selectCard(Card c, PlayerZone zone)
        {
          if(c.isCreature())
          {
            card.tap();
            AllZone.Human_Play.updateObservers();

            ability[0].setTargetCard(c);//since setTargetCard() changes stack description
            ability[0].setStackDescription(c +" gets +1/+1 until EOT");

            AllZone.InputControl.resetInput();
            AllZone.Stack.add(ability[0]);
          }
        }//selectCard()
        public void selectButtonCancel()
        {
          card.untap();
          stop();
        }
      };//Input target
      ability[0].setBeforePayMana(target);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Angelfire Crusader"))
    {
      final Command eot = new Command()
      {
        public void execute()
        {
          Card c = card;
          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(c.getAttack() - 1);
          }
        }//execute()
      };//Command

      final SpellAbility ability = new Ability(card, "R")
      {
        public void resolve()
        {
          Card c = card;
          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(c.getAttack() + 1);
            AllZone.EndOfTurn.addUntil(eot);
          }
        }
        public boolean canPlayAI()
        {
//          Combat combat = ComputerUtil.getAttackers();
//          Card[] att = combat.getAttackers();
//          for(int i = 0; i < att.length; i++)
//            if(att[i].equals(card))
//              return true;

          return false;
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setStackDescription("Angelfire Crusader gets +1/+0 until end of turn.");
      ability.setDescription("R: Angelfire Crusader gets +1/+0 until end of turn.");

      final Command paid  = new Command()
      {
        public void execute()
        {
          ability.setStackDescription(card +" gets +1/+0 until EOT");
          AllZone.Stack.add(ability);
          AllZone.InputControl.resetInput();
        }
      };
      ability.setBeforePayMana(new Input_PayManaCost_Ability(ability.getManaCost(), paid, Command.Blank));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Ghost-Lit Redeemer"))
    {
      final SpellAbility ability = new Ability_Tap(card, "W")
      {
        public boolean canPlayAI() {return AllZone.Phase.getPhase().equals(Constant.Phase.Main2);}

        public void resolve()
        {
          Card c = card;
          PlayerLife life = AllZone.GameAction.getPlayerLife(c.getController());
          life.addLife(2);
        }
      };//SpellAbility
      card.addSpellAbility(ability);
      ability.setDescription("W, tap: You gain 2 life");
      ability.setStackDescription("Computer gains 2 life");

      final Command paid  = new Command()
      {
        public void execute()
        {
          Card c = card;
          c.tap();
          AllZone.Human_Play.updateObservers();//so the card will tap at the correct time

          ability.setStackDescription(c.getController()+" gains 2 life");
          AllZone.Stack.add(ability);
          AllZone.InputControl.resetInput();
        }
      };
      ability.setBeforePayMana(new Input_PayManaCost_Ability(ability.getManaCost(), paid, Command.Blank));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Tremor"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());
          all = all.getType("Creature");

          for(int i = 0; i < all.size(); i++)
            if(! all.get(i).getKeyword().contains("Flying"))
              all.get(i).addDamage(1);
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Reviving Dose"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.addLife(3);

          AllZone.GameAction.drawCard(card.getController());
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Swords to Plowshares"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            //add life
            String player = getTargetCard().getController();
            PlayerLife life = AllZone.GameAction.getPlayerLife(player);
            life.addLife(getTargetCard().getAttack());

            //remove card from play
            PlayerZone zone = AllZone.getZone(getTargetCard());
            zone.remove(getTargetCard());
          }
        }//resolve()
        public boolean canPlayAI()
        {
          CardList creature = new CardList(AllZone.Human_Play.getCards());
          creature = creature.getType("Creature");
          return creature.size() != 0 && (AllZone.Phase.getTurn() > 4);
        }
        public void chooseTargetAI()
        {
          CardList play = new CardList(AllZone.Human_Play.getCards());
          Card target = CardFactoryUtil.AI_getBestCreature(play);
          setTargetCard(target);
        }
      };
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));

      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Crib Swap"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            String player = getTargetCard().getController();
            makeToken(player);

            //remove card from play
            PlayerZone zone = AllZone.getZone(getTargetCard());
            zone.remove(getTargetCard());
          }
        }//resolve()
        public boolean canPlayAI()
        {
          CardList creature = new CardList(AllZone.Human_Play.getCards());
          creature = creature.getType("Creature");
          return creature.size() != 0 && (AllZone.Phase.getTurn() > 4);
        }
        public void chooseTargetAI()
        {
          CardList play = new CardList(AllZone.Human_Play.getCards());
          Card target = CardFactoryUtil.AI_getBestCreature(play);
          setTargetCard(target);
        }
        void makeToken(String player)
        {
          Card c = new Card();

          c.setName("Token");

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Shapeshifter");
          c.setAttack(1);
          c.setDefense(1);

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, player);
          play.add(c);
        }//makeToken()

      };
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));

      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




//*************** START *********** START **************************
    if(cardName.equals("Demonic Tutor"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          String player = card.getController();
          if(player.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void humanResolve()
        {
          Object check = AllZone.Display.getChoiceOptional("Select card", AllZone.Human_Library.getCards());
          if(check != null)
          {
            PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
            AllZone.GameAction.moveTo(hand, (Card)check);
          }
          AllZone.GameAction.shuffle(Constant.Player.Human);
        }
        public void computerResolve()
        {
          Card[] library = AllZone.Computer_Library.getCards();
          CardList list = new CardList(library);


          //pick best creature
          Card c = CardFactoryUtil.AI_getBestCreature(list);
          if(c == null)
            c = library[0];
          //System.out.println("comptuer picked - " +c);
          AllZone.Computer_Library.remove(c);
          AllZone.Computer_Hand.add(c);
        }
        public boolean canPlay()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          return library.getCards().length != 0;
        }
        public boolean canPlayAI()
        {
          CardList creature = new CardList();
          creature.addAll(AllZone.Computer_Library.getCards());
          creature = creature.getType("Creature");
          return creature.size() != 0;
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Do or Die"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return 4 <= CardFactoryUtil.AI_getHumanCreature().size() && 4 < AllZone.Phase.getTurn();
        }

        public void resolve()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, getTargetPlayer());
          CardList list = new CardList(play.getCards());
          list = list.getType("Creature");

          list.shuffle();

          for(int i = 0; i < list.size() / 2; i++)
            AllZone.GameAction.destroyNoRegeneration(list.get(i));
        }
      };//SpellAbility
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetHuman());
      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));

      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Hymn to Tourach"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          AllZone.GameAction.discardRandom(opponent);
          AllZone.GameAction.discardRandom(opponent);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Beacon of Destruction"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              getTargetCard().addDamage(5);
              done();
            }
            else
              AllZone.GameAction.moveToGraveyard(card);
          }
          else
          {
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(5);
            done();
          }
        }//resolve()
        void done()
        {
          //shuffle card back into the library
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          library.add(card);
          AllZone.GameAction.shuffle(card.getController());
        }
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetHumanCreatureOrPlayer());

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Elvish Fury"))
    {
      final SpellAbility spell_one = new Spell(card)
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          final Card[] target = new Card[1];
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(target[0]))
              {
                target[0].setAttack (target[0].getAttack() - 2);
                target[0].setDefense(target[0].getDefense()- 2);
              }
            }
          };

          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 2);
            target[0].setDefense(target[0].getDefense()+ 2);

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };//SpellAbility

      final SpellAbility spell_two = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public void chooseTargetAI()
        {
          setTargetCard(getAttacker());
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();

          CardList list = new CardList(c.getAttackers());
          CardListUtil.sortFlying(list);

          Card[] att = list.toArray();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()


        public void resolve()
        {
          final Card[] target = new Card[1];
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(target[0]))
              {
                target[0].setAttack (target[0].getAttack() - 2);
                target[0].setDefense(target[0].getDefense()- 2);
              }
            }
          };

          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 2);
            target[0].setDefense(target[0].getDefense()+ 2);

            AllZone.EndOfTurn.addUntil(untilEOT);
            done();
          }
        }//resolve()
        void done()
        {
          //return card to the hand
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          AllZone.GameAction.moveTo(hand, card);
        }
      };//SpellAbility
      spell_two.setManaCost("4 G");

      spell_one.setDescription("Target creature gets +2/+2 until end of turn.");
      spell_two.setDescription("Buyback 4 - Pay 4G, put this card into your hand as it resolves.");

      spell_one.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell_one));
      spell_two.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell_two));

      card.clearSpellAbility();
      card.addSpellAbility(spell_one);
      card.addSpellAbility(spell_two);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Sprout Swarm"))
    {
      final Command makeToken = new Command()
      {
        public void execute()
        {
            Card c = new Card();

            c.setOwner(card.getController());
            c.setController(card.getController());

            c.setName("Token");
            c.setManaCost("G");
            c.setToken(true);

            c.addType("Creature");
            c.addType("Insect");
            c.setAttack(1);
            c.setDefense(1);

            PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
            play.add(c);
        }//execute()
      };//Command

      final SpellAbility spell_one = new Spell(card)
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          makeToken.execute();
        }//resolve()
      };//SpellAbility

      final SpellAbility spell_two = new Spell(card)
      {
        public void resolve()
        {
          makeToken.execute();

          //return card to the hand
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          AllZone.GameAction.moveTo(hand, card);
        }
      };//SpellAbility

      spell_one.setManaCost("1 G");
      spell_two.setManaCost("4 G");

      spell_one.setDescription("Put a 1/1 grean token into play. -- Sorry no convoke yet.");
      spell_two.setDescription("Buyback 3 - Pay 4G, put this card into your hand as it resolves.");

      spell_one.setStackDescription("Sprout Swarm - Put a 1/1 grean token into play");
      spell_two.setStackDescription("Sprout Swarm - Buyback, Put a 1/1 grean token into play");

      card.clearSpellAbility();
      card.addSpellAbility(spell_one);
      card.addSpellAbility(spell_two);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Threaten"))
    {
      final PlayerZone[] playEOT   = new PlayerZone[1];
      final String[] controllerEOT = new String[1];
      final Card[] target          = new Card[1];

      final Command untilEOT = new Command()
      {
        public void execute()
        {
          //if card isn't in play, do nothing
          if(! AllZone.GameAction.isCardInPlay(target[0]))
            return;

          target[0].setController(controllerEOT[0]);

          ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(false);
          ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(false);

          //moveTo() makes a new card, so you don't have to remove "Haste"
          AllZone.GameAction.moveTo(playEOT[0], target[0]);

          ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(true);
          ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(true);
        }//execute()
      };//Command

      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            playEOT[0]       = AllZone.getZone(getTargetCard());
            controllerEOT[0] = getTargetCard().getController();
            target[0]        = getTargetCard();

            //set the controller
            getTargetCard().setController(card.getController());

            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(false);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(false);

            PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
            play.add(getTargetCard());
            playEOT[0].remove(getTargetCard());

            ((PlayerZone_ComesIntoPlay)AllZone.Human_Play).setTrigger(true);
            ((PlayerZone_ComesIntoPlay)AllZone.Computer_Play).setTrigger(true);


            getTargetCard().untap();
            getTargetCard().addKeyword("Haste");

            AllZone.EndOfTurn.addUntil(untilEOT);
          }//is card in play?
        }//resolve()
        public boolean canPlayAI()
        {
          //only use this card if creatures power is greater than 2
          CardList list = new CardList(AllZone.Human_Play.getCards());
          for(int i = 0; i < list.size(); i++)
            if(2 < list.get(i).getAttack())
              return true;

          return false;
        }//canPlayAI()
        public void chooseTargetAI()
        {
          CardList list = new CardList(AllZone.Human_Play.getCards());
          setTargetCard(CardFactoryUtil.AI_getBestCreature(list));
        }
      };//SpellAbility
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Beacon of Unrest"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          Card c = getTargetCard();
          PlayerZone grave = AllZone.getZone(c);

          if(AllZone.GameAction.isCardInZone(c, grave))
          {
            //set the correct controller if needed
            c.setController(card.getController());

            //card changes zones
            AllZone.getZone(c).remove(c);
            PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
            play.add(c);

            //shuffle card back into the library
            PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
            library.add(card);
            AllZone.GameAction.shuffle(card.getController());
          }
        }//resolve()
        public boolean canPlay()
        {
          return getCreatures().length != 0;
        }
        //duplicated from Input below
        public Card[] getCreatures()
        {
          CardList creature = new CardList();
          creature.addAll(AllZone.Human_Graveyard.getCards());
          creature.addAll(AllZone.Computer_Graveyard.getCards());
          creature = creature.getType("Creature");
          return creature.toArray();
        }
        public void chooseTargetAI()
        {
          Card c[] = getCreatures();
          Card biggest = c[0];
          for(int i = 0; i < c.length; i++)
            if(biggest.getAttack() < c[i].getAttack())
              biggest = c[i];

          setTargetCard(biggest);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      Input target = new Input()
      {
        public void showMessage()
        {
          Object check = AllZone.Display.getChoiceOptional("Select creature", getCreatures());
          if(check != null)
          {
            spell.setTargetCard((Card)check);
            stopSetNext(new Input_PayManaCost(spell));
          }
          else
            stop();
        }//showMessage()

        //duplicated from SpellAbility above ^^^^^^^^
        public Card[] getCreatures()
        {
          CardList creature = new CardList();
          creature.addAll(AllZone.Human_Graveyard.getCards());
          creature.addAll(AllZone.Computer_Graveyard.getCards());
          creature = creature.getType("Creature");
          return creature.toArray();
        }
      };//Input
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Breath of Life") || cardName.equals("Resurrection"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          Card c = getTargetCard();
          PlayerZone grave = AllZone.getZone(c);

          if(AllZone.GameAction.isCardInZone(c, grave))
          {
            PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getController());
            AllZone.GameAction.moveTo(play, c);
          }
        }//resolve()
        public boolean canPlay()
        {
          return getCreatures().length != 0;
        }
        public Card[] getCreatures()
        {
          CardList creature = new CardList();
          PlayerZone zone = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          creature.addAll(zone.getCards());
          creature = creature.getType("Creature");
          return creature.toArray();
        }
        public void chooseTargetAI()
        {
          Card c[] = getCreatures();
          Card biggest = c[0];
          for(int i = 0; i < c.length; i++)
            if(biggest.getAttack() < c[i].getAttack())
              biggest = c[i];

          setTargetCard(biggest);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      Input target = new Input()
      {
        public void showMessage()
        {
          Object check = AllZone.Display.getChoiceOptional("Select creature", getCreatures());
          if(check != null)
          {
            spell.setTargetCard((Card)check);
            stopSetNext(new Input_PayManaCost(spell));
          }
          else
            stop();
        }//showMessage()

        public Card[] getCreatures()
        {
          CardList creature = new CardList();
          PlayerZone zone = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          creature.addAll(zone.getCards());
          creature = creature.getType("Creature");
          return creature.toArray();
        }
      };//Input
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Zombify"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          Card c = getTargetCard();
          PlayerZone grave = AllZone.getZone(c);

          if(AllZone.GameAction.isCardInZone(c, grave))
          {
            PlayerZone play = AllZone.getZone(Constant.Zone.Play, c.getController());
            AllZone.GameAction.moveTo(play, c);
          }
        }//resolve()
        public boolean canPlay()
        {
          return getCreatures().length != 0;
        }
        public Card[] getCreatures()
        {
          CardList creature = new CardList();
          PlayerZone zone = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          creature.addAll(zone.getCards());
          creature = creature.getType("Creature");
          return creature.toArray();
        }
        public void chooseTargetAI()
        {
          Card c[] = getCreatures();
          Card biggest = c[0];
          for(int i = 0; i < c.length; i++)
            if(biggest.getAttack() < c[i].getAttack())
              biggest = c[i];

          setTargetCard(biggest);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      Input target = new Input()
      {
        public void showMessage()
        {
          Object check = AllZone.Display.getChoiceOptional("Select creature", getCreatures());
          if(check != null)
          {
            spell.setTargetCard((Card)check);
            stopSetNext(new Input_PayManaCost(spell));
          }
          else
            stop();
        }//showMessage()

        public Card[] getCreatures()
        {
          CardList creature = new CardList();
          PlayerZone zone = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          creature.addAll(zone.getCards());
          creature = creature.getType("Creature");
          return creature.toArray();
        }
      };//Input
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Animate Dead"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          Card c = getTargetCard();

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          PlayerZone grave = AllZone.getZone(c);

          if(AllZone.GameAction.isCardInZone(c, grave))
          {
            c.setAttack(c.getAttack() - 1);
            c.setController(card.getController());

            play.add(c);
            grave.remove(c);
          }
        }//resolve()
        public boolean canPlay()
        {
          return getCreatures().length != 0;
        }
        public Card[] getCreatures()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());
          list = list.getType("Creature");

          return list.toArray();
        }
        public boolean canPlayAI()
        {
          CardList all = new CardList(getCreatures());
          if(all.isEmpty())
            return false;

          Card c = CardFactoryUtil.AI_getBestCreature(all);

          if(2 < c.getAttack() && 2 < c.getDefense())
            return true;

          return false;
        }

        public void chooseTargetAI()
        {
          Card c[] = getCreatures();
          Card biggest = c[0];
          for(int i = 0; i < c.length; i++)
            if(biggest.getAttack() < c[i].getAttack())
              biggest = c[i];

          setTargetCard(biggest);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      Input target = new Input()
      {
        public void showMessage()
        {
          Object check = AllZone.Display.getChoiceOptional("Select creature", getCreatures());
          if(check != null)
          {
            spell.setTargetCard((Card)check);
            stopSetNext(new Input_PayManaCost(spell));
          }
          else
            stop();
        }//showMessage()

        public Card[] getCreatures()
        {
          //get all creatures
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());
          list = list.getType("Creature");

          return list.toArray();
        }
      };//Input
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Blinding Light"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList nonwhite = new CardList();
          nonwhite.addAll(AllZone.Human_Play.getCards());
          nonwhite.addAll(AllZone.Computer_Play.getCards());
          nonwhite = nonwhite.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && (!CardUtil.getColors(c).contains(Constant.Color.White));
            }
          });
          for(int i = 0; i < nonwhite.size(); i++)
            nonwhite.get(i).tap();
        }//resolve()
        public boolean canPlayAI()
        {
          //the computer seems to play this card at stupid times
          return false;
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Raise the Alarm"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          for (int i = 0; i < 2; i++)
          {
            Card c = new Card();

            c.setOwner(card.getController());
            c.setController(card.getController());

            c.setName("Token");
            c.setManaCost("W");
            c.setToken(true);

            c.addType("Creature");
            c.addType("Soldier");
            c.setAttack(1);
            c.setDefense(1);

            play.add(c);
          }//for
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Empty the Warrens"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          int stormCount = 0;

          //get storm count
          CardList list = new CardList();
          list.addAll(AllZone.Human_Graveyard.getCards());
          list.addAll(AllZone.Computer_Graveyard.getCards());

          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());

          for(int i = 0; i < list.size(); i++)
            if(list.get(i).getTurnInZone() == AllZone.Phase.getTurn())
              stormCount++;

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          for (int i = 0; i < 2 * stormCount; i++)
          {
            Card c = new Card();

            c.setOwner(card.getController());
            c.setController(card.getController());

            c.setName("Token");
            c.setManaCost("R");
            c.setToken(true);

            c.addType("Creature");
            c.addType("Goblin");
            c.setAttack(1);
            c.setDefense(1);

            play.add(c);
          }//for
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************







    //*************** START *********** START **************************
    if(cardName.equals("Feudkiller's Verdict"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.addLife(10);

          String opponent = AllZone.GameAction.getOpponent(card.getController());
          PlayerLife oppLife = AllZone.GameAction.getPlayerLife(opponent);

          if(oppLife.getLife() < life.getLife())
            makeToken();
        }//resolve()

        void makeToken()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("W");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Giant");
          c.addType("Warrior");
          c.setAttack(5);
          c.setDefense(5);

          play.add(c);
        }//makeToken()

      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Reach of Branches"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());

          //make token
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setName("Token");
          c.setManaCost("G");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Treefolk");
          c.addType("Shaman");
          c.setAttack(2);
          c.setDefense(5);

          play.add(c);
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Pyroclasm"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());
          all = all.getType("Creature");

          for(int i = 0; i < all.size(); i++)
            all.get(i).addDamage(2);
        }
        public boolean canPlayAI()
        {
          CardList human    = new CardList(AllZone.Human_Play.getCards());
          CardList computer = new CardList(AllZone.Computer_Play.getCards());

          human    = human.getType("Creature");
          computer = computer.getType("Creature");

          human = CardListUtil.filterToughness(human, 2);
          computer = CardListUtil.filterToughness(computer, 2);

          //the computer will at least destroy 2 more human creatures
          return computer.size() < human.size()-1;
        }//canPlayAI()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Flamebreak"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return AllZone.Computer_Life.getLife()>3;
        }
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());
          all = all.getType("Creature");

          for(int i = 0; i < all.size(); i++)
            if(! all.get(i).getKeyword().contains("Flying"))
            {
              all.get(i).setShield(0);
              all.get(i).addDamage(3);
            }

          AllZone.Human_Life.subtractLife(3);
          AllZone.Computer_Life.subtractLife(3);
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Renewed Faith"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlay()
        {
          setStackDescription(card.getName() +" - " +card.getController() +" gains 6 life.");
          return super.canPlay();
        }

        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.addLife(6);
        }
      };
      spell.setDescription("You gain 6 life.");

      card.clearSpellAbility();
      card.addSpellAbility(spell);
      card.addSpellAbility(CardFactoryUtil.ability_cycle(card, "1 W"));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("TestLife"))
    {
      SpellAbility ability1 = new Ability_Activated(card, "1")
      {
        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.addLife(1);
        }
      };
      ability1.setStackDescription(card.getController() +" gains 1 life");
      ability1.setDescription("1: gain 1 life");
      card.addSpellAbility(ability1);

      SpellAbility ability2 = new Ability_Activated(card, "1")
      {
        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.subtractLife(1);
        }
      };
      ability2.setStackDescription(card.getController() +" looses 1 life");
      ability2.setDescription("1: loose 1 life");
      card.addSpellAbility(ability2);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Life Burst"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList count = new CardList();
          count.addAll(AllZone.Human_Graveyard.getCards());
          count.addAll(AllZone.Computer_Graveyard.getCards());
          count = count.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.getName().equals("Life Burst");
            }
          });

          PlayerLife life = AllZone.GameAction.getPlayerLife(getTargetPlayer());
          life.addLife(4 * count.size());
        }
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetComputer());
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Wit's End"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, getTargetPlayer());
          Card c[] = hand.getCards();
          for(int i = 0; i < c.length; i++)
            AllZone.GameAction.discard(c[i]);
        }
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetHuman());
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Lava Spike"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(3);
        }
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetHuman());
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Cranial Extraction"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          Card choice = null;

          //check for no cards in library
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, opponent);

          if(library.size() == 0)
            return;

          //human chooses
          if(opponent.equals(Constant.Player.Computer))
          {
            CardList all = AllZone.CardFactory.getAllCards();
            all.sort(new Comparator()
            {
              public int compare(Object a1, Object b1)
              {
                Card a = (Card)a1;
                Card b = (Card)b1;

                return a.getName().compareTo(b.getName());
              }
            });
            choice = (Card) AllZone.Display.getChoice("Choose", removeLand(all.toArray()));

            Card[] showLibrary = library.getCards();
            Comparator com = new TableSorter(new CardList(showLibrary), 2, true);
            Arrays.sort(showLibrary, com);

            AllZone.Display.getChoiceOptional("Opponent's Library", showLibrary);
            AllZone.GameAction.shuffle(opponent);
          }//if
          else//computer chooses
          {
            //the computer cheats by choosing a creature in the human players library or hand
            CardList all = new CardList();
            all.addAll(AllZone.Human_Hand.getCards());
            all.addAll(AllZone.Human_Library.getCards());

            CardList four = all.filter(new CardListFilter()
            {
              public boolean addCard(Card c)
              {
                if(c.isLand())
                  return false;

                return 3 < CardUtil.getConvertedManaCost(c.getManaCost());
              }
            });
            if(! four.isEmpty())
              choice = CardUtil.getRandom(four.toArray());
            else
              choice = CardUtil.getRandom(all.toArray());

          }//else
          remove(choice, opponent);
        }//resolve()

        void remove(Card c, String player)
        {
          PlayerZone hand    = AllZone.getZone(Constant.Zone.Hand, player);
          PlayerZone grave   = AllZone.getZone(Constant.Zone.Graveyard, player);
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);

          CardList all = new CardList();
          all.addAll(hand.getCards());
          all.addAll(grave.getCards());
          all.addAll(library.getCards());

          for(int i = 0; i < all.size(); i++)
            if(all.get(i).getName().equals(c.getName()))//same name?
              AllZone.GameAction.moveToGraveyard(all.get(i));
        }//remove()

        public boolean canPlayAI()
        {
          Card[] c = removeLand(AllZone.Human_Library.getCards());
          return 0 < c.length;
        }
        Card[] removeLand(Card[] in)
        {
          CardList c = new CardList(in);
          c = c.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return !c.isLand();
            }
          });
          return c.toArray();
        }//removeLand()
      };//SpellAbility spell
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(new Input_PayManaCost(spell));
      spell.setStackDescription(card.getName() +" - targeting opponent");
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Coercion"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          Card choice = null;

          //check for no cards in hand on resolve
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, opponent);
          Card[] handChoices = removeLand(hand.getCards());

          if(handChoices.length == 0)
            return;

          //human chooses
          if(opponent.equals(Constant.Player.Computer))
          {
            choice = (Card) AllZone.Display.getChoice("Choose", handChoices);
          }
          else//computer chooses
          {
            choice = CardUtil.getRandom(handChoices);
          }

          AllZone.GameAction.discard(choice);
        }//resolve()

        public boolean canPlayAI()
        {
          Card[] c = removeLand(AllZone.Human_Hand.getCards());
          return 0 < c.length;
        }

        Card[] removeLand(Card[] in)
        {
          return in;
        }//removeLand()
      };//SpellAbility spell
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(new Input_PayManaCost(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Thoughtseize") || cardName.equals("Distress"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          if(cardName.equals("Thoughtseize"))
            AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(2);

          Card choice = null;

          //check for no cards in hand on resolve
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, getTargetPlayer());
          Card[] handChoices = removeLand(hand.getCards());

          if(handChoices.length == 0)
            return;

          //human chooses
          if(card.getController().equals(Constant.Player.Human))
          {
            choice = (Card) AllZone.Display.getChoice("Choose", handChoices);
          }
          else//computer chooses
          {
            choice = CardUtil.getRandom(handChoices);
          }

          AllZone.GameAction.discard(choice);
        }//resolve()

        public boolean canPlayAI()
        {
          Card[] c = removeLand(AllZone.Human_Hand.getCards());
          return 0 < c.length;
        }

        Card[] removeLand(Card[] in)
        {
          CardList c = new CardList(in);
          c = c.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return !c.isLand();
            }
          });
          return c.toArray();
        }//removeLand()
      };//SpellAbility spell
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetHuman());
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Douse in Gloom"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList c = getCreature();
          if(c.isEmpty())
            return false;
          else
          {
            setTargetCard(c.get(0));
            return true;
          }
        }//canPlayAI()
        CardList getCreature()
        {
          CardList out = new CardList();
          CardList list = CardFactoryUtil.AI_getHumanCreature("Flying");
          list.shuffle();

          for(int i = 0; i < list.size(); i++)
            if((list.get(i).getAttack() >= 2) && (list.get(i).getDefense() <= 2))
              out.add(list.get(i));

          //in case human player only has a few creatures in play, target anything
          if(out.isEmpty() &&
              0 < CardFactoryUtil.AI_getHumanCreature(2).size() &&
             3 > CardFactoryUtil.AI_getHumanCreature().size())
          {
            out.addAll(CardFactoryUtil.AI_getHumanCreature(2).toArray());
            CardListUtil.sortFlying(out);
          }
          return out;
        }//getCreature()


        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            getTargetCard().addDamage(2);
            AllZone.GameAction.getPlayerLife(card.getController()).addLife(2);
          }
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Echoing Decay"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList c = getCreature();
          if(c.isEmpty())
            return false;
          else
          {
            setTargetCard(c.get(0));
            return true;
          }
        }//canPlayAI()
        CardList getCreature()
        {
          CardList out = new CardList();
          CardList list = CardFactoryUtil.AI_getHumanCreature("Flying");
          list.shuffle();

          for(int i = 0; i < list.size(); i++)
            if((list.get(i).getAttack() >= 2) && (list.get(i).getDefense() <= 2))
              out.add(list.get(i));

          //in case human player only has a few creatures in play, target anything
          if(out.isEmpty() &&
              0 < CardFactoryUtil.AI_getHumanCreature(2).size() &&
             3 > CardFactoryUtil.AI_getHumanCreature().size())
          {
            out.addAll(CardFactoryUtil.AI_getHumanCreature(2).toArray());
            CardListUtil.sortFlying(out);
          }
          return out;
        }//getCreature()


        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            getTargetCard().addDamage(2);

            if(! getTargetCard().isToken())
            {
              //get all creatures
              CardList list = new CardList();
              list.addAll(AllZone.Human_Play.getCards());
              list.addAll(AllZone.Computer_Play.getCards());

              list = list.getName(getTargetCard().getName());
              list.remove(getTargetCard());

              for(int i = 0; i < list.size(); i++)
                list.get(i).addDamage(2);
            }//is token?
          }//in play?
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
    }//*************** END ************ END **************************





//*************** START *********** START **************************
if(cardName.equals("Shock"))
{
final SpellAbility spell = new Spell(card)
{
  int damage = 2;
  Card check;
  public boolean canPlayAI()
  {
    if(AllZone.Human_Life.getLife() <= damage)
      return true;

    check = getFlying();
    return check != null;
  }
  public void chooseTargetAI()
  {
    if(AllZone.Human_Life.getLife() <= damage)
    {
      setTargetPlayer(Constant.Player.Human);
      return;
    }

    Card c = getFlying();
    if((c == null) || (! check.equals(c)))
      throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

    setTargetCard(c);
  }//chooseTargetAI()

//uses "damage" variable
  Card getFlying()
  {
    CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
    for(int i = 0; i < flying.size(); i++)
      if(flying.get(i).getDefense() <= damage)
        return flying.get(i);

    return null;
  }

  public void resolve()
  {
    if(getTargetCard() != null)
    {
      if(AllZone.GameAction.isCardInPlay(getTargetCard()))
      {
        Card c = getTargetCard();
        c.addDamage(damage);
      }
    }
    else
      AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
  }//resolve()
};//SpellAbility
card.clearSpellAbility();
card.addSpellAbility(spell);

spell.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(spell));
}//*************** END ************ END **************************









    //*************** START *********** START **************************
    if(cardName.equals("Sunlance"))
    {
      final SpellAbility spell = new Spell(card)
      {
        int damage = 3;
        Card check;
        public boolean canPlayAI()
        {
          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage &&
               (!CardUtil.getColors(flying.get(i)).contains(Constant.Color.White)))
          {
              return flying.get(i);
          }
          return null;
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            Card c = getTargetCard();
            c.addDamage(damage);
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      //target
      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target non-white creature for " +spell.getSourceCard());
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if((!CardUtil.getColors(card).contains(Constant.Color.White)) &&
             card.isCreature()                                          &&
             zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }
      };//SpellAbility - target

      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Saltblast"))
    {
      final SpellAbility spell = new Spell(card)
      {
        Card check;
        public boolean canPlayAI()
        {
          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(!CardUtil.getColors(flying.get(i)).contains(Constant.Color.White))
          {
              return flying.get(i);
          }
          return null;
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            Card c = getTargetCard();

            if(AllZone.GameAction.isCardInPlay(c))
              AllZone.GameAction.destroy(c);
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      //target
      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target non-white permanent for " +spell.getSourceCard());
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if((!CardUtil.getColors(card).contains(Constant.Color.White)) &&
             zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }
      };//SpellAbility - target

      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Firebolt"))
    {
      final SpellAbility spell = new Spell(card)
      {
        int damage = 2;
        Card check;
        public boolean canPlayAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
            return true;

          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
          {
            setTargetPlayer(Constant.Player.Human);
            return;
          }

          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage)
              return flying.get(i);

          return null;
        }

        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              Card c = getTargetCard();
              c.addDamage(damage);
            }
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(spell));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Spark Spray"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void chooseTargetAI()
        {
          setStackDescription("Spark Spray cycling - Computer draws a card");
        }//chooseTargetAI()

        public void resolve()
        {
          if(card.getController().equals(Constant.Player.Computer))
          {
            AllZone.GameAction.drawCard(Constant.Player.Computer);
            return;
          }

          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              Card c = getTargetCard();
              c.addDamage(1);
            }
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(1);
        }
      };//SpellAbility
      spell.setDescription("Spark Spray deals 1 damage to target creature or player.");
      card.clearSpellAbility();

      card.addSpellAbility(spell);
      card.addSpellAbility(CardFactoryUtil.ability_cycle(card, "R"));

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(spell));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Cackling Flames"))
    {
      final SpellAbility spell = new Spell(card)
      {
        int damage = 3;
        Card check;
        public boolean canPlayAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
            return true;

          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
          {
            setTargetPlayer(Constant.Player.Human);
            return;
          }

          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage)
              return flying.get(i);

          return null;
        }



        public void resolve()
        {
          int damage = getDamage();

          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              Card c = getTargetCard();
              c.addDamage(damage);
            }
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
        }
        int getDamage()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          if(hand.size() == 0)
            return 5;

          return 3;
        }
      };//SpellAbility
      card.clearSpellAbility();

      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Tribal Flames"))
    {
      final SpellAbility spell = new Spell(card)
      {
        Card check;
        public boolean canPlayAI()
        {
          int damage = countLandTypes();

          if(AllZone.Human_Life.getLife() <= damage)
            return true;

          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          int damage = countLandTypes();

          if(AllZone.Human_Life.getLife() <= damage)
          {
            setTargetPlayer(Constant.Player.Human);
            return;
          }

          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          int damage = countLandTypes();

          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage)
              return flying.get(i);

          return null;
        }


        public void resolve()
        {
          int damage = countLandTypes();

          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              Card c = getTargetCard();
              c.addDamage(damage);
            }
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
        }
        //count basic lands you control
        int countLandTypes()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList land = new CardList(play.getCards());

          String basic[] = {"Forest", "Plains", "Mountain", "Island", "Swamp"};
          int count = 0;

          for(int i = 0; i < basic.length; i++)
          {
            CardList c = land.getType(basic[i]);
            if(! c.isEmpty())
              count++;
          }

          return count;
        }//countLandTypes()

      };//SpellAbility
      card.clearSpellAbility();

      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(spell));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Erratic Explosion"))
    {
      final SpellAbility spell = new Spell(card)
      {
        int damage = 3;
        Card check;
        public boolean canPlayAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
            return true;

          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
          {
            setTargetPlayer(Constant.Player.Human);
            return;
          }

          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage)
              return flying.get(i);

          return null;
        }

        public void resolve()
        {
          int damage = getDamage();

          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              javax.swing.JOptionPane.showMessageDialog(null, "Erratic Explosion causes " +damage +" to " +getTargetCard());

              Card c = getTargetCard();
              c.addDamage(damage);
            }
          }
          else
          {
            javax.swing.JOptionPane.showMessageDialog(null, "Erratic Explosion causes " +damage +" to " +getTargetPlayer());
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
          }
        }
        //randomly choose a nonland card
        int getDamage()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          CardList notLand = new CardList(library.getCards());
          notLand = notLand.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return ! c.isLand();
            }
          });
          notLand.shuffle();

          if(notLand.isEmpty())
            return 0;

          Card card = notLand.get(0);
          return CardUtil.getConvertedManaCost(card.getSpellAbility()[0]);
        }
      };//SpellAbility
      card.clearSpellAbility();

      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(spell));
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Volcanic Hammer"))
    {
      final SpellAbility spell = new Spell(card)
      {
        int damage = 3;
        Card check;
        public boolean canPlayAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
            return true;

          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
          {
            setTargetPlayer(Constant.Player.Human);
            return;
          }

          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage)
              return flying.get(i);

          return null;
        }

        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              Card c = getTargetCard();
              c.addDamage(damage);
            }
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetCreaturePlayer(spell));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Lightning Bolt"))
    {
      final SpellAbility spell = new Spell(card)
      {
        int damage = 3;
        Card check;
        public boolean canPlayAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
            return true;

          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
          {
            setTargetPlayer(Constant.Player.Human);
            return;
          }

          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage)
              return flying.get(i);

          return null;
        }

        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              Card c = getTargetCard();
              c.addDamage(damage);
            }
          }
          else
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target Creature or Player");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }//selectCard()
        public void selectPlayer(String player)
        {
          spell.setTargetPlayer(player);
          stopSetNext(new Input_PayManaCost(spell));
        }
      };
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************


    //*************** START *********** START **************************
    if(cardName.equals("Char"))
    {
      final SpellAbility spell = new Spell(card)
      {
        int damage = 4;
        Card check;
        public boolean canPlayAI()
        {
          if(AllZone.Computer_Life.getLife() < 2)
            return false;

          if(AllZone.Human_Life.getLife() <= damage)
            return true;

          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
          {
            setTargetPlayer(Constant.Player.Human);
            return;
          }

          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage)
              return flying.get(i);

          return null;
        }
        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              Card c = getTargetCard();
              c.addDamage(damage);
              AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(2);
            }
          }
          else
          {
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
            AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(2);
          }

        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target Creature or Player");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }//selectCard()
        public void selectPlayer(String player)
        {
          spell.setTargetPlayer(player);
          stopSetNext(new Input_PayManaCost(spell));
        }
      };
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Psionic Blast"))
    {
      final SpellAbility spell = new Spell(card)
      {
        int damage = 4;
        Card check;
        public boolean canPlayAI()
        {
          if(AllZone.Computer_Life.getLife() < 2)
            return false;

          if(AllZone.Human_Life.getLife() <= damage)
            return true;

          check = getFlying();
          return check != null;
        }
        public void chooseTargetAI()
        {
          if(AllZone.Human_Life.getLife() <= damage)
          {
            setTargetPlayer(Constant.Player.Human);
            return;
          }

          Card c = getFlying();
          if((c == null) || (! check.equals(c)))
            throw new RuntimeException(card +" error in chooseTargetAI() - Card c is " +c +",  Card check is " +check);

          setTargetCard(c);
        }//chooseTargetAI()

        //uses "damage" variable
        Card getFlying()
        {
          CardList flying = CardFactoryUtil.AI_getHumanCreature("Flying");
          for(int i = 0; i < flying.size(); i++)
            if(flying.get(i).getDefense() <= damage)
              return flying.get(i);

          return null;
        }
        public void resolve()
        {
          if(getTargetCard() != null)
          {
            if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            {
              Card c = getTargetCard();
              c.addDamage(damage);
              AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(2);
            }
          }
          else
          {
            AllZone.GameAction.getPlayerLife(getTargetPlayer()).subtractLife(damage);
            AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(2);
          }

        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target Creature or Player");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }//selectCard()
        public void selectPlayer(String player)
        {
          spell.setTargetPlayer(player);
          stopSetNext(new Input_PayManaCost(spell));
        }
      };
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Hidetsugu's Second Rite"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(getTargetPlayer());
          if(life.getLife() == 10)
            life.subtractLife(10);
        }
        public boolean canPlay()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          PlayerLife p = AllZone.GameAction.getPlayerLife(opponent);
          return p.getLife() == 10;
        }
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetHuman());
      card.clearSpellAbility();
      card.addSpellAbility(spell);

      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("March of Souls"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          change(AllZone.Human_Play, card.getController());
          change(AllZone.Computer_Play, card.getController());
        }
        public void change(PlayerZone play, String owner)
        {
          Card[] c = play.getCards();
          for (int i = 0; i < c.length; i++)
          {
            if(c[i].isCreature())
            {
              AllZone.GameAction.destroyNoRegeneration(c[i]);
              play.add(getCreature(c[i].getController()));
            }
          }
        }//change()
        public Card getCreature(String owner)
        {
          //TODO: owner and controller is NOT the same player sometimes
          //owner is the player who played March of Souls
          //the controller is the player who's creature was destroyed
          Card c = new Card();
          c.setToken(true);
          c.setOwner(owner);
          c.setController(owner);

          c.setAttack(1);
          c.setDefense(1);
          c.addKeyword("Flying");

          c.setManaCost("W");
          c.addType("Creature");
          c.addType("Spirit");
          return c;
        }//getCreature()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************


//*************** START *********** START **************************
if(cardName.equals("Wrath of God") || cardName.equals("Damnation"))
{
 SpellAbility spell = new Spell(card)
{
public void resolve()
{
CardList all = new CardList();
all.addAll(AllZone.Human_Play.getCards());
all.addAll(AllZone.Computer_Play.getCards());

for(int i = 0; i < all.size(); i++)
{
  Card c = all.get(i);
  if(c.isCreature())
    AllZone.GameAction.destroyNoRegeneration(c);
}
}//resolve()
public boolean canPlayAI()
{
CardList human    = new CardList(AllZone.Human_Play.getCards());
CardList computer = new CardList(AllZone.Computer_Play.getCards());

human    = human.getType("Creature");
computer = computer.getType("Creature");

//the computer will at least destroy 2 more human creatures
return computer.size() < human.size()-1  || (AllZone.Computer_Life.getLife() < 7 && !human.isEmpty());
}
};//SpellAbility
card.clearSpellAbility();
card.addSpellAbility(spell);
}//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Incendiary Command"))
    {
      //not sure what to call variables, so I just made up something
      final String[] m_player = new String[1];
      final Card[] m_land = new Card[1];

      final ArrayList userChoice = new ArrayList();

      final String[] cardChoice = {
        "Incendiary Command deals 4 damage to target player",
        "Incendiary Command deals 2 damage to each creature",
        "Destroy target nonbasic land",
        "Each player discards all cards in his or her hand, then draws that many cards"
      };

      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
//          System.out.println(userChoice);
//          System.out.println(m_land[0]);
//          System.out.println(m_player[0]);

          //"Incendiary Command deals 4 damage to target player",
          if(userChoice.contains(cardChoice[0]))
            AllZone.GameAction.getPlayerLife(m_player[0]).subtractLife(4);

          //"Incendiary Command deals 2 damage to each creature",
          if(userChoice.contains(cardChoice[1]))
          {
            //get all creatures
            CardList list = new CardList();
            list.addAll(AllZone.Human_Play.getCards());
            list.addAll(AllZone.Computer_Play.getCards());
            list = list.getType("Creature");

            for(int i = 0; i < list.size(); i++)
              list.get(i).addDamage(2);
          }

          //"Destroy target nonbasic land",
          if(userChoice.contains(cardChoice[2]))
            AllZone.GameAction.destroy(m_land[0]);

          //"Each player discards all cards in his or her hand, then draws that many cards"
          if(userChoice.contains(cardChoice[3]))
          {
            discardDraw(Constant.Player.Computer);
            discardDraw(Constant.Player.Human);
          }
        }//resolve()

        void discardDraw(String player)
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, player);
          int n = hand.size();

          //technically should let the user discard one card at a time
          //in case graveyard order matters
          for(int i = 0; i < n; i++)
            AllZone.GameAction.discardRandom(player);

          for(int i = 0; i < n; i++)
            AllZone.GameAction.drawCard(player);
        }
        public boolean canPlayAI()
        {
          return false;
        }
      };//SpellAbility

      final Command setStackDescription = new Command()
      {
        public void execute()
        {
          ArrayList a = new ArrayList();
          if(userChoice.contains(cardChoice[0]))
            a.add("deals 4 damage to " +m_player[0]);

          if(userChoice.contains(cardChoice[1]))
            a.add("deals 2 damage to each creature");

          if(userChoice.contains(cardChoice[2]))
            a.add("destroy " +m_land[0]);

          if(userChoice.contains(cardChoice[3]))
            a.add("each player discards all cards in his or her hand, then draws that many cards");

          String s = a.get(0) +", " +a.get(1);
          spell.setStackDescription(card.getName() +" - " +s);
        }
      };//Command


      final Input targetLand = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target nonbasic land");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card c, PlayerZone zone)
        {
          if(c.isLand() &&
             zone.is(Constant.Zone.Play) &&
             !c.getType().contains("Basic"))
          {
            m_land[0] = c;
            setStackDescription.execute();

            stopSetNext(new Input_PayManaCost(spell));
          }//if
        }//selectCard()
      };//Input targetLand

      final Input targetPlayer = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target player");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectPlayer(String player)
        {
          m_player[0] = player;
          setStackDescription.execute();

          //if user needs to target nonbasic land
          if(userChoice.contains(cardChoice[2]))
            stopSetNext(targetLand);
          else
          {
            stopSetNext(new Input_PayManaCost(spell));
          }
        }//selectPlayer()
      };//Input targetPlayer


      Input chooseTwoInput = new Input()
      {
        public void showMessage()
        {
          //reset variables
          m_player[0] = null;
          m_land[0] = null;

          userChoice.clear();

          ArrayList display = new ArrayList();

          //get all
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());

          CardList land = list.getType("Land");
          CardList basicLand = list.getType("Basic");

          display.add("Incendiary Command deals 4 damage to target player");
          display.add("Incendiary Command deals 2 damage to each creature");
          if(land.size() != basicLand.size())
            display.add("Destroy target nonbasic land");
          display.add("Each player discards all cards in his or her hand, then draws that many cards");

          ArrayList a = chooseTwo(display);
          //everything stops here if user cancelled
          if(a == null)
          {
            stop();
            return;
          }

          userChoice.addAll(a);

          if(userChoice.contains(cardChoice[0]))
            stopSetNext(targetPlayer);
          else if(userChoice.contains(cardChoice[2]))
            stopSetNext(targetLand);
          else
          {
            setStackDescription.execute();

            stopSetNext(new Input_PayManaCost(spell));
          }
        }//showMessage()

        ArrayList chooseTwo(ArrayList choices)
        {
          ArrayList out = new ArrayList();
          Object o = AllZone.Display.getChoiceOptional("Choose Two", choices.toArray());
          if(o == null)
            return null;

          out.add(o);

          choices.remove(out.get(0));
          o = AllZone.Display.getChoiceOptional("Choose Two", choices.toArray());
          if(o == null)
            return null;

          out.add(o);

          return out;
        }//chooseTwo()
      };//Input chooseTwoInput

      card.clearSpellAbility();
      card.addSpellAbility(spell);
      spell.setBeforePayMana(chooseTwoInput);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Boil"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());

          for(int i = 0; i < all.size(); i++)
          {
            Card c = all.get(i);
            if(c.getType().contains("Island"))
              AllZone.GameAction.destroy(c);
          }
        }//resolve()
        public boolean canPlayAI()
        {
          CardList list = new CardList(AllZone.Human_Play.getCards());
          list = list.getType("Island");

          return 3 < list.size();
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Plague Wind"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, opponent);

          CardList all = new CardList(play.getCards());
          all = all.getType("Creature");

          for(int i = 0; i < all.size(); i++)
          {
            Card c = all.get(i);
            if(c.isCreature())
              AllZone.GameAction.destroyNoRegeneration(c);
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Path of Anger's Flame"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          Card[] att = c.getAttackers();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()
        public void resolve()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList start = new CardList(play.getCards());
          final CardList list = start.getType("Creature");

          for(int i = 0; i < list.size(); i++)
            list.get(i).setAttack(list.get(i).getAttack() + 2);

          play.updateObservers();

          Command untilEOT = new Command()
          {
            public void execute()
            {
              for(int i = 0; i < list.size(); i++)
                if(AllZone.GameAction.isCardInPlay(list.get(i)))
                  list.get(i).setAttack(list.get(i).getAttack() - 2);
            }
          };
          AllZone.EndOfTurn.addUntil(untilEOT);
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Kjeldoran War Cry"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          Card[] att = c.getAttackers();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()
        public void resolve()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList start = new CardList(play.getCards());
          final CardList list = start.getType("Creature");

          final int boost = countCards();

          for(int i = 0; i < list.size(); i++)
          {
            list.get(i).setAttack(list.get(i).getAttack() + boost);
            list.get(i).setDefense(list.get(i).getDefense() + boost);
          }

          play.updateObservers();

          Command untilEOT = new Command()
          {
            public void execute()
            {
              for(int i = 0; i < list.size(); i++)
                if(AllZone.GameAction.isCardInPlay(list.get(i)))
                {
                  list.get(i).setAttack(list.get(i).getAttack() - boost);
                  list.get(i).setDefense(list.get(i).getDefense() - boost);
                }
            }
          };
          AllZone.EndOfTurn.addUntil(untilEOT);
        }//resolve()
        int countCards()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Graveyard.getCards());
          all.addAll(AllZone.Computer_Graveyard.getCards());

          all = all.getName("Kjeldoran War Cry");
          return all.size();
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Jokulhaups"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());

          for(int i = 0; i < all.size(); i++)
          {
            Card c = all.get(i);
            if(c.isCreature() || c.isArtifact() || c.isLand())
              AllZone.GameAction.destroyNoRegeneration(c);
          }
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Wheel of Fortune"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          discardDraw7(Constant.Player.Human);
          discardDraw7(Constant.Player.Computer);
        }//resolve()
        void discardDraw7(String player)
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, player);
          Card[] c = hand.getCards();
          for(int i = 0; i < c.length; i++)
            AllZone.GameAction.discard(c[i]);

          for(int i = 0; i < 7; i++)
            AllZone.GameAction.drawCard(player);
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Armageddon"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());

          for(int i = 0; i < all.size(); i++)
          {
            Card c = all.get(i);
            if(c.isLand())
              AllZone.GameAction.destroy(c);
          }
        }//resolve()
        public boolean canPlayAI()
        {
          int human    = countPower(AllZone.Human_Play);
          int computer = countPower(AllZone.Computer_Play);

          return human < computer || MyRandom.percentTrue(10);
        }
        public int countPower(PlayerZone play)
        {
          CardList list = new CardList(play.getCards());
          list = list.getType("Creature");
          int power = 0;
          for(int i = 0; i < list.size(); i++)
            power += list.get(i).getAttack();

          return power;
        }
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************


    //*************** START *********** START **************************
    if(cardName.equals("Remove Soul"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          SpellAbility sa = AllZone.Stack.pop();
          AllZone.GameAction.moveToGraveyard(sa.getSourceCard());
        }
        public boolean canPlay()
        {
          if(AllZone.Stack.size() == 0)
            return false;

          //see if spell is on stack and that opponent played it
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          SpellAbility sa = AllZone.Stack.peek();

          //is spell?, did opponent play it?, is this a creature spell?
          return sa.isSpell() &&
                 opponent.equals(sa.getSourceCard().getController()) &&
                 sa.getSourceCard().getType().contains("Creature");
        }//canPlay()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Counterspell"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          SpellAbility sa = AllZone.Stack.pop();
          AllZone.GameAction.moveToGraveyard(sa.getSourceCard());
        }
        public boolean canPlay()
        {
          if(AllZone.Stack.size() == 0)
            return false;

          //see if spell is on stack and that opponent played it
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          SpellAbility sa = AllZone.Stack.peek();

          return sa.isSpell() && opponent.equals(sa.getSourceCard().getController());
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Remand"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          //counter spell, return it to owner's hand
          SpellAbility sa = AllZone.Stack.pop();
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, sa.getSourceCard().getOwner());
          AllZone.GameAction.moveTo(hand, sa.getSourceCard());

          //draw card
          AllZone.GameAction.drawCard(card.getController());
        }
        public boolean canPlay()
        {
          if(AllZone.Stack.size() == 0)
            return false;

          //see if spell is on stack and that opponent played it
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          SpellAbility sa = AllZone.Stack.peek();

          return sa.isSpell() && opponent.equals(sa.getSourceCard().getController());
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Regress"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            if(getTargetCard().isToken())
              AllZone.getZone(getTargetCard()).remove(getTargetCard());
            else
            {
              PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, getTargetCard().getOwner());
              AllZone.GameAction.moveTo(hand, getTargetCard());
            }
          }
        }//resolve()
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetType("All", AllZone.Human_Play));

      spell.setBeforePayMana(CardFactoryUtil.input_targetType(spell, "All"));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Echoing Truth"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList human = CardFactoryUtil.AI_getHumanCreature();
          return 4 < AllZone.Phase.getTurn() && 0 < human.size();
        }
        public void chooseTargetAI()
        {
          CardList human = CardFactoryUtil.AI_getHumanCreature();
          setTargetCard(CardFactoryUtil.AI_getBestCreature(human));
        }

        public void resolve()
        {
          //if target card is not in play, just quit
          if(! AllZone.GameAction.isCardInPlay(getTargetCard()))
            return;

          //get all permanents
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());

          CardList sameName = all.getName(getTargetCard().getName());

          //bounce all permanents with the same name
          for(int i = 0; i < sameName.size(); i++)
          {
            if(sameName.get(i).isToken())
              AllZone.GameAction.destroy(sameName.get(i));
            else
            {
              PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, sameName.get(i).getOwner());
              AllZone.GameAction.moveTo(hand, sameName.get(i));
            }
          }//for
        }//resolve()
      };//SpellAbility
      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target creature for " +spell.getSourceCard());
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(! card.isLand() && zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }
      };//Input

      spell.setBeforePayMana(target);
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Repulse"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList human = CardFactoryUtil.AI_getHumanCreature();
          return 3 < AllZone.Phase.getTurn() && 0 < human.size();
        }
        public void chooseTargetAI()
        {
          CardList human = CardFactoryUtil.AI_getHumanCreature();
          setTargetCard(CardFactoryUtil.AI_getBestCreature(human));
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            if(getTargetCard().isToken())
              AllZone.getZone(getTargetCard()).remove(getTargetCard());
            else
            {
              PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, getTargetCard().getOwner());
              AllZone.GameAction.moveTo(hand, getTargetCard());
            }
            AllZone.GameAction.drawCard(card.getController());
          }//if
        }//resolve()
      };//SpellAbility
      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target creature for " +spell.getSourceCard());
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }
      };//Input

      spell.setBeforePayMana(target);
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Impulse"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          CardList top = new CardList();
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());

          Card c;
          for(int i = 0; i < 4; i++)
          {
            c = library.get(0);
            library.remove(0);
            top.add(c);
          }

          //let user get choice
          Card chosen = (Card) AllZone.Display.getChoice("Choose a card to put into your hand", top.toArray());
          top.remove(chosen);

          //put card in hand
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
          hand.add(chosen);

          //add cards to bottom of library
          for(int i = 0; i < top.size(); i++)
            library.add(top.get(i));
        }//resolve()
      };//SpellAbility

      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Bribery"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          String player = card.getController();
          if(player.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void humanResolve()
        {
          //choose creature from opponents library to put into play
          //shuffle opponent's library
          String opponent    = AllZone.GameAction.getOpponent(card.getController());
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, opponent);
          CardList choices   = new CardList(library.getCards());

          choices = choices.getType("Creature");
          Object o = AllZone.Display.getChoiceOptional("Choose a creature", choices.toArray());
          if(o != null)
            resolve((Card)o);
        }
        public void computerResolve()
        {
          CardList all = new CardList(AllZone.Human_Library.getCards());
          all = all.getType("Creature");

          CardList flying = all.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.getKeyword().contains("Flying");
            }
          });
          //get biggest flying creature
          Card biggest = null;
          if(flying.size() != 0)
          {
            biggest = flying.get(0);

            for(int i = 0; i < flying.size(); i++)
              if(biggest.getAttack() < flying.get(i).getAttack())
                biggest = flying.get(i);
          }

          //if flying creature is small, get biggest non-flying creature
          if(all.size() != 0 &&
            (biggest == null || biggest.getAttack() < 3))
          {
            biggest = all.get(0);

            for(int i = 0; i < all.size(); i++)
              if(biggest.getAttack() < all.get(i).getAttack())
                biggest = all.get(i);
          }
          if(biggest != null)
            resolve(biggest);
        }//computerResolve()
        public void resolve(Card selectedCard)
        {
          String opponent    = AllZone.GameAction.getOpponent(card.getController());
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, opponent);

          Card c = selectedCard;
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());

          //need to set controller before adding it to "play"
          c.setController(card.getController());
          c.setSickness(true);

          library.remove(c);
          play.add(c);


          AllZone.GameAction.shuffle(opponent);
        }//resolve()
      };

      spell.setBeforePayMana(new Input_PayManaCost(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Words of Wisdom"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
          AllZone.GameAction.drawCard(card.getController());

          String opponent = AllZone.GameAction.getOpponent(card.getController());
          AllZone.GameAction.drawCard(opponent);
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Counsel of the Soratami"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
          AllZone.GameAction.drawCard(card.getController());
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Concentrate") || cardName.equals("Harmonize"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          AllZone.GameAction.drawCard(card.getController());
          AllZone.GameAction.drawCard(card.getController());
          AllZone.GameAction.drawCard(card.getController());
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Amnesia"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, getTargetPlayer());
          Card[] c = hand.getCards();

          for(int i = 0; i < c.length; i++)
            if(! c[i].isLand())
              AllZone.GameAction.discard(c[i]);
        }
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetHuman());

      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));

      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Evacuation"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList all = new CardList();
          all.addAll(AllZone.Human_Play.getCards());
          all.addAll(AllZone.Computer_Play.getCards());
          all = all.getType("Creature");

          for(int i = 0; i < all.size(); i++)
          {
            //if is token, remove token from play, else return creature to hand
            if(all.get(i).isToken())
              getPlay(all.get(i)).remove(all.get(i));
            else
              AllZone.GameAction.moveTo(getHand(all.get(i)), all.get(i));
          }
        }//resolve()
        PlayerZone getPlay(Card c)
        {
          return AllZone.getZone(Constant.Zone.Play, c.getController());
        }

        PlayerZone getHand(Card c)
        {
          return AllZone.getZone(Constant.Zone.Hand, c.getOwner());
        }
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Ancestral Recall"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          AllZone.GameAction.drawCard(getTargetPlayer());
          AllZone.GameAction.drawCard(getTargetPlayer());
          AllZone.GameAction.drawCard(getTargetPlayer());
        }
        public boolean canPlayAI()
        {
          return AllZone.Computer_Hand.getCards().length <= 5;
        }
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetComputer());

      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Allied Strategies"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          int n = countLandTypes();

          for(int i = 0; i < n; i++)
            AllZone.GameAction.drawCard(getTargetPlayer());
        }

        int countLandTypes()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, getTargetPlayer());
          CardList land = new CardList(play.getCards());

          String basic[] = {"Forest", "Plains", "Mountain", "Island", "Swamp"};
          int count = 0;

          for(int i = 0; i < basic.length; i++)
          {
            CardList c = land.getType(basic[i]);
            if(! c.isEmpty())
              count++;
          }

          return count;
        }//countLandTypes()

        public boolean canPlayAI() {return AllZone.Computer_Hand.getCards().length <= 5;}
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetComputer());

      spell.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Opt"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          String player = card.getController();
          if(player.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void computerResolve()
        {
          //if top card of library is a land, put it on bottom of library
          if(AllZone.Computer_Library.getCards().length != 0)
          {
            Card top = AllZone.Computer_Library.get(0);
            if(top.isLand())
            {
              AllZone.Computer_Library.remove(top);
              AllZone.Computer_Library.add(top);
            }
          }
          AllZone.GameAction.drawCard(card.getController());
        }//computerResolve()
        public void humanResolve()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());

          //see if any cards are in library
          if(library.getCards().length != 0)
          {
            Card top = library.get(0);

            Object o = top;
            while(o instanceof Card)
              o = AllZone.Display.getChoice("Do you want draw this card?", new Object[] {top, "Yes", "No"});

            if(o.toString().equals("No"))
            {
              library.remove(top);
              library.add(top);
            }
          }//if
          AllZone.GameAction.drawCard(card.getController());
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Needle Storm"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          CardList list = new CardList();
          list.addAll(AllZone.Human_Play.getCards());
          list.addAll(AllZone.Computer_Play.getCards());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c)
            {
              return c.isCreature() && c.getKeyword().contains("Flying");
            }
          });

          for(int i = 0; i < list.size(); i++)
            list.get(i).addDamage(4);
        }//resolve()

        public boolean canPlayAI() {return CardFactoryUtil.AI_getHumanCreature("Flying").size() != 0;}
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************







    //*************** START *********** START **************************
    if(cardName.equals("Wandering Stream"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
          life.addLife(countLandTypes() * 2);
        }//resolve()
        int countLandTypes()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList land = new CardList(play.getCards());

          String basic[] = {"Forest", "Plains", "Mountain", "Island", "Swamp"};
          int count = 0;

          for(int i = 0; i < basic.length; i++)
          {
            CardList c = land.getType(basic[i]);
            if(! c.isEmpty())
              count++;
          }

          return count;
        }//countLandTypes()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Worldly Tutor"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return 6 < AllZone.Phase.getTurn();
        }

        public void resolve()
        {
          String player = card.getController();
          if(player.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void computerResolve()
        {
          CardList creature = new CardList(AllZone.Computer_Library.getCards());
          creature = creature.getType("Creature");
          if(creature.size() != 0)
          {
            Card c = creature.get(0);
            AllZone.GameAction.shuffle(card.getController());

            //move to top of library
            AllZone.Computer_Library.remove(c);
            AllZone.Computer_Library.add(c, 0);
          }
        }//computerResolve()
        public void humanResolve()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());

          CardList list = new CardList(library.getCards());
          list = list.getType("Creature");

          if(list.size() != 0)
          {
            Object o = AllZone.Display.getChoiceOptional("Select a creature", list.toArray());

            AllZone.GameAction.shuffle(card.getController());
            if(o != null)
            {
              //put creature on top of library
              library.remove(o);
              library.add((Card)o, 0);
            }
          }//if
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Pulse of the Tangle"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("G");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Beast");
          c.setAttack(3);
          c.setDefense(3);

          play.add(c);

          //return card to hand if necessary
          String opponent = AllZone.GameAction.getOpponent(card.getController());
          PlayerZone oppPlay = AllZone.getZone(Constant.Zone.Play, opponent);
          PlayerZone myPlay  = AllZone.getZone(Constant.Zone.Play, card.getController());

          CardList oppList = new CardList(oppPlay.getCards());
          CardList myList = new CardList(myPlay.getCards());

          oppList = oppList.getType("Creature");
          myList  = myList.getType("Creature");

          //if true, return card to hand
          if(myList.size() < oppList.size())
          {
            PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());
            hand.add(card);
          }
          else
            AllZone.GameAction.moveToGraveyard(card);
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Delirium Skeins"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          for(int i = 0; i < 3; i++)
            AllZone.GameAction.discardRandom(Constant.Player.Computer);

          AllZone.InputControl.setInput(CardFactoryUtil.input_discard(3));
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Wrap in Vigor"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          Card[] att = c.getAttackers();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()

        public void resolve()
        {
          final Card[] c = AllZone.getZone(Constant.Zone.Play, card.getController()).getCards();

          for(int i = 0; i < c.length; i++)
            if(c[i].isCreature())
              c[i].addShield();

          AllZone.EndOfTurn.addUntil(new Command()
          {
            public void execute()
            {
              for(int i = 0; i < c.length; i++)
                c[i].resetShield();
            }
          });

        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Strangling Soot"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList c = CardFactoryUtil.AI_getHumanCreature(3);
          CardListUtil.sortAttack(c);
          CardListUtil.sortFlying(c);

          if(c.isEmpty())
            return false;
          else
          {
            setTargetCard(c.get(0));
            return true;
          }
        }//canPlayAI()

        public void resolve()
        {
          Card c = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(c) && c.getDefense() <= 3)
            AllZone.GameAction.destroy(c);
        }//resolve()
      };//SpellAbility

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target creature for " +card.getName() +" - creature must have a toughness of 3 or less");
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play) && card.getDefense() <= 3)
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }
      };//Input
      card.clearSpellAbility();
      card.addSpellAbility(spell);
      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Minions' Murmurs"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          int n = countCreatures();
          return 0 < n && n < AllZone.Computer_Life.getLife();
        }//canPlayAI()

        public void resolve()
        {
          int n = countCreatures();
          for(int i = 0; i < n; i++)
            AllZone.GameAction.drawCard(card.getController());

          AllZone.GameAction.getPlayerLife(card.getController()).subtractLife(n);
        }//resolve()

        int countCreatures()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList list = new CardList(play.getCards());
          list = list.getType("Creature");
          return list.size();
        }
      };//SpellAbility

      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Tendrils of Corruption"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList human = CardFactoryUtil.AI_getHumanCreature();
          return 0 < human.size();
        }//canPlayAI()

        public void chooseTargetAI()
        {
          CardList human = CardFactoryUtil.AI_getHumanCreature();
          CardListUtil.sortAttack(human);
          setTargetCard(human.get(0));
        }

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
          {
            int n = countSwamps();
            getTargetCard().addDamage(n);

            PlayerLife life = AllZone.GameAction.getPlayerLife(card.getController());
            life.addLife(n);
          }
        }//resolve()

        int countSwamps()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList list = new CardList(play.getCards());
          list = list.getType("Swamp");
          return list.size();
        }
      };//SpellAbility
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));

      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Ichor Slick"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList c = CardFactoryUtil.AI_getHumanCreature(3);
          CardListUtil.sortAttack(c);
          CardListUtil.sortFlying(c);

          if(c.isEmpty())
            return false;
          else
          {
            setTargetCard(c.get(0));
            return true;
          }
        }//canPlayAI()

        public void resolve()
        {
          if(AllZone.GameAction.isCardInPlay(getTargetCard()))
            AllZone.GameAction.destroy(getTargetCard());
        }//resolve()
      };//SpellAbility

      Input target = new Input()
      {
        public void showMessage()
        {
          AllZone.Display.showMessage("Select target creature for " +card.getName());
          ButtonUtil.enableOnlyCancel();
        }
        public void selectButtonCancel() {stop();}
        public void selectCard(Card card, PlayerZone zone)
        {
          if(card.isCreature() && zone.is(Constant.Zone.Play))
          {
            spell.setTargetCard(card);
            stopSetNext(new Input_PayManaCost(spell));
          }
        }
      };//Input
      spell.setDescription("Target creature gets -3/-3 until end of turn");

      card.clearSpellAbility();
      card.addSpellAbility(spell);
      card.addSpellAbility(CardFactoryUtil.ability_cycle(card, "2"));

      spell.setBeforePayMana(target);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Funeral Charm"))
    {
      //discard
      final SpellAbility spell_one = new Spell(card)
      {
        public boolean canPlayAI()
        {
          setTargetPlayer(Constant.Player.Human);
          return MyRandom.random.nextBoolean();
        }
        public void resolve()
        {
          if(Constant.Player.Computer.equals(getTargetPlayer()))
            AllZone.GameAction.discardRandom(getTargetPlayer());
          else
            AllZone.InputControl.setInput(CardFactoryUtil.input_discard());
        }//resolve()
      };//SpellAbility
      spell_one.setDescription("Target player discards a card.");
      spell_one.setBeforePayMana(CardFactoryUtil.input_targetPlayer(spell_one));


      //creature gets +2/-1
      final SpellAbility spell_two = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList list = new CardList(ComputerUtil.getAttackers().getAttackers());
          list = list.filter(new CardListFilter()
          {
            public boolean addCard(Card c) {return 1 < c.getDefense();}
          });

          list.shuffle();
          if(list.size() > 0)
            setTargetCard(list.get(0));

          return (list.size() > 0) && MyRandom.random.nextBoolean();
        }


        public void resolve()
        {
          final Card c = getTargetCard();

          if(AllZone.GameAction.isCardInPlay(c))
          {
            c.setAttack(c.getAttack() + 2);
            c.setDefense(c.getDefense() - 1);

            Command until = new Command()
            {
              public void execute()
              {
                c.setAttack(c.getAttack() - 2);
                c.setDefense(c.getDefense() + 1);
              }
            };//Command
            AllZone.EndOfTurn.addUntil(until);
          }//if card in play?
        }//resolve()
      };//SpellAbility
      spell_two.setDescription("Target creature gets +2/-1 until end of turn.");
      spell_two.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell_two));

      card.clearSpellAbility();
      card.addSpellAbility(spell_one);
      card.addSpellAbility(spell_two);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Regrowth"))
    {
      final SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          PlayerZone hand      = AllZone.getZone(Constant.Zone.Hand     , card.getController());
          PlayerZone graveyard = AllZone.getZone(Constant.Zone.Graveyard, card.getController());

          if(AllZone.GameAction.isCardInZone(getTargetCard(), graveyard))
          {
            graveyard.remove(getTargetCard());
            hand.add(getTargetCard());
          }
        }//resolve()
        public boolean canPlay()
        {
          PlayerZone graveyard = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          return graveyard.getCards().length != 0 && super.canPlay();
        }
      };
      Input runtime = new Input()
      {
        public void showMessage()
        {
          PlayerZone graveyard = AllZone.getZone(Constant.Zone.Graveyard, card.getController());
          Object o = AllZone.Display.getChoiceOptional("Select target card", graveyard.getCards());
          if(o == null)
            stop();
          else
          {
            spell.setStackDescription("Return " +o +" to its owner's hand");
            spell.setTargetCard((Card)o);

            stopSetNext(new Input_PayManaCost(spell));
          }
        }//showMessage()
      };
      spell.setChooseTargetAI(CardFactoryUtil.AI_targetType("All", AllZone.Computer_Graveyard));
      spell.setBeforePayMana(runtime);
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Commune with Nature"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI() {return false;}

        public void resolve()
        {
          String player = card.getController();
          if(player.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void computerResolve()
        {
          //get top 5 cards of library
          CardList top = new CardList();
          int limit = AllZone.Computer_Library.getCards().length;

          for(int i = 0; i < 5 && i < limit; i++)
          {
            top.add(AllZone.Computer_Library.get(0));
            AllZone.Computer_Library.remove(0);
          }

          //put creature card in hand, if there is one
          CardList creature = top.getType("Creature");
          if(creature.size() != 0)
          {
            AllZone.Computer_Hand.add(creature.get(0));
            top.remove(creature.get(0));
          }

          //put cards on bottom of library
          for(int i = 0; i < top.size(); i++)
            AllZone.Computer_Library.add(top.get(i));
        }//computerResolve()
        public void humanResolve()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone hand    = AllZone.getZone(Constant.Zone.Hand   , card.getController());

          CardList list = new CardList();
          for(int i = 0; i < 5 && i < library.getCards().length; i++)
            list.add(library.get(i));

          //optional, select a creature
          Object o = AllZone.Display.getChoiceOptional("Select a creature", list.toArray());
          if(o != null && ((Card)o).isCreature())
          {
            AllZone.GameAction.moveTo(hand, (Card)o);
            list.remove((Card)o);
          }

          //put remaining cards on the bottom of the library
          for(int i = 0; i < list.size(); i++)
          {
            library.remove(list.get(i));
            library.add(list.get(i));
          }
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Kodama's Reach"))
    {
      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          String player = card.getController();

          if(player.equals(Constant.Player.Human))
            humanResolve();
          else
            computerResolve();
        }
        public void computerResolve()
        {
          CardList land = new CardList(AllZone.Computer_Library.getCards());
          land = land.getType("Basic");

          //just to make the computer a little less predictable
          land.shuffle();

          //3 branches: 1-no land in deck, 2-one land in deck, 3-two or more land in deck
          if(land.size() != 0)
          {
            //branch 2 - at least 1 land in library
            Card tapped = land.remove(0);
            tapped.tap();

            AllZone.Computer_Play.add(tapped);
            AllZone.Computer_Library.remove(tapped);

            //branch 3
            if(land.size() != 0)
            {
              Card toHand = land.remove(0);
              AllZone.Computer_Hand.add(toHand);
              AllZone.Computer_Library.remove(toHand);
            }
          }
        }//computerResolve()

        public void humanResolve()
        {
          PlayerZone play    = AllZone.getZone(Constant.Zone.Play   , card.getController());
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone hand    = AllZone.getZone(Constant.Zone.Hand   , card.getController());

          CardList list = new CardList(library.getCards());
          list = list.getType("Basic");

          //3 branches: 1-no land in deck, 2-one land in deck, 3-two or more land in deck

          //branch 1
          if(list.size() == 0)
            return;

          //branch 2
          Object o = AllZone.Display.getChoiceOptional("Put into play tapped", list.toArray());
          if(o != null)
          {
            Card c = (Card)o;
            c.tap();
            list.remove(c);

            library.remove(c);
            play.add(c);

            if(list.size() == 0)
              return;

            o = AllZone.Display.getChoiceOptional("Put into your hand", list.toArray());
            if(o != null)
            {
              //branch 3
              library.remove(o);
              hand.add(o);
            }
            AllZone.GameAction.shuffle(card.getController());
          }//if
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Gift of Estates"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlay()
        {
          String oppPlayer = AllZone.GameAction.getOpponent(card.getController());

          PlayerZone selfZone = AllZone.getZone(Constant.Zone.Play, card.getController());
          PlayerZone oppZone = AllZone.getZone(Constant.Zone.Play, oppPlayer);

          CardList self = new CardList(selfZone.getCards());
          CardList opp = new CardList(oppZone.getCards());

          self = self.getType("Land");
          opp = opp.getType("Land");

          return (self.size() < opp.size()) && super.canPlay();
        }//canPlay()

        public void resolve()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());

          CardList plains = new CardList(library.getCards());
          plains = plains.getType("Plains");

          for(int i = 0; i < 3 && i < plains.size(); i++)
            AllZone.GameAction.moveTo(hand, plains.get(i));
        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Tithe"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean oppMoreLand()
        {
          String oppPlayer = AllZone.GameAction.getOpponent(card.getController());

          PlayerZone selfZone = AllZone.getZone(Constant.Zone.Play, card.getController());
          PlayerZone oppZone = AllZone.getZone(Constant.Zone.Play, oppPlayer);

          CardList self = new CardList(selfZone.getCards());
          CardList opp = new CardList(oppZone.getCards());

          self = self.getType("Land");
          opp = opp.getType("Land");

          return (self.size() < opp.size()) && super.canPlay();
        }//oppoMoreLand()

        public void resolve()
        {
          PlayerZone library = AllZone.getZone(Constant.Zone.Library, card.getController());
          PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, card.getController());

          CardList plains = new CardList(library.getCards());
          plains = plains.getType("Plains");

          if(0 < plains.size())
            AllZone.GameAction.moveTo(hand, plains.get(0));

          if(oppMoreLand() && 1 < plains.size())
            AllZone.GameAction.moveTo(hand, plains.get(1));

        }//resolve()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





//*************** START *********** START **************************
    if(cardName.equals("Giant Growth") || cardName.equals("Brute Force"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public void chooseTargetAI()
        {
          setTargetCard(getAttacker());
        }
        public Card getAttacker()
        {
//target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          Card[] att = c.getAttackers();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()
        public void resolve()
        {
          final Card[] target = new Card[1];
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(target[0]))
              {
                target[0].setAttack (target[0].getAttack() - 3);
                target[0].setDefense(target[0].getDefense()- 3);
              }
            }
          };

          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 3);
            target[0].setDefense(target[0].getDefense()+ 3);

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    //*************** START *********** START **************************
    if(cardName.equals("Nameless Inversion"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          CardList list = CardFactoryUtil.AI_getHumanCreature(3);
          CardListUtil.sortFlying(list);

          for(int i = 0; i < list.size(); i++)
            if(2 <= list.get(i).getAttack())
            {
              setTargetCard(list.get(i));
              return true;
            }
          return false;
        }//canPlayAI()

        public void resolve()
        {
          final Card[] target = new Card[1];
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(target[0]))
              {
                target[0].setAttack (target[0].getAttack() - 3);
                target[0].setDefense(target[0].getDefense()+ 3);
              }
            }
          };

          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 3);
            target[0].setDefense(target[0].getDefense()- 3);

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************








    //*************** START *********** START **************************
    if(cardName.equals("Tromp the Domains"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          Card[] att = c.getAttackers();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()

        int countLandTypes()
        {
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList land = new CardList(play.getCards());

          String basic[] = {"Forest", "Plains", "Mountain", "Island", "Swamp"};
          int count = 0;

          for(int i = 0; i < basic.length; i++)
          {
            CardList c = land.getType(basic[i]);
            if(! c.isEmpty())
              count++;
          }

          return count;
        }//countLandTypes()
        public void resolve()
        {

          final int boost = countLandTypes();
          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          CardList list = new CardList(play.getCards());
          Card c;

          for(int i = 0; i < list.size(); i++)
          {
            final Card[] target = new Card[1];
            target[0] = list.get(i);

            final Command untilEOT = new Command()
            {
              public void execute()
              {
                if(AllZone.GameAction.isCardInPlay(target[0]))
                {
                  target[0].setAttack (target[0].getAttack() - boost);
                  target[0].setDefense(target[0].getDefense()- boost);

                  target[0].removeKeyword("Trample");
                }
              }
            };//Command

            if(AllZone.GameAction.isCardInPlay(target[0]))
            {
              target[0].setAttack (target[0].getAttack() + boost);
              target[0].setDefense(target[0].getDefense()+ boost);

              target[0].addKeyword("Trample");

              AllZone.EndOfTurn.addUntil(untilEOT);
            }//if
          }//for
        }//resolve()
      };
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************





    //*************** START *********** START **************************
    if(cardName.equals("Primal Boost"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public void chooseTargetAI()
        {
          setTargetCard(getAttacker());
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();

          CardList list = new CardList(c.getAttackers());
          CardListUtil.sortFlying(list);

          Card[] att = list.toArray();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()
        public void resolve()
        {
          final Card[] target = new Card[1];
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(target[0]))
              {
                target[0].setAttack (target[0].getAttack() - 4);
                target[0].setDefense(target[0].getDefense()- 4);
              }
            }
          };

          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 4);
            target[0].setDefense(target[0].getDefense()+ 4);

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };
      spell.setDescription("Target creature gets +4/+4 until end of turn.");
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
      card.addSpellAbility(CardFactoryUtil.ability_cycle(card, "2 G"));
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Wildsize"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public void chooseTargetAI()
        {
          setTargetCard(getAttacker());
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();

          CardList list = new CardList(c.getAttackers());
          CardListUtil.sortFlying(list);

          Card[] att = list.toArray();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()

        public void resolve()
        {
          final Card[] target = new Card[1];
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(target[0]))
              {
                target[0].setAttack (target[0].getAttack() - 2);
                target[0].setDefense(target[0].getDefense()- 2);

                target[0].removeKeyword("Trample");
              }
            }
          };

          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 2);
            target[0].setDefense(target[0].getDefense()+ 2);
            target[0].addKeyword("Trample");

            AllZone.EndOfTurn.addUntil(untilEOT);
            AllZone.GameAction.drawCard(card.getController());
          }
        }//resolve()
      };
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Feral Lightning"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return AllZone.Phase.getPhase().equals(Constant.Phase.Main1);
        }


        public void resolve()
        {
          final Card[] token = new Card[3];
          final Command atEOT = new Command()
          {
            public void execute()
            {
              //destroy tokens at end of turn
              for(int i = 0; i < token.length; i++)
                if(AllZone.GameAction.isCardInPlay(token[i]))
                  AllZone.GameAction.destroy(token[i]);
            }
          };
          AllZone.EndOfTurn.addAt(atEOT);

          for(int i = 0; i < token.length; i++)
            token[i] = makeToken();
        }//resolve()
        Card makeToken()
        {
          Card c = new Card();

          c.setOwner(card.getController());
          c.setController(card.getController());

          c.setManaCost("R");
          c.setToken(true);

          c.addType("Creature");
          c.addType("Elemental");
          c.setAttack(3);
          c.setDefense(1);
          c.addKeyword("Haste");

          PlayerZone play = AllZone.getZone(Constant.Zone.Play, card.getController());
          play.add(c);

          return c;
        }//makeToken()
      };//SpellAbility
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************






    //*************** START *********** START **************************
    if(cardName.equals("Inspirit"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public void chooseTargetAI()
        {
          setTargetCard(getAttacker());
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          Card[] att = c.getAttackers();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()
        public void resolve()
        {
          final Card[] target = new Card[1];
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(target[0]))
              {
                target[0].setAttack (target[0].getAttack() - 2);
                target[0].setDefense(target[0].getDefense()- 4);
              }
            }
          };

          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 2);
            target[0].setDefense(target[0].getDefense()+ 4);

            target[0].untap();

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Might of Oaks"))
    {
      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public void chooseTargetAI()
        {
          setTargetCard(getAttacker());
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          Card[] att = c.getAttackers();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()
        public void resolve()
        {
          final Card[] target = new Card[1];
          final Command untilEOT = new Command()
          {
            public void execute()
            {
              if(AllZone.GameAction.isCardInPlay(target[0]))
              {
                target[0].setAttack (target[0].getAttack() - 7);
                target[0].setDefense(target[0].getDefense()- 7);
              }
            }
          };

          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 7);
            target[0].setDefense(target[0].getDefense()+ 7);

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
      };
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Aggressive Urge"))
    {
      final Card[] target = new Card[1];
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() - 1);
            target[0].setDefense(target[0].getDefense()- 1);
          }
        }
      };

      SpellAbility spell = new Spell(card)
      {
        public boolean canPlayAI()
        {
          return getAttacker() != null;
        }
        public void chooseTargetAI()
        {
          setTargetCard(getAttacker());
        }
        public Card getAttacker()
        {
          //target creature that is going to attack
          Combat c = ComputerUtil.getAttackers();
          Card[] att = c.getAttackers();
          if(att.length != 0)
            return att[0];
          else
            return null;
        }//getAttacker()
        public void resolve()
        {
          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].setAttack (target[0].getAttack() + 1);
            target[0].setDefense(target[0].getDefense()+ 1);

            AllZone.EndOfTurn.addUntil(untilEOT);
            AllZone.GameAction.drawCard(card.getController());
          }
        }//resolve()
      };
      spell.setBeforePayMana(CardFactoryUtil.input_targetCreature(spell));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************




    //*************** START *********** START **************************
    if(cardName.equals("Animate Land"))
    {
      final Card[] target = new Card[1];
      final Command untilEOT = new Command()
      {
        public void execute()
        {
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].removeType("Creature");
          }
        }
      };

      SpellAbility spell = new Spell(card)
      {
        public void resolve()
        {
          target[0] = getTargetCard();
          if(AllZone.GameAction.isCardInPlay(target[0]))
          {
            target[0].addType("Creature");
            target[0].setAttack(3);
            target[0].setDefense(3);

            AllZone.EndOfTurn.addUntil(untilEOT);
          }
        }//resolve()
        public boolean canPlayAI()
        {
          return false;
/* all this doesnt work, computer will not attack with the animated land

//does the computer have any land in play?
CardList land = new CardList(AllZone.Computer_Play.getCards());
land = land.getType("Land");
land = land.filter(new CardListFilter()
{
  public boolean addCard(Card c)
  {
          //checks for summoning sickness, and is not tapped
    return CombatUtil.canAttack(c);
  }
});
return land.size() > 1 && CardFactoryUtil.AI_isMainPhase();
*/
        }
      };//SpellAbility
//      spell.setChooseTargetAI(CardFactoryUtil.AI_targetType("Land", AllZone.Computer_Play));

      spell.setBeforePayMana(CardFactoryUtil.input_targetType(spell, "Land"));
      card.clearSpellAbility();
      card.addSpellAbility(spell);
    }//*************** END ************ END **************************



    return card;
  }
  //copies stats like attack, defense, etc..
  private Card copyStats(Object o)
  {
    Card sim = (Card) o;
    Card c = new Card();

    c.setAttack  (sim.getAttack());
    c.setDefense(sim.getDefense());
    c.setKeyword(sim.getKeyword());
    c.setName    (sim.getName());
    c.setType    (sim.getType());
    c.setText    (sim.getSpellText());
    c.setManaCost(sim.getManaCost());

    return c;
  }//copyStats()
  public static void main(String[] args)
  {
    CardFactory f = new CardFactory("cards.txt");
    Card c = f.getCard("Arc-Slogger",  "d");
    System.out.println(c.getOwner());
  }
}