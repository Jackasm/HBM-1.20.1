package com.hbm.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemStackUtil {

    public static ItemStack carefulCopy(ItemStack stack) {
        if(stack.isEmpty()) return ItemStack.EMPTY;
        return stack.copy();
    }

    public static ItemStack carefulCopyWithSize(ItemStack stack, int size) {
        if(stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack copy = stack.copy();
        copy.setCount(size);
        return copy;
    }

    /**
     * Returns a List<String> of all ore dict names (tags) for this stack. Stack cannot be null, list is empty when there are no tag entries.
     * @param stack
     * @return
     */
    public static List<String> getOreDictNames(ItemStack stack) {
        List<String> list = new ArrayList<>();

        if (stack.isEmpty()) return list;

        // Получаем все теги, к которым принадлежит предмет
        var tags = stack.getTags().toList();

        for (var tag : tags) {
            // Добавляем имя тега в формате "forge:ingots/iron"
            list.add(tag.location().toString());
        }

        return list;
    }

    /**
     * Runs carefulCopy over the entire ItemStack array.
     */
    public static ItemStack[] carefulCopyArray(ItemStack[] array) {
        return carefulCopyArray(array, 0, array.length - 1);
    }

    /**
     * Recreates the ItemStack array and only runs carefulCopy over the supplied range. All other fields remain null.
     */
    public static ItemStack[] carefulCopyArray(ItemStack[] array, int start, int end) {
        if(array == null)
            return null;

        ItemStack[] copy = new ItemStack[array.length];

        for(int i = start; i <= end; i++) {
            copy[i] = carefulCopy(array[i]);
        }

        return copy;
    }

    /**
     * Creates a new array that only contains the copied range.
     */
    public static ItemStack[] carefulCopyArrayTruncate(ItemStack[] array, int start, int end) {
        if(array == null)
            return null;

        int length = end - start + 1;
        ItemStack[] copy = new ItemStack[length];

        for(int i = 0; i < length; i++) {
            copy[i] = carefulCopy(array[start + i]);
        }

        return copy;
    }

    /**
     * UNSAFE! Will ignore all existing display tags and override them! In its current state, only fit for items we know don't have any display tags!
     * Will, however, respect existing NBT tags
     */
    public static ItemStack addTooltipToStack(ItemStack stack, String... lines) {

        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag display = new CompoundTag();
        ListTag lore = new ListTag();

        for(String line : lines) {
            lore.add(StringTag.valueOf(ChatFormatting.RESET + "" + ChatFormatting.GRAY + line));
        }

        display.put("Lore", lore);
        tag.put("display", display);

        return stack;
    }

    /**
     * Returns a List<String> of all ore dict names (now tag names) for this stack.
     * Stack cannot be null, list is empty when there are no tags.
     * @param stack
     * @return
     */
    public static List<String> getTagNames(ItemStack stack) {
        List<String> list = new ArrayList<>();

        if (stack.isEmpty()) return list;

        stack.getTags().forEach(tag -> list.add(tag.location().toString()));

        return list;
    }

    public static void addStacksToNBT(ItemStack stack, ItemStack... stacks) {

        CompoundTag tag = stack.getOrCreateTag();
        ListTag tags = new ListTag();

        for(int i = 0; i < stacks.length; i++) {
            if(stacks[i] != null && !stacks[i].isEmpty()) {
                CompoundTag slotNBT = new CompoundTag();
                slotNBT.putByte("slot", (byte) i);
                stacks[i].save(slotNBT);
                tags.add(slotNBT);
            }
        }
        tag.put("items", tags);
    }

    public static ItemStack[] readStacksFromNBT(ItemStack stack, int count) {

        if(!stack.hasTag())
            return null;

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("items", 9)) {
            return null;
        }

        ListTag list = tag.getList("items", 10);
        if(count == 0) {
            count = list.size();
        }

        ItemStack[] stacks = new ItemStack[count];

        for(int i = 0; i < count; i++) {
            CompoundTag slotNBT = list.getCompound(i);
            byte slot = slotNBT.getByte("slot");
            ItemStack loadedStack = ItemStack.of(slotNBT);
            if(slot >= 0 && slot < stacks.length && !loadedStack.isEmpty()) {
                stacks[slot] = loadedStack;
            }
        }

        return stacks;
    }

    public static ItemStack[] readStacksFromNBT(ItemStack stack) {
        return readStacksFromNBT(stack, 0);
    }

    public static List<net.minecraft.resources.ResourceLocation> getItemTags(ItemStack stack) {
        List<net.minecraft.resources.ResourceLocation> list = new ArrayList<>();

        if (!stack.isEmpty()) {
            // Получаем все теги для предмета
            stack.getTags().forEach(tagKey -> list.add(tagKey.location()));
        }

        return list;
    }

    // Альтернативно, для проверки наличия конкретного тега:
    public static boolean hasTag(ItemStack stack, net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tag) {
        return !stack.isEmpty() && stack.is(tag);
    }

    /**
     * Returns a String of the mod id of an itemstack. If a unique identifier can't be found in the registry, returns null.
     */
    public static String getModIdFromItemStack(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return ForgeRegistries.ITEMS.getKey(stack.getItem()).getNamespace();
    }

    public static void spillItems(Level world, int x, int y, int z, Block block, Random rand) {
        if (world.getBlockEntity(new net.minecraft.core.BlockPos(x, y, z)) instanceof Container container) {

            for(int slot = 0; slot < container.getContainerSize(); ++slot) {
                ItemStack itemstack = container.getItem(slot);

                if(!itemstack.isEmpty()) {
                    float oX = rand.nextFloat() * 0.8F + 0.1F;
                    float oY = rand.nextFloat() * 0.8F + 0.1F;
                    float oZ = rand.nextFloat() * 0.8F + 0.1F;

                    while(itemstack.getCount() > 0) {
                        int j1 = rand.nextInt(21) + 10;
                        if(j1 > itemstack.getCount()) j1 = itemstack.getCount();
                        itemstack.shrink(j1);

                        ItemStack dropStack = new ItemStack(itemstack.getItem(), j1);
                        dropStack.setTag(itemstack.getTag() != null ? itemstack.getTag().copy() : null);

                        ItemEntity entityitem = new ItemEntity(world, x + oX, y + oY, z + oZ, dropStack);

                        float motion = 0.05F;
                        entityitem.setDeltaMovement(
                                rand.nextGaussian() * motion,
                                rand.nextGaussian() * motion + 0.2F,
                                rand.nextGaussian() * motion
                        );
                        world.addFreshEntity(entityitem);
                    }
                }
            }
        }
    }

    public static boolean areStacksCompatible(ItemStack sta1, ItemStack sta2) {
        return sta1.getItem() == sta2.getItem() &&
                sta1.getDamageValue() == sta2.getDamageValue() &&
                ItemStack.isSameItemSameTags(sta1, sta2);
    }
}