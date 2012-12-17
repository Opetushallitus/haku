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

package fi.vm.sade.oppija.lomake.converter;

import com.google.common.base.Function;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import org.springframework.stereotype.Component;

/**
 * @author jukka
 * @version 9/14/123:44 PM}
 * @since 1.1
 */
@Component
public class FormModelToJsonString implements Function<FormModel, String> {
    @Override
    public String apply(FormModel formModel) {
        return JSON.serialize(new FormModelToMap().apply(formModel));
    }
}
