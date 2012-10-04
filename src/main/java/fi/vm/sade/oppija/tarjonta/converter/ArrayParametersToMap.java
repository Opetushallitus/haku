package fi.vm.sade.oppija.tarjonta.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ville
 * Date: 10/4/12
 * Time: 9:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayParametersToMap implements Converter<String[], Map<String, String>> {
    @Override
    public Map<String, String> convert(String[] source) {
        HashMap<String, String> target = new HashMap<String, String>();
        if (source != null) {
            for (String value : source) {
                target.put(value, value);
            }
        }
        return target;
    }
}
