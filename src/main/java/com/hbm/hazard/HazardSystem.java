package com.hbm.hazard;

import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.hazard.transformer.HazardTransformerBase;
import com.hbm.hazard.type.HazardTypeBase;

import com.hbm.inventory.recipes.common.ComparableStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * @author HBM
 * @since 2026
 */
public class HazardSystem {

    /*
     * Map for OreDict entries, always evaluated first. Avoid registering HazardData with 'doesOverride', as internal order is based on the item's ore dict keys.
     */
    public static final Map<String, HazardData> oreMap = new HashMap<>();

    /*
     * Map for items, either with wildcard meta or stuff that's expected to have a variety of damage values, like tools.
     */
    public static final Map<Item, HazardData> itemMap = new HashMap<>();

    /*
     * Very specific stacks with item and meta matching. ComparableStack does not support NBT matching, to scale hazards with NBT please use HazardModifiers.
     */
    public static final Map<ComparableStack, HazardData> stackMap = new HashMap<>();

    /*
     * For items that should, for whichever reason, be completely exempt from the hazard system.
     */
    public static final Set<ComparableStack> stackBlacklist = new HashSet<>();
    public static final Set<String> dictBlacklist = new HashSet<>();

    /*
     * List of hazard transformers, called in order before and after unrolling all the HazardEntries.
     */
    public static final List<HazardTransformerBase> transformers = new ArrayList<>();

    /**
     * Automatically casts the first parameter and registers it to the HazSys
     *
     * @param o    the object to register (String, Item, Block, ItemStack, ComparableStack)
     * @param data the hazard data to associate
     */
    public static void register(Object o, HazardData data) {
        if (o instanceof String s) {
            oreMap.put(s, data);
        } else if (o instanceof Item item) {
            itemMap.put(item, data);
        } else if (o instanceof Block block) {
            itemMap.put(Item.byBlock(block), data);
        } else if (o instanceof ItemStack stack) {
            stackMap.put(new ComparableStack(stack), data);
        } else if (o instanceof ComparableStack cs) {
            stackMap.put(cs, data);
        }
    }

    /**
     * Prevents the stack from returning any HazardData
     *
     * @param o the object to blacklist (ItemStack or String for ore dict)
     */
    public static void blacklist(Object o) {
        if (o instanceof ItemStack stack) {
            stackBlacklist.add(new ComparableStack(stack).makeSingular());
        } else if (o instanceof String s) {
            dictBlacklist.add(s);
        }
    }

