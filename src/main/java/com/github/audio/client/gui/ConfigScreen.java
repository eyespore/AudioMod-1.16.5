package com.github.audio.client.gui;

import com.github.audio.client.config.Config;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.sound.SoundChannel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

public final class ConfigScreen extends Screen {

    private static int buttonNum = 0;
    //TODO : add new parameter for this.
    private static final AudioConfigPara<Integer> TOAST_MSG_TONE_PARA = new AudioConfigPara<>(Config.MUSIC_BOX_CLEW_TONE.get());
    private static final AudioConfigPara<Integer> BACKPACK_SOUND_PARA = new AudioConfigPara<>(Config.BACK_PACK_SOUND_STATUE.get());
    private static final ArrayList<AudioConfigPara<Integer>> INTEGER_PARA = new ArrayList<>();

    static {
        INTEGER_PARA.add(TOAST_MSG_TONE_PARA);
        INTEGER_PARA.add(BACKPACK_SOUND_PARA);
    }

    public static class AudioConfigPara<T extends Number> {

        private T para;

        public Supplier<T> getPara() {
            return () -> para;
        }

        public void setPara(T t) {
            this.para = t;
        }

        public AudioConfigPara(T para) {
            this.para = para;
        }

        public T ensureIn(T min, T max) {
            boolean paraIn = this.para.doubleValue() <= max.doubleValue() && this.para.doubleValue() >= min.doubleValue();
            if (!paraIn) {
                this.setPara(min);
            }
            return this.para;
        }
    }

    private static final String[] BACKPACK_SOUND_STATUES = {"Multiple", "Single", "Null"};

    private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;
    private static final int TITLE_HEIGHT = 8;

    private static final int DEF_WIDTH = 150;
    private static final int DEF_HEIGHT = 20;

    private Button backpackSoundStatueButton;
    private Button musicBoxClewToneButton;

    public ConfigScreen() {
        super(new TranslationTextComponent("gui.audio.configScreen"));
    }

    private static class MultipleSwitchButtonContext {
        public int configScreenVal;
        public String translationKey;
        public String[] toggleNameList;
        public ForgeConfigSpec.IntValue configVal;

        public MultipleSwitchButtonContext(
                String translationKey, String[] toggleNameList,
                ForgeConfigSpec.IntValue configVal, int configScreenVal) {
            this.configScreenVal = configScreenVal;
            this.translationKey = translationKey;
            this.toggleNameList = toggleNameList;
            this.configVal = configVal;
        }
    }

    @Override
    protected void init() {

        ConfigScreen.buttonNum = 0;

        this.backpackSoundStatueButton = new Button(this.width / 2 - 75, getY(), DEF_WIDTH, DEF_HEIGHT,
                new TranslationTextComponent("gui.audio.soundStatue",
                        BACKPACK_SOUND_STATUES[Config.BACK_PACK_SOUND_STATUE.get()]),
                (button) -> {
                    if (BACKPACK_SOUND_PARA.para == BACKPACK_SOUND_STATUES.length - 1) {
                        BACKPACK_SOUND_PARA.para = 0;
                    } else {
                        BACKPACK_SOUND_PARA.para++;
                    }
                    Config.BACK_PACK_SOUND_STATUE.set(BACKPACK_SOUND_PARA.para);
                    Config.BACK_PACK_SOUND_STATUE.save();
                    this.backpackSoundStatueButton.setMessage(new TranslationTextComponent(
                            "gui.audio.soundStatue",
                            BACKPACK_SOUND_STATUES[Config.BACK_PACK_SOUND_STATUE.get()]
                    ));
                }
        );
        this.addButton(backpackSoundStatueButton);

        //MusicBoxClewTone Button
        this.musicBoxClewToneButton = new Button(this.width / 2 - 75, getY(), DEF_WIDTH, DEF_HEIGHT,
                new TranslationTextComponent("gui.audio.configScreen.musicBoxClewToneButton",
                        SoundChannel.MUSIC_BOX_CHANNEL.getList().get(TOAST_MSG_TONE_PARA.para).getSignedName()),
                button -> {
                    if (TOAST_MSG_TONE_PARA.para ==
                            SoundChannel.MUSIC_BOX_CHANNEL.getSize() - 1) {
                        TOAST_MSG_TONE_PARA.para = 0;
                    } else {
                        TOAST_MSG_TONE_PARA.para++;
                    }

                    SoundEvent soundEvent = SoundChannel.MUSIC_BOX_CHANNEL
                            .getList().get(TOAST_MSG_TONE_PARA.ensureIn(0, SoundChannel.MUSIC_BOX_CHANNEL.getSize() - 1)).getSoundEvent();

                    Config.MUSIC_BOX_CLEW_TONE.set(TOAST_MSG_TONE_PARA.para);
                    Config.MUSIC_BOX_CLEW_TONE.save();

                    this.musicBoxClewToneButton.setMessage(new TranslationTextComponent(
                            "gui.audio.configScreen.musicBoxClewToneButton",
                            SoundChannel.MUSIC_BOX_CHANNEL.getList().get(TOAST_MSG_TONE_PARA.para).getSignedName()));
                    if (soundEvent != null) {
                        Objects.requireNonNull(Minecraft.getInstance().player).playSound(soundEvent, 2, 1);
                    }
                });
        this.addButton(musicBoxClewToneButton);

        //TipExistingTime Row Button
        OptionsRowList tipExistingTime = new OptionsRowList(
                Objects.requireNonNull(this.minecraft), this.width, this.height,
                OPTIONS_LIST_TOP_HEIGHT, this.height - OPTIONS_LIST_BOTTOM_OFFSET,
                OPTIONS_LIST_ITEM_HEIGHT
        );

        //Done Button
        Button done = new Button(this.width / 2 - 75, this.height - 25, DEF_WIDTH, DEF_HEIGHT,
                new TranslationTextComponent("gui.audio.configScreen.done"),
                button -> ConfigScreen.this.closeScreen());
        this.addButton(done);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
//        this.optionsRowList.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title.getString(), this.width / 2, TITLE_HEIGHT, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    //TODO : add advanced menu for audio setting.

    private int getY() {
        int toReturn = this.height - (240 - ConfigScreen.buttonNum * 25);
        ConfigScreen.buttonNum++;
        return toReturn;
    }

    /**
     * Notice problems with initialization with this method.
     */
    public static int getSoundStatesLength() {
        return BACKPACK_SOUND_STATUES.length;
    }

    private Button getMultipleSwitchButton(MultipleSwitchButtonContext context) {
        return new Button(this.width / 2 - 75, this.getY(), 150, 20,
                new TranslationTextComponent(
                        context.translationKey, context.toggleNameList[context.configVal.get()]), (button) -> {
            if (context.configScreenVal == context.toggleNameList.length - 1) {
                context.configScreenVal = 0;
            } else {
                context.configScreenVal++;
            }
            context.configVal.set(context.configScreenVal);
            context.configVal.save();
            button.setMessage(new TranslationTextComponent(
                    context.translationKey, context.toggleNameList[context.configVal.get()]));
        });
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == 256 && this.shouldCloseOnEsc())
                || keyCode == KeyBinds.settingMenu.getKey().getKeyCode()
                || keyCode == 69) {
            this.closeScreen();
            return true;
        } else if (keyCode == 258) {
            boolean flag = !hasShiftDown();
            if (!this.changeFocus(flag)) {
                this.changeFocus(flag);
            }

            return false;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}

