package com.hbm.blocks.generic;

import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsCrate;
import com.hbm.items.ModToolItems;
import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockCrateSimple extends Block {

    private final CrateType type;

    public enum CrateType {
        SUPPLY, WEAPON, LEAD, METAL, RED, JUNGLE
    }

    public BlockCrateSimple(Properties props, CrateType type) {
        super(props);
        this.type = type;
    }

    public static Properties createPropertiesWood() {
        return Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(5.0F, 10.0F)
                .sound(SoundType.WOOD);
    }

    public static Properties createPropertiesMetal() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        if (held.getItem() == ModToolItems.CROWBAR.get()) {
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
        ItemPool pool = getItemPool();
        RandomSource rand = level.getRandom();
        int count = 3 + rand.nextInt(3); // 3-5 предметов

        for (int i = 0; i < count; i++) {
            for (ItemStack stack : pool.generate(rand, 1)) {
                if (!stack.isEmpty()) {
                    ItemEntity item = new ItemEntity(level,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            stack.copy());
                    level.addFreshEntity(item);
                }
            }
        }
    }

    private ItemPool getItemPool() {
        return switch (type) {
            case SUPPLY -> ItemPoolsCrate.getSupplyPool();
            case WEAPON -> ItemPoolsCrate.getWeaponPool();
            case LEAD -> ItemPoolsCrate.getLeadPool();
            case METAL -> ItemPoolsCrate.getMetalPool();
            case RED -> ItemPoolsCrate.getRedPool();
            case JUNGLE -> ItemPoolsCrate.getJunglePool();
        };
    }
}