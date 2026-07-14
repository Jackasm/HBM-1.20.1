package com.hbm.blocks.conduit;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.network.FluidDuctStandard;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FT_Corrosive;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.conduit.*;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConduitBlock extends BaseEntityBlock  implements ILookOverlay {

    public ConduitBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ConduitTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.CONDUIT.get()) {
            return (lvl, pos, st, te) -> {
                if (te instanceof ConduitTileEntity conduit) {
                    conduit.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (!(level.getBlockEntity(pos) instanceof ConduitTileEntity conduit)) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);

        // Идентификатор жидкости
        if (!held.isEmpty() && held.getItem() instanceof IItemFluidIdentifier identifier) {
            FluidTypeHBM newFluid = identifier.getType(level, pos, held);
            if (newFluid != null && newFluid != Fluids.NONE.get()) {
                for (ConduitChannel channel : conduit.getChannels()) {
                    if (channel instanceof FluidChannel fluidChannel && fluidChannel.getEntry().fluid == newFluid) {
                        player.displayClientMessage(Component.translatable("overlay.conduit.fluid_exists"), true);
                        return InteractionResult.FAIL;
                    }
                }
                for (ConduitChannel channel : conduit.getChannels()) {
                    if (channel instanceof FluidChannel fluidChannel && fluidChannel.getEntry().fluid == Fluids.NONE.get()) {
                        int pipeType = fluidChannel.getEntry().pipeType;
                        conduit.removeChannel(fluidChannel.getEntry());
                        conduit.addChannel(ConduitEntry.fluid(newFluid, pipeType));
                        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.25F, 0.75F);
                        return InteractionResult.SUCCESS;
                    }
                }
                player.displayClientMessage(Component.translatable("overlay.conduit.no_empty_pipe"), true);
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        }

        // Удаление канала (Shift + пустая рука)
        if (held.isEmpty() && player.isShiftKeyDown()) {
            if (!conduit.getChannels().isEmpty()) {
                if (conduit.getChannels().size() == 2) {
                    ConduitChannel remaining = conduit.getChannels().get(0);
                    ConduitChannel lastChannel = conduit.getChannels().get(1);
                    conduit.removeLastChannel();

                    if (remaining instanceof EnergyChannel) {
                        level.setBlock(pos, ModBlocks.RED_CABLE.get().defaultBlockState(), 3);
                    } else if (remaining instanceof FluidChannel fluidChannel) {
                        Block pipeBlock = switch (fluidChannel.getEntry().pipeType) {
                            case 0 -> ModBlocks.FLUID_DUCT.get();
                            case 1 -> ModBlocks.FLUID_DUCT_SILVER.get();
                            case 2 -> ModBlocks.FLUID_DUCT_COLORED.get();
                            default -> ModBlocks.FLUID_DUCT.get();
                        };
                        level.setBlock(pos, pipeBlock.defaultBlockState()
                                .setValue(FluidDuctStandard.PIPE_TYPE, fluidChannel.getEntry().pipeType), 3);
                        if (level.getBlockEntity(pos) instanceof TileEntityPipeBaseNT pipe) {
                            pipe.setFluidType(fluidChannel.getEntry().fluid);
                        }
                    }

                    ItemStack drop = getDropForChannel(lastChannel);
                    if (!drop.isEmpty()) {
                        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                    }
                } else {
                    ConduitChannel lastChannel = conduit.getChannels().get(conduit.getChannels().size() - 1);
                    ItemStack drop = getDropForChannel(lastChannel);
                    conduit.removeLastChannel();
                    if (!drop.isEmpty()) {
                        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                    }
                }
                level.playSound(null, pos, SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // Вспомогательный метод
    private ItemStack getDropForChannel(ConduitChannel channel) {
        if (channel instanceof EnergyChannel) {
            return new ItemStack(ModBlocks.RED_CABLE.get());
        } else if (channel instanceof FluidChannel fluidChannel) {
            return switch (fluidChannel.getEntry().pipeType) {
                case 0 -> new ItemStack(ModBlocks.FLUID_DUCT.get());
                case 1 -> new ItemStack(ModBlocks.FLUID_DUCT_SILVER.get());
                case 2 -> new ItemStack(ModBlocks.FLUID_DUCT_COLORED.get());
                default -> new ItemStack(ModBlocks.FLUID_DUCT.get());
            };
        }
        return ItemStack.EMPTY;
    }

    public static void makeConduit(Level level, BlockPos pos, ConduitEntry first, ConduitEntry second, Player player, ItemStack held) {
        level.setBlock(pos, ModBlocks.CONDUIT.get().defaultBlockState(), 3);
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof ConduitTileEntity conduit) {
            conduit.addChannel(first);
            conduit.addChannel(second);
            if (!player.isCreative()) held.shrink(1);
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        if (builder.getParameter(LootContextParams.BLOCK_ENTITY) instanceof ConduitTileEntity conduit) {
            for (ConduitChannel channel : conduit.getChannels()) {
                drops.add(getDropForChannel(channel));
            }
        }
        return drops;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();
        if (!(level.getBlockEntity(pos) instanceof ConduitTileEntity conduit)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(new ItemStack(this));
        section.addLine(Component.translatable(this.getDescriptionId()).withStyle(style -> style.withColor(0xFFD700)));

        if (conduit.getChannels().isEmpty()) {
            section.addLine(Component.translatable("overlay.conduit.no_channels").withStyle(ChatFormatting.GRAY));
        } else {
            for (ConduitChannel channel : conduit.getChannels()) {
                if (channel instanceof EnergyChannel) {
                    String cableName = Component.translatable(ModBlocks.RED_CABLE.get().getDescriptionId()).getString();
                    section.addLine(Component.literal("⚡ " + cableName));
                } else if (channel instanceof FluidChannel fluidChannel) {
                    addFluidLine(section, fluidChannel);
                }
            }
        }
        sections.add(section);
        return sections;
    }

    private void addFluidLine(OverlaySection section, FluidChannel fluidChannel) {
        int pipeType = fluidChannel.getEntry().pipeType;
        Block pipeBlock = switch (pipeType) {
            case 0 -> ModBlocks.FLUID_DUCT.get();
            case 1 -> ModBlocks.FLUID_DUCT_SILVER.get();
            case 2 -> ModBlocks.FLUID_DUCT_COLORED.get();
            default -> ModBlocks.FLUID_DUCT.get();
        };
        String pipeName = Component.translatable(pipeBlock.getDescriptionId()).getString();

        FluidTypeHBM fluid = fluidChannel.getEntry().fluid;
        if (fluid == null) return;

        String fluidIcon = fluid.isGaseous() ? "☁" : "\uD83D\uDCA7";
        ChatFormatting dangerColor = getFluidDangerColor(fluid);
        List<Component> dangerIcons = getFluidDangerIcons(fluid);

        MutableComponent line = Component.literal("");
        line.append(Component.literal(fluidIcon + " ").withStyle(style -> style.withColor(fluid.getColor())));
        line.append(Component.literal(pipeName + ": ").withStyle(ChatFormatting.WHITE));
        line.append(Component.literal(fluid.getLocalizedName()).withStyle(dangerColor));
        for (Component iconComp : dangerIcons) {
            line.append(Component.literal(" "));
            line.append(iconComp);
        }
        section.addLine(line);
    }

    private ChatFormatting getFluidDangerColor(FluidTypeHBM fluid) {
        if (fluid.isAntimatter()) return ChatFormatting.DARK_RED;
        if (fluid.isRadioactive()) return ChatFormatting.GOLD;
        if (fluid.isCorrosive()) {
            FT_Corrosive trait = fluid.getTrait(FT_Corrosive.class);
            if (trait != null && trait.isHighlyCorrosive()) return ChatFormatting.GOLD;
            return ChatFormatting.YELLOW;
        }
        if (fluid.isFlammable()) return ChatFormatting.YELLOW;
        if (fluid.isCombustible()) return ChatFormatting.GOLD;
        if (fluid.isHot()) return ChatFormatting.GOLD;
        return ChatFormatting.WHITE;
    }

    private List<Component> getFluidDangerIcons(FluidTypeHBM fluid) {
        List<Component> icons = new ArrayList<>();
        if (fluid.isAntimatter()) {
            icons.add(Component.literal("☢").withStyle(ChatFormatting.DARK_RED));
        } else if (fluid.isRadioactive()) {
            icons.add(Component.literal("☢").withStyle(ChatFormatting.GOLD));
        }
        if (fluid.isCorrosive()) {
            FT_Corrosive trait = fluid.getTrait(FT_Corrosive.class);
            ChatFormatting color = (trait != null && trait.isHighlyCorrosive()) ? ChatFormatting.GOLD : ChatFormatting.YELLOW;
            icons.add(Component.literal("⚠").withStyle(color));
        }
        if (fluid.isCombustible()) {
            icons.add(Component.literal("🔥").withStyle(ChatFormatting.GOLD));
        } else if (fluid.isFlammable()) {
            icons.add(Component.literal("🔥").withStyle(ChatFormatting.YELLOW));
        }
        if (fluid.isHot()) {
            icons.add(Component.literal("♨").withStyle(ChatFormatting.GOLD));
        }
        return icons;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return true;
    }
}