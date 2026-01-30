package com.AnonDex.DebugHud.Command;

import com.AnonDex.DebugHud.Mapping.DebugHud;
import com.AnonDex.DebugHud.System.DebugHudSystem;
import com.AnonDex.DebugHud.DebugHudPlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HudCommand extends AbstractAsyncPlayerCommand {

    // Logger instance
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static ConcurrentHashMap<UUID,Boolean> hudStatus = new ConcurrentHashMap<>();

    // Default HUD Components to restore
    public static final Set<HudComponent> DEFAULT_HUD_COMPONENTS = Set.of(HudComponent.UtilitySlotSelector,
            HudComponent.BlockVariantSelector, HudComponent.StatusIcons, HudComponent.Hotbar, HudComponent.Chat,
            HudComponent.Notifications, HudComponent.KillFeed, HudComponent.InputBindings, HudComponent.Reticle,
            HudComponent.Compass, HudComponent.Speedometer, HudComponent.ObjectivePanel, HudComponent.PortalPanel,
            HudComponent.EventTitle, HudComponent.Stamina, HudComponent.AmmoIndicator, HudComponent.Health, HudComponent.Mana,
            HudComponent.Oxygen, HudComponent.BuilderToolsLegend, HudComponent.Sleep);

    // DebugHudSystem instance

    // Constructor
    public HudCommand() {
        super("hud", "Opens the debug HUD for the player.");

        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
    }

    //Overridden Methods
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
        UUID playerUUID = playerRef.getUuid();
        Boolean isHudEnabled = hudStatus.putIfAbsent(playerUUID, false);
        LOGGER.atInfo().log("Toggling HUD for player: %s, Current Status: %s", player.getDisplayName(), isHudEnabled);

        // Get Debug Hud and open it
        if (!(player.getHudManager().getCustomHud() instanceof DebugHud))
        {
            LOGGER.atInfo().log("Enabling Debug HUD");
            hudStatus.put(playerUUID, true);
            player.getHudManager().setCustomHud(playerRef, new DebugHud(playerRef));
            DebugHudPlugin.setDebugHudSystem(new DebugHudSystem(0.1f));
            playerRef.sendMessage(Message.raw("Enabled Debug HUD."));
        }
        else
        {
            LOGGER.atInfo().log("Disabling Debug HUD");
            hudStatus.put(playerUUID, false);
            player.getHudManager().setCustomHud(playerRef, new CustomUIHud(playerRef) {
                @Override
                protected void build(@NonNull UICommandBuilder uiCommandBuilder) {
                    // No custom UI, just use default HUD components
                }
            });
            playerRef.sendMessage(Message.raw("Disabled Debug HUD."));
        }
        return CompletableFuture.completedFuture(null);
    }
}
