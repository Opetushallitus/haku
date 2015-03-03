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

import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.LISAOPETUS_KOMO;
import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.PERUSOPETUS_KOMO;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PERUSKOULU;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        pkKesken = new SuoritusDTO("pkKesken", PERUSOPETUS_KOMO, "myontaja",
                "KESKEN", new Date(cal.getTimeInMillis()), "personOid", "Ei", "fi");
        kymppiKesken = new SuoritusDTO("kymppiKesken", LISAOPETUS_KOMO, "myontaja",
                "KESKEN", new Date(cal.getTimeInMillis()), "personOid", "Ei", "fi");
        cal.set(2014, Calendar.JUNE, 1);
        pkValmis = new SuoritusDTO("pkValmis", PERUSOPETUS_KOMO, "myontaja",
                "VALMIS", new Date(cal.getTimeInMillis()), "personOid", "Ei", "fi");

    }

    @Test
    public void testGetArvosanatPk() {
        SuoritusrekisteriService suoritusrekisteriService = mockSuoritusrekisteriService("personOid",
                new HashMap<String, SuoritusDTO>() {{
                    put(PERUSOPETUS_KOMO, pkKesken);
                }});

        BaseEducationService baseEducationService = new BaseEducationServiceImpl(suoritusrekisteriService);
        when(suoritusrekisteriService.getArvosanat(eq("pkKesken")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("1", "AI", "9", false, "fi"));
                    add(new ArvosanaDTO("2", "AI", "8", true, "fi"));
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
                new HashMap<String, SuoritusDTO>() {{ put(PERUSOPETUS_KOMO, pkValmis); put(LISAOPETUS_KOMO, kymppiKesken);}});

        when(suoritusrekisteriService.getArvosanat(eq("pkValmis")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("1", "AI", "8", false, "fi"));

                    add(new ArvosanaDTO("2", "BI", "8", false, null));
                    add(new ArvosanaDTO("3", "BI", "7", true, null));

                    add(new ArvosanaDTO("4", "CI", "7", false, null));
                }});
        when(suoritusrekisteriService.getArvosanat(eq("kymppiKesken")))
                .thenReturn(new ArrayList<ArvosanaDTO>() {{
                    add(new ArvosanaDTO("5", "AI", "9", false, "fi"));
                    add(new ArvosanaDTO("6", "BI", "8", false, null));
                    add(new ArvosanaDTO("7", "BI", "8", true, null));
                }});

        BaseEducationService baseEducationService = new BaseEducationServiceImpl(suoritusrekisteriService);
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

    private SuoritusrekisteriService mockSuoritusrekisteriService(String personOid, Map<String, SuoritusDTO> suoritukset) {

        SuoritusrekisteriService suoritusrekisteriService = mock(SuoritusrekisteriService.class);
        when(suoritusrekisteriService.getSuoritukset(eq(personOid)))
                .thenReturn(suoritukset);

        return suoritusrekisteriService;
    }

}
