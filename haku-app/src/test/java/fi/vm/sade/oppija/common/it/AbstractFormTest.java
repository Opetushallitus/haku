
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

import fi.vm.sade.oppija.common.MongoWrapper;
import fi.vm.sade.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractFormTest extends AbstractRemoteTest {

    @Autowired
    protected MongoWrapper mongoWrapper;

    @Before
    public void setUp2() throws Exception {
        mongoWrapper.dropDatabase();
    }

    @After
    public void tearDown() throws Exception {
        mongoWrapper.dropDatabase();

    }

    protected ApplicationSystemHelper updateModelAndCreateFormModelHelper(final ApplicationSystem applicationSystem) {
        adminResourceClient.updateApplicationSystem(applicationSystem);
        return new ApplicationSystemHelper(applicationSystem);
    }

}
