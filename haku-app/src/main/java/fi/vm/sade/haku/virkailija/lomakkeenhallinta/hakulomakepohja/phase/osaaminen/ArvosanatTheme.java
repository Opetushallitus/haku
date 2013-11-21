package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;

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
        ElementUtil.setVerboseHelp(arvosanatTheme, "form.arvosanat.otsikko.verboseHelp", verboseHelps);

        GradesTable gradesTablePK = new GradesTable(koodistoService, true, formMessages, formErrors, verboseHelps);
        GradesTable gradesTableYO = new GradesTable(koodistoService, false, formMessages, formErrors, verboseHelps);

        // Peruskoulu
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formMessages, formErrors, verboseHelps);
        grid_pk.setHelp(createI18NText("form.arvosanat.help", formMessages));
        Expr vanhaPkTodistus = new And(
                new Not(new Equals(new Variable("PK_PAATTOTODISTUSVUOSI"), new Value("2014"))),
                atLeastOneValueEqualsToVariable(RELATED_ELEMENT_ID, OppijaConstants.PERUSKOULU,
                        OppijaConstants.ERITYISOPETUKSEN_YKSILOLLISTETTY, OppijaConstants.YKSILOLLISTETTY, OppijaConstants.OSITTAIN_YKSILOLLISTETTY));
        RelatedQuestionComplexRule relatedQuestionPk = new RelatedQuestionComplexRule("rule_grade_pk", vanhaPkTodistus);
        relatedQuestionPk.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPk);

        // Lukio
        String yoAnwers = orStr(OppijaConstants.YLIOPPILAS);
        RelatedQuestionRule relatedQuestionLukio = new RelatedQuestionRule("rule_grade_yo", RELATED_ELEMENT_ID,
                yoAnwers, false);
        GradeGrid grid_yo = gradesTableYO.createGradeGrid("grid_yo", formMessages, formErrors, verboseHelps);
        grid_yo.setHelp(createI18NText("form.arvosanat.help", formMessages));
        relatedQuestionLukio.addChild(grid_yo);
        arvosanatTheme.addChild(relatedQuestionLukio);

        // Ei kysytä arvosanoja
        Expr tuorePkTodistus = new Equals(new Variable("PK_PAATTOTODISTUSVUOSI"), new Value("2014"));
        Expr eiTutkintoa = atLeastOneValueEqualsToVariable(RELATED_ELEMENT_ID, "5", OppijaConstants.KESKEYTYNYT, OppijaConstants.ULKOMAINEN_TUTKINTO);

        RelatedQuestionComplexRule eiNayteta = new RelatedQuestionComplexRule("rule_grade_no", new Or(tuorePkTodistus, eiTutkintoa));
        eiNayteta.addChild(new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta", formMessages)));

        arvosanatTheme.addChild(eiNayteta);

        return arvosanatTheme;

    }

}
