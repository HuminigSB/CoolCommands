package com.mc.commands.cool.handlers;

import com.mc.commands.cool.models.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class PlayerRespawnHandler {
     public static void onPlayerRespawn(ServerPlayer serverPlayer, boolean venceuOJogo, Entity.RemovalReason reason) {
        Player player = ServerTickHandler.PARTICIPANTES.get(serverPlayer.getUUID());

        if (player != null) {
            player.reloadEffect(serverPlayer);
        }
    }
}
