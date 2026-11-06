package com.hbm.tileentity.network;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.ExplosionEffectWeapon;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityRadioTorchReceiver extends TileEntityRadioTorchBase {

    public TileEntityRadioTorchReceiver(BlockPos pos, BlockState state) {
        // См. комментарий в TileEntityRadioTorchSender: без своего типа тикер
        // не регистрируется и приёмник не читает канал.
        super(ModTileEntity.RADIO_TORCH_RECEIVER.get(), pos, state);
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

                if ("selfdestruct".equals(msg)) {
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

                if (this.customMap) {
                    for (int i = 15; i >= 0; i--) {
                        if (msg.equals(this.mapping[i])) {
                            nextState = i;
                            break;
                        }
                    }
                } else {
                    int sig = 0;
                    try { sig = Integer.parseInt(msg); } catch (Exception ignored) {}
                    nextState = Mth.clamp(sig, 0, 15);
                }

                if (chan.timeStamp < this.lastUpdate - 1 && this.polling) {
                    nextState = 0;
                }

                if (this.lastState != nextState) {
                    this.lastState = nextState;
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                    level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                    setChanged();
                }
            } else if (this.polling && this.lastState != 0) {
                this.lastState = 0;
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                setChanged();
            }
        }

        super.tick();
    }
}