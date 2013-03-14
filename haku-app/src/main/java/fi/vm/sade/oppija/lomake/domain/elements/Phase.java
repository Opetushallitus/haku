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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class Phase extends Titled {

    private static final long serialVersionUID = 1369853692287570194L;
    private transient Phase next;
    private transient Phase prev;
    private boolean preview;

    public Phase(@JsonProperty(value = "id") String id,
                 @JsonProperty(value = "i18nText") I18nText i18nText,
                 @JsonProperty(value = "preview") final boolean preview) {
        super(id, i18nText);
        this.preview = preview;
    }

    public void setNext(Phase element) {
        this.next = element;
    }

    public void setPrev(Phase prev) {
        this.prev = prev;
    }

    public void initChain(Phase prev) {
        if (prev != null) {
            setPrev(prev);
            prev.setNext(this);
        }
    }

    @JsonIgnore
    public boolean isHasNext() {
        return next != null;
    }

    @JsonIgnore
    public boolean isHasPrev() {
        return prev != null;
    }

    @JsonIgnore
    public Phase getNext() {
        return next;
    }

    @JsonIgnore
    public Phase getPrev() {
        return prev;
    }

    public boolean isPreview() {
        return preview;
    }
}
