package com.hbm.items.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.machine.TileEntitySolarMirror;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemMirrorTool extends Item {

    public ItemMirrorTool(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block == ModBlocks.MACHINE_SOLAR_BOILER.get()) {
            BlockPos corePos = ((BlockDummyable) block).findCore(level, pos);

            if (corePos != null && !level.isClientSide) {
                stack.getOrCreateTag().putInt("posX", corePos.getX());
                stack.getOrCreateTag().putInt("posY", corePos.getY() + 1);
                stack.getOrCreateTag().putInt("posZ", corePos.getZ());

                player.displayClientMessage(
                        Component.translatable(this.getDescriptionId() + ".linked")
                                .withStyle(ChatFormatting.YELLOW),
                        false
                );
            }
            return InteractionResult.SUCCESS;
        }

        if (block == ModBlocks.SOLAR_MIRROR.get() && stack.hasTag()) {
            if (!level.isClientSide) {
                BlockEntity te = level.getBlockEntity(pos);
                if (te instanceof TileEntitySolarMirror mirror) {
                    int tx = Objects.requireNonNull(stack.getTag()).getInt("posX");
                    int ty = stack.getTag().getInt("posY");
                    int tz = stack.getTag().getInt("posZ");

                    Vec3 delta = new Vec3(pos.getX() - tx, pos.getY() - ty, pos.getZ() - tz);
                    boolean withinReach = delta.length() <= 100;
                    boolean withinAngle = (pos.getX() - tx) * (pos.getX() - tx) + (pos.getZ() - tz) * (pos.getZ() - tz) >= (pos.getY() - ty) * (pos.getY() - ty);

                    if (!withinReach) {
                        player.displayClientMessage(
                                Component.translatable(this.getDescriptionId() + ".reach")
                                        .withStyle(ChatFormatting.RED),
                                false
                        );
                    } else if (!withinAngle) {
                        player.displayClientMessage(
                                Component.translatable(this.getDescriptionId() + ".angle")
                                        .withStyle(ChatFormatting.RED),
                                false
                        );
                    } else {
                        mirror.setTarget(tx, ty, tz);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String descKey = this.getDescriptionId() + ".desc";
        String[] lines = Component.translatable(descKey).getString().split("\\n");
        for (String line : lines) {
            tooltip.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EquipmentSlot.MAINHAND) {
            // Создаем изменяемую копию
            Multimap<Attribute, AttributeModifier> copy = HashMultimap.create(multimap);
            copy.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 2, AttributeModifier.Operation.ADDITION));
            return copy;
        }
        return multimap;
    }
}