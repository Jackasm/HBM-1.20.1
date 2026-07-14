package com.hbm.items.machine;

import com.hbm.blocks.machine.BlastFurnaceBlock;
import com.hbm.render.item.DiFurnaceExtensionItemRenderer;
import com.hbm.tileentity.machine.TileEntityBlastFurnace;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class ItemDiFurnaceExtension extends Item {

    public ItemDiFurnaceExtension(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final DiFurnaceExtensionItemRenderer renderer = new DiFurnaceExtensionItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        // Проверяем, что кликнули по печке
        if (!(state.getBlock() instanceof BlastFurnaceBlock)) {
            return InteractionResult.PASS;
        }

        // Проверяем, не улучшена ли уже печка
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof TileEntityBlastFurnace furnace)) {
            return InteractionResult.PASS;
        }

        // Проверяем, не стоит ли уже расширение
        if (furnace.hasExtension) {
            if (!level.isClientSide) {
                Objects.requireNonNull(context.getPlayer()).sendSystemMessage(Component.literal("This furnace already has an extension!"));
            }
            return InteractionResult.FAIL;
        }

        // Улучшаем печку
        if (!level.isClientSide) {
            furnace.hasExtension = true;
            furnace.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);

            // Съедаем предмет
            if (!Objects.requireNonNull(context.getPlayer()).isCreative()) {
                context.getItemInHand().shrink(1);
            }

            context.getPlayer().sendSystemMessage(Component.literal("Furnace upgraded with extension!"));
        }

        return InteractionResult.SUCCESS;
    }
}