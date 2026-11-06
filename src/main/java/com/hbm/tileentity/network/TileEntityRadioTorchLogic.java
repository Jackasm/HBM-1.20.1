package com.hbm.tileentity.network;

import com.hbm.tileentity.ModTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Логический RTTY-факел: сравнивает сигнал канала с таблицей констант и выдаёт
 * редстоун по номеру первой подошедшей строки.
 *
 * <p>Порт {@code ref:tileentity/network/TileEntityRadioTorchLogic.java}. Поля
 * канала, маппинга, опроса и последнего состояния живут в
 * {@link TileEntityRadioTorchBase}, здесь добавляются направление обхода и
 * условия сравнения.</p>
 */
public class TileEntityRadioTorchLogic extends TileEntityRadioTorchBase {

    /** Обход таблицы сверху вниз (с 15-й строки) вместо снизу вверх. */
    public boolean descending = false;

    /**
     * Условие для строки i: 0 — {@code <}, 1 — {@code <=}, 2 — {@code >=},
     * 3 — {@code >}, 4 — {@code ==}, 5 — {@code !=}, 6 — строка равна,
     * 7 — не равна, 8 — содержит, 9 — не содержит.
     */
    public final int[] conditions = new int[16];

    public TileEntityRadioTorchLogic(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADIO_TORCH_LOGIC.get(), pos, state);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            super.tick();
            return;
        }

        if (!this.channel.isEmpty() || this.polling) {
            RTTYSystem.RTTYChannel chan = this.channel.isEmpty() ? null : RTTYSystem.listen(level, this.channel);

            if (chan != null && (this.polling || (chan.timeStamp > this.lastUpdate - 1 && chan.timeStamp != -1))) {
                String msg = "" + chan.signal;
                this.lastUpdate = level.getGameTime();
                int nextState = 0;

                if (chan.timeStamp < this.lastUpdate - 1 && this.polling) {
                    // Опрос без нового сообщения: сравниваем с нулём, а не с мусором.
                    msg = "0";
                }

                if (this.descending) {
                    for (int i = 15; i >= 0; i--) {
                        if (!this.mapping[i].isEmpty() && parseSignal(msg, i)) {
                            nextState = i;
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i <= 15; i++) {
                        if (!this.mapping[i].isEmpty() && parseSignal(msg, i)) {
                            nextState = i;
                            break;
                        }
                    }
                }

                applyState(nextState);
            } else if (this.polling && this.lastState != 0) {
                applyState(0);
            }
        }

        super.tick();
    }

    private void applyState(int nextState) {
        if (this.lastState == nextState) return;
        this.lastState = nextState;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        setChanged();
    }

    /** Сравнение сигнала со строкой {@code mapping[index]} по условию {@code conditions[index]}. */
    public boolean parseSignal(String signal, int index) {
        String map = this.mapping[index] == null ? "" : this.mapping[index];

        if (this.conditions[index] <= 5) {
            long sig;
            long value;
            try {
                sig = Long.parseLong(signal);
                value = Long.parseLong(map);
            } catch (NumberFormatException e) {
                return false;
            }

            return switch (this.conditions[index]) {
                default -> sig < value;
                case 1 -> sig <= value;
                case 2 -> sig >= value;
                case 3 -> sig > value;
                case 4 -> sig == value;
                case 5 -> sig != value;
            };
        }

        return switch (this.conditions[index]) {
            default -> signal.equals(map);
            case 7 -> !signal.equals(map);
            case 8 -> signal.contains(map);
            case 9 -> !signal.contains(map);
        };
    }

    // === NBT ===

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.descending = nbt.getBoolean("d");
        for (int i = 0; i < 16; i++) this.conditions[i] = nbt.getInt("k" + i);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("d", this.descending);
        for (int i = 0; i < 16; i++) if (this.conditions[i] > 0) nbt.putInt("k" + i, this.conditions[i]);
    }

    // === Синхронизация ===

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(this.descending);
        for (int i = 0; i < 16; i++) buf.writeInt(this.conditions[i]);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.descending = buf.readBoolean();
        for (int i = 0; i < 16; i++) this.conditions[i] = buf.readInt();
    }

    @Override
    public void receiveControl(net.minecraft.world.entity.player.Player player, CompoundTag data) {
        super.receiveControl(player, data);
        if (data.contains("d")) this.descending = data.getBoolean("d");
        for (int i = 0; i < 16; i++) if (data.contains("k" + i)) this.conditions[i] = data.getInt("k" + i);
        setChanged();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.rttyLogic");
    }
}
