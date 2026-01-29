package com.AnonDex.DebugHud.DebugHud;

import com.AnonDex.DebugHud.DebugHudPlugin;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jspecify.annotations.NonNull;

public class DebugHud extends CustomUIHud {

    //Logger instance
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    //Player Variables
    private PlayerRef playerRef;

    // Constructor
    public DebugHud(@NonNull PlayerRef playerRef) {
        super(playerRef);
        this.playerRef = playerRef;
        LOGGER.atInfo().log("DebugHud initialized for player: %s", playerRef.getComponent(Player.getComponentType()).getDisplayName());
    }

    // Overridden Methods
    @Override
    protected void build(@NonNull UICommandBuilder uiCommandBuilder) {
        //LOGGER.atInfo().log("Building Debug HUD UI");
        uiCommandBuilder.append("Pages/Hud.ui");
        showDebugInfo(uiCommandBuilder);
    }

    public void showDebugInfo(@NonNull UICommandBuilder commandBuilder) {

        if(playerRef == null) {
            LOGGER.atSevere().log("PlayerRef is null in showDebugInfo");
            return;
        }
        Transform currentPosition = playerRef.getTransform();

        //LOGGER.atInfo().log(String.format("X: %.2f Y: %.2f Z: %.2f",
        //        currentPosition.getPosition().getX(), currentPosition.getPosition().getY(), currentPosition.getPosition().getZ()));
        commandBuilder.set("#PositionVal.Text", String.format("X: %.2f, Y: %.2f, Z: %.2f",
                currentPosition.getPosition().getX(), currentPosition.getPosition().getY(), currentPosition.getPosition().getZ()));
    }
}
