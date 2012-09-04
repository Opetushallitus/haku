package fi.vm.sade.oppija.haku.service;


import fi.vm.sade.oppija.haku.dao.ApplicationPeriodDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationPeriodService {


    final ApplicationPeriodDAO applicationPeriodDAO;

    @Autowired
    public ApplicationPeriodService(final ApplicationPeriodDAO applicationPeriodDAO) {
        this.applicationPeriodDAO = applicationPeriodDAO;
    }

    public List<Map<String, Object>> getApplicationPeriods() {
        return new ArrayList<Map<String, Object>>();
    }

    public Map<String, Object> getApplicationPeriod(final String applicationPeriodId) {
        return applicationPeriodDAO.find(applicationPeriodId);
    }

}
