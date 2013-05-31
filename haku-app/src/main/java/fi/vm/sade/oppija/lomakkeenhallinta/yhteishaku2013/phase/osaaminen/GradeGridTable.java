package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.Ids;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.Languages;

import java.util.ArrayList;
import java.util.List;

public class GradeGridTable {

    private GradeGridHelper gradeGridHelper;

    public GradeGridTable(final KoodistoService koodistoService, final boolean comprehensiveSchool) {
        gradeGridHelper = new GradeGridHelper(koodistoService, comprehensiveSchool);
    }

    public GradeGrid createGradeGrid(final String id) {
        boolean comprehensiveSchool = gradeGridHelper.isComprehensiveSchool();
        GradeGrid gradeGrid = new GradeGrid(id, ElementUtil.createI18NForm("form.arvosanat.otsikko"), comprehensiveSchool);
        ElementUtil.setVerboseHelp(gradeGrid);
        final String idPrefix = gradeGridHelper.getIdPrefix();

        List<SubjectRow> nativeLanguages = gradeGridHelper.getNativeLanguages();
        for (SubjectRow nativeLanguage : nativeLanguages) {
            gradeGrid.addChild(createGradeGridRow(nativeLanguage, true, true, comprehensiveSchool, idPrefix, "_1"));
            GradeGridRow additionalNativeLanguageRow =
                    createAdditionalNativeLanguageRow(nativeLanguage, 2, comprehensiveSchool, idPrefix);
            additionalNativeLanguageRow.addAttribute("hidden", "hidden");
            additionalNativeLanguageRow.addAttribute("group", "nativeLanguage");
            gradeGrid.addChild(additionalNativeLanguageRow);
        }
        I18nText addNativeLangText = ElementUtil.createI18NForm("form.add.lang.native");
        gradeGrid.addChild(createAddLangRow("nativeLanguage",
                addNativeLangText, nativeLanguages, true, comprehensiveSchool));

        for (SubjectRow defaultLanguage : gradeGridHelper.getDefaultLanguages()) {
            gradeGrid.addChild(createGradeGridRow(defaultLanguage, true, false, comprehensiveSchool, idPrefix, ""));
        }

        List<SubjectRow> subjects = gradeGridHelper.getSubjects();
        List<GradeGridRow> additionalLanguages = createAdditionalLanguages(5, subjects, comprehensiveSchool);
        for (GradeGridRow additionalLanguage : additionalLanguages) {
            additionalLanguage.addAttribute("group", "languages");
            gradeGrid.addChild(additionalLanguage);
        }
        gradeGrid.addChild(createAddLangRow("languages",
                ElementUtil.createI18NForm("form.add.lang"), subjects, false, comprehensiveSchool));


        for (SubjectRow subjectsAfterLanguage : gradeGridHelper.getNotLanguageSubjects()) {
            gradeGrid.addChild(createGradeGridRow(subjectsAfterLanguage, false, false, comprehensiveSchool, idPrefix, ""));
        }
        return gradeGrid;
    }


    List<GradeGridRow> createAdditionalLanguages(int maxAdditionalLanguages,
                                                 final List<SubjectRow> subjects, boolean extraColumn) {
        List<GradeGridRow> rows = new ArrayList<GradeGridRow>();
        for (int i = 0; i < maxAdditionalLanguages; i++) {
            rows.add(createAdditionalLanguageRow(i, subjects, extraColumn));
        }
        return rows;
    }

    GradeGridRow createAdditionalNativeLanguageRow(final SubjectRow subjectRow, int index, boolean extraColumn, final String idPrefix) {
        List<Option> gradeRangesWithDefault = gradeGridHelper.getGradeRangesWithDefault();

        Element[] columnsArray = createColumnsArray(index, extraColumn);

        String postfix = idPrefix + subjectRow.getId() + "_" + index;
        GradeGridOptionQuestion addLangs =
                new GradeGridOptionQuestion("custom-language-" + postfix, gradeGridHelper.getSubjectLanguages(), false);
        ElementUtil.setDisabled(addLangs);
        GradeGridOptionQuestion grades =
                new GradeGridOptionQuestion("custom-grades-" + postfix, gradeGridHelper.getGradeRanges(), false);
        ElementUtil.setDisabled(grades);
        GradeGridOptionQuestion gradesSelected =
                new GradeGridOptionQuestion("custom-optional-grades-" + postfix, gradeRangesWithDefault, true);
        ElementUtil.setDisabled(gradesSelected);
        GradeGridOptionQuestion gradesSelected2 = null;
        if (extraColumn) {
            gradesSelected2 =
                    new GradeGridOptionQuestion("second-custom-optional-grades-" + postfix, gradeRangesWithDefault, true);
            ElementUtil.setDisabled(gradesSelected2);
        }

        GradeGridTitle title = new GradeGridTitle(System.currentTimeMillis() + "", subjectRow.getI18nText(), true);

        columnsArray[0].addChild(title);
        columnsArray[1].addChild(addLangs);
        columnsArray[2].addChild(grades);
        columnsArray[3].addChild(gradesSelected);
        if (gradesSelected2 != null) {
            columnsArray[4].addChild(gradesSelected2);
        }

        GradeGridRow gradeGridRow = ElementUtil.createHiddenGradeGridRowWithId("additionalLanguageNativeRow-" + index);
        gradeGridRow.addChild(columnsArray);
        return gradeGridRow;
    }

