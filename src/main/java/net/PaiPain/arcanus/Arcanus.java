package net.PaiPain.arcanus;

import com.mojang.logging.LogUtils;
import net.PaiPain.arcanus.entity.ModEntities;
import net.PaiPain.arcanus.item.ModCreativeModTabs;
import net.PaiPain.arcanus.block.ModBlocks;
import net.PaiPain.arcanus.item.ModItems;
import net.PaiPain.arcanus.painting.ModPaintings;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Arcanus.MOD_ID)
public class Arcanus {
    public static final String MOD_ID = "arcanus";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Arcanus() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModTabs.register(modEventBus);

        // Registra os itens e blocos
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModPaintings.PAINTINGS.register(modEventBus);

        // Adiciona setup e creative tabs
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        // EventBus global (ex: eventos do servidor)
        MinecraftForge.EVENT_BUS.register(this);


    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Executando setup comum do mod Arcanus...");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {


        // Exibe o item no inventário criativo (opcional, se quiser mostrar algo agora)
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.Arcane_Whip);
            event.accept(ModItems.arcane_fragment);
            event.accept(ModItems.VOODOO);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ARCANE_ESSENCE);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.ARCANE_BLOCK_ITEM);
            event.accept(ModBlocks.CRYSTAL_CAPSTONE);
            event.accept(ModBlocks.FOUNDATION_ARCANE);
            event.accept(ModBlocks.SIGIL_COLUMN);
        }
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModBlocks.ARCANE_DEBRIS_ITEM);
        }

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Servidor iniciando com o mod Arcanus!");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Setup do CLIENTE do Arcanus iniciado!");
        }
    }
}
