package net.PaiPain.arcanus.sound;

import net.PaiPain.arcanus.Arcanus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Arcanus.MOD_ID);

    public static final RegistryObject<SoundEvent> ARCANUS_AND_SKY =
            SOUND_EVENTS.register("arcanus_and_sky", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation(Arcanus.MOD_ID, "arcanus_and_sky")));

}
