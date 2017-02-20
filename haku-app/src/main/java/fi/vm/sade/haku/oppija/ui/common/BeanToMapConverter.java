package fi.vm.sade.haku.oppija.ui.common;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonMap;

public class BeanToMapConverter {
    private static final Strategy[] STRATEGIES = {new StringToStringStrategy(), new StringToMapStrategy()};

    private BeanToMapConverter() {}
    public static Map<String, String> convert(Object o, Set<String> blacklist) {
        try {
            Map<String, ?> props = PropertyUtils.describe(o);
            Map<String, Object> filtered = new HashMap<>();
            for(Map.Entry<String,?> e : props.entrySet()) {
                if(e.getValue() != null && !blacklist.contains(e.getKey())) {
                    filtered.put(e.getKey(), e.getValue());
                }
            }
            return convertUsingStrategies(filtered);
        } catch(Exception e) {
            return emptyMap();
        }
    }
    public static Map<String, String> convert(Object o) {
        return convert(o, emptySet());
    }

    private static Map<String,String> convertUsingStrategies(Map<String,?> m) {
        Map<String, String> r = new HashMap<>();
        for(Map.Entry<String,?> e : m.entrySet()) {
            for(Strategy s : STRATEGIES) {
                Map<String, String> result = s.handle(e);
                if(!result.isEmpty()) {
                    r.putAll(result);
                }
            }
        }
        return r;
    }

    private interface Strategy {
        Map<String, String> handle(Map.Entry<String, ?> h);
    }
    private static class StringToStringStrategy implements Strategy {
        @Override
        public Map<String, String> handle(Map.Entry<String, ?> h) {
            if(h.getValue() != null && h.getValue() instanceof String && !"".equals(h.getValue())) {
                return singletonMap(h.getKey(), (String)h.getValue());
            }
            return emptyMap();
        }
    }
    private static class StringToMapStrategy implements Strategy {
        @Override
        public Map<String, String> handle(Map.Entry<String, ?> h) {
            if(h.getValue() != null && h.getValue() instanceof Map) {
                Map<String, ?> m = (Map<String, ?>)h.getValue();
                Map<String, String> r = convertUsingStrategies(m);
                Map<String,String> f = new HashMap<>();
                for(Map.Entry<String,String> e : r.entrySet()) {
                    f.put(new StringBuilder(h.getKey()).append(".").append(e.getKey()).toString(), e.getValue());
                }
                return f;
            }
            return emptyMap();
        }
    }
}
