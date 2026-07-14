package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.container.ContainerMachineGasCent;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.recipes.GasCentrifugeRecipes;
import com.hbm.inventory.recipes.GasCentrifugeRecipes.PseudoFluidType;
import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.main.MainRegistry;
import com.hbm.sound.AudioWrapper;
import com.hbm.sound.ModSounds;
import com.hbm.sound.SoundHelper;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.BufferUtil;

import com.hbm.util.InventoryUtil;
import com.hbm.util.Library;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TileEntityMachineGasCent extends TileEntityMachineBase implements MenuProvider, IEnergyReceiver, IFluidStandardReceiver{

    private long power;
    public int progress;
    private boolean isProgressing;
    private int audioDuration = 0;
    private AudioWrapper audio;
    private static final long MAX_POWER = 100_000;
    private static final int PROCESSING_SPEED_NORMAL = 150;
    private static final int PROCESSING_SPEED_FAST = 80;

    // Танки
    public FluidTankHBM tank;          // реальная жидкость (например, UF6)
    public PseudoFluidTank inputTank;  // псевдо-жидкость на входе (NUF6)
    public PseudoFluidTank outputTank; // псевдо-жидкость на выходе (LEUF6)

    // Слоты: 0-3 выходы, 4 батарея, 5 идентификатор жидкости, 6 улучшение скорости
    private final ItemStackHandler inventory = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMachineGasCent(BlockPos pos, BlockState state) {
        super(ModTileEntity.GAS_CENTRIFUGE.get(), pos, state);
        this.tank = new FluidTankHBM(Fluids.UF6.get(), 2000);
        this.inputTank = new PseudoFluidTank(PseudoFluidType.NUF6, 8000);
        this.outputTank = new PseudoFluidTank(PseudoFluidType.LEUF6, 8000);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.gasCentrifuge");
    }

    // ---------- Энергия ----------
    @Override
    public long getPower() { return power; }
    @Override
    public void setPower(long power) { this.power = power; }
    @Override
    public long getMaxPower() { return MAX_POWER; }
    @Override
    public long getReceiverSpeed() { return getProcessingSpeed() * 2L; }

    @Override
    public long transferPower(long power) {
        long newPower = this.power + power;
        if (newPower <= MAX_POWER) {
            this.power = newPower;
            return 0;
        }
        long received = MAX_POWER - this.power;
        this.power = MAX_POWER;
        return power - received;
    }

    private void updateConnections() {
        for (PosDir pos : getConPos()) {
            // энергия (потребление)
            this.trySubscribe(level, pos.pos(), pos.dir());
            // жидкость (приём реальной жидкости)
            if (GasCentrifugeRecipes.fluidConversions.containsKey(tank.getTankType())) {
                this.trySubscribe(tank.getTankType(), level, pos.pos(), pos.dir());
            }
        }
    }

    private PosDir[] getConPos() {
        return new PosDir[]{
                new PosDir(worldPosition.below(), Direction.DOWN),
                new PosDir(worldPosition.offset(1, 0, 0), Direction.EAST),
                new PosDir(worldPosition.offset(-1, 0, 0), Direction.WEST),
                new PosDir(worldPosition.offset(0, 0, 1), Direction.SOUTH),
                new PosDir(worldPosition.offset(0, 0, -1), Direction.NORTH)
        };
    }

    // ---------- Жидкости (IFluidStandardReceiver) ----------
    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return type == tank.getTankType() && dir != null;
    }

    // ---------- Логика центрифуги ----------
    public int getProcessingSpeed() {
        ItemStack speedUpgrade = inventory.getStackInSlot(6);
        if (!speedUpgrade.isEmpty() && speedUpgrade.getItem() == ModItems.UPGRADE_GC_SPEED.get()) {
            return PROCESSING_SPEED_FAST;
        }
        return PROCESSING_SPEED_NORMAL;
    }

    private boolean canEnrich() {
        if (power <= 0) return false;
        PseudoFluidType type = inputTank.getTankType();
        if (type == PseudoFluidType.NONE) return false;
        if (inputTank.getFill() < type.getFluidConsumed()) return false;
        if (outputTank.getFill() + type.getFluidProduced() > outputTank.getMaxFill()) return false;

        // проверка улучшения скорости
        if (type.getIfHighSpeed() && getProcessingSpeed() != PROCESSING_SPEED_FAST) return false;

        ItemStack[] outputs = type.getOutput();

        if (outputs == null || outputs.length == 0) return false;

        return InventoryUtil.doesArrayHaveSpace(getItemHandler(), 0, 3, outputs);
    }

    private void enrich() {
        PseudoFluidType type = inputTank.getTankType();
        inputTank.setFill(inputTank.getFill() - type.getFluidConsumed());
        outputTank.setFill(outputTank.getFill() + type.getFluidProduced());

        ItemStack[] outputs = type.getOutput();
        for (ItemStack out : outputs) {
            InventoryUtil.tryAddItemToInventory(getItemHandler(), 0, 3, out.copy());
        }
        progress = 0;
    }

    private void attemptConversion() {
        // преобразование реальной жидкости в псевдо-жидкость
        if (inputTank.getFill() < inputTank.getMaxFill() && tank.getFluidAmount() > 0) {
            int toConvert = Math.min(inputTank.getMaxFill() - inputTank.getFill(), tank.getFluidAmount());
            tank.setFill(tank.getFill() - toConvert);
            inputTank.setFill(inputTank.getFill() + toConvert);
        }
    }

    private boolean attemptTransfer(BlockEntity neighbor) {
        if (neighbor instanceof TileEntityMachineGasCent other) {
            if (other.tank.getTankType() == tank.getTankType()) {
                // синхронизация псевдо-типов
                if (other.inputTank.getTankType() != outputTank.getTankType() && outputTank.getTankType() != PseudoFluidType.NONE) {
                    other.inputTank.setTankType(outputTank.getTankType());
                    other.outputTank.setTankType(outputTank.getTankType().getOutputType());
                }
                // перекачка псевдо-жидкости
                if (other.inputTank.getFill() < other.inputTank.getMaxFill() && outputTank.getFill() > 0) {
                    int transfer = Math.min(other.inputTank.getMaxFill() - other.inputTank.getFill(), outputTank.getFill());
                    outputTank.setFill(outputTank.getFill() - transfer);
                    other.inputTank.setFill(other.inputTank.getFill() + transfer);
                }
                return true;
            }
        }
        return false;
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            updateConnections();

            power = Library.chargeTEFromItems(inventory, 4, power, MAX_POWER);
            setTankType(5);

            if (GasCentrifugeRecipes.fluidConversions.containsValue(inputTank.getTankType())) {
                 attemptConversion();
            }

            if (canEnrich()) {
                isProgressing = true;
                progress++;

                if (inventory.getStackInSlot(6).getItem() == ModItems.UPGRADE_GC_SPEED.get())
                    power -= 300;
                else
                    power -= 200;

                if (power < 0) {
                    power = 0;
                    progress = 0;
                }

                if (progress >= getProcessingSpeed())
                    enrich();

            } else {
                isProgressing = false;
                progress = 0;
            }

            if (level.getGameTime() % 10 == 0) {
                Direction dir = getFacing();
                BlockPos neighborPos = worldPosition.relative(dir);
                BlockEntity neighbor = level.getBlockEntity(neighborPos);

                if (!attemptTransfer(neighbor) && inputTank.getTankType() == PseudoFluidType.LEUF6) {
                    ItemStack[] converted = new ItemStack[]{
                            new ItemStack(ModItems.NUGGET_URANIUM_FUEL.get(), 6),
                            new ItemStack(ModItems.FLUORITE.get())
                    };

                    if (outputTank.getFill() >= 600 && InventoryUtil.doesArrayHaveSpace(getItemHandler(), 0, 3, converted)) {
                        outputTank.setFill(outputTank.getFill() - 600);
                        for (ItemStack stack : converted)
                            InventoryUtil.tryAddItemToInventory(getItemHandler(), 0, 3, stack);
                    }
                }
            }

            this.networkPackNT(50);

        } else {
            // Клиентская сторона - звук
            if (isProgressing) {
                audioDuration += 2;
            } else {
                audioDuration -= 3;
            }

            audioDuration = Mth.clamp(audioDuration, 0, 60);

            if (audioDuration > 10 && MainRegistry.proxy.me()
                    .distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) < 625) {
                if (audio == null) {
                    audio = createAudioLoop();
                    audio.startSound();
                } else if (!audio.isPlaying()) {
                    audio = rebootAudio(audio);
                }

                audio.updateVolume(getVolume(1F));
                audio.updatePitch((audioDuration - 10) / 100F + 0.5F);
                audio.keepAlive();
            } else {
                if (audio != null) {
                    audio.stopSound();
                    audio = null;
                }
            }
        }
    }

    public AudioWrapper createAudioLoop() {
        return SoundHelper.createLoopedSound(ModSounds.CENTRIFUGE_OPERATE.get(),
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                1.0F, 10F, 1.0F, 20);
    }

    public AudioWrapper rebootAudio(AudioWrapper wrapper) {
        wrapper.stopSound();
        return createAudioLoop();
    }

    public float getVolume(float base) {
        return base;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeInt(progress);
        buf.writeBoolean(isProgressing);
        buf.writeInt(inputTank.getFill());
        buf.writeInt(outputTank.getFill());
        BufferUtil.writeString(buf, inputTank.getTankType().name);
        BufferUtil.writeString(buf, outputTank.getTankType().name);
        tank.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        progress = buf.readInt();
        isProgressing = buf.readBoolean();
        inputTank.setFill(buf.readInt());
        outputTank.setFill(buf.readInt());
        inputTank.setTankType(PseudoFluidType.types.get(BufferUtil.readString(buf)));
        outputTank.setTankType(PseudoFluidType.types.get(BufferUtil.readString(buf)));
        tank.deserialize(buf);
    }

    private Direction getFacing() {
        BlockState state = Objects.requireNonNull(level).getBlockState(worldPosition);
        if (state.getBlock() instanceof BlockDummyable) {
            return state.getValue(BlockDummyable.FACING);
        }
        return Direction.NORTH;
    }

    private void setTankType(int slot) {
        ItemStack stack = inventory.getStackInSlot(slot);
        if (!stack.isEmpty() && stack.getItem() instanceof IItemFluidIdentifier identifier) {
            FluidTypeHBM type = identifier.getType(level, worldPosition, stack);
            if (type != null && tank.getTankType() != type) {
                PseudoFluidType pseudo = GasCentrifugeRecipes.fluidConversions.get(type);
                if (pseudo != null) {
                    inputTank.setTankType(pseudo);
                    outputTank.setTankType(pseudo.getOutputType());
                    tank.setType(type);
                }
            }
        }
    }


    // ---------- NBT ----------
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        power = nbt.getLong("power");
        progress = nbt.getInt("progress");
        isProgressing = nbt.getBoolean("progressing");
        tank.readFromNBT(nbt, "tank");
        inputTank.readFromNBT(nbt, "inputTank");
        outputTank.readFromNBT(nbt, "outputTank");
        inventory.deserializeNBT(nbt.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
        nbt.putInt("progress", progress);
        nbt.putBoolean("progressing", isProgressing);
        tank.writeToNBT(nbt, "tank");
        inputTank.writeToNBT(nbt, "inputTank");
        outputTank.writeToNBT(nbt, "outputTank");
        nbt.put("inventory", inventory.serializeNBT());
    }

    // ---------- Capabilities ----------
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return LazyOptional.of(() -> tank).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCap.invalidate();
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    // ---------- GUI ----------
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerMachineGasCent(windowId, inv, this);
    }

    // ---------- Вспомогательный внутренний класс для псевдо-баков ----------
    public static class PseudoFluidTank {
        private PseudoFluidType type;
        private int fill;
        private final int maxFill;

        public PseudoFluidTank(PseudoFluidType type, int maxFill) {
            this.type = type;
            this.maxFill = maxFill;
            this.fill = 0;
        }

        public void setFill(int fill) { this.fill = Math.min(maxFill, Math.max(0, fill)); }
        public void setTankType(PseudoFluidType type) {
            if (type == null) type = PseudoFluidType.NONE;
            if (this.type == type) return;
            this.type = type;
            this.fill = 0;
        }
        public PseudoFluidType getTankType() { return type; }
        public int getFill() { return fill; }
        public int getMaxFill() { return maxFill; }

        public void writeToNBT(CompoundTag nbt, String tag) {
            nbt.putInt(tag, fill);
            nbt.putString(tag + "_type", type.name);
        }
        public void readFromNBT(CompoundTag nbt, String tag) {
            fill = nbt.getInt(tag);
            String tName = nbt.getString(tag + "_type");
            type = PseudoFluidType.types.get(tName);
            if (type == null) type = PseudoFluidType.NONE;
        }
    }

    // ---------- Rendering bounding box ----------
    private AABB renderBox;
    @Override
    public AABB getRenderBoundingBox() {
        if (renderBox == null) {
            renderBox = new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                    worldPosition.getX() + 1, worldPosition.getY() + 3, worldPosition.getZ() + 1);
        }
        return renderBox;
    }
}