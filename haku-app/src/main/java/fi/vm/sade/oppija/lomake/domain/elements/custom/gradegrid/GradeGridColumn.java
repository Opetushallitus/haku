package fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid;

import fi.vm.sade.oppija.lomake.domain.elements.Element;

public class GradeGridColumn extends Element {

    private final boolean removable;

    public GradeGridColumn(final String id, final boolean removable) {
        super(id);
        this.removable = removable;
    }

    public boolean isRemovable() {
        return removable;
    }
}
