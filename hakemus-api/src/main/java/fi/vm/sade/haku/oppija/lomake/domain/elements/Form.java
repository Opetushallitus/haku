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

package fi.vm.sade.haku.oppija.lomake.domain.elements;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.exception.ElementNotFound;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
public class Form extends Titled {

    private static final long serialVersionUID = 8083152169717295356L;

    private transient Map<String, Element> allChildElements;

    public Form(final String id, final I18nText i18nText) {
        super(id, i18nText);
    }

    @Transient
    public synchronized Element getChildById(String id) {
        if(allChildElements == null) {
            allChildElements = new HashMap<>();
            addToCache(this);
        }
        if(allChildElements.containsKey(id)) {
            return allChildElements.get(id);
        }
        throw new ElementNotFound(id);
    }

    private void addToCache(final Element element) {
        allChildElements.put(element.getId(), element);
        for (Element child : element.getChildren()) {
            addToCache(child);
        }
    }
}
