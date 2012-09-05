package fi.vm.sade.oppija.haku.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormService {

    //final FormDAO formDAO;

    //@Autowired
    //public FormService(final FormDAO formDAO) {
        //this.formDAO = formDAO;
    //}

    public List<Map<String, Object>> getForms(final String applicationPeriodId) {
        final ArrayList<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        return maps;
    }

    public Map<String, Object> getForm(final String applicationPeriodId, final String formId) {
        //formDao
        return new HashMap<String, Object>();

    }

}
