package com.github.audio.sound;

import com.github.audio.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jaudiotagger.audio.exceptions.CannotReadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.audio.sound.SoundEventHelper.*;

public class SoundEventRegistryHandler {

    private static int registryID = 0;
    private static final String NON_NAMED = "non_named";

    /* Normal Mp3 Sound */
    public static AudioSound katanaZeroInit = new AudioSound("null", "null", null, 0);
    public static AudioSound katanaZeroEnd = new AudioSound("null", "null", null, 0);

    /* For load vanilla volume*/
    private static boolean hasInit = false;
    /* Default means song but not normal sound event. */
    protected static final long DEF_DURATION = 50;

    /* ChannelSoundList Define*/
    private static final LinkedList<AudioSound> KATANA_ZERO = new LinkedList<>();
    private static final ArrayList<AudioSound> BACKPACK_SOUND = new ArrayList<AudioSound>();
    private static final ArrayList<AudioSound> MUSIC_BOX_CLEW_TONE = new ArrayList<AudioSound>();
    private static final ArrayList<AudioSound> NORMAL_SOUND = new ArrayList<AudioSound>();
    /**
     * for registry custom sound.
     */
    private static final HashMap<String, AudioSound> CUSTOM_SOUND_MAP = new HashMap<String, AudioSound>();

    public static class SoundChannel {

        private final List<AudioSound> channelSoundList;

        /* Channel */
        public static final SoundChannel BACKPACK_CHANNEL = new SoundChannel(BACKPACK_SOUND);
        public static final SoundChannel MUSIC_BOX_CHANNEL = new SoundChannel(MUSIC_BOX_CLEW_TONE);
        public static final SoundChannel KATANA_ZERO_CHANNEL = new SoundChannel(KATANA_ZERO);
        public static final SoundChannel NORMAL_SOUND_CHANNEL = new SoundChannel(NORMAL_SOUND);

        private SoundChannel(List<AudioSound> channelSoundList) {
            this.channelSoundList = channelSoundList;
        }

        public AudioSound getFromRegistryName(String registryName) {
            List<String> collect = this.channelSoundList.stream().map(audioSound -> audioSound.registryName).collect(Collectors.toList());
            return this.channelSoundList.get(collect.contains(registryName) ? collect.indexOf(registryName) : 0);
        }

        public List<AudioSound> getChannelSoundList() {
            return channelSoundList;
        }
    }

    /* Sound Register */
    public static final DeferredRegister<SoundEvent> SOUND_REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Utils.MOD_ID);
    /*------------------ Default Sound Registry ----------------------------*/
