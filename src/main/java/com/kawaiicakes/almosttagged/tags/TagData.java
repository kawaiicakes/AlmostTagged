package com.kawaiicakes.almosttagged.tags;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.DebugDumper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This record is intended to be used to efficiently interact with the tags returned in the map of
 * <code>TagLoader#build</code>. This map contains keys of type <code>ResourceLocation</code> and
 * values of <code>Collection</code>s of type <code>V</code>.
 * <p>
 * Objects of this class contain a <code>Map</code> indexing all the instances of <code>V</code> bound to
 * a given tag; represented as a <code>ResourceLocation</code>.
 * <p>
 * The advantage of using generics here is apparent when considering that <code>TagKey</code>s often share
 * locations, but not registries. Generics allow for easier referencing of a <code>TagKey</code>s in different
 * registries but sharing a <code>ResourceLocation</code>.
 * <p>
 * Bear in mind that the type parameter <code>V</code> is actually given by <code>net.minecraft.tags.TagLoader</code>
 * as <code>T</code>. This may get confusing when considering that <code>TagKey</code>s share <code>T</code>
 * as a type parameter; <code>V extends ItemLike</code> is false whereas <code>T extends ItemLike</code> is true.
 * Maybe this only confused me because I'm still relatively inexperienced lol.
 *
 * @param <V>  the type parameter; intended to be some instance of <code>Holder.Reference&lt;T&gt;</code>).
 * @param data This field is simply the data, specifically the map, contained in <code>this</code>>
 *             instance of the object.
 */
public record TagData<V>(Map<ResourceLocation, Collection<V>> data) implements Map<ResourceLocation, Collection<V>> {

    /**
     * Method used to return information regarding what tags are to be bound to an instance of <code>V</code>. In
     * practice this is believed to not differ from referencing tags via registries or by <code>Holder</code>.
     * The advantage is being able to access these tags prior to them being bound.
     * <p>
     * The primary usage of this method is to assist in returning the tags associated with all items in a tag;
     * as in <code>TagLoaderAPI</code>.
     *
     * @param v an instance of type <code>V</code>; expected to be a <code>Holder.Reference</code> of
     *          either <code>Item</code> or <code>Block</code>.
     * @return A <code>Stream</code> of all <code>TagKey&lt;T&gt;</code>s to be bound to the instance of <code>V</code>,
     * where <code>T</code> is the <code>#value</code> of <code>V</code>.
     */
    public Stream<TagKey<?>> getTags(V v) {
        return this.entrySet().stream()
                .filter(entry -> entry.getValue().contains(v))
                .map(entry -> TagUtils.getTagKeyFromResourceLocation((v), entry.getKey()));
    }

    public void add(ResourceLocation resourceLocation, V v) {
        Collection<V> newCol = new ArrayList<>(this.data.get(resourceLocation));
        newCol.add(v); //this may run slow. optimize in the future
        this.data.put(resourceLocation, newCol);
    }

    public void print(DebugDumper.Type type) {
        DebugDumper.dump(this, type);
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
    public Collection<V> get(Object key) {
        return this.data.get(key);
    }

    @Nullable
    @Override
    public Collection<V> put(ResourceLocation key, Collection<V> value) {
        return this.data.put(key, value);
    }

    @Override
    public Collection<V> remove(Object key) {
        return this.data.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends ResourceLocation, ? extends Collection<V>> m) {
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
    public Collection<Collection<V>> values() {
        return this.data.values();
    }

    @NotNull
    @Override
    public Set<Entry<ResourceLocation, Collection<V>>> entrySet() {
        return this.data.entrySet();
    }
}