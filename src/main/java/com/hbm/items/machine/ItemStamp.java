package com.hbm.items.machine;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemStamp extends Item {

    protected StampType type;
    public static final HashMap<StampType, List<ItemStack>> stamps = new HashMap<>();

    public ItemStamp(int durability, StampType type, Properties properties) {
        super(properties.durability(durability).setNoRepair());
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if((this.type == StampType.PLATE || this.type == StampType.WIRE || this.type == StampType.CIRCUIT) && this.getMaxDamage(stack) > 0) {
            tooltip.add(Component.literal("[CREATED USING ANVIL]").withStyle(ChatFormatting.GRAY));
        }
    }

    public StampType getStampType() {
        return type;
    }

    // Отложенная инициализация списка штампов
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Теперь все предметы зарегистрированы, можно безопасно создавать ItemStack
        event.enqueueWork(ItemStamp::initializeStamps);
    }

    private static void initializeStamps() {
        // Очищаем на случай повторной инициализации
        stamps.clear();

        // Добавляем штампы для каждого типа
        for (StampType type : StampType.values()) {
            stamps.put(type, new ArrayList<>());
        }

        // Теперь нужно получить все зарегистрированные штампы и добавить их в списки
        // Для этого вам нужно будет передавать ссылки на предметы или использовать регистр
    }

    // Альтернативный подход - добавлять штампы по мере их создания, но после регистрации
    public static void addStampToRegistry(ItemStamp stamp) {
        if (stamp.type != null) {
            List<ItemStack> list = stamps.computeIfAbsent(stamp.type, k -> new ArrayList<>());
            list.add(new ItemStack(stamp));
        }
    }

    public enum StampType {
        FLAT,
        PLATE,
        WIRE,
        PIPE,
        SHELL,
        CIRCUIT,
        C357,
        C44,
        C50,
        C9,
        PRINTING1,
        PRINTING2,
        PRINTING3,
        PRINTING4,
        PRINTING5,
        PRINTING6,
        PRINTING7,
        PRINTING8
    }
}