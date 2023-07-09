package com.kawaiicakes.almosttagged.mixins;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.tags.TagReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mixin(value = TagLoader.class)
public class TagLoaderMixin {

    @Final
    @Shadow
    private String directory;

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN"))
    private <T> void build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> p_203899_, CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        switch (directory) {
            case "tags/items":
                AlmostTagged.Config.itemTagJsonMap.forEach((k, v) -> v.forEach(stringTag ->
                        map.getReturnValue().put(TagReference.getTagResourceLocationFromString(stringTag),
                                (Collection<T>) Collections.singleton(ForgeRegistries.ITEMS.getHolder(new ResourceLocation(k)).get()))));
                AlmostTagged.LOGGER.info("Almost Tagged has successfully inherited item tags.");
                break;
            case "tags/blocks":
                AlmostTagged.Config.blockTagJsonMap.forEach((k, v) -> v.forEach(stringTag ->
                        map.getReturnValue().put(TagReference.getTagResourceLocationFromString(stringTag),
                                (Collection<T>) Collections.singleton(ForgeRegistries.BLOCKS.getHolder(new ResourceLocation(k)).get()))));
                AlmostTagged.LOGGER.info("Almost Tagged has successfully inherited block tags.");
                break;
        }
    }
}
