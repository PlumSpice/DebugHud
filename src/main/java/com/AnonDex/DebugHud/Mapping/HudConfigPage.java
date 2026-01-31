package com.AnonDex.DebugHud.Mapping;

import com.AnonDex.DebugHud.Config.HudConfigData;
import com.AnonDex.DebugHud.Config.HudConfigStore;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jspecify.annotations.NonNull;

import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

public class HudConfigPage extends InteractiveCustomUIPage<HudConfigPage.HudConfigEventData> {

    public HudConfigPage(@NonNull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, HudConfigEventData.CODEC);
    }

    public static class HudConfigEventData {
        // Define event data fields here
        public String action;
        public Boolean showCoordinates;
        public Boolean showPing;
        public Boolean showWorld;
        public Boolean showGameTime;
        public Boolean showIRLTime;
        public Boolean showSpeed;

        public String hudBackground;
        public String hudTransparency;
        public String hudPosition;


        public static final BuilderCodec<HudConfigEventData> CODEC = BuilderCodec
                .builder(HudConfigEventData.class, HudConfigEventData::new)
                .append(new KeyedCodec<>("Action", Codec.STRING),
                        (obj, value) -> obj.action = value,
                        obj -> obj.action
                )
                .add()
                .append(new KeyedCodec<>("@showCoordinates", Codec.BOOLEAN),
                        (obj, value) -> obj.showCoordinates = value,
                        obj -> obj.showCoordinates
                )
                .add()
                .append(new KeyedCodec<>("@showPing", Codec.BOOLEAN),
                        (obj, value) -> obj.showPing = value,
                        obj -> obj.showPing
                )
                .add()
                .append(new KeyedCodec<>("@showWorld", Codec.BOOLEAN),
                        (obj, value) -> obj.showWorld = value,
                        obj -> obj.showWorld
                )
                .add()
                .append(new KeyedCodec<>("@showGameTime", Codec.BOOLEAN),
                        (obj, value) -> obj.showGameTime = value,
                        obj -> obj.showGameTime
                )
                .add()
                .append(new KeyedCodec<>("@showIRLTime", Codec.BOOLEAN),
                        (obj, value) -> obj.showIRLTime = value,
                        obj -> obj.showIRLTime
                )
                .add()
                .append(new KeyedCodec<>("@showSpeed", Codec.BOOLEAN),
                        (obj, value) -> obj.showSpeed = value,
                        obj -> obj.showSpeed
                )
                .add()
                .append(new KeyedCodec<>("@hudBackground", Codec.STRING),
                        (obj, value) -> obj.hudBackground = (String) value,
                        obj -> obj.hudBackground
                )
                .add()
                .append(new KeyedCodec<>("@hudTransparency", Codec.INTEGER),
                        (obj, value) -> obj.hudTransparency = String.format("%02X",
                                Math.round(255 * (value / 100.0f))),
                        obj -> null
                )
                .add()
                .append(new KeyedCodec<>("@hudPosition", Codec.STRING),
                        (obj, value) -> obj.hudPosition = (String) value,
                        obj -> obj.hudPosition
                )
                .add()
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
        uiCommandBuilder.append("Pages/HudConfig.ui");
        HudConfigData data = HudConfigStore.get(playerRef.getUuid());

        uiCommandBuilder.set("#showCoordinates #CheckBox.Value", data.showCoordinates);
        uiCommandBuilder.set("#showPing #CheckBox.Value", data.showPing);
        uiCommandBuilder.set("#showWorld #CheckBox.Value", data.showWorld);
        uiCommandBuilder.set("#showGameTime #CheckBox.Value", data.showGameTime);
        uiCommandBuilder.set("#showIRLTime #CheckBox.Value", data.showIRLTime);
        uiCommandBuilder.set("#showSpeed #CheckBox.Value", data.showSpeed);
        uiCommandBuilder.set("#hudTransparency.Value", transparencyPercentFromHexSafe(data.hudTransparency));
        uiCommandBuilder.set("#hudBackground.Value", data.hudBackground);
        uiCommandBuilder.set("#hudPosition.Value", data.hudPosition);

        //Save button event
        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SaveButton",
                new EventData()
                        .append("Action", "Save")
                        .append("@showCoordinates", "#showCoordinates #CheckBox.Value")
                        .append("@showPing", "#showPing #CheckBox.Value")
                        .append("@showWorld", "#showWorld #CheckBox.Value")
                        .append("@showGameTime", "#showGameTime #CheckBox.Value")
                        .append("@showIRLTime", "#showIRLTime #CheckBox.Value")
                        .append("@showSpeed", "#showSpeed #CheckBox.Value")
                        .append("@hudTransparency", "#hudTransparency.Value")
                        .append("@hudBackground", "#hudBackground.Value")
                        .append("@hudPosition", "#hudPosition.Value")
        );

        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CancelButton",
                new EventData().append("Action", "Cancel")
        );

    }

    @Override
    public void handleDataEvent(@NonNull Ref<EntityStore> ref,
                                @NonNull Store<EntityStore> store,
                                @NonNull HudConfigEventData data) {

        Player player = store.getComponent(ref, Player.getComponentType());

        if (data.action.equals("Save")) {
            HudConfigData configData = HudConfigStore.get(playerRef.getUuid());
            configData.showCoordinates = data.showCoordinates != null ? data.showCoordinates : configData.showCoordinates;
            configData.showPing = data.showPing != null ? data.showPing : configData.showPing;
            configData.showWorld = data.showWorld != null ? data.showWorld : configData.showWorld;
            configData.showGameTime = data.showGameTime != null ? data.showGameTime : configData.showGameTime;
            configData.showIRLTime = data.showIRLTime != null ? data.showIRLTime : configData.showIRLTime;
            configData.showSpeed = data.showSpeed != null ? data.showSpeed : configData.showSpeed;
            configData.hudBackground = data.hudBackground != null ? data.hudBackground : configData.hudBackground;
            configData.hudTransparency = data.hudTransparency != null ? data.hudTransparency : configData.hudTransparency;
            configData.hudPosition = data.hudPosition != null ? data.hudPosition : configData.hudPosition;
            HudConfigStore.update(playerRef.getUuid(), configData);

            // Close the page
            player.getPageManager().setPage(ref, store, Page.None);
        }
        else {
            // Close the page
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }

    public static String transparencyHex(String val)
    {
        int alphaDecimal = Math.round(255 * (Integer.parseInt(val) / 100.0f));
        String alphaHex = String.format("%02X", alphaDecimal);
        return alphaHex;
    }

    public static int transparencyPercentFromHexSafe(String hex) {
        try {
            int alpha = Integer.parseInt(hex, 16);
            return Math.round((alpha / 255.0f) * 100.0f);
        } catch (Exception e) {
            return 0;
        }
    }
}
