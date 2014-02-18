package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.*;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.validation.validators.UniqValuesValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate.IdEndsWith;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.function.ElementToId;

import java.util.ArrayList;
import java.util.List;

public class GradesTable {

    public static final String ADDITIONAL_LANGUAGES_GROUP = "additionalLanguages";
    public static final String NATIVE_LANGUAGE_GROUP = "nativeLanguage";
    public static final String OPPIAINE_SUFFIX = "_OPPIAINE";
    private GradeGridHelper gradeGridHelper;

    public GradesTable(final KoodistoService koodistoService, final boolean comprehensiveSchool, final String formMessages,
                       final String formErrors, final String verboseHelps) {
        gradeGridHelper = new GradeGridHelper(koodistoService, comprehensiveSchool, formMessages, formErrors, verboseHelps);
    }

    public GradeGrid createGradeGrid(final String id, final String formMessages,
                                     final String formErrors, final String verboseHelps) {
        GradeGrid gradeGrid = new GradeGrid(id,
                ElementUtil.createI18NText("form.arvosanat.otsikko", formMessages),
                gradeGridHelper.isComprehensiveSchool());

        ElementUtil.setVerboseHelp(gradeGrid, "form.arvosanat.otsikko.verboseHelp", verboseHelps);

        for (SubjectRow nativeLanguage : gradeGridHelper.getNativeLanguages()) {
            gradeGrid.addChild(createGradeGridRow(nativeLanguage, true, true, formMessages, formErrors, verboseHelps));
        }

        for (SubjectRow nativeLanguage : gradeGridHelper.getAdditionalNativeLanguages()) {
            gradeGrid.addChild(
                    createAdditionalLanguageRow(NATIVE_LANGUAGE_GROUP,
                            nativeLanguage, gradeGridHelper.getLanguageAndLiterature()));
        }
        I18nText addNativeLangText = ElementUtil.createI18NText("form.add.lang.native", formMessages);
        gradeGrid.addChild(createAddLangRow(NATIVE_LANGUAGE_GROUP, addNativeLangText));

        for (SubjectRow defaultLanguage : gradeGridHelper.getDefaultLanguages()) {
            gradeGrid.addChild(createGradeGridRow(defaultLanguage, true, false, formMessages, formErrors, verboseHelps));
        }

        for (SubjectRow additionalLanguages : gradeGridHelper.getAdditionalLanguages()) {
            gradeGrid.addChild(
                    createAdditionalLanguageRow(ADDITIONAL_LANGUAGES_GROUP,
                            additionalLanguages,
                            gradeGridHelper.getSubjectLanguages()));
        }
        I18nText addAdditionalanguages = ElementUtil.createI18NText("form.add.lang", formMessages);
        gradeGrid.addChild(createAddLangRow(ADDITIONAL_LANGUAGES_GROUP, addAdditionalanguages));


        for (SubjectRow subjectsAfterLanguage : gradeGridHelper.getNotLanguageSubjects()) {
            gradeGrid.addChild(createGradeGridRow(subjectsAfterLanguage, false, false, formMessages, formErrors, verboseHelps));
        }
        List<String> uniqLanguagesIds = Lists.transform(
                ElementUtil.filterElements(gradeGrid, new IdEndsWith(OPPIAINE_SUFFIX)),
                new ElementToId());
        gradeGrid.setValidator(
                new UniqValuesValidator(
                        gradeGrid.getId(),
                        uniqLanguagesIds,
                        ImmutableList.of(OppijaConstants.EDUCATION_LANGUAGE_EI_SUORITUSTA),
                        ElementUtil.createI18NText("yleinen.kielet.samoja", formErrors)));
        return gradeGrid;
    }

