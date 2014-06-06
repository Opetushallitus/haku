package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextBuilder.Text;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class ArvosanatTheme {

    public static final String POHJAKOULUTUS_ID = "POHJAKOULUTUS";
    public static final String RELATED_ELEMENT_ID = "POHJAKOULUTUS";

    private ArvosanatTheme() {
    }

    public static Element createArvosanatTheme(final FormParameters formParameters) {
        Element arvosanatTheme = new ThemeBuilder("arvosanat").previewable().formParams(formParameters).build();


        RelatedQuestionComplexRule relatedQuestionPK = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        GradesTable gradesTablePK = new GradesTable(true, formParameters);
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formParameters);
        grid_pk.setHelp(createI18NText("form.arvosanat.help.pk", formParameters));
        relatedQuestionPK.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPK);

        if (!formParameters.isPervako()) {
            RelatedQuestionComplexRule relatedQuestionLukio = createVarEqualsToValueRule(POHJAKOULUTUS_ID, YLIOPPILAS);
            GradesTable gradesTableYO = new GradesTable(false, formParameters);
            GradeGrid grid_yo = gradesTableYO.createGradeGrid("grid_yo", formParameters);
            grid_yo.setHelp(createI18NText("form.arvosanat.help.lk", formParameters));
            relatedQuestionLukio.addChild(grid_yo);
            arvosanatTheme.addChild(relatedQuestionLukio);

            RelatedQuestionComplexRule relatedQuestionEiTutkintoa = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                    KESKEYTYNYT, ULKOMAINEN_TUTKINTO);

            relatedQuestionEiTutkintoa.addChild(
                    Text("nogradegrid").labelKey("form.arvosanat.eikysyta").formParams(formParameters).build());
            arvosanatTheme.addChild(relatedQuestionEiTutkintoa);
        }
        return arvosanatTheme;
    }

    public static Element createArvosanatThemeKevat(final FormParameters formParameters) {
        Element arvosanatTheme = new ThemeBuilder("arvosanat").previewable().formParams(formParameters).build();

        GradesTable gradesTablePK = new GradesTable(true, formParameters);
        GradesTable gradesTableYO = new GradesTable(false, formParameters);

        // Peruskoulu
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formParameters);
        grid_pk.setHelp(createI18NText("form.arvosanat.help.pk", formParameters));
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
        grid_yo.setHelp(createI18NText("form.arvosanat.help.lk", formParameters));
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
                new Or(
                    new Equals(new Variable(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI), new Value(String.valueOf(hakukausiVuosi))),
                    new Not(new Regexp("_meta_grades_transferred_pk", "true"))
                )
        );
        eiNaytetaPk.addChild(Text("nogradegrid").labelKey("form.arvosanat.eiKysyta.pk").formParams(formParameters).build());
        arvosanatTheme.addChild(eiNaytetaPk);

        RelatedQuestionComplexRule eiNaytetaYo = new RelatedQuestionComplexRule("rule_grade_no_yo",
                new Or(
                    new Equals(new Variable("lukioPaattotodistusVuosi"), new Value(String.valueOf(hakukausiVuosi))),
                    new Not(new Regexp("_meta_grades_transferred_lk", "true"))
                )
        );
        eiNaytetaYo.addChild(Text("nogradegrid").labelKey("form.arvosanat.eiKysyta.yo").formParams(formParameters).build());
        arvosanatTheme.addChild(eiNaytetaYo);

        RelatedQuestionComplexRule eiNayteta = new RelatedQuestionComplexRule("rule_grade_no",
                ExprUtil.atLeastOneValueEqualsToVariable(RELATED_ELEMENT_ID, "5", OppijaConstants.KESKEYTYNYT, OppijaConstants.ULKOMAINEN_TUTKINTO));
        eiNayteta.addChild(Text("nogradegrid").labelKey("form.arvosanat.eikysyta").formParams(formParameters).build());
        arvosanatTheme.addChild(eiNayteta);

        return arvosanatTheme;

    }

}
