package com.mc.commands.cool.models.effects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;

public abstract class Effect {
    private int ticks = 0;

    protected abstract void addEffect(ServerPlayer player);
    protected abstract void removeEffect(ServerPlayer player);

    public void addTime(ServerPlayer player, int seconds){
        this.ticks += seconds * 20;
        this.addEffect(player);
    }

    public void tickStep(ServerPlayer player){
        if(this.ticks>0){
            this.ticks--;
            if(this.ticks == 0){
                this.removeEffect(player);
            }
        }
    }

    public void forceRemove(ServerPlayer player) {
        this.ticks = 0; 
        this.removeEffect(player);
    }

    public int getTicks(){
        return this.ticks;
    } 
    
    public void reloadEffect(ServerPlayer player){
        if(this.ticks>0){
            this.addEffect(player);
        }
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("EffectType", this.getClass().getSimpleName());
        tag.putInt("TicksLeft", this.ticks);
        return tag;
    }
    public void loadFromNBT(CompoundTag tag) {
        if (tag.contains("TicksLeft")) {
            this.ticks = tag.getInt("TicksLeft");
        }
    }
}
