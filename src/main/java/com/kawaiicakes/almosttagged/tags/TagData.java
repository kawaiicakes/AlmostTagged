package com.kawaiicakes.almosttagged.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This class is intended to be used to efficiently interact with the tags returned in the map of
 * <code>TagLoader#build</code>. This map contains keys of type <code>ResourceLocation</code> and
 * values of <code>Collection</code>s of type <code>V</code>.
 * <p>
 * These keys represent a distinct tag in <code>IForgeRegistry</code> of type <code>V</code>, and
 * whose values represent the instances of <code>V</code> subtyping <code>ItemLike</code> assigned to that
 * tag.
 * @param <V>   the object subclassed by <code>ItemLike</code>; intended to be either <code>Item</code>
 *              or <code>Block</code>.
 */
public class TagData<V extends ItemLike> implements Map<ResourceLocation, Collection<V>> {

    private final Map<ResourceLocation, Collection<V>> data;

    public TagData() {
        this.data = null;
    }

    public TagData(Map<ResourceLocation, Collection<V>> data) {
        this.data = data;
    }

    @Override
    public int size() {
        return (this.data == null) ? 0 : this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Collection<V> get(Object key) {
        return null;
    }

    @Nullable
    @Override
    public Collection<V> put(ResourceLocation key, Collection<V> value) {
        return null;
    }

    @Override
    public Collection<V> remove(Object key) {
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends ResourceLocation, ? extends Collection<V>> m) {

    }

    @Override
    public void clear() {

    }

    @NotNull
    @Override
    public Set<ResourceLocation> keySet() {
        return null;
    }

    @NotNull
    @Override
    public Collection<Collection<V>> values() {
        return null;
    }

    @NotNull
    @Override
    public Set<Entry<ResourceLocation, Collection<V>>> entrySet() {
        return null;
    }
}
