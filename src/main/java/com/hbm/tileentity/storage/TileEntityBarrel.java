package com.hbm.tileentity.storage;

import com.hbm.api.fluid.FluidNode;
import com.hbm.api.fluid.IFluidCopiable;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.container.ContainerBarrel;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Corrosive;
import com.hbm.inventory.fluid.trait.FT_Polluting;
import com.hbm.inventory.fluid.trait.FluidTrait.FluidReleaseType;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.HBMEnums;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class TileEntityBarrel extends TileEntityMachineBase implements IFluidStandardTransceiver,
        MenuProvider, IFluidCopiable {

    protected FluidNode node;
    protected FluidTypeHBM lastType;

    public FluidTankHBM tank;
    public short mode = 0;
    public static final short modes = 4;
    public int age = 0;
    public byte lastRedstone = 0;

    // ========== ItemStackHandler ==========
    private final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch(slot) {
                case 0, 1 -> stack.getItem() instanceof IItemFluidIdentifier; // слоты идентификаторов
                case 2, 3 -> true; // входные/выходные слоты для жидкости
                case 4, 5 -> true; // слоты для опустошения/заполнения
                default -> false;
            };
        }
    };

    private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<FluidTankHBM> fluidHandlerCap;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0 -> tank.getFill();
                case 1 -> tank.getMaxFill();
                case 2 -> Fluids.getID(tank.getTankType());
                case 3 -> mode;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 3) {
                mode = (short) value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public TileEntityBarrel(BlockPos pos, BlockState state, int capacity) {
        super(ModTileEntity.BARREL.get(), pos, state);
        this.tank = new FluidTankHBM(Fluids.NONE.get(), capacity);
        this.fluidHandlerCap = LazyOptional.of(() -> tank);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public ContainerData getContainerData() {
        return dataAccess;
    }

    public byte getComparatorPower() {
        if (tank.getFill() == 0) return 0;
        double frac = (double) tank.getFill() / (double) tank.getMaxFill() * 15D;
        return (byte) (Math.min((int) frac + 1, 15));
    }

    @Override
    public long getDemand(FluidTypeHBM type, int pressure) {
        if (this.mode == 2 || this.mode == 3) return 0;
        if (tank.getPressure() != pressure) return 0;
        return type == tank.getTankType() ? tank.getMaxFill() - tank.getFill() : 0;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        byte comp = getComparatorPower();
        if (comp != this.lastRedstone) {
            setChanged();
            for (PosDir pd : getConPos()) updateRedstoneConnection(pd.pos(), pd.dir());
        }
        this.lastRedstone = comp;

        // Обработка идентификаторов
        tank.setType(0, 1, slots(), itemHandler);

        tank.loadTank(2, 3, slots(), itemHandler);
        tank.unloadTank(4, 5, slots(), itemHandler);

        // Работа с сетью
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

        if (tank.getFill() > 0) {
            checkFluidInteraction();
        }

        networkPackNT(50);
    }

    private ItemStack[]  slots() {
        // Создаём массив из всех 6 слотов
        ItemStack[] allSlots = new ItemStack[6];
        for (int i = 0; i < 6; i++) {
            allSlots[i] = itemHandler.getStackInSlot(i);
        }

        return allSlots;
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

    protected List<PosDir> getConPos() {
        return List.of(
                new PosDir(worldPosition.relative(Direction.EAST), Direction.EAST),
                new PosDir(worldPosition.relative(Direction.WEST), Direction.WEST),
                new PosDir(worldPosition.relative(Direction.UP), Direction.UP),
                new PosDir(worldPosition.relative(Direction.DOWN), Direction.DOWN),
                new PosDir(worldPosition.relative(Direction.SOUTH), Direction.SOUTH),
                new PosDir(worldPosition.relative(Direction.NORTH), Direction.NORTH)
        );
    }

    public void checkFluidInteraction() {
        Block b = this.getBlockState().getBlock();

        // Антиматерия в обычной бочке
        if (b != ModBlocks.BARREL_ANTIMATTER.get() && tank.getTankType().isAntimatter()) {
            Objects.requireNonNull(level).removeBlock(worldPosition, false);
            level.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 5, Level.ExplosionInteraction.TNT);
        }

        // Горячие или коррозийные жидкости в пластиковой бочке
        if (b == ModBlocks.BARREL_PLASTIC.get() && (tank.getTankType().isCorrosive() || tank.getTankType().isHot())) {
            Objects.requireNonNull(level).removeBlock(worldPosition, false);
            level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        // Коррозия в железной или стальной бочке
        if ((b == ModBlocks.BARREL_IRON.get() && tank.getTankType().isCorrosive()) ||
                (b == ModBlocks.BARREL_STEEL.get() && tank.getTankType().hasTrait(FT_Corrosive.class) &&
                        tank.getTankType().getTrait(FT_Corrosive.class).getRating() > 50)) {

            ItemStack[] copy = new ItemStack[6];
            for (int i = 0; i < 6; i++) {
                copy[i] = itemHandler.getStackInSlot(i).copy();
            }

            Objects.requireNonNull(level).setBlock(worldPosition, ModBlocks.BARREL_CORRODED.get().defaultBlockState(), 3);
            TileEntityBarrel barrel = (TileEntityBarrel) level.getBlockEntity(worldPosition);

            if (barrel != null) {
                barrel.tank.setType(tank.getTankType());
                barrel.tank.setFill(Math.min(barrel.tank.getMaxFill(), tank.getFill()));
                for (int i = 0; i < 6; i++) {
                    barrel.itemHandler.setStackInSlot(i, copy[i]);
                }
            }

            level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        // Корродированная бочка течёт
        if (b == ModBlocks.BARREL_CORRODED.get()) {
            if (Objects.requireNonNull(level).random.nextInt(3) == 0) {
                tank.setFill(tank.getFill() - 1);
                FT_Polluting.pollute(level, worldPosition, tank.getTankType(), FluidReleaseType.SPILL, 1F);
            }
            if (level.random.nextInt(3 * 60 * 20) == 0) {
                level.removeBlock(worldPosition, false);
            }
        }
    }

    private void updateRedstoneConnection(BlockPos pos, Direction dir) {
        // Реализация зависит от вашей системы
    }

    public void loadFromItem(CompoundTag tag) {
        if (tag.contains("fill")) {
            tank.setFill(tag.getInt("fill"));
        }
        if (tag.contains("type")) {
            tank.setType(Fluids.fromName(tag.getString("type")));
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.barrel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerBarrel(containerId, playerInventory, this);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        mode = nbt.getShort("mode");
        tank.readFromNBT(nbt, "tank");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putShort("mode", mode);
        tank.writeToNBT(nbt, "tank");
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
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(Fluids.getID(tank.getTankType()));
        buf.writeInt(tank.getFill());
        buf.writeInt(tank.getMaxFill());
        buf.writeShort(mode);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        int typeId = buf.readInt();
        tank.setType(Fluids.fromID(typeId));
        tank.setFill(buf.readInt());
        buf.readInt();
        this.mode = buf.readShort();
    }

    @Override
    public boolean canConnect(FluidTypeHBM fluid, Direction dir) {
        return dir != null && fluid == this.tank.getTankType();
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return (mode == 1 || mode == 2) ? new FluidTankHBM[]{tank} : new FluidTankHBM[0];
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return (mode == 0 || mode == 1) ? new FluidTankHBM[]{tank} : new FluidTankHBM[0];
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tank};
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
    public void handleButtonPacket() {
        mode = (short) ((mode + 1) % modes);
        this.setChanged();
    }
}