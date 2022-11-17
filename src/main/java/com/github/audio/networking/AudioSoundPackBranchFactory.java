package com.github.audio.networking;

import com.github.audio.api.IAudioSoundPackBranch;

import java.util.HashMap;

public class AudioSoundPackBranchFactory {

    public enum JudgementType {REBORN, TOSS, CHANGE_DIMENSION, CLOSE_GUI;}

    public static final HashMap<Enum<JudgementType>, IAudioSoundPackBranch> JUDGEMENT_MAP = new HashMap<>();

    static {
        JUDGEMENT_MAP.put(JudgementType.REBORN, new AudioSoundPackBranch.PlayerReborn());
        JUDGEMENT_MAP.put(JudgementType.TOSS, new AudioSoundPackBranch.PlayerTossItem());
        JUDGEMENT_MAP.put(JudgementType.CLOSE_GUI, new AudioSoundPackBranch.PlayerCloseGUI());
        JUDGEMENT_MAP.put(JudgementType.CHANGE_DIMENSION, new AudioSoundPackBranch.PlayerChangeDimension());
    }
}
