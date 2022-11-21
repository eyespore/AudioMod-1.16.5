package com.github.audio.item;

import com.github.audio.Utils;
import com.github.audio.creativetab.ModCreativeTab;
import com.github.audio.item.mp3.Mp3;
import com.github.audio.sound.SoundEventRegistryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ItemRegisterHandler {

    public static final ArrayList<RegistryObject<Item>> DEFERRED_REGISTER_ARRAY_LIST = new ArrayList<>();

    public static final DeferredRegister<Item> ITEM_REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, Utils.MOD_ID);

//    public static final RegistryObject<Item> WORST_NEIGHBORS_EVER_MUSIC_DISC = ITEM_REGISTER.register(
//            "worst_neighbor_ever_music_disc" , () -> new MusicDiscItem(1 ,SoundEventRegistryHandler.WORST_NEIGHBOR_EVER,
//                    new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));

    public static final RegistryObject<Item> SNOW_MUSIC_DISC = asDisc("snow_music_disc", ItemMusicDisc.SnowMusicDisc::new).get();
    public static final RegistryObject<Item> NOCTURNE_MUSIC_DISC = asDisc("nocturne_music_disc", ItemMusicDisc.NocturneMusicDisc::new).get();
    public static final RegistryObject<Item> SILHOUETTE_MUSIC_DISC = asDisc("silhouette_music_disc", ItemMusicDisc.SilhouetteMusicDisc::new).get();
    public static final RegistryObject<Item> END_OF_THE_ROAD_MUSIC_DISC = asDisc("end_of_the_road_music_disc" , ItemMusicDisc.EndOfTheRoadMusicDisc::new).get();

    public static final RegistryObject<Item> CHINATOWN_MUSIC_DISC = asDisc("chinatown_music_disc", SoundEventRegistryHandler.CHINATOWN).get();
    public static final RegistryObject<Item> BLUE_ROOM_MUSIC_DISC = asDisc("blue_room_music_disc", SoundEventRegistryHandler.BLUE_ROOM).get();
    public static final RegistryObject<Item> PRISON_TWO_MUSIC_DISC = asDisc("prison_two_music_disc", SoundEventRegistryHandler.PRISON_TWO).get();
    public static final RegistryObject<Item> COME_AND_SEE_MUSIC_DISC = asDisc("come_and_see_music_disc", SoundEventRegistryHandler.COME_AND_SEE).get();
    public static final RegistryObject<Item> CHEMICAL_BREW_MUSIC_DISC = asDisc("chemical_brew_music_disc",SoundEventRegistryHandler.CHEMICAL_BREW).get();
    public static final RegistryObject<Item> SNEAKY_DRIVER_MUSIC_DISC = asDisc("sneaky_driver_music_disc", SoundEventRegistryHandler.SNEAKY_DRIVER).get();
    public static final RegistryObject<Item> DRIVING_FORCE_MUSIC_DISC = asDisc("driving_force_music_disc", SoundEventRegistryHandler.DRIVING_FORCE).get();
    public static final RegistryObject<Item> HIT_THE_FLOOR_MUSIC_DISC = asDisc("hit_the_floor_music_disc", SoundEventRegistryHandler.HIT_THE_FLOOR).get();
    public static final RegistryObject<Item> THIRD_DISTRICT_MUSIC_DISC = asDisc("third_district_music_disc", SoundEventRegistryHandler.THIRD_DISTRICT).get();
    public static final RegistryObject<Item> RAIN_ON_BRICKS_MUSIC_DISC = asDisc("rain_on_bricks_music_disc", SoundEventRegistryHandler.RAIN_ON_BRICKS).get();
    public static final RegistryObject<Item> FULL_CONFESSION_MUSIC_DISC = asDisc("full_confession_music_disc", SoundEventRegistryHandler.FULL_CONFESSION).get();
    public static final RegistryObject<Item> A_FINE_RED_MIST_MUSIC_DISC = asDisc("a_fine_red_mist_music_disc", SoundEventRegistryHandler.A_FINE_RED_MIST).get();
    public static final RegistryObject<Item> YOU_WILL_NEVER_KNOW_MUSIC_DISC = asDisc("you_will_never_know_music_disc", SoundEventRegistryHandler.YOU_WILL_NEVER_KNOW).get();
    public static final RegistryObject<Item> BREATH_OF_A_SERPENT_MUSIC_DISC = asDisc("breath_of_a_serpent_music_disc", SoundEventRegistryHandler.BREATH_OF_A_SERPENT).get();

    public static final RegistryObject<Item> Mp3 = asItem("audio", Mp3::new).get();
    public static final RegistryObject<Item> MUSIC_BOX = asItem("music_box" , 1).get();

    public static Supplier<RegistryObject<Item>> asDisc(String registryName , RegistryObject<SoundEvent> soundEvent) {
        return () -> ITEM_REGISTER.register(registryName , () -> new MusicDiscItem(1, soundEvent,
                new Item.Properties().maxStackSize(1).group(ModCreativeTab.TAB_AUDIO)));
    }

    public static Supplier<RegistryObject<Item>> asDisc(String registryName , Supplier<? extends MusicDiscItem> registryItem) {
        return () -> ITEM_REGISTER.register(registryName , registryItem);
    }

    public static Supplier<RegistryObject<Item>> asItem(String registryName , int maxStackSizeIn) {
        return () -> ITEM_REGISTER.register(registryName , () -> new Item(new Item.Properties().maxStackSize(maxStackSizeIn)
                .group(ModCreativeTab.TAB_AUDIO)));
    }

    public static Supplier<RegistryObject<Item>> asItem(String registryName , Supplier<? extends Item> registryItem) {
        return () -> ITEM_REGISTER.register(registryName , registryItem);
    }

    /**
     * For registry
     */
    public static void register(IEventBus eventBus) {
        ITEM_REGISTER.register(eventBus);
    }
}
