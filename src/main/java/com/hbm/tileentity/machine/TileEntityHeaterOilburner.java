package com.hbm.tileentity.machine;

import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.api.tile.IHeatSource;
import com.hbm.interfaces.IControlReceiver;
import com.hbm.inventory.container.ContainerOilburner;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.Library.PosDir;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Flammable;
import com.hbm.inventory.fluid.trait.FluidTrait;

import com.hbm.tileentity.IFluidCopiable;
import com.hbm.tileentity.TileEntityMachinePolluting;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityHeaterOilburner extends TileEntityMachinePolluting implements MenuProvider, IFluidStandardTransceiver, IHeatSource, IFluidCopiable, IControlReceiver {

    public boolean isOn = false;
    public FluidTankHBM tank;
    public int setting = 1;

    public int heatEnergy;
    public static final int maxHeatEnergy = 100_000;

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<FluidTankHBM> fluidHandlerCap = LazyOptional.of(() -> tank);

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public TileEntityHeaterOilburner(BlockPos pos, BlockState state) {
        super(ModTileEntity.HEATER_OILBURNER.get(), pos, state, 100);
        this.tank = new FluidTankHBM(Fluids.HEATINGOIL.get(), 16000);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.heaterOilburner");
    }

    public PosDir[] getConPos() {
        return new PosDir[]{
                new PosDir(worldPosition.offset(2, 0, 0), Direction.EAST),
                new PosDir(worldPosition.offset(-2, 0, 0), Direction.WEST),
                new PosDir(worldPosition.offset(0, 0, 2), Direction.SOUTH),
                new PosDir(worldPosition.offset(0, 0, -2), Direction.NORTH)
        };
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            // Загрузка/выгрузка жидкости из слотов
            tank.loadTank(0, 1, slots(), itemHandler);
            tank.setType(2, 2,slots(), itemHandler);

            // Подписка на жидкости
            for (PosDir pos : this.getConPos()) {
                this.trySubscribe(tank.getTankType(), level, pos.pos(), pos.dir());
                this.sendSmoke(pos.pos(), pos.dir());
            }

            boolean shouldCool = true;

            if (this.isOn && this.heatEnergy < maxHeatEnergy) {
                FluidTypeHBM fluidType = tank.getTankType();
                if (fluidType.hasTrait(FT_Flammable.class)) {
                    FT_Flammable trait = fluidType.getTrait(FT_Flammable.class);

                    int burnRate = setting;
                    int toBurn = Math.min(burnRate, tank.getFluidAmount());

                    if (toBurn > 0) {
                        tank.drain(toBurn, IFluidHandler.FluidAction.EXECUTE);

                        int heat = (int) (trait.getHeatEnergy() / 1000);
                        this.heatEnergy += heat * toBurn;

                        if (level.getGameTime() % 5 == 0 && toBurn > 0) {
                            super.pollute(fluidType, FluidTrait.FluidReleaseType.BURN, toBurn * 5);
                        }

                        shouldCool = false;
                    }
                }
            }

            if (this.heatEnergy >= maxHeatEnergy) {
                shouldCool = false;
            }

            if (shouldCool) {
                this.heatEnergy = Math.max(this.heatEnergy - Math.max(this.heatEnergy / 1000, 1), 0);
            }

            this.networkPackNT(25);
        }
    }

    private ItemStack[]  slots() {

        ItemStack[] allSlots = new ItemStack[3];
        for (int i = 0; i < 3; i++) {
            allSlots[i] = itemHandler.getStackInSlot(i);
        }

        return allSlots;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(Fluids.getID(tank.getTankType()));
        buf.writeInt(tank.getFill());
        buf.writeInt(tank.getMaxFill());
        buf.writeBoolean(isOn);
        buf.writeInt(heatEnergy);
        buf.writeByte((byte) this.setting);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        int typeId = buf.readInt();
        tank.setType(Fluids.fromID(typeId));
        tank.setFill(buf.readInt());
        buf.readInt();
        this.isOn = buf.readBoolean();
        this.heatEnergy = buf.readInt();
        this.setting = buf.readByte();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        this.tank.readFromNBT(nbt, "tank");
        isOn = nbt.getBoolean("isOn");
        heatEnergy = nbt.getInt("heatEnergy");
        setting = nbt.getByte("setting");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        this.tank.writeToNBT(nbt, "tank");
        nbt.putBoolean("isOn", isOn);
        nbt.putInt("heatEnergy", heatEnergy);
        nbt.putByte("setting", (byte) setting);
    }

    public void toggleSetting() {
        setting++;
        if (setting > 10) setting = 1;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCap.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return LazyOptional.of(() -> tank).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCap.invalidate();
        fluidHandlerCap.invalidate();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerOilburner(windowId, inv, this);
    }


    @Override
    public int getHeatStored() {
        return heatEnergy;
    }

    @Override
    public void useUpHeat(int heat) {
        this.heatEnergy = Math.max(0, this.heatEnergy - heat);
    }

    private AABB renderBB = null;

    @Override
    public AABB getRenderBoundingBox() {
        if (renderBB == null) {
            renderBB = new AABB(
                    worldPosition.getX() - 1,
                    worldPosition.getY(),
                    worldPosition.getZ() - 1,
                    worldPosition.getX() + 2,
                    worldPosition.getY() + 2,
                    worldPosition.getZ() + 2
            );
        }
        return renderBB;
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return this.getSmokeTanks();
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public CompoundTag getSettings(Level level, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("fluidID", new int[]{Fluids.getID(tank.getTankType())});
        tag.putInt("burnRate", setting);
        tag.putBoolean("isOn", isOn);
        return tag;
    }

    @Override
    public void pasteSettings(CompoundTag nbt, int index, Level level, Player player, BlockPos pos) {
        int[] ids = nbt.getIntArray("fluidID");
        if (ids.length > index) {
            tank.setType(Fluids.fromID(ids[index]));
        }
        if (nbt.contains("isOn")) isOn = nbt.getBoolean("isOn");
        if (nbt.contains("burnRate")) setting = nbt.getInt("burnRate");
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && type == this.tank.getTankType();
    }

    @Override
    public boolean hasPermission(Player player) {
        BlockPos pos = worldPosition;
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 256;
    }

    @Override
    public void receiveControl(CompoundTag data) {
        if (data.contains("toggle")) {
            this.isOn = !this.isOn;
        }
        this.setChanged();
    }

    public void handleButtonPacket(int button, int data) {
        if (button == 0) { // кнопка включения/выключения
            this.isOn = !this.isOn;
            this.setChanged();
        }
    }
}