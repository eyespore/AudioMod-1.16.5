package com.github.audio.master.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISwitchable {

    void toNext();

    void toLast();

    void otherChannel();

}
