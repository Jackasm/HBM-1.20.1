package com.hbm.extprop;

import com.hbm.config.RadiationConfig;
import com.hbm.entity.mob.EntityDuck;
import com.hbm.network.PacketDispatcher;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ModDamageSource;

import com.hbm.util.RefStrings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hbm.util.ResLocation.ResLocation;

@Mod.EventBusSubscriber
public class HbmLivingProps {

    // Capability для хранения данных
    public static final Capability<HbmLivingProps> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    // UUID для модификатора здоровья от дигаммы
    public static final UUID DIGAMMA_UUID = UUID.fromString("2a3d8aec-5ab9-4218-9b8b-ca812bdf378b");

    // Максимальные значения
    public static final int MAX_ASBESTOS = 60 * 60 * 20; // 1 час
    public static final int MAX_BLACK_LUNG = 2 * 60 * 60 * 20; // 2 часа
    public static final int MAX_PHOSPHORUS = 5 * 60 * 20; // 5 минут

    // Данные
    private float radiation;
    private float digamma;
    private int asbestos;
    private int blackLung;
    private float radEnv;
    private float radBuf;
    private int bombTimer;
    private int contagion;
    private int oil;
    public int fire;
    public int phosphorus;
    public int balefire;
    public int blackFire;
    private final List<ContaminationEffect> contamination = new ArrayList<>();

    private HbmLivingProps(LivingEntity entity) {
    }

