package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.tileentity.bomb.TileEntityLaunchPad;
import com.hbm.network.client.LaunchPadVisualPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/**
 * Non-destructive headless regression for the launch-pad's distant model.
 *
 * No missile is launched, no block is placed, and no chunk is changed.
 * Tests validate the vanilla chunk/update tag round trip and slot-0 changes.
 * Actual network delivery to distant clients requires multiplayer acceptance.
 */
public final class LaunchPadVisualSyncTestRunner extends TestRunner {

    private static final BlockPos POS = new BlockPos(8, 150, 8);

    public LaunchPadVisualSyncTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (tickCount++ != 0) return true;

        var blockState = ModBlocks.LAUNCH_PAD.get().defaultBlockState();
        TileEntityLaunchPad source = new TileEntityLaunchPad(POS, blockState);
        TileEntityLaunchPad observer = new TileEntityLaunchPad(POS, blockState);

        // Unloaded tile: an empty snapshot must clear a formerly visible model.
        observer.handleUpdateTag(source.getUpdateTag());
        check("initial_empty", observer.toRender == null);

        // A newly tracking client must be able to render an already loaded pad.
        source.getInventory().setStackInSlot(0, new ItemStack(ModItems.MISSILE_NUCLEAR.get(), 2));
        CompoundTag loaded = source.getUpdateTag();
        check("tag_contains_missile", loaded.contains("hbmRenderMissile", 10));
        check("tag_only_visual_state", loaded.getAllKeys().size() == 1);
        observer.handleUpdateTag(loaded);
        check("initial_missile_visible", observer.toRender != null && !observer.toRender.isEmpty());
        check("initial_missile_item", observer.toRender != null
                && observer.toRender.getItem() == ModItems.MISSILE_NUCLEAR.get());
        check("visual_stack_singular", observer.toRender != null && observer.toRender.getCount() == 1);

        // One missile is removed: one more remains, the pad must still render it.
        source.getInventory().extractItem(0, 1, false);
        observer.handleUpdateTag(source.getUpdateTag());
        check("partial_launch_still_visible", observer.toRender != null
                && observer.toRender.getItem() == ModItems.MISSILE_NUCLEAR.get());

        // Final missile removed: the old static model must disappear.
        source.getInventory().extractItem(0, 1, false);
        CompoundTag cleared = source.getUpdateTag();
        check("empty_tag_after_launch", !cleared.contains("hbmRenderMissile"));
        observer.handleUpdateTag(cleared);
        check("model_cleared_after_launch", observer.toRender == null);

        // Replacing a missile after launch must become visible again.
        source.getInventory().setStackInSlot(0, new ItemStack(ModItems.MISSILE_GENERIC.get()));
        observer.handleUpdateTag(source.getUpdateTag());
        check("reload_visible", observer.toRender != null
                && observer.toRender.getItem() == ModItems.MISSILE_GENERIC.get());

        // The inherited vanilla update-packet factory uses this compact tag.
        var updatePacket = source.getUpdatePacket();
        check("vanilla_update_packet_created", updatePacket != null);
        check("vanilla_packet_payload", updatePacket != null && updatePacket.getTag() != null
                && updatePacket.getTag().contains("hbmRenderMissile", 10));

        // Dedicated long-range event: round-trip a tiny packet independently
        // of chunk tracking and the 50-block full-machine BufPacket.
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            LaunchPadVisualPacket installed = new LaunchPadVisualPacket(
                    POS, new ItemStack(ModItems.MISSILE_NUCLEAR.get()));
            installed.encode(buffer);
            LaunchPadVisualPacket decoded = new LaunchPadVisualPacket();
            decoded.decode(buffer);
            check("distance_packet_position", POS.equals(decoded.getBlockPos()));
            check("distance_packet_item", decoded.getMissileItemId()
                    == net.minecraft.world.item.Item.getId(ModItems.MISSILE_NUCLEAR.get()));
        } finally {
            buffer.release();
        }

        FriendlyByteBuf clearedBuffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            LaunchPadVisualPacket clearedPacket = new LaunchPadVisualPacket(POS, ItemStack.EMPTY);
            clearedPacket.encode(clearedBuffer);
            LaunchPadVisualPacket decodedEmpty = new LaunchPadVisualPacket();
            decodedEmpty.decode(clearedBuffer);
            check("distance_packet_empty", decodedEmpty.getMissileItemId() == 0);
            check("distance_packet_no_inventory", clearedBuffer.writerIndex() <= 10);
        } finally {
            clearedBuffer.release();
        }

        metric("cases", 16);
        finish(checks.stream().allMatch(result -> result.pass));
        return true;
    }
}
