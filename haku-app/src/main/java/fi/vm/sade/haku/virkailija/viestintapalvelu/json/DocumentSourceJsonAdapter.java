package fi.vm.sade.haku.virkailija.viestintapalvelu.json;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import fi.vm.sade.haku.virkailija.viestintapalvelu.dto.DocumentSourceDTO;

public class DocumentSourceJsonAdapter implements JsonSerializer<DocumentSourceDTO>, JsonDeserializer<DocumentSourceDTO> {

	@Override
	public DocumentSourceDTO deserialize(JsonElement json, Type type, JsonDeserializationContext context) 
		throws JsonParseException {
		DocumentSourceDTO documentSource = new DocumentSourceDTO();
		
		JsonObject documentSourceJson = json.getAsJsonObject();
		documentSource.setDocumentName(getJsonString(documentSourceJson, "documentName"));
		documentSource.setSources(getArrayList(documentSourceJson, "sources"));
		
		return documentSource;
	}

	@Override
	public JsonElement serialize(DocumentSourceDTO documentSource, Type type, JsonSerializationContext context) {
		JsonObject documentSourceJson = new JsonObject();

		JsonArray sourcesJsonArray = new JsonArray(); 
		JsonElement sourcesJsonElement = new JsonPrimitive(documentSource.getSources().get(0));
		sourcesJsonArray.add(sourcesJsonElement);
		
		documentSourceJson.add("documentName", new JsonPrimitive(documentSource.getDocumentName()));
		documentSourceJson.add("sources", sourcesJsonArray);
		
		return documentSourceJson;
	}
	
    private String getJsonString(JsonObject documentSourceJson, String field) {
        JsonElement elem = documentSourceJson.get(field);
        
        String value = null;
        if (elem != null && !elem.isJsonNull()) {
            value = elem.getAsString();
        }
        
        return value;
    }
    
    @SuppressWarnings("unchecked")
	private ArrayList<String> getArrayList(JsonObject documentSourceJson, String field) {
    	Gson gson = new Gson();
    	JsonArray jsonArray = documentSourceJson.getAsJsonArray(field);
    	return gson.fromJson(jsonArray, ArrayList.class);
    }
}
