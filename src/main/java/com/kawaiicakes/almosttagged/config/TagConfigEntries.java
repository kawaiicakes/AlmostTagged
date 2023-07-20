package com.kawaiicakes.almosttagged.config;

import java.util.*;

public class TagConfigEntries {
    //These are not directly declared as instances of ConfigData as Gson will fail to read
    //and produce an IllegalArgumentException otherwise.
    public Map<String, Set<String>> itemBlacklist = new HashMap<>();
    public Map<String, Set<String>> blockBlacklist = new HashMap<>();

    public Map<String, Set<String>> itemTagBlacklist = new HashMap<>();
    public Map<String, Set<String>> blockTagBlacklist = new HashMap<>();
}
