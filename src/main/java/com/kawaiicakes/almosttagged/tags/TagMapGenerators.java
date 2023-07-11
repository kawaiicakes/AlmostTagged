package com.kawaiicakes.almosttagged.tags;

import com.kawaiicakes.almosttagged.api.AlmostUnifiedLookupWrapper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagMapGenerators {
    private static Map<Holder.Reference<Item>, Stream<ResourceLocation>> itemTagMap;
    public static Map<Holder.Reference<Item>, Stream<ResourceLocation>> getItemTagMap() {return itemTagMap;}

    private static Map<Holder.Reference<Block>, Stream<ResourceLocation>> blockTagMap;
    public static Map<Holder.Reference<Block>, Stream<ResourceLocation>> getBlockTagMap() {return blockTagMap;}

    private static Set<TagKey<Item>> tagSet;
    public static void tagSet(Set<TagKey<Item>> setter) {tagSet = setter;}

    @SuppressWarnings("unchecked")
    public static <T> void generatePreferredTMaps(@NotNull CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> mapCallback) {
        //bif (tagSet == null) throw new AssertionError();
        final Map<ResourceLocation, Collection<T>> mapRaw = mapCallback.getReturnValue();

        final Map<Holder.Reference<Item>, Stream<ResourceLocation>> mapItemReturn = new HashMap<>();
        final Map<Holder.Reference<Block>, Stream<ResourceLocation>> mapBlockReturn = new HashMap<>();

        final Stream<Map.Entry<ResourceLocation, Collection<T>>> mapLoader = mapRaw
                .entrySet()
                .stream()
                .filter(i -> tagSet.contains(new TagKey<>(ForgeRegistries.ITEMS.getRegistryKey(), i.getKey())));

        mapLoader.forEach(entry -> {
            final Holder.Reference<Item> tItem = ForgeRegistries.ITEMS.getDelegateOrThrow(entry.getKey());
            final Stream.Builder<ResourceLocation> locationStream = Stream.builder();
            entry.getValue().forEach(t -> ((Holder.Reference<Item>) t).tags().map(TagKey::location).forEach(locationStream::add));

            mapItemReturn.put(tItem, locationStream.build());

            if (Block.byItem(tItem.get()) != ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:air"))) {
                final Holder.Reference<Block> tBlock = ForgeRegistries.BLOCKS.getDelegateOrThrow(entry.getKey());
                final Stream.Builder<ResourceLocation> locationStreamBlock = Stream.builder();
                entry.getValue().forEach(t -> ((Holder.Reference<Block>) t).tags().map(TagKey::location).forEach(locationStreamBlock::add));

                mapBlockReturn.put(tBlock, locationStreamBlock.build());
            }
        });

        itemTagMap = mapItemReturn;
        blockTagMap = mapBlockReturn;
    }
}

/*
holy fuck code         mapLoader.forEach((rsc, col) -> {
            final TagKey<Item> tagKey = new TagKey<>(ForgeRegistries.ITEMS.getRegistryKey(), rsc);
            final Item preferredItem = AlmostUnifiedLookupWrapper.getPreferredItemForTag(tagKey);
            final Holder.Reference<Item> preferredItemHolder = ForgeRegistries.ITEMS.getDelegateOrThrow(preferredItem);

            AtomicReference<Stream<ResourceLocation>> tagItemStream = new AtomicReference<>();

            col.forEach(holder -> {
                final Holder.Reference<T> castedHolder = ((Holder.Reference<T>) holder);
                tagItemStream.set(castedHolder.tags().map(TagKey::location));
            });

            mapItemReturn.put(preferredItemHolder, tagItemStream.get());

            if (Block.byItem(preferredItem) != ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:air"))) {
                final Holder.Reference<Block> preferredBlockHolder = ForgeRegistries.BLOCKS.getDelegateOrThrow(Block.byItem(preferredItem));

                AtomicReference<Stream<ResourceLocation>> tagBlockStream = new AtomicReference<>();
            }
        });
 */
