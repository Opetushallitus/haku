package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil.ensureDefaultLanguageTranslations;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil.filterCodePrefix;

public final class ThemeQuestionConfigurator {

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
        return _findAndConfigure(theme, true, null, false);
    }

    public Element[] findAndConfigure(final String theme, final boolean groupOnly) {
        return _findAndConfigure(theme, true, null, groupOnly);
    }

    public Element[] findAndConfigure(final String theme, final String preferenceElementId) {
        return _findAndConfigure(theme, false, preferenceElementId, false);
    }

    private Element[] _findAndConfigure(final String theme,
                                        final Boolean generateTitledGroup,
                                        final String preferenceElementId,
                                        final boolean groupOnly) {
        LOGGER.debug("Configuring themequestions for application system: "+ formParameters.getApplicationSystem().getId() +" theme:"+ theme +" generating titled groups "+ generateTitledGroup);

        final List<Element> configuredApplicationOptions = configureOptions(theme, generateTitledGroup, preferenceElementId, true);
        if (! groupOnly) {
            configuredApplicationOptions.addAll(configureOptions(theme, generateTitledGroup, preferenceElementId, false));
        }

        LOGGER.debug("Configuration complete for application system " + formParameters.getApplicationSystem().getId() + " theme " + theme);
        return configuredApplicationOptions.toArray(new Element[configuredApplicationOptions.size()]);
    }

    private List<Element> configureOptions(final String theme,
                                           final Boolean generateTitledGroup,
                                           final String preferenceElementId,
                                           final Boolean groupOption) {
        final ThemeQuestionQueryParameters queryParameters = new ThemeQuestionQueryParameters();
        queryParameters.setApplicationSystemId(formParameters.getApplicationSystem().getId());
        queryParameters.setTheme(theme);
        queryParameters.setQueryGroups(groupOption);

        final List<String> optionIds = themeQuestionDAO.queryApplicationOptionsIn(queryParameters);
        LOGGER.debug("Got " + optionIds.size() + " application " + (groupOption ? "groups" : "options") + "for application system "+ formParameters.getApplicationSystem().getId() +" theme"+ theme);

        final ArrayList<Element> configuredOptions = new ArrayList<Element>(optionIds.size());
        for (String optionId : optionIds) {
            try {
                configuredOptions.add(configureThemeQuestionForOption(theme, optionId, generateTitledGroup, preferenceElementId, groupOption));
            } catch (Exception exception) {
                LOGGER.error("Failed to configure application " + (groupOption ? "group" : "option") + optionId + " for application applicationSystem " + formParameters.getApplicationSystem().getId() + " theme " + theme, exception);
            }
        }
        return configuredOptions;
    }


    private Element configureThemeQuestionForOption(final String theme,
                                                    final String optionId,
                                                    final Boolean titleApplicationOptions,
                                                    final String preferenceElementId,
                                                    final Boolean groupOption) {
        LOGGER.debug("Configuring application option "+ optionId +" for application system "+ formParameters.getApplicationSystem().getId() +" theme "+ theme);
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
        final List<Element> configuredQuestions = configureQuestions(theme, optionId);
        groupElement.addChild(configuredQuestions.toArray(new Element[configuredQuestions.size()]));
        LOGGER.debug("Configuration of application option "+ optionId +" complete for application system "+ formParameters.getApplicationSystem().getId() +" theme "+ theme);
        return baseElement;
    }

    private List<Element> configureQuestions(final String theme, final String optionId) {
        final List<ThemeQuestion> themeQuestions = queryQuestions(theme, optionId);
        LOGGER.debug("Configuring a list of "+ themeQuestions.size() +" themequestions");
        final ArrayList<Element> configuredElements = new ArrayList<Element>(themeQuestions.size());
        for (ThemeQuestion tq : themeQuestions) {
            Element cfgdElement = tq.generateElement(formParameters);
            LOGGER.debug("configured question {} of type {}", tq.getId(), tq.getClass().getSimpleName());
            configuredElements.add(cfgdElement);
        }
        LOGGER.debug("Configuration of the list complete");
        return configuredElements;
    }

    private List<ThemeQuestion> queryQuestions(final String theme, final String optionId) {
        final ThemeQuestionQueryParameters query = new ThemeQuestionQueryParameters();
        query.setApplicationSystemId(formParameters.getApplicationSystem().getId());
        query.setTheme(theme);
        query.setLearningOpportunityId(optionId);
        query.addSortBy(ThemeQuestion.FIELD_ORDINAL, ThemeQuestionQueryParameters.SORT_ASCENDING);
        LOGGER.debug("Querying questions with " + query);
        return themeQuestionDAO.query(query);
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
        List<String> applicationOptionsInGroup = hakukohdeService.findByGroupAndApplicationSystem(groupId, formParameters.getApplicationSystem().getId());

        // New crap
        Map<String, String> mangleMap = new HashMap<String, String>();
        for (String applicationOptionId: applicationOptionsInGroup){
            HakukohdeDTO hakukohde = hakukohdeService.findByOid(applicationOptionId);
            mangleMap = mangleProviderAndOption(mangleMap, ensureDefaultLanguageTranslations(filterCodePrefix(hakukohde.getTarjoajaNimi())), ensureDefaultLanguageTranslations(filterCodePrefix(hakukohde.getHakukohdeNimi())));
        }

        final Element group = TitledGroupBuilder.TitledGroup(ElementUtil.randomId())
          .i18nText(groupName)
          .help(new I18nText(mangleMap)).build();
        return group;
    }

    private Map<String,String> mangleProviderAndOption(final Map<String, String> mangleMap, final Map<String, String> providerName, final Map<String, String> applicationOption) {
        Set<String> keys = new HashSet<String>(providerName.keySet());
        keys.addAll(applicationOption.keySet());
        for (String key : keys){
            String mangled = mangleMap.get(key);
            if (null == mangled || "".equals(mangled))
                mangleMap.put(key, providerName.get(key) +" - "+ applicationOption.get(key));
            else {
                mangled.concat("\n");
                mangled.concat(providerName.get(key) +" - "+ applicationOption.get(key));
                mangleMap.put(key, mangled);
            }
        }
        return mangleMap;
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
}
