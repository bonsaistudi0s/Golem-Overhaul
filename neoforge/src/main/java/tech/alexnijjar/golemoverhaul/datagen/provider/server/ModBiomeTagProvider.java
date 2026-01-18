package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.internal.NeoForgeBiomeTagsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.tags.ModBiomeTags;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends BiomeTagsProvider {

    public ModBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, completableFuture, GolemOverhaul.MOD_ID, existingFileHelper);
    }

    private static ResourceLocation fromTerralith(String path) {
        return ResourceLocation.fromNamespaceAndPath("terralith", path);
    }

    private static ResourceLocation fromBiomesWeveGone(String path) {
        return ResourceLocation.fromNamespaceAndPath("biomeswevegone", path);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModBiomeTags.COAL_GOLEM_SPAWNS)
                .addTag(Tags.Biomes.IS_CAVE)
                .addTag(Tags.Biomes.IS_UNDERGROUND)
                .addTag(Tags.Biomes.IS_LUSH)
                .addOptionalTag(fromTerralith("caves"));

        tag(ModBiomeTags.HONEY_GOLEM_SPAWNS)
                .addTag(Tags.Biomes.IS_FOREST);

        tag(ModBiomeTags.SLIME_GOLEM_SPAWNS)
                .addTag(Tags.Biomes.IS_OVERWORLD);

        tag(ModBiomeTags.SLIME_GOLEM_SWAMP_SPAWNS)
                .addTag(Tags.Biomes.IS_SWAMP);

        tag(ModBiomeTags.TERRACOTTA_GOLEM_SPAWNS)
                .addTag(BiomeTags.HAS_MINESHAFT_MESA)
                .addTag(Tags.Biomes.IS_BADLANDS)
                .addOptional(fromTerralith("savanna_badlands"))
                .addOptional(fromBiomesWeveGone("atacama_outback"));
    }
}