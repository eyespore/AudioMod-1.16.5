package com.github.audio.sound;

import com.github.audio.Audio;
import com.github.audio.master.client.AudioSelector;
import net.minecraft.util.SoundEvents;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author qqa, blu3
 * @since 1.0.0
 */
public class AudioRegistryHandler {
    public static final LinkedHashMap<String, Long> CUSTOM_FILE_MAP = new LinkedHashMap<>();
    protected static final AudioSoundRegister REGISTER = new AudioSoundRegister();

    /*------------------ Used Sound ----------------------------*/
    public static final AudioSound NULL_SOUND_EVENT = REGISTER.registryDef("null_sound_event").get();
    public static final AudioSound KATANA_ZERO_END = REGISTER.registryDef("katana_zero_end").get();
    public static final AudioSound KATANA_ZERO_INIT = REGISTER.registryDef("katana_zero_init").get();
    public static final AudioSound BACKPACK_UNFOLD_SOUND = REGISTER.registryDef("backpack_unfold").get();
    public static final AudioSound BACKPACK_FOLD_SOUND = REGISTER.registryDef("backpack_fold").get();

    protected static void initVanillaSound() {
        SoundChannel.MUSIC_BOX_CHANNEL.add(
                new AudioSound.AudioSoundBuilder().tag("bit", "Bit").soundEvent(() -> SoundEvents.BLOCK_NOTE_BLOCK_BIT).build() ,
                new AudioSound.AudioSoundBuilder().tag("bell", "Bell").soundEvent(() -> SoundEvents.BLOCK_NOTE_BLOCK_BELL).build() ,
                new AudioSound.AudioSoundBuilder().tag("mute", "Mute").soundEvent(NULL_SOUND_EVENT::getSoundEvent).build() ,
                new AudioSound.AudioSoundBuilder().tag("pling", "Pling").soundEvent(() -> SoundEvents.BLOCK_NOTE_BLOCK_PLING).build() ,
                new AudioSound.AudioSoundBuilder().tag("picked", "Picked").soundEvent(() -> SoundEvents.ENTITY_ITEM_PICKUP).build() ,
                new AudioSound.AudioSoundBuilder().tag("book_turn", "Book Turn").soundEvent(() -> SoundEvents.ITEM_BOOK_PAGE_TURN).build());
    }

    /**
     * The register for registry AudioSound, this class owns kinds of situations while trying to registry
     * one or more AudioSound instance.
     */
    public static class AudioSoundRegister {

        private AudioSoundRegister() {}

        /* The AudioSound instance in this map should be defined in the inside. */
        private static final HashMap<String, AudioSound> DEFINED_SOUND_MAP = new HashMap<String, AudioSound>();

        private Supplier<AudioSound> registryDef(String registryName) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().tag(registryName , "non_name").build();
            return construct(registryAudioSound);
        }

        private Supplier<AudioSound> registryDef(String registryName , SoundChannel registryChannel) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().tag(registryName , "non_name").build().into(registryChannel);
            return construct(registryAudioSound);
        }

        private Supplier<AudioSound> construct(AudioSound registryAudioSound) {
            DEFINED_SOUND_MAP.put(registryAudioSound.getRegistryName() , registryAudioSound);
            return () -> registryAudioSound;
        }

        private void registryCus(String displayName , long duration) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder()
                    .display(displayName).duration(duration).build();
            registryAudioSound.into(SoundChannel.KATANA_ZERO_CHANNEL);
            Audio.info("registry into KATANA_ZERO_CHANNEL with : " + displayName + ":" + duration);
            AudioSelector.SOUND_SOURCE_PATH.add(registryAudioSound.getRegistryName());
        }

        protected void autoConstructor() {
            for (Map.Entry<String , Long> entry : CUSTOM_FILE_MAP.entrySet()) {
                registryCus(entry.getKey() , entry.getValue());
            }
        }

        @Deprecated
        private static String toDisplayName(String signedName) {
            String[] strings = signedName.split("_");
            StringBuilder toReturn = new StringBuilder();
            int i = 0;
            for (String str : strings) {
                str = str.substring(0, 1).toUpperCase() + str.substring(1);
                if (i < strings.length - 1) toReturn.append(str).append(" ");
                else toReturn.append(str);
                i++;
            }
            return toReturn.toString();
        }
    }
}
