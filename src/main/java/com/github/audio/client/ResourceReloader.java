package com.github.audio.client;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.PackLoadingManager;
import net.minecraft.client.gui.widget.list.ResourcePackList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
//TODO
public class ResourceReloader {
    private final File packDir;
//    private final PackLoadingManager audioPackLReloader;
    private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();
    private static final ResourceLocation DEFAULT_ICON = new ResourceLocation("textures/misc/unknown_pack.png");


    private net.minecraft.client.gui.widget.list.ResourcePackList availablePackList;
    private net.minecraft.client.gui.widget.list.ResourcePackList selectedPackList;

    public ResourceReloader(ResourcePackList resourcePackList, Consumer<ResourcePackList> consumer, File file) {
//        this.audioPackLReloader = new PackLoadingManager(this::populateLists, this::getPackIcon, resourcePackList, consumer);
        this.packDir = file;
    }

    public void closeAndReload() {
        //Commit the resourcePack.
//        this.audioPackLReloader.func_241618_c_();
    }

//    private void updateList(net.minecraft.client.gui.widget.list.ResourcePackList resourcePackList, Stream<PackLoadingManager.IPack> packStream) {
//        resourcePackList.getEventListeners().clear();
//        packStream.filter(PackLoadingManager.IPack::notHidden).forEach((pack) -> {
//            resourcePackList.getEventListeners().add(new net.minecraft.client.gui.widget.list.ResourcePackList.ResourcePackEntry(
//                    Minecraft.getInstance(), resourcePackList, this, pack));
//        });
//    }

}
