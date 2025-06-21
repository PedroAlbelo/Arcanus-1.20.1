package net.PaiPain.arcanus.painting;

import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "arcanus", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModPaintings {
    public static final DeferredRegister<PaintingVariant> PAINTINGS =
            DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, "arcanus");

    public static final RegistryObject<PaintingVariant> ARCANA_SUC = PAINTINGS.register("arcana_suc",
            () -> new PaintingVariant(32, 32));

    public static final RegistryObject<PaintingVariant> BUNNY_GIRL = PAINTINGS.register("bunny_girl",
            () -> new PaintingVariant(32, 48));

    public static final RegistryObject<PaintingVariant> DARK_EYE = PAINTINGS.register("dark_eye",
            () -> new PaintingVariant(16, 16));

    public static final RegistryObject<PaintingVariant> DRAGON_STONE = PAINTINGS.register("dragon_stone",
            () -> new PaintingVariant(32, 32));

    public static final RegistryObject<PaintingVariant> GREEN_WITCH = PAINTINGS.register("green_witch",
            () -> new PaintingVariant(32, 48));

    public static final RegistryObject<PaintingVariant> LADY_ARCANE = PAINTINGS.register("lady_arcane",
            () -> new PaintingVariant(32, 32));

    public static final RegistryObject<PaintingVariant> MAGIC_PIT = PAINTINGS.register("magic_pit",
            () -> new PaintingVariant(32, 32));

    public static final RegistryObject<PaintingVariant> PORTAL_RUNE = PAINTINGS.register("portal_rune",
            () -> new PaintingVariant(32, 32));

    public static final RegistryObject<PaintingVariant> STREET_GIRL = PAINTINGS.register("street_girl",
            () -> new PaintingVariant(32, 48));

    public static final RegistryObject<PaintingVariant> SUC_RUNES = PAINTINGS.register("suc_runes",
            () -> new PaintingVariant(32, 32));

    public static void register() {
        PAINTINGS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
