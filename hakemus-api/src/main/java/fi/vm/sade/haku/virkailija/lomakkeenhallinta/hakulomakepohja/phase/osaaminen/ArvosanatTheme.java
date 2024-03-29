package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder.Rule;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextBuilder.Text;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createVarEqualsToValueRule;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class ArvosanatTheme {

    private ArvosanatTheme() {
    }

    public static Element createArvosanatTheme(final FormParameters formParameters) {
        Element arvosanatTheme = arvosanatTeema(formParameters);

        if (formParameters.isOnlyThemeGenerationForFormEditor()){
            return arvosanatTheme;
        }

        Element relatedQuestionPK = createVarEqualsToValueRule(ELEMENT_ID_BASE_EDUCATION,
                PERUSKOULU,
                OSITTAIN_YKSILOLLISTETTY,
                ALUEITTAIN_YKSILOLLISTETTY,
                YKSILOLLISTETTY);

        Element arvosanataulukkoPkSv = Rule(
                new Equals(
                        new Variable(PERUSOPETUS_KIELI),
                        new Value("SV")))
                .build();
        arvosanataulukkoPkSv.addChild(arvosanataulukkoPK(formParameters, true));

        Element arvosanataulukkoPkMuut = Rule(
                new Not(
                        new Equals(
                                new Variable(PERUSOPETUS_KIELI),
                                new Value("SV"))))
                .build();
        arvosanataulukkoPkMuut.addChild(arvosanataulukkoPK(formParameters, false));

        relatedQuestionPK.addChild(arvosanataulukkoPkSv);
        relatedQuestionPK.addChild(arvosanataulukkoPkMuut);

        arvosanatTheme.addChild(relatedQuestionPK);

        if (formParameters.kysytaankoYlioppilastutkinto()) {
            Element pohjakoulutusOnYlioppilas = createVarEqualsToValueRule(ELEMENT_ID_BASE_EDUCATION, YLIOPPILAS);

            Element arvosanataulukkoYoSv = Rule(
                    new Equals(
                            new Variable(LUKIO_KIELI),
                            new Value("SV")))
                    .build();
            arvosanataulukkoYoSv.addChild(arvosanataulukkoYO(formParameters, true));

            Element arvosanataulukkoYoMuut = Rule(
                    new Not(
                            new Equals(
                                    new Variable(LUKIO_KIELI),
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
        Expr pkTaiKymppiPaattynytHakuvuonna = new ExprUtil().atLeastOneVariableEqualsToValue(String.valueOf(hakukausiVuosi),
                PERUSOPETUS_PAATTOTODISTUSVUOSI,
                KYMPPI_PAATTOTODISTUSVUOSI);
        Expr kysyArvosanatPk = new And(
                ExprUtil.atLeastOneValueEqualsToVariable(ELEMENT_ID_BASE_EDUCATION,
                        PERUSKOULU,
                        OSITTAIN_YKSILOLLISTETTY,
                        ALUEITTAIN_YKSILOLLISTETTY,
                        YKSILOLLISTETTY),
                new Or(
                        new Not(
                                pkTaiKymppiPaattynytHakuvuonna
                        ),
                        new Equals(new Variable("_meta_officerUi"), new Value("true"))
                ));
        Element relatedQuestionPk = Rule(kysyArvosanatPk).build();

        Element arvosanataulukkoPkSv = Rule(
                new Equals(
                        new Variable(PERUSOPETUS_KIELI),
                        new Value("SV")))
                .build();
        arvosanataulukkoPkSv.addChild(arvosanataulukkoPK(formParameters, true));

        Element arvosanataulukkoPkMuut = Rule(
                new Not(
                        new Equals(
                                new Variable(PERUSOPETUS_KIELI),
                                new Value("SV"))))
                .build();
        arvosanataulukkoPkMuut.addChild(arvosanataulukkoPK(formParameters, false));

        relatedQuestionPk.addChild(arvosanataulukkoPkSv);
        relatedQuestionPk.addChild(arvosanataulukkoPkMuut);

        arvosanatTheme.addChild(relatedQuestionPk);

        // Ei arvosanoja
        Element eiNaytetaPk = Rule(pkTaiKymppiPaattynytHakuvuonna).build();

        eiNaytetaPk.addChild(Text("nogradegrid").labelKey("form.arvosanat.eiKysyta.pk").formParams(formParameters).build());
        arvosanatTheme.addChild(eiNaytetaPk);

        if (formParameters.kysytaankoYlioppilastutkinto()) {
            // Ylioppilaat
            RelatedQuestionRuleBuilder naytetaankoLukionArvosanataulukko;
            if (formParameters.isLisahaku()) {
                naytetaankoLukionArvosanataulukko = Rule(new Equals(new Variable(ELEMENT_ID_BASE_EDUCATION), new Value(YLIOPPILAS)));
            } else {
                naytetaankoLukionArvosanataulukko = Rule(new And(
                        new Equals(new Variable(ELEMENT_ID_BASE_EDUCATION), new Value(YLIOPPILAS)),
                        new Or(
                                new Not(
                                        new Equals(
                                                new Variable(LUKIO_PAATTOTODISTUS_VUOSI),
                                                new Value(String.valueOf(hakukausiVuosi)))),
                                new Equals(new Variable("_meta_officerUi"), new Value("true")))));
            }
            Element arvosanataulukkoYoSv = Rule(
                    new Equals(
                            new Variable(LUKIO_KIELI),
                            new Value("SV")))
                    .build();
            arvosanataulukkoYoSv.addChild(arvosanataulukkoYO(formParameters, true));

            Element arvosanataulukkoYoMuut = Rule(
                    new Not(
                            new Equals(
                                    new Variable(LUKIO_KIELI),
                                    new Value("SV"))))
                    .build();
            arvosanataulukkoYoMuut.addChild(arvosanataulukkoYO(formParameters, false));

            naytetaankoLukionArvosanataulukko.addChild(arvosanataulukkoYoSv);
            naytetaankoLukionArvosanataulukko.addChild(arvosanataulukkoYoMuut);

            arvosanatTheme.addChild(naytetaankoLukionArvosanataulukko.build());
            if (!formParameters.isLisahaku()) {
                arvosanatTheme.addChild(Rule(
                                new Equals(new Variable("lukioPaattotodistusVuosi"), new Value(String.valueOf(hakukausiVuosi)))
                        )
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
        return Rule(ExprUtil.atLeastOneValueEqualsToVariable(ELEMENT_ID_BASE_EDUCATION, KESKEYTYNYT, ULKOMAINEN_TUTKINTO)).build();
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
        grid_pk.setHelp(formParameters.getI18nText(id + ".help"));
        return grid_pk;
    }

    private static Element arvosanatTeema(FormParameters formParameters) {
        return new ThemeBuilder("arvosanat").previewable().configurable().formParams(formParameters).build();
    }

}
