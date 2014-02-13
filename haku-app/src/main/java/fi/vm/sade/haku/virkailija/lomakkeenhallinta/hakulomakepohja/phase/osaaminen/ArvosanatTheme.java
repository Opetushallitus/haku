package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionComplexRule;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createVarEqualsToValueRule;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public final class ArvosanatTheme {

    public static final String POHJAKOULUTUS_ID = "POHJAKOULUTUS";
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

        RelatedQuestionComplexRule relatedQuestionPK = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                PERUSKOULU, OSITTAIN_YKSILOLLISTETTY, ERITYISOPETUKSEN_YKSILOLLISTETTY, YKSILOLLISTETTY);


        GradeGrid grid_pk = gradesTablePK.createGradeGrid("grid_pk", formMessages, formErrors, verboseHelps);
        grid_pk.setHelp(createI18NText("form.arvosanat.help", formMessages));
        relatedQuestionPK.addChild(grid_pk);
        arvosanatTheme.addChild(relatedQuestionPK);


        RelatedQuestionComplexRule relatedQuestionLukio = createVarEqualsToValueRule(POHJAKOULUTUS_ID, YLIOPPILAS);
        GradeGrid grid_yo = gradesTableYO.createGradeGrid("grid_yo", formMessages, formErrors, verboseHelps);
        grid_yo.setHelp(createI18NText("form.arvosanat.help", formMessages));
        relatedQuestionLukio.addChild(grid_yo);
        arvosanatTheme.addChild(relatedQuestionLukio);

        RelatedQuestionComplexRule relatedQuestionEiTutkintoa = createVarEqualsToValueRule(POHJAKOULUTUS_ID,
                KESKEYTYNYT, ULKOMAINEN_TUTKINTO);

        relatedQuestionEiTutkintoa.addChild(
                new Text("nogradegrid", createI18NText("form.arvosanat.eiKysyta", formMessages)));
        arvosanatTheme.addChild(relatedQuestionEiTutkintoa);

        ElementUtil.setVerboseHelp(arvosanatTheme, "form.arvosanat.otsikko.verboseHelp", verboseHelps);
        return arvosanatTheme;
    }

}
