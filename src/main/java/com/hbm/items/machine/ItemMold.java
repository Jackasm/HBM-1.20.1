package com.hbm.items.machine;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.items.ItemEnumMulti;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class ItemMold extends Item {

    public enum MoldType implements StringRepresentable {
        NUGGET("nugget", 0, MaterialShapes.NUGGET, 1),
        BILLET("billet", 0, MaterialShapes.BILLET, 1),
        INGOT("ingot", 0, MaterialShapes.INGOT, 1),
        PLATE("plate", 0, MaterialShapes.PLATE, 1),
        WIRE("wire", 0, MaterialShapes.WIRE, 8),
        PLATE_CAST("plate_cast", 0, MaterialShapes.CASTPLATE, 1),
        WIRE_DENSE("wire_dense", 0, MaterialShapes.DENSEWIRE, 1),
        BLADE("blade", 0, MaterialShapes.INGOT, 3) {
            @Override
            public ItemStack getOutput(NTMMaterial mat) {
                if (mat == Mats.MAT_TITANIUM) return new ItemStack(ModItems.BLADE_TITANIUM.get());
                if (mat == Mats.MAT_TUNGSTEN) return new ItemStack(ModItems.BLADE_TUNGSTEN.get());
                return null;
            }
        },
        BLADES("blades", 0, MaterialShapes.INGOT, 4) {
            @Override
            public ItemStack getOutput(NTMMaterial mat) {
                if (mat == Mats.MAT_STEEL) return new ItemStack(ModItems.BLADES_STEEL.get());
                if (mat == Mats.MAT_TITANIUM) return new ItemStack(ModItems.BLADES_TITANIUM.get());
                if (mat == Mats.MAT_ALLOY) return new ItemStack(ModItems.BLADES_ADVANCED_ALLOY.get());
                return null;
            }
        },
        STAMP("stamp", 0, MaterialShapes.INGOT, 4) {
            @Override
            public ItemStack getOutput(NTMMaterial mat) {
                if (mat == Mats.MAT_STONE) return new ItemStack(ModItems.STAMP_STONE_FLAT.get());
                if (mat == Mats.MAT_IRON) return new ItemStack(ModItems.STAMP_IRON_FLAT.get());
                if (mat == Mats.MAT_STEEL) return new ItemStack(ModItems.STAMP_STEEL_FLAT.get());
                if (mat == Mats.MAT_TITANIUM) return new ItemStack(ModItems.STAMP_TITANIUM_FLAT.get());
                if (mat == Mats.MAT_OBSIDIAN) return new ItemStack(ModItems.STAMP_OBSIDIAN_FLAT.get());
                return null;
            }
        },
        SHELL("shell", 1, MaterialShapes.SHELL, 1),
        PIPE("pipe", 1, MaterialShapes.PIPE, 1),
        PIPES("pipes", 1, MaterialShapes.PIPE, 9),
        INGOTS("ingots", 1, MaterialShapes.INGOT, 9),
        PLATES("plates", 1, MaterialShapes.PLATE, 9),
        WIRES_DENSE("wires_dense", 1, MaterialShapes.DENSEWIRE, 9),
        BLOCK("block", 1, MaterialShapes.BLOCK, 1) {
            @Override
            public ItemStack getOutput(NTMMaterial mat) {
                if (mat == Mats.MAT_STONE) return new ItemStack(Blocks.STONE);
                if (mat == Mats.MAT_OBSIDIAN) return new ItemStack(Blocks.OBSIDIAN);
                if (mat == Mats.MAT_GOLD) return new ItemStack(Blocks.GOLD_BLOCK);
                if (mat == Mats.MAT_IRON) return new ItemStack(Blocks.IRON_BLOCK);
                if (mat == Mats.MAT_COPPER) return new ItemStack(Blocks.COPPER_BLOCK);
                return super.getOutput(mat);
            }
        },
        BARREL_LIGHT("barrel_light", 0, MaterialShapes.LIGHTBARREL, 1),
        BARREL_HEAVY("barrel_heavy", 0, MaterialShapes.HEAVYBARREL, 1),
        RECEIVER_LIGHT("receiver_light", 0, MaterialShapes.LIGHTRECEIVER, 1),
        RECEIVER_HEAVY("receiver_heavy", 0, MaterialShapes.HEAVYRECEIVER, 1),
        MECHANISM("mechanism", 0, MaterialShapes.MECHANISM, 1),
        STOCK("stock", 0, MaterialShapes.STOCK, 1),
        GRIP("grip", 0, MaterialShapes.GRIP, 1);

        private final String name;
        public final int size; // 0 = small (FoundryMold), 1 = large (FoundryBasin)
        public final MaterialShapes shape;
        public final int amount;

        MoldType(String name, int size, MaterialShapes shape, int amount) {
            this.name = name;
            this.size = size;
            this.shape = shape;
            this.amount = amount;
        }

        public int getCost() {
            return shape.q(amount);
        }

        public ItemStack getOutput(NTMMaterial mat) {
            // 1. Пробуем через теги (если есть)
            String tagKey = shape.name() + "s/" + mat.names[0]; // "ingots/steel", "blocks/steel"
            ResourceLocation tagId = ResLocation("forge", tagKey);
            if (ForgeRegistries.ITEMS.tags() != null) {
                var tag = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(tagId);
                var items = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag);
                if (!items.isEmpty()) {
                    // Приоритет нашим предметам
                    ItemStack hbmItem = null;
                    for (var item : items) {
                        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
                        if (key != null && key.getNamespace().equals(RefStrings.MODID)) {
                            hbmItem = new ItemStack(item, this.amount);
                            break;
                        }
                    }
                    if (hbmItem != null) return hbmItem;
                    return new ItemStack(items.iterator().next(), this.amount);
                }
            }

            // 2. Fallback: прямое имя hbm:<shape>_<material>
            String registryName = shape.name() + "_" + mat.names[0]; // например "ingot_steel"
            ResourceLocation id = ResLocation(RefStrings.MODID, registryName);
            if (BuiltInRegistries.ITEM.containsKey(id)) {
                Item item = BuiltInRegistries.ITEM.get(id);
                return new ItemStack(item, this.amount);
            }

            ResourceLocation baseId = ResLocation(RefStrings.MODID, shape.name());
            if (BuiltInRegistries.ITEM.containsKey(baseId)) {
                Item baseItem = BuiltInRegistries.ITEM.get(baseId);
                if (baseItem instanceof ItemEnumMulti<?> enumMulti) {
                    Class<? extends Enum<?>> enumClass = enumMulti.theEnum;
                    for (Enum<?> constant : enumClass.getEnumConstants()) {
                        if (constant.name().equalsIgnoreCase(mat.names[0])) {
                            ItemStack result = new ItemStack(baseItem, this.amount);
                            result.getOrCreateTag().putInt("CustomModelData", constant.ordinal());
                            return result;
                        }
                    }
                }
            }

            // 3. Если ничего не найдено – материал не отливается в этой форме
            return null;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public String getTitle() {
            return Component.translatable("mold_" + this.getSerializedName()).getString() + " x" + this.amount;
        }
    }

    public ItemMold(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static MoldType getMoldType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int ordinal = tag != null ? tag.getInt("CustomModelData") : 0;
        MoldType[] values = MoldType.values();
        return values[Math.abs(ordinal) % values.length];
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        MoldType mold = getMoldType(stack);
        return Component.translatable(this.getDescriptionId())
                .append(" ")
                .append(Component.translatable("hbm.mold_" + mold.getSerializedName()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        MoldType mold = getMoldType(stack);
        tooltip.add(Component.literal(mold.getSerializedName() + " x" + mold.amount).withStyle(ChatFormatting.YELLOW));
        if (mold.size == 0) tooltip.add(Component.translatable(ModBlocks.FOUNDRY_MOLD.get().getDescriptionId()).withStyle(ChatFormatting.GOLD));
        if (mold.size == 1) tooltip.add(Component.translatable(ModBlocks.FOUNDRY_BASIN.get().getDescriptionId()).withStyle(ChatFormatting.RED));
    }

    public static ItemStack forMold(MoldType type) {
        ItemStack stack = new ItemStack(ModItems.MOLD.get());
        stack.getOrCreateTag().putInt("CustomModelData", type.ordinal());
        return stack;
    }
}