package com.kawaiicakes.almosttagged.datagen;

import com.almostreliable.unified.api.AlmostUnifiedLookup;
import com.kawaiicakes.almosttagged.AlmostTagged;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) //no need for this to be an event listener
public class DataGenerators { //https://github.com/AlmostReliable/almostunified/blob/1.19.2/Common/src/main/java/com/almostreliable/unified/mixin/runtime/RecipeManagerMixin.java
    @SubscribeEvent(priority = EventPriority.LOWEST) //literally just inject a call to my add tags method? huh (should be injected AFTER AU)
    public static void gatherData(GatherDataEvent event) {
        AlmostTagged.LOGGER.info(AlmostUnifiedLookup.INSTANCE.toString());
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeServer(), new BlockTagGenerator(gen, ForgeRegistries.BLOCKS, AlmostTagged.MOD_ID, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new ItemTagGenerator(gen, ForgeRegistries.ITEMS, AlmostTagged.MOD_ID, event.getExistingFileHelper()));
    }
}
