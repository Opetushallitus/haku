package fi.vm.sade.oppija.haku.dao;


import com.mongodb.DBObject;

import java.util.Map;

public interface ApplicationPeriodDAO {

    void insert(Map<String, Object> map);

    Map find(String applicationPeriodId);

}
