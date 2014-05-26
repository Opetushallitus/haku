package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot;

import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder.TextArea;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder.Theme;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.KielitaitokysymyksetTheme.createPohjakoilutusUlkomainenTaiKeskeyttanyt;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.EMAIL_REGEX;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createRegexValidator;

public class LisatiedotPhase {

    public static final String REQUIRED_EDUCATION_DEGREE = "32";
    public static final String MIN_AGE_REQUIRED_TO_WORK_EXPERIENCE_AGE = "16";
    public static final String TYOKOKEMUS_PATTERN = "^$|^([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|1000)$";

    private LisatiedotPhase() {
    }

    public static Element create(final FormParameters formParameters) {
        Element lisatiedot = Phase("lisatiedot").build(formParameters);
        if (!formParameters.isPervako()) {
            lisatiedot.addChild(createTyokokemus(formParameters));
        }
        Element element = lisatiedot
                .addChild(createLupatiedot(formParameters));
        if (!formParameters.isPervako()) {
            lisatiedot
                    .addChild(createUrheilijanLisakysymykset(formParameters));
        }
        return element;
    }

    static RelatedQuestionComplexRule createTyokokemus(final FormParameters formParameters) {
        Expr isEducation32 = ExprUtil.atLeastOneVariableEqualsToValue(REQUIRED_EDUCATION_DEGREE, OppijaConstants.AO_EDUCATION_DEGREE_KEYS);
        Expr olderThan16 = new OlderThan(new Value(MIN_AGE_REQUIRED_TO_WORK_EXPERIENCE_AGE));
        Expr pohjakoulutusKeskeyttanytTaiUlkomaillasuoritettu = createPohjakoilutusUlkomainenTaiKeskeyttanyt();

        Expr rules = new And(new Not(pohjakoulutusKeskeyttanytTaiUlkomaillasuoritettu), new And(isEducation32, olderThan16));


        Element workExperienceTheme = new ThemeBuilder("tyokokemus").previewable().build(formParameters);


        String tyokokemusId = "TYOKOKEMUSKUUKAUDET";
        TextQuestion tyokokemuskuukaudet = (TextQuestion) new TextQuestionBuilder(tyokokemusId)
                .validator(createRegexValidator(tyokokemusId, TYOKOKEMUS_PATTERN, formParameters, "lisatiedot.tyokokemus.virhe"))
                .size(8)
                .maxLength(4)
                .build(formParameters);
        workExperienceTheme.addChild(tyokokemuskuukaudet);
        RelatedQuestionComplexRule naytetaankoTyokokemus = new RelatedQuestionComplexRule(ElementUtil.randomId(), rules);
        naytetaankoTyokokemus.addChild(workExperienceTheme);
        return naytetaankoTyokokemus;
    }

    static Element createLupatiedot(final FormParameters formParameters) {
        Element lupatiedotTheme = Theme("lupatiedot").previewable().build(formParameters);

        lupatiedotTheme.addChild(
                TitledGroup("lupatiedot.ryhma").build(formParameters).addChild(
                        Checkbox("lupaMarkkinointi").build(formParameters),
                        Checkbox("lupaJulkaisu").build(formParameters),
                        Checkbox("lupaSahkoisesti").build(formParameters),
                        Checkbox("lupaSms").build(formParameters)));

        lupatiedotTheme.addChild(Radio(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE)
                .addOption("suomi", formParameters)
                .addOption("ruotsi", formParameters)
                .required()
                .build(formParameters));

        return lupatiedotTheme;
    }

    static Element createUrheilijanLisakysymykset(final FormParameters formParameters) {
        Element urheilijanLisakysymyksetTeema = new ThemeBuilder("urheilija").previewable().build(formParameters);

        Expr onkoUrheilija = ExprUtil.atLeastOneVariableEqualsToValue(ElementUtil.KYLLA,
                "preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference1_urheilijalinjan_lisakysymys",
                "preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference2_urheilijalinjan_lisakysymys",
                "preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference3_urheilijalinjan_lisakysymys",
                "preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference4_urheilijalinjan_lisakysymys",
                "preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                "preference5_urheilijalinjan_lisakysymys");

        RelatedQuestionComplexRule urheilijanLisakysymyksetSaanto = new RelatedQuestionComplexRule(ElementUtil.randomId(), onkoUrheilija);
        urheilijanLisakysymyksetSaanto.addChild(urheilijanLisakysymyksetTeema);

        Element opinnotGroup = TitledGroup("opinnot").build(formParameters)
                .addChild(
                        createTextQuestion("liikunnanopettajan-nimi", 50, formParameters),
                        createTextQuestion("lukuaineiden-keskiarvo", 4, formParameters),
                        createTextQuestion("pakollinen-liikunnan-numero", 2, formParameters));

        Element urheilulajitGroup = TitledGroup("urheilu").build(formParameters)
                .addChild(
                        createTextQuestion("Urheilulaji1", 50, formParameters),
                        createTextQuestion("lajiliitto1", 50, formParameters),
                        createTextQuestion("Urheilulaji2", 50, formParameters),
                        createTextQuestion("lajiliitto2", 50, formParameters));

        Element saavutuksetGroup = TitledGroup("saavutukset.ryhma").build(formParameters)
                .addChild(
                        TextArea("saavutukset").inline().maxLength(2000).build(formParameters));

        Element valmentajaGroup =
                TitledGroup("valmentajan-yhteystiedot").build(formParameters)
                        .addChild(createTextQuestion("valmentajan-nimi", 50, formParameters))
                        .addChild(createTextQuestion("valmentajan-puhelinnumero", 50, formParameters))
                        .addChild(TextQuestion("valmentajan-sähköpostiosoite")
                                .inline()
                                .size(30)
                                .maxLength(50)
                                .pattern(EMAIL_REGEX)
                                .build(formParameters));

        Element valmennusryhmaGroup =
                TitledGroup("valmennusryhma").build(formParameters)
                        .addChild(createTextQuestion("lajiliitto-maajoukkue", 100, formParameters))
                        .addChild(createTextQuestion("alue-piiri", 100, formParameters))
                        .addChild(createTextQuestion("seura", 100, formParameters));

        urheilijanLisakysymyksetTeema.addChild(opinnotGroup, urheilulajitGroup,
                saavutuksetGroup, valmentajaGroup, valmennusryhmaGroup);
        return urheilijanLisakysymyksetSaanto;
    }

    private static Element createTextQuestion(final String id, int maxLength, final FormParameters formParameters) {
        return TextQuestion(id)
                .inline()
                .size(30)
                .maxLength(maxLength)
                .build(formParameters);
    }
}
