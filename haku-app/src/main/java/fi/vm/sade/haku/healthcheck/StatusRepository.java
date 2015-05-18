package fi.vm.sade.haku.healthcheck;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StatusRepository {



    enum OperationState {
        START, HALTED, DONE, ERROR
    }

    void endSchedulerRun(String operation);

    void haltSchedulerRun(String operation);

    void schedulerError(String operation, String message);

    List<Map<String, String>> read();

    void recordLastSuccess(String operation, Date started);

    Date getLastSuccessStarted(String operation);

    void startSchedulerRun(String operation);

    void startOperation(String operation, String operationTarget);

    void endOperation(String operation, String operationTarget);
}
