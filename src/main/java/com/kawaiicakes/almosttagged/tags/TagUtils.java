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
    /**
     * Method returning a <code>TagKey</code> of appropriate registry based on the type of <code>V</code> and
     * a <code>ResourceLocation</code>, which may be any.
     * @param v     an instance of <code>V</code>; a <code>Holder.Reference&lt;T&gt;</code>.
     * @param location  any valid <code>ResourceLocation</code>.
     * @return              a <code>TagKey&lt;T&gt;</code>, where <code>T</code> denotes the registry type provided.
     * @param <V>   a <code>Holder.Reference&lt;T&gt;</code>. The specified registry will
     *              correspond to the registry type of <code>T</code>. This usage is derived from
     *              the behaviours of <code>TagData&lt;V&gt;</code>
     */
    public static <V> @NotNull TagKey<?> getTagKeyFromResourceLocation(@NotNull V v, ResourceLocation location) {
        return (((Holder.Reference<?>) v).get() instanceof Item)
                ? new TagKey<>(ForgeRegistries.ITEMS.getRegistryKey(), location)
                : new TagKey<>(ForgeRegistries.BLOCKS.getRegistryKey(), location);
    }
}
