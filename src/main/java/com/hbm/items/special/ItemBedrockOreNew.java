package com.hbm.items.special;

import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.inventory.material.NTMMaterial.SmeltingBehavior;
import com.hbm.items.ModItems;
import com.hbm.items.block.ItemBlockResourceStone;
import com.hbm.util.EnumUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import static com.hbm.inventory.material.Mats.*;
import static com.hbm.items.special.ItemBedrockOreNew.ProcessingTrait.*;

public class ItemBedrockOreNew extends Item {

    public ItemBedrockOreNew(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int code = getCustomModelData(stack);
        BedrockOreGrade grade = getGrade(code);
        BedrockOreType type = getType(code);
        String typeKey = this.getDescriptionId(stack) + ".type." + type.suffix + ".name";
        String gradeKey = this.getDescriptionId(stack) + ".grade." + grade.name().toLowerCase(Locale.US);
        return Component.translatable(gradeKey, Component.translatable(typeKey));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int code = getCustomModelData(stack);
        for (ProcessingTrait trait : getGrade(code).traits) {
            tooltip.add(Component.translatable(this.getDescriptionId(stack) + ".trait." + trait.name().toLowerCase(Locale.US)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) { // оверлей для окрашивания
            int code = getCustomModelData(stack);
            BedrockOreType type = getType(code);
            BedrockOreGrade grade = getGrade(code);
            // Можно использовать цвет из типа или града, или микс
            // Вернём цвет града (для примера)
            return grade.tint;
        }
        return 0xFFFFFF;
    }

    // ========== Работа с CustomModelData ==========

    private static int getCustomModelData(ItemStack stack) {
        return ItemBlockResourceStone.getType(stack);
    }

    private static void setCustomModelData(ItemStack stack, int code) {
        stack.getOrCreateTag().putInt("CustomModelData", code);
    }

    public static int encode(BedrockOreGrade grade, BedrockOreType type) {
        return (grade.ordinal() << 4) | type.ordinal();
    }

    public BedrockOreGrade getGrade(int code) {
        return EnumUtil.grabEnumSafely(BedrockOreGrade.class, code >> 4);
    }

    public BedrockOreType getType(int code) {
        return EnumUtil.grabEnumSafely(BedrockOreType.class, code & 15);
    }

    // ========== Статические методы для создания стеков ==========

    public static class BedrockOreOutput {
        public NTMMaterial mat;
        public int amount;

        public BedrockOreOutput(NTMMaterial mat, int amount) {
            this.mat = mat;
            this.amount = amount;
        }
    }

    public static BedrockOreOutput o(NTMMaterial mat, int amount) {
        return new BedrockOreOutput(mat, amount);
    }

    public static MaterialStack toFluid(BedrockOreOutput o, double amount) {
        if (o.mat != null && o.mat.smeltable == SmeltingBehavior.SMELTABLE) {
            return new MaterialStack(o.mat, (int) Math.ceil(MaterialShapes.FRAGMENT.q(o.amount) * amount));
        }
        return null;
    }

    public static ItemStack extract(BedrockOreOutput o, double amount) {
        ItemStack stack = new ItemStack(ModItems.BEDROCK_ORE_FRAGMENT.get(), Math.min((int) Math.ceil(o.amount * amount), 64));
        stack.getOrCreateTag().putInt("material", o.mat.id);
        return stack;
    }

    public static ItemStack make(BedrockOreGrade grade, BedrockOreType type, int amount) {
        ItemStack stack = new ItemStack(ModItems.BEDROCK_ORE.get(), amount);
        setCustomModelData(stack, encode(grade, type));
        return stack;
    }

    public static ItemStack make(BedrockOreGrade grade, BedrockOreType type) {
        return make(grade, type, 1);
    }

    // ========== Enums ==========

    public enum BedrockOreType {
        LIGHT_METAL(0xFFFFFF, 0x353535, "light",
                o(MAT_IRON, 9), o(MAT_COPPER, 9),
                o(MAT_TITANIUM, 6), o(MAT_BAUXITE, 9), o(MAT_CRYOLITE, 3),
                o(MAT_CHLOROCALCITE, 5), o(MAT_LITHIUM, 5), o(MAT_SODIUM, 3),
                o(MAT_CHLOROCALCITE, 6), o(MAT_LITHIUM, 6), o(MAT_SODIUM, 6)),

        HEAVY_METAL(0x868686, 0x000000, "heavy",
                o(MAT_TUNGSTEN, 9), o(MAT_LEAD, 9),
                o(MAT_GOLD, 2), o(MAT_GOLD, 2), o(MAT_BERYLLIUM, 3),
                o(MAT_TUNGSTEN, 9), o(MAT_LEAD, 9), o(MAT_GOLD, 5),
                o(MAT_BISMUTH, 1), o(MAT_BISMUTH, 1), o(MAT_GOLD, 6)),

        RARE_EARTH(0xE6E6B6, 0x1C1C00, "rare",
                o(MAT_COBALT, 5), o(MAT_RAREEARTH, 5),
                o(MAT_BORON, 5), o(MAT_LANTHANIUM, 3), o(MAT_NIOBIUM, 4),
                o(MAT_NEODYMIUM, 3), o(MAT_STRONTIUM, 3), o(MAT_ZIRCONIUM, 3),
                o(MAT_NIOBIUM, 5), o(MAT_NEODYMIUM, 5), o(MAT_STRONTIUM, 3)),

        ACTINIDE(0xC1C7BD, 0x2B3227, "actinide",
                o(MAT_URANIUM, 4), o(MAT_THORIUM, 4),
                o(MAT_RADIUM, 2), o(MAT_RADIUM, 2), o(MAT_POLONIUM, 2),
                o(MAT_RADIUM, 2), o(MAT_RADIUM, 2), o(MAT_POLONIUM, 2),
                o(MAT_TECHNETIUM, 1), o(MAT_TECHNETIUM, 1), o(MAT_U238, 1)),

        NON_METAL(0xAFAFAF, 0x0F0F0F, "nonmetal",
                o(MAT_COAL, 9), o(MAT_SULFUR, 9),
                o(MAT_LIGNITE, 9), o(MAT_KNO, 6), o(MAT_FLUORITE, 6),
                o(MAT_PHOSPHORUS, 5), o(MAT_FLUORITE, 6), o(MAT_SULFUR, 6),
                o(MAT_CHLOROCALCITE, 6), o(MAT_SILICON, 2), o(MAT_SILICON, 2)),

        CRYSTALLINE(0xE2FFFA, 0x1E8A77, "crystal",
                o(MAT_REDSTONE, 9), o(MAT_CINNABAR, 4),
                o(MAT_SODALITE, 9), o(MAT_ASBESTOS, 6), o(MAT_DIAMOND, 3),
                o(MAT_CINNABAR, 3), o(MAT_ASBESTOS, 5), o(MAT_EMERALD, 3),
                o(MAT_BORAX, 3), o(MAT_MOLYSITE, 3), o(MAT_SODALITE, 9));

        public final int light;
        public final int dark;
        public final String suffix;
        public final BedrockOreOutput primary1;
        public final BedrockOreOutput primary2;
        public final BedrockOreOutput byproductAcid1;
        public final BedrockOreOutput byproductAcid2;
        public final BedrockOreOutput byproductAcid3;
        public final BedrockOreOutput byproductSolvent1;
        public final BedrockOreOutput byproductSolvent2;
        public final BedrockOreOutput byproductSolvent3;
        public final BedrockOreOutput byproductRad1;
        public final BedrockOreOutput byproductRad2;
        public final BedrockOreOutput byproductRad3;

        BedrockOreType(int light, int dark, String suffix,
                       BedrockOreOutput p1, BedrockOreOutput p2,
                       BedrockOreOutput bA1, BedrockOreOutput bA2, BedrockOreOutput bA3,
                       BedrockOreOutput bS1, BedrockOreOutput bS2, BedrockOreOutput bS3,
                       BedrockOreOutput bR1, BedrockOreOutput bR2, BedrockOreOutput bR3) {
            this.light = light;
            this.dark = dark;
            this.suffix = suffix;
            this.primary1 = p1;
            this.primary2 = p2;
            this.byproductAcid1 = bA1;
            this.byproductAcid2 = bA2;
            this.byproductAcid3 = bA3;
            this.byproductSolvent1 = bS1;
            this.byproductSolvent2 = bS2;
            this.byproductSolvent3 = bS3;
            this.byproductRad1 = bR1;
            this.byproductRad2 = bR2;
            this.byproductRad3 = bR3;
        }
    }

    public enum ProcessingTrait {
        ROASTED,
        ARC,
        WASHED,
        CENTRIFUGED,
        SULFURIC,
        SOLVENT,
        RAD
    }

    public enum BedrockOreGrade {
        BASE(0xFFFFFF, "base"),
        BASE_ROASTED(0xCFCFCF, "base", ROASTED),
        BASE_WASHED(0xDBE2CB, "base", WASHED),
        PRIMARY(0xFFFFFF, "primary", CENTRIFUGED),
        PRIMARY_ROASTED(0xCFCFCF, "primary", ROASTED),
        PRIMARY_SULFURIC(0xFFFFD3, "primary", SULFURIC),
        PRIMARY_NOSULFURIC(0xD3D4FF, "primary", CENTRIFUGED, SULFURIC),
        PRIMARY_SOLVENT(0xD3F0FF, "primary", SOLVENT),
        PRIMARY_NOSOLVENT(0xFFDED3, "primary", CENTRIFUGED, SOLVENT),
        PRIMARY_RAD(0xECFFD3, "primary", RAD),
        PRIMARY_NORAD(0xEBD3FF, "primary", CENTRIFUGED, RAD),
        PRIMARY_FIRST(0xFFD3D4, "primary", CENTRIFUGED),
        PRIMARY_SECOND(0xD3FFEB, "primary", CENTRIFUGED),
        CRUMBS(0xFFFFFF, "crumbs", CENTRIFUGED),

        SULFURIC_BYPRODUCT(0xFFFFFF, "sulfuric", CENTRIFUGED, SULFURIC),
        SULFURIC_ROASTED(0xCFCFCF, "sulfuric", ROASTED, SULFURIC),
        SULFURIC_ARC(0xC3A2A2, "sulfuric", ARC, SULFURIC),
        SULFURIC_WASHED(0xDBE2CB, "sulfuric", WASHED, SULFURIC),

        SOLVENT_BYPRODUCT(0xFFFFFF, "solvent", CENTRIFUGED, SOLVENT),
        SOLVENT_ROASTED(0xCFCFCF, "solvent", ROASTED, SOLVENT),
        SOLVENT_ARC(0xC3A2A2, "solvent", ARC, SOLVENT),
        SOLVENT_WASHED(0xDBE2CB, "solvent", WASHED, SOLVENT),

        RAD_BYPRODUCT(0xFFFFFF, "rad", CENTRIFUGED, RAD),
        RAD_ROASTED(0xCFCFCF, "rad", ROASTED, RAD),
        RAD_ARC(0xC3A2A2, "rad", ARC, RAD),
        RAD_WASHED(0xDBE2CB, "rad", WASHED, RAD);

        public final int tint;
        public final String prefix;
        public final ProcessingTrait[] traits;

        BedrockOreGrade(int tint, String prefix, ProcessingTrait... traits) {
            this.tint = tint;
            this.prefix = prefix;
            this.traits = traits;
        }
    }
}