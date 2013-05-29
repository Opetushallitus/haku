package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.hakutoiveet;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.custom.Popup;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceTable;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.FormConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.*;

public class HakutoiveetPhase {
    public static final String DISCRETIONARY_EDUCATION_DEGREE = "32";
    public static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";
    private final Phase hakutoiveet;
    private final Theme hakutoiveetRyhma;

    public HakutoiveetPhase(final String aoidAdditionalQuestion) {

        // Hakutoiveet
        hakutoiveet = new Phase(HAKUTOIVEET_PHASE_ID, createI18NForm("form.hakutoiveet.otsikko"), false);

        hakutoiveetRyhma = createHakutoiveetRyhma(aoidAdditionalQuestion);
        hakutoiveet.addChild(hakutoiveetRyhma);
        createHakutoiveet(hakutoiveetRyhma);
    }

    private static Theme createHakutoiveetRyhma(final String aoidAdditionalQuestion) {
        final String elementIdPrefix = aoidAdditionalQuestion.replace('.', '_');

        Radio radio3 = new Radio(
                elementIdPrefix + "_additional_question_1",
                createI18NForm("form.hakutoiveet.paasykoe.tuloksiaSaaKayttaa"));
        radio3.addOption(elementIdPrefix + "_q1_option_1",
                createI18NForm("form.hakutoiveet.paasykoe.eiOsallistunut"), "q1_option_1");
        radio3.addOption(elementIdPrefix + "_q1_option_2",
                createI18NForm("form.hakutoiveet.paasykoe.eiSaaKayttaa"), "q1_option_2");
        radio3.addOption(elementIdPrefix + "_q1_option_3",
                createI18NForm("form.hakutoiveet.paasykoe.saaKayttaa"), "q1_option_3");

        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(radio3);

        Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();
        lisakysymysMap.put(aoidAdditionalQuestion, lisakysymysList);

        return new Theme("hakutoiveetGrp", createI18NForm("form.hakutoiveet.otsikko"), lisakysymysMap, true);
    }

    private static void createHakutoiveet(Theme hakutoiveetRyhma) {
        hakutoiveetRyhma
                .setHelp(createI18NForm("form.hakutoiveet.help"));

        PreferenceTable preferenceTable =
                new PreferenceTable("preferencelist", createI18NForm("form.hakutoiveet.otsikko"), "Ylös", "Alas");

        PreferenceRow pr1 = createI18NPreferenceRow("preference1", "1");
        pr1.addAttribute("required", "required");
        PreferenceRow pr2 = createI18NPreferenceRow("preference2", "2");
        PreferenceRow pr3 = createI18NPreferenceRow("preference3", "3");
        PreferenceRow pr4 = createI18NPreferenceRow("preference4", "4");
        PreferenceRow pr5 = createI18NPreferenceRow("preference5", "5");
        preferenceTable.addChild(pr1);
        preferenceTable.addChild(pr2);
        preferenceTable.addChild(pr3);
        preferenceTable.addChild(pr4);
        preferenceTable.addChild(pr5);
        preferenceTable.setVerboseHelp(FormConstants.VERBOSE_HELP);

        hakutoiveetRyhma.addChild(preferenceTable);
    }

    public static PreferenceRow createI18NPreferenceRow(final String id, final String title) {
        PreferenceRow pr = new PreferenceRow(id,
                createI18NForm("form.hakutoiveet.hakutoive", title),
                createI18NForm("form.yleinen.tyhjenna"),
                createI18NForm("form.hakutoiveet.koulutus"),
                createI18NForm("form.hakutoiveet.opetuspiste"),
                createI18NForm("form.hakutoiveet.sisaltyvatKoulutusohjelmat"),
                "Valitse koulutus");

        pr.addChild(createDiscretionaryQuestionsAndRules(id),
                createSoraQuestions(id),
                createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(id));
        return pr;
    }

    private static Element createDiscretionaryQuestionsAndRules(final String index) {
        Radio discretionary = new Radio(index + "-discretionary", createI18NForm("form.hakutoiveet.harkinnanvarainen"));
        addDefaultTrueFalseOptions(discretionary);
        setRequired(discretionary);

        DropdownSelect discretionaryFollowUp = new DropdownSelect(discretionary.getId() + "-follow-up",
                createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu"), null);
        discretionaryFollowUp.addOption(discretionaryFollowUp.getId() + "oppimisvaikudet",
                createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu.oppimisvaikeudet"), "oppimisvaikudet");
        discretionaryFollowUp.addOption(discretionaryFollowUp.getId() + "sosiaalisetsyyt",
                createI18NForm("form.hakutoiveet.harkinnanvarainen.perustelu.sosiaaliset"), "sosiaalisetsyyt");

        RelatedQuestionRule discretionaryFollowUpRule = new RelatedQuestionRule(index + "-discretionary-follow-up-rule",
                ImmutableList.of(discretionary.getId()), Boolean.TRUE.toString().toLowerCase(), false);
        discretionaryFollowUpRule.addChild(discretionaryFollowUp);


        discretionary.addChild(discretionaryFollowUpRule);

        RelatedQuestionRule discretionaryRule = new RelatedQuestionRule(index + "-discretionary-rule",
                ImmutableList.of(index + "-Koulutus-educationDegree"), DISCRETIONARY_EDUCATION_DEGREE, false);
        discretionaryRule.addChild(discretionary);

        return discretionaryRule;

    }

    public static Element createSoraQuestions(final String index) {
        // sora-kysymykset

        RelatedQuestionRule hasSora = new RelatedQuestionRule(index + "_sora_rule",
                ImmutableList.of(index + "-Koulutus-id-sora"), ElementUtil.KYLLA, false);

        Radio sora1 = new Radio(index + "_sora_terveys", createI18NForm("form.sora.terveys"));
        sora1.addOption(ElementUtil.EI, createI18NForm("form.yleinen.ei"), ElementUtil.EI);
        sora1.addOption(ElementUtil.KYLLA, createI18NForm("form.sora.kylla"), ElementUtil.KYLLA);
        ElementUtil.setRequired(sora1);
        sora1.setPopup(new Popup("sora-popup", createI18NForm("form.hakutoiveet.terveydentilavaatimukset.otsikko")));

        Radio sora2 = new Radio(index + "_sora_oikeudenMenetys", createI18NForm("form.sora.oikeudenMenetys"));
        sora2.addOption(ElementUtil.EI, createI18NForm("form.yleinen.ei"), ElementUtil.EI);
        sora2.addOption(ElementUtil.KYLLA, createI18NForm("form.sora.kylla"), ElementUtil.KYLLA);
        ElementUtil.setRequired(sora2);

        hasSora.addChild(sora1, sora2);
        return hasSora;
    }

    public static Element createUrheilijanAmmatillisenKoulutuksenLisakysymysAndRule(final String index) {

        Radio radio = new Radio(index + "_urheilijan_ammatillisen_koulutuksen_lisakysymys",
                createI18NForm("form.hakutoiveet.urheilijan.ammatillisen.koulutuksen.lisakysymys"));
        addDefaultTrueFalseOptions(radio);
        setRequired(radio);

        RelatedQuestionRule hasQuestion = new RelatedQuestionRule(radio.getId() + "_related_question_rule",
                ImmutableList.of(index + "-Koulutus-id-athlete"), ElementUtil.KYLLA, false);

        hasQuestion.addChild(radio);

        return hasQuestion;
    }

    public Phase getHakutoiveet() {
        return hakutoiveet;
    }

}
