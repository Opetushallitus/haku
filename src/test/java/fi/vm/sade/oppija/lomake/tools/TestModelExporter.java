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

package fi.vm.sade.oppija.lomake.tools;

import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.dao.FormModelDAO;
import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.lomake.domain.FormModel;

import java.io.File;

public class TestModelExporter {

    public static void main(String[] args) {
        new TestCommandLineTooling(new String[]{"export", "."}).execute();
    }

    private static class TestCommandLineTooling extends CommandExecutor {

        private TestCommandLineTooling(String[] args) {
            super(args);
        }

        protected void export(File file) {
            final FormModel model = getService().find(new FormModel()).get(0);
            final String contentAsString = new FormModelToJsonString().applyPretty(model);
            final String filename = createFilename(file, model);
            LOG.info("writing file: " + filename);
            write(contentAsString, filename);
        }

        @Override
        protected FormModelDAO getService() {
            return new FormModelDummyMemoryDaoImpl();
        }

        @Override
        protected String createFilename(File folder, FormModel dbObject) {
            return folder.getAbsolutePath() + "/src/test/resources/test-data.json";
        }
    }

}
