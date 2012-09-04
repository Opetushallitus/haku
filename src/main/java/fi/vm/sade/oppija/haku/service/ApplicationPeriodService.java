package fi.vm.sade.oppija.haku.service;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApplicationPeriodService {

    public List<Map<String, Object>> getApplicationPeriods() {
        final ArrayList<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        return maps;
    }

    public Map<String, Object> getApplicationPeriod(final String id) {
        final HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("name", "Ville");
        return data;
    }

}
