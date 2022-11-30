package com.github.audio.client.audio;

import com.github.audio.Utils;
import com.github.audio.client.audio.mp3.Mp3Context;
import com.github.audio.client.audio.mp3.Mp3HandleMethod;
import com.github.audio.client.gui.AudioToastMessage;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundChannel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundSource;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author clclFL
 * @Description: The super class of all audio context class, audio context is a kind of class that include
 * various kinds of parameters which are for called in the other class to use.
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT , modid = Utils.MOD_ID)
public abstract class AudioContext implements Serializable {

    private static final ArrayList<Object> TARGETS = new ArrayList<>();

    public static final Map<UUID, ISound> PLAYER_UUID_LIST = new ConcurrentHashMap<>();

    public boolean shouldInitRandomList = false;
    public final LinkedList<Integer> RANDOM_INDEX_LIST = new LinkedList<>();
    public final ArrayList<Integer> SOUND_INDEX_LIST = new ArrayList<>();

    public int entityID;
    public UUID clientPlayerUUID;

    public boolean isPlaySong;
    public SoundSource currentSource;
    public AudioSound currentAudioSound;
    public SoundChannel currentChannel;

    public boolean preventAutoSwitch = false;
    public long lastPreventAutoSwitchChecked = 0;

    private final Consumer<AudioContext> initializer = getInitializer();

    public AudioContext(SoundChannel currentChannel) {
        this.currentChannel = currentChannel;
    }

    /**
     * Main method to play itickable sound to player
     */
    public void playTickableSound(Supplier<AudioSound> sup, boolean renderLast) {
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        Entity entity = world.getEntityByID(entityID);
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        SoundEvent sound = sup.get().getSoundEvent();
        if (sound == null) {
            throw new RuntimeException("no such sound");
        }

        if (renderLast) {
            playSound(clientPlayerUUID, new EntityTickableSound(
                    sound, SoundCategory.RECORDS, 2, 1, entity));
        } else {
            Mp3HandleMethod.playSoundWithoutOverRender(clientPlayerUUID, new EntityTickableSound(
                    sound, SoundCategory.RECORDS, 2, 1, entity));
        }

        if (entity instanceof PlayerEntity && entity.getEntityWorld().isRemote) {
            ClientPlayerEntity playerClient = (ClientPlayerEntity) entity;
        }
    }

    /*---------------- Sound Play&Stop Operation --------------------------*/
    public void playSound(UUID playerUUID, ISound sound) {
        if (sound == null) {
            ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer == null) return;
            clientPlayer.sendChatMessage("ops, seems like we don't got the sound event you are asking for.");
            return;
        }

        if (PLAYER_UUID_LIST.containsKey(playerUUID)) {
            Minecraft.getInstance().getSoundHandler().stop(PLAYER_UUID_LIST.remove(playerUUID));
        }

