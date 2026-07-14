package com.hbm.handler;

import com.hbm.blocks.ICraftingMachine;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.inventory.recipes.RecipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CrafterUnlockHandler {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() == null || !(event.getEntity() instanceof Player player)) {
            return;
        }

        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockEntity te = level.getBlockEntity(pos);

        if (te instanceof ICraftingMachine machine) {
            RecipeType type = machine.getRecipeType();
            if (type != null && type.hasRecipes()) {
                HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
                if (props != null && !props.hasCrafter(type)) {
                    props.addCrafter(type);

                    // Выводим сообщение игроку
                    player.displayClientMessage(
                            Component.translatable("hbm.crafter.unlocked", type.getDisplayName())
                                    .withStyle(net.minecraft.ChatFormatting.GREEN),
                            false
                    );
                }
            }
        }
    }
}