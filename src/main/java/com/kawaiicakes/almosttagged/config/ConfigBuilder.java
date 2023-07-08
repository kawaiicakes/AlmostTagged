package com.kawaiicakes.almosttagged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class ConfigBuilder {
    public static final Gson BUILDER = (new GsonBuilder()).setPrettyPrinting().create();

    public static final Path file = FMLPaths.GAMEDIR.get().toAbsolutePath().resolve("config").resolve("almosttagged.json");

    public static ConfigEntries loadConfig() { //write, then reload if tags in config do not match the tagMaps in TagStreamGenerators.
        //this means a reload will always occur on first loading of a world since the config will be compared against null.
        //therefore consider moving this functionality into TagLoaderMixin and instead affirming #loadConfig as purely
        //reading the config. Reload functionality should occur prior to the passing of tags to the TagLoader.
        try {
            if (Files.notExists(file)) {
                ConfigEntries atConfig = new ConfigEntries();
                String defaultJson = BUILDER.toJson(atConfig);
                Files.writeString(file, defaultJson);
            }

            return BUILDER.fromJson(Files.readString(file), ConfigEntries.class);

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
