package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.ComprehensiveSchools;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.HighSchools;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.Ids;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate.Languages;

import java.util.List;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.addRequiredValidator;

public class GradeGridHelper {

    private final List<SubjectRow> subjects;
    private final List<Option> subjectLanguages;
    private final List<Option> gradeRanges;
    private final List<Option> languageAndLiterature;
    private final List<SubjectRow> nativeLanguages;
    private final boolean comprehensiveSchool;
    private final List<Option> gradeRangesWithDefault;
    private List<SubjectRow> additionalNativeLanguages;

    public GradeGridHelper(final KoodistoService koodistoService, final boolean comprehensiveSchool) {
        this.comprehensiveSchool = comprehensiveSchool;
        subjects = getHighOrComprehensiveSchoolSubjectRows(comprehensiveSchool, koodistoService.getSubjects());
        for (SubjectRow subject : subjects) {
            addRequiredValidator(subject);
        }
        subjectLanguages = koodistoService.getSubjectLanguages();
        gradeRanges = koodistoService.getGradeRanges();
        gradeRangesWithDefault = koodistoService.getGradeRanges();
        languageAndLiterature = koodistoService.getLanguageAndLiterature();
        nativeLanguages = ImmutableList.copyOf(Iterables.filter(subjects, new Ids<SubjectRow>("AI")));
        additionalNativeLanguages = ImmutableList.copyOf(Iterables.filter(subjects, new Ids<SubjectRow>("AI2")));
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

    public List<SubjectRow> getNativeLanguages() {
        return nativeLanguages;
    }

    public List<SubjectRow> getAdditionalNativeLanguages() {
        return additionalNativeLanguages;
    }

    public List<SubjectRow> getDefaultLanguages() {
        return ImmutableList.copyOf(Iterables.filter(subjects, new Ids<SubjectRow>("A1", "B1")));
    }

    public List<SubjectRow> getAdditionalLanguages() {
        return ImmutableList.copyOf(
                Iterables.filter(
                        Iterables.filter(subjects, new Languages()),
                        Predicates.not(new Ids<SubjectRow>("A1", "B1"))));
    }

    public List<SubjectRow> getNotLanguageSubjects() {
        return ImmutableList.copyOf(
                Iterables.filter(Iterables.filter(subjects, Predicates.not(new Languages())),
                        Predicates.not(new Ids<SubjectRow>("AI", "AI2"))));

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
}
