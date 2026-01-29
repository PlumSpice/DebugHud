package com.AnonDex.DebugHud.DebugHud;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DebugHudSystem extends DelayedEntitySystem<EntityStore> {

    private final ConcurrentHashMap<UUID, DebugHud> debugHuds = new ConcurrentHashMap<>();
    private DebugHudSystem instance;

    //Logger instance
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    //Constructors
    public DebugHudSystem(float intervalSec)
    {
        super(intervalSec);
        this.instance = this;
        LOGGER.atInfo().log("DebugHudSystem initialized with interval: %f seconds", intervalSec);
    }

    //Getter for instance
    public DebugHudSystem getInstance()
    {
        return this.instance;
    }

    @Override
    public void tick(float v, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {

        PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        if(playerRef == null || player == null) {
            return;
        }
        if(HudCommand.hudStatus.getOrDefault(playerRef.getUuid(), false)) {
            DebugHud debugHud = debugHuds.computeIfAbsent(playerRef.getUuid(), k -> new DebugHud(playerRef));
            debugHud.show();
        }
        else {
            debugHuds.remove(playerRef.getUuid());
        }

    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        Query<EntityStore> playerQuery = Query.and(
                Player.getComponentType(), PlayerRef.getComponentType(), TransformComponent.getComponentType()
        );
        return playerQuery;
    }
}
