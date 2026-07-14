package com.hbm.tileentity.machine;

import com.hbm.api.tile.IHeatSource;
import com.hbm.blocks.BlockDummyable;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;
import com.hbm.inventory.container.ContainerFurnaceSteel;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.BufferUtil;
import com.hbm.util.ItemStackUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
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

import java.util.List;
import java.util.Objects;

public class TileEntityFurnaceSteel extends TileEntityMachineBase implements MenuProvider {

    public int[] progress = new int[3];
    public int[] bonus = new int[3];
    public static final int processTime = 40_000;

    public int heat;
    public static final int maxHeat = 100_000;
    public static final double diffusion = 0.05D;

    private ItemStack[] lastItems = new ItemStack[3];

    public boolean wasOn = false;

    private final ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < 3) {
                return canSmelt(stack);
            }
            return false;
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityFurnaceSteel(BlockPos pos, BlockState state) {
        super(ModTileEntity.FURNACE_STEEL.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.furnaceSteel");
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            tryPullHeat();

            this.wasOn = false;

            int burn = (heat - TileEntityFurnaceSteel.maxHeat / 3) / 10;

            for (int i = 0; i < 3; i++) {
                ItemStack input = inventory.getStackInSlot(i);
                ItemStack output = inventory.getStackInSlot(i + 3);

                if (input.isEmpty() || lastItems[i] == null || !ItemStack.isSameItem(input, lastItems[i])) {
                    progress[i] = 0;
                    bonus[i] = 0;
                }

                if (canSmelt(i)) {
                    progress[i] += Math.max(burn, 0);
                    this.heat -= Math.max(burn, 0);
                    this.wasOn = true;
                    if (level.getGameTime() % 20 == 0) {
                        PollutionHandler.incrementPollution(level, worldPosition, PollutionType.SOOT, PollutionHandler.SOOT_PER_SECOND * 2);
                    }
                }

                lastItems[i] = input.isEmpty() ? null : input.copy();

                if (progress[i] >= processTime) {
                    ItemStack result = getSmeltingResult(input);

                    if (output.isEmpty()) {
                        inventory.setStackInSlot(i + 3, result.copy());
                    } else {
                        output.grow(result.getCount());
                    }

                    this.addBonus(input, i);

                    while (bonus[i] >= 100) {
                        output.grow(result.getCount());
                        bonus[i] -= 100;
                    }

                    input.shrink(1);
                    progress[i] = 0;
                }
            }

            this.networkPackNT(50);

        } else {
            if (this.wasOn) {
                Direction dir = getBlockState().getValue(BlockDummyable.FACING).getOpposite();
                Direction rot = dir.getClockWise();

                level.addParticle(ParticleTypes.SMOKE,
                        worldPosition.getX() + 0.5 - dir.getStepX() * 1.125 - rot.getStepX() * 0.75,
                        worldPosition.getY() + 2.625,
                        worldPosition.getZ() + 0.5 - dir.getStepZ() * 1.125 - rot.getStepZ() * 0.75,
                        0.0, 0.05, 0.0);

                if (level.random.nextInt(20) == 0) {
                    level.addParticle(ParticleTypes.CLOUD,
                            worldPosition.getX() + 0.5 + dir.getStepX() * 0.75,
                            worldPosition.getY() + 2,
                            worldPosition.getZ() + 0.5 + dir.getStepZ() * 0.75,
                            0.0, 0.05, 0.0);
                }

                if (level.random.nextInt(15) == 0) {
                    level.addParticle(ParticleTypes.LAVA,
                            worldPosition.getX() + 0.5 + dir.getStepX() * 1.5 + rot.getStepX() * (level.random.nextDouble() - 0.5),
                            worldPosition.getY() + 0.75,
                            worldPosition.getZ() + 0.5 + dir.getStepZ() * 1.5 + rot.getStepZ() * (level.random.nextDouble() - 0.5),
                            dir.getStepX() * 0.5, 0.05, dir.getStepZ() * 0.5);
                }
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        BufferUtil.writeIntArray(buf, this.progress);
        BufferUtil.writeIntArray(buf, this.bonus);
        buf.writeInt(this.heat);
        buf.writeBoolean(this.wasOn);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.progress = BufferUtil.readIntArray(buf);
        this.bonus = BufferUtil.readIntArray(buf);
        this.heat = buf.readInt();
        this.wasOn = buf.readBoolean();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));

        this.progress = nbt.getIntArray("progress");
        if (this.progress.length < 3) this.progress = new int[3];
        this.bonus = nbt.getIntArray("bonus");
        if (this.bonus.length < 3) this.bonus = new int[3];
        this.heat = nbt.getInt("heat");

        ListTag list = nbt.getList("lastItems", Tag.TAG_COMPOUND);
        this.lastItems = new ItemStack[3];
        for (int i = 0; i < list.size(); i++) {
            CompoundTag nbt1 = list.getCompound(i);
            byte slot = nbt1.getByte("slot");
            if (slot >= 0 && slot < lastItems.length) {
                lastItems[slot] = ItemStack.of(nbt1);
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putIntArray("progress", progress);
        nbt.putIntArray("bonus", bonus);
        nbt.putInt("heat", heat);

        ListTag list = new ListTag();
        for (int i = 0; i < lastItems.length; i++) {
            if (lastItems[i] != null && !lastItems[i].isEmpty()) {
                CompoundTag nbt1 = new CompoundTag();
                nbt1.putByte("slot", (byte) i);
                lastItems[i].save(nbt1);
                list.add(nbt1);
            }
        }
        nbt.put("lastItems", list);
    }

    protected void addBonus(ItemStack stack, int index) {
        List<String> names = ItemStackUtil.getOreDictNames(stack);

        for (String name : names) {
            if (name.startsWith("ore")) {
                this.bonus[index] += 25;
                return;
            }
            if (name.startsWith("log")) {
                this.bonus[index] += 50;
                return;
            }
            if (name.equals("anyTar")) {
                this.bonus[index] += 50;
                return;
            }
        }
    }

    protected void tryPullHeat() {
        if (this.heat >= TileEntityFurnaceSteel.maxHeat) return;

        BlockEntity con = Objects.requireNonNull(level).getBlockEntity(worldPosition.below());

        if (con instanceof IHeatSource source) {
            int diff = source.getHeatStored() - this.heat;

            if (diff == 0) {
                return;
            }

            if (diff > 0) {
                diff = (int) Math.ceil(diff * diffusion);
                source.useUpHeat(diff);
                this.heat += diff;
                if (this.heat > TileEntityFurnaceSteel.maxHeat)
                    this.heat = TileEntityFurnaceSteel.maxHeat;
                return;
            }
        }

        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }

    public boolean canSmelt(int index) {
        if (this.heat < TileEntityFurnaceSteel.maxHeat / 3) return false;
        ItemStack input = inventory.getStackInSlot(index);
        if (input.isEmpty()) return false;

        ItemStack result = getSmeltingResult(input);
        if (result.isEmpty()) return false;

        ItemStack output = inventory.getStackInSlot(index + 3);
        if (output.isEmpty()) return true;

        if (!ItemStack.isSameItem(output, result)) return false;
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private boolean canSmelt(ItemStack stack) {
        return !getSmeltingResult(stack).isEmpty();
    }

    private ItemStack getSmeltingResult(ItemStack stack) {
        if (level == null) return ItemStack.EMPTY;

        var recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING);

        for (var recipe : recipes) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return recipe.getResultItem(level.registryAccess());
            }
        }

        return ItemStack.EMPTY;
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerFurnaceSteel(windowId, inv, player, this);
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
                    worldPosition.getY() + 3,
                    worldPosition.getZ() + 2
            );
        }
        return renderBB;
    }
}