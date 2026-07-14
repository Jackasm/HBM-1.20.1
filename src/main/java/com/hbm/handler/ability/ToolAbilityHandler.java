package com.hbm.handler.ability;

import com.hbm.items.tool.ItemToolAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;


@Mod.EventBusSubscriber
public class ToolAbilityHandler {

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof ItemToolAbility tool) {

            Level world = player.level();
            BlockPos pos = event.getPos();
            BlockState state = event.getState();

            ItemToolAbility.Configuration config = tool.getConfiguration(stack);
            ToolPreset preset = config.getActivePreset();

            if (preset.harvestAbility == IToolHarvestAbility.SILK && preset.areaAbility == IToolAreaAbility.NONE) {
                event.setCanceled(true);
                preset.harvestAbility.onHarvestBlock(
                        preset.harvestAbilityLevel, world, pos, player, state);
                return;
            }

            if (preset.harvestAbility == IToolHarvestAbility.SMELTER && preset.areaAbility == IToolAreaAbility.NONE) {
                event.setCanceled(true);
                preset.harvestAbility.onHarvestBlock(
                        preset.harvestAbilityLevel, world, pos, player, state);
                return;
            }

            // Area ability
            if (preset.areaAbility != IToolAreaAbility.NONE) {
                event.setCanceled(true);
                preset.areaAbility.onDig(preset.areaAbilityLevel, world, pos, player, tool);
                preset.harvestAbility.onHarvestBlock(
                        preset.harvestAbilityLevel, world, pos, player, state);
            }

            // Harvest abilities
            preset.harvestAbility.preHarvestAll(preset.harvestAbilityLevel, world, player);

            // Отложенный post-harvest
            Objects.requireNonNull(world.getServer()).execute(() -> preset.harvestAbility.postHarvestAll(preset.harvestAbilityLevel, world, player));
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {

    }


}