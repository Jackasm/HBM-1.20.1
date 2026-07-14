package com.hbm.items.machine;

import com.hbm.inventory.recipes.loader.GenericRecipes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemBlueprintFolder extends Item {

    public ItemBlueprintFolder(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        List<String> pools = new ArrayList<>();

        for (String pool : GenericRecipes.blueprintPools.keySet()) {
            if (stack.getDamageValue() == 0 && pool.startsWith(GenericRecipes.POOL_PREFIX_ALT)) {
                pools.add(pool);
            }
            if (stack.getDamageValue() == 1 && pool.startsWith(GenericRecipes.POOL_PREFIX_DISCOVER)) {
                pools.add(pool);
            }
            if (stack.getDamageValue() == 2 && pool.startsWith(GenericRecipes.POOL_PREFIX_SECRET)) {
                pools.add(pool);
            }
        }

        if (!pools.isEmpty()) {
            stack.shrink(1);

            String chosen = pools.get(player.getRandom().nextInt(pools.size()));
            ItemStack blueprint = ItemBlueprints.make(chosen);

            if (!player.getInventory().add(blueprint)) {
                player.drop(blueprint, false);
            }
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int meta = stack.getDamageValue();
        String key = switch (meta) {
            case 1 -> "blueprint_folder.discover";
            case 2 -> "blueprint_folder.secret";
            default -> "blueprint_folder.alt";
        };
        tooltip.add(Component.translatable(key));
    }
}