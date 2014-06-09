package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextBuilder.Text;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createVarEqualsToValueRule;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class ArvosanatTheme {

    public static final String POHJAKOULUTUS_ID = "POHJAKOULUTUS";
    public static final String RELATED_ELEMENT_ID = "POHJAKOULUTUS";

    private ArvosanatTheme() {
    }

    public static Element createArvosanatTheme(final FormParameters formParameters) {
        Element arvosanatTheme = new ThemeBuilder("arvosanat").previewable().formParams(formParameters).build();


        Element relatedQuestionPK = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        GradesTable gradesTablePK = new GradesTable(true, formParameters);
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formParameters);
        grid_pk.setHelp(createI18NText("form.arvosanat.help.pk", formParameters));
        relatedQuestionPK.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPK);

        if (!formParameters.isPervako()) {
            Element relatedQuestionLukio = createVarEqualsToValueRule(POHJAKOULUTUS_ID, YLIOPPILAS);
            GradesTable gradesTableYO = new GradesTable(false, formParameters);
            GradeGrid grid_yo = gradesTableYO.createGradeGrid("grid_yo", formParameters);
            grid_yo.setHelp(createI18NText("form.arvosanat.help.lk", formParameters));
            relatedQuestionLukio.addChild(grid_yo);
            arvosanatTheme.addChild(relatedQuestionLukio);

            Element relatedQuestionEiTutkintoa = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                    KESKEYTYNYT, ULKOMAINEN_TUTKINTO);

            relatedQuestionEiTutkintoa.addChild(
                    Text("nogradegrid").labelKey("form.arvosanat.eikysyta").formParams(formParameters).build());
            arvosanatTheme.addChild(relatedQuestionEiTutkintoa);
        }
        return arvosanatTheme;
    }

    public static Element createArvosanatThemeKevat(final FormParameters formParameters) {
        Element arvosanatTheme = new ThemeBuilder("arvosanat").previewable().formParams(formParameters).build();


        // Peruskoulu
        GradesTable gradesTablePK = new GradesTable(true, formParameters);
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
        Element relatedQuestionPk = Rule("rule_grade_pk").setExpr(kysyArvosanatPk).build();
        relatedQuestionPk.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPk);

        // Ei arvosanoja
        Element eiNaytetaPk = Rule("rule_grade_no_pk").setExpr(new Or(
                new Equals(new Variable(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI), new Value(String.valueOf(hakukausiVuosi))),
                new Not(new Regexp("_meta_grades_transferred_pk", "true"))
        )).build();
        eiNaytetaPk.addChild(Text("nogradegrid").labelKey("form.arvosanat.eiKysyta.pk").formParams(formParameters).build());
        arvosanatTheme.addChild(eiNaytetaPk);
        if (!formParameters.isPervako()) {
            // Ylioppilaat
            GradesTable gradesTableYO = new GradesTable(false, formParameters);
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
            Element relatedQuestionYo = Rule("rule_grade_yo").setExpr(kysyArvosanatLukio).build();
            relatedQuestionYo.addChild(grid_yo);
            arvosanatTheme.addChild(relatedQuestionYo);

            Element eiNaytetaYo = Rule("rule_grade_no_yo").setExpr(new Or(
                    new Equals(new Variable("lukioPaattotodistusVuosi"), new Value(String.valueOf(hakukausiVuosi))),
                    new Not(new Regexp("_meta_grades_transferred_lk", "true"))
            )).build();
            eiNaytetaYo.addChild(Text("nogradegrid").labelKey("form.arvosanat.eiKysyta.yo").formParams(formParameters).build());
            arvosanatTheme.addChild(eiNaytetaYo);
        }
        Element eiNayteta = Rule("rule_grade_no").setExpr(ExprUtil.atLeastOneValueEqualsToVariable(RELATED_ELEMENT_ID, "5", OppijaConstants.KESKEYTYNYT, OppijaConstants.ULKOMAINEN_TUTKINTO)).build();
        eiNayteta.addChild(Text("nogradegrid").labelKey("form.arvosanat.eikysyta").formParams(formParameters).build());
        arvosanatTheme.addChild(eiNayteta);

        return arvosanatTheme;

    }

}
