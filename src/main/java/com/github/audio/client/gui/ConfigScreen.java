package com.github.audio.client.gui;

import com.github.audio.client.config.Config;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.sound.SoundEventRegistryHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Objects;

public final class ConfigScreen extends Screen {

    private static int buttonNum = 0;
    private static int musicBoxClewToneParameter = Config.MUSIC_BOX_CLEW_TONE.get();
    private static int backpackSoundStatue = Config.BACK_PACK_SOUND_STATUE.get();
    private static boolean disableBackpackSoundParameter = Config.DISABLE_BACKPACKSOUND.get();

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
                    if (backpackSoundStatue == BACKPACK_SOUND_STATUES.length - 1) {
                        backpackSoundStatue = 0;
                    } else {
                        backpackSoundStatue++;
                    }
                    Config.BACK_PACK_SOUND_STATUE.set(backpackSoundStatue);
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
                        SoundEventRegistryHandler.SoundChannel.MUSIC_BOX_CHANNEL.
                                getChannelSoundList().get(musicBoxClewToneParameter).getDisplayName()),
                button -> {
                    if (musicBoxClewToneParameter ==
                            SoundEventRegistryHandler.SoundChannel.MUSIC_BOX_CHANNEL.getChannelSoundList().size() - 1) {
                        musicBoxClewToneParameter = 0;
                    } else {
                        musicBoxClewToneParameter++;
                    }
                    Config.MUSIC_BOX_CLEW_TONE.set(musicBoxClewToneParameter);
                    Config.MUSIC_BOX_CLEW_TONE.save();
                    this.musicBoxClewToneButton.setMessage(new TranslationTextComponent(
                            "gui.audio.configScreen.musicBoxClewToneButton",
                            SoundEventRegistryHandler.SoundChannel.MUSIC_BOX_CHANNEL.
                                    getChannelSoundList().get(musicBoxClewToneParameter).getDisplayName()));

                    SoundEvent soundEvent = SoundEventRegistryHandler.SoundChannel.MUSIC_BOX_CHANNEL
                            .getChannelSoundList().get(musicBoxClewToneParameter).getSoundEvent();
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

        @Deprecated
        BooleanOption setConfig = new BooleanOption(
                "gui.audio.configScreen.setConfig", unused -> ConfigScreen.disableBackpackSoundParameter,
                (unused, newValue) -> {
                    ConfigScreen.setDisableBackpackSoundParameter(newValue);
//                    audio.getLogger().info("Config now : " + ConfigScreen.disableBackpackSoundParameter);
                    Config.DISABLE_BACKPACKSOUND.set(ConfigScreen.disableBackpackSoundParameter);
                    Config.AUDIO_CONFIG.save();
                });
//        this.optionsRowList.addOption(setConfig);
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
                || keyCode == KeyBinds.soundSetting.getKey().getKeyCode()
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

    @Deprecated
    public static void setDisableBackpackSoundParameter(boolean disableBackpackSoundParameter) {
        ConfigScreen.disableBackpackSoundParameter = disableBackpackSoundParameter;
    }
}

