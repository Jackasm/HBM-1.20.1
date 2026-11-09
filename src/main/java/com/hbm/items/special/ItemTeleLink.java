package com.hbm.items.special;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.machine.TileEntityMachineTeleporter;
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

public class ItemTeleLink extends Item {

    public ItemTeleLink(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (!player.isShiftKeyDown() && !level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);

            if (!(te instanceof TileEntityMachineTeleporter tele)) {
                // Сохраняем координаты назначения
                CompoundTag tag = stack.getOrCreateTag();
                tag.putInt("x", pos.getX());
                tag.putInt("y", pos.getY());
                tag.putInt("z", pos.getZ());
                tag.putInt("dim", level.dimension().location().hashCode());

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.TECH_BLEEP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(
                        Component.translatable("hbm.text.telelink_set_teleporter_exit_to", pos.getX(), pos.getY(), pos.getZ())
                                .withStyle(style -> style.withColor(0x00FFFF)),
                        false
                );
                player.swing(context.getHand());
                return InteractionResult.SUCCESS;

            } else {
                // Применяем сохраненные координаты к телепорту
                if (!stack.hasTag()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.TECH_BOOP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.displayClientMessage(
                            Component.translatable("hbm.text.telelink_no_destination_set")
                                    .withStyle(style -> style.withColor(0xFF0000)),
                            false
                    );
                    return InteractionResult.FAIL;
                }

                CompoundTag tag = stack.getTag();
                int x1 = Objects.requireNonNull(tag).getInt("x");
                int y1 = tag.getInt("y");
                int z1 = tag.getInt("z");
                int dim = tag.getInt("dim");

                tele.targetX = x1;
                tele.targetY = y1;
                tele.targetZ = z1;
                tele.targetDim = dim;
                tele.setChanged();

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.TECH_BLEEP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(
                        Component.translatable("hbm.text.telelink_teleporters_destination_has_been_set")
                                .withStyle(style -> style.withColor(0x00FFFF)),
                        false
                );
                player.swing(context.getHand());
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            tooltip.add(Component.translatable("hbm.text.x_2", Objects.requireNonNull(tag).getInt("x")));
            tooltip.add(Component.translatable("hbm.text.y", tag.getInt("y")));
            tooltip.add(Component.translatable("hbm.text.z", tag.getInt("z")));
            tooltip.add(Component.translatable("hbm.text.d", tag.getInt("dim")));
        } else {
            tooltip.add(Component.translatable("hbm.text.select_exit_location_first")
                    .withStyle(style -> style.withColor(0xFF0000)));
        }
    }
}