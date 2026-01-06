package tech.alexnijjar.golemoverhaul.datagen.provider.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.tags.ModBiomeTags;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends BiomeTagsProvider {

    public ModBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, completableFuture, GolemOverhaul.MOD_ID, existingFileHelper);
    }

    private static ResourceLocation cTag(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    private static ResourceLocation forgeTag(String path) {
        return ResourceLocation.fromNamespaceAndPath("forge", path);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModBiomeTags.COAL_GOLEM_SPAWNS)
                .addOptionalTag(forgeTag("is_cave"))
                .addOptionalTag(cTag("caves"))
                .addOptionalTag(forgeTag("is_underground"))
                .addOptionalTag(cTag("underground"))
                .addOptionalTag(forgeTag("is_lush"));

        tag(ModBiomeTags.HONEY_GOLEM_SPAWNS)
                .addTag(BiomeTags.IS_FOREST);

        tag(ModBiomeTags.SLIME_GOLEM_SPAWNS)
                .addTag(BiomeTags.IS_OVERWORLD);

        tag(ModBiomeTags.SLIME_GOLEM_SWAMP_SPAWNS)
                .addOptionalTag(forgeTag("is_swamp"))
                .addOptionalTag(cTag("swamp"));

        tag(ModBiomeTags.TERRACOTTA_GOLEM_SPAWNS)
                .addTag(BiomeTags.HAS_MINESHAFT_MESA)
                .addTag(BiomeTags.IS_BADLANDS)
                .addOptional(ResourceLocation.fromNamespaceAndPath("biomeswevegone", "atacama_outback"));
    }
}