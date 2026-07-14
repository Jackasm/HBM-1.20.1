package com.hbm.tileentity.storage;

import com.hbm.api.fluid.FluidNode;
import com.hbm.api.fluid.IFluidCopiable;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.api.fluid.IOverpressurable;
import com.hbm.api.redstoneoverradio.IRORInteractive;
import com.hbm.api.redstoneoverradio.IRORValueProvider;
import com.hbm.explosion.vanillant.ExplosionVNT;

import com.hbm.inventory.container.ContainerMachineFluidTank;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.*;
import com.hbm.inventory.fluid.trait.FluidTrait.FluidReleaseType;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.particle.helper.GasFlameCreator;
import com.hbm.tileentity.*;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.HBMEnums;
import com.hbm.util.Library.PosDir;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class TileEntityMachineFluidTank extends TileEntityMachineBase implements IFluidStandardTransceiver,
        IPersistentNBT, IOverpressurable, MenuProvider, IRepairable, IFluidCopiable, IRORValueProvider, IRORInteractive {

    protected FluidNode node;
    protected FluidTypeHBM lastType;

    public FluidTankHBM tank;
    public short mode = 0;
    public static final short modes = 4;
    public boolean hasExploded = false;
    public boolean onFire = false;
    public byte lastRedstone = 0;
    public Explosion lastExplosion = null;

    public int age = 0;

    // ========== НОВЫЙ ItemStackHandler ==========
    private final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<FluidTankHBM> fluidHandlerCap;

    public TileEntityMachineFluidTank(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_FLUID_TANK.get(), pos, state);
        tank = new FluidTankHBM(Fluids.NONE.get(), 256000);
        this.fluidHandlerCap = LazyOptional.of(() -> tank);
    }

    // Геттер для инвентаря (нужен для контейнера)
    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0 -> tank.getFill();           // текущее заполнение
                case 1 -> tank.getMaxFill();        // максимальная ёмкость
                case 2 -> Fluids.getID(tank.getTankType()); // ID типа жидкости
                case 3 -> mode;                      // режим работы
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0, 1, 2 -> {} // только для чтения
                case 3 -> mode = (short) value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public ContainerData getContainerData() {
        return dataAccess;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.fluidtank");
    }

    public byte getComparatorPower() {
        if (tank.getFill() == 0) return 0;
        double frac = (double) tank.getFill() / (double) tank.getMaxFill() * 15D;
        return (byte) (Mth.clamp((int) frac + 1, 0, 15));
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (!hasExploded) {
            age++;

            if (age >= 20) {
                age = 0;
                this.setChanged();
            }

            // In buffer mode, acts like a pipe block, providing fluid to its own node
            // otherwise, it is a regular providing/receiving machine, blocking further propagation
            if (mode == 1) {
                if (this.node == null || this.node.expired || tank.getTankType() != lastType) {

                    this.node = (FluidNode) UniNodespace.getNode(level, worldPosition, tank.getTankType().getNetworkProvider());

                    if (this.node == null || this.node.expired || tank.getTankType() != lastType) {
                        this.node = this.createNode(tank.getTankType());
                        UniNodespace.createNode(level, this.node);
                        lastType = tank.getTankType();
                    }
                }

                if (node != null && node.hasValidNet()) {
                    node.net.addProvider(this);
                    node.net.addReceiver(this);
                }
            } else {
                if (this.node != null) {
                    UniNodespace.destroyNode(level, worldPosition, tank.getTankType().getNetworkProvider());
                    this.node = null;
                }

                for (PosDir pd : getConPos()) {
                    FluidNode dirNode = (FluidNode) UniNodespace.getNode(level, pd.pos().getX(), pd.pos().getY(), pd.pos().getZ(), tank.getTankType().getNetworkProvider());

                    if (mode == 2) {
                        tryProvide(tank, level, pd.pos(), pd.dir());
                    } else {
                        if (dirNode != null && dirNode.hasValidNet()) dirNode.net.removeProvider(this);
                    }

                    if (mode == 0) {
                        if (dirNode != null && dirNode.hasValidNet()) dirNode.net.addReceiver(this);
                    } else {
                        if (dirNode != null && dirNode.hasValidNet()) dirNode.net.removeReceiver(this);
                    }
                }
            }

            tank.loadTank(2, 3, slots(), itemHandler);
            tank.setType(0, 1, slots(), itemHandler);
        } else if (this.node != null) {
            UniNodespace.destroyNode(level, worldPosition, tank.getTankType().getNetworkProvider());
            this.node = null;
        }

        byte comp = this.getComparatorPower();
        if (comp != this.lastRedstone) {
            this.setChanged();
            for (PosDir pd : getConPos()) this.updateRedstoneConnection(pd.pos(), pd.dir());
        }
        this.lastRedstone = comp;

        if (tank.getFill() > 0) {
            if (tank.getTankType().isAntimatter()) {
                new ExplosionVNT(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5, 5F).makeAmat().setBlockAllocator(null).setBlockProcessor(null).explode();
                this.explode();
                this.tank.setFill(0);
            }

            if (tank.getTankType().hasTrait(FT_Corrosive.class) && tank.getTankType().getTrait(FT_Corrosive.class).isHighlyCorrosive()) {
                this.explode();
            }

            if (this.hasExploded) {
                int leaking;
                if (tank.getTankType().isAntimatter()) {
                    leaking = tank.getFill();
                } else if (tank.getTankType().hasTrait(FT_Gaseous.class) || tank.getTankType().hasTrait(FT_Gaseous_ART.class)) {
                    leaking = Math.min(tank.getFill(), tank.getMaxFill() / 100);
                } else {
                    leaking = Math.min(tank.getFill(), tank.getMaxFill() / 10000);
                }
                updateLeak(leaking);
            }
        }

        tank.unloadTank(4, 5, slots(), itemHandler);
        networkPackNT(150);
    }

    private ItemStack[]  slots() {
        // Создаём массив из всех 6 слотов
        ItemStack[] allSlots = new ItemStack[6];
        for (int i = 0; i < 6; i++) {
            allSlots[i] = itemHandler.getStackInSlot(i);
        }

        return allSlots;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerMachineFluidTank(containerId, playerInventory, this);
    }

    protected FluidNode createNode(FluidTypeHBM type) {
        List<PosDir> conPos = getConPos();

        HashSet<BlockPos> posSet = new HashSet<>();
        posSet.add(worldPosition);
        for (PosDir pd : conPos) {
            posSet.add(new BlockPos(
                    pd.pos().getX() - pd.dir().getStepX(),
                    pd.pos().getY() - pd.dir().getStepY(),
                    pd.pos().getZ() - pd.dir().getStepZ()
            ));
        }

        return new FluidNode(type.getNetworkProvider(), posSet.toArray(new BlockPos[0]))
                .setConnections(
                        conPos.stream().map(PosDir::pos).toArray(BlockPos[]::new),
                        conPos.stream().map(PosDir::dir).toArray(Direction[]::new)
                );
    }

    @Override
    public boolean setFluidType(FluidTypeHBM type) {
        // Проверяем, отличается ли новый тип от текущего
        if (this.tank.getTankType() == type) {
            return false; // тип уже такой же
        }

        // Меняем тип жидкости
        this.tank.setType(type);
        this.tank.setFill(0); // сбрасываем количество при смене типа

        // Отмечаем, что данные изменились
        this.setChanged();
        return true;
    }


    /** called when the tank breaks due to hazardous materials or external force, can be used to quickly void part of the tank or spawn a mushroom cloud */
    public void explode() {
        this.hasExploded = true;
        this.onFire = tank.getTankType().hasTrait(FT_Flammable.class);
        this.setChanged();
    }

    @Override
    public void explode(Level world, int x, int y, int z) {
        if(this.hasExploded) return;
        this.onFire = tank.getTankType().hasTrait(FT_Flammable.class);
        this.hasExploded = true;
        this.setChanged();
    }

    /** called every tick post explosion, used for leaking fluid and spawning particles */
    public void updateLeak(int amount) {
        if (!hasExploded) return;
        if (amount <= 0) return;

        this.tank.getTankType().onFluidRelease(this, tank, amount);
        this.tank.setFill(Math.max(0, this.tank.getFill() - amount));

        FluidTypeHBM type = tank.getTankType();

        if (type.hasTrait(FT_Amat.class)) {
            new ExplosionVNT(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5, 5F).makeAmat().setBlockAllocator(null).setBlockProcessor(null).explode();

        } else if (type.hasTrait(FT_Flammable.class) && onFire) {
            AABB aabb = new AABB(worldPosition.getX() - 1.5, worldPosition.getY(), worldPosition.getZ() - 1.5,
                    worldPosition.getX() + 2.5, worldPosition.getY() + 5, worldPosition.getZ() + 2.5);
            List<Entity> affected = Objects.requireNonNull(level).getEntitiesOfClass(Entity.class, aabb);
            for (Entity e : affected) e.setRemainingFireTicks(100);
            RandomSource rand = level.random;
            GasFlameCreator.spawnEffect(
                    level,
                    worldPosition.getX() + rand.nextDouble(),
                    worldPosition.getY() + 0.5 + rand.nextDouble(),
                    worldPosition.getZ() + rand.nextDouble(),
                    rand.nextGaussian() * 0.2,
                    0.1,
                    rand.nextGaussian() * 0.2,
                    1.0f
            );

            if (level.getGameTime() % 5 == 0) {
                FT_Polluting.pollute(level, worldPosition, tank.getTankType(), FluidReleaseType.BURN, amount * 5);
            }

        } else if (type.hasTrait(FT_Gaseous.class) || type.hasTrait(FT_Gaseous_ART.class)) {

            if (Objects.requireNonNull(level).getGameTime() % 5 == 0) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "tower");
                data.putFloat("lift", 1F);
                data.putFloat("base", 1F);
                data.putFloat("max", 5F);
                data.putInt("life", 100 + level.random.nextInt(20));
                data.putInt("color", tank.getTankType().getColor());
                PacketDispatcher.sendAuxParticleNT(
                        data,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 1,
                        worldPosition.getZ() + 0.5,
                        level,
                        worldPosition
                );
            }

            if (level.getGameTime() % 5 == 0) {
                FT_Polluting.pollute(level, worldPosition, tank.getTankType(), FluidReleaseType.SPILL, amount * 5);
            }
        }
    }

    @Override
    public void tryExtinguish(Level world, BlockPos pos, EnumExtinguishType type) {
        if (!this.hasExploded || !this.onFire) return;

        if (type == EnumExtinguishType.WATER) {
            if (tank.getTankType().hasTrait(FT_Liquid.class)) { // extinguishing oil with water is a terrible idea!
                Objects.requireNonNull(level).explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5,
                        5F, Level.ExplosionInteraction.TNT);
            } else {
                this.onFire = false;
                this.setChanged();
                return;
            }
        }

        if (type == EnumExtinguishType.FOAM || type == EnumExtinguishType.CO2) {
            this.onFire = false;
            this.setChanged();
        }
    }

    protected List<PosDir> getConPos() {
        return List.of(
                new PosDir(new BlockPos(worldPosition.getX() + 2, worldPosition.getY(), worldPosition.getZ() - 1), Direction.EAST),
                new PosDir(new BlockPos(worldPosition.getX() + 2, worldPosition.getY(), worldPosition.getZ() + 1), Direction.EAST),
                new PosDir(new BlockPos(worldPosition.getX() - 2, worldPosition.getY(), worldPosition.getZ() - 1), Direction.WEST),
                new PosDir(new BlockPos(worldPosition.getX() - 2, worldPosition.getY(), worldPosition.getZ() + 1), Direction.WEST),
                new PosDir(new BlockPos(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() + 2), Direction.SOUTH),
                new PosDir(new BlockPos(worldPosition.getX() + 1, worldPosition.getY(), worldPosition.getZ() + 2), Direction.SOUTH),
                new PosDir(new BlockPos(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 2), Direction.NORTH),
                new PosDir(new BlockPos(worldPosition.getX() + 1, worldPosition.getY(), worldPosition.getZ() - 2), Direction.NORTH)
        );
    }

    public void handleButtonPacket(int value, int meta) {
        mode = (short) ((mode + 1) % modes);
        this.setChanged();
    }

    // Вспомогательный метод для обновления redstone соединения
    private void updateRedstoneConnection(BlockPos pos, Direction dir) {
        // Реализация зависит от вашей системы
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        mode = nbt.getShort("mode");
        tank.readFromNBT(nbt, "tank");
        hasExploded = nbt.getBoolean("exploded");
        onFire = nbt.getBoolean("onFire");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putShort("mode", mode);
        tank.writeToNBT(nbt, "tank");
        nbt.putBoolean("exploded", hasExploded);
        nbt.putBoolean("onFire", onFire);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCap.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCap.invalidate();
        fluidHandlerCap.invalidate();
    }

    @Override
    public long transferFluid(FluidTypeHBM type, int pressure, long fluid) {
        long toTransfer = Math.min(getDemand(type, pressure), fluid);
        tank.setFill(tank.getFill() + (int) toTransfer);
        return fluid - toTransfer;
    }

    @Override
    public long getDemand(FluidTypeHBM type, int pressure) {
        if (this.mode == 2 || this.mode == 3) return 0;
        if (tank.getPressure() != pressure) return 0;
        return type == tank.getTankType() ? tank.getMaxFill() - tank.getFill() : 0;
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        if (tank.getFill() == 0 && !this.hasExploded) return;
        CompoundTag data = new CompoundTag();
        this.tank.writeToNBT(data, "tank");
        data.putShort("mode", mode);
        data.putBoolean("hasExploded", hasExploded);
        data.putBoolean("onFire", onFire);
        nbt.put(NBT_PERSISTENT_KEY, data);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        CompoundTag data = nbt.getCompound(NBT_PERSISTENT_KEY);
        this.tank.readFromNBT(data, "tank");
        this.mode = data.getShort("mode");
        this.hasExploded = data.getBoolean("hasExploded");
        this.onFire = data.getBoolean("onFire");
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf); // отправляет muffled
        buf.writeInt(Fluids.getID(tank.getTankType()));
        buf.writeInt(tank.getFill());
        buf.writeInt(tank.getMaxFill());
        buf.writeShort(mode);
        buf.writeBoolean(hasExploded);
        buf.writeBoolean(onFire);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        int typeId = buf.readInt();
        tank.setType(Fluids.fromID(typeId));
        tank.setFill(buf.readInt());
        buf.readInt();
        this.mode = buf.readShort();
        this.hasExploded = buf.readBoolean();
        this.onFire = buf.readBoolean();
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && type == this.tank.getTankType();
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        if (this.hasExploded) return new FluidTankHBM[0];
        return (mode == 1 || mode == 2) ? new FluidTankHBM[]{tank} : new FluidTankHBM[0];
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        if (this.hasExploded) return new FluidTankHBM[0];
        return (mode == 0 || mode == 1) ? new FluidTankHBM[]{tank} : new FluidTankHBM[0];
    }

    @Override
    public HBMEnums.ConnectionPriority getFluidPriority() {
        return mode == 1 ? HBMEnums.ConnectionPriority.LOW : HBMEnums.ConnectionPriority.NORMAL;
    }

    @Override
    public int[] getFluidIDToCopy() {
        return new int[]{Fluids.getID(tank.getTankType())};
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return tank;
    }

    @Override
    public boolean isDamaged() {
        return this.hasExploded;
    }

    private final List<AStack> repair = new ArrayList<>();

    @Override
    public List<AStack> getRepairMaterials() {
        if (!repair.isEmpty()) return repair;
        repair.add(new ComparableStack(ModItems.PLATE_STEEL.get(), 6));
        return repair;
    }

    @Override
    public void repair() {
        this.hasExploded = false;
        this.setChanged();
    }

    @Override
    public String[] getFunctionInfo() {
        return new String[]{
                PREFIX_VALUE + "type",
                PREFIX_VALUE + "fill",
                PREFIX_VALUE + "fillpercent",
                PREFIX_FUNCTION + "setmode" + NAME_SEPARATOR + "mode",
                PREFIX_FUNCTION + "setmode" + NAME_SEPARATOR + "mode" + PARAM_SEPARATOR + "fallback",
        };
    }

    @Override
    public String provideRORValue(String name) {
        if ((PREFIX_VALUE + "type").equals(name)) return tank.getTankType().getName();
        if ((PREFIX_VALUE + "fill").equals(name)) return "" + tank.getFill();
        if ((PREFIX_VALUE + "fillpercent").equals(name)) return "" + (tank.getFill() * 100 / tank.getMaxFill());
        return null;
    }

    @Override
    public String runRORFunction(String name, String[] params) {
        if ((PREFIX_FUNCTION + "setmode").equals(name) && params.length > 0) {
            int mode = IRORInteractive.parseInt(params[0], 0, 3);

            if (mode != this.mode) {
                this.mode = (short) mode;
                this.setChanged();
                return null;
            } else if (params.length > 1) {
                int altmode = IRORInteractive.parseInt(params[1], 0, 3);
                this.mode = (short) altmode;
                this.setChanged();
                return null;
            }
        }
        return null;
    }
}