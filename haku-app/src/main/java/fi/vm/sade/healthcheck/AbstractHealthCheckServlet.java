package fi.vm.sade.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Implement this servlet to add "oph standard" healthcheck-feature to web application.
 * Serve from path [webapp]/healthcheck
 * Implement necessary checks in checkHealthAndReturnStats -method (eg. check database, infra and service connections).
 * Also return relevant stats/status data (eg. 'status: indexing 50%' or 'numberOfOrganizations:150').
 * Note that /healthcheck will be polled (with eg 1 minute interval).
 *
 * @author Antti Salonen
 */
public abstract class AbstractHealthCheckServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckServlet.class);

    @Override
    public void init() throws ServletException {
        log.info("inited healthcheck servlet");
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // autowire spring deps
            WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            ctx.getAutowireCapableBeanFactory().autowireBean(this);
            log.info("autowired healthcheck servlet");

            PrintWriter writer = resp.getWriter();
            Map<String,Object> stats = checkHealthAndReturnStats();
            log.info("healthcheck stats: {}", stats);
            writer.print(encodeJson(stats));
        } catch (Throwable e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(resp.getWriter());
        }
    }

    public String encodeJson(Map map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (map != null) {
            int i=0;
            for (Object key : map.keySet()) {
                Object val = map.get(key);
                if (val != null) {
                    if (i++>0) {
                        sb.append(", ");
                    }
                    String valAsJson = val instanceof Map ? encodeJson((Map)val) : "\""+val.toString().replaceAll("\"", "\\\\\"")+"\"";
                    sb.append("\"").append(key).append("\": ").append(valAsJson);
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }

    protected abstract Map<String,Object> checkHealthAndReturnStats();
}
