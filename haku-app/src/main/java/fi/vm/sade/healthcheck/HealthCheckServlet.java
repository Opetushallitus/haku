package fi.vm.sade.healthcheck;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.servlet.annotation.WebServlet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Antti Salonen
 */
//@WebServlet(urlPatterns = {"/healthcheck"}, loadOnStartup = 1)
public class HealthCheckServlet extends AbstractHealthCheckServlet {


    private final MongoTemplate mongo;

    @Autowired
    public HealthCheckServlet(final MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    protected Map<String, Object> checkHealthAndReturnStats() {
        // check mongo connection and return number of applications in system
        final int applications = mongo.getCollection("application").find((DBObject) JSON.parse("{'oid' : {$exists : 1}, 'state' : 'ACTIVE'}")).count();
        return new HashMap<String, Object>() {{
            put("applications", applications);
        }};
    }

}
