package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.FormModel;

import java.io.InputStream;

/**
 * @author jukka
 * @version 9/12/123:44 PM}
 * @since 1.1
 */
public interface AdminService {

    public void replaceModel(String file);

    public void replaceModel(InputStream inputStream);

    public void replaceModel(FormModel model);
}
