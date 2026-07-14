package com.hbm.tileentity.machine;

import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockTallPlant;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.IFluidCopiable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.ModDamageSource;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TileEntityMachineAutosaw extends TileEntityLoadedBase implements IBufPacketReceiver, IFluidStandardReceiver, IFluidCopiable {

    public static final Set<FluidTypeHBM> acceptedFuels = new HashSet<>();

    static {
        acceptedFuels.add(Fluids.WOODOIL.get());
        acceptedFuels.add(Fluids.ETHANOL.get());
        acceptedFuels.add(Fluids.FISHOIL.get());
        acceptedFuels.add(Fluids.HEAVYOIL.get());
    }

    public FluidTankHBM tank;

    public boolean isOn;
    public boolean isSuspended;
    private int forceSkip;
    public float syncYaw;
    public float rotationYaw;
    public float prevRotationYaw;
    public float syncPitch;
    public float rotationPitch;
    public float prevRotationPitch;

    // 0: searching, 1: extending, 2: retracting
    private int state = 0;

    private int turnProgress;

    public float spin;
    public float lastSpin;

    public TileEntityMachineAutosaw(BlockPos pos, BlockState state) {
        super(ModTileEntity.AUTOSAW.get(), pos, state);
        this.tank = new FluidTankHBM(Fluids.WOODOIL.get(), 100);
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir == Direction.DOWN && acceptedFuels.contains(type);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (!isSuspended && level.getGameTime() % 20 == 0) {
                if (tank.getFill() > 0) {
                    tank.drain(1, IFluidHandler.FluidAction.EXECUTE);
                    this.isOn = true;
                } else {
                    this.isOn = false;
                }

                this.subscribeToAllAround(tank.getTankType(), this);
            }

            if (isOn && !isSuspended) {
                Vec3 pivot = new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 1.75, worldPosition.getZ() + 0.5);
                Vec3 upperArm = new Vec3(0, 0, -4);
                upperArm = upperArm.xRot((float) Math.toRadians(80 - rotationPitch));
                upperArm = upperArm.yRot(-(float) Math.toRadians(rotationYaw));
                Vec3 lowerArm = new Vec3(0, 0, -4);
                lowerArm = lowerArm.xRot((float) -Math.toRadians(80 - rotationPitch));
                lowerArm = lowerArm.yRot(-(float) Math.toRadians(rotationYaw));
                Vec3 armTip = new Vec3(0, 0, -2);
                armTip = armTip.yRot(-(float) Math.toRadians(rotationYaw));

                double cX = pivot.x + upperArm.x + lowerArm.x + armTip.x;
                double cY = pivot.y;
                double cZ = pivot.z + upperArm.z + lowerArm.z + armTip.z;

                List<LivingEntity> affected = level.getEntitiesOfClass(LivingEntity.class,
                        new AABB(cX - 1, cY - 0.25, cZ - 1, cX + 1, cY + 0.25, cZ + 1));

                for (LivingEntity e : affected) {
                    if (e.isAlive() && e.hurt(ModDamageSource.createDamageSource(
                            ModDamageSource.TURBOFAN, null, null, level), 100)) {
                        level.playSound(null, e.getX(), e.getY(), e.getZ(),
                                SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.BLOCKS, 2.0F,
                                0.95F + level.random.nextFloat() * 0.2F);
                        int count = Math.min((int) Math.ceil(e.getMaxHealth() / 4), 250);
                        CompoundTag data = new CompoundTag();
                        data.putString("type", "vanillaburst");
                        data.putInt("count", count * 4);
                        data.putDouble("motion", 0.1D);
                        data.putString("mode", "blockdust");
                        data.putString("block", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(Blocks.REDSTONE_BLOCK)).toString());
                        PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data,
                                        e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ()),
                                level, e.blockPosition(), 50);
                    }
                }

                if (state == 0) {
                    this.rotationYaw += 1;

                    if (this.rotationYaw >= 360) {
                        this.rotationYaw -= 360;
                    }

                    if (forceSkip > 0) {
                        forceSkip--;
                    } else {
                        final double CUT_ANGLE = Math.toRadians(5);
                        double rotationYawRads = Math.toRadians((rotationYaw + 270) % 360);

                        outer:
                        for (int dx = -9; dx <= 9; dx++) {
                            for (int dz = -9; dz <= 9; dz++) {
                                int sqrDst = dx * dx + dz * dz;

                                if (sqrDst <= 4 || sqrDst > 81)
                                    continue;

                                double angle = Math.atan2(dz, dx);
                                double relAngle = Math.abs(angle - rotationYawRads);
                                relAngle = Math.abs((relAngle + Math.PI) % (2 * Math.PI) - Math.PI);

                                if (relAngle > CUT_ANGLE)
                                    continue;

                                int x = worldPosition.getX() + dx;
                                int y = worldPosition.getY() + 1;
                                int z = worldPosition.getZ() + dz;

                                BlockState bState = level.getBlockState(new BlockPos(x, y, z));
                                Block b = bState.getBlock();
                                if (!(bState.is(BlockTags.LOGS) ||
                                        bState.is(BlockTags.LEAVES) ||
                                        bState.is(BlockTags.FLOWERS) ||
                                        bState.is(BlockTags.CROPS) ||
                                        bState.is(BlockTags.SAPLINGS)))
                                    continue;

                                if (shouldIgnore(level, x, y, z, b, bState))
                                    continue;

                                state = 1;
                                break outer;
                            }
                        }
                    }
                }

                int hitY = (int) Math.floor(cY);
                int hitX0 = (int) Math.floor(cX - 0.5);
                int hitZ0 = (int) Math.floor(cZ - 0.5);
                int hitX1 = (int) Math.floor(cX + 0.5);
                int hitZ1 = (int) Math.floor(cZ + 0.5);

                this.tryInteract(hitX0, hitY, hitZ0);
                this.tryInteract(hitX1, hitY, hitZ0);
                this.tryInteract(hitX0, hitY, hitZ1);
                this.tryInteract(hitX1, hitY, hitZ1);

                if (state == 1) {
                    this.rotationPitch += 2;

                    if (this.rotationPitch > 80) {
                        this.rotationPitch = 80;
                        state = 2;
                    }
                }

                if (state == 2) {
                    this.rotationPitch -= 2;

                    if (this.rotationPitch <= 0) {
                        this.rotationPitch = 0;
                        state = 0;
                    }
                }
            }

            this.networkPackNT(100);
        } else {
            this.lastSpin = this.spin;

            if (isOn && !isSuspended) {
                this.spin += 15F;

                Vec3 vec = new Vec3(0.625, 0, 1.625);
                vec = vec.yRot(-(float) Math.toRadians(rotationYaw));

                level.addParticle(ParticleTypes.SMOKE,
                        worldPosition.getX() + 0.5 + vec.x,
                        worldPosition.getY() + 2.0625,
                        worldPosition.getZ() + 0.5 + vec.z,
                        0, 0, 0);
            }

            if (this.spin >= 360F) {
                this.spin -= 360F;
                this.lastSpin -= 360F;
            }

            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;

            if (this.turnProgress > 0) {
                double d0 = Mth.wrapDegrees(this.syncYaw - (double) this.rotationYaw);
                double d1 = Mth.wrapDegrees(this.syncPitch - (double) this.rotationPitch);
                this.rotationYaw = (float) ((double) this.rotationYaw + d0 / (double) this.turnProgress);
                this.rotationPitch = (float) ((double) this.rotationPitch + d1 / (double) this.turnProgress);
                --this.turnProgress;
            } else {
                this.rotationYaw = this.syncYaw;
                this.rotationPitch = this.syncPitch;
            }
        }
    }

    /** Anything additionally that the detector nor the blades should pick up on, like non-mature willows */
    public static boolean shouldIgnore(Level world, int x, int y, int z, Block b, BlockState state) {
        if (b == ModBlocks.PLANT_TALL.get()) {
            // Проверяем, является ли растение зрелым
            if (state.hasProperty(BlockTallPlant.TYPE)) {
                BlockTallPlant.EnumTallFlower type = state.getValue(BlockTallPlant.TYPE);
                DoubleBlockHalf half = state.getValue(BlockTallPlant.HALF);

                // Игнорируем верхнюю часть зрелых растений CD2 и CD3
                if (half == DoubleBlockHalf.UPPER) {
                    return type == BlockTallPlant.EnumTallFlower.CD2 ||
                            type == BlockTallPlant.EnumTallFlower.CD3;
                }
            }
            return false;
        }

        if (b instanceof IPlantable) {
            // Проверка на зрелость через интерфейс IGrowable
            if (b instanceof BonemealableBlock growable) {
                // Если растение не может быть удобрено костной мукой, значит оно зрелое
                return growable.isValidBonemealTarget(world, new BlockPos(x, y, z), state, world.isClientSide);
            }
            return true; // по умолчанию игнорируем
        }

        return false;
    }

    protected void tryInteract(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState bState = Objects.requireNonNull(level).getBlockState(pos);
        Block b = bState.getBlock();

        if (!shouldIgnore(level, x, y, z, b, bState)) {
            if (bState.is(BlockTags.LEAVES) ||
                    bState.is(BlockTags.CROPS) ||
                    bState.is(BlockTags.FLOWERS) ||
                    bState.is(BlockTags.SAPLINGS)) {
                cutCrop(x, y, z);
            } else if (bState.is(BlockTags.LOGS)) {
                fellTree(x, y, z);
                if (state == 1) {
                    state = 2;
                }
            }
        }

        // Return when hitting a wall
        if (state == 1 && !bState.getCollisionShape(level, pos).isEmpty()) {
            state = 2;
            forceSkip = 5;
        }
    }

    protected void cutCrop(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos soilPos = new BlockPos(x, y - 1, z);
        BlockState soilState = Objects.requireNonNull(level).getBlockState(soilPos);

        BlockState bState = level.getBlockState(pos);
        Block b = bState.getBlock();

        int eventData = Block.getId(bState);

        if (b instanceof BlockTallPlant) {
            BlockTallPlant.EnumTallFlower type = bState.getValue(BlockTallPlant.TYPE);
            eventData += (type.ordinal() << 12);
        }

        level.levelEvent(2001, pos, eventData);

        Block replacementBlock = Blocks.AIR;

        if (!level.isClientSide) {
            List<ItemStack> drops = Block.getDrops(bState, (net.minecraft.server.level.ServerLevel) level, pos, null);
            boolean replanted = false;

            for (ItemStack drop : drops) {
                if (!replanted && drop.getItem() instanceof IPlantable seed) {
                    if (soilState.canSustainPlant(level, soilPos, Direction.UP, seed)) {
                        replacementBlock = seed.getPlant(level, pos).getBlock();
                        replanted = true;
                        drop.shrink(1);
                    }
                }

                float delta = 0.7F;
                double dx = (double) (level.random.nextFloat() * delta) + (double) (1.0F - delta) * 0.5D;
                double dy = (double) (level.random.nextFloat() * delta) + (double) (1.0F - delta) * 0.5D;
                double dz = (double) (level.random.nextFloat() * delta) + (double) (1.0F - delta) * 0.5D;

                ItemEntity entityItem = new ItemEntity(level, x + dx, y + dy, z + dz, drop);
                entityItem.setDefaultPickUpDelay();
                level.addFreshEntity(entityItem);
            }

            // Workaround для пшеницы
            if (b == Blocks.WHEAT || b == Blocks.BEETROOTS  || b == Blocks.POTATOES && !replanted) {
                replacementBlock = b;
            }
        }

        level.setBlock(pos, replacementBlock.defaultBlockState(), 3);
    }

    protected void fellTree(int x, int y, int z) {
        BlockPos downPos = new BlockPos(x, y - 1, z);
        BlockPos furtherDownPos = new BlockPos(x, y - 2, z);

        // Спускаемся к основанию дерева
        if (Objects.requireNonNull(level).getBlockState(downPos).is(BlockTags.LOGS)) {
            y--;
            if (level.getBlockState(furtherDownPos).is(BlockTags.LOGS)) {
                y--;
            }
        }

        // Определяем тип дерева по первому бревну
        BlockState firstLog = level.getBlockState(new BlockPos(x, y, z));
        Block logBlock = firstLog.getBlock();
        Block saplingType = getSaplingFromLog(logBlock);

        // Удаляем все брёвна и ульи
        for (int i = y; i < y + 10; i++) {
            int[][] dir = new int[][]{{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};

            for (int[] d : dir) {
                BlockPos targetPos = new BlockPos(x + d[0], i, z + d[1]);
                BlockState targetState = level.getBlockState(targetPos);

                if (targetState.is(BlockTags.LOGS) || targetState.is(Blocks.BEE_NEST)) {
                    level.destroyBlock(targetPos, true);
                }
            }
        }

        // Удаляем листву, если рядом нет брёвен
        for (int i = y - 5; i < y + 15; i++) {
            for (int dx = -8; dx <= 8; dx++) {
                for (int dz = -8; dz <= 8; dz++) {
                    BlockPos targetPos = new BlockPos(x + dx, i, z + dz);
                    BlockState targetState = level.getBlockState(targetPos);

                    if (targetState.is(BlockTags.LEAVES)) {
                        boolean hasLogNearby = false;

                        // Проверяем наличие брёвен поблизости
                        for (int lx = -2; lx <= 2; lx++) {
                            for (int ly = -2; ly <= 2; ly++) {
                                for (int lz = -2; lz <= 2; lz++) {
                                    if (level.getBlockState(targetPos.offset(lx, ly, lz)).is(BlockTags.LOGS)) {
                                        hasLogNearby = true;
                                        break;
                                    }
                                }
                            }
                        }

                        // Если рядом нет брёвен, листва исчезает
                        if (!hasLogNearby) {
                            level.destroyBlock(targetPos, true);
                        }
                    }
                }
            }
        }

        // Сажаем саженец правильного типа
        if (saplingType != null) {
            BlockPos plantPos = new BlockPos(x, y, z);
            if (saplingType.defaultBlockState().canSurvive(level, plantPos)) {
                level.setBlock(plantPos, saplingType.defaultBlockState(), 3);
            }
        }
    }

    private Block getSaplingFromLog(Block log) {
        if (log == Blocks.OAK_LOG) return Blocks.OAK_SAPLING;
        if (log == Blocks.SPRUCE_LOG) return Blocks.SPRUCE_SAPLING;
        if (log == Blocks.BIRCH_LOG) return Blocks.BIRCH_SAPLING;
        if (log == Blocks.JUNGLE_LOG) return Blocks.JUNGLE_SAPLING;
        if (log == Blocks.ACACIA_LOG) return Blocks.ACACIA_SAPLING;
        if (log == Blocks.DARK_OAK_LOG) return Blocks.DARK_OAK_SAPLING;
        if (log == Blocks.MANGROVE_LOG) return Blocks.MANGROVE_PROPAGULE;
        if (log == Blocks.CHERRY_LOG) return Blocks.CHERRY_SAPLING;
        return Blocks.OAK_SAPLING; // по умолчанию
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeBoolean(this.isOn);
        buf.writeBoolean(this.isSuspended);
        buf.writeFloat(this.rotationYaw);
        buf.writeFloat(this.rotationPitch);
        this.tank.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.isOn = buf.readBoolean();
        this.isSuspended = buf.readBoolean();
        this.syncYaw = buf.readFloat();
        this.syncPitch = buf.readFloat();
        this.turnProgress = 3;
        this.tank.deserialize(buf);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.isOn = nbt.getBoolean("isOn");
        this.isSuspended = nbt.getBoolean("isSuspended");
        this.forceSkip = nbt.getInt("skip");
        this.rotationYaw = nbt.getFloat("yaw");
        this.rotationPitch = nbt.getFloat("pitch");
        this.state = nbt.getInt("state");
        this.tank.readFromNBT(nbt, "t");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("isOn", this.isOn);
        nbt.putBoolean("isSuspended", this.isSuspended);
        nbt.putInt("skip", this.forceSkip);
        nbt.putFloat("yaw", this.rotationYaw);
        nbt.putFloat("pitch", this.rotationPitch);
        nbt.putInt("state", this.state);
        this.tank.writeToNBT(nbt, "t");
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tank};
    }

    private AABB renderBB = null;

    @Override
    public AABB getRenderBoundingBox() {
        if (renderBB == null) {
            renderBB = new AABB(
                    worldPosition.getX() - 12,
                    worldPosition.getY(),
                    worldPosition.getZ() - 12,
                    worldPosition.getX() + 13,
                    worldPosition.getY() + 10,
                    worldPosition.getZ() + 13
            );
        }
        return renderBB;
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return tank;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return LazyOptional.of(() -> tank).cast();
        }
        return super.getCapability(cap, side);
    }
}