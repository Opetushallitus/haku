package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.service.AdminService;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author jukka
 * @version 9/12/123:44 PM}
 * @since 1.1
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired
    @Qualifier("formModelDAOMongoImpl")
    private FormModelDAO formModelDAO;

    @Override
    public void replaceModel(String file) {
        formModelDAO.insertModelAsJsonString(new StringBuilder(file));
    }

    @Override
    public void replaceModel(InputStream inputStream) {
        final StringBuilder stringBuilder = new FileHandling().readFile(inputStream);
        formModelDAO.insertModelAsJsonString(stringBuilder);
    }


    @Override
    public void replaceModel(FormModel model) {
        formModelDAO.insert(model);
    }
}
