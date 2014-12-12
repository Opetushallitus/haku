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

        Element arvosanataulukkoPkSv = Rule(
                new Equals(
                        new Variable(OppijaConstants.PERUSOPETUS_KIELI),
                        new Value("SV")))
                .build();
        arvosanataulukkoPkSv.addChild(arvosanataulukkoPK(formParameters, true));

        Element arvosanataulukkoPkMuut = Rule(
                new Not(
                        new Equals(
                                new Variable(OppijaConstants.PERUSOPETUS_KIELI),
                                new Value("SV"))))
                .build();
        arvosanataulukkoPkMuut.addChild(arvosanataulukkoPK(formParameters, false));

        relatedQuestionPK.addChild(arvosanataulukkoPkSv);
        relatedQuestionPK.addChild(arvosanataulukkoPkMuut);

        arvosanatTheme.addChild(relatedQuestionPK);

        if (!formParameters.isPerusopetuksenJalkeinenValmentava()) {
            Element pohjakoulutusOnYlioppilas = createVarEqualsToValueRule(POHJAKOULUTUS_ID, YLIOPPILAS);

            Element arvosanataulukkoYoSv = Rule(
                    new Equals(
                            new Variable(OppijaConstants.LUKIO_KIELI),
                            new Value("SV")))
                    .build();
            arvosanataulukkoYoSv.addChild(arvosanataulukkoYO(formParameters, true));

            Element arvosanataulukkoYoMuut = Rule(
                    new Not(
                            new Equals(
                                    new Variable(OppijaConstants.LUKIO_KIELI),
                                    new Value("SV"))))
                    .build();
            arvosanataulukkoYoMuut.addChild(arvosanataulukkoYO(formParameters, false));

            pohjakoulutusOnYlioppilas.addChild(arvosanataulukkoYoSv);
            pohjakoulutusOnYlioppilas.addChild(arvosanataulukkoYoMuut);

            arvosanatTheme.addChild(pohjakoulutusOnYlioppilas);
        }
        arvosanatTheme.addChild(eiArvosanataulukkoa(formParameters));
        
        ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionConfigurator();
        arvosanatTheme.addChild(configurator.findAndConfigure(arvosanatTheme.getId()));
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

        Element arvosanataulukkoPkSv = Rule(
                new Equals(
                        new Variable(OppijaConstants.PERUSOPETUS_KIELI),
                        new Value("SV")))
                .build();
        arvosanataulukkoPkSv.addChild(arvosanataulukkoPK(formParameters, true));

        Element arvosanataulukkoPkMuut = Rule(
                new Not(
                        new Equals(
                                new Variable(OppijaConstants.PERUSOPETUS_KIELI),
                                new Value("SV"))))
                .build();
        arvosanataulukkoPkMuut.addChild(arvosanataulukkoPK(formParameters, false));

        relatedQuestionPk.addChild(arvosanataulukkoPkSv);
        relatedQuestionPk.addChild(arvosanataulukkoPkMuut);

        arvosanatTheme.addChild(relatedQuestionPk);

        // Ei arvosanoja
        Element eiNaytetaPk = Rule(new Or(
                new Equals(new Variable(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI), new Value(String.valueOf(hakukausiVuosi))),
                new Regexp("_meta_grades_transferred_pk", "true")
        )).build();

        eiNaytetaPk.addChild(Text("nogradegrid").labelKey("form.arvosanat.eiKysyta.pk").formParams(formParameters).build());
        arvosanatTheme.addChild(eiNaytetaPk);

        if (!formParameters.isPerusopetuksenJalkeinenValmentava()) {
            // Ylioppilaat
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
            Element arvosanataulukkoYoSv = Rule(
                    new Equals(
                            new Variable(OppijaConstants.LUKIO_KIELI),
                            new Value("SV")))
                    .build();
            arvosanataulukkoYoSv.addChild(arvosanataulukkoYO(formParameters, true));

            Element arvosanataulukkoYoMuut = Rule(
                    new Not(
                            new Equals(
                                    new Variable(OppijaConstants.LUKIO_KIELI),
                                    new Value("SV"))))
                    .build();
            arvosanataulukkoYoMuut.addChild(arvosanataulukkoYO(formParameters, false));

            naytetaankoLukionArvosanataulukko.addChild(arvosanataulukkoYoSv);
            naytetaankoLukionArvosanataulukko.addChild(arvosanataulukkoYoMuut);

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

        ThemeQuestionConfigurator configurator = formParameters.getThemeQuestionConfigurator();
        arvosanatTheme.addChild(configurator.findAndConfigure(arvosanatTheme.getId()));

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

    private static GradeGrid arvosanataulukkoPK(FormParameters formParameters, boolean isSv) {
        return arvosanataulukko(formParameters, "pk", isSv);
    }

    private static GradeGrid arvosanataulukkoYO(FormParameters formParameters, boolean isSv) {
        return arvosanataulukko(formParameters, "yo", isSv);
    }

    private static GradeGrid arvosanataulukko(FormParameters formParameters, String suffix, boolean isSv) {
        GradesTable gradesTable = new GradesTable("pk".equals(suffix), formParameters);
        String id = "arvosanataulukko_" + suffix;
        GradeGrid grid_pk = gradesTable.createGradeGrid(id, formParameters, isSv);
        grid_pk.setHelp(createI18NText(id + ".help", formParameters));
        return grid_pk;
    }

    private static Element arvosanatTeema(FormParameters formParameters) {
        return new ThemeBuilder("arvosanat").previewable().configurable().formParams(formParameters).build();
    }

}
