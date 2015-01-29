package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.GroupConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class GroupRestrictionConfigurator {

    public static final String CONFIG_maximumNumberOf = "maximumNumberOf";

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupRestrictionConfigurator.class);
    private final FormParameters formParameters;
    private final HakuService hakuService;
    private final HakukohdeService hakukohdeService;
    private final OrganizationService organizationService;
    
    public GroupRestrictionConfigurator(final FormParameters formParameters,
                                        final HakukohdeService hakukohdeService,
                                        final OrganizationService organizationService) {
        this.formParameters = formParameters;
        //TODO: =RS= Tämä tarvitaan vielä jostain
        this.hakuService = null;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
    }

    public List<Validator> findAndConfigureGroupRestrictions(){
        FormConfiguration formConfiguration = formParameters.getFormConfiguration();
        // FormParameters got asID
        for (GroupConfiguration groupConfiguration : formConfiguration.getGroupConfigurations()){
            if (groupConfiguration.getType().equals(GroupConfiguration.GroupType.hakukohde_rajaava)){
                //TODO: =RS= generate HH-175 säännöt. Huom tarkastellaan vain listoja.
            }
            if (groupConfiguration.getType().equals(GroupConfiguration.GroupType.CONSTRAINT_GROUP)){
                //TODO: =RS= generate HH-20 säännöt. Huom tarkastellaan vain listoja.
            }
        }
        
        /*
        TODO Erikoistapaus HH-19
        - Hakuservice.getHakukohderyhmäs
          https://itest-virkailija.oph.ware.fi/tarjonta-service/rest/v1/haku/1.2.246.562.29.173465377510
        - every group test type == GroupConfiguration.GroupType.hakukohde_priorisoiva
        - generate HH-19 säännöt
        - validaattoriin ei tietoa prioriteettitasoista. Katsotaan vain että ovat oikeassa järjestyksessä.
        - lisää tieto prioriteeteista koulutusinformaatioon
        - varmista että toimii
        */
        return new ArrayList<Validator>(0);
    }
}
