package com.hbm.handler.ability;

import com.hbm.config.ToolConfig;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.handler.ThreeInts;
import com.hbm.items.tool.ItemToolAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public interface IToolAreaAbility extends IBaseAbility {

    void onDig(int level, Level world, BlockPos pos, Player player, ItemToolAbility tool);

    default boolean allowsHarvest(int level) {
        return true;
    }

    int SORT_ORDER_BASE = 0;

    IToolAreaAbility NONE = new IToolAreaAbility() {
        @Override
        public String getName() {
            return "";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE;
        }

        @Override
        public void onDig(int level, Level world, BlockPos pos, Player player, ItemToolAbility tool) {
        }
    };

    IToolAreaAbility RECURSION = new IToolAreaAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.recursion";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_VEIN;
        }

        private final int[] radiusAtLevel = {3, 4, 5, 6, 7, 9, 10};

        @Override
        public int levels() {
            return radiusAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + radiusAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 1;
        }

        private final Set<ThreeInts> visited = new HashSet<>();
        private final List<ThreeInts> offsets = new ArrayList<>(26) {{
            for(int dx = -1; dx <= 1; dx++) {
                for(int dy = -1; dy <= 1; dy++) {
                    for(int dz = -1; dz <= 1; dz++) {
                        if(dx != 0 || dy != 0 || dz != 0) {
                            add(new ThreeInts(dx, dy, dz));
                        }
                    }
                }
            }
        }};

        @Override
        public void onDig(int level, Level world, BlockPos pos, Player player, ItemToolAbility tool) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if(!isOreBlock(block) && !ToolConfig.RECURSIVE_STONE) {
                return;
            }

            if(!isOreBlock(block) && !ToolConfig.RECURSIVE_NETHERRACK) {
                return;
            }

            visited.clear();

            recurse(world, pos, pos, player, tool, 0, radiusAtLevel[level], block);
        }

        private void recurse(Level world, BlockPos current, BlockPos referencePos,
                             Player player, ItemToolAbility tool, int depth, int radius, Block referenceBlock) {
            List<ThreeInts> shuffledOffsets = new ArrayList<>(offsets);
            Collections.shuffle(shuffledOffsets);

            for(ThreeInts offset : shuffledOffsets) {
                BlockPos neighbor = current.offset(offset.x(), offset.y(), offset.z());
                breakExtra(world, neighbor, referencePos, player, tool, depth, radius, referenceBlock);
            }
        }

        private void breakExtra(Level world, BlockPos pos, BlockPos referencePos,
                                Player player, ItemToolAbility tool, int depth, int radius, Block referenceBlock) {
            ThreeInts key = new ThreeInts(pos.getX(), pos.getY(), pos.getZ());
            if(visited.contains(key)) return;

            depth++;
            if(depth > ToolConfig.RECURSION_DEPTH) return;

            visited.add(key);

            if(pos.equals(referencePos)) return;

            double distance = Math.sqrt(pos.distSqr(referencePos));
            if(distance > radius) return;

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if(!isSameBlock(block, referenceBlock)) return;

            ItemStack heldItem = player.getMainHandItem();
            if(heldItem.isEmpty()) return;

            tool.breakExtraBlock(world, pos, player, true);

            recurse(world, pos, referencePos, player, tool, depth, radius, referenceBlock);
        }

        private boolean isSameBlock(Block b1, Block b2) {
            if (b1 == b2) return true;

            boolean isOre1 = isOreBlock(b1);
            boolean isOre2 = isOreBlock(b2);

            if (isOre1 && isOre2) {
                return true;
            }

            return !isOre1 && !isOre2;
        }

        private boolean isOreBlock(Block block) {
            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
            String path = Objects.requireNonNull(blockId).getPath().toLowerCase();

            boolean isStandardOre = path.endsWith("_ore") ||
                    path.contains("_ore_") ||
                    path.startsWith("ore_");

            boolean isMineableWithPickaxe = block.defaultBlockState()
                    .is(BlockTags.MINEABLE_WITH_PICKAXE);

            return isStandardOre && isMineableWithPickaxe;
        }

    };

    IToolAreaAbility HAMMER = new IToolAreaAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.hammer";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_HAMMER;
        }

        private final int[] rangeAtLevel = {1, 2, 3, 4};

        @Override
        public int levels() {
            return rangeAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + rangeAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 2;
        }

        @Override
        public void onDig(int level, Level world, BlockPos pos, Player player, ItemToolAbility tool) {
            int range = rangeAtLevel[level];

            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    for (int dz = -range; dz <= range; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos target = pos.offset(dx, dy, dz);
                        tool.breakExtraBlock(world, target, player, true);
                    }
                }
            }
        }
    };

    IToolAreaAbility HAMMER_FLAT = new IToolAreaAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.hammer_flat";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_HAMMER;
        }

        private final int[] rangeAtLevel = {1, 2, 3, 4};

        @Override
        public int levels() {
            return rangeAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + rangeAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 3;
        }

        @Override
        public void onDig(int level, Level world, BlockPos pos, Player player, ItemToolAbility tool) {
            int range = rangeAtLevel[level];

            HitResult hit = getPlayerPOVHitResult(world, player);
            if (hit.getType() != HitResult.Type.BLOCK) return;

            BlockHitResult blockHit = (BlockHitResult) hit;
            Direction side = blockHit.getDirection();

            int xRange = range, yRange = range, zRange;

            zRange = switch (side) {
                case UP, DOWN -> {
                    yRange = 0;
                    yield range;
                }
                case NORTH, SOUTH -> {
                    xRange = range;
                    yield 0;
                }
                case EAST, WEST -> {
                    xRange = 0;
                    yield range;
                }
            };

            for (int dx = -xRange; dx <= xRange; dx++) {
                for (int dy = -yRange; dy <= yRange; dy++) {
                    for (int dz = -zRange; dz <= zRange; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos target = pos.offset(dx, dy, dz);
                        tool.breakExtraBlock(world, target, player, true);
                    }
                }
            }
        }

        private HitResult getPlayerPOVHitResult(Level world, Player player) {
            float f = player.getXRot();
            float f1 = player.getYRot();
            Vec3 vec3 = player.getEyePosition();
            float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
            float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
            float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
            float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
            float f6 = f3 * f4;
            float f7 = f2 * f4;
            double d0 = player.getBlockReach();
            Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
            return world.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        }
    };

    IToolAreaAbility EXPLOSION = new IToolAreaAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.explosion";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_EXPLOSION;
        }

        private final float[] strengthAtLevel = {2.5F, 5F, 10F, 15F};

        @Override
        public int levels() {
            return strengthAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + strengthAtLevel[level] + ")";
        }

        @Override
        public boolean allowsHarvest(int level) {
            return false;
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 4;
        }

        @Override
        public void onDig(int level, Level world, BlockPos pos, Player player, ItemToolAbility tool) {
            float strength = strengthAtLevel[level];

            ExplosionVNT ex = new ExplosionVNT(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, strength, player);

            ex.addAllAttrib(List.of(
                    ExplosionVNT.ExAttrib.ALLDROP,
                    ExplosionVNT.ExAttrib.NOHURT,
                    ExplosionVNT.ExAttrib.NOPARTICLE
            ));

            ex.explode();

            world.explode(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.1F, Level.ExplosionInteraction.NONE);
        }
    };

    IToolAreaAbility[] ABILITIES = {NONE, RECURSION, HAMMER, HAMMER_FLAT, EXPLOSION};

    static IToolAreaAbility getByName(String name) {
        for(IToolAreaAbility ability : ABILITIES) {
            if(ability.getName().equals(name)) {
                return ability;
            }
        }
        return NONE;
    }
}