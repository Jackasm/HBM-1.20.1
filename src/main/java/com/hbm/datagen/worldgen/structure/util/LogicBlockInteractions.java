package com.hbm.datagen.worldgen.structure.util;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.blocks.generic.LogicBlock;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;

import com.hbm.tileentity.TileEntityLockableBase;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

/** Interactions are called when the player right-clicks the block */
public class LogicBlockInteractions {

    /**
     * Consumer consists of:
     * [0] Level instance,
     * [1] TileEntityLogicBlock instance,
     * [2] int x,
     * [3] int y,
     * [4] int z,
     * [5] Player instance,
     * [6] int block side,
     * [7] float hitX,
     * [8] float hitY,
     * [9] float hitZ
     */
    public static LinkedHashMap<String, Consumer<Object[]>> interactions;

    public static Consumer<Object[]> TEST = (array) -> {
        LogicBlock.TileEntityLogicBlock logic = (LogicBlock.TileEntityLogicBlock) array[1];
        Player player = (Player) array[5];

        if (logic.phase > 1) return;

        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty()) held.shrink(1);

        logic.phase++;
    };

    public static Consumer<Object[]> RAD_CONTAINMENT_SYSTEM = (array) -> {
        LogicBlock.TileEntityLogicBlock logic = (LogicBlock.TileEntityLogicBlock) array[1];
        Player player = (Player) array[5];

        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && held.getItem() == ModItems.KEY.get()) {
            held.shrink(1);
            player.displayClientMessage(Component.literal(
                    ChatFormatting.LIGHT_PURPLE + "[RAD CONTAINMENT SYSTEM]" +
                            ChatFormatting.RESET + " Radiation treatment administered"), false);
            player.addEffect(new MobEffectInstance(HbmPotion.RADAWAY.get(), 3 * 60 * 20, 4));
            player.addEffect(new MobEffectInstance(HbmPotion.RADX.get(), 3 * 60 * 20, 4));
            logic.phase = 2;
            logic.timer = 0;
        }
    };

    public static Consumer<Object[]> POWER_LOCK = (array) -> {
        Level world = (Level) array[0];
        Player player = (Player) array[5];
        int x = (int) array[2];
        int y = (int) array[3];
        int z = (int) array[4];

        IEnergyHandler handler = null;

        for (Direction dir : Direction.values()) {
            BlockPos p = new BlockPos(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ());
            if (world.getBlockEntity(p) instanceof IEnergyHandler h) {
                handler = h;
                break;
            }
        }

        if (handler == null || !(handler.getPower() > 500_000)) {
            player.displayClientMessage(Component.literal(
                    ChatFormatting.LIGHT_PURPLE + "[POWER LOCK]" +
                            ChatFormatting.RESET + " Charge adjacent energy storage to at least 500KHE to release emergency lock"), false);
        } else {
            player.displayClientMessage(Component.literal(
                    ChatFormatting.LIGHT_PURPLE + "[POWER LOCK]" +
                            ChatFormatting.RESET + " Power Restored! Safe Unlocked!"), false);

            TileEntityLockableBase safe = null;
            for (Direction dir : Direction.values()) {
                BlockPos p = new BlockPos(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ());
                if (world.getBlockEntity(p) instanceof TileEntityLockableBase lockable) {
                    safe = lockable;
                    break;
                }
            }
            if (safe != null) {
                safe.unlock();
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvent.createVariableRangeEvent(
                                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "block.lockopen")),
                        SoundSource.PLAYERS, 3.0F, 0.8F);
            }
        }
    };

    public static List<String> getInteractionNames() {
        return new ArrayList<>(interactions.keySet());
    }

    static {
        initialize();
    }

    public static void initialize() {
        interactions = new LinkedHashMap<>();

        interactions.put("POWER_LOCK", POWER_LOCK);

        interactions.put("TEST", TEST);
        interactions.put("RADAWAY_INJECTOR", RAD_CONTAINMENT_SYSTEM);
    }
}