    public static boolean isItemBlacklisted(@NotNull ItemStack stack) {
        ComparableStack comp = new ComparableStack(stack).makeSingular();
        if (stackBlacklist.contains(comp)) return true;

        // Проверяем теги предмета
        for (String tagName : dictBlacklist) {
            TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), ResLocation(tagName));
            if (stack.is(tag)) {
                stackBlacklist.add(comp); // cache it
                return true;
            }
        }
        return false;
    }

    /**
     * Will return a full list of applicable HazardEntries for this stack.
     * <br><br>ORDER:
     * <ol>
     * <li>ore dict (if multiple keys, in order of the ore dict keys for this stack)
     * <li>item
     * <li>item stack
     * </ol>
     * <p>
     * "Applicable" means that entries that are overridden or excluded via mutex are not in this list.
     * Entries that are marked as "overriding" will delete all fetched entries that came before it.
     * Entries that use mutex will prevent subsequent entries from being considered, shall they collide. The mutex system already assumes that
     * two keys are the same in priority, so the flipped order doesn't matter.
     *
     * @param stack the item stack to evaluate
     * @return list of applicable hazard entries
     */
    @NotNull
    public static List<HazardEntry> getHazardsFromStack(@NotNull ItemStack stack) {
        if (isItemBlacklisted(stack)) {
            return new ArrayList<>();
        }

        List<HazardData> chronological = new ArrayList<>();

        /// TAGS ///
        ResourceKey<Item> key = BuiltInRegistries.ITEM.getResourceKey(stack.getItem()).orElse(null);
        if (key != null) {
            BuiltInRegistries.ITEM.getHolder(key).ifPresent(holder -> {
                for (TagKey<Item> tag : holder.tags().toList()) {
                    String tagName = tag.location().toString();
                    HazardData data = oreMap.get(tagName);
                    if (data != null) {
                        chronological.add(data);
                    }
                }
            });
        }

        /// ITEM ///
        HazardData itemData = itemMap.get(stack.getItem());
        if (itemData != null) {
            chronological.add(itemData);
        }

        /// STACK ///
        ComparableStack comp = new ComparableStack(stack).makeSingular();
        HazardData stackData = stackMap.get(comp);
        if (stackData != null) {
            chronological.add(stackData);
        }

        List<HazardEntry> entries = new ArrayList<>();

        // Pre-processing transformers
        for (HazardTransformerBase transformer : transformers) {
            transformer.transformPre(stack, entries);
        }

        int mutex = 0;

        for (HazardData data : chronological) {
            // if the current data is marked as an override, purge all previous data
            if (data.doesOverride()) {
                entries.clear();
            }

            if ((data.getMutex() & mutex) == 0) {
                entries.addAll(data.getEntries());
                mutex = mutex | data.getMutex();
            }
        }

        // Post-processing transformers
        for (HazardTransformerBase transformer : transformers) {
            transformer.transformPost(stack, entries);
        }

        return entries;
    }

    public static float getHazardLevelFromStack(@NotNull ItemStack stack, @NotNull HazardTypeBase hazard) {
        List<HazardEntry> entries = getHazardsFromStack(stack);
        for (HazardEntry entry : entries) {
            if (entry.getType() == hazard) {
                return HazardModifier.evalAllModifiers(stack, null, entry.getBaseLevel(), entry.getMods());
            }
        }
        return 0.0F;
    }

    /**
     * Will grab and iterate through all assigned hazards of the given stack and apply their effects to the holder.
     *
     * @param stack  the item stack
     * @param entity the entity holding the stack
     */
    public static void applyHazards(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        List<HazardEntry> hazards = getHazardsFromStack(stack);
        for (HazardEntry hazard : hazards) {
            hazard.applyHazard(stack, entity);
        }
    }

    /**
     * Will apply the effects of all carried items, including the armor inventory.
     *
     * @param player the player to update
     */
    public static void updatePlayerInventory(@NotNull Player player) {
        // Main inventory
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (!stack.isEmpty()) {
                applyHazards(stack, player);
                if (stack.isEmpty()) {
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                }
            }
        }

        // Armor slots
        for (ItemStack stack : player.getInventory().armor) {
            if (!stack.isEmpty()) {
                applyHazards(stack, player);
            }
        }

        // Offhand
        ItemStack offhand = player.getInventory().offhand.get(0);
        if (!offhand.isEmpty()) {
            applyHazards(offhand, player);
        }
    }

    public static void updateLivingInventory(@NotNull LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HAND || slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    applyHazards(stack, entity);
                }
            }
        }
    }

    public static void updateDroppedItem(@NotNull ItemEntity entity) {
        ItemStack stack = entity.getItem();

        if (!entity.isAlive() || stack.isEmpty()) return;

        List<HazardEntry> hazards = getHazardsFromStack(stack);
        for (HazardEntry entry : hazards) {
            entry.getType().updateEntity(entity, HazardModifier.evalAllModifiers(stack, null, entry.getBaseLevel(), entry.getMods()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addFullTooltip(@NotNull ItemStack stack, @Nullable Player player, @NotNull List<Component> tooltip) {
        List<HazardEntry> hazards = getHazardsFromStack(stack);
        for (HazardEntry hazard : hazards) {
            hazard.getType().addHazardInformation(player, tooltip, hazard.getBaseLevel(), stack, hazard.getMods());
        }
    }
}