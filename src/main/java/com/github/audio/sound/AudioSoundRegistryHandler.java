package com.github.audio.sound;

import com.github.audio.Utils;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.eventbus.api.IEventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Supplier;

import static com.github.audio.sound.AudioSound.SOUND_REGISTER;

/**
 * @author qqa, blu3
 * @since 1.0.0
 */
public class AudioSoundRegistryHandler {
    public static final HashMap<String, Long> CUSTOM_FILE_MAP = new HashMap<>();

    static {
        Utils.AudioHelper.initMusicFolderMap(CUSTOM_FILE_MAP);
    }
    private static boolean hasInit = false;
    private static final AudioSoundRegister REGISTER = new AudioSoundRegister();

    /*------------------ Default Sound Registry ----------------------------*/
    public static final AudioSound NULL_SOUND_EVENT = REGISTER.registryDef("null_sound_event").get();
    public static final AudioSound KATANA_ZERO_END = REGISTER.registryDef("katana_zero_end").get();
    public static final AudioSound KATANA_ZERO_INIT = REGISTER.registryDef("katana_zero_init").get();
    public static final AudioSound BACKPACK_UNFOLD_SOUND = REGISTER.registryDef("backpack_unfold").get();
    public static final AudioSound BACKPACK_FOLD_SOUND = REGISTER.registryDef("backpack_fold").get();
    /*------------------ Non Default Sound Registry ------------------------*/
    public static final AudioSound RAIN_ON_BRICKS = REGISTER.registryDef("rain_on_bricks", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound HIT_THE_FLOOR = REGISTER.registryDef("hit_the_floor", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound COME_AND_SEE = REGISTER.registryDef("come_and_see", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound NOCTURNE = REGISTER.registryDef("nocturne", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound A_FINE_RED_MIST = REGISTER.registryDef("a_fine_red_mist", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound END_OF_THE_ROAD = REGISTER.registryDef("end_of_the_road", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound PRISON_TWO = REGISTER.registryDef("prison_2", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound SILHOUETTE = REGISTER.registryDef("silhouette", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound CHEMICAL_BREW = REGISTER.registryDef("chemical_brew", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound YOU_WILL_NEVER_KNOW = REGISTER.registryDef("you_will_never_know", SoundChannel.KATANA_ZERO_CHANNEL).get();
    //    public static final AudioSound<SoundEvent> WORST_NEIGHBOR_EVER = REGISTER.registryDef("worst_neighbor_ever", SoundChannel.KATANA_ZERO_CHANNEL, 1481).get();
    public static final AudioSound BLUE_ROOM = REGISTER.registryDef("blue_room", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound FULL_CONFESSION = REGISTER.registryDef("full_confession", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound SNOW = REGISTER.registryDef("snow", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound CHINATOWN = REGISTER.registryDef("china_town", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound BREATH_OF_A_SERPENT = REGISTER.registryDef("breath_of_a_serpent", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound DRIVING_FORCE = REGISTER.registryDef("driving_force", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound SNEAKY_DRIVER = REGISTER.registryDef("sneaky_driver", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound THIRD_DISTRICT = REGISTER.registryDef("third_district", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound OVERDOSE = REGISTER.registryDef("overdose", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound KATANA_ZERO_Z = REGISTER.registryDef("katana_zero", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound MEAT_GRINDER = REGISTER.registryDef("meat_grinder", SoundChannel.KATANA_ZERO_CHANNEL).get();
    public static final AudioSound START_UP = REGISTER.registryDef("start_up", SoundChannel.KATANA_ZERO_CHANNEL).get();

    private static void init() throws IOException {
        if (!hasInit) {
            /* Add initialization information here. */
            initSoundEvent();
            /* Maybe registry custom audio sound here. */
            REGISTER.autoConstructor(20);
            hasInit = true;
        }
    }

    private static void initSoundEvent() {
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
    private static class AudioSoundRegister {
        /* For registry those AudioSound that need to be defined in the code rather than outside. */
        private static final HashMap<String, AudioSound> CUSTOM_SOUND_MAP = new HashMap<String, AudioSound>();
        /* The AudioSound instance in this map should be defined in the inside. */
        private static final HashMap<String, AudioSound> DEFINED_SOUND_MAP = new HashMap<String, AudioSound>();

        private Supplier<AudioSound> registryDef(String registryName) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().tag(registryName , toDisplayName(registryName)).build();
            return construct(registryAudioSound);
        }

        private Supplier<AudioSound> registryDef(String registryName , String displayName) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().tag(registryName, displayName).build();
            return construct(registryAudioSound);
        }

        private Supplier<AudioSound> registryDef(String registryName , SoundChannel registryChannel) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().tag(registryName , toDisplayName(registryName)).build().into(registryChannel);
            return construct(registryAudioSound);
        }

        private Supplier<AudioSound> registryDef(String registryName , String displayName, SoundChannel registryChannel) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().tag(registryName, displayName).build().into(registryChannel);
            return construct(registryAudioSound);
        }

        private Supplier<AudioSound> construct(AudioSound registryAudioSound) {
            DEFINED_SOUND_MAP.put(registryAudioSound.getRegistryName() , registryAudioSound);
            return () -> registryAudioSound;
        }

        /**
         * call this method to registry new sound audio into CUSTOM_SOUND_MAP.
         * Notice that this method should NOT be used individually, it should be called in method or loop.
         */
        @Deprecated
        private void registryCus() {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().build();
            CUSTOM_SOUND_MAP.put(registryAudioSound.getRegistryName() , registryAudioSound);
        }

        @SuppressWarnings("deprecation")
        private void registryCus(String signedName, long duration) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().duration(CUSTOM_FILE_MAP.get(signedName)).build();
            CUSTOM_SOUND_MAP.put(registryAudioSound.getRegistryName() , registryAudioSound);
        }

        private void autoConstructor(int num) {
            int i = 0;
            while (i < num) {
                REGISTER.registryCus();
                i ++;
            }
        }

        /**
         * this method should not be used at *registryCus*.
         * @param signedName The name introduced manually by the developer.
         * @return return displayName transform from signedName.
         */
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

    public static void register(IEventBus eventBus) {
        SOUND_REGISTER.register(eventBus);
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
