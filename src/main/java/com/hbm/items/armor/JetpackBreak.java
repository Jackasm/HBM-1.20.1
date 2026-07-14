package com.hbm.items.armor;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import com.hbm.util.ArmorUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class JetpackBreak extends JetpackFueledBase {

    public JetpackBreak(ArmorMaterial material, Type type, Properties properties, Supplier<FluidTypeHBM> fuel, int maxFuel) {
        super(material, type, properties, fuel, maxFuel);
    }

    @Override
    public void onJetpackTick(ItemStack stack, Level level, Player player) {
        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
        if (props == null) return;

        if (!level.isClientSide && getFuel(stack) > 0 && (props.isJetpackActive() || (!player.onGround() && !player.isShiftKeyDown() && props.isBackpackEnabled()))) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "jetpack");
            data.putInt("player", player.getId());
            PacketDispatcher.sendAuxParticleNT(data, player.getX(), player.getY(), player.getZ(), player);
        }

        if (getFuel(stack) > 0) {
            if (props.isJetpackActive()) {
                player.fallDistance = 0;
                if (player.getDeltaMovement().y < 0.4D) {
                    player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + 0.1D, player.getDeltaMovement().z);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.FLAMETHROWER_SHOOT.get(), SoundSource.PLAYERS, 0.25F, 1.5F);
                useUpFuel(player, stack, 5);
            } else if (!player.isShiftKeyDown() && !player.onGround() && props.isBackpackEnabled()) {
                player.fallDistance = 0;
                if (player.getDeltaMovement().y < -1) {
                    player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + 0.2D, player.getDeltaMovement().z);
                } else if (player.getDeltaMovement().y < -0.1) {
                    player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + 0.1D, player.getDeltaMovement().z);
                } else if (player.getDeltaMovement().y < 0) {
                    player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                }
                player.setDeltaMovement(player.getDeltaMovement().x * 1.025D, player.getDeltaMovement().y, player.getDeltaMovement().z * 1.025D);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.FLAMETHROWER_SHOOT.get(), SoundSource.PLAYERS, 0.25F, 1.5F);
                useUpFuel(player, stack, 10);
            }
            ArmorUtil.resetFlightTime(player);
        }
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        return "hbm:textures/models/armor/jetpack_blue.png";
    }
}