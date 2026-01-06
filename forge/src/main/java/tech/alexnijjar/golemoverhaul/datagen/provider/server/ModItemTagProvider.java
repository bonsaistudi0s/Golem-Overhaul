package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.tags.ModItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends TagsProvider<Item> {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, ExistingFileHelper existingFileHelper) {
        super(output, Registries.ITEM, completableFuture, GolemOverhaul.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModItemTags.WAX)
            .add(TagEntry.element(BuiltInRegistries.ITEM.getKey(Items.HONEYCOMB)))
            .addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "wax"))
            .addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "wax"));

        tag(ModItemTags.CACTUS)
            .add(TagEntry.element(BuiltInRegistries.ITEM.getKey(Items.CACTUS)))
            .addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "cactus"))
            .addOptionalTag(ResourceLocation.fromNamespaceAndPath("forge", "cactus"))
            .addOptional(ResourceLocation.fromNamespaceAndPath("creeperoverhaul", "tiny_cactus"));
    }
}
