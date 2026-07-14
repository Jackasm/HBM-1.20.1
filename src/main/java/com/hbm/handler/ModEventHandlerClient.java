package com.hbm.handler;

import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.config.ClientConfig;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.hazard.HazardSystem;
import com.hbm.inventory.gui.GUIArmorTable;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.items.armor.ArmorFSB;
import com.hbm.items.armor.IHelmetOverlay;
import com.hbm.items.armor.ItemArmorMod;
import com.hbm.main.ClientProxy;
import com.hbm.render.util.RenderScreenOverlay;
import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ItemStackUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ModEventHandlerClient {

    public static long flashTimestamp;
    public static long shakeTimestamp;


    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        BlockPos pos = event.getTarget().getBlockPos();
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ICustomBlockHighlight highlightBlock)) return;

        if (!highlightBlock.shouldDrawHighlight(level, pos)) return;

        event.setCanceled(true);

        highlightBlock.drawHighlight(event.getPoseStack(), event.getMultiBufferSource(), level, pos);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preRenderEventFirst(RenderLivingEvent.Pre<?, ?> event) {
        if(ClientProxy.isVanished(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onOverlayRender(RenderGuiOverlayEvent.Pre event) {


        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            /// HANDLE GEIGER COUNTER HUD ///
            ItemStack chest = player.getInventory().getArmor(2);
            if (!(chest.getItem() instanceof ArmorFSB armor && armor.customGeiger)) {
                if (player.getInventory().hasAnyMatching(stack -> stack.getItem() == ModItems.GEIGER_COUNTER.get())) {
                    float rads = HbmLivingProps.getRadiation(player);

                    GuiGraphics guiGraphics = event.getGuiGraphics();
                    RenderScreenOverlay.renderRadCounter(guiGraphics, rads);
                }
            }

            /// HANDLE FSB HUD ///

            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!helmet.isEmpty() && helmet.getItem() instanceof IHelmetOverlay overlay) {
                int width = mc.getWindow().getGuiScaledWidth();
                int height = mc.getWindow().getGuiScaledHeight();
                float partialTick = event.getPartialTick();

                overlay.renderHelmetOverlay(event.getGuiGraphics(), helmet, player, width, height, partialTick);
            }

            // Обработка панели рывков (dash bar)
            // Проверяем, не отменено ли событие и это HOTBAR (в 1.20.1 нет типа HOTBAR, используем проверку)
            if (!event.isCanceled()) {
                HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
                //noinspection ConstantValue
                if (props != null && props.getTotalDashCount() > 0) {
                    RenderScreenOverlay.renderDashBar(event.getGuiGraphics(), props);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        boolean showDetails = Screen.hasShiftDown();

        if (!showDetails) {
            /// DAMAGE RESISTANCE ///
            ArmorResistanceHandler.addInfo(stack, tooltip);
        }
        /// HAZMAT INFO ///
        Set<ArmorRegistry.HazardClass> hazInfo = ArmorRegistry.PROTECTION_REGISTRY.get(stack.getItem());


        if (hazInfo != null) {

            if (Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("hazard.prot").withStyle(ChatFormatting.GOLD));
                for (ArmorRegistry.HazardClass clazz : hazInfo) {
                    String langKey = "hazard." + clazz.name().toLowerCase();
                    tooltip.add(Component.literal("  ").append(Component.translatable(langKey)).withStyle(ChatFormatting.RED));
                }
            } else {
                tooltip.add(Component.translatable("armor.tooltip.showDetails").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
            }
        }

        /// CLADDING (LEGACY) ///
        double radResistance = HazmatRegistry.getResistance(stack);
        radResistance = ((int) (radResistance * 1000)) / 1000D;
        if (radResistance > 0) {
            DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.#");
            double protectionPercent = HazmatRegistry.coefficientToPercentage(radResistance) * 100;

            event.getToolTip().add(Component.literal(""));

            String formattedPercent = PERCENT_FORMAT.format(protectionPercent);
            event.getToolTip().add(
                    Component.translatable("armor.tooltip.radiation", formattedPercent)
                            .withStyle(ChatFormatting.DARK_GREEN)
            );
            tooltip.add(Component.translatable("trait.radResistance", radResistance).withStyle(ChatFormatting.GREEN));
        }


        /// ARMOR MODS ///
        if (stack.getItem() instanceof ArmorItem && ArmorModHandler.hasMods(stack)) {

            if (!Screen.hasShiftDown() && !(Minecraft.getInstance().screen instanceof GUIArmorTable)) {

                tooltip.add(Component.literal(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + "Hold <" +
                        ChatFormatting.YELLOW + ChatFormatting.ITALIC + "LSHIFT" +
                        ChatFormatting.DARK_GRAY + ChatFormatting.ITALIC + "> to display installed armor mods"));

            } else {

                tooltip.add(Component.literal(ChatFormatting.YELLOW + "Mods:"));

                ItemStack[] mods = ArmorModHandler.pryMods(stack);

                for (int i = 0; i < 8; i++) {

                    if (mods[i] != null && !mods[i].isEmpty() && mods[i].getItem() instanceof ItemArmorMod armorMod) {

                        armorMod.addDesc(tooltip, mods[i], stack);
                    }
                }
            }
        }

        /// HAZARDS ///
        HazardSystem.addFullTooltip(stack, event.getEntity(), tooltip);

        if (event.getFlags().isAdvanced() && ClientConfig.ITEM_TOOLTIP_SHOW_OREDICT) {
            List<String> names = ItemStackUtil.getOreDictNames(stack);

            if (!names.isEmpty()) {
                tooltip.add(Component.literal(ChatFormatting.BLUE + "Ore Dict:"));
                for (String s : names) {
                    tooltip.add(Component.literal(ChatFormatting.AQUA + " -" + s));
                }
            }
        }


        /// CUSTOM NUKE ///
        ComparableStack comp = new ComparableStack(stack).makeSingular();

        if (ClientConfig.ITEM_TOOLTIP_SHOW_CUSTOM_NUKE) {
            TileEntityNukeCustom.CustomNukeEntry entry = TileEntityNukeCustom.entries.get(comp);

            if (entry != null) {

                if (!tooltip.isEmpty())
                    tooltip.add(Component.empty());

                if (entry.entry == TileEntityNukeCustom.EnumEntryType.ADD)
                    tooltip.add(Component.literal(ChatFormatting.GOLD + "Adds " + entry.value + " to the custom nuke stage " + entry.type));

                if (entry.entry == TileEntityNukeCustom.EnumEntryType.MULT)
                    tooltip.add(Component.literal(ChatFormatting.GOLD + "Adds multiplier " + entry.value + " to the custom nuke stage " + entry.type));
            }
        }
        //TODO QuickManualAndWiki
        /*
        try {
            QuickManualAndWiki qmaw = QMAWLoader.triggers.get(comp);
            if (qmaw == null) {
                qmaw = QMAWLoader.triggers.get(new ComparableStack(comp.item, 1, OreDictionary.WILDCARD_VALUE));
            }
            if (qmaw != null) {
                tooltip.add(Component.literal(ChatFormatting.YELLOW + I18nUtil.resolveKey("qmaw.tab", Keybinds.QMAW_KEY.getKey().getDisplayName().getString())));
                lastQMAW = qmaw;
                qmawTimestamp = Clock.get_ms();
            }
        } catch (Exception ex) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Error loading cannery: " + ex.getLocalizedMessage()));
        }

        try {
            CanneryBase cannery = Jars.canneries.get(comp);
            if (cannery != null) {
                tooltip.add(Component.literal(ChatFormatting.GREEN + I18nUtil.resolveKey("cannery.f1",
                        Keybinds.SHIFT_KEY.getKey().getDisplayName().getString() + " + " +
                                Keybinds.QMAW_KEY.getKey().getDisplayName().getString())));
                lastCannery = comp;
                canneryTimestamp = Clock.get_ms();
            }
        } catch (Exception ex) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Error loading cannery: " + ex.getLocalizedMessage()));
        }

         */
    }
}