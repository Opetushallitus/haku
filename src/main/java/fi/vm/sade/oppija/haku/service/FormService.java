package fi.vm.sade.oppija.haku.service;


import fi.vm.sade.oppija.haku.dao.ApplicationPeriodDAO;
import fi.vm.sade.oppija.haku.model.ModelLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FormService {

    final ModelLoader model;

    final ApplicationPeriodDAO applicationPeriodDAO;

    @Autowired
    public FormService(final ApplicationPeriodDAO applicationPeriodDAO, ModelLoader modelLoader) {
        this.applicationPeriodDAO = applicationPeriodDAO;
        model = modelLoader;
    }

    public Map<String, Object> getApplicationPeriod(final String applicationPeriodId) {
        return model.getApplicationPeriod(applicationPeriodId);
    }

    public Map<String, Object> findForm(final String applicationPeriodId, final String formId) {
        return model.getForm(applicationPeriodId, formId);
    }

    public Map<String, Object> findFirstCategory(final String applicationPeriodId, final String formId) {
        final List<Map<String, Object>> categories = findCategories(applicationPeriodId, formId);
        return categories.get(0);
    }

    public List<Map<String, Object>> findCategories(String applicationPeriodId, String formId) {
        final Map<String, Object> form = findForm(applicationPeriodId, formId);
        return (List<Map<String, Object>>) form.get("categories");
    }
}
