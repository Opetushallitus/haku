package fi.vm.sade.oppija.haku.tools;

import org.junit.Test;

import java.util.ArrayList;

/**
 * @author jukka
 * @version 9/21/122:03 PM}
 * @since 1.1
 */
public class CommandExecutorTest {
    @Test
    public void testExport() throws Exception {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add("export");
        strings.add(System.getProperty("java.io.tmpdir"));
        new MockCommandExecutor(strings.toArray(new String[strings.size()])).execute();
    }

    @Test
    public void testImportNoValid() throws Exception {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add("import");
        strings.add("foo");
        final String[] args = strings.toArray(new String[strings.size()]);
        new CommandExecutor(args).execute();
    }

    @Test
    public void testImportWithMock() throws Exception {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add("import");
        strings.add("foo");
        new MockCommandExecutor(strings.toArray(new String[strings.size()])).execute();
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
