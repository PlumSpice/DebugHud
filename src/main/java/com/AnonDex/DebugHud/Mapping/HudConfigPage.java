package com.AnonDex.DebugHud.Mapping;

import com.AnonDex.DebugHud.Config.HudConfigData;
import com.AnonDex.DebugHud.Config.HudConfigStore;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class HudConfigPage extends InteractiveCustomUIPage<HudConfigPage.HudConfigEventData> {

    public HudConfigPage(@NonNull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, HudConfigEventData.CODEC);
    }

    public static class HudConfigEventData {
        // Define event data fields here

        public static final BuilderCodec<HudConfigEventData> CODEC = BuilderCodec
                .builder(HudConfigEventData.class, HudConfigEventData::new)
                // Append fields to the codec here
                .build();

        private HudConfigEventData() {
            // Initialize fields if necessary
        }
    }

    @Override
    public void build(@NonNull Ref<EntityStore> ref,
                      @NonNull UICommandBuilder uiCommandBuilder,
                      @NonNull UIEventBuilder uiEventBuilder,
                      @NonNull Store<EntityStore> store)
    {
        uiCommandBuilder.append("Pages/HudConfigPage.ui");

        UUID uuid = playerRef.getUuid();
        HudConfigData data = HudConfigStore.get(uuid);


    }
}
