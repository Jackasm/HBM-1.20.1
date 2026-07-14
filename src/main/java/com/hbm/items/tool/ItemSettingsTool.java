package com.hbm.items.tool;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.HbmKeybinds;
import com.hbm.interfaces.ICopiable;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.PlayerInformPacket;
import com.hbm.util.ChatBuilder;
import com.hbm.util.Either;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemSettingsTool extends Item {

    public ItemSettingsTool(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                              int slot, boolean selected) {
        if (!(entity instanceof Player player) || level.isClientSide) return;
        if (player.getMainHandItem() != stack && player.getOffhandItem() != stack) return;
        if (!stack.hasTag()) return;

        CompoundTag tag = stack.getTag();
        int delay = Objects.requireNonNull(tag).getInt("inputDelay");
        delay++;
        ListTag displayInfo = tag.getList("displayInfo", 10);

        if (HbmPlayerProps.getData(player).getKeyPressed(HbmKeybinds.EnumKeybind.TOOL_ALT) && delay > 4) {
            int index = tag.getInt("copyIndex") + 1;
            if (index > displayInfo.size() - 1) index = 0;
            tag.putInt("copyIndex", index);
            delay = 0;
        }

        tag.putInt("inputDelay", delay);
        if (level.getGameTime() % 5 != 0) return;

        if (!displayInfo.isEmpty() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            for (int j = 0; j < displayInfo.size(); j++) {
                CompoundTag nbt = displayInfo.getCompound(j);
                ChatFormatting format = tag.getInt("copyIndex") == j ? ChatFormatting.AQUA : ChatFormatting.YELLOW;
                String info = nbt.getString("info");
                PacketDispatcher.sendTo(
                        new PlayerInformPacket(
                                ChatBuilder.startTranslation(info).color(format).flush(),
                                897 + j, 4000
                        ),
                        serverPlayer
                );
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Can copy the settings (filters, fluid ID, etc) of machines"));
        tooltip.add(Component.literal("Shift right-click to copy, right click to paste"));
        tooltip.add(Component.literal("Ctrl click on pipes to paste settings to multiple pipes"));
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            if (Objects.requireNonNull(nbt).contains("tileName")) {
                tooltip.add(Component.translatable(nbt.getString("tileName") + ".name")
                        .withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add(Component.literal("None").withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        Either<BlockEntity, Block> schrodinger = getCopyInfoSource(level, pos);
        if (schrodinger == null) return InteractionResult.PASS;

        ICopiable copiable = schrodinger.cast();

        if (player.isShiftKeyDown()) {
            CompoundTag tag = copiable.getSettings(level, pos);
            if (tag != null) {
                stack.setTag(tag);
                tag.putString("tileName", copiable.getSettingsSourceID(schrodinger));
                tag.putInt("copyIndex", 0);
                tag.putInt("inputDelay", 0);
                String[] info = copiable.infoForDisplay(level, pos);
                if (info != null) {
                    ListTag displayInfo = new ListTag();
                    for (String str : info) {
                        CompoundTag nbt = new CompoundTag();
                        nbt.putString("info", str);
                        displayInfo.add(nbt);
                    }
                    tag.put("displayInfo", displayInfo);
                }

                if (level.isClientSide) {
                    player.displayClientMessage(
                            ChatBuilder.start("[").color(ChatFormatting.DARK_AQUA)
                                    .nextTranslation(this.getDescriptionId() + ".name").color(ChatFormatting.DARK_AQUA)
                                    .next("] ").color(ChatFormatting.DARK_AQUA)
                                    .next("Copied settings of " + copiable.getSettingsSourceDisplay(schrodinger)).color(ChatFormatting.AQUA)
                                    .flush(),
                            false
                    );
                }
            } else {
                player.displayClientMessage(
                        ChatBuilder.start("[").color(ChatFormatting.DARK_AQUA)
                                .nextTranslation(this.getDescriptionId() + ".name").color(ChatFormatting.DARK_AQUA)
                                .next("] ").color(ChatFormatting.DARK_AQUA)
                                .next("Copy failed, machine has no settings tool support: " + copiable.getSettingsSourceDisplay(schrodinger)).color(ChatFormatting.RED)
                                .flush(),
                        false
                );
            }
            return InteractionResult.SUCCESS;

        } else if (stack.hasTag()) {
            int index = Objects.requireNonNull(stack.getTag()).getInt("copyIndex");
            copiable.pasteSettings(stack.getTag(), index, level, player, pos);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Nullable
    private Either<BlockEntity, Block> getCopyInfoSource(Level level, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof ICopiable) {
            return Either.left(te);
        }

        Block block = level.getBlockState(pos).getBlock();
        if (block instanceof ICopiable) {
            return Either.right(block);
        }

        return null;
    }
}