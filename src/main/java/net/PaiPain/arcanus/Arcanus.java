package net.PaiPain.arcanus;

import net.PaiPain.arcanus.registry.ModItems;
import net.PaiPain.arcanus.registry.ModBlocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(Arcanus.MOD_ID)
public class Arcanus {
    public static final String MOD_ID = "arcanus";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Arcanus() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(bus);
        ModBlocks.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Servidor iniciando com o mod Arcanus!");
    }
}
