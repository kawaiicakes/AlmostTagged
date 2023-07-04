package com.kawaiicakes.almosttagged.datagen;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.api.AlmostUnifiedLookupWrapper;
import com.kawaiicakes.almosttagged.utils.TagReference;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class ItemTagGenerator extends ForgeRegistryTagsProvider<Item>{
    public ItemTagGenerator(DataGenerator generator, IForgeRegistry<Item> forgeRegistry, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, forgeRegistry, modId, existingFileHelper);
    }

    @Override
    public void addTags() {
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

            tagStream.forEach(iTag -> tag(iTag).add(preferredItem));
        }
    }
}
