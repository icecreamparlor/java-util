import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapUtil {
    public static Map<String, Object> convertToMapBlacklist(Object object, String... targets) {
        /*
            Object 를 Map 으로 변환시켜 주고,
            Parameter 에서 넘어온 값들을 제거한다.

         */
        Map<String, Object> map = convertToMap(object);
        return blacklist(map, targets);
    }

    public static Map<String, Object> convertToMapWhitelist(Object object, String... targets) {
        /*
            Object 를 Map 으로 변환시켜 주고,
            Parameter 에서 넘어온 String 들만 남긴다.
         */
        Map<String, Object> map = convertToMap(object);
        return whitelist(map, targets);
    }

    public static Map<String, Object> convertToMap(Object object) {
        /*
            Object 를 Map 으로 변환시켜준다.
         */
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
//        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
//        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, true);

        return objectMapper.convertValue(object, Map.class);
    }

    public static Map<String, Object> whitelist(Map<String, Object> map, String... targets) {
        /*
            Key 값들을 String 으로 받아서,
            map 에 해당하는 String 값들이 있으면, map 에서 제거 후에 return

         */
        Map<String, Object> result = new HashMap<>();
        for(String key : map.keySet()) {
            for(String target : targets) {
                if(key.equalsIgnoreCase(target)) {
                    result.put(key, map.get(key));
                }
            }
        }
        return result;
    }

    public static Map<String, Object> blacklist(Map<String, Object> map, String... targets) {
        Map<String, Object> result = new HashMap<>();
        for(String key : map.keySet()) {
            boolean match = false;
            for(String target : targets) {
                if(key.equalsIgnoreCase(target)) {
                    match = true;
                }
            }
            if(!match) {
                result.put(key, map.get(key));
            }

        }
        return result;
    }

    public static Map<String, Object> put(String k1, Object v1) {
         Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        return map;
    }
    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }
    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }
    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }
    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }
    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        return map;
    }
    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6, String k7, Object v7) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        return map;
    }
    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6, String k7, Object v7, String k8, Object v8) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        return map;
    }

    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6, String k7, Object v7, String k8, Object v8, String k9, Object v9) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        return map;
    }

    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6, String k7, Object v7, String k8, Object v8, String k9, Object v9, String k10, Object v10) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        return map;
    }

    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6, String k7, Object v7, String k8, Object v8, String k9, Object v9, String k10, Object v10, String k11, Object v11) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        return map;
    }

    public static Map<String, Object> put(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6, String k7, Object v7, String k8, Object v8, String k9, Object v9, String k10, Object v10, String k11, Object v11, String k12, Object v12) {
        Map<String, Object> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        map.put(k12, v12);
        return map;
    }

    public static Map<String, Object> put(Map<String, Object> map, String k1, Object v1) {
        if(map == null) {
            map = new HashMap<>();
        }
        map.put(k1, v1);
        return map;
    }
    public static Map<String, Object> put(Map<String, Object> map, String k1, Object v1, String k2, Object v2) {
        if(map == null) {
            map = new HashMap<>();
        }
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }
    public static Map<String, Object> put(Map<String, Object> map, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        if(map == null) {
            map = new HashMap<>();
        }
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }
    public static Map<String, Object> put(Map<String, Object> map, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        if(map == null) {
            map = new HashMap<>();
        }
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }
    public static Map<String, Object> put(Map<String, Object> map, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
        if(map == null) {
            map = new HashMap<>();
        }
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }
    public static Map<String, Object> put(Map<String, Object> map, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6) {
        if(map == null) {
            map = new HashMap<>();
        }
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        return map;
    }
    public static Map<String, Object> put(Map<String, Object> map, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6, String k7, Object v7) {
        if(map == null) {
            map = new HashMap<>();
        }
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        return map;
    }
    public static Map<String, Object> put(Map<String, Object> map, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5
            , String k6, Object v6, String k7, Object v7, String k8, Object v8) {
        if(map == null) {
            map = new HashMap<>();
        }
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        return map;
    }
}