package com.github.audio.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static KeyBinding soundSetting;
    public static KeyBinding nextDisc;
    public static KeyBinding lastDisc;
    public static KeyBinding pauseAndResume;

    public static void register(){
        soundSetting = new KeyBinding(new TranslationTextComponent("key.audio.soundsetting").getString() ,
                GLFW.GLFW_KEY_B , "key_categories.audio");
        nextDisc = new KeyBinding(new TranslationTextComponent("key.audio.nextDisc").getString() ,
                GLFW.GLFW_KEY_RIGHT_BRACKET , "key_categories.audio");
        lastDisc = new KeyBinding(new TranslationTextComponent("key.audio.lastDisc").getString() ,
                GLFW.GLFW_KEY_LEFT_BRACKET , "key_categories.audio");
        pauseAndResume = new KeyBinding(new TranslationTextComponent("key.audio.pauseAndResume").getString() ,
                GLFW.GLFW_KEY_BACKSLASH , "key_categories.audio");

        register(soundSetting, nextDisc, lastDisc , pauseAndResume);

        //        ClientRegistry.registerKeyBinding(soundSetting);
//        ClientRegistry.registerKeyBinding(startDisc);
    }

    private static void register(KeyBinding... keyBindings){
        for (KeyBinding keyBinding : keyBindings){
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }
}
