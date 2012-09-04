package fi.vm.sade.oppija.haku.service;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CategoryService {

    public List<Map<String, Object>> getCategories(final String applicationPeriodId, String formId) {
        final ArrayList<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        return maps;
    }

    public Map<String, Object> getCategory(final String applicationPeriodId, final String formId, final String categoryId) {
        return new HashMap<String, Object>();
    }

}
