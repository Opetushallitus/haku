package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextBuilder.Text;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createVarEqualsToValueRule;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class ArvosanatTheme {

    public static final String POHJAKOULUTUS_ID = "POHJAKOULUTUS";

    private ArvosanatTheme() {
    }

    public static Element createArvosanatTheme(final FormParameters formParameters) {
        Element arvosanatTheme = arvosanatTeema(formParameters);

        if (formParameters.isOnlyThemeGenerationForFormEditor()){
            return arvosanatTheme;
        }

        Element relatedQuestionPK = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                PERUSKOULU,
                OSITTAIN_YKSILOLLISTETTY,
                ALUEITTAIN_YKSILOLLISTETTY,
                YKSILOLLISTETTY);
        relatedQuestionPK.addChild(arvosanataulukkoPK(formParameters));
        arvosanatTheme.addChild(relatedQuestionPK);

        if (!formParameters.isPervako()) {
            Element pohjakoulutusOnYlioppilas = createVarEqualsToValueRule(POHJAKOULUTUS_ID, YLIOPPILAS);
            pohjakoulutusOnYlioppilas.addChild(arvosanataulukkoYO(formParameters));
            arvosanatTheme.addChild(pohjakoulutusOnYlioppilas);
        }
        arvosanatTheme.addChild(eiArvosanataulukkoa(formParameters));
        
        ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionGenerator();
        List<Element> themeQuestions = configurator.findAndConfigure(formParameters.getApplicationSystem(),arvosanatTheme.getId());
        if (themeQuestions.size() > 0) {
            arvosanatTheme.addChild(themeQuestions.toArray(new Element[themeQuestions.size()]));
        }
        return arvosanatTheme;
    }

    public static Element createArvosanatThemeKevat(final FormParameters formParameters) {
        Element arvosanatTheme = arvosanatTeema(formParameters);

        if (formParameters.isOnlyThemeGenerationForFormEditor()){
            return arvosanatTheme;
        }

        // Peruskoulu
        Integer hakukausiVuosi = formParameters.getApplicationSystem().getHakukausiVuosi();
        Expr kysyArvosanatPk = new Or(
                new And(
                        new Not(
                                new Equals(
                                        new Variable(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI),
                                        new Value(String.valueOf(hakukausiVuosi)))),
                        ExprUtil.atLeastOneValueEqualsToVariable(POHJAKOULUTUS_ID,
                                PERUSKOULU,
                                OSITTAIN_YKSILOLLISTETTY,
                                ALUEITTAIN_YKSILOLLISTETTY,
                                YKSILOLLISTETTY)),
                new Regexp("_meta_grades_transferred_pk", "true"));
        Element relatedQuestionPk = Rule(kysyArvosanatPk).build();
        relatedQuestionPk.addChild(arvosanataulukkoPK(formParameters));
        arvosanatTheme.addChild(relatedQuestionPk);

        // Ei arvosanoja
        Element eiNaytetaPk = Rule(new Or(
                new Equals(new Variable(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI), new Value(String.valueOf(hakukausiVuosi))),
                new Regexp("_meta_grades_transferred_pk", "true")
        )).build();

        eiNaytetaPk.addChild(Text("nogradegrid").labelKey("form.arvosanat.eiKysyta.pk").formParams(formParameters).build());
        arvosanatTheme.addChild(eiNaytetaPk);

        if (!formParameters.isPervako()) {
            // Ylioppilaat
            Expr kysyArvosanatLukio;
            RelatedQuestionRuleBuilder naytetaankoLukionArvosanataulukko;
            if (formParameters.isLisahaku()) {
                naytetaankoLukionArvosanataulukko = Rule(new Equals(new Variable(POHJAKOULUTUS_ID), new Value(OppijaConstants.YLIOPPILAS)));
            } else {
                naytetaankoLukionArvosanataulukko = Rule(
                        new Or(
                                new And(
                                        new Not(
                                                new Equals(
                                                        new Variable(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI),
                                                        new Value(String.valueOf(hakukausiVuosi)))),
                                        new Equals(new Variable(POHJAKOULUTUS_ID), new Value(OppijaConstants.YLIOPPILAS))),
                                new Equals(
                                        new Variable("_meta_grades_transferred_lk"),
                                        new Value("true"))));
            }
            naytetaankoLukionArvosanataulukko.addChild(arvosanataulukkoYO(formParameters));
            arvosanatTheme.addChild(naytetaankoLukionArvosanataulukko.build());
            if (!formParameters.isLisahaku()) {
                arvosanatTheme.addChild(Rule(
                        new Or(
                                new Equals(new Variable("lukioPaattotodistusVuosi"), new Value(String.valueOf(hakukausiVuosi))),
                                new Regexp("_meta_grades_transferred_lk", "true")
                        ))
                        .formParams(formParameters)
                        .addChild(Text().labelKey("form.arvosanat.eikysyta.yo"))
                        .build());
            }
        }

        arvosanatTheme.addChild(eiArvosanataulukkoa(formParameters));

        ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionGenerator();
        List<Element> themeQuestions = configurator.findAndConfigure(formParameters.getApplicationSystem(),arvosanatTheme.getId());
        if (themeQuestions.size() > 0) {
            arvosanatTheme.addChild(themeQuestions.toArray(new Element[themeQuestions.size()]));
        }
        return arvosanatTheme;

    }

    private static Element eiArvosanataulukkoa(FormParameters formParameters) {
        Element eiNayteta = pohjakoulutusOnKeskeytynytTaiUlkomainenRule();
        eiNayteta.addChild(textEiArvosanataulukkoa(formParameters));
        return eiNayteta;
    }

    private static Element pohjakoulutusOnKeskeytynytTaiUlkomainenRule() {
        return Rule(ExprUtil.atLeastOneValueEqualsToVariable(POHJAKOULUTUS_ID, OppijaConstants.KESKEYTYNYT, OppijaConstants.ULKOMAINEN_TUTKINTO)).build();
    }

    private static Element textEiArvosanataulukkoa(FormParameters formParameters) {
        return Text("nogradegrid").labelKey("form.arvosanat.eikysyta").formParams(formParameters).build();
    }

    private static GradeGrid arvosanataulukkoPK(FormParameters formParameters) {
        return arvosanataulukko(formParameters, "pk");
    }

    private static GradeGrid arvosanataulukkoYO(FormParameters formParameters) {
        return arvosanataulukko(formParameters, "yo");
    }

    private static GradeGrid arvosanataulukko(FormParameters formParameters, String suffix) {
        GradesTable gradesTable = new GradesTable("pk".equals(suffix), formParameters);
        String id = "arvosanataulukko_" + suffix;
        GradeGrid grid_pk = gradesTable.createGradeGrid(id, formParameters);
        grid_pk.setHelp(createI18NText(id + ".help", formParameters));
        return grid_pk;
    }

    private static Element arvosanatTeema(FormParameters formParameters) {
        return new ThemeBuilder("arvosanat").previewable().configurable().formParams(formParameters).build();
    }

}
