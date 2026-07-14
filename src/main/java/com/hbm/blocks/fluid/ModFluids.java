package com.hbm.blocks.fluid;

import com.hbm.blocks.ModBlocks;
import com.hbm.util.RefStrings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

import static com.hbm.util.ResLocation.ResLocation;

public class ModFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, RefStrings.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, RefStrings.MODID);

    public static final RegistryObject<FluidType> VOLCANIC_LAVA_TYPE = FLUID_TYPES.register("volcanic_lava",
            () -> new FluidType(FluidType.Properties.create()
                    .density(3000)
                    .viscosity(3000)
                    .temperature(1300)
                    .lightLevel(15)
            ) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation STILL = ResLocation(RefStrings.MODID, "block/volcanic_lava_still");
                        private static final ResourceLocation FLOWING = ResLocation(RefStrings.MODID, "block/volcanic_lava_flowing");

                        @Override
                        public ResourceLocation getStillTexture() {
                            return STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return FLOWING;
                        }
                    });
                }
            });

    public static final RegistryObject<FlowingFluid> VOLCANIC_LAVA_FLUID = FLUIDS.register("volcanic_lava_fluid",
            () -> new ForgeFlowingFluid.Source(createFluidProperties()));

    public static final RegistryObject<FlowingFluid> VOLCANIC_LAVA_FLUID_FLOWING = FLUIDS.register("volcanic_lava_fluid_flowing",
            () -> new ForgeFlowingFluid.Flowing(createFluidProperties()));

    private static ForgeFlowingFluid.Properties createFluidProperties() {
        return new ForgeFlowingFluid.Properties(
                VOLCANIC_LAVA_TYPE,
                VOLCANIC_LAVA_FLUID,
                VOLCANIC_LAVA_FLUID_FLOWING
        ).block(() -> (LiquidBlock)ModBlocks.VOLCANIC_LAVA_BLOCK.get());
    }

    public static final RegistryObject<FluidType> RAD_LAVA_TYPE = FLUID_TYPES.register("rad_lava",
            () -> new FluidType(FluidType.Properties.create()
                    .density(3000)
                    .viscosity(3000)
                    .temperature(1300)
                    .lightLevel(15)
            ) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation STILL = ResLocation(RefStrings.MODID, "block/rad_lava_still");
                        private static final ResourceLocation FLOWING = ResLocation(RefStrings.MODID, "block/rad_lava_flowing");

                        @Override
                        public ResourceLocation getStillTexture() {
                            return STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return FLOWING;
                        }
                    });
                }
            });

    public static final RegistryObject<FlowingFluid> RAD_LAVA_FLUID = FLUIDS.register("rad_lava_fluid",
            () -> new ForgeFlowingFluid.Source(createRadFluidProperties()));

    public static final RegistryObject<FlowingFluid> RAD_LAVA_FLUID_FLOWING = FLUIDS.register("rad_lava_fluid_flowing",
            () -> new ForgeFlowingFluid.Flowing(createRadFluidProperties()));

    private static ForgeFlowingFluid.Properties createRadFluidProperties() {
        return new ForgeFlowingFluid.Properties(
                RAD_LAVA_TYPE,
                RAD_LAVA_FLUID,
                RAD_LAVA_FLUID_FLOWING
        ).block(() -> (LiquidBlock)ModBlocks.RAD_LAVA_BLOCK.get());
    }

    public static final RegistryObject<FluidType> MUD_TYPE = FLUID_TYPES.register("mud",
            () -> new FluidType(FluidType.Properties.create()
                    .density(2000)
                    .viscosity(3000)
                    .lightLevel(0)
            ) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation STILL = ResLocation(RefStrings.MODID, "block/mud_still");
                        private static final ResourceLocation FLOWING = ResLocation(RefStrings.MODID, "block/mud_flowing");

                        @Override
                        public ResourceLocation getStillTexture() {
                            return STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return FLOWING;
                        }
                    });
                }
            });

    public static final RegistryObject<FlowingFluid> MUD_FLUID = FLUIDS.register("mud_fluid",
            () -> new ForgeFlowingFluid.Source(createMudFluidProperties()));

    public static final RegistryObject<FlowingFluid> MUD_FLUID_FLOWING = FLUIDS.register("mud_fluid_flowing",
            () -> new ForgeFlowingFluid.Flowing(createMudFluidProperties()));

    private static ForgeFlowingFluid.Properties createMudFluidProperties() {
        return new ForgeFlowingFluid.Properties(
                MUD_TYPE,
                MUD_FLUID,
                MUD_FLUID_FLOWING
        ).block(() -> (LiquidBlock) ModBlocks.MUD_BLOCK.get());
    }

    public static final RegistryObject<FluidType> TOXIC_TYPE = FLUID_TYPES.register("toxic",
            () -> new ToxicBlock.ToxicFluidType(FluidType.Properties.create()
                    .density(1200)
                    .viscosity(1500)
                    .lightLevel(0)
            ));

    public static final RegistryObject<FlowingFluid> TOXIC_FLUID = FLUIDS.register("toxic_fluid",
            () -> new ForgeFlowingFluid.Source(createToxicFluidProperties()));

    public static final RegistryObject<FlowingFluid> TOXIC_FLUID_FLOWING = FLUIDS.register("toxic_fluid_flowing",
            () -> new ForgeFlowingFluid.Flowing(createToxicFluidProperties()));

    private static ForgeFlowingFluid.Properties createToxicFluidProperties() {
        return new ForgeFlowingFluid.Properties(
                TOXIC_TYPE,
                TOXIC_FLUID,
                TOXIC_FLUID_FLOWING
        ).block(() -> (LiquidBlock) ModBlocks.TOXIC_BLOCK.get());
    }
}