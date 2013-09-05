package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import com.google.common.base.CaseFormat;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.oppija.lomake.domain.rules.expression.And;
import fi.vm.sade.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.oppija.lomake.domain.rules.expression.Not;
import fi.vm.sade.oppija.lomake.domain.rules.expression.Or;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.*;

public final class KielitaitokysymyksetTheme {
    private static final String[] PREFERENCE_IDS = new String[]{
            "preference1-Koulutus-id-lang",
            "preference2-Koulutus-id-lang",
            "preference3-Koulutus-id-lang",
            "preference4-Koulutus-id-lang",
            "preference5-Koulutus-id-lang"
    };
    private static final String[] LANGUAGE_QUESTIONS = new String[]{
            "aidinkieli",
            "perusopetuksen_kieli",
            "lukion_kieli",
    };

    public static Element createKielitaitokysymyksetTheme() {
        Expr suomiOnAidinkieliTaiKouluSuomeksi = atLeastOneVariableEqualsToValue("FI", LANGUAGE_QUESTIONS);
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksi = atLeastOneVariableEqualsToValue("SV", LANGUAGE_QUESTIONS);


        Expr haettuSuomenkieliseenKoulutukseen = atLeastOneVariableEqualsToValue("FI", PREFERENCE_IDS);
        Expr haettuRuotsinkieliseenKoulutukseen = atLeastOneVariableEqualsToValue("SV", PREFERENCE_IDS);


        Expr kysytaankoSuomi = new And(haettuSuomenkieliseenKoulutukseen, new Not(suomiOnAidinkieliTaiKouluSuomeksi));
        Expr kysytaankoRuotsi = new And(haettuRuotsinkieliseenKoulutukseen, new Not(ruotsiOnAidinkieliTaiKouluRuotsiksi));

        Expr naytetaankoKielitaitoteema = new Or(kysytaankoSuomi, kysytaankoRuotsi);

        RelatedQuestionComplexRule naytetaankoTeema = new RelatedQuestionComplexRule(ElementUtil.randomId(), naytetaankoKielitaitoteema);

        Theme kielitaitokysymyksetTheme =
                new Theme("KielitaitokysymyksetTheme", ElementUtil.createI18NForm("form.kielitaito.otsikko"), true);
        naytetaankoTeema.addChild(kielitaitokysymyksetTheme);

        RelatedQuestionComplexRule naytetaankoSuomi = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSuomi);
        RelatedQuestionComplexRule naytetaankoRuotsi = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoRuotsi);
        naytetaankoSuomi.addChild(createKielitutkinto("yleinen_kielitutkinto_fi"), createKielitutkinto("valtionhallinnon_kielitutkinto_fi"));
        naytetaankoRuotsi.addChild(createKielitutkinto("yleinen_kielitutkinto_sv"), createKielitutkinto("valtionhallinnon_kielitutkinto_sv"));
        kielitaitokysymyksetTheme.addChild(naytetaankoSuomi, naytetaankoRuotsi);
        return naytetaankoTeema;
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
