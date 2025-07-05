package net.PaiPain.arcanus.sound;

import net.PaiPain.arcanus.Arcanus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Arcanus.MOD_ID);

    // Sons principais
    public static final RegistryObject<SoundEvent> ARCANUS_AND_SKY =
            registerSound("arcanus_and_sky");

    // Sons do Angel
    public static final RegistryObject<SoundEvent> ANGEL_IDLE =
            registerSound("entity.angel.idle");
    public static final RegistryObject<SoundEvent> ANGEL_HURT =
            registerSound("entity.angel.hurt");
    public static final RegistryObject<SoundEvent> ANGEL_DEATH =
            registerSound("entity.angel.death");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        ResourceLocation id = new ResourceLocation(Arcanus.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerAll(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}

