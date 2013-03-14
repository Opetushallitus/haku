/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.lomake.domain.elements;


import fi.vm.sade.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class Titled extends Element {

    private static final long serialVersionUID = 761433927081818640L;

    private I18nText i18nText;

    // verbose help text that is rendered in a separate help window
    private String verboseHelp;

    public Titled(@JsonProperty(value = "id") final String id,
                  @JsonProperty(value = "i18nText") final I18nText i18nText) {
        super(id);
        this.i18nText = i18nText;
    }

    public I18nText getI18nText() {
        return i18nText;
    }

    public String getVerboseHelp() {
        return verboseHelp;
    }

    public void setVerboseHelp(String verboseHelp) {
        this.verboseHelp = verboseHelp;
    }
}
