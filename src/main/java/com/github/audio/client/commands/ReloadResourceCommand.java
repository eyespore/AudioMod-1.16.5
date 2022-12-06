package com.github.audio.client.commands;

import com.github.audio.util.JarHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
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
    private int ReloadResource(CommandSource source) throws CommandSyntaxException, IOException {

        Collection<ResourcePackInfo> enabledPacks = Minecraft.getInstance().getResourcePackList().getEnabledPacks();

//        for (ResourcePackInfo info : enabledPacks) {
//            enabledPacks.removeIf((i) -> i.getResourcePack().getResourceNamespaces(ResourcePackType.CLIENT_RESOURCES)
//                    .toString().equals("[audio]"));
//        }
        JarHelper.JAR_HELPER.folderInsert("./resourcepacks/audio_def_songs_resources_1.0.1.zip", new File("./music"), "assets/audio/sounds/",false);
        Minecraft.getInstance().reloadResources();
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendMessage(
                    new TranslationTextComponent("command.audio.reloadResource") , UUID.randomUUID());
        }

        return 1;
    }

}