package com.kawaiicakes.almosttagged.config;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigEntries {
    public Map<Item, Set<TagKey<Item>>> itemTagJsonMap = new HashMap<>();
    public Map<Item, Set<TagKey<Block>>> blockTagJsonMap = new HashMap<>();
}