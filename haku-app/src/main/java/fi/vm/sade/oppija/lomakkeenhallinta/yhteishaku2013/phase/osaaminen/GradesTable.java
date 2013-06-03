package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import java.util.List;

public class GradesTable {

    public static final String ADDITIONAL_LANGUAGES_GROUP = "additionalLanguages";
    public static final String NATIVE_LANGUAGE_GROUP = "nativeLanguage";
    private GradeGridHelper gradeGridHelper;

    public GradesTable(final KoodistoService koodistoService, final boolean comprehensiveSchool) {
        gradeGridHelper = new GradeGridHelper(koodistoService, comprehensiveSchool);
    }

    public GradeGrid createGradeGrid(final String id) {
        GradeGrid gradeGrid = new GradeGrid(id,
                ElementUtil.createI18NForm("form.arvosanat.otsikko"),
                gradeGridHelper.isComprehensiveSchool());

        ElementUtil.setVerboseHelp(gradeGrid);

        for (SubjectRow nativeLanguage : gradeGridHelper.getNativeLanguages()) {
            gradeGrid.addChild(createGradeGridRow(nativeLanguage, true, true));
        }

        for (SubjectRow nativeLanguage : gradeGridHelper.getAdditionalNativeLanguages()) {
            gradeGrid.addChild(
                    createAdditionalLanguageRow(NATIVE_LANGUAGE_GROUP,
                            nativeLanguage, gradeGridHelper.getLanguageAndLiterature()));
        }
        I18nText addNativeLangText = ElementUtil.createI18NForm("form.add.lang.native");
        gradeGrid.addChild(createAddLangRow(NATIVE_LANGUAGE_GROUP, addNativeLangText));

        for (SubjectRow defaultLanguage : gradeGridHelper.getDefaultLanguages()) {
            gradeGrid.addChild(createGradeGridRow(defaultLanguage, true, false));
        }

        for (SubjectRow additionalLanguages : gradeGridHelper.getAdditionalLanguages()) {
            gradeGrid.addChild(
                    createAdditionalLanguageRow(ADDITIONAL_LANGUAGES_GROUP,
                            additionalLanguages,
                            gradeGridHelper.getLanguages()));
        }
        I18nText addAdditionalLnaguages = ElementUtil.createI18NForm("form.add.lang");
        gradeGrid.addChild(createAddLangRow(ADDITIONAL_LANGUAGES_GROUP, addAdditionalLnaguages));


        for (SubjectRow subjectsAfterLanguage : gradeGridHelper.getNotLanguageSubjects()) {
            gradeGrid.addChild(createGradeGridRow(subjectsAfterLanguage, false, false));
        }
        return gradeGrid;
    }

    GradeGridRow createAdditionalLanguageRow(final String group,
                                             final SubjectRow subjectRow, final List<Option> languages) {

        List<Option> gradeRangesWithDefault = gradeGridHelper.getGradeRangesWithDefault();
        Element[] columnsArray = createColumnsArray(true);
        String id = gradeGridHelper.getIdPrefix() + subjectRow.getId();

        GradeGridOptionQuestion addLangs =
                new GradeGridOptionQuestion(id + "_OPPIAINE", languages, false);
        ElementUtil.setDisabled(addLangs);

        GradeGridOptionQuestion grades =
                new GradeGridOptionQuestion(id, gradeGridHelper.getGradeRanges(), false);
        ElementUtil.setDisabled(grades);

        GradeGridOptionQuestion gradesSelected =
                new GradeGridOptionQuestion(id + "_VAL1", gradeRangesWithDefault, true);
        ElementUtil.setDisabled(gradesSelected);


        GradeGridOptionQuestion gradesSelected2 = null;
        if (gradeGridHelper.isComprehensiveSchool()) {
            gradesSelected2 =
                    new GradeGridOptionQuestion(id + "_VAL1", gradeRangesWithDefault, true);
            ElementUtil.setDisabled(gradesSelected2);
        }

        GradeGridTitle title = new GradeGridTitle(ElementUtil.randomId(), subjectRow.getI18nText(), true);

        columnsArray[0].addChild(title);
        columnsArray[1].addChild(addLangs);
        columnsArray[2].addChild(grades);
        columnsArray[3].addChild(gradesSelected);
        if (gradesSelected2 != null) {
            columnsArray[4].addChild(gradesSelected2);
        }

        GradeGridRow gradeGridRow = ElementUtil.createHiddenGradeGridRowWithId("additionalRow-" + subjectRow.getId());
        gradeGridRow.addChild(columnsArray);
        gradeGridRow.addAttribute("hidden", "hidden");
        gradeGridRow.addAttribute("group", group);

        return gradeGridRow;
    }


