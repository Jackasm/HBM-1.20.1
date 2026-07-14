package com.hbm.event;

import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RightClickWithItemHandler {
    private static final Map<UUID, MaskHoldInfo> holdingPlayers = new HashMap<>();

    private static class MaskHoldInfo {
        public final InteractionHand hand;
        public final long startTime;
        public boolean soundPlayed;
        public boolean particlesStarted;
        public int particleTickCount;

        public MaskHoldInfo(InteractionHand hand) {
            this.hand = hand;
            this.startTime = System.currentTimeMillis();
            this.soundPlayed = false;
            this.particlesStarted = false;
            this.particleTickCount = 0;
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        InteractionHand hand = event.getHand();

        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND || !stack.is(ModArmorItems.MASK_DRY.get())) {
            return;
        }

        startHoldingTracking(player, hand);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        Level level = player.level();
        UUID playerId = player.getUUID();

        if (holdingPlayers.containsKey(playerId)) {
            MaskHoldInfo holdInfo = holdingPlayers.get(playerId);

            ItemStack heldStack = player.getItemInHand(holdInfo.hand);

            boolean shouldReset = !heldStack.is(ModArmorItems.MASK_DRY.get());

            if (holdInfo.hand != InteractionHand.MAIN_HAND) {
                shouldReset = true;
            }

            if (shouldReset) {
                holdingPlayers.remove(playerId);
                return;
            }

            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - holdInfo.startTime;

            if (elapsed >= 500) {
                if (!holdInfo.soundPlayed) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.8F, 1.1F);
                    holdInfo.soundPlayed = true;
                }

                if (elapsed < 3000 && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    if (!holdInfo.particlesStarted) {
                        holdInfo.particlesStarted = true;
                    }

                    if (holdInfo.particleTickCount % 2 == 0) {
                        double x = player.getX();
                        double y = player.getY() + 0.7;
                        double z = player.getZ();

                        float yaw = player.getYRot() * ((float) Math.PI / 180F);
                        float pitch = player.getXRot() * ((float) Math.PI / 180F);

                        double forwardX = -Math.sin(yaw) * 0.8;
                        double forwardZ = Math.cos(yaw) * 0.8;
                        double forwardY = -Math.sin(pitch) * 0.8;

                        double particleX = x + forwardX;
                        double particleY = y + forwardY + 0.1;
                        double particleZ = z + forwardZ;

                        serverLevel.sendParticles(
                                ParticleTypes.FALLING_HONEY,
                                particleX, particleY, particleZ,
                                3, 0.05, 0.05, 0.05, 0.1
                        );
                    }
                    holdInfo.particleTickCount++;
                }
            }

            if (elapsed >= 3000) {
                transformToPissMask(player, heldStack, holdInfo.hand, level);
                holdingPlayers.remove(playerId);
            }
        }
    }

    private static void startHoldingTracking(Player player, InteractionHand hand) {
        UUID playerId = player.getUUID();

        if (holdingPlayers.containsKey(playerId)) {
            MaskHoldInfo existing = holdingPlayers.get(playerId);
            if (existing.hand != hand) {
                holdingPlayers.put(playerId, new MaskHoldInfo(hand));
            }
        } else {
            holdingPlayers.put(playerId, new MaskHoldInfo(hand));
        }
    }

    private static void transformToPissMask(Player player, ItemStack stack, InteractionHand hand, Level level) {
        ItemStack pissMask = new ItemStack(ModArmorItems.MASK_PISS.get());
        if (stack.hasTag()) {
            pissMask.setTag(Objects.requireNonNull(stack.getTag()).copy());
        }
        pissMask.setDamageValue(stack.getDamageValue());
        pissMask.getOrCreateTag().putInt("remainingTime", 6000);

        player.setItemInHand(hand, pissMask);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);

    }
}
