package com.github.audio.master.exec;

import com.github.audio.Audio;
import com.github.audio.api.EchoConsumer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Consumer;

public class DemoExecutor {

    private static final DemoExecutor demoExecutor = new DemoExecutor();

    public static DemoExecutor getExecutor() {
        return demoExecutor;
    }

    private static final EchoConsumer<String> checker = new EchoConsumer<String>(() -> "abcdefgh" , 30) {
        @Override
        public Consumer<String> process() {
            return Audio::info;
        }
    };

    @SubscribeEvent
    public void demo(TickEvent.ServerTickEvent event) {
        checker.loop(event);
    }

}
