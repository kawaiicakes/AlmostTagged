package com.kawaiicakes.almosttagged.tags;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.api.AlmostUnifiedLookupWrapper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class TagStreamGenerators {
    public static void getAUItemTagStream() {
        final Set<TagKey<Item>> tagSet = AlmostUnifiedLookupWrapper.getConfiguredTags();
        assert tagSet != null;
        AlmostTagged.LOGGER.info(tagSet.toString());

        for (TagKey<Item> key : tagSet) {
            AlmostTagged.LOGGER.info(key.location().toString());
            final Item preferredItem = AlmostUnifiedLookupWrapper.getPreferredItemForTag(key);
            if (preferredItem.toString().equals("minecraft:air")) return;

            final Set<Item> potentialItems = Objects.requireNonNull(AlmostUnifiedLookupWrapper.getPotentialItems(key));
            if (potentialItems.size() == 0) return;

            final Stream<TagKey<Item>> tagStream = potentialItems
                    .stream()
                    .mapMulti((item, consumer) -> TagReference.getItemTagStreamFromItem(item).forEach(consumer));
        }
    }

    public static void getAUBlockTagStream() {
        final Set<TagKey<Item>> tagSet = AlmostUnifiedLookupWrapper.getConfiguredTags();
        assert tagSet != null;

        for (TagKey<Item> key : tagSet) {
            final Item preferredItem = AlmostUnifiedLookupWrapper.getPreferredItemForTag(key);
            if (preferredItem.toString().equals("minecraft:air")) return;

            final Set<Item> potentialItems = Objects.requireNonNull(AlmostUnifiedLookupWrapper.getPotentialItems(key));
            if (potentialItems.size() == 0) return;

            final Stream<TagKey<Block>> tagStream = potentialItems
                    .stream()
                    .mapMulti((item, consumer) -> TagReference.getBlockTagStreamFromItem(item).forEach(consumer));
        }
    }
}
