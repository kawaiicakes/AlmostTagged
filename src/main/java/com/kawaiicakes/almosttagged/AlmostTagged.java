package com.kawaiicakes.almosttagged;

import com.kawaiicakes.almosttagged.config.TagConfigBuilder;
import com.kawaiicakes.almosttagged.config.TagConfigEntries;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(AlmostTagged.MOD_ID)
public class AlmostTagged
{
    public static final String MOD_ID = "almosttagged";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static TagConfigEntries Config;

    public AlmostTagged()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::FMLConstructMod);
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void FMLConstructMod(final FMLConstructModEvent event) { //any event prior to a server load will not have ITagManager ready yet.
        Config = TagConfigBuilder.loadConfig();
        LOGGER.info(MOD_ID + " configs loaded during FMLConstructModEvent.");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tagsUpdated(final @NotNull TagsUpdatedEvent event) { //use different events prn for these purposes
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            //allows tags to load. should be called after tags are fully loaded. (but before our tags are passed to TagLoaderAPI)
        }
    }
}
