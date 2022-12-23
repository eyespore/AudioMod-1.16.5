package com.github.audio.master.client.sound;

import com.github.audio.sound.AudioSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public abstract class PlayableAudio extends TickableSound {

    protected double x;
    protected double y;
    protected double z;
    protected float pitch = DEF_PITCH;
    protected float volume = DEF_VOLUME;
    protected final AudioSound audioSound;
    protected Entity entity;

    private static final float DEF_PITCH = 1.0F;
    private static final float DEF_VOLUME = 1.0F;
    private static final SoundCategory DEF_CATEGORY = SoundCategory.RECORDS;

    private static class GlobalAudio extends PlayableAudio {
        private GlobalAudio(AudioSound audioSound) {
            super(audioSound, new BlockPos(0, 0, 0));
        }

        @Override
        public boolean isGlobal() {
            return true;
        }
    }

    private static class TickableAudio extends PlayableAudio {

        private TickableAudio(AudioSound audioSound, Entity entity) {
            super(audioSound, entity);
        }

        @Override
        public void tick() {
            if (getEntity().removed) {
                this.finishPlaying();
            } else {
                this.x = (double) ((float) this.entity.getPosX());
                this.y = (double) ((float) this.entity.getPosY());
                this.z = (double) ((float) this.entity.getPosZ());
            }
        }

        @Override
        public boolean shouldPlaySound() {
            return !getEntity().isSilent();
        }
    }

    private static class LocatedAudio extends PlayableAudio {
        private LocatedAudio(AudioSound audioSound, BlockPos pos) {
            super(audioSound, pos);
        }
    }

    public static GlobalAudio global(AudioSound audioSound) {
        return new GlobalAudio(audioSound);
    }

    public static LocatedAudio located(AudioSound audioSound, BlockPos pos) {
        return new LocatedAudio(audioSound, pos);
    }

    public static TickableAudio tickable(AudioSound audioSound, Entity entity) {
        return new TickableAudio(audioSound, entity);
    }

    /* for entity tickable sound created */
    private PlayableAudio(AudioSound audioSound, Entity entity) {
        this(audioSound, new BlockPos(entity.getPosX(), entity.getPosX(), entity.getPosZ()));
        this.entity = entity;
    }

    /* for located sound created */
    private PlayableAudio(AudioSound audioSound, BlockPos pos) {
        super(audioSound.getSoundEvent(), DEF_CATEGORY);
        this.audioSound = audioSound;
        this.x = (double) pos.getX();
        this.y = (double) pos.getY();
        this.z = (double) pos.getZ();
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public void tick() {
        return;
    }

    public boolean shouldPlaySound() {
        return true;
    }

    public void setVolume(float volume) {
        this.volume = MathHelper.clamp(volume, 0 , 1);;
    }

    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void stop() {
        Minecraft.getInstance().getSoundHandler().stop(this);
    }
}
