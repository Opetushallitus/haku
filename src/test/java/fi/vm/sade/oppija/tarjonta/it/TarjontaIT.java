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

package fi.vm.sade.oppija.tarjonta.it;

import fi.vm.sade.oppija.haku.it.TomcatContainerTest;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class TarjontaIT extends TomcatContainerTest {


    protected void initModel() throws IOException {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        setBaseUrl(getBaseUrl());
        beginAt("static-html/add.html");
        ClassPathResource classPathResource = new ClassPathResource("tarjontatieto-test-data.xml");
        String absolutePath = classPathResource.getFile().getAbsolutePath();
        setTextField("upfile", absolutePath);
        submit("Lataa");
    }


    @Test
    public void testTarjonta() throws Exception {
        initModel();
        setScriptingEnabled(false);
        beginAt("/tarjontatiedot");
        setTextField("text", "perustutkinto");
        dumpHtml();
        submit();
        assertLinkPresentWithExactText("sivu 1");
    }

    @Test
    public void testUpdate() throws Exception {
        initModel();
        setScriptingEnabled(false);
        beginAt("/tarjontatiedot?update=true");
        dumpHtml();
        assertCheckboxPresent("koulutustyyppi");
    }

    @Test
    public void testTarjontaLink() throws Exception {
        initModel();
        setScriptingEnabled(false);
        beginAt("/tarjontatiedot/1");
        dumpHtml();
        assertElementPresent("site");
    }
}
