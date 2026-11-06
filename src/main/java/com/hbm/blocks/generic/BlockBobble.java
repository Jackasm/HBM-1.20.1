package com.hbm.blocks.generic;

import com.hbm.blocks.IBlockMulti;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.inventory.container.ContainerBobble;
import com.hbm.items.deco.ItemBobble;
import com.hbm.tileentity.deco.TileEntityBobble;
import com.hbm.util.HBMEnums.ScrapType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockBobble extends BaseEntityBlock implements IBlockMulti, ITooltipProvider {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Shapes.box(5.5/16, 0, 5.5/16, 10.5/16, 10.0/16, 10.5/16);

    public BlockBobble(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityBobble(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityBobble bobble) {
            bobble.type = ItemBobble.getType(stack);
            bobble.setChanged();
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityBobble bobble) {
            NetworkHooks.openScreen((ServerPlayer) player,
                    new SimpleMenuProvider(
                            (windowId, inv, p) -> new ContainerBobble(windowId, inv, bobble),
                            Component.translatable("container.bobble")
                    ),
                    pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityBobble bobble) {
            return ItemBobble.withType(bobble.type);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, Player player) {
        if (!player.isCreative() && !level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityBobble bobble && bobble.type != null && bobble.type != BobbleType.NONE) {
                ItemStack stack = ItemBobble.withType(bobble.type);
                ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                item.setDeltaMovement(0, 0, 0);
                level.addFreshEntity(item);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public int getSubCount() {
        return BobbleType.values().length - 1; // без NONE
    }

    @Override
    public Component getOverrideDisplayName(ItemStack stack) {
        BobbleType type = ItemBobble.getType(stack);
        return Component.translatable(this.getDescriptionId() + "." + type.name().toLowerCase());
    }

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        BobbleType type = ItemBobble.getType(stack);
        if (type == BobbleType.NONE) return new String[0];

        java.util.List<String> keys = new java.util.ArrayList<>();
        keys.add("bobble." + type.name().toLowerCase() + ".name");
        if (type.contribution != null) keys.add("bobble." + type.name().toLowerCase() + ".contribution");
        if (type.inscription != null) keys.add("bobble." + type.name().toLowerCase() + ".inscription");
        return keys.toArray(new String[0]);
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(1.0F)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }

    public enum BobbleType {
        NONE(			"null",								"null",				null,														null,																								false,	ScrapType.BOARD_BLANK),
        STRENGTH(		"Strength",							"Strength",			null,														"It's essential to give your arguments impact.",													false,	ScrapType.BRIDGE_BIOS),
        PERCEPTION(		"Perception",						"Perception",		null,														"Only through observation will you perceive weakness.",												false,	ScrapType.BRIDGE_NORTH),
        ENDURANCE(		"Endurance",						"Endurance",		null,														"Always be ready to take one for the team.",														false,	ScrapType.BRIDGE_SOUTH),
        CHARISMA(		"Charisma",							"Charisma",			null,														"Nothing says pizzaz like a winning smile.",														false,	ScrapType.BRIDGE_IO),
        INTELLIGENCE(	"Intelligence",						"Intelligence",		null,														"It takes the smartest individuals to realize$there's always more to learn.",						false,	ScrapType.BRIDGE_BUS),
        AGILITY(		"Agility",							"Agility",			null,														"Never be afraid to dodge the sensitive issues.",													false,	ScrapType.BRIDGE_CHIPSET),
        LUCK(			"Luck",								"Luck",				null,														"There's only one way to give 110%.",																false,	ScrapType.BRIDGE_CMOS),
        BOB(			"Robert \"The Bobcat\" Katzinsky",	"HbMinecraft",		"Hbm's Nuclear Tech Mod",									"I know where you live, " + System.getProperty("user.name"),									false,	ScrapType.CPU_SOCKET),
        FRIZZLE(		"Frooz",							"Frooz",			"Weapon models",											"BLOOD IS FUEL",																					true,	ScrapType.CPU_CLOCK),
        PU238(			"Pu-238",							"Pu-238",			"Improved Tom impact mechanics",							null,																								false,	ScrapType.CPU_REGISTER),
        VT(				"VT-6/24",							"VT-6/24",			"Balefire warhead model and general texturework",			"You cannot unfuck a horse.",																		true,	ScrapType.CPU_EXT),
        DOC(			"The Doctor",						"Doctor17PH",		"Russian localization, lunar miner",						"Perhaps the moon rocks were too expensive",														true,	ScrapType.CPU_CACHE),
        BLUEHAT(		"The Blue Hat",						"The Blue Hat",		"Textures",													"payday 2's deagle freeaim champ of the year 2022",													true,	ScrapType.MEM_16K_A),
        PHEO(			"Pheo",								"Pheonix",			"Deuterium machines, tantalium textures, Reliant Rocket",	"RUN TO THE BEDROOM, ON THE SUITCASE ON THE LEFT,$YOU'LL FIND MY FAVORITE AXE",						true,	ScrapType.MEM_16K_B),
        ADAM29(			"Adam29",							"Adam29",			"Ethanol, liquid petroleum gas",							"You know, nukes are really quite beatiful.$It's like watching a star be born for a split second.",	true,	ScrapType.MEM_16K_C),
        UFFR(			"UFFR",								"UFFR",				"All sorts of things from his PR",							"fried shrimp",																						false,	ScrapType.MEM_SOCKET),
        VAER(			"vaer",								"vaer",				"ZIRNOX",													"taken de family out to the weekend cigarette festival",											true,	ScrapType.MEM_16K_D),
        NOS(			"Dr Nostalgia",						"Dr Nostalgia",		"SSG and Vortex models",									"Take a picture, I'ma pose, paparazzi$I've been drinking, moving like a zombie",					true,	ScrapType.BOARD_TRANSISTOR),
        DRILLGON(		"Drillgon200",						"Drillgon200",		"1.12 Port",												null,																								false,	ScrapType.CPU_LOGIC),
        CIRNO(			"Cirno",							"Cirno",			"the only multi layered skin i had",						"No brain. Head empty.",																			true,	ScrapType.BOARD_BLANK),
        MICROWAVE(		"Microwave",						"Microwave",		"OC Compatibility and massive RBMK/packet optimizations",	"they call me the food heater$john optimization",													true,	ScrapType.BOARD_CONVERTER),
        PEEP(			"Peep",								"LePeeperSauvage",	"Coilgun, Leadburster and Congo Lake models, BDCL QC",		"Fluffy ears can't hide in ash, nor snow.",															true,	ScrapType.CARD_BOARD),
        MELLOW(			"MELLOWARPEGGIATION",				"Mellow",			"NBT Structures, industrial lighting, animation tools",		"Make something cool now, ask for permission later.",												true,	ScrapType.CARD_PROCESSOR),
        ABEL(			"Abel1502", 						"Abel1502", 		"Abilities GUI, optimizations and many QoL improvements", 	"NANTO SUBARASHII",																				true,	ScrapType.CPU_REGISTER);

        public final String name;
        public final String label;
        public final String contribution;
        public final String inscription;
        public final boolean skinLayers;
        public final ScrapType scrap;

        BobbleType(String name, String label, String contribution, String inscription, boolean layers, ScrapType scrap) {
            this.name = name;
            this.label = label;
            this.contribution = contribution;
            this.inscription = inscription;
            this.skinLayers = layers;
            this.scrap = scrap;
        }
    }
}