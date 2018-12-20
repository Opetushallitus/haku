package fi.vm.sade.haku.oppija.configuration;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.Network;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.Socket;

@Service
@Profile("it")
public class MongoServer {
    private Logger logger = LoggerFactory.getLogger(MongoServer.class);
    private MongodExecutable mongodExecutable;
    private MongodProcess mongod;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public MongoServer(@Value("${mongodb.port:27018}") final String port) throws IOException {
        if (isFreeLocalPort(Integer.parseInt(port))) {
            logger.info("Starting embedded mongo on port " + port);
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.V3_4_15)
                .setParameter("maxBSONDepth", "1000")
                .net(new Net(NumberUtils.toInt(port), Network.localhostIsIPv6()))
                .build();
            MongodStarter runtime = MongodStarter.getInstance(new RuntimeConfigBuilder()
                .defaults(Command.MongoD)
                .processOutput(ProcessOutput.getDefaultInstanceSilent())
                .build());

            mongodExecutable = runtime.prepare(mongodConfig);
            mongod = mongodExecutable.start();

            MongoClient client = new MongoClient("localhost", Integer.parseInt(port));
            client.getDatabaseNames();
            client.close();
        } else {
            logger.info("Not starting embedded mongo: seems to be running on port " + port);
        }
    }

    /**
     * Database reset for tests using this Mongo instance
     */
    public void dropCollections() {
        mongoTemplate.dropCollection("application");
        mongoTemplate.dropCollection("applicationSystem");
        mongoTemplate.dropCollection("formconfiguration");
    }

    @PreDestroy
    public void destroy() {
        if (mongod != null) {
            mongod.stop();
            mongodExecutable.stop();
        }
    }

    public final static boolean isFreeLocalPort(int port) {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", port);
            socket.close();
        } catch (IOException e) {
            return true;
        }
        return false;
    }
}
