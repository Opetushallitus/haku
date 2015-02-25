package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PERUSKOULU;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseEducationServiceImplTest {

    private static final String pkKomo = "1.2.246.562.13.62959769647";
    private static final String kymppiKomo = "1.2.246.562.5.2013112814572435044876";

    private Date applicationPeriodStartDate;
    private Date applicationPeriodEndDate;

    private ApplicationSystem as;

    private SuoritusDTO pkKesken;
    private SuoritusDTO kymppiKesken;
    private SuoritusDTO pkValmis;

    @Before
    public void setUp() {
        final Calendar cal = GregorianCalendar.getInstance();
        cal.set(2015, Calendar.JANUARY, 1);
        applicationPeriodStartDate = new Date(cal.getTimeInMillis());
        cal.set(2015, Calendar.APRIL, 1);
        applicationPeriodEndDate = new Date(cal.getTimeInMillis());

        cal.set(2015, Calendar.JUNE, 1);
        as = new ApplicationSystemBuilder()
                .setId("asId")
                .setName(ElementUtil.createI18NAsIs("asName"))
                .setApplicationPeriods(new ArrayList<ApplicationPeriod>() {{
                    add(new ApplicationPeriod(applicationPeriodStartDate, applicationPeriodEndDate));
                }})
                .get();
        pkKesken = new SuoritusDTO("pkKesken", pkKomo, "myontaja",
                "KESKEN", new Date(cal.getTimeInMillis()), "personOid", "Ei", "fi");
        kymppiKesken = new SuoritusDTO("kymppiKesken", kymppiKomo, "myontaja",
                "KESKEN", new Date(cal.getTimeInMillis()), "personOid", "Ei", "fi");
        cal.set(2014, Calendar.JUNE, 1);
        pkValmis = new SuoritusDTO("pkValmis", pkKomo, "myontaja",
                "VALMIS", new Date(cal.getTimeInMillis()), "personOid", "Ei", "fi");

    }

    @Test
    public void testGetArvosanatPk() {
        SuoritusrekisteriService suoritusrekisteriService = mockSuoritusrekisteriService("personOid",
                new HashMap<String, SuoritusDTO>() {{ put(pkKomo, pkKesken); }});

        BaseEducationService baseEducationService = getBaseEducationService(suoritusrekisteriService);
        when(suoritusrekisteriService.getArvosanat(eq("pkKesken")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("1", "AI", "9", false, "fi"));
                    add(new ArvosanaDTO("2", "AI", "8", true, "fi"));
                }});

        Map<String, ArvosanaDTO> arvosanat = baseEducationService.getArvosanat("personOid", PERUSKOULU, as);

        assertEquals(2, arvosanat.size());
        assertTrue(arvosanat.containsKey("PK_AI"));
        assertTrue(arvosanat.containsKey("PK_AI_VAL1"));
        assertEquals("9", arvosanat.get("PK_AI").getArvosana());
        assertEquals("8", arvosanat.get("PK_AI_VAL1").getArvosana());
    }

    @Test
    public void testGetArvosanatPkJaKymppi() {
        SuoritusrekisteriService suoritusrekisteriService = mockSuoritusrekisteriService("personOid",
                new HashMap<String, SuoritusDTO>() {{ put(pkKomo, pkValmis); put(kymppiKomo, kymppiKesken);}});

        when(suoritusrekisteriService.getArvosanat(eq("pkValmis")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("1", "AI", "8", false, "fi"));

                    add(new ArvosanaDTO("2", "BI", "8", false, "fi"));
                    add(new ArvosanaDTO("3", "BI", "7", true, "fi"));

                    add(new ArvosanaDTO("4", "CI", "7", false, "fi"));
                }});
        when(suoritusrekisteriService.getArvosanat(eq("kymppiKesken")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("5", "AI", "9", false, "fi"));
                    add(new ArvosanaDTO("6", "BI", "8", false, "fi"));
                    add(new ArvosanaDTO("7", "BI", "8", true, "fi"));
                }});

        BaseEducationService baseEducationService = getBaseEducationService(suoritusrekisteriService);
        Map<String, ArvosanaDTO> arvosanat = baseEducationService.getArvosanat("personOid", PERUSKOULU, as);

        assertEquals(4, arvosanat.size());
        assertTrue(arvosanat.containsKey("PK_AI"));
        assertTrue(arvosanat.containsKey("PK_BI"));
        assertTrue(arvosanat.containsKey("PK_BI_VAL1"));
        assertTrue(arvosanat.containsKey("PK_CI"));

        assertEquals("9", arvosanat.get("PK_AI").getArvosana());
        assertEquals("8", arvosanat.get("PK_BI").getArvosana());
        assertEquals("8", arvosanat.get("PK_BI_VAL1").getArvosana());
        assertEquals("7", arvosanat.get("PK_CI").getArvosana());
    }

    private BaseEducationService getBaseEducationService(SuoritusrekisteriService suoritusrekisteriService) {
        BaseEducationServiceImpl baseEducationService = new BaseEducationServiceImpl(suoritusrekisteriService);
        baseEducationService.setPerusopetusKomoOid(pkKomo);
        baseEducationService.setLisaopetusKomoOid(kymppiKomo);
        return baseEducationService;
    }

    private SuoritusrekisteriService mockSuoritusrekisteriService(String personOid, Map<String, SuoritusDTO> suoritukset) {

        SuoritusrekisteriService suoritusrekisteriService = mock(SuoritusrekisteriService.class);
        when(suoritusrekisteriService.getSuoritukset(eq(personOid)))
                .thenReturn(suoritukset);

        return suoritusrekisteriService;
    }

}
