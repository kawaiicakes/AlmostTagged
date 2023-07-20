package com.kawaiicakes.almosttagged.config;

import java.util.*;

public class TagConfigEntries {
    public final String INFO = "The game will crash if your JSON is not formatted properly. Please make sure it is correct. Leave the value on a key blank to apply the restriction to all tags/items/blocks in that key.";

    public Map<String, Set<String>> itemTagBlacklist = new HashMap<>();
    public Map<String, Set<String>> blockTagBlacklist = new HashMap<>();

    public Map<String, Set<String>> itemBlacklist = new HashMap<>();
    public Map<String, Set<String>> blockBlacklist = new HashMap<>();

}
