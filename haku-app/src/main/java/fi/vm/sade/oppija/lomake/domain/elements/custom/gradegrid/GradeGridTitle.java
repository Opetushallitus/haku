package fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import org.codehaus.jackson.annotate.JsonProperty;

public class GradeGridTitle extends Titled {

    private boolean removable;

    public GradeGridTitle(@JsonProperty(value = "id") final String id,
                          @JsonProperty(value = "i18nText") final I18nText i18nText,
                          @JsonProperty(value = "removable") final boolean removable) {
        super(id, i18nText);
        this.removable = removable;
    }

    public boolean isRemovable() {
        return removable;
    }
}
