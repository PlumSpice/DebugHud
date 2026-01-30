package com.AnonDex.DebugHud.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HudConfigStore {

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final Type MAP_TYPE =
            new TypeToken<Map<UUID, HudConfigData>>() {}.getType();

    private static final Path CONFIG_PATH =
            Path.of("debughud", "hud-config.json");

    private static final Map<UUID, HudConfigData> CACHE =
            new ConcurrentHashMap<>();

    private HudConfigStore() {}

    /* -------------------- lifecycle -------------------- */

    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
                save(); // write empty file
                return;
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Map<UUID, HudConfigData> data =
                        GSON.fromJson(reader, MAP_TYPE);

                if (data != null) {
                    CACHE.putAll(data);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load HudConfig", e);
        }
    }

    public static synchronized void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(CACHE, MAP_TYPE, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save HudConfig", e);
        }
    }

    /* -------------------- API -------------------- */

    public static HudConfigData get(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, u -> new HudConfigData());
    }

    public static void update(UUID uuid, HudConfigData data) {
        CACHE.put(uuid, data);
        save(); // persist immediately
    }
}
