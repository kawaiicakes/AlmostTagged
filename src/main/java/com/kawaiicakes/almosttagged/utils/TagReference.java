package com.kawaiicakes.almosttagged.utils;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.Objects;
import java.util.stream.Stream;

/*
VERY crude implementation. Iterating through the entire registry of
tags seems unideal; and possibly laggy.

Additionally merging these two methods might be more elegant
of a solution. My concern is readability.
 */
class TagReference {
    static Stream<TagKey<Item>> getItemTagStreamFromItem(Item item) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.tags())
                .stream()
                .filter(itag -> itag.contains(item))
                .map(ITag::getKey);
    }

    static Stream<TagKey<Block>> getBlockTagStreamFromItem(Item item) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.tags())
                .stream()
                .filter(itag -> itag.contains(Block.byItem(item)))
                .map(ITag::getKey);
    }
}
