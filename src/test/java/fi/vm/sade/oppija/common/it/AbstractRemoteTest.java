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
import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/13/123:42 PM}
 * @since 1.1
 */
public abstract class AbstractRemoteTest extends TomcatContainerBase {

    public AbstractRemoteTest() {
    }

    protected FormModelHelper initModel(FormModel formModel1) {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        setBaseUrl(getBaseUrl());
        beginAt("/admin/edit");
        login("admin");
        gotoPage("/admin/edit");
        final String convert = new FormModelToJsonString().apply(formModel1);
        setTextField("model", convert);
        submit("tallenna");
        return new FormModelHelper(formModel1);
    }

}
