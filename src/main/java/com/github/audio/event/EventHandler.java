package com.github.audio.event;

import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.networking.*;
import com.github.audio.sound.AudioRegistryHandler;
import com.github.audio.util.Utils;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.OnDatapackSyncEvent;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.stream.Collectors;


@Mod.EventBusSubscriber(modid = Utils.MOD_ID)
public class EventHandler {

    private static final long CHECK_PLAYER_INVENTORY_DELAY = 80L;
    private static long lastPlayerInventoryChecked = 0L;

    /**
     * This event will be fired once player get into the server or put into command "/reload",
     * in this method developer can add some custom packet in it to make Sync.
     */
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {

        }
    }

    @SubscribeEvent
    public static void onPlayerJoinIn(EntityJoinWorldEvent event) {
        if (event.isCanceled() || event.getEntity() == null || !(event.getEntity() instanceof PlayerEntity)) return;
        if (event.getEntity().getEntityWorld().isRemote) {
            Mp3.hasInitStatue = false;
        }
    }

//    private static final Looper<List<ServerPlayerEntity>, Void> Mp3Checker = new Looper<List<ServerPlayerEntity>, Void>
//            (() -> ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers(), 30) {
//        @Override
//        public Function<List<ServerPlayerEntity>, Void> process() {
//            return serverPlayerEntities -> {
//                serverPlayerEntities.stream().filter(Objects::nonNull)
//                        .filter(p -> p.inventory.mainInventory.contains(new ItemStack(ItemRegisterHandler.Mp3.get())))
//                        .forEach(p -> NetworkingHandler.AUDIO_SOUND_CHANNEL.send(PacketDistributor.PLAYER.with(() -> p),
//                                new AudioSoundPack(ASPMethodFactory.ASPJudgementType.MISS_MP3)));
//                return (Void) null;
//            };
//        }
//    };

    @SubscribeEvent
    public static void tick(final TickEvent.WorldTickEvent event) {
        if (event.world.isRemote && event.phase != TickEvent.Phase.END) return;
        if (event.world.getGameTime() < lastPlayerInventoryChecked + CHECK_PLAYER_INVENTORY_DELAY) return;
        lastPlayerInventoryChecked = event.world.getGameTime();
        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayerEntity player : players) {
            if (player == null) return;
            List<ItemStack> collect = player.inventory.mainInventory.stream().filter(
                            itemStack -> itemStack.isItemEqual(new ItemStack(ItemRegisterHandler.Mp3.get())))
                    .collect(Collectors.toList());

            if (collect.isEmpty()) {
                NetworkingHandler.AUDIO_SOUND_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                        new AudioSoundPack(ASPMethodFactory.ASPJudgementType.MISS_MP3));
            } else {
                NetworkingHandler.AUDIO_SOUND_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                        new AudioSoundPack(ASPMethodFactory.ASPJudgementType.HAS_MP3));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerGuiClose(PlayerContainerEvent.Close event) {
        if (event.getContainer().getClass().getName()
                .equals("net.minecraft.inventory.container.PlayerContainer")) {
            PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
            for (ServerPlayerEntity player : playerList.getPlayers()) {
                NetworkingHandler.BACKPACK_SOUND_CHANNEL.send(
                        PacketDistributor.PLAYER.with(
                                () -> player),
                        new BackPackSoundPack(player.getUniqueID(),
                                false, false, event.getPlayer().getPosition()));
            }
        }
//        NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
//                PacketDistributor.PLAYER.with(
//                        () -> (ServerPlayerEntity) event.getPlayer()) ,
//                    new AudioSoundPack(ASPMethodFactory.ASPJudgementType.CLOSE_GUI));
    }

    /**
     * if the packet is from client, this method will simply send it back, change the argument "formClient" from
     * "true" to "false" to make sure the packet will be handled by the player client thread.
     *
     * @param clientPack
     */
    public static void sendClientPackBack(BackPackSoundPack clientPack) {
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
//        for (UUID uuid : PlayerManager.getUUIDSet()){
        for (ServerPlayerEntity player : playerList.getPlayers()) {
            NetworkingHandler.BACKPACK_SOUND_CHANNEL.send(
                    PacketDistributor.PLAYER.with(
                            () -> player),
                    new BackPackSoundPack(clientPack.getUUID(),
                            false, clientPack.isUnfold(), clientPack.getPos())
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        if (!event.getOriginal().getEntityWorld().isRemote) {
            NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
                    PacketDistributor.PLAYER.with(
                            () -> (ServerPlayerEntity) event.getPlayer()),
                    new AudioSoundPack(ASPMethodFactory.ASPJudgementType.REBORN));
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();
        if (serverPlayer != null) {
            NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
                    PacketDistributor.PLAYER.with(
                            () -> serverPlayer),
                    new AudioSoundPack(ASPMethodFactory.ASPJudgementType.CHANGE_DIMENSION));
        }
    }

    @SubscribeEvent
    public static void onMp3Thrown(ItemTossEvent event) {
        if (event.getEntityItem().getItem().isItemEqual(new ItemStack(ItemRegisterHandler.Mp3.get()))) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
                        PacketDistributor.PLAYER.with(
                                () -> (ServerPlayerEntity) event.getPlayer()),
                        new AudioSoundPack(ASPMethodFactory.ASPJudgementType.TOSS));
            } else if (event.getPlayer() instanceof ClientPlayerEntity) {
                //Client Thread
                ASPMethodFactory.BRANCH_MAP.get(ASPMethodFactory.ASPJudgementType.TOSS)
                        .withBranch((ClientPlayerEntity) event.getPlayer());
                Mp3.isMp3InInventory = false;
            }
        }
    }

    @Deprecated
    public static void onPlayerUnfoldBackPack(PlayerEntity playerIn, World worldIn) {
        worldIn.playSound(playerIn, playerIn.getPosition(), AudioRegistryHandler.BACKPACK_UNFOLD_SOUND.getSoundEvent(),
                SoundCategory.PLAYERS, 1f, 1f);
    }


    @Deprecated
    public static void onPlayerLoggedOut(EntityLeaveWorldEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof PlayerEntity) {
            if (!event.getEntity().getEntityWorld().isRemote) {
                NetworkingHandler.AUDIO_SOUND_CHANNEL.send(
                        PacketDistributor.PLAYER.with(
                                () -> (ServerPlayerEntity) event.getEntity()),
                        new AudioSoundPack(ASPMethodFactory.ASPJudgementType.PLAYER_LOGOUT));
            }
        }
    }
}

