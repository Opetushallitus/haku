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
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO;
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

        OpiskelijaDTO opiskelija = null;

        if (!opiskelijatiedot.isEmpty()) {
            Date hakukausiStart = resolveHakukausiStart(as);
            List<OpiskelijaDTO> validOpiskelijatiedot = opiskelijatiedot.stream()
                    .filter(o -> StringUtils.isNotEmpty(o.getOppilaitosOid()))
                    //.filter(o -> o.getLoppuPaiva() == null || o.getLoppuPaiva().after(hakukausiStart))
                    .collect(Collectors.toList());
            if(as.getKohdejoukkoUri().equals(KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO)) {
                LOGGER.info(String.format("KOSKI_JK - Selvitetään sopiva luokkatieto hakijalle %s", personOid));
                Map<String, List<SuoritusDTO>> suoritustiedot = suoritusrekisteriService.getSuoritukset(personOid, "");
                OpiskelijaDTO op = selectPreferredOpiskelijatietoForToinenAste(validOpiskelijatiedot, suoritustiedot, false);
                if (op == null) {
                    op = selectPreferredOpiskelijatietoForToinenAste(validOpiskelijatiedot, suoritustiedot, true);
                }
                if (op != null) {
                    opiskelija =  op;
                }
            } else {
                boolean found = false;
                for (OpiskelijaDTO dto : validOpiskelijatiedot) {
                    if (dto.getLoppuPaiva() == null || dto.getLoppuPaiva().after(hakukausiStart)) {
                        if (found) {
                            throw new ResourceNotFoundException("Person " + personOid + " in enrolled in multiple schools");
                        }
                        opiskelija = dto;
                        found = true;
                    }
                }
            }
            if (opiskelija != null) {
                LOGGER.info(String.format("KOSKI_JK - (Henkilö %s) : Löydettiin sopiva luokkatieto. Oppilaitos: %s ", opiskelija.getHenkiloOid(), opiskelija.getOppilaitosOid()));
            }
            Map<String, String> educationAnswers = new HashMap<>(application.getPhaseAnswers(PHASE_EDUCATION));

            educationAnswers = handleOpiskelija(educationAnswers, application, opiskelija);
            application.setVaiheenVastauksetAndSetPhaseId(PHASE_EDUCATION, educationAnswers);
        }

        return application;
    }

