package com.hbm.entity.missile;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.items.ModItems;

import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class EntityMissileShuttle extends EntityMissileBaseNT {

    public EntityMissileShuttle(EntityType<? extends EntityMissileShuttle> entityType, Level level) {
        super(entityType, level);
    }

    public EntityMissileShuttle(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileShuttle> entityType) {
        super(level, x, y, z, targetX, targetZ, entityType);
    }

    @Override
    public void onMissileImpact(HitResult mop) {
        ExplosionVNT explosion = new ExplosionVNT(level(), getX() + 0.5F, getY() + 0.5F, getZ() + 0.5F, 20.0F);
        explosion.setBlockAllocator(new BlockAllocatorStandard(64));
        explosion.addAllAttrib(new ExplosionVNT.ExAttrib[]{ExplosionVNT.ExAttrib.NOSOUND, ExplosionVNT.ExAttrib.NOPARTICLE});
        explosion.explode();

        CompoundTag data = new CompoundTag();
        data.putString("type", "rbmkmush");
        data.putFloat("scale", 10);
        PacketDispatcher.sendAuxParticleNT(data, getX() + 0.5, getY() + 1, getZ() + 0.5, null);

        level().playSound(null, getX(), getY(), getZ(),
                ModSounds.ROBIN_EXPLOSION.get(), SoundSource.PLAYERS, 4.0F,
                (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
    }

    @Override
    public List<ItemStack> getDebris() {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(ModItems.PLATE_STEEL.get(), 8));
        list.add(new ItemStack(ModItems.THRUSTER_MEDIUM.get(), 2));
        list.add(new ItemStack(ModItems.CAN_EMPTY.get(), 1));
        list.add(new ItemStack(Blocks.GLASS_PANE.asItem(), 2));
        return list;
    }

    @Override
    public ItemStack getDebrisRareDrop() {
        return new ItemStack(ModItems.MISSILE_GENERIC.get());
    }

    @Override
    public String getUnlocalizedName() {
        return "radar.target.shuttle";
    }

    @Override
    public ItemStack getMissileItemForInfo() {
        return new ItemStack(ModItems.MISSILE_SHUTTLE.get());
    }
}