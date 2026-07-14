package com.hbm.tileentity.machine;

import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.blocks.BlockDummyable;
import com.hbm.handler.pollution.PollutionHandler;

import com.hbm.handler.pollution.PollutionType;
import com.hbm.inventory.container.ContainerMachineRotaryFurnace;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.inventory.recipes.RotaryFurnaceRecipes;
import com.hbm.inventory.recipes.RotaryFurnaceRecipes.RotaryFurnaceRecipe;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.module.ModuleBurnTime;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.*;
import com.hbm.util.CrucibleUtil;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TileEntityMachineRotaryFurnace extends TileEntityMachinePolluting
        implements IFluidStandardTransceiver, IFluidCopiable, MenuProvider {

    public FluidTankHBM[] tanks;
    public boolean isProgressing;
    public float progress;
    public int burnTime;
    public double burnHeat = 1D;
    public int maxBurnTime;
    public int steamUsed = 0;
    public boolean isVenting;
    public Mats.MaterialStack output;
    public static final int maxOutput = 16 * 144; // MaterialShapes.BLOCK.q(16) = 16 * 144 = 2304

    public int anim;
    public int lastAnim;

    public static ModuleBurnTime burnModule = new ModuleBurnTime()
            .setCokeTimeMod(1.25)
            .setRocketTimeMod(1.5)
            .setSolidTimeMod(1.5)
            .setBalefireTimeMod(1.5)
            .setSolidHeatMod(1.5)
            .setRocketHeatMod(3)
            .setBalefireHeatMod(10);

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < 3 || slot == 4) return true;
            if (slot == 3) return stack.getItem() instanceof IItemFluidIdentifier;
            return false;
        }
    };

    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> itemHandler);

    public TileEntityMachineRotaryFurnace(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_ROTARY_FURNACE.get(), pos, state, 50);
        tanks = new FluidTankHBM[3];
        tanks[0] = new FluidTankHBM(Fluids.NONE.get(), 16_000);
        tanks[1] = new FluidTankHBM(Fluids.STEAM.get(), 12_000);
        tanks[2] = new FluidTankHBM(Fluids.SPENTSTEAM.get(), 120);
    }

    public PosDir[] getSteamPos() {
        Direction dir = getBlockState().getValue(BlockDummyable.FACING);
        Direction right = dir.getClockWise();
        Direction forward = dir.getOpposite();

        return new PosDir[]{
                new PosDir(worldPosition.relative(forward, -2).relative(right, -2), forward),
                new PosDir(worldPosition.relative(forward, -2).relative(right, -1), forward)
        };
    }

    public PosDir[] getFluidPos() {
        Direction dir = getBlockState().getValue(BlockDummyable.FACING);
        Direction right = dir.getClockWise();
        Direction forward = dir.getOpposite();

        return new PosDir[]{
                new PosDir(worldPosition.relative(forward, 1).relative(right, 3), right.getOpposite()),
                new PosDir(worldPosition.relative(forward, -1).relative(right, 3), right.getOpposite())
        };
    }

    public void tick() {
        if (level == null) return;

        Direction dir = getBlockState().getValue(BlockDummyable.FACING);
        Direction right = dir.getClockWise();

        if (!level.isClientSide) {
            // Обновление типа жидкости из слота идентификатора
            if (!itemHandler.getStackInSlot(3).isEmpty()) {
                tanks[0].setType(3, 3, getSlots(), itemHandler);
            }

            // Подписка на пар и отправка отработанного пара
            for (PosDir pos : getSteamPos()) {
                this.trySubscribe(tanks[1].getTankType(), level, pos.pos(), pos.dir());
                if (tanks[2].getFill() > 0) {
                    this.sendFluid(tanks[2], level, pos.pos(), pos.dir());
                }
            }

            // Подписка на жидкость из рецепта
            if (tanks[0].getTankType() != Fluids.NONE.get()) {
                for (PosDir pos : getFluidPos()) {
                    this.trySubscribe(tanks[0].getTankType(), level, pos.pos(), pos.dir());
                }
            }

            // Отправка дыма
            if (smoke.getFill() > 0) {
                this.sendFluid(smoke, level, worldPosition.relative(right, 1).above(4), Direction.UP);
            }

            // Выливание output
            if (this.output != null) {
                int prev = this.output.amount;
                Vec3[] impact = new Vec3[1];  // массив из одного элемента
                double pourX = worldPosition.getX() + 0.5 + right.getStepX() * 2.875;
                double pourY = worldPosition.getY() + 1.25;
                double pourZ = worldPosition.getZ() + 0.5 + right.getStepZ() * 2.875;

                Mats.MaterialStack leftover = CrucibleUtil.pourSingleStack(
                        level,
                        pourX, pourY, pourZ,
                        6, true, this.output, MaterialShapes.INGOT.q(1), impact
                );

                if (leftover != this.output) {
                    this.output = leftover;
                    this.setChanged();

                    double length = Math.max(1.0, worldPosition.getY() + 1.25 - (Math.ceil(impact[0].y) - 1.125)) - 1;
                    // Отправляем частицы только при реальном выливании
                    CompoundTag data = new CompoundTag();
                    data.putString("type", "foundry");
                    data.putInt("color", this.output != null ? this.output.material.moltenColor : 0);
                    data.putByte("dir", (byte) right.ordinal());
                    data.putFloat("off", 0.625F);
                    data.putFloat("base", 0.625F);
                    data.putFloat("len", (float) length);

                    double px = worldPosition.getX() + 0.5 + right.getStepX() * 2.875;
                    double py = worldPosition.getY() + 0.75;
                    double pz = worldPosition.getZ() + 0.5 + right.getStepZ() * 2.875;

                    PacketDispatcher.sendToAllAround(
                            new AuxParticlePacketNT(data, px, py, pz),
                            level, worldPosition, 50
                    );
                }

                if (this.output != null && this.output.amount <= 0) this.output = null;
            }

            // Рецепты
            RotaryFurnaceRecipe recipe = RotaryFurnaceRecipes.getRecipe(getSlots()[0], getSlots()[1], getSlots()[2]);
            this.isProgressing = false;

            if (recipe != null) {
                ItemStack fuelStack = itemHandler.getStackInSlot(4);

                if (this.burnTime <= 0 && !fuelStack.isEmpty() && isItemFuel(fuelStack)) {
                    this.burnHeat = burnModule.getMod(fuelStack, burnModule.getModHeat());
                    this.maxBurnTime = this.burnTime = burnModule.getBurnTime(fuelStack) / 2;
                    itemHandler.extractItem(4, 1, false);
                    this.setChanged();
                }

                if (this.canProcess(recipe)) {
                    float speed = Math.max((float) burnHeat, 1);
                    this.progress += speed / recipe.duration;

                    speed = (float) (13 * Math.log10(speed) + 1);
                    tanks[1].setFill((int) (tanks[1].getFill() - recipe.steam * speed));
                    steamUsed += (int) (recipe.steam * speed);
                    this.isProgressing = true;

                    if (this.progress >= 1F) {
                        this.progress -= 1F;
                        this.consumeItems(recipe);

                        if (this.output == null) {
                            this.output = recipe.output.copy();
                        } else {
                            this.output.amount += recipe.output.amount;
                        }
                        this.setChanged();
                    }

                    if (this.burnTime > 0) {
                        this.pollute(PollutionType.SOOT, PollutionHandler.SOOT_PER_SECOND / 10F);
                        this.burnTime--;
                    }
                } else {
                    this.progress = 0;
                }

                if (this.steamUsed >= 100) {
                    int steamReturn = this.steamUsed / 100;
                    int canReturn = tanks[2].getMaxFill() - tanks[2].getFill();
                    int doesReturn = Math.min(steamReturn, canReturn);
                    this.steamUsed -= doesReturn * 100;
                    tanks[2].setFill(tanks[2].getFill() + doesReturn);
                }
            } else {
                this.progress = 0;
            }

            if (level.getGameTime() % 2 == 0) {
                int xOffset = 0;
                int zOffset = 0;
                switch (dir){
                    case NORTH:
                        xOffset = 1;
                        break;
                    case SOUTH:
                        xOffset = -1;
                        break;
                    case EAST:
                        zOffset = 1;
                        break;
                    case WEST:
                        zOffset = -1;

                }

                CompoundTag fx = new CompoundTag();
                fx.putString("type", "tower");
                fx.putFloat("lift", 10F);
                fx.putFloat("base", 0.25F);
                fx.putFloat("max", 2.5F);
                fx.putInt("life", 100 + level.random.nextInt(20));
                fx.putInt("color", 0x202020);
                fx.putDouble("posX", worldPosition.getX() + 0.5 + right.getStepX());
                fx.putDouble("posY", worldPosition.getY() + 1);
                fx.putDouble("posZ", worldPosition.getZ() + 0.5 + right.getStepZ());
                PacketDispatcher.sendToAllAround(
                        new AuxParticlePacketNT(fx, worldPosition.getX() + 0.5 + xOffset, worldPosition.getY() + 5, worldPosition.getZ() + 0.5 + zOffset),
                        level, worldPosition, 50);
            }

            this.isVenting = false;

            this.networkPackNT(50);

        } else {
            // Клиентская часть
            if (this.burnTime > 0 && Objects.requireNonNull(Minecraft.getInstance().player).distanceToSqr(
                    worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) < 625) {
                RandomSource rand = level.random;
                level.addParticle(ParticleTypes.FLAME,
                        worldPosition.getX() + 0.5 - dir.getStepX() + right.getStepX() + rand.nextGaussian() * 0.25,
                        worldPosition.getY() + 0.375,
                        worldPosition.getZ() + 0.5 + dir.getStepZ() * 0.5 + right.getStepZ() + rand.nextGaussian() * 0.25,
                        0, 0, 0);
            }



            this.lastAnim = this.anim;
            if (this.isProgressing) {
                ItemStack fuelStack = itemHandler.getStackInSlot(4);
                this.anim += (int) Math.max(burnModule.getMod(fuelStack, burnModule.getModHeat()), 1);
            }
        }
    }

    private ItemStack[] getSlots() {
        ItemStack[] slots = new ItemStack[5];
        for (int i = 0; i < 5; i++) {
            slots[i] = itemHandler.getStackInSlot(i);
        }
        return slots;
    }

    private boolean isItemFuel(ItemStack stack) {
        return stack.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0;
    }

    public boolean canProcess(RotaryFurnaceRecipe recipe) {
        if (this.burnTime <= 0) return false;

        if (recipe.fluid != null) {
            if (this.tanks[0].getTankType() != recipe.fluid.type) return false;
            if (this.tanks[0].getFill() < recipe.fluid.fill) return false;
        }

        float speed = Math.max((float) burnHeat, 1);
        if (tanks[1].getFill() < recipe.steam * speed) return false;
        if (tanks[2].getMaxFill() - tanks[2].getFill() < recipe.steam * speed / 100) return false;
        if (this.steamUsed > 100) return false;


        if (this.output != null) {
            if (this.output.material != recipe.output.material) return false;
            return this.output.amount + recipe.output.amount <= maxOutput;
        }
        return true;
    }

    public void consumeItems(RotaryFurnaceRecipe recipe) {
        for (AStack aStack : recipe.ingredients) {
            for (int i = 0; i < 3; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (aStack.matchesRecipe(stack, true) && stack.getCount() >= aStack.stacksize) {
                    itemHandler.extractItem(i, aStack.stacksize, false);
                    break;
                }
            }
        }
        if (recipe.fluid != null) {
            tanks[0].setFill(tanks[0].getFill() - recipe.fluid.fill);
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        tanks[0].serialize(buf);
        tanks[1].serialize(buf);
        tanks[2].serialize(buf);
        buf.writeBoolean(isVenting);
        buf.writeBoolean(isProgressing);
        buf.writeFloat(progress);
        buf.writeInt(burnTime);
        buf.writeInt(maxBurnTime);
        if (this.output != null) {
            buf.writeBoolean(true);
            buf.writeInt(this.output.material.id);
            buf.writeInt(this.output.amount);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        tanks[0].deserialize(buf);
        tanks[1].deserialize(buf);
        tanks[2].deserialize(buf);
        isVenting = buf.readBoolean();
        isProgressing = buf.readBoolean();
        progress = buf.readFloat();
        burnTime = buf.readInt();
        maxBurnTime = buf.readInt();
        if (buf.readBoolean()) {
            NTMMaterial mat = Mats.matById.get(buf.readInt());
            int amount = buf.readInt();
            this.output = new Mats.MaterialStack(mat, amount);
        } else {
            this.output = null;
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        tanks[0].readFromNBT(nbt, "t0");
        tanks[1].readFromNBT(nbt, "t1");
        tanks[2].readFromNBT(nbt, "t2");
        progress = nbt.getFloat("prog");
        burnTime = nbt.getInt("burn");
        burnHeat = nbt.getDouble("heat");
        maxBurnTime = nbt.getInt("maxBurn");
        if (nbt.contains("outType")) {
            NTMMaterial mat = Mats.matById.get(nbt.getInt("outType"));
            int outAmount = nbt.getInt("outAmount");
            this.output = new Mats.MaterialStack(mat, outAmount);
        } else {
            this.output = null;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        tanks[0].writeToNBT(nbt, "t0");
        tanks[1].writeToNBT(nbt, "t1");
        tanks[2].writeToNBT(nbt, "t2");
        nbt.putFloat("prog", progress);
        nbt.putInt("burn", burnTime);
        nbt.putDouble("heat", burnHeat);
        nbt.putInt("maxBurn", maxBurnTime);
        if (this.output != null) {
            nbt.putInt("outType", this.output.material.id);
            nbt.putInt("outAmount", this.output.amount);
        }
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tanks[0], tanks[1], tanks[2], smoke};
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[]{tanks[2], smoke};
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tanks[0], tanks[1]};
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && (type == tanks[0].getTankType()
                || type == tanks[1].getTankType()
                || type == tanks[2].getTankType());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 2,
                worldPosition.getY(),
                worldPosition.getZ() - 2,
                worldPosition.getX() + 3,
                worldPosition.getY() + 5,
                worldPosition.getZ() + 3
        );
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerMachineRotaryFurnace(windowId, playerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.machine_rotary_furnace");
    }
}