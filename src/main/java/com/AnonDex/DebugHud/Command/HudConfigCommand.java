package com.AnonDex.DebugHud.Command;

import com.AnonDex.DebugHud.Mapping.HudConfigPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class HudConfigCommand extends AbstractAsyncPlayerCommand {

    public HudConfigCommand() {
        super("hudconfig", "Configures the debug HUD settings for the player.");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
    }

    @Override
    protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext,
                                                            @NonNull Store<EntityStore> store,
                                                            @NonNull Ref<EntityStore> ref,
                                                            @NonNull PlayerRef playerRef,
                                                            @NonNull World world)
    {

        Player player = store.getComponent(ref, Player.getComponentType());

        HudConfigPage hudConfigPage = new HudConfigPage(playerRef);
        player.getPageManager().openCustomPage(ref, store, hudConfigPage);


        return CompletableFuture.completedFuture(null);
    }
}
