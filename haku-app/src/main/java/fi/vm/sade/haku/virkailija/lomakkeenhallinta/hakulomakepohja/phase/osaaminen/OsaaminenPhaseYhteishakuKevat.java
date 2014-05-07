/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.*;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public class OsaaminenPhaseYhteishakuKevat {

    public static final String RELATED_ELEMENT_ID = "POHJAKOULUTUS";
    public static final String ARVOSANAT_THEME_ID = "arvosanatTheme";


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

    public static Phase create(final FormParameters formParameters) {
        Phase osaaminen = new Phase("osaaminen", createI18NText("form.osaaminen.otsikko", formParameters), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));
        osaaminen.addChild(createArvosanatTheme(formParameters));
        osaaminen.addChild(createKielitaitokysymyksetTheme(formParameters));
        return osaaminen;
    }

    private static Element createKielitaitokysymyksetTheme(final FormParameters formParameters) {

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

        RelatedQuestionComplexRule naytetaankoSuomiPK = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSuomiPK);
        RelatedQuestionComplexRule naytetaankoRuotsiPK = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoRuotsiPK);
        RelatedQuestionComplexRule naytetaankoSaamePK = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSaamePK);
        RelatedQuestionComplexRule naytetaankoViittomaPK = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoViittomaPK);


        Integer hakukausiVuosi = formParameters.getApplicationSystem().getHakukausiVuosi();
        Expr tuoreTodistusPK = new Not(ExprUtil.atLeastOneVariableEqualsToValue(String.valueOf(hakukausiVuosi), OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI));

        RelatedQuestionComplexRule kysytaankoArvosanaPkFi = new RelatedQuestionComplexRule(ElementUtil.randomId(), tuoreTodistusPK);
        kysytaankoArvosanaPkFi.addChild(createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_fi", formParameters));
        naytetaankoSuomiPK.addChild(kysytaankoArvosanaPkFi,
                createKielitutkinto("yleinen_kielitutkinto_fi", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", formParameters));

        RelatedQuestionComplexRule kysytaankoArvosanaPkSv = new RelatedQuestionComplexRule(ElementUtil.randomId(), tuoreTodistusPK);
        kysytaankoArvosanaPkSv.addChild(createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_sv", formParameters));
        naytetaankoRuotsiPK.addChild(kysytaankoArvosanaPkSv,
                createKielitutkinto("yleinen_kielitutkinto_sv", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", formParameters));

        RelatedQuestionComplexRule kysytaankoArvosanaPkSe = new RelatedQuestionComplexRule(ElementUtil.randomId(), tuoreTodistusPK);
        kysytaankoArvosanaPkSe.addChild(createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_se", formParameters));
        naytetaankoSaamePK.addChild(kysytaankoArvosanaPkSe,
                createKielitutkinto("yleinen_kielitutkinto_se", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_se", formParameters));

        RelatedQuestionComplexRule kysytaankoArvosanaPkVk = new RelatedQuestionComplexRule(ElementUtil.randomId(), tuoreTodistusPK);
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


        RelatedQuestionComplexRule naytetaankoSuomiYO = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSuomiYO);
        RelatedQuestionComplexRule naytetaankoRuotsiYO = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoRuotsiYO);
        RelatedQuestionComplexRule naytetaankoSaameYO = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSaameYO);
        RelatedQuestionComplexRule naytetaankoViittomaYO = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoViittomaYO);
        Expr tuoreTodistusYo = new Not(ExprUtil.atLeastOneVariableEqualsToValue(String.valueOf(hakukausiVuosi), OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI));

        RelatedQuestionComplexRule kysytaankoArvosanaYoFi = new RelatedQuestionComplexRule(ElementUtil.randomId(), tuoreTodistusYo);
        kysytaankoArvosanaYoFi.addChild(createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_fi", formParameters));
        naytetaankoSuomiYO.addChild(kysytaankoArvosanaYoFi,
                createKielitutkinto("yleinen_kielitutkinto_fi", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", formParameters));

        RelatedQuestionComplexRule kysytaankoArvosanaYoSv = new RelatedQuestionComplexRule(ElementUtil.randomId(), tuoreTodistusYo);
        kysytaankoArvosanaYoSv.addChild(createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_sv", formParameters));
        naytetaankoRuotsiYO.addChild(kysytaankoArvosanaYoSv,
                createKielitutkinto("yleinen_kielitutkinto_sv", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", formParameters));

        RelatedQuestionComplexRule kysytaankoArvosanaYoSe = new RelatedQuestionComplexRule(ElementUtil.randomId(), tuoreTodistusYo);
        kysytaankoArvosanaYoSe.addChild(createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_se", formParameters));
        naytetaankoSaameYO.addChild(kysytaankoArvosanaYoSe,
                createKielitutkinto("yleinen_kielitutkinto_se", formParameters),
                createKielitutkinto("valtionhallinnon_kielitutkinto_se", formParameters));

        RelatedQuestionComplexRule kysytaankoArvosanaYoVk = new RelatedQuestionComplexRule(ElementUtil.randomId(), tuoreTodistusYo);
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


        RelatedQuestionComplexRule naytetaankoSuomiKeskUlk = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSuomiKeskUlk);
        RelatedQuestionComplexRule naytetaankoRuotsiKeskUlk = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoRuotsiKeskUlk);
        RelatedQuestionComplexRule naytetaankoSaameKeskUlk = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSaameKeskUlk);
        RelatedQuestionComplexRule naytetaankoViittomaKeskUlk = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoViittomaKeskUlk);
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

        RelatedQuestionComplexRule naytetaankoTeema = new RelatedQuestionComplexRule(ElementUtil.randomId(), naytetaankoKielitaitoteema);
        Theme kielitaitokysymyksetTheme =
                new Theme("KielitaitokysymyksetTheme", ElementUtil.createI18NText("form.kielitaito.otsikko", formParameters), true);
        kielitaitokysymyksetTheme.addChild(naytetaankoSuomiPK, naytetaankoRuotsiPK, naytetaankoSuomiYO, naytetaankoRuotsiYO,
                naytetaankoSuomiKeskUlk, naytetaankoRuotsiKeskUlk, naytetaankoSaamePK, naytetaankoSaameYO, naytetaankoSaameKeskUlk,
                naytetaankoViittomaPK, naytetaankoViittomaYO, naytetaankoViittomaKeskUlk);
        ElementUtil.setVerboseHelp(kielitaitokysymyksetTheme, "form.kielitaito.otsikko.verboseHelp", formParameters);
        naytetaankoTeema.addChild(kielitaitokysymyksetTheme);

        return naytetaankoTeema;
    }

    public static Expr createPohjakoilutusUlkomainenTaiKeskeyttanyt() {
        return ExprUtil.atLeastOneValueEqualsToVariable(OppijaConstants.ELEMENT_ID_BASE_EDUCATION,
                BASE_EDUCATION_KESK_ULK);
    }

    private static Radio createKielitutkinto(final String id, final FormParameters formParameters) {
        Radio radio = new Radio(id,
                createI18NText("form.kielitaito." +
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, id).replace('_', '.'), formParameters.getFormMessagesBundle()));
        addDefaultTrueFalseOptions(radio, formParameters);
        addRequiredValidator(radio, formParameters);
        return radio;
    }

    private static Theme createArvosanatTheme(final FormParameters formParameters) {
        Theme arvosanatTheme = new Theme(
                ARVOSANAT_THEME_ID,
                createI18NText("form.arvosanat.otsikko", formParameters.getFormMessagesBundle()),
                true);
        ElementUtil.setVerboseHelp(arvosanatTheme, "form.arvosanat.otsikko.verboseHelp", formParameters);

        GradesTable gradesTablePK = new GradesTable(true, formParameters);
        GradesTable gradesTableYO = new GradesTable(false, formParameters);

        // Peruskoulu
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formParameters);
        grid_pk.setHelp(createI18NText("form.arvosanat.help", formParameters.getFormMessagesBundle()));
        Integer hakukausiVuosi = formParameters.getApplicationSystem().getHakukausiVuosi();
        Expr kysyArvosanatPk = new Or(
                new And(
                        new Not(
                                new Equals(
                                        new Variable(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI),
                                        new Value(String.valueOf(hakukausiVuosi)))),
                        ExprUtil.atLeastOneValueEqualsToVariable(RELATED_ELEMENT_ID, OppijaConstants.PERUSKOULU,
                                OppijaConstants.ALUEITTAIN_YKSILOLLISTETTY, OppijaConstants.YKSILOLLISTETTY,
                                OppijaConstants.OSITTAIN_YKSILOLLISTETTY)),
                new Regexp("_meta_grades_transferred_pk", "true"));
        RelatedQuestionComplexRule relatedQuestionPk = new RelatedQuestionComplexRule("rule_grade_pk", kysyArvosanatPk);
        relatedQuestionPk.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPk);

        // Ylioppilaat
        GradeGrid grid_yo = gradesTableYO.createGradeGrid("grid_yo", formParameters);
        grid_yo.setHelp(createI18NText("form.arvosanat.help", formParameters.getFormMessagesBundle()));
        Expr kysyArvosanatLukio = new Or(
                new And(
                        new Not(
                                new Equals(
                                        new Variable(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI),
                                        new Value(String.valueOf(hakukausiVuosi)))),
                        ExprUtil.atLeastOneValueEqualsToVariable(RELATED_ELEMENT_ID, OppijaConstants.YLIOPPILAS)),
                new Regexp("_meta_grades_transferred_lk", "true"));
        RelatedQuestionComplexRule relatedQuestionYo = new RelatedQuestionComplexRule("rule_grade_yo", kysyArvosanatLukio);
        relatedQuestionYo.addChild(grid_yo);
        arvosanatTheme.addChild(relatedQuestionYo);

        // Ei arvosanoja
        RelatedQuestionComplexRule eiNaytetaPk = new RelatedQuestionComplexRule("rule_grade_no_pk",
                new Equals(new Variable(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI), new Value(String.valueOf(hakukausiVuosi))));
        eiNaytetaPk.addChild(new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta.pk", formParameters.getFormMessagesBundle())));
        arvosanatTheme.addChild(eiNaytetaPk);

        RelatedQuestionComplexRule eiNaytetaYo = new RelatedQuestionComplexRule("rule_grade_no_yo",
                new Equals(new Variable("lukioPaattotodistusVuosi"), new Value(String.valueOf(hakukausiVuosi))));
        eiNaytetaYo.addChild(new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta.yo", formParameters.getFormMessagesBundle())));
        arvosanatTheme.addChild(eiNaytetaYo);

        RelatedQuestionComplexRule eiNayteta = new RelatedQuestionComplexRule("rule_grade_no",
                ExprUtil.atLeastOneValueEqualsToVariable(RELATED_ELEMENT_ID, "5", OppijaConstants.KESKEYTYNYT, OppijaConstants.ULKOMAINEN_TUTKINTO));
        eiNayteta.addChild(new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta", formParameters.getFormMessagesBundle())));
        arvosanatTheme.addChild(eiNayteta);

        return arvosanatTheme;

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
}
