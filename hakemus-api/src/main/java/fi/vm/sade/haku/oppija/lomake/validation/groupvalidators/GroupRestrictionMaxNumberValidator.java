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

package fi.vm.sade.haku.oppija.lomake.validation.groupvalidators;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.GroupRestrictionValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupRestrictionMaxNumberValidator extends GroupRestrictionValidator {

    private final int max;

    public GroupRestrictionMaxNumberValidator(final String groupId, final Integer max, final I18nText errorMessage) {
        super(errorMessage, groupId);
        if (max == null || max.intValue() < 1) {
            throw new IllegalArgumentException("Maximum must be non null and positive");
        }
        this.max = max.intValue();
    }

    @Override
    public Map<String, I18nText> validate(Map<String, List<String>>  aoGroups) {
        // TODO HH-175 real implementation
        return new HashMap<String, I18nText>();
    }
}
