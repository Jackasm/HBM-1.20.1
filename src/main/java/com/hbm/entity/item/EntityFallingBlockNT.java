package com.hbm.entity.item;

import com.hbm.blocks.BlockFallingNT;
import com.hbm.blocks.ISpotlight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class EntityFallingBlockNT extends Entity {

    private static final EntityDataAccessor<Integer> DATA_BLOCK_ID = SynchedEntityData.defineId(EntityFallingBlockNT.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_META = SynchedEntityData.defineId(EntityFallingBlockNT.class, EntityDataSerializers.INT);

    private Block fallingBlock;
    private int fallingMeta = 0;
    public int fallingTicks;
    public boolean canDrop = true;
    private boolean destroyOnLand;
    private boolean canHurtEntities;
    private int damageCap = 40;
    private float damageAmount = 2.0F;
    public CompoundTag tileNBT;

    public EntityFallingBlockNT(EntityType<?> type, Level level) {
        super(type, level);
        this.setBoundingBox(new AABB(this.getX(), this.getY(), this.getZ(), this.getX(), this.getY(), this.getZ()).inflate(0.98F));
        this.blocksBuilding = true;
    }

    public EntityFallingBlockNT(EntityType<?> type, Level level, double x, double y, double z, BlockState state) {
        super(type, level);
        this.fallingBlock = state.getBlock();
        this.entityData.set(DATA_BLOCK_ID, BuiltInRegistries.BLOCK.getId(this.fallingBlock));
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_BLOCK_ID, 0);
        this.entityData.define(DATA_META, 0);
    }

    public Block getBlock() {
        if (this.fallingBlock != null) return this.fallingBlock;

        int blockId = this.entityData.get(DATA_BLOCK_ID);
        this.fallingBlock = BuiltInRegistries.BLOCK.byId(blockId);
        return this.fallingBlock;
    }

    public int getMeta() {
        return this.fallingMeta;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (this.getBlock() == null || this.getBlock() == Blocks.AIR || this.getBlock() instanceof ISpotlight) {
            this.discard();
            return;
        }

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        this.fallingTicks++;
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04, 0));
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.98, 0.98));

        if (!this.level().isClientSide) {
            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            int meta = this.getMeta();

            if (this.fallingTicks == 1) {
                if (this.level().getBlockState(pos).getBlock() != this.getBlock()) {
                    this.discard();
                    return;
                }
                this.level().removeBlock(pos, false);
            }

            if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));

                if (this.level().getBlockState(pos).getBlock() != Blocks.MOVING_PISTON) {
                    this.discard();

                    if (!this.destroyOnLand && this.replacementCheck(pos)) {

                        boolean blockPlaced = this.level().setBlock(pos, this.getBlock().defaultBlockState(), 3);

                        if (blockPlaced) {

                            Block block = this.getBlock();

                            if (block instanceof BlockFallingNT falling) {
                                falling.onLand(this.level(), pos, this.getBlock().defaultBlockState(),
                                        this.level().getBlockState(pos), this);
                            }

                            if (this.tileNBT != null && block instanceof EntityBlock entityBlock) {
                                BlockEntity tile = entityBlock.newBlockEntity(pos, this.getBlock().defaultBlockState());
                                if (tile != null) {
                                    CompoundTag nbt = tile.saveWithoutMetadata();
                                    for (String key : this.tileNBT.getAllKeys()) {
                                        Tag base = this.tileNBT.get(key);
                                        if (!key.equals("x") && !key.equals("y") && !key.equals("z")) {
                                            nbt.put(key, Objects.requireNonNull(base).copy());
                                        }
                                    }
                                    tile.load(nbt);
                                    this.level().setBlockEntity(tile);
                                }
                            }
                        } else if (this.canDrop && !this.destroyOnLand) {
                            ItemStack stack = new ItemStack(this.getBlock());
                            this.spawnAtLocation(stack, 0);
                        }
                    } else if (this.canDrop && !this.destroyOnLand) {
                        ItemStack stack = new ItemStack(this.getBlock());
                        this.spawnAtLocation(stack, 0);
                    }
                }
            } else if (this.fallingTicks > 100 && !this.level().isClientSide &&
                    (pos.getY() < this.level().getMinBuildHeight() ||
                            pos.getY() > this.level().getMaxBuildHeight()) ||
                    this.fallingTicks > 600) {
                if (this.canDrop) {
                    ItemStack stack = new ItemStack(this.getBlock());
                    this.spawnAtLocation(stack, 0);
                }
                this.discard();
            }
        }
    }

    public boolean replacementCheck(BlockPos pos) {
        return this.level().getBlockState(pos).canBeReplaced() && this.getBlock().defaultBlockState().canSurvive(this.level(), pos);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        if (this.canHurtEntities) {
            int fall = (int) Math.ceil(fallDistance - 1.0);

            if (fall > 0) {
                AABB box = this.getBoundingBox();
                List<Entity> list = this.level().getEntities(this, box);
                boolean isAnvil = this.getBlock() == Blocks.ANVIL;
                DamageSource dmgSource = isAnvil ? this.damageSources().anvil(this) : this.damageSources().fallingBlock(this);

                for (Entity entity : list) {
                    if (entity != this) {
                        entity.hurt(dmgSource, Math.min(fall * this.damageAmount, this.damageCap));
                    }
                }

                if (isAnvil && this.random.nextFloat() < 0.05 + fall * 0.05) {
                    int meta = this.getMeta();
                    int damage = meta >> 2;
                    int variant = meta & 3;
                    damage++;

                    if (damage > 2) {
                        this.destroyOnLand = true;
                    } else {
                        this.fallingMeta = variant | (damage << 2);
                    }
                }
            }
        }
        return super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("TileID", BuiltInRegistries.BLOCK.getId(this.fallingBlock));
        tag.putInt("Data", this.fallingMeta);
        tag.putInt("Time", this.fallingTicks);
        tag.putBoolean("DropItem", this.canDrop);
        tag.putBoolean("HurtEntities", this.canHurtEntities);
        tag.putFloat("FallHurtAmount", this.damageAmount);
        tag.putInt("FallHurtMax", this.damageCap);

        if (this.tileNBT != null) {
            tag.put("TileEntityData", this.tileNBT);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("TileID")) {
            int blockId = tag.getInt("TileID");
            this.fallingBlock = BuiltInRegistries.BLOCK.byId(blockId);
        }

        this.fallingMeta = tag.getInt("Data");
        this.fallingTicks = tag.getInt("Time");

        if (tag.contains("HurtEntities")) {
            this.canHurtEntities = tag.getBoolean("HurtEntities");
            this.damageAmount = tag.getFloat("FallHurtAmount");
            this.damageCap = tag.getInt("FallHurtMax");
        } else if (this.fallingBlock == Blocks.ANVIL) {
            this.canHurtEntities = true;
        }

        if (tag.contains("DropItem")) {
            this.canDrop = tag.getBoolean("DropItem");
        }

        if (tag.contains("TileEntityData")) {
            this.tileNBT = tag.getCompound("TileEntityData");
        }

        if (this.fallingBlock == null || this.fallingBlock == Blocks.AIR) {
            this.fallingBlock = Blocks.SAND;
        }
    }

    public void setHurtEntities(boolean hurt) {
        this.canHurtEntities = hurt;
    }

    @OnlyIn(Dist.CLIENT)
    public Level getWorldForRender() {
        return this.level();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean displayFireAnimation() {
        return false;
    }

    public Block getBlockForRender() {
        return this.getBlock();
    }
}