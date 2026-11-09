package com.hbm.blocks.network;

import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.network.TileEntityRadioTorchBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public abstract class RadioTorchRWBase extends RadioTorchBase {

    public RadioTorchRWBase(Properties properties, String textureOff, String textureOn) {
        super(properties, textureOff, textureOn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityRadioTorchBase radio)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(new net.minecraft.world.item.ItemStack(this));
        section.addLine(Component.translatable(this.getDescriptionId())
                .withStyle(s -> s.withColor(0xFFFF00)));

        if (radio.channel != null && !radio.channel.isEmpty()) {
            section.addLine(Component.translatable("hbm.text.freq", radio.channel));
        }
        section.addLine(Component.translatable("hbm.text.signal_2", radio.lastState));

        sections.add(section);
        return sections;
    }
}