package com.hbm.network.client;

import com.hbm.items.IAnimatedItem;
import com.hbm.items.armor.ArmorTrenchmaster;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.GunItem.LambdaContext;
import com.hbm.network.PacketBase;
import com.hbm.render.anim.AnimationEnums.GunAnimation;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.anim.HbmAnimations.Animation;
import com.hbm.util.EnumUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class HbmAnimationPacket extends PacketBase  implements PacketBase.DecodablePacket {

    public short type;
    public int receiverIndex;
    public int itemIndex;

    public HbmAnimationPacket() { }

    public HbmAnimationPacket(int type) {
        this.type = (short) type;
        this.receiverIndex = 0;
        this.itemIndex = 0;
    }

    public HbmAnimationPacket(int type, int rec) {
        this.type = (short) type;
        this.receiverIndex = rec;
        this.itemIndex = 0;
    }

    public HbmAnimationPacket(int type, int rec, int gun) {
        this.type = (short) type;
        this.receiverIndex = rec;
        this.itemIndex = gun;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeShort(type);
        buf.writeInt(receiverIndex);
        buf.writeInt(itemIndex);
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.type = buf.readShort();
        this.receiverIndex = buf.readInt();
        this.itemIndex = buf.readInt();
        return this;
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            int slot = player.getInventory().selected;

            if (stack.isEmpty()) return;

            if (stack.getItem() instanceof GunItem) {
                handleSedna(player, stack, slot, GunAnimation.values()[type], receiverIndex, itemIndex);
            } else if (stack.getItem() instanceof IAnimatedItem) {
                handleItem(player, stack, slot, type, receiverIndex, itemIndex);
            }
        });
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleSedna(Player player, ItemStack stack, int slot, GunAnimation type, int receiverIndex, int gunIndex) {
        GunItem gun = (GunItem) stack.getItem();
        GunConfig config = gun.getConfig(stack, gunIndex);

        if (type == GunAnimation.CYCLE) {
            if (gunIndex < gun.lastShot.length) gun.lastShot[gunIndex] = System.currentTimeMillis();
            gun.shotRand = player.level().random.nextDouble();

            Receiver[] receivers = config.getReceivers(stack);
            if (receiverIndex >= 0 && receiverIndex < receivers.length) {
                Receiver rec = receivers[receiverIndex];
                BiConsumer<ItemStack, LambdaContext> onRecoil = rec.getRecoil(stack);
                if (onRecoil != null) {
                    onRecoil.accept(stack, new LambdaContext(config, player, player.getInventory(), receiverIndex));
                }
            }
        }

        BiFunction<ItemStack, GunAnimation, BusAnimation> anims = config.getAnims(stack);
        BusAnimation animation = Objects.requireNonNull(anims).apply(stack, type);

        if (animation == null && (type == GunAnimation.ALT_CYCLE || type == GunAnimation.CYCLE_EMPTY)) {
            animation = anims.apply(stack, GunAnimation.CYCLE);
        }

        if (animation != null) {
            boolean isReloadAnimation = type == GunAnimation.RELOAD || type == GunAnimation.RELOAD_CYCLE;
            if (isReloadAnimation && ArmorTrenchmaster.isTrenchMaster(player)) {
                animation.setTimeMult(0.5D);
            }
            HbmAnimations.hotbar[slot][gunIndex] = new Animation(stack.getItem().getDescriptionId(), System.currentTimeMillis(), animation, isReloadAnimation && config.getReloadAnimSequential(stack));


        }
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void handleItem(Player player, ItemStack stack, int slot, short type, int receiverIndex, int itemIndex) {
        IAnimatedItem item = (IAnimatedItem) stack.getItem();
        Class<?> animClass = item.getEnum();

        // Получаем enum константу
        Enum<?> animType = EnumUtil.grabEnumSafely((Class) animClass, (int) type);

        // Приводим IAnimatedItem к сырому типу для вызова getAnimation
        BusAnimation animation = item.getAnimation(animType, stack);

        if (animation != null) {
            HbmAnimations.hotbar[slot][itemIndex] = new Animation(stack.getItem().getDescriptionId(), System.currentTimeMillis(), animation);
        }
    }
}