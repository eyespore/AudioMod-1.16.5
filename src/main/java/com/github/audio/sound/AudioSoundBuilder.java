package com.github.audio.sound;

import net.minecraft.util.SoundEvent;

public final class AudioSoundBuilder {
    private String registryName;
    private String displayName;
    private long duration;
    private SoundEvent soundEvent;

    private AudioSoundBuilder() {
    }

    public static AudioSoundBuilder anAudioSoundBuilder() {
        return new AudioSoundBuilder();
    }

    public AudioSoundBuilder withRegistryName(String registryName) {
        this.registryName = registryName;
        return this;
    }

    public AudioSoundBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public AudioSoundBuilder withDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public AudioSoundBuilder withSoundEvent(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
        return this;
    }

    public AudioSound build() {
        return new AudioSound(registryName, displayName, soundEvent, duration);
    }
}
