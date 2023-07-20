package com.kawaiicakes.almosttagged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class TagConfigBuilder {
    public static final Gson BUILDER = (new GsonBuilder()).setPrettyPrinting().create();

    public static final Path file = FMLPaths.GAMEDIR.get().toAbsolutePath().resolve("config").resolve("almostunified").resolve("tags.json");

    public static TagConfigEntries loadConfig() {
        try {
            if (Files.notExists(file)) {
                TagConfigEntries atConfig = new TagConfigEntries();

                atConfig.itemBlacklist.put("minecraft:item_or_block_example", Collections.singleton("minecraft:item_tag_example"));
                atConfig.blockBlacklist.put("minecraft:item_or_block_example", Collections.singleton("minecraft:block_tag_example"));

                atConfig.itemTagBlacklist.put("minecraft:item_tag_example", Collections.singleton("minecraft:item_or_block_example"));
                atConfig.blockTagBlacklist.put("minecraft:block_tag_example", Collections.singleton("minecraft:item_or_block_example"));

                String defaultJson = BUILDER.toJson(atConfig, new TypeToken<TagConfigEntries>(){}.getType());
                Files.writeString(file, defaultJson);
            }

            return BUILDER.fromJson(Files.readString(file), TagConfigEntries.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
