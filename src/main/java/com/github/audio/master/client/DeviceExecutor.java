package com.github.audio.master.client;

import com.github.audio.client.gui.AudioToastMessage;
import com.github.audio.master.client.api.IAudioExecutor;
import com.github.audio.sound.AudioSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.Serializable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public abstract class DeviceExecutor<K extends AudioContext, T extends Selector> extends AudioExecutor
        implements Serializable, IAudioExecutor<K, T> {

    protected T sel;
    protected K ctx;
    protected final List<ISound> devicePlayList = Collections.synchronizedList(new ArrayList<>());

    private boolean hasChecked = false;
    private long firstCheck;

    public DeviceExecutor() {
    }

    public final Judge playDelaySound(AudioSound audioSound, long delay, TickEvent.ClientTickEvent event) {
        return () -> {
            if (!hasChecked) {
                firstCheck = getGameTime().get();
                hasChecked = true;
            }
            if (getGameTime().get() > firstCheck + delay) {
                playAudio(audioSound);
                hasChecked = false;
                return true;
            }
            return false;
        };
    }

    public void drawToast() {
        String displayName = sel.getCurrent().getDisplayName();
        new AudioToastMessage().show("Now Playing:", displayName.length() > 20 ?
                displayName.substring(0, 20) + "..." : displayName);
    }

    public void stopDevice() {
        if (isNullEnv()) return;
        if (!devicePlayList.isEmpty()) {
            devicePlayList.forEach(Minecraft.getInstance().getSoundHandler()::stop);
            devicePlayList.clear();
        }
    }

    public final boolean isPlaying() {
        return !this.devicePlayList.isEmpty();
    }

    protected final void playSound(ISound sound) {
        devicePlayList.add(sound);
        super.playSound(sound);
    }

    /**
     * @Description: Compare the client player and the client world to judge if this context has not been initialized
     * yet, using this method to judge if this context is null will be more efficient than individually judge whether
     * player is null or world is null.
     * @return The result that if the environment now is null, in such environment the code should stop.
     */
    @Override
    public boolean isNullEnv() {
        return super.isNullEnv()
                || ctx == null
                || sel == null
                || ctx.isNull()
                || sel.isNull();
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
        devicePlayList.clear();
    }

}
