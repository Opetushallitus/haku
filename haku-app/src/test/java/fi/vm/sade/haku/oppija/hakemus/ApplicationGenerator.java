package fi.vm.sade.haku.oppija.hakemus;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import fi.vm.sade.haku.oppija.hakemus.converter.ApplicationToDBObjectFunction;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationOidDAOMongoImpl;
import fi.vm.sade.haku.oppija.lomake.service.SHA2Encrypter;
import fi.vm.sade.haku.oppija.lomake.service.impl.AESEncrypter;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApplicationGenerator {

    private static final DateFormat hetuDate = new SimpleDateFormat("ddMMyy");
    private static final String[] checks = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C",
            "D", "E", "F", "H", "J", "K", "L", "M", "N", "P", "R", "S", "T", "U", "V", "W", "X", "Y"};
    public static final String oidPrefix = "1.2.3.4.5";

    private ApplicationToDBObjectFunction applicationToDBObjectFunction;
    private MongoTemplate mongoTemplate;
    private MongoClient client;
    private DB db;
    private ApplicationOidDAOMongoImpl oidDao;

    private HashMap<String, Application> hetut = new HashMap<String, Application>();
    private AESEncrypter aesEncrypter;
    private SHA2Encrypter sha2Encrypter;

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException, UnknownHostException {

        ApplicationGenerator appGen = new ApplicationGenerator();
        for (int i = 0; i < 100; i++) {
            appGen.generate();
        }
    }

    public ApplicationGenerator() throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException, UnknownHostException {
        aesEncrypter = new AESEncrypter("tahantuleelaittaajotainfiksua", "tassapitaaolla32merkkiapitkajutt");
        sha2Encrypter = new SHA2Encrypter("tassajotainkummaakanssa");
        applicationToDBObjectFunction = new ApplicationToDBObjectFunction(aesEncrypter, sha2Encrypter);
        client = new MongoClient(new ServerAddress("localhost", 27017));
        db = client.getDB("hakulomake");
        mongoTemplate = new MongoTemplate(client, "hakulomake");
        oidDao = new ApplicationOidDAOMongoImpl(mongoTemplate, oidPrefix);
    }

    public Application generate() {
        Application application = new Application();

        Map<String, String> henkilotiedot = generateHenkilotiedot();
        Map<String, String> lisatiedot = generateLisatiedot();
        Map<String, String> hakutoiveet = generateHakutoiveet();
        Map<String, String> koulutustausta = generateKoulutustausta();
        Map<String, String> osaaminen = generateOsaaminen();

        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", henkilotiedot);
        application.setVaiheenVastauksetAndSetPhaseId("lisatiedot", lisatiedot);
        application.setVaiheenVastauksetAndSetPhaseId("hakutoiveet", hakutoiveet);
        application.setVaiheenVastauksetAndSetPhaseId("koulutustausta", koulutustausta);
        application.setVaiheenVastauksetAndSetPhaseId("osaaminen", osaaminen);
        application.updateNameMetadata();

        application.setOid(oidDao.generateNewOid());
        application.setApplicationSystemId("1.2.246.562.5.2013060313080811526781");

        db.getCollection("application").save(applicationToDBObjectFunction.apply(application));

        return application;
    }

    private Map<String, String> generateOsaaminen() {
        Map<String, String> osaaminen = new HashMap<String, String>();
        return osaaminen;
    }

    private Map<String, String> generateKoulutustausta() {
        Map<String, String> koulutustausta = new HashMap<String, String>();
        koulutustausta.put("POHJAKOULUTUS", "0");
        return koulutustausta;
    }

    private Map<String, String> generateHakutoiveet() {
        Map<String, String> hakutoiveet = new HashMap<String, String>();
        for (int i = 1; i <= 5; i++) {
            hakutoiveet.put("preference" + i + "-Opetuspiste", i == 1 ? "Helmi Liiketalousopisto" : "");
            hakutoiveet.put("preference" + i + "-discretionary", i == 1 ? "true" : "");
            hakutoiveet.put("preference" + i + "-Koulutus-educationDegree", i == 1 ? "32" : "");
            hakutoiveet.put("preference" + i + "-Koulutus-id", i == 1 ? "1.2.246.562.5.10067_02_873_1616" : "");
            hakutoiveet.put("preference" + i + "-Opetuspiste-id", i == 1 ? "1.2.246.562.10.50942158994" : "");
            hakutoiveet.put("preference" + i + "-Koulutus-id-sora", i == 1 ? "false" : "");
            hakutoiveet.put("preference" + i + "-Koulutus", i == 1 ? "Liiketalouden perustutkinto, pk" : "");
            hakutoiveet.put("preference" + i + "-Koulutus-id-aoIdentifier", i == 1 ? "873" : "");
            hakutoiveet.put("preference" + i + "-Koulutus-id-lang", i == 1 ? "FI" : "");
            hakutoiveet.put("preference" + i + "-Koulutus-id-athlete", i == 1 ? "false" : "");
        }
        return hakutoiveet;
    }

    private Map<String, String> generateLisatiedot() {
        Map<String, String> lisatiedot = new HashMap<String, String>();
        lisatiedot.put("asiointikieli", "suomi");

        return lisatiedot;
    }

    private Map<String, String> generateHenkilotiedot() {

        String hetu = generateHetu();
        while (hetut.get(hetu) != null) {
            hetu = generateHetu();
        }

        String firstName = RandomStringUtils.randomAlphabetic(3 + (int) (5 * Math.random()));
        String secondName = RandomStringUtils.randomAlphabetic(3 + (int) (5 * Math.random()));
        String lastName = RandomStringUtils.randomAlphabetic(4 + (int) (8 * Math.random()));

        Map<String, String> henkilotiedot = new HashMap<String, String>();
        henkilotiedot.put("kansalaisuus", "FIN");
        henkilotiedot.put("asuinmaa", "FIN");
        henkilotiedot.put("matkapuhelinnumero1", "");
        henkilotiedot.put("Sukunimi", lastName);
        henkilotiedot.put("Postinumero", "00100");
        henkilotiedot.put("lahiosoite", "Testikatu 4");
        henkilotiedot.put(OppijaConstants.ELEMENT_ID_SEX, getSex(hetu));
        henkilotiedot.put("Sähköposti", "");
        henkilotiedot.put("Kutsumanimi", firstName);
        henkilotiedot.put("Etunimet", firstName + " " + secondName);
        henkilotiedot.put("kotikunta", "jalasjarvi");
        henkilotiedot.put("aidinkieli", "FI");
        henkilotiedot.put("Henkilotunnus", hetu);

        return henkilotiedot;

    }

    private String getSex(String hetu) {
        int sexyNumber = Integer.valueOf(hetu.substring(9, 10));
        return String.valueOf(sexyNumber % 2 == 0 ? 2 : 1);
    }

    private static String generateHetu() {
        long start = 59958269570679L;
        long maxDelta = 473385626574L;

        long delta = (long) (maxDelta * Math.random());
        long ts = start + delta;
        String dob = hetuDate.format(new Date(ts));
        String id = String.format("%03d", (int) (Math.random() * 1000));
        int ssnNumber = Integer.valueOf(dob + id);
        String check = checks[ssnNumber % 31]; // NOSONAR

        StringBuilder hetuBuilder = new StringBuilder()
                .append(dob)
                .append("-")
                .append(id)
                .append(check);
        return hetuBuilder.toString();
    }

}
