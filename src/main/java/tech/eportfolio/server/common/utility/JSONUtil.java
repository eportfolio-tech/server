package tech.eportfolio.server.common.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;

import java.util.HashMap;

public class JSONUtil {
    private JSONUtil() {

    }

    public static BasicDBObject convertJsonNodeToDbObject(JsonNode jsonPayLoad) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        return new BasicDBObject(mapper.convertValue(jsonPayLoad, typeRef));
    }
}
