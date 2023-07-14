package com.kawaiicakes.almosttagged;

import com.kawaiicakes.almosttagged.config.TagStorageBuilder;
import com.kawaiicakes.almosttagged.config.TagStorageEntries;
import com.kawaiicakes.almosttagged.tags.TagCollections;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
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
    public static TagStorageEntries Config;
    public AlmostTagged()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::FMLConstructMod);
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void FMLConstructMod(final FMLConstructModEvent event) { //this event fires prior to tags being loaded
        Config = TagStorageBuilder.loadConfig(); //load config. config must have valid kv pairs at this time.
        LOGGER.info(MOD_ID + " config loaded during FMLConstructModEvent.");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tagsUpdated(final TagsUpdatedEvent event) { //use different events prn for these purposes
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            TagCollections.generateAUTagMaps(); //allows tags to load. should be called after tags are fully loaded. (but before our tags are passed to TagLoaderAPI)
            //registry + holder values are loaded past this line.
            final Map<String, Set<String>> itemTagsJson = new HashMap<>();
            final Map<String, Set<String>> blockTagsJson = new HashMap<>();

            TagCollections.getItemMap().forEach((k, v) -> itemTagsJson.put(k.toString(), v.stream().map(TagKey::toString).collect(Collectors.toSet())));
            TagCollections.getBlockMap().forEach((k, v) -> {
                if (Block.byItem(k) != ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:air"))) {
                    blockTagsJson.put(k.toString(), v.stream().map(TagKey::toString).collect(Collectors.toSet()));
                }
            }); //replace this block with the returns from #loaderReturnJsonStr

            //this reload criteria needs to be improved. if for some reason tags are removed from something,
            //this returns as true. this is because the tags in the config are passed to the TagLoaderAPI
            //prior to this being evaluated; meaning it will always be true if no other tags have been
            //added anywhere. Even then, the 'phantom' tags will remain.
            if (!(Config.itemTagJsonMap.equals(itemTagsJson) && Config.blockTagJsonMap.equals(blockTagsJson))) {
                LOGGER.info(MOD_ID + " config is being overwritten!");
                TagStorageBuilder.reloadConfig();

                LOGGER.info(MOD_ID + " is forcing a data reload!");
                //add the reload shit here
            } else {
                LOGGER.info(MOD_ID + " config matches registered tags.");
            }
        }
    }
}
