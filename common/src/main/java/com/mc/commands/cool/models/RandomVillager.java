package com.mc.commands.cool.models;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import  net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.VillagerProfession;

public class RandomVillager {
    public void spawnRandomVillager(Player player){
        ServerLevel world = (ServerLevel) player.level();
        Villager villager = EntityType.VILLAGER.create(world);

        if (villager != null) {
            villager.moveTo(player.getX(), player.getY(), player.getZ(), 0.0F, 0.0F);
            VillagerData dadosAtuais = villager.getVillagerData();
            villager.setVillagerData(dadosAtuais.setProfession(VillagerProfession.NITWIT));
            
            villager.setCustomName(Component.literal("§a§lMr_Randon Entediado"));
            villager.setCustomNameVisible(true);
            villager.setSilent(true); // Silencia os "Humm" do villager
            villager.setPersistenceRequired();
            
            ItemStack cabecaMrRandon = Heads.getInstance().getRandonHead(world);
            villager.setItemSlot(EquipmentSlot.HEAD, cabecaMrRandon);
            
            world.addFreshEntity(villager);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), 
                            SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1.5F, 1.0F);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }
}
