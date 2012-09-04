package fi.vm.sade.oppija.haku.dao.impl;

import fi.vm.sade.oppija.haku.dao.FormDAO;
import org.springframework.stereotype.Service;

@Service
public class FormDAOMongoImpl extends AbstractDAOMongoImpl implements FormDAO {

    @Override
    public String getCollectionName() {
        return "haku";
    }
}
