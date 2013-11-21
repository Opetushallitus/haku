package fi.vm.sade.haku.oppija.configuration;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
@Profile("it")
public class MongoServer {
    private final MongodExecutable mongodExecutable;
    private final MongodProcess mongod;

    @Autowired
    public MongoServer(@Value("${mongodb.port:27018}") final String port) throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(NumberUtils.toInt(port), Network.localhostIsIPv6()))
                .build();
        MongodStarter runtime = MongodStarter.getDefaultInstance();

        mongodExecutable = runtime.prepare(mongodConfig);
        mongod = mongodExecutable.start();

    }

    @PreDestroy
    public void destroy() {
        mongod.stop();
        mongodExecutable.stop();
        mongodExecutable.cleanup();
    }


}
