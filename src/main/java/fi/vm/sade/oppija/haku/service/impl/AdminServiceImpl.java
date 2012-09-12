package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.service.AdminService;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author jukka
 * @version 9/12/123:44 PM}
 * @since 1.1
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    @Qualifier("formModelDAOMongoImpl")
    private FormModelDAO formModelDAO;

    @Override
    public void replaceModel(MultipartFile file) {
        try {
            final StringBuilder stringBuilder = new FileHandling().readFile(file.getInputStream());
            formModelDAO.insertModelAsJsonString(stringBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
