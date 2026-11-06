package com.hbm.tileentity.block;


import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.EntityUndeadSoldier;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.tileentity.ModTileEntity;

import com.hbm.util.EnumUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TileEntityDungeonSpawner extends BlockEntity {

    public int phase = 0;
    public int timer = 0;
    public EnumSpawnerType type = EnumSpawnerType.ABERRATOR;

    public TileEntityDungeonSpawner(BlockPos pos, BlockState state) {
        super(ModTileEntity.DUNGEON_SPAWNER.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        type.phase.accept(this);
        if (type.phaseCondition.apply(this)) {
            phase++;
            timer = 0;
        } else {
            timer++;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("phase", phase);
        tag.putByte("type", (byte) type.ordinal());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.phase = tag.getInt("phase");
        this.type = EnumUtil.grabEnumSafely(EnumSpawnerType.class, tag.getByte("type"));
    }

    public enum EnumSpawnerType {
        ABERRATOR(CON_ABERRATOR, PHASE_ABERRATOR);

        public final Function<TileEntityDungeonSpawner, Boolean> phaseCondition;
        public final Consumer<TileEntityDungeonSpawner> phase;

        EnumSpawnerType(Function<TileEntityDungeonSpawner, Boolean> con, Consumer<TileEntityDungeonSpawner> ph) {
            this.phaseCondition = con;
            this.phase = ph;
        }
    }

    // === ABERRATOR ===

    public static final Function<TileEntityDungeonSpawner, Boolean> CON_ABERRATOR = (tile) -> {
        Level world = tile.level;
        if (world == null) return false;

        // Проверка сложности
        if (world.getDifficulty().getId() == 0) return false;

        BlockPos pos = tile.getBlockPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (tile.phase == 0) {
            if (world.getGameTime() % 20 != 0) return false;

            // Проверяем, есть ли игроки рядом
            AABB box = new AABB(x, y - 2, z, x + 1, y, z + 1).inflate(20, 10, 20);
            List<Player> players = world.getEntitiesOfClass(Player.class, box);
            return !players.isEmpty();
        }

        if (tile.phase < 3) {
            if (world.getGameTime() % 20 != 0 || tile.timer < 60) return false;

            // Проверяем, нет ли поблизости солдат
            AABB box = new AABB(x - 2, y, z, x, y + 1, z + 1).inflate(50, 20, 50);
            List<EntityUndeadSoldier> soldiers = world.getEntitiesOfClass(EntityUndeadSoldier.class, box);
            return soldiers.isEmpty();
        }

        return false;
    };

    public static final Consumer<TileEntityDungeonSpawner> PHASE_ABERRATOR = (tile) -> {
        Level world = tile.level;
        if (world == null) return;

        BlockPos pos = tile.getBlockPos();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (tile.phase == 1 || tile.phase == 2) {
            if (tile.timer == 0) {
                Vec3 vec = new Vec3(10, 0, 0);
                for (int i = 0; i < 10; i++) {
                    EntityUndeadSoldier mob = new EntityUndeadSoldier(ModEntities.UNDEAD_SOLDIER.get(), world);
                    for (int j = 0; j < 7; j++) {
                        mob.setPos(x + 0.5 + vec.x, y - 2, z + 0.5 + vec.z);
                        mob.setYRot(i * 36F);
                        mob.setXRot(0);
                        mob.addRandomArmor();
                        mob.setType(world.random.nextBoolean() ? EntityUndeadSoldier.TYPE_ZOMBIE : EntityUndeadSoldier.TYPE_SKELETON);
                        if (mob.checkSpawnRules(world, MobSpawnType.EVENT)) {
                            world.addFreshEntity(mob);
                            break;
                        }
                    }
                    // Вращение вокруг Y на 36 градусов
                    vec = vec.yRot((float)Math.toRadians(36D));
                }
            }
        }

        if (tile.phase > 2) {
            // Ищем скелет-холдер на 18 блоков выше
            BlockPos holderPos = pos.above(18);
            BlockEntity te = world.getBlockEntity(holderPos);

            if (te instanceof TileEntitySkeletonHolder holder) {
                if (world.random.nextInt(5) == 0) {
                    holder.setItem(new ItemStack(ModGunItems.GUN_ABERRATOR.get()));
                } else {
                    holder.setItem(new ItemStack(ModItems.CLAY_TABLET.get(), 1));
                }
                holder.setChanged();
                world.sendBlockUpdated(holderPos, holder.getBlockState(), holder.getBlockState(), 3);
            }

            // Заменяем спавнер на обсидиан
            world.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
        }
    };
}
