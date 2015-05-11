package fi.vm.sade.haku.healthcheck;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StatusRepository {
    List<Map<String, String>> read();

    void write (String operation);

    void write (String operation, Map<String, String> statusData);

    void recordLastSuccess(String operation, Date started);

    Date getLastSuccessStarted(String operation);
}
