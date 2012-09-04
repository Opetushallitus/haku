package fi.vm.sade.oppija.haku.dao;


import java.util.Map;

public interface FormDAO {
    void insert(Map<Object, Object> map);

    Map listAll();
}
