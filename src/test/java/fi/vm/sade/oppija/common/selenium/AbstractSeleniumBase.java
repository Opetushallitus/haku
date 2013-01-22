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

package fi.vm.sade.oppija.common.selenium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.SeleniumContainer;
import fi.vm.sade.oppija.lomake.dao.TestDBFactoryBean;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.common.it.TomcatContainerBase;
import fi.vm.sade.oppija.ui.selenium.SeleniumHelper;
import org.junit.Before;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jukka
 * @version 10/15/121:13 PM}
 * @since 1.1
 *        <p/>
 *        NOTE: NO OTHER CONTEXT DEFINITIONS here, do not mix test context with
 *        it-context
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
        dbFactory.drop();
        seleniumHelper = container.getSeleniumHelper();
        seleniumHelper.logout();
    }

    protected FormModelHelper initModel(FormModel formModel1) {

        final AdminEditPage adminEditPage = new AdminEditPage(getBaseUrl(), seleniumHelper);
        seleniumHelper.navigate(adminEditPage);
        adminEditPage.login("admin");
        seleniumHelper.navigate(adminEditPage);
        adminEditPage.submitForm(formModel1);
        seleniumHelper.getDriver().get(getBaseUrl() + "/" + "admin/index/update");
        seleniumHelper.logout();
        return new FormModelHelper(formModel1);
    }
    
    protected void screenshot(String suffix) {
        WebDriver driver = seleniumHelper.getDriver();
        String filename = "target/" + this.getClass().getSimpleName() + "_" +suffix + ".png";
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File dstFile = new File(filename);
        FileChannel source = null;
        FileChannel destination = null;
        try {
            if (!dstFile.exists()) {
                dstFile.createNewFile();
            }
            source = new FileInputStream(srcFile).getChannel();
            destination = new FileOutputStream(dstFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