//    case s if s.suoritus.komo == Oids.lukioKomoOid => getOppilaitosAndLuokka("L", s, Oids.lukioKomoOid)
//    case s if s.suoritus.komo == Oids.lukioonvalmistavaKomoOid => getOppilaitosAndLuokka("ML", s, Oids.lukioonvalmistavaKomoOid)
//    case s if s.suoritus.komo == Oids.ammatillinenKomoOid => getOppilaitosAndLuokka("AK", s, Oids.ammatillinenKomoOid)
//    case s if s.suoritus.komo == Oids.ammatilliseenvalmistavaKomoOid => getOppilaitosAndLuokka("M", s, Oids.ammatilliseenvalmistavaKomoOid)
//    case s if s.suoritus.komo == Oids.ammattistarttiKomoOid => getOppilaitosAndLuokka("A", s, Oids.ammattistarttiKomoOid)
//    case s if s.suoritus.komo == Oids.valmentavaKomoOid => getOppilaitosAndLuokka("V", s, Oids.valmentavaKomoOid)
//    case s if s.suoritus.komo == Oids.valmaKomoOid => getOppilaitosAndLuokka("VALMA", s, Oids.valmaKomoOid)
//    case s if s.suoritus.komo == Oids.telmaKomoOid => getOppilaitosAndLuokka("TELMA", s, Oids.telmaKomoOid)
//    case s if s.suoritus.komo == Oids.lisaopetusKomoOid => getOppilaitosAndLuokka("10", s, Oids.lisaopetusKomoOid)
//    case s if s.suoritus.komo == Oids.perusopetusKomoOid && (s.luokkataso.getOrElse("").equals("9") || s.luokkataso.getOrElse("").equals("AIK")) => getOppilaitosAndLuokka("9", s, Oids.perusopetusKomoOid)

    private OpiskelijaDTO selectPreferredOpiskelijatietoForToinenAste(List<OpiskelijaDTO> opiskelijaDTOs, Map<String, List<SuoritusDTO>> suorituksetByKomoOids, boolean keskeytyneetSuorituksetOk) {

        //Preferenssijärjestys. Jos halutuimpia luokkatietoja löytyy tasan yksi, palautetaan se. Jos niitä löytyy useampia, kyseessä virhetilanne. Jos 0, siirrytään seuraavaan.
        List<String> luokkatasotJarj = Arrays.asList("10", "VALMA", "TELMA", "ML", "9", "AK", "L");
        List<SuoritusDTO> kaikkiSuoritukset = Collections.emptyList();
        for (String key : suorituksetByKomoOids.keySet()) {
            kaikkiSuoritukset = ListUtils.union(kaikkiSuoritukset, suorituksetByKomoOids.get(key));
        }
        LOGGER.info(String.format("KOSKI_JK - (Henkilö %s) : Suorituksia yhteensä %s kpl.", opiskelijaDTOs.get(0).getHenkiloOid(), kaikkiSuoritukset.size()));

        OpiskelijaDTO found = null;
        for (String luokkataso : luokkatasotJarj) {
            List<OpiskelijaDTO> oikeantasoisetLuokkatiedot = opiskelijaDTOs.stream().filter(lt -> lt.getLuokkataso().equals(luokkataso)).collect(Collectors.toList());
            LOGGER.info(String.format("KOSKI_JK - (Henkilö %s) : Oikeantasoisia (%s) luokkatietoja: %s kpl", opiskelijaDTOs.get(0).getHenkiloOid(), luokkataso, oikeantasoisetLuokkatiedot.size()));
            if (!oikeantasoisetLuokkatiedot.isEmpty()) {
                for (OpiskelijaDTO luokkatieto : oikeantasoisetLuokkatiedot) {
                    String suorituksenTila = getSuorituksenTilaForLuokkatieto(luokkatieto, kaikkiSuoritukset);
                    if (suorituksenTila.isEmpty() || suorituksenTila.equals("EPÄSELVÄ") || suorituksenTila.equals("TUNTEMATON")) {
                        LOGGER.warn(String.format("KOSKI_JK - (Henkilö %s) : Luokkatiedon tilan selvittämisessä oli ongelmia. Tila: ", luokkatieto.getHenkiloOid(), suorituksenTila));
                        throw new ResourceNotFoundException("Luokkatietoon liittyvän suorituksen tilan selvittäminen ei onnistunut.");
                    } else if (keskeytyneetSuorituksetOk == suorituksenTila.equals("KESKEYTYNYT")) {
                        if (found == null) {
                            LOGGER.info(String.format("KOSKI_JK - (Henkilö %s) : Valitaan luokkatieto: %s", luokkatieto.getHenkiloOid(), luokkatieto.toString()));
                            found = luokkatieto;
                        } else {
                            throw new ResourceNotFoundException("Sopivia luokkatietoja yli yksi.");
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
        List<SuoritusDTO> found = suoritukset.stream().filter(s -> s.getVahvistettu() && s.getMyontaja().equals(luokkatieto.getOppilaitosOid()) && s.getValmistuminen().equals(luokkatieto.getLoppuPaiva())).collect(Collectors.toList());
        if (found.size() == 1) {
            LOGGER.info(String.format("KOSKI_JK - (Henkilö %s) : Tasan yksi sopiva suoritus löytyi useita luokkatiedolle oppilaitoksessa %s. Palautetaan suorituksen tila: %s", luokkatieto.getHenkiloOid(), luokkatieto.getOppilaitosOid(), found.get(0).getTila()));
            return found.get(0).getTila();
        } else if (found.size() > 1 ) {
            LOGGER.warn(String.format("KOSKI_JK - (Henkilö %s) : Sopivia suorituksia löytyi useita luokkatiedolle oppilaitoksessa %s. Tämä saattaa olla ongelma, mutta palautetaan kuitenkin niiden tila jos se sattuu olemaan kaikille sama.", luokkatieto.getHenkiloOid(), luokkatieto.getOppilaitosOid()));
            for (SuoritusDTO s : found) {
                if (!found.get(0).getTila().equals(s.getTila())) {
                    return "EPÄSELVÄ";
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
