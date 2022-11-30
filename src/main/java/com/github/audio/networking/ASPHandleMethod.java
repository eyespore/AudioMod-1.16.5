package com.github.audio.networking;

import com.github.audio.api.Interface.IAudioSoundPackBranch;
import com.github.audio.client.audio.mp3.Mp3HandleMethod;
import com.github.audio.client.audio.mp3.Mp3Context;
import com.github.audio.item.mp3.Mp3;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public class ASPHandleMethod {

    protected static class PlayerReborn implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3Context.getCtx().initSoundIndexList();
        }
    }

    protected static class PlayerChangeDimension implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3HandleMethod.stopSound(clientPlayer.getUniqueID());
            Mp3Context.getCtx().initSoundIndexList();
            Mp3Context.getCtx().playMp3EndSound();
        }
    }

    protected static class PlayerTossMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3.isMp3InInventory = false;
            Mp3.isHoldingMp3 = false;
            new PlayerChangeDimension().withBranch(clientPlayer);
        }
    }

    protected static class PlayerCloseGUI implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (Mp3Context.getCtx().currentSource != null && Mp3Context.getCtx().isPaused) {
                Mp3Context.getCtx().currentSource.pause();
            }
        }
    }

    protected static class PlayerMissMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (Mp3.isMp3InInventory) Mp3Context.getCtx().toStop();
            Mp3.isHoldingMp3 = false;
        }
    }

    protected static class PlayerHasMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (!Mp3.isMp3InInventory) Mp3.isMp3InInventory = true;
        }
    }
}
