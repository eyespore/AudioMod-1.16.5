package com.github.audio.client.clienthandler.mp3;

import com.github.audio.api.AudioContext;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundSource;
import net.minecraft.client.entity.player.ClientPlayerEntity;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class Mp3Context extends AudioContext {

    /**
     * The sound channel using for playing now.
     */
    public static final SoundChannel CURRENT_SOUND_CHANNEL =
            SoundChannel.KATANA_ZERO_CHANNEL;

    /* If you change the value "CURRENT_SOUND_CHANNEL", at the same time you should change this field as well. */
    static final int SOUND_STOP_CHECK_INTERVAL = 10;
    public static boolean isPaused;
    public static boolean isPlaySong;
    public static boolean hasPlayInit;
    public static SoundSource currentSource;
    public static AudioSound currentAudioSound;
    public static String currentSongNameRollingBar;
    public static final ArrayList<String> soundSourcePath = new ArrayList<String>();

    private static Enum<Mp3.RelayMode> recordMode;
    private static boolean hasCheckedLastTickMode = false;

    protected static boolean gonnaPlay = false;
    protected static boolean preventAutoSwitch = false;

    static long lastPreventAutoSwitchChecked = 0;
    static long lastAutoSwitchChecked = 0;
    public static boolean currentSourceHasChanged = false;

    protected static Enum<HandleMethodFactory.HandleMethodType> getHandler() {
        return Mp3HandleMethod.toBeSolved;
    }

    /* Call this method only in client side, reset all mark defined in the class. */
    public static void reset() {
        isPaused = false;
        isPlaySong = false;
        hasPlayInit = false;
        gonnaPlay = false;

        preventAutoSwitch = false;
        currentSourceHasChanged = false;
        hasCheckedLastTickMode = false;

        Mp3.isHoldingMp3 = false;
        Mp3HandleMethod.hasRecord = false;
        Mp3HandleMethod.hasInitRFB = false;
        Mp3HandleMethod.shouldInitRandomList = (Mp3.currentMode == Mp3.RelayMode.RANDOM);
        Mp3HandleMethod.toBeSolved = HandleMethodFactory.HandleMethodType.NULL;
    }

    /**
     * gathering the information for play itickable sound to player or entity.
     */
    public static class Mp3SoundContext{
        public int entityID;
        public UUID clientPlayerUUID;
        public AudioSound currentAudioSound;
        public SoundChannel currentChannel = CURRENT_SOUND_CHANNEL;
        public Enum<Mp3.RelayMode> recordMode = Mp3Context.recordMode;
        public boolean hasCheckedLastTickMode = Mp3Context.hasCheckedLastTickMode;


        private Mp3SoundContext(SoundChannel currentChannel, UUID clientPlayerUUID, int entityID) {
            this.entityID = entityID;
            this.currentChannel = currentChannel;
            this.clientPlayerUUID = clientPlayerUUID;
            this.currentAudioSound = Mp3Context.currentAudioSound;
        }

        public static class Mp3SoundCtxBuilder {

            private int entityID;
            private UUID clientPlayerUUID;
            private SoundChannel currentChannel;

            private Mp3SoundCtxBuilder() {
            }

            public static Mp3SoundCtxBuilder ctxBuilder() {
                return new Mp3SoundCtxBuilder();
            }

            public Mp3SoundCtxBuilder currentChannel(SoundChannel channel) {
                this.currentChannel = channel;
                return this;
            }

            public Mp3SoundCtxBuilder entityID(int entityID) {
                this.entityID = entityID;
                return this;
            }

            public Mp3SoundCtxBuilder UUID(UUID uuid) {
                this.clientPlayerUUID = uuid;
                return this;
            }

            public Mp3SoundContext build() {
                return new Mp3SoundContext(currentChannel, clientPlayerUUID, entityID);
            }
        }

    }

    public static Supplier<Mp3SoundContext> getCtx() {
        if (Minecraft.getInstance().player != null) {
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            return () -> Mp3SoundContext.Mp3SoundCtxBuilder.ctxBuilder()
                    .currentChannel(CURRENT_SOUND_CHANNEL)
                    .entityID(clientPlayer.getEntityId())
                    .UUID(clientPlayer.getUniqueID())
                    .build();
        }
        return () -> null;
    }
}
