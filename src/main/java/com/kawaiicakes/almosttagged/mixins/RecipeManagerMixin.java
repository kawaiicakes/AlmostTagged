package com.kawaiicakes.almosttagged.mixins;

import com.google.gson.JsonElement;
import com.kawaiicakes.almosttagged.api.AlmostUnifiedLookupWrapper;
import com.kawaiicakes.almosttagged.tags.TagCollections;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

//priority 1500 bc i don't wanna be near kjs or au :skull:
@Mixin(value = RecipeManager.class, priority = 1_500)
public class RecipeManagerMixin {
    //This mixin simply assigns a value to TagCollections.tagSet while ensuring AU configs have a chance to load first
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void apply(Map<ResourceLocation, JsonElement> recipes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        TagCollections.tagSet(AlmostUnifiedLookupWrapper.getConfiguredTags()); //generateAUTags must be called after this is fired, but also after tags load
    }
}