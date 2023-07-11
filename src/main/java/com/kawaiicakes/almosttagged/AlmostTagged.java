package com.kawaiicakes.almosttagged;

import com.kawaiicakes.almosttagged.config.ConfigBuilder;
import com.kawaiicakes.almosttagged.config.ConfigEntries;
import com.mojang.logging.LogUtils;
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
            final Map<String, Set<String>> itemTagsJsonAU = new HashMap<>();
            final Map<String, Set<String>> blockTagsJsonAU = new HashMap<>();

            //this reload criteria needs to be improved. if for some reason tags are removed from something,
            //this returns as true. this is because the tags in the config are passed to the TagLoader
            //prior to this being evaluated; meaning it will always be true if no other tags have been
            //added anywhere. Even then, the 'phantom' tags will remain.
            if (!(Config.itemTagJsonMap.equals(itemTagsJsonAU) && Config.blockTagJsonMap.equals(blockTagsJsonAU))) {
                LOGGER.info(MOD_ID + " config is being overwritten!");
                ConfigBuilder.reloadConfig();

                LOGGER.info(MOD_ID + " is forcing a data reload!");
                //add the reload shit here
            } else {
                LOGGER.info(MOD_ID + " config matches registered tags.");
            }
        }
    }
}
