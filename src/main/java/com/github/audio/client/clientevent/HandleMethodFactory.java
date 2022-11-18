package com.github.audio.client.clientevent;

import com.github.audio.api.ISoundHandlerJudgement;

import java.util.HashMap;

public class HandleMethodFactory {

    protected static final HashMap<Enum<HandleMethodType> , ISoundHandlerJudgement> SOUND_HANDLER_JUDGEMENT_MAP = new HashMap<>();

    static {
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.SWITCH_TO_NEXT , new HandleMethod.SwitchToNext());
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.SWITCH_TO_LAST , new HandleMethod.SwitchToLast());
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.PAUSE_OR_RESUME , new HandleMethod.PauseOrResume());
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.GONNA_PLAY , new HandleMethod.GonnaPlay());
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.AUTO_SWITCH_NEXT, new HandleMethod.AutoSwitch());
    }

}
