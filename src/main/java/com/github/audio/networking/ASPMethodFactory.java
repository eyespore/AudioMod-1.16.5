package com.github.audio.networking;

import com.github.audio.api.Interface.IAudioSoundPackBranch;

import java.util.HashMap;

public class ASPMethodFactory {

    public enum ASPJudgementType {REBORN, TOSS, CHANGE_DIMENSION, CLOSE_GUI, MISS_MP3, HAS_MP3, PLAYER_LOGOUT;}

    public static final HashMap<Enum<ASPJudgementType>, IAudioSoundPackBranch> BRANCH_MAP = new HashMap<>();

    static {
        BRANCH_MAP.put(ASPJudgementType.REBORN, new ASPHandleMethod.PlayerReborn());
        BRANCH_MAP.put(ASPJudgementType.TOSS, new ASPHandleMethod.PlayerTossMp3());
        BRANCH_MAP.put(ASPJudgementType.CLOSE_GUI, new ASPHandleMethod.PlayerCloseGUI());
        BRANCH_MAP.put(ASPJudgementType.CHANGE_DIMENSION, new ASPHandleMethod.PlayerChangeDimension());
        BRANCH_MAP.put(ASPJudgementType.MISS_MP3 , new ASPHandleMethod.PlayerMissMp3());
        BRANCH_MAP.put(ASPJudgementType.HAS_MP3 , new ASPHandleMethod.PlayerHasMp3());
        BRANCH_MAP.put(ASPJudgementType.PLAYER_LOGOUT , new ASPHandleMethod.PlayerLogout());
    }
}
