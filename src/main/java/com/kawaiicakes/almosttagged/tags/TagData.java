package com.kawaiicakes.almosttagged.tags;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * This class is intended to be used to efficiently interact with the tags returned in the map of
 * <code>TagLoader#build</code>. This map contains keys of type <code>ResourceLocation</code> and
 * values of <code>Collection</code>s of type <code>V</code>.
 * <p>
 * These keys represent a distinct tag in <code>IForgeRegistry</code> of type <code>V</code>, and
 * whose values represent the instances of <code>V</code> subtyping <code>ItemLike</code> assigned to that
 * tag.
 * @param <V>   the object type; usually intended to be either <code>Item</code>
 *              or <code>Block</code> (or more specifically the type parameter of <code>Holder.Reference&lt;V&gt;</code>).
 */
public class TagData<V> implements Map<ResourceLocation, Collection<Holder.Reference<V>>> {

    private final Map<ResourceLocation, Collection<Holder.Reference<V>>> data;

    public TagData() {
        this.data = new HashMap<>();
    }
    public TagData(Map<ResourceLocation, Collection<Holder.Reference<V>>> data) {
        this.data = data;
    }

    public Map<ResourceLocation, Collection<Holder.Reference<V>>> getData() {return this.data;}

    public Stream<TagKey<?>> getTags(V v) {
        return this.entrySet().stream()
                .filter(entry -> entry.getValue().contains(v))
                .mapMulti((entry, consumer) -> {
                    if (v.getClass() == Item.class) {
                        consumer.accept(TagUtils.getItemTagKeyFromResource(entry.getKey()));
                    } else {
                        consumer.accept(TagUtils.getBlockTagKeyFromResource(entry.getKey()));
                    }
                });
    }

    @Nullable
    public Stream<?> get(ResourceLocation resourceLocation) {
        return this.data.get(resourceLocation).stream().map(e -> ((Holder.Reference<?>) e).get());
    }

    @Override
    public int size() {
        return (this.data == null) ? 0 : this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public boolean containsKey(Object resourceLocation) {
        return this.data.containsKey(resourceLocation);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.data.containsValue(value);
    }

    @Override
    public Collection<Holder.Reference<V>> get(Object key) {
        return this.data.get(key);
    }

    @Nullable
    @Override
    public Collection<Holder.Reference<V>> put(ResourceLocation key, Collection<Holder.Reference<V>> value) {
        return this.data.put(key, value);
    }

    @Override
    public Collection<Holder.Reference<V>> remove(Object key) {
        return this.data.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends ResourceLocation, ? extends Collection<Holder.Reference<V>>> m) {
        this.data.putAll(m);
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    @NotNull
    @Override
    public Set<ResourceLocation> keySet() {
        return this.data.keySet();
    }

    @NotNull
    @Override
    public Collection<Collection<Holder.Reference<V>>> values() {
        return this.data.values();
    }

    @NotNull
    @Override
    public Set<Entry<ResourceLocation, Collection<Holder.Reference<V>>>> entrySet() {
        return this.data.entrySet();
    }
}
