package com.kawaiicakes.almosttagged.utils;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.kawaiicakes.almosttagged.api.AlmostUnifiedWrapper;


public class Tags {

    //https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.19.3/net/minecraftforge/registries/IForgeRegistry.html

    //getAllTagsInItemTag() must run only after all tags have been added to items
    public static <T> HashMap<T, Set<TagKey<T>>> getAllTagsInItemTag(TagKey<Item> key) {
        Set<Item> itemList = Objects.requireNonNull(AlmostUnifiedWrapper.getPotentialItems(key));
        Set<TagKey<Item>> tagsInTag = new HashSet<>();

        itemList.forEach(item -> {
            item.getDefaultInstance().getTags().forEach(tagsInTag::add);
        });

        return tagsInTag; //unfinished
    }
}
