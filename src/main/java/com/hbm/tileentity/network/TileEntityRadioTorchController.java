package com.hbm.tileentity.network;

import com.hbm.api.redstoneoverradio.IRORInteractive;
import com.hbm.api.redstoneoverradio.RORFunctionException;
import com.hbm.blocks.network.RadioTorchBase;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.ExplosionEffectWeapon;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.inventory.container.ContainerRadioTorch;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * RTTY-контроллер: принимает команду из канала и передаёт её соседней машине
 * через {@link IRORInteractive#runRORFunction}.
 *
 * <p>Порт {@code ref:tileentity/network/TileEntityRadioTorchController.java}.
 * Команда разбирается статикой того же интерфейса: имя до {@code !},
 * параметры через {@code :}. Исключение разбора не ломает тик — строка
 * просто игнорируется, как в эталоне.</p>
 *
 * <p>{@code selfdestruct} в канале взрывает контроллер — эталонное поведение,
 * оно перенесено без изменений.</p>
 */
public class TileEntityRadioTorchController extends TileEntityLoadedBase implements MenuProvider,
        com.hbm.interfaces.IControlReceiver {

    public String channel = "";
    public String prev;
    public boolean polling = true;
    private boolean receiving;
    private boolean firstTick = true;

    public TileEntityRadioTorchController(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADIO_TORCH_CONTROLLER.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        boolean receiving = false;

        if (this.channel != null && !this.channel.isEmpty()) {
            BlockPos attach = worldPosition.relative(getBlockState().getValue(RadioTorchBase.FACING));
            BlockEntity be = level.getBlockEntity(attach);

            if (be instanceof IRORInteractive ror) {
                RTTYSystem.RTTYChannel chan = RTTYSystem.listen(level, this.channel);

                if (chan != null) {
                    String rec = "" + chan.signal;

                    if ("selfdestruct".equals(rec)) {
                        level.destroyBlock(worldPosition, false);
                        ExplosionVNT vnt = new ExplosionVNT(level,
                                worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                                5, null);
                        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, 50).setupPiercing(5F, 0.5F));
                        vnt.setPlayerProcessor(new PlayerProcessorStandard());
                        vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
                        vnt.explode();
                        return;
                    }

                    if (chan.timeStamp >= level.getGameTime() - 1 && chan.timeStamp != -1) {
                        receiving = true;
                        boolean changed = !rec.equals(this.prev);

                        if (this.polling || !this.receiving || changed) {
                            try {
                                if (!rec.isEmpty()) {
                                    ror.runRORFunction(
                                            IRORInteractive.PREFIX_FUNCTION + IRORInteractive.getCommand(rec),
                                            IRORInteractive.getParams(rec));
                                }
                            } catch (RORFunctionException ignored) {
                                // Кривая команда — не повод падать, эталон её тоже глотает.
                            }
                        }

                        if (changed) {
                            this.prev = rec;
                            setChanged();
                        }
                    }
                }
            }
        }

        // Очередь рассылки после загрузки пуста, поэтому сохранённое состояние
        // «принимаю» живёт ещё один тик (эталонное поведение).
        if (!this.firstTick || receiving) {
            if (this.receiving != receiving) {
                this.receiving = receiving;
                setChanged();
            }
        }
        this.firstTick = false;

        networkPackNT(50);
    }

    // === NBT ===

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.polling = nbt.getBoolean("p");
        this.channel = nbt.getString("c");
        this.prev = nbt.contains("prev") ? nbt.getString("prev") : null;
        this.receiving = nbt.contains("r") ? nbt.getBoolean("r") : (this.prev != null && !this.prev.isEmpty());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("p", this.polling);
        if (this.channel != null) nbt.putString("c", this.channel);
        if (this.prev != null) nbt.putString("prev", this.prev);
        nbt.putBoolean("r", this.receiving);
    }

    // === Синхронизация ===

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(this.polling);
        BufferUtil.writeString(buf, this.channel);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.polling = buf.readBoolean();
        this.channel = BufferUtil.readString(buf);
    }

    public void receiveControl(Player player, CompoundTag data) {
        if (data.contains("p")) this.polling = data.getBoolean("p");
        if (data.contains("c")) this.setChannelName(data.getString("c"));
        setChanged();
    }

    private void setChannelName(String channel) {
        if (!channel.equals(this.channel)) {
            this.channel = channel;
            this.prev = null;
            this.receiving = false;
            setChanged();
        }
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
        return Component.translatable("container.rttyController");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerRadioTorch(id, inv, this);
    }
}
