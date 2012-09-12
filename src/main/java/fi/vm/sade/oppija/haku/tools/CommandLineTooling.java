package fi.vm.sade.oppija.haku.tools;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDAOMongoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * @author jukka
 * @version 9/11/123:27 PM}
 * @since 1.1
 */
public class CommandLineTooling {

    private enum Command {
        IMPORT, EXPORT


    }

    private final static Logger log = LoggerFactory.getLogger(CommandLineTooling.class);

    public static void main(String[] args) {

        if (args.length < 2 || !new File(args[1]).exists()) {
            System.err.print("USAGE CommandLineTooling [command] [file]");
            System.exit(1);
        }
        Command command = Command.valueOf(args[0].toUpperCase());
        switch (command) {
            case IMPORT: {
                importAll(args);
            }
            case EXPORT:
                exportAll(args);
        }
    }

    private static void exportAll(String[] args) {
        final boolean directory = new File(args[1]).isDirectory();
        if (!directory) {
            System.err.println("Export destination must be a directory");
            System.exit(1);
        }
        final DBCursor dbObjects = getService().getCollection().find();
        for (DBObject dbObject : dbObjects) {
            final String serialize = JSON.serialize(dbObject);
            System.out.println(serialize);
            //TODO:write file
        }

    }

    private static void importAll(String[] args) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if (!new File(arg).exists()) {
                System.err.println("File not found");
                System.exit(1);
            }

            read(arg);
        }
    }

    private static void read(String filename) {
        final FormModelDAOMongoImpl formModelDAOMongoImpl = getService();
        final StringBuilder stringBuilder = FileHandling.readStreamFromFile(filename);
        final String s = stringBuilder.toString();
        log.info("inserting file " + filename);
        log.debug("with content " + s);
        formModelDAOMongoImpl.getCollection().insert((DBObject) JSON.parse(s));
        log.info("done");
    }

    private static FormModelDAOMongoImpl getService() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring/database-context.xml");
        return applicationContext.getBean("formModelDAOMongoImpl", FormModelDAOMongoImpl.class);
    }

}
