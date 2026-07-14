package com.hbm.blocks.network;

import com.hbm.api.block.ICrucibleAcceptor;
import com.hbm.api.block.IToolable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.items.machine.ItemMold;
import com.hbm.items.machine.ItemScraps;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.network.TileEntityFoundryBase;
import com.hbm.tileentity.network.TileEntityFoundryCastingBase;
import com.hbm.tileentity.network.TileEntityFoundryChannel;
import com.hbm.tileentity.network.TileEntityFoundryOutlet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class FoundryCastingBase extends BaseEntityBlock implements ICrucibleAcceptor, IToolable, ILookOverlay {

    protected FoundryCastingBase(Properties properties) {
        super(properties);
    }

    // Делегирование ICrucibleAcceptor → TileEntity
    @Override
    public boolean canAcceptPartialPour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        BlockEntity te = level.getBlockEntity(pos);
        return te instanceof ICrucibleAcceptor acc && acc.canAcceptPartialPour(level, pos, dX, dY, dZ, side, stack);
    }

    @Override
    public MaterialStack pour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        BlockEntity te = level.getBlockEntity(pos);
        return te instanceof ICrucibleAcceptor acc ? acc.pour(level, pos, dX, dY, dZ, side, stack) : stack;
    }

    @Override
    public boolean canAcceptPartialFlow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        BlockEntity te = level.getBlockEntity(pos);
        return te instanceof ICrucibleAcceptor acc && acc.canAcceptPartialFlow(level, pos, side, stack);
    }

    @Override
    public MaterialStack flow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        BlockEntity te = level.getBlockEntity(pos);
        return te instanceof ICrucibleAcceptor acc ? acc.flow(level, pos, side, stack) : stack;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityFoundryCastingBase cast)) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);

        // 1. Приоритет: готовое изделие
        if (!cast.inventory.getStackInSlot(1).isEmpty()) {
            ItemStack out = cast.inventory.extractItem(1, 64, false);
            if (!player.getInventory().add(out)) {
                player.drop(out, false);
            }
            cast.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            return InteractionResult.CONSUME;
        }

        // 2. Если нет готового изделия, проверяем пустую руку для извлечения шаблона
        if (held.isEmpty()) {
            if (!cast.inventory.getStackInSlot(0).isEmpty() && cast.amount == 0) {
                ItemStack mold = cast.inventory.extractItem(0, 1, false);
                if (!player.addItem(mold)) {
                    player.drop(mold, false);
                }
                cast.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }

        // 3. Вставить молд
        if (held.getItem() instanceof ItemMold && cast.inventory.getStackInSlot(0).isEmpty()) {
            ItemMold.MoldType mold = ItemMold.getMoldType(held);
            if (mold.size == cast.getMoldSize()) {
                ItemStack copy = held.copy();
                copy.setCount(1);
                held.shrink(1);
                cast.inventory.setStackInSlot(0, copy);
                level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.BLOCKS, 1.0F, 1.0F);
                cast.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.CONSUME;
            }
        }

        // 4. Лопата – выгрести расплав
        if (held.getItem() instanceof ShovelItem) {
            if (cast.amount > 0) {
                ItemStack scrap = ItemScraps.create(new MaterialStack(cast.type, cast.amount));
                if (!player.getInventory().add(scrap)) player.drop(scrap, false);
                cast.amount = 0;
                cast.type = null;
                cast.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityFoundryCastingBase cast) {
                if (cast.amount > 0) {
                    ItemStack scrap = ItemScraps.create(new MaterialStack(cast.type, cast.amount));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, scrap));
                }
                for (int i = 0; i < cast.inventory.getSlots(); i++) {
                    ItemStack stack = cast.inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float fX, float fY, float fZ, ToolType tool) {
        if (tool != ToolType.SCREWDRIVER) return false;
        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityFoundryCastingBase cast)) return false;
        if (cast.inventory.getStackInSlot(0).isEmpty() || cast.amount > 0) return false;

        ItemStack moldStack = cast.inventory.extractItem(0, 1, false);
        if (!moldStack.isEmpty()) {
            if (!player.getInventory().add(moldStack)) player.drop(moldStack, false);
            cast.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityFoundryBase foundryBase)) return sections;

        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        mainSection.setIcon(new ItemStack(this));

        // Название блока
        String blockName = Component.translatable(this.getDescriptionId()).getString();
        mainSection.addLine(Component.literal(blockName).withStyle(style -> style.withColor(0xFFD700)));

        // Для каналов и аутлетов показываем материал и количество
        if (foundryBase instanceof TileEntityFoundryChannel || foundryBase instanceof TileEntityFoundryOutlet) {
            if (foundryBase.type != null && foundryBase.amount > 0) {
                int color = foundryBase.type.moltenColor;
                String matName = Component.translatable(foundryBase.type.getUnlocalizedName()).getString();
                String amountStr = foundryBase.amount + " / " + foundryBase.getCapacity() + " q";
                mainSection.addLine(Component.literal(matName + ": " + amountStr).withStyle(style -> style.withColor(color)));
            } else {
                mainSection.addLine(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            }
        }

        // Для Outlet дополнительная информация
        if (foundryBase instanceof TileEntityFoundryOutlet outlet) {
            // Фильтр
            if (outlet.filter != null) {
                String filterName = Component.translatable(outlet.filter.getUnlocalizedName()).getString();
                mainSection.addLine(Component.literal("Filter: " + filterName).withStyle(ChatFormatting.AQUA));
                if (outlet.invertFilter) {
                    mainSection.addLine(Component.literal("  (Inverted)").withStyle(ChatFormatting.GRAY));
                }
            } else {
                mainSection.addLine(Component.literal("Filter: None").withStyle(ChatFormatting.GRAY));
            }

            // Состояние
            mainSection.addLine(Component.literal("Closed: " + (outlet.isClosed() ? "Yes" : "No"))
                    .withStyle(outlet.isClosed() ? ChatFormatting.RED : ChatFormatting.GREEN));
            if (outlet.invertRedstone) {
                mainSection.addLine(Component.literal("  (Redstone Inverted)").withStyle(ChatFormatting.GRAY));
            }
        }

        // Для CastingBase (Mold, Basin) — молд и отливка
        if (foundryBase instanceof TileEntityFoundryCastingBase cast) {
            // Установленный молд
            ItemMold.MoldType mold = cast.getInstalledMold();
            if (mold != null) {
                mainSection.addLine(Component.literal("Mold: " + mold.getSerializedName()).withStyle(ChatFormatting.BLUE));
            } else {
                mainSection.addLine(Component.literal("No mold installed").withStyle(ChatFormatting.RED));
            }

            // Материал и количество
            if (cast.type != null && cast.amount > 0) {
                int color = cast.type.moltenColor;
                String matName = Component.translatable(cast.type.getUnlocalizedName()).getString();
                String amountStr = cast.amount + " / " + cast.getCapacity() + " q";
                mainSection.addLine(Component.literal(matName + ": " + amountStr).withStyle(style -> style.withColor(color)));
            }

            // Готовый предмет на выходе
            ItemStack output = cast.inventory.getStackInSlot(1);
            if (!output.isEmpty()) {
                mainSection.addLine(Component.literal("Output: " + output.getHoverName().getString()).withStyle(ChatFormatting.GREEN));
            }

            // Прогресс охлаждения
            if (mold != null && cast.amount == cast.getCapacity() && output.isEmpty()) {
                int progress = (200 - cast.cooloff) * 100 / 200;
                mainSection.addLine(Component.literal("Casting: " + progress + "%").withStyle(ChatFormatting.YELLOW));
            }
        }

        sections.add(mainSection);
        return sections;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return true;
    }
}