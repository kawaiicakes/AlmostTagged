package com.kawaiicakes.almosttagged.tags;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class TagMaps {
    /*
    Okay so basically this branch is gonna assume we can read/write using the actual objects as opposed to strings.
    That being said, we read Map<ResourceLocation, Collection<T>> from the TagLoader, where the key is the tag
    and the value is a Collection<Registry.Holder<T>> when T is either an instance of Item or Block.

    This is convenient as #generateAUTagMaps requires me to go out of my way to return a stream of items inside
    a tag. What's even more convenient is that the Holders allow me to reference the tags on that item directly;
    #tags() returns a Stream<TagKey<T>>. For passing a map to the config, this is actually perfect.

    For passing the config into the map, the story is a bit more complicated. I may need to keep my original spaghetti
    code for converting it into an appeasing format. I need to get the Holder for the preferred item, and then for
    each tag inside the map, add the holder to it. This may be possible using #getDelegate in IForgeRegistry<V>.

    My concern now is whether the config will necessarily match the generated map, as ITagManager<V>
    makes no guarantees about its persistence relative to a registry value. I guess I'll have to try and see.
    I could also match the map based on content of its keys rather than both order and content.
    */

    //This is called *before* tags are loaded from our config.

}
