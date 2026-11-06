package com.hbm.tileentity;

import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemKey;
import com.hbm.util.ArmorUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class TileEntityLockableBase extends TileEntityLoadedBase {

    protected int lock;
    private boolean isLocked = false;
    protected double lockMod = 0.1D;
    /** Whether a counterfeit lock can be made out of it */
    public boolean cheesable = true;

    public TileEntityLockableBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isLocked() { return isLocked; }

    public void lock() {
        // if (lock == 0) — в 1.20.1 нет MainRegistry.logger, можно System.err или просто пропустить
        isLocked = true;
        setChanged();
    }

    public void unlock() {
        isLocked = false;
        setChanged();
    }

    public void setPins(int pins) { lock = pins; setChanged(); }
    public int getPins() { return lock; }
    public void setMod(double mod) { lockMod = mod; setChanged(); }
    public double getMod() { return lockMod; }

    public boolean isCheesable() { return cheesable; }
    public void setCheesable(boolean value) { this.cheesable = value; setChanged(); }

    // === NBT ===

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        lock = nbt.getInt("lock");
        cheesable = !nbt.contains("cheesable") || nbt.getBoolean("cheesable"); // fallback = true
        isLocked = nbt.getBoolean("isLocked");
        lockMod = nbt.getDouble("lockMod");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("lock", lock);
        nbt.putBoolean("cheesable", cheesable);
        nbt.putBoolean("isLocked", isLocked);
        nbt.putDouble("lockMod", lockMod);
    }

    // === Сеть ===

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(lock);
        buf.writeBoolean(cheesable);
        buf.writeBoolean(isLocked);
        buf.writeDouble(lockMod);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        lock = buf.readInt();
        cheesable = buf.readBoolean();
        isLocked = buf.readBoolean();
        lockMod = buf.readDouble();
    }

    // === Доступ ===

    public boolean canAccess(Player player) {
        if (!isLocked) return true;
        if (player == null) return false;

        ItemStack stack = player.getMainHandItem();

        if (!stack.isEmpty() && stack.getItem() instanceof ItemKey && ItemKey.getPins(stack) == this.lock) {
            playUnlockSound(player);
            return true;
        }

        if (!stack.isEmpty() && stack.getItem() == ModItems.KEY_RED.get()) {
            playUnlockSound(player);
            return true;
        }

        return tryPick(player);
    }

    private void playUnlockSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private boolean tryPick(Player player) {
        boolean canPick = false;
        ItemStack stack = player.getMainHandItem();
        double chanceOfSuccess = this.lockMod * 100;

        // Игрок держит PIN + имеет отвёртку в инвентаре
        if (!stack.isEmpty() && stack.getItem() == ModItems.PIN.get()
                && (hasItem(player, ModItems.SCREWDRIVER.get()) || hasItem(player, ModItems.SCREWDRIVER_DESH.get()))) {
            stack.shrink(1);
            canPick = true;
        }

        // Игрок держит отвёртку + имеет PIN в инвентаре
        if (!stack.isEmpty() && (stack.getItem() == ModItems.SCREWDRIVER.get() || stack.getItem() == ModItems.SCREWDRIVER_DESH.get())
                && hasItem(player, ModItems.PIN.get())) {
            consumeItem(player, ModItems.PIN.get(), 1);
            canPick = true;
        }

        if (canPick) {
            if (ArmorUtil.checkArmorPiece(player, ModArmorItems.JACKET.get(), 2)
                    || ArmorUtil.checkArmorPiece(player, ModArmorItems.JACKET_2.get(), 2)) {
                chanceOfSuccess *= 100D;
            }

            double rand = player.level().random.nextDouble() * 100;

            if (chanceOfSuccess > rand) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.8F + player.level().random.nextFloat() * 0.2F);
        }
        return false;
    }

    /** Проверка наличия предмета в инвентаре игрока (1.20.1). */
    private static boolean hasItem(Player player, net.minecraft.world.item.Item item) {
        for (ItemStack s : player.getInventory().items) {
            if (!s.isEmpty() && s.getItem() == item) return true;
        }
        for (ItemStack s : player.getInventory().offhand) {
            if (!s.isEmpty() && s.getItem() == item) return true;
        }
        return false;
    }

    /** Уменьшение количества предмета в инвентаре игрока (1.20.1). */
    private static void consumeItem(Player player, net.minecraft.world.item.Item item, int amount) {
        for (int i = 0; i < player.getInventory().items.size() && amount > 0; i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (!s.isEmpty() && s.getItem() == item) {
                int take = Math.min(amount, s.getCount());
                s.shrink(take);
                amount -= take;
                if (s.isEmpty()) player.getInventory().items.set(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < player.getInventory().offhand.size() && amount > 0; i++) {
            ItemStack s = player.getInventory().offhand.get(i);
            if (!s.isEmpty() && s.getItem() == item) {
                int take = Math.min(amount, s.getCount());
                s.shrink(take);
                amount -= take;
                if (s.isEmpty()) player.getInventory().offhand.set(i, ItemStack.EMPTY);
            }
        }
    }
}