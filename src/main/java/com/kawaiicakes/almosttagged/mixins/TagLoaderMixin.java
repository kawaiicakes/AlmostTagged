package com.kawaiicakes.almosttagged.mixins;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.api.TagLoaderAPI;
import com.kawaiicakes.almosttagged.tags.TagData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(value = TagLoader.class, priority = 25_000)
public class TagLoaderMixin {

    @Final
    @Shadow
    private String directory;

    //For future reference:
    //By the time #build is called, Almost Unified has already unified items.
    //Datapack tags also seem to have loaded in. These behaviours are desired.
    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN"))
    private <T> void build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> p_203899_, CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        if (directory.equals("tags/items")) {
            TagLoaderAPI.setItemTagData(new TagData<>());
            TagLoaderAPI.getItemTagData().forEach((k, v) -> AlmostTagged.LOGGER.info(k.toString() + ": " + v.stream().map(t -> ((Holder.Reference<T>) t).get()).collect(Collectors.toSet())));
            //TagLoaderAPI.modifyReturn(map, directory);
        } else if (directory.equals("tags/blocks")) {
            TagLoaderAPI.setBlockTagData(new TagData<>());
            //TagLoaderAPI.modifyReturn(map, directory);
        }
    }
}
