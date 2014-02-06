package fi.vm.sade.haku.virkailija.authentication;

import com.google.gson.*;
import fi.vm.sade.authentication.service.types.dto.SukupuoliType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HEAD;
import java.lang.reflect.Type;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PersonJsonAdapter implements JsonSerializer<Person>, JsonDeserializer<Person> {

    private static Logger log = LoggerFactory.getLogger(PersonJsonAdapter.class);

// byHetu
//    {
//            "etunimet": "aa",
//            "syntymaaika": null,
//            "passinnumero": null,
//            "hetu": "123456-789",
//            "kutsumanimi": "aa",
//            "oidHenkilo": "1.2.246.562.24.71944845619",
//            "oppijanumero": null,
//            "sukunimi": "AA",
//            "sukupuoli": null,
//            "turvakielto": null,
//            "henkiloTyyppi": "OPPIJA",
//            "eiSuomalaistaHetua": false,
//            "passivoitu": false,
//            "yksiloity": false,
//            "kayttajatiedot": {
//            "username": "",
//            "kielisyys": [ {
//                "kieliKoodi": "fi",
//                "kieliTyyppi": "suomi" }
//            ],
//            "kansalaisuus": [ { "kansalaisuusKoodi": "Suomi" } ]
//    }

//    by oid
//    {
//        "id": 1,
//            "etunimet": "ROOT",
//            "syntymaaika": null,
//            "passinnumero": null,
//            "hetu": null,
//            "kutsumanimi": "ROOT",
//            "oidHenkilo": "1.2.246.562.24.00000000001",
//            "oppijanumero": null,
//            "sukunimi": "USER",
//            "sukupuoli": "MIES",
//            "turvakielto": false,
//            "henkiloTyyppi": "VIRKAILIJA",
//            "eiSuomalaistaHetua": false,
//            "passivoitu": false,
//            "yksiloity": true,
//            "asiointiKieli": {
//                "kieliKoodi": "fi",
//                "kieliTyyppi": "suomi"
//            },
//        "yksilointitieto": null,
//            "kayttajatiedot": {
//        "username": "ophadmin"
//    },
//        "kielisyys": [
//        {
//            "kieliKoodi": "fi",
//                "kieliTyyppi": "suomi"
//        }
//        ],
//        "kansalaisuus": [
//        {
//            "kansalaisuusKoodi": "Suomi"
//        }
//        ],
//        "yhteystiedotRyhma": []
//    }


    @Override
    public JsonElement serialize(Person person, Type typeOfSrc, JsonSerializationContext context) {
        log.debug("Serializing person {"+person+"}");
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
            log.debug("Has hetu: "+hetu);
            personJson.add("hetu", new JsonPrimitive(hetu));
            personJson.add("eiSuomalaistaHetua", new JsonPrimitive(false));
        } else {
            log.debug("Has no hetu");
            personJson.add("eiSuomalaistaHetua", new JsonPrimitive(true));
        }

        String sex = person.getSex();
        if (!isEmpty(sex)) {
            log.debug("Sex defined: "+sex);
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
                .setNoSocialSecurityNumber(getJsonBoolean(personJson, "eiSuomalaistaHetua"));

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

//        personBuilder.setEmail();
//        personBuilder.setHomeCity();
//        personBuilder.setLanguage();
//        personBuilder.setNationality();

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
