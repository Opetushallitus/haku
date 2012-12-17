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

package fi.vm.sade.oppija.lomake.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;

/**
 * Table element with data sorting functionality
 *
 * @author Mikko Majapuro
 */
public class SortableTable extends Titled {

    // label text for up button
    private String moveUpLabel;
    // label text for down button
    private String moveDownLabel;

    public SortableTable(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                         @JsonProperty(value = "moveUpLabel") String moveUpLabel,
                         @JsonProperty(value = "moveDownLabel") String moveDownLabel) {
        super(id, title);
        this.moveUpLabel = moveUpLabel;
        this.moveDownLabel = moveDownLabel;
    }

    public String getMoveUpLabel() {
        return moveUpLabel;
    }

    public String getMoveDownLabel() {
        return moveDownLabel;
    }
}
