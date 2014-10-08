package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate.ComprehensiveSchools;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate.HighSchools;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate.Ids;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate.Languages;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;

import java.util.Arrays;
import java.util.List;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.addRequiredValidator;

public class GradeGridHelper {

    private final List<SubjectRow> subjects;
    private final List<Option> subjectLanguages;
    private final List<Option> gradeRanges;
    private final List<Option> languageAndLiterature;
    private final List<SubjectRow> nativeLanguages;
    private final boolean comprehensiveSchool;
    private final List<Option> gradeRangesWithDefault;
    private List<SubjectRow> additionalNativeLanguages;
    private Ordering<SubjectRow> ordering = new Ordering<SubjectRow>() {
        List<String> order = Arrays.asList("A1", "B1", "MA", "BI", "GE", "FY", "KE", "TE", "KT", "HI", "YH", "MU", "KU", "KS", "LI", "KO", "FI", "PS");

        public int compare(SubjectRow o1, SubjectRow o2) {
            return Integer.valueOf(order.indexOf(o1.getId())).compareTo(order.indexOf(o2.getId()));
        }
    };

    public GradeGridHelper(final boolean comprehensiveSchool, final FormParameters formParameters) {
        this.comprehensiveSchool = comprehensiveSchool;
        KoodistoService koodistoService = formParameters.getKoodistoService();
        subjects = getHighOrComprehensiveSchoolSubjectRows(comprehensiveSchool, koodistoService.getSubjects());
        for (SubjectRow subject : subjects) {
            addRequiredValidator(subject, formParameters);
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

    public List<SubjectRow> getDefaultLanguages(final boolean isSv) {
        Predicate predicate = isSv
                ? new Ids<SubjectRow>("A1", "A2")
                : new Ids<SubjectRow>("A1", "B1");
        return ordering.immutableSortedCopy(Iterables.filter(subjects, predicate));
    }

    public List<SubjectRow> getAdditionalLanguages(final boolean isSv) {
        Predicate predicate = isSv
                ? new Ids<SubjectRow>("A1", "A2")
                : new Ids<SubjectRow>("A1", "B1");
        return ImmutableList.copyOf(
                Iterables.filter(
                        Iterables.filter(subjects, new Languages()),
                        Predicates.not(predicate)));
    }

    public List<SubjectRow> getNotLanguageSubjects() {
        return ordering.immutableSortedCopy(
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
