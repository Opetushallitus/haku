package fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid;

import com.google.common.base.Preconditions;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import java.util.List;

public class GradeGridOptionQuestion extends Element {


    private final List<Option> gradeRange;
    private final boolean selected;

    @PersistenceConstructor
    public GradeGridOptionQuestion(final String id,
                                   final List<Option> gradeRange,
                                   final boolean selected) {
        super(id);
        addAttribute("name", id);
        Preconditions.checkNotNull(gradeRange);
        Preconditions.checkNotNull(selected);
        this.gradeRange = gradeRange;
        this.selected = selected;
    }

    public List<Option> getGradeRange() {
        return gradeRange;
    }

    @Transient
    public List<Option> getOptions() {
        return gradeRange;
    }


    public boolean isSelected() {
        return selected;
    }
}
