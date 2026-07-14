package com.hbm.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddBonusPowderModifier extends LootModifier {
    public static final Supplier<Codec<AddBonusPowderModifier>> CODEC = Suppliers.memoize(()
            -> RecordCodecBuilder.create(inst -> codecStart(inst)
            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("bonusItem").forGetter(m -> m.bonusItem))
            .and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
            .apply(inst, AddBonusPowderModifier::new)));

    private final Item bonusItem;
    private final float chance;

    public AddBonusPowderModifier(LootItemCondition[] conditionsIn, Item bonusItem, float chance) {
        super(conditionsIn);
        this.bonusItem = bonusItem;
        this.chance = chance;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Простая проверка - добавляем предмет с фиксированным шансом
        if (context.getRandom().nextFloat() < chance) {
            generatedLoot.add(new ItemStack(bonusItem));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}