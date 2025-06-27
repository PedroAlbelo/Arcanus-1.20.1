package net.PaiPain.arcanus.entity.client;

import net.PaiPain.arcanus.Arcanus;
import net.PaiPain.arcanus.entity.ModEntities;
import net.PaiPain.arcanus.entity.client.ArcaneSlave;
import net.PaiPain.arcanus.entity.client.ArcaneSlaveRenderer;
import net.PaiPain.arcanus.entity.client.ModModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcanus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterLayers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(net.PaiPain.arcanus.entity.client.ModModelLayers.ARCANE_SLAVE_LAYER, ArcaneSlave::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ARCANE_SLAVE.get(), ArcaneSlaveRenderer::new);
    }
}
