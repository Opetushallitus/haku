package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.orStr;

public final class ArvosanatTheme {

    public static final String RELATED_ELEMENT_ID = "POHJAKOULUTUS";
    public static final String ARVOSANAT_THEME_ID = "arvosanatTheme";


    private ArvosanatTheme() {
    }

    public static Theme createArvosanatTheme(final KoodistoService koodistoService, final String formMessages,
                                             final String formErrors, final String verboseHelps) {
        Theme arvosanatTheme = new Theme(
                ARVOSANAT_THEME_ID,
                createI18NText("form.arvosanat.otsikko", formMessages),
                true);

        GradesTable gradesTablePK = new GradesTable(koodistoService, true, formMessages, formErrors, verboseHelps);
        GradesTable gradesTableYO = new GradesTable(koodistoService, false, formMessages, formErrors, verboseHelps);

        String pkAnwers = orStr(OppijaConstants.PERUSKOULU, OppijaConstants.OSITTAIN_YKSILOLLISTETTY,
                OppijaConstants.ERITYISOPETUKSEN_YKSILOLLISTETTY, OppijaConstants.YKSILOLLISTETTY
        );
        RelatedQuestionRule relatedQuestionPK = new RelatedQuestionRule("rule_grade_pk", RELATED_ELEMENT_ID,
                pkAnwers, false);
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formMessages, formErrors, verboseHelps);
        grid_pk.setHelp(createI18NText("form.arvosanat.help", formMessages));
        relatedQuestionPK.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPK);

        String yoAnwers = orStr(OppijaConstants.YLIOPPILAS);
        RelatedQuestionRule relatedQuestionLukio = new RelatedQuestionRule("rule_grade_yo", RELATED_ELEMENT_ID,
                yoAnwers, false);
        GradeGrid grid_yo = gradesTableYO.createGradeGrid("grid_yo", formMessages, formErrors, verboseHelps);
        grid_yo.setHelp(createI18NText("form.arvosanat.help", formMessages));
        relatedQuestionLukio.addChild(grid_yo);
        arvosanatTheme.addChild(relatedQuestionLukio);

        RelatedQuestionRule relatedQuestionEiTutkintoa = new RelatedQuestionRule("rule_grade_no", RELATED_ELEMENT_ID,
                orStr("5", OppijaConstants.KESKEYTYNYT, OppijaConstants.ULKOMAINEN_TUTKINTO), false);
        relatedQuestionEiTutkintoa.addChild(
                new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta", formMessages)));
        arvosanatTheme.addChild(relatedQuestionEiTutkintoa);

        ElementUtil.setVerboseHelp(arvosanatTheme, "form.arvosanat.otsikko.verboseHelp", verboseHelps);
        return arvosanatTheme;
    }

}
