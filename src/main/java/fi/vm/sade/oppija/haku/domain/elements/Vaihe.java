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

package fi.vm.sade.oppija.haku.domain.elements;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/1210:28 AM}
 * @since 1.1
 */
public class Vaihe extends Titled {

    private transient Vaihe next;
    private transient Vaihe prev;
    private boolean preview;

    public Vaihe(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title,
                 @JsonProperty(value = "preview") final boolean preview) {
        super(id, title);
        this.preview = preview;
    }

    public void setNext(Vaihe element) {
        this.next = element;
    }

    public void setPrev(Vaihe prev) {
        this.prev = prev;
    }

    public void initChain(Vaihe prev) {
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
    public Vaihe getNext() {
        return next;
    }

    @JsonIgnore
    public Vaihe getPrev() {
        return prev;
    }

    public Link asLink() {
        return new Link(title, id);
    }

    public boolean isPreview() {
        return preview;
    }
}
