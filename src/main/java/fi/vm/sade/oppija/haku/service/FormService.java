package fi.vm.sade.oppija.haku.service;


import fi.vm.sade.oppija.haku.dao.ApplicationPeriodDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FormService {


    final ApplicationPeriodDAO applicationPeriodDAO;

    @Autowired
    public FormService(final ApplicationPeriodDAO applicationPeriodDAO) {
        this.applicationPeriodDAO = applicationPeriodDAO;
    }

    public Map<String, Object> getApplicationPeriod(final String applicationPeriodId) {
        return applicationPeriodDAO.find(applicationPeriodId);
    }

    public Map<String, Object> findForm(final String applicationPeriodId, final String formId) {
        return applicationPeriodDAO.findForm(applicationPeriodId, formId);
    }

    public Map<String, Object> findFirstCategory(final String applicationPeriodId, final String formId) {
        final Map<String, Object> form = findForm(applicationPeriodId, formId);
        final List<Map<String, Object>> categories = (List<Map<String, Object>>) form.get("categories");

        return categories.get(0);
    }
}
