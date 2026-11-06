package com.hbm_addons.handler;

import com.hbm.util.RefStrings;
import com.hbm_addons.config.AddonConfig;
import com.hbm_addons.main.AddonRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Выдача книги квестов при первом входе — только когда аддон квестов включён.
 *
 * <p>Флаг живёт в persistent data игрока. Как и стартовый набор ядра, флаг
 * переезжает в нового игрока при смерти, иначе книга выдавалась бы после каждой
 * смерти.</p>
 */
@Mod.EventBusSubscriber(modid = RefStrings.MODID)
public final class AddonPlayerEvents {

    private static final String GOT_QUEST_BOOK = "hbmAddonsQuestBookGiven";

    private AddonPlayerEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!AddonConfig.enableQuests) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(GOT_QUEST_BOOK)) return;

        ItemStack book = new ItemStack(AddonRegistry.QUEST_BOOK.get());
        if (!player.getInventory().add(book)) {
            player.drop(book, false);
        }
        data.putBoolean(GOT_QUEST_BOOK, true);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        event.getEntity().getPersistentData().putBoolean(GOT_QUEST_BOOK,
                event.getOriginal().getPersistentData().getBoolean(GOT_QUEST_BOOK));
    }
}
