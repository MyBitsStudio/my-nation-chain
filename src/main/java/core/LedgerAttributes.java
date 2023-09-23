package core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LedgerAttributes {

    public static final Map<String, Object> ATTRIBUTES = new ConcurrentHashMap<>();

    static{
        ATTRIBUTES.put("MINIMUM_TRANSACTION_AMOUNT", 0.000005D);
    }

    public static double getDoubleValue(String key){return (double) ATTRIBUTES.getOrDefault(key, 0.000001D);}
}
