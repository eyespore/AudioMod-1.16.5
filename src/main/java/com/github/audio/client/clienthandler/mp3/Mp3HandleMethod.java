package com.github.audio.client.clienthandler.mp3;

import com.github.audio.Utils;
import com.github.audio.api.annotation.ClientOnly;
import com.github.audio.api.Interface.ISoundHandlerBranch;
import com.github.audio.client.clienthandler.ClientEventHandler;
import com.github.audio.client.gui.AudioToastMessage;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.AudioSoundRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static com.github.audio.client.clienthandler.mp3.Mp3Context.currentAudioSound;

/**
 * Specific method and function about how the soundEvent from audio correctly runs, the logic of how those method
 * running is in the class {@link Mp3SoundHandler}.
 */
public final class Mp3HandleMethod {

    static final Map<UUID, ISound> PLAYER_UUID_LIST = new ConcurrentHashMap<>();
    static final int SOUND_AUTO_SWITCH_CHECK_INTERVAL = 60;

    /* For init */
    static boolean hasInitRFB = false;
    static Utils.RollFontHelper rfb = new Utils.RollFontHelper("");
    public static boolean hasInitSoundSourcePath = false;

    public static Enum<HandleMethodFactory.HandleMethodType> toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
    static final LinkedList<Integer> RANDOM_INDEX_LIST = new LinkedList<>();
    static boolean shouldInitRandomList = false;
    static boolean hasRecord = false;
    static long firstRecord;
    static long lastPlaybackChecked = 0;

    private static final ArrayList<Integer> SOUND_INDEX_LIST = new ArrayList<>();

    static void recordNow() {
        if (!hasRecord) {
            firstRecord = Objects.requireNonNull(Minecraft.getInstance().world).getGameTime();
            hasRecord = true;
        }
    }

    /**
     * ----------------- Sound Switch Operation ---------------------------
     */

    static AudioSound toNext() {
        if (isChannelEmpty()) return null;
        if (Mp3.currentMode == Mp3.RelayMode.DEFAULT || Mp3.getCurrentMode() == Mp3.RelayMode.SINGLE) {
            int currentIndex = getChannelSoundList().indexOf(currentAudioSound);
            currentIndex = currentIndex + 1 > getChannelSize() - 1 ? 0 : currentIndex + 1;
            return currentAudioSound = getChannelSoundList().get(currentIndex);

        } else if (Mp3.currentMode == Mp3.RelayMode.RANDOM) {
            if (shouldInitRandomList) initRandomSoundList(true);
            int nextIndex = RANDOM_INDEX_LIST.indexOf(getChannelSoundList().indexOf(currentAudioSound)) + 1;
            return currentAudioSound = getChannelSoundList().get(RANDOM_INDEX_LIST
                    .get(nextIndex > RANDOM_INDEX_LIST.size() - 1 ? initRandomSoundList(true) + 1 : nextIndex));
        }
        return null;
    }

    static AudioSound toLast() {
        if (isChannelEmpty()) return null;
        if (Mp3.currentMode == Mp3.RelayMode.DEFAULT || Mp3.getCurrentMode() == Mp3.RelayMode.SINGLE) {
            int currentIndex = getChannelSoundList().indexOf(currentAudioSound);
            currentIndex = currentIndex - 1 < 0 ? getChannelSize() - 1 : currentIndex - 1;
            return currentAudioSound = getChannelSoundList().get(currentIndex);

        } else if (Mp3.currentMode == Mp3.RelayMode.RANDOM) {
            int lastIndex = RANDOM_INDEX_LIST.indexOf(getChannelSoundList().indexOf(currentAudioSound)) - 1;
            return currentAudioSound = getChannelSoundList().get(RANDOM_INDEX_LIST
                    .get(lastIndex < 0 ? initRandomSoundList(false) - 1 : lastIndex));
        }
        return null;
    }

    protected static AudioSound onCurrent() {
        if (isChannelEmpty()) return null;
        return currentAudioSound;
    }

    /**
     * Method that used for initialize the sound index array while player change the mp3 mode into random, immediately mp3's mode
     * is turn into random, this method will be called to construct a new array.
     *
     * @param startFromBeginning to point which index will this method return.
     * @return if this parameter is true, this method will return the very first index which is 0, or this method will
     * return the last index of the new constructed array which is the final index of that array.
     */
    protected static int initRandomSoundList(boolean startFromBeginning) {
        int toReturn;
        RANDOM_INDEX_LIST.clear();
        RANDOM_INDEX_LIST.addAll(SOUND_INDEX_LIST);
        Collections.shuffle(RANDOM_INDEX_LIST);
        RANDOM_INDEX_LIST.removeIf(integer -> integer == getChannelSoundList().indexOf(currentAudioSound));
        if (startFromBeginning) {
            RANDOM_INDEX_LIST.addFirst(getChannelSoundList().indexOf(currentAudioSound));
            toReturn = 0;
        } else {
            RANDOM_INDEX_LIST.addLast(getChannelSoundList().indexOf(currentAudioSound));
            toReturn = RANDOM_INDEX_LIST.size() - 1;
        }
        shouldInitRandomList = false;
        return toReturn;
    }

