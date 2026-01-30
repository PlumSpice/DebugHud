package com.AnonDex.DebugHud.Mapping;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.packets.connection.PongType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

public class DebugHud extends CustomUIHud {

    //Logger instance
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    //Player Variables
    private PlayerRef playerRef;
    private Transform lastTransform;
    private long lastSampleTime;

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

        //Check Player Ref
        if(playerRef == null) {
            LOGGER.atSevere().log("PlayerRef is null in showDebugInfo");
            return;
        }

        //Get Position
        Transform currentPosition = playerRef.getTransform();

        //Get Ping
        long pingMs = getPlayerPingMs(playerRef);
        long pingP99 = getPingP99Ms(playerRef.getPacketHandler(), 0);

        //Get World
        UUID worldUuid = playerRef.getWorldUuid();
        World world = Universe.get().getWorld(worldUuid);
        String worldName = world.getName();

        //Get Biome
        Player player = playerRef.getComponent(Player.getComponentType());
        WorldMapTracker tracker = player.getWorldMapTracker();
        String biomeName = tracker != null ? tracker.getCurrentBiomeName() : "Unknown";

        //Get Game Time
        WorldTimeResource time = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
        int hour = time.getCurrentHour();
        float dayProgress = time.getDayProgress();
        LocalDateTime dateTime = time.getGameDateTime();
        int moonPhase = time.getMoonPhase();

        //Get IRL Time
        LocalDateTime serverTime = LocalDateTime.now();
        DateTimeFormatter baseFormatter = DateTimeFormatter.ofPattern("d MMM yyyy, hh:mm a");
        String formatted = serverTime.format(baseFormatter);

        //Get Player Speed & Movement State
        double speed = getPlayerSpeed(playerRef);
        String movementState = getPlayerMovementState(playerRef);


        //Set UI Values
        commandBuilder.set("#PositionVal.Text", String.format("X: %.2f, Y: %.2f, Z: %.2f",
                currentPosition.getPosition().getX(), currentPosition.getPosition().getY(), currentPosition.getPosition().getZ()));
        commandBuilder.set("#PingVal.Text", String.format("%d ms", pingMs));
        commandBuilder.set("#PingP99Val.Text", String.format("%d ms", pingP99));
        commandBuilder.set("#WorldVal.Text", String.format("%s", worldName));
        commandBuilder.set("#BiomeVal.Text", String.format("%s", biomeName));
        commandBuilder.set("#TimeVal.Text", String.format("Hour: %02d (%.2f%%), Date: %s, Moon Phase: %d",
                hour, dayProgress * 100.0f, dateTime.toLocalDate().toString(), moonPhase));
        commandBuilder.set("#IRLTimeVal.Text", String.format("%s", formatted));
        commandBuilder.set("#SpeedVal.Text", String.format("%.2f units/s", speed));
        commandBuilder.set("#MovementStateVal.Text", String.format("%s", movementState));
    }

    public static long getPlayerPingMs(PlayerRef playerRef) {
        PacketHandler handler = playerRef.getPacketHandler();

        PacketHandler.PingInfo pingInfo =
                handler.getPingInfo(PongType.Tick);

        HistoricMetric metric = pingInfo.getPingMetricSet();

        // 1-second rolling average
        double avgMicros = metric.getAverage(0);

        return Math.round(avgMicros / 1000.0);
    }

    public static long getPingP99Ms(PacketHandler handler, int periodIndex) {
        PacketHandler.PingInfo pingInfo =
                handler.getPingInfo(PongType.Tick);

        HistoricMetric metric = pingInfo.getPingMetricSet();
        long[] valuesMicros = metric.getValues(periodIndex);

        if (valuesMicros.length == 0) {
            return -1;
        }

        Arrays.sort(valuesMicros);

        int index = (int) Math.ceil(0.99 * valuesMicros.length) - 1;
        index = Math.max(0, Math.min(index, valuesMicros.length - 1));

        return Math.round(valuesMicros[index] / 1000.0);
    }

    public double getPlayerSpeed(PlayerRef playerRef) {
        Transform current = playerRef.getTransform();
        long now = System.nanoTime();

        if (lastTransform == null) {
            lastTransform = current.clone(); // 🔑 CRITICAL
            lastSampleTime = now;
            return 0.0;
        }

        Vector3d p0 = lastTransform.getPosition();
        Vector3d p1 = current.getPosition();

        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        double dz = p1.getZ() - p0.getZ();

        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
        double dt = (now - lastSampleTime) / 1_000_000_000.0;

        lastTransform.assign(current); // reuse object safely
        lastSampleTime = now;

        return dt > 0 ? distance / dt : 0.0;
    }

    public static String getPlayerMovementState(PlayerRef playerRef) {

        MovementStatesComponent comp = playerRef.getComponent(MovementStatesComponent.getComponentType());
        if (comp == null) {
            return "Unknown";
        }

        MovementStates states = comp.getMovementStates();
        if (states == null) {
            return "Unknown";
        }

        // -------- Base movement --------
        String movement;
        if (states.sprinting) {
            movement = "Sprinting";
        } else if (states.running) {
            movement = "Running";
        } else if (states.walking) {
            movement = "Walking";
        } else if (states.idle || states.horizontalIdle) {
            movement = "Idle";
        } else {
            movement = "Moving";
        }

        // -------- Modifiers --------
        if (states.crouching || states.forcedCrouching) {
            movement += " (Crouched)";
        }
        if (!states.onGround) {
            movement += " (Air)";
        }
        if (states.inFluid) {
            movement += " (Fluid)";
        }
        if (states.swimming) {
            movement += " (Swimming)";
        }
        if (states.climbing) {
            movement += " (Climbing)";
        }
        if (states.gliding) {
            movement += " (Gliding)";
        }
        if (states.flying) {
            movement += " (Flying)";
        }
        return movement;
    }
}
