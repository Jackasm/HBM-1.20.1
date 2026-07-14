package com.hbm.tileentity.machine;

import com.hbm.api.tile.IHeatSource;
import com.hbm.blocks.BlockDummyable;
import com.hbm.entity.projectile.EntitySawblade;
import com.hbm.inventory.recipes.common.OreDictStack;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;

import com.hbm.util.BufferUtil;
import com.hbm.util.ItemStackUtil;
import com.hbm.util.ModDamageSource;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TileEntitySawmill extends TileEntityMachineBase {

    public int heat;
    public static final double diffusion = 0.1D;
    private int warnCooldown = 0;
    private int overspeed = 0;
    public boolean hasBlade = true;
    public int progress = 0;
    public static final int processingTime = 600;

    public float spin;
    public float lastSpin;

    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return inventory.getStackInSlot(0).isEmpty() &&
                        inventory.getStackInSlot(1).isEmpty() &&
                        inventory.getStackInSlot(2).isEmpty() &&
                        stack.getCount() == 1 && getOutput(stack) != null;
            }
            return false;
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntitySawmill(BlockPos pos, BlockState state) {
        super(ModTileEntity.SAWMILL.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    public ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (hasBlade) {
                tryPullHeat();

                if (warnCooldown > 0) warnCooldown--;

                if (heat >= 100) {
                    ItemStack result = getOutput(inventory.getStackInSlot(0));

                    if (result != null) {
                        progress += heat / 10;

                        if (progress >= processingTime) {
                            progress = 0;
                            inventory.setStackInSlot(0, ItemStack.EMPTY);
                            inventory.setStackInSlot(1, result);

                            if (result.getItem() != ModItems.POWDER_SAWDUST.get()) {
                                float chance = result.getItem() == Items.STICK ? 0.1F : 0.5F;
                                if (level.random.nextFloat() < chance) {
                                    inventory.setStackInSlot(2, new ItemStack(ModItems.POWDER_SAWDUST.get()));
                                }
                            }

                            this.setChanged();
                        }
                    } else {
                        this.progress = 0;
                    }

                    AABB aabb = new AABB(-1D, 0.375D, -1D, -0.875, 2.375D, 1D);
                    aabb = BlockDummyable.getAABBRotationOffset(aabb,
                            worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5,
                            Direction.from3DDataValue(this.getBlockState().getValue(BlockDummyable.FACING).get3DDataValue()).getClockWise());
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);

                    for (LivingEntity e : entities) {
                        if (e.isAlive() && e.hurt(ModDamageSource.createDamageSource(
                                ModDamageSource.TURBOFAN, null, null, level), 100)) {
                            level.playSound(null, e.getX(), e.getY(), e.getZ(),
                                    SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.BLOCKS, 2.0F, 0.95F + level.random.nextFloat() * 0.2F);
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
                } else {
                    this.progress = 0;
                }

                if (heat > 300) {
                    this.overspeed++;

                    if (overspeed > 60 && warnCooldown == 0) {
                        warnCooldown = 100;
                        level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                                SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 2.0F, 1.0F);
                    }

                    if (overspeed > 300) {
                        this.hasBlade = false;
                        level.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, 5F, Level.ExplosionInteraction.BLOCK);

                        int orientation = this.getBlockState().getValue(BlockDummyable.FACING).get3DDataValue();
                        Direction dir = Direction.from3DDataValue(orientation);
                        EntitySawblade cog = new EntitySawblade(level,
                                worldPosition.getX() + 0.5 + dir.getStepX(),
                                worldPosition.getY() + 1,
                                worldPosition.getZ() + 0.5 + dir.getStepZ())
                                .setOrientation(orientation);
                        Direction rot = dir.getClockWise();

                        cog.setDeltaMovement(rot.getStepX(), 1 + (heat - 100) * 0.0001D, rot.getStepZ());
                        level.addFreshEntity(cog);

                        this.setChanged();
                    }
                } else {
                    this.overspeed = 0;
                }
            } else {
                this.overspeed = 0;
                this.warnCooldown = 0;
            }

            networkPackNT(150);

            this.heat = 0;

        } else {
            float momentum = heat * 25F / 300F;

            this.lastSpin = this.spin;
            this.spin += momentum;

            if (this.spin >= 360F) {
                this.spin -= 360F;
                this.lastSpin -= 360F;
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(heat);
        buf.writeInt(progress);
        buf.writeBoolean(hasBlade);

        for (int i = 0; i < 3; i++) {
            BufferUtil.writeItemStack(buf, inventory.getStackInSlot(i));
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.heat = buf.readInt();
        this.progress = buf.readInt();
        this.hasBlade = buf.readBoolean();

        for (int i = 0; i < 3; i++) {
            inventory.setStackInSlot(i, BufferUtil.readItemStack(buf));
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        this.hasBlade = nbt.getBoolean("hasBlade");
        this.progress = nbt.getInt("progress");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putBoolean("hasBlade", hasBlade);
        nbt.putInt("progress", progress);
    }

    protected void tryPullHeat() {
        if (level == null) return;

        BlockEntity con = level.getBlockEntity(worldPosition.below());

        if (con instanceof IHeatSource source) {
            int heatSrc = (int) (source.getHeatStored() * diffusion);

            if (heatSrc > 0) {
                source.useUpHeat(heatSrc);
                this.heat += heatSrc;
                return;
            }
        }

        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }

    private static class BlankContainer extends AbstractContainerMenu {
        protected BlankContainer() {
            super(null, 0);
        }

        @Override
        public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(@NotNull Player player) {
            return false;
        }
    }

    protected TransientCraftingContainer craftingInventory =
            new TransientCraftingContainer(new BlankContainer(), 1, 1);

    public ItemStack getOutput(ItemStack input) {
        if (input == null || input.isEmpty()) return null;

        // Очищаем контейнер перед использованием
        craftingInventory.clearContent();

        // Устанавливаем предмет
        craftingInventory.setItem(0, input);

        List<String> names = ItemStackUtil.getOreDictNames(input);

        if (names.contains("stickWood")) {
            return new ItemStack(ModItems.POWDER_SAWDUST.get());
        }

        if (names.contains("logWood")) {
            return Objects.requireNonNull(level).getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, craftingInventory, level)
                    .map(holder -> {
                        ItemStack out = holder.assemble(craftingInventory, level.registryAccess()).copy();
                        out.setCount(out.getCount() * 6 / 4); // 4 planks become 6
                        return out;
                    })
                    .orElse(null);
        }

        if (names.contains("plankWood")) {
            return new ItemStack(Items.STICK, 6);
        }

        if (names.contains("treeSapling")) {
            return new ItemStack(Items.STICK, 1);
        }

        return null;
    }

    public static HashMap<Object, ItemStack[]> getRecipes() {
        HashMap<Object, ItemStack[]> recipes = new HashMap<>();

        recipes.put(new OreDictStack("logWood"),
                new ItemStack[]{new ItemStack(Blocks.OAK_PLANKS, 6),
                        ItemStackUtil.addTooltipToStack(new ItemStack(ModItems.POWDER_SAWDUST.get()), "§c50%")});
        recipes.put(new OreDictStack("plankWood"),
                new ItemStack[]{new ItemStack(Items.STICK, 6),
                        ItemStackUtil.addTooltipToStack(new ItemStack(ModItems.POWDER_SAWDUST.get()), "§c10%")});
        recipes.put(new OreDictStack("stickWood"),
                new ItemStack[]{new ItemStack(ModItems.POWDER_SAWDUST.get())});
        recipes.put(new OreDictStack("treeSapling"),
                new ItemStack[]{new ItemStack(Items.STICK, 1),
                        ItemStackUtil.addTooltipToStack(new ItemStack(ModItems.POWDER_SAWDUST.get()), "§c10%")});

        return recipes;
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
}