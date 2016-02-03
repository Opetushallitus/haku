package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl;


import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;

import java.util.List;

public class FormConfigurationDAOMockImpl implements FormConfigurationDAO{
    @Override
    public FormConfiguration findByApplicationSystem(String asId) {
        return null;
    }

    @Override
    public List<FormConfiguration> find(FormConfiguration formConfiguration) {
        return null;
    }

    @Override
    public int update(FormConfiguration o, FormConfiguration n) {
        return 0;
    }

    @Override
    public void update(FormConfiguration n) {

    }

    @Override
    public void save(FormConfiguration formConfiguration) {

    }
}
