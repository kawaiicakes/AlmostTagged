package com.kawaiicakes.almosttagged.mixins;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.tags.TagStreamGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = TagLoader.class)
public class TagLoaderMixin {

    @Final
    @Shadow
    private String directory;

    //Injecting here gives a chance for tags to load before generating maps such that they are not empty
    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN"))
    private <T> void build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> p_203899_, CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        switch (this.directory) {
            case "tags/items" -> {
                final Map<ResourceLocation, Collection<T>> returnMap = map.getReturnValue();
                final Map<Item, Set<TagKey<Item>>> itemMap = TagStreamGenerators.getItemMap();

                itemMap.forEach((item, tagSet) -> tagSet.forEach(tag -> {
                    AlmostTagged.LOGGER.info(((Collection<T>) item).toString());
                    returnMap.put(tag.location(), (Collection<T>) item);
                }));
            }
            case "tags/blocks" -> {
                final Map<Item, Set<TagKey<Block>>> blockMap = TagStreamGenerators.getBlockMap();
            }
        }
    }
}
