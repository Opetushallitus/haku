package fi.vm.sade.haku.oppija.common.organisaatio.impl;

import com.google.gson.*;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationGroupListRestDTO;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationGroupRestDTO;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizationGroupListRestDTOAdapter
        implements JsonSerializer<OrganizationGroupListRestDTO>,
        JsonDeserializer<OrganizationGroupListRestDTO> {

    @Override
    public OrganizationGroupListRestDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray groupList = json.getAsJsonArray();
        OrganizationGroupListRestDTO groupListRestDTO = new OrganizationGroupListRestDTO();

        for (int i = 0; i < groupList.size(); i++) {
            JsonObject elem = groupList.get(i).getAsJsonObject();
            String oid = elem.get("oid").getAsString();
            I18nText nimi = buildNimi(elem.get("nimi").getAsJsonObject());
            JsonArray tyypit = elem.get("ryhmatyypit").getAsJsonArray();
            JsonArray kayttoRyhmat = elem.get("kayttoryhmat").getAsJsonArray();

            groupListRestDTO.addGroup(
                    new OrganizationGroupRestDTO(oid, nimi, toStringList(tyypit), toStringList(kayttoRyhmat)));
        }

        return groupListRestDTO;
    }



    @Override
    public JsonElement serialize(OrganizationGroupListRestDTO src, Type typeOfSrc, JsonSerializationContext context) {
        return JsonNull.INSTANCE;
    }

    private List<String> toStringList(JsonArray arr) {
        List<String> list = new ArrayList<String>(arr.size());
        for (int i = 0; i < arr.size(); i++) {
            JsonElement elem = arr.get(i);
            if (elem != null && !elem.isJsonNull() && elem.isJsonPrimitive()) {
                list.add(elem.getAsString());
            }
        }
        return list;
    }

    private I18nText buildNimi(JsonObject nimi) {

        Map<String, String> translations = new HashMap<String, String>(3);
        for (String lang : new String[] { "fi", "sv", "en"}) {
            JsonElement trans = nimi.get(lang);
            if (trans != null && !trans.isJsonNull()) {
                translations.put(lang, trans.getAsString());
            }
        }
        return new I18nText(translations);
    }
}
