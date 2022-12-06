package com.github.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.File;
import java.util.Arrays;

import static java.io.File.separator;

public enum Env {

    RUN_CLIENT, GAME , DEDICATED_SERVER;

    public static Env getEnv() {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return DEDICATED_SERVER;
        else if (Arrays.stream(new File(".").getAbsolutePath().split(separator + separator))
                .noneMatch(s -> s.equals("run"))) return GAME;
        else return RUN_CLIENT;
    }



}
