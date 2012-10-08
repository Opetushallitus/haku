package fi.vm.sade.oppija.haku.tools;

import fi.vm.sade.oppija.haku.converter.FormModelToJsonString;
import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDAOMongoImpl;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.FormModelFactory;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CommandExecutor {
    public static final Logger LOG = LoggerFactory.getLogger(CommandLineTooling.class);
    private List<String> args;

    public CommandExecutor(String[] args) {
        this.args = Arrays.asList(args);
    }

    public void execute() {
        if (args.size() < 2 || !fileExists(args.get(1))) {
            usage();
            return;
        }
        createCommand();
    }

    private void usage() {
        LOG.info("USAGE CommandLineTooling [command] [file]");
    }

    protected void createCommand() {
        Command command = Command.valueOf(args.get(0).toUpperCase(Locale.getDefault()));
        switch (command) {
            case IMPORT: {
                importAll();
                break;
            }
            case EXPORT:
                exportAll(args.get(1));
                break;
            default:
                break;
        }
    }

    protected void exportAll(String foldername) {
        final File file = new File(foldername);
        final boolean directory = file.isDirectory();
        if (!directory) {
            usage();
            LOG.info("Export destination must be a directory");
            return;
        }
        export(file);

    }

    protected void export(File file) {
        final FormModel model = getService().find();
        final String contentAsString = new FormModelToJsonString().convert(model);
        final String filename = createFilename(file, model);
        LOG.info("writing file: " + filename);
        write(contentAsString, filename);
    }

    protected void write(String contentAsString, String filename) {
        new FileHandling().writeFile(filename, contentAsString);
    }

    protected String createFilename(File folder, FormModel dbObject) {
        final ObjectId objectId = dbObject.get_id();
        return folder.getPath() + "/" + objectId.toStringMongod() + ".json";
    }

    protected void importAll() {
        for (String arg : args.subList(1, args.size())) {
            read(arg);
        }
    }

    protected boolean fileExists(String arg) {
        return new File(arg).exists();
    }

    protected void read(String filename) {
        final FormModel content = getModel(filename);
        LOG.info("inserting file " + filename);
        doInsert(content);
        LOG.info("done");
    }

    protected void doInsert(final FormModel formModel) {
        final FormModelDAO formModelDAOMongoImpl = getService();
        formModelDAOMongoImpl.insert(formModel);
    }

    protected FormModel getModel(String filename) {
        return FormModelFactory.fromFileName(filename);
    }

    protected FormModelDAO getService() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring/command-line-context.xml");
        return applicationContext.getBean("formModelDAOMongoImpl", FormModelDAOMongoImpl.class);
    }

    private enum Command {
        IMPORT, EXPORT
    }
}
