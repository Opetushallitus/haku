package fi.vm.sade.oppija.haku.service.impl;


import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.Form;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FormServiceImpl implements FormService {

    @Autowired()
    @Qualifier("FormModelDummyMemoryDao")
    FormModelDAO formModelDAO;

    @Override
    public FormModel getModel() {
        return formModelDAO.find();
    }

    @Override
    public Form getActiveForm(String applicationPeriodId, String formId) {
        FormModel model = getModel();
        ApplicationPeriod activePeriodById = model.getActivePeriodById(applicationPeriodId);
        return activePeriodById.getFormById(formId);
    }


}
