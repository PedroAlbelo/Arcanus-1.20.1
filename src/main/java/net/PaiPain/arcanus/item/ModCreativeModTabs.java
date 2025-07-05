package net.PaiPain.arcanus.item;

import net.PaiPain.arcanus.Arcanus;
import net.PaiPain.arcanus.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Arcanus.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ARCANUS_TAB = CREATIVE_MODE_TABS.register
            ("arcanus_tab",() -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ARCANE_ESSENCE.get()))
                    .title(Component.translatable("creativetab.arcanus_tab"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.ARCANE_ESSENCE.get());
                        output.accept(ModItems.arcane_fragment.get());
                        output.accept(ModItems.Arcane_Whip.get());
                        output.accept(ModBlocks.ARCANE_DEBRIS.get());
                        output.accept(ModBlocks.ARCANE_BLOCK.get());
                        output.accept(ModBlocks.FOUNDATION_ARCANE.get());
                        output.accept(ModBlocks.SIGIL_COLUMN.get());
                        output.accept(ModBlocks.CRYSTAL_CAPSTONE.get());
                        output.accept(ModBlocks.ARCANE_PILLAR.get());
                        output.accept(ModItems.VOODOO.get());
                        output.accept(ModItems.ARCANE_SLAVE_SPAWN_EGG.get());
                        output.accept(ModItems.ARCANE_HAMMER.get());
                        output.accept(ModBlocks.pandora_box.get());
                        output.accept(ModItems.ANGEL_TEAR.get());
                    } ))
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
