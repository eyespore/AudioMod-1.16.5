package com.github.audio.client.clientevent;

import com.github.audio.api.ISoundHandlerJudgement;

import java.util.HashMap;

public class HandleMethodFactory {

    protected static final HashMap<Enum<HandleMethodType> , ISoundHandlerJudgement> SOUND_HANDLER_JUDGEMENT_MAP = new HashMap<>();

    static {
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.SWITCH_TO_NEXT , new HandleMethod.SwitchToNextMethod());
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.SWITCH_TO_LAST , new HandleMethod.SwitchToLastMethod());
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.PAUSE_OR_RESUME , new HandleMethod.PauseOrResumeMethod());
        SOUND_HANDLER_JUDGEMENT_MAP.put(HandleMethodType.GONNA_PLAY , new HandleMethod.GonnaPlayMethod());
    }

}
