package com.hbm.tileentity;

import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemKey;
import com.hbm.util.ArmorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class TileEntityLockableBase extends TileEntityLoadedBase {

    protected int lock;
    private boolean isLocked = false;
    protected double lockMod = 0.1D;

    public TileEntityLockableBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void lock() {
        if (lock == 0) {
            // Логгирование, если нужно
        }
        isLocked = true;
        setChanged();
    }

    public void setPins(int pins) { lock = pins; setChanged(); }
    public int getPins() { return lock; }
    public void setMod(double mod) { lockMod = mod; setChanged(); }
    public double getMod() { return lockMod; }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        lock = nbt.getInt("lock");
        isLocked = nbt.getBoolean("isLocked");
        lockMod = nbt.getDouble("lockMod");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("lock", lock);
        nbt.putBoolean("isLocked", isLocked);
        nbt.putDouble("lockMod", lockMod);
    }

    public boolean canAccess(Player player) {
        if (!isLocked) return true;
        if (player == null) return false;

        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof ItemKey && ItemKey.getPins(stack) == this.lock) {
            playUnlockSound(player);
            return true;
        }

        if (stack.getItem() == ModItems.KEY_RED.get()) {
            playUnlockSound(player);
            return true;
        }

        return tryPick(player);
    }

    private void playUnlockSound(Player player) {
        // Воспроизвести звук открытия замка
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private boolean tryPick(Player player) {
        boolean canPick = false;
        ItemStack stack = player.getMainHandItem();
        double chanceOfSuccess = this.lockMod * 100;

        if (!stack.isEmpty() && stack.getItem() == ModItems.PIN.get() &&
                (player.getInventory().contains(ModItems.SCREWDRIVER.get().getDefaultInstance()) ||
                        player.getInventory().contains(ModItems.SCREWDRIVER_DESH.get().getDefaultInstance()))) {
            stack.shrink(1);
            canPick = true;
        }

        if (!stack.isEmpty() && (stack.getItem() == ModItems.SCREWDRIVER.get() || stack.getItem() == ModItems.SCREWDRIVER_DESH.get()) &&
                player.getInventory().contains(ModItems.PIN.get().getDefaultInstance())) {
            player.getInventory().removeItem(ModItems.PIN.get().getDefaultInstance());
            canPick = true;
        }

        if (canPick) {
            if (ArmorUtil.checkArmorPiece(player, ModArmorItems.JACKET.get(), 2) || ArmorUtil.checkArmorPiece(player, ModArmorItems.JACKET_2.get(), 2))
                chanceOfSuccess *= 100D;

            double rand = player.level().random.nextDouble() * 100;

            if (chanceOfSuccess > rand) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.ITEM_BREAK, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.8F + player.level().random.nextFloat() * 0.2F);
        }
        return false;
    }
}