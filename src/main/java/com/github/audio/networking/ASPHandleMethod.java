package com.github.audio.networking;

import com.github.audio.api.Interface.IAudioSoundPackBranch;
import com.github.audio.client.clienthandler.mp3.Mp3HandleMethod;
import com.github.audio.client.clienthandler.mp3.Mp3Context;
import com.github.audio.item.mp3.Mp3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;

import java.util.Objects;

public class ASPHandleMethod {

    protected static class PlayerReborn implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3Context.Mp3Ctx.init();
        }
    }

    protected static class PlayerChangeDimension implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3HandleMethod.stopSound(clientPlayer.getUniqueID());
            Mp3Context.Mp3Ctx.init();
            Mp3.playMp3EndSound(Objects.requireNonNull(Minecraft.getInstance().player));
        }
    }

    protected static class PlayerTossMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3.hasMp3InInventory = false;
            new PlayerChangeDimension().withBranch(clientPlayer);
        }
    }

    protected static class PlayerCloseGUI implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (Mp3Context.Mp3Ctx.currentSource != null && Mp3Context.Mp3Ctx.isPaused) {
                Mp3Context.Mp3Ctx.currentSource.pause();
            }
        }
    }

    protected static class PlayerMissMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (Mp3.hasMp3InInventory) Mp3.stopMp3(clientPlayer);
        }
    }

    protected static class PlayerLogout implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            Mp3Context.Mp3Ctx.init();
        }
    }

    protected static class PlayerHasMp3 implements IAudioSoundPackBranch {
        @Override
        public void withBranch(ClientPlayerEntity clientPlayer) {
            if (!Mp3.hasMp3InInventory) Mp3.hasMp3InInventory = true;
        }
    }

}
