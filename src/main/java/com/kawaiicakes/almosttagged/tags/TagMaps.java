package com.kawaiicakes.almosttagged.tags;

import com.kawaiicakes.almosttagged.AlmostTagged;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

public class TagMaps {

    //what a mess lmao. I should really clean this up
    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    public static <T> void loadTags(CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> mapCallback, @NotNull String directory) {
        switch (directory) {
            case "tags/items":
                AlmostTagged.Config.itemTagJsonMap.forEach((k, v) -> v.forEach(stringTag ->
                        mapCallback.getReturnValue().put(TagReference.getTagResourceLocationFromString(stringTag),
                                (Collection<T>) Collections.singleton(ForgeRegistries.ITEMS.getHolder(new ResourceLocation(k)).get()))));
                AlmostTagged.LOGGER.info("Almost Tagged has successfully inherited item tags.");
                break;
            case "tags/blocks":
                AlmostTagged.Config.blockTagJsonMap.forEach((k, v) ->
                        v.forEach(stringTag ->
                                mapCallback.getReturnValue().put(TagReference.getTagResourceLocationFromString(stringTag),
                                (Collection<T>) Collections.singleton(ForgeRegistries.BLOCKS.getHolder(new ResourceLocation(k)).get()))));
                AlmostTagged.LOGGER.info("Almost Tagged has successfully inherited block tags.");
                break;
        }
    }

    //This method should read the contents of the TagLoader PRIOR to the Almost Tagged config's tags being loaded.
    //Otherwise, it should contain all tags.
    public static <T> @NotNull Map<String, Set<String>> tagLoaderToJsonString(@NotNull CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> mapCallback) {
        final Map<ResourceLocation, Collection<T>> raw = mapCallback.getReturnValue();

        return new HashMap<>(TagReference.resolveMapFromHolder(raw));
    }
}
