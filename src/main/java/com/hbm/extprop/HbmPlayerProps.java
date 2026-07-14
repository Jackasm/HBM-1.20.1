package com.hbm.extprop;

import com.hbm.handler.ArmorModHandler;
import com.hbm.handler.HbmKeybinds.EnumKeybind;
import com.hbm.inventory.recipes.RecipeType;
import com.hbm.items.armor.ItemModShield;
import com.hbm.network.PacketDispatcher;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class HbmPlayerProps {

    public static final int dashCooldownLength = 5;

    // Capability system for 1.20.1
    public static final Capability<IHbmPlayerProps> HBM_PLAYER_PROPS =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation KEY = ResLocation.ResLocation(RefStrings.MODID, "player_props");
    public int shield;

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IHbmPlayerProps.class);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player player) {
            event.addCapability(KEY, new HbmPlayerPropsProvider(player));
        }
    }

    public static IHbmPlayerProps getData(Player player) {
        return player.getCapability(HBM_PLAYER_PROPS).orElse(null);
    }

    public static IHbmPlayerProps getData(Player player, boolean createIfMissing) {
        LazyOptional<IHbmPlayerProps> cap = player.getCapability(HBM_PLAYER_PROPS);
        if(cap.isPresent()) {
            return cap.orElseThrow(() -> new IllegalStateException("Capability not found"));
        }
        return null;
    }

    public void setKeyPressed(EnumKeybind key, boolean pressed) {
        // Реализация зависит от того, как вы храните состояния клавиш
        // Например:
        // keyStates.put(key, pressed);
    }

    // Interface for the capability
    public interface IHbmPlayerProps {

        boolean hasReceivedBook();
        void setHasReceivedBook(boolean value);

        boolean isHudEnabled();
        void setHudEnabled(boolean value);

        boolean isBackpackEnabled();
        void setBackpackEnabled(boolean value);

        boolean isMagnetEnabled();
        void setMagnetEnabled(boolean value);

        boolean[] getKeysPressed();
        void setKeyPressed(EnumKeybind key, boolean pressed);
        boolean getKeyPressed(EnumKeybind key);

        boolean isDashActivated();
        void setDashActivated(boolean value);

        int getDashCooldown();
        void setDashCooldown(int cooldown);

        int getTotalDashCount();
        void setTotalDashCount(int count);

        int getStamina();
        void setStamina(int stamina);

        int getPlinkCooldown();
        void setPlinkCooldown(int cooldown);

        float getShield();
        void setShield(float shield);

        float getMaxShield();
        void setMaxShield(float maxShield);

        int getLastDamage();
        void setLastDamage(int damage);

        int getReputation();
        void setReputation(int reputation);

        boolean isOnLadder();
        void setIsOnLadder(boolean value);

        boolean isJetpackActive();
        boolean isMagnetActive();

        float getEffectiveMaxShield();

        Set<RecipeType> getUnlockedCrafters();
        void addCrafter(RecipeType type);
        boolean hasCrafter(RecipeType type);

        void serialize(ByteBuf buf);
        void deserialize(ByteBuf buf);

        CompoundTag serializeNBT();
        void deserializeNBT(CompoundTag nbt);
    }

    // Implementation class
    public static class HbmPlayerPropsImpl implements IHbmPlayerProps {

        private final Player player;

        private boolean hasReceivedBook = false;
        private boolean enableHUD = true;
        private boolean enableBackpack = true;
        private boolean enableMagnet = true;
        private boolean[] keysPressed = new boolean[EnumKeybind.values().length];
        private boolean dashActivated = true;

        private int dashCooldown = 0;
        private int totalDashCount = 0;
        private int stamina = 0;
        private int plinkCooldown = 0;

        private float shield = 0;
        private float maxShield = 0;
        private int lastDamage = 0;
        private int reputation = 0;
        private boolean isOnLadder = false;

        private final Set<RecipeType> unlockedCrafters = new HashSet<>();

        public HbmPlayerPropsImpl(Player player) {
            this.player = player;
        }

        @Override
        public boolean hasReceivedBook() { return hasReceivedBook; }

        @Override
        public void setHasReceivedBook(boolean value) { this.hasReceivedBook = value; }

        @Override
        public boolean isHudEnabled() { return enableHUD; }

        @Override
        public void setHudEnabled(boolean value) { this.enableHUD = value; }

        @Override
        public boolean isBackpackEnabled() { return enableBackpack; }

        @Override
        public void setBackpackEnabled(boolean value) { this.enableBackpack = value; }

        @Override
        public boolean isMagnetEnabled() { return enableMagnet; }

        @Override
        public void setMagnetEnabled(boolean value) { this.enableMagnet = value; }

        @Override
        public boolean[] getKeysPressed() { return keysPressed.clone(); }



        @Override
        public void setKeyPressed(EnumKeybind key, boolean pressed) {
            if(!getKeyPressed(key) && pressed) {
                handleKeyPress(key);
            }
            keysPressed[key.ordinal()] = pressed;
        }

        @Override
        public boolean getKeyPressed(EnumKeybind key) {
            return keysPressed[key.ordinal()];
        }

        private void handleKeyPress(EnumKeybind key) {
            if (this.player == null) {
                return;
            }

            // Для клавиш оружия отправляем пакет на сервер
            if (key == EnumKeybind.RELOAD || key == EnumKeybind.TRAIN) {
                if (player.level().isClientSide()) {
                    // Отправляем пакет на сервер
                    PacketDispatcher.sendKeybind(key, true);
                }
                return;
            }

            if(!player.level().isClientSide()) {
                switch(key) {
                    case TOGGLE_JETPACK:
                        this.enableBackpack = !this.enableBackpack;
                        if(this.enableBackpack) {
                            player.displayClientMessage(
                                    Component.literal("Jetpack ON").withStyle(net.minecraft.ChatFormatting.GREEN),
                                    true
                            );
                        } else {
                            player.displayClientMessage(
                                    Component.literal("Jetpack OFF").withStyle(net.minecraft.ChatFormatting.RED),
                                    true
                            );
                        }
                        break;

                    case TOGGLE_MAGNET:
                        this.enableMagnet = !this.enableMagnet;
                        if(this.enableMagnet) {
                            player.displayClientMessage(
                                    Component.literal("Magnet ON").withStyle(net.minecraft.ChatFormatting.GREEN),
                                    true
                            );
                        } else {
                            player.displayClientMessage(
                                    Component.literal("Magnet OFF").withStyle(net.minecraft.ChatFormatting.RED),
                                    true
                            );
                        }
                        break;

                    case TOGGLE_HEAD:
                        this.enableHUD = !this.enableHUD;
                        if(this.enableHUD) {
                            player.displayClientMessage(
                                    Component.literal("HUD ON").withStyle(net.minecraft.ChatFormatting.GREEN),
                                    true
                            );
                        } else {
                            player.displayClientMessage(
                                    Component.literal("HUD OFF").withStyle(net.minecraft.ChatFormatting.RED),
                                    true
                            );
                        }
                        break;

                    case TRAIN:
                        if (!player.level().isClientSide()) {
                            Entity vehicle = player.getVehicle();
                            //TODO добавить EntityRailCarBase
                            /*
                            if (vehicle instanceof EntityRailCarBase && vehicle instanceof IGUIProvider) {
                                // Открываем GUI для вагона
                                if (vehicle instanceof MenuProvider provider) {
                                    NetworkHooks.openScreen((ServerPlayer) player, provider, vehicle.blockPosition());
                                }
                            }

                             */
                        }
                        break;
                    case RELOAD:

                        break;
                }
            }
        }

        @Override
        public Set<RecipeType> getUnlockedCrafters() {
            // Если игрок в креативном режиме - возвращаем ВСЕ типы крафтеров
            if (player.isCreative()) {
                Set<RecipeType> allTypes = new HashSet<>();
                // Добавляем только те типы, у которых есть рецепты
                for (RecipeType type : RecipeType.values()) {
                    if (type.hasRecipes()) {
                        allTypes.add(type);
                    }
                }
                return allTypes;
            }
            // В выживании - возвращаем только разблокированные
            return new HashSet<>(unlockedCrafters);
        }

        @Override
        public void addCrafter(RecipeType type) {
            if (type != null) {
                unlockedCrafters.add(type);
                if (!player.level().isClientSide()) {
                    // Отправляем обновление клиенту
                    PacketDispatcher.sendCrafterUpdate(player, type);
                }
            }
        }

        @Override
        public boolean hasCrafter(RecipeType type) {
            if (type == null) return false;
            // В креативе - всегда true (если у типа есть рецепты)
            if (player.isCreative() && type.hasRecipes()) {
                return true;
            }
            return unlockedCrafters.contains(type);
        }

        @Override
        public boolean isJetpackActive() {
            return this.enableBackpack && getKeyPressed(EnumKeybind.JETPACK);
        }

        @Override
        public boolean isMagnetActive() {
            return this.enableMagnet;
        }

        @Override
        public boolean isDashActivated() { return dashActivated; }

        @Override
        public void setDashActivated(boolean value) { this.dashActivated = value; }

        @Override
        public int getDashCooldown() { return dashCooldown; }

        @Override
        public void setDashCooldown(int cooldown) { this.dashCooldown = cooldown; }

        @Override
        public int getTotalDashCount() { return totalDashCount; }

        @Override
        public void setTotalDashCount(int count) { this.totalDashCount = count; }

        @Override
        public int getStamina() { return stamina; }

        @Override
        public void setStamina(int stamina) { this.stamina = stamina; }

        @Override
        public int getPlinkCooldown() { return plinkCooldown; }

        @Override
        public void setPlinkCooldown(int cooldown) { this.plinkCooldown = cooldown; }

        @Override
        public float getShield() { return shield; }

        @Override
        public void setShield(float shield) { this.shield = Math.min(shield, 100.0f); }

        @Override
        public float getMaxShield() { return maxShield; }

        @Override
        public void setMaxShield(float maxShield) { this.maxShield = maxShield; }

        @Override
        public int getLastDamage() { return lastDamage; }

        @Override
        public void setLastDamage(int damage) { this.lastDamage = damage; }

        @Override
        public int getReputation() { return reputation; }

        @Override
        public void setReputation(int reputation) { this.reputation = reputation; }

        @Override
        public boolean isOnLadder() { return isOnLadder; }

        @Override
        public void setIsOnLadder(boolean value) { this.isOnLadder = value; }

        @Override
        public float getEffectiveMaxShield() {
            float max = this.maxShield;

            ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
            if(!chestplate.isEmpty()) {
                ItemStack[] mods = ArmorModHandler.pryMods(chestplate);
                if(mods[ArmorModHandler.KEVLAR] != null &&
                        mods[ArmorModHandler.KEVLAR].getItem() instanceof ItemModShield) {
                    ItemModShield mod = (ItemModShield) mods[ArmorModHandler.KEVLAR].getItem();
                    max += mod.shield;
                }
            }


            return max;
        }

        @Override
        public void serialize(ByteBuf buf) {
            buf.writeBoolean(this.hasReceivedBook);
            buf.writeFloat(this.shield);
            buf.writeFloat(this.maxShield);
            buf.writeBoolean(this.enableBackpack);
            buf.writeBoolean(this.enableHUD);
            buf.writeBoolean(this.enableMagnet);
            buf.writeInt(this.reputation);
            buf.writeBoolean(this.isOnLadder);
        }

        @Override
        public void deserialize(ByteBuf buf) {
            if(buf.readableBytes() > 0) {
                this.hasReceivedBook = buf.readBoolean();
                this.shield = buf.readFloat();
                this.maxShield = buf.readFloat();
                this.enableBackpack = buf.readBoolean();
                this.enableHUD = buf.readBoolean();
                this.enableMagnet = buf.readBoolean();
                this.reputation = buf.readInt();
                this.isOnLadder = buf.readBoolean();
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("hasReceivedBook", hasReceivedBook);
            nbt.putFloat("shield", shield);
            nbt.putFloat("maxShield", maxShield);
            nbt.putBoolean("enableBackpack", enableBackpack);
            nbt.putBoolean("enableMagnet", enableMagnet);
            nbt.putBoolean("enableHUD", enableHUD);
            nbt.putInt("reputation", reputation);
            nbt.putBoolean("isOnLadder", isOnLadder);
            nbt.putInt("dashCooldown", dashCooldown);
            nbt.putInt("totalDashCount", totalDashCount);
            nbt.putInt("stamina", stamina);
            nbt.putInt("plinkCooldown", plinkCooldown);
            nbt.putBoolean("dashActivated", dashActivated);
            ListTag craftersList = new ListTag();
            for (RecipeType type : unlockedCrafters) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("type", type.ordinal());
                craftersList.add(entry);
            }
            nbt.put("unlockedCrafters", craftersList);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.hasReceivedBook = nbt.getBoolean("hasReceivedBook");
            this.shield = nbt.getFloat("shield");
            this.maxShield = nbt.getFloat("maxShield");
            this.enableBackpack = nbt.getBoolean("enableBackpack");
            this.enableMagnet = nbt.getBoolean("enableMagnet");
            this.enableHUD = nbt.getBoolean("enableHUD");
            this.reputation = nbt.getInt("reputation");
            this.isOnLadder = nbt.getBoolean("isOnLadder");
            this.dashCooldown = nbt.getInt("dashCooldown");
            this.totalDashCount = nbt.getInt("totalDashCount");
            this.stamina = nbt.getInt("stamina");
            this.plinkCooldown = nbt.getInt("plinkCooldown");
            this.dashActivated = nbt.getBoolean("dashActivated");
            unlockedCrafters.clear();
            ListTag craftersList = nbt.getList("unlockedCrafters", Tag.TAG_COMPOUND);
            RecipeType[] values = RecipeType.values();
            for (int i = 0; i < craftersList.size(); i++) {
                CompoundTag entry = craftersList.getCompound(i);
                int ordinal = entry.getInt("type");
                if (ordinal >= 0 && ordinal < values.length) {
                    unlockedCrafters.add(values[ordinal]);
                }
            }
        }
    }

    // Capability provider
    public static class HbmPlayerPropsProvider implements net.minecraftforge.common.capabilities.ICapabilityProvider {

        private final IHbmPlayerProps instance;
        private final LazyOptional<IHbmPlayerProps> optional;
        private Player player;

        public HbmPlayerPropsProvider(Player player) {
            this.player = player;
            this.instance = new HbmPlayerPropsImpl(player);
            this.optional = LazyOptional.of(() -> instance);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
            return HBM_PLAYER_PROPS.orEmpty(cap, optional);
        }
    }

    // Helper method for plink sound
    public static void plink(Player player, net.minecraft.resources.ResourceLocation sound, float volume, float pitch) {
        IHbmPlayerProps props = getData(player);
        if(props != null && props.getPlinkCooldown() <= 0) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvent.createVariableRangeEvent(sound),
                    player.getSoundSource(), volume, pitch);
            props.setPlinkCooldown(10);
        }
    }

    // Helper method for plink sound with string
    public static void plink(Player player, String sound, float volume, float pitch) {
        plink(player, ResLocation.ResLocation(RefStrings.MODID, sound), volume, pitch);
    }
}