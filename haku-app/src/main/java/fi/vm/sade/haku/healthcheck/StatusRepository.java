package fi.vm.sade.haku.healthcheck;

import java.util.List;
import java.util.Map;

/**
 * Created by jteuho on 27.10.14.
 */
public interface StatusRepository {
    List<Map<String, String>> read();

    void write (String operation);

    void write (String operation, Map<String, String> statusData);
}
