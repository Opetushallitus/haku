package fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import org.codehaus.jackson.annotate.JsonProperty;

public class GradeGridAddLang extends Titled {

    public GradeGridAddLang(@JsonProperty(value = "id") final String id,
                            @JsonProperty(value = "i18nText") final I18nText i18nText) {
        super(id, i18nText);

    }
}
