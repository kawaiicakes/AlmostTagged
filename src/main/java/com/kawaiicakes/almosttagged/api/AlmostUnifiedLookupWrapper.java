package com.kawaiicakes.almosttagged.api;

import com.almostreliable.unified.api.AlmostUnifiedLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

public class AlmostUnifiedLookupWrapper {
    private static boolean isLoaded() {

        return ModList.get().isLoaded("almostunified");

    }

    @Nullable
    public static Item getPreferredItemForTag(TagKey<Item> tag) {
        if (isLoaded()) {
            return Adapter.getPreferredItemForTag(tag);
        }

        return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag).stream()
                .findFirst()
                .orElseThrow();
    }

    @Nullable
    public static Set<Item> getPotentialItems(TagKey<Item> key) {
        if (isLoaded()) {
            return Adapter.getPotentialItems(key);
        }
        return null;
    }

    @Nullable
    public static Set<TagKey<Item>> getConfiguredTags() {
        if (isLoaded()) {
            return Adapter.getConfiguredTags();
        }
        return null;
    }

    private static class Adapter {
        @Nullable
        private static Item getPreferredItemForTag(TagKey<Item> tag) {
            return AlmostUnifiedLookup.INSTANCE.getPreferredItemForTag(tag);
        }

        private static Set<Item> getPotentialItems(TagKey<Item> key) {
            return AlmostUnifiedLookup.INSTANCE.getPotentialItems(key);
        }

        private static Set<TagKey<Item>> getConfiguredTags() {
            return AlmostUnifiedLookup.INSTANCE.getConfiguredTags();
        }
    }
}
