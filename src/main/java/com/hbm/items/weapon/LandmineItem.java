package com.hbm.items.weapon;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.item.LandMineItemRenderer;
import com.hbm.tileentity.bomb.TileEntityLandmine;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class LandmineItem extends BlockItem {
    public LandmineItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private LandMineItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new LandMineItemRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        // Позиция глаз игрока и направление взгляда
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        double distance = 5; // расстояние до установки мины
        Vec3 targetPos = eyePos.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
        BlockPos targetBlockPos = BlockPos.containing(targetPos);

        // Проверяем, является ли целевой блок водой
        BlockState state = level.getBlockState(targetBlockPos);
        if (state.liquid()) {
            // Устанавливаем мину в воду (можно также выше воды, но пока так)
            level.setBlock(targetBlockPos, Blocks.STONE.defaultBlockState(), 3);
            level.setBlock(targetBlockPos, ModBlocks.MINE_NAVAL.get().defaultBlockState(), 3);
            if (level.getBlockEntity(targetBlockPos) instanceof TileEntityLandmine landmine) {
                landmine.waitingForPlayer = true;

            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }


}
