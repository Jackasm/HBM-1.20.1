package com.hbm.tileentity.storage;

import com.hbm.sound.ModSounds;
import com.hbm.inventory.container.ContainerFileCabinet;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TileEntityFileCabinet extends TileEntityCrateBase {

    // Анимация ящиков
    public float lowerExtent = 0;
    public float prevLowerExtent = 0;
    public float upperExtent = 0;
    public float prevUpperExtent = 0;
    private int playersUsing = 0;
    private int openTimer = 0;

    public TileEntityFileCabinet(BlockPos pos, BlockState state) {
        super(ModTileEntity.FILING_CABINET.get(), pos, state, 8); // 8 слотов
    }
    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (playersUsing > 0) {
                if (openTimer < 10) openTimer++;
            } else {
                openTimer = 0;
            }
            if (level.getGameTime() % 5 == 0) {
                syncWithClient();
            }
        }
        updateAnimation();
    }

    private void updateAnimation() {
        prevLowerExtent = lowerExtent;
        prevUpperExtent = upperExtent;

        float openSpeed = playersUsing > 0 ? 0.0625F : 0.04F;
        float maxExtent = 0.8F;

        if (playersUsing > 0) {
            lowerExtent = Math.min(lowerExtent + openSpeed, maxExtent);
            if (openTimer >= 5) {
                upperExtent = Math.min(upperExtent + openSpeed, maxExtent);
            }
        } else {
            if (upperExtent > 0) upperExtent = Math.max(upperExtent - openSpeed, 0);
            if (lowerExtent > 0) lowerExtent = Math.max(lowerExtent - openSpeed, 0);
        }

        lowerExtent = Math.min(Math.max(lowerExtent, 0), maxExtent);
        upperExtent = Math.min(Math.max(upperExtent, 0), maxExtent);
    }

    public void startOpen(@NotNull Player player) {
        if (!Objects.requireNonNull(level).isClientSide) {
            playersUsing++;
            openTimer = 0;
            if (playersUsing == 1) {
                level.playSound(null, getBlockPos(), ModSounds.FILING_CABINET_OPEN.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            syncWithClient();
        }
    }

    public void stopOpen(@NotNull Player player) {
        if (!Objects.requireNonNull(level).isClientSide) {
            playersUsing--;
            if (playersUsing <= 0) {
                level.playSound(null, getBlockPos(), ModSounds.FILING_CABINET_CLOSE.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            syncWithClient();
        }
    }

    @Override
    public void openInventory(Player player) {
        super.openInventory(player);
        startOpen(player);
    }

    @Override
    public void closeInventory(Player player) {
        super.closeInventory(player);
        stopOpen(player);
    }

    // Синхронизация анимации с клиентом
    private void syncWithClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            setChanged();
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        playersUsing = tag.getInt("PlayersUsing");
        openTimer = tag.getInt("OpenTimer");
        lowerExtent = tag.getFloat("LowerExtent");
        upperExtent = tag.getFloat("UpperExtent");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("PlayersUsing", playersUsing);
        tag.putInt("OpenTimer", openTimer);
        tag.putFloat("LowerExtent", lowerExtent);
        tag.putFloat("UpperExtent", upperExtent);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.file_cabinet");
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerFileCabinet(windowId, inv, this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldAnimate() {
        return lowerExtent != prevLowerExtent || upperExtent != prevUpperExtent ||
                lowerExtent > 0 || upperExtent > 0;
    }

    public void drops() {
        if (level != null && !level.isClientSide) {
            for (int i = 0; i < slots.length; i++) {
                ItemStack stack = slots[i];
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                }
            }
        }
    }
}