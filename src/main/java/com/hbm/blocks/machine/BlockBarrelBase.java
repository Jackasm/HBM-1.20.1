package com.hbm.blocks.machine;

import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.storage.TileEntityBarrel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class BlockBarrelBase extends BaseEntityBlock implements ILookOverlay {

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public BlockBarrelBase(Properties properties) {
        super(properties);
    }

    public abstract int getCapacity();
    public abstract BarrelType getBarrelType();

    public enum BarrelType {
        IRON,
        STEEL,
        PLASTIC,
        CORRODED,
        ANTIMATTER,
        TCALLOY
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityBarrel(pos, state, getCapacity());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityBarrel barrel) {
            NetworkHooks.openScreen((ServerPlayer) player, barrel, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.BARREL.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntityBarrel barrel) {
                    barrel.tick();
                }
            };
        }
        return null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityBarrel barrel && stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("FluidData")) {
                barrel.loadFromItem(tag.getCompound("FluidData"));
            }
        }
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
            });
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // Добавляем информацию о жидкости из NBT
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("FluidData")) {
                CompoundTag fluidData = tag.getCompound("FluidData");
                int fill = fluidData.getInt("fill");
                String fluidName = fluidData.getString("type");

                FluidTypeHBM fluid = Fluids.fromName(fluidName);
                if (fluid != Fluids.NONE.get()) {
                    tooltip.add(Component.literal("§e" + fill + "/" + getCapacity() + "mB " + fluid.getLocalizedName()));
                }
            }
        }

        // Добавляем информацию о типе бочки
        switch (getBarrelType()) {
            case IRON:
                tooltip.add(Component.literal("§aCapacity: " + getCapacity() + "mB"));
                tooltip.add(Component.literal("§aCan store hot fluids"));
                tooltip.add(Component.literal("§cCannot store corrosive fluids properly"));
                tooltip.add(Component.literal("§cCannot store antimatter"));
                break;
            case STEEL:
                tooltip.add(Component.literal("§aCapacity: " + getCapacity() + "mB"));
                tooltip.add(Component.literal("§aCan store hot fluids"));
                tooltip.add(Component.literal("§aCan store corrosive fluids"));
                tooltip.add(Component.literal("§cCannot store highly corrosive fluids properly"));
                tooltip.add(Component.literal("§cCannot store antimatter"));
                break;
            case PLASTIC:
                tooltip.add(Component.literal("§aCapacity: " + getCapacity() + "mB"));
                tooltip.add(Component.literal("§cCannot store hot fluids"));
                tooltip.add(Component.literal("§cCannot store corrosive fluids"));
                tooltip.add(Component.literal("§cCannot store antimatter"));
                break;
            case CORRODED:
                tooltip.add(Component.literal("§aCapacity: " + getCapacity() + "mB"));
                tooltip.add(Component.literal("§aCan store hot fluids"));
                tooltip.add(Component.literal("§aCan store highly corrosive fluids"));
                tooltip.add(Component.literal("§cCannot store antimatter"));
                tooltip.add(Component.literal("§cLeaky"));
                break;
            case ANTIMATTER:
                tooltip.add(Component.literal("§aCapacity: " + getCapacity() + "mB"));
                tooltip.add(Component.literal("§aCan store hot fluids"));
                tooltip.add(Component.literal("§aCan store highly corrosive fluids"));
                tooltip.add(Component.literal("§aCan store antimatter"));
                break;
            case TCALLOY:
                tooltip.add(Component.literal("§aCapacity: " + getCapacity() + "mB"));
                tooltip.add(Component.literal("§aCan store hot fluids"));
                tooltip.add(Component.literal("§aCan store highly corrosive fluids"));
                tooltip.add(Component.literal("§cCannot store antimatter"));
                break;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof TileEntityBarrel barrel)) return sections;

        // Основная секция с информацией о бочке
        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        mainSection.setIcon(getMachineItem());

        // Название бочки
        String barrelName = Component.translatable(this.getDescriptionId()).getString();
        mainSection.addLine(Component.literal(barrelName).withStyle(style -> style.withColor(0xFFD700)));

        // Информация о жидкости
        FluidTankHBM tank = barrel.tank;
        FluidTypeHBM fluidType = tank.getTankType();
        int fill = tank.getFill();
        int capacity = tank.getMaxFill();
        float percentage = capacity > 0 ? (fill * 100f / capacity) : 0;

        if (fluidType != null && fluidType != Fluids.NONE.get()) {
            // Название жидкости
            mainSection.addLine(Component.translatable("overlay.fluid_tank.fluid_type")
                    .append(Component.literal(": "))
                    .append(fluidType.getLocalizedName()));

            // Количество
            String amount = String.format(Locale.US, "%,d", fill) + " / " +
                    String.format(Locale.US, "%,d", capacity) + " mB";
            mainSection.addLine(Component.translatable("overlay.fluid_tank.amount")
                    .append(Component.literal(": "))
                    .append(Component.literal(amount)));

            // Процент заполнения
            String bar = createBar(percentage, 15);
            int barColor = fluidType.getColor();
            mainSection.addLine(Component.translatable("overlay.fluid_tank.fill")
                    .append(Component.literal(": "))
                    .append(Component.literal(bar).withStyle(style -> style.withColor(barColor))));
        } else {
            mainSection.addLine(Component.translatable("overlay.fluid_tank.empty"));
        }

        String modeKey = getMode(barrel.mode);
        mainSection.addLine(Component.translatable("overlay.fluid_tank.mode")
                .append(Component.literal(": "))
                .append(Component.translatable(modeKey)));

        sections.add(mainSection);

        return sections;
    }

    @OnlyIn(Dist.CLIENT)
    private String getMode(short mode) {
        return switch(mode) {
            case 0 -> "overlay.fluid_tank.mode.input";
            case 1 -> "overlay.fluid_tank.mode.buffer";
            case 2 -> "overlay.fluid_tank.mode.output";
            case 3 -> "overlay.fluid_tank.mode.disabled";
            default -> "overlay.fluid_tank.mode.unknown";
        };
    }

    @OnlyIn(Dist.CLIENT)
    private String createBar(float percentage, int length) {
        int filledChars = (int) (percentage / 100 * length);
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < length; i++) {
            bar.append(i < filledChars ? "█" : "░");
        }

        return String.format("%s %.1f%%", bar, percentage);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return true;
    }

    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }
}