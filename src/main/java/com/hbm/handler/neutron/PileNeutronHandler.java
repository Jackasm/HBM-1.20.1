package com.hbm.handler.neutron;

import com.hbm.api.block.IPileNeutronReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.pile.BlockGraphiteDrilledBase;

import com.hbm.tileentity.machine.pile.TileEntityPileBase;
import com.hbm.util.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PileNeutronHandler {

    public static final int RANGE = 5;

    public static class PileNeutronNode extends NeutronNode {
        public PileNeutronNode(TileEntityPileBase tile) {
            super(tile, NeutronStream.NeutronType.PILE);
        }
    }

    public static PileNeutronNode makeNode(NeutronNodeWorld.StreamWorld streamWorld, TileEntityPileBase tile) {
        BlockPos pos = tile.getBlockPos();
        PileNeutronNode node = (PileNeutronNode) streamWorld.getNode(pos);
        return node != null ? node : new PileNeutronNode(tile);
    }

    private static BlockEntity getTileEntity(Level level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    public static class PileNeutronStream extends NeutronStream {

        public PileNeutronStream(NeutronNode origin, Vec3 vector, double flux) {
            super(origin, vector, flux, 0D, NeutronType.PILE);
        }

        @Override
        public void runStreamInteraction(Level level, NeutronNodeWorld.StreamWorld streamWorld) {
            TileEntityPileBase originTE = (TileEntityPileBase) origin.tile;
            BlockPos pos = originTE.getBlockPos();

            for (float i = 1; i <= RANGE; i += 0.5F) {
                BlockPos nodePos = new BlockPos(
                        (int) Math.floor(pos.getX() + 0.5 + vector.x * i),
                        (int) Math.floor(pos.getY() + 0.5 + vector.y * i),
                        (int) Math.floor(pos.getZ() + 0.5 + vector.z * i)
                );

                if (nodePos.equals(pos)) continue;

                BlockEntity tile;
                NeutronNode node = streamWorld.getNode(nodePos);
                if (node instanceof PileNeutronNode) {
                    tile = node.tile;
                } else {
                    tile = getTileEntity(level, nodePos);
                    if (tile == null) return;
                    if (tile instanceof TileEntityPileBase) {
                        streamWorld.addNode(new PileNeutronNode((TileEntityPileBase) tile));
                    }
                }

                BlockState state = tile.getBlockState();
                Block block = state.getBlock();
                int meta = 0;
                if (block instanceof BlockGraphiteDrilledBase base) {
                    meta = base.getMeta(state);
                }

                if (!(tile instanceof TileEntityPileBase)) {
                    if (block == ModBlocks.BLOCK_BORON.get()) return;
                    if (block == ModBlocks.CONCRETE.get() ||
                            block == ModBlocks.CONCRETE_SMOOTH.get() ||
                            block == ModBlocks.CONCRETE_ASBESTOS.get() ||
                            block == ModBlocks.CONCRETE_COLORED.get() ||
                            block == ModBlocks.BRICK_CONCRETE.get()) {
                        fluxQuantity *= 0.25;
                    }
                    if (block == ModBlocks.BLOCK_GRAPHITE_ROD.get() && (meta & 8) == 0) {
                        return;
                    }
                }

                if (tile instanceof IPileNeutronReceiver rec) {
                    rec.receiveNeutrons((int) Math.floor(fluxQuantity));
                    if (block != ModBlocks.BLOCK_GRAPHITE_DETECTOR.get() || (meta & 8) == 0) {
                        return;
                    }
                }

                int x = (int) (nodePos.getX() + 0.5);
                int y = (int) (nodePos.getY() + 0.5);
                int z = (int) (nodePos.getZ() + 0.5);
                AABB box = new AABB(x, y, z, x + 1, y + 1, z + 1);
                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);
                if (entities != null) {
                    float rad = (float) (fluxQuantity / 4D);
                    for (LivingEntity e : entities) {
                        ContaminationUtil.contaminate(e, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, rad);
                    }
                }
            }
        }
    }
}