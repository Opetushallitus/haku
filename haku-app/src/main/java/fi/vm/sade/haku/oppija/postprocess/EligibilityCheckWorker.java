package fi.vm.sade.haku.oppija.postprocess;

import java.util.Date;

public interface EligibilityCheckWorker {


    String SCHEDULER_ELIGIBILITY_CHECK = "ELIGIBILITY CHECK";

    void checkEligibilities(Date since);

}
