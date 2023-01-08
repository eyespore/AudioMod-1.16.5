package com.github.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.File;
import java.util.Arrays;

import static java.io.File.separator;

public enum Env {

    ON_TEST, IN_GAME, DEDICATED_SERVER, MAIN_METHOD;

    public static Env getEnv() {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return DEDICATED_SERVER;
        else if (Arrays.stream(new File(".").getAbsolutePath().split(separator + separator))
                .noneMatch(s -> s.equals("run"))) return IN_GAME;
        else return ON_TEST;
    }

    public static boolean isServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    public static boolean isInGame() {
        return !isOnTest();
    }

    public static boolean isOnTest() {
        return Arrays.asList(new File(".")
                .getAbsolutePath().split(separator + separator)).contains("run");
    }
}
