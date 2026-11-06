package com.hbm_addons.items;

import com.hbm_addons.client.QuestBookOpener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

/**
 * Книга квестов аддона.
 *
 * <p>Раньше книга существовала только как экран по клавише Tab — предмета не было
 * вовсе. Теперь это обычный предмет: его выдаёт аддон при первом входе, если
 * включены квесты, и он же открывает тот самый экран.</p>
 *
 * <p>Открытие экрана клиентское, поэтому идёт через {@link DistExecutor}: на
 * сервере клиентские классы не должны грузиться.</p>
 */
public class ItemQuestBook extends Item {

    public ItemQuestBook() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> QuestBookOpener::open);
        }

        return InteractionResultHolder.success(stack);
    }
}
