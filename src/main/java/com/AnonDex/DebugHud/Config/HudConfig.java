package com.AnonDex.DebugHud.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HudConfig {

    // Map of player UUIDs to their HUD configuration data
    private static ConcurrentHashMap<UUID, HudConfigData> hudConfigs = new ConcurrentHashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Path.of("config/debughud.json");

    public HudConfig() {

    }

}
