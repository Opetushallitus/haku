package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.HAKUKAUSI_KEVAT;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PHASE_EDUCATION;
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

        List<OpiskelijaDTO> opiskelijatiedot = suoritusrekisteriService.getOpiskelijatiedot(personOid);
        if (!opiskelijatiedot.isEmpty()) {
            Date hakukausiStart = resolveHakukausiStart(as);
            List<OpiskelijaDTO> validOpiskelijatiedot = opiskelijatiedot.stream()
                    .filter(o -> StringUtils.isNotEmpty(o.getOppilaitosOid()))
                    .collect(Collectors.toList());
            List<SuoritusDTO> suoritustiedot = suoritusrekisteriService.getSuorituksetAsList(personOid);

            //Yritetään ensin tuoreilla kesken olevilla luokkatiedoilla, sitten kaikilla ei-keskeytyneillä ja lopuksi kaikilla keskeytyneillä
            List<OpiskelijaDTO> tuoreetOpiskelijatiedot = validOpiskelijatiedot.stream().filter(o -> o.getLoppuPaiva().after(hakukausiStart)).collect(Collectors.toList());
            LOGGER.info("Jälkikäsittely - tuoreita opiskelijatietoja " + tuoreetOpiskelijatiedot.size());
            OpiskelijaDTO opiskelija = selectPreferredLuokkatieto(tuoreetOpiskelijatiedot, suoritustiedot, false);
            if (opiskelija == null) {
                opiskelija = selectPreferredLuokkatieto(validOpiskelijatiedot, suoritustiedot, false);
                if (opiskelija == null) {
                    opiskelija = selectPreferredLuokkatieto(validOpiskelijatiedot, suoritustiedot, true);
                }
            }

            Map<String, String> educationAnswers = new HashMap<>(application.getPhaseAnswers(PHASE_EDUCATION));

            educationAnswers = handleOpiskelija(educationAnswers, application, opiskelija);
            application.setVaiheenVastauksetAndSetPhaseId(PHASE_EDUCATION, educationAnswers);
        }

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
        List<String> luokkatasotJarj = Arrays.asList("10", "VALMA", "TELMA", "ML", "9", "AK", "L");

        LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) : Suorituksia yhteensä %s kpl.", opiskelijaDTOs.get(0).getHenkiloOid(), kaikkiSuoritukset.size()));

        OpiskelijaDTO found = null;
        for (String luokkataso : luokkatasotJarj) {
            List<OpiskelijaDTO> oikeantasoisetLuokkatiedot = opiskelijaDTOs.stream().filter(lt -> lt.getLuokkataso().equals(luokkataso)).collect(Collectors.toList());
            if (!oikeantasoisetLuokkatiedot.isEmpty()) {
                LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) : Oikeantasoisia (%s) luokkatietoja: %s kpl", opiskelijaDTOs.get(0).getHenkiloOid(), luokkataso, oikeantasoisetLuokkatiedot.size()));
            }
            if (!oikeantasoisetLuokkatiedot.isEmpty()) {
                for (OpiskelijaDTO luokkatieto : oikeantasoisetLuokkatiedot) {
                    String suorituksenTila = getSuorituksenTilaForLuokkatieto(luokkatieto, kaikkiSuoritukset);
                    if (suorituksenTila.isEmpty() || suorituksenTila.equals("TUNTEMATON")) {
                        LOGGER.warn(String.format("Jälkikäsittely - (Henkilö %s) : Luokkatiedon tilan selvittämisessä oli ongelmia. Tila: ", luokkatieto.getHenkiloOid(), suorituksenTila));
                    } else if (keskeytyneetSuorituksetOk == suorituksenTila.equals("KESKEYTYNYT")) {
                        if (found == null) {
                            found = luokkatieto;
                        } else {
                            LOGGER.warn(String.format("Jälkikäsittely - (Henkilö %s) : Palautetaan luokkatiedoksi null, koska soveltuvia luokkatietoja oli enemmän kuin yksi.", luokkatieto.getHenkiloOid()));
                            return null;
                        }
                    }
                }
                if (found != null) {
                    return found;
                }
            }
        }
        return found;
    }

    private String getSuorituksenTilaForLuokkatieto(OpiskelijaDTO luokkatieto, List<SuoritusDTO> suoritukset) {
        List<SuoritusDTO> found = suoritukset.stream().filter(s -> s.getVahvistettu() && s.getMyontaja().equals(luokkatieto.getOppilaitosOid())).collect(Collectors.toList());
        if (found.size() == 1) {
            LOGGER.info(String.format("Jälkikäsittely - (Henkilö %s) : Tasan yksi sopiva suoritus löytyi luokkatiedolle oppilaitoksessa %s. Palautetaan suorituksen tila: %s", luokkatieto.getHenkiloOid(), luokkatieto.getOppilaitosOid(), found.get(0).getTila()));
            return found.get(0).getTila();
        } else if (found.size() > 1 ) {
            LOGGER.warn(String.format("Jälkikäsittely - (Henkilö %s) : Sopivia suorituksia löytyi useita luokkatiedolle oppilaitoksessa %s. Tämä saattaa olla ongelma, mutta palautetaan kuitenkin niiden tila jos se sattuu olemaan kaikille sama.", luokkatieto.getHenkiloOid(), luokkatieto.getOppilaitosOid()));
            for (SuoritusDTO s : found) {
                if (!found.get(0).getTila().equals(s.getTila())) {
                    return "TUNTEMATON";
                }
            }
            return found.get(0).getTila();
        } else {
            return "TUNTEMATON";
            //throw new ResourceNotFoundException(String.format("Opiskelijalle %s ei löytynyt yksiselitteistä luokkatietoa %s vastaavaa suoritusta. Sopivia suorituksia löytyi %s kpl.", luokkatieto.getHenkiloOid(), luokkatieto.toString(), found.size()));
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
