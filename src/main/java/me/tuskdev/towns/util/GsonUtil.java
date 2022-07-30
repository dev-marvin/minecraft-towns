package me.tuskdev.towns.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.tuskdev.towns.enums.Rank;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GsonUtil {

    private static final Type TYPE_TOKEN_MAP = new TypeToken<Map<UUID, Rank>>() {}.getType();
    private static final Type TYPE_TOKEN_LIST = new TypeToken<List<String>>() {}.getType();
    private static final Gson GSON = new Gson();

    public static String toJson(Map<UUID, Rank> map) {
        return GSON.toJson(map, TYPE_TOKEN_MAP);
    }

    public static String toJson(List<String> list) {
        return GSON.toJson(list, TYPE_TOKEN_LIST);
    }

    public static Map<UUID, Rank> fromJsonMap(String json) {
        return GSON.fromJson(json, TYPE_TOKEN_MAP);
    }

    public static List<String> fromJsonList(String json) {
        return GSON.fromJson(json, TYPE_TOKEN_LIST);
    }

}
