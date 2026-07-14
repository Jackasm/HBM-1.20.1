package com.hbm.items;

import com.hbm.sound.ModSounds;
import com.hbm.util.ItemStackUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/**
 * Base class for items containing an inventory. This can be seen in crates, containment boxes, and the toolbox.
 */
public abstract class ItemInventory implements Container {

    public Player player;
    public java.util.List<ItemStack> slots;
    public ItemStack target;

    @Override
    public void setChanged() {
        if (slots == null) return;

        for (int i = 0; i < getContainerSize(); ++i) {
            if (getItem(i) != null && getItem(i).isEmpty()) {
                slots.set(i, ItemStack.EMPTY);
            }
        }

        ItemStackUtil.addStacksToNBT(target, slots.toArray(new ItemStack[0]));
        target.setTag(checkNBT(target.getTag()));
    }

    public CompoundTag checkNBT(CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty())
            return null;

        Random random = new Random();
        Level level = player.level();

        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            NbtIo.writeCompressed(nbt, byteOutput);
            byte[] abyte = byteOutput.toByteArray();

            if (abyte.length > 6000) {
                player.displayClientMessage(Component.literal("Warning: Container NBT exceeds 6kB, contents will be ejected!").withStyle(style -> style.withColor(0xFF0000)), false);

                for (int i1 = 0; i1 < this.getContainerSize(); ++i1) {
                    ItemStack itemstack = this.getItem(i1);

                    if (itemstack != null && !itemstack.isEmpty()) {
                        float f = random.nextFloat() * 0.8F + 0.1F;
                        float f1 = random.nextFloat() * 0.8F + 0.1F;
                        float f2 = random.nextFloat() * 0.8F + 0.1F;

                        while (itemstack.getCount() > 0) {
                            int j1 = random.nextInt(21) + 10;

                            if (j1 > itemstack.getCount()) {
                                j1 = itemstack.getCount();
                            }

                            itemstack.shrink(j1);
                            ItemStack dropStack = new ItemStack(itemstack.getItem(), j1);
                            dropStack.setDamageValue(itemstack.getDamageValue());
                            if (itemstack.hasTag()) {
                                dropStack.setTag(Objects.requireNonNull(itemstack.getTag()).copy());
                            }

                            ItemEntity entityitem = new ItemEntity(level, player.getX() + f, player.getY() + f1, player.getZ() + f2, dropStack);

                            float f3 = 0.05F;
                            entityitem.setDeltaMovement(
                                    (float) random.nextGaussian() * f3 + player.getDeltaMovement().x,
                                    (float) random.nextGaussian() * f3 + 0.2F + player.getDeltaMovement().y,
                                    (float) random.nextGaussian() * f3 + player.getDeltaMovement().z
                            );
                            level.addFreshEntity(entityitem);
                        }
                    }
                }

                return null; // Reset.
            }
        } catch (IOException ignored) {}

        return nbt;
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) {
            if (stack.getCount() > amount) {
                stack = stack.split(amount);
                setChanged();
            } else {
                setItem(slot, ItemStack.EMPTY);
            }
        }
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.setCount(Math.min(stack.getCount(), this.getMaxStackSize()));
        }

        slots.set(slot, stack);
        setChanged();
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        if (slots == null || slot >= slots.size()) return ItemStack.EMPTY;
        return slots.get(slot);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void startOpen(@NotNull Player player) {
        Level level = player.level();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.CRATE_OPEN.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
    }

    @Override
    public void stopOpen(@NotNull Player player) {
        Level level = player.level();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.CRATE_CLOSE.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
    }

    @Override
    public boolean isEmpty() {
        if (slots == null) return true;
        for (ItemStack stack : slots) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void clearContent() {
        if (slots != null) {
            slots.clear();
        }
    }
}