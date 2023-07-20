package com.kawaiicakes.almosttagged.api;

import com.almostreliable.unified.AlmostUnified;
import com.almostreliable.unified.api.AlmostUnifiedLookup;
import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.config.ConfigData;
import com.kawaiicakes.almosttagged.tags.TagData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.kawaiicakes.almosttagged.AlmostTagged.Config;

/**
 * This class is made to allow simpler interaction with the tags from <code>TagLoader</code>,
 * whose data is instantiated in <code>TagData&lt;V&gt;</code>.
 */
public class TagLoaderAPI {
    private static final ConfigData itemBlacklist = new ConfigData(Config.itemBlacklist);
    private static final ConfigData blockBlacklist = new ConfigData(Config.blockBlacklist);

    private static TagData<Holder.Reference<Item>> itemTagData; //these work and return tags loading onto stuff including from datapack edits
    @SuppressWarnings("unchecked")
    public static <V> void setItemTagData(TagData<V> tagData) {itemTagData = (TagData<Holder.Reference<Item>>) tagData;}

    private static TagData<Holder.Reference<Block>> blockTagData;
    @SuppressWarnings("unchecked")
    public static <V> void setBlockTagData(TagData<V> tagData) {blockTagData = (TagData<Holder.Reference<Block>>) tagData;}

    /**
     * This is by far the longest and most complex method in the entire module. In spite of that,
     * it's not actually *that* bad. When <code>#unifyTags</code> is called, the <code>itemTagData</code>
     * and <code>blockTagData</code> fields are modified.
     * <p>
     * These modifications require <code>AlmostUnified#isRuntimeLoaded</code> to be <code>true</code> as
     * many of the necessary calls to the Almost Unified API will not function if the AU runtime is not
     * initialized.
     * <p>
     * For every tag that AU intends to unify, there are items (the 'potential items') which have the tag,
     * and a preferred item which AU will use to unify entries. This method simply gets the potential items
     * bound per tag to be unified, gets the tags (the 'tag pool') associated with the potential items, and
     * then modifies the corresponding field such that the preferred item is now bound to all tags in the
     * tag pool. It also excludes any tags or items specified in <code>TagConfig</code>.
     */
    public static void unifyTags() {
        if (!AlmostUnified.isRuntimeLoaded()) throw new RuntimeException();
        final AlmostUnifiedLookup INST = AlmostUnifiedLookup.INSTANCE;
        final Block AIR = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:air"));

        (new ConfigData(Config.itemTagBlacklist)).inverseToTarget(itemBlacklist);
        (new ConfigData(Config.blockTagBlacklist)).inverseToTarget(blockBlacklist);

        for (TagKey<Item> tag : INST.getConfiguredTags()) {
            Holder.Reference<Item> preferredItemHolder = Objects.requireNonNull(INST.getPreferredItemForTag(tag))
                    .builtInRegistryHolder(); //Item#builtInRegistryHolder is deprecated but whatever lmao (Block too)

            INST.getPotentialItems(tag)
                    .stream()
                    .<TagKey<?>>mapMulti((item, consumer) -> itemBlacklist
                            .filter(itemTagData, item.builtInRegistryHolder(), consumer))
                    .map(TagKey::location)
                    .forEach(resourceLocation -> itemTagData.add(resourceLocation, preferredItemHolder));

            if (Block.byItem(INST.getPreferredItemForTag(tag)) != AIR) {
                Holder.Reference<Block> preferredBlockHolder = Block.byItem(INST
                                .getPreferredItemForTag(tag))
                                .builtInRegistryHolder();

                INST.getPotentialItems(tag)
                        .stream()
                        .<Block>mapMulti((item, consumer) -> {
                            if (Block.byItem(item) != AIR) {
                                consumer.accept(Block.byItem(item));
                            }
                        })
                        .<TagKey<?>>mapMulti((block, consumer) -> blockBlacklist
                                .filter(blockTagData, block.builtInRegistryHolder(), consumer))
                        .map(TagKey::location)
                        .forEach(resourceLocation -> blockTagData.add(resourceLocation, preferredBlockHolder));
            }
        }

        itemTagData.print(); //for debug purposes only
        blockTagData.print();

        itemBlacklist.forEach((key, value) -> {
            AlmostTagged.LOGGER.info(key);
            AlmostTagged.LOGGER.info(value.toString());
        });

        blockBlacklist.forEach((key, value) -> {
            AlmostTagged.LOGGER.info(key);
            AlmostTagged.LOGGER.info(value.toString());
        });
    }

    public static <T> void modifyReturn(CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map,
                                        @NotNull String directory) {
        switch (directory) {
            case "tags/items":

                break;

            case "tags/blocks":

                break;
        }
    }

}
