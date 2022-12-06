package com.github.audio.client.audio;

import com.github.audio.client.gui.AudioToastMessage;
import com.github.audio.sound.AudioSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public abstract class AudioExecutor<K extends AudioContext, T extends AudioSelector> extends Executor
        implements Serializable, IAudioExecutor<K, T> {

    protected T sel;
    protected K ctx;

    private boolean hasChecked = false;
    private long firstCheck;
    public static final Map<UUID, ISound> PLAYER_UUID_LIST = new ConcurrentHashMap<>();

    public AudioExecutor() {
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        return;
    }

    @SubscribeEvent
    public void tick2(TickEvent.ClientTickEvent event) {
        return;
    }

    @SubscribeEvent
    public void onLogin(EntityJoinWorldEvent event) {
        return;
    }

    @SubscribeEvent
    public void onLogout(EntityLeaveWorldEvent event) {
        return;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        return;
    }

    @SubscribeEvent
    public void onSourceChange(SoundEvent.SoundSourceEvent event) {
        return;
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        return;
    }

    @SubscribeEvent
    public void onReborn(PlayerEvent.Clone event) {
        return;
    }

    public final Judge playDelaySound(AudioSound audioSound, long delay, TickEvent.ClientTickEvent event) {
        return () -> {
            if (!hasChecked) {
                firstCheck = getTime().get();
                hasChecked = true;
            }
            if (getTime().get() > firstCheck + delay) {
                playAudio(audioSound);
                hasChecked = false;
                return true;
            }
            return false;
        };
    }

    public void drawToast() {
        String signedName = sel.getCurrent().getSignedName();
        new AudioToastMessage().show("Now Playing:", signedName.length() > 20 ?
                signedName.substring(0, 20) + "..." : signedName);
    }

    public void playAudio(AudioSound audioSound) {
        if (isNullEnv()) return;
        playSound(new EntityTickableSound(audioSound.getSoundEvent(),
                SoundCategory.RECORDS, 2, 1, ctx.player));
    }

    /**
     * @param audioSound the audio sound that plays to the player.
     * @Description: This method will play an audio sound that introduce to this method, call getSoundEvent()
     * method from the audio sound and play the sound event to the player.
     * @Notice : this method will NOT render over the audio sound that last play to the player.
     */
    public void playSound(AudioSound audioSound) {
        if (isNullEnv()) return;
        Minecraft.getInstance().getSoundHandler().play(new EntityTickableSound(audioSound.getSoundEvent(),
                SoundCategory.RECORDS, 2, 1, ctx.player));
    }

    public void playAudio(AudioSound audioSound, BlockPos pos) {
        playSound(SimpleSound.ambientWithAttenuation(
                audioSound.getSoundEvent(), pos.getX(), pos.getY(), pos.getZ()));
    }

    public final void stopAudio() {
        if (isNullEnv()) return;
        if (PLAYER_UUID_LIST.containsKey(ctx.getUUID())) {
            Minecraft.getInstance().getSoundHandler().stop(PLAYER_UUID_LIST.remove(ctx.getUUID()));
        }
    }

    private void playSound(ISound sound) {
        PLAYER_UUID_LIST.put(ctx.getUUID(), sound);
        Minecraft.getInstance().getSoundHandler().play(sound);
    }

    /**
     * @Description: Compare the client player and the client world to judge if this context has not been initialized
     * yet, using this method to judge if this context is null will be more efficient than individually judge whether
     * player is null or world is null.
     * @return The result that if the environment now is null, in such environment the code should stop.
     */
    @Override
    public boolean isNullEnv() {
        return  Minecraft.getInstance().world == null
                || Minecraft.getInstance().player == null
                || ctx == null
                || sel == null
                || ctx.isNull();
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    public final Object readResolve() {
        return this;
    }

    @SuppressWarnings({"unused", "java:S1172"})
    @SubscribeEvent
    //recognize which event this method should be subscribed to
    public void onWorldUnload(WorldEvent.Unload event) {
        AudioExecutor.PLAYER_UUID_LIST.clear();
    }

}
