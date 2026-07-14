package com.hbm.tileentity.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.handler.pollution.PollutionHandler;

import com.hbm.handler.pollution.PollutionType;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.container.ContainerFurnaceIron;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.module.ModuleBurnTime;
import com.hbm.tileentity.IUpgradeInfoProvider;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class TileEntityFurnaceIron extends TileEntityMachineBase implements MenuProvider, IUpgradeInfoProvider{

    public int maxBurnTime;
    public int burnTime;
    public boolean wasOn = false;

    public int progress;
    public int processingTime;
    public static final int baseTime = 160;

    public ModuleBurnTime burnModule;

    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();

    private final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return getSmeltingResult(stack) != null;
            }
            if (slot < 3) {
                return burnModule.getBurnTime(stack) > 0;
            }
            return slot == 4; // upgrade slot
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityFurnaceIron(BlockPos pos, BlockState state) {
        super(ModTileEntity.FURNACE_IRON.get(), pos, state);
        burnModule = new ModuleBurnTime()
                .setLigniteTimeMod(1.25)
                .setCoalTimeMod(1.25)
                .setCokeTimeMod(1.5)
                .setSolidTimeMod(2)
                .setRocketTimeMod(2)
                .setBalefireTimeMod(2);
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.furnaceIron");
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            upgradeManager.checkSlots(this, inventory, 4, 4);
            this.processingTime = baseTime - ((baseTime / 2) * upgradeManager.getLevel(UpgradeType.SPEED) / 3);

            wasOn = false;

            if (burnTime <= 0) {
                for (int i = 1; i < 3; i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        int fuel = burnModule.getBurnTime(stack);

                        if (fuel > 0) {
                            this.maxBurnTime = this.burnTime = fuel;
                            stack.shrink(1);

                            if (stack.isEmpty()) {
                                inventory.setStackInSlot(i, stack.getCraftingRemainingItem());
                            }

                            break;
                        }
                    }
                }
            }

            if (canSmelt()) {
                wasOn = true;
                this.progress++;
                this.burnTime--;

                if (this.progress % 15 == 0 && !this.muffled) {
                    level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.FIRE_AMBIENT, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.5F + level.random.nextFloat() * 0.5F);
                }

                if (this.progress >= this.processingTime) {
                    ItemStack input = inventory.getStackInSlot(0);
                    ItemStack result = getSmeltingResult(input);

                    ItemStack output = inventory.getStackInSlot(3);
                    if (output.isEmpty()) {
                        inventory.setStackInSlot(3, result.copy());
                    } else {
                        output.grow(result.getCount());
                    }

                    input.shrink(1);
                    this.progress = 0;
                }
                if (level.getGameTime() % 20 == 0) {
                    PollutionHandler.incrementPollution(level, worldPosition, PollutionType.SOOT, PollutionHandler.SOOT_PER_SECOND);
                }
            } else {
                this.progress = 0;
            }

            this.networkPackNT(50);
        } else {
            if (this.progress > 0) {
                Direction dir = getBlockState().getValue(BlockDummyable.FACING).getOpposite();
                Direction rot = dir.getClockWise();

                double offset = this.progress % 2 == 0 ? 1 : 0.5;

                double baseX = worldPosition.getX() + 0.5;
                double baseZ = worldPosition.getZ() + 0.5;
                double baseY = worldPosition.getY();

                switch (dir) {
                    case NORTH: baseX -= 1.0; baseZ -= 1.0; break;
                    case SOUTH: baseX += 1.0; baseZ += 1.0; break;
                    case WEST:  baseX -= 1.0; baseZ += 1.0; break;
                    case EAST:  baseX += 1.0; baseZ -= 1.0; break;
                }

                // Дым
                level.addParticle(ParticleTypes.SMOKE,
                        baseX - dir.getStepX() * offset - rot.getStepX() * 0.1875,
                        baseY + 2,
                        baseZ - dir.getStepZ() * offset - rot.getStepZ() * 0.1875,
                        0.0, 0.01, 0.0);


                switch (dir) {
                    case NORTH:
                        baseX += 1.0;
                        break;
                    case SOUTH:
                        baseX -= 1.0;
                        break;
                    case WEST:
                        baseZ -= 1.0;
                        break;
                    case EAST:
                        baseZ += 1.0;
                        break;
                }

                // Пламя (пока оставим как есть)
                if (this.progress % 5 == 0) {
                    double rand = level.random.nextDouble();
                    level.addParticle(ParticleTypes.FLAME,
                            baseX + dir.getStepX() * 0.35 - rot.getStepX() * rand,
                            baseY + 0.25 + level.random.nextDouble() * 0.25,
                            baseZ + dir.getStepZ() * 0.35 - rot.getStepZ() * rand,
                            0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(this.maxBurnTime);
        buf.writeInt(this.burnTime);
        buf.writeInt(this.progress);
        buf.writeInt(this.processingTime);
        buf.writeBoolean(this.wasOn);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.maxBurnTime = buf.readInt();
        this.burnTime = buf.readInt();
        this.progress = buf.readInt();
        this.processingTime = buf.readInt();
        this.wasOn = buf.readBoolean();
    }

    public boolean canSmelt() {
        if (this.burnTime <= 0) return false;
        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return false;

        ItemStack result = getSmeltingResult(input);
        if (result.isEmpty()) return false;

        ItemStack output = inventory.getStackInSlot(3);
        if (output.isEmpty()) return true;

        if (!ItemStack.isSameItem(output, result)) return false;
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
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
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        this.maxBurnTime = nbt.getInt("maxBurnTime");
        this.burnTime = nbt.getInt("burnTime");
        this.progress = nbt.getInt("progress");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putInt("maxBurnTime", maxBurnTime);
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("progress", progress);
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
        return new ContainerFurnaceIron(windowId, inv, player, this);
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

    @Override
    public boolean canProvideInfo(UpgradeType type, int level, boolean extendedInfo) {
        return type == UpgradeType.SPEED;
    }

    @Override
    public void provideInfo(UpgradeType type, int level, List<Component> info, boolean extendedInfo) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.FURNACE_IRON.get()));
        if (type == UpgradeType.SPEED) {
            info.add(Component.literal("-" + (level * 50 / 3) + "%")
                    .withStyle(ChatFormatting.GREEN));
        }
    }

    @Override
    public HashMap<UpgradeType, Integer> getValidUpgrades() {
        HashMap<UpgradeType, Integer> upgrades = new HashMap<>();
        upgrades.put(UpgradeType.SPEED, 3);
        return upgrades;
    }
}