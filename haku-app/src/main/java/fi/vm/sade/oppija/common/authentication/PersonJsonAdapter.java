package fi.vm.sade.oppija.common.authentication;

import com.google.gson.*;
import fi.vm.sade.authentication.service.types.dto.SukupuoliType;

import java.lang.reflect.Type;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PersonJsonAdapter implements JsonSerializer<Person> {

//    {
//        "id" : 168,
//        "etunimet" : "Etu Nimet",
//        "hetu" : "010101-0101",
//        "kotikunta" : "Kaupunki",
//        "kutsumanimi" : "Etu",
//        "oidHenkilo" : "1.2.246.562.24.99999999999",
//        "sukunimi" : "Sukunimi",
//        "sukupuoli" : "MIES",
//        "turvakielto" : false,
//        "kayttajatunnus" : "etu.sukunimi@example.com",
//        "henkiloTyyppi" : "OPPIJA",
//        "eiSuomalaistaHetua" : false,
//        "passivoitu" : false,
//        "yksiloity" : false,
//        "yksilointitieto" : null,
//        "kielisyys" : [ {
//            "id" : 117,
//            "kieliKoodi" : "fi",
//            "kieliTyyppi" : "suomi"
//        } ],
//        "kansalaisuus" : [ {
//            "id" : 120,
//            "kansalaisuusKoodi" : "Suomi"
//        } ]
//    }

//    "kayttajatunnus",
//    "kansalaisuus",
//    "asiointiKieli"

    @Override
    public JsonElement serialize(Person person, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject personJson = new JsonObject();
        personJson.add("etunimet", new JsonPrimitive(person.getFirstNames()));
        personJson.add("hetu", new JsonPrimitive(person.getSocialSecurityNumber()));
        personJson.add("kotikunta", new JsonPrimitive(person.getHomeCity()));
        personJson.add("kutsumanimi", new JsonPrimitive(person.getNickName()));
        personJson.add("sukunimi", new JsonPrimitive(person.getLastName()));
        String sex = person.getSex();
        if (!isEmpty(sex)) {
            personJson.add("sukupuoli", new JsonPrimitive(sex.equals(SukupuoliType.MIES.value()) ? "MIES" : "NAINEN"));
        }
        personJson.add("turvakielto", new JsonPrimitive(person.isSecurityOrder()));
        personJson.add("henkiloTyyppi", new JsonPrimitive("OPPIJA"));
        personJson.add("eiSuomalaistaHetua", new JsonPrimitive(person.isNoSocialSecurityNumber()));

        return personJson;
    }

}