    GradeGridRow createAdditionalLanguageRow(final String group,
                                             final SubjectRow subjectRow, final List<Option> languages) {

        List<Option> gradeRangesWithDefault = gradeGridHelper.getGradeRangesWithDefault();
        String id = gradeGridHelper.getIdPrefix() + subjectRow.getId();
        Element[] columnsArray = createColumnsArray(true, id);


        GradeGridOptionQuestion addLangs =
                new GradeGridOptionQuestion(id + OPPIAINE_SUFFIX, languages, false, true, null);
        ElementUtil.setDisabled(addLangs);

        GradeGridOptionQuestion grades =
                new GradeGridOptionQuestion(id, gradeGridHelper.getGradeRanges(), false, false, null);
        ElementUtil.setDisabled(grades);

        GradeGridOptionQuestion gradesSelected = null;
        GradeGridOptionQuestion gradesSelected2 = null;
        if (gradeGridHelper.isComprehensiveSchool()) {
            gradesSelected =
                    new GradeGridOptionQuestion(id + "_VAL1", gradeRangesWithDefault, true, false, null);
            ElementUtil.setDisabled(gradesSelected);

            gradesSelected2 =
                    new GradeGridOptionQuestion(id + "_VAL2", gradeRangesWithDefault, true, false, null);
            ElementUtil.setDisabled(gradesSelected2);
        }

        GradeGridTitle title = new GradeGridTitle(ElementUtil.randomId(), subjectRow.getI18nText(), true);

        columnsArray[0].addChild(title);
        columnsArray[1].addChild(addLangs);
        columnsArray[2].addChild(grades);
        if (gradesSelected2 != null && gradesSelected != null) {
            columnsArray[3].addChild(gradesSelected);
            columnsArray[4].addChild(gradesSelected2);
        }

        GradeGridRow gradeGridRow = ElementUtil.createHiddenGradeGridRowWithId("additionalRow-" + subjectRow.getId());
        gradeGridRow.addChild(columnsArray);
        gradeGridRow.addAttribute("data-group", group);

        return gradeGridRow;
    }


    Element[] createColumnsArray(boolean removable, final String idPrefix) {
        if (gradeGridHelper.isComprehensiveSchool()) {
            return new Element[]{
                    new GradeGridColumn(idPrefix + "_column1", removable),
                    new GradeGridColumn(idPrefix + "_column2", false),
                    new GradeGridColumn(idPrefix + "_column3", false),
                    new GradeGridColumn(idPrefix + "_column4", false),
                    new GradeGridColumn(idPrefix + "_column5", false),
            };
        } else {
            return new Element[]{
                    new GradeGridColumn(idPrefix + "_column1", removable),
                    new GradeGridColumn(idPrefix + "_column2", false),
                    new GradeGridColumn(idPrefix + "_column3", false)
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

    GradeGridRow createGradeGridRow(final SubjectRow subjectRow, boolean language, boolean literature,
                                    final String formMessages, final String formErrors, final String verboseHelps) {

        GradeGridRow gradeGridRow = new GradeGridRow(subjectRow.getId());
        String id = gradeGridHelper.getIdPrefix() + subjectRow.getId();
        Element[] columns = createColumnsArray(false, id);

        columns[0].addChild(new GradeGridTitle(System.currentTimeMillis() + "", subjectRow.getI18nText(), false));

        if (subjectRow.isLanguage() || language) {
            List<Option> subjectLanguages;
            GradeGridOptionQuestion child;
            if (literature) {
                subjectLanguages = gradeGridHelper.getLanguageAndLiterature();
                child = new GradeGridOptionQuestion(id + "_OPPIAINE", subjectLanguages, false, true, "fi_vm_sade_oppija_language");
            } else {
                subjectLanguages = new ArrayList<Option>();
                subjectLanguages.addAll(gradeGridHelper.getSubjectLanguages());
                child = new GradeGridOptionQuestion(id + "_OPPIAINE", subjectLanguages, false, true, null);
            }
            ElementUtil.addRequiredValidator(child, formErrors);
            columns[1].addChild(child);
        } else {
            columns[0].addAttribute("colspan", "2");
        }
        GradeGridOptionQuestion child1 = new GradeGridOptionQuestion(id, gradeGridHelper.getGradeRanges(), false, false, null);
        ElementUtil.addRequiredValidator(child1, formErrors);
        columns[2].addChild(child1);

        gradeGridRow.addChild(columns[0]);
        if (subjectRow.isLanguage() || language) {
            gradeGridRow.addChild(columns[1]);
        }
        gradeGridRow.addChild(columns[2]);
        if (gradeGridHelper.isComprehensiveSchool()) {
            GradeGridOptionQuestion gradeGridOptionQuestion = new GradeGridOptionQuestion(id + "_VAL1", gradeGridHelper.getGradeRangesWithDefault(), true, false, null);
            ElementUtil.addRequiredValidator(gradeGridOptionQuestion, formErrors);
            columns[3].addChild(gradeGridOptionQuestion);

            GradeGridOptionQuestion child2 = new GradeGridOptionQuestion(id + "_VAL2", gradeGridHelper.getGradeRangesWithDefault(), true, false, null);
            ElementUtil.addRequiredValidator(child2, formErrors);
            columns[4].addChild(child2);

            gradeGridRow.addChild(columns[3]);
            gradeGridRow.addChild(columns[4]);
        }
        return gradeGridRow;

    }
}
