package com.github.audio.sound;

import net.minecraft.util.SoundEvent;

/* sound with complete information */
public class AudioSound {
    protected String registryName;
    protected String displayName;
    protected long duration;
    protected SoundEvent soundEvent;

    public AudioSound(String registryName, String displayName, SoundEvent soundEvent, long duration) {
        this.registryName = registryName;
        this.displayName = displayName;
        this.soundEvent = soundEvent;
        this.duration = duration;
    }

    public void reset(AudioSound audioSound) {
        this.registryName = audioSound.registryName;
        this.displayName = audioSound.displayName;
        this.soundEvent = audioSound.soundEvent;
        this.duration = audioSound.duration;
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * get the duration of a song, means the length of how long the song is ganna play.
     *
     * @return the length of song, unit has been translated into "ticks" already.
     */
    public long getDuration() {
        return duration;
    }

    public SoundEvent getSoundEvent() {
        return soundEvent;
    }
}
