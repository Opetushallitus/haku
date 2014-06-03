package fi.vm.sade.haku.virkailija.authentication;

import com.google.gson.*;
import fi.vm.sade.authentication.service.types.dto.SukupuoliType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PersonJsonAdapter implements JsonSerializer<Person>, JsonDeserializer<Person> {

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
            personJson.add("eiSuomalaistaHetua", new JsonPrimitive(false));
        } else {
            log.debug("Has no hetu");
            personJson.add("eiSuomalaistaHetua", new JsonPrimitive(true));
        }

        String sex = person.getSex();
        if (!isEmpty(sex)) {
            log.debug("Sex defined: " + sex);
            personJson.add("sukupuoli", new JsonPrimitive(sex.equals(SukupuoliType.MIES.value()) ? "MIES" : "NAINEN"));
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
                .setStudentOid(getJsonString(personJson, "oppijanumero"))
                .setNoSocialSecurityNumber(getJsonBoolean(personJson, "eiSuomalaistaHetua"));

        log.debug("Deserialized basic info");
        String sex = getJsonString(personJson, "sukupuoli");
        if (sex != null && sex.equals("MIES")) {
            personBuilder.setSex(SukupuoliType.MIES.value());
        } else if (sex != null && sex.equals("NAINEN")) {
            personBuilder.setSex(SukupuoliType.NAINEN.value());
        }

        Boolean securityOrder = getJsonBoolean(personJson, "turvakielto");
        if (securityOrder != null) {
            personBuilder.setSecurityOrder(securityOrder.booleanValue());
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
