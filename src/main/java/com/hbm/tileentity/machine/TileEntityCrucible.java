package com.hbm.tileentity.machine;

import com.hbm.api.block.ICrucibleAcceptor;
import com.hbm.api.tile.IHeatSource;
import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.container.ContainerCrucible;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.inventory.recipes.CrucibleRecipes.CrucibleRecipe;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemCrucibleTemplate;
import com.hbm.items.machine.ItemScraps;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.*;
import com.hbm.util.CrucibleUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TileEntityCrucible extends TileEntityMachineBase implements ICrucibleAcceptor, MenuProvider {

    public int heat;
    public int progress;
    public List<MaterialStack> recipeStack = new ArrayList<>();
    public List<MaterialStack> wasteStack = new ArrayList<>();

    public static int recipeZCapacity = MaterialShapes.BLOCK.q(16);
    public static int wasteZCapacity = MaterialShapes.BLOCK.q(16);
    public static int processTime = 20_000;
    public static double diffusion = 0.25D;
    public static int maxHeat = 100_000;

    private final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) return stack.getItem() == ModItems.CRUCIBLE_TEMPLATE.get();
            return isItemSmeltable(stack);
        }
    };
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> itemHandler);

    public TileEntityCrucible(BlockPos pos, BlockState state) {
        super(ModTileEntity.CRUCIBLE.get(), pos, state);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            tryPullHeat();
            collectItems();
            checkEntityDamage();
            igniteBlocks();

            if (!trySmelt()) this.progress = 0;
            tryRecipe();

            pourStacks();

            recipeStack.removeIf(o -> o.amount <= 0);
            wasteStack.removeIf(x -> x.amount <= 0);

            networkPackNT(25);

            if (!recipeStack.isEmpty() || !wasteStack.isEmpty()) {
                if (level.getGameTime() % 10 == 0) {
                    CompoundTag fx = new CompoundTag();
                    fx.putString("type", "tower");
                    fx.putFloat("lift", 10F);
                    fx.putFloat("base", 0.75F);
                    fx.putFloat("max", 3.5F);
                    fx.putInt("life", 100 + level.random.nextInt(20));
                    fx.putInt("color", 0x202020);
                    fx.putDouble("posX", worldPosition.getX() + 0.5);
                    fx.putDouble("posY", worldPosition.getY() + 1);
                    fx.putDouble("posZ", worldPosition.getZ() + 0.5);
                    PacketDispatcher.sendToAllAround(
                            new AuxParticlePacketNT(fx, worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5),
                            level, worldPosition, 50);
                }
            }
        }
    }

    private void tryPullHeat() {
        if (heat >= maxHeat) return;
        BlockEntity con = Objects.requireNonNull(level).getBlockEntity(worldPosition.below());
        if (con instanceof IHeatSource source) {
            int diff = source.getHeatStored() - heat;
            if (diff <= 0) return;
            diff = Math.min(diff, maxHeat - heat);
            diff = (int) Math.ceil(diff * diffusion);
            source.useUpHeat(diff);
            heat += diff;
        } else {
            heat = Math.max(heat - Math.max(heat / 1000, 1), 0);
        }
    }

    private void collectItems() {
        if (Objects.requireNonNull(level).getGameTime() % 5 != 0) return;
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class,
                new AABB(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                        worldPosition.getX() + 1.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 1.5));
        for (ItemEntity item : items) {
            if (!item.isAlive()) continue;
            ItemStack stack = item.getItem();
            if (isItemSmeltable(stack)) {
                for (int i = 1; i < 10; i++) {
                    ItemStack slot = itemHandler.getStackInSlot(i);
                    if (slot.isEmpty()) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        itemHandler.setStackInSlot(i, copy);
                        stack.shrink(1);
                        if (stack.isEmpty()) item.discard();
                        setChanged();
                        break;
                    }
                }
            }
        }
    }

    private void checkEntityDamage() {
        // Область 3x3 прямо над тиглем (центр - блок тигля)
        AABB damageBox = new AABB(
                worldPosition.getX() - 1, worldPosition.getY() + 2, worldPosition.getZ() - 1,
                worldPosition.getX() + 2, worldPosition.getY() + 3, worldPosition.getZ() + 2
        );

        List<LivingEntity> living = Objects.requireNonNull(level).getEntitiesOfClass(LivingEntity.class, damageBox);
        for (LivingEntity entity : living) {
            entity.hurt(level.damageSources().lava(), 5.0F);
            entity.setRemainingFireTicks(100);
        }
    }

    private void igniteBlocks() {
        if (recipeStack.isEmpty() & wasteStack.isEmpty()) return;

        if (Objects.requireNonNull(level).getGameTime() % 20 != 0) return;

        BlockPos.betweenClosed(
                worldPosition.getX() - 1, worldPosition.getY() + 1, worldPosition.getZ() - 1,
                worldPosition.getX() + 1, worldPosition.getY() + 4, worldPosition.getZ() + 1
        ).forEach(pos -> {
            if (level.getBlockState(pos).isFlammable(level, pos, Direction.UP)) {
                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            }
        });
    }

    private boolean trySmelt() {
        if (heat < maxHeat / 2) return false;
        int slot = getFirstSmeltableSlot();
        if (slot == -1) return false;

        ItemStack stack = itemHandler.getStackInSlot(slot);
        List<MaterialStack> materials = Mats.getSmeltingMaterialsFromItem(stack);
        if (materials.isEmpty()) return false;

        CrucibleRecipe recipe = getLoadedRecipe();

        // Проверка места перед плавкой
        for (MaterialStack mat : materials) {
            boolean main = recipe != null && (getQuantaFromArray(recipe.input, mat.material) > 0 || getQuantaFromArray(recipe.output, mat.material) > 0);
            List<MaterialStack> target = main ? recipeStack : wasteStack;
            int capacity = main ? recipeZCapacity : wasteZCapacity;
            if (!canAddMaterial(mat, target, capacity)) return false; // нет места — не плавим
        }

        // Начисляем прогресс
        int delta = (int) ((heat - (double) maxHeat / 2) * 0.05);
        progress += delta;
        heat -= delta;

        if (progress >= processTime) {
            progress = 0;
            for (MaterialStack mat : materials) {
                boolean main = recipe != null && (getQuantaFromArray(recipe.input, mat.material) > 0 || getQuantaFromArray(recipe.output, mat.material) > 0);
                if (main) {
                    addToRecipeStack(mat);
                } else {
                    addToStack(wasteStack, mat);
                }
            }
            itemHandler.extractItem(slot, 1, false);
        }
        return true;
    }

    private void tryRecipe() {
        CrucibleRecipe recipe = getLoadedRecipe();
        if (recipe == null || Objects.requireNonNull(level).getGameTime() % recipe.frequency != 0) return;

        for (MaterialStack in : recipe.input) {
            if (getQuantaFromList(recipeStack, in.material) < in.amount) return;
        }

        // Проверка места (как раньше)
        List<MaterialStack> temp = new ArrayList<>(recipeStack);
        for (MaterialStack in : recipe.input) removeFromStack(temp, in.material, in.amount);
        int currentTotal = getTotal(temp);
        int required = 0;
        for (MaterialStack out : recipe.output) required += out.amount;
        if (currentTotal + required > recipeZCapacity) return;

        // Удаляем входные
        for (MaterialStack in : recipe.input) removeFromRecipeStack(in.material, in.amount);
        // Добавляем выходные
        for (MaterialStack out : recipe.output) addToRecipeStack(out.copy());
    }

    private void addToRecipeStack(MaterialStack mat) {
        addToStack(recipeStack, mat);
        sortRecipeStack();
    }

    private void removeFromRecipeStack(NTMMaterial mat, int amount) {
        removeFromStack(recipeStack, mat, amount);
        sortRecipeStack();
    }

    private void sortRecipeStack() {
        CrucibleRecipe recipe = getLoadedRecipe();
        if (recipe == null || recipeStack.isEmpty()) return;

        List<MaterialStack> sorted = new ArrayList<>();

        // 1. Выходы рецепта в порядке объявления (первый – основной продукт)
        for (MaterialStack out : recipe.output) {
            for (MaterialStack ms : recipeStack) {
                if (ms.material == out.material && notAlreadyInList(sorted, ms.material)) {
                    sorted.add(ms);
                    break;
                }
            }
        }

        // 2. Остальные материалы (например, входные, которые ещё не ушли в рецепт)
        for (MaterialStack ms : recipeStack) {
            if (notAlreadyInList(sorted, ms.material)) {
                sorted.add(ms);
            }
        }

        recipeStack.clear();
        recipeStack.addAll(sorted);
    }

    private boolean notAlreadyInList(List<MaterialStack> list, NTMMaterial mat) {
        for (MaterialStack ms : list) if (ms.material == mat) return false;
        return true;
    }

    private void pourStacks() {
        Direction dir = getBlockState().getValue(BlockDummyable.FACING);
        Direction back = dir.getOpposite();

        BlockPos backNozzle = worldPosition.relative(dir, 2).below();
        tryPourIntoBasin(recipeStack, backNozzle, dir, false);

        BlockPos frontNozzle = worldPosition.relative(back, 2).below();
        tryPourIntoBasin(wasteStack, frontNozzle, back, true);
    }

    private void tryPourIntoBasin(List<MaterialStack> stack, BlockPos nozzlePos, Direction pourDir, boolean isRecipe) {
        if (stack.isEmpty()) return;


        double pourY = nozzlePos.getY() + 1.0;
        double pourX = nozzlePos.getX() + 0.5;
        double pourZ = nozzlePos.getZ() + 0.5;

        MaterialStack toPour = stack.get(0).copy();
        int prevAmount = toPour.amount;
        Vec3[] impact = new Vec3[1];

        MaterialStack leftover = CrucibleUtil.pourSingleStack(
                Objects.requireNonNull(level),
                pourX, pourY, pourZ,
                6, true, toPour, MaterialShapes.INGOT.q(1), impact
        );

        if (leftover == null) {
            stack.remove(0);
        } else if (leftover.amount != prevAmount) {
            stack.get(0).amount = leftover.amount;
        } else {
            // Ничего не вылилось
            return;
        }

        double length = Math.max(1.0, worldPosition.getY() + 1.25 - (Math.ceil(impact[0].y) - 1.125)) - 1.5;

        CompoundTag data = new CompoundTag();
        data.putString("type", "foundry");
        data.putInt("color", toPour.material.moltenColor);
        data.putByte("dir", (byte) pourDir.ordinal());
        data.putFloat("off", 0.625F);
        data.putFloat("base", 0.625F);
        data.putFloat("len", (float) length);

        PacketDispatcher.sendToAllAround(
                new AuxParticlePacketNT(data, pourX, pourY, pourZ),
                level, worldPosition, 50
        );
    }

    public void scoopOut(Player player) {
        List<MaterialStack> all = new ArrayList<>();
        all.addAll(recipeStack);
        all.addAll(wasteStack);
        for (MaterialStack s : all) {
            ItemStack scrap = ItemScraps.create(new MaterialStack(s.material, s.amount));
            if (!player.getInventory().add(scrap)) player.drop(scrap, false);
        }
        recipeStack.clear();
        wasteStack.clear();
        setChanged();
    }

    // Helpers
    private int getFirstSmeltableSlot() {
        for (int i = 1; i < 10; i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) return i;
        }
        return -1;
    }

    public boolean isItemSmeltable(ItemStack stack) {
        return !Mats.getSmeltingMaterialsFromItem(stack).isEmpty();
    }

    private CrucibleRecipe getLoadedRecipe() {
        ItemStack template = itemHandler.getStackInSlot(0);
        if (!template.isEmpty() && template.getItem() == ModItems.CRUCIBLE_TEMPLATE.get()) {
            return ItemCrucibleTemplate.getRecipe(template);
        }
        return null;
    }

    private void addToStack(List<MaterialStack> list, MaterialStack mat) {
        for (MaterialStack s : list) {
            if (s.material == mat.material) { s.amount += mat.amount; return; }
        }
        list.add(mat.copy());
    }

    private void removeFromStack(List<MaterialStack> list, NTMMaterial mat, int amount) {
        for (MaterialStack s : list) {
            if (s.material == mat) { s.amount -= amount; return; }
        }
    }

    private int getQuantaFromList(List<MaterialStack> list, NTMMaterial mat) {
        for (MaterialStack s : list) if (s.material == mat) return s.amount;
        return 0;
    }

    private int getQuantaFromArray(MaterialStack[] arr, NTMMaterial mat) {
        for (MaterialStack s : arr) if (s.material == mat) return s.amount;
        return 0;
    }

    // ICrucibleAcceptor
    @Override
    public boolean canAcceptPartialPour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        CrucibleRecipe recipe = getLoadedRecipe();
        if (recipe == null) return getTotal(wasteStack) < wasteZCapacity;
        int max = getQuantaFromArray(recipe.input, stack.material) * recipeZCapacity / recipe.getInputAmount();
        return getQuantaFromList(recipeStack, stack.material) < max && getTotal(recipeStack) < recipeZCapacity;
    }

    @Override
    public MaterialStack pour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        CrucibleRecipe recipe = getLoadedRecipe();
        if (recipe == null) {
            // wasteStack без сортировки
            int space = wasteZCapacity - getTotal(wasteStack);
            if (space >= stack.amount) { addToStack(wasteStack, stack.copy()); return null; }
            addToStack(wasteStack, new MaterialStack(stack.material, space));
            return new MaterialStack(stack.material, stack.amount - space);
        }
        int max = getQuantaFromArray(recipe.input, stack.material) * recipeZCapacity / recipe.getInputAmount();
        int current = getQuantaFromList(recipeStack, stack.material);
        int space = Math.min(max - current, recipeZCapacity - getTotal(recipeStack));
        if (space >= stack.amount) { addToRecipeStack(stack.copy()); return null; }
        if (space > 0) addToRecipeStack(new MaterialStack(stack.material, space));
        return new MaterialStack(stack.material, stack.amount - space);
    }

    @Override public boolean canAcceptPartialFlow(Level level, BlockPos pos, Direction side, MaterialStack stack) { return false; }
    @Override public MaterialStack flow(Level level, BlockPos pos, Direction side, MaterialStack stack) { return null; }

    private int getTotal(List<MaterialStack> list) {
        int sum = 0;
        for (MaterialStack s : list) sum += s.amount;
        return sum;
    }

    // Serialization
    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(progress);
        buf.writeInt(heat);
        buf.writeShort(recipeStack.size());
        for (MaterialStack s : recipeStack) { buf.writeInt(s.material.id); buf.writeInt(s.amount); }
        buf.writeShort(wasteStack.size());
        for (MaterialStack s : wasteStack) { buf.writeInt(s.material.id); buf.writeInt(s.amount); }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        progress = buf.readInt(); heat = buf.readInt();
        recipeStack.clear(); wasteStack.clear();
        int count = buf.readShort();
        for (int i = 0; i < count; i++) recipeStack.add(new MaterialStack(Mats.matById.get(buf.readInt()), buf.readInt()));
        count = buf.readShort();
        for (int i = 0; i < count; i++) wasteStack.add(new MaterialStack(Mats.matById.get(buf.readInt()), buf.readInt()));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("progress", progress);
        nbt.putInt("heat", heat);
        int[] rec = new int[recipeStack.size() * 2];
        for (int i = 0; i < recipeStack.size(); i++) { rec[i * 2] = recipeStack.get(i).material.id; rec[i * 2 + 1] = recipeStack.get(i).amount; }
        nbt.put("recipeStack", new IntArrayTag(rec));
        int[] was = new int[wasteStack.size() * 2];
        for (int i = 0; i < wasteStack.size(); i++) { was[i * 2] = wasteStack.get(i).material.id; was[i * 2 + 1] = wasteStack.get(i).amount; }
        nbt.put("wasteStack", new IntArrayTag(was));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("progress"); heat = nbt.getInt("heat");
        recipeStack.clear(); wasteStack.clear();
        int[] rec = nbt.getIntArray("recipeStack");
        for (int i = 0; i < rec.length / 2; i++) recipeStack.add(new MaterialStack(Mats.matById.get(rec[i * 2]), rec[i * 2 + 1]));
        int[] was = nbt.getIntArray("wasteStack");
        for (int i = 0; i < was.length / 2; i++) wasteStack.add(new MaterialStack(Mats.matById.get(was[i * 2]), was[i * 2 + 1]));

        sortRecipeStack();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() { super.invalidateCaps(); itemCap.invalidate(); }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1,
                worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2);
    }

    public IItemHandler getItemHandler() { return itemHandler; }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerCrucible(id, inv, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.hbm.machines.machine_crucible");
    }

    private int getRemainingSpace(List<MaterialStack> list, int capacity) {
        return capacity - getTotal(list);
    }

    private boolean canAddMaterial(MaterialStack mat, List<MaterialStack> targetList, int capacity) {
        int remaining = getRemainingSpace(targetList, capacity);
        return remaining >= mat.amount;
    }
}