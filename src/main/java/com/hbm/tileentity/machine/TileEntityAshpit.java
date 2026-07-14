package com.hbm.tileentity.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.container.ContainerAshpit;
import com.hbm.items.DictFrame;
import com.hbm.items.ModItems;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.HBMEnums;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class TileEntityAshpit extends TileEntityMachineBase implements IConfigurableMachine, MenuProvider {

    private int playersUsing = 0;
    public float doorAngle = 0;
    public float prevDoorAngle = 0;
    public boolean isFull;

    public int ashLevelWood;
    public int ashLevelCoal;
    public int ashLevelMisc;
    public int ashLevelFly;
    public int ashLevelSoot;

    // Configurable values
    public static int thresholdWood = 2000;
    public static int thresholdCoal = 2000;
    public static int thresholdMisc = 2000;
    public static int thresholdFly = 2000;
    public static int thresholdSoot = 8000;

    // Инвентарь для пепельницы (5 слотов)
    protected ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityAshpit(BlockPos pos, BlockState state) {
        super(ModTileEntity.ASHPIT.get(), pos, state);
    }

    @Override
    public String getConfigName() {
        return "ashpit";
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        thresholdWood = IConfigurableMachine.grab(obj, "I:thresholdWood", thresholdWood);
        thresholdCoal = IConfigurableMachine.grab(obj, "I:thresholdCoal", thresholdCoal);
        thresholdMisc = IConfigurableMachine.grab(obj, "I:thresholdMisc", thresholdMisc);
        thresholdFly = IConfigurableMachine.grab(obj, "I:thresholdFly", thresholdFly);
        thresholdSoot = IConfigurableMachine.grab(obj, "I:thresholdSoot", thresholdSoot);
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("I:thresholdWood").value(thresholdWood);
        writer.name("I:thresholdCoal").value(thresholdCoal);
        writer.name("I:thresholdMisc").value(thresholdMisc);
        writer.name("I:thresholdFly").value(thresholdFly);
        writer.name("I:thresholdSoot").value(thresholdSoot);
    }

    public void onOpen() {
        if (level != null && !level.isClientSide) {
            this.playersUsing++;
        }
    }

    public void onClose() {
        if (level != null && !level.isClientSide) {
            this.playersUsing--;
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.ashpit");
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (processAsh(ashLevelWood, HBMEnums.EnumAshType.WOOD, thresholdWood)) ashLevelWood -= thresholdWood;
            if (processAsh(ashLevelCoal, HBMEnums.EnumAshType.COAL, thresholdCoal)) ashLevelCoal -= thresholdCoal;
            if (processAsh(ashLevelMisc, HBMEnums.EnumAshType.MISC, thresholdMisc)) ashLevelMisc -= thresholdMisc;
            if (processAsh(ashLevelFly, HBMEnums.EnumAshType.FLY, thresholdFly)) ashLevelFly -= thresholdFly;
            if (processAsh(ashLevelSoot, HBMEnums.EnumAshType.SOOT, thresholdSoot)) ashLevelSoot -= thresholdSoot;

            isFull = false;
            for (int i = 0; i < 5; i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) {
                    isFull = true;
                    break;
                }
            }

            this.networkPackNT(50);
        } else {
            this.prevDoorAngle = this.doorAngle;
            float swingSpeed = (doorAngle / 10F) + 3;

            if (this.playersUsing > 0) {
                this.doorAngle += swingSpeed;
            } else {
                this.doorAngle -= swingSpeed;
            }

            this.doorAngle = Mth.clamp(this.doorAngle, 0F, 135F);
        }
    }

    protected boolean processAsh(int level, HBMEnums.EnumAshType type, int threshold) {
        if (level >= threshold) {
            for (int i = 0; i < 5; i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.isEmpty()) {
                    inventory.setStackInSlot(i, DictFrame.fromOne(ModItems.POWDER_ASH.get(), type));
                    return true;
                } else if (stack.getCount() < stack.getMaxStackSize() &&
                        stack.getItem() == ModItems.POWDER_ASH.get()) {

                    // Получаем тип из CustomModelData
                    int stackType = 0;
                    if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("CustomModelData")) {
                        stackType = stack.getTag().getInt("CustomModelData");
                    }

                    if (stackType == type.ordinal()) {
                        stack.grow(1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(this.playersUsing);
        buf.writeBoolean(this.isFull);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.playersUsing = buf.readInt();
        this.isFull = buf.readBoolean();
    }


    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        this.ashLevelWood = nbt.getInt("ashLevelWood");
        this.ashLevelCoal = nbt.getInt("ashLevelCoal");
        this.ashLevelMisc = nbt.getInt("ashLevelMisc");
        this.ashLevelFly = nbt.getInt("ashLevelFly");
        this.ashLevelSoot = nbt.getInt("ashLevelSoot");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putInt("ashLevelWood", ashLevelWood);
        nbt.putInt("ashLevelCoal", ashLevelCoal);
        nbt.putInt("ashLevelMisc", ashLevelMisc);
        nbt.putInt("ashLevelFly", ashLevelFly);
        nbt.putInt("ashLevelSoot", ashLevelSoot);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCap.invalidate();
    }

    public IItemHandler getInventory() {
        return inventory;
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
                    worldPosition.getY() + 1,
                    worldPosition.getZ() + 2
            );
        }
        return renderBB;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        this.onOpen();
        return new ContainerAshpit(windowId, inv, this);
    }
}