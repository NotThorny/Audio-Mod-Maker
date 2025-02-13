package dev.thorny.utils;

import java.io.File;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class FileSerializer implements JsonSerializer<File> {
    @Override
    public JsonElement serialize(File file, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(file.getPath());
    }
}