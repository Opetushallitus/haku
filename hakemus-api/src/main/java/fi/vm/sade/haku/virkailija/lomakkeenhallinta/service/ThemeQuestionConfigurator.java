package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationOptionAttachmentRequest;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.AttachmentRequest;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil.ensureDefaultLanguageTranslations;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil.filterCodePrefix;

public final class ThemeQuestionConfigurator {

    public static enum ConfiguratorFilter {
        ALL_QUESTIONS,
        ONLY_GROUP_QUESTIONS,
        NO_GROUP_QUESTIONS
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionConfigurator.class);
    private static final String PREFERENCE_PREFIX = "preference";
    private static final String OPTION_POSTFIX = "-Koulutus-id";
    private static final String GROUP_POSTFIX = "-Koulutus-id-ao-groups";
    private final ThemeQuestionDAO themeQuestionDAO;
    private final HakukohdeService hakukohdeService;
    private final FormParameters formParameters;
    private final OrganizationService organizationService;

    public ThemeQuestionConfigurator(final ThemeQuestionDAO themeQuestionDAO, final HakukohdeService hakukohdeService,
      final FormParameters formParameters, OrganizationService organizationService) {
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.formParameters = formParameters;
        this.organizationService = organizationService;
    }

    public Element[] findAndConfigure(final String theme) {
        return _findAndConfigure(theme, true, null, ConfiguratorFilter.ALL_QUESTIONS);
    }

    public Element[] findAndConfigure(final String theme, final ConfiguratorFilter filter) {
        return _findAndConfigure(theme, true, null, filter);
    }

    public Element[] findAndConfigure(final String theme, final String preferenceElementId) {
        return _findAndConfigure(theme, false, preferenceElementId, ConfiguratorFilter.ALL_QUESTIONS);
    }

    public Element[] findAndConfigure(final String theme, final String preferenceElementId, final ConfiguratorFilter filter) {
        return _findAndConfigure(theme, false, preferenceElementId, filter);
    }


    private Element[] _findAndConfigure(final String theme,
                                        final Boolean generateTitledGroup,
                                        final String preferenceElementId,
                                        final ConfiguratorFilter filter) {
        LOGGER.debug("Configuring themequestions for application system: "+ formParameters.getApplicationSystem().getId() +" theme:"+ theme +" generating titled groups "+ generateTitledGroup);

        final ThemeQuestionQueryParameters baseQuery = new ThemeQuestionQueryParameters();
        baseQuery.setApplicationSystemId(formParameters.getApplicationSystem().getId());
        baseQuery.setTheme(theme);

        final List<Element> configuredApplicationOptions = new ArrayList<Element>();
        if (ConfiguratorFilter.ALL_QUESTIONS.equals(filter) || ConfiguratorFilter.ONLY_GROUP_QUESTIONS.equals(filter)) {
         configuredApplicationOptions.addAll(configureOptions(baseQuery, generateTitledGroup, preferenceElementId, true));
        }
        if (ConfiguratorFilter.ALL_QUESTIONS.equals(filter) || ConfiguratorFilter.NO_GROUP_QUESTIONS.equals(filter)) {
            configuredApplicationOptions.addAll(configureOptions(baseQuery, generateTitledGroup, preferenceElementId, false));
        }

        LOGGER.debug("Configuration complete for application system " + formParameters.getApplicationSystem().getId() + " theme " + theme);
        return configuredApplicationOptions.toArray(new Element[configuredApplicationOptions.size()]);
    }

    private List<Element> configureOptions(final ThemeQuestionQueryParameters baseQuery,
                                           final Boolean generateTitledGroup,
                                           final String preferenceElementId,
                                           final Boolean groupOption) {
        final ThemeQuestionQueryParameters queryParameters = baseQuery.clone();
        queryParameters.setQueryGroups(groupOption);

        final List<String> optionIds = themeQuestionDAO.queryApplicationOptionsIn(queryParameters);
        LOGGER.debug("Got " + optionIds.size() + " application " + (groupOption ? "groups" : "options") + " with base query: "+ baseQuery.toString());

        final ArrayList<Element> configuredOptions = new ArrayList<Element>(optionIds.size());
        for (String optionId : optionIds) {
            try {
                configuredOptions.add(configureThemeQuestionForOption(baseQuery, optionId, generateTitledGroup, preferenceElementId, groupOption));
            } catch (Exception exception) {
                LOGGER.error("Failed to configure application " + (groupOption ? "group" : "option") + optionId + " with base query: "+ baseQuery.toString(), exception);
            }
        }
        return configuredOptions;
    }


