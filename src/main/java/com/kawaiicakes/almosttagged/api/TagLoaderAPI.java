package com.kawaiicakes.almosttagged.api;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.tags.TagData;
import com.kawaiicakes.almosttagged.tags.TagUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TagLoaderAPI {

    private static TagData<Item> itemTagData; //these work and return tags loading onto stuff including from datapack edits
    @SuppressWarnings("unchecked")
    public static void setItemTagData(TagData<?> tagData) {itemTagData = (TagData<Item>) tagData;}
    public static TagData<Item> getItemTagData() {return itemTagData;}

    private static TagData<Block> blockTagData;
    @SuppressWarnings("unchecked")
    public static void setBlockTagData(TagData<?> tagData) {blockTagData = (TagData<Block>) tagData;}
    public static TagData<Block> getBlockTagData() {return blockTagData;}

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    public static <T> void modifyReturn(CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map,
                                        @NotNull String directory) {
        switch (directory) {
            case "tags/items":
                AlmostTagged.StorageConfig.itemTagJsonMap.forEach((k, v) -> v.forEach(stringTag ->
                        map.getReturnValue().put(TagUtils.getTagResourceLocationFromString(stringTag),
                                (Collection<T>) Collections.singleton(ForgeRegistries.ITEMS
                                        .getHolder(new ResourceLocation(k)).get()))));
                AlmostTagged.LOGGER.info("Almost Tagged has successfully inherited item tags.");
                break;

            case "tags/blocks":
                AlmostTagged.StorageConfig.blockTagJsonMap.forEach((k, v) -> v.forEach(stringTag ->
                        map.getReturnValue().put(TagUtils.getTagResourceLocationFromString(stringTag),
                                (Collection<T>) Collections.singleton(ForgeRegistries.BLOCKS
                                        .getHolder(new ResourceLocation(k)).get()))));
                AlmostTagged.LOGGER.info("Almost Tagged has successfully inherited block tags.");
                break;
        }
    }

}
