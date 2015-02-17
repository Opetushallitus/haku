package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.GroupRestrictionValidator;
import fi.vm.sade.haku.oppija.lomake.validation.groupvalidators.GroupRestrictionMaxNumberValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.GroupConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GroupRestrictionConfigurator {

    public static final String CONFIG_maximumNumberOf = "maximumNumberOf";

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupRestrictionConfigurator.class);
    private final FormParameters formParameters;
    private final HakukohdeService hakukohdeService;
    private final OrganizationService organizationService;
    
    public GroupRestrictionConfigurator(final FormParameters formParameters,
                                        final HakukohdeService hakukohdeService,
                                        final OrganizationService organizationService) {
        this.formParameters = formParameters;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
    }

    public List<GroupRestrictionValidator> findAndConfigureGroupRestrictions(){
        FormConfiguration formConfiguration = formParameters.getFormConfiguration();
        List<GroupRestrictionValidator> validators = new ArrayList<GroupRestrictionValidator>();

        for (GroupConfiguration groupConfiguration : formConfiguration.getGroupConfigurations()){
            switch (groupConfiguration.getType()) {
                case hakukohde_rajaava:
                    createRajaavaValidator(validators, groupConfiguration);
                    break;
                case hakukohde_priorisoiva:
                    createPriorisoivaValidator(validators, groupConfiguration);
                    break;
                case CONSTRAINT_GROUP:
                    //TODO: =RS= generate HH-20 säännöt. Huom tarkastellaan vain listoja.
                    break;
            }
        }
        

        return validators;
    }

    private void createPriorisoivaValidator(List<GroupRestrictionValidator> validators, GroupConfiguration groupConfiguration) {
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
    }

    private void createRajaavaValidator(List<GroupRestrictionValidator> validators, GroupConfiguration groupConfiguration) {
        if(groupConfiguration.getConfigurations().containsKey(CONFIG_maximumNumberOf)) {
            I18nText errorMessage = formParameters.getI18nText("rajaava.ryhma.max.virhe");
            validators.add(new GroupRestrictionMaxNumberValidator(
                    groupConfiguration.getGroupId(),
                    Integer.valueOf(groupConfiguration.getConfigurations().get(CONFIG_maximumNumberOf)),
                    errorMessage
            ));
        }
    }
}
