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
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

public class OsaaminenPhaseYhteishakuKevat {

    private static final String FORM_MESSAGES = "form_messages_yhteishaku_kevat";
    private static final String FORM_ERRORS = "form_errors_yhteishaku_kevat";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_yhteishaku_kevat";

    private static final String[] PREFERENCE_IDS = new String[]{
            "preference1-Koulutus-id-lang",
            "preference2-Koulutus-id-lang",
            "preference3-Koulutus-id-lang",
            "preference4-Koulutus-id-lang",
            "preference5-Koulutus-id-lang"
    };
    private static final String[] LANGUAGE_QUESTIONS_PK = new String[]{
            "aidinkieli",
            "perusopetuksen_kieli"
    };
    private static final String[] LANGUAGE_QUESTIONS_YO = new String[]{
            "aidinkieli",
            "lukion_kieli"
    };
    private static final String[] BASE_EDUCATION_PK = new String[] {
            OppijaConstants.PERUSKOULU,
            OppijaConstants.OSITTAIN_YKSILOLLISTETTY,
            OppijaConstants.ERITYISOPETUKSEN_YKSILOLLISTETTY,
            OppijaConstants.YKSILOLLISTETTY
    };

    private static final String[] BASE_EDUCATION_KESK_ULK = new String[] {
            OppijaConstants.KESKEYTYNYT,
            OppijaConstants.ULKOMAINEN_TUTKINTO
    };

    public static Phase create(final KoodistoService koodistoService) {
        Phase osaaminen = new Phase("osaaminen", createI18NText("form.osaaminen.otsikko", FORM_MESSAGES), false);
        osaaminen.addChild(ArvosanatTheme.createArvosanatTheme(koodistoService, FORM_MESSAGES, FORM_ERRORS, FORM_VERBOSE_HELP));
        osaaminen.addChild(createKielitaitokysymyksetTheme());
        return osaaminen;
    }

