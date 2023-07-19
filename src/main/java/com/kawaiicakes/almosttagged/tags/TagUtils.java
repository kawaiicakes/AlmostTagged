package com.kawaiicakes.almosttagged.tags;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

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
    public static <V> @NotNull TagKey<?> getTagKeyFromResourceLocation(@NotNull V v, ResourceLocation location) {
        return (((Holder.Reference<?>) v).get() instanceof Item)
                ? new TagKey<>(ForgeRegistries.ITEMS.getRegistryKey(), location)
                : new TagKey<>(ForgeRegistries.BLOCKS.getRegistryKey(), location);
    }

}
