package fi.vm.sade.oppija.common.authentication.impl;

import fi.vm.sade.authentication.service.UserManagementService;
import fi.vm.sade.authentication.service.types.AddHenkiloDataType;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.authentication.service.types.dto.HenkiloTyyppiType;
import fi.vm.sade.authentication.service.types.dto.KielisyysType;
import fi.vm.sade.authentication.service.types.dto.SukupuoliType;
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

    @Autowired
    private UserManagementService userManagementService;

    public String addPerson(Person person) {
        AddHenkiloDataType addHenkiloDataType = new AddHenkiloDataType();
        KielisyysType contactLanguageType = new KielisyysType();
        contactLanguageType.setKieliKoodi(person.getContactLanguage());
        addHenkiloDataType.setAsiointiKieli(contactLanguageType);
        addHenkiloDataType.setEiSuomalaistaHetua(Boolean.FALSE);
        addHenkiloDataType.setEtunimet(person.getFirstNames());
        addHenkiloDataType.setHenkiloTyyppi(HenkiloTyyppiType.OPPIJA);
        addHenkiloDataType.setHetu(person.getSocialSecurityNumber());
        addHenkiloDataType.setKayttajatunnus(person.getEmail()); //?
        addHenkiloDataType.setKotikunta(person.getHomeCity());
        addHenkiloDataType.setKutsumanimi(person.getNickName());
        addHenkiloDataType.setSukunimi(person.getLastName());
        addHenkiloDataType.setSukupuoli(resolveSexType(person.getSex()));
        addHenkiloDataType.setTurvakielto(person.isSecurityOrder());

        HenkiloType henkiloType = userManagementService.addHenkilo(addHenkiloDataType);

        return henkiloType.getOidHenkilo();
    }

    private SukupuoliType resolveSexType(String sex) {
        // do stuff

        return SukupuoliType.MIES;
    }
}
