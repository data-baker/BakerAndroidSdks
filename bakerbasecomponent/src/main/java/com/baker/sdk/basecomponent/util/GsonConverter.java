package com.baker.sdk.basecomponent.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hsj55
 * 2020/9/18
 */
public class GsonConverter {
    public static <T> T fromJson(String json, Class<T> type) throws JsonIOException, JsonSyntaxException {
        return GsonHolder.GSON.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return GsonHolder.GSON.fromJson(json, type);
    }

    public static <T> List<T> fromJsonList(String json, Class<T> type) {
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        List<T> list = new ArrayList<>(array.size());
        for (final JsonElement elem : array) {
            list.add(GsonHolder.GSON.fromJson(elem, type));
        }
        return list;
    }

    public static <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return GsonHolder.GSON.fromJson(reader, typeOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        return GsonHolder.GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return GsonHolder.GSON.fromJson(json, typeOfT);
    }

    public static String toJson(Object src) {
        return GsonHolder.GSON.toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return GsonHolder.GSON.toJson(src, typeOfSrc);
    }

    private static class GsonHolder {
        private final static Gson GSON = new Gson();
    }
}
