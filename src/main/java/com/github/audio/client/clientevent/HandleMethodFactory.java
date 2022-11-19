package com.github.audio.client.clientevent;

import com.github.audio.api.ISoundHandlerJudgement;
import com.github.audio.item.mp3.Mp3;

import java.util.HashMap;

public class HandleMethodFactory {

    protected static final HashMap<Enum<HandleMethodType> , ISoundHandlerJudgement> DEFAULT_SOUND_HANDLER_MAP = new HashMap<>();
    protected static final HashMap<Enum<Mp3.RelayMode> , HashMap<Enum<HandleMethodType> , ISoundHandlerJudgement>> MODE_METHOD = new HashMap<>();

    static {
        DEFAULT_SOUND_HANDLER_MAP.put(HandleMethodType.SWITCH_TO_NEXT , new HandleMethod.ToNext());
        DEFAULT_SOUND_HANDLER_MAP.put(HandleMethodType.SWITCH_TO_LAST , new HandleMethod.ToLast());

        DEFAULT_SOUND_HANDLER_MAP.put(HandleMethodType.PAUSE_OR_RESUME , new HandleMethod.PauseOrResume());
        DEFAULT_SOUND_HANDLER_MAP.put(HandleMethodType.GONNA_PLAY , new HandleMethod.GonnaPlay());
        DEFAULT_SOUND_HANDLER_MAP.put(HandleMethodType.AUTO_SWITCH_NEXT, new HandleMethod.AutoSwitch());
    }

    /**
     * GONNA_PLAY should not be used for current, this parameter may cause some unknown problem.
     */
    enum HandleMethodType {
        SWITCH_TO_NEXT, SWITCH_TO_LAST, PAUSE_OR_RESUME, NULL, GONNA_PLAY, AUTO_SWITCH_NEXT;
    }
}
