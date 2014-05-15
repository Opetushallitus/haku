package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class ArvosanatTheme {

    public static final String POHJAKOULUTUS_ID = "POHJAKOULUTUS";


    private ArvosanatTheme() {
    }

    public static Element createArvosanatTheme(final FormParameters formParameters) {
        Element arvosanatTheme = new ThemeBuilder("arvosanat").previewable().build(formParameters);


        RelatedQuestionComplexRule relatedQuestionPK = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);

        GradesTable gradesTablePK = new GradesTable(true, formParameters);
        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formParameters);
        grid_pk.setHelp(createI18NText("form.arvosanat.help", formParameters.getFormMessagesBundle()));
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
                    new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta", formParameters.getFormMessagesBundle())));
            arvosanatTheme.addChild(relatedQuestionEiTutkintoa);
        }
        return arvosanatTheme;
    }

}
