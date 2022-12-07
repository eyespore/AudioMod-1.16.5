package com.github.audio.networking;

import com.github.audio.api.Interface.IAudioSoundPackBranch;
import com.github.audio.master.client.exec.Mp3Executor;
import com.github.audio.item.mp3.Mp3;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public class ASPHandleMethod {

    protected static class PlayerReborn implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3Executor.getExecutor().getSel().reload();
        }
    }

    protected static class PlayerChangeDimension implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3Executor.getExecutor().stopExecutor();
            Mp3Executor.getExecutor().getSel().reload();
            Mp3Executor.getExecutor().playEnd();
        }
    }

    protected static class PlayerTossMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3.isMp3InInventory = false;
            Mp3.isHoldingMp3 = false;
            Mp3Executor.getExecutor().getCtx().preventChecked = true;
            new PlayerChangeDimension().withBranch(clientPlayer);
        }
    }

    protected static class PlayerCloseGUI implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (Mp3Executor.getExecutor().getSel().source != null && Mp3Executor.getExecutor().getCtx().isPaused) {
                Mp3Executor.getExecutor().getSel().source.pause();
            }
        }
    }

    protected static class MissMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (Mp3.isMp3InInventory) Mp3Executor.getExecutor().stopExecutor();
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
