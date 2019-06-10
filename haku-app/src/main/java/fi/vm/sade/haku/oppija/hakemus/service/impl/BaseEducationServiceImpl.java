package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static java.util.Calendar.*;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Service
public class BaseEducationServiceImpl implements BaseEducationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseEducationServiceImpl.class);

    private final SuoritusrekisteriService suoritusrekisteriService;
    private final ApplicationSystemService applicationSystemService;

    @Autowired
    public BaseEducationServiceImpl(SuoritusrekisteriService suoritusrekisteriService,
                                    ApplicationSystemService applicationSystemService) {
        this.suoritusrekisteriService = suoritusrekisteriService;
        this.applicationSystemService = applicationSystemService;
    }

    private static boolean hasSendingSchoolQuestion(Element elem, Application application) {
        if(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL.equals(elem.getId())) {
            return true;
        }
        for (Element child : elem.getChildren(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION))) {
            if(hasSendingSchoolQuestion(child, application)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSendingSchoolQuestionOnForm(ApplicationSystem as, Application application) {
        return hasSendingSchoolQuestion(as.getForm().getChildById(OppijaConstants.PHASE_EDUCATION), application);
    }

    @Override
    public Application addSendingSchool(Application application) {
        String personOid = application.getPersonOid();
        if (isEmpty(personOid)) {
            return application;
        }

        ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());
        if(hasSendingSchoolQuestionOnForm(as, application)) {
            return application;
        }

        Date hakukausiStart = resolveHakukausiStart(as);
        List<OpiskelijaDTO> opiskelijatiedot = suoritusrekisteriService.getOpiskelijatiedot(personOid);
        List<OpiskelijaDTO> tuoreetOpiskelijatiedot = opiskelijatiedot.stream()
                .filter(o -> StringUtils.isNotEmpty(o.getOppilaitosOid()))
                .filter(o -> o.getLoppuPaiva() != null && o.getLoppuPaiva().after(hakukausiStart))
                .collect(Collectors.toList());


        OpiskelijaDTO opiskelija = null;
        if (!tuoreetOpiskelijatiedot.isEmpty()) {
            List<SuoritusDTO> suoritustiedot = suoritusrekisteriService.getSuorituksetAsList(personOid);

            //Yritetään ensin tuoreilla kesken olevilla luokkatiedoilla, sitten tuoreilla keskeytyneillä.
            LOGGER.info("Jälkikäsittely - tuoreita opiskelijatietoja " + tuoreetOpiskelijatiedot.size());
            opiskelija = selectPreferredLuokkatieto(tuoreetOpiskelijatiedot, suoritustiedot, false);
            if (opiskelija == null) {
                opiskelija = selectPreferredLuokkatieto(tuoreetOpiskelijatiedot, suoritustiedot, true);
            }
            if (opiskelija != null) {
                LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) Löydettiin yksiselitteinen luokkatieto, oppilaitos: %s.", opiskelija.getHenkiloOid(), opiskelija.getOppilaitosOid()));
            } else {
                LOGGER.warn(String.format("Jälkikäsittely - (Henkilö %s) Ei löydetty soveltuvaa luokkatietoa Suresta.", personOid));
            }
        }

        Map<String, String> educationAnswers = new HashMap<>(application.getPhaseAnswers(PHASE_EDUCATION));

        educationAnswers = handleOpiskelija(educationAnswers, application, opiskelija);
        application.setVaiheenVastauksetAndSetPhaseId(PHASE_EDUCATION, educationAnswers);

        return application;
    }

    //    9	yhdeksän
    //    10 kymmenen
    //    A	ammattistartti
    //    AK ammatillinen peruskoulutus
    //    L	lukio
    //    M	maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus
    //    ML maahanmuuttajien lukiokoulutukseen valmistava koulutus
    //    TELMA	työhön ja itsenäiseen elämään valmentava koulutus
    //    V	vammaisten valmentava ja kuntouttava opetus ja ohjaus
    //    VALMA	ammatilliseen peruskoulutukseen valmentava koulutus

    private OpiskelijaDTO selectPreferredLuokkatieto(List<OpiskelijaDTO> opiskelijaDTOs, List<SuoritusDTO> kaikkiSuoritukset, boolean keskeytyneetSuorituksetOk) {
        if (opiskelijaDTOs.isEmpty()) {
            return null;
        }
        //Preferenssijärjestys. Jos halutuimpia luokkatietoja löytyy tasan yksi, palautetaan se. Jos niitä löytyy useampia, kyseessä virhetilanne. Jos 0, siirrytään seuraavaan.
        List<String> luokkatasotJarj = Arrays.asList("10", "VALMA", "TELMA", "ML", "9", "L");
        List<String> luokkatasojenKomoOidsJarj = Arrays.asList(KOMO_OID_KYMPPI, KOMO_OID_VALMA, KOMO_OID_TELMA, KOMO_OID_MLV, KOMO_OID_PERUSOPETUS, KOMO_OID_LUKIO);
        LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) : Suorituksia yhteensä %s kpl.", opiskelijaDTOs.get(0).getHenkiloOid(), kaikkiSuoritukset.size()));

        OpiskelijaDTO found = null;
        int komoIndex = 0;
        for (String luokkataso : luokkatasotJarj) {
            String luokkatasonKomoOid = luokkatasojenKomoOidsJarj.get(komoIndex);
            List<OpiskelijaDTO> oikeantasoisetLuokkatiedot = opiskelijaDTOs.stream().filter(lt -> lt.getLuokkataso().equals(luokkataso)).collect(Collectors.toList());
            LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) : Oikeantasoisia (%s) luokkatietoja: %s kpl. Haettava KomoOid: %s", opiskelijaDTOs.get(0).getHenkiloOid(), luokkataso, oikeantasoisetLuokkatiedot.size(), luokkatasonKomoOid));
            if (!oikeantasoisetLuokkatiedot.isEmpty()) {
                //LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) : Oikeantasoisia (%s) luokkatietoja: %s kpl.", opiskelijaDTOs.get(0).getHenkiloOid(), luokkataso, oikeantasoisetLuokkatiedot.size()));
                if (luokkataso.equals("VALMA") && oikeantasoisetLuokkatiedot.size() == 1) {
                    OpiskelijaDTO luokkatieto = oikeantasoisetLuokkatiedot.get(0);
                    LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s): vain yksi VALMA-luokkatieto löytyi. Valitaan se lähtökouluksi tutkimatta mahdollisen suorituksen tilaa tarkemmin.", luokkatieto.getHenkiloOid()));
                    return luokkatieto;
                }
                for (OpiskelijaDTO luokkatieto : oikeantasoisetLuokkatiedot) {
                    String suorituksenTila = getSuorituksenTilaForLuokkatieto(luokkatieto, kaikkiSuoritukset, luokkatasonKomoOid);
                    if (suorituksenTila.isEmpty() || suorituksenTila.equals("TUNTEMATON")) {
                        LOGGER.error(String.format("Jälkikäsittely - (Henkilö %s) : Luokkatiedon tilan selvittämisessä oli ongelmia. Tila: ", luokkatieto.getHenkiloOid(), suorituksenTila));
                        throw new ResourceNotFoundException("Luokkatiedon tilan selvittäminen ei onnistunut yksiselitteisesti.");
                    } else if (keskeytyneetSuorituksetOk == suorituksenTila.equals("KESKEYTYNYT")) {
                        if (found == null) {
                            found = luokkatieto;
                        } else {
                            LOGGER.error(String.format("Jälkikäsittely - (Henkilö %s) : Palautetaan luokkatiedoksi null, koska soveltuvia luokkatietoja oli enemmän kuin yksi.", luokkatieto.getHenkiloOid()));
                            throw new ResourceNotFoundException("Soveltuvia luokkatietoja oli enemmän kuin yksi.");
                        }
                    }
                }
                if (found != null) {
                    return found;
                }
            }
            komoIndex++;
        }
        return null;
    }

    private String getSuorituksenTilaForLuokkatieto(OpiskelijaDTO luokkatieto, List<SuoritusDTO> suoritukset, String haluttuKomoOid) {
        List<SuoritusDTO> foundMatchingOppilaitosAndKomo = suoritukset.stream()
                .filter(s -> s.getMyontaja().equals(luokkatieto.getOppilaitosOid()))
                .filter(s -> s.getKomo() == null || s.getKomo().equals(haluttuKomoOid))
                .filter(SuoritusDTO::getVahvistettu)
                .collect(Collectors.toList());
        List<SuoritusDTO> foundMatchingOppilaitosAndLoppupaiva = suoritukset.stream()
                .filter(s -> (luokkatieto.getLoppuPaiva() == null || s.getValmistuminen() == null) || luokkatieto.getLoppuPaiva().equals(s.getValmistuminen()))
                .filter(s -> s.getMyontaja().equals(luokkatieto.getOppilaitosOid()))
                .filter(SuoritusDTO::getVahvistettu)
                .collect(Collectors.toList());
        if (foundMatchingOppilaitosAndKomo.size() == 1) {
            LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) : Tasan yksi sopiva suoritus löytyi luokkatiedolle oppilaitoksessa %s. Palautetaan suorituksen tila: %s", luokkatieto.getHenkiloOid(), luokkatieto.getOppilaitosOid(), foundMatchingOppilaitosAndKomo.get(0).getTila()));
            return foundMatchingOppilaitosAndKomo.get(0).getTila();
        } else if (foundMatchingOppilaitosAndLoppupaiva.size() == 1) {
            LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) : Tasan yksi sopiva suoritus oikealla päivämäärällä löytyi luokkatiedolle oppilaitoksessa %s. Palautetaan suorituksen tila: %s", luokkatieto.getHenkiloOid(), luokkatieto.getOppilaitosOid(), foundMatchingOppilaitosAndLoppupaiva.get(0).getTila()));
            return foundMatchingOppilaitosAndLoppupaiva.get(0).getTila();
        } else if (foundMatchingOppilaitosAndKomo.size() > 1 ) {
            LOGGER.warn(String.format("Jälkikäsittely - (Henkilö %s) : Sopivia suorituksia löytyi useita luokkatiedolle oppilaitoksessa %s. Tämä saattaa olla ongelma, mutta palautetaan kuitenkin niiden tila jos se sattuu olemaan kaikille sama.", luokkatieto.getHenkiloOid(), luokkatieto.getOppilaitosOid()));
            for (SuoritusDTO s : foundMatchingOppilaitosAndKomo) {
                if (!foundMatchingOppilaitosAndKomo.get(0).getTila().equals(s.getTila())) {
                    return "TUNTEMATON";
                }
            }
            return foundMatchingOppilaitosAndKomo.get(0).getTila();
        } else {
            return "TUNTEMATON";
            //throw new ResourceNotFoundException(String.format("Opiskelijalle %s ei löytynyt yksiselitteistä luokkatietoa %s vastaavaa suoritusta. Sopivia suorituksia löytyi %s kpl.", luokkatieto.getHenkiloOid(), luokkatieto.toString(), foundMatchingOppilaitosAndLoppupaiva.size()));
        }
    }

    private Date resolveHakukausiStart(ApplicationSystem as) {
        Calendar start = GregorianCalendar.getInstance();
        start.set(DAY_OF_MONTH, 1);
        start.set(MONTH, HAKUKAUSI_KEVAT.equals(as.getHakukausiUri()) ? JANUARY : AUGUST);
        start.set(YEAR, as.getHakukausiVuosi());
        return start.getTime();
    }

    private Map<String, String> handleOpiskelija(Map<String, String> answers, Application application, OpiskelijaDTO opiskelija) {
        String sendingSchool = opiskelija != null ? opiskelija.getOppilaitosOid() : null;
        String sendingClass = opiskelija != null ? opiskelija.getLuokka() : null;
        String classLevel = opiskelija != null ? opiskelija.getLuokkataso() : null;

        if (isNotEmpty(sendingClass)) {
            sendingClass = sendingClass.toUpperCase();
        }
        if (isNotEmpty(classLevel)) {
            classLevel = classLevel.toUpperCase();
        }

        answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_SENDING_SCHOOL, sendingSchool);
        answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_SENDING_CLASS, sendingClass);
        answers = addRegisterValue(application, answers, OppijaConstants.ELEMENT_ID_CLASS_LEVEL, classLevel);

        return answers;
    }

    private Map<String, String> addRegisterValue(Application application, Map<String, String> answers,
                                                 String key, String value) {
        String oldValue = answers.put(key, value);
        if (value == null) {
            answers.remove(key);
        }
        application.addOverriddenAnswer(key, oldValue);
        LOGGER.debug("Changing value key: {}, value: {} -> {}", key, oldValue, value);
        return answers;
    }
}
