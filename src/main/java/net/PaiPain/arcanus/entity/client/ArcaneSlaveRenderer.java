package net.PaiPain.arcanus.entity.client;

import net.PaiPain.arcanus.entity.custom.ArcaneSlaveEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class ArcaneSlaveRenderer extends MobRenderer<ArcaneSlaveEntity, ArcaneSlave<ArcaneSlaveEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("arcanus", "textures/entity/arcane_slave.png");

    public ArcaneSlaveRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneSlave<>(context.bakeLayer(net.PaiPain.arcanus.entity.client.ModModelLayers.ARCANE_SLAVE_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ArcaneSlaveEntity entity) {
        return TEXTURE;
    }
}
