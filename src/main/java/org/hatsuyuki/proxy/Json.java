package org.hatsuyuki.proxy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * Created by Hatsuyuki.
 * Wrapper for Jackson library to make it behave like play.lib.json
 */
class Json {
    private static ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


    public static ArrayNode newArray() {
        return mapper.createArrayNode();
    }

    public static ObjectNode newObject() {
        return mapper.createObjectNode();
    }

    public static JsonNode parse(String jsonStr) throws IOException {
        return mapper.readTree(jsonStr);
    }

    public static <T> T parse(String jsonString, Class<T> clazz) {
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            return "";
        }
    }
}