    public static AudioSound getCurrentAudioSound() {
        return currentAudioSound;
    }

    public static void flushCurrentRollingBar() {
        rfb = Utils.getRollingBar(getCurrentAudioSound().getSignedName()).get();
        Mp3Context.currentSongNameRollingBar = rfb.nextRollingFormat();
        Mp3SoundHandler.timeTicker = 0;
    }

    protected static void audioToastDraw() {
        new AudioToastMessage().show("Now Playing:", getCurrentAudioSound().getSignedName().length() > 20 ?
                getCurrentAudioSound().getSignedName().substring(0, 20) + "..." : getCurrentAudioSound().getSignedName());
    }

    protected static void preventAutoSwitch() {
        if (Minecraft.getInstance().world != null) {
            Mp3Context.lastPreventAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
        }
        Mp3Context.preventAutoSwitch = true;
    }

    /*---------------- Sound Play&Stop Operation --------------------------*/
    public static void playSound(UUID playerUUID, ISound sound) {
        if (sound == null) {
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null) return;
            clientPlayer.sendChatMessage("ops, seems like we don't got the sound event you are asking for.");
            Mp3.stopMp3(clientPlayer);
            return;
        }

        if (PLAYER_UUID_LIST.containsKey(playerUUID)) {
            Minecraft.getInstance().getSoundHandler().stop(PLAYER_UUID_LIST.remove(playerUUID));
        }

