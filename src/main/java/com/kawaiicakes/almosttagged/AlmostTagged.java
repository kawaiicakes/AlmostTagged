package com.kawaiicakes.almosttagged;

import com.kawaiicakes.almosttagged.config.ConfigBuilder;
import com.kawaiicakes.almosttagged.config.ConfigEntries;
import com.kawaiicakes.almosttagged.tags.TagStreamGenerators;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AlmostTagged.MOD_ID)
public class AlmostTagged
{
    public static final String MOD_ID = "almosttagged";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ConfigEntries Config = ConfigBuilder.loadConfig();
    public AlmostTagged()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::FMLConstructMod);
        modEventBus.addListener(this::gatherData);
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void FMLConstructMod(final FMLConstructModEvent event) { //this event fires prior to tags being loaded
        Config = ConfigBuilder.loadConfig(); //load config. config must have valid kv pairs at this time.
        LOGGER.info(MOD_ID + " config loaded during FMLConstructModEvent.");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void gatherData(final GatherDataEvent event) { //use different events prn for these purposes
        TagStreamGenerators.generateAUTagMaps(); //allows tags to load. should be called after tags are fully loaded.
    }
}
