package fi.vm.sade.oppija.haku.dao.impl;

import fi.vm.sade.oppija.haku.domain.FormModel;

/**
 *
 * @author hannu
 */
public interface FormModelDAO {

    FormModel find();

    void insert(FormModel formModel);
}
