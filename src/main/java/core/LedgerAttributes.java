package core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LedgerAttributes {

    public static final Map<String, Object> ATTRIBUTES = new ConcurrentHashMap<>();

    static{
        //core settings
        ATTRIBUTES.put("ENGINE_PROCESSING_CYCLE_RATE", 600);
        ATTRIBUTES.put("CHAIN_SAVING_CYCLE", "JSON");

        //chain settings
        ATTRIBUTES.put("MINIMUM_TRANSACTION_AMOUNT", 0.000005D);
    }

    public static double getDoubleValue(String key){return (double) ATTRIBUTES.getOrDefault(key, 0.000001D);}
    public static int getIntValue(String key){return (int) ATTRIBUTES.getOrDefault(key, -1);}

    public static String getStringValue(String key){return (String) ATTRIBUTES.getOrDefault(key, "");}
}
