package fi.vm.sade.haku.virkailija.koulutusinformaatio.impl;

import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.koulutusinformaatio.domain.dto.AddressDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.LearningOpportunityProviderDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile(value = {"dev", "it"})
public class KoulutusinformaatioServiceMockImpl implements
		KoulutusinformaatioService {

	@Override
	public ApplicationOptionDTO getApplicationOption(String oid) {

        if ("1.2.246.562.5.20176855623".equals(oid)) {
            return getAo1();
        } else if ("1.2.246.562.14.79893512065".equals(oid)) {
            return getAo2();
        } else if ("1.2.246.562.20.30500448839".equals(oid)) {
            return getAo3();
        } else if ("1.2.246.562.14.673437691210".equals(oid)) {
            return getAo4();
        }

		ApplicationOptionDTO applicationOption = new ApplicationOptionDTO();
		applicationOption.setName("MockKoulutus");
        LearningOpportunityProviderDTO provider = new LearningOpportunityProviderDTO();
        provider.setName("MockKoulu");
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setPostOffice("HELSINKI");
        addressDTO.setPostalCode("00100");
        addressDTO.setStreetAddress("Katukuja 1");
        provider.setPostalAddress(addressDTO);
        applicationOption.setProvider(provider);
        applicationOption.setAttachments(new ArrayList());
		return applicationOption;
	}

	@Override
	public List<ApplicationOptionDTO> getApplicationOptions(List<String> oids) {
		List<ApplicationOptionDTO> applicationOptions = new ArrayList<ApplicationOptionDTO>();
		applicationOptions.add(new ApplicationOptionDTO());
		return applicationOptions;
	}

    @Override
    public ApplicationOptionDTO getApplicationOption(String aoOid, String lang) {
        return getApplicationOption(aoOid);
    }

    private ApplicationOptionDTO getAo1() {
        return new ApplicationOptionDTO() {{
            setId("1.2.246.562.5.20176855623");
            setName("Tieto- ja tietoliikennetekniikan perustutkinto, yo");
            setAoIdentifier("143");
            setEducationDegree("32");
            setSora(false);
            setTeachingLanguages(new ArrayList<String>(1) {{
                add("FI");
            }});
            setAthleteEducation(true);
            setProvider(new LearningOpportunityProviderDTO() {{
                setId("1.2.246.562.10.51872958189");
                setName("Stadin ammattiopisto, Sturenkadun toimipaikka");
            }});
        }};
    }

    private ApplicationOptionDTO getAo2() {
        return new ApplicationOptionDTO() {{
            setId("1.2.246.562.14.79893512065");
            setName("Kaivosalan perustutkinto, pk");
            setAoIdentifier("333");
            setEducationDegree("32");
            setEducationCodeUri("koulutus_381203");
            setTeachingLanguages(new ArrayList<String>(1) {{
                add("FI");
            }});
            setAthleteEducation(true);
            setKaksoistutkinto(false);
            setVocational(true);
            setProvider(new LearningOpportunityProviderDTO() {{
                setId("1.2.246.562.10.89537774706");
                setName("FAKTIA, Espoo op");
            }});
            setSora(true);
        }};
    }

    private ApplicationOptionDTO getAo3() {
        return new ApplicationOptionDTO() {{
            setId("1.2.246.562.20.30500448839");
            setName("Kymppiluokka");
            setAoIdentifier("019");
            setEducationDegree("22");
            setEducationCodeUri("koulutus_222222");
            setTeachingLanguages(new ArrayList<String>(1) {{
                add("FI");
            }});
            setAthleteEducation(false);
            setKaksoistutkinto(false);
            setVocational(true);
            setProvider(new LearningOpportunityProviderDTO() {{
                setId("1.2.246.562.10.35241670047");
                setName("Anna Tapion koulu");
            }});
            setSora(false);
        }};
    }


    private ApplicationOptionDTO getAo4() {
        return new ApplicationOptionDTO() {{
            setId("1.2.246.562.14.673437691210");
            setName("Talonrakennus ja ymäristösuunnittelu, yo");
            setEducationDegree("32");
            setTeachingLanguages(new ArrayList<String>(1) {{
                add("FI");
            }});
            setSora(true);
            setVocational(true);
            setProvider(new LearningOpportunityProviderDTO() {{
                setId("1.2.246.562.10.89537774706");
                setName("FAKTIA, Espoo op");
            }});
            setSora(false);
        }};
    }

}
