package com.mc.commands.cool.models;
import java.util.List;
import java.util.Optional;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import java.util.UUID;

public class Heads {

    private static final Heads heads = new Heads();
    private List<ResolvableProfile> skinPool = new java.util.ArrayList<>();

    private Heads() {}
    
    public static Heads getInstance() {
        return heads;
    }

    public ItemStack getRandomHead(ServerLevel overworld){
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        this.updatePool(overworld);
        if (!this.skinPool.isEmpty()) {
            int randoMIndex = overworld.random.nextInt(this.skinPool.size());
            ResolvableProfile profile = this.skinPool.get(randoMIndex);
            head.set(DataComponents.PROFILE, profile);
        }
        return head;
    }
    
    public ItemStack getRandonHead(ServerLevel overworld){
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        UUID uuidMrRandon = UUID.fromString("a91f47c6-6785-413b-ab0b-8a59f5012988"); 
        ResolvableProfile unresolvedProfile = new ResolvableProfile(Optional.of("Mr_Randon"), Optional.of(uuidMrRandon), new PropertyMap());
        try{            
            ResolvableProfile resolvedProfile = unresolvedProfile.resolve().join();
            head.set(DataComponents.PROFILE, resolvedProfile);
            return head;
        } catch (Exception e) {
            head.set(DataComponents.PROFILE, unresolvedProfile);
            return new ItemStack(Items.PLAYER_HEAD);
        }
    }

    private void updatePool(ServerLevel overworld){
        this.skinPool.clear();
        UUID uuidMrRandon = UUID.fromString("a91f47c6-6785-413b-ab0b-8a59f5012988"); 
        ResolvableProfile unresolvedProfile = new ResolvableProfile(Optional.of("Mr_Randon"), Optional.of(uuidMrRandon), new PropertyMap());
        unresolvedProfile.resolve().thenAcceptAsync(perfilResolvido -> {
            this.skinPool.add(perfilResolvido);
        }, overworld.getServer());
        List<ServerPlayer> onlinePlayers = overworld.getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : onlinePlayers) {
            this.skinPool.add(new ResolvableProfile(player.getGameProfile()));
        }
    }
}
