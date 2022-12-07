package com.github.audio.master.client;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.Serializable;
import java.util.*;

/**
 * @author clclFL
 * @Description: The super class of all audio context class, audio context is a kind of class that include
 * various kinds of parameters which are for called in the other class to use.
 */
@OnlyIn(Dist.CLIENT)
public abstract class AudioContext implements Serializable {

    public ClientPlayerEntity player;
    public ClientWorld world;

    public AudioContext(ClientPlayerEntity player, ClientWorld world) {
        this.world = world;
        this.player = player;
    }

    public abstract void reload();

    public final UUID getUUID() {
        return player.getUniqueID();
    }

    public final int getID() {
        return player.getEntityId();
    }

    public boolean isNull() {
        return (player == null || world == null);
    }

    public final Object readResolve() {
        return this;
    }

}
