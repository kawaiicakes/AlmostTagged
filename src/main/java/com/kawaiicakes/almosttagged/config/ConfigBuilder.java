package com.kawaiicakes.almosttagged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kawaiicakes.almosttagged.tags.TagStreamGenerators;
import net.minecraft.tags.TagKey;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class ConfigBuilder {
    public static final Gson BUILDER = (new GsonBuilder()).setPrettyPrinting().create();

    public static final Path file = FMLPaths.GAMEDIR.get().toAbsolutePath().resolve("config").resolve("almosttagged.json");

    public static ConfigEntries loadConfig() {
        //write, then reload if tags in config do not match the tagMaps in TagStreamGenerators.
        //this means a reload will always occur on first loading of a world since the config will be compared against null.
        //therefore consider moving this functionality into TagLoaderMixin and instead affirming #loadConfig as purely
        //reading the config. Reload functionality should occur prior to the passing of tags to the TagLoader.
        try {
            if (Files.notExists(file)) {
                ConfigEntries atConfig = new ConfigEntries();

                final Set<String> initialSet = new HashSet<>();
                initialSet.add("balls");
                initialSet.add("penis");

                atConfig.itemTagJsonMap.put("air", initialSet);
                atConfig.blockTagJsonMap.put("air", initialSet);

                String defaultJson = BUILDER.toJson(atConfig, new TypeToken<ConfigEntries>(){}.getType());
                Files.writeString(file, defaultJson);
            }

            return BUILDER.fromJson(Files.readString(file), ConfigEntries.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void reloadConfig() {
        try {
            ConfigEntries atConfig = new ConfigEntries();

            final Map<String, Set<String>> itemStringMap = new HashMap<>();
            final Map<String, Set<String>> blockStringMap = new HashMap<>();

            TagStreamGenerators.getItemMap().forEach((k, v) -> itemStringMap.put(k.toString(), v.stream().map(TagKey::toString).collect(Collectors.toSet())));
            TagStreamGenerators.getBlockMap().forEach((k, v) -> blockStringMap.put(k.toString(), v.stream().map(TagKey::toString).collect(Collectors.toSet())));

            atConfig.itemTagJsonMap.putAll(itemStringMap);
            atConfig.blockTagJsonMap.putAll(blockStringMap);

            String defaultJson = BUILDER.toJson(atConfig);
            Files.writeString(file, defaultJson);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
