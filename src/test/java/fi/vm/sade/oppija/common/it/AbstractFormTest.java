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

package fi.vm.sade.oppija.common.it;

import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFormTest extends AbstractRemoteTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractFormTest.class);

    public AbstractFormTest() {
    }

    protected FormModelHelper updateModelAndCreateFormModelHelper(FormModel formModel) {
        super.initTestEngine();
        new AdminResourceClient(getBaseUrl()).updateModel(formModel);
        return new FormModelHelper(formModel);
    }

}
