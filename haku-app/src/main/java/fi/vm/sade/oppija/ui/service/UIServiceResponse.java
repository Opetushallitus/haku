package fi.vm.sade.oppija.ui.service;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.hakemus.domain.Application;

import java.util.HashMap;
import java.util.Map;

public class UIServiceResponse {
    public static final String OID = "oid";
    public static final String HAKEMUS = "hakemus"; // TODO remove
    public static final String APPLICATION = "application";
    public static final String CATEGORY_DATA = "categoryData";
    public static final String APPLICATION_PHASE_ID = "applicationPhaseId";
    public static final String ERROR_MESSAGES = "errorMessages";
    private final Map<String, String> errors = new HashMap<String, String>();
    private Map<String, Object> model = new HashMap<String, Object>();

    public final Map<String, Object> getModel() {
        model.put(ERROR_MESSAGES, ImmutableMap.copyOf(errors));
        return ImmutableMap.copyOf(model);
    }

    public final void addObjectToModel(final String key, final Object object) {
        this.model.put(key, object);
    }

    public void setApplication(final Application application) {
        this.addObjectToModel(APPLICATION, application);
        this.addObjectToModel(HAKEMUS, application);
        this.addObjectToModel(OID, application.getOid());
        this.addObjectToModel(APPLICATION_PHASE_ID, application.getPhaseId());
        this.addObjectToModel(CATEGORY_DATA, application.getVastauksetMerged());
    }

    public void setErrorMessages(final Map<String, String> errors) {
        this.errors.putAll(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

}
