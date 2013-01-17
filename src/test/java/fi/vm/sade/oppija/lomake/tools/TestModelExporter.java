package fi.vm.sade.oppija.lomake.tools;

import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.converter.FormModelToMap;
import fi.vm.sade.oppija.lomake.dao.FormModelDAO;
import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.lomake.domain.FormModel;

import java.io.File;

/**
 * @author jukka
 * @version 10/5/1210:56 AM}
 * @since 1.1
 */
public class TestModelExporter {

    public static void main(String[] args) {
        new TestCommandLineTooling(new String[] { "export", "." }).execute();
    }

    private static class TestCommandLineTooling extends CommandExecutor {

        private TestCommandLineTooling(String[] args) {
            super(args);
        }

        protected void export(File file) {
            System.out.println(new FormModelToMap().apply(new FormModel()));
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
