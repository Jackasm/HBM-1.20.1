package com.hbm.items.tool;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.TileEntityLockableBase;
import com.hbm.util.CompatExternal;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class ItemLock extends ItemKeyPin {

    public double lockMod = 0.1D;

    public ItemLock(Properties properties, double mod) {
        super(properties);
        this.lockMod = mod;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (getPins(stack) == 0) return InteractionResult.PASS;

        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockEntity te = CompatExternal.getCoreFromPos(level, context.getClickedPos());
        if (!(te instanceof TileEntityLockableBase lockable)) return InteractionResult.PASS;

        if (lockable.isLocked()) return InteractionResult.FAIL;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        lockable.setPins(getPins(stack));
        lockable.lock();
        lockable.setMod(lockMod);

        level.playSound(null,
                context.getClickedPos().getX() + 0.5,
                context.getClickedPos().getY() + 0.5,
                context.getClickedPos().getZ() + 0.5,
                ModSounds.PADLOCK.get(),
                SoundSource.BLOCKS, 1.0F, 1.0F);

        stack.shrink(1);

        return InteractionResult.CONSUME;
    }
}