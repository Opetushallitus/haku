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

import fi.vm.sade.oppija.lomake.domain.FormModel;
import org.junit.Test;

public class CommandExecutorTest {

    public static final String PROPERTY_JAVA_IO_TMPDIR = System.getProperty("java.io.tmpdir");
    public static final String EXPORT = "export";
    public static final String IMPORT = "import";
    public static final String FILENAME_FOO = "foo";

    @Test
    public void testImportWithValid() throws Exception {
        new CommandExecutor(createArgs(IMPORT, "src/test/resources/test-data.json")).execute();
    }

    @Test
    public void testExport() throws Exception {
        new MockCommandExecutor(createArgs(EXPORT, PROPERTY_JAVA_IO_TMPDIR)).execute();
    }

    @Test
    public void testImportNoValid() throws Exception {
        new CommandExecutor(createImportFooArgs()).execute();
    }

    @Test(expected = RuntimeException.class)
    public void testImportWithMock() throws Exception {
        new MockCommandExecutor(createImportFooArgs()).execute();
    }

    private String[] createImportFooArgs() {
        return createArgs(IMPORT, FILENAME_FOO);
    }

    private String[] createArgs(final String action, final String filename) {
        final String[] args = new String[2];
        args[0] = action;
        args[1] = filename;
        return args;
    }


    private class MockCommandExecutor extends CommandExecutor {
        public MockCommandExecutor(String[] args) {
            super(args);
        }

        @Override
        protected boolean fileExists(String arg) {
            return true;
        }

        @Override
        protected void doInsert(final FormModel formModel) {
        }


        @Override
        protected void write(String contentAsString, String filename) {

        }
    }

}
