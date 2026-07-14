package com.hbm.commands;


import com.hbm.extprop.HbmLivingProps;
import com.hbm.util.RefStrings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DebugCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal(RefStrings.MODID)
                .then(Commands.literal("blacklung")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {
                                            int level = IntegerArgumentType.getInteger(ctx, "level");
                                            for (ServerPlayer player : EntityArgument.getPlayers(ctx, "targets")) {
                                                HbmLivingProps.setBlackLung(player, level);
                                                player.sendSystemMessage(Component.literal("Black Lung set to " + level));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("asbestos")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {
                                            int level = IntegerArgumentType.getInteger(ctx, "level");
                                            for (ServerPlayer player : EntityArgument.getPlayers(ctx, "targets")) {
                                                HbmLivingProps.setAsbestos(player, level);
                                                player.sendSystemMessage(Component.literal("Asbestos set to " + level));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )



        );
    }
}