package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionNotRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.*;

public final class KielitaitokysymyksetTheme {
    private static final String NOT_FI = "^((?!FI)[A-Z]{2})$";
    private static final String NOT_SV = "^((?!SV)[A-Z]{2})$";

    public static Theme createKielitaitokysymyksetTheme() {
        Theme kielitaitokysymyksetTheme =
                new Theme("KielitaitokysymyksetTheme", ElementUtil.createI18NForm("form.kielitaito.otsikko"), null, true);

        ImmutableList<String> ids = ImmutableList.of(
                "preference1-Koulutus-id-lang",
                "preference2-Koulutus-id-lang",
                "preference3-Koulutus-id-lang",
                "preference4-Koulutus-id-lang",
                "preference5-Koulutus-id-lang"
        );
        RelatedQuestionRule suomenkielinenHakutoive = new RelatedQuestionRule("preference_fi_rule", ids, "FI", false);
        RelatedQuestionNotRule aidinkieliTaiPerusopetuksenKieliEiOleSuomi =
                new RelatedQuestionNotRule("kielitutkinto_fi_rule",
                        ImmutableList.of("aidinkieli", "perusopetuksen_kieli"), NOT_FI);
        RelatedQuestionRule oppiaineEioleSuomi = new RelatedQuestionRule("oppiaine_fi_rule", ImmutableList.of("PK_AI_OPPIAINE"), NOT_FI, false);

        suomenkielinenHakutoive.addChild(
                aidinkieliTaiPerusopetuksenKieliEiOleSuomi.addChild(
                        oppiaineEioleSuomi.addChild(
                                createKielitutkinto("yleinen_kielitutkinto_fi"),
                                createKielitutkinto("valtionhallinnon_kielitutkinto_fi"))));

        RelatedQuestionRule ruotsinkielinenHakutoive = new RelatedQuestionRule("preference_sv_rule", ids, "SV", false);
        RelatedQuestionNotRule aidinkieliTaiPerusopetuksenKieliEiOleRuotsi =
                new RelatedQuestionNotRule("kielitutkinto_sv_rule",
                        ImmutableList.of("aidinkieli", "perusopetuksen_kieli"), NOT_SV);
        RelatedQuestionRule oppiaineEioleRuotsi = new RelatedQuestionRule("oppiaine_sv_rule", ImmutableList.of("PK_AI_OPPIAINE"), NOT_SV, false);
        ruotsinkielinenHakutoive.addChild(
                aidinkieliTaiPerusopetuksenKieliEiOleRuotsi.addChild(
                        oppiaineEioleRuotsi.addChild(
                                createKielitutkinto("yleinen_kielitutkinto_sv"),
                                createKielitutkinto("valtionhallinnon_kielitutkinto_sv"))));

        kielitaitokysymyksetTheme.addChild(
                suomenkielinenHakutoive,
                ruotsinkielinenHakutoive);
        return kielitaitokysymyksetTheme;
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
