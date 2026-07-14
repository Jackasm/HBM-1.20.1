package com.hbm.tileentity.bomb;

import com.hbm.api.item.IDesignatorItem;
import com.hbm.blocks.bomb.LaunchPadRusted;
import com.hbm.entity.ModEntities;
import com.hbm.entity.missile.EntityMissileTier4.EntityMissileDoomsdayRusted;
import com.hbm.interfaces.IBomb.BombReturnCode;
import com.hbm.interfaces.IControlReceiver;
import com.hbm.inventory.container.ContainerLaunchPadRusted;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileEntityLaunchPadRusted extends TileEntityMachineBase implements MenuProvider, IControlReceiver {

    public int prevRedstonePower = -1;
    public int redstonePower = -1;
    public Set<BlockPos> activatedBlocks = new HashSet<>();

    public boolean missileLoaded;

    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch (slot) {
                case 0 -> false; // output slot
                case 1 -> stack.getItem() == ModItems.LAUNCH_CODE.get();
                case 2 -> stack.getItem() == ModItems.LAUNCH_KEY.get();
                case 3 -> stack.getItem() instanceof IDesignatorItem;
                default -> false;
            };
        }
    };

    private final LazyOptional<ItemStackHandler> itemHandlerCap = LazyOptional.of(() -> inventory);

    public TileEntityLaunchPadRusted(BlockPos pos, BlockState state) {
        super(ModTileEntity.LAUNCH_PAD_RUSTED.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (this.redstonePower > 0 && this.prevRedstonePower <= 0) {
                this.launch();
            }
            this.prevRedstonePower = this.redstonePower;
            this.networkPackNT(50);
        } else {
            // Smoke particles on client
            AABB box = new AABB(
                    worldPosition.getX() - 0.5, worldPosition.getY(), worldPosition.getZ() - 0.5,
                    worldPosition.getX() + 1.5, worldPosition.getY() + 10, worldPosition.getZ() + 1.5
            );

            List<EntityMissileDoomsdayRusted> entities = level.getEntitiesOfClass(
                    EntityMissileDoomsdayRusted.class, box);

            if (!entities.isEmpty()) {
                for (int i = 0; i < 15; i++) {
                    Direction dir = this.getBlockState().getValue(LaunchPadRusted.FACING);
                    if (level.random.nextBoolean()) dir = dir.getOpposite();
                    if (level.random.nextBoolean()) dir = dir.getClockWise();

                    float moX = (float) ((level.random.nextGaussian() * 0.15F + 0.75) * dir.getStepX());
                    float moZ = (float) ((level.random.nextGaussian() * 0.15F + 0.75) * dir.getStepZ());

                    CompoundTag data = new CompoundTag();
                    data.putDouble("posX", worldPosition.getX() + 0.5);
                    data.putDouble("posY", worldPosition.getY() + 0.25);
                    data.putDouble("posZ", worldPosition.getZ() + 0.5);
                    data.putString("type", "launchSmoke");
                    data.putDouble("moX", moX);
                    data.putDouble("moY", 0);
                    data.putDouble("moZ", moZ);
                    MainRegistry.proxy.effectNT(data);
                }
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(this.missileLoaded);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.missileLoaded = buf.readBoolean();
    }

    public BombReturnCode launch() {
        if (level == null || level.isClientSide) return BombReturnCode.UNDEFINED;

        ItemStack codeStack = inventory.getStackInSlot(1);
        ItemStack keyStack = inventory.getStackInSlot(2);
        ItemStack designatorStack = inventory.getStackInSlot(3);

        if (codeStack.getItem() == ModItems.LAUNCH_CODE.get() &&
                keyStack.getItem() == ModItems.LAUNCH_KEY.get() &&
                designatorStack.getItem() instanceof IDesignatorItem designator &&
                this.missileLoaded) {

            if (!designator.isReady(level, designatorStack, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) {
                return BombReturnCode.ERROR_MISSING_COMPONENT;
            }

            Vec3 coords = designator.getCoords(level, designatorStack, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
            int targetX = (int) Math.floor(coords.x);
            int targetZ = (int) Math.floor(coords.z);

            EntityMissileDoomsdayRusted missile = new EntityMissileDoomsdayRusted(
                    level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1,
                    worldPosition.getZ() + 0.5,
                    targetX, targetZ,
                    ModEntities.MISSILE_DOOMSDAY_RUSTED.get()
            );
            level.addFreshEntity(missile);

            level.playSound(null, worldPosition, ModSounds.MISSILE_TAKEOFF.get(),
                    net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 1.0F);

            this.missileLoaded = false;
            inventory.extractItem(1, 1, false);
            setChanged();

            return BombReturnCode.LAUNCHED;
        }

        return BombReturnCode.ERROR_MISSING_COMPONENT;
    }

    // ================= NBT =================
    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putBoolean("missileLoaded", missileLoaded);
        nbt.putInt("redstonePower", redstonePower);
        nbt.putInt("prevRedstonePower", prevRedstonePower);

        CompoundTag activatedBlocksTag = new CompoundTag();
        int i = 0;
        for (BlockPos p : activatedBlocks) {
            activatedBlocksTag.putInt("x" + i, p.getX());
            activatedBlocksTag.putInt("y" + i, p.getY());
            activatedBlocksTag.putInt("z" + i, p.getZ());
            i++;
        }
        nbt.put("activatedBlocks", activatedBlocksTag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        missileLoaded = nbt.getBoolean("missileLoaded");
        redstonePower = nbt.getInt("redstonePower");
        prevRedstonePower = nbt.getInt("prevRedstonePower");

        activatedBlocks.clear();
        CompoundTag activatedBlocksTag = nbt.getCompound("activatedBlocks");
        for (int i = 0; i < activatedBlocksTag.getAllKeys().size() / 3; i++) {
            activatedBlocks.add(new BlockPos(
                    activatedBlocksTag.getInt("x" + i),
                    activatedBlocksTag.getInt("y" + i),
                    activatedBlocksTag.getInt("z" + i)
            ));
        }
    }

    // ================= Redstone =================
    public void updateRedstonePower(BlockPos pos) {
        if (level == null) return;

        boolean powered = level.getBestNeighborSignal(pos) > 0;
        boolean contained = activatedBlocks.contains(pos);

        if (!contained && powered) {
            activatedBlocks.add(pos);
            if (redstonePower == -1) {
                redstonePower = 0;
            }
            redstonePower++;
        } else if (contained && !powered) {
            activatedBlocks.remove(pos);
            redstonePower--;
            if (redstonePower == 0) {
                redstonePower = -1;
            }
        }
    }

    // ================= Rendering =================
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 2,
                worldPosition.getY(),
                worldPosition.getZ() - 2,
                worldPosition.getX() + 3,
                worldPosition.getY() + 15,
                worldPosition.getZ() + 3
        );
    }

    // ================= Capabilities =================
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCap.invalidate();
    }

    // ================= MenuProvider =================
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerLaunchPadRusted(id, inv, this);
    }

    @Override
    public net.minecraft.network.chat.@NotNull Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.hbm.launch_pad_rusted");
    }

    // ================= IControlReceiver =================
    @Override
    public boolean hasPermission(Player player) {
        return this.isUsableByPlayer(player);
    }

    @Override
    public void receiveControl(CompoundTag data) {
        if (data.contains("release") && data.getBoolean("release")) {
            if (this.missileLoaded && inventory.getStackInSlot(0).isEmpty()) {
                this.missileLoaded = false;
                inventory.setStackInSlot(0, new ItemStack(ModItems.MISSILE_DOOMSDAY_RUSTED.get()));
                this.setChanged();
            }
        }
    }
}