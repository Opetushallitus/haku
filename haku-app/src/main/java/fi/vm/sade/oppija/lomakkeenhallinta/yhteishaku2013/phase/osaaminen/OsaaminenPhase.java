package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import java.util.List;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NForm;

public class OsaaminenPhase {

    public static Phase create(final KoodistoService koodistoService) {
        Phase osaaminen = new Phase("osaaminen", createI18NForm("form.osaaminen.otsikko"), false);
        osaaminen.addChild(ArvosanatTheme.createArvosanatTheme(koodistoService));

        Expr leftValue = new Variable("AIDINKIELI_ID");
        Expr rightValue = new Value("FI");

        Expr leftValue2 = new Variable("PK_AI_OPPIAINE");
        Expr rightValue2 = new Value("FI");

        new Variable("preference1-Koulutus-id-lang");
        new Variable("preference2-Koulutus-id-lang");
        new Variable("preference3-Koulutus-id-lang");
        new Variable("preference4-Koulutus-id-lang");
        new Variable("preference5-Koulutus-id-lang");


        Expr op = new Equals(leftValue, rightValue);
        Expr op2 = new Equals(leftValue2, rightValue2);
        Expr or = new Or(op, op2);

        RelatedQuestionComplexRule relatedQuestionComplexRule = new RelatedQuestionComplexRule(ElementUtil.randomId(), or);
        Theme kielitaitokysymyksetTheme = KielitaitokysymyksetTheme.createKielitaitokysymyksetTheme();
        relatedQuestionComplexRule.addChild(kielitaitokysymyksetTheme);
        osaaminen.addChild(relatedQuestionComplexRule);
        return osaaminen;
    }

    private static Expr atLeastOneVariableEqualsToValue(final List<String> ids, final String value) {
        if (ids.size() == 1) {
            return new Equals(new Variable(ids.get(0)), new Value(value));
        } else {
            Expr current = null;
            Expr equal;
            for (String id : ids) {
                equal = new Equals(new Variable(id), new Value(value));
                if (current == null) {
                    current = new Equals(new Variable(id), new Value(value));
                } else {
                    current = new Or(current, equal);
                }
            }
            return current;
        }
    }

    private static Expr valueIsIn(final String id, final String... values) {
        if (values.length == 1) {
            return new Equals(new Variable(id), new Value(values[0]));
        } else {
            Expr current = null;
            Expr equal;
            for (String value : values) {
                equal = new Equals(new Variable(id), new Value(value));
                if (current == null) {
                    current = new Equals(new Variable(id), new Value(value));
                } else {
                    current = new Or(current, equal);
                }
            }
            return current;
        }
    }

    public static void main(String[] args) {
        ImmutableList<String> preferenceIds = ImmutableList.of(
                "preference1-Koulutus-id-lang",
                "preference2-Koulutus-id-lang",
                "preference3-Koulutus-id-lang",
                "preference4-Koulutus-id-lang",
                "preference5-Koulutus-id-lang"
        );
        ImmutableList<String> languageQuestions = ImmutableList.of(
                "aidinkieli",
                "perusopetuksen_kieli",
                "lukion_kieli",
                "PK_AI_OPPIAINE",
                "LK_AI_OPPIAINE"

        );
        Expr suomiOnAidinkieliTaiKouluSuomeksi = atLeastOneVariableEqualsToValue(languageQuestions, "FI");
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksi = atLeastOneVariableEqualsToValue(languageQuestions, "SV");


        Expr suomenArvosanatRiittaa = arvosanatarkastus("FI");
        Expr ruotsinArvosanatRiittaa = arvosanatarkastus("SV");


        Expr haettuSuomenkieliseenKoulutukseen = atLeastOneVariableEqualsToValue(preferenceIds, "FI");
        Expr haettuRuotsinkieliseenKoulutukseen = atLeastOneVariableEqualsToValue(preferenceIds, "SV");

        Expr riittavaSuomenkielenTaito = new Or(suomiOnAidinkieliTaiKouluSuomeksi, suomenArvosanatRiittaa);
        Expr riittavaRuotsinkielenTaito = new Or(ruotsiOnAidinkieliTaiKouluRuotsiksi, ruotsinArvosanatRiittaa);


        Expr kysytaankoSuomi = new And(haettuSuomenkieliseenKoulutukseen, new Not(riittavaSuomenkielenTaito));
        Expr kysytaankoRuotsi = new And(haettuRuotsinkieliseenKoulutukseen, new Not(riittavaRuotsinkielenTaito));

        Expr naytetaankoKielitaitoteema = new Or(kysytaankoSuomi, kysytaankoRuotsi);


        ImmutableMap<String, String> context = ImmutableMap.of(
                "preference1-Koulutus-id-lang", "FI",
                "preference2-Koulutus-id-lang", "SV",
                "aidinkieli", "SV",
                "PK_A1", "6",
                "PK_A1_OPPIAINE" ,"FI");

        boolean result = naytetaankoKielitaitoteema.evaluate(context);
        System.out.println(result);
    }

    private static Expr arvosanatarkastus(final String language) {
        Expr pkA1OnSuomi = atLeastOneVariableEqualsToValue(ImmutableList.of("PK_A1_OPPIAINE"), language);
        Expr lkA1OnSuomi = atLeastOneVariableEqualsToValue(ImmutableList.of("LK_A1_OPPIAINE"), language);
        Expr pkA2OnSuomi = atLeastOneVariableEqualsToValue(ImmutableList.of("PK_A2_OPPIAINE"), language);
        Expr lkA2OnSuomi = atLeastOneVariableEqualsToValue(ImmutableList.of("LK_A2_OPPIAINE"), language);
        Expr pkA1ArvosanaOnHyva = valueIsIn("PK_A1", "7", "8", "9", "10");
        Expr lkA1ArvosanaOnHyva = valueIsIn("LK_A1", "7", "8", "9", "10");
        Expr pkA2ArvosanaOnHyva = valueIsIn("PK_A2", "7", "8", "9", "10");
        Expr lkA2ArvosanaOnHyva = valueIsIn("LK_A2", "7", "8", "9", "10");
        Expr pkA1Riittaa = new And(pkA1OnSuomi, pkA1ArvosanaOnHyva);
        Expr lkA1Riittaa = new And(lkA1OnSuomi, lkA1ArvosanaOnHyva);
        Expr pkA2Riittaa = new And(pkA2OnSuomi, pkA2ArvosanaOnHyva);
        Expr lkA2Riittaa = new And(lkA2OnSuomi, lkA2ArvosanaOnHyva);
        return new Or(new Or(pkA1Riittaa, pkA2Riittaa), new Or(lkA1Riittaa, lkA2Riittaa));
    }
}
