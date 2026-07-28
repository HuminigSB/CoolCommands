package com.mc.commands.cool.handlers;

import com.mc.commands.cool.events.JailEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import dev.architectury.event.EventResult;
import dev.architectury.utils.value.IntValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;

public class JailProtectionHandler {
     public static EventResult onBlockPlace(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (JailEvent.isActive && isInsideJailArea(pos)) {
            JailEvent.playerBlocks.add(pos.immutable());
        }
        return EventResult.pass();
    }

    public static EventResult onBlockBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player, IntValue intValue) {
        if (JailEvent.isActive && isInsideJailArea(pos) && !JailEvent.playerBlocks.contains(pos)) {
            return EventResult.interruptFalse(); 
        }
        if(JailEvent.playerBlocks.contains(pos)){
            JailEvent.playerBlocks.remove(pos);
        }
        return EventResult.pass();
    }

    public static EventResult onExplosionDetonate(net.minecraft.world.level.Level level, net.minecraft.world.level.Explosion explosion) {
        if (JailEvent.isActive) {
            explosion.getToBlow().removeIf(pos -> !JailEvent.playerBlocks.contains(pos));
            for (BlockPos pos : explosion.getToBlow()) {
                if(JailEvent.playerBlocks.contains(pos)){
                    JailEvent.playerBlocks.remove(pos);
                }
            }
        }
        return EventResult.pass();
    }

    private static boolean isInsideJailArea(BlockPos pos) {
        if (JailEvent.islandSize == null) return false;

        int minX = -32;
        int maxX = -32 + JailEvent.islandSize.getX();
        int minY = 168;
        int maxY = 168 + JailEvent.islandSize.getY();
        int minZ = -32;
        int maxZ = -32 + JailEvent.islandSize.getZ();

        return pos.getX() >= minX && pos.getX() <= maxX &&
               pos.getY() >= minY && pos.getY() <= maxY &&
               pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }
}
