package com.mc.commands.cool.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mc.commands.cool.events.JailEventManager;
import com.mc.commands.cool.utils.PresetsLoader;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;

public class JailCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
            Commands.literal("jail")
                .requires(source -> source.hasPermission(2)) // Apenas Admins
                .then(Commands.argument("quantia", IntegerArgumentType.integer(1, 100)) // Limite de 1 a 100 mobs
                    .then(Commands.argument("mob", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                        .executes(JailCommand::executeJail)
                    )
                )
        );

        PresetsLoader loader = PresetsLoader.getInstance();

        dispatcher.register(
            Commands.literal("jailPreset")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("mobPreset", IntegerArgumentType.integer(-1, loader.loadPresetMobs().size() - 1))
                    .executes(ctx -> executePresetJail(ctx, false))
                    .then(Commands.argument("mobAmount", IntegerArgumentType.integer(-1, 100))
                        .executes(ctx -> executePresetJail(ctx, true))
                    )
                )
        );
    }

    private static int executeJail(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int quantia = IntegerArgumentType.getInteger(context, "quantia");
        Holder.Reference<EntityType<?>> mobHolder = ResourceArgument.getResource(context, "mob", Registries.ENTITY_TYPE);
        EntityType<?> tipoMob = mobHolder.value();
        MinecraftServer server = context.getSource().getServer();
        JailEventManager.startJail(server, quantia, tipoMob);

        return 1;
    }

    private static int executePresetJail(CommandContext<CommandSourceStack> context, boolean hasAmount) throws CommandSyntaxException {
        int mobIndex = IntegerArgumentType.getInteger(context, "mobPreset");
        int amount = hasAmount ? IntegerArgumentType.getInteger(context, "mobAmount") : -1;
        MinecraftServer server = context.getSource().getServer();
        JailEventManager.startPresetJail(server, mobIndex, amount);

        return 1;
    }
}
