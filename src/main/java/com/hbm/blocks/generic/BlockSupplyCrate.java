package com.hbm.blocks.generic;

import java.util.ArrayList;
import java.util.List;

import com.hbm.entity.logic.EntityC130;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsC130;
import com.hbm.items.ModToolItems;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.storage.TileEntitySupplyCrate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockSupplyCrate extends Block implements EntityBlock {

    public EntityC130.C130PayloadType payload;

    public BlockSupplyCrate(EntityC130.C130PayloadType payload) {
        super(Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.0f)
                .sound(SoundType.WOOD)
                .noOcclusion());
        this.payload = payload;

    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySupplyCrate(pos, state);
    }



    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        // Предотвращаем выпадение предметов по умолчанию
        return new ArrayList<>();
    }

    @Override
    public void playerDestroy(@NotNull Level level, Player player, @NotNull BlockPos pos, @NotNull BlockState state,
                              BlockEntity blockEntity, @NotNull ItemStack tool) {

        if (!player.isCreative() && !level.isClientSide && blockEntity instanceof TileEntitySupplyCrate inv) {

            ItemStack drop = new ItemStack(this);
            CompoundTag nbt = new CompoundTag();

            List<ItemStack> items = inv.items;
            nbt.putInt("amount", items.size());

            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);
                if (stack == null || stack.isEmpty()) continue;
                CompoundTag slot = new CompoundTag();
                stack.save(slot);
                nbt.put("slot" + i, slot);
            }

            if (!nbt.isEmpty()) {
                drop.setTag(nbt);
            }

            ItemEntity itemEntity = new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    drop);
            level.addFreshEntity(itemEntity);
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void setPlacedBy(Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            LivingEntity placer, @NotNull ItemStack stack) {

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntitySupplyCrate crate) {

            if (stack.hasTag()) {
                loadFromNBT(crate, stack.getTag());
            } else {
                generateContents(crate, level.getRandom());
            }
        }

        super.setPlacedBy(level, pos, state, placer, stack);
    }

    private void loadFromNBT(TileEntitySupplyCrate crate, CompoundTag nbt) {
        int amount = nbt.getInt("amount");
        for (int i = 0; i < amount; i++) {
            if (nbt.contains("slot" + i, Tag.TAG_COMPOUND)) {
                CompoundTag slot = nbt.getCompound("slot" + i);
                ItemStack itemStack = ItemStack.of(slot);
                if (!itemStack.isEmpty()) {
                    crate.items.add(itemStack);
                }
            }
        }
    }

    private void generateContents(TileEntitySupplyCrate crate, RandomSource random) {
        if (payload == EntityC130.C130PayloadType.SUPPLIES) {
            crate.items.addAll(ItemPoolsC130.getSuppliesPool().generate(random, 5));
        } else if (payload == EntityC130.C130PayloadType.WEAPONS) {
            crate.items.addAll(ItemPoolsC130.getWeaponsPool().generate(random, 1 + random.nextInt(2)));
            crate.items.addAll(ItemPoolsC130.getAmmoPool().generate(random, 6));
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() == ModToolItems.CROWBAR.get()) {
            if (!level.isClientSide) {
                dropContents(level, pos);
                level.destroyBlock(pos, false);
                level.playSound(null, pos, ModSounds.CRATE_BREAK.get(), player.getSoundSource(), 0.5F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void dropContents(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntitySupplyCrate crate) {

            for (ItemStack item : crate.items) {
                if (item != null && !item.isEmpty()) {
                    popResource(level, pos, item);
                }
            }
        }
    }


}