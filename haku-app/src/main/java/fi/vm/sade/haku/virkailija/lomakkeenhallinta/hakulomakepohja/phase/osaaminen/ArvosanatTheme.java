package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class ArvosanatTheme {

    public static final String POHJAKOULUTUS_ID = "POHJAKOULUTUS";
    public static final String ARVOSANAT_THEME_ID = "arvosanatTheme";


    private ArvosanatTheme() {
    }

    public static Theme createArvosanatTheme(final FormParameters formParameters) {
        Theme arvosanatTheme = new Theme(
                ARVOSANAT_THEME_ID,
                createI18NText("form.arvosanat.otsikko", formParameters.getFormMessagesBundle()),
                true);

        GradesTable gradesTablePK = new GradesTable(true, formParameters);
        GradesTable gradesTableYO = new GradesTable(false, formParameters);

        RelatedQuestionComplexRule relatedQuestionPK = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ALUEITTAIN_YKSILOLLISTETTY, YKSILOLLISTETTY);


        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formParameters);
        grid_pk.setHelp(createI18NText("form.arvosanat.help", formParameters.getFormMessagesBundle()));
        setHelp(grid_pk, "form.arvosanat.help", formParameters);
        relatedQuestionPK.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPK);


        RelatedQuestionComplexRule relatedQuestionLukio = createVarEqualsToValueRule(POHJAKOULUTUS_ID, YLIOPPILAS);
        GradeGrid grid_yo = gradesTableYO.createGradeGrid("grid_yo", formParameters);
        setHelp(grid_yo, "form.arvosanat.help", formParameters);
        relatedQuestionLukio.addChild(grid_yo);
        arvosanatTheme.addChild(relatedQuestionLukio);

        RelatedQuestionComplexRule relatedQuestionEiTutkintoa = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                KESKEYTYNYT, ULKOMAINEN_TUTKINTO);

        relatedQuestionEiTutkintoa.addChild(
                new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta", formParameters.getFormMessagesBundle())));
        arvosanatTheme.addChild(relatedQuestionEiTutkintoa);

        ElementUtil.setVerboseHelp(arvosanatTheme, "form.arvosanat.otsikko.verboseHelp", formParameters);
        return arvosanatTheme;
    }

}
