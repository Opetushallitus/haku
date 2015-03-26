package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvioDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.LISAOPETUS_KOMO;
import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.PERUSOPETUS_KOMO;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PERUSKOULU;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseEducationServiceImplTest {

    private Date applicationPeriodStartDate;
    private Date applicationPeriodEndDate;

    private ApplicationSystem as;

    private SuoritusDTO pkKesken;
    private SuoritusDTO kymppiKesken;
    private SuoritusDTO pkValmis;

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

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
        pkKesken = new SuoritusDTO("pkKesken", PERUSOPETUS_KOMO, "myontaja", "KESKEN", tomorrow(), "personOid",
                "Ei", "FI", "source", true);
        kymppiKesken = new SuoritusDTO("kymppiKesken", LISAOPETUS_KOMO, "myontaja", "KESKEN", tomorrow(), "personOid",
                "Ei", "FI", "source", true);
        pkValmis = new SuoritusDTO("pkValmis", PERUSOPETUS_KOMO, "myontaja", "KESKEN", yesterday(), "personOid",
                "Ei", "FI", "source", true);
    }

    @Test
    public void testGetArvosanatPk() {
        SuoritusrekisteriService suoritusrekisteriService = mockSuoritusrekisteriService("personOid",
                new HashMap<String, List<SuoritusDTO>>() {{
                    put(PERUSOPETUS_KOMO, Collections.singletonList(pkKesken));
                }});

        ApplicationSystemService applicationSystemService = mock(ApplicationSystemService.class);
        ApplicationSystem as = mock(ApplicationSystem.class);
        when(applicationSystemService.getApplicationSystem(any(String.class))).thenReturn(as);
        when(as.getApplicationPeriods()).thenReturn(
                new ArrayList<ApplicationPeriod>() {{ add(new ApplicationPeriod(yesterday(), tomorrow())); }} );

        BaseEducationService baseEducationService = new BaseEducationServiceImpl(suoritusrekisteriService, applicationSystemService);
        when(suoritusrekisteriService.getArvosanat(eq("pkKesken")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("1", "suoritusId", new ArvioDTO("9", "4-10", null), "AI", "source", false, "fi", yesterday(), null));
                    add(new ArvosanaDTO("2", "suoritusId", new ArvioDTO("8", "4-10", null), "AI", "source", true, "fi", yesterday(), 1));
                }});

        Map<String, String> arvosanat = baseEducationService.getArvosanat("personOid", PERUSKOULU, as);

        assertEquals(5, arvosanat.size());
        assertTrue(arvosanat.containsKey("PK_AI"));
        assertTrue(arvosanat.containsKey("PK_AI_OPPIAINE"));
        assertTrue(arvosanat.containsKey("PK_AI_VAL1"));
        assertTrue(arvosanat.containsKey("PK_AI_VAL2"));
        assertTrue(arvosanat.containsKey("PK_AI_VAL3"));
        assertEquals("9", arvosanat.get("PK_AI"));
        assertEquals("fi", arvosanat.get("PK_AI_OPPIAINE"));
        assertEquals("8", arvosanat.get("PK_AI_VAL1"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_AI_VAL2"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_AI_VAL3"));
    }

    @Test
    public void testGetArvosanatPkJaKymppi() {
        SuoritusrekisteriService suoritusrekisteriService = mockSuoritusrekisteriService("personOid",
                new HashMap<String, List<SuoritusDTO>>() {{
                    put(PERUSOPETUS_KOMO, Collections.singletonList(pkValmis));
                    put(LISAOPETUS_KOMO, Collections.singletonList(kymppiKesken));
                }});
        ApplicationSystemService applicationSystemService = mock(ApplicationSystemService.class);
        ApplicationSystem as = mock(ApplicationSystem.class);
        when(applicationSystemService.getApplicationSystem(any(String.class))).thenReturn(as);
        when(as.getApplicationPeriods()).thenReturn(
                new ArrayList<ApplicationPeriod>() {{ add(new ApplicationPeriod(yesterday(), tomorrow())); }} );

        BaseEducationService baseEducationService = new BaseEducationServiceImpl(suoritusrekisteriService, applicationSystemService);

        when(suoritusrekisteriService.getArvosanat(eq("pkValmis")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("1", "suoritusId", new ArvioDTO("8", "4-10", null), "AI", "source", false, "fi", yesterday(), null));

                    add(new ArvosanaDTO("2", "suoritusId", new ArvioDTO("8", "4-10", null), "BI", "source", false, null, yesterday(), null));
                    add(new ArvosanaDTO("3", "suoritusId", new ArvioDTO("7", "4-10", null), "BI", "source", true, null, yesterday(), 1));
                    add(new ArvosanaDTO("4", "suoritusId", new ArvioDTO("7", "4-10", null), "CI", "source", false, null, yesterday(), null));
                }});
        when(suoritusrekisteriService.getArvosanat(eq("kymppiKesken")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("5", "suoritusId", new ArvioDTO("9", "4-10", null), "AI", "source", false, null, yesterday(), null));
                    add(new ArvosanaDTO("6", "suoritusId", new ArvioDTO("8", "4-10", null), "BI", "source", false, null, yesterday(), null));
                    add(new ArvosanaDTO("7", "suoritusId", new ArvioDTO("8", "4-10", null), "BI", "source", true, null, yesterday(), 1));
                }});

        Map<String, String> arvosanat = baseEducationService.getArvosanat("personOid", PERUSKOULU, as);

        assertEquals(13, arvosanat.size());
        assertTrue(arvosanat.containsKey("PK_AI"));
        assertTrue(arvosanat.containsKey("PK_AI_OPPIAINE"));
        assertTrue(arvosanat.containsKey("PK_AI_VAL1"));
        assertTrue(arvosanat.containsKey("PK_AI_VAL2"));
        assertTrue(arvosanat.containsKey("PK_AI_VAL3"));
        assertTrue(arvosanat.containsKey("PK_BI"));
        assertTrue(arvosanat.containsKey("PK_BI_VAL1"));
        assertTrue(arvosanat.containsKey("PK_BI_VAL2"));
        assertTrue(arvosanat.containsKey("PK_BI_VAL3"));
        assertTrue(arvosanat.containsKey("PK_CI"));
        assertTrue(arvosanat.containsKey("PK_CI_VAL1"));
        assertTrue(arvosanat.containsKey("PK_CI_VAL2"));
        assertTrue(arvosanat.containsKey("PK_CI_VAL3"));

        assertEquals("9", arvosanat.get("PK_AI"));
        assertEquals("fi", arvosanat.get("PK_AI_OPPIAINE"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_AI_VAL1"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_AI_VAL2"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_AI_VAL3"));
        assertEquals("8", arvosanat.get("PK_BI"));
        assertEquals("8", arvosanat.get("PK_BI_VAL1"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_BI_VAL2"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_BI_VAL3"));
        assertEquals("7", arvosanat.get("PK_CI"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_CI_VAL1"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_CI_VAL2"));
        assertEquals("Ei arvosanaa", arvosanat.get("PK_CI_VAL3"));
    }

    private SuoritusrekisteriService mockSuoritusrekisteriService(String personOid, Map<String, List<SuoritusDTO>> suoritukset) {

        SuoritusrekisteriService suoritusrekisteriService = mock(SuoritusrekisteriService.class);
        when(suoritusrekisteriService.getSuoritukset(eq(personOid)))
                .thenReturn(suoritukset);

        return suoritusrekisteriService;
    }

    private Date tomorrow() {
        return new Date(System.currentTimeMillis() + ONE_DAY);
    }
    private Date yesterday() {
        return new Date(System.currentTimeMillis() - ONE_DAY);
    }

}
