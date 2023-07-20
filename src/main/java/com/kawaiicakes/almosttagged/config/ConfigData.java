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

public record ConfigData(Map<String, Set<String>> data) implements Map<String, Set<String>>{
    public <V> void filter(@NotNull TagData<V> vTagData, @NotNull V v, Consumer<TagKey<?>> consumer) {
        if (this.containsKey(((Holder.Reference<?>) v).get().toString())) {
            vTagData.getTags(v)
                    .filter(i -> !Objects.requireNonNull(this.get(((Holder.Reference<?>) v).get()
                            .toString())).contains(i.toString()))
                    .forEach(consumer);
        } else if (!this.containsKey(((Holder.Reference<?>) v).get().toString())) {
            vTagData.getTags(v).forEach(consumer);
        }
    }

    public void inverseToTarget(@NotNull ConfigData target) {
        ConfigData inverseMap = new ConfigData(new HashMap<>());

        //#distinct is used so that for every Holder.Reference<V>, a value is assigned to it only once.
        this.values().stream().distinct().<String>mapMulti(Iterable::forEach).forEach(t ->
                inverseMap.put(t, this.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue().contains(t))
                        .map(Entry::getKey)
                        .collect(Collectors.toSet())));

        target.concat(inverseMap);
    }

    public void concat(@NotNull ConfigData pData) {
        pData.forEach((key, value) -> {
            if (this.containsKey(key)) {
                Objects.requireNonNull(pData.get(key)).forEach(Objects.requireNonNull(this.get(key))::add);
            } else {
                this.put(key, value);
            }
        });
    }

    public void print() {
        AlmostTagged.LOGGER.info("******");
        AlmostTagged.LOGGER.info("ConfigData of " + this);
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
