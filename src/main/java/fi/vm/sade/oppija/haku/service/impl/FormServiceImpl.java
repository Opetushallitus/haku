package fi.vm.sade.oppija.haku.service.impl;


import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.Category;
import fi.vm.sade.oppija.haku.domain.Form;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
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
        if (model == null) throw new ResourceNotFoundException("Model not found");
        ApplicationPeriod applicationPeriod = model.getApplicationPeriodById(applicationPeriodId);
        if (applicationPeriod == null) throw new ResourceNotFoundException("not found");
        if (!applicationPeriod.isActive()) throw new ResourceNotFoundException("Not active");
        return applicationPeriod.getFormById(formId);
    }

    @Override
    public Category getFirstCategory(String applicationPeriodId, String formId) {
        Category firstCategory = getActiveForm(applicationPeriodId, formId).getFirstCategory();
        if (firstCategory == null) {
            throw new ResourceNotFoundException("First category not found");
        }
        return firstCategory;
    }


}
