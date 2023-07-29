package com.kawaiicakes.almosttagged.mixins;

import com.kawaiicakes.almosttagged.api.TagLoaderAPI;
import com.kawaiicakes.almosttagged.tags.TagData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(value = TagLoader.class, priority = 25_000)
public class TagLoaderMixin {

    @Final
    @Shadow
    private String directory;

    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN"))
    private <T> void build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> p_203899_, CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        if (directory.equals("tags/items")) {
            TagLoaderAPI.setItemTagData(new TagData<>(map.getReturnValue()));
            TagLoaderAPI.modifyReturn(map, directory);
        } else if (directory.equals("tags/blocks")) {
            TagLoaderAPI.setBlockTagData(new TagData<>(map.getReturnValue()));
            TagLoaderAPI.modifyReturn(map, directory);
        }
    }
}
