package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public final class KielitaitokysymyksetTheme {

    private static final String[] LANGUAGE_QUESTIONS = new String[]{
            "aidinkieli",
            OppijaConstants.PERUSOPETUS_KIELI,
            OppijaConstants.LUKIO_KIELI,
    };

    private static final String[] LANGUAGE_QUESTIONS_PK = new String[]{
            "aidinkieli",
            OppijaConstants.PERUSOPETUS_KIELI
    };
    private static final String[] LANGUAGE_QUESTIONS_YO = new String[]{
            "aidinkieli",
            OppijaConstants.LUKIO_KIELI
    };
    private static final String[] BASE_EDUCATION_PK = new String[]{
            OppijaConstants.PERUSKOULU,
            OppijaConstants.OSITTAIN_YKSILOLLISTETTY,
            OppijaConstants.ALUEITTAIN_YKSILOLLISTETTY,
            OppijaConstants.YKSILOLLISTETTY
    };

    private static final String[] BASE_EDUCATION_KESK_ULK = new String[]{
            OppijaConstants.KESKEYTYNYT,
            OppijaConstants.ULKOMAINEN_TUTKINTO
    };

    private KielitaitokysymyksetTheme() {
    }

    public static Element createKielitaitokysymyksetTheme(final FormParameters formParameters) {
        if (formParameters.getFormTemplateType().equals(FormParameters.FormTemplateType.YHTEISHAKU_KEVAT)) {
            return createKielitaitokysymyksetThemeKevat(formParameters);
        } else {
            return createKielitaitokysymyksetThemeSyksy(formParameters);
        }
    }

    private static Element createKielitaitokysymyksetThemeKevat(final FormParameters formParameters) {
        //PK
        Expr pohjakoulutusOnPK = ExprUtil.atLeastOneValueEqualsToVariable(OppijaConstants.ELEMENT_ID_BASE_EDUCATION, BASE_EDUCATION_PK);

        Expr suomiOnAidinkieliTaiKouluSuomeksiPK = ExprUtil.atLeastOneVariableEqualsToValue("FI", LANGUAGE_QUESTIONS_PK);
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksiPK = ExprUtil.atLeastOneVariableEqualsToValue("SV", LANGUAGE_QUESTIONS_PK);
        Expr saameOnAidinkieliTaiKouluSaameksiPK = ExprUtil.atLeastOneVariableEqualsToValue("SE", LANGUAGE_QUESTIONS_PK);
        Expr viittomaOnAidinkieliTaiKouluViittomaksiPK = ExprUtil.atLeastOneVariableEqualsToValue("VK", LANGUAGE_QUESTIONS_PK);

        Expr haettuSuomenkieliseenAmmatilliseenKoulutukseen = haettuAmmatilliseenOppilaitokseenKielella(OppijaConstants.EDUCATION_LANGUAGE_FI, 5);
        Expr haettuRuotsinkieliseenAmmatilliseenKoulutukseen = haettuAmmatilliseenOppilaitokseenKielella(OppijaConstants.EDUCATION_LANGUAGE_SV, 5);
        Expr haettuSaamenkieliseenAmmatilliseenKoulutukseen = haettuAmmatilliseenOppilaitokseenKielella(OppijaConstants.EDUCATION_LANGUAGE_SE, 5);
        Expr haettuViittomakieliseenAmmatilliseenKoulutukseen = haettuAmmatilliseenOppilaitokseenKielella(OppijaConstants.EDUCATION_LANGUAGE_VE, 5);

        Expr kysytaankoSuomiPK = new And(new And(haettuSuomenkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnPK), new Not(suomiOnAidinkieliTaiKouluSuomeksiPK));
        Expr kysytaankoRuotsiPK = new And(new And(haettuRuotsinkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnPK), new Not(ruotsiOnAidinkieliTaiKouluRuotsiksiPK));
        Expr kysytaankoSaamePK = new And(new And(haettuSaamenkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnPK), new Not(saameOnAidinkieliTaiKouluSaameksiPK));
        Expr kysytaankoViittomaPK = new And(new And(haettuViittomakieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnPK), new Not(viittomaOnAidinkieliTaiKouluViittomaksiPK));

        RelatedQuestionRule naytetaankoSuomiPK = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoSuomiPK);
        RelatedQuestionRule naytetaankoRuotsiPK = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoRuotsiPK);
        RelatedQuestionRule naytetaankoSaamePK = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoSaamePK);
        RelatedQuestionRule naytetaankoViittomaPK = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoViittomaPK);


        Integer hakukausiVuosi = formParameters.getApplicationSystem().getHakukausiVuosi();
        Expr tuoreTodistusPK = new Not(ExprUtil.atLeastOneVariableEqualsToValue(String.valueOf(hakukausiVuosi), OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI));

        RelatedQuestionRule kysytaankoArvosanaPkFi = new RelatedQuestionRule(ElementUtil.randomId(), tuoreTodistusPK);
        kysytaankoArvosanaPkFi.addChild(createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_fi", formParameters));
        naytetaankoSuomiPK.addChild(kysytaankoArvosanaPkFi,
                createKielitutkinto("yleinen_kielitutkinto_fi", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", formParameters));

        RelatedQuestionRule kysytaankoArvosanaPkSv = new RelatedQuestionRule(ElementUtil.randomId(), tuoreTodistusPK);
        kysytaankoArvosanaPkSv.addChild(createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_sv", formParameters));
        naytetaankoRuotsiPK.addChild(kysytaankoArvosanaPkSv,
                createKielitutkinto("yleinen_kielitutkinto_sv", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", formParameters));

        RelatedQuestionRule kysytaankoArvosanaPkSe = new RelatedQuestionRule(ElementUtil.randomId(), tuoreTodistusPK);
        kysytaankoArvosanaPkSe.addChild(createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_se", formParameters));
        naytetaankoSaamePK.addChild(kysytaankoArvosanaPkSe,
                createKielitutkinto("yleinen_kielitutkinto_se", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_se", formParameters));

        RelatedQuestionRule kysytaankoArvosanaPkVk = new RelatedQuestionRule(ElementUtil.randomId(), tuoreTodistusPK);
        kysytaankoArvosanaPkVk.addChild(createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_vk", formParameters));
        naytetaankoViittomaPK.addChild(kysytaankoArvosanaPkVk,
                createKielitutkinto("yleinen_kielitutkinto_vk", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_vk", formParameters));

        //YO
        Expr pohjakoulutusOnYO = ExprUtil.atLeastOneValueEqualsToVariable(OppijaConstants.ELEMENT_ID_BASE_EDUCATION, OppijaConstants.YLIOPPILAS);

        Expr suomiOnAidinkieliTaiKouluSuomeksiYO = ExprUtil.atLeastOneVariableEqualsToValue("FI", LANGUAGE_QUESTIONS_YO);
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksiYO = ExprUtil.atLeastOneVariableEqualsToValue("SV", LANGUAGE_QUESTIONS_YO);
        Expr saameOnAidinkieliTaiKouluSaameksiYO = ExprUtil.atLeastOneVariableEqualsToValue("SE", LANGUAGE_QUESTIONS_YO);
        Expr viittomaOnAidinkieliTaiKouluViittomaksiYO = ExprUtil.atLeastOneVariableEqualsToValue("VK", LANGUAGE_QUESTIONS_YO);

        Expr kysytaankoSuomiYO = new And(new And(haettuSuomenkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnYO), new Not(suomiOnAidinkieliTaiKouluSuomeksiYO));
        Expr kysytaankoRuotsiYO = new And(new And(haettuRuotsinkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnYO), new Not(ruotsiOnAidinkieliTaiKouluRuotsiksiYO));
        Expr kysytaankoSaameYO = new And(new And(haettuSaamenkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnYO), new Not(saameOnAidinkieliTaiKouluSaameksiYO));
        Expr kysytaankoViittomaYO = new And(new And(haettuViittomakieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnYO), new Not(viittomaOnAidinkieliTaiKouluViittomaksiYO));


        RelatedQuestionRule naytetaankoSuomiYO = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoSuomiYO);
        RelatedQuestionRule naytetaankoRuotsiYO = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoRuotsiYO);
        RelatedQuestionRule naytetaankoSaameYO = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoSaameYO);
        RelatedQuestionRule naytetaankoViittomaYO = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoViittomaYO);
        Expr tuoreTodistusYo = new Not(ExprUtil.atLeastOneVariableEqualsToValue(String.valueOf(hakukausiVuosi), OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI));

        RelatedQuestionRule kysytaankoArvosanaYoFi = new RelatedQuestionRule(ElementUtil.randomId(), tuoreTodistusYo);
        kysytaankoArvosanaYoFi.addChild(createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_fi", formParameters));
        naytetaankoSuomiYO.addChild(kysytaankoArvosanaYoFi,
                createKielitutkinto("yleinen_kielitutkinto_fi", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", formParameters));

        RelatedQuestionRule kysytaankoArvosanaYoSv = new RelatedQuestionRule(ElementUtil.randomId(), tuoreTodistusYo);
        kysytaankoArvosanaYoSv.addChild(createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_sv", formParameters));
        naytetaankoRuotsiYO.addChild(kysytaankoArvosanaYoSv,
                createKielitutkinto("yleinen_kielitutkinto_sv", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", formParameters));

        RelatedQuestionRule kysytaankoArvosanaYoSe = new RelatedQuestionRule(ElementUtil.randomId(), tuoreTodistusYo);
        kysytaankoArvosanaYoSe.addChild(createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_se", formParameters));
        naytetaankoSaameYO.addChild(kysytaankoArvosanaYoSe,
                createKielitutkinto("yleinen_kielitutkinto_se", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_se", formParameters));

        RelatedQuestionRule kysytaankoArvosanaYoVk = new RelatedQuestionRule(ElementUtil.randomId(), tuoreTodistusYo);
        kysytaankoArvosanaYoVk.addChild(createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_vk", formParameters));
        naytetaankoViittomaYO.addChild(kysytaankoArvosanaYoVk,
                createKielitutkinto("yleinen_kielitutkinto_vk", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_vk", formParameters));


        //KESKEYTTANYT TAI ULKOMAILLA SUORITTANUT
        Expr pohjakoulutusOnKeskUlk = createPohjakoilutusUlkomainenTaiKeskeyttanyt();

        Expr suomiOnAidinkieliKeskUlk = new Equals(new Variable("aidinkieli"), new Value("FI"));
        Expr ruotsiOnAidinkieliKeskUlk = new Equals(new Variable("aidinkieli"), new Value("SV"));
        Expr saameOnAidinkieliKeskUlk = new Equals(new Variable("aidinkieli"), new Value("SE"));
        Expr viittomaOnAidinkieliKeskUlk = new Equals(new Variable("aidinkieli"), new Value("VK"));

        Expr kysytaankoSuomiKeskUlk = new And(new And(haettuSuomenkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnKeskUlk), new Not(suomiOnAidinkieliKeskUlk));
        Expr kysytaankoRuotsiKeskUlk = new And(new And(haettuRuotsinkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnKeskUlk), new Not(ruotsiOnAidinkieliKeskUlk));
        Expr kysytaankoSaameKeskUlk = new And(new And(haettuSaamenkieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnKeskUlk), new Not(saameOnAidinkieliKeskUlk));
        Expr kysytaankoViittomaKeskUlk = new And(new And(haettuViittomakieliseenAmmatilliseenKoulutukseen, pohjakoulutusOnKeskUlk), new Not(viittomaOnAidinkieliKeskUlk));


        RelatedQuestionRule naytetaankoSuomiKeskUlk = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoSuomiKeskUlk);
        RelatedQuestionRule naytetaankoRuotsiKeskUlk = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoRuotsiKeskUlk);
        RelatedQuestionRule naytetaankoSaameKeskUlk = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoSaameKeskUlk);
        RelatedQuestionRule naytetaankoViittomaKeskUlk = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoViittomaKeskUlk);
        naytetaankoSuomiKeskUlk.addChild(createKielitutkinto("yleinen_kielitutkinto_fi", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", formParameters));
        naytetaankoRuotsiKeskUlk.addChild(createKielitutkinto("yleinen_kielitutkinto_sv", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", formParameters));
        naytetaankoSaameKeskUlk.addChild(createKielitutkinto("yleinen_kielitutkinto_se", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_se", formParameters));
        naytetaankoViittomaKeskUlk.addChild(createKielitutkinto("yleinen_kielitutkinto_vk", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_vk", formParameters));


        Expr naytetaankoKielitaitoteema = ExprUtil.reduceToOr(ImmutableList.of(kysytaankoSuomiPK,
                kysytaankoRuotsiPK, kysytaankoSuomiYO, kysytaankoRuotsiYO, kysytaankoSuomiKeskUlk, kysytaankoRuotsiKeskUlk,
                kysytaankoSaamePK, kysytaankoSaameYO, kysytaankoSaameKeskUlk, kysytaankoViittomaPK, kysytaankoViittomaYO,
                kysytaankoViittomaKeskUlk));

        RelatedQuestionRule naytetaankoTeema = new RelatedQuestionRule(ElementUtil.randomId(), naytetaankoKielitaitoteema);
        Element kielitaitokysymyksetTheme = new ThemeBuilder("kielitaito").previewable().formParams(formParameters).build();
        kielitaitokysymyksetTheme.addChild(naytetaankoSuomiPK, naytetaankoRuotsiPK, naytetaankoSuomiYO, naytetaankoRuotsiYO,
                naytetaankoSuomiKeskUlk, naytetaankoRuotsiKeskUlk, naytetaankoSaamePK, naytetaankoSaameYO, naytetaankoSaameKeskUlk,
                naytetaankoViittomaPK, naytetaankoViittomaYO, naytetaankoViittomaKeskUlk);
        naytetaankoTeema.addChild(kielitaitokysymyksetTheme);

        return naytetaankoTeema;
    }

    public static Expr createPohjakoilutusUlkomainenTaiKeskeyttanyt() {
        return ExprUtil.atLeastOneValueEqualsToVariable(OppijaConstants.ELEMENT_ID_BASE_EDUCATION,
                BASE_EDUCATION_KESK_ULK);
    }

    public static Expr haettuAmmatilliseenOppilaitokseenKielella(String educationDegreeLang, int count) {
        Preconditions.checkArgument(count > 0);
        List<Expr> exprs = new ArrayList<Expr>(count);
        for (int i = 1; i <= count; i++) {
            exprs.add(
                    new And(
                            new Equals(new Variable(String.format(OppijaConstants.EDUCATION_VOCATIONAL, i)), new Value(ElementUtil.KYLLA)),
                            new Equals(new Variable(String.format(OppijaConstants.EDUCATION_LANGUAGE, i)), new Value(educationDegreeLang)))
            );
        }
        return ExprUtil.reduceToOr(exprs);
    }

    public static Element createKielitaitokysymyksetThemeSyksy(final FormParameters formParameters) {
        Expr suomiOnAidinkieliTaiKouluSuomeksi = ExprUtil.atLeastOneVariableEqualsToValue("FI", LANGUAGE_QUESTIONS);
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksi = ExprUtil.atLeastOneVariableEqualsToValue("SV", LANGUAGE_QUESTIONS);

        Expr haettuSuomenkieliseenAmmatilliseenKoulutukseen = haettuAmmatilliseenOppilaitokseenKielella(OppijaConstants.EDUCATION_LANGUAGE_FI, 5);
        Expr haettuRuotsinkieliseenAmmatilliseenKoulutukseen = haettuAmmatilliseenOppilaitokseenKielella(OppijaConstants.EDUCATION_LANGUAGE_SV, 5);

        Expr kysytaankoSuomi = new And(haettuSuomenkieliseenAmmatilliseenKoulutukseen, new Not(suomiOnAidinkieliTaiKouluSuomeksi));
        Expr kysytaankoRuotsi = new And(haettuRuotsinkieliseenAmmatilliseenKoulutukseen, new Not(ruotsiOnAidinkieliTaiKouluRuotsiksi));

        Expr naytetaankoKielitaitoteema = new Or(kysytaankoSuomi, kysytaankoRuotsi);

        RelatedQuestionRule naytetaankoTeema = new RelatedQuestionRule(ElementUtil.randomId(), naytetaankoKielitaitoteema);

        Element kielitaitokysymyksetTheme =
                new ThemeBuilder("kielitaito").previewable().formParams(formParameters).build();
        naytetaankoTeema.addChild(kielitaitokysymyksetTheme);

        RelatedQuestionRule naytetaankoSuomi = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoSuomi);
        RelatedQuestionRule naytetaankoRuotsi = new RelatedQuestionRule(ElementUtil.randomId(), kysytaankoRuotsi);

        naytetaankoSuomi.addChild(createKielitutkinto("yleinen_kielitutkinto_fi", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", formParameters));
        naytetaankoRuotsi.addChild(createKielitutkinto("yleinen_kielitutkinto_sv", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", formParameters));
        kielitaitokysymyksetTheme.addChild(naytetaankoSuomi, naytetaankoRuotsi);
        return naytetaankoTeema;
    }

    private static Element createKielitutkinto(final String id, final FormParameters formParameters) {
        I18nText i18NText = createI18NText("form.kielitaito." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, id).replace('_', '.'), formParameters);
        return Radio(id)
                .addOptions(ImmutableList.of(
                        new Option(createI18NText("form.yleinen.kylla", formParameters), KYLLA),
                        new Option(createI18NText("form.yleinen.ei", formParameters), EI)))
                .required()
                .i18nText(i18NText)
                .formParams(formParameters).build();
    }
}
