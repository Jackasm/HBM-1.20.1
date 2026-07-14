package com.hbm.items.weapon;

import java.util.List;

import com.hbm.entity.missile.MissileStruct;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemCustomMissilePart.FuelType;
import com.hbm.items.weapon.ItemCustomMissilePart.WarheadType;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.hbm.util.ResLocation.ResLocation;

public class ItemCustomMissile extends Item {

    public ItemCustomMissile(Properties properties) {
        super(properties);
    }

    public static ItemStack buildMissile(Item chip, Item warhead, Item fuselage, Item stability, Item thruster) {
        if (stability == null) {
            return buildMissile(new ItemStack(chip), new ItemStack(warhead), new ItemStack(fuselage), null, new ItemStack(thruster));
        } else {
            return buildMissile(new ItemStack(chip), new ItemStack(warhead), new ItemStack(fuselage), new ItemStack(stability), new ItemStack(thruster));
        }
    }

    public static ItemStack buildMissile(ItemStack chip, ItemStack warhead, ItemStack fuselage, ItemStack stability, ItemStack thruster) {
        ItemStack missile = new ItemStack(ModItems.MISSILE_CUSTOM.get());

        writeToNBT(missile, "chip", chip.getItem());
        writeToNBT(missile, "warhead", warhead.getItem());
        writeToNBT(missile, "fuselage", fuselage.getItem());
        writeToNBT(missile, "thruster", thruster.getItem());

        if (stability != null)
            writeToNBT(missile, "stability", stability.getItem());

        return missile;
    }

    private static void writeToNBT(ItemStack stack, String key, Item item) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(key, item.getDescriptionId().toString());
    }

    public static Item readFromNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(key))
            return null;

        String registryName = tag.getString(key);
        return net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(ResLocation(registryName));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!stack.hasTag())
            return;

        try {
            Item chip = readFromNBT(stack, "chip");
            Item warhead = readFromNBT(stack, "warhead");
            Item fuselage = readFromNBT(stack, "fuselage");
            Item stability = readFromNBT(stack, "stability");
            Item thruster = readFromNBT(stack, "thruster");

            if (warhead instanceof ItemCustomMissilePart warheadPart) {
                // warhead name
                tooltip.add(Component.translatable("item.missile.desc.warhead")
                        .withStyle(ChatFormatting.BOLD)
                        .append(": ")
                        .append(Component.translatable(warheadPart.getWarhead((WarheadType) warheadPart.attributes[0]))
                                .withStyle(ChatFormatting.GRAY)));

                // strength
                tooltip.add(Component.translatable("item.missile.desc.strength")
                        .withStyle(ChatFormatting.BOLD)
                        .append(": ")
                        .append(Component.literal(String.valueOf(warheadPart.attributes[1]))
                                .withStyle(ChatFormatting.GRAY)));
            }

            if (fuselage instanceof ItemCustomMissilePart fuselagePart) {
                // fuel type & amount
                tooltip.add(Component.translatable("item.missile.desc.fuelType")
                        .withStyle(ChatFormatting.BOLD)
                        .append(": ")
                        .append(Component.translatable(fuselagePart.getFuel((FuelType) fuselagePart.attributes[0]))
                                .withStyle(ChatFormatting.GRAY)));

                tooltip.add(Component.translatable("item.missile.desc.fuelAmount")
                        .withStyle(ChatFormatting.BOLD)
                        .append(": ")
                        .append(Component.literal(String.valueOf(fuselagePart.attributes[1]) + "l")
                                .withStyle(ChatFormatting.GRAY)));
            }

            if (chip instanceof ItemCustomMissilePart chipPart) {
                // chip inaccuracy
                tooltip.add(Component.translatable("item.missile.desc.chipInaccuracy")
                        .withStyle(ChatFormatting.BOLD)
                        .append(": ")
                        .append(Component.literal(String.valueOf((Float) chipPart.attributes[0] * 100) + "%")
                                .withStyle(ChatFormatting.GRAY)));
            }

            // fin inaccuracy
            if (stability != null && stability instanceof ItemCustomMissilePart stabilityPart) {
                tooltip.add(Component.translatable("item.missile.desc.finInaccuracy")
                        .withStyle(ChatFormatting.BOLD)
                        .append(": ")
                        .append(Component.literal(String.valueOf((Float) stabilityPart.attributes[0] * 100) + "%")
                                .withStyle(ChatFormatting.GRAY)));
            } else {
                tooltip.add(Component.translatable("item.missile.desc.finInaccuracy")
                        .withStyle(ChatFormatting.BOLD)
                        .append(": ")
                        .append(Component.literal("100%")
                                .withStyle(ChatFormatting.GRAY)));
            }

            if (fuselage instanceof ItemCustomMissilePart fuselagePart) {
                // size
                tooltip.add(Component.translatable("item.missile.desc.size")
                        .withStyle(ChatFormatting.BOLD)
                        .append(": ")
                        .append(Component.literal(fuselagePart.getSize(fuselagePart.top) + "/" + fuselagePart.getSize(fuselagePart.bottom))
                                .withStyle(ChatFormatting.GRAY)));
            }

            // health
            float health = 0;
            if (warhead instanceof ItemCustomMissilePart warheadPart) health += warheadPart.health;
            if (fuselage instanceof ItemCustomMissilePart fuselagePart) health += fuselagePart.health;
            if (thruster instanceof ItemCustomMissilePart thrusterPart) health += thrusterPart.health;
            if (stability != null && stability instanceof ItemCustomMissilePart stabilityPart) health += stabilityPart.health;

            tooltip.add(Component.translatable("item.missile.desc.health")
                    .withStyle(ChatFormatting.BOLD)
                    .append(": ")
                    .append(Component.literal(String.valueOf(health) + "HP")
                            .withStyle(ChatFormatting.GRAY)));

        } catch (Exception ex) {
            tooltip.add(Component.literal("error.generic")
                    .withStyle(ChatFormatting.RED));
        }
    }

    public static MissileStruct getStruct(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemCustomMissile))
            return null;

        Item warheadItem = readFromNBT(stack, "warhead");
        Item fuselageItem = readFromNBT(stack, "fuselage");
        Item stabilityItem = readFromNBT(stack, "stability");
        Item thrusterItem = readFromNBT(stack, "thruster");

        if (!(warheadItem instanceof ItemCustomMissilePart warhead) ||
                !(fuselageItem instanceof ItemCustomMissilePart fuselage) ||
                !(thrusterItem instanceof ItemCustomMissilePart thruster))
            return null;

        ItemCustomMissilePart stability = (stabilityItem instanceof ItemCustomMissilePart) ? (ItemCustomMissilePart) stabilityItem : null;

        return new MissileStruct(warhead, fuselage, stability, thruster);
    }
}