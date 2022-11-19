package com.github.audio.item;

import com.github.audio.Utils;
import com.github.audio.creativetab.ModCreativeTab;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegisterHandler {

    public static final DeferredRegister<Item> ITEM_REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, Utils.MOD_ID);

    public static final RegistryObject<Item> Audio = ITEM_REGISTER.register("audio" ,
            () -> new Mp3(new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> RAIN_ON_BRICKS_MUSIC_DISC = ITEM_REGISTER.register(
            "rain_on_bricks_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.RAIN_ON_BRICKS::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

//    public static final RegistryObject<Item> WORST_NEIGHBORS_EVER_MUSIC_DISC = ITEM_REGISTER.register(
//            "worst_neighbor_ever_music_disc" , () -> new MusicDiscItem(1 ,
//                    SoundEventRegistryHandler.WORST_NEIGHBOR_EVER::get,
//                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> BLUE_ROOM_MUSIC_DISC = ITEM_REGISTER.register(
            "blue_room_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.BLUE_ROOM::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> FULL_CONFESSION_MUSIC_DISC = ITEM_REGISTER.register(
            "full_confession_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.FULL_CONFESSION::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> SNOW_MUSIC_DISC = ITEM_REGISTER.register(
            "snow_music_disc" , () -> new SnowMusicDisc(1 ,
                    SoundEventRegistryHandler.SNOW::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> HIT_THE_FLOOR_MUSIC_DISC = ITEM_REGISTER.register(
            "hit_the_floor_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.HIT_THE_FLOOR::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> YOU_WILL_NEVER_KNOW_MUSIC_DISC = ITEM_REGISTER.register(
            "you_will_never_know_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.YOU_WILL_NEVER_KNOW::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> A_FINE_RED_MIST_MUSIC_DISC = ITEM_REGISTER.register(
            "a_fine_red_mist_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.A_FINE_RED_MIST::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> COME_AND_SEE_MUSIC_DISC = ITEM_REGISTER.register(
            "come_and_see_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.COME_AND_SEE::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> END_OF_THE_ROAD_MUSIC_DISC = ITEM_REGISTER.register(
            "end_of_the_road_music_disc" , () -> new EndOfTheRoadMusicDisc(1 ,
                    SoundEventRegistryHandler.END_OF_THE_ROAD::get ,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> PRISON_TWO_MUSIC_DISC = ITEM_REGISTER.register(
            "prison_two_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.PRISON_TWO::get ,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> SILHOUETTE_MUSIC_DISC = ITEM_REGISTER.register(
            "silhouette_music_disc" , () -> new SilhouetteMusicDisc(1 ,
                    SoundEventRegistryHandler.SILHOUETTE::get ,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> NOCTURNE_MUSIC_DISC = ITEM_REGISTER.register(
            "nocturne_music_disc" , () -> new NocturneMusicDisc(1 ,
                    SoundEventRegistryHandler.NOCTURNE::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> CHEMICAL_BREW_MUSIC_DISC = ITEM_REGISTER.register(
            "chemical_brew_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.CHEMICAL_BREW::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> CHINATOWN_MUSIC_DISC = ITEM_REGISTER.register(
            "chinatown_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.CHINATOWN::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> BREATH_OF_A_SERPENT_MUSIC_DISC = ITEM_REGISTER.register(
            "breath_of_a_serpent_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.BREATH_OF_A_SERPENT::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> DRIVING_FORCE_MUSIC_DISC = ITEM_REGISTER.register(
            "driving_force_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.DRIVING_FORCE::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> SNEAKY_DRIVER_MUSIC_DISC = ITEM_REGISTER.register(
            "sneaky_driver_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.SNEAKY_DRIVER::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> THIRD_DISTRICT_MUSIC_DISC = ITEM_REGISTER.register(
            "third_district_music_disc" , () -> new MusicDiscItem(1 ,
                    SoundEventRegistryHandler.THIRD_DISTRICT::get,
                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> MUSIC_BOX = ITEM_REGISTER.register(
            "music_box" , () -> new Item(new Item.Properties().maxStackSize(1)
                    .group(ModCreativeTab.TAB_AUDIO)));

    /** For registry */
    @Deprecated
    public static void register(IEventBus eventBus){
        ITEM_REGISTER.register(eventBus);
    }
}
