package fi.vm.sade.haku.oppija.ui.service;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelResponse {
    public static final String OID = "oid";
    public static final String APPLICATION_SYSTEM_ID = "applicationSystemId";
    public static final String APPLICATION = "application";
    public static final String ANSWERS = "answers";
    public static final String APPLICATION_PHASE_ID = "applicationPhaseId";
    public static final String ERROR_MESSAGES = "errorMessages";
    public static final String KOULUTUSINFORMAATIO_BASE_URL = "koulutusinformaatioBaseUrl";
    public static final String ELEMENT = "element";
    public static final String TEMPLATE = "template";
    public static final String FORM = "form";
    public static final String DISCRETIONARY_ATTACHMENT_AO_IDS = "discretionaryAttachmentAOIds";
    public static final String APPLICATION_COMPLETE_ELEMENTS = "applicationCompleteElements";
    public static final String APPLICATION_SYSTEMS = "applicationSystems";


    private final Map<String, I18nText> errors = new HashMap<String, I18nText>();
    private Map<String, Object> model = new HashMap<String, Object>();

    public ModelResponse() {
    }

    public ModelResponse(final Application application) {
        setApplication(application);

    }

    public ModelResponse(final Application application, final Form form) {
        this(application);
        setForm(form);
    }

    public ModelResponse(final Application application, final Form form, final Element element) {
        this(application, form);
        setElement(element);
    }

    public ModelResponse(final Application application,
                         final ApplicationSystem applicationSystem,
                         final List<String> discretionaryAttachmentAOIds,
                         final String koulutusinformaatioBaseUrl) {
        this(application, applicationSystem.getForm());
        setApplicationCompleteElements(applicationSystem.getApplicationCompleteElements());
        setDiscretionaryAttachmentAOIds(discretionaryAttachmentAOIds);
        setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);
    }

    public ModelResponse(final Application application,
                         final Form form,
                         final Element element,
                         final ValidationResult validationResult,
                         final String koulutusinformaatioBaseUrl) {
        this(application, form, element);
        setErrorMessages(validationResult.getErrorMessages());
        setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);

    }

    public ModelResponse(final ApplicationSystem applicationSystem) {
        setForm(applicationSystem.getForm());
        setApplicationSystemId(applicationSystem.getId());
    }

    public final Map<String, Object> getModel() {
        model.put(ERROR_MESSAGES, ImmutableMap.copyOf(errors));
        return ImmutableMap.copyOf(model);
    }

    public final void addObjectToModel(final String key, final Object value) {
        if (value != null) {
            this.model.put(key, value);
        }
    }

    public void setApplication(final Application application) {
        this.addObjectToModel(APPLICATION, application);
        this.addObjectToModel(APPLICATION_SYSTEM_ID, application.getApplicationSystemId());
        setOid(application.getOid());
        this.addObjectToModel(APPLICATION_PHASE_ID, application.getPhaseId());
        this.addObjectToModel(ANSWERS, application.getVastauksetMerged());
    }

    public void setErrorMessages(final Map<String, I18nText> errors) {
        this.errors.putAll(errors);
    }

    public Map<String, I18nText> getErrorMessages() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addAnswers(final Map<String, String> answers) {
        Map<String, String> tmp = (Map<String, String>) this.model.get(ANSWERS);
        if (tmp == null) {
            addObjectToModel(ANSWERS, answers);
        } else {
            tmp.putAll(answers);
        }
    }

    public void setKoulutusinformaatioBaseUrl(final String url) {
        this.addObjectToModel(KOULUTUSINFORMAATIO_BASE_URL, url);
    }

    public void setForm(final Form form) {
        this.addObjectToModel(FORM, form);
    }

    public void setElement(final Element element) {
        this.addObjectToModel(ELEMENT, element);
        this.addObjectToModel(TEMPLATE, element.getType());
    }

    public void setDiscretionaryAttachmentAOIds(final List<String> discretionaryAttachmentAOIds) {
        this.addObjectToModel(DISCRETIONARY_ATTACHMENT_AO_IDS, discretionaryAttachmentAOIds);
    }

    public void setApplicationCompleteElements(final List<Element> applicationCompleteElements) {
        this.addObjectToModel(APPLICATION_COMPLETE_ELEMENTS, applicationCompleteElements);
    }

    public void setApplicationSystemId(final String asid) {
        this.addObjectToModel(APPLICATION_SYSTEM_ID, asid);
    }

    public void setApplicationState(final ApplicationState applicationState) {
        this.setErrorMessages(applicationState.getErrors());
        for (Map.Entry<String, Object> entry : applicationState.getModelObjects().entrySet()) {
            this.addObjectToModel(entry.getKey(), entry.getValue());
        }
        this.addObjectToModel(APPLICATION_PHASE_ID, getPhaseId());
    }

    public String getOid() {
        return model.get(OID).toString();
    }

    public void setOid(final String oid) {
        this.addObjectToModel(OID, oid);
    }

    public String getPhaseId() {
        return model.get(APPLICATION_PHASE_ID).toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModelResponse{");
        sb.append("errors=").append(errors);
        sb.append(", model=").append(model);
        sb.append('}');
        return sb.toString();
    }

    public Application getApplication() {
        return (Application) model.get(APPLICATION);
    }
}
