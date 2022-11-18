package com.github.audio.api.data;

import com.github.audio.Audio;
import com.github.audio.sound.AudioSound;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class AudioDataReloadListener extends JsonReloadListener {

    private static final Gson GSON = (new GsonBuilder()).create();
    private static final Map<SoundEvent , CompoundNBT> CAPABILITY_SOUND_DATA_MAP = new HashMap<SoundEvent , CompoundNBT>();

    public AudioDataReloadListener() {
        super(GSON, "capabilities");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        for (Map.Entry<ResourceLocation , JsonElement> entry : objectIn.entrySet()) {
            ResourceLocation rl = entry.getKey();
            String path = rl.getPath();

            if (path.contains("/")) {
                String[] strings = path.split("/" , 2);
                ResourceLocation registryName = new ResourceLocation(rl.getNamespace() , strings[1]);
                SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(registryName);

                if (soundEvent == null) {
                    Audio.getLOGGER().warn("registry name with : " + registryName + " does not exist.");
                    return;
                }

                CompoundNBT nbt = null;

                try {
                    nbt = JsonToNBT.getTagFromJson(entry.getValue().toString());
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }

                if (strings[0].equals("audios")) {
                    CAPABILITY_SOUND_DATA_MAP.put(soundEvent , nbt);

                }

            }

        }
    }

    public static AudioSoundCapability deserializeAudioSound (AudioSound audioSound, CompoundNBT tag) {
        AudioSoundCapability toReturn = new AudioSoundCapability();
        if (tag.contains("attributes")) {
            CompoundNBT attributes = tag.getCompound("attributes");
            toReturn.setDuration(attributes.getLong("duration"));
        }

        toReturn.setAudioSound(audioSound);
        return toReturn;
    }

    public static class AudioSoundCapability {
        private AudioSound audioSound;

        public AudioSound getAudioSound() {
            return audioSound;
        }

        public void setAudioSound(AudioSound audioSound) {
            this.audioSound = audioSound;
        }

        private long duration;

        public AudioSoundCapability(long duration) {
            this.duration = duration;
        }

        public AudioSoundCapability() {}

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }
}
