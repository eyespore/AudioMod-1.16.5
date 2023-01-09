package com.github.audio.commands;

import com.github.audio.util.gen.ClientFileOperator;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.UUID;

public class ReloadResourceCommand {

    public ReloadResourceCommand(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(Commands.literal("reloadresource").executes(
                (command) -> {
                    try {
                        return ReloadResource(command.getSource());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    @SuppressWarnings("deprecation")
    private int ReloadResource(CommandSource source) throws IOException {

//        Utils.getIOHelper().moveOgg(false);
        ClientFileOperator.getClientFileOperator().flushOgg();
        Minecraft.getInstance().reloadResources();
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendMessage(
                    new TranslationTextComponent("command.audio.reloadResource") , UUID.randomUUID());
        }
        return 1;
    }

}