package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao;

import fi.vm.sade.haku.oppija.common.dao.BaseDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;

public interface FormConfigurationDAO extends BaseDAO<FormConfiguration> {

    FormConfiguration findByApplicationSystem(String asId);

    void update(FormConfiguration formConfiguration);
}
