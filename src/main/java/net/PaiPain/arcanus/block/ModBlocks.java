package net.PaiPain.arcanus.block;

import net.PaiPain.arcanus.Arcanus;
import net.PaiPain.arcanus.block.custom.ArcanePillarBlock;
import net.PaiPain.arcanus.block.custom.CrystalCapstoneBlock;
import net.PaiPain.arcanus.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Arcanus.MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Arcanus.MOD_ID);

    public static final RegistryObject<Block> ARCANE_BLOCK = BLOCKS.register("arcane_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK).sound(SoundType.CALCITE)));

    public static final RegistryObject<Block> ARCANE_DEBRIS = BLOCKS.register("arcane_debris",
            () -> new DropExperienceBlock(BlockBehaviour.Properties
                    .copy(Blocks.DEEPSLATE)
                    .sound(SoundType.AMETHYST)
                    .strength(4.0f, 6.0f)           // força + resistência explosão
                    .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> FOUNDATION_ARCANE = BLOCKS.register("foundation_arcane",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Item> FOUNDATION_ARCANE_ITEM = ITEMS.register("foundation_arcane",
            () -> new BlockItem(FOUNDATION_ARCANE.get(), new Item.Properties()));

    public static final RegistryObject<Block> SIGIL_COLUMN = BLOCKS.register("sigil_column",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)));

    public static final RegistryObject<Item> SIGIL_COLUMN_ITEM = ITEMS.register("sigil_column",
            () -> new BlockItem(SIGIL_COLUMN.get(), new Item.Properties()));

    public static final RegistryObject<Block> CRYSTAL_CAPSTONE = BLOCKS.register("crystal_capstone",
            () -> new CrystalCapstoneBlock(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK)
                    .lightLevel(state -> 15)
                    .strength(4.5f)));

    public static final RegistryObject<Item> CRYSTAL_CAPSTONE_ITEM = ITEMS.register("crystal_capstone",
            () -> new BlockItem(CRYSTAL_CAPSTONE.get(), new Item.Properties()));

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static final RegistryObject<Item> ARCANE_DEBRIS_ITEM = ITEMS.register("arcane_debris",
            () -> new BlockItem(ARCANE_DEBRIS.get(), new Item.Properties()));

    public static final RegistryObject<Item> ARCANE_BLOCK_ITEM = ITEMS.register("arcane_block",
            () -> new BlockItem(ARCANE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> ARCANE_PILLAR = BLOCKS.register("arcane_pillar",
            () -> new ArcanePillarBlock(BlockBehaviour.Properties
                    .copy(Blocks.QUARTZ_BLOCK)
                    .lightLevel(state -> 12)
                    .noOcclusion()// valor de 0 a 15
            ));

    public static final RegistryObject<Item> ARCANE_PILLAR_ITEM = ITEMS.register("arcane_pillar",
            () -> new BlockItem(ARCANE_PILLAR.get(), new Item.Properties()));




    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
