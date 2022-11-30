package com.github.audio.client.audio.mp3;

import com.github.audio.Utils;
import com.github.audio.client.audio.AudioContext;
import com.github.audio.api.Interface.ISoundHandlerBranch;
import com.github.audio.client.ClientEventHandler;
import com.github.audio.client.gui.AudioToastMessage;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.sound.AudioSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;


/**
 * Specific method and function about how the soundEvent from audio correctly runs, the logic of how those method
 * running is in the class {@link Mp3SoundHandler}.
 */
public final class Mp3HandleMethod {

    public static final ArrayList<String> SOUND_SOURCE_PATH = new ArrayList<String>();
    static final int SOUND_AUTO_SWITCH_CHECK_INTERVAL = 60;

    /* For init */
    static boolean hasInitRFB = false;
    static Utils.RollFontHelper rfb = new Utils.RollFontHelper("");
    public static boolean hasInitSoundSourcePath = false;

    public static Enum<HandleMethodFactory.HandleMethodType> handler = HandleMethodFactory.HandleMethodType.NULL;
    static long lastPlaybackChecked = 0;
    /* For font time ticker */
    static int timeTicker = 0;

    public static AudioSound getCurrentAudioSound() {
        return Mp3Context.getCtx().currentAudioSound;
    }

    public static void flushCurrentRollingBar() {
        rfb = Utils.getRollingBar(getCurrentAudioSound() == null ? "No Audio Sound Found" : getCurrentAudioSound().getSignedName()).get();
        Mp3Context.getCtx().currentSongNameRollingBar = rfb.nextRollingFormat();
        timeTicker = 0;
    }

    protected static void audioToastDraw() {
        new AudioToastMessage().show("Now Playing:", getCurrentAudioSound().getSignedName().length() > 20 ?
                getCurrentAudioSound().getSignedName().substring(0, 20) + "..." : getCurrentAudioSound().getSignedName());
    }

    protected static void preventAutoSwitch() {
        if (Minecraft.getInstance().world != null) {
            Mp3Context.getCtx().lastPreventAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
        }
        Mp3Context.getCtx().preventAutoSwitch = true;
    }

    /**
     * play a sound to player while this method will not render the last song.
     */
    public static void playSoundWithoutOverRender(UUID playerUUID, ISound sound) {
        AudioContext.PLAYER_UUID_LIST.remove(playerUUID);
        Minecraft.getInstance().getSoundHandler().play(sound);
        AudioContext.PLAYER_UUID_LIST.put(playerUUID, sound);
    }

    public static void stopSound(UUID playerUUID) {
        if (AudioContext.PLAYER_UUID_LIST.containsKey(playerUUID)) {
            Minecraft.getInstance().getSoundHandler().stop(AudioContext.PLAYER_UUID_LIST.remove(playerUUID));
        }
    }

    /* To judge when exactly the custom sound source has changed */
    public static void initSoundList() {
        Utils.CollectionHelper.add(SOUND_SOURCE_PATH, "a_fine_red_mist", "blue_room", "breath_of_a_serpent", "chemical_brew", "china_town",
                "come_and_see", "driving_force", "end_of_the_road", "full_confession", "hit_the_floor", "katana_zero",
                "meat_grinder", "nocturne", "overdose", "prison_2", "rain_on_bricks", "silhouette", "sneaky_driver",
                "snow", "worst_neighbor_ever", "third_district", "you_will_never_know", "start_up", "katana_zero_init", "katana_zero_end");
        Utils.CollectionHelper.add(Mp3.MODE_LIST, Mp3.RelayMode.DEFAULT, Mp3.RelayMode.SINGLE, Mp3.RelayMode.RANDOM);
    }

    /**
     * draw the statues bar while player holding mod item such as Mp3.
     *
     * @param event
     */
    @SubscribeEvent
    public static void tick2(TickEvent.ClientTickEvent event) {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        ClientWorld clientWorld = Minecraft.getInstance().world;
        if (clientPlayer == null || clientWorld == null) return;

        if (Mp3Context.getCtx().currentSourceHasChanged || !hasInitRFB) {
            flushCurrentRollingBar();
            Mp3Context.getCtx().currentSourceHasChanged = false;
            hasInitRFB = true;
        }

        timeTicker++;
        if (timeTicker >= 50) {
            Mp3Context.getCtx().currentSongNameRollingBar = rfb.nextRollingFormat();
            timeTicker = 0;
        }

        if (Mp3Context.getCtx().gonnaPlay) {
            preventAutoSwitch();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(HandleMethodFactory.HandleMethodType.GONNA_PLAY).withBranch(clientPlayer, Mp3Context.getCtx());
        }

        if (!Mp3Context.getCtx().hasCheckedLastTickMode) {
            Mp3Context.getCtx().recordMode = Mp3.currentMode;
            Mp3Context.getCtx().hasCheckedLastTickMode = true;
        }

        if (Mp3Context.getCtx().recordMode != Mp3.currentMode) {
            Mp3Context.getCtx().recordMode = Mp3.currentMode;
            if (Mp3Context.getCtx().recordMode == Mp3.RelayMode.RANDOM) {
                Mp3Context.getCtx().shouldInitRandomList = true;
            }
            Mp3Context.getCtx().hasCheckedLastTickMode = false;
        }
    }

    @SubscribeEvent
    public static void tick1(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().world == null || Minecraft.getInstance().player == null) return;
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;

        if (handler != HandleMethodFactory.HandleMethodType.NULL && handler != HandleMethodFactory.HandleMethodType.GONNA_PLAY) {
            preventAutoSwitch();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(handler).withBranch(clientPlayer, Mp3Context.getCtx());
        }

        boolean flag1 = Mp3Context.getCtx().currentSource != null && Mp3Context.getCtx().currentSource.isStopped() && Mp3Context.getCtx().isPlaySong && !Mp3Context.getCtx().gonnaPlay;
        boolean flag2 = Minecraft.getInstance().world.getGameTime() > Mp3Context.getCtx().lastAutoSwitchChecked + SOUND_AUTO_SWITCH_CHECK_INTERVAL;
        boolean flag3 = Minecraft.getInstance().world.getGameTime() > Mp3Context.getCtx().lastPreventAutoSwitchChecked + SOUND_AUTO_SWITCH_CHECK_INTERVAL;
        boolean flag4 = getHandler() == HandleMethodFactory.HandleMethodType.NULL && !Mp3Context.getCtx().gonnaPlay && Mp3.getCurrentMode() != Mp3.RelayMode.SINGLE;

        if (flag3) {
            Mp3Context.getCtx().preventAutoSwitch = false;
            Mp3Context.getCtx().lastPreventAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
        }

        if (flag1 && flag2 && flag4 && !Mp3Context.getCtx().preventAutoSwitch) {
            Mp3Context.getCtx().lastAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(HandleMethodFactory.HandleMethodType.AUTO_SWITCH_NEXT).withBranch(clientPlayer, Mp3Context.getCtx());
        }
    }

    public static Enum<HandleMethodFactory.HandleMethodType> getHandler() {
        return handler;
    }

    public static class AutoSwitch implements ISoundHandlerBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer, AudioContext context) {
            ClientEventHandler.trySwitchToNext();
        }
    }

}
