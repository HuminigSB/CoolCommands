package com.mc.commands.cool.models;

import com.mc.commands.cool.models.effects.Effect;
import com.mc.commands.cool.models.effects.Resize;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.ListTag;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Player {
    private final UUID uuid;
    private List<Effect> activeEffects = new ArrayList<>();

    public Player(UUID uuid) {
        this.uuid = uuid;
    }
    
    public void addEffect(Effect effect){
        this.activeEffects.add(effect);
    }

    public void removeEffect(Effect effect){
        this.activeEffects.remove(effect);
    }

    public List<Effect> getActiveEffects() {
        return this.activeEffects;
    }
    @SuppressWarnings("unchecked")
    public <T extends Effect> T getActiveEffect(Class<T> effectClass) {
        for (Effect effect : this.activeEffects) {
            if (effectClass.isInstance(effect)) {
                return (T) effect;
            }
        }
        return null;
    }

    public void tickStep(MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayer(this.uuid);

        if (player == null) return;

        Iterator<Effect> iterator = this.activeEffects.iterator();        
        while (iterator.hasNext()) {
            Effect effect = iterator.next();
            effect.tickStep(player);
            if (effect.getTicks() <= 0) {
                iterator.remove();
            }
        }
    }

    public void reloadEffect(ServerPlayer player) {
        for (Effect effect : this.activeEffects) {
            effect.reloadEffect(player);
        }
    }

    public ListTag saveEffectsToNBT() {
        ListTag listTag = new ListTag();
        for (Effect effect : this.activeEffects) {
            if (effect.getTicks() > 0) {
                listTag.add(effect.saveToNBT());
            }
        }
        return listTag;
    }
    public void loadEffectsFromNBT(net.minecraft.nbt.ListTag listTag) {
        this.activeEffects.clear();         
        for (int i = 0; i < listTag.size(); i++) {
            net.minecraft.nbt.CompoundTag effectTag = listTag.getCompound(i);
            String type = effectTag.getString("EffectType");
            Effect effect = null;
            if (type.equals("Resize")) {
                effect = new Resize();
            }
            if (effect != null) {
                effect.loadFromNBT(effectTag);
                this.activeEffects.add(effect);
            }
        }
    }
}
