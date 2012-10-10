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

package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.haku.converter.JsonStringToFormModelConverter;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * @author jukka
 * @version 9/14/121:44 PM}
 * @since 1.1
 */
public final class FormModelFactory {
    public static final Logger LOG = LoggerFactory.getLogger(FormModelFactory.class);

    private FormModelFactory() {
    }

    public static FormModel fromJSONString(String json) {
        return new JsonStringToFormModelConverter().convert(json);
    }

    public static FormModel fromFileName(String filename) {
        final FileHandling fileHandling = new FileHandling();
        final String content = fileHandling.readFile(new File(filename));
        return new JsonStringToFormModelConverter().convert(content);
    }

    public static FormModel fromClassPathResource(String fileName) {
        return fromClassPathResource(new ClassPathResource(fileName));
    }

    public static FormModel fromClassPathResource(ClassPathResource file) {
        String json = "";
        try {
            json = new FileHandling().readFile(file.getFile());
        } catch (IOException e) {
            LOG.error("", e);
        }
        return new JsonStringToFormModelConverter().convert(json);
    }
}