    private static Element createKielitaitokysymyksetTheme() {

        //PK
        Expr pohjakoulutusOnPK = atLeastOneValueEqualsToVariable(OppijaConstants.ELEMENT_ID_BASE_EDUCATION, BASE_EDUCATION_PK);

        Expr suomiOnAidinkieliTaiKouluSuomeksiPK = atLeastOneVariableEqualsToValue("FI", LANGUAGE_QUESTIONS_PK);
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksiPK = atLeastOneVariableEqualsToValue("SV", LANGUAGE_QUESTIONS_PK);
        Expr saameOnAidinkieliTaiKouluSaameksiPK = atLeastOneVariableEqualsToValue("SE", LANGUAGE_QUESTIONS_PK);
        Expr viittomaOnAidinkieliTaiKouluViittomaksiPK = atLeastOneVariableEqualsToValue("VK", LANGUAGE_QUESTIONS_PK);

        Expr haettuSuomenkieliseenKoulutukseen = atLeastOneVariableEqualsToValue("FI", PREFERENCE_IDS);
        Expr haettuRuotsinkieliseenKoulutukseen = atLeastOneVariableEqualsToValue("SV", PREFERENCE_IDS);
        Expr haettuSaamenkieliseenKoulutukseen = atLeastOneVariableEqualsToValue("SE", PREFERENCE_IDS);
        Expr haettuViittomakieliseenKoulutukseen = atLeastOneVariableEqualsToValue("VK", PREFERENCE_IDS);

        Expr kysytaankoSuomiPK = new And(new And(haettuSuomenkieliseenKoulutukseen, pohjakoulutusOnPK), new Not(suomiOnAidinkieliTaiKouluSuomeksiPK));
        Expr kysytaankoRuotsiPK = new And(new And(haettuRuotsinkieliseenKoulutukseen, pohjakoulutusOnPK), new Not(ruotsiOnAidinkieliTaiKouluRuotsiksiPK));
        Expr kysytaankoSaamePK = new And(new And(haettuSaamenkieliseenKoulutukseen, pohjakoulutusOnPK), new Not(saameOnAidinkieliTaiKouluSaameksiPK));
        Expr kysytaankoViittomaPK = new And(new And(haettuViittomakieliseenKoulutukseen, pohjakoulutusOnPK), new Not(viittomaOnAidinkieliTaiKouluViittomaksiPK));

        RelatedQuestionComplexRule naytetaankoSuomiPK = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSuomiPK);
        RelatedQuestionComplexRule naytetaankoRuotsiPK = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoRuotsiPK);
        RelatedQuestionComplexRule naytetaankoSaamePK = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSaamePK);
        RelatedQuestionComplexRule naytetaankoViittomaPK = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoViittomaPK);
        naytetaankoSuomiPK.addChild(createKielitutkinto("yleinen_kielitutkinto_fi", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_fi", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoRuotsiPK.addChild(createKielitutkinto("yleinen_kielitutkinto_sv", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_sv", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoSaamePK.addChild(createKielitutkinto("yleinen_kielitutkinto_se", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_se", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_se", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoViittomaPK.addChild(createKielitutkinto("yleinen_kielitutkinto_vk", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_vk", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("peruskoulun_paattotodistus_vahintaan_seitseman_vk", FORM_MESSAGES, FORM_ERRORS));

        //YO
        Expr pohjakoulutusOnYO = atLeastOneValueEqualsToVariable(OppijaConstants.ELEMENT_ID_BASE_EDUCATION, OppijaConstants.YLIOPPILAS);

        Expr suomiOnAidinkieliTaiKouluSuomeksiYO = atLeastOneVariableEqualsToValue("FI", LANGUAGE_QUESTIONS_YO);
        Expr ruotsiOnAidinkieliTaiKouluRuotsiksiYO = atLeastOneVariableEqualsToValue("SV", LANGUAGE_QUESTIONS_YO);
        Expr saameOnAidinkieliTaiKouluSaameksiYO = atLeastOneVariableEqualsToValue("SE", LANGUAGE_QUESTIONS_YO);
        Expr viittomaOnAidinkieliTaiKouluViittomaksiYO = atLeastOneVariableEqualsToValue("VK", LANGUAGE_QUESTIONS_YO);

        Expr kysytaankoSuomiYO = new And(new And(haettuSuomenkieliseenKoulutukseen, pohjakoulutusOnYO), new Not(suomiOnAidinkieliTaiKouluSuomeksiYO));
        Expr kysytaankoRuotsiYO = new And(new And(haettuRuotsinkieliseenKoulutukseen, pohjakoulutusOnYO), new Not(ruotsiOnAidinkieliTaiKouluRuotsiksiYO));
        Expr kysytaankoSaameYO = new And(new And(haettuSaamenkieliseenKoulutukseen, pohjakoulutusOnYO), new Not(saameOnAidinkieliTaiKouluSaameksiYO));
        Expr kysytaankoViittomaYO = new And(new And(haettuViittomakieliseenKoulutukseen, pohjakoulutusOnYO), new Not(viittomaOnAidinkieliTaiKouluViittomaksiYO));


        RelatedQuestionComplexRule naytetaankoSuomiYO = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSuomiYO);
        RelatedQuestionComplexRule naytetaankoRuotsiYO = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoRuotsiYO);
        RelatedQuestionComplexRule naytetaankoSaameYO = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSaameYO);
        RelatedQuestionComplexRule naytetaankoViittomaYO = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoViittomaYO);
        naytetaankoSuomiYO.addChild(createKielitutkinto("yleinen_kielitutkinto_fi", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_fi", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoRuotsiYO.addChild(createKielitutkinto("yleinen_kielitutkinto_sv", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_sv", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoSaameYO.addChild(createKielitutkinto("yleinen_kielitutkinto_se", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_se", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_se", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoViittomaYO.addChild(createKielitutkinto("yleinen_kielitutkinto_vk", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_vk", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("lukion_paattotodistus_vahintaan_seitseman_vk", FORM_MESSAGES, FORM_ERRORS));


        //KESKEYTTANYT TAI ULKOMAILLA SUORITTANUT
        Expr pohjakoulutusOnKeskUlk = atLeastOneValueEqualsToVariable(OppijaConstants.ELEMENT_ID_BASE_EDUCATION,
                BASE_EDUCATION_KESK_ULK);

        Expr suomiOnAidinkieliKeskUlk = new Equals(new Variable("aidinkieli"), new Value("FI"));
        Expr ruotsiOnAidinkieliKeskUlk = new Equals(new Variable("aidinkieli"), new Value("SV"));
        Expr saameOnAidinkieliKeskUlk = new Equals(new Variable("aidinkieli"), new Value("SE"));
        Expr viittomaOnAidinkieliKeskUlk = new Equals(new Variable("aidinkieli"), new Value("VK"));

        Expr kysytaankoSuomiKeskUlk = new And(new And(haettuSuomenkieliseenKoulutukseen, pohjakoulutusOnKeskUlk), new Not(suomiOnAidinkieliKeskUlk));
        Expr kysytaankoRuotsiKeskUlk = new And(new And(haettuRuotsinkieliseenKoulutukseen, pohjakoulutusOnKeskUlk), new Not(ruotsiOnAidinkieliKeskUlk));
        Expr kysytaankoSaameKeskUlk = new And(new And(haettuSaamenkieliseenKoulutukseen, pohjakoulutusOnKeskUlk), new Not(saameOnAidinkieliKeskUlk));
        Expr kysytaankoViittomaKeskUlk = new And(new And(haettuViittomakieliseenKoulutukseen, pohjakoulutusOnKeskUlk), new Not(viittomaOnAidinkieliKeskUlk));


        RelatedQuestionComplexRule naytetaankoSuomiKeskUlk = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSuomiKeskUlk);
        RelatedQuestionComplexRule naytetaankoRuotsiKeskUlk = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoRuotsiKeskUlk);
        RelatedQuestionComplexRule naytetaankoSaameKeskUlk = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoSaameKeskUlk);
        RelatedQuestionComplexRule naytetaankoViittomaKeskUlk = new RelatedQuestionComplexRule(ElementUtil.randomId(), kysytaankoViittomaKeskUlk);
        naytetaankoSuomiKeskUlk.addChild(createKielitutkinto("yleinen_kielitutkinto_fi", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_fi", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoRuotsiKeskUlk.addChild(createKielitutkinto("yleinen_kielitutkinto_sv", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_sv", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoSaameKeskUlk.addChild(createKielitutkinto("yleinen_kielitutkinto_se", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_se", FORM_MESSAGES, FORM_ERRORS));
        naytetaankoViittomaKeskUlk.addChild(createKielitutkinto("yleinen_kielitutkinto_vk", FORM_MESSAGES, FORM_ERRORS),
                createKielitutkinto("valtionhallinnon_kielitutkinto_vk", FORM_MESSAGES, FORM_ERRORS));


        Expr naytetaankoKielitaitoteema = new Or(new Or(new Or(new Or(new Or(new Or(new Or(new Or(new Or(new Or(new Or(kysytaankoSuomiPK,
                kysytaankoRuotsiPK), kysytaankoSuomiYO), kysytaankoRuotsiYO), kysytaankoSuomiKeskUlk), kysytaankoRuotsiKeskUlk),
                kysytaankoSaamePK), kysytaankoSaameYO), kysytaankoSaameKeskUlk), kysytaankoViittomaPK), kysytaankoViittomaYO),
                kysytaankoViittomaKeskUlk);

        RelatedQuestionComplexRule naytetaankoTeema = new RelatedQuestionComplexRule(ElementUtil.randomId(), naytetaankoKielitaitoteema);
        Theme kielitaitokysymyksetTheme =
                new Theme("KielitaitokysymyksetTheme", ElementUtil.createI18NText("form.kielitaito.otsikko", FORM_MESSAGES), true);
        kielitaitokysymyksetTheme.addChild(naytetaankoSuomiPK, naytetaankoRuotsiPK, naytetaankoSuomiYO, naytetaankoRuotsiYO,
                naytetaankoSuomiKeskUlk, naytetaankoRuotsiKeskUlk, naytetaankoSaamePK, naytetaankoSaameYO, naytetaankoSaameKeskUlk,
                naytetaankoViittomaPK, naytetaankoViittomaYO, naytetaankoViittomaKeskUlk);
        ElementUtil.setVerboseHelp(kielitaitokysymyksetTheme, "form.kielitaito.otsikko.verboseHelp", FORM_VERBOSE_HELP);
        naytetaankoTeema.addChild(kielitaitokysymyksetTheme);

        return naytetaankoTeema;
    }

    private static Radio createKielitutkinto(final String id, final String formMessages, final String formErrors) {
        Radio radio = new Radio(id,
                createI18NText("form.kielitaito." +
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, id).replace('_', '.'), formMessages));
        addDefaultTrueFalseOptions(radio, formMessages);
        addRequiredValidator(radio, formErrors);
        return radio;
    }
}
