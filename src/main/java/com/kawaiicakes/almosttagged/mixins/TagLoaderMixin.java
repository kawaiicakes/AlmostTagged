package com.kawaiicakes.almosttagged.mixins;

import com.kawaiicakes.almosttagged.AlmostTagged;
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

    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN"))
    private <T> void build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> p_203899_, CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        switch (directory) {
            case "tags/item": //iterate over the maps and pass them in to map appropriately
                final Map<Item, Set<TagKey<Item>>> itemMap = AlmostTagged.Config.itemTagJsonMap;
                break;
            case "tags/block":
                final Map<Item, Set<TagKey<Block>>> blockMap = AlmostTagged.Config.blockTagJsonMap;
                break;
        }
    }
}
