package fi.vm.sade.oppija.haku.tools;

import fi.vm.sade.oppija.lomake.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.tools.CommandExecutor;

import java.io.File;

/**
 * @author jukka
 * @version 10/5/1210:56 AM}
 * @since 1.1
 */
public class TestModelExporter {


    public static void main(String[] args) {
        new TestCommandLineTooling(new String[]{"export", "."}).execute();
    }

    private static class TestCommandLineTooling extends CommandExecutor {

        private TestCommandLineTooling(String[] args) {
            super(args);
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
