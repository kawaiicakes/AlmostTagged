package com.kawaiicakes.almosttagged.datagen;

import com.kawaiicakes.almosttagged.AlmostTagged;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class DataGenerators {
    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeServer(), new BlockTagGenerator(gen, ForgeRegistries.BLOCKS, AlmostTagged.MOD_ID, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new ItemTagGenerator(gen, ForgeRegistries.ITEMS, AlmostTagged.MOD_ID, event.getExistingFileHelper()));
    }
}
