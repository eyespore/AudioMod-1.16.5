package com.github.audio.client.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISwitchable {

    void toNext();

    void toLast();

    void toStop();

    void otherChannel();

}
