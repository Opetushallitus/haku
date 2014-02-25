package fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SuoritusrekisteriServiceImplTest {

    SuoritusrekisteriServiceImpl suoritusrekisteriService;
    CachingRestClient cachingRestClient;

    Date tomorrow;
    Date yesterday;
    long ONE_DAY = 1000 * 60 * 60 * 24;

    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    DateFormat zulu = new SimpleDateFormat("dd.MM.yyyy");

    @Before
    public void setUp() {
        suoritusrekisteriService =  new SuoritusrekisteriServiceImpl();
        cachingRestClient = mock(CachingRestClient.class);

        tomorrow = new Date(System.currentTimeMillis() + ONE_DAY);
        yesterday = new Date(System.currentTimeMillis() - ONE_DAY);
    }

    @Test
    public void testSuorituksetPK() throws IOException {
        when(cachingRestClient.get(any(String.class))).thenReturn(getSuoritus("peruskoulu", "Ei"));
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        List<SuoritusDTO> suoritukset = suoritusrekisteriService.getSuoritukset("1.2.246.562.24.50387424171");
        assertEquals(1, suoritukset.size());
        SuoritusDTO suoritus = suoritukset.get(0);
        assertEquals(Integer.valueOf(OppijaConstants.PERUSKOULU), suoritus.getPohjakoulutus());

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(tomorrow);
        Calendar calSuoritus = GregorianCalendar.getInstance();
        calSuoritus.setTime(suoritus.getValmistuminen());
        assertEquals(cal.get(Calendar.YEAR), calSuoritus.get(Calendar.YEAR));

        assertEquals("AR", suoritus.getSuorituskieli());
    }

    @Test
    public void testSuorituksetLukio() throws IOException {
        when(cachingRestClient.get(any(String.class))).thenReturn(getSuoritus("lukio", "Ei"));
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        List<SuoritusDTO> suoritukset = suoritusrekisteriService.getSuoritukset("1.2.246.562.24.50387424171");
        assertEquals(1, suoritukset.size());
        SuoritusDTO suoritus = suoritukset.get(0);
        assertEquals(Integer.valueOf(OppijaConstants.YLIOPPILAS), suoritus.getPohjakoulutus());

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(tomorrow);
        Calendar calSuoritus = GregorianCalendar.getInstance();
        calSuoritus.setTime(suoritus.getValmistuminen());
        assertEquals(cal.get(Calendar.YEAR), calSuoritus.get(Calendar.YEAR));
    }

    @Test
    public void testSuorituksetYksilollistetty() throws IOException {
        when(cachingRestClient.get(any(String.class))).thenReturn(getSuoritus("peruskoulu", "Alueittain"));
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        List<SuoritusDTO> suoritukset = suoritusrekisteriService.getSuoritukset("1.2.246.562.24.50387424171");
        assertEquals(1, suoritukset.size());
        SuoritusDTO suoritus = suoritukset.get(0);
        assertEquals(Integer.valueOf(OppijaConstants.ALUEITTAIN_YKSILOLLISTETTY), suoritus.getPohjakoulutus());

        when(cachingRestClient.get(any(String.class))).thenReturn(getSuoritus("peruskoulu", "Osittain"));
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        suoritukset = suoritusrekisteriService.getSuoritukset("1.2.246.562.24.50387424171");
        assertEquals(1, suoritukset.size());
        suoritus = suoritukset.get(0);
        assertEquals(Integer.valueOf(OppijaConstants.OSITTAIN_YKSILOLLISTETTY), suoritus.getPohjakoulutus());

        when(cachingRestClient.get(any(String.class))).thenReturn(getSuoritus("peruskoulu", "Kokonaan"));
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        suoritukset = suoritusrekisteriService.getSuoritukset("1.2.246.562.24.50387424171");
        assertEquals(1, suoritukset.size());
        suoritus = suoritukset.get(0);
        assertEquals(Integer.valueOf(OppijaConstants.YKSILOLLISTETTY), suoritus.getPohjakoulutus());

    }

    @Test
    public void testOpiskelijat() throws IOException {
        when(cachingRestClient.get(any(String.class))).thenReturn(getOpiskelija());
        suoritusrekisteriService.setCachingRestClient(cachingRestClient);

        List<OpiskelijaDTO> opiskelijat = suoritusrekisteriService.getOpiskelijat("1.2.246.562.24.50387424171");

        assertEquals(1, opiskelijat.size());
        OpiskelijaDTO opiskelija = opiskelijat.get(0);
        assertEquals("1.2.246.562.10.27450788669", opiskelija.getOppilaitosOid());
        assertEquals("10", opiskelija.getLuokkataso());
        assertEquals("10Y", opiskelija.getLuokka());

    }

    private InputStream getSuoritus(String pohjakoulutus, String yksilollistaminen) throws UnsupportedEncodingException {
        JsonObject suoritus = new JsonObject();
        suoritus.add("id", new JsonPrimitive("7514285c-921b-44a9-b9d7-511d0eb39160"));
        suoritus.add("tila", new JsonPrimitive("KESKEN"));
        suoritus.add("valmistuminen", new JsonPrimitive(df.format(tomorrow)));
        suoritus.add("henkiloOid", new JsonPrimitive("1.2.246.562.24.50387424171"));
        suoritus.add("yksilollistaminen", new JsonPrimitive(yksilollistaminen));
        suoritus.add("suoritusKieli", new JsonPrimitive("AR"));

        JsonObject komoto = new JsonObject();
        komoto.add("oid", new JsonPrimitive("FIXME"));
        komoto.add("komo", new JsonPrimitive(pohjakoulutus));
        komoto.add("tarjoaja", new JsonPrimitive("1.2.246.562.10.27450788669"));

        suoritus.add("komoto", komoto);

        JsonArray suoritukset = new JsonArray();
        suoritukset.add(suoritus);

        return new ByteArrayInputStream(suoritukset.toString().getBytes("UTF-8"));
    }

    private InputStream getOpiskelija() throws UnsupportedEncodingException {
        JsonObject opiskelija = new JsonObject();
        opiskelija.add("oppilaitosOid", new JsonPrimitive("1.2.246.562.10.27450788669"));
        opiskelija.add("luokkataso", new JsonPrimitive("10"));
        opiskelija.add("luokka", new JsonPrimitive("10Y"));
        opiskelija.add("henkiloOid", new JsonPrimitive("1.2.246.562.24.59031586696"));
        opiskelija.add("alkuPaiva", new JsonPrimitive(zulu.format(yesterday)));

        JsonArray opiskelijat = new JsonArray();
        opiskelijat.add(opiskelija);
        return new ByteArrayInputStream(opiskelijat.toString().getBytes("UTF-8"));

    }
}
