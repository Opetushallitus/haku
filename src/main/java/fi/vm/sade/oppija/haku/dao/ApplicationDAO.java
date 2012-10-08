package fi.vm.sade.oppija.haku.dao;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;

/**
 * DAO interface for saving, updating and finding applications made by users.
 *
 * @author Hannu Lyytikainen
 */
public interface ApplicationDAO {

    void update(Hakemus hakemus);

    Hakemus find(HakemusId hakemusId);
}
