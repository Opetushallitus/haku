package fi.vm.sade.oppija.haku.service;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StudyApplicationProcessService {

    public List<Map<String, Object>> listStudyApplicationProcesses(final String studyApplicationId) {
        final ArrayList<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        return maps;
    }

    public Map<String, Object> getStudyApplicationProcess(final String studyApplicationId, final String studyApplicationProcessId) {
        return new HashMap<String, Object>();
    }

}
