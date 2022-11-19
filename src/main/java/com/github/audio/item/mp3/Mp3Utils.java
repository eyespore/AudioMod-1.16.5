package com.github.audio.item.mp3;

import com.github.audio.Audio;

import java.util.ArrayList;
import java.util.UUID;

public class Mp3Utils {

    protected static final ArrayList<UUID> MP3_UUID_LIST = new ArrayList<>();

    public static void printUUID () {
        MP3_UUID_LIST.forEach((UUID) -> Audio.getLOGGER().info("uuid : " + UUID));
    }

}
