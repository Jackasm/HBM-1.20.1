package com.hbm.tileentity.network;

import com.hbm.blocks.network.RadioTorchBase;
import com.hbm.inventory.container.ContainerRadioTorchCounter;
import com.hbm.module.ModulePatternMatcher;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * RTTY-счётчик предметов: три канала, три образца. Считает в контейнере,
 * на котором висит факел, все стеки, подходящие под образец слота, и
 * передаёт число в канал.
 *
 * <p>Порт {@code ref:tileentity/network/TileEntityRadioTorchCounter.java}.
 * Опорный контейнер берётся по {@code FACING} — в порте эта сторона уже
 * смотрит на опору (в 1.7.10 её давала {@code meta.getOpposite()}).</p>
 *
 * <p>Отличие от эталона: {@code ModulePatternMatcher} в 1.20.1 работает по
 * тегам предметов вместо ore dictionary, см. его документацию.</p>
 */
public class TileEntityRadioTorchCounter extends TileEntityLoadedBase implements Container, MenuProvider,
        com.hbm.interfaces.IControlReceiver {

    public final String[] channel = new String[3];
    public final int[] lastCount = new int[3];
    private final boolean[] forceUpdate = new boolean[3];
    public boolean polling = false;
    public final ModulePatternMatcher matcher = new ModulePatternMatcher(3);

    private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);

    public TileEntityRadioTorchCounter(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADIO_TORCH_COUNTER.get(), pos, state);
        for (int i = 0; i < 3; i++) this.channel[i] = "";
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        Direction dir = getBlockState().getValue(RadioTorchBase.FACING);
        BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));

        if (be instanceof Container inv) {
            for (int i = 0; i < 3; i++) {
                if (this.channel[i] == null || this.channel[i].isEmpty()) continue;

                ItemStack pattern = this.slots.get(i);
                if (pattern.isEmpty()) continue;

                int count = 0;

                for (int j = 0; j < inv.getContainerSize(); j++) {
                    ItemStack stack = inv.getItem(j);
                    if (!stack.isEmpty() && this.matcher.isValidForFilter(pattern, i, stack)) {
                        count += stack.getCount();
                    }
                }

                if (this.polling || this.forceUpdate[i] || this.lastCount[i] != count) {
                    RTTYSystem.broadcast(level, this.channel[i], count);
                    this.forceUpdate[i] = false;
                }

                this.lastCount[i] = count;
            }
        }

        networkPackNT(15);
    }

    /** Смена режима образца: колесо/правый клик на слоте. */
    public void nextMode(int index) {
        this.matcher.nextMode(level, this.slots.get(index), index);
        this.forceUpdate[index] = true;
        setChanged();
    }

    // === Container ===

    @Override public int getContainerSize() { return this.slots.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.slots) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override public @NotNull ItemStack getItem(int slot) { return this.slots.get(slot); }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.slots, slot, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.slots, slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.slots.set(slot, stack);
        this.matcher.initPatternStandard(level, stack, slot);
        this.forceUpdate[slot] = true;
        setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return level != null
                && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 256.0;
    }

    @Override public void clearContent() { this.slots.clear(); setChanged(); }

    // === NBT ===

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.polling = nbt.getBoolean("p");
        for (int i = 0; i < 3; i++) {
            this.channel[i] = nbt.getString("c" + i);
            this.lastCount[i] = nbt.getInt("l" + i);
            this.slots.set(i, ItemStack.of(nbt.getCompound("s" + i)));
            this.forceUpdate[i] = true;
        }
        this.matcher.readFromNBT(nbt);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("p", this.polling);
        for (int i = 0; i < 3; i++) {
            if (this.channel[i] != null) nbt.putString("c" + i, this.channel[i]);
            nbt.putInt("l" + i, this.lastCount[i]);
            if (!this.slots.get(i).isEmpty()) {
                CompoundTag stack = new CompoundTag();
                this.slots.get(i).save(stack);
                nbt.put("s" + i, stack);
            }
        }
        this.matcher.writeToNBT(nbt);
    }

    // === Синхронизация ===

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(this.polling);
        BufferUtil.writeIntArray(buf, this.lastCount);
        this.matcher.serialize(buf);
        for (int i = 0; i < 3; i++) {
            BufferUtil.writeString(buf, this.channel[i]);
            BufferUtil.writeItemStack(buf, this.slots.get(i));
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.polling = buf.readBoolean();
        int[] counts = BufferUtil.readIntArray(buf);
        for (int i = 0; i < Math.min(counts.length, this.lastCount.length); i++) this.lastCount[i] = counts[i];
        this.matcher.deserialize(buf);
        for (int i = 0; i < 3; i++) {
            this.channel[i] = BufferUtil.readString(buf);
            this.slots.set(i, BufferUtil.readItemStack(buf));
        }
    }

    /** Настройки от клиента: опрос, каналы, образцы. */
    public void receiveControl(Player player, CompoundTag data) {
        if (data.contains("polling")) this.polling = data.getBoolean("polling");
        for (int i = 0; i < 3; i++) {
            if (data.contains("c" + i)) {
                String channel = data.getString("c" + i);
                if (!channel.equals(this.channel[i])) {
                    this.channel[i] = channel;
                    this.forceUpdate[i] = true;
                }
            }
            if (data.contains("s" + i)) {
                this.slots.set(i, ItemStack.of(data.getCompound("s" + i)));
                this.forceUpdate[i] = true;
            }
        }
        setChanged();
    }

    // === IControlReceiver ===

    @Override
    public void receiveControl(@NotNull CompoundTag data) {
        receiveControl(null, data);
    }

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 256.0;
    }

    // === MenuProvider ===

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.rttyCounter");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerRadioTorchCounter(id, inv, this);
    }
}
