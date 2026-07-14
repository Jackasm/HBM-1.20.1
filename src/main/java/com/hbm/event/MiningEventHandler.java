package com.hbm.event;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.RadiationConfig;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ArmorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraft.world.level.block.Block.popResource;

public class MiningEventHandler {

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        var block = event.getState().getBlock();
        Level level = (ServerLevel) event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getPlayer();

        if (block == Blocks.COAL_ORE ||
                block == Blocks.DEEPSLATE_COAL_ORE ||
                block == Blocks.COAL_BLOCK ||
                block == ModBlocks.ORE_LIGNITE.get()) {

            for (Direction dir : Direction.values()) {
                if (dir == Direction.UP || dir == Direction.DOWN) continue; // только горизонтальные направления, если нужно

                BlockPos neighborPos = pos.relative(dir);

                if (level.random.nextInt(2) == 0 &&
                        level.getBlockState(neighborPos).isAir()) {
                    level.setBlock(neighborPos, ModBlocks.GAS_COAL.get().defaultBlockState(), 3);
                }
            }
        }

        if (RadiationConfig.enablePollution.get() && RadiationConfig.enableLeadFromBlocks.get()) {
            if (!ArmorRegistry.hasProtection(player, 3, ArmorRegistry.HazardClass.PARTICLE_FINE)) {

                float metal = PollutionHandler.getPollution(level, pos, PollutionType.HEAVYMETAL);

                if (metal < 5) return;

                int amplifier;
                int duration = 100; // 5 секунд (100 тиков)

                if (metal < 10) {
                    amplifier = 0;
                } else if (metal < 25) {
                    amplifier = 1;
                } else {
                    amplifier = 2;
                }

                player.addEffect(new MobEffectInstance(HbmPotion.LEAD.get(), duration, amplifier));
            }
        }

        if (event.getPlayer() != null && event.getPlayer().getAbilities().instabuild) return;



        // Обработка ВСЕХ руд для бонусного порошка
        if (event.getLevel().isClientSide()) return;
        if (player == null) return;

        int fortuneLevel;
        var tool = player.getMainHandItem();
        fortuneLevel = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);

        // Рассчитываем шанс: 3.5% + 3.5% * уровень Fortune
        float chance = 0.035f + (fortuneLevel * 0.035f);

        // ВАНИЛЬНЫЕ РУДЫ
        if (block == Blocks.IRON_ORE && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_IRON_TINY.get()));
        } else if (block == Blocks.DEEPSLATE_IRON_ORE && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_IRON_TINY.get()));
        } else if (block == Blocks.GOLD_ORE && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_GOLD_TINY.get()));
        } else if (block == Blocks.DEEPSLATE_GOLD_ORE && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_GOLD_TINY.get()));
        } else if (block == Blocks.COPPER_ORE && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_COPPER_TINY.get()));
        } else if (block == Blocks.DEEPSLATE_COPPER_ORE && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_COPPER_TINY.get()));
        }
        // РУДЫ HBM
        else if (block == ModBlocks.ORE_ALUMINIUM.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_ALUMINIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_ALUMINIUM_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_ALUMINIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_TITANIUM.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_TITANIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_TITANIUM_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_TITANIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_URANIUM.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_URANIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_URANIUM_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_URANIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_NETHER_PLUTONIUM.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_PLUTONIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_THORIUM.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_THORIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_THORIUM_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_THORIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_LEAD.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_LEAD_TINY.get()));
        } else if (block == ModBlocks.ORE_LEAD_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_LEAD_TINY.get()));
        } else if (block == ModBlocks.ORE_ZINC.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_ZINC_TINY.get()));
        } else if (block == ModBlocks.ORE_ZINC_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_ZINC_TINY.get()));
        } else if (block == ModBlocks.ORE_TUNGSTEN.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_TUNGSTEN_TINY.get()));
        } else if (block == ModBlocks.ORE_TUNGSTEN_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_TUNGSTEN_TINY.get()));
        } else if (block == ModBlocks.ORE_COBALT.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_COBALT_TINY.get()));
        } else if (block == ModBlocks.ORE_COBALT_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_COBALT_TINY.get()));
        } else if (block == ModBlocks.ORE_BERYLLIUM.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_BERYLLIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_BERYLLIUM_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_BERYLLIUM_TINY.get()));
        } else if (block == ModBlocks.ORE_ASBESTOS.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_ASBESTOS_TINY.get()));
        } else if (block == ModBlocks.ORE_ASBESTOS_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_ASBESTOS_TINY.get()));
        } else if (block == ModBlocks.ORE_NITER.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_NITER_TINY.get()));
        } else if (block == ModBlocks.ORE_NITER_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_NITER_TINY.get()));
        }
        // РУДЫ HBM КОТОРЫЕ ДРОПАЮТ ПРЕДМЕТЫ
        else if (block == ModBlocks.ORE_CINNABAR.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_CINNABAR_TINY.get()));
        } else if (block == ModBlocks.ORE_CINNABAR_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_CINNABAR_TINY.get()));
        } else if (block == ModBlocks.ORE_FLUORITE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_FLUORITE_TINY.get()));
        } else if (block == ModBlocks.ORE_FLUORITE_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_FLUORITE_TINY.get()));
        } else if (block == ModBlocks.ORE_SULFUR.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_SULFUR_TINY.get()));
        } else if (block == ModBlocks.ORE_SULFUR_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_SULFUR_TINY.get()));
        } else if (block == ModBlocks.ORE_RARE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.RARE_EARTH_CHUNK.get()));
        } else if (block == ModBlocks.ORE_RARE_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.RARE_EARTH_CHUNK.get()));
        }
        else if (block == ModBlocks.ORE_LIGNITE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_LIGNITE_TINY.get()));
        }
        else if (block == ModBlocks.ORE_LIGNITE_DEEPSLATE.get() && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_LIGNITE_TINY.get()));
        }
        else if (block == Blocks.COAL_ORE && level.getRandom().nextFloat() < chance) {
            popResource(level, pos, new ItemStack(ModItems.POWDER_COAL_TINY.get()));
        }

    }
}