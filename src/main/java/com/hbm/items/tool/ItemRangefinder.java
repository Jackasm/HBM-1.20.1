package com.hbm.items.tool;

import com.hbm.network.PacketHandler;
import com.hbm.network.client.PlayerInformPacket;
import com.hbm.util.ChatBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ItemRangefinder extends Item {

    public static final int META_POLARIZED = 1;

    public ItemRangefinder(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());

        HitResult hitResult = player.pick(300, 0, false);

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            double dist = start.distanceTo(hitResult.getLocation());
            String msg = ((int) (dist * 10D)) / 10D + "m";

            if (stack.getDamageValue() == META_POLARIZED) {
                msg = ChatFormatting.LIGHT_PURPLE + msg + ChatFormatting.RESET;
            }

            PacketHandler.sendToPlayer(new PlayerInformPacket(ChatBuilder.start(msg).flush(), 8, 5000), (ServerPlayer) player);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Component name = super.getName(stack);
        if (stack.getDamageValue() == META_POLARIZED) {
            return Component.literal(ChatFormatting.LIGHT_PURPLE + name.getString() + ChatFormatting.RESET);
        }
        return name;
    }
}