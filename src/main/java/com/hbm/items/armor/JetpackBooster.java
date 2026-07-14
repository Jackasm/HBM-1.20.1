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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class JetpackBooster extends JetpackFueledBase {

    public JetpackBooster(ArmorMaterial material, Type type, Properties properties, Supplier<FluidTypeHBM> fuel, int maxFuel) {
        super(material, type, properties, fuel, maxFuel);
    }

    @Override
    public void onJetpackTick(ItemStack stack, Level level, Player player) {
        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
        if (props == null) return;

        if (!level.isClientSide && getFuel(stack) > 0 && props.isJetpackActive()) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "jetpack");
            data.putInt("player", player.getId());
            data.putInt("mode", 1);
            PacketDispatcher.sendAuxParticleNT(data, player.getX(), player.getY(), player.getZ(), player);
        }

        if (getFuel(stack) > 0 && props.isJetpackActive()) {
            if (player.getDeltaMovement().y < 0.6D) {
                player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + 0.1D, player.getDeltaMovement().z);
            }
            Vec3 look = player.getLookAngle();
            Vec3 motion = player.getDeltaMovement();
            if (motion.length() < 5) {
                player.setDeltaMovement(
                        motion.x + look.x * 0.25,
                        motion.y + look.y * 0.25,
                        motion.z + look.z * 0.25
                );
                if (look.y > 0) {
                    player.fallDistance = 0;
                }
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.FLAMETHROWER_SHOOT.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
            useUpFuel(player, stack, 1);
            ArmorUtil.resetFlightTime(player);
        }
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        return "hbm:textures/models/armor/jetpack.png";
    }
}