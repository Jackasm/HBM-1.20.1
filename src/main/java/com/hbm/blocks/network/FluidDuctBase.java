package com.hbm.blocks.network;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.HbmKeybinds;
import com.hbm.interfaces.IAnalyzable;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.items.fluid.ItemFluidID;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.uninos.UniNodespace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class FluidDuctBase extends BaseEntityBlock implements IBlockFluidDuct, IAnalyzable {

    public FluidDuctBase(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPipeBaseNT(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && heldItem.getItem() instanceof IItemFluidIdentifier identifier) {
            FluidTypeHBM type = identifier.getType(level, pos, heldItem);

            if (!HbmPlayerProps.getData(player).getKeyPressed(HbmKeybinds.EnumKeybind.TOOL_CTRL) && !player.isShiftKeyDown()) {

                BlockEntity te = level.getBlockEntity(pos);

                if (te instanceof TileEntityPipeBaseNT pipe) {

                    if (HbmPlayerProps.getData(player).getKeyPressed(HbmKeybinds.EnumKeybind.TOOL_ALT)) {
                        if (heldItem.getItem() instanceof ItemFluidID) {
                            if (identifier.getType(level, pos, heldItem) != pipe.getFluidType()) {
                                ItemFluidID.setFluidType(heldItem, pipe.getFluidType());
                                level.playSound(player, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.25F, 0.75F);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }

                    if (pipe.getFluidType() != type) {
                        pipe.setFluidType(type);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {

                BlockEntity te = level.getBlockEntity(pos);

                if (te instanceof TileEntityPipeBaseNT pipe) {

                    if (HbmPlayerProps.getData(player).getKeyPressed(HbmKeybinds.EnumKeybind.TOOL_ALT)) {
                        if (heldItem.getItem() instanceof ItemFluidID) {
                            if (identifier.getType(level, pos, heldItem) != pipe.getFluidType()) {
                                ItemFluidID.setFluidType(heldItem, pipe.getFluidType());
                                level.playSound(player, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.25F, 0.75F);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }

                    changeTypeRecursively(level, pos, pipe.getFluidType(), type, 64);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void changeTypeRecursively(Level level, BlockPos pos, FluidTypeHBM prevType, FluidTypeHBM type, int loopsRemaining) {

        BlockEntity te = level.getBlockEntity(pos);

        if (te instanceof TileEntityPipeBaseNT pipe) {

            if (pipe.getFluidType() == prevType && pipe.getFluidType() != type) {
                pipe.setFluidType(type);

                if (loopsRemaining > 0) {
                    for (Direction dir : Direction.values()) {
                        Block b = level.getBlockState(pos).getBlock();

                        if (b instanceof IBlockFluidDuct duct) {
                            duct.changeTypeRecursively(level, pos.relative(dir), prevType, type, loopsRemaining - 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<Component> getDebugInfo(Level level, BlockPos pos) {

        BlockEntity te = level.getBlockEntity(pos);

        if (te instanceof TileEntityPipeBaseNT pipe) {
            FluidTypeHBM type = pipe.getFluidType();

            if (type != null) {

                com.hbm.api.fluid.FluidNode node = (com.hbm.api.fluid.FluidNode) UniNodespace.getNode(level, pos, type.getNetworkProvider());

                if (node != null && node.net != null) {
                    com.hbm.api.fluid.FluidNet net = node.net;

                    List<Component> debug = new ArrayList<>();
                    debug.add(Component.literal("Links: " + net.getLinks().size()));
                    debug.add(Component.literal("Subscribers: " + net.getReceivers().size()));
                    debug.add(Component.literal("Providers: " + net.getProviders().size()));
                    debug.add(Component.literal("Transfer: " + net.fluidTracker));
                    return debug;
                }
            }
        }

        return null;
    }
}