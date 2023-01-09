package com.github.audio.commands;

import com.github.audio.Audio;
import com.github.audio.master.net.SignalPacket;
import com.github.audio.registryHandler.NetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.network.PacketDistributor;

import java.io.IOException;

public class SendMusicCommand {
    public SendMusicCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sendmusic").executes(
                (command) -> {
                    try {
                        return SendMusic(command.getSource());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    private int SendMusic(CommandSource source) throws IOException {
        NetworkHandler.SIGNAL_CHANNEL.send(PacketDistributor.PLAYER.with(() -> {
            try {
                return source.asPlayer();
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }), new SignalPacket(SignalPacket.Signal.CALL_CLIENT_TO_SEND_MUSIC));
        Audio.getLOGGER().info("Signal sent");
        return 1;
    }
}
