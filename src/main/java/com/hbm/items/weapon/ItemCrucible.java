package com.hbm.items.weapon;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hbm.items.IAnimatedItem;
import com.hbm.items.IEquipReceiver;

import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.render.anim.AnimationEnums.ToolAnimation;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.HbmAnimations;

import com.hbm.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ItemCrucible extends ItemSwordAbility implements IEquipReceiver, IAnimatedItem<ToolAnimation> {

    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID MOVEMENT_SPEED_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    public ItemCrucible(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public void onEquip(Player player, ItemStack stack) {
        if (!(player instanceof ServerPlayer)) return;

        if (player.getMainHandItem() == stack && stack.getDamageValue() < stack.getMaxDamage()) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ARMOR_EQUIP_IRON,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
            playAnimation(player, ToolAnimation.EQUIP);
        }
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) return false;

        if (stack.getDamageValue() >= stack.getMaxDamage()) return false;

        playAnimation(player, ToolAnimation.SWING);
        return false;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        boolean active = stack.getDamageValue() < stack.getMaxDamage();

        if (active) {
            attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                    net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 0.75F + target.getRandom().nextFloat() * 0.2F);

            if (!attacker.level().isClientSide && !target.isAlive()) {
                int count = Math.min((int) Math.ceil(target.getMaxHealth() / 3D), 250);
                double x = target.getX();
                double y = target.getY() + target.getBbHeight() * 0.5;
                double z = target.getZ();

                // Отправляем эффект взрыва частиц через существующий метод
                if (attacker.level() instanceof ServerLevel) {
                    // Создаём данные для эффекта
                    CompoundTag data = new CompoundTag();
                    data.putString("type", "vanillaburst");
                    data.putInt("count", count * 4);
                    data.putDouble("motion", 0.1D);
                    data.putString("mode", "blockdust");
                    data.putString("block", "minecraft:redstone_block");

                    PacketDispatcher.sendToAllAround(
                            new AuxParticlePacketNT(data, x, y, z),
                            attacker.level(), target.blockPosition(), 50);
                }
            }

            return super.hurtEnemy(stack, target, attacker);
        } else {
            if (!attacker.level().isClientSide && attacker instanceof Player player) {
                player.sendSystemMessage(Component.literal("Not enough energy.").withStyle(ChatFormatting.RED));
            }
            return false;
        }
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = ArrayListMultimap.create();
        multimap.putAll(super.getAttributeModifiers(slot, stack));

        if (slot == EquipmentSlot.MAINHAND && stack.getDamageValue() < stack.getMaxDamage()) {
            multimap.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 100.0D, AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(MOVEMENT_SPEED_MODIFIER, "Tool modifier", 1.0D, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int damage = stack.getDamageValue();
        int maxDamage = stack.getMaxDamage();

        StringBuilder charge = new StringBuilder(ChatFormatting.RED + "Charge [");
        for (int i = maxDamage - 1; i >= 0; i--) {
            if (damage <= i) {
                charge.append("||||||");
            } else {
                charge.append("   ");
            }
        }
        charge.append("]");

        tooltip.add(Component.literal(charge.toString()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BusAnimation getAnimation(ToolAnimation type, ItemStack stack) {
        if (type == ToolAnimation.EQUIP) {
            return new BusAnimation()
                    .addBus("GUARD_ROT", new BusAnimationSequence()
                            .addPos(90, 0, 1, 0)
                            .addPos(90, 0, 1, 800)
                            .addPos(0, 0, 1, 50));
        }

        if (type == ToolAnimation.SWING) {
            if (HbmAnimations.getRelevantTransformation("SWING_ROT")[0] == 0) {
                Random random = new Random();
                int offset = random.nextInt(80) - 20;
                playSwing();

                return new BusAnimation()
                        .addBus("SWING_ROT", new BusAnimationSequence()
                                .addPos(90 - offset, 90 - offset, 35, 75)
                                .addPos(90 + offset, 90 - offset, -45, 150)
                                .addPos(0, 0, 0, 500))
                        .addBus("SWING_TRANS", new BusAnimationSequence()
                                .addPos(-3, 0, 0, 75)
                                .addPos(8, 0, 0, 150)
                                .addPos(0, 0, 0, 500));
            }
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    private void playSwing() {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(ModSounds.CRUCIBLE_SWING.get(), 1.0F));
    }

    @Override
    public Class<ToolAnimation> getEnum() {
        return ToolAnimation.class;
    }

    @Override
    public boolean shouldPlayerModelAim(ItemStack stack) {
        return false;
    }
}