        Minecraft.getInstance().getSoundHandler().play(sound);
        PLAYER_UUID_LIST.put(playerUUID, sound);
    }

    //ISound
    public void playSimpleSound(SoundEvent soundEvent, UUID backpackUuid, BlockPos pos) {
        playSound(backpackUuid, SimpleSound.ambientWithAttenuation(soundEvent, pos.getX(), pos.getY(), pos.getZ()));
    }

    public void playSimpleSound(AudioSound audioSound, UUID playerUUID, BlockPos pos) {
        playSound(playerUUID, SimpleSound.ambientWithAttenuation(
                audioSound.getSoundEvent(), pos.getX(), pos.getY(), pos.getZ()));
    }

    private void playTickableSound(SoundEvent soundEvent, UUID playerUUID, int entityId) {
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

    public void playTickableSound(AudioSound audioSound) {
        playTickableSound(audioSound.getSoundEvent() , clientPlayerUUID , entityID);
    }

    public final AudioSound getNext() {
        if (isChannelEmpty()) return null;
        int currentIndex = getChannelSoundList().indexOf(currentAudioSound);
        currentIndex = currentIndex + 1 > getChannelSize() - 1 ? 0 : currentIndex + 1;
        return currentAudioSound = getChannelSoundList().get(currentIndex);
    }

    public final AudioSound getLast() {
        if (isChannelEmpty()) return null;
        int currentIndex = getChannelSoundList().indexOf(currentAudioSound);
        currentIndex = currentIndex - 1 < 0 ? getChannelSize() - 1 : currentIndex - 1;
        return currentAudioSound = getChannelSoundList().get(currentIndex);
    }

    public final AudioSound getRandomNext() {
        if (isChannelEmpty()) return null;
        if (shouldInitRandomList) initRandomSoundList(Position.HEAD);
        int nextIndex = RANDOM_INDEX_LIST.indexOf(getChannelSoundList().indexOf(currentAudioSound)) + 1;
        return currentAudioSound = getChannelSoundList().get(RANDOM_INDEX_LIST
                .get(nextIndex > RANDOM_INDEX_LIST.size() - 1 ? initRandomSoundList(Position.HEAD) + 1 : nextIndex));
    }

    public final AudioSound getRandomLast() {
        if (isChannelEmpty()) return null;
        int lastIndex = RANDOM_INDEX_LIST.indexOf(getChannelSoundList().indexOf(currentAudioSound)) - 1;
        return currentAudioSound = getChannelSoundList().get(RANDOM_INDEX_LIST
                .get(lastIndex < 0 ? initRandomSoundList(Position.TAIL) - 1 : lastIndex));
    }

    public final AudioSound getCurrent() {
        if (isChannelEmpty()) return null;
        return currentAudioSound;
    }

    public final void initSoundIndexList() {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        this.entityID = clientPlayer == null ? null : clientPlayer.getEntityId();
        this.clientPlayerUUID = clientPlayer == null ? null : clientPlayer.getUniqueID();
        this.initializer.accept(this);
        this.isPlaySong = false;

        for (int i = 0; i < Mp3Context.getCtx().getChannelSize(); i++) {
            SOUND_INDEX_LIST.add(i, i);
        }

        currentAudioSound = Mp3Context.getCtx().currentChannel.getChannelSoundList().get(0);
    }

    public abstract Consumer<AudioContext> getInitializer();

    public final int getChannelSize() {
        return this.currentChannel.getChannelSoundList().size();
    }

    public final boolean isChannelEmpty() {
        return this.currentChannel.getChannelSoundList().isEmpty();
    }

    public final List<AudioSound> getChannelSoundList() {
        return this.currentChannel.getChannelSoundList();
    }

    public final Object readResolve() {
        return this;
    }

    public void drawAudioToast() {
        new AudioToastMessage().show("Now Playing:", currentAudioSound.getSignedName().length() > 20 ?
                currentAudioSound.getSignedName().substring(0, 20) + "..." : currentAudioSound.getSignedName());
    }

    public void stopSound() {
        if (PLAYER_UUID_LIST.containsKey(clientPlayerUUID)) {
            Minecraft.getInstance().getSoundHandler().stop(PLAYER_UUID_LIST.remove(clientPlayerUUID));
        }
    }

    protected void preventAutoSwitch() {
        if (Minecraft.getInstance().world != null) {
            lastPreventAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
        }
        preventAutoSwitch = true;
    }

    /**
     * Method that used for initialize the sound index array while player change the mp3 mode into random, immediately mp3's mode
     * is turn into random, this method will be called to construct a new array.
     *
     * @param startAt the index that this method return, point to the start position of random array.
     * @return if this parameter is true, this method will return the very first index which is 0, or this method will
     * return the last index of the new constructed array which is the final index of that array.
     */
    public final int initRandomSoundList(Enum<Position> startAt) {
        int toReturn = 0;
        RANDOM_INDEX_LIST.clear();
        RANDOM_INDEX_LIST.addAll(SOUND_INDEX_LIST);
        Collections.shuffle(RANDOM_INDEX_LIST);
        RANDOM_INDEX_LIST.removeIf(integer -> integer == getChannelSoundList().indexOf(this.currentAudioSound));
        if (startAt == Position.HEAD) {
            RANDOM_INDEX_LIST.addFirst(getChannelSoundList().indexOf(this.currentAudioSound));
        } else if (startAt == Position.TAIL) {
            RANDOM_INDEX_LIST.addLast(getChannelSoundList().indexOf(this.currentAudioSound));
            toReturn = RANDOM_INDEX_LIST.size() - 1;
        }
        this.shouldInitRandomList = false;
        return toReturn;
    }

    public enum Position{
        HEAD , TAIL;
    }
}
