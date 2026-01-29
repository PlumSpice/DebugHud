package com.AnonDex.DebugHud;

import com.AnonDex.DebugHud.DebugHud.DebugHudSystem;
import com.AnonDex.DebugHud.DebugHud.HudCommand;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class DebugHudPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public DebugHudPlugin(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {

        this.getCommandRegistry().registerCommand(new HudCommand());

    }

    public static void setDebugHudSystem(DebugHudSystem debugHudSystem)
    {
        ComponentRegistryProxy<EntityStore> registry = Universe.get().getEntityStoreRegistry();
        try {
            registry.registerSystem(debugHudSystem);
        }
        catch (Exception e)
        {
            LOGGER.atSevere().log("Failed to register DebugHudSystem: %s", e.getMessage());
        }
    }
}
