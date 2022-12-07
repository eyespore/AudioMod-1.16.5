package com.github.audio.master.exec;

import com.github.audio.Audio;
import com.github.audio.api.annotation.Exec;
import com.github.audio.master.ServerExecutor;
import com.github.audio.util.Utils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Exec(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(modid = Utils.MOD_ID)
public class SimpleExecutor extends ServerExecutor {

    private static final SimpleExecutor simpleExecutor = new SimpleExecutor();

    private SimpleExecutor() {}

    public static SimpleExecutor getExecutor() {
        return simpleExecutor;
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Audio.info("Hello! this is server side");
    }

}
