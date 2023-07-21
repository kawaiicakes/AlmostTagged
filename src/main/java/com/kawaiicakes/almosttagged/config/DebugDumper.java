package com.kawaiicakes.almosttagged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kawaiicakes.almosttagged.tags.TagData;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DebugDumper {
    public enum Type {
        ITEM,
        BLOCK,
        CONFIG
    }
    public static final Gson BUILDER = (new GsonBuilder()).setPrettyPrinting().create();

    public static final Path fileDir = FMLPaths.GAMEDIR.get().toAbsolutePath().resolve("logs")
            .resolve("almostunified");

    public static void dump(@NotNull TagData<?> data, DebugDumper.Type type) {
        try {
            if (Files.notExists(fileDir)) Files.createDirectory(fileDir);

            Path file = switch (type) {
                case ITEM -> fileDir.resolve("item_tag_dump.json");
                case BLOCK -> fileDir.resolve("block_tag_dump.json");
                default -> throw new RuntimeException("Invalid enum " + type + " as argument!");
            };

            if (Files.notExists(file)) Files.createFile(file);

            final Map<String, Set<String>> deepCopy = data.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(),
                            e -> Set.copyOf(e.getValue().stream()
                                    .<String>mapMulti((i, consumer) -> consumer.accept(i.toString()))
                                    .collect(Collectors.toList()))));
            String defaultJson = BUILDER.toJson(deepCopy, new TypeToken<Map<String, Set<String>>>(){}.getType());
            Files.writeString(file, defaultJson);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
