package com.hbm.items.armor;

import com.hbm.items.ModArmorItems;
import com.hbm.util.ArmorRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mod.EventBusSubscriber
public class MaskItem extends ArmorItem {
    public static final ArmorMaterial MASK_DRY_MATERIAL = createMaterial("mask_dry");
    public static final ArmorMaterial MASK_WET_MATERIAL = createMaterial("mask_wet");
    public static final ArmorMaterial MASK_PISS_MATERIAL = createMaterial("mask_piss");

    private final boolean isWet;

    public MaskItem(ArmorMaterial material, Properties properties, boolean isWet) {
        super(material, Type.HELMET, properties);
        this.isWet = isWet;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Level level = player.level();
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

            if (!helmet.isEmpty() && helmet.getItem() instanceof MaskItem mask) {
                mask.handleMaskTick(helmet, level, player);
            }
        }
    }

    private void handleMaskTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide() && isWet) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

            if (!ItemStack.isSameItemSameTags(helmet, stack)) {
                return;
            }

            if (!stack.getOrCreateTag().contains("remainingTime")) {
                stack.getOrCreateTag().putInt("remainingTime", 6000);
            } else {
                int remaining = stack.getOrCreateTag().getInt("remainingTime") - 1;

                if (remaining <= 0) {
                    transformToDry(stack, player);
                } else {
                    stack.getOrCreateTag().putInt("remainingTime", remaining);
                }
            }
        }
    }

    private void transformToDry(ItemStack stack, Player player) {
        ItemStack dryMask = new ItemStack(ModArmorItems.MASK_DRY.get());

        if (stack.hasTag()) {
            dryMask.setTag(Objects.requireNonNull(stack.getTag()).copy());
            Objects.requireNonNull(dryMask.getTag()).remove("remainingTime");
        }

        dryMask.setDamageValue(stack.getDamageValue());

        player.setItemSlot(EquipmentSlot.HEAD, dryMask);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                net.minecraft.sounds.SoundSource.PLAYERS, 0.5F, 1.0F);

    }

    public boolean protectsFrom(ArmorRegistry.HazardClass hazard) {
        Set<ArmorRegistry.HazardClass> protections = ArmorRegistry.PROTECTION_REGISTRY.get(this);
        if (protections != null) {
            return protections.contains(hazard);
        }
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (!isWet) {
            tooltip.add(Component.translatable("tooltip.mask.dry.description"));
        } else if (this == ModArmorItems.MASK_WET.get()) {
            tooltip.add(Component.translatable("tooltip.mask.wet.description"));
        } else if (this == ModArmorItems.MASK_PISS.get()) {
            tooltip.add(Component.translatable("tooltip.mask.piss.description"));
        }

        if (isWet && stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("remainingTime")) {
            int remaining = stack.getTag().getInt("remainingTime");
            int minutes = remaining / 1200;
            int seconds = (remaining % 1200) / 20;
            tooltip.add(Component.translatable("tooltip.mask.humidity",
                    minutes, String.format("%02d", seconds)));
        }
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return isWet && stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("remainingTime");
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        if (!isWet || !stack.hasTag() || !Objects.requireNonNull(stack.getTag()).contains("remainingTime")) {
            return 0x3498db;
        }

        int remaining = stack.getTag().getInt("remainingTime");
        float progress = Math.max(0, remaining) / 6000f;

        int r = 52 + (int)((255 - 52) * (1 - progress));
        int g = 152 + (int)((255 - 152) * (1 - progress));
        int b = 219 + (int)((255 - 219) * (1 - progress));

        return (r << 16) | (g << 8) | b;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        if (!isWet || !stack.hasTag() || !Objects.requireNonNull(stack.getTag()).contains("remainingTime")) {
            return 13;
        }

        int remaining = stack.getTag().getInt("remainingTime");
        float progress = Math.max(0, remaining) / 6000f;
        return Math.round(13 * progress);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return InteractionResultHolder.pass(stack);
    }

    private static ArmorMaterial createMaterial(String name) {
        return new ArmorMaterial() {
            private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};

            @Override
            public int getDurabilityForType(@NotNull Type type) {
                return BASE_DURABILITY[type.getSlot().getIndex()] * 20;
            }

            @Override
            public int getDefenseForType(@NotNull Type type) {
                return 0;
            }

            @Override
            public int getEnchantmentValue() {
                return 5;
            }

            @Override
            public @NotNull SoundEvent getEquipSound() {
                return SoundEvents.ARMOR_EQUIP_LEATHER;
            }

            @Override
            public @NotNull Ingredient getRepairIngredient() {
                return Ingredient.of(Items.STRING);
            }

            @Override
            public @NotNull String getName() {
                return "hbm:" + name;
            }

            @Override
            public float getToughness() {
                return 0.0F;
            }

            @Override
            public float getKnockbackResistance() {
                return 0.0F;
            }
        };
    }

}