//    public static final RegistryObject<SoundEvent> NULL = registryIntoSoundChannel("null", SoundChannel.NORMAL_SOUND_CHANNEL, false);
    public static final RegistryObject<SoundEvent> BACKPACK_UNFOLD_SOUND = registryIntoSoundChannel("backpack_unfold", SoundChannel.BACKPACK_CHANNEL, true).get();
    public static final RegistryObject<SoundEvent> BACKPACK_FOLD_SOUND = registryIntoSoundChannel("backpack_fold", SoundChannel.BACKPACK_CHANNEL, true).get();
    /*------------------ Non Default Sound Registry ------------------------*/
    public static final RegistryObject<SoundEvent> RAIN_ON_BRICKS = registryIntoSoundChannel("rain_on_bricks", SoundChannel.KATANA_ZERO_CHANNEL, 1619).get();
    public static final RegistryObject<SoundEvent> HIT_THE_FLOOR = registryIntoSoundChannel("hit_the_floor", SoundChannel.KATANA_ZERO_CHANNEL, 4216).get();
    public static final RegistryObject<SoundEvent> COME_AND_SEE = registryIntoSoundChannel("come_and_see", SoundChannel.KATANA_ZERO_CHANNEL, 2084).get();
    public static final RegistryObject<SoundEvent> NOCTURNE = registryIntoSoundChannel("nocturne", SoundChannel.KATANA_ZERO_CHANNEL, 2673).get();
    public static final RegistryObject<SoundEvent> A_FINE_RED_MIST = registryIntoSoundChannel("a_fine_red_mist", SoundChannel.KATANA_ZERO_CHANNEL, 1456).get();
    public static final RegistryObject<SoundEvent> END_OF_THE_ROAD = registryIntoSoundChannel("end_of_the_road", SoundChannel.KATANA_ZERO_CHANNEL, 6001).get();
    public static final RegistryObject<SoundEvent> PRISON_TWO = registryIntoSoundChannel("prison_2", SoundChannel.KATANA_ZERO_CHANNEL, 2856).get();
    public static final RegistryObject<SoundEvent> SILHOUETTE = registryIntoSoundChannel("silhouette", SoundChannel.KATANA_ZERO_CHANNEL, 4320).get();
    public static final RegistryObject<SoundEvent> CHEMICAL_BREW = registryIntoSoundChannel("chemical_brew", SoundChannel.KATANA_ZERO_CHANNEL, 6629).get();
    public static final RegistryObject<SoundEvent> YOU_WILL_NEVER_KNOW = registryIntoSoundChannel("you_will_never_know", SoundChannel.KATANA_ZERO_CHANNEL, 3916).get();
    //    public static final RegistryObject<SoundEvent> WORST_NEIGHBOR_EVER = registryIntoSoundChannel("worst_neighbor_ever", SoundChannel.KATANA_ZERO_CHANNEL, 1481).get();
    public static final RegistryObject<SoundEvent> BLUE_ROOM = registryIntoSoundChannel("blue_room", SoundChannel.KATANA_ZERO_CHANNEL, 3495).get();
    public static final RegistryObject<SoundEvent> FULL_CONFESSION = registryIntoSoundChannel("full_confession", SoundChannel.KATANA_ZERO_CHANNEL, 4912).get();
    public static final RegistryObject<SoundEvent> SNOW = registryIntoSoundChannel("snow", SoundChannel.KATANA_ZERO_CHANNEL, 2806).get();
    public static final RegistryObject<SoundEvent> CHINATOWN = registryIntoSoundChannel("china_town", SoundChannel.KATANA_ZERO_CHANNEL, 7243).get();
    public static final RegistryObject<SoundEvent> BREATH_OF_A_SERPENT = registryIntoSoundChannel("breath_of_a_serpent", SoundChannel.KATANA_ZERO_CHANNEL, 4331).get();
    public static final RegistryObject<SoundEvent> DRIVING_FORCE = registryIntoSoundChannel("driving_force", SoundChannel.KATANA_ZERO_CHANNEL, 5139).get();
    public static final RegistryObject<SoundEvent> SNEAKY_DRIVER = registryIntoSoundChannel("sneaky_driver", SoundChannel.KATANA_ZERO_CHANNEL, 5599).get();
    public static final RegistryObject<SoundEvent> THIRD_DISTRICT = registryIntoSoundChannel("third_district", SoundChannel.KATANA_ZERO_CHANNEL, 5488).get();
    public static final RegistryObject<SoundEvent> OVERDOSE = registryIntoSoundChannel("overdose", SoundChannel.KATANA_ZERO_CHANNEL, 5324).get();
    public static final RegistryObject<SoundEvent> KATANA_ZERO_Z = registryIntoSoundChannel("katana_zero", SoundChannel.KATANA_ZERO_CHANNEL, 6001).get();
    public static final RegistryObject<SoundEvent> MEAT_GRINDER = registryIntoSoundChannel("meat_grinder", SoundChannel.KATANA_ZERO_CHANNEL, 4495).get();
    public static final RegistryObject<SoundEvent> START_UP = registryIntoSoundChannel("start_up", SoundChannel.KATANA_ZERO_CHANNEL, 440).get();
    /*--------------------- Normal Sound Registry --------------------------*/
    public static final RegistryObject<SoundEvent> KATANA_ZERO_INIT = registryAsNormalAudioSound("katana_zero_init", katanaZeroInit, 50).get();
    public static final RegistryObject<SoundEvent> KATANA_ZERO_END = registryAsNormalAudioSound("katana_zero_end", katanaZeroEnd, 28).get();
    /*--------------------- Custom Sound Registry --------------------------*/
    public static final RegistryObject<SoundEvent> CUS_00 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_01 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_02 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_03 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_04 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_05 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_06 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_07 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_08 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_09 = asCustomSound().get();
    public static final RegistryObject<SoundEvent> CUS_10 = asCustomSound().get();


    public static String getCustomSoundRegistryID() {
        return "custom_" + (++registryID);
    }

    private static Supplier<RegistryObject<SoundEvent>> asCustomSound() {
        String registryID = getCustomSoundRegistryID();
        return () -> SOUND_REGISTER.register(registryID, () -> {
            SoundEvent registrySoundEvent = new SoundEvent(new ResourceLocation(Utils.MOD_ID, registryID));
            CUSTOM_SOUND_MAP.put(registryID, new AudioSoundBuilder().registryName(registryID).displayName(NON_NAMED).soundEvent(registrySoundEvent).duration(DEF_DURATION).build());
            return registrySoundEvent;
        });
    }

    /* Method for audioSound instance construction. */

    /**
     * to registry a song into a channel, the registry name will have to be divided into part with underscore
     * and lowercase such as "breath_of_a_serpent".
     */
    private static Supplier<RegistryObject<SoundEvent>> registryIntoSoundChannel(String registryName, SoundChannel registryChannel, boolean useDefDuration) {
        try {
            return registryIntoSoundChannel(registryName, registryChannel,
                    (useDefDuration ? DEF_DURATION : getSongDuration(registryName).orElse(DEF_DURATION)));
        } catch (IOException | CannotReadException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean hasPrint = false;

    private static Supplier<RegistryObject<SoundEvent>> registryIntoSoundChannel(String registryName, SoundChannel registryChannel, long duration) {
        return () -> SOUND_REGISTER.register(registryName, () -> {
            SoundEvent registrySoundEvent = new SoundEvent(new ResourceLocation(Utils.MOD_ID, registryName));
            registryChannel.channelSoundList.add(new AudioSound(registryName, getSongName(registryName),
                    registrySoundEvent, duration));
            return registrySoundEvent;
        });
    }

    private static Supplier<RegistryObject<SoundEvent>> registryAsNormalAudioSound(String registryName, AudioSound audioSound, boolean useDefDuration) {
        return () -> {
            try {
                return registryAsNormalAudioSound(registryName, audioSound, useDefDuration ? DEF_DURATION :
                        getSongDuration(registryName).orElse(DEF_DURATION)).get();
            } catch (IOException | CannotReadException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Supplier<RegistryObject<SoundEvent>> registryAsNormalAudioSound(String registryName, AudioSound audioSound, long duration) {
        return () -> SOUND_REGISTER.register(registryName, () -> {

            SoundEvent registrySoundEvent = new SoundEvent(new ResourceLocation(Utils.MOD_ID, registryName));
            AudioSound build = new AudioSoundBuilder().registryName(registryName).displayName(getSongName(registryName))
                    .duration(duration).soundEvent(registrySoundEvent).build();
            audioSound.reset(build);

//            Mp3SoundHandler.SOURCE_PATH.add(registryName);
            return registrySoundEvent;
        });
    }

    /**
     * the song registry name should be the lower case and make sure they have been divided into part
     * by underscore such as "breath_of_a_serpent".
     */
    private static String getSongName(String registryName) {
        String[] strings = registryName.split("_");
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

    private static void init() throws IOException {
        if (!hasInit) {
            /* Add initialization information here. */
            initVanillaSoundEvent();
            hasInit = true;
        }
    }

    /**
     * Initial vanilla sound event here.
     */
    private static void initVanillaSoundEvent() {
        /* Music box clew tone */
        MUSIC_BOX_CLEW_TONE.add(new AudioSound("pling", "Pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING, DEF_DURATION));
        MUSIC_BOX_CLEW_TONE.add(new AudioSound("bit", "Bit", SoundEvents.BLOCK_NOTE_BLOCK_BIT, DEF_DURATION));
        MUSIC_BOX_CLEW_TONE.add(new AudioSound("bell", "Bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL, DEF_DURATION));
        MUSIC_BOX_CLEW_TONE.add(new AudioSound("picked", "Picked", SoundEvents.ENTITY_ITEM_PICKUP, DEF_DURATION));
        MUSIC_BOX_CLEW_TONE.add(new AudioSound("book_turn", "Book Turn", SoundEvents.ITEM_BOOK_PAGE_TURN, DEF_DURATION));
        MUSIC_BOX_CLEW_TONE.add(new AudioSound("null", "Null", null, DEF_DURATION));
    }

    /* registry normal sound event */
    private static RegistryObject<SoundEvent> registrySoundEvent(String registryName) {

        return SOUND_REGISTER.register(registryName, () -> new SoundEvent(
                new ResourceLocation(Utils.MOD_ID, registryName)));
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
