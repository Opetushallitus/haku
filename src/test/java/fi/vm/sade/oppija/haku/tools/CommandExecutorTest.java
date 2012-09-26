package fi.vm.sade.oppija.haku.tools;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jukka
 * @version 9/21/122:03 PM}
 * @since 1.1
 */
public class CommandExecutorTest {

    public static final String PROPERTY_JAVA_IO_TMPDIR = System.getProperty("java.io.tmpdir");
    public static final String EXPORT = "export";
    public static final String IMPORT = "import";
    public static final String FILENAME_FOO = "foo";

    @Test
    public void testExport() throws Exception {
        new MockCommandExecutor(createArgs(EXPORT, PROPERTY_JAVA_IO_TMPDIR)).execute();
    }

    @Test
    public void testImportNoValid() throws Exception {
        new CommandExecutor(createImportFooArgs()).execute();
    }

    @Test
    @Ignore
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
        protected void doInsert(final String json) {
        }


        @Override
        protected void write(String contentAsString, String filename) {

        }
    }

}
