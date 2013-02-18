package fi.vm.sade.oppija.common.authentication.impl;

import fi.vm.sade.authentication.service.UserManagementService;
import fi.vm.sade.authentication.service.types.AddHenkiloDataType;
import fi.vm.sade.authentication.service.types.dto.*;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
public class AuthenticationServiceImpl implements AuthenticationService {

    private UserManagementService userManagementService;

    @Autowired
    public AuthenticationServiceImpl(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    public String addPerson(Person person) {
        AddHenkiloDataType addHenkiloDataType = new AddHenkiloDataType();
        addHenkiloDataType.setEiSuomalaistaHetua(Boolean.FALSE);
        addHenkiloDataType.setEtunimet(person.getFirstNames());
        addHenkiloDataType.setHenkiloTyyppi(HenkiloTyyppiType.OPPIJA);
        addHenkiloDataType.setHetu(person.getSocialSecurityNumber());
        addHenkiloDataType.setKayttajatunnus(person.getEmail());
        addHenkiloDataType.setKotikunta(person.getHomeCity());
        addHenkiloDataType.setKutsumanimi(person.getNickName());
        addHenkiloDataType.setSukunimi(person.getLastName());
        addHenkiloDataType.setSukupuoli(resolveSexType(person.getSex()));
        addHenkiloDataType.setTurvakielto(person.isSecurityOrder());

        // TODO: resolve proper language when user management service
        // allows adding people with koodisto languahe codes
        KielisyysType contactLanguageType = new KielisyysType();
        contactLanguageType.setKieliKoodi("fi");
        contactLanguageType.setId(117l);
        contactLanguageType.setKieliTyyppi("suomi");
        addHenkiloDataType.setAsiointiKieli(contactLanguageType);
        KielisyysType lang = new KielisyysType();
        lang.setId(117l);
        lang.setKieliKoodi("fi");
        lang.setKieliTyyppi("suomi");
        KansalaisuusType nat = new KansalaisuusType();
        nat.setId(120L);
        nat.setKansalaisuusKoodi("fi");
        addHenkiloDataType.getKielisyys().add(lang);
        addHenkiloDataType.getKansalaisuus().add(nat);

        HenkiloType henkiloType = userManagementService.addHenkilo(addHenkiloDataType);

        return henkiloType.getOidHenkilo();
    }

    private SukupuoliType resolveSexType(String sex) {
        //TODO: is there a koodisto for sex?

        return SukupuoliType.MIES;
    }
}
