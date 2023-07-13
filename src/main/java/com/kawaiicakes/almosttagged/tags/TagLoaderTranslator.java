package com.kawaiicakes.almosttagged.tags;

import com.kawaiicakes.almosttagged.AlmostTagged;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

public class TagLoaderTranslator {
    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    public static <T> void modifyReturn(CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map, @NotNull String directory) {
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

    @SuppressWarnings("unchecked")
    public static <T> @NotNull Map<String, Set<String>> loaderReturnJsonStr(@NotNull CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        final Map<String, Set<String>> returnMap = new HashMap<>();
        map.getReturnValue().forEach((key, value) -> {
            final String resourceString = key.getNamespace() + ":" + key.getPath();
            final Set<String> collectionString = ((Collection<Holder.Reference<T>>) value).stream()
                    .<String>mapMulti((holder, consumer) -> holder.tags().map(TagKey::toString).forEach(consumer))
                    .collect(Collectors.toSet()); //shit. i forgot the holder's tags arent accessible until AFTER this method is called.

            returnMap.put(resourceString, collectionString);
        });

        return returnMap;
    }
}
