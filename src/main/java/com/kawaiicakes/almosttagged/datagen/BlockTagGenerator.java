package com.kawaiicakes.almosttagged.datagen;

import com.kawaiicakes.almosttagged.api.AlmostUnifiedLookupWrapper;
import com.kawaiicakes.almosttagged.utils.TagReference;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class BlockTagGenerator extends ForgeRegistryTagsProvider<Block>{
    public BlockTagGenerator(DataGenerator generator, IForgeRegistry<Block> forgeRegistry, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, forgeRegistry, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
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

            tagStream.forEach(iTag -> tag(iTag).add(Block.byItem(preferredItem)));
        }
    }
}
