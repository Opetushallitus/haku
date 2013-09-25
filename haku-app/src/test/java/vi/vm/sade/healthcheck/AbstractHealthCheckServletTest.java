package vi.vm.sade.healthcheck;

import fi.vm.sade.healthcheck.AbstractHealthCheckServlet;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Antti Salonen
 */
public class AbstractHealthCheckServletTest {

    @Test
    public void testEncodeJson() {
        HashMap<String, Object> map = new HashMap<String, Object>() {{
            put("applications", 1);
            put("nullukka", null);
            put("mäppi", new HashMap<String, Object>() {{
                put("inner", "inner\"val");
            }});
        }};
        Assert.assertEquals("{\"applications\": \"1\", \"mäppi\": {\"inner\": \"inner\\\"val\"}}", new AbstractHealthCheckServlet(){
            protected Map<String, Object> checkHealthAndReturnStats() {
                return null;
            }
        }.encodeJson(map));
    }

}
