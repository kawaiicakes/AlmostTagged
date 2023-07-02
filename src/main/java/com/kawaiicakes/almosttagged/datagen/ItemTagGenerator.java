package com.kawaiicakes.almosttagged.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

public class ItemTagGenerator extends ForgeRegistryTagsProvider<Item>{
    public ItemTagGenerator(DataGenerator generator, IForgeRegistry<Item> forgeRegistry, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, forgeRegistry, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        //shit i'll finish this later lmao got stuff to do
    }
}
