package com.kawaiicakes.almosttagged;

import com.kawaiicakes.almosttagged.config.ConfigBuilder;
import com.kawaiicakes.almosttagged.config.ConfigEntries;
import com.kawaiicakes.almosttagged.tags.TagStreamGenerators;
import com.mojang.logging.LogUtils;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mod(AlmostTagged.MOD_ID)
public class AlmostTagged
{
    public static final String MOD_ID = "almosttagged";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ConfigEntries Config;
    public AlmostTagged()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::FMLConstructMod);
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void FMLConstructMod(final FMLConstructModEvent event) { //this event fires prior to tags being loaded
        Config = ConfigBuilder.loadConfig(); //load config. config must have valid kv pairs at this time.
        LOGGER.info(MOD_ID + " config loaded during FMLConstructModEvent.");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tagsUpdated(final TagsUpdatedEvent event) { //use different events prn for these purposes
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            TagStreamGenerators.generateAUTagMaps(); //allows tags to load. should be called after tags are fully loaded.
            final Map<String, Set<String>> itemTagsJsonAU = new HashMap<>();
            final Map<String, Set<String>> blockTagsJsonAU = new HashMap<>();

            TagStreamGenerators.getItemMap().forEach((k, v) -> itemTagsJsonAU.put(k.toString(), v.stream().map(TagKey::toString).collect(Collectors.toSet())));
            TagStreamGenerators.getBlockMap().forEach((k, v) -> blockTagsJsonAU.put(k.toString(), v.stream().map(TagKey::toString).collect(Collectors.toSet())));

            if (!(Config.itemTagJsonMap.equals(itemTagsJsonAU) && Config.blockTagJsonMap.equals(blockTagsJsonAU))) {
                LOGGER.info(MOD_ID + " config is being overwritten!");
                ConfigBuilder.reloadConfig();

                LOGGER.info(MOD_ID + " is forcing a data reload!");
            } else {
                LOGGER.info(MOD_ID + " config matches registered tags.");
            }
        }
    }
}
