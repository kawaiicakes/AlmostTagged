package com.kawaiicakes.almosttagged.api;

import com.almostreliable.unified.api.AlmostUnifiedLookup;
import com.kawaiicakes.almosttagged.AlmostTagged;
import com.kawaiicakes.almosttagged.tags.TagCollections;
import com.kawaiicakes.almosttagged.tags.TagUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagLoaderAPI {

    private static Map<Item, Set<TagKey<Item>>> tagsByItem = new HashMap<>();
    private static Map<Block, Set<TagKey<Block>>> tagsByBlock = new HashMap<>();

    public static Map<Item, Set<TagKey<Item>>> getTagsByItem() {return tagsByItem;}
    public static Map<Block, Set<TagKey<Block>>> getTagsByBlock() {return tagsByBlock;}

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    public static <T> void modifyReturn(CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map, @NotNull String directory) {
        switch (directory) {
            case "tags/items":
                AlmostTagged.Config.itemTagJsonMap.forEach((k, v) -> v.forEach(stringTag ->
                        map.getReturnValue().put(TagUtils.getTagResourceLocationFromString(stringTag),
                                (Collection<T>) Collections.singleton(ForgeRegistries.ITEMS.getHolder(new ResourceLocation(k)).get()))));
                AlmostTagged.LOGGER.info("Almost Tagged has successfully inherited item tags.");
                break;

            case "tags/blocks":
                AlmostTagged.Config.blockTagJsonMap.forEach((k, v) -> v.forEach(stringTag ->
                        map.getReturnValue().put(TagUtils.getTagResourceLocationFromString(stringTag),
                                (Collection<T>) Collections.singleton(ForgeRegistries.BLOCKS.getHolder(new ResourceLocation(k)).get()))));
                AlmostTagged.LOGGER.info("Almost Tagged has successfully inherited block tags.");
                break;
        }
    }

    //This should only be called *after* AULookup is available.
    //Wait a sec... I could just copy the code from TagCollections and massage it into working here...
    public static void getTagsByPreferredItem(Map<Item, Set<TagKey<Item>>> itemReturnMap, Map<Block, Set<TagKey<Block>>> blockReturnMap) {
        TagCollections.getTagSet().forEach(key -> {
           final Item pref = AlmostUnifiedLookup.INSTANCE.getPreferredItemForTag(key);
           final Set<TagKey<Item>> tags = AlmostUnifiedLookup.INSTANCE.getPotentialItems(key)
                   .stream()
                   .<TagKey<Item>>mapMulti(((item, objectConsumer) ->
                           TagLoaderAPI.returnTagsOnItem(item.toString()).forEach(objectConsumer)))
                   .collect(Collectors.toSet());

           itemReturnMap.put(pref, tags);

           if (Block.byItem(pref) != ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:air"))) {
               final Set<TagKey<Block>> tagsBlock = AlmostUnifiedLookup.INSTANCE.getPotentialItems(key)
                       .stream()
                       .<TagKey<Block>>mapMulti(((item, objectConsumer) -> {
                           if (Block.byItem(item) != ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:air"))) {
                               TagLoaderAPI.returnTagsOnBlock(Block.byItem(item).toString()).forEach(objectConsumer);
                           }
                       }))
                       .collect(Collectors.toSet());

               if (!tagsBlock.isEmpty()) blockReturnMap.put(Block.byItem(pref), tagsBlock);
           }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull Map<String, Set<String>> loaderReturnJsonStr(@NotNull CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        final Map<String, Set<String>> returnMap = new HashMap<>();
        map.getReturnValue().forEach((key, value) -> {
            final String resourceString = key.getNamespace() + ":" + key.getPath();
            final Set<String> collectionString = ((Collection<Holder.Reference<T>>) value).stream()
                    .<String>mapMulti((holder, consumer) -> holder.tags().map(TagKey::toString).forEach(consumer))
                    .collect(Collectors.toSet());

            returnMap.put(resourceString, collectionString);
        });

        return returnMap;
    }

    //This method is necessary as both Holders and Registries are still null by the time this return is needed.
    //Basically just like obtaining the tags on an item via ITagManager before it's loaded (and before tags are inherited)
    public static Set<TagKey<Item>> returnTagsOnItem(String item) {
        return tagsByItem.entrySet().stream()
                .filter(itemSetEntry -> itemSetEntry.getKey().toString().equals(item))
                .map(Map.Entry::getValue)
                .<TagKey<Item>>mapMulti(Iterable::forEach)
                .collect(Collectors.toSet());
    }

    //The reason a String is used as an arg as opposed to an Item is because I don't think IForgeRegistry is available yet

    //This method is necessary as both Holders and Registries are still null by the time this return is needed.
    //Basically just like obtaining the tags on a block via ITagManager before it's loaded (and before tags are inherited)
    public static Set<TagKey<Block>> returnTagsOnBlock(String block) {
        return tagsByBlock.entrySet().stream()
                .filter(blockSetEntry -> blockSetEntry.getKey().toString().equals(block))
                .map(Map.Entry::getValue)
                .<TagKey<Block>>mapMulti(Iterable::forEach)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public static <T> void loaderGetAllTagsByItem(@NotNull CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        final Map<Item, Set<TagKey<Item>>> returnMap = new HashMap<>();

        final Set<Holder.Reference<Item>> completeItemSet = new HashSet<>();
        map.getReturnValue().forEach((key, value) -> completeItemSet.addAll(((Collection<Holder.Reference<Item>>) value)));

        completeItemSet.forEach(holder -> {
            final Set<TagKey<Item>> tagsInHolder = new HashSet<>();
            map.getReturnValue().forEach((key, value) -> {
                if (value.contains(holder)) tagsInHolder.add(TagUtils.getItemTagKeyFromResource(key));
            });

            if (!tagsInHolder.isEmpty()) returnMap.put(holder.get(), tagsInHolder);
        });

        tagsByItem = returnMap;
    }

    @SuppressWarnings("unchecked")
    public static <T> void loaderGetAllTagsByBlock(@NotNull CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> map) {
        final Map<Block, Set<TagKey<Block>>> returnMap = new HashMap<>();

        final Set<Holder.Reference<Block>> completeBlockSet = new HashSet<>();
        map.getReturnValue().forEach((key, value) -> completeBlockSet.addAll(((Collection<Holder.Reference<Block>>) value)));

        completeBlockSet.forEach(holder -> {
            final Set<TagKey<Block>> tagsInHolder = new HashSet<>();
            map.getReturnValue().forEach((key, value) -> {
                if (value.contains(holder)) tagsInHolder.add(TagUtils.getBlockTagKeyFromResource(key));
            });

            if (!tagsInHolder.isEmpty()) returnMap.put(holder.get(), tagsInHolder);
        });

        tagsByBlock = returnMap;
    }

    //This method is necessary as we cannot access preferred items or blocks while tags are loading.
    public static <T> Map<T, Set<TagKey<T>>> filterStreamByPreferred(@NotNull Stream<Map.Entry<T, Set<TagKey<T>>>> stream, @NotNull Set<T> preferred) {
        return stream.filter(t -> preferred.contains(t.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
