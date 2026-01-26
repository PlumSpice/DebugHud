package com.AnonDex.DebugHud.DebugHud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class HudCommand extends AbstractAsyncPlayerCommand {
    public HudCommand() {
        super("debug", "Opens the debug HUD for the player.");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
    }

    @Override
    protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext,
                                                            @NonNull Store<EntityStore> store,
                                                            @NonNull Ref<EntityStore> ref,
                                                            @NonNull PlayerRef playerRef,
                                                            @NonNull World world)
    {
        // Get Player Component
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) {
            return CompletableFuture.completedFuture(null);
        }

        // Get Debug Hud and open it
        if (player.getHudManager().getCustomHud() == null)
        {
            player.getHudManager().setCustomHud(playerRef, new DebugHud(playerRef));
            playerRef.sendMessage(Message.raw("Enabled Debug HUD."));
        }
        else
        {
            player.getHudManager().resetHud(playerRef);
            playerRef.sendMessage(Message.raw("Disabled Debug HUD."));
        }
        return CompletableFuture.completedFuture(null);
    }
}
