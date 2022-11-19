package com.github.audio.networking;

import com.github.audio.api.AudioAnnotation;
import com.github.audio.api.IAudioSoundPackBranch;
import com.github.audio.client.clientevent.HandleMethod;
import com.github.audio.client.clientevent.SoundHandler;
import com.github.audio.item.mp3.Mp3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;

import java.util.Objects;

public class AudioSoundPackBranch {

    @AudioAnnotation.ClientOnly
    protected static class PlayerReborn implements IAudioSoundPackBranch {
        @Override
        public void branch(ClientPlayerEntity clientPlayer) {
            HandleMethod.resetAllParameter();
        }
    }

    @AudioAnnotation.ClientOnly
    protected static class PlayerChangeDimension implements IAudioSoundPackBranch {
        @Override
        public void branch(ClientPlayerEntity clientPlayer) {
            SoundHandler.stopSound(clientPlayer.getUniqueID());
            HandleMethod.resetAllParameter();
            Mp3.playMp3EndSound(Objects.requireNonNull(Minecraft.getInstance().player));
        }
    }

    @AudioAnnotation.ClientOnly
    protected static class PlayerTossItem implements IAudioSoundPackBranch {
        @Override
        public void branch(ClientPlayerEntity clientPlayer) {
            new PlayerChangeDimension().branch(clientPlayer);
        }
    }

    @AudioAnnotation.ClientOnly
    protected static class PlayerCloseGUI implements IAudioSoundPackBranch {
        @Override
        public void branch(ClientPlayerEntity clientPlayer) {
            if (SoundHandler.currentSource != null && HandleMethod.isPaused) {
                SoundHandler.currentSource.pause();
            }
        }
    }

}
