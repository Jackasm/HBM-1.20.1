package com.hbm.items.tool;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.api.block.IToolable;

import com.hbm.items.IAnimatedItem;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.render.anim.AnimationEnums.ToolAnimation;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.sound.ModSounds;
import com.hbm.util.EntityDamageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemBoltgun extends Item implements IAnimatedItem<ToolAnimation> {

    public ItemBoltgun(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        Level level = player.level();
        if (!entity.isAlive()) return false;

        ItemStack[] bolts = new ItemStack[]{
                new ItemStack(ModItems.BOLT_SPIKE.get()),
                new ItemStack(ModItems.BOLT_STEEL.get()),
                new ItemStack(ModItems.BOLT_TUNGSTEN.get()),
                new ItemStack(ModItems.BOLT_DURA_STEEL.get())
        };

        for (ItemStack bolt : bolts) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack slot = player.getInventory().getItem(i);

                if (!slot.isEmpty()) {
                    if (slot.getItem() == bolt.getItem() && slot.getDamageValue() == bolt.getDamageValue()) {
                        if (!level.isClientSide) {
                            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                    ModSounds.BOLTGUN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            slot.shrink(1);
                            player.containerMenu.broadcastChanges();
                            EntityDamageUtil.attackEntityFromIgnoreIFrame(entity, level.damageSources().playerAttack(player), 10F);

                            if (!entity.isAlive() && entity instanceof Player && player instanceof ServerPlayer serverPlayer) {
                                ModCriteriaTriggers.GO_FISH.trigger(serverPlayer);
                            }

                            CompoundTag data = new CompoundTag();
                            data.putString("type", "vanillaExt");
                            data.putString("mode", "largeexplode");
                            data.putFloat("size", 1F);
                            data.putByte("count", (byte) 1);

                            // Отправка пакета
                            PacketDispatcher.sendToAllAround(
                                    new AuxParticlePacketNT(data, entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ()),
                                    level,
                                    BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()),
                                    50
                            );

                            playAnimation(player, ToolAnimation.SWING);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        Direction side = context.getClickedFace();
        float fX = (float) context.getClickLocation().x - pos.getX();
        float fY = (float) context.getClickLocation().y - pos.getY();
        float fZ = (float) context.getClickLocation().z - pos.getZ();

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IToolable toolable) {
            if (toolable.onScrew(level, player, pos, side, fX, fY, fZ, IToolable.ToolType.BOLT)) {
                if (!level.isClientSide) {
                    level.playSound(null, Objects.requireNonNull(player).getX(), player.getY(), player.getZ(),
                            ModSounds.BOLTGUN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.containerMenu.broadcastChanges();

                    double off = 0.25;

                    CompoundTag data = new CompoundTag();
                    data.putString("type", "vanillaExt");
                    data.putString("mode", "largeexplode");
                    data.putFloat("size", 1F);
                    data.putByte("count", (byte) 1);

                    PacketDispatcher.sendToAllAround(
                            new AuxParticlePacketNT(data,
                                    pos.getX() + fX + side.getStepX() * off,
                                    pos.getY() + fY + side.getStepY() * off,
                                    pos.getZ() + fZ + side.getStepZ() * off),
                            level,
                            pos,
                            50
                    );

                    playAnimation(player, ToolAnimation.SWING);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public Class<ToolAnimation> getEnum() {
        return ToolAnimation.class;
    }

    @Override
    public BusAnimation getAnimation(ToolAnimation type, ItemStack stack) {
        return new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(1, 0, 1, 50)
                        .addPos(0, 0, 1, 100));
    }

    @Override
    public boolean shouldPlayerModelAim(ItemStack stack) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playAnimation(Player player, ToolAnimation anim) {
        IAnimatedItem.super.playAnimation(player, anim);
    }
}