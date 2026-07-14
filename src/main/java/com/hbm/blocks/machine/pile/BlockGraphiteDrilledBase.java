package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IInsertable;
import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockFlammable;
import com.hbm.blocks.machine.pile. BlockGraphiteRod.MetaBlock;
import com.hbm.items.ModItems;

import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

public abstract class BlockGraphiteDrilledBase extends BlockFlammable implements IToolable, IInsertable {

    public static final IntegerProperty META = IntegerProperty.create("meta", 0, 15);

    @OnlyIn(Dist.CLIENT)
    protected ResourceLocation blockIcon;


    public BlockGraphiteDrilledBase(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(META, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(META);
    }

    public int getMeta(BlockState state) {
        return state.getValue(META);
    }

    public BlockState setMeta(BlockState state, int meta) {
        return state.setValue(META, meta);
    }

    @OnlyIn(Dist.CLIENT)
    protected ResourceLocation sideIcon;

    @OnlyIn(Dist.CLIENT)
    protected ResourceLocation blockIconAluminum;

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getIcon(int side, int metadata) {
        int cfg = metadata & 3;
        int meta = metadata & 4;

        if (side == cfg * 2 || side == cfg * 2 + 1) {
            if (meta == 4) {
                return blockIconAluminum != null ? blockIconAluminum :
                        ResLocation(RefStrings.MODID, "textures/block/block_graphite_drilled_aluminum.png");
            }
            return sideIcon != null ? sideIcon :
                    ResLocation(RefStrings.MODID, "textures/block/block_graphite.png");
        }

        return sideIcon != null ? sideIcon :
                ResLocation(RefStrings.MODID, "textures/block/block_graphite.png");
    }

    @OnlyIn(Dist.CLIENT)
    public void initTextures() {
        this.sideIcon = ResLocation(RefStrings.MODID, "textures/block/block_graphite.png");
        this.blockIcon = ResLocation(RefStrings.MODID, "textures/block/block_graphite.png");
        this.blockIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_drilled_aluminum.png");
    }

    protected static void ejectItem(Level level, BlockPos pos, Direction dir, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;

        double x = pos.getX() + 0.5 + dir.getStepX() * 0.75;
        double y = pos.getY() + 0.5 + dir.getStepY() * 0.75;
        double z = pos.getZ() + 0.5 + dir.getStepZ() * 0.75;

        ItemEntity item = new ItemEntity(level, x, y, z, stack);
        item.setDeltaMovement(
                dir.getStepX() * 0.25,
                dir.getStepY() * 0.25,
                dir.getStepZ() * 0.25
        );
        level.addFreshEntity(item);
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, ToolType tool) {
        if (tool != ToolType.SCREWDRIVER) return false;

        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            int meta = getMeta(state);
            int cfg = meta & 3;

            if (side.get3DDataValue() == cfg * 2 || side.get3DDataValue() == cfg * 2 + 1) {
                // Удаляем вставку
                level.setBlock(pos, ModBlocks.BLOCK_GRAPHITE_DRILLED.get().defaultBlockState(), 3);
                ejectItem(level, pos, side, new ItemStack(getInsertedItem(meta)));
            }
        }

        return true;
    }

    protected Item getInsertedItem(int meta) {
        return getInsertedItem();
    }

    protected Item getInsertedItem() {
        return null;
    }

    public List<ItemStack> getDrops(Level level, BlockPos pos, BlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        int meta = getMeta(state);

        drops.add(new ItemStack(ModItems.INGOT_GRAPHITE.get(), 8));
        if ((meta & 4) == 4) {
            drops.add(new ItemStack(ModItems.SHELL_ALUMINIUM.get(), 1));
        }
        Item inserted = getInsertedItem(meta);
        if (inserted != null) {
            drops.add(new ItemStack(inserted, 1));
        }
        return drops;
    }

