package com.github.audio.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static KeyBinding settingMenu;
    public static KeyBinding relayNext;
    public static KeyBinding relayLast;
    public static KeyBinding pauseOrResume;

    public static void register(){
        settingMenu = new KeyBinding(new TranslationTextComponent("key.audio.soundsetting").getString() ,
                GLFW.GLFW_KEY_B , "key_categories.audio");
        relayNext = new KeyBinding(new TranslationTextComponent("key.audio.nextDisc").getString() ,
                GLFW.GLFW_KEY_RIGHT_BRACKET , "key_categories.audio");
        relayLast = new KeyBinding(new TranslationTextComponent("key.audio.lastDisc").getString() ,
                GLFW.GLFW_KEY_LEFT_BRACKET , "key_categories.audio");
        pauseOrResume = new KeyBinding(new TranslationTextComponent("key.audio.pauseAndResume").getString() ,
                GLFW.GLFW_KEY_BACKSLASH , "key_categories.audio");

        register(settingMenu, relayNext, relayLast, pauseOrResume);

        //        ClientRegistry.registerKeyBinding(soundSetting);
//        ClientRegistry.registerKeyBinding(startDisc);
    }

    private static void register(KeyBinding... keyBindings){
        for (KeyBinding keyBinding : keyBindings){
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }
}
