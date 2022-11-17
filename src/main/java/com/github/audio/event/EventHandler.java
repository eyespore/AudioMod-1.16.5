package com.github.audio.event;

import com.github.audio.Utils;
import com.github.audio.client.clientevent.ClientEventHandler;
import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.networking.AudioSoundPack;
import com.github.audio.networking.NetworkingHandler;
import com.github.audio.networking.BackPackSoundEventPack;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;


@Mod.EventBusSubscriber(modid = Utils.MOD_ID)
public class EventHandler {

    /**
     * This event will be fired once player get into the server or put into command "/reload",
     * in this method developer can add some custom packet in it to make Sync.
     */
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {

        }
    }

    @SubscribeEvent
    public static void onPlayerFoldBackPack(PlayerContainerEvent.Close event) {
        if (event.getContainer().getClass().getName()
                .equals("net.minecraft.inventory.container.PlayerContainer")) {
            PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
            for (ServerPlayerEntity player : playerList.getPlayers()) {
                NetworkingHandler.BACKPACK_SOUND_CHANNEL.send(
                        PacketDistributor.PLAYER.with(
                                () -> player),
                        new BackPackSoundEventPack(player.getUniqueID(),
                                false, false, event.getPlayer().getPosition()));
            }
        }
    }

    /**
     * if the packet is from client, this method will simply send it back, change the argument "formClient" from
     * "true" to "false" to make sure the packet will be handled by the player client thread.
     *
     * @param clientPack
     */
    public static void sendClientPackBack(BackPackSoundEventPack clientPack) {
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
//        for (UUID uuid : PlayerManager.getUUIDSet()){
        for (ServerPlayerEntity player : playerList.getPlayers()) {
            NetworkingHandler.BACKPACK_SOUND_CHANNEL.send(
                    PacketDistributor.PLAYER.with(
                            () -> player),
                    new BackPackSoundEventPack(clientPack.getUuid(),
                            false, clientPack.isUnfold(), clientPack.getPos())
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event){
        if (!event.getOriginal().getEntityWorld().isRemote){
            NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
                    PacketDistributor.PLAYER.with(
                            () -> (ServerPlayerEntity) event.getPlayer()),
                    new AudioSoundPack(AudioSoundPack.Judgement.REBORN));
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();
        if (serverPlayer != null) {
            NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
                    PacketDistributor.PLAYER.with(
                            () -> serverPlayer),
                    new AudioSoundPack(AudioSoundPack.Judgement.CHANGE_DIMENSION));
        }
    }

    @SubscribeEvent
    public static void onMp3Thrown(ItemTossEvent event) {
        if (event.getEntityItem().getItem().isItemEqual(new ItemStack(ItemRegisterHandler.Audio.get()))) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
                        PacketDistributor.PLAYER.with(
                                () -> (ServerPlayerEntity) event.getPlayer()),
                        new AudioSoundPack(AudioSoundPack.Judgement.TOSS));
            } else if (event.getPlayer() instanceof ClientPlayerEntity){
                NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
                        PacketDistributor.PLAYER.with(
                                () -> ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(event.getPlayer().getUniqueID())),
                        new AudioSoundPack(AudioSoundPack.Judgement.TOSS));
            }
        }
    }

    @Deprecated
    public static void onPlayerUnfoldBackPack(PlayerEntity playerIn, World worldIn) {
        worldIn.playSound(playerIn, playerIn.getPosition(), SoundEventRegistryHandler.BACKPACK_UNFOLD_SOUND.get(),
                SoundCategory.PLAYERS, 1f, 1f);
    }
}

