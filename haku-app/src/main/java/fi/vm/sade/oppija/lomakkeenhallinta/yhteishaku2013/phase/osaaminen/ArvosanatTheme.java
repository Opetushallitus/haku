package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.elements.Text;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.util.OppijaConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;
import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.orStr;

public final class ArvosanatTheme {

    public static final String RELATED_ELEMENT_ID = "POHJAKOULUTUS";
    public static final String ARVOSANAT_THEME_ID = "arvosanatTheme";


    public static Theme createArvosanatTheme(final KoodistoService koodistoService) {
        Map<String, List<Question>> oppiaineetAdditionalQuestions = createOppiaineetAdditionalQuestions();
        Theme arvosanatTheme = new Theme(
                ARVOSANAT_THEME_ID,
                createI18NForm("form.arvosanat.otsikko"),
                oppiaineetAdditionalQuestions,
                true);

        GradeGridTable gradeGridBuilder = new GradeGridTable(koodistoService);

        String pkAnwers = orStr(OppijaConstants.PERUSKOULU, OppijaConstants.OSITTAIN_YKSILOLLISTETTY,
                OppijaConstants.ERITYISOPETUKSEN_YKSILOLLISTETTY, OppijaConstants.YKSILOLLISTETTY
        );
        RelatedQuestionRule relatedQuestionPK = new RelatedQuestionRule("rule_grade_pk", RELATED_ELEMENT_ID,
                pkAnwers, false);
        relatedQuestionPK.addChild(gradeGridBuilder.createGradeGrid("grid_pk", true));
        arvosanatTheme.addChild(relatedQuestionPK);

        String yoAnwers = orStr(OppijaConstants.YLIOPPILAS);
        RelatedQuestionRule relatedQuestionLukio = new RelatedQuestionRule("rule_grade_yo", RELATED_ELEMENT_ID,
                yoAnwers, false);
        relatedQuestionLukio.addChild(gradeGridBuilder.createGradeGrid("grid_yo", false));
        arvosanatTheme.addChild(relatedQuestionLukio);

        RelatedQuestionRule relatedQuestionEiTutkintoa = new RelatedQuestionRule("rule_grade_no", RELATED_ELEMENT_ID,
                orStr("5", "7"), false);
        relatedQuestionEiTutkintoa.addChild(
                new Text("nogradegrid", createI18NForm("form.arvosanat.eiKysyta")));
        arvosanatTheme.addChild(relatedQuestionEiTutkintoa);

        arvosanatTheme.setHelp(createI18NForm("form.arvosanat.help"));
        return arvosanatTheme;
    }


    private static Map<String, List<Question>> createOppiaineetAdditionalQuestions() {
        Map<String, List<Question>> oppiaineMap = new HashMap<String, List<Question>>();
        List<Question> oppiaineList = new ArrayList<Question>();
        oppiaineList.add(new SubjectRow("tietotekniikka",
                createI18NForm("form.oppiaineet.tietotekniikka"), true, false, false, false));
        oppiaineList.add(new SubjectRow("kansantaloustiede",
                createI18NForm("form.oppiaineet.kansantaloustiede"), true, false, false, false));
        oppiaineMap.put("1.2.246.562.14.79893512065", oppiaineList);
        return oppiaineMap;
    }
}
