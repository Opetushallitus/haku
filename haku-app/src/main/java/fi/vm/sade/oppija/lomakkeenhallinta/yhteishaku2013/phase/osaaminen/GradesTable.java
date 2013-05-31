package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import java.util.List;
import java.util.UUID;

public class GradesTable {

    private GradeGridHelper gradeGridHelper;

    public GradesTable(final KoodistoService koodistoService, final boolean comprehensiveSchool) {
        gradeGridHelper = new GradeGridHelper(koodistoService, comprehensiveSchool);
    }

    public GradeGrid createGradeGrid(final String id) {
        boolean comprehensiveSchool = gradeGridHelper.isComprehensiveSchool();
        GradeGrid gradeGrid = new GradeGrid(id, ElementUtil.createI18NForm("form.arvosanat.otsikko"), comprehensiveSchool);
        ElementUtil.setVerboseHelp(gradeGrid);
        final String idPrefix = gradeGridHelper.getIdPrefix();

        for (SubjectRow nativeLanguage : gradeGridHelper.getNativeLanguages()) {
            gradeGrid.addChild(createGradeGridRow(nativeLanguage, true, true, comprehensiveSchool, idPrefix));
        }
        for (SubjectRow nativeLanguage : gradeGridHelper.getAdditionalNativeLanguages()) {
            GradeGridRow additionalLanguageRow = createAdditionalLanguageRow(nativeLanguage, gradeGridHelper.getLanguageAndLiterature());
            additionalLanguageRow.addAttribute("group", "nativeLanguage");
            additionalLanguageRow.addAttribute("hidden", "hidden");
            gradeGrid.addChild(additionalLanguageRow);
        }


        I18nText addNativeLangText = ElementUtil.createI18NForm("form.add.lang.native");
        gradeGrid.addChild(createAddLangRow("nativeLanguage",
                addNativeLangText, gradeGridHelper.getAdditionalNativeLanguages(), true, comprehensiveSchool));

        for (SubjectRow defaultLanguage : gradeGridHelper.getDefaultLanguages()) {
            gradeGrid.addChild(createGradeGridRow(defaultLanguage, true, false, comprehensiveSchool, idPrefix));
        }

        for (SubjectRow additionalLanguages : gradeGridHelper.getAdditionalLanguages()) {
            GradeGridRow additionalLanguageRow = createAdditionalLanguageRow(additionalLanguages, gradeGridHelper.getLanguages());
            additionalLanguageRow.addAttribute("group", "languages2");
            additionalLanguageRow.addAttribute("hidden", "hidden");
            gradeGrid.addChild(additionalLanguageRow);
        }

        gradeGrid.addChild(createAddLangRow("languages2",
                ElementUtil.createI18NForm("form.add.lang"), gradeGridHelper.getAdditionalNativeLanguages(), true, comprehensiveSchool));


        for (SubjectRow subjectsAfterLanguage : gradeGridHelper.getNotLanguageSubjects()) {
            gradeGrid.addChild(createGradeGridRow(subjectsAfterLanguage, false, false, comprehensiveSchool, idPrefix));
        }
        return gradeGrid;
    }

    GradeGridRow createAdditionalLanguageRow(final SubjectRow subjectRow, final List<Option> languages) {

        List<Option> gradeRangesWithDefault = gradeGridHelper.getGradeRangesWithDefault();
        Element[] columnsArray = createColumnsArray(gradeGridHelper.isComprehensiveSchool());
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

        GradeGridTitle title = new GradeGridTitle(UUID.randomUUID().toString().replace('.', '_'), subjectRow.getI18nText(), true);

        columnsArray[0].addChild(title);
        columnsArray[1].addChild(addLangs);
        columnsArray[2].addChild(grades);
        columnsArray[3].addChild(gradesSelected);
        if (gradesSelected2 != null) {
            columnsArray[4].addChild(gradesSelected2);
        }

        GradeGridRow gradeGridRow = ElementUtil.createHiddenGradeGridRowWithId("additionalRow-" + subjectRow.getId());
        gradeGridRow.addChild(columnsArray);
        return gradeGridRow;
    }


    Element[] createColumnsArray(boolean extraColumn) {
        if (extraColumn) {
            return new Element[]{
                    new GradeGridColumn("column1", true),
                    new GradeGridColumn("column2", false),
                    new GradeGridColumn("column3", false),
                    new GradeGridColumn("column4", false),
                    new GradeGridColumn("column5", false),
            };
        } else {
            return new Element[]{
                    new GradeGridColumn("column1", true),
                    new GradeGridColumn("column2", false),
                    new GradeGridColumn("column3", false),
                    new GradeGridColumn("column4", false),
            };
        }
    }

    GradeGridRow createAddLangRow(final String group, I18nText i18nText, List<SubjectRow> subjects,
                                  boolean literature, boolean extraColumn) {
        GradeGridRow gradeGridRow = new GradeGridRow(System.currentTimeMillis() + "");
        GradeGridColumn column1 = new GradeGridColumn(gradeGridRow.getId() + "-addlang", false);
        GradeGridAddLang child = new GradeGridAddLang(group, i18nText);
        column1.addChild(child);
        column1.addAttribute("colspan", (extraColumn ? "5" : "4"));
        gradeGridRow.addChild(column1);
        return gradeGridRow;
    }

    GradeGridRow createGradeGridRow(final SubjectRow subjectRow, boolean language,
                                    boolean literature, boolean extraColumn, final String idPrefix) {
        GradeGridRow gradeGridRow = new GradeGridRow(subjectRow.getId());
        GradeGridColumn column1 = new GradeGridColumn("column1", false);
        column1.addChild(new GradeGridTitle(System.currentTimeMillis() + "", subjectRow.getI18nText(), false));
        GradeGridColumn column2 = new GradeGridColumn("column2", false);
        GradeGridColumn column3 = new GradeGridColumn("column3", false);
        GradeGridColumn column4 = new GradeGridColumn("column4", false);
        GradeGridColumn column5 = null;
        if (extraColumn) {
            column5 = new GradeGridColumn("column5", false);
        }
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
            GradeGridOptionQuestion child = new GradeGridOptionQuestion(idPrefix + subjectRow.getId() + "_OPPIAINE", subjectLanguages, false);
            child.addAttribute("required", "required");
            column2.addChild(child);
        } else {
            column1.addAttribute("colspan", "2");
        }
        GradeGridOptionQuestion child1 = new GradeGridOptionQuestion(idPrefix + subjectRow.getId(), gradeRanges, false);
        child1.addAttribute("required", "required");
        column3.addChild(child1);
        GradeGridOptionQuestion gradeGridOptionQuestion = new GradeGridOptionQuestion(idPrefix + subjectRow.getId() + "_VAL1", gradeRanges, true);
        gradeGridOptionQuestion.addAttribute("required", "required");
        column4.addChild(gradeGridOptionQuestion);
        if (column5 != null) {
            GradeGridOptionQuestion child2 = new GradeGridOptionQuestion(idPrefix + subjectRow.getId() + "_VAL2", gradeRanges, true);
            child2.addAttribute("required", "required");
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
