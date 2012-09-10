package fi.vm.sade.oppija.haku.model;

import fi.vm.sade.oppija.haku.dao.ApplicationPeriodDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author jukka
 * @version 9/6/122:45 PM}
 * @since 1.1
 */
@Component
public class ModelLoader {

    final ApplicationPeriodDAO dao;
    private final Map<String, Map<String, Object>> model;

    @Autowired
    public ModelLoader(ApplicationPeriodDAO dao) {
        this.dao = dao;
        model = dao.findAll();
    }

    public Map<String, Map<String, Object>> getModel() {
        return model;
    }

    public Map<String, Object> getApplicationPeriod(String id) {
        return model.get(id);
    }

    public Map<String, Object> getForm(String applicationPeriodId, String formId) {

        //TODO: chekkaa onko modelissa virhe!!!
        final Map<String, Object> form = (Map<String, Object>) getApplicationPeriod(applicationPeriodId).get("form");
        return (Map<String, Object>) form.get(formId);
    }
}