    protected MetaBlock checkInteractions(ItemStack stack) {
        Item item = stack.getItem();
        if (item == ModItems.PILE_ROD_URANIUM.get()) return new MetaBlock(ModBlocks.BLOCK_GRAPHITE_FUEL.get());
        if (item == ModItems.PILE_ROD_PU239.get()) return new MetaBlock(ModBlocks.BLOCK_GRAPHITE_FUEL.get(), 0b1000);
        if (item == ModItems.PILE_ROD_PLUTONIUM.get()) return new MetaBlock(ModBlocks.BLOCK_GRAPHITE_PLUTONIUM.get());
        if (item == ModItems.PILE_ROD_SOURCE.get()) return new MetaBlock(ModBlocks.BLOCK_GRAPHITE_SOURCE.get());
        if (item == ModItems.PILE_ROD_BORON.get()) return new MetaBlock(ModBlocks.BLOCK_GRAPHITE_ROD.get());
        if (item == ModItems.PILE_ROD_LITHIUM.get()) return new MetaBlock(ModBlocks.BLOCK_GRAPHITE_LITHIUM.get());
        if (item == ModItems.CELL_TRITIUM.get()) return new MetaBlock(ModBlocks.BLOCK_GRAPHITE_TRITIUM.get());
        if (item == ModItems.PILE_ROD_DETECTOR.get()) return new MetaBlock(ModBlocks.BLOCK_GRAPHITE_DETECTOR.get());
        return null;
    }

    @Override
    public boolean insertItem(Level level, BlockPos pos, Direction dir, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        MetaBlock baseBlock = checkInteractions(stack);
        if (baseBlock == null) return false;

        BlockState state = level.getBlockState(pos);
        int pureMeta = getMeta(state) & 3;
        int side = dir.get3DDataValue();

        if (side == pureMeta * 2 || side == pureMeta * 2 + 1) {
            // Проверяем возможность проталкивания
            for (int i = 0; i <= 3; i++) {
                BlockPos checkPos = pos.relative(dir, i);
                Block b = level.getBlockState(checkPos).getBlock();

                if (b instanceof BlockGraphiteDrilledBase) {
                    int baseMeta = getMeta(level.getBlockState(checkPos));
                    if ((baseMeta & 3) != pureMeta) return false;

                    if (((BlockGraphiteDrilledBase) b).getInsertedItem(baseMeta) == null) {
                        break;
                    } else if (i >= 3) {
                        return false;
                    }
                } else {
                    BlockState checkState = level.getBlockState(checkPos);
                    if (checkState.isSolid()) {
                        return false;
                    } else {
                        break;
                    }
                }
            }

            // Выполняем вставку и проталкивание
            int oldMeta = pureMeta | baseBlock.meta;
            Block oldBlock = baseBlock.block;
            CompoundTag oldTag = new CompoundTag();
            oldTag.putInt("x", pos.getX());
            oldTag.putInt("y", pos.getY());
            oldTag.putInt("z", pos.getZ());

            for (int i = 0; i <= 3; i++) {
                BlockPos checkPos = pos.relative(dir, i);
                Block newBlock = level.getBlockState(checkPos).getBlock();

                if (newBlock instanceof BlockGraphiteDrilledBase) {
                    int newMeta = getMeta(level.getBlockState(checkPos));
                    CompoundTag newTag = new CompoundTag();

                    if (newBlock instanceof BlockGraphiteDrilledTE) {
                        BlockEntity te = level.getBlockEntity(checkPos);
                        if (te != null) {
                            newTag = te.saveWithoutMetadata();
                            newTag.putInt("x", te.getBlockPos().getX() + dir.getStepX());
                            newTag.putInt("y", te.getBlockPos().getY() + dir.getStepY());
                            newTag.putInt("z", te.getBlockPos().getZ() + dir.getStepZ());
                        }
                    }

                    level.setBlock(checkPos, oldBlock.defaultBlockState(), 0);

                    if (oldBlock instanceof BlockGraphiteDrilledTE && !oldTag.isEmpty()) {
                        BlockEntity te = level.getBlockEntity(checkPos);
                        if (te != null) {
                            te.load(oldTag);
                        }
                    }

                    level.sendBlockUpdated(checkPos, newBlock.defaultBlockState(), oldBlock.defaultBlockState(), 3);

                    oldMeta = newMeta;
                    oldBlock = newBlock;
                    oldTag = newTag;

                    if (oldBlock instanceof BlockGraphiteDrilled) {
                        break;
                    }
                } else {
                    Item eject = ((BlockGraphiteDrilledBase) oldBlock).getInsertedItem(oldMeta);
                    if (eject != null) {
                        BlockPos prevPos = checkPos.relative(dir.getOpposite());
                        ejectItem(level, prevPos, dir, new ItemStack(eject));
                    }
                    level.playSound(null, checkPos.getX() + 0.5, checkPos.getY() + 0.5, checkPos.getZ() + 0.5,
                            SoundEvents.NOTE_BLOCK_PLING.get(), SoundSource.BLOCKS, 1.25F, 1.0F);
                    break;
                }
            }

            return true;
        }

        return false;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }
}