package com.github.audio.client.gui;

import com.github.audio.Utils;
import com.github.audio.client.clienthandler.mp3.Mp3Context;
import com.github.audio.client.config.Config;
import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.sound.SoundChannel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import java.util.Objects;

public class AudioToastMessage implements IToast {

    private long firstDrawTime;
    private boolean hasNewOutput = true;
    private String title;
    private String description;
    private ItemStack itemStack;
    private boolean hasPlayedSound = false;
    private static int stringNum = 0;
    public static boolean shouldHide = false;

    private final static int TOAST_Y = 0;
    private final ResourceLocation TEXTURE_TOASTS = new ResourceLocation(Utils.MOD_ID, "textures/gui/toasts.png");

    private int getStringPosY() {
        return stringNum++ == 0 ? TOAST_Y + 7 : TOAST_Y + 18;
    }

    public void show(String title, String description) {
        this.title = title;
        this.description = description;
        this.itemStack = new ItemStack(ItemRegisterHandler.MUSIC_BOX.get());
        Minecraft.getInstance().getToastGui().add(this);
    }

    //draw method
    @SuppressWarnings("NullableProblems")
    @Override
    public Visibility func_230444_a_(MatrixStack matrixStack, ToastGui toastGui, long clientWorldTime) {
        stringNum = 0;
        if (hasNewOutput) {
            this.firstDrawTime = clientWorldTime;
            hasNewOutput = false;
        }
        toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        GlStateManager.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
        toastGui.blit(new MatrixStack(), 0, TOAST_Y, 0, 0, 160, 32);
        toastGui.getMinecraft().fontRenderer.drawString(matrixStack, title, 30, getStringPosY(), 100000);
        toastGui.getMinecraft().fontRenderer.drawString(matrixStack, description, 30, getStringPosY(), 100000);
        if (!this.hasPlayedSound && clientWorldTime > 0L) {
            this.hasPlayedSound = true;
            SoundEvent soundEvent = SoundChannel.MUSIC_BOX_CHANNEL
                    .getChannelSoundList().get(Config.MUSIC_BOX_CLEW_TONE.get()).getSoundEvent();
            if (soundEvent != null) {
                toastGui.getMinecraft().getSoundHandler().play(
                        new EntityTickableSound(soundEvent, SoundCategory.RECORDS, 2, 1,
                                Objects.requireNonNull(Minecraft.getInstance().player)));
            }
        }

        RenderHelper.enableStandardItemLighting();
        toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(null, itemStack, 8, TOAST_Y + 8);
        //TODO : make function to control the hide and show mode of toast Message.

        boolean flag1 = clientWorldTime - this.firstDrawTime >= 2000L;
        boolean flag2 = !Mp3Context.isPlaySong;

        return (flag1 || flag2) ? Visibility.HIDE : Visibility.SHOW;
    }
}
