package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IToolable;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hbm.util.ResLocation.ResLocation;

public class BlockGraphiteBreedingProduct extends BlockGraphiteDrilledBase implements IToolable {

    public BlockGraphiteBreedingProduct(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initTextures() {
        super.initTextures();
        this.blockIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_tritium_aluminum.png");
    }

    @Override
    protected Item getInsertedItem() {
        return ModItems.CELL_TRITIUM.get();
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }
}