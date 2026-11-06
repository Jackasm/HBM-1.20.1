package com.hbm.tileentity.deco;

import com.hbm.entity.missile.EntityBobmazon;
import com.hbm.extprop.HbmPlayerProps;

import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;

import com.hbm.items.special.ItemKitCustom;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.IRepairable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileEntityLanternBehemoth extends TileEntityLoadedBase implements IRepairable {

    public boolean isBroken = false;
    public int comTimer = -1;
    public int reputation = 0;

    private List<AStack> repair;

    public TileEntityLanternBehemoth(BlockPos pos, BlockState state) {
        super(ModTileEntity.LANTERN_BEHEMOTH.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (comTimer == 360) playSound(ModSounds.BLOCK_HORN_NEAR_SINGLE.get(), 10F);
        if (comTimer == 280) playSound(ModSounds.BLOCK_HORN_FAR_SINGLE.get(), 10000F);
        if (comTimer == 220) playSound(ModSounds.BLOCK_HORN_NEAR_DUAL.get(), 10F);
        if (comTimer == 100) playSound(ModSounds.BLOCK_HORN_FAR_DUAL.get(), 10000F);

        if (comTimer == 0) {
            boolean bonus = reputation >= 10;
            EntityBobmazon shuttle = new EntityBobmazon(level);
            shuttle.setPos(
                    worldPosition.getX() + 0.5 + level.random.nextGaussian() * 10,
                    300,
                    worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 10
            );
            shuttle.payload = ItemKitCustom.create("Supplies", null, 0xffffff, 0x008000,
                    new ItemStack(ModItems.CIRCUIT_BASIC.get(), 4 + level.random.nextInt(4)),
                    new ItemStack(ModItems.CIRCUIT_ADVANCED.get(), 4 + level.random.nextInt(2)),
                    new ItemStack(ModItems.COIN_TOKEN.get(), Math.max(1, reputation)),
                    bonus ? new ItemStack(ModItems.GEM_ALEXANDRITE.get())
                            : new ItemStack(Items.DIAMOND, 6 + level.random.nextInt(6)),
                    new ItemStack(Blocks.POPPY));
            level.addFreshEntity(shuttle);
        }

        if (comTimer >= 0) comTimer--;

        networkPackNT(250);
    }

    private void playSound(SoundEvent ev, float volume) {
        if (level == null || ev == null) return;
        level.playSound(null, worldPosition, ev, SoundSource.BLOCKS, volume, 1F);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level == null || level.isClientSide) return;

        AABB box = new AABB(worldPosition).inflate(50);
        for (Player player : level.getEntitiesOfClass(Player.class, box)) {
            HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
            if (props != null && props.getReputation() > -25) {
                props.setReputation(props.getReputation() - 1);
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isBroken", isBroken);
        tag.putInt("comTimer", comTimer);
        tag.putInt("reputation", reputation);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        isBroken = tag.getBoolean("isBroken");
        comTimer = tag.getInt("comTimer");
        reputation = tag.getInt("reputation");
    }

    @Override
    public void serialize(io.netty.buffer.ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(isBroken);
    }

    @Override
    public void deserialize(io.netty.buffer.ByteBuf buf) {
        super.deserialize(buf);
        this.isBroken = buf.readBoolean();
    }

    // === IRepairable ===

    @Override
    public List<AStack> getRepairMaterials() {
        if (repair == null) {
            repair = new ArrayList<>();
            repair.add(new ComparableStack(ModItems.PLATE_STEEL.get(), 2));
            repair.add(new ComparableStack(ModItems.CIRCUIT_BASIC.get()));
        }
        return repair;
    }

    @Override
    public void repair(Player player) {
        this.isBroken = false;
        this.comTimer = 400;
        this.reputation = HbmPlayerProps.getData(player).getReputation();
        setChanged();
    }

    @Override
    public boolean isDamaged() {
        return isBroken;
    }

    @Override
    public void tryExtinguish(Level level, BlockPos pos, IRepairable.EnumExtinguishType type) {
        // В оригинале метод был пустой — фонарь не тушится.
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX(),
                worldPosition.getY(),
                worldPosition.getZ(),
                worldPosition.getX() + 1,
                worldPosition.getY() + 6,
                worldPosition.getZ() + 1
        );
    }
}