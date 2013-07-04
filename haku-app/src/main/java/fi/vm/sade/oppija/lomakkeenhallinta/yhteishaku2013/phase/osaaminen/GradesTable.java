package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.validation.validators.UniqValuesValidator;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.util.function.ElementToId;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.IdEndsWith;

import java.util.List;

public class GradesTable {

    public static final String ADDITIONAL_LANGUAGES_GROUP = "additionalLanguages";
    public static final String NATIVE_LANGUAGE_GROUP = "nativeLanguage";
    public static final String OPPIAINE_SUFFIX = "_OPPIAINE";
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
                            gradeGridHelper.getSubjectLanguages()));
        }
        I18nText addAdditionalanguages = ElementUtil.createI18NForm("form.add.lang");
        gradeGrid.addChild(createAddLangRow(ADDITIONAL_LANGUAGES_GROUP, addAdditionalanguages));


        for (SubjectRow subjectsAfterLanguage : gradeGridHelper.getNotLanguageSubjects()) {
            gradeGrid.addChild(createGradeGridRow(subjectsAfterLanguage, false, false));
        }
        List<String> uniqLanguagesIds = Lists.transform(
                ElementUtil.filterElements(gradeGrid, new IdEndsWith(OPPIAINE_SUFFIX)),
                new ElementToId());
        gradeGrid.setValidator(
                new UniqValuesValidator(
                        gradeGrid.getId(), uniqLanguagesIds,
                        ElementUtil.createI18NTextError("yleinen.kielet.samoja")));
        return gradeGrid;
    }

    GradeGridRow createAdditionalLanguageRow(final String group,
                                             final SubjectRow subjectRow, final List<Option> languages) {

        List<Option> gradeRangesWithDefault = gradeGridHelper.getGradeRangesWithDefault();
        Element[] columnsArray = createColumnsArray(true);
        String id = gradeGridHelper.getIdPrefix() + subjectRow.getId();

        GradeGridOptionQuestion addLangs =
                new GradeGridOptionQuestion(id + OPPIAINE_SUFFIX, languages, false);
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
                    new GradeGridOptionQuestion(id + "_VAL2", gradeRangesWithDefault, true);
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
        Element[] columns = createColumnsArray(false);

        columns[0].addChild(new GradeGridTitle(System.currentTimeMillis() + "", subjectRow.getI18nText(), false));
        String id = gradeGridHelper.getIdPrefix() + subjectRow.getId();

        if (subjectRow.isLanguage() || language) {
            List<Option> subjectLanguages;
            if (literature) {
                subjectLanguages = gradeGridHelper.getLanguageAndLiterature();
                ElementUtil.setDefaultOption("FI", subjectLanguages);
            } else {
                subjectLanguages = gradeGridHelper.getSubjectLanguages();
            }
            GradeGridOptionQuestion child = new GradeGridOptionQuestion(id + "_OPPIAINE", subjectLanguages, false);
            ElementUtil.addRequiredValidator(child);
            columns[1].addChild(child);
        } else {
            columns[0].addAttribute("colspan", "2");
        }
        GradeGridOptionQuestion child1 = new GradeGridOptionQuestion(id, gradeGridHelper.getGradeRanges(), false);
        ElementUtil.addRequiredValidator(child1);
        columns[2].addChild(child1);
        GradeGridOptionQuestion gradeGridOptionQuestion = new GradeGridOptionQuestion(id + "_VAL1", gradeGridHelper.getGradeRangesWithDefault(), true);
        ElementUtil.addRequiredValidator(gradeGridOptionQuestion);
        columns[3].addChild(gradeGridOptionQuestion);

        gradeGridRow.addChild(columns[0]);
        if (subjectRow.isLanguage() || language) {
            gradeGridRow.addChild(columns[1]);
        }
        gradeGridRow.addChild(columns[2]);
        gradeGridRow.addChild(columns[3]);
        if (gradeGridHelper.isComprehensiveSchool()) {
            GradeGridOptionQuestion child2 = new GradeGridOptionQuestion(id + "_VAL2", gradeGridHelper.getGradeRangesWithDefault(), true);
            ElementUtil.addRequiredValidator(child2);
            columns[4].addChild(child2);
            gradeGridRow.addChild(columns[4]);
        }
        return gradeGridRow;

    }
}
