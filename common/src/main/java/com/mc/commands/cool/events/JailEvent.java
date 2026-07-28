package com.mc.commands.cool.events;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class JailEvent {
    public static boolean isActive = false;
    public static final Map<UUID, Boolean> MOBS_ALVOS = new ConcurrentHashMap<>();
    public static final Map<UUID, PlayerLocationBackup> POSICOES_ANTIGAS = new ConcurrentHashMap<>();
    public static net.minecraft.core.Vec3i islandSize;
    public static boolean useJail = false;
    public static final java.util.Set<net.minecraft.core.BlockPos> playerBlocks = java.util.concurrent.ConcurrentHashMap.newKeySet();


    public record PlayerLocationBackup(
        double x, double y, double z, 
        float yRot, float xRot, ResourceKey<Level> dimension,
        BlockPos respawnPos,           
        ResourceKey<Level> respawnDim, 
        boolean respawnForced  
    ) {}

    public static void reset() {
        isActive = false;
        MOBS_ALVOS.clear();
        POSICOES_ANTIGAS.clear();
        islandSize = null;
        useJail = false;
        playerBlocks.clear();
    }


}
