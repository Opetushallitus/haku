package fi.vm.sade.haku.oppija.ui.service.impl;

import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import fi.vm.sade.haku.virkailija.valinta.dto.HakijaDTO;
import fi.vm.sade.haku.virkailija.valinta.impl.ValintaServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Note: This test calls valinta-tulos-service on luokka to get a certain test application, so it may fail.
 * If it becomes a nuisance, @Ignore or delete it.
 */
public class ValintaServiceIntegrationTest {
    private final String luokkaValintaTulosServiceUrl = "https://itest-virkailija.oph.ware.fi/valinta-tulos-service";
    private final String localValintaTulosServiceUrl = "http://localhost:8097/valinta-tulos-service";
    private final String valintaTulosServiceUrl = luokkaValintaTulosServiceUrl;
    private final ValintaServiceImpl valintaService;

    public ValintaServiceIntegrationTest() {
        UrlConfiguration urlConfiguration = new UrlConfiguration(UrlConfiguration.SPRING_IT_PROFILE);
        urlConfiguration.addDefault("host.virkailija", "itest-virkailija.oph.ware.fi");
        urlConfiguration.addDefault("host.cas", "localhost");
        valintaService = new ValintaServiceImpl(urlConfiguration);
    }

    @Before
    public void initClient() {
        CachingRestClient realRestClient = new CachingRestClient(4000);
        realRestClient.setCasService(valintaTulosServiceUrl);
        valintaService.setCachingRestClientValintaTulosService(realRestClient);
    }

    @Test
    public void retrievesHakijaDtoFromValintaTulosService() {
        HakijaDTO inexistentHakijaDto = valintaService.getHakija("1", "2");
        Assert.assertNotNull(inexistentHakijaDto);
        Assert.assertEquals(null, inexistentHakijaDto.getHakemusOid());

        Types.ApplicationSystemOid kkKevat2015 = Types.ApplicationSystemOid.of("1.2.246.562.29.95390561488");
        Types.ApplicationOid tiinaMakinenApplication = Types.ApplicationOid.of("1.2.246.562.11.00000000288");
        HakijaDTO existingHakijaDto = valintaService.getHakija(kkKevat2015.getValue(), tiinaMakinenApplication.getValue());
        Assert.assertNotNull(existingHakijaDto);
        Assert.assertEquals(tiinaMakinenApplication.getValue(), existingHakijaDto.getHakemusOid());
    }
}
