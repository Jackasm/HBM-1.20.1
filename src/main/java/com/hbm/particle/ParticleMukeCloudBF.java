package com.hbm.particle;

import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleMukeCloudBF extends ParticleMukeCloud {

    private static final ResourceLocation TEXTURE_BF =
            ResLocation.ResLocation(RefStrings.MODID, "textures/particles/explosion_bf.png");

    public ParticleMukeCloudBF(ClientLevel level, double x, double y, double z, double mx, double my, double mz) {
        super(level, x, y, z, mx, my, mz);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE_BF;
    }
}