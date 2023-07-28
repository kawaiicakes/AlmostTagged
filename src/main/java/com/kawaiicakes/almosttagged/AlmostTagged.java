package com.kawaiicakes.almosttagged;

import com.kawaiicakes.almosttagged.config.TagConfigBuilder;
import com.kawaiicakes.almosttagged.config.TagConfigEntries;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Main class for the mod. Entry points are here and in the RecipeManagerMixin and TagLoaderMixin.
 * Pretty self-explanatory.
 */
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
    public void FMLConstructMod(final FMLConstructModEvent event) {
        Config = TagConfigBuilder.loadConfig(); //Doing it like this means the game has to be restarted to apply changes
        LOGGER.info(MOD_ID + " config loaded during FMLConstructModEvent.");
    }
}
