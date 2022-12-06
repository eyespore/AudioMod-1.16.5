package com.github.audio.client.audio.exec;

import com.github.audio.Audio;
import com.github.audio.api.Interface.Looper;
import com.github.audio.util.Roller;
import com.github.audio.api.annotation.Executor;
import com.github.audio.api.exception.InitBeforeWorldGenerationException;
import com.github.audio.api.exception.MultipleSingletonException;
import com.github.audio.client.audio.*;
import com.github.audio.client.audio.ctx.Mp3Context;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.sound.AudioSoundRegistryHandler;
import com.github.audio.sound.SoundChannel;
import com.github.audio.util.Utils;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

@Executor
@OnlyIn(Dist.CLIENT)
public class Mp3Executor extends AudioExecutor<Mp3Context, RandomSelector>
        implements IAutoSwitchable, IPauseable {

    private static final Mp3Executor EX = getExecutor();
    private static Enum<Handler> handler = Handler.NULL;
    private static Saver saver;

    public String rollString;
    public boolean hasInitPath = false;
    public Roller rs = new Roller(() -> isNullEnv() ?
            "No Source Found" : sel.getCurrent().getRegistryName());

    private Mp3Executor() {
        if (Mp3ExecutorHolder.EX != null) {
            throw new MultipleSingletonException(getExecutor());
        }
    }

    private static class Mp3ExecutorHolder {
        public static final Mp3Executor EX = new Mp3Executor();
    }

    public static Mp3Executor getExecutor() {
        return Mp3ExecutorHolder.EX;
    }

    public void playInit() {
        playSound(AudioSoundRegistryHandler.KATANA_ZERO_INIT);
    }

    public void playEnd() {
        playSound(AudioSoundRegistryHandler.KATANA_ZERO_END);
    }

    @Override
    public RandomSelector getSel() {
        return sel;
    }

    @Override
    public Mp3Context getCtx() {
        return ctx;
    }

    @Override
    public void init() throws InitBeforeWorldGenerationException {
        ClientPlayerEntity player = getPlayer().get();
        ClientWorld world = getWorld().get();
        if (player == null || world == null)
            throw new InitBeforeWorldGenerationException("Minecraft.getInstance().player");
        ctx = new Mp3Context(player, world);
        sel = new RandomSelector(new DefaultSelector(SoundChannel.KATANA_ZERO_CHANNEL));
        saver = new Saver(() -> sel.getPointer() , 80);
        Mp3.MODE_LIST.addAll(Arrays.asList(Mp3.RelayMode.DEFAULT, Mp3.RelayMode.SINGLE, Mp3.RelayMode.RANDOM));
        sel.reload();
        ctx.reload();
    }

    @Override
    public void toNext() {
        ctx.preventChecked = true;
        boolean flag1 = Mp3.currentMode == Mp3.RelayMode.RANDOM;
        stopAudio();
        if (ctx.isPaused || !ctx.isPlaySong) {
            if (!flag1) sel.next();
            else sel.randNext();
        } else {
            if (!flag1) playAudio(sel.next());
            else playAudio(sel.randNext());
            drawToast();
            ctx.isPlaySong = true;
        }
        ctx.isPaused = false;
        sel.sourceChange = true;
    }

    @Override
    public void toLast() {
        ctx.preventChecked = true;
        boolean flag1 = Mp3.currentMode == Mp3.RelayMode.RANDOM;
        stopAudio();
        if (ctx.isPaused || !ctx.isPlaySong) {
            if (!flag1) sel.last();
            else sel.randLast();
        } else {
            if (!flag1) playAudio(sel.last());
            else playAudio(sel.randLast());
            ctx.isPlaySong = true;
        }
        ctx.isPaused = false;
        sel.sourceChange = true;
    }

    @Override
    public void toStop() {
        if (ctx.isPlaySong) {
            playEnd();
            stopAudio();
            ctx.isPaused = false;
            ctx.isPlaySong = false;
            ctx.preventChecked = true;
        }
    }

    @Override
    public void otherChannel() {

    }

    @Override
    public void toPause() {
        ctx.preventChecked = true;
        if (!ctx.isPlaySong && !ctx.isPaused) {
            playInit();
            ctx.gonnaPlay = true;
            return;

        } else if (ctx.isPlaySong && !ctx.isPaused) {
            sel.source.pause();

        } else if (!ctx.isPlaySong) {
            sel.source.resume();

        }
        ctx.isPaused = !ctx.isPaused;
        ctx.isPlaySong = !ctx.isPlaySong;
    }

    @Override
    public void onLogin(EntityJoinWorldEvent event) {
        if (event.isCanceled() || event.getEntity() == null
                || !(event.getEntity() instanceof PlayerEntity)) return;
        if (event.getEntity().getEntityWorld().isRemote) {
            try {
                init();
            } catch (InitBeforeWorldGenerationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLogout(EntityLeaveWorldEvent event) {
        if (event.isCanceled() || event.getEntity() == null
                || !(event.getEntity() instanceof PlayerEntity)) return;
        if (event.getEntity().getEntityWorld().isRemote) {
            toStop();
        }
    }

    @Override
    public void onReborn(PlayerEvent.Clone event) {
        if (event.getPlayer() != null && event.getPlayer().getEntityWorld().isRemote) {
//            Audio.getLOGGER().info("detect player death.");
            toStop();
        }
    }



    @Override
    public void tick(TickEvent.ClientTickEvent event) {
        if (isNullEnv()) return;
        /* detect time and save player's pointer */
        saver.loop(event);
        if (handler.equals(Handler.TO_NEXT)) EX.toNext();
        else if (handler.equals(Handler.TO_LAST)) EX.toLast();
        else if (handler.equals(Handler.PAUSE)) EX.toPause();
        handler = Handler.NULL;
    }

    @Override
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBinds.relayLast.isPressed()) {
            if (!Mp3.isHoldingMp3) return;
            handler = Handler.TO_LAST;
        } else if (KeyBinds.relayNext.isPressed()) {
            if (!Mp3.isHoldingMp3) return;
            handler = Handler.TO_NEXT;
        } else if (KeyBinds.pauseOrResume.isPressed()) {
            if (!Mp3.isHoldingMp3) return;
            handler = Handler.PAUSE;
        }
    }


    @Override
    public void tick2(TickEvent.ClientTickEvent event) {
        if (isNullEnv()) return;

        rollString = rs.loop(event);

        if (sel.sourceChange) {
            ctx.preventChecked = true;
            sel.sourceChange = false;
        }

        if (ctx.gonnaPlay) {
            if (playDelaySound(sel.getCurrent(), 40, event).judge()) {
                drawToast();
                ctx.gonnaPlay = false;
                ctx.isPaused = false;
                ctx.isPlaySong = true;
            }
        }
        checker(event);
    }

    @Override
    public void checker(TickEvent.ClientTickEvent event) {
        if (Mp3.currentMode == Mp3.RelayMode.SINGLE) return;
        boolean flag1 = sel.source != null
                && sel.source.isStopped()
                && ctx.isPlaySong
                && !ctx.gonnaPlay
                && !ctx.preventChecked
                && handler == Handler.NULL;

        if (getTime().get() > ctx.lastChecked + INTERVAL) {
            ctx.lastChecked = getTime().get();
            ctx.preventChecked = false;
        }

        if (flag1) {
            ctx.lastChecked = getTime().get();
            toNext();
        }
    }

    @Override
    public void onSourceChange(SoundEvent.SoundSourceEvent event) {
        if (isNullEnv()) return;
        if (AudioSelector.SOUND_SOURCE_PATH.contains(event.getName())) {
            sel.source = event.getSource();
            sel.sourceChange = true;
        }
    }

    @Override
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity().getEntityWorld().isRemote) {
            Audio.getLOGGER().info("hello from client side!");
        }
    }

    @Override
    public boolean isNullEnv() {
        return super.isNullEnv() || saver == null;
    }

    private static class Saver extends Looper<Integer, Void> {

        public Saver(Supplier<Integer> src, long interval) {
            super(src, interval);
        }

        @Override
        public Function<Integer, Void> process() {
            return (integer) -> {
                if (!isNullEnv()) {
                    getExecutor().getPlayer().get().getPersistentData()
                            .putInt(Utils.MOD_ID + "channel_pointer", src.get());
                }
                return (Void) null;
            };
        }
    }

    private enum Handler {
        NULL, TO_NEXT, TO_LAST, PAUSE, STOP;
    }
}
