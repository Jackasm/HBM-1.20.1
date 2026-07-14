package com.hbm.network.server;

import com.hbm.entity.missile.EntityBobmazon;

import com.hbm.inventory.gui.GUIScreenBobmazon.Offer;
import com.hbm.items.BobmazonOfferFactory;
import com.hbm.items.ModItems;
import com.hbm.network.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemBobmazonPacket extends PacketBase implements PacketBase.DecodablePacket {

    private int offerIndex;

    public ItemBobmazonPacket() {}

    public ItemBobmazonPacket(Player player, Offer offer) {
        if (player.getMainHandItem() != null && player.getMainHandItem().getItem() == ModItems.BOBMAZON.get()) {
            this.offerIndex = BobmazonOfferFactory.standard.indexOf(offer);
        }
        if (player.getMainHandItem() != null && player.getMainHandItem().getItem() == ModItems.BOBMAZON_HIDDEN.get()) {
            this.offerIndex = BobmazonOfferFactory.special.indexOf(offer);
        }
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new ItemBobmazonPacket(buf.readInt());
    }

    private ItemBobmazonPacket(int offerIndex) {
        this.offerIndex = offerIndex;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(offerIndex);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null) return;

        Level level = player.level();

        Offer offer = null;
        if (player.getMainHandItem() != null && player.getMainHandItem().getItem() == ModItems.BOBMAZON.get()) {
            if (offerIndex >= 0 && offerIndex < BobmazonOfferFactory.standard.size()) {
                offer = BobmazonOfferFactory.standard.get(offerIndex);
            }
        }
        if (player.getMainHandItem() != null && player.getMainHandItem().getItem() == ModItems.BOBMAZON_HIDDEN.get()) {
            if (offerIndex >= 0 && offerIndex < BobmazonOfferFactory.special.size()) {
                offer = BobmazonOfferFactory.special.get(offerIndex);
            }
        }

        if (offer == null) {
            player.displayClientMessage(
                    Component.literal("[BOBMAZON] There appears to be a mismatch between the offer you have requested and the offers that exist."),
                    false
            );
            player.displayClientMessage(
                    Component.literal("[BOBMAZON] Engaging fail-safe..."),
                    false
            );
            player.hurt(level.damageSources().generic(), 1000);
            player.setDeltaMovement(player.getDeltaMovement().x, 2.0D, player.getDeltaMovement().z);
            return;
        }

        ItemStack stack = offer.offer;
        if (offer.requirement.fulfills(player) || player.isCreative()) {
            if (countCaps(player) >= offer.cost || player.isCreative()) {
                payCaps(player, offer.cost);
                player.containerMenu.broadcastChanges();

                RandomSource rand = level.random;
                EntityBobmazon bob = new EntityBobmazon(level);
                bob.setPos(player.getX() + rand.nextGaussian() * 10, 300, player.getZ() + rand.nextGaussian() * 10);
                bob.payload = stack.copy();

                level.addFreshEntity(bob);
            } else {
                player.displayClientMessage(Component.literal("[BOBMAZON] Not enough caps!"), false);
            }
        } else {
            player.displayClientMessage(Component.literal("[BOBMAZON] Achievement requirement not met!"), false);
        }
    }

    private int countCaps(Player player) {
        int count = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!stack.isEmpty()) {
                Item item = stack.getItem();

                if (item == ModItems.CAP_FRITZ.get() ||
                        item == ModItems.CAP_KORL.get() ||
                        item == ModItems.CAP_NUKA.get() ||
                        item == ModItems.CAP_QUANTUM.get() ||
                        item == ModItems.CAP_RAD.get() ||
                        item == ModItems.CAP_SPARKLE.get()) {
                    count += stack.getCount();
                }
            }
        }

        return count;
    }

    private void payCaps(Player player, int price) {
        if (price == 0) return;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!stack.isEmpty()) {
                Item item = stack.getItem();

                if (item == ModItems.CAP_FRITZ.get() ||
                        item == ModItems.CAP_KORL.get() ||
                        item == ModItems.CAP_NUKA.get() ||
                        item == ModItems.CAP_QUANTUM.get() ||
                        item == ModItems.CAP_RAD.get() ||
                        item == ModItems.CAP_SPARKLE.get()) {

                    int size = stack.getCount();
                    for (int j = 0; j < size; j++) {
                        stack.shrink(1);
                        price--;

                        if (price == 0) return;
                    }
                }
            }
        }
    }
}