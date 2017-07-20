/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.oppija.ui.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.util.AttachmentUtil;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.Session;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.MergedAnswers;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class UIServiceImpl implements UIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UIServiceImpl.class);


    @Value("${koulutusinformaatio.oppija.aosearch.ongoing:true}")
    private boolean aoSearchOnlyOngoing;

    private final ApplicationService applicationService;
    private final ApplicationSystemService applicationSystemService;
    private final Session userSession;
    private final AuthenticationService authenticationService;
    private final I18nBundleService i18nBundleService;
    private final PDFService pdfService;
    private final HakumaksuService hakumaksuService;
    private final boolean demoMode;

    @Autowired
    public UIServiceImpl(final ApplicationService applicationService,
                         final ApplicationSystemService applicationSystemService,
                         final Session userSession,
                         final AuthenticationService authenticationService,
                         final I18nBundleService i18nBundleService,
                         final PDFService pdfService,
                         final HakumaksuService hakumaksuService,
                         @Value("${mode.demo:false}") final boolean demoMode) {
        this.applicationService = applicationService;
        this.applicationSystemService = applicationSystemService;
        this.userSession = userSession;
        this.authenticationService = authenticationService;
        this.i18nBundleService = i18nBundleService;
        this.pdfService = pdfService;
        this.hakumaksuService = hakumaksuService;
        this.demoMode = demoMode;
    }

    @Override
    public ModelResponse getCompleteApplication(final String applicationSystemId, final String oid) {
        final ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Application application = applicationService.getSubmittedApplication(applicationSystemId, oid);

        ModelResponse response = new ModelResponse(application, activeApplicationSystem,
                AttachmentUtil.resolveAttachments(application));

        response.addObjectToModel("alatunnisterivit", new ArrayList<I18nText>(4) {{
            add(i18nBundleService.getBundle(activeApplicationSystem).get("lomake.tulostus.alatunniste.rivi1"));
            add(i18nBundleService.getBundle(activeApplicationSystem).get("lomake.tulostus.alatunniste.rivi2"));
            add(i18nBundleService.getBundle(activeApplicationSystem).get("lomake.tulostus.alatunniste.rivi3"));
            add(i18nBundleService.getBundle(activeApplicationSystem).get("lomake.tulostus.alatunniste.rivi4"));
        }});
        response.addObjectToModel("demoMode", this.demoMode);

        if (activeApplicationSystem.isMaksumuuriKaytossa()) {
            response.addObjectToModel("paymentRequired", hakumaksuService.isPaymentRequired(MergedAnswers.of(application)));
        }

        return response;
    }

    @Override
    public ModelResponse getAllApplicationSystems(String... includeFields) {
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.addObjectToModel(ModelResponse.APPLICATION_SYSTEMS,
                applicationSystemService.getAllApplicationSystems(includeFields));
        return modelResponse;
    }

    @Override
    public ModelResponse getPreview(String applicationSystemId) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Application application = applicationService.getApplication(applicationSystemId);
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.addAnswers(application.getVastauksetMerged());
        modelResponse.setElement(activeApplicationSystem.getForm());
        modelResponse.addObjectToModel("demoMode", this.demoMode);
        return modelResponse;
    }

    @Override
    public ModelResponse getPhase(String applicationSystemId, String phaseId, String lang) throws ExecutionException {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeApplicationSystem.getForm());
        Element phase = activeApplicationSystem.getForm().getChildById(phaseId);
        Application application = applicationService.getApplication(applicationSystemId);

        Map<String, String> answers = userSession.populateWithPrefillData(ensureApplicationOptionGroupData(phaseId, application.getVastauksetMerged(), lang));

        if (phaseId.equals(PHASE_APPLICATION_OPTIONS) && activeApplicationSystem.isMaksumuuriKaytossa()) {
            answers.putAll(paymentNotificationAnswers(answers));
        }

        elementTree.checkPhaseTransfer(application.getPhaseId(), phaseId);
        ModelResponse modelResponse = new ModelResponse(activeApplicationSystem);
        modelResponse.addAnswers(answers);
        modelResponse.setElement(phase);
        modelResponse.addObjectToModel("baseEducationDoesNotRestrictApplicationOptions", activeApplicationSystem.baseEducationDoesNotRestrictApplicationOptions());
        modelResponse.addObjectToModel("ongoing", aoSearchOnlyOngoing);
        modelResponse.addObjectToModel("demoMode", this.demoMode);
        return modelResponse;
    }

    private Map<String, String> ensureApplicationOptionGroupData(String phaseId, Map<String, String> answers, String lang) {
        //TODO this is an evil kludge, pls kill it asap
        if (!OppijaConstants.PHASE_APPLICATION_OPTIONS.equals(phaseId))
            return answers;
        return applicationService.ensureApplicationOptionGroupData(answers, lang);
    }

    @Override
    public void storePrefilledAnswers(String applicationSystemId, Map<String, String> answers, String lang) {
        userSession.addPrefillData(applicationSystemId, applicationService.ensureApplicationOptionGroupData(answers, lang));
    }

    @Override
    public Map<String, Object> getElementHelp(final String applicationSystemId, final String elementId, final Map<String, String> answers) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Application application = applicationService.getApplication(applicationSystemId);
        final Map<String, String> vastauksetMerged = application.getVastauksetMerged();
        final Map<String, String>  combinedAnswers = Maps.newHashMapWithExpectedSize(vastauksetMerged.size() + answers.size());
        combinedAnswers.putAll(vastauksetMerged);
        combinedAnswers.putAll(answers);
        Element root = activeApplicationSystem.getForm().getChildById(elementId);
        List<Element> listOfTitledElements = ElementUtil.filterElements(root, new Predicate<Element>() {
            @Override
            public boolean apply(Element input) {
                return (input instanceof Titled);
            }
        },
                combinedAnswers);
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("theme", root); //why theme?
        model.put("listsOfTitledElements", listOfTitledElements);
        return model;
    }

    @Override
    public Map<String, Object> getAdditionalLanguageRow(String applicationSystemId, String gradeGridId) {
        Form activeForm = applicationSystemService.getActiveApplicationSystem(applicationSystemId).getForm();
        Element element = activeForm.getChildById(gradeGridId);
        GradeGrid gradeGrid = (GradeGrid) element;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(ModelResponse.ELEMENT, gradeGrid);
        model.put(ModelResponse.TEMPLATE, "gradegrid/additionalLanguageRow");
        return model;
    }

    @Override
    public ModelResponse updateRules(String applicationSystemId, String phaseId, String elementId, Map<String, String> currentAnswers) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Form activeForm = activeApplicationSystem.getForm();
        Application application = applicationService.getApplication(applicationSystemId);
        Map<String, String> otherValues = application.getVastauksetMergedIgnoringPhase(phaseId);
        currentAnswers.putAll(otherValues);

        ModelResponse modelResponse = new ModelResponse();
        modelResponse.addAnswers(currentAnswers);
        modelResponse.setElement(activeForm.getChildById(elementId));
        modelResponse.setForm(activeForm);
        modelResponse.setApplicationSystemId(applicationSystemId);
        modelResponse.addObjectToModel("ongoing", aoSearchOnlyOngoing);
        modelResponse.addObjectToModel("baseEducationDoesNotRestrictApplicationOptions", activeApplicationSystem.baseEducationDoesNotRestrictApplicationOptions());
        modelResponse.addObjectToModel("demoMode", this.demoMode);
        return modelResponse;
    }

    public ImmutableMap<String, String> paymentNotificationAnswers(Map<String, String> answers) {
        ImmutableMap<ApplicationOptionOid, Boolean> payments = hakumaksuService.getPaymentRequirementsForApplicationOptions(answers);
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (String key : answers.keySet()) {
            if (key != null && key.startsWith(PREFERENCE_PREFIX) && key.endsWith(OPTION_ID_POSTFIX)
                    && isNotEmpty(answers.get(key))
                    && payments.get(ApplicationOptionOid.of(answers.get(key)))) {
                String paymentRequirementKey = key.replace(OPTION_ID_POSTFIX, PAYMENT_NOTIFICATION_POSTFIX);
                builder.put(paymentRequirementKey, "true");
            }
        }

        return builder.build();
    }

    @Override
    public ModelResponse updateRulesMulti(String applicationSystemId, String phaseId, List<String> ruleIds, Map<String, String> currentAnswers) throws ExecutionException {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        final Form activeForm = activeApplicationSystem.getForm();
        Application application = applicationService.getApplication(applicationSystemId);
        Map<String, String> otherAnswers = application.getVastauksetMergedIgnoringPhase(phaseId);
        currentAnswers.putAll(otherAnswers);

        List<Element> ruleElements = Lists.transform(ruleIds, new Function<String, Element>() {
            @Override
            public Element apply(String input) {
                return activeForm.getChildById(input);
            }
        });

        if (phaseId.equals(PHASE_APPLICATION_OPTIONS) && activeApplicationSystem.isMaksumuuriKaytossa()) {
            currentAnswers.putAll(paymentNotificationAnswers(currentAnswers));
        }

        ModelResponse modelResponse = new ModelResponse();
        modelResponse.addAnswers(currentAnswers);
        modelResponse.addObjectToModel("elements", ruleElements);
        modelResponse.setForm(activeForm);
        modelResponse.setApplicationSystemId(applicationSystemId);
        modelResponse.addObjectToModel("ongoing", aoSearchOnlyOngoing);
        modelResponse.addObjectToModel("baseEducationDoesNotRestrictApplicationOptions", activeApplicationSystem.baseEducationDoesNotRestrictApplicationOptions());
        modelResponse.addObjectToModel("demoMode", this.demoMode);
        return modelResponse;
    }

    @Override
    public ModelResponse getPhaseElement(String applicationSystemId, String phaseId, String elementId) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Form activeForm = activeApplicationSystem.getForm();
        Application application = applicationService.getApplication(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeForm);
        elementTree.checkPhaseTransfer(application.getPhaseId(), phaseId);
        ModelResponse modelResponse = new ModelResponse(application, activeForm, activeForm.getChildById(elementId));
        modelResponse.addAnswers(userSession.populateWithPrefillData(application.getVastauksetMerged()));
        modelResponse.setApplicationSystemId(applicationSystemId);
        modelResponse.addObjectToModel("ongoing", aoSearchOnlyOngoing);
        modelResponse.addObjectToModel("baseEducationDoesNotRestrictApplicationOptions", activeApplicationSystem.baseEducationDoesNotRestrictApplicationOptions());
        modelResponse.addObjectToModel("demoMode", this.demoMode);

        return modelResponse;
    }

    @Override
    public ModelResponse savePhase(String applicationSystemId, String phaseId, Map<String, String> originalAnswers, String lang) throws ExecutionException {
        Map<String, String> ensuredAnswers = ensureApplicationOptionGroupData(phaseId, originalAnswers, lang);
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);

        if (phaseId.equals(PHASE_APPLICATION_OPTIONS) && activeApplicationSystem.isMaksumuuriKaytossa()) {
            ensuredAnswers.putAll(paymentNotificationAnswers(ensuredAnswers));
        }

        Form activeForm = activeApplicationSystem.getForm();
        ApplicationState applicationState = applicationService.saveApplicationPhase(
                new ApplicationPhase(applicationSystemId, phaseId, ensuredAnswers));

        ModelResponse modelResponse = new ModelResponse();
        modelResponse.addObjectToModel("ongoing", aoSearchOnlyOngoing);
        modelResponse.addObjectToModel("baseEducationDoesNotRestrictApplicationOptions", activeApplicationSystem.baseEducationDoesNotRestrictApplicationOptions());
        modelResponse.addObjectToModel("demoMode", this.demoMode);
        modelResponse.setApplicationState(applicationState);
        if (!applicationState.isValid()) {
            modelResponse.setApplicationState(applicationState);
            modelResponse.setApplicationSystemId(applicationSystemId);
            modelResponse.setElement(activeForm.getChildById(phaseId));
            modelResponse.setForm(activeForm);
        }
        return modelResponse;

    }

    @Override
    public ModelResponse submitApplication(final String applicationSystemId, String language) {
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.setApplication(applicationService.submitApplication(applicationSystemId, language));
        return modelResponse;
    }

    @Override
    public ModelResponse getApplication(String applicationSystemId) {
        Application application = userSession.getApplication(applicationSystemId);
        if (application.isNew()) {
            Form activeForm = applicationSystemService.getActiveApplicationSystem(applicationSystemId).getForm();
            application.setPhaseId(ElementTree.getFirstChild(activeForm).getId());
        }
        return new ModelResponse(application);
    }

    @Override
    public HttpResponse getUriToPDF(String applicationSystemId, String oid) {
        Application application = applicationService.getSubmittedApplication(applicationSystemId, oid);
        if (application != null
                && application.getApplicationSystemId().equals(applicationSystemId)
                && application.getOid().equals(oid)) {
            return pdfService.getUriToPDF(oid);
        }
        throw new ResourceNotFoundException("Not allowed");
    }

    @Override
    public String ensureLanguage(HttpServletRequest request, String applicationSystemId) {
        if (request == null) {
            return null;
        }
        String lang = request.getAttribute("fi_vm_sade_oppija_language").toString();

        ApplicationSystem as = applicationSystemService.getApplicationSystem(applicationSystemId);
        List<String> allowedLanguages = as.getAllowedLanguages();
        if (!allowedLanguages.contains(lang)) {
            lang = allowedLanguages.get(0);
            HttpSession session = request.getSession();
            Locale newLocale = new Locale(lang);
            Config.set(session, Config.FMT_LOCALE, newLocale);
            request.setAttribute("fi_vm_sade_oppija_language", lang);
        }
        return lang;
    }

    @Override
    public Form getApplicationSystemForm(String applicationSystemId) {
        ApplicationSystem as = applicationSystemService.getApplicationSystem(applicationSystemId, "form");
        return as.getForm();
    }

    private Cookie getLangCookie(HttpServletRequest request) {
        String langCookie = authenticationService.getLangCookieName();
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(langCookie)) {
                return cookie;
            }
        }
        return null;
    }
}
