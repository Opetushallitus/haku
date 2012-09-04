package fi.vm.sade.oppija.haku.service;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormService {

    public List<Map<String, Object>> getForms(final String formId) {
        final ArrayList<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        return maps;
    }

    public Map<String, Object> getStudyApplicationProcess(final String applicationPeriodId, final String formId) {
        return new HashMap<String, Object>();
    }

}
