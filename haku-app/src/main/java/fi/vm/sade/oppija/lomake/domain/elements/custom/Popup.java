package fi.vm.sade.oppija.lomake.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import org.codehaus.jackson.annotate.JsonProperty;

public class Popup extends Titled {

    private static final long serialVersionUID = -5061797006494502983L;

    public Popup(@JsonProperty(value = "id") final String id,
                 @JsonProperty(value = "i18nText") final I18nText i18nText) {
        super(id, i18nText);
    }

}
