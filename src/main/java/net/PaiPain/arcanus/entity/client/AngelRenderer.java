package net.PaiPain.arcanus.entity.client;

import net.PaiPain.arcanus.Arcanus;
import net.PaiPain.arcanus.entity.custom.AngelEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AngelRenderer extends MobRenderer<AngelEntity, Angel<AngelEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Arcanus.MOD_ID, "textures/entity/angel.png");

    public AngelRenderer(EntityRendererProvider.Context context) {
        super(context, new Angel(context.bakeLayer(Angel.LAYER_LOCATION)), 0.4f); // shadow size
    }

    @Override
    public ResourceLocation getTextureLocation(AngelEntity entity) {
        return TEXTURE;
    }
}