    Element[] createColumnsArray(boolean removable) {
        if (gradeGridHelper.isComprehensiveSchool()) {
            return new Element[]{
                    new GradeGridColumn("column1", removable),
                    new GradeGridColumn("column2", false),
                    new GradeGridColumn("column3", false),
                    new GradeGridColumn("column4", false),
                    new GradeGridColumn("column5", false),
            };
        } else {
            return new Element[]{
                    new GradeGridColumn("column1", removable),
                    new GradeGridColumn("column2", false),
                    new GradeGridColumn("column3", false),
                    new GradeGridColumn("column4", false),
            };
        }
    }

    GradeGridRow createAddLangRow(final String group, final I18nText i18nText) {
        GradeGridRow gradeGridRow = new GradeGridRow(ElementUtil.randomId());
        GradeGridColumn column1 = new GradeGridColumn(gradeGridRow.getId() + "-addlang", false);
        GradeGridAddLang child = new GradeGridAddLang(group, i18nText);
        column1.addChild(child);
        column1.addAttribute("colspan", (gradeGridHelper.isComprehensiveSchool() ? "5" : "4"));
        gradeGridRow.addChild(column1);
        return gradeGridRow;
    }

    GradeGridRow createGradeGridRow(final SubjectRow subjectRow, boolean language, boolean literature) {

        GradeGridRow gradeGridRow = new GradeGridRow(subjectRow.getId());

        GradeGridColumn column1 = new GradeGridColumn("column1", false);
        GradeGridColumn column2 = new GradeGridColumn("column2", false);
        GradeGridColumn column3 = new GradeGridColumn("column3", false);
        GradeGridColumn column4 = new GradeGridColumn("column4", false);
        GradeGridColumn column5 = null;
        if (gradeGridHelper.isComprehensiveSchool()) {
            column5 = new GradeGridColumn("column5", false);
        }

        column1.addChild(new GradeGridTitle(System.currentTimeMillis() + "", subjectRow.getI18nText(), false));
        String id = gradeGridHelper.getIdPrefix() + subjectRow.getId();
        List<Option> gradeRanges = gradeGridHelper.getGradeRanges();
        ElementUtil.setDefaultOption("Ei arvosanaa", gradeRanges);
        if (subjectRow.isLanguage() || language) {
            List<Option> subjectLanguages;
            if (literature) {
                subjectLanguages = gradeGridHelper.getLanguageAndLiterature();
                ElementUtil.setDefaultOption("FI", subjectLanguages);
            } else {
                subjectLanguages = gradeGridHelper.getSubjectLanguages();
            }
            GradeGridOptionQuestion child = new GradeGridOptionQuestion(id + "_OPPIAINE", subjectLanguages, false);
            ElementUtil.setRequired(child);
            column2.addChild(child);
        } else {
            column1.addAttribute("colspan", "2");
        }
        GradeGridOptionQuestion child1 = new GradeGridOptionQuestion(id, gradeRanges, false);
        ElementUtil.setRequired(child1);
        column3.addChild(child1);
        GradeGridOptionQuestion gradeGridOptionQuestion = new GradeGridOptionQuestion(id + "_VAL1", gradeRanges, true);
        ElementUtil.setRequired(gradeGridOptionQuestion);
        column4.addChild(gradeGridOptionQuestion);
        if (column5 != null) {
            GradeGridOptionQuestion child2 = new GradeGridOptionQuestion(id + "_VAL2", gradeRanges, true);
            ElementUtil.setRequired(child2);
            column5.addChild(child2);
        }
        gradeGridRow.addChild(column1);
        if (subjectRow.isLanguage() || language) {
            gradeGridRow.addChild(column2);
        }
        gradeGridRow.addChild(column3);
        gradeGridRow.addChild(column4);
        if (column5 != null) {
            gradeGridRow.addChild(column5);
        }
        return gradeGridRow;

    }
}
