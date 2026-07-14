package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IToolable;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class BlockGraphiteRod extends BlockGraphiteDrilledBase implements IToolable {

    @OnlyIn(Dist.CLIENT)
    protected ResourceLocation outIcon;
    @OnlyIn(Dist.CLIENT)
    protected ResourceLocation outIconAluminum;

    public BlockGraphiteRod(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initTextures() {
        super.initTextures();
        this.blockIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_rod_in_aluminum.png");
        this.outIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_rod_out_aluminum.png");
        this.outIcon = ResLocation(RefStrings.MODID, "textures/block/block_graphite_rod_out.png");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getIcon(int side, int metadata) {
        int cfg = metadata & 3;

        if (side == cfg * 2 || side == cfg * 2 + 1) {
            if ((metadata & 4) == 4) {
                return ((metadata & 8) > 0) ? this.outIconAluminum : this.blockIconAluminum;
            }
            return ((metadata & 8) > 0) ? this.outIcon : this.blockIcon;
        }

        return this.sideIcon;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        int oldMeta = getMeta(state);
        int newMeta = oldMeta ^ 8;
        int pureMeta = oldMeta & 3;
        Direction side = hit.getDirection();

        if (side.get3DDataValue() == pureMeta * 2 || side.get3DDataValue() == pureMeta * 2 + 1) {
            if (level.isClientSide) return InteractionResult.SUCCESS;

            // В 1.20.1 нужно использовать BlockState свойства
            // Если у блока есть свойство META, используем его
            // level.setBlock(pos, state.setValue(META, newMeta), 3);

            // Пока просто оставляем как есть
            level.setBlock(pos, state, 3);

            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.BLOCKS,
                    0.3F, pureMeta == (oldMeta & 11) ? 0.75F : 0.65F);

            // Распространяем изменение по направлению
            for (int i = -1; i <= 1; i += 1) {
                if (i == 0) continue;

                int ix = pos.getX() + side.getStepX() * i;
                int iy = pos.getY() + side.getStepY() * i;
                int iz = pos.getZ() + side.getStepZ() * i;
                BlockPos checkPos = new BlockPos(ix, iy, iz);

                while (level.getBlockState(checkPos).getBlock() == this && getMeta(level.getBlockState(checkPos)) == oldMeta) {
                    level.setBlock(checkPos, state, 3);

                    ix += side.getStepX() * i;
                    iy += side.getStepY() * i;
                    iz += side.getStepZ() * i;
                    checkPos = new BlockPos(ix, iy, iz);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected Item getInsertedItem() {
        return ModItems.PILE_ROD_BORON.get();
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }

    public static class MetaBlock {
        public final Block block;
        public final int meta;

        public MetaBlock(Block block) {
            this(block, 0);
        }

        public MetaBlock(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }
    }
}