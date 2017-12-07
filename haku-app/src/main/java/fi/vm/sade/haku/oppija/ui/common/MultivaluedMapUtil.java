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

package fi.vm.sade.haku.oppija.ui.common;

import com.google.common.collect.Sets;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

public final class MultivaluedMapUtil {
    private MultivaluedMapUtil() { // NOSONAR
    }// TODO: implement param reader for Map

    public static Map<String, String> toSingleValueMap(MultivaluedMap<String, String> multi) {
        HashMap<String, String> singleValueMap = new HashMap<String, String>(multi.size());
        for (Map.Entry<String, List<String>> entry : multi.entrySet()) {
            singleValueMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return singleValueMap;
    }

    public static MultivaluedMap<String,String> removeKeys(MultivaluedMap<String,String> source, String... exclude) {
        HashSet<String> excludeStrings = Sets.newHashSet(exclude);
        MultivaluedHashMap<String, String> dest = new MultivaluedHashMap<>();
        for(String key : source.keySet()) {
            if(!excludeStrings.contains(key)) {
                dest.put(key, source.get(key));
            }
        }
        return dest;
    }

    public static MultivaluedMap<String,String> filterOPHParameters(MultivaluedMap<String,String> post) {
        return removeKeys(post, "CSRF", "clientSubSystemCode");
    }
}
