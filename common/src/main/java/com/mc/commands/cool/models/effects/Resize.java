package com.mc.commands.cool.models.effects;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerPlayer;

public class Resize extends Effect{
    private double newSize = 1.0;
    
    @Override
    protected void addEffect(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.SCALE);
        if(attribute != null){
            attribute.setBaseValue(this.newSize);
        }
    }
    @Override
    protected void removeEffect(ServerPlayer player) {
         AttributeInstance attribute = player.getAttribute(Attributes.SCALE);
        if (attribute != null) {
            attribute.setBaseValue(1.0);
        }
    }
    public void setNewSize(double newSize) {
        this.newSize = newSize;
    }
    public double getNewSize(){
        return this.newSize;
    }
}
