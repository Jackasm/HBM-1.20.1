package com.hbm.items.special;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemCustomMissilePart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemLootCrate extends Item {

    public static List<ItemCustomMissilePart> list10 = new ArrayList<>();
    public static List<ItemCustomMissilePart> list15 = new ArrayList<>();
    public static List<ItemCustomMissilePart> listMisc = new ArrayList<>();
    private static final Random RAND = new Random();

    public ItemLootCrate(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        player.containerMenu.broadcastChanges();

        ItemCustomMissilePart result = null;

        if (stack.getItem() == ModItems.LOOT_10.get()) {
            result = choose(list10);
        } else if (stack.getItem() == ModItems.LOOT_15.get()) {
            result = choose(list15);
        } else if (stack.getItem() == ModItems.LOOT_MISC.get()) {
            result = choose(listMisc);
        }

        if (result != null) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(result));
        }

        stack.shrink(1);

        return InteractionResultHolder.sidedSuccess(stack, true);
    }

    private ItemCustomMissilePart choose(List<ItemCustomMissilePart> parts) {
        if (parts.isEmpty()) return null;

        boolean flag = true;
        ItemCustomMissilePart item = null;

        while (flag) {
            item = parts.get(RAND.nextInt(parts.size()));

            switch (item.rarity) {
                case COMMON:
                    flag = false;
                    break;
                case UNCOMMON:
                    if (RAND.nextInt(5) == 0) flag = false;
                    break;
                case RARE:
                    if (RAND.nextInt(10) == 0) flag = false;
                    break;
                case EPIC:
                    if (RAND.nextInt(25) == 0) flag = false;
                    break;
                case LEGENDARY:
                    if (RAND.nextInt(50) == 0) flag = false;
                    break;
                case SEWS_CLOTHES_AND_SUCKS_HORSE_COCK:
                    if (RAND.nextInt(100) == 0) flag = false;
                    break;
                default:
                    flag = false;
                    break;
            }
        }

        return item;
    }
}