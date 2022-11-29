package com.github.audio.client.clienthandler.mp3;

import com.github.audio.Utils;
import com.github.audio.item.mp3.Mp3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Utils.MOD_ID, value = Dist.CLIENT)
public final class Mp3SoundHandler {

    /* For font time ticker */
    static int timeTicker = 0;

    private Mp3SoundHandler() {
    }

    /**
     * draw the statues bar while player holding mod item such as Mp3.
     *
     * @param event
     */
    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        ClientWorld clientWorld = Minecraft.getInstance().world;
        if (clientPlayer == null || clientWorld == null) return;

        Mp3Context.Mp3SoundContext context = Mp3Context.getCtx().get();

        if (Mp3Context.Mp3Ctx.currentSourceHasChanged || !Mp3HandleMethod.hasInitRFB) {
            Mp3HandleMethod.flushCurrentRollingBar();
            Mp3Context.Mp3Ctx.currentSourceHasChanged = false;
            Mp3HandleMethod.hasInitRFB = true;
        }

        timeTicker++;
        if (timeTicker >= 50) {
            Mp3Context.Mp3Ctx.currentSongNameRollingBar = Mp3HandleMethod.rfb.nextRollingFormat();
            timeTicker = 0;
        }

        if (Mp3Context.Mp3Ctx.gonnaPlay) {
            Mp3HandleMethod.preventAutoSwitch();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(HandleMethodFactory.HandleMethodType.GONNA_PLAY).withBranch(clientPlayer, context);
        }

        if (!context.hasCheckedLastTickMode) {
            context.recordMode = Mp3.currentMode;
            context.hasCheckedLastTickMode = true;
        }

        if (context.recordMode != Mp3.currentMode) {
            context.recordMode = Mp3.currentMode;
            if (context.recordMode == Mp3.RelayMode.RANDOM) {
                Mp3HandleMethod.shouldInitRandomList = true;
            }
            context.hasCheckedLastTickMode = false;
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().world == null || Minecraft.getInstance().player == null) return;
        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        Mp3Context.Mp3SoundContext context = Mp3Context.getCtx().get();

        if (Mp3HandleMethod.toBeSolved != HandleMethodFactory.HandleMethodType.NULL && Mp3HandleMethod.toBeSolved != HandleMethodFactory.HandleMethodType.GONNA_PLAY) {
            Mp3HandleMethod.preventAutoSwitch();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(Mp3HandleMethod.toBeSolved).withBranch(clientPlayer, context);
        }

        boolean flag1 = Mp3Context.Mp3Ctx.currentSource != null && Mp3Context.Mp3Ctx.currentSource.isStopped() && Mp3Context.Mp3Ctx.isPlaySong && !Mp3Context.Mp3Ctx.gonnaPlay;
        boolean flag2 = Minecraft.getInstance().world.getGameTime() > Mp3Context.Mp3Ctx.lastAutoSwitchChecked + Mp3HandleMethod.SOUND_AUTO_SWITCH_CHECK_INTERVAL;
        boolean flag3 = Minecraft.getInstance().world.getGameTime() > Mp3Context.Mp3Ctx.lastPreventAutoSwitchChecked + Mp3HandleMethod.SOUND_AUTO_SWITCH_CHECK_INTERVAL;
        boolean flag4 = Mp3Context.getHandler() == HandleMethodFactory.HandleMethodType.NULL && !Mp3Context.Mp3Ctx.gonnaPlay && Mp3.getCurrentMode() != Mp3.RelayMode.SINGLE;

        if (flag3) {
            Mp3Context.Mp3Ctx.preventAutoSwitch = false;
            Mp3Context.Mp3Ctx.lastPreventAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
        }

        if (flag1 && flag2 && flag4 && !Mp3Context.Mp3Ctx.preventAutoSwitch) {
            Mp3Context.Mp3Ctx.lastAutoSwitchChecked = Minecraft.getInstance().world.getGameTime();
            HandleMethodFactory.DEFAULT_SOUND_HANDLER_MAP.get(HandleMethodFactory.HandleMethodType.AUTO_SWITCH_NEXT).withBranch(clientPlayer, context);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote) return;
        if (!Mp3HandleMethod.PLAYER_UUID_LIST.isEmpty() && Mp3HandleMethod.lastPlaybackChecked < event.world.getGameTime()) {
            Mp3HandleMethod.lastPlaybackChecked = event.world.getGameTime();
            Mp3HandleMethod.PLAYER_UUID_LIST.entrySet().removeIf(entry -> {
                if (!Minecraft.getInstance().getSoundHandler().isPlaying(entry.getValue())) {
                    return true;
                }
                return false;
            });
        }
    }

    @SuppressWarnings({"unused", "java:S1172"})
    // needs to be here for addListener to recognize which event this method should be subscribed to
    public static void onWorldUnload(WorldEvent.Unload evt) {
        Mp3HandleMethod.PLAYER_UUID_LIST.clear();
        Mp3HandleMethod.lastPlaybackChecked = 0;
    }

}
