package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityAcidBomb;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class RenderAcidBomb extends ThrownItemRenderer<EntityAcidBomb> {

    public RenderAcidBomb(EntityRendererProvider.Context context) {
        super(context, 1.0F, true);
    }
}