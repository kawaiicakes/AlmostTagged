package com.kawaiicakes.almosttagged.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

/*
VERY crude implementation. Iterating through the entire registry of
tags seems unideal; and possibly laggy.
 */
public class TagUtils {
    public static Stream<TagKey<Item>> getItemTagStreamFromItem(Item item) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.tags())
                .stream()
                .filter(itag -> itag.contains(item))
                .map(ITag::getKey);
    }

    public static Stream<TagKey<Block>> getBlockTagStreamFromItem(Item item) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.tags())
                .stream()
                .filter(itag -> itag.contains(Block.byItem(item)))
                .map(ITag::getKey);
    }

    public static @NotNull ResourceLocation getTagResourceLocationFromString(@NotNull String string) {
        String location = string.replaceAll(".+/", "").strip().substring(0, string.replaceAll(".+/", "").strip().indexOf("]")).strip();
        return new ResourceLocation(location);
    }

    public static @NotNull TagKey<Item> getItemTagKeyFromResource(ResourceLocation location) {
        return new TagKey<>(ForgeRegistries.ITEMS.getRegistryKey(), location);
    }

    public static @NotNull TagKey<Block> getBlockTagKeyFromResource(ResourceLocation location) {
        return new TagKey<>(ForgeRegistries.BLOCKS.getRegistryKey(), location);
    }

}
