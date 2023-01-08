package com.github.audio.master.client;

import com.github.audio.Audio;
import com.github.audio.client.gui.ToastMessage;
import com.github.audio.master.client.api.IDeviceExecutor;
import com.github.audio.master.client.sound.PlayableAudio;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class DeviceExecutor<K extends AudioContext, T extends Selector> extends AudioExecutor
        implements Serializable, IDeviceExecutor<K, T> {

    protected T sel;
    protected K ctx;
    protected final List<PlayableAudio> devicePlayList = Collections.synchronizedList(new ArrayList<>());

    public DeviceExecutor() {
    }

    public void drawToast() {
        String displayName = sel.getCurrent().getDisplayName();
        new ToastMessage().show("Now Playing:", displayName.length() > 20 ?
                displayName.substring(0, 20) + "..." : displayName);
        Audio.info("registry name : " + sel.getCurrent().getRegistryName());
        Audio.info("display name : " + sel.getCurrent().getDisplayName());
        Audio.info("sound event :" + sel.getCurrent().getSoundEvent());
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

    protected final void playForSingleTime(PlayableAudio sound) {
        super.playSound(sound);
    }

    @Override
    protected final void playSound(PlayableAudio sound) {
        devicePlayList.add(sound);
        super.playSound(sound);
    }

    protected void setVolume(final float volume) {
        if (this.devicePlayList.isEmpty()) return;
        this.devicePlayList.forEach(a -> a.setVolume(volume));
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
