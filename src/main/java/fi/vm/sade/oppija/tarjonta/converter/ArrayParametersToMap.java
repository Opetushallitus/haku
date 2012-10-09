/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

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
