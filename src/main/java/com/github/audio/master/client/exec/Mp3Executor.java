package com.github.audio.master.client.exec;

import com.github.audio.Audio;
import com.github.audio.api.EchoConsumer;
import com.github.audio.api.annotation.Exec;
import com.github.audio.api.exception.InitBeforeWorldGenerationException;
import com.github.audio.api.exception.MultipleSingletonException;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.master.client.DeviceExecutor;
import com.github.audio.master.client.api.IPauseable;
import com.github.audio.master.client.api.ISwitchable;
import com.github.audio.master.client.ctx.Mp3Context;
import com.github.audio.master.client.sel.DefSelector;
import com.github.audio.master.client.sel.RandSelector;
import com.github.audio.master.client.sound.PlayableAudio;
import com.github.audio.master.net.Mp3Packet;
import com.github.audio.registryHandler.AudioRegistryHandler;
import com.github.audio.sound.AudioSound;
import com.github.audio.sound.SoundChannel;
import com.github.audio.util.Utils;
import com.github.audio.util.gen.TextHelper;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.function.Consumer;

@Exec(Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class Mp3Executor
        extends DeviceExecutor<Mp3Context, RandSelector>
        implements ISwitchable, IPauseable {

    public String displayStr;
    private TextHelper.Scroller scroller;

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
        playForSingleTime(PlayableAudio.global(AudioRegistryHandler.KATANA_ZERO_INIT));
    }

    public void playEnd() {
        playForSingleTime(PlayableAudio.global(AudioRegistryHandler.KATANA_ZERO_END));
    }

    @Override
    public RandSelector getSel() {
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
        sel = new RandSelector(new DefSelector(SoundChannel.KATANA_ZERO_CHANNEL));
        sel.reload();
        ctx.reload();
        scroller = Utils.getScroller(() -> sel.getCurrent().getDisplayName(), 12);
        scroller.reset();
        Mp3.MODE_LIST.addAll(Arrays.asList(Mp3.RelayMode.DEFAULT, Mp3.RelayMode.SINGLE, Mp3.RelayMode.RANDOM));
        /* To avoid the echo execute just after player join in the world. */
        checker3.setCanceled(true);
        checker3.reset();
        checker1.reset();
        checker2.reset();
    }

    public void stopAudio() {
        super.stopDevice();
        this.checker3.setCanceled(true);
        this.checker3.reset();
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

    private void toTurnUp() {
        if (isNullEnv()) return;

        //TODO
    }

    private void toTurnDown() {
        if (sel.source == null || isNullEnv()) return;

    }

    @Override
    public void toPause() {
        ctx.preventChecked = true;
        if (!ctx.isPlaySong && !ctx.isPaused) {
            playInit();
            ctx.gonnaPlay = true;
            return;

        } else if (ctx.isPlaySong && !ctx.isPaused) {
            if (sel.source == null) {
                Audio.warn("No sound source to pause!");
                return;
            }
            sel.source.pause();

        } else if (!ctx.isPlaySong) {
            if (sel.source == null) {
                Audio.warn("No sound source to resume!");
                return;
            }
            sel.source.resume();

        }
        ctx.isPaused = !ctx.isPaused;
        ctx.isPlaySong = !ctx.isPlaySong;
    }

    @Override
    public void stopDevice() {
        if (isPlaying()) playEnd();
        stopAudio();
        super.stopDevice();
        ctx.gonnaPlay = false;
        ctx.isPaused = false;
        ctx.isPlaySong = false;
        ctx.preventChecked = true;
    }

    @Override
    public void otherChannel() {

    }

    @SubscribeEvent
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

    @SubscribeEvent
    public void onLogout(EntityLeaveWorldEvent event) {
        if (event.isCanceled() || event.getEntity() == null
                || !(event.getEntity() instanceof PlayerEntity)) return;
        if (event.getEntity().getEntityWorld().isRemote) {
            stopDevice();
        }
    }

    @SubscribeEvent
    public void onReborn(PlayerEvent.Clone event) {
        if (event.getPlayer() != null && event.getPlayer().getEntityWorld().isRemote) {
//            Audio.getLOGGER().info("detect player death.");
            stopDevice();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBinds.relayLast.isPressed()) {
            if (!Mp3.isHoldingMp3) return;
            if (Screen.hasControlDown()) options(Handler.TURN_DOWN);
            else options(Handler.TO_LAST);

        } else if (KeyBinds.relayNext.isPressed()) {
            if (!Mp3.isHoldingMp3) return;
            if (Screen.hasControlDown()) options(Handler.TURN_UP);
            else options(Handler.TO_NEXT);

        } else if (KeyBinds.pauseOrResume.isPressed()) {
            if (!Mp3.isHoldingMp3) return;
            options(Handler.PAUSE);
        }
    }

    private void options(Handler handler) {
        if (isNullEnv()) return;
        switch (handler) {
            case TO_NEXT:
                toNext();
                break;
            case TO_LAST:
                toLast();
                break;
            case PAUSE:
                toPause();
                break;
            case TURN_UP:
                toTurnUp();
                break;
            case TURN_DOWN:
                toTurnDown();
                break;
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (isNullEnv()) return;
        displayStr = scroller.loop(event, 40).toStr();
        checker1.loop(event);
        checker2.loop(event, 60);
        checker3.loop(event, 45);
    }

    /**
     * @Description: this is the main check of auto switching song function.
     */
    private final EchoConsumer<ClientPlayerEntity> checker1 = new EchoConsumer<ClientPlayerEntity>(getPlayer()) {
        @Override
        public Consumer<ClientPlayerEntity> process() {
            return p -> {
                if (sel.sourceChange) {
                    ctx.preventChecked = true;
                    sel.sourceChange = false;
                }

                if (ctx.gonnaPlay) {
                    checker3.setCanceled(false);
                    ctx.gonnaPlay = false;
                }

                boolean flag1 = sel.source != null
                        && sel.source.isStopped()
                        && ctx.isPlaySong
                        && !ctx.gonnaPlay
                        && !ctx.preventChecked;

                boolean flag2 = !(Mp3.currentMode == Mp3.RelayMode.SINGLE)
                        && (Mp3.isHoldingMp3 || Mp3.isMp3InInventory);

                if (flag1 && flag2) {
                    ctx.lastChecked = getGameTime().get();
                    toNext();
                }
            };
        }
    };

    /* for detect when should play a song just after the music *Mp3_init* music finish playing */
    private final EchoConsumer<AudioSound> checker3 = new EchoConsumer<AudioSound>(() -> sel.getCurrent()) {
        @Override
        public Consumer<AudioSound> process() {
            return a -> {
                if (isNullEnv()) return;
                playAudio(a);
                drawToast();
                ctx.isPaused = false;
                ctx.isPlaySong = true;
                reset();
                setCanceled(true);
            };
        }
    };

    /* for reset prevent parameter for auto switching song */
    private final EchoConsumer<ClientPlayerEntity> checker2 = new EchoConsumer<ClientPlayerEntity>(getPlayer()) {
        @Override
        public Consumer<ClientPlayerEntity> process() {
            return p -> {
                ctx.preventChecked = false;
                p.getPersistentData().putInt(Utils.MOD_ID + "channel_pointer", sel.pointer);
            };
        }
    };

    @SubscribeEvent
    public void onSourceChange(SoundEvent.SoundSourceEvent event) {
        if (isNullEnv()) return;
        if (AudioRegistryHandler.SOUND_SOURCE_PATH.contains(event.getName())) {
            sel.source = event.getSource();
            sel.sourceChange = true;
        }
    }

    @Override
    public boolean isNullEnv() {
        return super.isNullEnv() || !Mp3.isMp3InInventory;
    }


    public void onTossMp3() {
        stopDevice();
    }

    public void onChangeDimen() {
        stopDevice();
    }

    public void refresh(Mp3Packet.Type type) {
        if (type.equals(Mp3Packet.Type.HAS_MP3)) {
            Mp3.isMp3InInventory = true;
        }

        if (type.equals(Mp3Packet.Type.NOT_HAS_MP3)) {
            stopDevice();
            Mp3.isMp3InInventory = false;
            Mp3.isHoldingMp3 = false;
        }
    }

    private enum Handler {
        TO_NEXT, TO_LAST, PAUSE, STOP, TURN_UP, TURN_DOWN;
        //TODO : add turn up and down function to mp3.
    }
}
