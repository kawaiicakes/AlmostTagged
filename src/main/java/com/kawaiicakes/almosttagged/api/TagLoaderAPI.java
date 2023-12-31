package com.kawaiicakes.almosttagged.api;

import com.almostreliable.unified.AlmostUnified;
import com.almostreliable.unified.api.AlmostUnifiedLookup;
import com.kawaiicakes.almosttagged.config.ConfigData;
import com.kawaiicakes.almosttagged.DebugDumper;
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
 * This class is the main interaction point of this mod with the tags from <code>TagLoader</code>,
 * whose data is instantiated in instances of <code>TagData&lt;V&gt;</code>. All overarching operations
 * which perform the unification of tags are likely to be found here.
 * <p>
 * All tag changes intended to be made are passed when <code>#modifyReturn</code> is called.
 */
public class TagLoaderAPI { //AlmostUnifiedRuntimeImpl and ReplacementMap are probably what I want to use. I may also just
    //have to make a new (but identical) instance of StoneStrataHandler.
    private static final ConfigData itemBlacklist = new ConfigData(Config.itemBlacklist);
    private static final ConfigData blockBlacklist = new ConfigData(Config.blockBlacklist);
    private static final ConfigData itemTagBlacklist = new ConfigData(Config.itemTagBlacklist);
    private static final ConfigData blockTagBlacklist = new ConfigData(Config.blockTagBlacklist);

    /**
     * Both <code>itemTagData</code> and <code>blockTagData</code> will contain their respective maps
     * but with unified tags.
     */
    private static TagData<Holder.Reference<Item>> itemTagData;
    @SuppressWarnings("unchecked")
    public static <V> void setItemTagData(TagData<V> tagData) {itemTagData = (TagData<Holder.Reference<Item>>) tagData;}

    private static TagData<Holder.Reference<Block>> blockTagData;
    @SuppressWarnings("unchecked")
    public static <V> void setBlockTagData(TagData<V> tagData) {blockTagData = (TagData<Holder.Reference<Block>>) tagData;}

    /**
     * This is by far the longest and most complex method in the entire mod. In spite of that,
     * it's not actually *that* bad despite being an eyesore. When <code>#unifyTags</code> is called,
     * the <code>itemTagData</code> and <code>blockTagData</code> fields are modified.
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
        DebugDumper.dump(itemTagData, DebugDumper.Type.CONFIG);

        /*
        This 2 call sequence is a cheeky little way to transform the other TagConfigEntries fields into
        the one I've already coded logic for (item and blockBlacklist). It could be called somewhere else
        but here makes sense to me.
         */
        blockTagBlacklist.inverseToTarget(itemBlacklist);
        itemTagBlacklist.inverseToTarget(blockBlacklist);

        for (TagKey<Item> tag : INST.getConfiguredTags()) {
            Holder.Reference<Item> preferredItemHolder = Objects.requireNonNull(INST.getPreferredItemForTag(tag))
                    .builtInRegistryHolder(); //Item#builtInRegistryHolder is deprecated but whatever lmao (Block too)

            /*
            this returns a stream of all tags on the items in a tag, excluding those specified in the blacklist.
            anything that appears to be an ore is filtered out and processed later such that only ores within the
            same strata are included in the pool.

            *new idea: instead, the Stream<Item> I get is going to be partitioned using Collectors according to
            whether or not the item in question satisfies #isStoneStrataTag. In cases where this is false,
            proceed as normal. In cases where this is true, each pool will instead be generated per tag per strata.

            Collectors#groupingBy accepts a function accepting elements of the stream based on the return of
            the function. it maps these elements to keys provided by the function. I can use this to group the ores
            containing strata tags into their respective stratum, and then derive their tag pools from there.

            logic also has to be redone with the #filterBlacklisted method bc it doesn't seem to work... at present
            this is a shit fuck and needs to be moved into different classes or something...

            oh right. and i still gotta obtain the StoneStrataHandler instantiated in AlmostUnifiedRuntimeImpl...
            At present I may just have to instantiate one using identical args, but let's see what the guys in the
            discord have to say...

            Example of mentioned Collectors stuff:
                Random r = new Random();

                Map<Boolean, List<String>> groups = stream
                    .collect(Collectors.partitioningBy(x -> r.nextBoolean()));

                System.out.println(groups.get(false).size());
                System.out.println(groups.get(true).size()); check this shit out

                for grouping into more than 2 groups:
                Map<Object, List<String>> groups = stream
                    .collect(Collectors.groupingBy(x -> r.nextInt(3)));
                System.out.println(groups.get(0).size());
                System.out.println(groups.get(1).size());
                System.out.println(groups.get(2).size());
            */
            INST.getPotentialItems(tag)
                    .stream()
                    .<TagKey<?>>mapMulti((item, consumer) -> itemBlacklist
                            .filterBlacklisted(itemTagData, itemTagBlacklist, item.builtInRegistryHolder(), consumer))
                    .map(TagKey::location)
                    .forEach(resourceLocation -> itemTagData.add(resourceLocation, preferredItemHolder));

            //starting on the line below this, same logic as above is executed but slightly changed to handle blocks
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
                                .filterBlacklisted(blockTagData, blockTagBlacklist, block.builtInRegistryHolder(), consumer))
                        .map(TagKey::location)
                        .forEach(resourceLocation -> blockTagData.add(resourceLocation, preferredBlockHolder));
            }
        }

        itemTagData.print(DebugDumper.Type.ITEM);
        blockTagData.print(DebugDumper.Type.BLOCK);
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
