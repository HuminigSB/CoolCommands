package com.mc.commands.cool.handlers;

import com.mc.commands.cool.models.Player;
import net.minecraft.server.MinecraftServer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class ServerTickHandler {
    public static final Map<UUID, Player> PARTICIPANTES = new ConcurrentHashMap<>();

    public static void onServerTick(MinecraftServer server) {
        if (PARTICIPANTES.isEmpty()) return;

        for (Player player : PARTICIPANTES.values()) {
            player.tickStep(server);
        }
    }
}