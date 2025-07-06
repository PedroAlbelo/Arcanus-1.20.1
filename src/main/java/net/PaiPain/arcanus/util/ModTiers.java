package net.PaiPain.arcanus.util;

import net.PaiPain.arcanus.item.ModItems;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;


public class ModTiers {

    public static final Tier ARCANE = TierSortingRegistry.registerTier(
            new ForgeTier(
                    3, // Nível de mineração (como diamante)
                    1250, // Durabilidade (entre ferro e netherite)
                    9.0F, // Velocidade de ataque
                    3.0F, // Dano extra
                    15, // Encantabilidade
                    TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("minecraft", "needs_diamond_tool")),
                    () -> Ingredient.of(ModItems.ARCANE_ESSENCE.get())
            ),
            new ResourceLocation("arcanus", "arcane"), // ID do tier
            List.of(Tiers.DIAMOND), // Vem depois do diamante
            List.of(Tiers.NETHERITE) // Mas antes da netherite
    );
}
