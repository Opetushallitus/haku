package fi.vm.sade.oppija.haku.dao;


import java.util.Map;

public interface ApplicationPeriodDAO {

    void insert(Map<String, Object> map);

    Map<String, Object> find(String applicationPeriodId);

}
