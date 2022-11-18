package com.github.audio.client.clientevent;

/**
 * GONNA_PLAY should not be used for current, this parameter may cause some unknown problem.
 */
enum HandleMethodType {
    SWITCH_TO_NEXT, SWITCH_TO_LAST, PAUSE_OR_RESUME, NULL, GONNA_PLAY, AUTO_SWITCH_NEXT;
}
