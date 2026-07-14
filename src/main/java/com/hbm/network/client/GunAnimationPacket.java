package com.hbm.network.client;

import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.network.PacketBase;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.HbmAnimations;
import com.hbm.render.anim.BusAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.BiFunction;

public class GunAnimationPacket extends PacketBase implements PacketBase.DecodablePacket{
    private int animationType;
    private int data;
    private int configIndex;
    private long shotTime;

    public GunAnimationPacket() {
        // Для десериализации
    }

    public GunAnimationPacket(int animationType, int data, int configIndex) {
        this.animationType = animationType;
        this.data = data;
        this.configIndex = configIndex;
    }

    public GunAnimationPacket(int animationType, int data, int configIndex, long shotTime) {
        this.animationType = animationType;
        this.data = data;
        this.configIndex = configIndex;
        this.shotTime = shotTime;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(animationType);
        buf.writeInt(data);
        buf.writeInt(configIndex);
        buf.writeLong(shotTime);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleClient));
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack heldItem = mc.player.getMainHandItem();

        if (heldItem.getItem() instanceof GunItem gun) {
            AnimationEnums.GunAnimation animType = AnimationEnums.GunAnimation.values()[animationType];

            if (animType == AnimationEnums.GunAnimation.CYCLE) {
                // Устанавливаем время последнего выстрела
                if (configIndex < gun.lastShot.length) {
                    gun.lastShot[configIndex] = shotTime > 0 ? shotTime : System.currentTimeMillis();
                }

                // Устанавливаем случайное значение для эффектов
                gun.shotRand = mc.player.getRandom().nextDouble();

                // Получаем и вызываем лямбду отдачи
                GunConfig config = gun.getConfig(heldItem, configIndex);
                if (config != null) {
                    Receiver[] receivers = config.getReceivers(heldItem);
                    if (data >= 0 && data < receivers.length) { // data = receiverIndex
                        Receiver rec = receivers[data];
                        BiConsumer<ItemStack, GunItem.LambdaContext> onRecoil = rec.getRecoil(heldItem);
                        if (onRecoil != null) {
                            onRecoil.accept(heldItem, new GunItem.LambdaContext(
                                    config,
                                    mc.player,
                                    mc.player.getInventory(),
                                    data
                            ));
                        }
                    }
                }
            }

            // 1. Устанавливаем NBT
            GunItem.setLastAnim(heldItem, configIndex, animType);
            GunItem.setAnimTimer(heldItem, configIndex, data);
            if (shotTime > 0) {
                gun.lastShot[configIndex] = shotTime;
            }

            // 2. Запускаем анимацию через HbmAnimations
            startHbmAnimation(heldItem, gun, animType, configIndex);

        }
    }

    @OnlyIn(Dist.CLIENT)
    private void startHbmAnimation(ItemStack stack, GunItem gun,
                                   AnimationEnums.GunAnimation animType, int index) {
        try {
            GunConfig config = gun.getConfig(stack, index);
            if (config == null) {
                System.out.println("  ERROR: Config is null!");
                return;
            }

            BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> animFunc = config.getAnims(stack);

            if (animFunc != null) {
                BusAnimation animation = animFunc.apply(stack, animType);

                if (animation != null) {
                    HbmAnimations.startAnimation(stack, animation, index);
                }
            }
        } catch (Exception e) {
            System.err.println("Error starting HbmAnimation:");
            e.printStackTrace();
        }
    }


    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.animationType = buf.readInt();
        this.data = buf.readInt();
        this.configIndex = buf.readInt();
        if (buf.readableBytes() >= 8) { // long = 8 байт
            this.shotTime = buf.readLong();
        } else {
            this.shotTime = 0L; // Для обратной совместимости со старыми пакетами
        }
        return this;
    }
}