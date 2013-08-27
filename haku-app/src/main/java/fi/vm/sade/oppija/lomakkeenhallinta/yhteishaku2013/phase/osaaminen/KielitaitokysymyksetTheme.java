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
    private static final String[] preferenceIds = new String[]{
            "preference1-Koulutus-id-lang",
            "preference2-Koulutus-id-lang",
            "preference3-Koulutus-id-lang",
            "preference4-Koulutus-id-lang",
            "preference5-Koulutus-id-lang"
    };
    private static final String[] languageQuestions = new String[]{
            "aidinkieli",
            "perusopetuksen_kieli",
            "lukion_kieli",
            "PK_AI_OPPIAINE",
            "LK_AI_OPPIAINE"
    };

    public static Element createKielitaitokysymyksetTheme() {
        Expr suomiOnAidinkieliTaiKouluSuomeksi = atLeastOneVariableEqualsToValue("FI", languageQuestions);
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksi = atLeastOneVariableEqualsToValue("SV", languageQuestions);

        Expr suomenArvosanatRiittaa = arvosanatarkastus("FI");
        Expr ruotsinArvosanatRiittaa = arvosanatarkastus("SV");


        Expr haettuSuomenkieliseenKoulutukseen = atLeastOneVariableEqualsToValue("FI", preferenceIds);
        Expr haettuRuotsinkieliseenKoulutukseen = atLeastOneVariableEqualsToValue("SV", preferenceIds);

        Expr riittavaSuomenkielenTaito = new Or(suomiOnAidinkieliTaiKouluSuomeksi, suomenArvosanatRiittaa);
        Expr riittavaRuotsinkielenTaito = new Or(ruotsiOnAidinkieliTaiKouluRuotsiksi, ruotsinArvosanatRiittaa);


        Expr kysytaankoSuomi = new And(haettuSuomenkieliseenKoulutukseen, new Not(riittavaSuomenkielenTaito));
        Expr kysytaankoRuotsi = new And(haettuRuotsinkieliseenKoulutukseen, new Not(riittavaRuotsinkielenTaito));

        Expr naytetaankoKielitaitoteema = new Or(kysytaankoSuomi, kysytaankoRuotsi);

        RelatedQuestionComplexRule naytetaankoTeema = new RelatedQuestionComplexRule("naytä-teema-" + ElementUtil.randomId(), naytetaankoKielitaitoteema);

        Theme kielitaitokysymyksetTheme =
                new Theme("KielitaitokysymyksetTheme", ElementUtil.createI18NForm("form.kielitaito.otsikko"), null, true);
        naytetaankoTeema.addChild(kielitaitokysymyksetTheme);

        RelatedQuestionComplexRule naytetaankoSuomi = new RelatedQuestionComplexRule("nayta-suomi-" + ElementUtil.randomId(), kysytaankoSuomi);
        RelatedQuestionComplexRule naytetaankoRuotsi = new RelatedQuestionComplexRule("nayta-Ruotsi-" + ElementUtil.randomId(), kysytaankoRuotsi);
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

    private static Expr arvosanatarkastus(final String language) {
        Expr pkA1OnSuomi = atLeastOneVariableEqualsToValue(language, "PK_A1_OPPIAINE");
        Expr lkA1OnSuomi = atLeastOneVariableEqualsToValue(language, "LK_A1_OPPIAINE");
        Expr pkA2OnSuomi = atLeastOneVariableEqualsToValue(language, "PK_A2_OPPIAINE");
        Expr lkA2OnSuomi = atLeastOneVariableEqualsToValue(language, "LK_A2_OPPIAINE");
        Expr pkA1ArvosanaOnHyva = atLeastOneValueEqualsToVariable("PK_A1", "7", "8", "9", "10");
        Expr lkA1ArvosanaOnHyva = atLeastOneValueEqualsToVariable("LK_A1", "7", "8", "9", "10");
        Expr pkA2ArvosanaOnHyva = atLeastOneValueEqualsToVariable("PK_A2", "7", "8", "9", "10");
        Expr lkA2ArvosanaOnHyva = atLeastOneValueEqualsToVariable("LK_A2", "7", "8", "9", "10");
        Expr pkA1Riittaa = new And(pkA1OnSuomi, pkA1ArvosanaOnHyva);
        Expr lkA1Riittaa = new And(lkA1OnSuomi, lkA1ArvosanaOnHyva);
        Expr pkA2Riittaa = new And(pkA2OnSuomi, pkA2ArvosanaOnHyva);
        Expr lkA2Riittaa = new And(lkA2OnSuomi, lkA2ArvosanaOnHyva);
        return new Or(new Or(pkA1Riittaa, pkA2Riittaa), new Or(lkA1Riittaa, lkA2Riittaa));
    }
}
