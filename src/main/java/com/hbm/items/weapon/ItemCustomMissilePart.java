package com.hbm.items.weapon;

import com.hbm.entity.missile.EntityMissileCustom;
import com.hbm.render.item.ItemRenderMissilePart;
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

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ItemCustomMissilePart extends Item {

    public PartType type;
    public PartSize top;
    public PartSize bottom;
    public Rarity rarity;
    public float health;
    private String title;
    private String author;
    private String witty;

    public static HashMap<Integer, ItemCustomMissilePart> parts = new HashMap<>();

    public Object[] attributes;

    public enum PartType {
        CHIP,
        WARHEAD,
        FUSELAGE,
        FINS,
        THRUSTER
    }

    public enum PartSize {
        ANY,
        NONE,
        SIZE_10,
        SIZE_15,
        SIZE_20
    }

    public enum WarheadType {
        HE, INC, BUSTER, CLUSTER, NUCLEAR, TX, N2, BALEFIRE, SCHRAB, TAINT, CLOUD, TURBINE,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7, CUSTOM8, CUSTOM9;

        public final Consumer<EntityMissileCustom> impactCustom = null;
        public final Consumer<EntityMissileCustom> updateCustom = null;
        public final String labelCustom = null;
    }

    public enum FuelType {
        KEROSENE, SOLID, HYDROGEN, XENON, BALEFIRE
    }

    public enum Rarity {
        COMMON("COMMON", ChatFormatting.GRAY),
        UNCOMMON("UNCOMMON", ChatFormatting.YELLOW),
        RARE("RARE", ChatFormatting.AQUA),
        EPIC("EPIC", ChatFormatting.LIGHT_PURPLE),
        LEGENDARY("LEGENDARY", ChatFormatting.DARK_GREEN),
        SEWS_CLOTHES_AND_SUCKS_HORSE_COCK("STRANGE", ChatFormatting.DARK_AQUA);

        private final String name;
        private final ChatFormatting color;

        Rarity(String name, ChatFormatting color) {
            this.name = name;
            this.color = color;
        }

        public String getDisplay() {
            return color + name;
        }
    }

    public ItemCustomMissilePart(Properties properties) {
        super(properties);
    }

    public ItemCustomMissilePart makeChip(float inaccuracy) {
        this.type = PartType.CHIP;
        this.top = PartSize.ANY;
        this.bottom = PartSize.ANY;
        this.attributes = new Object[]{inaccuracy};
        parts.put(this.hashCode(), this);
        return this;
    }

    public ItemCustomMissilePart makeWarhead(WarheadType type, float punch, float weight, PartSize size) {
        this.type = PartType.WARHEAD;
        this.top = PartSize.NONE;
        this.bottom = size;
        this.attributes = new Object[]{type, punch, weight};
        parts.put(this.hashCode(), this);
        return this;
    }

    public ItemCustomMissilePart makeFuselage(FuelType type, float fuel, PartSize top, PartSize bottom) {
        this.type = PartType.FUSELAGE;
        this.top = top;
        this.bottom = bottom;
        this.attributes = new Object[]{type, fuel};
        parts.put(this.hashCode(), this);
        return this;
    }

    public ItemCustomMissilePart makeStability(float inaccuracy, PartSize size) {
        this.type = PartType.FINS;
        this.top = size;
        this.bottom = size;
        this.attributes = new Object[]{inaccuracy};
        parts.put(this.hashCode(), this);
        return this;
    }

    public ItemCustomMissilePart makeThruster(FuelType type, float consumption, float lift, PartSize size) {
        this.type = PartType.THRUSTER;
        this.top = size;
        this.bottom = PartSize.NONE;
        this.attributes = new Object[]{type, consumption, lift};
        parts.put(this.hashCode(), this);
        return this;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private ItemRenderMissilePart renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new ItemRenderMissilePart();
                }
                return renderer;
            }
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (title != null) {
            tooltip.add(Component.literal("\"" + title + "\"").withStyle(ChatFormatting.DARK_PURPLE));
        }

        try {
            switch (type) {
                case CHIP:
                    tooltip.add(Component.translatable("item.missile.part.inaccuracy")
                            .append(": ")
                            .append(Component.literal(((Float) attributes[0]) * 100 + "%"))
                            .withStyle(ChatFormatting.BOLD));
                    break;
                case WARHEAD:
                    tooltip.add(Component.translatable("item.missile.part.size")
                            .append(": ")
                            .append(Component.literal(getSize(bottom)))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.type")
                            .append(": ")
                            .append(Component.literal(getWarhead((WarheadType) attributes[0])))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.strength")
                            .append(": ")
                            .append(Component.literal(String.valueOf(attributes[1])))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.weight")
                            .append(": ")
                            .append(Component.literal(attributes[2] + "t"))
                            .withStyle(ChatFormatting.BOLD));
                    break;
                case FUSELAGE:
                    tooltip.add(Component.translatable("item.missile.part.topSize")
                            .append(": ")
                            .append(Component.literal(getSize(top)))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.bottomSize")
                            .append(": ")
                            .append(Component.literal(getSize(bottom)))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.fuelType")
                            .append(": ")
                            .append(Component.literal(getFuel((FuelType) attributes[0])))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.fuelAmount")
                            .append(": ")
                            .append(Component.literal(attributes[1] + "l"))
                            .withStyle(ChatFormatting.BOLD));
                    break;
                case FINS:
                    tooltip.add(Component.translatable("item.missile.part.size")
                            .append(": ")
                            .append(Component.literal(getSize(top)))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.inaccuracy")
                            .append(": ")
                            .append(Component.literal(((Float) attributes[0]) * 100 + "%"))
                            .withStyle(ChatFormatting.BOLD));
                    break;
                case THRUSTER:
                    tooltip.add(Component.translatable("item.missile.part.size")
                            .append(": ")
                            .append(Component.literal(getSize(top)))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.fuelType")
                            .append(": ")
                            .append(Component.literal(getFuel((FuelType) attributes[0])))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.fuelConsumption")
                            .append(": ")
                            .append(Component.literal(attributes[1] + "l/tick"))
                            .withStyle(ChatFormatting.BOLD));
                    tooltip.add(Component.translatable("item.missile.part.maxPayload")
                            .append(": ")
                            .append(Component.literal(attributes[2] + "t"))
                            .withStyle(ChatFormatting.BOLD));
                    break;
            }
        } catch (Exception ex) {
            tooltip.add(Component.literal("Error reading data").withStyle(ChatFormatting.RED));
        }

        if (type != PartType.CHIP) {
            tooltip.add(Component.translatable("item.missile.part.health")
                    .append(": ")
                    .append(Component.literal((int) health + "HP"))
                    .withStyle(ChatFormatting.BOLD));
        }

        if (this.rarity != null) {
            tooltip.add(Component.translatable("item.missile.part.rarity")
                    .append(": ")
                    .append(Component.literal(this.rarity.getDisplay()))
                    .withStyle(ChatFormatting.BOLD));
        }
        if (author != null) {
            tooltip.add(Component.literal("   by " + author)
                    .withStyle(ChatFormatting.WHITE));
        }
        if (witty != null) {
            tooltip.add(Component.literal("   \"" + witty + "\"")
                    .withStyle(ChatFormatting.GOLD.ITALIC));
        }
    }

    String getSize(PartSize size) {
        return switch (size) {
            case ANY -> "Any";
            case SIZE_10 -> "1.0m";
            case SIZE_15 -> "1.5m";
            case SIZE_20 -> "2.0m";
            default -> "None";
        };
    }

    String getWarhead(WarheadType type) {
        if (type.labelCustom != null) return type.labelCustom;

        return switch (type) {
            case HE -> ChatFormatting.YELLOW + "HE";
            case INC -> ChatFormatting.GOLD + "Incendiary";
            case CLUSTER -> ChatFormatting.GRAY + "Cluster";
            case BUSTER -> ChatFormatting.WHITE + "Bunker Buster";
            case NUCLEAR -> ChatFormatting.DARK_GREEN + "Nuclear";
            case TX -> ChatFormatting.DARK_PURPLE + "Thermonuclear";
            case N2 -> ChatFormatting.RED + "N2";
            case BALEFIRE -> ChatFormatting.GREEN + "Balefire";
            case SCHRAB -> ChatFormatting.AQUA + "Schrabidium";
            case TAINT -> ChatFormatting.DARK_PURPLE + "Taint";
            case CLOUD -> ChatFormatting.LIGHT_PURPLE + "Cloud";
            case TURBINE ->
                    (System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE) + "Turbine";
            default -> ChatFormatting.BOLD + "Unknown";
        };
    }

    String getFuel(FuelType type) {
        return switch (type) {
            case KEROSENE -> ChatFormatting.LIGHT_PURPLE + "Kerosene/Peroxide";
            case SOLID -> ChatFormatting.GOLD + "Solid";
            case HYDROGEN -> ChatFormatting.DARK_AQUA + "Hydrogen";
            case XENON -> ChatFormatting.DARK_PURPLE + "Xenon";
            case BALEFIRE -> ChatFormatting.GREEN + "Balefire";
        };
    }

    public ItemCustomMissilePart setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ItemCustomMissilePart setTitle(String title) {
        this.title = title;
        return this;
    }

    public ItemCustomMissilePart setWittyText(String witty) {
        this.witty = witty;
        return this;
    }

    public ItemCustomMissilePart setHealth(float health) {
        this.health = health;
        return this;
    }

    public ItemCustomMissilePart setRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public ItemCustomMissilePart copy() {
        ItemCustomMissilePart part = new ItemCustomMissilePart(new Properties());
        part.type = this.type;
        part.top = this.top;
        part.bottom = this.bottom;
        part.health = this.health;
        part.attributes = this.attributes;
        part.setTitle(this.title);
        part.setAuthor(this.author);
        part.setWittyText(this.witty);
        part.setRarity(this.rarity);
        return part;
    }
}