    GradeGridRow createAdditionalLanguageRow(int index, final List<SubjectRow> subjects, boolean extraColumn) {
        List<Option> gradeRangesWithDefault = gradeGridHelper.getGradeRangesWithDefault();
        GradeGridRow gradeGridRow = new GradeGridRow("additionalLanguageRow-" + index);
        gradeGridRow.addAttribute("hidden", "hidden");
        Element[] columnsArray = createColumnsArray(index, extraColumn);

        List<Option> options = getLanguageSubjects(subjects);
        GradeGridOptionQuestion addSubs = new GradeGridOptionQuestion("custom-scope-" + index, options, false);
        ElementUtil.setDisabled(addSubs);
        GradeGridOptionQuestion addLangs = new GradeGridOptionQuestion("custom-language-" + index, gradeGridHelper.getSubjectLanguages(), false);
        ElementUtil.setDisabled(addLangs);
        GradeGridOptionQuestion grades = new GradeGridOptionQuestion("custom-grades-" + index, gradeGridHelper.getGradeRanges(), false);
        ElementUtil.setDisabled(grades);
        GradeGridOptionQuestion gradesSelected = new GradeGridOptionQuestion("custom-optional-grades-" + index, gradeRangesWithDefault, true);
        ElementUtil.setDisabled(gradesSelected);
        GradeGridOptionQuestion gradesSelected2 = null;
        if (extraColumn) {
            gradesSelected2 = new GradeGridOptionQuestion("second-custom-optional-grades-" + index, gradeRangesWithDefault, true);
            ElementUtil.setDisabled(gradesSelected2);
        }

        columnsArray[0].addChild(addSubs);
        columnsArray[1].addChild(addLangs);
        columnsArray[2].addChild(grades);
        columnsArray[3].addChild(gradesSelected);
        if (gradesSelected2 != null) {
            columnsArray[4].addChild(gradesSelected2);
        }

        gradeGridRow.addChild(columnsArray);
        return gradeGridRow;
    }

    Element[] createColumnsArray(final int index, boolean extraColumn) {
        if (extraColumn) {
            return new Element[]{
                    new GradeGridColumn("column1-" + index, true),
                    new GradeGridColumn("column2-" + index, false),
                    new GradeGridColumn("column3-" + index, false),
                    new GradeGridColumn("column4-" + index, false),
                    new GradeGridColumn("column5-" + index, false),
            };
        } else {
            return new Element[]{
                    new GradeGridColumn("column1-" + index, true),
                    new GradeGridColumn("column2-" + index, false),
                    new GradeGridColumn("column3-" + index, false),
                    new GradeGridColumn("column4-" + index, false),
            };
        }
    }


    List<Option> getLanguageSubjects(final List<SubjectRow> subjects) {
        List<SubjectRow> listOfLanguages = ImmutableList.copyOf(Iterables.filter(subjects, new Languages()));
        List<SubjectRow> additionalLanguages = ImmutableList.copyOf(
                Iterables.filter(listOfLanguages, Predicates.not(new Ids<SubjectRow>("A1", "B1"))));
        List<Option> options = new ArrayList<Option>();
        for (SubjectRow additionalLanguage : additionalLanguages) {
            options.add(new Option("subject-template", additionalLanguage.getI18nText(), additionalLanguage.getId()));
        }
        return options;
    }

    GradeGridRow createAddLangRow(final String group, I18nText i18nText, List<SubjectRow> subjects,
                                  boolean literature, boolean extraColumn) {
        GradeGridRow gradeGridRow = new GradeGridRow(System.currentTimeMillis() + "");
        GradeGridColumn column1 = new GradeGridColumn(gradeGridRow.getId() + "-addlang", false);
        List<Option> subjectOptions = getLanguageSubjects(subjects);
        List<Option> languageOptions;
        if (literature) {
            languageOptions = gradeGridHelper.getLanguageAndLiterature();
        } else {
            languageOptions = gradeGridHelper.getLanguages();
        }
        GradeGridAddLang child = new GradeGridAddLang(group, i18nText, subjectOptions, languageOptions,
                gradeGridHelper.getGradeRanges());
        column1.addChild(child);
        column1.addAttribute("colspan", (extraColumn ? "5" : "4"));
        gradeGridRow.addChild(column1);
        return gradeGridRow;
    }

    GradeGridRow createGradeGridRow(final SubjectRow subjectRow, boolean language,
                                    boolean literature, boolean extraColumn, final String idPrefix, final String idSuffix) {
        GradeGridRow gradeGridRow = new GradeGridRow(subjectRow.getId() + idSuffix);
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
            GradeGridOptionQuestion child = new GradeGridOptionQuestion(idPrefix + subjectRow.getId() + idSuffix + "_OPPIAINE", subjectLanguages, false);
            child.addAttribute("required", "required");
            column2.addChild(child);
        } else {
            column1.addAttribute("colspan", "2");
        }
        GradeGridOptionQuestion child1 = new GradeGridOptionQuestion(idPrefix + subjectRow.getId() + idSuffix, gradeRanges, false);
        child1.addAttribute("required", "required");
        column3.addChild(child1);
        GradeGridOptionQuestion gradeGridOptionQuestion = new GradeGridOptionQuestion(idPrefix + subjectRow.getId() + idSuffix + "_VAL1", gradeRanges, true);
        gradeGridOptionQuestion.addAttribute("required", "required");
        column4.addChild(gradeGridOptionQuestion);
        if (column5 != null) {
            GradeGridOptionQuestion child2 = new GradeGridOptionQuestion(idPrefix + subjectRow.getId() + idSuffix + "_VAL2", gradeRanges, true);
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
