package com.mc.commands.cool.SavedData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.Vec3i;

import java.util.Map;
import java.util.UUID;

import com.mc.commands.cool.events.JailEvent;

public class JailEventSavedData extends SavedData {
    private static final String FILE_NAME = "cool_commands_jailevent";

    private JailEventSavedData() {}

    public static JailEventSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<JailEventSavedData>(
                    () -> new JailEventSavedData(), 
                    JailEventSavedData::load, 
                    DataFixTypes.LEVEL
                ),
                FILE_NAME
        );
    }

    public static JailEventSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        JailEventSavedData data = new JailEventSavedData();
        
        JailEvent.isActive = tag.getBoolean("isActive");
        JailEvent.useJail = tag.getBoolean("useJail");
        
        if (tag.contains("sizeX")) {
            JailEvent.islandSize = new Vec3i(tag.getInt("sizeX"), tag.getInt("sizeY"), tag.getInt("sizeZ"));
        }

        JailEvent.POSICOES_ANTIGAS.clear();
        if (tag.contains("posicoesAntigas", Tag.TAG_LIST)) {
            ListTag list = tag.getList("posicoesAntigas", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag pTag = list.getCompound(i);
                UUID uuid = pTag.getUUID("uuid");
                
                BlockPos rPos = pTag.contains("rPosX") ? new BlockPos(pTag.getInt("rPosX"), pTag.getInt("rPosY"), pTag.getInt("rPosZ")) : null;
                ResourceKey<Level> rDim = pTag.contains("rDim") ? ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(pTag.getString("rDim"))) : null;

                JailEvent.PlayerLocationBackup backup = new JailEvent.PlayerLocationBackup(
                    pTag.getDouble("x"), pTag.getDouble("y"), pTag.getDouble("z"),
                    pTag.getFloat("yRot"), pTag.getFloat("xRot"),
                    ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(pTag.getString("dim"))),
                    rPos, rDim, pTag.getBoolean("rForced")
                );
                JailEvent.POSICOES_ANTIGAS.put(uuid, backup);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("isActive", JailEvent.isActive);
        tag.putBoolean("useJail", JailEvent.useJail);
        
        if (JailEvent.islandSize != null) {
            tag.putInt("sizeX", JailEvent.islandSize.getX());
            tag.putInt("sizeY", JailEvent.islandSize.getY());
            tag.putInt("sizeZ", JailEvent.islandSize.getZ());
        }

        ListTag list = new ListTag();
        for (Map.Entry<UUID, JailEvent.PlayerLocationBackup> entry : JailEvent.POSICOES_ANTIGAS.entrySet()) {
            CompoundTag pTag = new CompoundTag();
            pTag.putUUID("uuid", entry.getKey());
            
            JailEvent.PlayerLocationBackup b = entry.getValue();
            pTag.putDouble("x", b.x());
            pTag.putDouble("y", b.y());
            pTag.putDouble("z", b.z());
            pTag.putFloat("yRot", b.yRot());
            pTag.putFloat("xRot", b.xRot());
            pTag.putString("dim", b.dimension().location().toString());
            
            if (b.respawnPos() != null) {
                pTag.putInt("rPosX", b.respawnPos().getX());
                pTag.putInt("rPosY", b.respawnPos().getY());
                pTag.putInt("rPosZ", b.respawnPos().getZ());
            }
            if (b.respawnDim() != null) {
                pTag.putString("rDim", b.respawnDim().location().toString());
            }
            pTag.putBoolean("rForced", b.respawnForced());
            list.add(pTag);
        }
        tag.put("posigasAntigas", list);
        return tag;
    }
}
