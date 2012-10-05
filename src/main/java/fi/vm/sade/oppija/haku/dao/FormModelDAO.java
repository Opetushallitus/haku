package fi.vm.sade.oppija.haku.dao;

import fi.vm.sade.oppija.haku.domain.FormModel;

/**
 * @author hannu
 */
public interface FormModelDAO {

    FormModel find();

    void insert(FormModel formModel);

    void insertModelAsJsonString(String jsonString);

    void delete(FormModel formModel);


}
