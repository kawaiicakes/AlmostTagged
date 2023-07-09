package com.kawaiicakes.almosttagged.tags;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.api.AlmostUnifiedLookupWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class TagStreamGenerators {
    private static Map<Item, Set<TagKey<Item>>> itemMap;
    public static Map<Item, Set<TagKey<Item>>> getItemMap() {return itemMap;}
    private static Map<Item, Set<TagKey<Block>>> blockMap;
    public static Map<Item, Set<TagKey<Block>>> getBlockMap() {return blockMap;}

    private static Set<TagKey<Item>> tagSet;
    public static void tagSet(Set<TagKey<Item>> setter) {tagSet = setter;}

    public static void generateAUTagMaps() {
        assert tagSet != null;
        final Map<Item, Set<TagKey<Item>>> tagItemMap = new HashMap<>();
        final Map<Item, Set<TagKey<Block>>> tagBlockMap = new HashMap<>();

        for (TagKey<Item> key : tagSet) {
            final Item preferredItem = AlmostUnifiedLookupWrapper.getPreferredItemForTag(key);
            if (preferredItem.toString().equals("minecraft:air")) return;

            final Set<Item> potentialItems = Objects.requireNonNull(AlmostUnifiedLookupWrapper.getPotentialItems(key));
            if (potentialItems.size() == 0) return;

            final Set<TagKey<Item>> inheritedItemSet = potentialItems
                    .stream()
                    .<TagKey<Item>>mapMulti(((item, tagKeyConsumer) -> TagReference.getItemTagStreamFromItem(item).forEach(tagKeyConsumer)))
                    .collect(Collectors.toSet());

            final Set<TagKey<Block>> inheritedBlockSet = potentialItems
                    .stream()
                    .filter(i -> Block.byItem(i) != ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:air")))
                    .<TagKey<Block>>mapMulti(((item, tagKeyConsumer) -> TagReference.getBlockTagStreamFromItem(item).forEach(tagKeyConsumer)))
                    .collect(Collectors.toSet());

            tagItemMap.put(preferredItem, inheritedItemSet);
            if (Block.byItem(preferredItem) != ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:air"))) {
                tagBlockMap.put(preferredItem, inheritedBlockSet);
            }
        }

        itemMap = tagItemMap;
        blockMap = tagBlockMap;
        AlmostTagged.LOGGER.info(tagItemMap.toString());
        AlmostTagged.LOGGER.info(tagBlockMap.toString());
    }
}
