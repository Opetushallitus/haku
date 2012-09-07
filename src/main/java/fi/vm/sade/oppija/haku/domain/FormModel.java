package fi.vm.sade.oppija.haku.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:25 AM}
 * @since 1.1
 */
public class FormModel {
    Map<String, ApplicationPeriod> applicationPerioidMap = new HashMap<String, ApplicationPeriod>();

    ApplicationPeriod getApplicationPeriodById(String id) {
        return applicationPerioidMap.get(id);
    }
}
