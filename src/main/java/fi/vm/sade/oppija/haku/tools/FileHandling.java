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

package fi.vm.sade.oppija.haku.tools;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileHandling {

    public static final Logger LOG = LoggerFactory.getLogger(FileHandling.class);


    public String readFile(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    public String readFile(File file) {
        StringWriter stringWriter = new StringWriter();
        try {
            IOUtils.copy(new FileInputStream(file), stringWriter);
        } catch (IOException e) {
            LOG.info("Error reading file", e);
        }
        return stringWriter.toString();
    }

    public void writeFile(String filename, String contentAsString) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(filename));
            fileWriter.write(contentAsString);
        } catch (IOException e) {
            LOG.error("Error writing file", e);
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }
}
