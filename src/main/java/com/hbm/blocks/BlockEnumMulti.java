package com.hbm.blocks;

import com.hbm.util.EnumUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class BlockEnumMulti extends BlockMulti {

    public static final IntegerProperty META = IntegerProperty.create("meta", 0, 15);

    public Class<? extends Enum> theEnum;
    public boolean multiName;
    private boolean multiTexture;

    public BlockEnumMulti(Properties properties, Class<? extends Enum> theEnum, boolean multiName, boolean multiTexture) {
        super(properties);
        this.theEnum = theEnum;
        this.multiName = multiName;
        this.multiTexture = multiTexture;

        this.registerDefaultState(this.stateDefinition.any().setValue(META, 0));
    }

    // Конструктор для обратной совместимости, если нужен Material
    @Deprecated
    public BlockEnumMulti(MapColor color, Class<? extends Enum> theEnum, boolean multiName, boolean multiTexture) {
        this(Properties.of().mapColor(color), theEnum, multiName, multiTexture);
    }

    public int validateMeta(int meta) {
        int max = this.theEnum.getEnumConstants().length - 1;
        if (meta < 0) return 0;
        if (meta > max) return max;
        return meta;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(META);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        int meta = stack.getDamageValue();
        meta = validateMeta(meta); // ← валидация
        return this.defaultBlockState().setValue(META, meta);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, BlockState state) {
        int meta = state.getValue(META);
        meta = validateMeta(meta); // ← валидация
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("meta", meta);
        return stack;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return this.getUnlocalizedName();
    }

    public String getUnlocalizedName() {
        return super.getDescriptionId();
    }

    public String getUnlocalizedName(ItemStack stack) {
        if (this.multiName) {
            Enum num = EnumUtil.grabEnumSafely(this.theEnum, stack.getDamageValue());
            return getUnlocalizedMultiName(num);
        }
        return this.getUnlocalizedName();
    }

    @Override
    public @NotNull MutableComponent getName() {
        return Component.translatable(this.getDescriptionId());
    }

    public Component getDescription() {
        return this.getName();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (multiName) {
            Enum num = EnumUtil.grabEnumSafely(this.theEnum, stack.getDamageValue());
            tooltip.add(Component.translatable("block.type").append(": ").append(
                    Component.translatable(getUnlocalizedMultiName(num)).withStyle(style -> style.withColor(0xFFFF55))
            ));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public String getTextureMultiName(Enum num) {
        String textureName = this.getTextureName();
        if (textureName == null) {
            textureName = "unknown";
        }
        return textureName + "_" + num.name().toLowerCase(Locale.US);
    }

    public String getUnlocalizedMultiName(Enum num) {
        return super.getDescriptionId() + "_" + num.name().toLowerCase(Locale.US);
    }

    public String getTextureName() {
        // В 1.20.1 получаем имя текстуры из registry name через BuiltInRegistries
        var registryName = BuiltInRegistries.BLOCK.getKey(this);
        if (registryName != null) {
            return registryName.getPath();
        }
        return null;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    public int getSubCount() {
        return this.theEnum.getEnumConstants().length;
    }

    public ItemStack stackFromEnum(Enum num, int count) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTag().putInt("CustomModelData", num.ordinal());
        return stack;
    }

    public ItemStack stackFromEnum(Enum num) {
        return stackFromEnum(num, 1);
    }
}