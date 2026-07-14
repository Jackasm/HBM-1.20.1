package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModInk extends ItemArmorMod {

    public ItemModInk(Properties properties) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.LIGHT_PURPLE + "10% chance to nullify damage"));
        tooltip.add(Component.literal(ChatFormatting.LIGHT_PURPLE + "Flowers!"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public void modDamage(LivingHurtEvent event, ItemStack armor) {
        if (event.getEntity().level().random.nextInt(10) == 0) {
            event.setAmount(0);

            if (!event.getEntity().level().isClientSide) {
                Level level = event.getEntity().level();
                BlockPos pos = event.getEntity().blockPosition();

                if (level.random.nextInt(10) == 0) {
                    level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
                            new ItemStack(Blocks.DANDELION)));
                }

                Block[] flowers = new Block[] {
                        Blocks.POPPY,
                        Blocks.BLUE_ORCHID,
                        Blocks.ALLIUM,
                        Blocks.AZURE_BLUET,
                        Blocks.RED_TULIP,
                        Blocks.ORANGE_TULIP,
                        Blocks.WHITE_TULIP,
                        Blocks.PINK_TULIP,
                        Blocks.OXEYE_DAISY
                };

                Block flower = flowers[level.random.nextInt(flowers.length)];
                level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
                        new ItemStack(flower)));
            }
        }
    }
}