package com.hbm.items.armor;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.ArmorModHandler;
import com.hbm.potion.HbmPotion;
import com.hbm.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModAuto extends ItemArmorMod {

    public ItemModAuto(Properties properties) {
        super(ArmorModHandler.EXTRA, false, true, false, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.BLUE + "Imported from Japsterdam."));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide) return;

        if (HbmLivingProps.getDigamma(entity) >= 5F) {
            // Удаляем мод из брони
            // TODO: реализовать удаление мода через ArmorModHandler

            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.SYRINGE_USE.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

            HbmLivingProps.setDigamma(entity, HbmLivingProps.getDigamma(entity) - 5F);
            entity.addEffect(new MobEffectInstance(HbmPotion.STABILITY.get(), 60 * 20, 0));
            entity.heal(20F);
        }
    }
}