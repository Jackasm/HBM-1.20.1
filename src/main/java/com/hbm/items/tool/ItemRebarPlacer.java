package com.hbm.items.tool;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.container.ContainerRebarPlacer;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ItemInventory;
import com.hbm.tileentity.block.TileEntityRebar;
import com.hbm.util.ChatBuilder;
import com.hbm.util.InventoryUtil;
import com.hbm.util.ItemStackUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemRebarPlacer extends Item {

    public static List<Block> acceptableConk = new ArrayList<>();

    static {
        acceptableConk.add(ModBlocks.CONCRETE.get());
        acceptableConk.add(ModBlocks.CONCRETE_REBAR.get());
        acceptableConk.add(ModBlocks.CONCRETE_SMOOTH.get());
        acceptableConk.add(ModBlocks.CONCRETE_PILLAR.get());

        // обычный бетон, 16 цветов
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_WHITE.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_ORANGE.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_MAGENTA.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_LIGHT_BLUE.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_YELLOW.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_LIME.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_PINK.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_GRAY.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_LIGHT_GRAY.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_CYAN.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_PURPLE.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_BLUE.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_BROWN.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_GREEN.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_RED.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_BLACK.get());

        // ext, 8 типов
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_EXT_MACHINE.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_EXT_MACHINE_STRIPE.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_EXT_INDIGO.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_EXT_PURPLE.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_EXT_PINK.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_EXT_HAZARD.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_EXT_SAND.get());
        acceptableConk.add(ModBlocks.CONCRETE_COLORED_EXT_BRONZE.get());
    }

    public ItemRebarPlacer(Properties properties) {
        super(properties);
    }

    public static boolean isValidConk(Item item) {
        for (Block block : acceptableConk) {
            if (item == block.asItem()) return true;
        }
        return false;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag();
        if (Objects.requireNonNull(tag).contains("pos")) {
            ItemStack theConk = Objects.requireNonNull(ItemStackUtil.readStacksFromNBT(stack, 1))[0];
            if (!selected || theConk.isEmpty() || !isValidConk(theConk.getItem())) {
                tag.remove("pos");
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player.isShiftKeyDown()) {
            NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable("container.rebarPlacer");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
                    return new ContainerRebarPlacer(id, inv, new InventoryRebarPlacer(player, stack));
                }
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide || player == null) return InteractionResult.SUCCESS;

        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
            ItemStackUtil.addStacksToNBT(stack, new ItemStack(ModBlocks.CONCRETE_REBAR.get()));
        }

        ItemStack theConk = Objects.requireNonNull(ItemStackUtil.readStacksFromNBT(stack, 1))[0];
        boolean hasConk = !theConk.isEmpty() && isValidConk(theConk.getItem());

        if (!hasConk) {
            player.displayClientMessage(
                    ChatBuilder.start("[").color(ChatFormatting.DARK_AQUA)
                            .nextTranslation(this.getDescriptionId() + ".name").color(ChatFormatting.DARK_AQUA)
                            .next("] ").color(ChatFormatting.DARK_AQUA)
                            .next("No valid concrete type set!").color(ChatFormatting.RED)
                            .flush(),
                    false
            );
            return InteractionResult.SUCCESS;
        }

        CompoundTag tag = stack.getTag();

        if (!Objects.requireNonNull(tag).contains("pos")) {
            BlockPos targetPos = pos.relative(side);
            tag.putIntArray("pos", new int[]{targetPos.getX(), targetPos.getY(), targetPos.getZ()});
        } else {
            int rebarLeft = InventoryUtil.countAStackMatches(player, new ComparableStack(ModBlocks.REBAR.get()), true);
            if (rebarLeft <= 0) {
                player.displayClientMessage(
                        ChatBuilder.start("[").color(ChatFormatting.DARK_AQUA)
                                .nextTranslation(this.getDescriptionId() + ".name").color(ChatFormatting.DARK_AQUA)
                                .next("] ").color(ChatFormatting.DARK_AQUA)
                                .next("Out of rebar!").color(ChatFormatting.RED)
                                .flush(),
                        false
                );
                tag.remove("pos");
                return InteractionResult.SUCCESS;
            }

            int[] storedPos = tag.getIntArray("pos");
            BlockPos startPos = new BlockPos(storedPos[0], storedPos[1], storedPos[2]);
            BlockPos endPos = pos.relative(side);

            int minX = Math.min(startPos.getX(), endPos.getX());
            int maxX = Math.max(startPos.getX(), endPos.getX());
            int minY = Math.min(startPos.getY(), endPos.getY());
            int maxY = Math.max(startPos.getY(), endPos.getY());
            int minZ = Math.min(startPos.getZ(), endPos.getZ());
            int maxZ = Math.max(startPos.getZ(), endPos.getZ());

            int rebarUsed = 0;

            outer:
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    for (int x = minX; x <= maxX; x++) {
                        if (rebarLeft <= 0) break outer;
                        BlockPos currentPos = new BlockPos(x, y, z);
                        BlockState state = level.getBlockState(currentPos);
                        if (state.canBeReplaced() && player.mayUseItemAt(currentPos, side, stack)) {
                            level.setBlock(currentPos, ModBlocks.REBAR.get().defaultBlockState(), 3);
                            BlockEntity te = level.getBlockEntity(currentPos);
                            if (te instanceof TileEntityRebar rebarTile) {
                                rebarTile.setup(Block.byItem(theConk.getItem()));
                            }
                            rebarUsed++;
                            rebarLeft--;
                        }
                    }
                }
            }

            InventoryUtil.tryConsumeAStack(
                    player.getInventory().items.toArray(new ItemStack[0]),
                    0,
                    player.getInventory().getContainerSize() - 1,
                    new ComparableStack(ModBlocks.REBAR.get(), rebarUsed)
            );

            player.displayClientMessage(
                    ChatBuilder.start("[").color(ChatFormatting.DARK_AQUA)
                            .nextTranslation(this.getDescriptionId() + ".name").color(ChatFormatting.DARK_AQUA)
                            .next("] ").color(ChatFormatting.DARK_AQUA)
                            .next("Placed " + rebarUsed + " rebar!").color(ChatFormatting.GREEN)
                            .flush(),
                    false
            );

            tag.remove("pos");
            player.containerMenu.broadcastChanges();
        }

        return InteractionResult.SUCCESS;
    }

    public static class InventoryRebarPlacer extends ItemInventory {

        public InventoryRebarPlacer(Player player, ItemStack box) {
            this.player = player;
            this.target = box;
            this.slots = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

            if (!box.hasTag()) box.setTag(new CompoundTag());

            ItemStack[] fromNBT = ItemStackUtil.readStacksFromNBT(box, slots.size());
            if (fromNBT != null) {
                for (int i = 0; i < fromNBT.length && i < slots.size(); i++) {
                    slots.set(i, fromNBT[i] == null ? ItemStack.EMPTY : fromNBT[i]);
                }
            }
        }

        @Override
        public int getContainerSize() {
            return 1;
        }

        public Component getDisplayName() {
            return Component.translatable("container.rebarPlacer");
        }

        public boolean hasCustomName() {
            return target.hasCustomHoverName();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

    }
}