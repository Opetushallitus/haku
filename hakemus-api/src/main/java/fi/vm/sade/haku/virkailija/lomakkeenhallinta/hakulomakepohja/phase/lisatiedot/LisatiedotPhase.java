package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.hakemus.service.Role.*;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder.TextArea;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public class LisatiedotPhase {

    public static final String MIN_AGE_REQUIRED_TO_WORK_EXPERIENCE_AGE = "16";
    public static final String TYOKOKEMUS_PATTERN = "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$";
    public static final String OPPISOPIMUS_THEME_ID = "oppisopimus";

    private LisatiedotPhase() {
    }

    public static Element create(final FormParameters formParameters) {
        Element lisatiedot = Phase("lisatiedot").setEditAllowedByRoles(ROLE_RU, ROLE_CRUD, ROLE_OPO, ROLE_HETUTTOMIENKASITTELY, ROLE_KKVIRKAILIJA).formParams(formParameters).build();
        if (formParameters.kysytaankoTyokokemus()) {
            lisatiedot.addChild(createTyokokemus(formParameters));
        }

        // Erkkahaun spesiaalit
        if (formParameters.kysytaankoErityisopetuksenTarve()) {
            Element erityisopetuksenTarve = Theme("erityisopetuksen_tarve")
                    .formParams(formParameters)
                    .build();

            Element hojks = Radio("hojks")
                    .addOptions(ImmutableList.of(
                            new Option(formParameters.getI18nText("form.yleinen.kylla"), KYLLA),
                            new Option(formParameters.getI18nText("form.yleinen.ei"), EI)))
                    .required()
                    .formParams(formParameters).build();
            erityisopetuksenTarve.addChild(hojks);

            Element koulutuskokeilu = Radio("koulutuskokeilu")
                    .addOptions(ImmutableList.of(
                            new Option(formParameters.getI18nText("form.yleinen.kylla"), KYLLA),
                            new Option(formParameters.getI18nText("form.yleinen.ei"), EI)))
                    .required()
                    .formParams(formParameters).build();
            erityisopetuksenTarve.addChild(koulutuskokeilu);

            Element miksiAmmatilliseen = TextArea("miksi_ammatilliseen")
                    .maxLength(2000)
                    .required()
                    .formParams(formParameters)
                    .build();
            erityisopetuksenTarve.addChild(miksiAmmatilliseen);
            lisatiedot.addChild(erityisopetuksenTarve);
        }

        Element element = lisatiedot
                .addChild(createLupatiedot(formParameters));

        if(formParameters.kysytaankoOppisopimuskysymys()){
            lisatiedot.addChild(createOppisopimusLisakysymys(formParameters));
        }

        return element;
    }

    private static final String[] BASE_EDUCATION_KESK_ULK = new String[]{
            OppijaConstants.KESKEYTYNYT,
            OppijaConstants.ULKOMAINEN_TUTKINTO
    };

    static Expr createPohjakoilutusUlkomainenTaiKeskeyttanyt() {
        return ExprUtil.atLeastOneValueEqualsToVariable(OppijaConstants.ELEMENT_ID_BASE_EDUCATION,
                BASE_EDUCATION_KESK_ULK);
    }

    static Element createTyokokemus(final FormParameters formParameters) {
        final String REQUIRED_EDUCATION_DEGREE = formParameters.useEducationDegreeURI() ? "koulutusasteoph2002_32" : "32";
        Expr isEducation32 = ExprUtil.atLeastOneVariableEqualsToValue(REQUIRED_EDUCATION_DEGREE, OppijaConstants.AO_EDUCATION_DEGREE_KEYS);
        Expr olderThan16 = new OlderThan(new Value(MIN_AGE_REQUIRED_TO_WORK_EXPERIENCE_AGE));
        Expr pohjakoulutusKeskeyttanytTaiUlkomaillasuoritettu = createPohjakoilutusUlkomainenTaiKeskeyttanyt();

        Expr rules = new And(new Not(pohjakoulutusKeskeyttanytTaiUlkomaillasuoritettu), (formParameters.isDemoMode()? isEducation32:new And(isEducation32, olderThan16)));

        String tyokokemusId = "TYOKOKEMUSKUUKAUDET";
        Element workExperienceTheme = Theme("tyokokemus").previewable().formParams(formParameters)
                .addChild(TextQuestionBuilder.TextQuestion(tyokokemusId)
                        .validator(createRegexValidator(TYOKOKEMUS_PATTERN, "lisatiedot.tyokokemus.virhe"))
                        .size(8)
                        .maxLength(4)).build();

        Element naytetaankoTyokokemus = Rule(rules).build();
        naytetaankoTyokokemus.addChild(workExperienceTheme);
        return naytetaankoTyokokemus;
    }

    static Element createLupatiedot(final FormParameters formParameters) {
        Element lupatiedotTheme = Theme("lupatiedot").previewable().configurable().formParams(formParameters).build();

        if(formParameters.isOnlyThemeGenerationForFormEditor())
            return lupatiedotTheme;

        Element lupatietoGrp = TitledGroup("lupatiedot.ryhma").formParams(formParameters).build();
        lupatietoGrp.addChild(Checkbox("lupaMarkkinointi").formParams(formParameters).build());
        lupatietoGrp.addChild(Checkbox("lupaJulkaisu").formParams(formParameters).build());
        if(!formParameters.isSahkoinenViestintaLupa()) {
            lupatietoGrp.addChild(Checkbox("lupaSahkoisesti").formParams(formParameters).build());
        }
        lupatietoGrp.addChild(Checkbox("lupaSms").formParams(formParameters).build());
        //lupatietoGrp.addChild(Checkbox("lupaKela").formParams(formParameters).build());

        lupatiedotTheme.addChild(lupatietoGrp);

        if(formParameters.isSahkoinenViestintaLupa() && !formParameters.isHigherEd()) {
            Element sahkoinenViestintaGrp = RadioBuilder.Radio("lupatiedot-sahkoinen-viestinta")
                    .addOptions(ImmutableList.of(
                            new Option(formParameters.getI18nText("lupatiedot.sahkoinen.suostun"), KYLLA),
                            new Option(formParameters.getI18nText("lupatiedot.sahkoinen.ensuostu"), EI)))
                    .i18nText(formParameters.getI18nText("lupatiedot.sahkoinen.viestinta"))
                    .help(formParameters.getI18nText("lupatiedot.sahkoinen.viestinta.help"))
                    .required()
                    .formParams(formParameters).build();

            lupatiedotTheme.addChild(sahkoinenViestintaGrp);
        }

        OptionQuestionBuilder kieliRadioBuilder = Radio(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE)
                .addOption("suomi", formParameters);
        if (!formParameters.isAmmattillinenEritysopettajaTaiOppilaanohjaajaKoulutus()
                && !formParameters.isAmmattillinenOpettajaKoulutus()) {
            kieliRadioBuilder.addOption("ruotsi", formParameters);
        }
        if (formParameters.isHigherEd()
                && !formParameters.isAmmattillinenEritysopettajaTaiOppilaanohjaajaKoulutus()) {
            kieliRadioBuilder.addOption("englanti", formParameters);
        }
        
        lupatiedotTheme.addChild(kieliRadioBuilder
                .required()
                .formParams(formParameters).build());

        ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionConfigurator();
        lupatiedotTheme.addChild(configurator.findAndConfigure(lupatiedotTheme.getId()));
        return lupatiedotTheme;
    }

    static Element createOppisopimusLisakysymys(final FormParameters formParameters) {
        final List<String> vocationalKeys = new ArrayList<String>();
        for (int i = 1; i <= formParameters.getApplicationSystem().getMaxApplicationOptions(); i++) {
            vocationalKeys.add(OppijaConstants.PREFERENCE_PREFIX + i + OppijaConstants.OPTION_ID_POSTFIX + "-vocational");
        }
        Expr hakeeAmmatilliseen = ExprUtil.atLeastOneVariableEqualsToValue(ElementUtil.KYLLA,
                vocationalKeys.toArray(new String[vocationalKeys.size()]));

        Element hakeeAmmatilliseenSaanto = Rule(hakeeAmmatilliseen).build();
        Element hakeeAmmatilliseenTeema = Theme(OPPISOPIMUS_THEME_ID).previewable().formParams(formParameters).build();
        hakeeAmmatilliseenSaanto.addChild(hakeeAmmatilliseenTeema);

        hakeeAmmatilliseenTeema.addChild(Checkbox("kiinnostunutoppisopimuksesta").formParams(formParameters).build());
        return hakeeAmmatilliseenSaanto;
    }


    private static Element createTextQuestion(final String id, int maxLength, final FormParameters formParameters) {
        return TextQuestion(id)
                .inline()
                .size(30)
                .maxLength(maxLength)
                .formParams(formParameters).build();
    }
}
