package io.github.aglushkovsky.util;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static <T> String getJsonFromList(List<T> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    public static <T> String getJsonFromObject(T entity) {
        Gson gson = new Gson();
        return gson.toJson(entity);
    }

    static String getJsonErrorMessage(String message) {
        Gson gson = new Gson();
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("message", message);
        return gson.toJson(jsonMap);
    }

    private JsonUtils() {
    }
}
