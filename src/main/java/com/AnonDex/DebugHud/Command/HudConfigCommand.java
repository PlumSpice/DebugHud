package com.AnonDex.DebugHud.Command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class HudConfigCommand extends AbstractAsyncPlayerCommand {

    public HudConfigCommand() {
        super("hudconfig", "Configures the debug HUD settings for the player.");
    }

    @Override
    protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext,
                                                            @NonNull Store<EntityStore> store,
                                                            @NonNull Ref<EntityStore> ref,
                                                            @NonNull PlayerRef playerRef,
                                                            @NonNull World world)
    {

        return CompletableFuture.completedFuture(null);
    }
}
