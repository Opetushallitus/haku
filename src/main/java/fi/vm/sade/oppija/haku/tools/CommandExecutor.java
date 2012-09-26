package fi.vm.sade.oppija.haku.tools;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDAOMongoImpl;
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
    public static final Logger log = LoggerFactory.getLogger(CommandLineTooling.class);
    private final List<String> args;

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
        log.info("USAGE CommandLineTooling [command] [file]");
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
            log.info("Export destination must be a directory");
            return;
        }
        final DBCursor dbObjects = getService().getAll();
        for (DBObject dbObject : dbObjects) {
            final String contentAsString = JSON.serialize(dbObject);
            final String filename = createFilename(file, dbObject);
            log.info("writing file: " + filename);
            write(contentAsString, filename);
        }

    }

    protected void write(String contentAsString, String filename) {
        new FileHandling().writeFile(filename, contentAsString);
    }

    protected String createFilename(File file, DBObject dbObject) {
        final ObjectId objectId = (ObjectId) dbObject.get("_id");
        final String id = (String) dbObject.get("id");
        return file.getPath() + "/" + objectId.toStringMongod() + "_" + id + ".json";
    }

    protected void importAll() {
        for (String arg : args) {
            read(arg);
        }
    }

    protected boolean fileExists(String arg) {
        return new File(arg).exists();
    }

    protected void read(String filename) {
        final String content = getStringBuilder(filename);
        log.info("inserting file " + filename);
        doInsert(content);
        log.info("done");
    }

    protected void doInsert(final String jsonString) {
        final FormModelDAOMongoImpl formModelDAOMongoImpl = getService();
        formModelDAOMongoImpl.insertModelAsJsonString(jsonString);
    }

    protected String getStringBuilder(String filename) {
        return new FileHandling().readStreamFromFile(filename);
    }

    protected FormModelDAOMongoImpl getService() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring/database-context.xml");
        return applicationContext.getBean("formModelDAOMongoImpl", FormModelDAOMongoImpl.class);
    }

    private enum Command {
        IMPORT, EXPORT
    }
}
