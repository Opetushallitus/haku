/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.lomake.domain.elements.custom;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;

import java.util.HashMap;
import java.util.Map;

public class HigherEducationAttachments extends Titled {

    private Map<String, I18nText> attachmentNotes;

    public HigherEducationAttachments(final String id, final I18nText i18nText) {
        super(id, i18nText);
        attachmentNotes = new HashMap<String, I18nText>();
    }

    public Map<String, I18nText> getAttachmentNotes() {
        return attachmentNotes;
    }

    public void addAttachmentNote(String key, I18nText text) {
        attachmentNotes.put(key, text);
    }
}
