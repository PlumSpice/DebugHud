package com.AnonDex.DebugHud.DebugHud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jspecify.annotations.NonNull;

public class DebugHud extends CustomUIHud {

    public DebugHud(@NonNull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@NonNull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Pages/Hud.ui");
    }
}
