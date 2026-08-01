package com.mc.commands.cool.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mc.commands.cool.SavedData.JailEventSavedData;
import com.mc.commands.cool.models.Heads;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class JailEventManager {
    public static void iniciarJail(MinecraftServer server, int quantiaMobs, EntityType<?> tipoMob) {
        if (JailEvent.isActive) return;
        JailEvent.reset();
        JailEvent.isActive = true;

        ServerLevel overworld = server.overworld();
        StructureTemplateManager templateManager = overworld.getStructureManager();
        BlockPos posStart = new BlockPos(-32, 168, -32); 
        BlockPos posEnd = new BlockPos(32, 232, 32);
        BlockPos spawnJail = new BlockPos(0, 170, 0);

        List<ResourceLocation> jails = new ArrayList<>();
        int idJailCheck = 1;

        while (true) {
            ResourceLocation jailId = ResourceLocation.fromNamespaceAndPath("minecraft", "jail_" + idJailCheck);
            Optional<StructureTemplate> checK = templateManager.get(jailId);
            if (checK.isPresent()) {
                jails.add(jailId);
                idJailCheck++;
            } else {
                break;
         
            }
        }
        if (!jails.isEmpty()) {
            int randomIndex = overworld.random.nextInt(jails.size());
            ResourceLocation estruturaSorteada = jails.get(randomIndex);
            StructureTemplate template = templateManager.get(estruturaSorteada).get();
            JailEvent.islandSize = template.getSize();
            JailEvent.useJail = true;

            List<StructureTemplate.StructureBlockInfo> spawnBlocks = template.filterBlocks(
                posStart, 
                new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE), 
                Blocks.RESPAWN_ANCHOR
            );
            if (!spawnBlocks.isEmpty()) {
                spawnJail = spawnBlocks.get(0).pos().above();
            }

            StructurePlaceSettings configuracoes = new StructurePlaceSettings()
                    .setMirror(Mirror.NONE)
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(true);

            template.placeInWorld(overworld, posStart, posStart, configuracoes, overworld.random, 2);
            
        } else {
            for (BlockPos pos : BlockPos.betweenClosed(posStart, posEnd)) {
                boolean isBorder = pos.getX() == -32 || pos.getX() == 32 ||
                                pos.getY() == 168 || pos.getY() == 232 ||
                                pos.getZ() == -32 || pos.getZ() == 32;
                if (isBorder) {
                    overworld.setBlockAndUpdate(pos, Blocks.BARRIER.defaultBlockState());
                } else {
                    overworld.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
            JailEvent.islandSize = new Vec3i(64, 64, 64);
            JailEvent.useJail = false;
        }

        if (spawnJail == null) {
            spawnJail = new BlockPos(0, 170, 0); 
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            JailEvent.POSICOES_ANTIGAS.put(player.getUUID(), new JailEvent.PlayerLocationBackup(
                player.getX(), player.getY(), player.getZ(), 
                player.getYRot(), player.getXRot(), 
                player.level().dimension(),
                player.getRespawnPosition(),
                player.getRespawnDimension(),
                player.isRespawnForced()
            ));
            player.setRespawnPosition(overworld.dimension(), spawnJail, 0.0F, true, false);
            player.teleportTo(overworld, spawnJail.getX() + 0.5D, spawnJail.getY(), spawnJail.getZ() + 0.5D, 0.0F, 0.0F);
            player.sendSystemMessage(Component.literal("§c§lJAIL! §7Sobreviva e mate todos os alvos para escapar!"));
        }

        for (int i = 0; i < quantiaMobs; i++) {
            Entity entidade = tipoMob.create(overworld);
            if (entidade instanceof Mob mob) {
                //spawna nos 10 blocos em volta do spawn do player
                double spawnX = spawnJail.getX() + (overworld.random.nextDouble() * 20) - 10;
                double spawnZ = spawnJail.getZ() + (overworld.random.nextDouble() * 20) - 10;
                mob.moveTo(spawnX, spawnJail.getY(), spawnZ, 0.0F, 0.0F);
                
                mob.setCustomName(Component.literal("§4§l[JAIL TARGET]"));
                mob.setCustomNameVisible(true);//mostra o nome
                mob.setPersistenceRequired(); 

                mob.finalizeSpawn(
                    overworld,
                    overworld.getCurrentDifficultyAt(mob.blockPosition()),
                    MobSpawnType.EVENT,
                    null
                );
                
                ItemStack head = Heads.getInstance().getRandomHead(overworld);//pega cabeça aleatoria de player online
                mob.setItemSlot(EquipmentSlot.HEAD, head);
                mob.setDropChance(EquipmentSlot.HEAD, 0.085F);

                overworld.addFreshEntity(mob);
                JailEvent.MOBS_ALVOS.put(mob.getUUID(), true); 
            }
        }
        try {
            JailEventSavedData.get(overworld).setDirty();
        } catch (Exception e) {
            server.sendSystemMessage(Component.literal("§c[CoolCommands] Erro ao salvar dados do evento!"));
            e.printStackTrace();
        }
    }

    public static void verificarFimDoEvento(MinecraftServer server) {
        if (!JailEvent.isActive) return;
        if (JailEvent.MOBS_ALVOS.values().stream().anyMatch(vivo -> vivo)) return;
        ServerLevel overworld = server.overworld();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            JailEvent.PlayerLocationBackup backup = JailEvent.POSICOES_ANTIGAS.get(player.getUUID());
            if (backup != null) {
                ServerLevel dimTarget = server.getLevel(backup.dimension());
                ServerLevel destino = dimTarget != null ? dimTarget : overworld;
                player.setRespawnPosition(backup.respawnDim(), backup.respawnPos(), 0.0F, backup.respawnForced(), false);
                player.teleportTo(destino, backup.x(), backup.y(), backup.z(), backup.yRot(), backup.xRot());
                player.sendSystemMessage(Component.literal("§a§lLIBERADO! §7Todos os alvos foram eliminados!"));
            }
        }

        BlockPos posStart = new BlockPos(-32, 168, -32);
        BlockPos posEnd = posStart.offset(JailEvent.islandSize);
        for (BlockPos pos : BlockPos.betweenClosed(posStart, posEnd)) {
            overworld.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        JailEvent.reset();
        JailEventSavedData.get(overworld).setDirty();
    }
}
