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

package fi.vm.sade.oppija.haku.selenium;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.SeleniumContainer;
import fi.vm.sade.oppija.haku.dao.TestDBFactoryBean;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.it.TomcatContainerBase;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @author jukka
 * @version 10/15/121:13 PM}
 * @since 1.1
 */

public abstract class AbstractSeleniumBase extends TomcatContainerBase {

    protected SeleniumHelper seleniumHelper;

    @Autowired
    SeleniumContainer container;

    @Autowired
    TestDBFactoryBean dbFactory;

    public AbstractSeleniumBase() {
        super();
    }

    @Before
    public void before() {
        Set<String> collectionNames = dbFactory.getObject().getCollectionNames();
        for (String collectionName : collectionNames) {
            if (collectionName.contains("test")) {
                dbFactory.getObject().getCollection(collectionName).drop();
            }
        }
        seleniumHelper = container.getSeleniumHelper();
        seleniumHelper.logout();
    }

    protected FormModelHelper initModel(FormModel formModel1) {

        final AdminEditPage adminEditPage = new AdminEditPage(getBaseUrl(), seleniumHelper);
        seleniumHelper.navigate(adminEditPage);
        adminEditPage.login("admin");
        seleniumHelper.navigate(adminEditPage);
        adminEditPage.submitForm(formModel1);
        seleniumHelper.logout();
        return new FormModelHelper(formModel1);
    }


}
