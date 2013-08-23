package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.rules.LanguageTestRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionNotRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.*;

public final class KielitaitokysymyksetTheme {
    private static ImmutableList<String> ids = ImmutableList.of(
            "preference1-Koulutus-id-lang",
            "preference2-Koulutus-id-lang",
            "preference3-Koulutus-id-lang",
            "preference4-Koulutus-id-lang",
            "preference5-Koulutus-id-lang"
    );

    public static Theme createKielitaitokysymyksetTheme() {
        Theme kielitaitokysymyksetTheme =
                new Theme("KielitaitokysymyksetTheme", ElementUtil.createI18NForm("form.kielitaito.otsikko"), null, true);

        kielitaitokysymyksetTheme.addChild(
                createHakutoiveRule("fi"),
                createHakutoiveRule("sv"));
        return kielitaitokysymyksetTheme;
    }

    private static RelatedQuestionRule createHakutoiveRule(final String lang) {
        RelatedQuestionRule hakutoive = new RelatedQuestionRule("preference_" + lang + "_rule", ids,
                lang.toUpperCase(), false);
        RelatedQuestionNotRule aidinkieliTaiPerusopetuksenKieliEiOle =
                new RelatedQuestionNotRule("kielitutkinto_" + lang + "_rule",
                        ImmutableList.of("aidinkieli", "perusopetuksen_kieli"), lang.toUpperCase());







        LanguageTestRule langTest = new LanguageTestRule("langTest_" + lang, lang.toUpperCase());
        langTest.addChild(createKielitutkinto("yleinen_kielitutkinto_" + lang),
                createKielitutkinto("valtionhallinnon_kielitutkinto_" + lang));
        aidinkieliTaiPerusopetuksenKieliEiOle.addChild(langTest);

        hakutoive.addChild(aidinkieliTaiPerusopetuksenKieliEiOle);
        return hakutoive;
    }


    private static Radio createKielitutkinto(final String id) {
        Radio radio = new Radio(id,
                createI18NForm("form.kielitaito." +
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, id).replace('_', '.')));
        addDefaultTrueFalseOptions(radio);
        addRequiredValidator(radio);
        return radio;
    }
}
