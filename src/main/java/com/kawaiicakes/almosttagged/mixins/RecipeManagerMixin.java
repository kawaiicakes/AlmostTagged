package com.kawaiicakes.almosttagged.mixins;

import com.google.gson.JsonElement;
import com.kawaiicakes.almosttagged.api.TagLoaderAPI;
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
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void apply(Map<ResourceLocation, JsonElement> recipes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        TagLoaderAPI.unifyTags(); //AU's Runtime API is initialized at the spot this is injected.
    }
}