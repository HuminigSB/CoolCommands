package com.mc.commands.cool.neoforge;

import net.neoforged.fml.common.Mod;

import com.mc.commands.cool.CoolCommandsMod;

@Mod(CoolCommandsMod.MOD_ID)
public final class CoolCommandsModNeoForge {
    public CoolCommandsModNeoForge() {
        // Run our common setup.
        CoolCommandsMod.init();
    }
}
