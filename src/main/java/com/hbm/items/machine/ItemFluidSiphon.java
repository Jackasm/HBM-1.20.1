package com.hbm.items.machine;

import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.inventory.FluidContainer;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Unsiphonable;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemPipette;
import com.hbm.util.CompatExternal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class ItemFluidSiphon extends Item {

    public ItemFluidSiphon(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        BlockEntity te = CompatExternal.getCoreFromPos(level, pos);

        if (te instanceof IFluidStandardReceiver receiver) {
            FluidTankHBM[] tanks = receiver.getReceivingTanks();

            boolean hasDrainedTank = false;

            for (FluidTankHBM tank : tanks) {
                if (tank.getFill() <= 0) continue;

                ItemStack availablePipette = null;
                FluidTypeHBM tankType = tank.getTankType();

                if (tankType.hasTrait(FT_Unsiphonable.class)) continue;

                Inventory inventory = player.getInventory();

                for (int j = 0; j < inventory.getContainerSize(); j++) {
                    ItemStack inventoryStack = inventory.getItem(j);
                    if (inventoryStack.isEmpty()) continue;

                    FluidContainer container = FluidContainerRegistry.getContainer(tankType, inventoryStack);

                    if (availablePipette == null && inventoryStack.getItem() instanceof ItemPipette pipette) {
                        if (!pipette.willFizzle(tankType) && pipette != ModItems.PIPETTE_LABORATORY.get()) {
                            availablePipette = inventoryStack;
                        }
                    }

                    if (container == null) continue;

                    ItemStack full = FluidContainerRegistry.getFullContainer(inventoryStack, tankType);

                    while (tank.getFill() >= container.content && inventoryStack.getCount() > 0) {
                        hasDrainedTank = true;

                        inventoryStack.shrink(1);
                        if (inventoryStack.isEmpty()) {
                            inventory.setItem(j, ItemStack.EMPTY);
                        }

                        ItemStack filledContainer = full.copy();
                        tank.setFill(tank.getFill() - container.content);
                        ItemHandlerHelper.giveItemToPlayer(player, filledContainer);
                    }
                }

                if (availablePipette != null && tank.getFill() < 1000) {
                    ItemPipette pipette = (ItemPipette) availablePipette.getItem();

                    if (pipette.acceptsFluid(tankType, availablePipette)) {
                        hasDrainedTank = true;
                        int remaining = pipette.tryFill(tankType, tank.getFill(), availablePipette);
                        tank.setFill(remaining);
                    }
                }

                if (hasDrainedTank) {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}