    private Element configureThemeQuestionForOption(final ThemeQuestionQueryParameters baseQuery,
                                                    final String optionId,
                                                    final Boolean titleApplicationOptions,
                                                    final String preferenceElementId,
                                                    final Boolean groupOption) {
        LOGGER.debug("Configuring application option "+ optionId +" with base query: "+ baseQuery.toString());
        final Element baseElement = generateApplicationOptionRule(optionId, preferenceElementId, groupOption);
        Element groupElement = baseElement;
        if (titleApplicationOptions) {
            if (groupOption) {
                groupElement = generateTitleGroupForApplicationOptionGroup(optionId);
            } else {
                groupElement = generateTitleGroupForApplicationOption(optionId);
            }
            baseElement.addChild(groupElement);
        }
        final List<Element> configuredQuestions = configureQuestions(baseQuery, optionId);
        groupElement.addChild(configuredQuestions.toArray(new Element[configuredQuestions.size()]));
        LOGGER.debug("Configuration of application option "+ optionId +" complete with base query: "+ baseQuery.toString());
        return baseElement;
    }

    private List<Element> configureQuestions(final ThemeQuestionQueryParameters baseQuery, final String optionId) {
        final List<ThemeQuestion> themeQuestions = queryQuestions(baseQuery, optionId);
        LOGGER.debug("Configuring a list of "+ themeQuestions.size() +" themequestions");
        final ArrayList<Element> configuredElements = new ArrayList<Element>(themeQuestions.size());
        for (ThemeQuestion tq : themeQuestions) {
            configuredElements.add(tq.generateElement(formParameters));
            LOGGER.debug("configured question {} of type {}", tq.getId(), tq.getClass().getSimpleName());
        }
        LOGGER.debug("Configuration of the list complete");
        return configuredElements;
    }

    private List<ThemeQuestion> queryQuestions(final ThemeQuestionQueryParameters baseQuery, final String optionId) {
        ThemeQuestionQueryParameters query = baseQuery.clone();
        query.setLearningOpportunityId(optionId);
        query.addSortBy(ThemeQuestion.FIELD_ORDINAL, ThemeQuestionQueryParameters.SORT_ASCENDING);
        LOGGER.debug("Querying questions with " + query);
        return moveNullOrdinalsToTheEnd(themeQuestionDAO.query(query));
    }

    private List<ThemeQuestion> moveNullOrdinalsToTheEnd(List<ThemeQuestion> themeQuestions){
        if (0 == themeQuestions.size() || null != themeQuestions.get(0).getOrdinal()){
            return themeQuestions;
        }
        final ArrayList<ThemeQuestion> newOrder = new ArrayList<ThemeQuestion>(themeQuestions.size());
        int i = 0;
        do {
            ThemeQuestion tq = themeQuestions.remove(i);
            newOrder.add(tq);
        } while (null  == themeQuestions.get(++i).getOrdinal());
        newOrder.addAll(0, themeQuestions);
        return newOrder;
    }

    private Element generateTitleGroupForApplicationOption(final String optionId) {
        LOGGER.debug("Generating Titled group for application option {}", optionId);
        final HakukohdeDTO hakukohde = hakukohdeService.findByOid(optionId);
        final Map<String, String> applicationOptionName = ensureDefaultLanguageTranslations(filterCodePrefix(hakukohde.getHakukohdeNimi()));
        final Map<String, String> providerName = ensureDefaultLanguageTranslations(filterCodePrefix(hakukohde.getTarjoajaNimi()));
        final Element group = TitledGroupBuilder.TitledGroup(ElementUtil.randomId())
          .i18nText(new I18nText(providerName))
          .help(new I18nText(applicationOptionName)).build();
        return group;
    }

    private Element generateTitleGroupForApplicationOptionGroup(final String groupId) {
        LOGGER.debug("Generating Titled group for application option group {}", groupId);
        I18nText groupName;
        try {
            final Organization applicationOptionGroup = organizationService.findByOid(groupId);
            groupName = ensureDefaultLanguageTranslations(applicationOptionGroup.getName());
        } catch (IOException ioException){
            LOGGER.error("Failed to get organization for id {}", groupId);
            groupName = new I18nText(new HashMap<String,String>());
        }

        final Element group = TitledGroupBuilder.TitledGroup(ElementUtil.randomId())
          .i18nText(groupName)
          .build();
        return group;
    }

    private Element generateApplicationOptionRule(final String applicationOptionId, final String preferenceElementId, final Boolean groupOption) {
        LOGGER.debug("Generating the ApplicationOptionRule group");
        Expr ruleExpr = null;
        if (groupOption){
            ruleExpr = generateOptionGroupExpr(applicationOptionId, preferenceElementId);
        } else {
            ruleExpr = generateApplicationOptionExpr(applicationOptionId, preferenceElementId);
        }
        final Element rule = Rule(ruleExpr).build();
        return rule;
    }


