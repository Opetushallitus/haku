package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.elements.Text;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NForm;
import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.orStr;

public final class ArvosanatTheme {

    public static final String RELATED_ELEMENT_ID = "POHJAKOULUTUS";
    public static final String ARVOSANAT_THEME_ID = "arvosanatTheme";


    private ArvosanatTheme() {
    }

    public static Theme createArvosanatTheme(final KoodistoService koodistoService) {
        Theme arvosanatTheme = new Theme(
                ARVOSANAT_THEME_ID,
                createI18NForm("form.arvosanat.otsikko"),
                true);

        GradesTable gradesTablePK = new GradesTable(koodistoService, true);
        GradesTable gradesTableYO = new GradesTable(koodistoService, false);

        String pkAnwers = orStr(OppijaConstants.PERUSKOULU, OppijaConstants.OSITTAIN_YKSILOLLISTETTY,
                OppijaConstants.ERITYISOPETUKSEN_YKSILOLLISTETTY, OppijaConstants.YKSILOLLISTETTY
        );
        RelatedQuestionRule relatedQuestionPK = new RelatedQuestionRule("rule_grade_pk", RELATED_ELEMENT_ID,
                pkAnwers, false);
        relatedQuestionPK.addChild(gradesTablePK.createGradeGrid("grid_pk"));
        arvosanatTheme.addChild(relatedQuestionPK);

        String yoAnwers = orStr(OppijaConstants.YLIOPPILAS);
        RelatedQuestionRule relatedQuestionLukio = new RelatedQuestionRule("rule_grade_yo", RELATED_ELEMENT_ID,
                yoAnwers, false);
        relatedQuestionLukio.addChild(gradesTableYO.createGradeGrid("grid_yo"));
        arvosanatTheme.addChild(relatedQuestionLukio);

        RelatedQuestionRule relatedQuestionEiTutkintoa = new RelatedQuestionRule("rule_grade_no", RELATED_ELEMENT_ID,
                orStr("5", OppijaConstants.KESKEYTYNYT, OppijaConstants.ULKOMAINEN_TUTKINTO), false);
        relatedQuestionEiTutkintoa.addChild(
                new Text("nogradegrid", createI18NForm("form.arvosanat.eiKysyta")));
        arvosanatTheme.addChild(relatedQuestionEiTutkintoa);

        arvosanatTheme.setHelp(createI18NForm("form.arvosanat.help"));
        return arvosanatTheme;
    }

}
