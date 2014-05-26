package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class ArvosanatTheme {

    public static final String POHJAKOULUTUS_ID = "POHJAKOULUTUS";
    public static final String RELATED_ELEMENT_ID = "POHJAKOULUTUS";

    private ArvosanatTheme() {
    }

    public static Element createArvosanatTheme(final FormParameters formParameters) {
        Element arvosanatTheme = new ThemeBuilder("arvosanat").previewable().build(formParameters);


        RelatedQuestionComplexRule relatedQuestionPK = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        GradesTable gradesTablePK = new GradesTable(true, formParameters);
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formParameters);
        grid_pk.setHelp(createI18NText("form.arvosanat.help", formParameters));
        setHelp(grid_pk, "form.arvosanat.help", formParameters);
        relatedQuestionPK.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPK);

        if (!formParameters.isPervako()) {
            RelatedQuestionComplexRule relatedQuestionLukio = createVarEqualsToValueRule(POHJAKOULUTUS_ID, YLIOPPILAS);
            GradesTable gradesTableYO = new GradesTable(false, formParameters);
            GradeGrid grid_yo = gradesTableYO.createGradeGrid("grid_yo", formParameters);
            setHelp(grid_yo, "form.arvosanat.help", formParameters);
            relatedQuestionLukio.addChild(grid_yo);
            arvosanatTheme.addChild(relatedQuestionLukio);

            RelatedQuestionComplexRule relatedQuestionEiTutkintoa = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                    KESKEYTYNYT, ULKOMAINEN_TUTKINTO);

            relatedQuestionEiTutkintoa.addChild(
                    new Text("nogradegrid", createI18NText("form.arvosanat.eikysyta", formParameters)));
            arvosanatTheme.addChild(relatedQuestionEiTutkintoa);
        }
        return arvosanatTheme;
    }

    public static Element createArvosanatThemeKevat(final FormParameters formParameters) {
        Element arvosanatTheme = new ThemeBuilder("arvosanat").previewable().build(formParameters);

        GradesTable gradesTablePK = new GradesTable(true, formParameters);
        GradesTable gradesTableYO = new GradesTable(false, formParameters);

        // Peruskoulu
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formParameters);
        grid_pk.setHelp(createI18NText("form.arvosanat.help", formParameters));
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
        grid_yo.setHelp(createI18NText("form.arvosanat.help", formParameters));
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
        eiNaytetaPk.addChild(new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta.pk", formParameters)));
        arvosanatTheme.addChild(eiNaytetaPk);

        RelatedQuestionComplexRule eiNaytetaYo = new RelatedQuestionComplexRule("rule_grade_no_yo",
                new Equals(new Variable("lukioPaattotodistusVuosi"), new Value(String.valueOf(hakukausiVuosi))));
        eiNaytetaYo.addChild(new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta.yo", formParameters)));
        arvosanatTheme.addChild(eiNaytetaYo);

        RelatedQuestionComplexRule eiNayteta = new RelatedQuestionComplexRule("rule_grade_no",
                ExprUtil.atLeastOneValueEqualsToVariable(RELATED_ELEMENT_ID, "5", OppijaConstants.KESKEYTYNYT, OppijaConstants.ULKOMAINEN_TUTKINTO));
        eiNayteta.addChild(new Text("nogradegrid", createI18NText("form.arvosanat.eikysyta", formParameters)));
        arvosanatTheme.addChild(eiNayteta);

        return arvosanatTheme;

    }

}
