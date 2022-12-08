package com.github.audio.master.client.exec;

import com.github.audio.api.annotation.Exec;
import com.github.audio.client.commands.ReloadResourceCommand;
import com.github.audio.client.gui.ConfigScreen;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.master.client.ClientExecutor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.command.ConfigCommand;

@Exec(Dist.CLIENT)
public class SimpleExecutor extends ClientExecutor {

    private static final SimpleExecutor SIMPLE_EXECUTOR = new SimpleExecutor();

    public static SimpleExecutor getExecutor() {
        return SIMPLE_EXECUTOR;
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        new ReloadResourceCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    //    @SubscribeEvent
    public void onMp3Deleted(GuiScreenEvent.MouseClickedEvent event) {
        if (!event.isCanceled() && event.getGui().getClass().getName()
                .equals("net.minecraft.client.gui.screen.inventory.CreativeScreen")) {
            System.out.println(event.getResult());
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) return;
        Minecraft client = Minecraft.getInstance();
        if (KeyBinds.settingMenu.isPressed()) client.displayGuiScreen(new ConfigScreen());
    }
}
