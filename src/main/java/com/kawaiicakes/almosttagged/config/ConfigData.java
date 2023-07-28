package com.kawaiicakes.almosttagged.config;

import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.tags.TagData;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This record contains data loaded from each <code>TagConfigEntries</code>. Its purpose is to permit
 * easier operation and reference of this data, including remapping functions. It is intended that
 * there is one instance of <code>ConfigData</code> per field in <code>TagConfigEntries</code>.
 * <p>
 * It exists to provide an interface of sorts between <code>TagData&lt;V&gt;</code> and <code>String</code>s.
 *
 * @param data  a <code>Map&lt;String, Set&lt;String&gt;&gt;</code> containing data from the config corresponding to
 *              a field of <code>TagConfigEntries</code>.
 */
public record ConfigData(Map<String, Set<String>> data) implements Map<String, Set<String>>{

    /**
     * <code>#strainer</code> is meant to be used in the context of a <code>mapMulti</code> operation.
     * Its purpose is to selectively pass <code>TagKey&lt;?&gt;</code>s to the <code>Consumer</code>
     * based on criteria defined by the blacklisted objects in the config. It is meant to be used on
     * instances of <code>ConfigData</code> from the item/blockBlacklist fields in <code>TagConfigEntries</code>.
     * <p>
     * The <code>Consumer</code> accepts tags from <code>vTagData#getTags</code> if this instance
     * of <code>ConfigData</code> does not contain key of instance <code>V</code>. It also accepts tags selectively
     * from <code>vTagData</code> otherwise, provided that the specific tag does not match a blacklisted
     * tag for the key of instance <code>V</code> in the config.
     *
     * @param vTagData  the <code>TagData</code> representing all tags and their bindings for <code>V</code>.
     * @param vHolder   an instance of <code>V</code>. Will always be a <code>Holder.Reference&lt;?&gt;</code>
     * @param consumer  the <code>Consumer</code> accepting <code>TagKey&lt;?&gt;</code>s.
     * @param <V>       an instance of <code>Holder.Reference&lt;?&gt;</code>.
     */
    public <V> void strainer(@NotNull TagData<V> vTagData, ConfigData tagBlacklist, @NotNull V vHolder, Consumer<TagKey<?>> consumer) {
        if (this.containsKey(((Holder.Reference<?>) vHolder).get().toString())) {
            if (Objects.requireNonNull(this.get(vHolder)).isEmpty()) return;
            vTagData.getTags(vHolder)
                    .filter(i -> !Objects.requireNonNull(this
                            .get(((Holder.Reference<?>) vHolder).get().toString())).contains(i.location().toString()))
                    .filter(i -> !tagBlacklist.containsKey(i.location().toString()))
                    .forEach(consumer);
        } else {
            vTagData.getTags(vHolder)
                    .filter(i -> !tagBlacklist.containsKey(i.location().toString()))
                    .forEach(consumer);
        }
    }

    /**
     * Intended to be used on instances of <code>ConfigData</code> which represent the itemTagBlacklist and
     * blockTagBlacklist fields in <code>TagConfigEntries</code>.
     * <p>
     * This method's function simply inverts the mappings in this object. More formally, for the data
     * <code>Map&lt;K, Set&lt;V&gt;&gt;</code> of this object, the data is remapped to
     * <code>Map&lt;V, Set&lt;K&gt;&gt;</code>; such that all occurrences of <code>K</code> in which
     * <code>V</code> appears are returned as a <code>Set&lt;K&gt;</code>. In addition, it checks for
     * if the target key's <code>Set</code> is empty, in which case it will not merge anything to there.
     * <p>
     * The resulting 'inverse' map is then merged with <code>target</code> using <code>#merge</code>.
     *
     * @param target    the target <code>ConfigData</code> to which this object's inverted mapping will be pushed.
     */
    public void inverseToTarget(@NotNull ConfigData target) {
        ConfigData inverseMap = new ConfigData(new HashMap<>());

        //#distinct is used so that for every Holder.Reference<V>, a value is assigned to it only once.
        this.values().stream().distinct().<String>mapMulti(Iterable::forEach).forEach(t -> {
            if (target.containsKey(t)) { //I split conditions to avoid NullPointerExceptions if no such key t exists
                if (Objects.requireNonNull(target.get(t)).isEmpty()) return;
            }
            inverseMap.put(t, this.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().contains(t))
                    .map(Entry::getKey)
                    .collect(Collectors.toSet()));
        });
        target.merge(inverseMap);
    }

    /**
     * Method purposed for non-destructively appending keys and values to this object. If a key exists in both
     * this and <code>pData</code>, then the corresponding value in <code>pData</code> is iterated over
     * and its elements are added to the <code>Set&lt;String&gt;</code> in this object.
     * <p>
     * Bear in mind that the object this is used on will be modified.
     * @param pData     the <code>ConfigData</code> to be merged with this object.
     */
    public void merge(@NotNull ConfigData pData) {
        pData.forEach((key, value) -> {
            if (this.containsKey(key)) {
                Objects.requireNonNull(pData.get(key)).forEach(Objects.requireNonNull(this.get(key))::add);
            } else {
                this.put(key, value);
            }
        });
    }

    /**
     * Debug method intended to log this data. For dev use. Should be self-explanatory.
     */
    public void print() {
        AlmostTagged.LOGGER.info("******");
        AlmostTagged.LOGGER.info("ConfigData of " + this.hashCode());
        this.forEach((key, value) -> {
            AlmostTagged.LOGGER.info(key);
            AlmostTagged.LOGGER.info(value.toString());
        });
        AlmostTagged.LOGGER.info("******");
    }

    @Override
    public int size() {return this.data.size();}
    @Override
    public boolean isEmpty() {return this.data.isEmpty();}
    @Override
    public boolean containsKey(Object key) {return this.data.containsKey(key);}
    @Override
    public boolean containsValue(Object value) {return this.data.containsValue(value);}
    @Override
    public @Nullable Set<String> get(Object key) {return this.data.get(key);}
    @Nullable
    @Override
    public Set<String> put(String key, Set<String> value) {return this.data.put(key, value);}
    @Override
    public Set<String> remove(Object key) {return this.data.remove(key);}
    @Override
    public void putAll(@NotNull Map<? extends String, ? extends Set<String>> m) {this.data.putAll(m);}
    @Override
    public void clear() {this.data.clear();}
    @NotNull
    @Override
    public Set<String> keySet() {return this.data.keySet();}
    @NotNull
    @Override
    public Collection<Set<String>> values() {return this.data.values();}
    @NotNull
    @Override
    public Set<Entry<String, Set<String>>> entrySet() {return this.data.entrySet();}
}
