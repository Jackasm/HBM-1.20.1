package com.hbm.tileentity.network;

import com.hbm.api.block.ICrucibleAcceptor;
import com.hbm.blocks.network.FoundryOutlet;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TileEntityFoundryOutlet extends TileEntityFoundryBase {

    public NTMMaterial filter = null;
    public boolean invertFilter = false;
    public boolean invertRedstone = false;

    public TileEntityFoundryOutlet(BlockPos pos, BlockState state) {
        super(ModTileEntity.FOUNDRY_OUTLET.get(), pos, state);
    }

    protected TileEntityFoundryOutlet(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isClosed() {
        if (level == null) return true;
        return invertRedstone ^ level.hasNeighborSignal(worldPosition);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityFoundryOutlet te) {
        TileEntityFoundryBase.serverTick(level, pos, state, te);
    }

    @Override
    public boolean canAcceptPartialFlow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        if (filter != null && (filter != stack.material ^ invertFilter)) return false;
        if (isClosed()) return false;

        Direction facing = getBlockState().getValue(FoundryOutlet.FACING);
        if (side != facing.getOpposite()) return false;

        Vec3 start = new Vec3(pos.getX() + 0.5, pos.getY() - 0.125, pos.getZ() + 0.5);
        Vec3 end = new Vec3(pos.getX() + 0.5, pos.getY() - 3.875, pos.getZ() + 0.5);

        BlockHitResult hit = level.clip(new net.minecraft.world.level.ClipContext(start, end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE, null));

        if (hit.getType() != HitResult.Type.BLOCK) return false;

        BlockEntity te = level.getBlockEntity(hit.getBlockPos());
        if (te instanceof ICrucibleAcceptor acc) {
            return acc.canAcceptPartialPour(level, hit.getBlockPos(), hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, Direction.UP, stack);
        }
        return false;
    }

    @Override
    public MaterialStack flow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        Vec3 start = new Vec3(pos.getX() + 0.5, pos.getY() - 0.125, pos.getZ() + 0.5);
        Vec3 end = new Vec3(pos.getX() + 0.5, pos.getY() - 3.875, pos.getZ() + 0.5);

        BlockHitResult hit = level.clip(new net.minecraft.world.level.ClipContext(start, end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE, null));

        if (hit.getType() != HitResult.Type.BLOCK) return stack;

        BlockEntity te = level.getBlockEntity(hit.getBlockPos());
        if (te instanceof ICrucibleAcceptor acc) {
            MaterialStack didPour = acc.pour(level, hit.getBlockPos(), hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, Direction.UP, stack);

            if (didPour != null) {
                Direction dir = side.getOpposite();
                CompoundTag data = new CompoundTag();
                data.putString("type", "foundry");
                data.putInt("color", stack.material.moltenColor);
                data.putByte("dir", (byte) dir.ordinal());
                data.putFloat("off", 0.375F);
                data.putFloat("base", 0F);
                data.putFloat("len", Math.max(1F, pos.getY() - (float) (Math.ceil(hit.getLocation().y) - 0.875)));
                PacketDispatcher.sendToAllAround(
                        new AuxParticlePacketNT(data, pos.getX() + 0.5 - dir.getStepX() * 0.125, pos.getY() + 0.125, pos.getZ() + 0.5 - dir.getStepZ() * 0.125),
                        level, pos, 50);
            }
            return didPour;
        }
        return stack;
    }

    @Override
    public int getCapacity() { return 0; }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.invertRedstone = nbt.getBoolean("invert");
        this.invertFilter = nbt.getBoolean("invertFilter");
        this.filter = Mats.matById.get(nbt.getShort("filter"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("invert", this.invertRedstone);
        nbt.putBoolean("invertFilter", this.invertFilter);
        nbt.putShort("filter", this.filter == null ? -1 : (short) this.filter.id);
    }
}