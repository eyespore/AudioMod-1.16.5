package com.github.audio.api.Interface;

import net.minecraftforge.event.TickEvent;

public interface IAutoSave extends Cloneable{

    long INTERVAL = 60;

    void save(TickEvent.ClientTickEvent event);

}
