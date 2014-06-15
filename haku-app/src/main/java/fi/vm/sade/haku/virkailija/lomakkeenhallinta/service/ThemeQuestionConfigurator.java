package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
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

import javax.ws.rs.HEAD;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil.ensureDefaultLanguageTranslations;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil.filterCodePrefix;

public final class ThemeQuestionConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionConfigurator.class);

    private final ThemeQuestionDAO themeQuestionDAO;
    private final HakukohdeService hakukohdeService;
    private final ApplicationOptionService applicationOptionService;
    private final FormParameters formParameters;

    private static final String PREFERENCE_PREFIX = "preference";
    private static final String OPTION_POSTFIX = "-Koulutus-id";

    public ThemeQuestionConfigurator(final ThemeQuestionDAO themeQuestionDAO, final HakukohdeService hakukohdeService,
                                     ApplicationOptionService applicationOptionService, final FormParameters formParameters) {
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.applicationOptionService = applicationOptionService;
        this.formParameters = formParameters;
    }

    public List<Element> findAndConfigure(final ApplicationSystem applicationSystem, final String theme){
        return findAndConfigure(applicationSystem, theme, true, null);
    }

    public List<Element> findAndConfigure(final ApplicationSystem applicationSystem, final String theme, final String preferenceElementId){
        return findAndConfigure(applicationSystem, theme, false, preferenceElementId);
    }

    private List<Element> findAndConfigure(final ApplicationSystem applicationSystem, final String theme,
                                           final Boolean titleApplicationOptions, final String preferenceElementId){
        LOGGER.debug("Configuring themequestions for application system: "+ applicationSystem.getId()
                + " theme:" +theme+" generating titled groups " + titleApplicationOptions);
        ThemeQuestionQueryParameters queryParameters = new ThemeQuestionQueryParameters();
        queryParameters.setApplicationSystemId(applicationSystem.getId());
        queryParameters.setTheme(theme);
        List<String> applicationOptionIds = themeQuestionDAO.queryApplicationOptionsIn(queryParameters);
        LOGGER.debug("Got " + applicationOptionIds.size() + " application options for application system "
                + applicationSystem.getId() + " theme" + theme);
        ArrayList<Element> configuredApplicationOptions = new ArrayList<Element>(applicationOptionIds.size());
        for (String applicationOptionId : applicationOptionIds) {
            try {
                configuredApplicationOptions.add(configureThemeQuestionForApplicationOption(applicationSystem, theme,
                        applicationOptionId, titleApplicationOptions, preferenceElementId));
            }catch (RuntimeException exception){
                LOGGER.error("Failed to configure application option "+ applicationOptionId + " for application applicationSystem "
                        + applicationSystem.getId() + " theme " +theme, exception);
            }
        }
        LOGGER.debug("Configuration complete for application system "+ applicationSystem.getId() + " theme " + theme);
        return configuredApplicationOptions;
    }

    private Element configureThemeQuestionForApplicationOption(final ApplicationSystem applicationSystem, final String theme,
                                                               final String applicationOptionId, final Boolean titleApplicationOptions,
                                                               final String  preferenceElementId ) {
        LOGGER.debug("Configuring application option " +  applicationOptionId +" for application system "
                +applicationSystem.getId() + " theme " + theme);
        Element baseElement = generateApplicationOptionRule(applicationSystem,applicationOptionId, preferenceElementId);
        Element groupElement = baseElement;
        if (titleApplicationOptions){
            groupElement = generateTitleGroupForApplicationOption(applicationOptionId);
            baseElement.addChild(groupElement);
        }
        final List<Element> configuredQuestions = configureQuestions(applicationSystem.getId(), theme,applicationOptionId);
        groupElement.addChild(configuredQuestions.toArray(new Element[configuredQuestions.size()]));
        LOGGER.debug("Configuration of application option " + applicationOptionId + " complete for application system "
                +applicationSystem.getId() + " theme " + theme);
        return baseElement;
    }

    private List<ThemeQuestion> findQuestions(final String asId, final String theme, final String applicationOptionId){
        ThemeQuestionQueryParameters query = new ThemeQuestionQueryParameters();
        query.setApplicationSystemId(asId);
        query.setTheme(theme);
        query.setLearningOpportunityId(applicationOptionId);
        LOGGER.debug("Querying questions with " + query);
        return themeQuestionDAO.query(query);
    }

    private List<Element> configureQuestions(final String asId, final String theme, final String applicationOptionId){
        List<ThemeQuestion> themeQuestions = findQuestions(asId, theme,applicationOptionId);
        LOGGER.debug("Configuring a list of " +themeQuestions.size() + " themequestions");
        ArrayList<Element> configuredElements = new ArrayList<Element>(themeQuestions.size());
        for(ThemeQuestion tq : themeQuestions){
            Element cfgdElement = tq.generateElement(formParameters);
            LOGGER.debug("configured question {} of type {}", tq.getId(), tq.getType());
            configuredElements.add(cfgdElement);
        }
        LOGGER.debug("Configuration of the list complete");
        return configuredElements;
    }

    private Element generateTitleGroupForApplicationOption(final String applicationOptionId){
        LOGGER.debug("Generating Titled group");
        HakukohdeDTO hakukohde = hakukohdeService.findByOid(applicationOptionId);
        Map<String,String> applicationOptionName = ensureDefaultLanguageTranslations(filterCodePrefix(hakukohde.getHakukohdeNimi()));
        Map<String,String> providerName = ensureDefaultLanguageTranslations(filterCodePrefix(hakukohde.getTarjoajaNimi()));
        Element group = TitledGroupBuilder.TitledGroup(ElementUtil.randomId())
                .i18nText(new I18nText(applicationOptionName))
                .help(new I18nText(providerName)).build();
        return group;
    }

    private Element generateApplicationOptionRule(final ApplicationSystem applicationSystem, final String applicationOptionId,
                                                  final String  preferenceElementId){
        LOGGER.debug("Generating the ApplicationOptionRule group");
        final Expr ruleExpr = generateExpr(applicationSystem, applicationOptionId, preferenceElementId);
        Element rule = Rule(ElementUtil.randomId()).setExpr(ruleExpr).build();
        return rule;
    }


    private Expr generateExpr(final ApplicationSystem applicationSystem, final String applicationOptionId, final String preferenceElementId){
        List<String> preferenceAoKeys = new ArrayList<String>();
        if (null != preferenceElementId){
            preferenceAoKeys.add(preferenceElementId+OPTION_POSTFIX);
        }
        else {
            // TODO: FIX use from application system when it knows the number of allowed preferences
            for (int i = 1; i <= applicationSystem.getMaxApplicationOptions(); i++){
                preferenceAoKeys.add(PREFERENCE_PREFIX+i+OPTION_POSTFIX);
            }
        }
        return ExprUtil.atLeastOneVariableEqualsToValue(applicationOptionId,
                preferenceAoKeys.toArray(new String[preferenceAoKeys.size()]));
    }
}
