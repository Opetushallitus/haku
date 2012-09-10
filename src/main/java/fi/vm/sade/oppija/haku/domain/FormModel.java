package fi.vm.sade.oppija.haku.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:25 AM}
 * @since 1.1
 */
public class FormModel {

    final Map<String, ApplicationPeriod> applicationPerioidMap;

    public FormModel() {
        this.applicationPerioidMap = new HashMap<String, ApplicationPeriod>();
    }

    public FormModel(Map<String, ApplicationPeriod> applicationPeriods) {
        this.applicationPerioidMap = applicationPeriods;
    }

    ApplicationPeriod getApplicationPeriodById(String id) {
        return applicationPerioidMap.get(id);
    }

    public void addApplicationPeriod(ApplicationPeriod applicationPeriod) {
        applicationPerioidMap.put(applicationPeriod.getId(), applicationPeriod);
    }
}
