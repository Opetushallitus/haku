package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.FormModel;

import java.io.InputStream;

/**
 * @author jukka
 * @version 9/12/123:44 PM}
 * @since 1.1
 */
public interface AdminService {

    void replaceModel(String file);

    void replaceModel(InputStream inputStream);

    void replaceModel(FormModel model);
}
