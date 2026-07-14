package com.hbm.items.weapon;

import com.hbm.render.item.MissileItemRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ItemMissile extends Item {

    public final MissileFormFactor formFactor;
    public final MissileTier tier;
    public final MissileFuel fuel;
    public int fuelCap;
    public boolean launchable = true;

    public ItemMissile(Properties properties, MissileFormFactor form, MissileTier tier) {
        this(properties, form, tier, form.defaultFuel);
    }

    public ItemMissile(Properties properties, MissileFormFactor form, MissileTier tier, MissileFuel fuel) {
        super(properties);
        this.formFactor = form;
        this.tier = tier;
        this.fuel = fuel;
        this.setFuelCap(fuel.defaultCap);
    }

    public ItemMissile notLaunchable() {
        this.launchable = false;
        return this;
    }

    public ItemMissile setFuelCap(int fuelCap) {
        this.fuelCap = fuelCap;
        return this;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private MissileItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new MissileItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String tierKey = "item.missile.tier." + this.tier.name().toLowerCase();
        tooltip.add(Component.translatable(tierKey).withStyle(ChatFormatting.ITALIC));

        if (!this.launchable) {
            tooltip.add(Component.translatable("item.missile.desc.notLaunchable").withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(Component.translatable("item.missile.desc.fuel")
                    .append(": ")
                    .append(Component.translatable(this.fuel.key).withStyle(this.fuel.color)));
            if (this.fuelCap > 0) {
                tooltip.add(Component.translatable("item.missile.desc.fuelCapacity")
                        .append(": ")
                        .append(Component.literal(this.fuelCap + "mB")));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public enum MissileFormFactor {
        ABM(MissileFuel.SOLID),
        MICRO(MissileFuel.SOLID),
        V2(MissileFuel.ETHANOL_PEROXIDE),
        STRONG(MissileFuel.KEROSENE_PEROXIDE),
        HUGE(MissileFuel.KEROSENE_LOXY),
        ATLAS(MissileFuel.JETFUEL_LOXY),
        OTHER(MissileFuel.KEROSENE_PEROXIDE);

        private final MissileFuel defaultFuel;

        MissileFormFactor(MissileFuel defaultFuel) {
            this.defaultFuel = defaultFuel;
        }
    }

    public enum MissileTier {
        TIER0("Tier 0"),
        TIER1("Tier 1"),
        TIER2("Tier 2"),
        TIER3("Tier 3"),
        TIER4("Tier 4");

        public final String display;

        MissileTier(String display) {
            this.display = display;
        }
    }

    public enum MissileFuel {
        SOLID("item.missile.fuel.solid.prefueled", ChatFormatting.GOLD, 0),
        ETHANOL_PEROXIDE("item.missile.fuel.ethanol_peroxide", ChatFormatting.AQUA, 4_000),
        KEROSENE_PEROXIDE("item.missile.fuel.kerosene_peroxide", ChatFormatting.BLUE, 8_000),
        KEROSENE_LOXY("item.missile.fuel.kerosene_loxy", ChatFormatting.LIGHT_PURPLE, 12_000),
        JETFUEL_LOXY("item.missile.fuel.jetfuel_loxy", ChatFormatting.RED, 16_000);

        private final String key;
        public final ChatFormatting color;
        public final int defaultCap;

        MissileFuel(String key, ChatFormatting color, int defaultCap) {
            this.key = key;
            this.color = color;
            this.defaultCap = defaultCap;
        }
    }
}