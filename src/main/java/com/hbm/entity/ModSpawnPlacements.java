package com.hbm.entity;

import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Правила постановки для природного спавна голубя и мусорного пакета.
 *
 * <p>Forge для незарегистрированного типа отдаёт {@code NO_RESTRICTIONS} — то есть моб
 * спавнится где угодно, в том числе пакет в воздухе. Здесь правило уточняется:
 * голубь — только на твёрдой земле, пакет — только в воде и в полосе {@code y 45..63},
 * как {@code getCanSpawnHere} в 1.7.10.</p>
 */
@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSpawnPlacements {

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        // Голубь: на твёрдой земле, как обычная живность.
        event.register(ModEntities.PIGEON.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ModSpawnPlacements::checkAnimalSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);

        // Мусорный пакет: в воде и только в полосе y 45..63, как getCanSpawnHere в 1.7.10.
        event.register(ModEntities.PLASTIC_BAG.get(),
                SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ModSpawnPlacements::checkWaterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    private static <T extends Mob> boolean checkAnimalSpawnRules(EntityType<T> type, ServerLevelAccessor level,
                                                                 MobSpawnType reason, BlockPos pos,
                                                                 RandomSource random) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isSolidRender(level, below)
                && level.getFluidState(pos).isEmpty()
                && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    private static <T extends Mob> boolean checkWaterSpawnRules(EntityType<T> type, ServerLevelAccessor level,
                                                                MobSpawnType reason, BlockPos pos,
                                                                RandomSource random) {
        return pos.getY() > 45 && pos.getY() < 63
                && level.getFluidState(pos).is(FluidTags.WATER)
                && random.nextInt(10) == 0;
    }
}
