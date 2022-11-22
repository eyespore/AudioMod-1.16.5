package com.github.audio.networking;

import com.github.audio.api.AudioAnnotation;
import com.github.audio.api.IAudioSoundPackBranch;
import com.github.audio.client.clienthandler.mp3.Mp3HandleMethod;
import com.github.audio.client.clienthandler.mp3.Mp3Statues;
import com.github.audio.item.mp3.Mp3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;

import java.util.Objects;

public class ASPHandleMethod {

    @AudioAnnotation.ClientOnly
    protected static class PlayerReborn implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3Statues.resetAllParameter();
        }
    }

    @AudioAnnotation.ClientOnly
    protected static class PlayerChangeDimension implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3HandleMethod.stopSound(clientPlayer.getUniqueID());
            Mp3Statues.resetAllParameter();
            Mp3.playMp3EndSound(Objects.requireNonNull(Minecraft.getInstance().player));
        }
    }

    @AudioAnnotation.ClientOnly
    protected static class PlayerTossMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3.hasMp3InInventory = false;
            new PlayerChangeDimension().withBranch(clientPlayer);
        }
    }

    @AudioAnnotation.ClientOnly
    protected static class PlayerCloseGUI implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (Mp3Statues.currentSource != null && Mp3Statues.isPaused) {
                Mp3Statues.currentSource.pause();
            }
        }
    }

    protected static class PlayerMissMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (Mp3.hasMp3InInventory) Mp3.stopMp3(clientPlayer);
        }
    }

    protected static class PlayerHasMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (!Mp3.hasMp3InInventory) Mp3.hasMp3InInventory = true;
        }
    }

}
