package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.service.SessionDataHolder;
import fi.vm.sade.oppija.haku.validation.FormValidator;
import fi.vm.sade.oppija.haku.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
@Service
public class HakemusServiceImpl implements HakemusService {
    private static final Logger LOG = LoggerFactory.getLogger(HakemusServiceImpl.class);

    private final ApplicationDAO sessionDataHolder;
    private final ApplicationDAO applicationDAO;
    private final FormValidator formValidator;

    @Autowired
    public HakemusServiceImpl(@Qualifier("sessionDataHolder") SessionDataHolder sessionDataHolder, @Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO, FormValidator formValidator) {
        this.sessionDataHolder = sessionDataHolder;
        this.applicationDAO = applicationDAO;
        this.formValidator = formValidator;
    }


    @Override
    public ValidationResult save(HakemusId hakemusId, Map<String, String> values) {
        final Hakemus hakemus = getHakemus(hakemusId);
        hakemus.getValues().putAll(values);

        ValidationResult validationResult = validate(hakemus);
        // Validation based on rule events, should be skipped?
        updateApplication(hakemus);
        return validationResult;
    }

    @Override
    public Hakemus getHakemus(HakemusId hakemusId) {
        return selectDao(hakemusId).find(hakemusId);
    }

    private ApplicationDAO selectDao(HakemusId hakemusId) {
        ApplicationDAO dao = sessionDataHolder;
        if (hakemusId.isUserKnown()) {
            dao = applicationDAO;
        }
        return dao;
    }


    private ValidationResult validate(Hakemus hakemus) {
        return formValidator.validate(hakemus);
    }

    private void updateApplication(Hakemus hakemus) {
        selectDao(hakemus.getHakemusId()).update(hakemus);
    }


}
