package fi.vm.sade.haku.virkailija.authentication;

import com.google.gson.*;
import fi.vm.sade.authentication.service.types.dto.SukupuoliType;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PersonJsonAdapter implements JsonSerializer<Person>, JsonDeserializer<Person> {

    private final static DateFormat AUTH_DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private final static DateFormat HAKU_DATE_FMT = new SimpleDateFormat("dd.MM.yyyy");

    private static Logger log = LoggerFactory.getLogger(PersonJsonAdapter.class);

//    {
//        "id": 73628,
//        "etunimet": "Asiakas",
//        "hetu": "240582-988J",
//        "kotikunta": null,
//        "kutsumanimi": "Asiakas",
//        "oidHenkilo": "1.2.246.562.24.30165282063",
//        "oppijanumero": null,
//        "sukunimi": "Testi",
//        "sukupuoli": null,
//        "turvakielto": null,
//        "kayttajatunnus": "240582-988J",
//        "henkiloTyyppi": "OPPIJA",
//        "eiSuomalaistaHetua": false,
//        "passivoitu": false,
//        "yksiloity": false,
//        "asiointiKieli": null,
//        "yksilointitieto": null,
//        "kielisyys": [],
//        "kansalaisuus": []
//    }


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
        if (isNotBlank(sex) && (SukupuoliType.MIES.value().equals(sex) || OppijaConstants.SUKUPUOLI_MIES.equals(sex))) {
            personBuilder.setSex(OppijaConstants.SUKUPUOLI_MIES);
        } else if (isNotBlank(sex) && (SukupuoliType.NAINEN.value().equals(sex) || OppijaConstants.SUKUPUOLI_NAINEN.equals(sex))) {
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
                log.error("Couldn't parse date of birth: "+dobStr);
            }
        }

        JsonElement kieliElem = personJson.get("asiointiKieli");
        if (kieliElem != null && !kieliElem.isJsonNull()) {
            JsonObject kieliObj = kieliElem.getAsJsonObject();
            if (kieliObj != null && !kieliObj.isJsonNull()) {
                personBuilder.setContactLanguage(getJsonString(kieliObj, "kieliKoodi"));
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
}
