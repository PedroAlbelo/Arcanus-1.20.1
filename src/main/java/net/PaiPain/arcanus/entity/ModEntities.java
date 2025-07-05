package net.PaiPain.arcanus.entity;

import net.PaiPain.arcanus.Arcanus;
import net.PaiPain.arcanus.entity.custom.AngelEntity;
import net.PaiPain.arcanus.entity.custom.ArcaneSlaveEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcanus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Arcanus.MOD_ID);

    public static final RegistryObject<EntityType<ArcaneSlaveEntity>> ARCANE_SLAVE =
            ENTITY_TYPES.register("arcane_slave",
                    () -> EntityType.Builder
                            .<ArcaneSlaveEntity>of(ArcaneSlaveEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build(new ResourceLocation(Arcanus.MOD_ID, "arcane_slave.png").toString()));

    public static final RegistryObject<EntityType<AngelEntity>> ANGEL =
            ENTITY_TYPES.register("angel",
                    () -> EntityType.Builder
                            .<AngelEntity>of(AngelEntity::new, MobCategory.MONSTER)
                            .sized(0.9F, 0.9F) // Cabeça voadora, tamanho pequeno
                            .build(new ResourceLocation(Arcanus.MOD_ID, "angel.png").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ARCANE_SLAVE.get(), ArcaneSlaveEntity.createAttributes().build());
        event.put(ANGEL.get(), AngelEntity.createAttributes().build());
    }
}
