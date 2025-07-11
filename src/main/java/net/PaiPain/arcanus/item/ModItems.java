package net.PaiPain.arcanus.item;

import net.PaiPain.arcanus.item.custom.WhisperBladeItem;
import net.PaiPain.arcanus.Arcanus;
import net.PaiPain.arcanus.entity.ModEntities;
import net.PaiPain.arcanus.item.custom.*;
import net.PaiPain.arcanus.util.ModTiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.http.config.Registry;



public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Arcanus.MOD_ID);

    public static final RegistryObject<Item> ARCANE_ESSENCE = ITEMS.register("arcane_essence",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> arcane_fragment = ITEMS.register("arcane_fragment",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> Arcane_Whip = ITEMS.register("arcane_whip",
            () -> new ArcaneWhipItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> VOODOO = ITEMS.register("voodoo",
            () -> new ItemVoodoo(new Item.Properties().stacksTo(1).durability(1)));

    public static final RegistryObject<Item> ARCANE_SLAVE_SPAWN_EGG = ITEMS.register("arcane_slave_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ARCANE_SLAVE, 0x7a4cff, 0x2e003d, new Item.Properties()));


    public static final RegistryObject<Item> ARCANE_HAMMER = ITEMS.register("arcane_hammer",
            () -> new ArcaneHammerItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ANGEL_TEAR = ITEMS.register("angel_tear",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ANGEL_SPAWN_EGG = ModItems.ITEMS.register("angel_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ANGEL,
                    0xF0E6FF,  // lilás claro (primária)
                    0xA97FFF,  // roxo suave (secundária)
                    new Item.Properties()));

    public static final RegistryObject<Item> ARCANE_MAGNET = ITEMS.register("arcane_magnet",
            () -> new ArcaneMagnetItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ARCANE_DAGGER = ITEMS.register("arcane_dagger",
            () -> new ArcaneDaggerItem(ModTiers.ARCANE, 2, -1.4F, new Item.Properties()));


    public static final RegistryObject<Item> OATH_OF_TWILIGHT = ITEMS.register("oath_of_twilight",
            () -> new OathOfTwilightItem());

    public static final RegistryObject<Item> WHISPERBLADE = ITEMS.register("whisperblade",
            () -> new WhisperBladeItem());

    public static final RegistryObject<Item> BLOOD_MOON = ITEMS.register("blood_moon",
            () -> new BloodMoonItem());

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        bus.addListener(ModItems::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ARCANE_ESSENCE);
        }
    }
}