    // Регистрация capability
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(HbmLivingProps.class);
    }

    // Прикрепление capability к сущности
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity living) {
            event.addCapability(
                    ResLocation(RefStrings.MODID, "living_props"),
                    new HbmLivingPropsProvider(living)
            );
        }
    }

    // Получение данных
    public static HbmLivingProps getData(LivingEntity entity) {
        if (entity == null) return null;

        LazyOptional<HbmLivingProps> capability = entity.getCapability(CAPABILITY);
        if (capability.isPresent()) {
            return capability.orElse(null);
        }
        return null;
    }

    // --- РАДИАЦИЯ ---
    public static float getRadiation(LivingEntity entity) {
        if (!RadiationConfig.ENABLE_CONTAMINATION) return 0;
        HbmLivingProps props = getData(entity);
        return props != null ? props.radiation : 0;
    }

    public static void setRadiation(LivingEntity entity, float rad) {
        if (!RadiationConfig.ENABLE_CONTAMINATION) return;
        HbmLivingProps props = getData(entity);
        if (props != null) {
            props.radiation = Math.max(0, Math.min(rad, 2500));
        }
    }

    public static void incrementRadiation(LivingEntity entity, float rad) {
        if (!RadiationConfig.ENABLE_CONTAMINATION) return;
        setRadiation(entity, getRadiation(entity) + rad);
    }

    // --- РАДИАЦИОННАЯ СРЕДА ---
    public static float getRadEnv(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.radEnv : 0;
    }

    public static void setRadEnv(LivingEntity entity, float rad) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.radEnv = rad;
    }

    // --- РАДИАЦИОННЫЙ БУФЕР ---
    public static float getRadBuf(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.radBuf : 0;
    }

    public static void setRadBuf(LivingEntity entity, float rad) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.radBuf = rad;
    }

    // --- КОНТАМИНАЦИЯ ---
    public static List<ContaminationEffect> getCont(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.contamination : new ArrayList<>();
    }

    public static void addCont(LivingEntity entity, ContaminationEffect cont) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.contamination.add(cont);
    }

    // --- ДИГАММА ---
    public static float getDigamma(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.digamma : 0;
    }

    public static void setDigamma(LivingEntity entity, float digamma) {
        if (entity.level().isClientSide()) return;

        HbmLivingProps props = getData(entity);
        if (props == null) return;

        if (entity instanceof EntityDuck) digamma = 0.0F;

        props.digamma = digamma;

        // Применяем модификатор здоровья
        AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.removeModifier(DIGAMMA_UUID);

            float healthMod = (float)Math.pow(0.5, digamma) - 1F;
            AttributeModifier modifier = new AttributeModifier(
                    DIGAMMA_UUID,
                    "digamma",
                    healthMod,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            healthAttribute.addTransientModifier(modifier);

            if (entity.getHealth() > entity.getMaxHealth() && entity.getMaxHealth() > 0) {
                entity.setHealth(entity.getMaxHealth());
            }
        }

        // Смерть от дигаммы
        if ((entity.getMaxHealth() <= 0 || digamma >= 10.0F) && entity.isAlive()) {
            entity.setAbsorptionAmount(0);
            entity.hurt(ModDamageSource.causeDigammaDamage(entity), 500F);
            entity.setHealth(0);
            entity.die(ModDamageSource.causeDigammaDamage(entity));

            // Отправляем частицы
            CompoundTag data = new CompoundTag();
            data.putString("type", "sweat");
            data.putInt("count", 50);
            data.putInt("block", Block.getId(Blocks.SOUL_SAND.defaultBlockState()));
            data.putInt("entity", entity.getId());

            // Отправка через PacketDispatcher
            PacketDispatcher.sendAuxParticleNT(data, entity.getX(), entity.getY(), entity.getZ(), null);
        }
    }

    public static void incrementDigamma(LivingEntity entity, float digamma) {
        if (entity instanceof EntityDuck) digamma = 0.0F;
        setDigamma(entity, getDigamma(entity) + digamma);
    }

    // --- АСБЕСТ --- (ваши существующие методы)
    public static int getAsbestos(LivingEntity entity) {
        if (RadiationConfig.disableAsbestos.get()) return 0;
        HbmLivingProps props = getData(entity);
        return props != null ? props.asbestos : 0;
    }

    public static void setAsbestos(LivingEntity entity, int value) {
        if (RadiationConfig.disableAsbestos.get()) return;
        HbmLivingProps props = getData(entity);
        if (props != null) {
            props.asbestos = Math.max(0, Math.min(value, MAX_ASBESTOS));

            if (value >= MAX_ASBESTOS) {
                props.asbestos = 0;
                entity.hurt(ModDamageSource.causeAsbestosDamage(entity), 1000F);
            }

            if (!entity.level().isClientSide() && entity instanceof ServerPlayer player) {
                // Синхронизация с клиентом
                PacketDispatcher.sendHazardsSync(player, getBlackLung(entity), value);
            }
        }
    }

    public static void incrementAsbestos(LivingEntity entity, int amount) {
        if (RadiationConfig.DISABLE_ASBESTOS) return;
        if (ArmorRegistry.hasProtection(entity, 3, ArmorRegistry.HazardClass.PARTICLE_FINE)) {
            return;
        }
        setAsbestos(entity, getAsbestos(entity) + amount);
    }

    // --- ЧЁРНОЕ ЛЁГКОЕ --- (ваши существующие методы)
    public static int getBlackLung(LivingEntity entity) {
        if (RadiationConfig.DISABLE_COAL) return 0;
        HbmLivingProps props = getData(entity);
        return props != null ? props.blackLung : 0;
    }

    public static void setBlackLung(LivingEntity entity, int value) {
        if (RadiationConfig.DISABLE_COAL) return;
        HbmLivingProps props = getData(entity);
        if (props != null) {
            props.blackLung = Math.max(0, Math.min(value, MAX_BLACK_LUNG));

            if (value >= MAX_BLACK_LUNG) {
                props.blackLung = 0;
                entity.hurt(ModDamageSource.causeBlackLungDamage(entity), 1000F);
            }

            if (!entity.level().isClientSide() && entity instanceof ServerPlayer player) {
                PacketDispatcher.sendHazardsSync(player, value, getAsbestos(entity));
            }
        }
    }

    public static void incrementBlackLung(LivingEntity entity, int amount) {
        if (RadiationConfig.DISABLE_COAL) return;
        if (ArmorRegistry.hasProtection(entity, 3, ArmorRegistry.HazardClass.PARTICLE_COARSE)) {
            return;
        }
        setBlackLung(entity, getBlackLung(entity) + amount);
    }

    // --- ТАЙМЕР БОМБЫ ---
    public static int getTimer(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.bombTimer : 0;
    }

    public static void setTimer(LivingEntity entity, int bombTimer) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.bombTimer = bombTimer;
    }

    // --- КОНТАГИЙ ---
    public static int getContagion(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.contagion : 0;
    }

    public static void setContagion(LivingEntity entity, int contagion) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.contagion = contagion;
    }

    // --- МАСЛО ---
    public static int getOil(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.oil : 0;
    }

    public static void setOil(LivingEntity entity, int oil) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.oil = oil;
    }

    // --- ОГОНЬ ---
    public static int getFire(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.fire : 0;
    }

    public static void setFire(LivingEntity entity, int fire) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.fire = fire;
    }

    // --- ФОСФОР ---
    public static int getPhosphorus(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.phosphorus : 0;
    }

    public static void setPhosphorus(LivingEntity entity, int phosphorus) {
        HbmLivingProps props = getData(entity);
        if (props != null) {
            props.phosphorus = Math.max(0, Math.min(phosphorus, MAX_PHOSPHORUS));

            if (phosphorus >= MAX_PHOSPHORUS) {
                entity.hurt(ModDamageSource.causePhosphorusDamage(entity), 5.0F);
            }
        }
    }

    public static void incrementPhosphorus(LivingEntity entity, int amount) {
        if (ArmorRegistry.hasProtection(entity, 3, ArmorRegistry.HazardClass.CHEMICAL)) {
            return;
        }
        setPhosphorus(entity, getPhosphorus(entity) + amount);
    }

    // --- БАЛЕФАЙР ---
    public static int getBalefire(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.balefire : 0;
    }

    public static void setBalefire(LivingEntity entity, int balefire) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.balefire = balefire;
    }

    // --- ЧЁРНЫЙ ОГОНЬ ---
    public static int getBlackFire(LivingEntity entity) {
        HbmLivingProps props = getData(entity);
        return props != null ? props.blackFire : 0;
    }

    public static void setBlackFire(LivingEntity entity, int blackFire) {
        HbmLivingProps props = getData(entity);
        if (props != null) props.blackFire = blackFire;
    }

    // --- КЛАСС КОНТАМИНАЦИИ ---
    public static class ContaminationEffect {
        public float maxRad;
        public int maxTime;
        public int time;
        public boolean ignoreArmor;

        public ContaminationEffect(float rad, int time, boolean ignoreArmor) {
            this.maxRad = rad;
            this.maxTime = this.time = time;
            this.ignoreArmor = ignoreArmor;
        }

        public float getRad() {
            return maxRad * ((float)time / (float)maxTime);
        }

        public void serialize(FriendlyByteBuf buf) {
            buf.writeFloat(this.maxRad);
            buf.writeInt(this.maxTime);
            buf.writeInt(this.time);
            buf.writeBoolean(ignoreArmor);
        }

        public static ContaminationEffect deserialize(FriendlyByteBuf buf) {
            float maxRad = buf.readFloat();
            int maxTime = buf.readInt();
            int time = buf.readInt();
            boolean ignoreArmor = buf.readBoolean();
            ContaminationEffect effect = new ContaminationEffect(maxRad, maxTime, ignoreArmor);
            effect.time = time;
            return effect;
        }

        public void save(CompoundTag nbt, int index) {
            CompoundTag me = new CompoundTag();
            me.putFloat("maxRad", this.maxRad);
            me.putInt("maxTime", this.maxTime);
            me.putInt("time", this.time);
            me.putBoolean("ignoreArmor", ignoreArmor);
            nbt.put("cont_" + index, me);
        }

        public static ContaminationEffect load(CompoundTag nbt, int index) {
            CompoundTag me = nbt.getCompound("cont_" + index);
            float maxRad = me.getFloat("maxRad");
            int maxTime = me.getInt("maxTime");
            int time = me.getInt("time");
            boolean ignoreArmor = me.getBoolean("ignoreArmor");

            ContaminationEffect effect = new ContaminationEffect(maxRad, maxTime, ignoreArmor);
            effect.time = time;
            return effect;
        }
    }

    // --- ПРОВАЙДЕР И ХРАНИЛИЩЕ ДЛЯ CAPABILITY ---
    public static class HbmLivingPropsProvider implements net.minecraftforge.common.capabilities.ICapabilitySerializable<CompoundTag> {
        private final HbmLivingProps data;
        private final LazyOptional<HbmLivingProps> optional;

        public HbmLivingPropsProvider(LivingEntity entity) {
            this.data = new HbmLivingProps(entity);
            this.optional = LazyOptional.of(() -> this.data);
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.@NotNull Capability<T> cap, net.minecraft.core.Direction side) {
            return CAPABILITY.orEmpty(cap, optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();

            nbt.putFloat("hfr_radiation", data.radiation);
            nbt.putFloat("hfr_digamma", data.digamma);
            nbt.putInt("hfr_asbestos", data.asbestos);
            nbt.putInt("hfr_bomb", data.bombTimer);
            nbt.putInt("hfr_contagion", data.contagion);
            nbt.putInt("hfr_blacklung", data.blackLung);
            nbt.putInt("hfr_oil", data.oil);
            nbt.putInt("hfr_fire", data.fire);
            nbt.putInt("hfr_phosphorus", data.phosphorus);
            nbt.putInt("hfr_balefire", data.balefire);
            nbt.putInt("hfr_blackfire", data.blackFire);

            nbt.putInt("hfr_cont_count", data.contamination.size());
            for (int i = 0; i < data.contamination.size(); i++) {
                data.contamination.get(i).save(nbt, i);
            }

            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.radiation = nbt.getFloat("hfr_radiation");
            data.digamma = nbt.getFloat("hfr_digamma");
            data.asbestos = nbt.getInt("hfr_asbestos");
            data.bombTimer = nbt.getInt("hfr_bomb");
            data.contagion = nbt.getInt("hfr_contagion");
            data.blackLung = nbt.getInt("hfr_blacklung");
            data.oil = nbt.getInt("hfr_oil");
            data.fire = nbt.getInt("hfr_fire");
            data.phosphorus = nbt.getInt("hfr_phosphorus");
            data.balefire = nbt.getInt("hfr_balefire");
            data.blackFire = nbt.getInt("hfr_blackfire");

            int cont = nbt.getInt("hfr_cont_count");
            for (int i = 0; i < cont; i++) {
                data.contamination.add(ContaminationEffect.load(nbt, i));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetFire(LivingEntity entity, int fire) {
        if (entity.level().isClientSide()) {
            setFire(entity, fire);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetPhosphorus(LivingEntity entity, int phosphorus) {
        if (entity.level().isClientSide()) {
            HbmLivingProps props = getData(entity);
            if (props != null) props.phosphorus = phosphorus;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetBalefire(LivingEntity entity, int balefire) {
        if (entity.level().isClientSide()) {
            setBalefire(entity, balefire);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetBlackFire(LivingEntity entity, int blackFire) {
        if (entity.level().isClientSide()) {
            setBlackFire(entity, blackFire);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetBlackLung(LivingEntity entity, int blackLung) {
        if (entity.level().isClientSide()) {
            HbmLivingProps props = getData(entity);
            if (props != null) props.blackLung = blackLung;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetAsbestos(LivingEntity entity, int asbestos) {
        if (entity.level().isClientSide()) {
            HbmLivingProps props = getData(entity);
            if (props != null) props.asbestos = asbestos;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetRadiation(LivingEntity entity, float radiation) {
        if (entity.level().isClientSide()) {
            HbmLivingProps props = getData(entity);
            if (props != null) props.radiation = radiation;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetDigamma(LivingEntity entity, float digamma) {
        if (entity.level().isClientSide()) {
            HbmLivingProps props = getData(entity);
            if (props != null) props.digamma = digamma;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetOil(LivingEntity entity, int oil) {
        if (entity.level().isClientSide()) {
            setOil(entity, oil);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetContagion(LivingEntity entity, int contagion) {
        if (entity.level().isClientSide()) {
            setContagion(entity, contagion);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetTimer(LivingEntity entity, int timer) {
        if (entity.level().isClientSide()) {
            setTimer(entity, timer);
        }
    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeFloat(radiation);
        buf.writeFloat(digamma);
        buf.writeInt(asbestos);
        buf.writeInt(bombTimer);
        buf.writeInt(contagion);
        buf.writeInt(blackLung);
        buf.writeInt(oil);
        buf.writeInt(this.contamination.size());
        for (ContaminationEffect effect : this.contamination) {
            effect.serialize(buf);
        }
    }

    public void deserialize(FriendlyByteBuf buf) {
        if (buf.readableBytes() > 0) {
            this.radiation = buf.readFloat();
            this.digamma = buf.readFloat();
            this.asbestos = buf.readInt();
            this.bombTimer = buf.readInt();
            this.contagion = buf.readInt();
            this.blackLung = buf.readInt();
            this.oil = buf.readInt();
            int size = buf.readInt();
            this.contamination.clear();
            for (int i = 0; i < size; i++) {
                this.contamination.add(ContaminationEffect.deserialize(buf));
            }
        }
    }
}