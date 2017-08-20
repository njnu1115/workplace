package cn.cycletec.badland;

import java.util.HashMap;

/**
 * Created by xliuchn on 1/3/2017.
 */

public class DistoGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String UUID_DISTO_SERVICE =                                           "3ab10100-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_DISTANCE =                           "3ab10101-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_DISTANCE_DISPLAY_UNIT =              "3ab10102-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_INCLINATION =                        "3ab10103-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_INCLINATION_DISPLAY_UNIT=            "3ab10104-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_GEOGRAPHIC_DIRECTION=                "3ab10105-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_GEOGRAPHIC_DIRECTION_DISTPLAY_UNIT = "3ab10106-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_HORIZONTAL_INCLINE =                 "3ab10107-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_VERTICAL_INCLINE=                    "3ab10108-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_COMMAND =                            "3ab10109-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_CHARACTERISTIC_STATE_RESPONSE =                     "3ab1010A-f831-4395-b29d-570977d5bf94";
    public static String UUID_DISTO_DESCRIPTOR=                                         "00002902-0000-1000-8000-00805f9b34fb";
    public static String[] sDistoCommandTable = new String[] {"a","b","gi","iv","N00N","g", "o", "p","N02N", "cfm","cfb 0"};
    public static String S910Address = "CC:7E:4B:90:0F:5D";
    static {
        //Services.
        attributes.put(UUID_DISTO_SERVICE, "DISTO SERVICE");
        //Characteristics.
        attributes.put(UUID_DISTO_CHARACTERISTIC_DISTANCE, "DISTANCE");
        attributes.put(UUID_DISTO_CHARACTERISTIC_DISTANCE_DISPLAY_UNIT, "DISTANCE DISPLAY UNIT");
        attributes.put(UUID_DISTO_CHARACTERISTIC_INCLINATION, "INCLINATION");
        attributes.put(UUID_DISTO_CHARACTERISTIC_INCLINATION_DISPLAY_UNIT, "INCLINATION DISPLAY UNIT");
        attributes.put(UUID_DISTO_CHARACTERISTIC_GEOGRAPHIC_DIRECTION, "GEOGRAPHIC_DIRECTION");
        attributes.put(UUID_DISTO_CHARACTERISTIC_GEOGRAPHIC_DIRECTION_DISTPLAY_UNIT, "GEOGRAPHIC DIRECTION DISTPLAY UNIT");
        attributes.put(UUID_DISTO_CHARACTERISTIC_HORIZONTAL_INCLINE, "HORIZONTAL INCLINE");
        attributes.put(UUID_DISTO_CHARACTERISTIC_VERTICAL_INCLINE, "VERTICAL INCLINE");
        attributes.put(UUID_DISTO_CHARACTERISTIC_COMMAND, "COMMAND");
        attributes.put(UUID_DISTO_CHARACTERISTIC_STATE_RESPONSE, "STATE RESPONSE");
        attributes.put(UUID_DISTO_DESCRIPTOR, "DESCRIPTOR");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
