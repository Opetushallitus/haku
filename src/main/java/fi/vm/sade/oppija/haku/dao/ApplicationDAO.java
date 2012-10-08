package fi.vm.sade.oppija.haku.dao;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;

/**
 * DAO interface for saving, updating and finding applications made by users.
 *
 * @author Hannu Lyytikainen
 */
public interface ApplicationDAO {


    /**
     * Update single application. If the application can not be found
     * in the db, a new one is inserted.
     *
     * @param hakemus application to be updated
     */
    public void update(Hakemus hakemus);

    /**
     * Find Application by userId and applicationId.
     *
     * @param hakemusId application identifier
     * @return hakemus
     */
    Hakemus find(HakemusId hakemusId);
}
