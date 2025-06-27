package ru.webapp.util;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class SectionJsonAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private static final Logger log = LoggerFactory.getLogger(SectionJsonAdapter.class);
    private static final String CLASSNAME = "CLASNAME";
    private static final String INSTANCE = "INSTANCE";

    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        log.debug("Deserializing Section from JSON: {}", json);
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();
        try {
            Class clazz = Class.forName(className);
            T result = context.deserialize(jsonObject.get(INSTANCE), clazz);
            log.debug("Deserialized Section into class: {}", className);
            return result;
        } catch (ClassNotFoundException e) {
            log.error("Class not found during deserialization: {}", className, e);
            throw new JsonParseException(e.getMessage());
        }
    }

    @Override
    public JsonElement serialize(T section, Type typeOfSrc, JsonSerializationContext context) {
        log.debug("Serializing Section of type: {}", section.getClass().getName());
        JsonObject retValue = new JsonObject();
        retValue.addProperty(CLASSNAME, section.getClass().getName());
        JsonElement jsonElement = context.serialize(section);
        retValue.add(INSTANCE, jsonElement);
        log.debug("Serialized Section: {}", retValue);
        return retValue;
    }
}
