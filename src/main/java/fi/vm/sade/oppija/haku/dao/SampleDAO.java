package fi.vm.sade.oppija.haku.dao;

import java.util.Map;

/**
 * @author jukka
 * @version 8/31/123:07 PM}
 * @since 1.1
 */
public interface SampleDAO {
    void insert(Map<String, String> map);

    Map listAll();
}
