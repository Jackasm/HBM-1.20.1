package com.hbm.entity.item;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.logic.EntityC130;
import com.hbm.tileentity.storage.TileEntitySupplyCrate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EntityParachuteCrate extends Entity {

    public List<ItemStack> items = new ArrayList<>();
    public EntityC130.C130PayloadType payload;

    public EntityParachuteCrate(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {

        this.xo = this.xOld = getX();
        this.yo = this.yOld = getY();
        this.zo = this.zOld = getZ();

        Vec3 motion = this.getDeltaMovement();
        this.setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);

        if(motion.y > -0.2) {
            this.setDeltaMovement(motion.x, motion.y - 0.02, motion.z);
        }

        if(getY() > 600) {
            this.setPos(getX(), 600, getZ());
        }

        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());

        if(this.level().getBlockState(pos).getBlock() != Blocks.AIR) {

            this.discard();

            if(!level().isClientSide) {
                BlockPos cratePos = BlockPos.containing(this.getX(), this.getY() + 1, this.getZ());

                Block crateBlock = switch (payload) {
                    case SUPPLIES -> ModBlocks.CRATE_SUPPLIES.get();
                    case WEAPONS -> ModBlocks.CRATE_WEAPONS.get();
                    default -> ModBlocks.CRATE_SUPPLIES.get();
                };

                level().setBlockAndUpdate(cratePos, crateBlock.defaultBlockState());

                if(level().getBlockEntity(cratePos) instanceof TileEntitySupplyCrate crate) {
                    crate.items.addAll(this.items);
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() { }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        items.clear();
        ListTag list = tag.getList("items", Tag.TAG_COMPOUND);
        for(int i = 0; i < list.size(); i++) {
            CompoundTag nbt = list.getCompound(i);
            items.add(ItemStack.of(nbt));
        }

        payload = EntityC130.C130PayloadType.valueOf(tag.getString("payload"));

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        ListTag list = new ListTag();
        for (ItemStack item : items) {
            CompoundTag nbt = new CompoundTag();
            item.save(nbt);
            list.add(nbt);
        }
        tag.put("items", list);
        tag.putString("payload", payload.name());
    }

}