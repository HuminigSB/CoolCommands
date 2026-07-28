package com.mc.commands.cool.handlers;

import com.mc.commands.cool.models.Player;
import net.minecraft.server.level.ServerPlayer;

public class PlayerConnectionHandler {
    public static void onPlayerJoin(ServerPlayer serverPlayer) {
        Player player = new Player(serverPlayer.getUUID());
        ServerTickHandler.PARTICIPANTES.put(serverPlayer.getUUID(), player);
    }

    public static void onPlayerQuit(ServerPlayer player) {
        ServerTickHandler.PARTICIPANTES.remove(player.getUUID());
    }
}
