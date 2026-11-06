package com.hbm.tileentity.network;

import com.hbm.inventory.container.ContainerRadioTorch;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityRadioTorchBase extends TileEntityLoadedBase implements MenuProvider,
        com.hbm.interfaces.IControlReceiver {

    public String channel = "";
    public int lastState = 0;
    public long lastUpdate;
    public boolean polling = false;
    public boolean customMap = false;
    public String[] mapping = new String[16];

    public TileEntityRadioTorchBase(BlockPos pos, BlockState state) {
        this(ModTileEntity.RADIO_TORCH_BASE.get(), pos, state);
    }

    /** Конструктор для наследников со своим типом TE (logic, counter и т. д.). */
    protected TileEntityRadioTorchBase(net.minecraft.world.level.block.entity.BlockEntityType<?> type,
                                       BlockPos pos, BlockState state) {
        super(type, pos, state);
        for (int i = 0; i < 16; i++) mapping[i] = "";
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        networkPackNT(50);
    }

    // === NBT ===

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.polling = nbt.getBoolean("p");
        this.customMap = nbt.getBoolean("m");
        this.lastState = nbt.getInt("l");
        this.lastUpdate = nbt.getLong("u");
        this.channel = nbt.getString("c");
        for (int i = 0; i < 16; i++) this.mapping[i] = nbt.getString("m" + i);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("p", polling);
        nbt.putBoolean("m", customMap);
        nbt.putInt("l", lastState);
        nbt.putLong("u", lastUpdate);
        if (channel != null) nbt.putString("c", channel);
        for (int i = 0; i < 16; i++) if (mapping[i] != null) nbt.putString("m" + i, mapping[i]);
    }

    // === Синхронизация ===

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(this.polling);
        buf.writeBoolean(this.customMap);
        com.hbm.util.BufferUtil.writeString(buf, this.channel);
        for (int i = 0; i < 16; i++) com.hbm.util.BufferUtil.writeString(buf, this.mapping[i]);
        // lastState нужен клиенту: RenderRTTY выбирает по нему текстуру on/off,
        // а тултип показывает «Signal». Без поля факел всегда рисовался
        // выключенным, хотя серверный lastState был > 0 (#165).
        buf.writeInt(this.lastState);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.polling = buf.readBoolean();
        this.customMap = buf.readBoolean();
        this.channel = com.hbm.util.BufferUtil.readString(buf);
        for (int i = 0; i < 16; i++) this.mapping[i] = com.hbm.util.BufferUtil.readString(buf);
        this.lastState = buf.readInt();
    }

    /**
     * Блок-пакет (его шлют sender и receiver через {@code sendBlockUpdated})
     * по умолчанию несёт пустой {@link CompoundTag}: {@code BlockEntity}
     * отдаёт заготовку без полей. {@code onDataPacket} зовёт {@code load} с
     * этим тегом, то есть клиентское состояние ещё и обнулялось. Отдаём полный
     * слепок — тот же, что уходит на диск.
     */
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    // === Доступ ===

    public boolean hasPermission(Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) < 256;
    }

    /** Аналог receiveControl — обновление настроек от клиента. */
    public void receiveControl(Player player, CompoundTag data) {
        if (data.contains("p")) this.polling = data.getBoolean("p");
        if (data.contains("m")) this.customMap = data.getBoolean("m");
        if (data.contains("c")) this.channel = data.getString("c");
        for (int i = 0; i < 16; i++) if (data.contains("m" + i)) this.mapping[i] = data.getString("m" + i);
        setChanged();
    }

    /**
     * Точка входа для {@code NBTControlPacket}: без неё кнопка «Save Settings»
     * молча ничего не делала — пакет ищет IControlReceiver, а TE его не
     * реализовывал.
     */
    @Override
    public void receiveControl(@NotNull CompoundTag data) {
        receiveControl(null, data);
    }

    // === MenuProvider ===

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.rttySender");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerRadioTorch(id, inv, this);
    }

    public boolean stillValid(Player player) {
        return level != null
                && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 256.0;
    }
}