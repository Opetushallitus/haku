package fi.vm.sade.haku.virkailija.authentication;

import com.google.gson.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PersonJsonAdapter implements JsonSerializer<Person>, JsonDeserializer<Person> {

    private final static DateFormat AUTH_DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private final static DateFormat HAKU_DATE_FMT = new SimpleDateFormat("dd.MM.yyyy");

    private final static Locale fi = new Locale("fi");
    private final static Map<String, String> kielityypit = new HashMap<>();

    private final static Map<String, String> asiointikielet = new HashMap<String, String>(6) {{
        put("suomi", "fi"); put("ruotsi", "sv"); put("englanti", "en");
    }};

    private final static Map<String, String> asiointikielikoodit = new HashMap<String, String>(6) {{
        put("fi", "suomi"); put("sv", "ruotsi"); put("en", "englanti");
    }};

    private static Logger log = LoggerFactory.getLogger(PersonJsonAdapter.class);

    /*
        Esimerkki-JSON, haettu henkilöpalvelusta:

        {
          "id": 4056760,
          "etunimet": "sven",
          "syntymaaika": "1995-07-31",
          "passinnumero": null,
          "hetu": "310795-9958",
          "kutsumanimi": "sven",
          "oidHenkilo": "1.2.246.562.24.64490029019",
          "oppijanumero": null,
          "sukunimi": "svensson",
          "sukupuoli": "1",
          "turvakielto": null,
          "henkiloTyyppi": "VIRKAILIJA",
          "eiSuomalaistaHetua": false,
          "passivoitu": false,
          "yksiloity": false,
          "yksiloityVTJ": false,
          "yksilointiYritetty": false,
          "duplicate": false,
          "created": 1438332515439,
          "modified": 1438332515439,
          "kasittelijaOid": "1.2.246.562.24.18542484257",
          "asiointiKieli": {
            "id": 3,
            "kieliKoodi": "sv",
            "kieliTyyppi": "svenska"
          },
          "aidinkieli": null,
          "huoltaja": null,
          "kayttajatiedot": {
            "id": 4056761,
            "username": "svensson"
          },
          "kielisyys": [],
          "kansalaisuus": [],
          "yhteystiedotRyhma": []
        }
     */

    @Override
    public JsonElement serialize(Person person, Type typeOfSrc, JsonSerializationContext context) {
        log.debug("Serializing person {" + person + "}");
        JsonObject personJson = new JsonObject();
        personJson.add("etunimet", new JsonPrimitive(person.getFirstNames()));
        personJson.add("kutsumanimi", new JsonPrimitive(person.getNickName()));
        personJson.add("sukunimi", new JsonPrimitive(person.getLastName()));
        personJson.add("henkiloTyyppi", new JsonPrimitive("OPPIJA"));
        personJson.add("yksiloity", new JsonPrimitive(false));
        personJson.add("passivoitu", new JsonPrimitive(false));
        log.debug("Serialized basic info");

        String hetu = person.getSocialSecurityNumber();
        if (!isEmpty(hetu)) {
            log.debug("Has hetu: " + hetu);
            personJson.add("hetu", new JsonPrimitive(hetu));
        } else {
            log.debug("Has no hetu");
        }

        String dateOfBirth = person.getDateOfBirth();
        if (!isEmpty(dateOfBirth)) {
            try {
                Date dob = HAKU_DATE_FMT.parse(dateOfBirth);
                personJson.add("syntymaaika", new JsonPrimitive(AUTH_DATE_FMT.format(dob)));
            } catch (ParseException e) {
                log.error("Couldn't parse date of birth: "+dateOfBirth);
            }
        }

        String sex = person.getSex();
        if (!isEmpty(sex) && OppijaConstants.SUKUPUOLI_MIES.equals(sex)) {
            personJson.add("sukupuoli", new JsonPrimitive(OppijaConstants.SUKUPUOLI_MIES));
        } else if (!isEmpty(sex) && OppijaConstants.SUKUPUOLI_NAINEN.equals(sex)) {
            personJson.add("sukupuoli", new JsonPrimitive(OppijaConstants.SUKUPUOLI_NAINEN));
        }

        personJson = addJsonLanguage("aidinkieli", person.getLanguage(), personJson);
        personJson = addJsonLanguage("asiointiKieli", asiointikielet.get(person.getContactLanguage()), personJson);

        return personJson;
    }

    @Override
    public Person deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject personJson = json.getAsJsonObject();

        PersonBuilder personBuilder = PersonBuilder.start()
                .setFirstNames(getJsonString(personJson, "etunimet"))
                .setNickName(getJsonString(personJson, "kutsumanimi"))
                .setLastName(getJsonString(personJson, "sukunimi"))
                .setSocialSecurityNumber(getJsonString(personJson, "hetu"))
                .setPersonOid(getJsonString(personJson, "oidHenkilo"))
                .setStudentOid(getJsonString(personJson, "oppijanumero"));

        log.debug("Deserialized basic info");
        String sex = getJsonString(personJson, "sukupuoli");
        if (OppijaConstants.SUKUPUOLI_MIES.equals(sex)) {
            personBuilder.setSex(OppijaConstants.SUKUPUOLI_MIES);
        } else if (OppijaConstants.SUKUPUOLI_NAINEN.equals(sex)) {
            personBuilder.setSex(OppijaConstants.SUKUPUOLI_NAINEN);
        }

        Boolean securityOrder = getJsonBoolean(personJson, "turvakielto");
        if (securityOrder != null) {
            personBuilder.setSecurityOrder(securityOrder.booleanValue());
        }

        String dobStr = getJsonString(personJson, "syntymaaika");
        if (isNotBlank(dobStr)) {
            try {
                Date dob = AUTH_DATE_FMT.parse(dobStr);
                personBuilder.setDateOfBirth(HAKU_DATE_FMT.format(dob));
            } catch (ParseException e) {
                log.error("Couldn't parse date of birth: '"+dobStr+"' for user "+personBuilder.getPersonOid());
            } catch (NumberFormatException nfe) {
                log.error("Couldn't format date of birth: '"+dobStr+"' for user "+personBuilder.getPersonOid());
            }
        }

        JsonElement kieliElem = personJson.get("aidinkieli");
        if (kieliElem != null && !kieliElem.isJsonNull()) {
            JsonObject kieliObj = kieliElem.getAsJsonObject();
            if (kieliObj != null && !kieliObj.isJsonNull()) {
                String lang = getJsonString(kieliObj, "kieliKoodi");
                personBuilder.setLanguage(lang != null ? lang.toUpperCase() : null);
            }
        }
        kieliElem = personJson.get("asiointiKieli");
        if (kieliElem != null && !kieliElem.isJsonNull()) {
            JsonObject kieliObj = kieliElem.getAsJsonObject();
            if (kieliObj != null && !kieliObj.isJsonNull()) {
                String asiointikielikoodi = getJsonString(kieliObj, "kieliKoodi");
                personBuilder.setContactLanguage(asiointikielikoodi);
            }
        }

        return personBuilder.get();
    }

    private String getJsonString(JsonObject personJson, String field) {
        JsonElement elem = personJson.get(field);
        String value = null;
        if (elem != null && !elem.isJsonNull()) {
            value = elem.getAsString();
        }
        return value;
    }

    private Boolean getJsonBoolean(JsonObject personJson, String field) {
        JsonElement elem = personJson.get(field);
        Boolean value = null;
        if (elem != null && !elem.isJsonNull()) {
            value = elem.getAsBoolean();
        }
        return value;
    }

    private JsonObject addJsonLanguage(String langElem, String lang, JsonObject personJson) {
        if (isEmpty(lang)) {
            return personJson;
        }
        lang = lang.toLowerCase(fi);
        JsonObject langObj = new JsonObject();
        langObj.add("kieliKoodi", new JsonPrimitive(lang));
        langObj.add("kieliTyyppi", new JsonPrimitive(getLanguageName(lang)));
        personJson.add(langElem, langObj);
        return personJson;
    }

    private static String getLanguageName(String lang){
        String name = kielityypit.get(lang);
        if (name == null) {
            kielityypit.put(lang, (new Locale(lang)).getDisplayLanguage(fi));
            name = kielityypit.get(lang);
        }
        return name;
    }
}
