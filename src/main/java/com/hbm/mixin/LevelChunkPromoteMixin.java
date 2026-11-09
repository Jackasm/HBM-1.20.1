package com.hbm.mixin;

import com.hbm.blocks.BlockDummyable;
import com.hbm.handler.MixinStats;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Отбрасывает мёртвые отложенные теги {@code DUMMY} до того, как ваниль напишет
 * о них предупреждение (#204).
 *
 * <p>Почему здесь, а не событием. {@code WorldGenRegion.setBlock} пишет тег на
 * любой блок с {@code hasBlockEntity()}. Пока чанк — {@code ProtoChunk}, замена
 * блока тег снимает. Но в {@code LevelChunk} чанк превращается раньше, чем
 * {@code ChunkMap.prepareTickingChunk} вызовет {@code postProcessGeneration}, а
 * {@code LevelChunk.removeBlockEntity} карту отложенных тегов <b>не трогает</b>.
 * Всё, что соседняя генерация успела записать в этом окне (в нашем случае —
 * вода поверх крыши подлодки), оставляет тег, и промоушен пишет в лог
 * «Tried to load a DUMMY block entity …». Событие {@code ChunkEvent.Load} для
 * этого не годится: у затронутого чанка карта на нём ещё пуста — теги приходят
 * позже, из чужой генерации.</p>
 *
 * <p>Что отбрасываем: тег, у которого на месте блока нет блока-сущности (вода,
 * воздух, обычный камень), и тег на не-ядерной заглушке {@link BlockDummyable}
 * (её {@code newBlockEntity} всегда {@code null}). Настоящие блоки-сущности
 * разбираются ванилью как обычно.</p>
 *
 * <p>Решение и его границы — ADR-0030.</p>
 */
@Mixin(LevelChunk.class)
public abstract class LevelChunkPromoteMixin {

    @Inject(method = "promotePendingBlockEntity", at = @At("HEAD"), cancellable = true)
    private void hbm$dropStaleDummyTag(BlockPos pos, CompoundTag tag,
                                       CallbackInfoReturnable<BlockEntity> callback) {
        if (!"DUMMY".equals(tag.getString("id"))) return;

        LevelChunk self = (LevelChunk) (Object) this;
        BlockState state = self.getBlockState(pos);

        boolean noBlockEntity = !state.hasBlockEntity();
        boolean dummyPart = state.getBlock() instanceof BlockDummyable dummy && !dummy.isCore(state);
        if (!noBlockEntity && !dummyPart) return;

        MixinStats.DROPPED_DUMMY_TAGS.incrementAndGet();
        callback.setReturnValue(null);
    }
}
