package fi.vm.sade.haku.oppija.postprocess;

import java.util.Date;

public interface EligibilityCheckWorker {

    void checkEligibilities(Date since);

}
