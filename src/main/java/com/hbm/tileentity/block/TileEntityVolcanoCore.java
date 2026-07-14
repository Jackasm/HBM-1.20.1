package com.hbm.tileentity.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockVolcano;
import com.hbm.entity.projectile.EntityShrapnel;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.ExplosionVNT.ExAttrib;
import com.hbm.network.PacketDispatcher;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class TileEntityVolcanoCore extends BlockEntity {

    private static final List<ExAttrib> volcanoExplosion = Arrays.asList(ExAttrib.NODROP, ExAttrib.LAVA_V, ExAttrib.NOSOUND, ExAttrib.ALLMOD, ExAttrib.NOHURT);
    private static final List<ExAttrib> volcanoRadExplosion = Arrays.asList(ExAttrib.NODROP, ExAttrib.LAVA_R, ExAttrib.NOSOUND, ExAttrib.ALLMOD, ExAttrib.NOHURT);

    public int volcanoTimer;

    public TileEntityVolcanoCore(BlockPos pos, BlockState state) {
        super(ModTileEntity.VOLCANO_CORE.get(), pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    public static void addTooltip(int meta, List<Component> tooltip) {
        if (meta == BlockVolcano.META_SMOLDERING) {
            tooltip.add(Component.literal(ChatFormatting.GOLD + "SHIELD VOLCANO"));
            return;
        }

        tooltip.add(Component.literal((BlockVolcano.isGrowing(meta) ? ChatFormatting.RED : ChatFormatting.DARK_GRAY) + (BlockVolcano.isGrowing(meta) ? "DOES GROW" : "DOES NOT GROW")));
        tooltip.add(Component.literal((BlockVolcano.isExtinguishing(meta) ? ChatFormatting.RED : ChatFormatting.DARK_GRAY) + (BlockVolcano.isExtinguishing(meta) ? "DOES EXTINGUISH" : "DOES NOT EXTINGUISH")));
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        this.volcanoTimer++;

        if (this.volcanoTimer % 10 == 0) {
            //if that type has a vertical channel, blast it open and raise the magma
            if (this.hasVerticalChannel()) {
                this.blastMagmaChannel();
                this.raiseMagma();
            }

            double magmaChamber = this.magmaChamberSize();
            if (magmaChamber > 0) this.blastMagmaChamber(magmaChamber);

            Object[] melting = this.surfaceMeltingParams();
            if (melting != null) this.meltSurface((int) melting[0], (double) melting[1], (double) melting[2]);

            //self-explanatory
            if (this.isSpewing()) this.spawnBlobs();
            if (this.isSmoking()) this.spawnSmoke();

            //generates a 3x3x3 cube of lava
            this.surroundLava();
        }

        if (this.volcanoTimer >= this.getUpdateRate()) {
            this.volcanoTimer = 0;

            if (this.shouldGrow()) {
                level.setBlock(worldPosition.above(), this.getBlockState().getBlock().defaultBlockState(), 3);
                level.setBlock(worldPosition, getLava().defaultBlockState(), 3);
                return;
            } else if (this.isExtinguishing()) {
                level.setBlock(worldPosition, getLava().defaultBlockState(), 3);
                return;
            }
        }
    }

    public boolean isRadioactive() {
        return this.getBlockState().getBlock() == ModBlocks.VOLCANO_RAD_CORE.get();
    }

    protected Block getLava() {
        if (isRadioactive()) return ModBlocks.RAD_LAVA_BLOCK.get();
        return ModBlocks.VOLCANIC_LAVA_BLOCK.get();
    }

    protected List<ExAttrib> getExpAttrb() {
        return this.isRadioactive() ? volcanoRadExplosion : volcanoExplosion;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.volcanoTimer = nbt.getInt("timer");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("timer", this.volcanoTimer);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        load(nbt);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null) {
            load(nbt);
        }
    }

    private boolean shouldGrow() {
        return isGrowing() && worldPosition.getY() < 200;
    }

    private boolean isGrowing() {
        int meta = this.getBlockMetadata();
        return BlockVolcano.isGrowing(meta);
    }

    private boolean isExtinguishing() {
        int meta = this.getBlockMetadata();
        return BlockVolcano.isExtinguishing(meta);
    }

    private boolean isSmoking() {
        return this.getBlockMetadata() != BlockVolcano.META_SMOLDERING;
    }

    private boolean isSpewing() {
        return this.getBlockMetadata() != BlockVolcano.META_SMOLDERING;
    }

    private boolean hasVerticalChannel() {
        return this.getBlockMetadata() != BlockVolcano.META_SMOLDERING;
    }

    private double magmaChamberSize() {
        return this.getBlockMetadata() == BlockVolcano.META_SMOLDERING ? 15 : 0;
    }

    /* count per tick, radius, depth */
    private Object[] surfaceMeltingParams() {
        return this.getBlockMetadata() == BlockVolcano.META_SMOLDERING ? new Object[]{50, 50D, 10D} : null;
    }

    private int getUpdateRate() {
        return switch (this.getBlockMetadata()) {
            case BlockVolcano.META_STATIC_EXTINGUISHING -> 60 * 60 * 20; //once per hour
            case BlockVolcano.META_GROWING_ACTIVE, BlockVolcano.META_GROWING_EXTINGUISHING -> 60 * 60 * 20 / 250; //250x per hour
            default -> 10;
        };
    }

    private int getBlockMetadata() {
        // В 1.20.1 метаданные заменены на свойства блока
        // Временно возвращаем 0, нужно будет добавить свойство в BlockState
        return 0;
    }

    /** Causes two magma explosions, one from bedrock to the core and one from the core to 15 blocks above. */
    private void blastMagmaChannel() {
        ExplosionVNT explosion = new ExplosionVNT(level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + level.random.nextInt(15) + 1.5,
                worldPosition.getZ() + 0.5, 7, null);
        explosion.addAllAttrib(getExpAttrb()).explode();

        ExplosionVNT explosion2 = new ExplosionVNT(level,
                worldPosition.getX() + 0.5 + level.random.nextGaussian() * 3,
                level.random.nextInt(worldPosition.getY() + 1),
                worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 3, 10, null);
        explosion2.addAllAttrib(getExpAttrb()).explode();
    }

    /** Causes two magma explosions at a random position around the core, one at normal and one at half range. */
    private void blastMagmaChamber(double size) {
        for (int i = 0; i < 2; i++) {
            double dist = size / (double) (i + 1);
            ExplosionVNT explosion = new ExplosionVNT(level,
                    worldPosition.getX() + 0.5 + level.random.nextGaussian() * dist,
                    worldPosition.getY() + 0.5 + level.random.nextGaussian() * dist,
                    worldPosition.getZ() + 0.5 + level.random.nextGaussian() * dist, 7, null);
            explosion.addAllAttrib(getExpAttrb()).explode();
        }
    }

    /** Randomly selects surface blocks and converts them into lava if solid or air if not solid. */
    private void meltSurface(int count, double radius, double depth) {
        for (int i = 0; i < count; i++) {
            int x = (int) Math.floor(worldPosition.getX() + level.random.nextGaussian() * radius);
            int z = (int) Math.floor(worldPosition.getZ() + level.random.nextGaussian() * radius);
            //gaussian distribution makes conversions more likely on the surface and rarer at the bottom
            int y = level.getHeight() + 1 - (int) Math.floor(Math.abs(level.random.nextGaussian() * depth));

            BlockPos targetPos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(targetPos);

            if (!state.isAir() && state.getDestroySpeed(level, targetPos) < Blocks.OBSIDIAN.defaultBlockState().getDestroySpeed(level, targetPos)) {
                //turn into lava if solid block, otherwise just break
                level.setBlock(targetPos, state.isSolid() ? getLava().defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    /** Increases the magma level in a small radius around the core. */
    private void raiseMagma() {
        int rX = worldPosition.getX() - 10 + level.random.nextInt(21);
        int rY = worldPosition.getY() + level.random.nextInt(11);
        int rZ = worldPosition.getZ() - 10 + level.random.nextInt(21);
        BlockPos pos = new BlockPos(rX, rY, rZ);

        if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).getBlock() == getLava()) {
            level.setBlock(pos, getLava().defaultBlockState(), 3);
        }
    }

    /** Creates a 3x3x3 lava sphere around the core. */
    private void surroundLava() {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (i != 0 || j != 0 || k != 0) {
                        level.setBlock(worldPosition.offset(i, j, k), getLava().defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    /** Spews specially tagged shrapnels which create volcanic lava and monoxide clouds. */
    private void spawnBlobs() {
        for (int i = 0; i < 3; i++) {
            EntityShrapnel frag = new EntityShrapnel(level);
            frag.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5);
            frag.setDeltaMovement(
                    level.random.nextGaussian() * 0.2,
                    1.0 + level.random.nextDouble(),
                    level.random.nextGaussian() * 0.2
            );
            if (this.isRadioactive()) {
                frag.setRadVolcano(true);
            } else {
                frag.setVolcano(true);
            }
            level.addFreshEntity(frag);
        }
    }

    /** I SEE SMOKE, AND WHERE THERE'S SMOKE THERE'S FIRE! */
    private void spawnSmoke() {
        CompoundTag data = new CompoundTag();
        data.putString("type", "vanillaExt");
        data.putString("mode", "volcano");
        PacketDispatcher.sendAuxParticleNT(data,
                worldPosition.getX() + 0.5, worldPosition.getY() + 10, worldPosition.getZ() + 0.5,
                level, worldPosition);
    }
}