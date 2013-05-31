package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.ComprehensiveSchools;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.HighSchools;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.Ids;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.Languages;

import java.util.List;

public class GradeGridHelper {

    private final List<SubjectRow> subjects;
    private final List<Option> subjectLanguages;
    private final List<Option> gradeRanges;
    private final List<Option> languageAndLiterature;
    private final List<Option> languages;
    private final List<SubjectRow> listOfLanguages;
    private final List<SubjectRow> nativeLanguages;
    private final boolean comprehensiveSchool;
    private final List<Option> gradeRangesWithDefault;

    public GradeGridHelper(final KoodistoService koodistoService, final boolean comprehensiveSchool) {
        this.comprehensiveSchool = comprehensiveSchool;
        subjects = getHighOrComprehensiveSchoolSubjectRows(comprehensiveSchool, koodistoService.getSubjects());
        for (SubjectRow subject : subjects) {
            ElementUtil.setRequired(subject);
        }
        subjectLanguages = koodistoService.getSubjectLanguages();
        gradeRanges = koodistoService.getGradeRanges();
        gradeRangesWithDefault = koodistoService.getGradeRanges();
        ElementUtil.setDefaultOption("Ei arvosanaa", gradeRangesWithDefault); // TODOO kielistys
        languageAndLiterature = koodistoService.getLanguageAndLiterature();
        languages = koodistoService.getLanguages();
        nativeLanguages = ImmutableList.copyOf(Iterables.filter(subjects, new Ids<SubjectRow>("AI1", "AI2")));
        listOfLanguages = ImmutableList.copyOf(Iterables.filter(subjects, new Languages()));
    }

    private List<SubjectRow> getHighOrComprehensiveSchoolSubjectRows(boolean comprehensiveSchool, List<SubjectRow> subjects) {
        List<SubjectRow> filtered;
        if (comprehensiveSchool) {
            filtered = ImmutableList.copyOf(Iterables.filter(subjects,
                    new ComprehensiveSchools()));
        } else {
            filtered = ImmutableList.copyOf(Iterables.filter(subjects,
                    new HighSchools()));
        }
        return filtered;
    }

    public List<SubjectRow> getListOfLanguages() {
        return listOfLanguages;
    }

    public List<SubjectRow> getNativeLanguages() {
        return nativeLanguages;
    }

    public List<SubjectRow> getDefaultLanguages() {
        return ImmutableList.copyOf(Iterables.filter(subjects, new Ids<SubjectRow>("A1", "B1")));
    }

    public List<SubjectRow> getNotLanguageSubjects() {
        return ImmutableList.copyOf(
                Iterables.filter(Iterables.filter(subjects, Predicates.not(new Languages())),
                        Predicates.not(new Ids<SubjectRow>("AI1", "AI2"))));

    }

    public List<SubjectRow> getSubjects() {
        return subjects;
    }

    public String getIdPrefix() {
        return comprehensiveSchool ? "PK_" : "LK_";
    }

    public boolean isComprehensiveSchool() {
        return comprehensiveSchool;
    }

    public List<Option> getGradeRanges() {
        return gradeRanges;
    }

    public List<Option> getGradeRangesWithDefault() {
        return gradeRangesWithDefault;
    }

    public List<Option> getSubjectLanguages() {
        return subjectLanguages;
    }

    public List<Option> getLanguageAndLiterature() {
        return languageAndLiterature;
    }

    public List<Option> getLanguages() {
        return languages;
    }
}
