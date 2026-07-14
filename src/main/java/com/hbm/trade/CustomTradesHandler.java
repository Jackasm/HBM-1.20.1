package com.hbm.trade;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RefStrings.MODID)
public class CustomTradesHandler {

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FARMER) {
            var level1Trades = event.getTrades().get(1);
            level1Trades.clear();

            // Добавляем сделку через анонимный класс (проще без создания отдельного класса)
            level1Trades.add((trader, random) -> new MerchantOffer(
                    new ItemStack(ModItems.BOTTLE_CAP.get(), 2), // Цена (крышки)
                    new ItemStack(Items.WHEAT, 5),               // Результат (пшеница)
                    16,    // Макс использований
                    10,    // Опыт
                    0.05f  // Множитель
            ));
        }
    }
}
