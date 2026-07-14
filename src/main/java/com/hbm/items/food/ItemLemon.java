package com.hbm.items.food;

import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemLemon extends Item {

    public ItemLemon(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (entity instanceof Player player) {
            // Лечение эффектов для ipecac и PTSD
            if (this == ModItems.MED_IPECAC.get() || this == ModItems.MED_PTSD.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 50, 49));

                if (!level.isClientSide) {
                    CompoundTag nbt = new CompoundTag();
                    nbt.putString("type", "vomit");
                    nbt.putInt("entity", player.getId());
                    PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(nbt, 0, 0, 0),
                            level, player.blockPosition(), 25);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.PLAYER_VOMIT.get(), player.getSoundSource(), 1.0F, 1.0F);
                }
            }

            if (this == ModItems.LOOP_STEW.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 20, 2));
            }
        }

        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (this == ModItems.LOOP_STEW.get() && entity instanceof Player) {
            return new ItemStack(Items.BOWL);
        }

        return result;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.LEMON.get()) {
            tooltip.add(Component.translatable("item.lemon.desc"));
        }

        if (this == ModItems.MED_IPECAC.get()) {
            String[] lines = Component.translatable("item.med_ipecac.desc").getString().split("\\n");
            for (String line : lines) {
                tooltip.add(Component.literal(line));
            }
        }

        if (this == ModItems.MED_PTSD.get()) {
            String[] lines = Component.translatable("item.med_ptsd.desc").getString().split("\\n");
            for (String line : lines) {
                tooltip.add(Component.literal(line));
            }
        }

        if (this == ModItems.MED_SCHIZOPHRENIA.get()) {
            String[] lines = Component.translatable("item.med_schizophrenia.desc").getString().split("\\n");
            for (String line : lines) {
                tooltip.add(Component.literal(line));
            }
        }

        if (this == ModItems.LOOPS.get()) {
            tooltip.add(Component.translatable("item.loops.desc"));
        }

        if (this == ModItems.LOOP_STEW.get()) {
            tooltip.add(Component.translatable("item.loop_stew.desc"));
        }

        if (this == ModItems.TWINKIE.get()) {
            tooltip.add(Component.translatable("item.twinkie.desc"));
        }

        if (this == ModItems.PUDDING.get()) {
            String[] lines = Component.translatable("item.pudding.desc").getString().split("\\n");
            for (String line : lines) {
                tooltip.add(Component.literal(line));
            }
        }

        if (this == ModItems.INGOT_SEMTEX.get()) {
            String[] lines = Component.translatable("item.ingot_semtex.desc").getString().split("\\n");
            for (String line : lines) {
                tooltip.add(Component.literal(line));
            }
        }

        if (this == ModItems.PEAS.get()) {
            tooltip.add(Component.translatable("item.peas.desc"));
        }

        if (this == ModItems.QUESADILLA.get()) {
            tooltip.add(Component.translatable("item.cheese_quesadilla.desc"));
        }
    }

    // Фабричный метод для создания свойств еды
    public static Properties createFoodProperties(int hunger, float saturation, boolean alwaysEdible) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(hunger)
                .saturationMod(saturation);

        if (alwaysEdible) {
            builder.alwaysEat();
        }

        return new Item.Properties().food(builder.build());
    }

    // Фабричный метод для обычной еды
    public static Properties createFoodProperties(int hunger, float saturation) {
        return createFoodProperties(hunger, saturation, false);
    }
}