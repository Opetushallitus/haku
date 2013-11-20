package fi.vm.sade.haku.oppija.ui.service;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIServiceResponse {
    public static final String OID = "oid";
    public static final String APPLICATION_SYSTEM_ID = "applicationSystemId";
    public static final String APPLICATION = "application";
    public static final String CATEGORY_DATA = "categoryData";
    public static final String APPLICATION_PHASE_ID = "applicationPhaseId";
    public static final String ERROR_MESSAGES = "errorMessages";
    public static final String KOULUTUSINFORMAATIO_BASE_URL = "koulutusinformaatioBaseUrl";
    public static final String ELEMENT = "element";
    public static final String TEMPLATE = "template";
    public static final String FORM = "form";
    public static final String DISCRETIONARY_ATTACHMENT_AO_IDS = "discretionaryAttachmentAOIds";
    public static final String APPLICATION_COMPLETE_ELEMENTS = "applicationCompleteElements";


    private final Map<String, I18nText> errors = new HashMap<String, I18nText>();
    private Map<String, Object> model = new HashMap<String, Object>();

    public UIServiceResponse() {
    }

    public UIServiceResponse(final Application application) {
        setApplication(application);
    }

    public UIServiceResponse(final Application application, final Form form) {
        this(application);
        setForm(form);
    }

    public UIServiceResponse(final Application application, final Form form, final Element element) {
        this(application, form);
        setElement(element);
    }

    public UIServiceResponse(final Application application,
                             final Form form,
                             final List<String> discretionaryAttachmentAOIds,
                             final String koulutusinformaatioBaseUrl) {
        this(application, form);
        setDiscretionaryAttachmentAOIds(discretionaryAttachmentAOIds);
        setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);
    }

    public UIServiceResponse(final Application application,
                             final Form form,
                             final Element element,
                             final ValidationResult validationResult,
                             final String koulutusinformaatioBaseUrl) {
        this(application, form, element);
        setErrorMessages(validationResult.getErrorMessages());
        setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);

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
        this.addObjectToModel(OID, application.getOid());
        this.addObjectToModel(APPLICATION_PHASE_ID, application.getPhaseId());
        this.addObjectToModel(CATEGORY_DATA, application.getVastauksetMerged());
    }

    public void setErrorMessages(final Map<String, I18nText> errors) {
        this.errors.putAll(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addAnswers(final Map<String, String> answers) {
        ((Map<String, String>) this.model.get(CATEGORY_DATA)).putAll(answers);
    }

    public void setKoulutusinformaatioBaseUrl(final String url) {
        this.addObjectToModel(KOULUTUSINFORMAATIO_BASE_URL, url);
    }

    public void setForm(final Form form) {
        Validate.notNull(form, "Form was null");
        this.addObjectToModel(FORM, form);
    }

    public void setElement(final Element element) {
        Validate.notNull(element, "Element was null");
        this.addObjectToModel(ELEMENT, element);
        this.addObjectToModel(TEMPLATE, element.getType());
    }

    public void setDiscretionaryAttachmentAOIds(final List<String> discretionaryAttachmentAOIds) {
        this.addObjectToModel(DISCRETIONARY_ATTACHMENT_AO_IDS, discretionaryAttachmentAOIds);
    }

    public void setApplicationCompleteElements(final List<Element> applicationCompleteElements) {
        this.addObjectToModel(APPLICATION_COMPLETE_ELEMENTS, applicationCompleteElements);
    }
}
