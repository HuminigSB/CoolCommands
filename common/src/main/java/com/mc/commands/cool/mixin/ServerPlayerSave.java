package com.mc.commands.cool.mixin;

import com.mc.commands.cool.models.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mc.commands.cool.handlers.ServerTickHandler;

@Mixin(ServerPlayer.class)
public class ServerPlayerSave {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onSaveData(CompoundTag tag, CallbackInfo ci) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
        Player player = ServerTickHandler.PARTICIPANTES.get(serverPlayer.getUUID());
        if (player != null) {
            tag.put("CoolCommandsMod", player.saveEffectsToNBT());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onLoadData(CompoundTag tag, CallbackInfo ci) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
        
        Player player = ServerTickHandler.PARTICIPANTES.computeIfAbsent(
           serverPlayer.getUUID(), 
            uuid -> new Player(uuid)
        );

        if (tag.contains("CoolCommandsMod")) {
            player.loadEffectsFromNBT(tag.getList("CoolCommandsMod", 10));
            player.reloadEffect(serverPlayer);
        }
    }
}
