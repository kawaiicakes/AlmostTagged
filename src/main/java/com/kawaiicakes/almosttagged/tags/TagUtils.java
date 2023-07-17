package com.kawaiicakes.almosttagged.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * This class provides static utility methods for tags,
 * in particular operations which are trivial to perform,
 * repetitive, or which are not necessary to be written
 * in other classes.
 * <p>
 * Its importance is in improving readability; namely by
 * introducing statements checking that
 * <code>ITagManager&lt;V&gt;</code> has loaded where applicable.
 * This is because instances of <code>ITagKey&lt;V&gt;</code> do not have
 * anything bound to them prior to loading the logical server.
 * <p>
 * This will cut down on writing overly long code in contexts where
 * it may already be complex. It will also simplify debugging.
 */
public class TagUtils {

    /**
     * This is a simple <code>boolean</code> field indicating whether <code>ITagManager&lt;V&gt;</code>
     * has loaded or not.
     * <p>
     * <code>false</code> by default until <code>#tagsLoaded</code> is called during the appropriate time.
     */
    private static boolean TAGS_LOADED = false;

    /**
     * When called, indicates <code>ITagManager&lt;V&gt;</code> has successfully loaded.
     */
    public static void tagsLoaded() {
        TAGS_LOADED = true;
    }

    /**Simple method returning a <code>Stream</code> of <code>TagKey&lt;Item&gt;</code>
     * corresponding to the <code>Item</code> of a given <code>ResourceLocation</code>.
     * <p>
     * A <code>ResourceLocation</code> is taken as a param as it's simply what is easiest
     * to work with in this context. A <code>Stream</code> is also kept as the return
     * to allow potential operations on the data prior to collecting it.
     *
     * @param resourceLocation  the <code>ResourceLocation</code> used to resolve tags.
     * @return      a <code>Stream</code> containing <code>TagKey&lt;Item&gt;</code>.
     * @throws AssertionError   thrown when this method is called before
     *                          <code>ITagManager&lt;V&gt;</code> has loaded.
     * @throws NullPointerException thrown when the argument points to no valid <code>Item</code> or
     *                              if for whatever Godforsaken reason <code>IForgeRegistry&lt;Item&gt;</code>
     *                              does not support tags/has a wrapper registry.
     */
    public static @NotNull Stream<TagKey<Item>> itemTagsFromHolder(ResourceLocation resourceLocation) {
        if (!TAGS_LOADED) throw new AssertionError();
        final Item item = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(resourceLocation));

        return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getReverseTag(item).orElseThrow().getTagKeys();
    }

    /**
     * Basically the same as <code>#itemTagsFromHolder</code>, but using <code>Block</code>
     * in lieu of <code>Item</code>.
     *
     * @param resourceLocation  the <code>ResourceLocation</code> used to resolve tags.
     * @return      a <code>Stream</code> containing <code>TagKey&lt;Block&gt;</code>.
     * @throws AssertionError   thrown when this method is called before
     *                          <code>ITagManager&lt;V&gt;</code> has loaded.
     * @throws NullPointerException thrown when the argument points to no valid <code>Block</code> or
     *                              if for whatever Godforsaken reason <code>IForgeRegistry&lt;Block&gt;</code>
     *                              does not support tags/has a wrapper registry.
     */
    public static @NotNull Stream<TagKey<Block>> blockTagsFromHolder(ResourceLocation resourceLocation) {
        if (!TAGS_LOADED) throw new AssertionError();
        final Block block = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(resourceLocation));

        return Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getReverseTag(block).orElseThrow().getTagKeys();
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
