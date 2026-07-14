package com.hbm.items.armor;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.handler.ArmorModHandler;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemModKnife extends ItemArmorMod {

    public static final UUID TRIGAMMA_UUID = UUID.fromString("86d44ca9-44f1-4ca6-bdbb-d9d33bead251");

    public ItemModKnife(Properties properties) {
        super(ArmorModHandler.EXTRA, false, true, false, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.RED + "Pain."));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal(ChatFormatting.RED + "Hurts, doesn't it?"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide) return;

        if (entity.tickCount % 50 == 0 && entity.getMaxHealth() > 2F) {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.SLICER.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

            CompoundTag nbt = new CompoundTag();
            nbt.putString("type", "bloodvomit");
            nbt.putInt("entity", entity.getId());
            PacketDispatcher.sendAuxParticleNT(nbt, entity.getX(), entity.getY(), entity.getZ(), entity);

            float health = entity.getMaxHealth();

            // Удаляем старый модификатор
            if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
                Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH)).removeModifier(TRIGAMMA_UUID);
            }

            // Добавляем новый модификатор
            double reduction = -(entity.getMaxHealth() - health + 2);
            if (entity.getMaxHealth() > 2F) {
                AttributeModifier modifier = new AttributeModifier(TRIGAMMA_UUID, "digamma", reduction, AttributeModifier.Operation.ADDITION);
                if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
                    Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH)).addTransientModifier(modifier);
                }
            }

            // Эффект джолта для игрока
            if (entity instanceof Player player) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "properJolt");

                if (entity.getMaxHealth() > 2F) {
                    data.putInt("time", 10000 + entity.getRandom().nextInt(10000));
                    data.putInt("maxTime", 10000);
                } else {
                    data.putInt("time", 0);
                    data.putInt("maxTime", 0);
                    if (player instanceof ServerPlayer serverPlayer) {
                        ModCriteriaTriggers.SOME_WOUNDS.trigger(serverPlayer);
                    }
                }
                PacketDispatcher.sendAuxParticleNT(data, entity.getX(), entity.getY(), entity.getZ(), entity);
            }
        }
    }
}