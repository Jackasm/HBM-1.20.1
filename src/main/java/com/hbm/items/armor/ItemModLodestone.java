package com.hbm.items.armor;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModLodestone extends ItemArmorMod {

    private final int range;

    public ItemModLodestone(Properties properties, int range) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
        this.range = range;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.DARK_GRAY + "Attracts nearby items"));
        tooltip.add(Component.literal(ChatFormatting.DARK_GRAY + "Item attraction range: " + range));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public void modUpdate(Entity entity, ItemStack armor) {
        if (!(entity instanceof Player player)) return;

        // No magnet if keybind toggled
        if (!HbmPlayerProps.getData(player).isMagnetActive()) return;

        Level level = entity.level();
        AABB aabb = entity.getBoundingBox().inflate(range, range, range);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, aabb);

        for (ItemEntity item : items) {
            Vec3 vec = new Vec3(
                    entity.getX() - item.getX(),
                    entity.getY() - item.getY(),
                    entity.getZ() - item.getZ()
            );
            vec = vec.normalize();

            item.setDeltaMovement(
                    item.getDeltaMovement().x + vec.x * 0.05,
                    item.getDeltaMovement().y + vec.y * 0.05,
                    item.getDeltaMovement().z + vec.z * 0.05
            );

            if (vec.y > 0 && item.getDeltaMovement().y < 0.04) {
                item.setDeltaMovement(item.getDeltaMovement().x, item.getDeltaMovement().y + 0.2, item.getDeltaMovement().z);
            }
        }
    }
}