    private Expr generateApplicationOptionExpr(final String applicationOptionId, final String preferenceElementId) {
        final List<String> preferenceAoKeys = new ArrayList<String>();
        if (null != preferenceElementId) {
            preferenceAoKeys.add(preferenceElementId + OPTION_POSTFIX);
        } else {
            for (int i = 1; i <= formParameters.getApplicationSystem().getMaxApplicationOptions(); i++) {
                preferenceAoKeys.add(PREFERENCE_PREFIX + i + OPTION_POSTFIX);
            }
        }
        return ExprUtil.atLeastOneVariableEqualsToValue(applicationOptionId,
          preferenceAoKeys.toArray(new String[preferenceAoKeys.size()]));
    }

    private Expr generateOptionGroupExpr(final String groupId, final String preferenceElementId) {
        final List<String> preferenceAoKeys = new ArrayList<String>();
        if (null != preferenceElementId) {
            preferenceAoKeys.add(preferenceElementId + GROUP_POSTFIX);
        } else {
            for (int i = 1; i <= formParameters.getApplicationSystem().getMaxApplicationOptions(); i++) {
                preferenceAoKeys.add(PREFERENCE_PREFIX + i + GROUP_POSTFIX);
            }
        }
        return ExprUtil.atLeastOneVariableContainsValue(groupId,
          preferenceAoKeys.toArray(new String[preferenceAoKeys.size()]));
    }

    // COPY-AND-PASTE HELL
    // PLS DO FIX LATER

    public List<ApplicationOptionAttachmentRequest> findAndConfigureAttachmentRequests(){
        LOGGER.debug("Configuring themequestion attachment requests for application system: "+ formParameters.getApplicationSystem().getId());

        final ThemeQuestionQueryParameters baseQuery = new ThemeQuestionQueryParameters();
        baseQuery.setApplicationSystemId(formParameters.getApplicationSystem().getId());
        baseQuery.setOnlyWithAttachmentRequests(true);

        //retaining order
        final List<ApplicationOptionAttachmentRequest> configuredAttachementRequests = configureAttachementRequests(baseQuery, true);
        configuredAttachementRequests.addAll(configureAttachementRequests(baseQuery, false));

        LOGGER.debug("Configuration of themequestion attachment requests complete for application system " + formParameters.getApplicationSystem().getId());
        return configuredAttachementRequests;
    }

    private List<ApplicationOptionAttachmentRequest> configureAttachementRequests(final ThemeQuestionQueryParameters baseQuery, final Boolean groupOption) {
        final ThemeQuestionQueryParameters queryParameters = baseQuery.clone();
        queryParameters.setQueryGroups(groupOption);

        final List<String> optionIds = themeQuestionDAO.queryApplicationOptionsIn(queryParameters);
        LOGGER.debug("Got " + optionIds.size() + " application " + (groupOption ? "groups" : "options") + " with base query: "+ baseQuery.toString());

        final ArrayList<ApplicationOptionAttachmentRequest> configuredAttachmentRequests = new ArrayList<ApplicationOptionAttachmentRequest>(optionIds.size());
        for (String optionId : optionIds) {
            try {
                configuredAttachmentRequests.addAll(configureAttactmentRequestsForOption(baseQuery, optionId));
            } catch (Exception exception) {
                LOGGER.error("Failed to configure application " + (groupOption ? "group" : "option") + optionId + " with base query: "+ baseQuery.toString(), exception);
            }
        }
        return configuredAttachmentRequests;
    }

    private List<ApplicationOptionAttachmentRequest> configureAttactmentRequestsForOption(final ThemeQuestionQueryParameters baseQuery, final String optionId) {
        final List<ThemeQuestion> themeQuestions = queryQuestions(baseQuery, optionId);
        LOGGER.debug("Configuring a list of "+ themeQuestions.size() +" themequestions");
        final ArrayList<ApplicationOptionAttachmentRequest> configuredAttachmentRequests = new ArrayList<ApplicationOptionAttachmentRequest>(themeQuestions.size());
        for (ThemeQuestion tq : themeQuestions) {
            configuredAttachmentRequests.addAll(tq.generateAttactmentRequests(formParameters));
            LOGGER.debug("configured question {} of type {}", tq.getId(), tq.getClass().getSimpleName());
        }
        LOGGER.debug("Configuration of the list complete");
        return configuredAttachmentRequests;
    }
}
