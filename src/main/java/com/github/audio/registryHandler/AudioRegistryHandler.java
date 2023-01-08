package com.github.audio.registryHandler;

import com.github.audio.api.NameGenerator;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundChannel;
import com.github.audio.util.gen.ClientFileOperator;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * @author qqa, blu3
 * @since 1.0.0
 */
public class AudioRegistryHandler {

    public static final AudioSoundRegister REGISTER = new AudioSoundRegister();
    public static final int REGISTRY_SOUND_EVENT_AMOUNT = 50;
    /**
     * @Description: This is the unit registry name for the whole custom sound event.
     */
    public static final String DEF_REGISTRY_NAME = "custom_";
    private static final ClientFileOperator CLIENT_FILE_OPERATOR = ClientFileOperator.getClientFileOperator();
    private static int existedOggAmount = 0;
    /**
     * @Description: This generator is for auto register, which is used for registry an amount of custom ogg sound, this
     * generator is for sound event registry, both for client side and the server side.
     */
    private static final NameGenerator generator = new NameGenerator(DEF_REGISTRY_NAME);

    /*------------------ Used Sound ----------------------------*/
    public static final AudioSound NULL_SOUND_EVENT = REGISTER.registryDef("null_sound_event").get();
    public static final AudioSound KATANA_ZERO_END = REGISTER.registryDef("katana_zero_end").get();
    public static final AudioSound KATANA_ZERO_INIT = REGISTER.registryDef("katana_zero_init").get();
    public static final AudioSound BACKPACK_UNFOLD_SOUND = REGISTER.registryDef("backpack_unfold").get();
    public static final AudioSound BACKPACK_FOLD_SOUND = REGISTER.registryDef("backpack_fold").get();
    /**
     * @Description: The list that hold all the custom sounds name in it, which is used for judge if
     * a sound source belongs to the player's custom song, the condition for the judgement is if the
     * playing song name has a pattern such as "custom_num".
     */
    public static final ArrayList<String> SOUND_SOURCE_PATH = new ArrayList<>();

    public static void initVanillaSound() {
        SoundChannel.MUSIC_BOX_CHANNEL.add(
                new AudioSound.AudioSoundBuilder().tag("bit", "Bit").soundEvent(() -> SoundEvents.BLOCK_NOTE_BLOCK_BIT).build(),
                new AudioSound.AudioSoundBuilder().tag("bell", "Bell").soundEvent(() -> SoundEvents.BLOCK_NOTE_BLOCK_BELL).build(),
                new AudioSound.AudioSoundBuilder().tag("mute", "Mute").soundEvent(NULL_SOUND_EVENT::getSoundEvent).build(),
                new AudioSound.AudioSoundBuilder().tag("pling", "Pling").soundEvent(() -> SoundEvents.BLOCK_NOTE_BLOCK_PLING).build(),
                new AudioSound.AudioSoundBuilder().tag("picked", "Picked").soundEvent(() -> SoundEvents.ENTITY_ITEM_PICKUP).build(),
                new AudioSound.AudioSoundBuilder().tag("book_turn", "Book Turn").soundEvent(() -> SoundEvents.ITEM_BOOK_PAGE_TURN).build());
    }

    /**
     * The register for registry PlayableAudio, this class owns kinds of situations while trying to registry
     * one or more PlayableAudio instance.
     */
    public static class AudioSoundRegister {

        private AudioSoundRegister() {
        }

        private Supplier<AudioSound> registryDef(String registryName) {
            AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().tag(registryName, "as_acoustics").build();
            return () -> registryAudioSound;
        }

        @OnlyIn(Dist.CLIENT)
        private static void moveExistedOggIntoChannel(String registryName, ClientFileOperator.AudioSoundContext context) {
            AudioSound currentAudioSound = SoundChannel.CUSTOM_SOUND_CHANNEL.get(++existedOggAmount).into(SoundChannel.KATANA_ZERO_CHANNEL);
            currentAudioSound.setDisplayName(context.getDisplayName());
            currentAudioSound.setRegistryName(context.getRegistryName());
        }

        @OnlyIn(Dist.CLIENT)
        public static void convertClientOgg() {
            CLIENT_FILE_OPERATOR.contextMap.forEach(AudioSoundRegister::moveExistedOggIntoChannel);
        }

        /**
         * @Description: This is for registry custom sound, registry should be before the sound being loaded, after the
         * sounds are loaded into the client side, the custom sounds should be set to the map's entry.
         */
        private static void registryCus(int amount) {
            for (int i = 0; i < amount; i++) {
                AudioSound registryAudioSound = new AudioSound.AudioSoundBuilder().tag(generator.get(), "non_named").build();
                SOUND_SOURCE_PATH.add(registryAudioSound.into(SoundChannel.CUSTOM_SOUND_CHANNEL).getRegistryName());
            }
        }

        public void autoConstructor() {
            registryCus(50);
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
