package com.hbm.inventory.fluid;

import com.hbm.api.fluid.FluidNet;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.*;
import com.hbm.uninos.INetworkProvider;
import com.hbm.uninos.networkproviders.FluidNetProvider;
import com.hbm.util.HBMEnums;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidType;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class FluidTypeHBM extends FluidType {
    private final String name;
    private final int color;
    private final int poison;
    private final int flammability;
    private final int reactivity;
    private final HBMEnums.EnumSymbol symbol;
    public boolean renderWithTint = false;
    private int id;
    public static final int ROOM_TEMPERATURE = 20;
    private int temperature = ROOM_TEMPERATURE;
    private final ResourceLocation texture;
    private int guiTint = 0xffffff;
    private final List<FluidTrait> traits = new ArrayList<>();
    private final Map<Class<? extends FluidTrait>, FluidTrait> traitMap = new HashMap<>();

    public FluidTypeHBM(String name, int color, int poison, int flammability,
                        int reactivity,HBMEnums.EnumSymbol symbol, ResourceLocation texture) {
        super(FluidType.Properties.create()
                .descriptionId("hbmfluid." + name.toLowerCase(Locale.US))
                .canConvertToSource(false)
                .canDrown(true)
                .canExtinguish(false)
                .canHydrate(false)
                .canPushEntity(false)
                .canSwim(false)
                .fallDistanceModifier(0F)
                .motionScale(0.014D)
                .viscosity(1000)
        );
        this.name = name;
        this.color = color;
        this.poison = poison;
        this.flammability = flammability;
        this.reactivity = reactivity;
        this.symbol = symbol;
        this.texture = texture;
    }

    public FluidTypeHBM(String name, int color, int poison, int flammability,
                     int reactivity, HBMEnums.EnumSymbol symbol, String texName, int tint, int id) {
        super(FluidType.Properties.create()
                .descriptionId("hbmfluid." + name.toLowerCase(Locale.US))
                .canConvertToSource(false)
                .canDrown(true)
                .canExtinguish(false)
                .canHydrate(false)
                .canPushEntity(false)
                .canSwim(false)
                .fallDistanceModifier(0F)
                .motionScale(0.014D)
                .viscosity(1000)
        );
        this.name = name;
        this.color = color;
        this.poison = poison;
        this.flammability = flammability;
        this.reactivity = reactivity;
        this.symbol = symbol;
        this.texture = ResLocation(RefStrings.MODID + ":textures/gui/fluids/" + texName + ".png");
        this.guiTint = tint;
        this.renderWithTint = true;
        this.id = id;
    }

    // Методы для работы с трейтами
    public FluidTypeHBM addTraits(FluidTrait... traitsToAdd) {
        for (FluidTrait trait : traitsToAdd) {
            this.traits.add(trait);
            this.traitMap.put(trait.getClass(), trait);
        }
        return this;
    }

    public boolean hasTrait(Class<? extends FluidTrait> traitClass) {
        return traitMap.containsKey(traitClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends FluidTrait> T getTrait(Class<T> traitClass) {
        return (T) traitMap.get(traitClass);
    }

    public List<FluidTrait> getTraits() {
        return Collections.unmodifiableList(traits);
    }

    // Геттеры и утилитарные методы
    public String getName() { return name; }
    public int getColor() { return color; }
    public int getPoison() { return poison; }
    public int getFlammability() { return flammability; }
    public int getReactivity() { return reactivity; }
    public HBMEnums.EnumSymbol getSymbol() { return symbol; }
    public int getTemperature() { return temperature; }
    public ResourceLocation getTexture() { return texture; }

    public FluidTypeHBM setTemperature(int temp) {
        this.temperature = temp;
        return this;
    }

    // Утилитарные методы для проверки свойств через трейты
    public boolean isGaseous() {
        return hasTrait(com.hbm.inventory.fluid.trait.FT_Gaseous.class) ||
                hasTrait(com.hbm.inventory.fluid.trait.FT_Gaseous_ART.class);
    }

    public boolean isCorrosive() {
        return hasTrait(com.hbm.inventory.fluid.trait.FT_Corrosive.class);
    }

    public boolean isFlammable() {
        return hasTrait(com.hbm.inventory.fluid.trait.FT_Flammable.class);
    }

    public boolean isCombustible() {
        return hasTrait(com.hbm.inventory.fluid.trait.FT_Combustible.class);
    }

    public boolean isRadioactive() {
        return hasTrait(com.hbm.inventory.fluid.trait.FT_VentRadiation.class);
    }

    public boolean isAntimatter() {
        return hasTrait(com.hbm.inventory.fluid.trait.FT_Amat.class);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(getDescriptionId());
    }

    public boolean isHot() {
        return this.temperature > 100;
    }
    public String getLocalizedName() {
        // Возвращаем локализованное имя без префикса
        String key = this.getDescription().getString();
        if (key.startsWith("hbmfluid.")) {
            // Пытаемся получить локализацию через Minecraft
            return net.minecraft.client.resources.language.I18n.get(key);
        }
        return key;
    }

    protected INetworkProvider<FluidNet> NETWORK_PROVIDER = new FluidNetProvider(this);

    public INetworkProvider<FluidNet> getNetworkProvider() {
        return NETWORK_PROVIDER;
    }

    public void onFluidRelease(BlockEntity te, FluidTankHBM tank, int overflowAmount) {
        this.onFluidRelease(te.getLevel(), te.getBlockPos(), tank, overflowAmount);
    }

    public void onFluidRelease(Level level, BlockPos pos, FluidTankHBM tank, int overflowAmount) {
        // реализация
    }

    public int getTint() {
        return this.guiTint;
    }

    public boolean hasNoID() {
        return hasTrait(FluidTraitsSimple.FT_NoID.class);
    }

    @OnlyIn(Dist.CLIENT)
    public void addInfo(List<Component> info) {
        if (temperature != ROOM_TEMPERATURE) {
            if (temperature < 0) {
                info.add(Component.literal(temperature + "°C").withStyle(ChatFormatting.BLUE));
            }
            if (temperature > 0) {
                info.add(Component.literal(temperature + "°C").withStyle(ChatFormatting.RED));
            }
        }

        boolean shiftHeld = GLFW.glfwGetKey(org.lwjgl.glfw.GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;

        List<Component> hidden = new ArrayList<>();

        for (Class<? extends FluidTrait> clazz : FluidTrait.traitList) {
            FluidTrait trait = this.getTrait(clazz);
            if (trait != null) {
                trait.addInfo(info);
                if (shiftHeld) trait.addInfoHidden(info);
                trait.addInfoHidden(hidden);
            }
        }

        if (!hidden.isEmpty() && !shiftHeld) {
            info.add(Component.translatable("tooltip.hold.shift")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
        }
    }

    public boolean hasNoContainer() {
        return this.traitMap.containsKey(FT_NoContainer.class);
    }

    public boolean isDispersable() {
        return !(this.traitMap.containsKey(FT_Amat.class) || this.traitMap.containsKey(FT_NoContainer.class) || this.traitMap.containsKey(FT_Viscous.class));
    }

    public boolean needsLeadContainer() {
        return this.traitMap.containsKey(FT_LeadContainer.class);
    }
}