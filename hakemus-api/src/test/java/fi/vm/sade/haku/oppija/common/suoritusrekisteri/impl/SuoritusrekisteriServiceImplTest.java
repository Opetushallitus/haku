package fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SuoritusrekisteriServiceImplTest {

    SuoritusrekisteriServiceImpl suoritusrekisteriService;
    CachingRestClient cachingRestClient;

    Date yesterday;
    long ONE_DAY = 1000 * 60 * 60 * 24;

    @Before
    public void setUp() {
        UrlConfiguration urlConfiguration = new UrlConfiguration();
        suoritusrekisteriService =  new SuoritusrekisteriServiceImpl(urlConfiguration);
        cachingRestClient = mock(CachingRestClient.class);

        yesterday = new Date(System.currentTimeMillis() - ONE_DAY);
    }

    @Test
    public void testSingleSuoritus() throws IOException {
        String suoritukset = "["+getSuoritus("1.2.246.562.13.62959769647")+"]";
        InputStream is = new ByteArrayInputStream(suoritukset.getBytes("UTF-8"));
        when(cachingRestClient.get(any(String.class))).thenReturn(is);
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        Map<String, List<SuoritusDTO>> suoritusDTOs = suoritusrekisteriService.getSuoritukset("1.2.246.562.24.50387424171");
        assertEquals(1, suoritusDTOs.size());
        SuoritusDTO suoritus = suoritusDTOs.get("1.2.246.562.13.62959769647").get(0);
        assertEquals(suoritus.getKomo(), "1.2.246.562.13.62959769647");

    }

    @Test
    public void testMultipleLegalSuoritus() throws IOException {
        String suoritukset = "["+getSuoritus("1.2.246.562.13.62959769647")+","
                +getSuoritus("1.2.246.562.5.2013112814572435044876")+"]";
        InputStream is = new ByteArrayInputStream(suoritukset.getBytes("UTF-8"));
        when(cachingRestClient.get(any(String.class))).thenReturn(is);
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        Map<String, List<SuoritusDTO>> suoritusDTOs = suoritusrekisteriService.getSuoritukset("1.2.246.562.24.50387424171");
        assertEquals(2, suoritusDTOs.size());
        SuoritusDTO suoritus = suoritusDTOs.get("1.2.246.562.13.62959769647").get(0);
        assertEquals(suoritus.getKomo(), "1.2.246.562.13.62959769647");
        suoritus = suoritusDTOs.get("1.2.246.562.5.2013112814572435044876").get(0);
        assertEquals(suoritus.getKomo(), "1.2.246.562.5.2013112814572435044876");

    }

    @Rule public ExpectedException thrown= ExpectedException.none();
    @Ignore
    @Test
    public void testMultipleFailingSuoritus() throws IOException {
        thrown.expect(ResourceNotFoundException.class);
        thrown.expectMessage("Found multiple instances of komo 1.2.246.562.13.62959769647 for personOid 1.2.246.562.24.15469000319");
        String suoritukset = "["+getSuoritus("1.2.246.562.13.62959769647")+","+getSuoritus("1.2.246.562.13.62959769647")+"]";
        InputStream is = new ByteArrayInputStream(suoritukset.getBytes("UTF-8"));
        when(cachingRestClient.get(any(String.class))).thenReturn(is);
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        Map<String, List<SuoritusDTO>> suoritusDTOs = suoritusrekisteriService.getSuoritukset("1.2.246.562.24.50387424171");

    }

    @Test
    public void testOpiskelijat() throws IOException {
        when(cachingRestClient.get(any(String.class))).thenReturn(getOpiskelija());
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        List<OpiskelijaDTO> opiskelijat = suoritusrekisteriService.getOpiskelijatiedot("1.2.246.562.24.50387424171");

        assertEquals(1, opiskelijat.size());
        OpiskelijaDTO opiskelija = opiskelijat.get(0);
        assertEquals("1.2.246.562.10.27450788669", opiskelija.getOppilaitosOid());
        assertEquals("10", opiskelija.getLuokkataso());
        assertEquals("10Y", opiskelija.getLuokka());

    }

    private String getSuoritus(String komo) {
        return "{\"id\":\"b32ba8e6-63d7-4946-8717-61fdb85f2860\"," +
                "\"komo\":\"" + komo + "\"," +
                "\"myontaja\":\"1.2.246.562.10.74977233621\"," +
                "\"tila\":\"KESKEN\"," +
                "\"valmistuminen\":\"30.05.2014\"," +
                "\"henkiloOid\":\"1.2.246.562.24.15469000319\"," +
                "\"yksilollistaminen\":\"Alueittain\"," +
                "\"suoritusKieli\":\"GL\"}";
    }

    private InputStream getOpiskelija() throws UnsupportedEncodingException {
        JsonObject opiskelija = new JsonObject();
        opiskelija.add("oppilaitosOid", new JsonPrimitive("1.2.246.562.10.27450788669"));
        opiskelija.add("luokkataso", new JsonPrimitive("10"));
        opiskelija.add("luokka", new JsonPrimitive("10Y"));
        opiskelija.add("henkiloOid", new JsonPrimitive("1.2.246.562.24.59031586696"));
        opiskelija.add("alkuPaiva", new JsonPrimitive(zulu().format(yesterday)));

        JsonArray opiskelijat = new JsonArray();
        opiskelijat.add(opiskelija);
        return new ByteArrayInputStream(opiskelijat.toString().getBytes("UTF-8"));
    }

    private DateFormat zulu() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }
}
