package com.hbm.items.tool;

import com.hbm.entity.logic.EntityBomber;
import com.hbm.sound.ModSounds;
import com.hbm.world.WorldUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBombCaller extends Item {

    public enum Type {
        CARPET(0, "Carpet bombing", false),
        NAPALM(1, "Napalm", false),
        GAS(2, "Poison gas", false),
        ORANGE(3, "Agent orange", false),
        NUKE(4, "Atomic bomb", true),
        STINGER(5, "VT stinger rockets", true),
        BOXCAR(6, "PIP OH GOD", true),
        CLOUD(7, "Cloud the cloud oh god the cloud", true);

        public final int meta;
        public final String name;
        public final boolean isNuclear;

        Type(int meta, String name, boolean isNuclear) {
            this.meta = meta;
            this.name = name;
            this.isNuclear = isNuclear;
        }
    }

    private final Type type;

    public ItemBombCaller(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Aim & click to call an airstrike!"));
        tooltip.add(Component.literal("Type: " + type.name));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        HitResult hitResult = player.pick(500, 1.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHit)) {
            return InteractionResultHolder.pass(stack);
        }

        int x = blockHit.getBlockPos().getX();
        int y = blockHit.getBlockPos().getY();
        int z = blockHit.getBlockPos().getZ();

        if (!level.isClientSide) {
            EntityBomber bomber = switch (type) {
                case NAPALM -> EntityBomber.statFacNapalm(level, x, y, z);
                case GAS -> EntityBomber.statFacChlorine(level, x, y, z);
                case ORANGE -> EntityBomber.statFacOrange(level, x, y, z);
                case NUKE -> EntityBomber.statFacABomb(level, x, y, z);
                case STINGER -> EntityBomber.statFacStinger(level, x, y, z);
                case BOXCAR -> EntityBomber.statFacBoxcar(level, x, y, z);
                case CLOUD -> EntityBomber.statFacPC(level, x, y, z);
                default -> EntityBomber.statFacCarpet(level, x, y, z);
            };

            WorldUtil.loadAndSpawnEntityInWorld(bomber);
            player.sendSystemMessage(Component.literal("Called in airstrike!"));
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TECH_BLEEP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        stack.shrink(1);
        return InteractionResultHolder.success(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@NotNull ItemStack stack) {
        return type.isNuclear;
    }
}