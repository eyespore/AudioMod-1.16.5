package com.github.audio.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static ForgeConfigSpec AUDIO_CONFIG;
    public static ForgeConfigSpec.BooleanValue DISABLE_BACKPACK_SOUND;
    public static ForgeConfigSpec.IntValue BACK_PACK_SOUND_STATUE;
    public static ForgeConfigSpec.IntValue MUSIC_BOX_CLEW_TONE;
    public static ForgeConfigSpec.LongValue MUSIC_BOX_TIP_EXIST;

    static {
        //create a builder.
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        //upload options into builder with "push", and refresh the data with "pop".
        CONFIG_BUILDER.comment("General Setting").push("general");

        DISABLE_BACKPACK_SOUND = CONFIG_BUILDER.comment("Disable backpack sound")
                .define("disable backpack sound", false);

        BACK_PACK_SOUND_STATUE = CONFIG_BUILDER.comment("Parameter of backpack sound setting , " +
                "if you wanna change this one you should change the setting in the game rather then here. ").defineInRange(
                "back pack sound parameter", 0, 0, 2);

        MUSIC_BOX_CLEW_TONE = CONFIG_BUILDER.comment("The parameter for choosing the music box" +
                " clew tone while player have a tip in the game.").defineInRange(
                "toast message clew tone type parameter", 0, 0,
                Integer.MAX_VALUE);

        MUSIC_BOX_TIP_EXIST = CONFIG_BUILDER.comment("Parameter that define the time toast message" +
                " stay in the player's screen.").defineInRange(
                "toast message clew tone time parameter" , 2000L , 500L , 8000L
        );

        CONFIG_BUILDER.pop();
        AUDIO_CONFIG = CONFIG_BUILDER.build();
    }
}
