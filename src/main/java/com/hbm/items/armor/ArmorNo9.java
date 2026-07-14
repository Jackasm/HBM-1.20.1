package com.hbm.items.armor;

import com.hbm.blocks.gas.BlockGasBase;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.HbmKeybinds;
import com.hbm.render.model.ModelNo9;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ArmorNo9 extends ArmorModel implements IAttackHandler, IDamageHandler {

    @OnlyIn(Dist.CLIENT)
    private ModelNo9 model;

    // === СТАТИЧЕСКИЕ ПОЛЯ ===
    public static Set<BlockPos> breadcrumb = new HashSet<>();
    public static Map<Level, Long> lastChecks = new ConcurrentHashMap<>();
    public static Map<LightEntry, Long> lightCheck = new ConcurrentHashMap<>();


    // Для мобов
    private static final Map<UUID, BlockPos> mobLightPositions = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> lastMobLightUpdate = new ConcurrentHashMap<>();
    public static final int MOB_LIGHT_INTERVAL = 20;


    public ArmorNo9(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    // === КЛИЕНТСКАЯ МОДЕЛЬ ===
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (slot == EquipmentSlot.HEAD) {
                    if (model == null) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelNo9.NO9);
                        model = new ModelNo9(root);
                    }
                    model.young = living.isBaby();
                    model.setEntity(living);
                    return model;
                }
                return defaultModel;
            }
        });
    }

    // === ЗАЩИТА ОТ УРОНА ===
    @Override
    public void handleDamage(LivingHurtEvent event, ItemStack stack) {
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        float amount = event.getAmount() - 0.5F;
        if (amount < 0) amount = 0;
        event.setAmount(amount);
    }

    @Override
    public void handleAttack(LivingAttackEvent event, ItemStack armor) {
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        if (event.getAmount() <= 0.5F) {
            event.setCanceled(true);
        }
    }

    // === ТУЛТИП ===
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("+0.5 DT").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("armor.no9.tooltip").withStyle(ChatFormatting.YELLOW));
    }

    // === ЛОГИКА ДЛЯ ИГРОКА ===
    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        boolean isInHelmetSlot = slotIndex == 39;
        if (!isInHelmetSlot) return;

        if (!level.isClientSide) {
            if (!stack.hasTag()) stack.getOrCreateTag();

            if (!stack.getOrCreateTag().contains("isOn")) {
                stack.getOrCreateTag().putBoolean("isOn", true);
            }

            HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
            if (props == null) return;

            boolean keyPressed = props.getKeyPressed(HbmKeybinds.EnumKeybind.HELMET_MODE);
            long lastToggle = stack.getOrCreateTag().getLong("lastToggleTime");
            long now = level.getGameTime();
            boolean wasOn = stack.getOrCreateTag().getBoolean("isOn");
            boolean turnOn = wasOn;

            if (keyPressed && (now - lastToggle) > 5) {
                turnOn = !wasOn;
                stack.getOrCreateTag().putLong("lastToggleTime", now);
            }

            if (turnOn != wasOn) {
                if (turnOn) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.FLINTANDSTEEL_USE, player.getSoundSource(), 1.0F, 1.5F);
                } else {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.FIRE_EXTINGUISH, player.getSoundSource(), 0.5F, 2.0F);
                    clearAllLights(level, player.getUUID());
                }
                stack.getOrCreateTag().putBoolean("isOn", turnOn);
            }

            // Лечение чёрного лёгкого
            int blackLung = HbmLivingProps.getBlackLung(player);
            int maxBlackLung = HbmLivingProps.MAX_BLACK_LUNG;
            if (blackLung > maxBlackLung * 0.9) {
                HbmLivingProps.setBlackLung(player, (int) (maxBlackLung * 0.9));
            }
            if (blackLung >= maxBlackLung * 0.25) {
                HbmLivingProps.setBlackLung(player, blackLung - 1);
            }
        }

        if (!level.isClientSide && stack.hasTag() && stack.getOrCreateTag().getBoolean("isOn")) {
            handlePlayerLight(level, player);
        }
    }

    // === ОБРАБОТКА СВЕТА ДЛЯ ИГРОКА ===
    private static void handlePlayerLight(Level level, Player player) {
        if (level.getGameTime() % 5 == 0) {
            updateWorldHook(level);
        }
        if (level.getGameTime() % 2 == 0) {
            float range = 50F;
            Vec3 start = player.getEyePosition(1.0F);
            Vec3 look = player.getViewVector(1.0F);
            Vec3 end = start.add(look.x * range, look.y * range, look.z * range);

            BlockHitResult hit = level.clip(new ClipContext(start, end,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

            if (hit.getType() == HitResult.Type.BLOCK) {
                Vec3 hitVec = hit.getLocation();
                Vec3 lookVec = new Vec3(
                        player.getX() - hitVec.x,
                        player.getY() + player.getEyeHeight() - hitVec.y,
                        player.getZ() - hitVec.z
                );
                int lightLevel = Math.min(15, (int) (25 - (lookVec.length() * 25 / range)));
                BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
                lightUpRecursively(level, pos.getX(), pos.getY(), pos.getZ(), lightLevel, player.getUUID());
                breadcrumb.clear();
            }
        }
    }

    // === РЕКУРСИВНАЯ УСТАНОВКА БЛОКОВ СВЕТА ===
    public static void lightUpRecursively(Level level, int x, int y, int z, int light, UUID owner) {
        if (light <= 0) return;
        BlockPos pos = new BlockPos(x, y, z);
        if (breadcrumb.contains(pos)) return;
        breadcrumb.add(pos);

        int occupancy = level.getBlockState(pos).getLightBlock(level, pos);
        if (occupancy >= 255) return;

        if (!level.isClientSide) {
            BlockState existing = level.getBlockState(pos);
            Block block = existing.getBlock();

            // Обновляем существующий LightBlock
            if (block instanceof LightBlock) {
                int existingLight = existing.getValue(LightBlock.LEVEL);
                int newLight = Math.max(existingLight, light);
                if (newLight > existingLight) {
                    level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, newLight), 3);
                }
                lightCheck.put(new LightEntry(level, pos, owner), level.getGameTime() + 20);
                return;
            }

            // Если это газ — просто включаем подсветку
            if (block instanceof BlockGasBase gasBlock) {
                gasBlock.setGlowing(level, pos, true);
                lightCheck.put(new LightEntry(level, pos, owner), level.getGameTime() + 20);
                return;
            }

            // Если воздух — ставим LightBlock
            if (existing.isAir()) {
                level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, light), 3);
                lightCheck.put(new LightEntry(level, pos, owner), level.getGameTime() + 20);
            }
        }

        // Рекурсивные вызовы
        if (light > 1) {
            lightUpRecursively(level, x + 1, y, z, light - 1, owner);
            lightUpRecursively(level, x - 1, y, z, light - 1, owner);
            lightUpRecursively(level, x, y + 1, z, light - 1, owner);
            lightUpRecursively(level, x, y - 1, z, light - 1, owner);
            lightUpRecursively(level, x, y, z + 1, light - 1, owner);
            lightUpRecursively(level, x, y, z - 1, light - 1, owner);
        }
    }

    // === ОБНОВЛЕНИЕ И УДАЛЕНИЕ СВЕТА ===
    public static void checkLights(Level level, boolean force) {
        Iterator<Map.Entry<LightEntry, Long>> it = lightCheck.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<LightEntry, Long> entry = it.next();
            LightEntry light = entry.getKey();
            if (light.level == level && (level.getGameTime() > entry.getValue() || force)) {
                BlockPos pos = light.pos;
                // Восстанавливаем газ (выключаем подсветку)
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof BlockGasBase gasBlock) {
                    gasBlock.setGlowing(level, pos, false);
                }
                it.remove();
            }
        }
        lastChecks.put(level, level.getGameTime());
    }

    public static void updateWorldHook(Level level) {
        if (level == null || level.isClientSide) return;
        Long last = lastChecks.get(level);
        if (last != null && last < level.getGameTime() + 15) {
            checkLights(level, false);
        }
    }

    public static void clearAllLights(Level level, @Nullable UUID owner) {
        Iterator<Map.Entry<LightEntry, Long>> it = lightCheck.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<LightEntry, Long> entry = it.next();
            LightEntry light = entry.getKey();
            if (light.level == level && (owner == null || light.owner.equals(owner))) {
                BlockPos pos = light.pos;
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof BlockGasBase gasBlock) {
                    gasBlock.setGlowing(level, pos, false);
                } else if (state.getBlock() instanceof LightBlock) {
                    level.removeBlock(pos, false);
                }
                it.remove();
            }
        }
    }

    // === ЛОГИКА ДЛЯ МОБОВ ===
    public static void handleMobLight(Level level, LivingEntity entity, ItemStack stack, long gameTime) {
        if (!stack.hasTag()) stack.getOrCreateTag();

        if (!stack.getOrCreateTag().contains("isOn")) {
            stack.getOrCreateTag().putBoolean("isOn", true);
        }

        boolean isOn = stack.getOrCreateTag().getBoolean("isOn");
        UUID uuid = entity.getUUID();

        if (!isOn || entity.isRemoved() || !entity.isAlive()) {
            removeMobLight(level, uuid);
            return;
        }

        Long last = lastMobLightUpdate.get(uuid);
        if (last != null && gameTime - last < MOB_LIGHT_INTERVAL) return;
        lastMobLightUpdate.put(uuid, gameTime);

        BlockPos target = calculateMobLightPosition(entity);
        if (target == null) return;

        BlockPos current = mobLightPositions.get(uuid);
        if (current == null || !current.equals(target)) {
            removeMobLight(level, uuid);
            if (placeLightBlock(level, target)) {
                mobLightPositions.put(uuid, target);
            }
        }
    }

    private static BlockPos calculateMobLightPosition(LivingEntity entity) {
        double x = entity.getX();
        double y = entity.getY() + entity.getEyeHeight() - 0.5;
        double z = entity.getZ();
        float yaw = entity.getYRot();
        double offX = -Math.sin(Math.toRadians(yaw)) * 0.8;
        double offZ = Math.cos(Math.toRadians(yaw)) * 0.8;
        BlockPos pos = BlockPos.containing(x + offX, y, z + offZ);
        if (canPlaceLightBlock(entity.level(), pos)) return pos;
        pos = entity.blockPosition().above(1);
        if (canPlaceLightBlock(entity.level(), pos)) return pos;
        return null;
    }

    private static boolean canPlaceLightBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.getBlock() instanceof LightBlock;
    }

    private static boolean placeLightBlock(Level level, BlockPos pos) {
        BlockState existing = level.getBlockState(pos);
        Block block = existing.getBlock();

        if (block instanceof LightBlock) {
            int newLight = Math.max(existing.getValue(LightBlock.LEVEL), 15);
            level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, newLight), 3);
            return true;
        }

        if (block instanceof BlockGasBase gasBlock) {
            gasBlock.setGlowing(level, pos, true);
            return true;
        }

        if (existing.isAir()) {
            level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
            return true;
        }

        return false;
    }

    private static void removeMobLight(Level level, UUID uuid) {
        BlockPos pos = mobLightPositions.remove(uuid);
        if (pos != null) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof BlockGasBase gasBlock) {
                gasBlock.setGlowing(level, pos, false);
            } else if (state.getBlock() instanceof LightBlock) {
                level.removeBlock(pos, false);
            }
        }
    }

    // === ОБРАБОТЧИКИ СОБЫТИЙ ===
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        removeMobLight(event.getEntity().level(), event.getEntity().getUUID());
        lastMobLightUpdate.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            Level level = (Level) event.getLevel();

            Iterator<Map.Entry<UUID, BlockPos>> it = mobLightPositions.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, BlockPos> entry = it.next();
                BlockPos pos = entry.getValue();
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof BlockGasBase gasBlock) {
                    gasBlock.setGlowing(level, pos, false);
                } else if (state.getBlock() instanceof LightBlock) {
                    level.removeBlock(pos, false);
                }
                it.remove();
            }
            lastMobLightUpdate.clear();
            clearAllLights(level, null);
            lastChecks.remove(level);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Level level = player.level();
            if (level.isClientSide) return;

            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            boolean hasHelmet = helmet.getItem() instanceof ArmorNo9;

            UUID playerUUID = player.getUUID();
            boolean hasLight = false;
            for (Map.Entry<LightEntry, Long> entry : lightCheck.entrySet()) {
                LightEntry light = entry.getKey();
                if (light.level == level && light.owner.equals(playerUUID)) {
                    hasLight = true;
                    break;
                }
            }

            if (!hasHelmet && hasLight) {
                clearAllLights(level, playerUUID);
            }
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЙ КЛАСС ДЛЯ ХРАНЕНИЯ СВЕТА ===
    public static class LightEntry {
        public Level level;
        public BlockPos pos;
        public UUID owner;

        public LightEntry(Level level, BlockPos pos, UUID owner) {
            this.level = level;
            this.pos = pos;
            this.owner = owner;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof LightEntry other)) return false;
            return level == other.level && pos.equals(other.pos) && owner.equals(other.owner);
        }

        @Override
        public int hashCode() {
            return Objects.hash(level, pos, owner);
        }
    }
}