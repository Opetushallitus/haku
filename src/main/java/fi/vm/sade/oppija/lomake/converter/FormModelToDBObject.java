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
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.lomake.domain.FormModel;

import java.util.Map;

/**
 * @author jukka
 * @version 9/12/124:29 PM}
 * @since 1.1
 */
public class FormModelToDBObject implements Function<FormModel, DBObject> {

    private final FormModelToMap formModelToMapConverter = new FormModelToMap();

    @Override
    public DBObject apply(final FormModel formModel) {
        Map formModelMap = formModelToMapConverter.apply(formModel);
        return new BasicDBObject(formModelMap);
    }


}
