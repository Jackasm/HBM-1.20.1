package com.hbm_addons.commands;

import com.hbm.util.RefStrings;
import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.entity.ai.EncounterGoal;
import com.hbm_addons.handler.EncounterGroup;
import com.hbm_addons.handler.EncounterSpawnHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ветка /hbm encounter ... — отладочные команды аддона.
 * Brigadier сольёт литерал "hbm" с тем, что регистрирует ядро.
 */
@Mod.EventBusSubscriber
public final class AddonCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal(RefStrings.MODID)
                .then(Commands.literal("encounter")
                        .requires(src -> src.hasPermission(2))
                        // /hbm encounter — случайно всё, для игрока-источника
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                    player.level(), player, null, null, null);
                            return reportResult(ctx.getSource(), group);
                        })
                        // /hbm encounter <goal>
                        .then(Commands.argument("goal", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (EncounterGoal g : EncounterGoal.values()) {
                                        builder.suggest(g.name().toLowerCase());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    EncounterGoal goal = parseGoal(StringArgumentType.getString(ctx, "goal"));
                                    if (goal == null) {
                                        ctx.getSource().sendFailure(Component.literal(
                                                "Unknown goal. Try: " + goalList()));
                                        return 0;
                                    }
                                    EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                            player.level(), player, goal, null, null);
                                    return reportResult(ctx.getSource(), group);
                                })
                                // /hbm encounter <goal> <count>
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 32))
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            EncounterGoal goal = parseGoal(StringArgumentType.getString(ctx, "goal"));
                                            if (goal == null) {
                                                ctx.getSource().sendFailure(Component.literal(
                                                        "Unknown goal. Try: " + goalList()));
                                                return 0;
                                            }
                                            int count = IntegerArgumentType.getInteger(ctx, "count");
                                            EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                                    player.level(), player, goal, null, count);
                                            return reportResult(ctx.getSource(), group);
                                        })
                                )
                        )
                        // /hbm encounter friendly <goal> <count>
                        .then(Commands.literal("friendly")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                            player.level(), player, null, false, null);
                                    return reportResult(ctx.getSource(), group);
                                })
                                .then(Commands.argument("goal", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (EncounterGoal g : EncounterGoal.values()) {
                                                builder.suggest(g.name().toLowerCase());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            EncounterGoal goal = parseGoal(StringArgumentType.getString(ctx, "goal"));
                                            if (goal == null) {
                                                ctx.getSource().sendFailure(Component.literal(
                                                        "Unknown goal. Try: " + goalList()));
                                                return 0;
                                            }
                                            EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                                    player.level(), player, goal, false, null);
                                            return reportResult(ctx.getSource(), group);
                                        })
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 32))
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    EncounterGoal goal = parseGoal(StringArgumentType.getString(ctx, "goal"));
                                                    if (goal == null) {
                                                        ctx.getSource().sendFailure(Component.literal(
                                                                "Unknown goal. Try: " + goalList()));
                                                        return 0;
                                                    }
                                                    int count = IntegerArgumentType.getInteger(ctx, "count");
                                                    EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                                            player.level(), player, goal, false, count);
                                                    return reportResult(ctx.getSource(), group);
                                                })
                                        )
                                )
                        )
                        // /hbm encounter hostile <goal> <count>
                        .then(Commands.literal("hostile")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                            player.level(), player, null, true, null);
                                    return reportResult(ctx.getSource(), group);
                                })
                                .then(Commands.argument("goal", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (EncounterGoal g : EncounterGoal.values()) {
                                                builder.suggest(g.name().toLowerCase());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            EncounterGoal goal = parseGoal(StringArgumentType.getString(ctx, "goal"));
                                            if (goal == null) {
                                                ctx.getSource().sendFailure(Component.literal(
                                                        "Unknown goal. Try: " + goalList()));
                                                return 0;
                                            }
                                            EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                                    player.level(), player, goal, true, null);
                                            return reportResult(ctx.getSource(), group);
                                        })
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 32))
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    EncounterGoal goal = parseGoal(StringArgumentType.getString(ctx, "goal"));
                                                    if (goal == null) {
                                                        ctx.getSource().sendFailure(Component.literal(
                                                                "Unknown goal. Try: " + goalList()));
                                                        return 0;
                                                    }
                                                    int count = IntegerArgumentType.getInteger(ctx, "count");
                                                    EncounterGroup group = EncounterSpawnHandler.forceSpawn(
                                                            player.level(), player, goal, true, count);
                                                    return reportResult(ctx.getSource(), group);
                                                })
                                        )
                                )
                        )
                )
        );
    }

    private static EncounterGoal parseGoal(String name) {
        if (name == null || name.isEmpty() || name.equalsIgnoreCase("random")) return null;
        try {
            return EncounterGoal.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String goalList() {
        StringBuilder sb = new StringBuilder();
        for (EncounterGoal g : EncounterGoal.values()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(g.name().toLowerCase());
        }
        return sb.toString();
    }

    private static int reportResult(CommandSourceStack source, EncounterGroup group) {
        if (group == null || group.members.isEmpty()) {
            source.sendFailure(Component.literal(
                    "[Encounter] Failed to spawn: no valid LOS position or blocked"));
            return 0;
        }
        String msg = "[Encounter] Spawned: goal=" + group.goal
                + " faction=" + (group.hostile ? "HOSTILE" : "FRIENDLY")
                + " count=" + group.members.size()
                + " at " + String.format("%.1f,%.1f,%.1f",
                        group.spawnPos.x, group.spawnPos.y, group.spawnPos.z);
        source.sendSuccess(() -> Component.literal(msg), true);
        System.out.println("HBM_ENCOUNTER: " + msg);
        return 1;
    }

    private AddonCommands() {
    }
}
