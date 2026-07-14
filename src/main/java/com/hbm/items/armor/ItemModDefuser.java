package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModDefuser extends ItemArmorMod {

    public ItemModDefuser(Properties properties) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Defuses nearby creepers"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide) return;
        if (entity.tickCount % 20 != 0) return;

        AABB box = entity.getBoundingBox().inflate(5, 5, 5);
        List<Creeper> creepers = entity.level().getEntitiesOfClass(Creeper.class, box);

        for (Creeper creeper : creepers) {
            // Проверяем, взрывается ли крипер
            if (creeper.isIgnited() || creeper.getSwellDir() > 0) {
                // Деактивируем взрыв
                creeper.ignite();
                creeper.setSwellDir(-1);

                // Создаём эффект деактивации
                entity.level().playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(),
                        SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);

                // Убираем задачу взрыва
                // В 1.20.1 сложно удалить конкретную задачу из AI, поэтому просто убиваем крипера с дропом
                creeper.spawnAtLocation(ModItems.SAFETY_FUSE.get());
                creeper.hurt(entity.damageSources().playerAttack((Player) entity), 1.0F);
                creeper.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
            }
        }
    }
}