        Minecraft.getInstance().getSoundHandler().play(sound);
        PLAYER_UUID_LIST.put(playerUUID, sound);
    }

    /**
     * play a sound to player while this method will not render the last song.
     */
    public static void playSoundWithoutOverRender(UUID playerUUID, ISound sound) {
        PLAYER_UUID_LIST.remove(playerUUID);
        Minecraft.getInstance().getSoundHandler().play(sound);
        PLAYER_UUID_LIST.put(playerUUID, sound);
    }

    //ISound
    public static void playSimpleSound(SoundEvent soundEvent, UUID backpackUuid, BlockPos pos) {
        playSound(backpackUuid, SimpleSound.ambientWithAttenuation(soundEvent, pos.getX(), pos.getY(), pos.getZ()));
    }

    public static void playSimpleSound(AudioSound audioSound, UUID playerUUID, BlockPos pos) {
        playSound(playerUUID, SimpleSound.ambientWithAttenuation(
                audioSound.getSoundEvent(), pos.getX(), pos.getY(), pos.getZ()));
    }

    private static void playTickableSound(SoundEvent soundEvent, UUID playerUUID, int entityId) {
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        Entity entity = world.getEntityByID(entityId);
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        playSound(playerUUID, new EntityTickableSound(soundEvent, SoundCategory.RECORDS, 2, 1, entity));
    }

    public static void playTickableSound(AudioSound audioSound, UUID playerUUID, int entityId) {
        playTickableSound(audioSound.getSoundEvent(), playerUUID, entityId);
    }

    /**
     * Main method to play itickable sound to player
     */
    public static void playTickableSound(Mp3Context.Mp3SoundContext context, Supplier<AudioSound> sup, boolean renderLast) {
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        Entity entity = world.getEntityByID(context.entityID);
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        SoundEvent sound = (Objects.requireNonNull(sup.get()).getSoundEvent());
        if (renderLast) {
            playSound(context.clientPlayerUUID, new EntityTickableSound(
                    sound, SoundCategory.RECORDS, 2, 1, entity));
        } else {
            playSoundWithoutOverRender(context.clientPlayerUUID, new EntityTickableSound(
                    sound, SoundCategory.RECORDS, 2, 1, entity));
        }

        if (entity instanceof PlayerEntity && entity.getEntityWorld().isRemote) {
            ClientPlayerEntity playerClient = (ClientPlayerEntity) entity;
        }
    }

    public static void stopSound(UUID playerUUID) {
        if (PLAYER_UUID_LIST.containsKey(playerUUID)) {
            Minecraft.getInstance().getSoundHandler().stop(PLAYER_UUID_LIST.remove(playerUUID));
        }
    }

    /*------------------------- GETTER -------------------------------------*/
    static int getChannelSize() {
        return Mp3Context.CURRENT_SOUND_CHANNEL.getChannelSoundList().size();
    }

    protected static boolean isChannelEmpty() {
        return Mp3Context.CURRENT_SOUND_CHANNEL.getChannelSoundList().isEmpty();
    }

    protected static List<AudioSound> getChannelSoundList() {
        return Mp3Context.CURRENT_SOUND_CHANNEL.getChannelSoundList();
    }

    /* To judge when exactly the custom sound source has changed */
    public static void initSoundList() {
        Utils.CollectionHelper.add(Mp3Context.soundSourcePath, "a_fine_red_mist", "blue_room", "breath_of_a_serpent", "chemical_brew", "china_town",
                "come_and_see", "driving_force", "end_of_the_road", "full_confession", "hit_the_floor", "katana_zero",
                "meat_grinder", "nocturne", "overdose", "prison_2", "rain_on_bricks", "silhouette", "sneaky_driver",
                "snow", "worst_neighbor_ever", "third_district", "you_will_never_know", "start_up" , "katana_zero_init" , "katana_zero_end");
        Utils.CollectionHelper.add(Mp3.MODE_LIST, Mp3.RelayMode.DEFAULT, Mp3.RelayMode.SINGLE, Mp3.RelayMode.RANDOM);

        for (int i = 0; i < getChannelSize(); i++) {
            SOUND_INDEX_LIST.add(i, i);
        }

        currentAudioSound = Mp3Context.CURRENT_SOUND_CHANNEL.getChannelSoundList().get(0);
    }

    public static final class ToNext implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, Mp3Context.Mp3SoundContext context) {
            if (Mp3Context.isPaused || !Mp3Context.isPlaySong) {
                currentAudioSound = toNext();
            } else {
                stopSound(clientPlayer.getUniqueID());
                playTickableSound(context, Mp3HandleMethod::toNext, true);
                audioToastDraw();
                Mp3Context.isPlaySong = true;
            }
            toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
            Mp3Context.currentSourceHasChanged = true;
            Mp3Context.isPaused = false;
        }
    }

    @ClientOnly
    public static final class ToLast implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, Mp3Context.Mp3SoundContext context) {
            if (Mp3Context.isPaused || !Mp3Context.isPlaySong) {
                currentAudioSound = toLast();
            } else {
                stopSound(clientPlayer.getUniqueID());
                playTickableSound(context, Mp3HandleMethod::toLast, true);
                Mp3Context.isPlaySong = true;
            }
            Mp3Context.isPaused = false;
            Mp3Context.currentSourceHasChanged = true;
            toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
        }
    }

    @ClientOnly
    public static class PauseOrResume implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, Mp3Context.Mp3SoundContext context) {
            if (!Mp3Context.isPlaySong && !Mp3Context.isPaused) {
                if (!Mp3Context.hasPlayInit) {
                    recordNow();
                    playInitMusic(context);
                    Mp3Context.hasPlayInit = true;
                    Mp3Context.gonnaPlay = true;
                }
            } else if (Mp3Context.isPlaySong && !Mp3Context.isPaused) {
                if (Mp3Context.currentSource == null) return;
                /* If the sound has started to player, first press button turn into pause. */
                Mp3Context.currentSource.pause();
                Mp3Context.isPaused = true;
                Mp3Context.isPlaySong = false;
            } else if (!Mp3Context.isPlaySong) {
                /* The second time when player press the button it turns into resume the sound. */
                Mp3Context.currentSource.resume();
                Mp3Context.isPaused = false;
                Mp3Context.isPlaySong = true;
            }
            toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
        }
    }

    @ClientOnly
    public static class GonnaPlay implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, Mp3Context.Mp3SoundContext context) {
            ClientWorld clientWorld = Minecraft.getInstance().world;
            if (clientWorld == null) return;
            if (clientWorld.getGameTime() - firstRecord > AudioSoundRegistryHandler.KATANA_ZERO_INIT.getDuration() - 10) {
                /* The sound haven't started yet, start from the one displaying in to tooltip of mp3. */
                playTickableSound(context, Mp3HandleMethod::onCurrent, false);
                audioToastDraw();
                Mp3Context.isPaused = false;
                Mp3Context.isPlaySong = true;
                Mp3Context.gonnaPlay = false;
            }
        }
    }

    public static class AutoSwitch implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, Mp3Context.Mp3SoundContext context) {
            ClientEventHandler.trySwitchToNext();
        }
    }

    public static void playInitMusic(Mp3Context.Mp3SoundContext context) {
        playTickableSound(context, () -> AudioSoundRegistryHandler.KATANA_ZERO_INIT, true);
    }

}
