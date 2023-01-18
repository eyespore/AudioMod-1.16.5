package com.github.audio.commands;

import com.github.audio.master.client.NetHandler;
import com.github.audio.master.net.SignalPacket;
import com.github.audio.registryHandler.NetworkHandler;
import com.github.audio.util.gen.ClientFileOperator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayNewSoundEvent {

    public PlayNewSoundEvent(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(Commands.literal("playnewsound").executes(
                (command) -> {
                    try {
                        return newSoundEvent(command.getSource());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    @SuppressWarnings("deprecation")
    private int newSoundEvent(CommandSource source) throws IOException, CommandSyntaxException {

        NetworkHandler.SIGNAL_CHANNEL.send(PacketDistributor.PLAYER.with(() -> {
            try {
                return source.asPlayer();
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }), new SignalPacket(SignalPacket.Signal.PLAY_NEW_SOUND_EVENT));
        return 1;
    }

}
