package forge.card.staticAbility;

import forge.*;

import java.util.HashMap;
import java.util.Map;

public class StaticAbility {

    private Card hostCard = null;
    private HashMap<String, String> mapParams = new HashMap<String, String>();

    /**
     * <p>getHostCard.</p>
     *
     * @return a {@link forge.Card} object.
     */
    public Card getHostCard() {
        return hostCard;
    }

    /**
     * <p>Getter for the field <code>mapParams</code>.</p>
     *
     * @return a {@link java.util.HashMap} object.
     */
    public HashMap<String, String> getMapParams() {
        return mapParams;
    }

    //*******************************************************

    /**
     * <p>Getter for the field <code>mapParams</code>.</p>
     *
     * @param abString a {@link java.lang.String} object.
     * @param hostCard a {@link forge.Card} object.
     * @return a {@link java.util.HashMap} object.
     */
    public HashMap<String, String> getMapParams(String abString, Card hostCard) {
        HashMap<String, String> mapParameters = new HashMap<String, String>();

        if (!(abString.length() > 0))
            throw new RuntimeException("StaticEffectFactory : getAbility -- abString too short in " + hostCard.getName() + ": [" + abString + "]");

        String a[] = abString.split("\\|");

        for (int aCnt = 0; aCnt < a.length; aCnt++)
            a[aCnt] = a[aCnt].trim();

        if (!(a.length > 0))
            throw new RuntimeException("StaticEffectFactory : getAbility -- a[] too short in " + hostCard.getName());

        for (int i = 0; i < a.length; i++) {
            String aa[] = a[i].split("\\$");

            for (int aaCnt = 0; aaCnt < aa.length; aaCnt++)
                aa[aaCnt] = aa[aaCnt].trim();

            if (aa.length != 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("StaticEffectFactory Parsing Error: Split length of ");
                sb.append(a[i]).append(" in ").append(hostCard.getName()).append(" is not 2.");
                throw new RuntimeException(sb.toString());
            }

            mapParameters.put(aa[0], aa[1]);
        }

        return mapParameters;
    }
    
    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString() {
        if (mapParams.containsKey("Description")) {
            return mapParams.get("Description").replace("CARDNAME", hostCard.getName());
        } else return "";
    }
    
    //main constructor
    public StaticAbility(String params, Card host) {
        mapParams = getMapParams(params, host);
        hostCard = host;
    }
    
    public StaticAbility(HashMap<String, String> params, Card host) {
        mapParams = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            mapParams.put(entry.getKey(), entry.getValue());
        }
        hostCard = host;
    }
    
    //apply the ability if it has the right mode
    public void applyAbility(String mode) {
    	
        if (Constant.Runtime.DevMode[0])
            System.out.println("staticAbility " + mode + " host: " + hostCard.toString());
    	
    	//don't apply the ability if it hasn't got the right mode
    	if (!mapParams.get("Mode").equals(mode))
    		return;
    	
    	//TODO: Check requirements here
    	
    	if (mode.equals("Continuous"))
    		StaticAbility_Continuous.applyContinuousAbility(this);
    }

}//end class StaticEffectFactory
