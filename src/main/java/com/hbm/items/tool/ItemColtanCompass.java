package com.hbm.items.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemColtanCompass extends Item {

    public int lastX = 0;
    public int lastZ = 0;
    public long lease = 0;

    public ItemColtanCompass(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("hbm.text.points_towards_the_coltan_deposit"));
        tooltip.add(Component.translatable("hbm.text.the_deposit_is_a_large_area_where_coltan_ore_spa"));
        tooltip.add(Component.translatable("hbm.text.it_s_not_one_large_blob_of_ore_on_that_exact_loc"));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                              int slot, boolean selected) {
        if (level.isClientSide && entity instanceof Player player) {
            if (stack.hasTag()) {
                lastX = Objects.requireNonNull(stack.getTag()).getInt("colX");
                lastZ = stack.getTag().getInt("colZ");
                lease = System.currentTimeMillis() + 1000;

                Vec3 vec = new Vec3(entity.getX() - lastX, 0, entity.getZ() - lastZ);
                int distance = (int) vec.length();

                // Отображаем расстояние в Action Bar
                player.displayClientMessage(
                        Component.literal(distance + "m"),
                        true // true = в Action Bar (над хотбаром)
                );

            }

            if (ItemColtanCompass.this.lease < System.currentTimeMillis()) {
                lastX = 0;
                lastZ = 0;
            }

        } else {
            if (!stack.hasTag()) {
                stack.setTag(new net.minecraft.nbt.CompoundTag());
                if (level instanceof ServerLevel serverLevel) {
                    long seed = serverLevel.getSeed();
                    RandomSource colRand = RandomSource.create(seed + 5);
                    int colX = (int) (colRand.nextGaussian() * 1500);
                    int colZ = (int) (colRand.nextGaussian() * 1500);

                    Objects.requireNonNull(stack.getTag()).putInt("colX", colX);
                    stack.getTag().putInt("colZ", colZ);
                }
            }
        }
    }
}