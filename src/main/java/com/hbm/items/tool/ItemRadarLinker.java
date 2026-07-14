package com.hbm.items.tool;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.IRadarCommandReceiver;
import com.hbm.tileentity.machine.TileEntityMachineRadarScreen;
import com.hbm.util.CompatExternal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemRadarLinker extends Item {

    public ItemRadarLinker(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (canGrabCoordinateHere(level, pos)) {
            if (!level.isClientSide) {
                BlockPos targetPos = getCoordinates(level, pos);

                CompoundTag tag = stack.getOrCreateTag();
                tag.putInt("posX", targetPos.getX());
                tag.putInt("posY", targetPos.getY());
                tag.putInt("posZ", targetPos.getZ());

                onTargetSet(level, targetPos.getX(), targetPos.getY(), targetPos.getZ(), player);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /** Whether this position can be saved or if the position target is valid */
    public boolean canGrabCoordinateHere(Level level, BlockPos pos) {
        BlockEntity te = CompatExternal.getCoreFromPos(level, pos);
        return te instanceof IRadarCommandReceiver || te instanceof TileEntityMachineRadarScreen;
    }

    /** Modified the saved coordinates, for example detecting the core for multiblocks */
    public BlockPos getCoordinates(Level level, BlockPos pos) {
        BlockEntity te = CompatExternal.getCoreFromPos(level, pos);
        return te.getBlockPos();
    }

    /** Extra on successful target set, eg. sounds */
    public void onTargetSet(Level level, int x, int y, int z, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.TECH_BLEEP.get(),
                SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            tooltip.add(Component.literal("X: " + Objects.requireNonNull(tag).getInt("posX")));
            tooltip.add(Component.literal("Y: " + tag.getInt("posY")));
            tooltip.add(Component.literal("Z: " + tag.getInt("posZ")));
        } else {
            tooltip.add(Component.literal("No position set!").withStyle(style -> style.withColor(0xFF0000)));
        }
    }
}