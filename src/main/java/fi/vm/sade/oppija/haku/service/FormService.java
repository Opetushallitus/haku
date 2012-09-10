package fi.vm.sade.oppija.haku.service;


import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormService {

    @Autowired
    FormModelDAO formModelDAO;

    public FormModel getModel() {
        return formModelDAO.find();
    }


}
