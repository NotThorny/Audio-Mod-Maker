package dev.thorny.utils;

import java.io.File;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class FileDeserializer implements JsonDeserializer<File> {
    @Override
    public File deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return new File(json.getAsString());
    }
    
}
