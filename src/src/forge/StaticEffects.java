package forge;

import com.esotericsoftware.minlog.Log;
import forge.card.cardFactory.CardFactoryUtil;

import java.util.HashMap;


public class StaticEffects {
    //this is used to keep track of all state-based effects in play:
    private HashMap<String, Integer> stateBasedMap = new HashMap<String, Integer>();

    //this is used to define all cards that are state-based effects, and map the corresponding commands to their cardnames
    private static HashMap<String, String[]> cardToEffectsList = new HashMap<String, String[]>();

    public StaticEffects() {
        initStateBasedEffectsList();
    }

    public void initStateBasedEffectsList() {
        //value has to be an array, since certain cards have multiple commands associated with them

        cardToEffectsList.put("Avatar", new String[]{"Ajani_Avatar_Token"});
        cardToEffectsList.put("Coat of Arms", new String[]{"Coat_of_Arms"});
        cardToEffectsList.put("Conspiracy", new String[]{"Conspiracy"});
        cardToEffectsList.put("Favor of the Mighty", new String[]{"Favor_of_the_Mighty"});
        cardToEffectsList.put("Gaddock Teeg", new String[]{"Gaddock_Teeg"});

        cardToEffectsList.put("Homarid", new String[]{"Homarid"});
        cardToEffectsList.put("Iona, Shield of Emeria", new String[]{"Iona_Shield_of_Emeria"});
        cardToEffectsList.put("Liu Bei, Lord of Shu", new String[]{"Liu_Bei"});

        cardToEffectsList.put("Meddling Mage", new String[]{"Meddling_Mage"});
        cardToEffectsList.put("Mul Daya Channelers", new String[]{"Mul_Daya_Channelers"});
        cardToEffectsList.put("Muraganda Petroglyphs", new String[]{"Muraganda_Petroglyphs"});
        cardToEffectsList.put("Old Man of the Sea", new String[]{"Old_Man_of_the_Sea"});

        cardToEffectsList.put("Phylactery Lich", new String[]{"Phylactery_Lich"});
        cardToEffectsList.put("Tarmogoyf", new String[]{"Tarmogoyf"});

        cardToEffectsList.put("Umbra Stalker", new String[]{"Umbra_Stalker"});
        cardToEffectsList.put("Wolf", new String[]{"Sound_the_Call_Wolf"});

    }

    public HashMap<String, String[]> getCardToEffectsList() {
        return cardToEffectsList;
    }

    public void addStateBasedEffect(String s) {
        if (stateBasedMap.containsKey(s))
            stateBasedMap.put(s, stateBasedMap.get(s) + 1);
        else
            stateBasedMap.put(s, 1);
    }

    public void removeStateBasedEffect(String s) {
        if (stateBasedMap.containsKey(s)) {
            stateBasedMap.put(s, stateBasedMap.get(s) - 1);
            if (stateBasedMap.get(s) == 0)
                stateBasedMap.remove(s);
        }
    }

    public HashMap<String, Integer> getStateBasedMap() {
        return stateBasedMap;
    }

    public void reset() {
        stateBasedMap.clear();
    }

    public void rePopulateStateBasedList() {
        reset();

        CardList cards = AllZoneUtil.getCardsInPlay();

        Log.debug("== Start add state effects ==");
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (cardToEffectsList.containsKey(c.getName())) {
                String[] effects = getCardToEffectsList().get(c.getName());
                for (String effect : effects) {
                    addStateBasedEffect(effect);
                    Log.debug("Added " + effect);
                }
            }
            if (c.isEmblem() && !CardFactoryUtil.checkEmblemKeyword(c).equals("")) {
                String s = CardFactoryUtil.checkEmblemKeyword(c);
                addStateBasedEffect(s);
                Log.debug("Added " + s);
            }
        }
        Log.debug("== End add state effects ==");

    }
}
