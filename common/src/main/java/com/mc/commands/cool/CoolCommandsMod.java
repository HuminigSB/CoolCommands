package com.mc.commands.cool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mc.commands.cool.handlers.JailProtectionHandler;
import com.mc.commands.cool.handlers.PlayerConnectionHandler;
import com.mc.commands.cool.handlers.PlayerRespawnHandler;
import com.mc.commands.cool.handlers.ServerTickHandler;
import com.mc.commands.cool.commands.JailCommand;
import com.mc.commands.cool.commands.ResizeCommand;
import com.mc.commands.cool.events.JailEvent;
import com.mc.commands.cool.events.JailEventManager;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.ExplosionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.MinecraftServer;

public final class CoolCommandsMod {
    public static final String MOD_ID = "cool_commands";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Initializing Cool Commands Mod");

        TickEvent.SERVER_POST.register(ServerTickHandler::onServerTick);
        PlayerEvent.PLAYER_RESPAWN.register(PlayerRespawnHandler::onPlayerRespawn);
        PlayerEvent.PLAYER_JOIN.register(PlayerConnectionHandler::onPlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(PlayerConnectionHandler::onPlayerQuit);

        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            ResizeCommand.register(dispatcher);
            JailCommand.register(dispatcher, registry); 
            BlockEvent.PLACE.register(JailProtectionHandler::onBlockPlace);
            BlockEvent.BREAK.register(JailProtectionHandler::onBlockBreak);
            ExplosionEvent.PRE.register(JailProtectionHandler::onExplosionDetonate);
        });

        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (!JailEvent.isActive) return EventResult.pass();

            if (JailEvent.MOBS_ALVOS.containsKey(entity.getUUID())) {
                JailEvent.MOBS_ALVOS.put(entity.getUUID(), false);
                MinecraftServer server = entity.getServer();
                if (server != null) {
                    JailEventManager.verificarFimDoEvento(server);
                }
            }
            return EventResult.pass();
        });
    }
}
