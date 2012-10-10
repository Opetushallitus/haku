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

package fi.vm.sade.oppija.tarjonta.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Infobox {
    private final String header;
    private final List<InfoboxItem> items = new ArrayList<InfoboxItem>();
    private final String footer;

    public Infobox(final String header, final String footer) {
        this.header = header;
        this.footer = footer;
    }
    public void addInfoboxItem(final InfoboxItem infoboxItem) {
        this.items.add(infoboxItem);
    }

    public String getHeader() {
        return header;
    }

    public List<InfoboxItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public String getFooter() {
        return footer;
    }
}
