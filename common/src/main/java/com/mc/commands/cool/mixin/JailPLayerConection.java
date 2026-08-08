package com.mc.commands.cool.mixin;

import com.mc.commands.cool.events.JailEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class JailPLayerConection {
    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerDisconnect(ServerPlayer player, CallbackInfo ci) {
        if (JailEvent.isActive) {
            JailEvent.PlayerLocationBackup backup = JailEvent.POSICOES_ANTIGAS.remove(player.getUUID());
            if (backup != null) {
                ServerLevel dimTarget = player.server.getLevel(backup.dimension());
                ServerLevel destino = dimTarget != null ? dimTarget : player.server.overworld();
                
                player.setRespawnPosition(backup.respawnDim(), backup.respawnPos(), 0.0F, backup.respawnForced(), false);
                player.teleportTo(destino, backup.x(), backup.y(), backup.z(), backup.yRot(), backup.xRot());
            }
        }
    }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void onPlayerConnect(Connection connection, ServerPlayer player, int i, CallbackInfo ci) {
        if (JailEvent.isActive) {
            if (!JailEvent.POSICOES_ANTIGAS.containsKey(player.getUUID())) {
                ServerLevel overworld = player.server.overworld();
                
                JailEvent.POSICOES_ANTIGAS.put(player.getUUID(), new JailEvent.PlayerLocationBackup(
                    player.getX(), player.getY(), player.getZ(), 
                    player.getYRot(), player.getXRot(), 
                    player.level().dimension(),
                    player.getRespawnPosition(),
                    player.getRespawnDimension(),
                    player.isRespawnForced()
                ));
                
                BlockPos spawnJail = new BlockPos(0, 170, 0); // Ajuste se quiser buscar a âncora dinamicamente
                player.setRespawnPosition(overworld.dimension(), spawnJail, 0.0F, true, false);
                player.teleportTo(overworld, 0.0, 170.0, 0.0, 0.0F, 0.0F);
                player.sendSystemMessage(Component.literal("§c§lJAIL! §7Você retornou ao evento pendente!"));
            }
        }
    }
}
