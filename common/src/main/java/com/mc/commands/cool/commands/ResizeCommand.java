package com.mc.commands.cool.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mc.commands.cool.handlers.ServerTickHandler;
import com.mc.commands.cool.models.Player;
import com.mc.commands.cool.models.effects.Resize; 
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ResizeCommand {
     public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("resize")
                .requires(source -> source.hasPermission(2)) // Exige nível de OP 2 (Admin)
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("scale", DoubleArgumentType.doubleArg(0.1, 10.0)) // Escala entre 0.1 e 10
                        .then(Commands.argument("duration", IntegerArgumentType.integer(1)) // Duração mínima de 1 segundo
                            .executes(ResizeCommand::executeSizeCommand)
                        )
                    )
                )
        );
    }

    private static int executeSizeCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        double scale = DoubleArgumentType.getDouble(context, "scale");
        int duration = IntegerArgumentType.getInteger(context, "duration");

        Player player = ServerTickHandler.PARTICIPANTES.computeIfAbsent(
            targetPlayer.getUUID(), 
            uuid -> new Player(uuid)
        );

        Resize existingEffect = player.getActiveEffect(Resize.class);
        if (existingEffect != null && existingEffect.getNewSize() == scale) {
            existingEffect.addTime(targetPlayer, duration);
        } else {
            if(existingEffect != null){
                existingEffect.forceRemove(targetPlayer);
                player.removeEffect(existingEffect);
            }
            Resize resizeEffect = new Resize();
            resizeEffect.setNewSize(scale);
            player.addEffect(resizeEffect);
            resizeEffect.addTime(targetPlayer, duration);
        }

        context.getSource().sendSuccess(() -> Component.literal(
            "§aEscala de " + targetPlayer.getScoreboardName() + " alterada para " + scale + " por " + duration + "segundos!"
        ), true);

        return 1; // Retorno padrão de sucesso para o Brigadier
    }
}
